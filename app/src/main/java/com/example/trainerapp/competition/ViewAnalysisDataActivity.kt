package com.example.trainerapp.competition

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.Adapter.competition.ViewCompetitionAdapter
import com.example.model.SelectedValue
import com.example.model.competition.create.AddCompetitionBodyAthlete
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
import com.google.android.datatransport.runtime.firebase.transport.LogEventDropped
import com.google.gson.Gson
import com.google.gson.GsonBuilder
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

    private lateinit var warmupDesc: String
    private lateinit var mentalDesc: String
    private lateinit var physicalDesc: String
    private lateinit var strategyDesc: String
    private lateinit var techniqueDesc: String


    lateinit var title: String
    lateinit var eventId: String
    lateinit var eventIdss: String
    var areaId = SelectedValue(null)
    var userType: String? = null
    var catName: String? = null
    var compDate: String? = null
    var value: String? = null

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
        warmupDesc = getString(R.string.warmupDesc)
        mentalDesc = getString(R.string.mentalDesc)
        physicalDesc = getString(R.string.physicalDesc)
        strategyDesc = getString(R.string.strategyDesc)
        techniqueDesc = getString(R.string.techniqueDesc)

        val userType = preferenceManager.GetFlage()
        if (userType == "Athlete") {
            getCompetitionDataAthlete()
            setDefaultRecyclerAthlete()


        } else {
            getCompetitionData()
            setDefaultRecycler()

        }

//            updateCompetitionProgress()
        checkButtonCLick()


        setData()

        viewAnalysisBinding.back.setOnClickListener(View.OnClickListener { finish() })
    }

    private fun checkButtonCLick() {

        viewAnalysisBinding.save.isActivated
        viewAnalysisBinding.save.isEnabled = true

        viewAnalysisBinding.save.setOnClickListener {
            val dataList: MutableList<RatingDataEdite> = mutableListOf()
            val dataListAthlete: MutableList<RatingDataAthlete> = mutableListOf()

            for (i in analysisData) {
                Log.d("DEBUG_LOG", "checkButtonCLick: coachRating=${i.coachRating}, name=${i.name}")

                Log.d("MGNGNN", "checkButtonCLick: ${analysisData.get(0).name}")

                if (userType == "Athlete") {
                    if (i.athleteRating != null && i.athleteRating != 0) {
                        viewAnalysisBinding.save.setBackgroundResource(R.drawable.card_select_1)
                        dataListAthlete.add(
                            RatingDataAthlete(
                                i.name ?: "Unknown",
                                i.athleteRating!!
                            )
                        )
                    } else {
                        viewAnalysisBinding.save.setCardBackgroundColor(Color.parseColor("#535353"))
                        Toast.makeText(
                            this,
                            "Please rate all fields for Athlete",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }
                } else {
                    if (i.coachRating == null || i.coachRating == 0) {
                        Toast.makeText(
                            this,
                            "Coach's rating is required for ${i.name ?: ""}",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    } else {
                        for ((index, i) in analysisData.withIndex()) {
                            val competitionProgressId =
                                globalCompetitionProgressIds.getOrNull(index)
                            if (competitionProgressId != null && competitionProgressId > 0) {
                                dataList.add(
                                    RatingDataEdite(
                                        competitionProgressId,
                                        i.coachRating!!,
                                    )
                                )
                            } else {
                                Toast.makeText(
                                    this,
                                    "Invalid competitionProgressId for ${i.name ?: "Unknown"}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@setOnClickListener
                            }
                        }
                    }
                }
            }

            // Save data for Athlete
            if (userType == "Athlete" && dataListAthlete.isNotEmpty()) {
                dataListAthlete.forEach { data ->
                    Log.d(
                        "MKMMKMKMKKM",
                        "checkButtonCLick: ${data.title}  ----   ${data.athleteStar}"
                    )
                }
                Log.d("SAVE_DATA", "Athlete data being saved: $dataListAthlete")
                saveCompetitionDataAthleteForEdit(dataListAthlete)

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
        viewAnalysisBinding.save.visibility = View.GONE
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
        eventIdss = intent.getStringExtra("eventIdss") ?: "DefaultEventId"
        compDate = intent.getStringExtra("Date") ?: "DefaultDate"
        catName = intent.getStringExtra("CatName") ?: "DefaultDate"
        value = intent.getStringExtra("value") ?: "false"
        Log.d("ReceivedDate", "compDate: $value")
        Log.d("ReceivedIDDDDDDD", "compDate: $eventIdss   ---   $eventId")


        Log.d(
            "Event Data :- ",
            "$title \n area Id - ${areaId.id} \n eventId - $eventId \n Date - $compDate"
        )

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
            val gson = GsonBuilder().setPrettyPrinting().create()
            apiInterface.GetCompetitionAnalysisDataAthlete()
                .enqueue(object : Callback<GetCompetition> {
                    override fun onResponse(call: Call<GetCompetition>, response: Response<GetCompetition>) {
                        viewAnalysisBinding.progressBar.visibility = View.GONE
                        Log.d("TAG", "Response Code: ${response.code()}")

                        val code = response.code()
                        if (code == 200) {
                            val responseBody = response.body()
                            val success: Boolean = responseBody?.status ?: false

                            if (success && responseBody!!.data != null) {
                                val compData = responseBody.data

                                // **Pretty Print JSON**
                                val json = gson.toJson(responseBody)
                                Log.d("API Response", "Formatted Data:\n$json")

                                compData!!.forEach { item ->
                                    Log.d("Event ID:", "Item Event ID: ${item.event_id}")
                                    Log.d("Category:", "Item Category: ${item.category}")
                                    Log.d("Date:", "Item Date: ${item.date}")
                                    Log.d("Analysis Area ID:", "Item Analysis ID: ${item.competition_analysis_area?.id}")
                                    Log.d("Athlete Name:", "Athlete: ${item.athlete?.name}")

                                    if (competitionData.none { it.id == item.id!!.toInt() }) {
                                        Log.d("Adding Data:", "Adding event_id: ${item.event_id}")
                                        competitionData.add(item)
                                    } else {
                                        Log.d("Skipping Data:", "Duplicate event_id found: ${item.event_id}")
                                    }
                                }

                                val filteredCompetitionData = competitionData.filter { it.id == eventId!!.toInt() }

                                Log.d("Filtered Data", "Event ID Filter: $eventIdss")
                                Log.d("Filtered Data", "Filtered List: ${gson.toJson(filteredCompetitionData)}")

                                competition = filteredCompetitionData.toMutableList()

                                if (competition.isNotEmpty()) {
                                    Log.e(
                                        "Final Competition Data",
                                        "Athlete Star: ${competition[0].competition_progress?.get(0)?.athlete_star}"
                                    )

                                    setRatingData(competition)

                                    val competitionProgressData = competition[0].competition_progress

                                    // **Check if data is null or empty**
                                    if (competitionProgressData.isNullOrEmpty()) {
                                        Log.e("Chart Data", "competition_progress is NULL or EMPTY!")
                                    } else {
                                        Log.d("Chart Data", "Competition Progress Data: ${Gson().toJson(competitionProgressData)}")
                                        setChartData(competitionProgressData)
                                    }
                                } else {
                                    Log.d("Competition Data", "Competition list is empty.")
                                }
                            }
                        } else if (code == 403) {
                            Utils.setUnAuthDialog(this@ViewAnalysisDataActivity)
                        } else {
                            Toast.makeText(this@ViewAnalysisDataActivity, "Failed", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<GetCompetition>, t: Throwable) {
                        viewAnalysisBinding.progressBar.visibility = View.GONE
                        Log.e("API Failure", "Error: ${t.message}", t)
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
        chartView.invalidate() // Request a layout update
        chartView.requestLayout()
    }


//    private fun setChartData(competitionProgress: List<Competition.CompetitionProgress>?) {
//        if (competitionProgress.isNullOrEmpty()) {
//            Log.e("Chart Data", "No competition progress data available!")
//            return
//        }
//
//        viewAnalysisBinding.chartView.visibility = View.VISIBLE
//        viewAnalysisBinding.chartView.addFont(R.font.poppins_medium)
//
//        val options = HIOptions()
//        options.chart = HIChart().apply {
//            polar = true
//            height = "100%"
//        }
//
//        val athleteData = competitionProgress.mapNotNull { it.athlete_star?.toFloat() }
//        val coachData = competitionProgress.mapNotNull { it.coach_star?.toFloat() }
//
//        Log.d("Chart Data", "Athlete Data: $athleteData")
//        Log.d("Chart Data", "Coach Data: $coachData")
//
//        if (athleteData.isEmpty() && coachData.isEmpty()) {
//            Log.e("Chart Data", "No valid athlete or coach data found!")
//            return
//        }
//
//        val series1 = HIColumn().apply {
//            name = "Athlete"
//            color = HIColor.initWithRGB(251, 193, 82)
//            data = ArrayList(athleteData)
//        }
//
//        val series2 = HIColumn().apply {
//            name = "Coach"
//            color = HIColor.initWithRGB(255, 0, 0)
//            data = ArrayList(coachData)
//        }
//
//        options.series = arrayListOf(series1, series2)
//        chartView.options = options
//    }


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
            Log.d("JDJDJDJD", "setRatingData: $value")
            if (value == "false") {
                viewAnalysisBinding.performanceRly.visibility = View.VISIBLE
                compAdapter = ViewCompetitionAdapter(analysisData, this, false, false)
                viewAnalysisBinding.performanceRly.adapter = compAdapter
            } else {
                viewAnalysisBinding.performanceRly.visibility = View.VISIBLE
                compAdapter = ViewCompetitionAdapter(analysisData, this, true, false)
                viewAnalysisBinding.performanceRly.adapter = compAdapter
            }

        }

    }

    private fun updateCompetitionProgress(data: MutableList<RatingDataEdite>) {
        Log.d("OKKKKKK", "updateCompetitionProgress: OKKKK")

        var successfulUpdates = 0
        var failedUpdates = 0
        val totalRequests = data.size

        data.forEach { item ->
            Log.d(
                "API_DEBUG",
                "Sending competition_progress_id=${item.competitionProgressId}, coachStar=${item.coachStar}"
            )

            apiInterface.EditCompetitionAnalysisData(
                competitionProgressId = item.competitionProgressId,
                coachStar = item.coachStar
            ).enqueue(object : Callback<EditeAnalsisData> {
                override fun onResponse(
                    call: Call<EditeAnalsisData>,
                    response: retrofit2.Response<EditeAnalsisData>
                ) {

                    Log.d("SKSKSKKSKS", "onResponse: ${response.code()}")
                    Log.d("SKSKSKKSKS", "onResponse: ${response.message()}")
                    Log.d("SKSKSKKSKS", "onResponse: ${response.errorBody()}")
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null && responseBody.status) {
                            successfulUpdates++
                        } else {
                            failedUpdates++
                        }
                    } else {
                        failedUpdates++
                    }

                    if (successfulUpdates + failedUpdates == totalRequests) {
                        displayToast(successfulUpdates, failedUpdates)
                    }
                }

                override fun onFailure(call: Call<EditeAnalsisData>, t: Throwable) {
                    failedUpdates++
                    Log.d("API_ERROR", "onFailure: ${t.message}")

                    if (successfulUpdates + failedUpdates == totalRequests) {
                        displayToast(successfulUpdates, failedUpdates)
                    }
                }
            })
        }
    }

    private fun displayToast(successfulUpdates: Int, failedUpdates: Int) {
        val message = if (failedUpdates == 0) {
            "updates successful."
        } else {
            "Updates Successful."
        }

        Toast.makeText(this@ViewAnalysisDataActivity, message, Toast.LENGTH_SHORT).show()
    }

    private fun saveCompetitionDataAthleteForEdit(data: MutableList<RatingDataAthlete>) {
        try {
            viewAnalysisBinding.progressBar.visibility = View.VISIBLE

            // Log event IDs
            Log.d("CompetitionData", "Logging all competition event IDs:")
            data.forEach { competition ->
                Log.d(
                    "CompetitionData",
                    "Event ID: ${competition.title} ====   ${competition.athleteStar}"
                )
            }

            Log.d("SKSKSKSK", "saveCompetitionDataAthleteForEdit: $catName")

            val editCompetitionData = AddCompetitionBodyAthlete(
                eventId = eventIdss.toInt(),
                categoryName = catName.toString(),
                date = compDate.toString(),
                areaId = areaId.id!!,
                data = data
            )

            Log.d("EditRequestBody", "Request Body: ${Gson().toJson(editCompetitionData)}")

            apiInterface.CreateCompetitionAnalysisDataAthelete(editCompetitionData)
                ?.enqueue(object : Callback<Competition> {
                    override fun onResponse(
                        call: Call<Competition>,
                        response: Response<Competition>
                    ) {
                        viewAnalysisBinding.progressBar.visibility = View.GONE

                        Log.d("EditCompetitionResponse", "Response Code: ${response.code()}")
                        Log.d("EditCompetitionResponse", "Response Message: ${response.message()}")
                        Log.d(
                            "EditCompetitionResponse",
                            "Request URL: ${response.raw().request.url}"
                        )

                        if (response.isSuccessful) {
                            val responseData = response.body()
                            val responseJson = Gson().toJson(responseData)
                            Log.d("EditCompetitionResponse", "Full Response Body: $responseJson")

                            val success = responseData?.status ?: false
                            if (success) {
                                Toast.makeText(
                                    this@ViewAnalysisDataActivity,
                                    responseData?.message ?: "Edit Successful",
                                    Toast.LENGTH_SHORT
                                ).show()

                                // Optionally update chart data
                                responseData?.data?.competition_progress?.let { chartData ->
                                    if (chartData.isNotEmpty()) {
                                        // Handle chart data update
                                        setChartData(competition[0].competition_progress)
                                    }
                                }
                            } else {
                                val errorMsg = responseData?.message ?: "Edit Failed"
                                Toast.makeText(
                                    this@ViewAnalysisDataActivity,
                                    errorMsg,
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.e("EditError", "Message: $errorMsg")
                            }
                        } else {
                            val errorBody = response.errorBody()?.string()
                            Log.e("EditCompetitionResponse", "Error Response Body: $errorBody")

                            if (response.code() == 403) {
                                Utils.setUnAuthDialog(this@ViewAnalysisDataActivity)
                            } else {
                                Toast.makeText(
                                    this@ViewAnalysisDataActivity,
                                    "Failed with code ${response.code()}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }

                    override fun onFailure(call: Call<Competition>, t: Throwable) {
                        viewAnalysisBinding.progressBar.visibility = View.GONE
                        Log.e("EditCompetitionError", "Failure: ${t.message}")
                    }
                })

        } catch (e: Exception) {
            viewAnalysisBinding.progressBar.visibility = View.GONE
            Log.e("EditException", "Error: ${e.message}")
        }
    }

}