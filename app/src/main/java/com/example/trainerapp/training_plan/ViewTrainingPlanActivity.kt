package com.example.trainerapp.training_plan

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.Adapter.training_plan.view.ViewTrainingAdapter
import com.example.OnItemClickListener
import com.example.model.training_plan.TrainingPlanData
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.PreferencesManager
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.ActivityViewTrainingPlanBinding
import com.example.trainerapp.training_plan.view_planning_cycle.ViewTrainingPlanListActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewTrainingPlanActivity : AppCompatActivity(), OnItemClickListener.OnItemClickCallback {
    lateinit var viewTrainingPlanBinding: ActivityViewTrainingPlanBinding
    private lateinit var apiInterface: APIInterface
    private lateinit var preferenceManager: PreferencesManager
    private lateinit var apiClient: APIClient
    var id: Int? = null
    lateinit var programData: MutableList<TrainingPlanData.TrainingPlan>
    lateinit var viewTraining: ViewTrainingAdapter

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
                        Utils.setUnAuthDialog(this@ViewTrainingPlanActivity)
                    } else {
                        Toast.makeText(
                            this@ViewTrainingPlanActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Toast.makeText(
                        this@ViewTrainingPlanActivity,
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
        viewTrainingPlanBinding = ActivityViewTrainingPlanBinding.inflate(layoutInflater)
        setContentView(viewTrainingPlanBinding.root)
        initViews()
        loadData()
        checkButtonClick()
    }

    private fun checkButtonClick() {
        viewTrainingPlanBinding.back.setOnClickListener {
            finish()
        }
    }

    private fun loadData() {
        programData.clear()
        try {
            GetProgramData()
        } catch (e: Exception) {
            Log.d("Exception", "${e.message}")
        }
    }

    private fun GetProgramData() {
        programData.clear()
        viewTrainingPlanBinding.progresBar.visibility = View.VISIBLE
        apiInterface.GetTrainingPlan()?.enqueue(object : Callback<TrainingPlanData?> {
            override fun onResponse(
                call: Call<TrainingPlanData?>, response: Response<TrainingPlanData?>
            ) {
                viewTrainingPlanBinding.progresBar.visibility = View.GONE

                Log.d("TAG", response.code().toString() + "")
                val code = response.code()
                if (code == 200) {
                    val resource: TrainingPlanData? = response.body()
                    val Success: Boolean = resource?.status!!
                    val Message: String = resource.message!!
                    Log.d("TAG", resource.data.toString())

                    if (Success) {
                        try {
                            val data = response.body()!!.data!!.filter {
                                it.id == id
                            }
                            setProgramDataset(data)
                            programData.addAll(data)

//                        setProgramData()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        Toast.makeText(
                            this@ViewTrainingPlanActivity,
                            "" + Message,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                } else if (code == 403) {
                    Utils.setUnAuthDialog(this@ViewTrainingPlanActivity)
                } else {
                    Toast.makeText(
                        this@ViewTrainingPlanActivity,
                        "" + response.message(),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    call.cancel()
                }

            }

            override fun onFailure(call: Call<TrainingPlanData?>, t: Throwable) {
                viewTrainingPlanBinding.progresBar.visibility = View.GONE
                Toast.makeText(this@ViewTrainingPlanActivity, "" + t.message, Toast.LENGTH_SHORT)
                    .show()
                call.cancel()
            }
        })
    }

    private fun setProgramDataset(data: List<TrainingPlanData.TrainingPlan>) {
        viewTrainingPlanBinding.edtProgramName.setText(data[0].name)
        viewTrainingPlanBinding.edtStartDate.setText(data[0].start_date)
        viewTrainingPlanBinding.edtEndDate.setText(data[0].competition_date)
        viewTrainingPlanBinding.days.text = data[0].mesocycle + " Days"

        initRecyclerView(arrayListOf(data[0]))
    }

    private fun initRecyclerView(programExercises: ArrayList<TrainingPlanData.TrainingPlan>) {

        viewTrainingPlanBinding.recyclerView.layoutManager = LinearLayoutManager(this)
        viewTraining = ViewTrainingAdapter(programExercises, this, this)
        viewTrainingPlanBinding.recyclerView.adapter = viewTraining
    }

    private fun initViews() {
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        preferenceManager = PreferencesManager(this)
        programData = mutableListOf()
        id = intent.getIntExtra("Id", 0)
    }

    override fun onItemClicked(view: View, position: Int, type: Long, string: String) {
        val intent = Intent(this, ViewTrainingPlanListActivity::class.java)
//
        val trainingData = programData.get(position)

        when (string) {
            "pre_session" -> {
                intent.putExtra("seasonId", trainingData.pre_season?.id)
                intent.putExtra("mainId", trainingData.id)
                intent.putExtra("startDate", trainingData.start_date)
                intent.putExtra("endDate", trainingData.competition_date)
            }

            "pre_competitive" -> {
                intent.putExtra("seasonId", trainingData.pre_competitive?.id)
                intent.putExtra("mainId", trainingData.id)
                intent.putExtra("startDate", trainingData.start_date)
                intent.putExtra("endDate", trainingData.competition_date)
            }

            "competitive" -> {
                intent.putExtra("seasonId", trainingData.competitive?.id)
                intent.putExtra("mainId", trainingData.id)
                intent.putExtra("startDate", trainingData.start_date)
                intent.putExtra("endDate", trainingData.competition_date)
            }

            "transition" -> {
                intent.putExtra("seasonId", trainingData.transition?.id)
                intent.putExtra("mainId", trainingData.id)
                intent.putExtra("startDate", trainingData.start_date)
                intent.putExtra("endDate", trainingData.competition_date)
            }
        }
        intent.putExtra("CardType", string)
        startActivity(intent)
    }
}