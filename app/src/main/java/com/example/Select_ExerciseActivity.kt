package com.example

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.model.Ecercise_data_list
import com.example.model.Ecercise_list_data
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.ExcerciseData
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.PreferencesManager
import com.example.trainerapp.R
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.ActivitySelectExerciseBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Select_ExerciseActivity : AppCompatActivity(), OnItemClickListener.OnItemClickCallback {
    lateinit var selectExerciseBinding: ActivitySelectExerciseBinding
    lateinit var apiInterface: APIInterface
    lateinit var adapter: ExcerciselistselectAdapter
    lateinit var exerciselist: ArrayList<ExcerciseData.Exercise>
    private lateinit var exercise_list: ArrayList<Ecercise_data_list>
    private lateinit var exerciseDataList: ArrayList<Ecercise_list_data>
    lateinit var preferenceManager: PreferencesManager
    lateinit var generallist: ArrayList<ExcerciseData.Exercise>
    lateinit var specificlist: ArrayList<ExcerciseData.Exercise>
    lateinit var apiClient: APIClient
    private var checktype: Boolean? = false
    private var excIds: ArrayList<Int> = ArrayList()
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
                        Utils.setUnAuthDialog(this@Select_ExerciseActivity)
                    } else {
                        Toast.makeText(
                            this@Select_ExerciseActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Toast.makeText(
                        this@Select_ExerciseActivity,
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
        selectExerciseBinding = ActivitySelectExerciseBinding.inflate(layoutInflater)
        setContentView(selectExerciseBinding.root)
        apiClient = APIClient(this)
        preferenceManager = PreferencesManager(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        exerciselist = ArrayList()
        exercise_list = ArrayList()
        exerciseDataList = ArrayList()
        generallist = ArrayList()
        specificlist = ArrayList()
        type = intent.getStringExtra("type")
        if (!type.isNullOrEmpty()) {
            when (type!!) {
                "create" -> {
                    excIds = ArrayList()
                }

                "edit" -> {
                    try {
                        excIds = intent.getIntegerArrayListExtra("programId")!!
                    } catch (e: Exception) {
                        Log.d("Exception :-", "$e \t ${e.message}")
                    }
                }
            }
        }
        Log.d("Program Ids :", "$excIds")
        GetExercise()

        selectExerciseBinding.generalCard.setOnClickListener {
            generallist.clear()
            checktype = false
            for (i in 0 until exerciselist.size) {
                if (exerciselist[i].type == "General") {
                    generallist.add(exerciselist[i])
                }
            }
            initRecycler(generallist)
            selectExerciseBinding.generalCard.setCardBackgroundColor(resources.getColor(R.color.splash_text_color))
            selectExerciseBinding.specificCard.setCardBackgroundColor(resources.getColor(R.color.grey))
        }

        selectExerciseBinding.specificCard.setOnClickListener {
            specificlist.clear()
            for (i in 0 until exerciselist.size) {
                if (exerciselist[i].type == "Specific") {
                    specificlist.add(exerciselist[i])
                }
            }
            checktype = true
            initRecycler(specificlist)
            selectExerciseBinding.generalCard.setCardBackgroundColor(resources.getColor(R.color.grey))
            selectExerciseBinding.specificCard.setCardBackgroundColor(resources.getColor(R.color.splash_text_color))

        }

        selectExerciseBinding.save.setOnClickListener {
            if (::adapter.isInitialized) {
                if (adapter.getSelectedExercises().isEmpty()) {
                    Toast.makeText(this, "Please select at least one exercise.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                preferenceManager.setexercisedata(true)
                adapter.resetExerciseList()
                excIds.clear()

                finish()
            } else {
                Toast.makeText(this, "Adapter is not initialized.", Toast.LENGTH_SHORT).show()
            }
        }

        selectExerciseBinding.back.setOnClickListener {
            finish()
        }
    }

    private fun GetExercise() {
        selectExerciseBinding.progressBar.visibility = View.VISIBLE
        apiInterface.GetExercise()?.enqueue(object : Callback<ExcerciseData?> {
            override fun onResponse(
                call: Call<ExcerciseData?>,
                response: Response<ExcerciseData?>
            ) {
                val code = response.code()
                if (code == 200) {
                    Log.d("TAG", response.code().toString() + "")
                    val resource: ExcerciseData? = response.body()
                    val success = resource?.status ?: false
                    val Message: String = resource?.message!!
                    selectExerciseBinding.progressBar.visibility = View.GONE

                    if (success) {
                        val data = resource?.data
                        if (!data.isNullOrEmpty()) {
                            exerciselist = data
                            initRecycler(exerciselist)
                        } else {
                            Toast.makeText(
                                this@Select_ExerciseActivity,
                                "No exercise data found",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                } else if (response.code() == 403) {
                    Utils.setUnAuthDialog(this@Select_ExerciseActivity)
//                    val message = response.message()
//                    Toast.makeText(
//                        this@Select_ExerciseActivity,
//                        "" + message,
//                        Toast.LENGTH_SHORT
//                    )
//                        .show()
//                    call.cancel()
//                    startActivity(
//                        Intent(
//                            this@Select_ExerciseActivity,
//                            SignInActivity::class.java
//                        )
//                    )
//                    finish()
                } else {
                    val message = response.message()
                    Toast.makeText(this@Select_ExerciseActivity, "" + message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }

            }

            override fun onFailure(call: Call<ExcerciseData?>, t: Throwable) {
                selectExerciseBinding.progressBar.visibility = View.GONE
                Toast.makeText(this@Select_ExerciseActivity, "" + t.message, Toast.LENGTH_SHORT)
                    .show()
                call.cancel()
            }
        })

    }

    private fun initRecycler(data: ArrayList<ExcerciseData.Exercise>) {
        Log.d("no of value :-", "${excIds.size}\n$excIds")
        selectExerciseBinding.rlyExerciseList.layoutManager = LinearLayoutManager(this)
//        adapter =
//            ExcerciselistselectAdapter(data, this, this)
//        selectExerciseBinding.rlyExerciseList.adapter = adapter
        if (excIds.size != 0) {
            adapter =
                ExcerciselistselectAdapter(data, this, this, excIds, type)
            selectExerciseBinding.rlyExerciseList.adapter = adapter
        } else {
            adapter =
                ExcerciselistselectAdapter(data, this, this, null, type)
            selectExerciseBinding.rlyExerciseList.adapter = adapter
        }


    }

    override fun onItemClicked(view: View, position: Int, type: Long, string: String) {

    }

}