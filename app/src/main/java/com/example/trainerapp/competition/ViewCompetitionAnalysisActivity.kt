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
import com.example.model.competition.create.RatingData
import com.example.model.newClass.competition.Competition
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.EventListData
import com.example.trainerapp.ApiClass.RatingItem
import com.example.trainerapp.ApiClass.RegisterData
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

    lateinit var eventList: ArrayList<EventListData.testData>
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    lateinit var preferenceManager: PreferencesManager
    lateinit var chartView: HIChartView

    var coach = true
    lateinit var compAdapter: ViewCompetitionAdapter
    lateinit var analysisData: MutableList<RatingItem>

    var warmupDesc = "Give a score to the following entries pre-competition and during warm-up"
    var mentalDesc = "Give a score to the following entries."
    var physicalDesc = "Give a score to the following entries."
    var strategyDesc = "Give a score to the following entries."
    var techniqueDesc = "Give a score to the following entries."

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
        viewCompetitionAnalysisBinding =
            ActivityViewCompetitionAnalysisBinding.inflate(layoutInflater)
        setContentView(viewCompetitionAnalysisBinding.root)
        initViews()
        checkButtonClick()
    }

    private fun checkButtonClick() {
        viewCompetitionAnalysisBinding.save.isActivated
        viewCompetitionAnalysisBinding.save.isEnabled = true
        viewCompetitionAnalysisBinding.save.isClickable = true


        viewCompetitionAnalysisBinding.save.setOnClickListener {
            val dataList: MutableList<RatingData> = mutableListOf()
            for (i in analysisData) {

                val userType = preferenceManager.GetFlage()

                if (userType == "Athlete"){

                    if (i.athleteRating != 0) {
                        viewCompetitionAnalysisBinding.save.setBackgroundResource(R.drawable.card_select_1)
                        dataList.add(RatingData(i.name!!, i.athleteRating!!))
                    } else {
                        Toast.makeText(this, "Please Rating Athlete All Fields", Toast.LENGTH_SHORT)
                            .show()
                        break
                    }
                }else{
                    if (i.coachRating != 0) {
                    viewCompetitionAnalysisBinding.save.isEnabled = true
                        viewCompetitionAnalysisBinding.save.setBackgroundResource(R.drawable.card_select_1)
                        dataList.add(RatingData(i.name!!, i.coachRating!!))
                    } else {
                        Toast.makeText(this, "Please Rating Coach All Fields", Toast.LENGTH_SHORT)
                            .show()
                        break
                    }
                }

            }
            if (dataList.isNotEmpty()) {
                saveCompetitionData(dataList)
            }
        }

        viewCompetitionAnalysisBinding.back.setOnClickListener { finish() }
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
                            Toast.makeText(
                                this@ViewCompetitionAnalysisActivity,
                                message,
                                Toast.LENGTH_SHORT
                            ).show()
//                            val chartData = data.data!!
                            if (data.data!!.competition_progress != null) {
                                val chartData = data.data.competition_progress
                                if (chartData!!.isNotEmpty()) {
                                    setChartOnlineData(chartData)
                                }
                            }
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

    private fun setChartOnlineData(chartData: List<Competition.CompetitionProgress>) {
        viewCompetitionAnalysisBinding.chartView.visibility = View.VISIBLE
        val options = HIOptions()

        val chart = HIChart()
        chart.polar = true
        chart.height = "100%"
        options.chart = chart
        chartView.theme = "dark"

        val pane = HIPane()
        pane.startAngle = 0
        pane.endAngle = 360
        options.pane = arrayListOf(pane)

        val xAxis = HIXAxis()
//        xAxis.tickInterval = 45
        xAxis.min = 0
        xAxis.max = chartData.size
        xAxis.labels = HILabels()
        xAxis.labels.style = HICSSObject().apply { color = "#FFFFFF" }
        xAxis.categories =
            chartData.mapIndexed { index, data -> (index + 1).toString() } as ArrayList
        xAxis.labels.distance = 10
        xAxis.labels.rotation = 0

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
        chart.backgroundColor = HIColor.initWithRGB(0, 0, 0)

        val athleteData =
            chartData.mapNotNull { it.athlete_star?.toFloat() }.ifEmpty { emptyList() }
        val coachData = chartData.mapNotNull { it.coach_star?.toFloat() }.ifEmpty { emptyList() }

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
        legend.itemStyle = itemstyle
        options.legend = legend
        options.series = ArrayList(Arrays.asList(series1, series3))

        val exporting = HIExporting()
        exporting.enabled = false
        options.exporting = exporting

        val credits = HICredits()
        credits.enabled = false
        options.credits = credits


        chartView.options = options
    }

    private fun initViews() {
        preferenceManager = PreferencesManager(this)
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        eventList = arrayListOf()
        chartView = viewCompetitionAnalysisBinding.chartView
        getEventData()
        getIntData()
        analysisData = mutableListOf()
        Log.d(
            "Values Passed :- ",
            "${athleteId.id}\n${catId.id}\n${areaId.id}\n${areaName}\n${compDate}\n${compName}\n${eventId.id}"
        )
        viewCompetitionAnalysisBinding.tvTitle.text = areaName
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
        compAdapter = ViewCompetitionAdapter(analysisData, this, coach)
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