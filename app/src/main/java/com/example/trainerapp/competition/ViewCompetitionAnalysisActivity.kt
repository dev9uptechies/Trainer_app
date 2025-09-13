package com.example.trainerapp.competition

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.Adapter.competition.ViewCompetitionAdapter
import com.example.model.SelectedValue
import com.example.model.competition.create.AddCompetitionBody
import com.example.model.competition.create.AddCompetitionBodyAthlete
import com.example.model.competition.create.RatingData
import com.example.model.competition.create.RatingDataAthlete
import com.example.model.newClass.competition.Competition
import com.example.model.newClass.competition.GetCompetition
import com.example.model.newClass.competition.GetCompetitionAll
import com.example.model.newClass.competition.GetCompetitionAthlete
import com.example.model.newClass.competition.GetCompetitionRequest
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.EventListData
import com.example.trainerapp.ApiClass.RatingItem
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.PreferencesManager
import com.example.trainerapp.R
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.ActivityViewCompetitionAnalysisBinding
import com.google.gson.Gson
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
import com.highsoft.highcharts.common.hichartsclasses.HIStyle
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

class ViewCompetitionAnalysisActivity : AppCompatActivity() {
    lateinit var viewCompetitionAnalysisBinding: ActivityViewCompetitionAnalysisBinding

    //required data
    //athlete id , event id, category name, date, area id, data [title,coach_star]
    var athleteId = SelectedValue(null)
    var catId = SelectedValue(null)
    var catName = ""

    var areaId = SelectedValue(null)
    var areaName = ""
    var eventId = SelectedValue(null)

    var compDate = ""
    var compName = ""


    lateinit var competitionData: MutableList<Competition.CompetitionData>
    lateinit var competition: MutableList<Competition.CompetitionData>
    lateinit var eventList: ArrayList<EventListData.testData>
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    lateinit var preferenceManager: PreferencesManager
    lateinit var chartView: HIChartView

    var coach = true
    var athlete = true
    lateinit var compAdapter: ViewCompetitionAdapter
    lateinit var analysisData: MutableList<RatingItem>


    private lateinit var warmupDesc: String
    private lateinit var mentalDesc: String
    private lateinit var physicalDesc: String
    private lateinit var strategyDesc: String
    private lateinit var techniqueDesc: String

    var arrWarmup = arrayOf(
        "Activation",
        "Motivation",
        "Goals Clearness",
        "Enthusiasm",
        "Confidence",
        "Focus",
        "Overall well-being",
        "Experienced Energy",
        "Positive immersion in Pre-Competition Routine"
    )
    var arrMentalArea =
        arrayOf("Goal setting and focus", "Emotions", "Self Talk", "Arousal", "Imagery")
    var arrPhysicalArea = arrayOf(
        "Speed",
        "Strength",
        "General Endurance",
        "Speed Endurance",
        "Reactivity",
        "Pace and action Frequency"
    )
    var arrStrategy = arrayOf("Plan A", "Plan B", "Improvisation")
    var arrTechniqueAndTactic = arrayOf(
        "Technique Repertoire Suitability",
        "Tactic Repertoire Suitability",
        "Tactic Choices Suitability",
        "Defence actions",
        "Attack actions",
        "Counter actions",
        "Cross-Counter Actions",
        "Play in advance actions"
    )

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
                        Utils.setUnAuthDialog(this@ViewCompetitionAnalysisActivity)
                    } else {
                        Toast.makeText(
                            this@ViewCompetitionAnalysisActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Toast.makeText(
                        this@ViewCompetitionAnalysisActivity,
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
        viewCompetitionAnalysisBinding = ActivityViewCompetitionAnalysisBinding.inflate(layoutInflater)
        setContentView(viewCompetitionAnalysisBinding.root)
        initViews()
        checkButtonClick()

        val userType = preferenceManager.GetFlage()
        if (userType == "Athlete"){
            getCompetitionDataAthlete()
        }else {
            getCompetitionData()
        }

    }

    private fun checkButtonClick() {
        viewCompetitionAnalysisBinding.save.isActivated
        viewCompetitionAnalysisBinding.save.isEnabled = true
        viewCompetitionAnalysisBinding.save.isClickable = true
        viewCompetitionAnalysisBinding.save.setBackgroundResource(R.drawable.card_select_1)

        viewCompetitionAnalysisBinding.save.setOnClickListener {
            val dataList: MutableList<RatingData> = mutableListOf()
            val dataListAthlete: MutableList<RatingDataAthlete> = mutableListOf()


            for (i in analysisData) {

                val userType = preferenceManager.GetFlage()
                preferenceManager.GetFlage()

                if (userType == "Athlete"){
                    if (i.athleteRating != 0) {
                        viewCompetitionAnalysisBinding.save.setBackgroundResource(R.drawable.card_select_1)
                        dataListAthlete.add(RatingDataAthlete(i.name!!, i.athleteRating!!))
                    } else {
                        Toast.makeText(this, "Please Rating Athlete All Fields", Toast.LENGTH_SHORT).show()
                        break
                        return@setOnClickListener
                    }
                }else{
                    if (i.coachRating != 0) {
                        viewCompetitionAnalysisBinding.save.setBackgroundResource(R.drawable.card_select_1)
                        dataList.add(RatingData(i.name!!, i.coachRating!!))
                    } else {
                        Toast.makeText(this, "Please Rating Coach All Fields", Toast.LENGTH_SHORT).show()
                        break
                        return@setOnClickListener

                    }
                }
            }
            if (dataList.isNotEmpty()) {

                val userType = preferenceManager.GetFlage()

                if (userType == "Athlete") {
                    saveCompetitionDataAthlete(dataListAthlete)
                }else{
                    saveCompetitionData(dataList)
                }
            }
            if (dataListAthlete.isNotEmpty()) {

                val userType = preferenceManager.GetFlage()

                if (userType == "Athlete") {
                    saveCompetitionDataAthlete(dataListAthlete)
                }else{
                    saveCompetitionData(dataList)
                }
            }
        }

        viewCompetitionAnalysisBinding.back.setOnClickListener { finish() }
    }

    private fun getCompetitionData() {
        try {
            viewCompetitionAnalysisBinding.progressBar.visibility = View.VISIBLE

            val request = GetCompetitionAthlete(
                athlete_id = athleteId.id?.toInt() ?: 0,
                eventId = eventId.id!!.toInt(),
                category = catName,
                date = compDate,
                areaId = areaId.id!!.toInt()
            )

            apiInterface.GetCompetitionAnalysisDataAll(request)
                .enqueue(object : Callback<GetCompetitionAll> {
                    override fun onResponse(call: Call<GetCompetitionAll>, response: Response<GetCompetitionAll>) {
                        viewCompetitionAnalysisBinding.progressBar.visibility = View.GONE

                        if (response.isSuccessful) {
                            val responseBody = response.body()
                            val gson = Gson()
                            val jsonResponse = gson.toJson(responseBody)
                            Log.d("API Response JSON", jsonResponse)

                            if (responseBody?.status == true) {
                                val compData = responseBody.data

                                if (compData != null) {
                                    Log.d("Event ID:", "Item Event ID: ${compData.event_id}")
                                    Log.d("Category:", "Item Category: ${compData.category}")
                                    Log.d("Date:", "Item Date: ${compData.date}")
                                    Log.d("Area ID:", "Item Area ID: ${compData.competition_analysis_area?.id}")
                                    Log.d("Athlete Name:", "Athlete: ${compData.athlete?.name}")

                                    competitionData.clear()
                                    competitionData.add(compData)

                                    Log.d("Final Competition Data", "$competitionData")

                                    setRatingData(competitionData,true,false)
                                    competitionData.get(0).competition_progress?.let {
                                        setChartData(
                                            it
                                        )
                                    }
                                } else {
                                    Log.e("API Error", "Data is null in response: $jsonResponse")
                                }
                            } else {
                                Log.e("API Error", "Status is false, response: $jsonResponse")
                            }
                        } else {
                            Log.e("API Failure", "Response code: ${response.code()}, message: ${response.message()}")
                        }
                    }

                    override fun onFailure(call: Call<GetCompetitionAll>, t: Throwable) {
                        viewCompetitionAnalysisBinding.progressBar.visibility = View.GONE
                        Log.d("API Failure", "Error: ${t.message}")
                    }
                })
        } catch (e: Exception) {
            Log.d("Error :-", "${e.message}")
        }
    }

    private fun getCompetitionDataAthlete() {
        try {
            viewCompetitionAnalysisBinding.progressBar.visibility = View.VISIBLE

            val request = GetCompetitionRequest(
                eventId = eventId.id!!.toInt(),
                category = catName,
                date = compDate,
                areaId = areaId.id!!.toInt()
            )

            apiInterface.GetCompetitionAnalysisDataAthleteAll(request)
                .enqueue(object : Callback<GetCompetitionAll> {
                    override fun onResponse(call: Call<GetCompetitionAll>, response: Response<GetCompetitionAll>) {
                        viewCompetitionAnalysisBinding.progressBar.visibility = View.GONE

                        if (response.isSuccessful) {
                            val responseBody = response.body()
                            val gson = Gson()
                            val jsonResponse = gson.toJson(responseBody)
                            Log.d("API Response JSON", jsonResponse)

                            if (responseBody?.status == true) {
                                val compData = responseBody.data

                                if (compData != null) {
                                    Log.d("Event ID:", "Item Event ID: ${compData.event_id}")
                                    Log.d("Category:", "Item Category: ${compData.category}")
                                    Log.d("Date:", "Item Date: ${compData.date}")
                                    Log.d("Area ID:", "Item Area ID: ${compData.competition_analysis_area?.id}")
                                    Log.d("Athlete Name:", "Athlete: ${compData.athlete?.name}")

                                    competitionData.clear()
                                    competitionData.add(compData)

                                    Log.d("Final Competition Data", "$competitionData")

                                    setRatingData(competitionData,false,true)
                                    competitionData.get(0).competition_progress?.let {
                                        setChartData(
                                            it
                                        )
                                    }
                                } else {
                                    Log.e("API Error", "Data is null in response: $jsonResponse")
                                }
                            } else {
                                Log.e("API Error", "Status is false, response: $jsonResponse")
                            }
                        } else {
                            Log.e("API Failure", "Response code: ${response.code()}, message: ${response.message()}")
                        }
                    }

                    override fun onFailure(call: Call<GetCompetitionAll>, t: Throwable) {
                        viewCompetitionAnalysisBinding.progressBar.visibility = View.GONE
                        Log.d("API Failure", "Error: ${t.message}")
                    }
                })
        } catch (e: Exception) {
            Log.d("Error :-", "${e.message}")
        }
    }

    private fun setRatingData(competition: MutableList<Competition.CompetitionData>,isCoach:Boolean, isAthlete:Boolean) {
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
                        isByCoach = isCoach,
                        isByAthlete = isAthlete
                    )
                )

            }
        }

        val userType = preferenceManager.GetFlage()

        if (userType == "Athlete") {
            viewCompetitionAnalysisBinding.performanceRly.visibility = View.VISIBLE
            compAdapter = ViewCompetitionAdapter(analysisData, this, false, true)
            viewCompetitionAnalysisBinding.performanceRly.adapter = compAdapter
        } else {
            if ("false" == "false"){
                viewCompetitionAnalysisBinding.performanceRly.visibility = View.VISIBLE
                compAdapter = ViewCompetitionAdapter(analysisData, this, true, false)
                viewCompetitionAnalysisBinding.performanceRly.adapter = compAdapter
            }else{
                viewCompetitionAnalysisBinding.performanceRly.visibility = View.VISIBLE
                compAdapter = ViewCompetitionAdapter(analysisData, this, true, false)
                viewCompetitionAnalysisBinding.performanceRly.adapter = compAdapter
            }

        }

    }

    private fun saveCompetitionData(data: MutableList<RatingData>) {
        try {
            viewCompetitionAnalysisBinding.progressBar.visibility = View.VISIBLE
            val addCompetitionData = AddCompetitionBody(
                athleteId = athleteId.id!!,
                eventId = eventId.id!!,
                categoryName = catName,
                date = compDate,
                areaId = areaId.id!!,
                data = data
            )

            Log.d("AddCompetition Data :-", "$addCompetitionData")
            apiInterface.CreateCompetitionAnalysisData(addCompetitionData)!!.enqueue(object :
                Callback<Competition> {
                override fun onResponse(
                    call: Call<Competition>,
                    response: Response<Competition>
                ) {
                    viewCompetitionAnalysisBinding.progressBar.visibility = View.GONE
                    Log.d("TAG", response.code().toString() + "")

                    val code = response.code()
                    if (code == 200) {
                        val success: Boolean = response.body()!!.status!!
                        if (success) {
                            val data = response.body()!!
                            Log.d("Athlete :- Data ", "${data}")
                            val message = data.message ?: "Success"
                            Toast.makeText(this@ViewCompetitionAnalysisActivity, message, Toast.LENGTH_SHORT).show()
//                            val chartData = data.data!!
                            if (data.data!!.competition_progress != null) {
                                val chartData = data.data.competition_progress

                                Log.d("CTTTCTCTT", "onResponse: $chartData")
                                if (chartData!!.isNotEmpty()) {
                                    setChartData(chartData)
                                }
                            }
//                            finish()
                            //setRadarChart(rangeData)
                        }
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@ViewCompetitionAnalysisActivity)
                    } else {
                        Toast.makeText(
                            this@ViewCompetitionAnalysisActivity,
                            "Failed",
                            Toast.LENGTH_SHORT
                        ).show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<Competition>, t: Throwable) {
                    viewCompetitionAnalysisBinding.progressBar.visibility = View.GONE
                    Log.d("Tag", t.message.toString())
                }

            })
        } catch (e: Exception) {
            Log.d("Exception Object :-", "${e.message}")
        }
    }

    private fun saveCompetitionDataAthlete(data: MutableList<RatingDataAthlete>) {
        try {
            viewCompetitionAnalysisBinding.progressBar.visibility = View.VISIBLE
            val addCompetitionData = AddCompetitionBodyAthlete(
                eventId = eventId.id!!,
                categoryName = catName,
                date = compDate,
                areaId = areaId.id!!,
                data = data
            )

            Log.d("AddCompetition Data :-", "$addCompetitionData")
            apiInterface.CreateCompetitionAnalysisDataAthelete(addCompetitionData)!!.enqueue(object :
                Callback<Competition> {
                override fun onResponse(
                    call: Call<Competition>,
                    response: Response<Competition>
                ) {
                    viewCompetitionAnalysisBinding.progressBar.visibility = View.GONE
                    Log.d("TAG", response.code().toString() + "")

                    val code = response.code()
                    if (code == 200) {
                        val success: Boolean = response.body()!!.status!!
                        if (success) {
                            val data = response.body()!!
                            Log.d("Athlete :- Data ", "${data}")
                            val message = data.message ?: "Success"
                            Toast.makeText(
                                this@ViewCompetitionAnalysisActivity,
                                message,
                                Toast.LENGTH_SHORT
                            ).show()
//                            val chartData = data.data!!
                            if (data.data!!.competition_progress != null) {
                                val chartData = data.data.competition_progress
                                if (chartData!!.isNotEmpty()) {
                                    setChartData(chartData)
                                }
                            }
//                            finish()
                            //setRadarChart(rangeData)
                        }
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@ViewCompetitionAnalysisActivity)
                    } else {
                        Toast.makeText(
                            this@ViewCompetitionAnalysisActivity,
                            "Failed",
                            Toast.LENGTH_SHORT
                        ).show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<Competition>, t: Throwable) {
                    viewCompetitionAnalysisBinding.progressBar.visibility = View.GONE
                    Log.d("Tag", t.message.toString())
                }

            })
        } catch (e: Exception) {
            Log.d("Exception Object :-", "${e.message}")
        }
    }

    private fun setChartData(competitionProgress: List<Competition.CompetitionProgress>?) {
        viewCompetitionAnalysisBinding.chartView.visibility = View.VISIBLE
        viewCompetitionAnalysisBinding.chartView.addFont(R.font.poppins_medium)

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
        xAxis.categories = competitionProgress.mapIndexed { index, data -> (index + 1).toString() } as ArrayList
        xAxis.labels.distance = 10
        xAxis.labels.rotation = 10

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
        itemstyle.fontSize = "12px"
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


    private fun initViews() {
        preferenceManager = PreferencesManager(this)
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        eventList = arrayListOf()
        chartView = viewCompetitionAnalysisBinding.chartView
        analysisData = mutableListOf()
        competition = mutableListOf()
        competitionData = mutableListOf()

        warmupDesc = getString(R.string.warmupDesc)
        mentalDesc = getString(R.string.mentalDesc)
        physicalDesc = getString(R.string.physicalDesc)
        strategyDesc = getString(R.string.strategyDesc)
        techniqueDesc = getString(R.string.techniqueDesc)
        getEventData()
        getIntData()

        Log.d(
            "Values Passed :- ",
            "${athleteId.id}\n${catId.id}\n${areaId.id}\n${areaName}\n${compDate}\n${compName}\n${eventId.id}"
        )



        viewCompetitionAnalysisBinding.tvTitle.text = areaName

        val usertype = preferenceManager.GetFlage()
        if (usertype == "Athlete") {
            when (areaId.id) {
                1 -> {
                    viewCompetitionAnalysisBinding.tvSubTitle.text = warmupDesc
                    for (i in arrWarmup) {
                        analysisData.add(
                            RatingItem(
                                name = i,
                                coachRating = 0,
                                athleteRating = 0,
                                isByCoach = false,
                                isByAthlete = athlete
                            )
                        )
                    }
                    setRecyclerViewAthlete()

                }

                2 -> {
                    viewCompetitionAnalysisBinding.tvSubTitle.text = mentalDesc
                    for (i in arrMentalArea) {
                        analysisData.add(
                            RatingItem(
                                name = i,
                                coachRating = 0,
                                athleteRating = 0,
                                isByCoach = false,
                                isByAthlete = athlete
                            )
                        )
                    }
                    setRecyclerViewAthlete()
                }

                3 -> {
                    viewCompetitionAnalysisBinding.tvSubTitle.text = physicalDesc
                    for (i in arrPhysicalArea) {
                        analysisData.add(
                            RatingItem(
                                name = i,
                                coachRating = 0,
                                athleteRating = 0,
                                isByCoach = false,
                                isByAthlete = athlete
                            )
                        )
                    }
                    setRecyclerViewAthlete()
                }

                4 -> {
                    viewCompetitionAnalysisBinding.tvSubTitle.text = strategyDesc
                    for (i in arrStrategy) {
                        analysisData.add(
                            RatingItem(
                                name = i.toString(),
                                coachRating = 0,
                                athleteRating = 0,
                                isByCoach = false,
                                isByAthlete = athlete
                            )
                        )
                    }
                    setRecyclerViewAthlete()
                }

                5 -> {
                    viewCompetitionAnalysisBinding.tvSubTitle.text = techniqueDesc
                    for (i in arrTechniqueAndTactic) {
                        analysisData.add(
                            RatingItem(
                                name = i.toString(),
                                coachRating = 0,
                                athleteRating = 0,
                                isByCoach = false,
                                isByAthlete = athlete
                            )
                        )
                    }
                    setRecyclerViewAthlete()
                }
            }
        }else{
            when (areaId.id) {
                1 -> {
                    viewCompetitionAnalysisBinding.tvSubTitle.text = warmupDesc
                    for (i in arrWarmup) {
                        analysisData.add(
                            RatingItem(
                                name = i,
                                coachRating = 0,
                                athleteRating = 0,
                                isByCoach = coach,
                                isByAthlete = false
                            )
                        )
                    }
                    setRecyclerView()

                }

                2 -> {
                    viewCompetitionAnalysisBinding.tvSubTitle.text = mentalDesc
                    for (i in arrMentalArea) {
                        analysisData.add(
                            RatingItem(
                                name = i,
                                coachRating = 0,
                                athleteRating = 0,
                                isByCoach = coach,
                                isByAthlete = false
                            )
                        )
                    }
                    setRecyclerView()
                }

                3 -> {
                    viewCompetitionAnalysisBinding.tvSubTitle.text = physicalDesc
                    for (i in arrPhysicalArea) {
                        analysisData.add(
                            RatingItem(
                                name = i,
                                coachRating = 0,
                                athleteRating = 0,
                                isByCoach = coach,
                                isByAthlete = false
                            )
                        )
                    }
                    setRecyclerView()
                }

                4 -> {
                    viewCompetitionAnalysisBinding.tvSubTitle.text = strategyDesc
                    for (i in arrStrategy) {
                        analysisData.add(
                            RatingItem(
                                name = i.toString(),
                                coachRating = 0,
                                athleteRating = 0,
                                isByCoach = coach,
                                isByAthlete = false
                            )
                        )
                    }
                    setRecyclerView()
                }

                5 -> {
                    viewCompetitionAnalysisBinding.tvSubTitle.text = techniqueDesc
                    for (i in arrTechniqueAndTactic) {
                        analysisData.add(
                            RatingItem(
                                name = i.toString(),
                                coachRating = 0,
                                athleteRating = 0,
                                isByCoach = coach,
                                isByAthlete = false
                            )
                        )
                    }
                    setRecyclerView()
                }
            }

        }
    }

    private fun getEventData() {
        apiInterface.GetEvent()?.enqueue(object : Callback<EventListData?> {
            override fun onResponse(
                call: Call<EventListData?>,
                response: Response<EventListData?>
            ) {
                viewCompetitionAnalysisBinding.progressBar.visibility = View.GONE
                Log.d("TAG", response.code().toString() + "")
                val code = response.code()
                if (code == 200) {
                    val resource: EventListData? = response.body()
                    val Success: Boolean = resource?.status!!
                    val Message: String = resource.message!!
                    if (Success == true) {
                        if (resource.data != null) {
                            eventList = resource.data!!
                        }
                    }
                } else if (code == 403) {
                    Utils.setUnAuthDialog(this@ViewCompetitionAnalysisActivity)
                } else {
                    Toast.makeText(
                        this@ViewCompetitionAnalysisActivity,
                        "" + response.message(),
                        Toast.LENGTH_SHORT
                    ).show()
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<EventListData?>, t: Throwable) {
                viewCompetitionAnalysisBinding.progressBar.visibility = View.GONE
                Toast.makeText(
                    this@ViewCompetitionAnalysisActivity,
                    "" + t.message,
                    Toast.LENGTH_SHORT
                )
                    .show()
                call.cancel()
            }
        })
    }

    private fun getIntData() {
        athleteId.id = intent.getIntExtra("athleteId", 0)
        catId.id = intent.getIntExtra("catId", 0)
        areaId.id = intent.getIntExtra("areaId", 0)
        areaName = intent.getStringExtra("areaName").toString()
        compDate = intent.getStringExtra("compDate").toString()
        compName = intent.getStringExtra("compName").toString()
        catName = intent.getStringExtra("catName").toString()
        eventId.id = intent.getIntExtra("compId", 0)
    }

    private fun setRecyclerView() {
        viewCompetitionAnalysisBinding.performanceRly.layoutManager = LinearLayoutManager(this)
        compAdapter = ViewCompetitionAdapter(analysisData, this, true,false)
        viewCompetitionAnalysisBinding.performanceRly.adapter = compAdapter
    }

    private fun setRecyclerViewAthlete() {
        viewCompetitionAnalysisBinding.performanceRly.layoutManager = LinearLayoutManager(this)
        compAdapter = ViewCompetitionAdapter(analysisData, this, false,athlete)
        viewCompetitionAnalysisBinding.performanceRly.adapter = compAdapter
    }
}

//private fun setRadarChart(rangeData: MutableList<Data>) {
//        viewCompetitionAnalysisBinding.chartView.visibility = View.VISIBLE
//        val options = HIOptions()
//
//        val chart = HIChart()
//        chart.polar = true
//        chart.height = "100%"
//        options.chart = chart
//        chartView.theme = "dark"
//
//        val pane = HIPane()
//        pane.startAngle = 0
//        pane.endAngle = 360
//        options.pane = arrayListOf(pane)
//
//        val xAxis = HIXAxis()
////        xAxis.tickInterval = 45
//        xAxis.min = 0
//        xAxis.max = rangeData.size
//        xAxis.labels = HILabels()
//        xAxis.labels.style = HICSSObject().apply { color = "#FFFFFF" }
//        xAxis.categories =
//            rangeData.mapIndexed { index, data -> (index + 1).toString() } as ArrayList
//        xAxis.labels.distance = 10
//        xAxis.labels.rotation = 0
//
//        xAxis.labels.formatter = HIFunction(
//            HIFunctionInterface { f: HIChartContext ->
//                f.getProperty(
//                    "value"
//                ).toString() + ""
//            }, arrayOf("value")
//        )
//        options.xAxis = object : ArrayList<HIXAxis?>() {
//            init {
//                add(xAxis)
//            }
//        }
//
//        val yAxis = HIYAxis()
//        yAxis.min = 0
//        yAxis.labels = HILabels()
//        yAxis.labels.enabled = false
//        options.yAxis = arrayListOf(yAxis)
//
//        val plotOptions = HIPlotOptions()
//        plotOptions.series = HISeries()
//        plotOptions.series.pointStart = 0
//        plotOptions.column = HIColumn()
//        plotOptions.column.pointPadding = 0
//        plotOptions.column.groupPadding = 0
//        options.plotOptions = plotOptions
//        chart.backgroundColor = HIColor.initWithRGB(0, 0, 0)
//
//        //val athleteData = rangeData.map { it.coach_star.toFloat() }
//        val coachData = rangeData.map { it.coach_star.toFloat() }
//
//        //Log.d("Series Data :-", "athlete :- $athleteData")
//        Log.d("Series Data :-", "athlete :- $coachData")
//
//        val series1 = HIColumn()
//        series1.name = "Athlete"
//        series1.color = HIColor.initWithRGB(251, 193, 82)
//
//        val hiTitle = HITitle()
//        hiTitle.text = ""
//        options.title = hiTitle
//
//        val series3 = HIColumn()
//        series3.name = "Coach"
//
//        series3.color = HIColor.initWithRGB(255, 0, 0)
//
//        val series3_data = coachData.map { it }
//        Log.d("Series Data :-", "coach :- $series3_data")
//        series3.data = ArrayList(series3_data)
//
//        val legend = HILegend()
//        legend.enabled = true
//        val itemstyle = HICSSObject()
//        itemstyle.fontSize = "14px"
//        itemstyle.fontWeight = "regular"
//        itemstyle.color = "#FFFFFF"
//        legend.itemStyle = itemstyle
//        options.legend = legend
//        options.series = ArrayList(Arrays.asList(series1, series3))
//
//        val exporting = HIExporting()
//        exporting.enabled = false
//        options.exporting = exporting
//
//        val credits = HICredits()
//        credits.enabled = false
//        options.credits = credits
//
//
//        chartView.options = options
//
//    }