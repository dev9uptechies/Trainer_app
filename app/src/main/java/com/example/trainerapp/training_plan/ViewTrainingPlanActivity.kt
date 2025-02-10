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

    // Pre Season
    var AthleteGroupPreSeason: String ?= null
    var AthleteGroupName: String ?= null
    var AthleteGroupStartDate: String ?= null
    var AthleteGroupEndDate: String ?= null
    var AthleteGroupMesocycle: String ?= null

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
            SetAthleteData()
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

        Log.d("SSKKSKSKSKKS", "initViews: $AthleteGroupName \n $AthleteGroupStartDate \n $AthleteGroupEndDate")


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

    }

    private fun SetAthleteData() {
        val parentLayout = findViewById<LinearLayout>(R.id.liner_for_athlete)

        if (AthleteGroupPreSeason == "Pre Season") {
            val athleteView = LayoutInflater.from(this).inflate(R.layout.viewtrainingplanlist, parentLayout, false)

            val AthletePlanName = athleteView.findViewById<TextView>(R.id.training_name_one)
            val AthletePlanStartDate = athleteView.findViewById<TextView>(R.id.start_date_one)
            val AthletePlanEndDate = athleteView.findViewById<TextView>(R.id.end_date_one)
            val AthletePlanMesocycle = athleteView.findViewById<TextView>(R.id.mesocycle_one)

            AthletePlanName.text = AthleteGroupName ?: "Pre Season"
            AthletePlanStartDate.text = AthleteGroupStartDate ?: ""
            AthletePlanEndDate.text = AthleteGroupEndDate ?: ""
            AthletePlanMesocycle.text = AthleteGroupMesocycle ?: "0 Cycle"

            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                resources.getDimensionPixelSize(R.dimen._80sdp)
            ).apply {
                setMargins(0, 10, 0, 10)
            }

            athleteView.layoutParams = layoutParams

            parentLayout.addView(athleteView) // ✅ Add to parent layout
        } else {
            Log.d("PreSeasonCheck", "Pre-season data is null, not adding layout")
        }

        if (AthleteGroupPreCompetitive == "Pre Competitive") {
            val athleteView = LayoutInflater.from(this).inflate(R.layout.viewtrainingplanlist, parentLayout, false)

            val AthletePlanName = athleteView.findViewById<TextView>(R.id.training_name_one)
            val AthletePlanStartDate = athleteView.findViewById<TextView>(R.id.start_date_one)
            val AthletePlanEndDate = athleteView.findViewById<TextView>(R.id.end_date_one)
            val AthletePlanMesocycle = athleteView.findViewById<TextView>(R.id.mesocycle_one)

            AthletePlanName.text = AthletePreComGroupName ?: "Pre Competitive"
            AthletePlanStartDate.text = AthletePreComGroupStartDate ?: "Invalid Date"
            AthletePlanEndDate.text = AthletePreComGroupEndDate ?: "Invalid Date"
            AthletePlanMesocycle.text = AthletePreComGroupMesocycle ?: "0 Cycle"

            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                resources.getDimensionPixelSize(R.dimen._80sdp)
            ).apply {
                setMargins(0, 10, 0, 10)
            }

            athleteView.layoutParams = layoutParams

            parentLayout.addView(athleteView) // ✅ Add to parent layout
        } else {
            Log.d("PreCompetitiveCheck", "Pre-season data is null, not adding layout")
        }

        if (AthleteGroupCompetitive == "Competitive") {
            val athleteView = LayoutInflater.from(this).inflate(R.layout.viewtrainingplanlist, parentLayout, false)

            val AthletePlanName = athleteView.findViewById<TextView>(R.id.training_name_one)
            val AthletePlanStartDate = athleteView.findViewById<TextView>(R.id.start_date_one)
            val AthletePlanEndDate = athleteView.findViewById<TextView>(R.id.end_date_one)
            val AthletePlanMesocycle = athleteView.findViewById<TextView>(R.id.mesocycle_one)

            AthletePlanName.text = AthleteComGroupName ?: "Competitive"
            AthletePlanStartDate.text = AthleteComGroupStartDate ?: "Invalid Date"
            AthletePlanEndDate.text = AthleteComGroupEndDate ?: "Invalid Date"
            AthletePlanMesocycle.text = AthleteComGroupMesocycle ?: "0 Cycle"

            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                resources.getDimensionPixelSize(R.dimen._80sdp)
            ).apply {
                setMargins(0, 10, 0, 10)
            }

            athleteView.layoutParams = layoutParams

            parentLayout.addView(athleteView)
        } else {
            Log.d("PreCompetitiveCheck", "Pre-competitive data is null, not adding layout")
        }

        if (AthleteGroupTransition == "Transition") {
            val athleteView = LayoutInflater.from(this).inflate(R.layout.viewtrainingplanlist, parentLayout, false)

            val AthletePlanName = athleteView.findViewById<TextView>(R.id.training_name_one)
            val AthletePlanStartDate = athleteView.findViewById<TextView>(R.id.start_date_one)
            val AthletePlanEndDate = athleteView.findViewById<TextView>(R.id.end_date_one)
            val AthletePlanMesocycle = athleteView.findViewById<TextView>(R.id.mesocycle_one)

            AthletePlanName.text = AthleteTranGroupName ?: "Competitive"
            AthletePlanStartDate.text = AthleteTranGroupStartDate ?: ""
            AthletePlanEndDate.text = AthleteTranGroupEndDate ?: ""
            AthletePlanMesocycle.text = AthleteTranGroupMesocycle ?: "0 Cycle"

            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                resources.getDimensionPixelSize(R.dimen._80sdp)
            ).apply {
                setMargins(0, 10, 0, 10)
            }

            athleteView.layoutParams = layoutParams

            parentLayout.addView(athleteView)
        } else {
            Log.d("PreCompetitiveCheck", "Pre-competitive data is null, not adding layout")
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