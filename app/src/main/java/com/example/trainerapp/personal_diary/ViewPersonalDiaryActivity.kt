package com.example.trainerapp.personal_diary

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.Adapter.personal_diary.PersonalDiaryShareAdapter
import com.example.Adapter.personal_diary.ViewPersonalDairyListAdapter
import com.example.OnItemClickListener
import com.example.model.AthleteDataPackage.AthleteDatas
import com.example.model.AthleteDataPackage.AthltepersonaldiaryModel
import com.example.model.AthleteDataPackage.Datas
import com.example.model.personal_diary.GetPersonalDiary
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.PreferencesManager
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.ActivityViewPersonalDiaryBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewPersonalDiaryActivity : AppCompatActivity(), OnItemClickListener.OnItemClickCallback {

    private lateinit var binding: ActivityViewPersonalDiaryBinding
    private lateinit var apiInterface: APIInterface
    private lateinit var diaryData: MutableList<GetPersonalDiary.Data>
    private lateinit var diaryshareData: MutableList<Datas>
    private lateinit var viewDiary: ViewPersonalDairyListAdapter
    private lateinit var viewShareDiary: PersonalDiaryShareAdapter
    lateinit var preferenceManager: PreferencesManager
    private var mainId: Int = 0
    private var isUserInteraction = false // Flag to track user interaction



    override fun onResume() {
        checkUser()
        super.onResume()

    }

    private fun checkUser() {
        try {
            apiInterface.ProfileData()?.enqueue(object : Callback<RegisterData?> {
                override fun onResponse(
                    call: Call<RegisterData?>,
                    response: Response<RegisterData?>
                ) {
                    Log.d("TAG", response.code().toString() + "")
                    val code = response.code()
                    if (code == 200) {

                        Log.d("Get Profile Data ", "${response.body()}")
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@ViewPersonalDiaryActivity)
                    } else {
                        Toast.makeText(
                            this@ViewPersonalDiaryActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Toast.makeText(
                        this@ViewPersonalDiaryActivity,
                        "" + t.message,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    call.cancel()
                }
            })
        } catch (e: Exception) {
            Log.d("Exception", "${e.message}")
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewPersonalDiaryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        setupButtonClickListeners()
        setupSwitch()

        val userType = preferenceManager.GetFlage()

        if (userType == "Athlete") {
            binding.shareCard.visibility = View.VISIBLE

            val savedStatus = getShareStatus()
            binding.switchShare.isChecked = savedStatus == 1
        } else {
            binding.shareCard.visibility = View.GONE
        }
    }

    private fun setupButtonClickListeners() {
        binding.swipeReferesh.setOnRefreshListener {
            try {
//            loadData()
            } catch (e: Exception) {
                Log.d("errro", e.message.toString())
            }
        }

        binding.swipeReferesh.setOnRefreshListener {
            loadData()
        }
        binding.back.setOnClickListener { finish() }

        binding.add.setOnClickListener {
            startActivity(Intent(this, AddPersonalDIaryActivity::class.java))
        }

        binding.switchShare.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AddShareData(1)

            } else {
                AddShareData(0)
            }
        }

    }



    private fun AddShareData(shareValue: Int) {
        try {
            val requestBody = mapOf("share" to shareValue)

            apiInterface.updateShareStatus(requestBody).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        saveShareStatus(shareValue) // Save only the switch's status

                        if (isUserInteraction) {
                            val message = if (shareValue == 0) {
                                "Now coach can't see your personal diary data."
                            } else {
                                "Now coach can see your personal diary data."
                            }
                            Toast.makeText(this@ViewPersonalDiaryActivity, message, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(
                            this@ViewPersonalDiaryActivity,
                            "Error: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    isUserInteraction = false // Reset the flag after API response
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.e("API_ERROR", t.message ?: "Unknown error")
                    Toast.makeText(
                        this@ViewPersonalDiaryActivity,
                        "API call failed: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    isUserInteraction = false // Reset the flag after API failure
                }
            })

        } catch (e: Exception) {
            Log.d("CATch", "AddShareData: ${e.message.toString()}")
        }
    }

    private fun saveShareStatus(shareValue: Int) {
        val sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("ShareStatus", shareValue)
        editor.apply()
    }

    // Retrieve share status from SharedPreferences
    private fun getShareStatus(): Int {
        val sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        return sharedPreferences.getInt("ShareStatus", 0) // Default is 0 (unchecked)
    }

    // Set up the switch and restore its state
    private fun setupSwitch() {
        val savedStatus = getShareStatus()
        binding.switchShare.isChecked = (savedStatus == 1)

        // Listen for changes only when the user interacts
        binding.switchShare.setOnCheckedChangeListener { _, isChecked ->
            isUserInteraction = true // Mark as user-initiated action
            val shareValue = if (isChecked) 1 else 0
            AddShareData(shareValue)
        }
    }


    private fun initViews() {
        apiInterface = APIClient(this).client().create(APIInterface::class.java)
        diaryData = mutableListOf()
        diaryshareData = mutableListOf()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        preferenceManager = PreferencesManager(this)

        mainId = intent.getIntExtra("athleteId", 0)
        Log.d("MAINID", "MAINID:- $mainId")

        if (mainId != 0) {
            getpersonaldiaryshare(mainId)
            binding.swipeReferesh.isRefreshing = false

        } else {
            loadData()
            binding.swipeReferesh.isRefreshing = false
        }
    }

    private fun loadData() {
        getpersonaldiary()
        binding.swipeReferesh.isRefreshing = false
    }

//    private fun getpersonaldiary() {
//        try {
//            diaryData.clear()
//            binding.progresBar.visibility = View.VISIBLE
//
//            apiInterface.GetPersonalDiaryListData()?.enqueue(object : Callback<GetPersonalDiary> {
//                override fun onResponse(
//                    call: Call<GetPersonalDiary>,
//                    response: Response<GetPersonalDiary>
//                ) {
//                    binding.progresBar.visibility = View.GONE
//                    Log.d("TAG", "Response code: ${response.code()}")
//
//                    when (response.code()) {
//                        200 -> {
//                            response.body()?.data?.let { data ->
//                                if (data.isNotEmpty()) {
//                                    diaryData = data.toMutableList()
//                                    setAdapter(diaryData)
//                                } else {
//                                    Log.d("DATA_TAG", "No Data Available")
//                                    binding.recyclerView.visibility = View.GONE
//                                }
//                            } ?: Log.d("DATA_TAG", "Response body is null")
//                        }
//                        403 -> Utils.setUnAuthDialog(this@ViewPersonalDiaryActivity)
//                        else -> {
//                            Toast.makeText(
//                                this@ViewPersonalDiaryActivity,
//                                "Error: ${response.message()}",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                    }
//                }
//
//                override fun onFailure(call: Call<GetPersonalDiary>, t: Throwable) {
//                    binding.progresBar.visibility = View.GONE
//                    Log.d("TAG Category", "Error: ${t.message}")
//                }
//            })
//        } catch (e: Exception) {
//            binding.progresBar.visibility = View.GONE
//            Log.d("Exception", "Error: ${e.message}")
//        }
//    }

    private fun getpersonaldiary() {
        try {
            diaryData.clear()
            binding.progresBar.visibility = View.VISIBLE

            apiInterface.GetPersonalDiaryListData()!!.enqueue(object : Callback<GetPersonalDiary> {
                override fun onResponse(
                    call: Call<GetPersonalDiary>,
                    response: Response<GetPersonalDiary>
                ) {
                    binding.progresBar.visibility = View.GONE
                    binding.swipeReferesh.isRefreshing = false  // Stop the refresh animation

                    Log.d("TAG", "Response code: ${response.code()}")

                    when (response.code()) {
                        200 -> {
                            response.body()?.data?.let { data ->
                                if (data.isNotEmpty()) {
                                    diaryData = data.toMutableList()
                                    setAdapter(diaryData)
                                } else {
                                    binding.swipeReferesh.isRefreshing = false
                                    Log.d("DATA_TAG", "No Data Available")
                                    binding.recyclerView.visibility = View.GONE
                                    binding.tvNodata.visibility = View.VISIBLE
                                }
                            } ?: Log.d("DATA_TAG", "Response body is null")
                        }

                        403 -> Utils.setUnAuthDialog(this@ViewPersonalDiaryActivity)
                        else -> {
                            Toast.makeText(
                                this@ViewPersonalDiaryActivity,
                                "Error: ${response.message()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                override fun onFailure(call: Call<GetPersonalDiary>, t: Throwable) {
                    binding.progresBar.visibility = View.GONE
                    binding.swipeReferesh.isRefreshing = false  // Stop the refresh animation

                    Log.d("TAG Category", "Error: ${t.message}")
                    Toast.makeText(
                        this@ViewPersonalDiaryActivity,
                        "Error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } catch (e: Exception) {
            binding.progresBar.visibility = View.GONE
            binding.swipeReferesh.isRefreshing = false
            Log.d("Exception", "Error: ${e.message}")
        }
    }

//    private fun setAdapter(data: MutableList<GetPersonalDiary.Data>) {
//        try {
//            if (data.isNotEmpty()) {
//                binding.recyclerView.visibility = View.VISIBLE
//                viewDiary = ViewPersonalDairyListAdapter(data, this, this)
//                binding.recyclerView.adapter = viewDiary
//            } else {
//                binding.recyclerView.visibility = View.GONE
//            }
//        } catch (e: Exception) {
//            Log.d("AdapterError", "Error: ${e.message}")
//        }
//    }


    private fun setAdapter2(data: MutableList<Datas>) {
        try {
            if (data.isNotEmpty()) {
                binding.recyclerView.visibility = View.VISIBLE
                viewShareDiary = PersonalDiaryShareAdapter(data, this, this)
                binding.recyclerView.adapter = viewShareDiary
            } else {
                binding.recyclerView.visibility = View.GONE
            }
        } catch (e: Exception) {
            Log.d("AdapterError", "Error: ${e.message}")
        }
    }


    private fun getpersonaldiaryshare(id: Int) {
        try {
            diaryData.clear()
            binding.progresBar.visibility = View.VISIBLE

            apiInterface.GetPersonalDiaryListShareData(id)
                ?.enqueue(object : Callback<AthltepersonaldiaryModel> {
                    override fun onResponse(
                        call: Call<AthltepersonaldiaryModel>,
                        response: Response<AthltepersonaldiaryModel>
                    ) {


                        binding.progresBar.visibility = View.GONE
                        binding.swipeReferesh.isRefreshing = false  // Stop the refresh animation

                        Log.d("TAG2", "Response code: ${response.code()}")

                        when (response.code()) {
                            200 -> {
                                response.body()?.data?.let { data ->
                                    diaryshareData.add(data)
                                    setAdapter2(diaryshareData)

                                } ?: run {
                                    Log.d("DATA_TAG", "No Data Available")
                                    binding.recyclerView.visibility = View.GONE
                                    binding.tvNodata.visibility = View.VISIBLE
                                }
                            }

                            403 -> Utils.setUnAuthDialog(this@ViewPersonalDiaryActivity)
                            else -> {
                                Log.d("ERROR", "ERROR:-  ${response.message()}")
                                Toast.makeText(
                                    this@ViewPersonalDiaryActivity,
                                    "Error: ${response.message()}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }

                    override fun onFailure(call: Call<AthltepersonaldiaryModel>, t: Throwable) {
                        binding.progresBar.visibility = View.GONE
                        binding.swipeReferesh.isRefreshing = false  // Stop the refresh animation
                        Log.d("TAG Category", "Error: ${t.message}")
                        Toast.makeText(
                            this@ViewPersonalDiaryActivity,
                            "Error: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        } catch (e: Exception) {
            binding.progresBar.visibility = View.GONE
            Log.d("Exception", "Error: ${e.message}")
        }
    }


    private fun setAdapter(data: MutableList<GetPersonalDiary.Data>) {
        try {
            if (data.isNotEmpty()) {
                binding.recyclerView.visibility = View.VISIBLE
                binding.tvNodata.visibility = View.GONE  // Hide the "No Data Found" message
                viewDiary = ViewPersonalDairyListAdapter(data, this, this)
                binding.recyclerView.adapter = viewDiary
            } else {
                binding.recyclerView.visibility = View.GONE
                binding.tvNodata.visibility = View.VISIBLE  // Show "No Data Found" message
            }
        } catch (e: Exception) {
            Log.d("AdapterError", "Error: ${e.message}")
        }
    }

    override fun onItemClicked(view: View, position: Int, type: Long, string: String) {
        // Handle item click here
    }
}
