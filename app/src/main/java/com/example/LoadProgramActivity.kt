package com.example

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.ProgramListData
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.PreferencesManager
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.ActivityLoadProgramBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoadProgramActivity : AppCompatActivity(), OnItemClickListener.OnItemClickCallback {
    lateinit var loadProgramBinding: ActivityLoadProgramBinding
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    lateinit var adapter: ProgramListAdapter
    lateinit var preferenceManager: PreferencesManager

    private var lessonIds: ArrayList<Int> = ArrayList()
    private var type: String? = ""
    private var Sname: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadProgramBinding = ActivityLoadProgramBinding.inflate(layoutInflater)
        setContentView(loadProgramBinding.root)
        initViews()

        GetProgramList()

        loadProgramBinding.back.setOnClickListener { finish() }

        loadProgramBinding.cardSave.setOnClickListener {
            preferenceManager.setexercisedata(true)
            finish()

        }
    }

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
                        Utils.setUnAuthDialog(this@LoadProgramActivity)
                    } else {
                        Toast.makeText(
                            this@LoadProgramActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Toast.makeText(
                        this@LoadProgramActivity,
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

    private fun initViews() {
        apiClient = APIClient(this)
        preferenceManager = PreferencesManager(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        type = intent.getStringExtra("type")
        Sname = intent.getStringExtra("SectionName").toString()

        Log.d("YUYUYUUUY","JUST:- $Sname")
        if (!type.isNullOrEmpty()) {
            when (type!!) {
                "create" -> {
                    lessonIds = ArrayList()
                }

                "edit" -> {
                    try {
                        lessonIds = intent.getIntegerArrayListExtra("lessonId")!!
                    } catch (e: Exception) {
                        Log.d("Exception :-", "$e \t ${e.message}")
                    }
                }
            }
        }
        Log.d("time Ids :", "$type \t $lessonIds")
    }

    private fun GetProgramList() {
        loadProgramBinding.programProgress.visibility = View.VISIBLE
        apiInterface.GetProgam()?.enqueue(object : Callback<ProgramListData?> {
            override fun onResponse(
                call: Call<ProgramListData?>,
                response: Response<ProgramListData?>
            ) {
                loadProgramBinding.programProgress.visibility = View.GONE
                Log.d("TAG", response.code().toString() + "")
                val code = response.code()
                if (code == 200) {
                    val resource: ProgramListData? = response.body()
                    val Success: Boolean = resource?.status!!
                    val Message: String = resource.message!!
                    if (Success) {
                        if (resource.data!! != null) {
                            initrecycler(resource.data)
                        }
                    } else {
                        Toast.makeText(this@LoadProgramActivity, "" + Message, Toast.LENGTH_SHORT)
                            .show()
                    }
                } else if (code == 403) {
                    Utils.setUnAuthDialog(this@LoadProgramActivity)
                } else {
                    call.cancel()
                    Toast.makeText(
                        this@LoadProgramActivity,
                        "" + response.message(),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }

            override fun onFailure(call: Call<ProgramListData?>, t: Throwable) {
                loadProgramBinding.programProgress.visibility = View.GONE
                Toast.makeText(this@LoadProgramActivity, "" + t.message, Toast.LENGTH_SHORT)
                    .show()
                call.cancel()
            }
        })

    }

    private fun initrecycler(data: ArrayList<ProgramListData.testData>?) {
        loadProgramBinding.programRly.layoutManager = LinearLayoutManager(this)

        if (lessonIds.size != 0) {
            adapter = ProgramListAdapter(data, this, this, lessonIds, Sname,type)
            loadProgramBinding.programRly.adapter = adapter
        } else {
            adapter = ProgramListAdapter(data, this, this, null, Sname,type)
            loadProgramBinding.programRly.adapter = adapter
        }
//        adapter = ProgramListAdapter(data, this, this)
//        loadProgramBinding.programRly.adapter = adapter
    }

//    private fun initRecyclerView(user: ArrayList<com.example.model.AthleteDataPackage.AthleteData.Athlete>) {
//        getAthletesBinding.rvAllAthletes.layoutManager = LinearLayoutManager(this)
//
//        if (timeIds.size != 0) {
//            ath_adapter =
//                AtheleteAdapter(user, this, timeIds, type)
//            getAthletesBinding.rvAllAthletes.adapter = ath_adapter
//        } else {
//            ath_adapter =
//                AtheleteAdapter(user, this, null, type)
//            getAthletesBinding.rvAllAthletes.adapter = ath_adapter
//        }
//    }

    override fun onItemClicked(view: View, position: Int, type: Long, string: String) {

    }
}