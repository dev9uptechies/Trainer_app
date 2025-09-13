package com.example.trainerapp

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.Adapter.athlete.AtheleteAdapter
import com.example.model.newClass.athlete.AthleteData
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.databinding.ActivityGetAthletesBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GetAthletesActivity : AppCompatActivity() {
    lateinit var getAthletesBinding: ActivityGetAthletesBinding
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    private lateinit var id: ArrayList<String>
    private lateinit var id_list: java.util.ArrayList<Id_list>

    //    lateinit var adapter: AtheleteDataAdapter
    lateinit var ath_adapter: AtheleteAdapter
    private var timeIds: ArrayList<Int> = ArrayList()
    private var type: String? = ""

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
                        Utils.setUnAuthDialog(this@GetAthletesActivity)
                    } else {
                        Toast.makeText(
                            this@GetAthletesActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Toast.makeText(
                        this@GetAthletesActivity,
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

    override fun onResume() {
        checkUser()
        super.onResume()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getAthletesBinding = ActivityGetAthletesBinding.inflate(layoutInflater)
        setContentView(getAthletesBinding.root)
        initViews()
        loadData()
        checkButtonClick()
    }

    private fun checkButtonClick() {
        getAthletesBinding.btnSaveAthlete.setOnClickListener {
            finish()
        }

        getAthletesBinding.icTopMarkAttendance.tvTitleTopHeader.text = getString(R.string.markAttendance)

//        getAthletesBinding.cbSelectAll.setOnCheckedChangeListener { _, isChecked ->
//            ath_adapter.selectAll(isChecked)
//        }

//        getAthletesBinding.cbSelectAll.setOnClickListener {
//            if (getAthletesBinding.cbSelectAll.isChecked) {
//                ath_adapter.selectAll(true)
//            } else {
//                ath_adapter.selectAll(false)
//            }
//        }

        getAthletesBinding.icTopMarkAttendance.ivBackTopHeader.setOnClickListener {
            finish()
        }
    }

    private fun loadData() {
        try {
            getAthletes()
        } catch (e: Exception) {
            Log.d("Exception", e.message.toString())
        }
    }

    private fun getAthletes() {
        getAthletesBinding.progressBar.visibility = View.VISIBLE
        apiInterface.GetAthleteList()!!.enqueue(
            object : Callback<AthleteData> {
                override fun onResponse(
                    call: Call<AthleteData>,
                    response: Response<AthleteData>
                ) {
                    Log.d("Athlete :- Tag ", response.code().toString() + "")
                    val code = response.code()
                    if (code == 200) {
                        val data = response.body()!!
                        val success: Boolean = data.status!!
                        if (success) {
                            if (data.data!! != null) {
                                Log.d("Athlete :- Data ", "${data}")
                                for (i in data.data) {
                                    Log.d("Athlete $i", "${i.name} \t ${i.id} \t ${i.athletes}")
                                }
                                initRecyclerView(data.data)
                            }
                        }
                        getAthletesBinding.progressBar.visibility = View.GONE
                    } else if (response.code() == 403) {
                        Utils.setUnAuthDialog(this@GetAthletesActivity)
//                        val message = response.message()
//                        Toast.makeText(
//                            this@GetAthletesActivity,
//                            "" + message,
//                            Toast.LENGTH_SHORT
//                        )
//                            .show()
//                        call.cancel()
//                        startActivity(
//                            Intent(
//                                this@GetAthletesActivity,
//                                SignInActivity::class.java
//                            )
//                        )
//                        finish()
                    } else {
                        val message = response.message()
                        Toast.makeText(this@GetAthletesActivity, "" + message, Toast.LENGTH_SHORT)
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<AthleteData>, t: Throwable) {
                    getAthletesBinding.progressBar.visibility = View.GONE
                    Toast.makeText(this@GetAthletesActivity, "" + t.message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }

            }
        )
    }

    private fun initViews() {
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        getAthletesBinding.progressBar.visibility = View.VISIBLE
        type = intent.getStringExtra("type")
        if (!type.isNullOrEmpty()) {
            when (type!!) {
                "create" -> {
                    timeIds = ArrayList()
                }

                "edit" -> {
                    try {
                        timeIds = intent.getIntegerArrayListExtra("testId")!!
                    } catch (e: Exception) {
                        Log.d("Exception :-", "$e \t ${e.message}")
                    }
                }
            }
        }
        Log.d("time Ids :", "$timeIds")
    }

    private fun initRecyclerView(user: ArrayList<AthleteData.Athlete>) {
        getAthletesBinding.rvAllAthletes.layoutManager = LinearLayoutManager(this)

        if (timeIds.size != 0) {
            ath_adapter =
                AtheleteAdapter(user, this, timeIds, type)
            getAthletesBinding.rvAllAthletes.adapter = ath_adapter
        } else {
            ath_adapter =
                AtheleteAdapter(user, this, null, type)
            getAthletesBinding.rvAllAthletes.adapter = ath_adapter
        }
    }

//    private fun initrecyclerview(user: ArrayList<AthleteDatalist.AtheleteList>?) {
//        getAthletesBinding.rvAllAthletes.layoutManager = LinearLayoutManager(this)
//        adapter =
//            AtheleteDataAdapter(user, this)
//        getAthletesBinding.rvAllAthletes.adapter = adapter
//    }

    fun getObjectJson(c: Context, key: String): List<String> {
        val appSharedPrefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(
            c.applicationContext
        )

        /*** get user data     */
        val json = appSharedPrefs.getString(key, "")
        val gson = Gson()
        val type = object : TypeToken<List<String>>() {}.type
        val arrayList: List<String> = gson.fromJson<List<String>>(json, type)
        return arrayList
//        return gson.fromJson(
//            json,
//            ArrayList<User_Data>()
//        )
    }
}