package com.example.trainerapp.training_plan

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
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
import com.example.trainerapp.R
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.ActivityViewTrainingPlanBinding
import com.example.trainerapp.training_plan.view_planning_cycle.ViewTrainingPlanListActivity
import com.google.android.datatransport.runtime.firebase.transport.LogEventDropped
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

class ViewTrainingPlanActivity : AppCompatActivity(), OnItemClickListener.OnItemClickCallback {
    lateinit var viewTrainingPlanBinding: ActivityViewTrainingPlanBinding
    private lateinit var apiInterface: APIInterface
    private lateinit var preferenceManager: PreferencesManager
    private lateinit var apiClient: APIClient
    var id: Int? = null
    var idforgroup: Int? = null
    lateinit var programData: MutableList<TrainingPlanData.TrainingPlan>
    lateinit var viewTraining: ViewTrainingAdapter

    private var globalMesocycles: List<Map<String, Any>> = emptyList()
    private var globalPreCompetitiveMesocycles: List<Map<String, Any>> = emptyList()
    private var globalCompetitiveMesocycles: List<Map<String, Any>> = emptyList()
    private var globalTransitionMesocycles: List<Map<String, Any>> = emptyList()


    // Pre Season
    var AthleteGroupPreSeason: String ?= null
    var AthleteGroupName: String ?= null
    var AthleteGroupStartDate: String ?= null
    var AthleteGroupEndDate: String ?= null
    var AthleteGroupMesocycle: String ?= null
    var AthleteGroupMesocycles: String ?= null

    // Pre Competitive
    var AthleteGroupPreCompetitive: String ?= null
    var AthletePreComGroupName: String ?= null
    var AthletePreComGroupStartDate: String ?= null
    var AthletePreComGroupEndDate: String ?= null
    var AthletePreComGroupMesocycle: String ?= null

    // Competitive
    var AthleteGroupCompetitive: String ?= null
    var AthleteComGroupName: String ?= null
    var AthleteComGroupStartDate: String ?= null
    var AthleteComGroupEndDate: String ?= null
    var AthleteComGroupMesocycle: String ?= null

    // Transition
    var AthleteGroupTransition: String ?= null
    var AthleteTranGroupName: String ?= null
    var AthleteTranGroupStartDate: String ?= null
    var AthleteTranGroupEndDate: String ?= null
    var AthleteTranGroupMesocycle: String ?= null

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

        val userType = preferenceManager.GetFlage()
        if (userType == "Athlete"){
            viewTrainingPlanBinding.textView3.text = "Training Plan"
            viewTrainingPlanBinding.edtLy.visibility = View.GONE
            viewTrainingPlanBinding.cardDate.visibility = View.GONE
            viewTrainingPlanBinding.cardEndDate.visibility = View.GONE
            viewTrainingPlanBinding.linerDays.visibility = View.GONE
            viewTrainingPlanBinding.imageView.visibility = View.GONE
            SetAthleteData()
        }else{
            viewTrainingPlanBinding.linerForAthlete.visibility = View.GONE
            viewTrainingPlanBinding.recyclerView.visibility = View.VISIBLE
        }

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

        val startDateMillis = formatDateToMillis2(data[0].start_date)
        val formattedStartDate = formatMillisToDateString(startDateMillis)

        val endDateInMillis = formatDateToMillis2(data[0].competition_date)
        val formattedEndDate = formatMillisToDateString(endDateInMillis)


        viewTrainingPlanBinding.edtProgramName.setText(data[0].name)
        viewTrainingPlanBinding.edtStartDate.setText(formattedStartDate)
        viewTrainingPlanBinding.edtEndDate.setText(formattedEndDate)
        viewTrainingPlanBinding.days.text = data[0].mesocycle

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
        Log.e("FAFFAFFAFAFAF", "loadData: "+id )

        val planningId = intent.getIntExtra("Id", 0)
        Log.d("PlanningID", "Received Planning ID: $planningId")

        AthleteGroupName = intent.getStringExtra("AthleteGroupName")
        AthleteGroupPreSeason = intent.getStringExtra("AthleteGroupPreSeason")
        AthleteGroupStartDate = intent.getStringExtra("AthleteGroupStartDate")
        AthleteGroupEndDate = intent.getStringExtra("AthleteGroupEndDate")
        AthleteGroupMesocycle = intent.getStringExtra("AthleteGroupMesocycle")
        val preSeasonJson = intent.getStringExtra("AthleteGroupMesocycles")
        val preCompetitiveJson = intent.getStringExtra("AthleteGroupPreCompetitiveMesocycles")
        val CompetitiveJson = intent.getStringExtra("AthleteGroupCompetitiveMesocycles")
        val TransitionJson = intent.getStringExtra("AthleteGroupTransitionMesocycles")

        Log.d("DKDKDKDKDKD", "initViews: $preSeasonJson   \n  $preCompetitiveJson   \n $preCompetitiveJson  \n  $TransitionJson")


        Log.d("DATES", "PRE START:$AthleteGroupStartDate ")


        val userType = preferenceManager.GetFlage()

        if (userType == "Athlete"){

        val gson = Gson()
        val type = object : TypeToken<Map<String, Any>>() {}.type

        fun safeParseJson(json: String?): Map<String, Any>? {
            return try {
                if (!json.isNullOrEmpty()) gson.fromJson<Map<String, Any>>(json, type) else null
            } catch (e: Exception) {
                Log.e("JSON_ERROR", "Failed to parse JSON: $json", e)
                null
            }
        }

        // Function to safely extract "mesocycles" as a List<Map<String, Any>>
        fun extractMesocycles(map: Map<String, Any>?): List<Map<String, Any>> {
            return (map?.get("mesocycles") as? List<*>)?.mapNotNull { it as? Map<String, Any> } ?: emptyList()
        }

        val preSeasonMap = safeParseJson(preSeasonJson) ?: emptyMap()
        val preCompetitiveMap = safeParseJson(preCompetitiveJson) ?: emptyMap()
        val competitiveMap = safeParseJson(CompetitiveJson) ?: emptyMap()
        val transitionMap = safeParseJson(TransitionJson) ?: emptyMap()

        val PreSeasonMesocycles = extractMesocycles(preSeasonMap)
        val PreCompetitiveMesocycles = extractMesocycles(preCompetitiveMap)
        val CompetitiveMesocycles = extractMesocycles(competitiveMap)
        val TransitionMesocycles = extractMesocycles(transitionMap)

        Log.d("DEBUG", "PreSeasonMesocycles: ${PreSeasonMesocycles.size}")
        Log.d("DEBUG", "PreCompetitiveMesocycles: ${PreCompetitiveMesocycles.size}")
        Log.d("DEBUG", "CompetitiveMesocycles: ${CompetitiveMesocycles.size}")
        Log.d("DEBUG", "TransitionMesocycles: ${TransitionMesocycles.size}")

        if (!PreSeasonMesocycles.isNullOrEmpty()) {
            globalMesocycles = PreSeasonMesocycles

            Log.d("777778888888888888", "initViews: $PreSeasonMesocycles")
            for (mesocycle in globalMesocycles) {
                val id = mesocycle["id"]
                val name = mesocycle["name"]
                val startDate = mesocycle["start_date"]
                val endDate = mesocycle["end_date"]
                Log.d("SSKKSKSKSKKS", "Mesocycle -> ID: $id, Name: $name, Start: $startDate, End: $endDate")
            }
        } else {
            Log.d("SSKKSKSKSKKS", "Mesocycles: NULL or EMPTY")
        }

        if (!PreCompetitiveMesocycles.isNullOrEmpty()) {
            globalPreCompetitiveMesocycles = PreCompetitiveMesocycles // Store globally

            for (mesocycle in globalPreCompetitiveMesocycles) {
                val id = mesocycle["id"]
                val name = mesocycle["name"]
                val startDate = mesocycle["start_date"]
                val endDate = mesocycle["end_date"]
                Log.d("SSKKSKSKSKKS", "Mesocycle -> ID: $id, Name: $name, Start: $startDate, End: $endDate")
            }
        } else {
            Log.d("SSKKSKSKSKKS", "Mesocycles: NULL or EMPTY")
        }

        if (!CompetitiveMesocycles.isNullOrEmpty()) {
            globalCompetitiveMesocycles = CompetitiveMesocycles // Store globally

            for (mesocycle in globalPreCompetitiveMesocycles) {
                val id = mesocycle["id"]
                val name = mesocycle["name"]
                val startDate = mesocycle["start_date"]
                val endDate = mesocycle["end_date"]
                Log.d("SSKKSKSKSKKS", "Mesocycle -> ID: $id, Name: $name, Start: $startDate, End: $endDate")
            }
        } else {
            Log.d("SSKKSKSKSKKS", "Mesocycles: NULL or EMPTY")
        }
        if (!TransitionMesocycles.isNullOrEmpty()) {
            globalTransitionMesocycles = TransitionMesocycles // Store globally

            for (mesocycle in globalPreCompetitiveMesocycles) {
                val id = mesocycle["id"]
                val name = mesocycle["name"]
                val startDate = mesocycle["start_date"]
                val endDate = mesocycle["end_date"]
                Log.d("SSKKSKSKSKKS", "Mesocycle -> ID: $id, Name: $name, Start: $startDate, End: $endDate")
            }
        } else {
            Log.d("SSKKSKSKSKKS", "Mesocycles: NULL or EMPTY")
        }

        // Pre Competitive
        AthleteGroupPreCompetitive = intent.getStringExtra("AthleteGroupPreCompetitive")
        AthletePreComGroupName = intent.getStringExtra("AthletePreComGroupName")
        AthletePreComGroupStartDate = intent.getStringExtra("AthletePreComGroupStartDate")
        AthletePreComGroupEndDate = intent.getStringExtra("AthletePreComGroupEndDate")
        AthletePreComGroupMesocycle = intent.getStringExtra("AthletePreComGroupMesocycle")

        // Competitive
        AthleteGroupCompetitive = intent.getStringExtra("AthleteGroupCompetitive")
        AthleteComGroupName = intent.getStringExtra("AthleteComGroupName")
        AthleteComGroupStartDate = intent.getStringExtra("AthleteComGroupStartDate")
        AthleteComGroupEndDate = intent.getStringExtra("AthleteComGroupEndDate")
        AthleteComGroupMesocycle = intent.getStringExtra("AthleteComGroupMesocycle")

        // Transition
        AthleteGroupTransition = intent.getStringExtra("AthleteGroupTransition")
        AthleteTranGroupName = intent.getStringExtra("AthleteTranGroupName")
        AthleteTranGroupStartDate = intent.getStringExtra("AthleteTranGroupStartDate")
        AthleteTranGroupEndDate = intent.getStringExtra("AthleteTranGroupEndDate")
        AthleteTranGroupMesocycle = intent.getStringExtra("AthleteTranGroupMesocycle")


        Log.d("TAGGGGGGGG", "initViews: $AthleteGroupTransition")

        }

    }


        fun formatDate(dateString: String?): String {
            Log.d("SKKSKKSKS", "formatDate: $dateString")

            if (dateString.isNullOrEmpty()) {
                return "Invalid Date"
            }

            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())

                val date = inputFormat.parse(dateString)
                date?.let { outputFormat.format(it) } ?: "Invalid Date"

            } catch (e: Exception) {
                Log.e("SKKSKKSKS", "Date parsing failed: ${e.message}")
                "Invalid Date"
            }
        }


    private fun SetAthleteData() {
        val parentLayout = findViewById<LinearLayout>(R.id.liner_for_athlete)

        fun addAthleteView(
            phase: String,
            name: String?,
            startDate: String?,
            endDate: String?,
            mesocycle: String?,
            onClick: () -> Unit
        ) {
            val athleteView = LayoutInflater.from(this).inflate(R.layout.viewtrainingplanlist, parentLayout, false)

            val AthletePlanName = athleteView.findViewById<TextView>(R.id.training_name_one)
            val AthletePlanStartDate = athleteView.findViewById<TextView>(R.id.start_date_one)
            val AthletePlanEndDate = athleteView.findViewById<TextView>(R.id.end_date_one)
            val AthletePlanMesocycle = athleteView.findViewById<TextView>(R.id.mesocycle_one)

            Log.d("DLDLDLDLDL", "addAthleteView: $startDate   $endDate")

            AthletePlanName.text = name ?: phase
            val start = formatDate(startDate)
            val end = formatDate(endDate)
            AthletePlanStartDate.text = ("Start: "+start)
            AthletePlanEndDate.text = ("End: " + end)
            AthletePlanMesocycle.text = if (mesocycle?.contains("MesoCycle") == true) {
                "Mesocycle: "+mesocycle
            } else {
                "${"Mesocycle: "+ mesocycle ?: "0"} MesoCycle"
            }

            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                resources.getDimensionPixelSize(R.dimen._80sdp)
            ).apply {
                setMargins(0, 10, 0, 10)
            }
            athleteView.layoutParams = layoutParams

            athleteView.setOnClickListener {
                onClick()
            }

            parentLayout.addView(athleteView)
        }

        // Pre Season
        if (AthleteGroupPreSeason == "Pre Season") {
            Log.d(":5555555", "SetAthleteData: $AthleteGroupStartDate")
            addAthleteView("Pre Season", AthleteGroupName, AthleteGroupStartDate, AthleteGroupEndDate, AthleteGroupMesocycle) {
                Log.d("CLICK_EVENT", "Pre Season clicked!")

                val gson = Gson()
                val jsonMesocycles = gson.toJson(globalMesocycles) // Convert list to JSON

                Log.d("SLLSLSLSL", "SetAthleteData: $jsonMesocycles")

                val intent = Intent(this, ViewTrainingPlanListActivity::class.java)
                intent.putExtra("PreSeasonJson", jsonMesocycles) // Send JSON string
                intent.putExtra("PreSeason", "PreSeason") // Send JSON string
                startActivity(intent)

            }
        }

        // Pre Competitive
        if (AthleteGroupPreCompetitive == "Pre Competitive") {
            addAthleteView("Pre Competitive", AthletePreComGroupName, AthletePreComGroupStartDate, AthletePreComGroupEndDate, AthletePreComGroupMesocycle) {
                Log.d("CLICK_EVENT", "Pre Competitive clicked!")
                val gson = Gson()
                val jsonMesocycles = gson.toJson(globalPreCompetitiveMesocycles)

                val intent = Intent(this, ViewTrainingPlanListActivity::class.java)
                intent.putExtra("PreCompetitiveJson", jsonMesocycles)
                intent.putExtra("PreCompetitive", "PreCompetitive")
                startActivity(intent)
            }
        }

        // Competitive
        if (AthleteGroupCompetitive == "Competitive") {
            addAthleteView("Competitive", AthleteComGroupName, AthleteComGroupStartDate, AthleteComGroupEndDate, AthleteComGroupMesocycle) {
                Log.d("CLICK_EVENT", "Competitive clicked!")
                val gson = Gson()
                val jsonMesocycles = gson.toJson(globalCompetitiveMesocycles)

                val intent = Intent(this, ViewTrainingPlanListActivity::class.java)
                intent.putExtra("CompetitiveJson", jsonMesocycles)
                intent.putExtra("Competitive", "Competitive")
                startActivity(intent)
            }
        }

        // Transition
        if (AthleteGroupTransition == "Transition") {
            addAthleteView("Transition", AthleteTranGroupName, AthleteTranGroupStartDate, AthleteTranGroupEndDate, AthleteTranGroupMesocycle) {
                Log.d("CLICK_EVENT", "Transition clicked!")
                val gson = Gson()
                val jsonMesocycles = gson.toJson(globalTransitionMesocycles)

                val intent = Intent(this, ViewTrainingPlanListActivity::class.java)
                intent.putExtra("TransitionJson", jsonMesocycles)
                intent.putExtra("Transition", "Transition")
                startActivity(intent)

            }
        }
    }

    private fun formatDateToMillis2(dateString: String?): Long {
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = format.parse(dateString)
            date?.time ?: System.currentTimeMillis() // Fallback to current time if parsing fails
        } catch (e: Exception) {
            Log.e("DateConversion", "Error converting date: ${e.message}")
            System.currentTimeMillis() // Fallback to current time
        }
    }

    // Method to format milliseconds into the desired date format
    private fun formatMillisToDateString(millis: Long): String {
        val format = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
        return format.format(millis)
    }

    override fun onItemClicked(view: View, position: Int, type: Long, string: String) {
        val intent = Intent(this, ViewTrainingPlanListActivity::class.java)

        val trainingData = programData.get(position)

        Log.d("DATE","START ${trainingData.start_date}     ${trainingData.competition_date}")

        when (string) {
            "pre_session" -> {
                intent.putExtra("seasonId", trainingData.pre_season?.id)
                intent.putExtra("mainId", id)
                intent.putExtra("startDate", trainingData.pre_season?.start_date)
                intent.putExtra("endDate", trainingData.pre_season?.end_date)
            }

            "pre_competitive" -> {
                intent.putExtra("seasonId", trainingData.pre_competitive?.id)
                intent.putExtra("mainId", trainingData.id)
                intent.putExtra("startDate", trainingData.pre_season?.start_date)
                intent.putExtra("endDate", trainingData.pre_season?.end_date)
            }

            "competitive" -> {
                intent.putExtra("seasonId", trainingData.competitive?.id)
                intent.putExtra("mainId", trainingData.id)
                intent.putExtra("startDate", trainingData.pre_season?.start_date)
                intent.putExtra("endDate", trainingData.pre_season?.end_date)
            }

            "transition" -> {
                intent.putExtra("seasonId", trainingData.transition?.id)
                intent.putExtra("mainId", trainingData.id)
                intent.putExtra("startDate", trainingData.pre_season?.start_date)
                intent.putExtra("endDate", trainingData.pre_season?.end_date)
            }
        }
        intent.putExtra("CardType", string)
        startActivity(intent)
    }
}