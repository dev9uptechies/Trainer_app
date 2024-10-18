package com.example.trainerapp.training_plan.view_planning_cycle

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import com.example.model.training_plan.cycles.AddMesocycleCompatitive
import com.example.model.training_plan.cycles.AddMesocyclePreCompatitive
import com.example.model.training_plan.cycles.AddMesocyclePresession
import com.example.model.training_plan.cycles.AddMesocycleTransition
import com.example.model.training_plan.cycles.GetMessocyclePreSession
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.PreferencesManager
import com.example.trainerapp.R
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.ActivityEditMesocycleListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EditMesocycleListActivity : AppCompatActivity() {
    lateinit var editMesocycleListBinding: ActivityEditMesocycleListBinding
    private lateinit var apiInterface: APIInterface
    private lateinit var preferenceManager: PreferencesManager
    private lateinit var apiClient: APIClient

    private lateinit var trainingPlanContainer: LinearLayout
    private var programData: MutableList<GetMessocyclePreSession.Data> = mutableListOf()
    private var id: Int = 0
    private var seasonid: Int = 0
    private var mainId: Int = 0
    private var startdate: String? = null
    private var endDate: String? = null
    private var cardType: String? = null
    private val missingIndices = mutableListOf<Int>()
    var size = 0

    private val trainingPlanLayouts = mutableListOf<View>()


    private var preSeason: MutableList<AddMesocyclePresession> = mutableListOf()
    private var preCompatitive: MutableList<AddMesocyclePreCompatitive> = mutableListOf()
    private var Compatitive: MutableList<AddMesocycleCompatitive> = mutableListOf()
    private var Transition: MutableList<AddMesocycleTransition> = mutableListOf()

    private var startDateMillis: Long = 0
    private var endDateMillis: Long = 0

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
                        Utils.setUnAuthDialog(this@EditMesocycleListActivity)
                    } else {
                        Toast.makeText(
                            this@EditMesocycleListActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Toast.makeText(
                        this@EditMesocycleListActivity,
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
        editMesocycleListBinding = ActivityEditMesocycleListBinding.inflate(layoutInflater)
        setContentView(editMesocycleListBinding.root)
        initViews()
        loadData()
        checkButtonClick()
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

    private fun checkButtonClick() {
        editMesocycleListBinding.add.setOnClickListener {
            addTrainingPlan()
        }

        editMesocycleListBinding.back.setOnClickListener {
            finish()
        }

        editMesocycleListBinding.cardSave.setOnClickListener {

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

        Log.d(
            "EditeMesocycle",
            "Id: $seasonid, MainId: $mainId, StatDate: $startdate, EndDate: $endDate, CardType: $cardType"
        )
        editMesocycleListBinding.startDate.text = startdate
        editMesocycleListBinding.endDate.text = endDate

        trainingPlanContainer = editMesocycleListBinding.linerAddTraining
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
                val mesocycles: TextView? = layout.findViewById(R.id.linear_days_list)

                val name = nameEditText?.text.toString().trim()
                val start = startDateEditText?.text.toString().trim()
                val end = endDateEditText?.text.toString().trim()

                if (name.isEmpty() || start.isEmpty() || end.isEmpty()) {
                    Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show()
                    return
                }

                val newName = "Mesocycle ${i + 1}"

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
                    AddMesocyclePresession(
                        id = if (i < programData.size) programData[i].id else null,
                        planning_ps_id = seasonid.toString(),
                        name = newName,
                        start_date = start,
                        end_date = end,
                        periods = mesocycles?.text.toString()
                    )
                )
            }
            editMesocycleListBinding.progresBar.visibility = View.VISIBLE
            apiInterface.EditeMesocyclePresession(preSeason)
                ?.enqueue(object : Callback<GetMessocyclePreSession> {
                    override fun onResponse(
                        call: Call<GetMessocyclePreSession>,
                        response: Response<GetMessocyclePreSession>
                    ) {
                        Log.d("TAG", "Response Code: ${response.code()}")
                        Log.d("TAG1", "Response Message: ${response.message()}")
                        editMesocycleListBinding.progresBar.visibility = View.GONE
                        Log.d("TAG", response.code().toString() + "")
                        val code = response.code()
                        if (code == 200) {
                            if (response.isSuccessful) {
                                response.body()?.let { responseBody ->
                                    if (responseBody.status == true) {
                                        Toast.makeText(
                                            this@EditMesocycleListActivity,
                                            "Success: ${responseBody.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        finish()
                                    } else {
                                        val errorMessage =
                                            responseBody.message ?: "Something went wrong"
                                        Log.e("TAG2", "Error: $errorMessage")
                                        Toast.makeText(
                                            this@EditMesocycleListActivity,
                                            errorMessage,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } ?: run {
                                    Log.e("TAG2", "Response body is null")
                                    Toast.makeText(
                                        this@EditMesocycleListActivity,
                                        "Unexpected response",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                                Log.e("TAG4", "Error Response: $errorBody")
                                Toast.makeText(
                                    this@EditMesocycleListActivity,
                                    "Error: $errorBody",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else if (code == 403) {
                            Utils.setUnAuthDialog(this@EditMesocycleListActivity)
                        } else {
                            Toast.makeText(
                                this@EditMesocycleListActivity,
                                "" + response.message(),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            call.cancel()
                        }
                    }

                    override fun onFailure(call: Call<GetMessocyclePreSession>, t: Throwable) {
                        editMesocycleListBinding.progresBar.visibility = View.GONE
                        Log.e("TAG3", "onResponse failure: ${t.message}", t)
                        Toast.makeText(
                            this@EditMesocycleListActivity,
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
            preCompatitive.clear()

            // Loop through each child in trainingPlanContainer
            for (i in 0 until trainingPlanContainer.childCount) {
                val layout = trainingPlanContainer.getChildAt(i)
                val nameEditText: AppCompatEditText? = layout.findViewById(R.id.ent_pre_sea_name)
                val startDateEditText: AppCompatEditText? =
                    layout.findViewById(R.id.ent_start_date_liner)
                val endDateEditText: AppCompatEditText? =
                    layout.findViewById(R.id.ent_ent_date_liner)
                val mesocycles: TextView? = layout.findViewById(R.id.linear_days_list)

                val name = nameEditText?.text.toString().trim()
                val start = startDateEditText?.text.toString().trim()
                val end = endDateEditText?.text.toString().trim()

                // Check for empty fields
                if (name.isEmpty() || start.isEmpty() || end.isEmpty()) {
                    Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show()
                    return
                }

                val newName = "Mesocycle ${i + 1}" // This will format names as Mesocycle 1, 2, 3...

                // Ensure the newName is unique
                if (preCompatitive.any { it.name == newName }) {
                    Toast.makeText(
                        this,
                        "Name already exists, please use a different name.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }

                Log.d("Data", "Name: $name, Start: $start, End: $end")

                preCompatitive.add(
                    AddMesocyclePreCompatitive(
                        id = if (i < programData.size) programData[i].id else null,
                        planning_pc_id = seasonid.toString(),
                        name = newName,
                        start_date = start,
                        end_date = end,
                        periods = "test"
                    )
                )
            }
            editMesocycleListBinding.progresBar.visibility = View.VISIBLE
            apiInterface.EditeMesocyclePreCompatitive(preCompatitive)
                ?.enqueue(object : Callback<GetMessocyclePreSession> {
                    override fun onResponse(
                        call: Call<GetMessocyclePreSession>,
                        response: Response<GetMessocyclePreSession>
                    ) {
                        Log.d("TAG", "Response Code: ${response.code()}")
                        Log.d("TAG1", "Response Message: ${response.message()}")
                        editMesocycleListBinding.progresBar.visibility = View.GONE

                        Log.d("TAG", response.code().toString() + "")
                        val code = response.code()
                        if (code == 200) {
                            if (response.isSuccessful) {
                                response.body()?.let { responseBody ->
                                    if (responseBody.status == true) {
                                        Toast.makeText(
                                            this@EditMesocycleListActivity,
                                            "Success: ${responseBody.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        finish()

                                    } else {
                                        val errorMessage =
                                            responseBody.message ?: "Something went wrong"
                                        Log.e("TAG2", "Error: $errorMessage")
                                        Toast.makeText(
                                            this@EditMesocycleListActivity,
                                            errorMessage,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } ?: run {
                                    Log.e("TAG2", "Response body is null")
                                    Toast.makeText(
                                        this@EditMesocycleListActivity,
                                        "Unexpected response",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                                Log.e("TAG4", "Error Response: $errorBody")
                                Toast.makeText(
                                    this@EditMesocycleListActivity,
                                    "Error: $errorBody",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else if (code == 403) {
                            Utils.setUnAuthDialog(this@EditMesocycleListActivity)
                        } else {
                            Toast.makeText(
                                this@EditMesocycleListActivity,
                                "" + response.message(),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            call.cancel()
                        }


                    }

                    override fun onFailure(call: Call<GetMessocyclePreSession>, t: Throwable) {
                        editMesocycleListBinding.progresBar.visibility = View.GONE
                        Log.e("TAG3", "onResponse failure: ${t.message}", t)
                        Toast.makeText(
                            this@EditMesocycleListActivity,
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
            Compatitive.clear()

            // Loop through each child in trainingPlanContainer
            for (i in 0 until trainingPlanContainer.childCount) {
                val layout = trainingPlanContainer.getChildAt(i)
                val nameEditText: AppCompatEditText? = layout.findViewById(R.id.ent_pre_sea_name)
                val startDateEditText: AppCompatEditText? =
                    layout.findViewById(R.id.ent_start_date_liner)
                val endDateEditText: AppCompatEditText? =
                    layout.findViewById(R.id.ent_ent_date_liner)
                val mesocycles: TextView? = layout.findViewById(R.id.linear_days_list)

                val name = nameEditText?.text.toString().trim()
                val start = startDateEditText?.text.toString().trim()
                val end = endDateEditText?.text.toString().trim()

                // Check for empty fields
                if (name.isEmpty() || start.isEmpty() || end.isEmpty()) {
                    Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show()
                    return
                }


                val newName = "Mesocycle ${i + 1}" // This will format names as Mesocycle 1, 2, 3...

                // Ensure the newName is unique
                if (Compatitive.any { it.name == newName }) {
                    Toast.makeText(
                        this,
                        "Name already exists, please use a different name.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }

                Log.d("Data", "Name: $name, Start: $start, End: $end")

                Compatitive.add(
                    AddMesocycleCompatitive(
                        id = if (i < programData.size) programData[i].id else null,
                        planning_c_id = seasonid.toString(),
                        name = newName,
                        start_date = start,
                        end_date = end,
                        periods = "test"
                    )
                )
            }
            editMesocycleListBinding.progresBar.visibility = View.VISIBLE
            apiInterface.EditeMesocycleCompatitive(Compatitive)
                ?.enqueue(object : Callback<GetMessocyclePreSession> {
                    override fun onResponse(
                        call: Call<GetMessocyclePreSession>,
                        response: Response<GetMessocyclePreSession>
                    ) {
                        Log.d("TAG", "Response Code: ${response.code()}")
                        Log.d("TAG1", "Response Message: ${response.message()}")
                        editMesocycleListBinding.progresBar.visibility = View.GONE

                        Log.d("TAG", response.code().toString() + "")
                        val code = response.code()
                        if (code == 200) {
                            if (response.isSuccessful) {
                                response.body()?.let { responseBody ->
                                    if (responseBody.status == true) {
                                        Toast.makeText(
                                            this@EditMesocycleListActivity,
                                            "Success: ${responseBody.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
//                                    startActivity(
//                                        Intent(
//                                            this@EditMesocycleListActivity,
//                                            ViewTraingPlanList::class.java
//                                        )
//                                    )

                                    } else {
                                        val errorMessage =
                                            responseBody.message ?: "Something went wrong"
                                        Log.e("TAG2", "Error: $errorMessage")
                                        Toast.makeText(
                                            this@EditMesocycleListActivity,
                                            errorMessage,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } ?: run {
                                    Log.e("TAG2", "Response body is null")
                                    Toast.makeText(
                                        this@EditMesocycleListActivity,
                                        "Unexpected response",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                                Log.e("TAG4", "Error Response: $errorBody")
                                Toast.makeText(
                                    this@EditMesocycleListActivity,
                                    "Error: $errorBody",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else if (code == 403) {
                            Utils.setUnAuthDialog(this@EditMesocycleListActivity)
                        } else {
                            Toast.makeText(
                                this@EditMesocycleListActivity,
                                "" + response.message(),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            call.cancel()
                        }
                    }

                    override fun onFailure(call: Call<GetMessocyclePreSession>, t: Throwable) {
                        editMesocycleListBinding.progresBar.visibility = View.GONE
                        Log.e("TAG3", "onResponse failure: ${t.message}", t)
                        Toast.makeText(
                            this@EditMesocycleListActivity,
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
            Transition.clear()

            // Loop through each child in trainingPlanContainer
            for (i in 0 until trainingPlanContainer.childCount) {
                val layout = trainingPlanContainer.getChildAt(i)
                val nameEditText: AppCompatEditText? = layout.findViewById(R.id.ent_pre_sea_name)
                val startDateEditText: AppCompatEditText? =
                    layout.findViewById(R.id.ent_start_date_liner)
                val endDateEditText: AppCompatEditText? =
                    layout.findViewById(R.id.ent_ent_date_liner)
                val mesocycles: TextView? = layout.findViewById(R.id.linear_days_list)

                val name = nameEditText?.text.toString().trim()
                val start = startDateEditText?.text.toString().trim()
                val end = endDateEditText?.text.toString().trim()

                // Check for empty fields
                if (name.isEmpty() || start.isEmpty() || end.isEmpty()) {
                    Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show()
                    return
                }


                val newName = "Mesocycle ${i + 1}" // This will format names as Mesocycle 1, 2, 3...

                // Ensure the newName is unique
                if (Transition.any { it.name == newName }) {
                    Toast.makeText(
                        this,
                        "Name already exists, please use a different name.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }

                Log.d("Data", "Name: $name, Start: $start, End: $end")

                Transition.add(
                    AddMesocycleTransition(
                        id = if (i < programData.size) programData[i].id else null,
                        planning_t_id = seasonid.toString(),
                        name = newName,
                        start_date = start,
                        end_date = end,
                        periods = "test"
                    )
                )
            }
            editMesocycleListBinding.progresBar.visibility = View.VISIBLE
            apiInterface.EditeMesocycleTransition(Transition)
                ?.enqueue(object : Callback<GetMessocyclePreSession> {
                    override fun onResponse(
                        call: Call<GetMessocyclePreSession>,
                        response: Response<GetMessocyclePreSession>
                    ) {
                        Log.d("TAG", "Response Code: ${response.code()}")
                        Log.d("TAG1", "Response Message: ${response.message()}")

                        editMesocycleListBinding.progresBar.visibility = View.GONE
                        Log.d("TAG", response.code().toString() + "")
                        val code = response.code()
                        if (code == 200) {
                            if (response.isSuccessful) {
                                response.body()?.let { responseBody ->
                                    if (responseBody.status == true) {
                                        Toast.makeText(
                                            this@EditMesocycleListActivity,
                                            "Success: ${responseBody.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
//                                    startActivity(
//                                        Intent(
//                                            this@EditMesocycleListActivity,
//                                            ViewTraingPlanList::class.java
//                                        )
//                                    )

                                    } else {
                                        val errorMessage =
                                            responseBody.message ?: "Something went wrong"
                                        Log.e("TAG2", "Error: $errorMessage")
                                        Toast.makeText(
                                            this@EditMesocycleListActivity,
                                            errorMessage,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } ?: run {
                                    Log.e("TAG2", "Response body is null")
                                    Toast.makeText(
                                        this@EditMesocycleListActivity,
                                        "Unexpected response",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                                Log.e("TAG4", "Error Response: $errorBody")
                                Toast.makeText(
                                    this@EditMesocycleListActivity,
                                    "Error: $errorBody",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else if (code == 403) {
                            Utils.setUnAuthDialog(this@EditMesocycleListActivity)
                        } else {
                            Toast.makeText(
                                this@EditMesocycleListActivity,
                                "" + response.message(),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            call.cancel()
                        }


                    }

                    override fun onFailure(call: Call<GetMessocyclePreSession>, t: Throwable) {
                        editMesocycleListBinding.progresBar.visibility = View.GONE
                        Log.e("TAG3", "onResponse failure: ${t.message}", t)
                        Toast.makeText(
                            this@EditMesocycleListActivity,
                            "Network error: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })

        } catch (e: Exception) {
            Log.d("TAG", "editMesocyclePresession: ${e.message}")
        }
    }

    private fun addTrainingPlan(mesocycle: GetMessocyclePreSession.Data? = null) {
        try {
            val indexToAdd = if (missingIndices.isNotEmpty()) {
                val reusedIndex = missingIndices.removeAt(0)
                Log.d("AddTrainingPlan", "Reusing missing index: $reusedIndex")
                reusedIndex
            } else {
                trainingPlanLayouts.size
            }

            val newTrainingPlanLayout = LayoutInflater.from(this)
                .inflate(R.layout.editetriningplanactivityadapter, trainingPlanContainer, false)

            val nameEditText: AppCompatEditText =
                newTrainingPlanLayout.findViewById(R.id.ent_pre_sea_name)


            nameEditText.setText("Mesocycle ${indexToAdd + 1}")

            val startDateEditText: AppCompatEditText =
                newTrainingPlanLayout.findViewById(R.id.ent_start_date_liner)
            val endDateEditText: AppCompatEditText =
                newTrainingPlanLayout.findViewById(R.id.ent_ent_date_liner)
            val mesocycles: TextView = newTrainingPlanLayout.findViewById(R.id.linear_days_list)

            mesocycles.text = ""

            if (mesocycle != null) {
                //nameEditText.setText(mesocycle.name)
                startDateEditText.setText(mesocycle.start_date)
                endDateEditText.setText(mesocycle.end_date)
                mesocycles.text = mesocycle.periods ?: ""
            }

            // Date selection listeners
            startDateEditText.setOnClickListener {
                selectTrainingPlanStartDate(startDateEditText)
            }

            endDateEditText.setOnClickListener {
                selectTrainingPlanEndDate(endDateEditText)
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
                editMesocycleListBinding.progresBar.visibility = View.VISIBLE
                apiInterface.delete_PreSession(id = cycleId, psid = seasonid)
                    .enqueue(object : Callback<GetMessocyclePreSession> {
                        override fun onResponse(
                            call: Call<GetMessocyclePreSession>,
                            response: Response<GetMessocyclePreSession>
                        ) {
                            editMesocycleListBinding.progresBar.visibility = View.GONE
                            Log.d("delete_tag", "Invalid ID ${response.code()} ")
                            Log.d("delete_tag", "Invalid ID ${response.body()} ")
                            Log.d("delete_tag", "Invalid ID ${response.errorBody()} ")

                            Log.d("TAG", response.code().toString() + "")
                            val code = response.code()
                            if (code == 200) {
                                if (response.isSuccessful && response.body() != null) {
                                    val message = response.body()?.message ?: "Item deleted"
                                    Toast.makeText(
                                        this@EditMesocycleListActivity,
                                        message,
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                } else {
                                    Log.d("delete_tag", "Failed to delete: ${response.code()}")
                                    Toast.makeText(
                                        this@EditMesocycleListActivity,
                                        "Failed to delete",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    loadData()
                                }
                            } else if (code == 403) {
                                Utils.setUnAuthDialog(this@EditMesocycleListActivity)
                            } else {
                                Toast.makeText(
                                    this@EditMesocycleListActivity,
                                    "" + response.message(),
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                call.cancel()
                            }


                        }

                        override fun onFailure(call: Call<GetMessocyclePreSession>, t: Throwable) {
                            editMesocycleListBinding.progresBar.visibility = View.GONE
                            Log.d("delete_tag", "Error: ${t.message}")
                            Toast.makeText(
                                this@EditMesocycleListActivity,
                                t.message,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            call.cancel()
                        }
                    })
            }

            "pre_competitive" -> {
                editMesocycleListBinding.progresBar.visibility = View.VISIBLE
                apiInterface.delete_PreCompatitive(id = cycleId, psid = seasonid)
                    .enqueue(object : Callback<GetMessocyclePreSession> {
                        override fun onResponse(
                            call: Call<GetMessocyclePreSession>,
                            response: Response<GetMessocyclePreSession>
                        ) {
                            editMesocycleListBinding.progresBar.visibility = View.GONE
                            Log.d("delete_tag", "Invalid ID ${response.code()} ")
                            Log.d("delete_tag", "Invalid ID ${response.body()} ")
                            Log.d("delete_tag", "Invalid ID ${response.errorBody()} ")

                            Log.d("TAG", response.code().toString() + "")
                            val code = response.code()
                            if (code == 200) {
                                if (response.isSuccessful && response.body() != null) {
                                    val message = response.body()?.message ?: "Item deleted"
                                    Toast.makeText(
                                        this@EditMesocycleListActivity,
                                        message,
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                } else {
                                    Log.d("delete_tag", "Failed to delete: ${response.code()}")
                                    Toast.makeText(
                                        this@EditMesocycleListActivity,
                                        "Failed to delete",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    loadData()
                                }
                            } else if (code == 403) {
                                Utils.setUnAuthDialog(this@EditMesocycleListActivity)
                            } else {
                                Toast.makeText(
                                    this@EditMesocycleListActivity,
                                    "" + response.message(),
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                call.cancel()
                            }


                        }

                        override fun onFailure(call: Call<GetMessocyclePreSession>, t: Throwable) {
                            editMesocycleListBinding.progresBar.visibility = View.GONE
                            Log.d("delete_tag", "Error: ${t.message}")
                            Toast.makeText(
                                this@EditMesocycleListActivity,
                                t.message,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            call.cancel()
                        }
                    })
            }

            "competitive" -> {
                editMesocycleListBinding.progresBar.visibility = View.VISIBLE
                apiInterface.delete_Compatitive(id = cycleId, psid = seasonid)
                    .enqueue(object : Callback<GetMessocyclePreSession> {
                        override fun onResponse(
                            call: Call<GetMessocyclePreSession>,
                            response: Response<GetMessocyclePreSession>
                        ) {

                            Log.d("delete_tag", "Invalid ID ${response.code()} ")
                            Log.d("delete_tag", "Invalid ID ${response.body()} ")
                            Log.d("delete_tag", "Invalid ID ${response.errorBody()} ")
                            editMesocycleListBinding.progresBar.visibility = View.GONE
                            Log.d("TAG", response.code().toString() + "")
                            val code = response.code()
                            if (code == 200) {
                                if (response.isSuccessful && response.body() != null) {
                                    val message = response.body()?.message ?: "Item deleted"
                                    Toast.makeText(
                                        this@EditMesocycleListActivity,
                                        message,
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                } else {
                                    Log.d("delete_tag", "Failed to delete: ${response.code()}")
                                    Toast.makeText(
                                        this@EditMesocycleListActivity,
                                        "Failed to delete",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    loadData()
                                }
                            } else if (code == 403) {
                                Utils.setUnAuthDialog(this@EditMesocycleListActivity)
                            } else {
                                Toast.makeText(
                                    this@EditMesocycleListActivity,
                                    "" + response.message(),
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                call.cancel()
                            }


                        }

                        override fun onFailure(call: Call<GetMessocyclePreSession>, t: Throwable) {
                            Log.d("delete_tag", "Error: ${t.message}")
                            editMesocycleListBinding.progresBar.visibility = View.GONE
                            Toast.makeText(
                                this@EditMesocycleListActivity,
                                t.message,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            call.cancel()
                        }
                    })
            }

            "transition" -> {
                editMesocycleListBinding.progresBar.visibility = View.VISIBLE
                apiInterface.delete_Transition(id = cycleId, psid = seasonid)
                    .enqueue(object : Callback<GetMessocyclePreSession> {
                        override fun onResponse(
                            call: Call<GetMessocyclePreSession>,
                            response: Response<GetMessocyclePreSession>
                        ) {
                            editMesocycleListBinding.progresBar.visibility = View.GONE
                            Log.d("delete_tag", "Invalid ID ${response.code()} ")
                            Log.d("delete_tag", "Invalid ID ${response.body()} ")
                            Log.d("delete_tag", "Invalid ID ${response.errorBody()} ")

                            Log.d("TAG", response.code().toString() + "")
                            val code = response.code()
                            if (code == 200) {
                                if (response.isSuccessful && response.body() != null) {
                                    val message = response.body()?.message ?: "Item deleted"
                                    Toast.makeText(
                                        this@EditMesocycleListActivity,
                                        message,
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                } else {
                                    Log.d("delete_tag", "Failed to delete: ${response.code()}")
                                    Toast.makeText(
                                        this@EditMesocycleListActivity,
                                        "Failed to delete",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    loadData()
                                }
                            } else if (code == 403) {
                                Utils.setUnAuthDialog(this@EditMesocycleListActivity)
                            } else {
                                Toast.makeText(
                                    this@EditMesocycleListActivity,
                                    "" + response.message(),
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                call.cancel()
                            }


                        }

                        override fun onFailure(call: Call<GetMessocyclePreSession>, t: Throwable) {
                            Log.d("delete_tag", "Error: ${t.message}")
                            editMesocycleListBinding.progresBar.visibility = View.GONE
                            Toast.makeText(
                                this@EditMesocycleListActivity,
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

    private fun getPreSessionData(id: Int) {
        editMesocycleListBinding.progresBar.visibility = View.VISIBLE
        apiInterface.GetMesocyclePreSession(id).enqueue(object : Callback<GetMessocyclePreSession> {
            override fun onResponse(
                call: Call<GetMessocyclePreSession>,
                response: Response<GetMessocyclePreSession>
            ) {
                editMesocycleListBinding.progresBar.visibility = View.GONE
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
                    Utils.setUnAuthDialog(this@EditMesocycleListActivity)
                } else {
                    Toast.makeText(
                        this@EditMesocycleListActivity,
                        "" + response.message(),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    call.cancel()
                }


            }

            override fun onFailure(call: Call<GetMessocyclePreSession>, t: Throwable) {
                editMesocycleListBinding.progresBar.visibility = View.GONE
                Log.e("Error", "Network Error: ${t.message}")
            }
        })
    }


    private fun getPreCompatiiveData(id: Int) {
        editMesocycleListBinding.progresBar.visibility = View.VISIBLE
        apiInterface.GetMesocyclePreCompatitive(id)
            .enqueue(object : Callback<GetMessocyclePreSession> {
                override fun onResponse(
                    call: Call<GetMessocyclePreSession>,
                    response: Response<GetMessocyclePreSession>
                ) {
                    Log.d("TAG", response.code().toString() + "")
                    editMesocycleListBinding.progresBar.visibility = View.GONE
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
                        Utils.setUnAuthDialog(this@EditMesocycleListActivity)
                    } else {
                        Toast.makeText(
                            this@EditMesocycleListActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }

                }

                override fun onFailure(call: Call<GetMessocyclePreSession>, t: Throwable) {
                    editMesocycleListBinding.progresBar.visibility = View.GONE
                    Log.e("Error", "Network Error: ${t.message}")
                }
            })
    }


    private fun getCompatiiveData(id: Int) {
        editMesocycleListBinding.progresBar.visibility = View.VISIBLE
        apiInterface.GetMesocycleCompatitive(id)
            .enqueue(object : Callback<GetMessocyclePreSession> {
                override fun onResponse(
                    call: Call<GetMessocyclePreSession>,
                    response: Response<GetMessocyclePreSession>
                ) {
                    editMesocycleListBinding.progresBar.visibility = View.GONE
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
                        Utils.setUnAuthDialog(this@EditMesocycleListActivity)
                    } else {
                        Toast.makeText(
                            this@EditMesocycleListActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }

                }

                override fun onFailure(call: Call<GetMessocyclePreSession>, t: Throwable) {
                    editMesocycleListBinding.progresBar.visibility = View.GONE
                    Log.e("Error", "Network Error: ${t.message}")
                }
            })
    }

    private fun getTransitionData(id: Int) {
        editMesocycleListBinding.progresBar.visibility = View.VISIBLE
        apiInterface.GetMesocycleTransition(id)
            .enqueue(object : Callback<GetMessocyclePreSession> {
                override fun onResponse(
                    call: Call<GetMessocyclePreSession>,
                    response: Response<GetMessocyclePreSession>
                ) {
                    editMesocycleListBinding.progresBar.visibility = View.GONE
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
                        Utils.setUnAuthDialog(this@EditMesocycleListActivity)
                    } else {
                        Toast.makeText(
                            this@EditMesocycleListActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }

                }

                override fun onFailure(call: Call<GetMessocyclePreSession>, t: Throwable) {
                    editMesocycleListBinding.progresBar.visibility = View.GONE
                    Log.e("Error", "Network Error: ${t.message}")
                }
            })
    }


    private fun selectTrainingPlanStartDate(editText: AppCompatEditText) {
        val mainStartDate = editMesocycleListBinding.startDate.text.toString()
        val maxStartDate = editMesocycleListBinding.endDate.text.toString()

        val minDateMillis =
            if (mainStartDate.isNotEmpty()) formatDateToMillis(mainStartDate) else System.currentTimeMillis()
        val maxDateMillis =
            if (maxStartDate.isNotEmpty()) formatDateToMillis(maxStartDate) else Long.MAX_VALUE

        Utils.selectDate3(
            this,
            editText,
            minDateMillis, // Minimum date for selection
            maxDateMillis  // Maximum date for selection
        ) { dateMillis ->
            // When a date is selected, update startDateMillis and display the formatted date
            startDateMillis = dateMillis
            val formattedDate = formatDate(dateMillis)
            editText.setText(formattedDate)
        }
    }

    private fun selectTrainingPlanEndDate(editText: AppCompatEditText) {

        val mainStartDate = editMesocycleListBinding.startDate.text.toString()
        val maxStartDate = editMesocycleListBinding.endDate.text.toString()

        val minDateMillis =
            if (mainStartDate.isNotEmpty()) formatDateToMillis(mainStartDate) else System.currentTimeMillis()
        val maxDateMillis =
            if (maxStartDate.isNotEmpty()) formatDateToMillis(maxStartDate) else Long.MAX_VALUE

        // Call Utils.selectDate3 with the min and max dates
        Utils.selectDate3(
            this,
            editText,
            minDateMillis,
            maxDateMillis
        ) { dateMillis ->
            // When a date is selected, update startDateMillis and display the formatted date
            startDateMillis = dateMillis
            val formattedDate = formatDate(dateMillis)
            editText.setText(formattedDate)
        }
    }

    private fun formatDateToMillis(dateString: String): Long {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return format.parse(dateString)?.time
            ?: Long.MAX_VALUE // Return millis or a very high value if parsing fails
    }

    // Utility function to format the date to a readable format
    private fun formatDate(dateMillis: Long): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date(dateMillis))
    }
}