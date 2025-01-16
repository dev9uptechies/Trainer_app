package com.example.trainerapp.competition

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.Adapter.competition.ViewCompetitionAdapter
import com.example.model.SelectedValue
import com.example.model.competition.create.RatingDataAthlete
import com.example.model.competition.create.RatingDataEdite
import com.example.model.newClass.competition.Competition
import com.example.model.newClass.competition.EditeAnalsisData
import com.example.model.newClass.competition.GetCompetition
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.RatingItem
import com.example.trainerapp.PreferencesManager
import com.example.trainerapp.R
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.ActivityViewCompetitionAnalysisBinding
import com.highsoft.highcharts.common.HIColor
import com.highsoft.highcharts.common.hichartsclasses.HICSSObject
import com.highsoft.highcharts.common.hichartsclasses.HIChart
import com.highsoft.highcharts.common.hichartsclasses.HIColumn
import com.highsoft.highcharts.common.hichartsclasses.HICredits
import com.highsoft.highcharts.common.hichartsclasses.HIExporting
import com.highsoft.highcharts.common.hichartsclasses.HILabels
import com.highsoft.highcharts.common.hichartsclasses.HILegend
import com.highsoft.highcharts.common.hichartsclasses.HIOptions
import com.highsoft.highcharts.common.hichartsclasses.HIPane
import com.highsoft.highcharts.common.hichartsclasses.HIPlotOptions
import com.highsoft.highcharts.common.hichartsclasses.HISeries
import com.highsoft.highcharts.common.hichartsclasses.HITitle
import com.highsoft.highcharts.common.hichartsclasses.HIXAxis
import com.highsoft.highcharts.common.hichartsclasses.HIYAxis
import com.highsoft.highcharts.core.HIChartContext
import com.highsoft.highcharts.core.HIChartView
import com.highsoft.highcharts.core.HIFunction
import com.highsoft.highcharts.core.HIFunctionInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Arrays

class ViewAnalysisDataActivity : AppCompatActivity() {
    lateinit var viewAnalysisBinding: ActivityViewCompetitionAnalysisBinding

    lateinit var compAdapter: ViewCompetitionAdapter
    lateinit var analysisData: MutableList<RatingItem>

    var warmupDesc = "Give a score to the following entries pre-competition and during warm-up"
    var mentalDesc = "Give a score to the following entries."
    var physicalDesc = "Give a score to the following entries."
    var strategyDesc = "Give a score to the following entries."
    var techniqueDesc = "Give a score to the following entries."

    lateinit var title: String
    lateinit var eventId: String
    var areaId = SelectedValue(null)
    var userType: String? = null
    var catName = "fggcg"
    var compDate = "2025-01-11"

    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    lateinit var preferenceManager: PreferencesManager

    lateinit var competitionData: MutableList<Competition.CompetitionData>
    lateinit var competition: MutableList<Competition.CompetitionData>
    lateinit var chartView: HIChartView

    private var globalCompetitionProgressIds: MutableList<Int> = mutableListOf()
//    lateinit var eventList: ArrayList<EventListData.testData>
//    var eventData: MutableList<EventListData.testData> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewAnalysisBinding = ActivityViewCompetitionAnalysisBinding.inflate(layoutInflater)
        setContentView(viewAnalysisBinding.root)
        initViews()


        val userType = preferenceManager.GetFlage()
        if (userType == "Athlete") {
            getCompetitionDataAthlete()
            setDefaultRecyclerAthlete()


        } else {
            getCompetitionData()
            setDefaultRecycler()

        }

        viewAnalysisBinding.save.setOnClickListener {
//            updateCompetitionProgress()
            checkButtonCLick()
        }

        setData()

        viewAnalysisBinding.back.setOnClickListener(View.OnClickListener { finish() })
    }

    private fun checkButtonCLick() {
        viewAnalysisBinding.save.setOnClickListener {
            val dataList: MutableList<RatingDataEdite> = mutableListOf()
            val dataListAthlete: MutableList<RatingDataAthlete> = mutableListOf()

            for (i in analysisData) {
                Log.d("DEBUG_LOG", "checkButtonCLick: coachRating=${i.coachRating}, name=${i.name}")

                Log.d("MGNGNN","checkButtonCLick: ${analysisData.get(0).name}")

                if (userType == "Athlete") {
                    if (i.athleteRating != null && i.athleteRating != 0) {
                        viewAnalysisBinding.save.setBackgroundResource(R.drawable.card_select_1)
                        dataListAthlete.add(RatingDataAthlete(i.name ?: "Unknown", i.athleteRating!!))
                    } else {
                        Toast.makeText(this, "Please rate all fields for Athlete", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                } else {
                    if (i.coachRating == null || i.coachRating == 0) {
                        Toast.makeText(this, "Coach's rating is required for ${i.name ?: ""}", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    } else {
                        for ((index, i) in analysisData.withIndex()) {
                            val competitionProgressId = globalCompetitionProgressIds.getOrNull(index)
                            if (competitionProgressId != null && competitionProgressId > 0) {
                                dataList.add(RatingDataEdite(competitionProgressId, i.coachRating!!,))
                            } else {
                                Toast.makeText(this, "Invalid competitionProgressId for ${i.name ?: "Unknown"}", Toast.LENGTH_SHORT).show()
                                return@setOnClickListener
                            }
                        }
                    }
                }
            }

            // Save data for Athlete
            if (userType == "Athlete" && dataListAthlete.isNotEmpty()) {
                Log.d("SAVE_DATA", "Athlete data being saved: $dataListAthlete")
                // Add saving logic for Athlete if needed
//                saveCompetitionDataAthleteForEdit(dataListAthlete)
                updateCompetitionProgress(dataList)

            }
            // Save data for Coach
            else if (userType == "Coach" && dataList.isNotEmpty()) {
                Log.d("SAVE_DATA", "Coach data being updated: $dataList")
                updateCompetitionProgress(dataList)
            } else {
                Toast.makeText(this, "No data to save or update", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setDefaultRecycler() {
        viewAnalysisBinding.performanceRly.layoutManager = LinearLayoutManager(this)
        compAdapter = ViewCompetitionAdapter(analysisData, this, true, false)
        viewAnalysisBinding.performanceRly.adapter = compAdapter
    }

    private fun setDefaultRecyclerAthlete() {
        viewAnalysisBinding.performanceRly.layoutManager = LinearLayoutManager(this)
        compAdapter = ViewCompetitionAdapter(analysisData, this, false, true)
        viewAnalysisBinding.performanceRly.adapter = compAdapter
    }

    private fun setData() {
        viewAnalysisBinding.tvTitle.text = title

        when (areaId.id) {
            1 -> {
                viewAnalysisBinding.tvSubTitle.text = warmupDesc
            }

            2 -> {
                viewAnalysisBinding.tvSubTitle.text = mentalDesc
            }

            3 -> {
                viewAnalysisBinding.tvSubTitle.text = physicalDesc
            }

            4 -> {
                viewAnalysisBinding.tvSubTitle.text = strategyDesc
            }

            5 -> {
                viewAnalysisBinding.tvSubTitle.text = techniqueDesc
            }
        }
    }

    private fun initViews() {
        title = intent.getStringExtra("title").toString()
        areaId.id = intent.getStringExtra("areaId")!!.toInt()
        eventId = intent.getIntExtra("eventId", 0).toString()

        Log.d("Event Data :- ", "$title \n area Id - ${areaId.id} \n eventId - $eventId")

        competitionData = mutableListOf()
        competition = mutableListOf()
        analysisData = mutableListOf()

        preferenceManager = PreferencesManager(this)
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        chartView = viewAnalysisBinding.chartView
        userType = preferenceManager.GetFlage()
    }

    private fun getCompetitionData() {
        try {
            competitionData.clear()
            competition.clear()
            viewAnalysisBinding.progressBar.visibility = View.VISIBLE
            apiInterface.GetCompetitionAnalysisData().enqueue(object : Callback<GetCompetition> {
                override fun onResponse(
                    call: Call<GetCompetition>,
                    response: Response<GetCompetition>
                ) {
                    viewAnalysisBinding.progressBar.visibility = View.GONE
                    Log.d("TAG", response.code().toString() + "")

                    val code = response.code()
                    if (code == 200) {
                        val success: Boolean = response.body()!!.status!!
                        if (success) {
                            val data = response.body()!!
                            Log.d("Athlete :- Data ", "${data}")
                            val message = data.message ?: "Success"
                            if (data.data != null) {
                                val compData = data.data
                                val filteredCompetitionData =
                                    competitionData.filter { it.id == eventId.toInt() }

                                for (i in compData) {
                                    Log.d(
                                        "Competition Datasssss :-",
                                        "${i.id} \n ${i.athlete!!.name}"
                                    )

                                    Log.e(
                                        "IOIPO{O{{P{P{",
                                        "onResponse: ${i.competition_progress?.get(0)!!.id}",
                                    )
                                    competitionData.add(i)
                                }

                                if (filteredCompetitionData.isNotEmpty()) {
                                    Log.d(
                                        "Competition Data :-",
                                        "${filteredCompetitionData[0].category}"
                                    )

                                } else {
                                    Log.d(
                                        "Competition Data :-",
                                        "No data found for eventId: $eventId"
                                    )
                                }
                                competition = competitionData.filter { it.id == eventId.toInt() }
                                    .toMutableList()
                                Log.d("Competition Data co:-", "$competition")
                                if (competition != null) {
                                    setRatingData(competition)
                                    if (competition.isNotEmpty()) {
                                        setChartData(competition[0].competition_progress)
                                    } else {
                                        Log.d("Competition Data", "Competition list is empty.")
                                    }
                                } else {
                                    Log.d("No Data", "No Data Found")
                                }
                                for (data in competitionData) {
                                    data.competition_progress?.forEach { progress ->
                                        Log.d("OXOOXOXOXOXO", "ID: ${progress.id}")
                                    }
                                }
                            }
                        }
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@ViewAnalysisDataActivity)
                    } else {
                        Toast.makeText(
                            this@ViewAnalysisDataActivity,
                            "Failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(p0: Call<GetCompetition>, p1: Throwable) {
                    viewAnalysisBinding.progressBar.visibility = View.GONE
                }

            })
        } catch (e: Exception) {
            Log.d("Error :-", "${e.message}")
        }
    }

    private fun getCompetitionDataAthlete() {
        try {
            competitionData.clear()
            competition.clear()
            viewAnalysisBinding.progressBar.visibility = View.VISIBLE
            apiInterface.GetCompetitionAnalysisDataAthlete()
                .enqueue(object : Callback<GetCompetition> {
                    override fun onResponse(
                        call: Call<GetCompetition>,
                        response: Response<GetCompetition>
                    ) {
                        viewAnalysisBinding.progressBar.visibility = View.GONE
                        Log.d("TAG", response.code().toString() + "")

                        val code = response.code()
                        if (code == 200) {
                            val success: Boolean = response.body()!!.status!!
                            if (success) {
                                val data = response.body()!!
                                Log.d("Athlete :- Data ", "${data}")
                                val message = data.message ?: "Success"
                                if (data.data != null) {
                                    val compData = data.data
                                    val filteredCompetitionData =
                                        competitionData.filter { it.id == eventId.toInt() }

                                    for (i in compData) {
                                        Log.d(
                                            "Competition Data :-",
                                            "${i.category} \n ${i.athlete!!.name}"
                                        )
                                        competitionData.add(i)

                                    }

                                    if (filteredCompetitionData.isNotEmpty()) {
                                        Log.d(
                                            "Competition Data :-",
                                            "${filteredCompetitionData[0].category}"
                                        )
                                    } else {
                                        Log.d(
                                            "Competition Data :-",
                                            "No data found for eventId: $eventId"
                                        )
                                    }
                                    competition =
                                        competitionData.filter { it.id == eventId.toInt() }
                                            .toMutableList()
                                    Log.d("Competition Data co:-", "$competition")
                                    if (competition != null) {
                                        setRatingData(competition)
                                        if (competition.isNotEmpty()) {
                                            setChartData(competition[0].competition_progress)
                                        } else {
                                            Log.d("Competition Data", "Competition list is empty.")
                                        }
                                    } else {
                                        Log.d("No Data", "No Data Found")
                                    }
                                    Log.d(
                                        "OXOOXOXOXOXO",
                                        "${competitionData.get(0).competition_progress!!.get(0).id}"
                                    )
                                }
                            }
                        } else if (code == 403) {
                            Utils.setUnAuthDialog(this@ViewAnalysisDataActivity)
                        } else {
                            Toast.makeText(
                                this@ViewAnalysisDataActivity,
                                "Failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(p0: Call<GetCompetition>, p1: Throwable) {
                        viewAnalysisBinding.progressBar.visibility = View.GONE
                    }

                })
        } catch (e: Exception) {
            Log.d("Error :-", "${e.message}")
        }
    }

    private fun setChartData(competitionProgress: List<Competition.CompetitionProgress>?) {
        viewAnalysisBinding.chartView.visibility = View.VISIBLE
        viewAnalysisBinding.chartView.addFont(R.font.poppins_medium)

        val options = HIOptions()

//        val options = HIOptions()

        // Set chart configuration
        options.chart = HIChart().apply {
            polar = true
            height = "100%"
            style = HICSSObject().apply {
                fontFamily = "poppins_medium"
                fontSize = "12px"
                color = "#FFFFFF"
            }
        }

//        val chart = HIChart()
//        chart.polar = true
//        chart.height = "100%"
//        options.chart = chart
        chartView.theme = "dark"

        val pane = HIPane()
        pane.startAngle = 0
        pane.endAngle = 360
        options.pane = arrayListOf(pane)

        val xAxis = HIXAxis()
//        xAxis.tickInterval = 45
        xAxis.min = 0
        xAxis.max = competitionProgress!!.size
//        xAxis.labels = HILabels()
        xAxis.labels = HILabels().apply {
            style = HICSSObject().apply {
                fontFamily = "poppins_medium"
                fontSize = "12px"
                fontWeight = "bold"
                color = "#FFFFFF"
            }
            distance = 2
            rotation = 0
        }
        xAxis.categories =
            competitionProgress.mapIndexed { index, data -> (index + 1).toString() } as ArrayList
        xAxis.labels.distance = 10
        xAxis.labels.rotation = 0

        xAxis.title = HITitle().apply {
            text = ""
            style = HICSSObject().apply {
                fontFamily = "poppins_medium"
                fontSize = "16px"
                color = "#333333"
            }
        }

        xAxis.labels.formatter = HIFunction(
            HIFunctionInterface { f: HIChartContext ->
                f.getProperty(
                    "value"
                ).toString() + ""
            }, arrayOf("value")

        )

        options.xAxis = object : ArrayList<HIXAxis?>() {
            init {
                add(xAxis)
            }
        }

        val yAxis = HIYAxis()
        yAxis.min = 0
        yAxis.labels = HILabels()
        yAxis.labels.enabled = false
        options.yAxis = arrayListOf(yAxis)

        val plotOptions = HIPlotOptions()
        plotOptions.series = HISeries()
        plotOptions.series.pointStart = 0
        plotOptions.column = HIColumn()
        plotOptions.column.pointPadding = 0
        plotOptions.column.groupPadding = 0
        options.plotOptions = plotOptions
        options.chart.backgroundColor = HIColor.initWithRGB(0, 0, 0)

        val athleteData =
            competitionProgress.mapNotNull { it.athlete_star?.toFloat() }.ifEmpty { emptyList() }
        val coachData =
            competitionProgress.mapNotNull { it.coach_star?.toFloat() }.ifEmpty { emptyList() }

        Log.d("Series Data :-", "athlete :- $athleteData")
        Log.d("Series Data :-", "athlete :- $coachData")

        val hiTitle = HITitle()
        hiTitle.text = ""
        options.title = hiTitle

        val series1 = HIColumn()
        series1.name = "Athlete"
        series1.color = HIColor.initWithRGB(251, 193, 82)

        val series1_data = athleteData.map { it }
        Log.d("Series Data :-", "coach :- $series1_data")
        series1.data = ArrayList(series1_data)

        val series3 = HIColumn()
        series3.name = "Coach"
        series3.color = HIColor.initWithRGB(255, 0, 0)

        val series3_data = coachData.map { it }
        Log.d("Series Data :-", "coach :- $series3_data")
        series3.data = ArrayList(series3_data)

        val legend = HILegend()
        legend.enabled = true
        val itemstyle = HICSSObject()
        itemstyle.fontSize = "14px"
        itemstyle.fontWeight = "regular"
        itemstyle.color = "#FFFFFF"
        itemstyle.fontFamily = "poppins_medium"

        legend.itemStyle = itemstyle
        options.legend = legend
        options.series = ArrayList(Arrays.asList(series1, series3))


        val exporting = HIExporting()
        exporting.enabled = false
        options.exporting = exporting

        val credits = HICredits()
        credits.enabled = false
        options.credits = credits

//        legend = HILegend().apply {
//            enabled = true
//            itemStyle = HICSSObject().apply {
//                fontSize = "14px"
//                fontWeight = "regular"
//                color = "#FFFFFF"
//                fontFamily = "poppins_medium"
//            }
//        }

        chartView.options = options
    }

    private fun setRatingData(competition: MutableList<Competition.CompetitionData>) {
        analysisData.clear()
        for (i in competition) {
            for (j in i.competition_progress!!) {

                Log.d("@@@@@22222", "setRatingData: ${j.id}")
                val coachRating = j.coach_star?.toIntOrNull() ?: 0
                val athleteRating = j.athlete_star?.toIntOrNull() ?: 0
                analysisData.add(
                    RatingItem(
                        name = j.title,
                        coachRating = coachRating,
                        athleteRating = athleteRating,
                        isByCoach = false,
                        isByAthlete = false
                    )
                )
                j.id?.let { globalCompetitionProgressIds.add(it) }

                Log.d("HFHFHFHFH", "setRatingData: $globalCompetitionProgressIds")

            }
        }

        if (userType == "Athlete") {
            viewAnalysisBinding.performanceRly.visibility = View.VISIBLE
            compAdapter = ViewCompetitionAdapter(analysisData, this, false, true)
            viewAnalysisBinding.performanceRly.adapter = compAdapter
        } else {
            viewAnalysisBinding.performanceRly.visibility = View.VISIBLE
            compAdapter = ViewCompetitionAdapter(analysisData, this, true, false)
            viewAnalysisBinding.performanceRly.adapter = compAdapter
        }

    }

    private fun updateCompetitionProgress(data: MutableList<RatingDataEdite>) {

        Log.d("OKKKKKK", "updateCompetitionProgress: OKKKK")
        data.forEach { item ->
            Log.d("API_DEBUG", "Sending competition_progress_id=${item.competitionProgressId}, coachStar=${item.coachStar}")

            apiInterface.EditCompetitionAnalysisData(
                competitionProgressId = item.competitionProgressId,
                coachStar = item.coachStar
            ).enqueue(object : Callback<EditeAnalsisData> {
                override fun onResponse(
                    call: Call<EditeAnalsisData>,
                    response: retrofit2.Response<EditeAnalsisData>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null && responseBody.status) {
                            if (response.code() == 200){
                                Toast.makeText(this@ViewAnalysisDataActivity, "Update Successfully", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            val errorMessage = responseBody?.message ?: "Unknown error"
                            Toast.makeText(
                                this@ViewAnalysisDataActivity,
                                "Update failed: $errorMessage",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@ViewAnalysisDataActivity,
                            "Failed with code: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<EditeAnalsisData>, t: Throwable) {
                    Toast.makeText(
                        this@ViewAnalysisDataActivity,
                        "Request failed: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d("API_ERROR", "onFailure: ${t.message}")
                }
            })
        }
    }


//    private fun saveCompetitionDataAthleteForEdit(data: MutableList<RatingDataAthlete>) {
//        try {
//            viewAnalysisBinding.progressBar.visibility = View.VISIBLE
//
//            // Log event IDs
//            Log.d("BMMBMBMM", "Logging all event IDs:")
//            competitionData.forEach { competition ->
//                Log.d("BMMBMBMM", "Event ID: ${competition.event_id}")
//            }
//
//            // Safely fetch the first event ID or default to 0
//            val eventId = competitionData.firstOrNull()?.event_id?.toInt() ?: 0
//            Log.d("BMMBMBMM", "Selected Event ID for Edit: $eventId")
//
//            // Prepare the request body with updated data
//            val editCompetitionData = AddCompetitionBodyAthlete(
//                athleteId = 196,
//                eventId = eventId, // Single Event ID
//                categoryName = catName,
//                date = compDate,
//                areaId = areaId.id!!,
//                data = data // Updated data for editing
//            )
//
//            Log.d("EditCompetition Data :-", "$editCompetitionData")
//
//            // API call for editing the data
//            apiInterface.CreateCompetitionAnalysisDataAthelete(editCompetitionData)!!.enqueue(object :
//                Callback<Competition> {
//                override fun onResponse(call: Call<Competition>, response: Response<Competition>) {
//                    viewAnalysisBinding.progressBar.visibility = View.GONE
//
//                    val code = response.code()
//                    if (code == 200) {
//                        val success = response.body()?.status ?: false
//                        if (success) {
//                            val responseData = response.body()
//                            Log.d("Athlete Edit :- Data", "$responseData")
//
//                            val message = responseData?.message ?: "Edit Successful"
//                            Toast.makeText(
//                                this@ViewAnalysisDataActivity,
//                                message,
//                                Toast.LENGTH_SHORT
//                            ).show()
//
//                            // Update chart data if available
//                            responseData?.data?.competition_progress?.let { chartData ->
//                                if (chartData.isNotEmpty()) {
//                                    // Uncomment and update this to handle chart data
//                                    // setChartOnlineData(chartData)
//                                }
//                            }
//                        } else {
//                            val errorMsg = response.body()?.message ?: "Edit Failed"
//                            Toast.makeText(
//                                this@ViewAnalysisDataActivity,
//                                errorMsg,
//                                Toast.LENGTH_SHORT
//                            ).show()
//                            Log.d("Response Error", "Code: ${response.code()}, Message: $errorMsg")
//                        }
//                    } else if (code == 403) {
//                        Utils.setUnAuthDialog(this@ViewAnalysisDataActivity)
//                    } else {
//                        Toast.makeText(
//                            this@ViewAnalysisDataActivity,
//                            "Failed with code $code",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                        Log.e("Error", "Failed response code: $code")
//                    }
//                }
//
//                override fun onFailure(call: Call<Competition>, t: Throwable) {
//                    viewAnalysisBinding.progressBar.visibility = View.GONE
//                    Log.e("Edit Failure Tag", "Error: ${t.message}")
//                }
//            })
//        } catch (e: Exception) {
//            viewAnalysisBinding.progressBar.visibility = View.GONE
//            Log.e("Exception in Edit", "Error: ${e.message}")
//        }
//    }



}