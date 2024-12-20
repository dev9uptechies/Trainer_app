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
    private var mainId: Int = 0



    override fun onResume() {
        checkUser()
        super.onResume()
    }

//    private fun checkUser() {
//        try {
//            apiInterface.ProfileData()?.enqueue(object : Callback<RegisterData?> {
//                override fun onResponse(
//                    call: Call<RegisterData?>,
//                    response: Response<RegisterData?>
//                ) {
//                    Log.d("TAG", response.code().toString() + "")
//                    val code = response.code()
//                    if (code == 200) {
//                        Log.d("Get Profile Data ", "${response.body()}")
//                        loadData()
//                    } else if (code == 403) {
//                        Utils.setUnAuthDialog(this@ViewPersonalDiaryActivity)
//                    } else {
//                        Toast.makeText(
//                            this@ViewPersonalDiaryActivity,
//                            "" + response.message(),
//                            Toast.LENGTH_SHORT
//                        )
//                            .show()
//                        call.cancel()
//                    }
//                }
//
//                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
//                    Toast.makeText(
//                        this@ViewPersonalDiaryActivity,
//                        "" + t.message,
//                        Toast.LENGTH_SHORT
//                    )
//                        .show()
//                    call.cancel()
//                }
//            })
//        } catch (e: Exception) {
//            Log.d("Exception", "${e.message}")
//        }
//    }

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
    }

    private fun setupButtonClickListeners() {
        binding.swipeReferesh.setOnRefreshListener {
            try {
//            loadData()
            }catch (e:Exception){
                Log.d("errro",e.message.toString())
            }
        }

        binding.back.setOnClickListener { finish() }

        binding.add.setOnClickListener {
            startActivity(Intent(this, AddPersonalDIaryActivity::class.java))
        }
    }
    private fun initViews() {
        apiInterface = APIClient(this).client().create(APIInterface::class.java)
        diaryData = mutableListOf()
        diaryshareData = mutableListOf()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        // Get the athleteId passed from the intent
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
                    Log.d("TAG", "Response code: ${response.code()}")

                    when (response.code()) {
                        200 -> {
                            response.body()?.data?.let { data ->
                                if (data.isNotEmpty()) {
                                    diaryData = data.toMutableList()
                                    setAdapter(diaryData)
                                } else {
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


    // Function to get the shared personal diary data
    private fun getpersonaldiaryshare(id: Int) {
        try {
            diaryData.clear()  // Assuming diaryData is a list of type Data or similar
            binding.progresBar.visibility = View.VISIBLE

            // Ensure the parameter name matches the function signature
            apiInterface.GetPersonalDiaryListShareData(id)?.enqueue(object : Callback<AthltepersonaldiaryModel> {
                override fun onResponse(
                    call: Call<AthltepersonaldiaryModel>,
                    response: Response<AthltepersonaldiaryModel>
                ) {
                    binding.progresBar.visibility = View.GONE
                    Log.d("TAG2", "Response code: ${response.code()}")

                    when (response.code()) {
                        200 -> {
                            response.body()?.data?.let { data ->
                                diaryshareData.add(data)
                                setAdapter2(diaryshareData)

//
                            } ?: run {
                                Log.d("DATA_TAG", "No Data Available")
                                binding.recyclerView.visibility = View.GONE
                                binding.tvNodata.visibility = View.VISIBLE
                            }
                        }
                        403 -> Utils.setUnAuthDialog(this@ViewPersonalDiaryActivity)
                        else -> {
                            Log.d("ERROR","ERROR:-  ${response.message()}")
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
