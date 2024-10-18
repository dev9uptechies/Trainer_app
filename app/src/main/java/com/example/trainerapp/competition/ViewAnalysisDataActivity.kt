package com.example.trainerapp.competition

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.Adapter.competition.ViewCompetitionAdapter
import com.example.model.SelectedValue
import com.example.model.newClass.competition.Competition
import com.example.model.newClass.competition.GetCompetition
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.RatingItem
import com.example.trainerapp.PreferencesManager
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

    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    lateinit var preferenceManager: PreferencesManager

    lateinit var competitionData: MutableList<Competition.CompetitionData>
    lateinit var competition: MutableList<Competition.CompetitionData>
    lateinit var chartView: HIChartView

//    lateinit var eventList: ArrayList<EventListData.testData>
//    var eventData: MutableList<EventListData.testData> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewAnalysisBinding = ActivityViewCompetitionAnalysisBinding.inflate(layoutInflater)
        setContentView(viewAnalysisBinding.root)
        initViews()
        getCompetitionData()
        //getAnalysisData()
        setDefaultRecycler()
        setData()
    }

    private fun setDefaultRecycler() {
        viewAnalysisBinding.performanceRly.layoutManager = LinearLayoutManager(this)
        compAdapter = ViewCompetitionAdapter(analysisData, this, true, true)
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
                                for (i in compData) {
                                    Log.d(
                                        "Competition Data :-",
                                        "${i.category} \n ${i.athlete!!.name}"
                                    )
                                    competitionData.add(i)
                                }
                                Log.d(
                                    "Competition Data :-",
                                    "${
                                        competitionData.filter { it.id == eventId.toInt() }
                                            .get(0).category
                                    }"
                                )
                                competition = competitionData.filter { it.id == eventId.toInt() }
                                    .toMutableList()
                                Log.d("Competition Data co:-", "$competition")
                                if (competition != null) {
                                    setRatingData(competition)
                                    setChartData(competition[0].competition_progress)
                                } else {
                                    Log.d("No Data", "No Data Found")
                                }
                                Log.d("Competition Data :-", "${competitionData}")
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
        xAxis.max = competitionProgress!!.size
        xAxis.labels = HILabels()
        xAxis.labels.style = HICSSObject().apply { color = "#FFFFFF" }
        xAxis.categories =
            competitionProgress.mapIndexed { index, data -> (index + 1).toString() } as ArrayList
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

    private fun setRatingData(competition: MutableList<Competition.CompetitionData>) {
        analysisData.clear()
        for (i in competition) {
            for (j in i.competition_progress!!) {
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
            }
        }
        viewAnalysisBinding.performanceRly.visibility = View.VISIBLE
        compAdapter = ViewCompetitionAdapter(analysisData, this, false, true)
        viewAnalysisBinding.performanceRly.adapter = compAdapter
    }
}