package com.example.trainerapp.training_plan

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.Adapter.training_plan.VewTrainingPlanAdapter
import com.example.OnItemClickListener
import com.example.model.training_plan.TrainingPlanData
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.R
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.ActivityTrainingPlanBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TrainingPlanActivity : AppCompatActivity(), OnItemClickListener.OnItemClickCallback {
    lateinit var trainingPlanBinding: ActivityTrainingPlanBinding
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient

    lateinit var viewTraining: VewTrainingPlanAdapter
    lateinit var trainingData: MutableList<TrainingPlanData.TrainingPlan>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        trainingPlanBinding = ActivityTrainingPlanBinding.inflate(layoutInflater)
        setContentView(trainingPlanBinding.root)
        initViews()
        checkButtonClick()
    }

    private fun checkButtonClick() {
        trainingPlanBinding.swipeReferesh.setOnRefreshListener {
            loadData()
        }

        trainingPlanBinding.back.setOnClickListener {
            finish()
        }

        trainingPlanBinding.cardAdd.setOnClickListener {
            startActivity(Intent(this, AddTrainingPlanActivity::class.java))
        }
    }

    private fun loadData() {
        getTrainingData()
        trainingPlanBinding.swipeReferesh.isRefreshing = false
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
                        loadData()
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@TrainingPlanActivity)
                    } else {
                        Toast.makeText(
                            this@TrainingPlanActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Toast.makeText(
                        this@TrainingPlanActivity,
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

    private fun getTrainingData() {
        try {
            trainingData = mutableListOf()
            trainingData.clear()
            apiInterface.GetTrainingPlan()?.enqueue(object : Callback<TrainingPlanData> {
                override fun onResponse(
                    call: Call<TrainingPlanData>,
                    response: Response<TrainingPlanData>
                ) {
                    trainingPlanBinding.Progress.visibility = View.GONE
                    Log.d("TAG", response.code().toString() + "")
                    val code = response.code()
                    if (code == 200) {
                        if (response.isSuccessful && response.body() != null) {
                            val data = response.body()?.data ?: mutableListOf()
                            if (data.isNotEmpty()) {
                                trainingData = data.toMutableList()
                                setAdapter(trainingData)
                            }
                        } else {
                            Log.d("DATA_TAG", "No Data Available")
                        }
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@TrainingPlanActivity)
                    } else {
                        Toast.makeText(
                            this@TrainingPlanActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<TrainingPlanData>, t: Throwable) {
                    trainingPlanBinding.Progress.visibility = View.GONE
                    Log.d("TAG Category", t.message.toString())
                    call.cancel()
                }
            })

            trainingPlanBinding.Progress.visibility = View.VISIBLE

        } catch (e: Exception) {
            Log.d("Exception", e.toString())
        }
    }

    private fun setAdapter(trainingData: MutableList<TrainingPlanData.TrainingPlan>) {
        if (trainingData.isNotEmpty()) {
            trainingPlanBinding.recyclerViewTrainingPlan.visibility = View.VISIBLE
            viewTraining = VewTrainingPlanAdapter(trainingData, this, this)
            trainingPlanBinding.recyclerViewTrainingPlan.layoutManager = LinearLayoutManager(this)
            trainingPlanBinding.recyclerViewTrainingPlan.adapter = viewTraining
        } else {
            trainingPlanBinding.recyclerViewTrainingPlan.visibility = View.GONE
        }
    }


    private fun initViews() {
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        trainingData = mutableListOf()
    }

    override fun onItemClicked(view: View, position: Int, type: Long, string: String) {
        when (string) {
            "Duplicate" -> {
                val id = type.toInt()
                if (id == null) {
                    Toast.makeText(this, "Invalid ID", Toast.LENGTH_SHORT).show()
                    return
                }
                try {
                    trainingPlanBinding.Progress.visibility = View.VISIBLE
                    apiInterface.DuplicatePlanning(id).enqueue(object : Callback<TrainingPlanData> {
                        override fun onResponse(
                            call: Call<TrainingPlanData>,
                            response: Response<TrainingPlanData>
                        ) {
                            trainingPlanBinding.Progress.visibility = View.GONE
                            Log.d("TAG", response.code().toString() + "")
                            val code = response.code()
                            if (code == 200) {
                                if (response.isSuccessful && response.body() != null) {
                                    loadData()
                                } else {
                                    val message =
                                        response.body()?.message ?: "Failed to Create Duplicate"
                                    Log.d("delete_tag", "Failed to Duplicate: ${response.code()}")
                                    Toast.makeText(
                                        this@TrainingPlanActivity,
                                        "" + message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else if (code == 403) {
                                Utils.setUnAuthDialog(this@TrainingPlanActivity)
                            } else {
                                Toast.makeText(
                                    this@TrainingPlanActivity,
                                    "" + response.message(),
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                call.cancel()
                            }
                        }

                        override fun onFailure(call: Call<TrainingPlanData>, t: Throwable) {
                            trainingPlanBinding.Progress.visibility = View.GONE
                            Log.d("delete_tag", "Error: ${t.message}")
                            Toast.makeText(this@TrainingPlanActivity, t.message, Toast.LENGTH_SHORT)
                                .show()
                            call.cancel()
                        }
                    })
                } catch (e: Exception) {
                    trainingPlanBinding.Progress.visibility = View.GONE
                    Log.d("Exception", "${e.message}")
                }
            }

            "Delete" -> {

                val dialog = Dialog(this)
                val view = LayoutInflater.from(this).inflate(R.layout.delete_layout, null)
                dialog.setContentView(view)
                dialog.setCancelable(false)
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                val title = view.findViewById<TextView>(R.id.dialogTitle)
                val editTitle = view.findViewById<TextView>(R.id.editTextTitle)
                val addButton = view.findViewById<TextView>(R.id.okButton)
                val cancelButton = view.findViewById<TextView>(R.id.cancelButton)

                title.text = "Alert"
                editTitle.text = "Are You Sure you want to Delete ?"
                addButton.text = "Ok"
                cancelButton.text = "Cancel"

                addButton.setOnClickListener {
                    trainingPlanBinding.main.setBackgroundColor(resources.getColor(R.color.black))
                    deleteData(type)
                    dialog.dismiss()
                }

                cancelButton.setOnClickListener {
                    trainingPlanBinding.main.setBackgroundColor(resources.getColor(R.color.black))
                    dialog.dismiss()
                }
                dialog.show()
            }

            "Edit" -> {
                val intent = Intent(this, EditTrainingPlanActivity::class.java)
                intent.putExtra("Id", type.toInt())
                startActivity(intent)
            }

        }
    }

    private fun deleteData(type: Long) {
        val id = type.toInt()
        if (id == null) {
            Toast.makeText(this, "Invalid ID", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            trainingPlanBinding.Progress.visibility = View.VISIBLE
            apiInterface.DeletePlanning(id).enqueue(object : Callback<TrainingPlanData> {
                override fun onResponse(
                    call: Call<TrainingPlanData>,
                    response: Response<TrainingPlanData>
                ) {
                    trainingPlanBinding.Progress.visibility = View.GONE
                    Log.d("TAG", response.code().toString() + "")
                    val code = response.code()
                    if (code == 200) {
                        if (response.isSuccessful && response.body() != null) {
                            val message = response.body()?.message ?: "Item deleted"
                            Toast.makeText(this@TrainingPlanActivity, message, Toast.LENGTH_SHORT)
                                .show()
                            loadData()
                        } else {
                            val message = response.body()?.message ?: "Failed to delete"
                            Log.d("delete_tag", "Failed to delete: ${response.code()}")
                            Toast.makeText(
                                this@TrainingPlanActivity,
                                "" + message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@TrainingPlanActivity)
                    } else {
                        Toast.makeText(
                            this@TrainingPlanActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<TrainingPlanData>, t: Throwable) {
                    trainingPlanBinding.Progress.visibility = View.GONE
                    Log.d("delete_tag", "Error: ${t.message}")
                    Toast.makeText(this@TrainingPlanActivity, t.message, Toast.LENGTH_SHORT).show()
                    call.cancel()
                }
            })
        } catch (e: Exception) {
            trainingPlanBinding.Progress.visibility = View.GONE
            Log.d("Exception", "${e.message}")
        }
    }
}