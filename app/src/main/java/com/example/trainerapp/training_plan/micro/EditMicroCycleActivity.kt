package com.example.trainerapp.training_plan.micro

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.Adapter.training_plan.view.AbilitiesAdapter
import com.example.OnItemClickListener
import com.example.model.training_plan.MicroCycle.AbilityData
import com.example.model.training_plan.MicroCycle.AddAblilityClass
import com.example.model.training_plan.MicroCycle.AddMicrocycleCompatitive
import com.example.model.training_plan.MicroCycle.AddMicrocyclePreCompatitive
import com.example.model.training_plan.MicroCycle.AddMicrocyclePreSeason
import com.example.model.training_plan.MicroCycle.AddMicrocycleTransition
import com.example.model.training_plan.MicroCycle.GetMicrocycle
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.PreferencesManager
import com.example.trainerapp.R
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.ActivityEditMicroCycleBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditMicroCycleActivity : AppCompatActivity() {

    lateinit var editMicroCycleActivity: ActivityEditMicroCycleBinding

    private lateinit var apiInterface: APIInterface
    private lateinit var preferenceManager: PreferencesManager
    private lateinit var apiClient: APIClient

    private lateinit var ablilityContainer: LinearLayout
    private var splist: MutableList<AbilityData> = mutableListOf()
    private lateinit var trainingPlanContainer: LinearLayout
    private var programData: MutableList<GetMicrocycle.Data> = mutableListOf()
    private var seasonid: Int = 0
    private var mainId: Int = 0
    private var startdate: String? = null
    private var endDate: String? = null
    private var cardType: String? = null
    private val missingIndices = mutableListOf<Int>()
    var size = 0

    private var preSeason: MutableList<AddMicrocyclePreSeason> = mutableListOf()
    private var preCompetitive: MutableList<AddMicrocyclePreCompatitive> = mutableListOf()
    private var competitive: MutableList<AddMicrocycleCompatitive> = mutableListOf()
    private var transient: MutableList<AddMicrocycleTransition> = mutableListOf()

    private val trainingPlanLayouts = mutableListOf<View>()
    private val selectedAbilityIds = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        editMicroCycleActivity = ActivityEditMicroCycleBinding.inflate(layoutInflater)
        setContentView(editMicroCycleActivity.root)
        initViews()
        loadData()
        buttonCLick()

    }

    private fun buttonCLick() {
        editMicroCycleActivity.cardSave.setOnClickListener {
            try {
                programData.clear()

                when (cardType) {

                    "pre_session" -> {
                        editMesocyclePresession()
                    }

                    "pre_competitive" -> {
                        editMesocyclePreCompatitive()
                    }

                    "competitive" -> {
                        editMesocycleCompatitive()
                    }

                    "transition" -> {
                        editMesocycleTransition()
                    }

                    else -> {
                        Toast.makeText(this, "invalid card type", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (e: Exception) {
                Log.d("errror", e.message.toString())
            }
        }

        editMicroCycleActivity.add.setOnClickListener {
            addTrainingPlan()
        }

        editMicroCycleActivity.back.setOnClickListener {
            finish()
        }

        editMicroCycleActivity.cardAddAbility.setOnClickListener {
            showAbilityDialog()
        }
    }

    private fun initViews() {
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        preferenceManager = PreferencesManager(this)

        seasonid = intent.getIntExtra("SeasonId", 0)
        mainId = intent.getIntExtra("mainId", 0)
        startdate = intent.getStringExtra("StartDate")
        endDate = intent.getStringExtra("EndDate")
        cardType = intent.getStringExtra("CardType")
        ablilityContainer = editMicroCycleActivity.chvvp
        trainingPlanContainer = editMicroCycleActivity.linerAddTraining

        Log.d(
            "EditeMesocycle",
            "Id: $seasonid, MainId: $mainId, StatDate: $startdate, EndDate: $endDate, CardType: $cardType"
        )

    }

    private fun loadData() {
        try {
            programData.clear()
            when (cardType) {

                "pre_session" -> {
                    getPreSessionData(seasonid)
                }

                "pre_competitive" -> {
                    getPreCompatiiveData(seasonid)
                }

                "competitive" -> {
                    getCompatiiveData(seasonid)
                }

                "transition" -> {
                    getTransitionData(seasonid)
                }

                else -> {
                    Toast.makeText(this, "invalid card type", Toast.LENGTH_SHORT).show()
                }
            }

        } catch (e: Exception) {
            Log.d("errror", e.message.toString())
        }
    }

    private fun getPreSessionData(id: Int) {
        try {
            editMicroCycleActivity.progresBar.visibility = View.VISIBLE
            apiInterface.GetMicrocyclePreSeason(id).enqueue(object : Callback<GetMicrocycle> {
                override fun onResponse(
                    call: Call<GetMicrocycle>,
                    response: Response<GetMicrocycle>
                ) {
                    editMicroCycleActivity.progresBar.visibility = View.GONE
                    Log.d("TAG", response.code().toString() + "")
                    val code = response.code()
                    if (code == 200) {
                        if (response.isSuccessful) {
                            response.body()?.let { responseBody ->
                                programData = responseBody.data!!.toMutableList()
                                for (mesocycles in programData) {
                                    addTrainingPlan(mesocycles)
                                }
                            }
                        } else {
                            Log.e("Error", "Failed to fetch data")
                        }
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@EditMicroCycleActivity)
                    } else {
                        Toast.makeText(
                            this@EditMicroCycleActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<GetMicrocycle>, t: Throwable) {
                    editMicroCycleActivity.progresBar.visibility = View.GONE
                    Log.e("Error", "Network Error: ${t.message}")
                }
            })
        } catch (e: Exception) {
            Log.d("Excation", e.message.toString())
        }
    }

    private fun getPreCompatiiveData(id: Int) {
        try {
            editMicroCycleActivity.progresBar.visibility = View.VISIBLE
            apiInterface.GetMicrocyclePreCompatitive(id).enqueue(object : Callback<GetMicrocycle> {
                override fun onResponse(
                    call: Call<GetMicrocycle>,
                    response: Response<GetMicrocycle>
                ) {
                    editMicroCycleActivity.progresBar.visibility = View.GONE
                    Log.d("TAG", response.code().toString() + "")
                    val code = response.code()
                    if (code == 200) {
                        if (response.isSuccessful) {
                            response.body()?.let { responseBody ->
                                programData = responseBody.data!!.toMutableList()
                                for (mesocycles in programData) {
                                    addTrainingPlan(mesocycles)
                                }
                            }
                        } else {
                            Log.e("Error", "Failed to fetch data")
                        }
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@EditMicroCycleActivity)
                    } else {
                        Toast.makeText(
                            this@EditMicroCycleActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<GetMicrocycle>, t: Throwable) {
                    editMicroCycleActivity.progresBar.visibility = View.GONE
                    Log.e("Error", "Network Error: ${t.message}")
                }
            })
        } catch (e: Exception) {
            Log.d("Excation", e.message.toString())
        }
    }

    private fun getTransitionData(id: Int) {
        try {
            editMicroCycleActivity.progresBar.visibility = View.VISIBLE
            apiInterface.GetMicrocycleTransition(id).enqueue(object : Callback<GetMicrocycle> {
                override fun onResponse(
                    call: Call<GetMicrocycle>,
                    response: Response<GetMicrocycle>
                ) {
                    editMicroCycleActivity.progresBar.visibility = View.GONE
                    Log.d("TAG", response.code().toString() + "")
                    val code = response.code()
                    if (code == 200) {
                        if (response.isSuccessful) {
                            response.body()?.let { responseBody ->
                                programData = responseBody.data!!.toMutableList()
                                for (mesocycles in programData) {
                                    addTrainingPlan(mesocycles)
                                }
                            }
                        } else {
                            Log.e("Error", "Failed to fetch data")
                        }
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@EditMicroCycleActivity)
                    } else {
                        Toast.makeText(
                            this@EditMicroCycleActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<GetMicrocycle>, t: Throwable) {
                    editMicroCycleActivity.progresBar.visibility = View.GONE
                    Log.e("Error", "Network Error: ${t.message}")
                }
            })
        } catch (e: Exception) {
            Log.d("Excation", e.message.toString())
        }
    }

    private fun getCompatiiveData(id: Int) {
        try {
            editMicroCycleActivity.progresBar.visibility = View.VISIBLE
            apiInterface.GetMicrocycleCompatitive(id).enqueue(object : Callback<GetMicrocycle> {
                override fun onResponse(
                    call: Call<GetMicrocycle>,
                    response: Response<GetMicrocycle>
                ) {
                    editMicroCycleActivity.progresBar.visibility = View.GONE
                    Log.d("TAG", response.code().toString() + "")
                    val code = response.code()
                    if (code == 200) {
                        if (response.isSuccessful) {
                            response.body()?.let { responseBody ->
                                programData = responseBody.data!!.toMutableList()
                                for (mesocycles in programData) {
                                    addTrainingPlan(mesocycles)
                                }
                            }
                        } else {
                            Log.e("Error", "Failed to fetch data")
                        }
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@EditMicroCycleActivity)
                    } else {
                        Toast.makeText(
                            this@EditMicroCycleActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<GetMicrocycle>, t: Throwable) {
                    editMicroCycleActivity.progresBar.visibility = View.GONE
                    Log.e("Error", "Network Error: ${t.message}")
                }
            })
        } catch (e: Exception) {
            Log.d("Excation", e.message.toString())
        }
    }

    private fun addTrainingPlan(mesocycle: GetMicrocycle.Data? = null) {
        try {
            val indexToAdd = if (missingIndices.isNotEmpty()) {
                val reusedIndex = missingIndices.removeAt(0)
                Log.d("AddTrainingPlan", "Reusing missing index: $reusedIndex")
                reusedIndex
            } else {
                trainingPlanLayouts.size
            }

            val newTrainingPlanLayout = LayoutInflater.from(this)
                .inflate(R.layout.add_microcycle_layout, trainingPlanContainer, false)

            val nameEditText: AppCompatEditText =
                newTrainingPlanLayout.findViewById(R.id.ent_pre_sea_name)
            nameEditText.setText("Mesocycle ${indexToAdd + 1}")

            val startDateEditText: AppCompatEditText =
                newTrainingPlanLayout.findViewById(R.id.ent_start_date_liner)
            val endDateEditText: AppCompatEditText =
                newTrainingPlanLayout.findViewById(R.id.ent_ent_date_liner)
            val seekBar: SeekBar = newTrainingPlanLayout.findViewById(R.id.seekbar_workload_layout)

            val workloadTextView: TextView =
                newTrainingPlanLayout.findViewById(R.id.edit_worklord_txt)
            workloadTextView.setOnClickListener {
                Log.d("ClickEvent", "TextView clicked, showing dialog")

                val currentProgress = seekBar.progress
                showWorkloadDialog(object : OnItemClickListener.WorkloadDialogListener {
                    override fun onWorkloadProgressSelected(progress: Int, colorCode: String) {
                        seekBar.progress = progress
                        Log.d("ProgressSet", "Progress: $progress, Color: $colorCode")
                    }
                }, currentProgress)
            }


            seekBar.isEnabled = false

            if (mesocycle != null) {
                startDateEditText.setText(mesocycle.start_date)
                endDateEditText.setText(mesocycle.end_date)
                seekBar.progress = mesocycle.workload ?: 0
            }

            startDateEditText.setOnClickListener {
                Utils.selectDate(this, startDateEditText)
            }

            endDateEditText.setOnClickListener {
                Utils.selectDate(this, endDateEditText)
            }

            trainingPlanLayouts.add(newTrainingPlanLayout)
            trainingPlanContainer.addView(newTrainingPlanLayout)

            // Delete button listener
            val delete: ImageView = newTrainingPlanLayout.findViewById(R.id.img_delete)
            delete.setOnClickListener {
                trainingPlanContainer.removeView(newTrainingPlanLayout)
                trainingPlanLayouts.removeAt(indexToAdd)
                removeTrainingPlan(newTrainingPlanLayout, mesocycle!!.id!!)
            }

        } catch (e: Exception) {
            Log.d("TAG", "addTrainingPlan Error: ${e.message}")
        }
    }

    private fun removeTrainingPlan(trainingPlanLayout: View, cycleId: Int) {

        when (cardType) {
            "pre_session" -> {
                editMicroCycleActivity.progresBar.visibility = View.VISIBLE
                apiInterface.delete_MicrocyclePreSession(id = cycleId, psid = seasonid)
                    .enqueue(object : Callback<GetMicrocycle> {
                        override fun onResponse(
                            call: Call<GetMicrocycle>,
                            response: Response<GetMicrocycle>
                        ) {
                            editMicroCycleActivity.progresBar.visibility = View.GONE
                            Log.d("delete_tag", "Invalid ID ${response.code()} ")
                            Log.d("delete_tag", "Invalid ID ${response.body()} ")
                            Log.d("delete_tag", "Invalid ID ${response.errorBody()} ")

                            Log.d("TAG", response.code().toString() + "")
                            val code = response.code()
                            if (code == 200) {
                                if (response.isSuccessful && response.body() != null) {
                                    val message = response.body()?.message ?: "Item deleted"
                                    Toast.makeText(
                                        this@EditMicroCycleActivity,
                                        message,
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                } else {
                                    Log.d("delete_tag", "Failed to delete: ${response.code()}")
                                    Toast.makeText(
                                        this@EditMicroCycleActivity,
                                        "Failed to delete",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    loadData()
                                }
                            } else if (code == 403) {
                                Utils.setUnAuthDialog(this@EditMicroCycleActivity)
                            } else {
                                Toast.makeText(
                                    this@EditMicroCycleActivity,
                                    "" + response.message(),
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                call.cancel()
                            }


                        }

                        override fun onFailure(call: Call<GetMicrocycle>, t: Throwable) {
                            editMicroCycleActivity.progresBar.visibility = View.GONE
                            Log.d("delete_tag", "Error: ${t.message}")
                            Toast.makeText(
                                this@EditMicroCycleActivity,
                                t.message,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            call.cancel()
                        }
                    })
            }

            "pre_competitive" -> {
                editMicroCycleActivity.progresBar.visibility = View.VISIBLE
                apiInterface.delete_MicrocyclePreCompatitive(id = cycleId, psid = seasonid)
                    .enqueue(object : Callback<GetMicrocycle> {
                        override fun onResponse(
                            call: Call<GetMicrocycle>,
                            response: Response<GetMicrocycle>
                        ) {
                            editMicroCycleActivity.progresBar.visibility = View.GONE
                            Log.d("delete_tag", "Invalid ID ${response.code()} ")
                            Log.d("delete_tag", "Invalid ID ${response.body()} ")
                            Log.d("delete_tag", "Invalid ID ${response.errorBody()} ")

                            Log.d("TAG", response.code().toString() + "")
                            val code = response.code()
                            if (code == 200) {
                                if (response.isSuccessful && response.body() != null) {
                                    val message = response.body()?.message ?: "Item deleted"
                                    Toast.makeText(
                                        this@EditMicroCycleActivity,
                                        message,
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                } else {
                                    Log.d("delete_tag", "Failed to delete: ${response.code()}")
                                    Toast.makeText(
                                        this@EditMicroCycleActivity,
                                        "Failed to delete",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    loadData()
                                }
                            } else if (code == 403) {
                                Utils.setUnAuthDialog(this@EditMicroCycleActivity)
                            } else {
                                Toast.makeText(
                                    this@EditMicroCycleActivity,
                                    "" + response.message(),
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                call.cancel()
                            }


                        }

                        override fun onFailure(call: Call<GetMicrocycle>, t: Throwable) {
                            editMicroCycleActivity.progresBar.visibility = View.GONE
                            Log.d("delete_tag", "Error: ${t.message}")
                            Toast.makeText(
                                this@EditMicroCycleActivity,
                                t.message,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            call.cancel()
                        }
                    })
            }

            "competitive" -> {
                editMicroCycleActivity.progresBar.visibility = View.VISIBLE
                apiInterface.delete_MicrocyclePreCompatitive(id = cycleId, psid = seasonid)
                    .enqueue(object : Callback<GetMicrocycle> {
                        override fun onResponse(
                            call: Call<GetMicrocycle>,
                            response: Response<GetMicrocycle>
                        ) {

                            Log.d("delete_tag", "Invalid ID ${response.code()} ")
                            Log.d("delete_tag", "Invalid ID ${response.body()} ")
                            Log.d("delete_tag", "Invalid ID ${response.errorBody()} ")
                            editMicroCycleActivity.progresBar.visibility = View.GONE
                            Log.d("TAG", response.code().toString() + "")
                            val code = response.code()
                            if (code == 200) {
                                if (response.isSuccessful && response.body() != null) {
                                    val message = response.body()?.message ?: "Item deleted"
                                    Toast.makeText(
                                        this@EditMicroCycleActivity,
                                        message,
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                } else {
                                    Log.d("delete_tag", "Failed to delete: ${response.code()}")
                                    Toast.makeText(
                                        this@EditMicroCycleActivity,
                                        "Failed to delete",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    loadData()
                                }
                            } else if (code == 403) {
                                Utils.setUnAuthDialog(this@EditMicroCycleActivity)
                            } else {
                                Toast.makeText(
                                    this@EditMicroCycleActivity,
                                    "" + response.message(),
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                call.cancel()
                            }


                        }

                        override fun onFailure(call: Call<GetMicrocycle>, t: Throwable) {
                            Log.d("delete_tag", "Error: ${t.message}")
                            editMicroCycleActivity.progresBar.visibility = View.GONE
                            Toast.makeText(
                                this@EditMicroCycleActivity,
                                t.message,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            call.cancel()
                        }
                    })
            }

            "transition" -> {
                editMicroCycleActivity.progresBar.visibility = View.VISIBLE
                apiInterface.delete_MicrocycleTransition(id = cycleId, psid = seasonid)
                    .enqueue(object : Callback<GetMicrocycle> {
                        override fun onResponse(
                            call: Call<GetMicrocycle>,
                            response: Response<GetMicrocycle>
                        ) {
                            editMicroCycleActivity.progresBar.visibility = View.GONE
                            Log.d("delete_tag", "Invalid ID ${response.code()} ")
                            Log.d("delete_tag", "Invalid ID ${response.body()} ")
                            Log.d("delete_tag", "Invalid ID ${response.errorBody()} ")

                            Log.d("TAG", response.code().toString() + "")
                            val code = response.code()
                            if (code == 200) {
                                if (response.isSuccessful && response.body() != null) {
                                    val message = response.body()?.message ?: "Item deleted"
                                    Toast.makeText(
                                        this@EditMicroCycleActivity,
                                        message,
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                } else {
                                    Log.d("delete_tag", "Failed to delete: ${response.code()}")
                                    Toast.makeText(
                                        this@EditMicroCycleActivity,
                                        "Failed to delete",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    loadData()
                                }
                            } else if (code == 403) {
                                Utils.setUnAuthDialog(this@EditMicroCycleActivity)
                            } else {
                                Toast.makeText(
                                    this@EditMicroCycleActivity,
                                    "" + response.message(),
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                call.cancel()
                            }


                        }

                        override fun onFailure(call: Call<GetMicrocycle>, t: Throwable) {
                            Log.d("delete_tag", "Error: ${t.message}")
                            editMicroCycleActivity.progresBar.visibility = View.GONE
                            Toast.makeText(
                                this@EditMicroCycleActivity,
                                t.message,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            call.cancel()
                        }
                    })
            }
        }
    }

    private fun editMesocyclePresession() {
        try {
            Log.d(
                "EditeData",
                "Child count in trainingPlanContainer: ${trainingPlanContainer.childCount}"
            )

            if (trainingPlanContainer.childCount == 0) {
                Toast.makeText(this, "No data to save.", Toast.LENGTH_SHORT).show()
                return
            }
            preSeason.clear()

            for (i in 0 until trainingPlanContainer.childCount) {
                val layout = trainingPlanContainer.getChildAt(i)
                val nameEditText: AppCompatEditText? = layout.findViewById(R.id.ent_pre_sea_name)
                val startDateEditText: AppCompatEditText? =
                    layout.findViewById(R.id.ent_start_date_liner)
                val endDateEditText: AppCompatEditText? =
                    layout.findViewById(R.id.ent_ent_date_liner)
                val seekBar: SeekBar = layout.findViewById(R.id.seekbar_workload_layout)
                val workloadProgress = seekBar.progress
                val (colorCode, _) = getWorkloadColorAndString(workloadProgress)


                val name = nameEditText?.text.toString().trim()
                val start = startDateEditText?.text.toString().trim()
                val end = endDateEditText?.text.toString().trim()

                if (name.isEmpty() || start.isEmpty() || end.isEmpty()) {
                    Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show()
                    return
                }

                val newName = "Microcycle ${i + 1}"

                if (preSeason.any { it.name == newName }) {
                    Toast.makeText(
                        this,
                        "Name already exists, please use a different name.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }

                Log.d("Data", "Name: $name, Start: $start, End: $end")

                preSeason.add(
                    AddMicrocyclePreSeason(
                        id = if (i < programData.size) programData[i].id else null,
                        ps_mesocycle_id = seasonid,
                        name = newName,
                        start_date = start,
                        end_date = end,
                        workload = workloadProgress,
                        workload_color = colorCode,
                        ability_ids = selectedAbilityIds
                    )
                )
            }
            editMicroCycleActivity.progresBar.visibility = View.VISIBLE
            apiInterface.EditeMicrocyclePresession(preSeason)
                ?.enqueue(object : Callback<GetMicrocycle> {
                    override fun onResponse(
                        call: Call<GetMicrocycle>,
                        response: Response<GetMicrocycle>
                    ) {
                        Log.d("TAG", "Response Code: ${response.code()}")
                        Log.d("TAG1", "Response Message: ${response.message()}")
                        editMicroCycleActivity.progresBar.visibility = View.GONE
                        Log.d("TAG", response.code().toString() + "")
                        val code = response.code()
                        if (code == 200) {
                            if (response.isSuccessful) {
                                response.body()?.let { responseBody ->
                                    if (responseBody.status == true) {
                                        Toast.makeText(
                                            this@EditMicroCycleActivity,
                                            "Success: ${responseBody.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        finish()
                                    } else {
                                        val errorMessage =
                                            responseBody.message ?: "Something went wrong"
                                        Log.e("TAG2", "Error: $errorMessage")
                                        Toast.makeText(
                                            this@EditMicroCycleActivity,
                                            errorMessage,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } ?: run {
                                    Log.e("TAG2", "Response body is null")
                                    Toast.makeText(
                                        this@EditMicroCycleActivity,
                                        "Unexpected response",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                                Log.e("TAG4", "Error Response: $errorBody")
                                Toast.makeText(
                                    this@EditMicroCycleActivity,
                                    "Error: $errorBody",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else if (code == 403) {
                            Utils.setUnAuthDialog(this@EditMicroCycleActivity)
                        } else {
                            Toast.makeText(
                                this@EditMicroCycleActivity,
                                "" + response.message(),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            call.cancel()
                        }
                    }

                    override fun onFailure(call: Call<GetMicrocycle>, t: Throwable) {
                        editMicroCycleActivity.progresBar.visibility = View.GONE
                        Log.e("TAG3", "onResponse failure: ${t.message}", t)
                        Toast.makeText(
                            this@EditMicroCycleActivity,
                            "Network error: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })

        } catch (e: Exception) {
            Log.d("TAG", "editMesocyclePresession: ${e.message}")
        }
    }

    private fun editMesocyclePreCompatitive() {
        try {
            Log.d(
                "EditeData",
                "Child count in trainingPlanContainer: ${trainingPlanContainer.childCount}"
            )

            if (trainingPlanContainer.childCount == 0) {
                Toast.makeText(this, "No data to save.", Toast.LENGTH_SHORT).show()
                return
            }
            preCompetitive.clear()

            for (i in 0 until trainingPlanContainer.childCount) {
                val layout = trainingPlanContainer.getChildAt(i)
                val nameEditText: AppCompatEditText? = layout.findViewById(R.id.ent_pre_sea_name)
                val startDateEditText: AppCompatEditText? =
                    layout.findViewById(R.id.ent_start_date_liner)
                val endDateEditText: AppCompatEditText? =
                    layout.findViewById(R.id.ent_ent_date_liner)
                val seekBar: SeekBar = layout.findViewById(R.id.seekbar_workload_layout)
                val workloadProgress = seekBar.progress
                val (colorCode, _) = getWorkloadColorAndString(workloadProgress)


                val name = nameEditText?.text.toString().trim()
                val start = startDateEditText?.text.toString().trim()
                val end = endDateEditText?.text.toString().trim()

                if (name.isEmpty() || start.isEmpty() || end.isEmpty()) {
                    Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show()
                    return
                }

                val newName = "Microcycle ${i + 1}"

                if (preCompetitive.any { it.name == newName }) {
                    Toast.makeText(
                        this,
                        "Name already exists, please use a different name.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }

                Log.d("Data", "Name: $name, Start: $start, End: $end")

                preCompetitive.add(
                    AddMicrocyclePreCompatitive(
                        id = if (i < programData.size) programData[i].id else null,
                        pc_mesocycle_id = seasonid,
                        name = newName,
                        start_date = start,
                        end_date = end,
                        workload = workloadProgress,
                        workload_color = colorCode,
                        ability_ids = selectedAbilityIds
                    )
                )
            }
            editMicroCycleActivity.progresBar.visibility = View.VISIBLE
            apiInterface.EditeMicrocyclePreCompatitive(preCompetitive)
                ?.enqueue(object : Callback<GetMicrocycle> {
                    override fun onResponse(
                        call: Call<GetMicrocycle>,
                        response: Response<GetMicrocycle>
                    ) {
                        Log.d("TAG", "Response Code: ${response.code()}")
                        Log.d("TAG1", "Response Message: ${response.message()}")
                        editMicroCycleActivity.progresBar.visibility = View.GONE
                        Log.d("TAG", response.code().toString() + "")
                        val code = response.code()
                        if (code == 200) {
                            if (response.isSuccessful) {
                                response.body()?.let { responseBody ->
                                    if (responseBody.status == true) {
                                        Toast.makeText(
                                            this@EditMicroCycleActivity,
                                            "Success: ${responseBody.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        finish()
                                    } else {
                                        val errorMessage =
                                            responseBody.message ?: "Something went wrong"
                                        Log.e("TAG2", "Error: $errorMessage")
                                        Toast.makeText(
                                            this@EditMicroCycleActivity,
                                            errorMessage,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } ?: run {
                                    Log.e("TAG2", "Response body is null")
                                    Toast.makeText(
                                        this@EditMicroCycleActivity,
                                        "Unexpected response",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                                Log.e("TAG4", "Error Response: $errorBody")
                                Toast.makeText(
                                    this@EditMicroCycleActivity,
                                    "Error: $errorBody",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else if (code == 403) {
                            Utils.setUnAuthDialog(this@EditMicroCycleActivity)
                        } else {
                            Toast.makeText(
                                this@EditMicroCycleActivity,
                                "" + response.message(),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            call.cancel()
                        }
                    }

                    override fun onFailure(call: Call<GetMicrocycle>, t: Throwable) {
                        editMicroCycleActivity.progresBar.visibility = View.GONE
                        Log.e("TAG3", "onResponse failure: ${t.message}", t)
                        Toast.makeText(
                            this@EditMicroCycleActivity,
                            "Network error: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })

        } catch (e: Exception) {
            Log.d("TAG", "editMesocyclePresession: ${e.message}")
        }
    }

    private fun editMesocycleCompatitive() {
        try {
            Log.d(
                "EditeData",
                "Child count in trainingPlanContainer: ${trainingPlanContainer.childCount}"
            )

            if (trainingPlanContainer.childCount == 0) {
                Toast.makeText(this, "No data to save.", Toast.LENGTH_SHORT).show()
                return
            }
            competitive.clear()

            for (i in 0 until trainingPlanContainer.childCount) {
                val layout = trainingPlanContainer.getChildAt(i)
                val nameEditText: AppCompatEditText? = layout.findViewById(R.id.ent_pre_sea_name)
                val startDateEditText: AppCompatEditText? =
                    layout.findViewById(R.id.ent_start_date_liner)
                val endDateEditText: AppCompatEditText? =
                    layout.findViewById(R.id.ent_ent_date_liner)
                val seekBar: SeekBar = layout.findViewById(R.id.seekbar_workload_layout)
                val workloadProgress = seekBar.progress
                val (colorCode, _) = getWorkloadColorAndString(workloadProgress)


                val name = nameEditText?.text.toString().trim()
                val start = startDateEditText?.text.toString().trim()
                val end = endDateEditText?.text.toString().trim()

                if (name.isEmpty() || start.isEmpty() || end.isEmpty()) {
                    Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show()
                    return
                }

                val newName = "Microcycle ${i + 1}"

                if (competitive.any { it.name == newName }) {
                    Toast.makeText(
                        this,
                        "Name already exists, please use a different name.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }

                Log.d("Data", "Name: $name, Start: $start, End: $end")

                competitive.add(
                    AddMicrocycleCompatitive(
                        id = if (i < programData.size) programData[i].id else null,
                        c_mesocycle_id = seasonid,
                        name = newName,
                        start_date = start,
                        end_date = end,
                        workload = workloadProgress,
                        workload_color = colorCode,
                        ability_ids = selectedAbilityIds
                    )
                )
            }
            editMicroCycleActivity.progresBar.visibility = View.VISIBLE
            apiInterface.EditeMicrocycleCompatitive(competitive)
                ?.enqueue(object : Callback<GetMicrocycle> {
                    override fun onResponse(
                        call: Call<GetMicrocycle>,
                        response: Response<GetMicrocycle>
                    ) {
                        Log.d("TAG", "Response Code: ${response.code()}")
                        Log.d("TAG1", "Response Message: ${response.message()}")
                        editMicroCycleActivity.progresBar.visibility = View.GONE
                        Log.d("TAG", response.code().toString() + "")
                        val code = response.code()
                        if (code == 200) {
                            if (response.isSuccessful) {
                                response.body()?.let { responseBody ->
                                    if (responseBody.status == true) {
                                        Toast.makeText(
                                            this@EditMicroCycleActivity,
                                            "Success: ${responseBody.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        finish()
                                    } else {
                                        val errorMessage =
                                            responseBody.message ?: "Something went wrong"
                                        Log.e("TAG2", "Error: $errorMessage")
                                        Toast.makeText(
                                            this@EditMicroCycleActivity,
                                            errorMessage,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } ?: run {
                                    Log.e("TAG2", "Response body is null")
                                    Toast.makeText(
                                        this@EditMicroCycleActivity,
                                        "Unexpected response",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                                Log.e("TAG4", "Error Response: $errorBody")
                                Toast.makeText(
                                    this@EditMicroCycleActivity,
                                    "Error: $errorBody",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else if (code == 403) {
                            Utils.setUnAuthDialog(this@EditMicroCycleActivity)
                        } else {
                            Toast.makeText(
                                this@EditMicroCycleActivity,
                                "" + response.message(),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            call.cancel()
                        }
                    }

                    override fun onFailure(call: Call<GetMicrocycle>, t: Throwable) {
                        editMicroCycleActivity.progresBar.visibility = View.GONE
                        Log.e("TAG3", "onResponse failure: ${t.message}", t)
                        Toast.makeText(
                            this@EditMicroCycleActivity,
                            "Network error: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })

        } catch (e: Exception) {
            Log.d("TAG", "editMesocyclePresession: ${e.message}")
        }
    }

    private fun editMesocycleTransition() {
        try {
            Log.d(
                "EditeData",
                "Child count in trainingPlanContainer: ${trainingPlanContainer.childCount}"
            )

            if (trainingPlanContainer.childCount == 0) {
                Toast.makeText(this, "No data to save.", Toast.LENGTH_SHORT).show()
                return
            }
            transient.clear()

            for (i in 0 until trainingPlanContainer.childCount) {
                val layout = trainingPlanContainer.getChildAt(i)
                val nameEditText: AppCompatEditText? = layout.findViewById(R.id.ent_pre_sea_name)
                val startDateEditText: AppCompatEditText? =
                    layout.findViewById(R.id.ent_start_date_liner)
                val endDateEditText: AppCompatEditText? =
                    layout.findViewById(R.id.ent_ent_date_liner)
                val seekBar: SeekBar = layout.findViewById(R.id.seekbar_workload_layout)
                val workloadProgress = seekBar.progress
                val (colorCode, _) = getWorkloadColorAndString(workloadProgress)


                val name = nameEditText?.text.toString().trim()
                val start = startDateEditText?.text.toString().trim()
                val end = endDateEditText?.text.toString().trim()

                if (name.isEmpty() || start.isEmpty() || end.isEmpty()) {
                    Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show()
                    return
                }

                val newName = "Microcycle ${i + 1}"

                if (transient.any { it.name == newName }) {
                    Toast.makeText(
                        this,
                        "Name already exists, please use a different name.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }

                Log.d("Data", "Name: $name, Start: $start, End: $end")

                transient.add(
                    AddMicrocycleTransition(
                        id = if (i < programData.size) programData[i].id else null,
                        pt_mesocycle_id = seasonid,
                        name = newName,
                        start_date = start,
                        end_date = end,
                        workload = workloadProgress,
                        workload_color = colorCode,
                        ability_ids = selectedAbilityIds
                    )
                )
            }
            editMicroCycleActivity.progresBar.visibility = View.VISIBLE
            apiInterface.EditeMicrocycleTransition(transient)
                ?.enqueue(object : Callback<GetMicrocycle> {
                    override fun onResponse(
                        call: Call<GetMicrocycle>,
                        response: Response<GetMicrocycle>
                    ) {
                        Log.d("TAG", "Response Code: ${response.code()}")
                        Log.d("TAG1", "Response Message: ${response.message()}")
                        editMicroCycleActivity.progresBar.visibility = View.GONE
                        Log.d("TAG", response.code().toString() + "")
                        val code = response.code()
                        if (code == 200) {
                            if (response.isSuccessful) {
                                response.body()?.let { responseBody ->
                                    if (responseBody.status == true) {
                                        Toast.makeText(
                                            this@EditMicroCycleActivity,
                                            "Success: ${responseBody.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        finish()
                                    } else {
                                        val errorMessage =
                                            responseBody.message ?: "Something went wrong"
                                        Log.e("TAG2", "Error: $errorMessage")
                                        Toast.makeText(
                                            this@EditMicroCycleActivity,
                                            errorMessage,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } ?: run {
                                    Log.e("TAG2", "Response body is null")
                                    Toast.makeText(
                                        this@EditMicroCycleActivity,
                                        "Unexpected response",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                                Log.e("TAG4", "Error Response: $errorBody")
                                Toast.makeText(
                                    this@EditMicroCycleActivity,
                                    "Error: $errorBody",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else if (code == 403) {
                            Utils.setUnAuthDialog(this@EditMicroCycleActivity)
                        } else {
                            Toast.makeText(
                                this@EditMicroCycleActivity,
                                "" + response.message(),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            call.cancel()
                        }
                    }

                    override fun onFailure(call: Call<GetMicrocycle>, t: Throwable) {
                        editMicroCycleActivity.progresBar.visibility = View.GONE
                        Log.e("TAG3", "onResponse failure: ${t.message}", t)
                        Toast.makeText(
                            this@EditMicroCycleActivity,
                            "Network error: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })

        } catch (e: Exception) {
            Log.d("TAG", "editMesocyclePresession: ${e.message}")
        }
    }


    private fun showWorkloadDialog(
        listener: OnItemClickListener.WorkloadDialogListener,
        currentProgress: Int
    ) {
        try {
            Log.d("Dialog", "Preparing to show dialog")

            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.dialog_workload)
            dialog.window?.apply {
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                val displayMetrics = DisplayMetrics()
                windowManager.defaultDisplay.getMetrics(displayMetrics)
                val width = (displayMetrics.widthPixels * 0.9f).toInt()
                setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)
            }

            val seekBar: SeekBar = dialog.findViewById(R.id.seekbar_workload)
            seekBar.progress = currentProgress

            val applyButton: Button = dialog.findViewById(R.id.btn_apply)
            val closeButton: Button = dialog.findViewById(R.id.btn_cancel)

            applyButton.setOnClickListener {
                val progress = seekBar.progress
                val (colorCode, _) = getWorkloadColorAndString(progress)
                listener.onWorkloadProgressSelected(progress, colorCode)
                Log.d("Dialog", "Progress: $progress, Color: $colorCode")
                dialog.dismiss()
            }

            closeButton.setOnClickListener { dialog.dismiss() }

            dialog.show()
            Log.d("Dialog", "Dialog displayed successfully")

        } catch (e: Exception) {
            Log.e("Exception", "Error showing dialog: ${e.message}")
        }
    }

    private fun getWorkloadColorAndString(progress: Int): Pair<String, String> {
        return when {
            progress in 82..100 -> Pair("#FF0000", "$progress") // Red
            progress in 60..81 -> Pair("#F1790B", "$progress") // Orange
            progress in 45..59 -> Pair("#E2C110", "$progress") // Yellow
            progress in 10..44 -> Pair("#10E218", "$progress") // Green
            progress in 0..9 -> Pair("#F3F3F3", "$progress") // Light Grey
            else -> Pair("#000000", "Invalid") // Default Black
        }
    }

    private fun showAbilityDialog() {
        try {
            Log.d("Dialog", "Preparing to show dialog")
            val dialog = Dialog(this)
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics) // Ensure you get the metrics
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val width = (displayMetrics.widthPixels * 0.9f).toInt()
            val height = WindowManager.LayoutParams.WRAP_CONTENT
            dialog.window!!.setLayout(width, height)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.dialog_abilitie)
            dialog.show()

            val save = dialog.findViewById<Button>(R.id.appCompatButton)
            save.setOnClickListener {
                saveSelectedAbility()
                dialog.cancel()
            }
            val add = dialog.findViewById<LinearLayout>(R.id.add_layout)
            add.setOnClickListener { showAddAbilitiesDialog() }

            val recyclerView: RecyclerView = dialog.findViewById(R.id.recyclerView)
            recyclerView.layoutManager = LinearLayoutManager(this)

            fetchAbilities(recyclerView)

            Log.d("Dialog", "Dialog displayed successfully")
        } catch (e: Exception) {
            Log.e("Exception", "Error showing dialog: ${e.message}")
        }
    }

    private fun fetchAbilities(recyclerView: RecyclerView) {
        apiInterface.Get_Abilitiees()?.enqueue(object : Callback<AddAblilityClass> {
            override fun onResponse(
                call: Call<AddAblilityClass>,
                response: Response<AddAblilityClass>
            ) {
                Log.d("TAG", "Response code: ${response.code()}")

                when (response.code()) {
                    200 -> {
                        val abilityDataList = response.body()?.data
                        if (!abilityDataList.isNullOrEmpty()) {
                            splist.clear()
                            splist.addAll(abilityDataList)

                            Log.d("success", "${splist}")

                            recyclerView.adapter =
                                AbilitiesAdapter(splist, this@EditMicroCycleActivity)
                        } else {
                            Log.d("DATA_TAG", "Response body is null or empty")
                        }
                    }

                    403 -> {
                        Utils.setUnAuthDialog(this@EditMicroCycleActivity)
                    }

                    else -> {
                        Toast.makeText(
                            this@EditMicroCycleActivity,
                            response.message(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<AddAblilityClass>, t: Throwable) {
                Log.d("TAG Category", "Error: ${t.message}")
            }
        })
    }

    private fun saveSelectedAbility() {
        try {
            Log.d("splist Contents", splist.toString())

            val selectedAbilities = splist.filter { it.isSelected }

            ablilityContainer.removeAllViews()

            Log.d("Selected Abilities", "Count: ${selectedAbilities.size}")

            selectedAbilityIds.clear()
            selectedAbilityIds.addAll(selectedAbilities.map { it.id })

            editMicroCycleActivity.card.visibility =
                if (selectedAbilities.isNotEmpty()) View.VISIBLE else View.GONE

            for (ability in selectedAbilities) {
                val abilityLayout = LayoutInflater.from(this)
                    .inflate(R.layout.ability_item, ablilityContainer, false)

                val textView: TextView = abilityLayout.findViewById(R.id.ability_txt)
                textView.text = ability.name

                ablilityContainer.addView(abilityLayout)
            }

            Log.d("Abilities Added", "Number of abilities added: ${selectedAbilities.size}")

            if (selectedAbilities.isEmpty()) {
                Log.d("Selection Info", "No abilities were selected.")
            }
        } catch (e: Exception) {
            Log.e("Exception", "Error: ${e.message}")
        }
    }

    private fun showAddAbilitiesDialog() {
        try {
            Log.d("Dialog", "Preparing to show dialog")

            val dialog = Dialog(this)
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)


            dialog.window?.apply {
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                val width = (displayMetrics.widthPixels * 0.9f).toInt()
                val height = WindowManager.LayoutParams.WRAP_CONTENT
                setLayout(width, height)
            }

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.dialog_add_abilities)
            dialog.show()

            val abilityName: EditText = dialog.findViewById(R.id.etAbilities)
            val addButton: Button = dialog.findViewById(R.id.btnAdd)

            addButton.setOnClickListener {
                val name = abilityName.text.toString().trim()

                if (name.isNotEmpty()) {
                    apiInterface.Create_Abilities(name)
                        ?.enqueue(object : Callback<AddAblilityClass> {
                            override fun onResponse(
                                call: Call<AddAblilityClass>,
                                response: Response<AddAblilityClass>
                            ) {
                                Log.d("APIResponse", "Response: ${response.code()}")

                                if (response.isSuccessful) {
                                    val message =
                                        response.body()?.message ?: "Ability added successfully"
                                    Toast.makeText(
                                        this@EditMicroCycleActivity,
                                        message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    dialog.dismiss()
                                } else {
                                    val errorBody =
                                        response.errorBody()?.string() ?: "Unknown error"
                                    Log.e("API Error", "Response: ${response.code()} - $errorBody")
                                    Toast.makeText(
                                        this@EditMicroCycleActivity,
                                        "Error: $errorBody",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            override fun onFailure(call: Call<AddAblilityClass>, t: Throwable) {
                                Log.e("API Error", "Error: ${t.message}")
                                Toast.makeText(
                                    this@EditMicroCycleActivity,
                                    "Network error: ${t.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                } else {
                    Toast.makeText(this, "Ability name cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Log.e("Exception", "Error showing dialog: ${e.message}")
        }
    }
}