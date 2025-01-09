package com.example.trainerapp.performance_profile.view_graph

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.model.performance_profile.performance.category.PerformanceCategoryData
import com.example.model.performance_profile.performance.quality.PerformanceQuality
import com.example.model.performance_profile.performance.quality.PerformanceQualityData
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.PreferencesManager
import com.example.trainerapp.R
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.ActivityViewPerformanceProfileBinding
import com.highsoft.highcharts.common.HIColor
import com.highsoft.highcharts.common.hichartsclasses.HIBackground
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Arrays
import kotlin.math.log

class ViewPerformanceProfileActivity : AppCompatActivity() {
    lateinit var viewPerformanceProfileBinding: ActivityViewPerformanceProfileBinding
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    lateinit var preferenceManager: PreferencesManager
    lateinit var qualityData: MutableList<PerformanceQualityData>
    lateinit var categoryData: MutableList<PerformanceCategoryData>
    var title = ""
    var catId = ""
    var athleteId = ""
    lateinit var chartView: HIChartView

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
                        Utils.setUnAuthDialog(this@ViewPerformanceProfileActivity)
                    } else {
                        Toast.makeText(
                            this@ViewPerformanceProfileActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Toast.makeText(
                        this@ViewPerformanceProfileActivity,
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
        viewPerformanceProfileBinding =
            ActivityViewPerformanceProfileBinding.inflate(layoutInflater)
        setContentView(viewPerformanceProfileBinding.root)
        initView()
        checkButtonClick()
    }

    private fun checkButtonClick() {
        viewPerformanceProfileBinding.back.setOnClickListener {
            finish()
        }
    }

    private fun initView() {
        preferenceManager = PreferencesManager(this)
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        qualityData = mutableListOf()
        categoryData = mutableListOf()

        catId = intent.getStringExtra("catId").toString()
        athleteId = intent.getStringExtra("athleteId").toString()
        title = intent.getStringExtra("catName").toString()
        viewPerformanceProfileBinding.title.text = title

        Log.d("GHGHHGH", "initView: $catId")

        chartView = viewPerformanceProfileBinding.chartView

        val userType = preferenceManager.GetFlage()

        if (userType == "Athlete") {
            loadPerformanceQualityAthlete(catId.toInt())
        } else {
            getQualityData(catId, athleteId)

        }
    }

    private fun getQualityData(catId: String, athleteId: String) {

        try {

            qualityData.clear()
            viewPerformanceProfileBinding.ProgressBar.visibility = View.VISIBLE
            apiInterface.GetPerformanceQuality(
                id = athleteId.toInt(),
                performId = catId.toInt()
            ).enqueue(object : Callback<PerformanceQuality> {
                override fun onResponse(
                    call: Call<PerformanceQuality>,
                    response: Response<PerformanceQuality>
                ) {
                    Log.d("TAG", response.code().toString() + "")
                    val code = response.code()
                    if (code == 200) {
                        if (response.isSuccessful) {
                            val data = response.body()!!.data ?: mutableListOf()

                            val addedQualityIds = qualityData.map { it.id }.toMutableSet()

                            for (quality in data) {
                                if (!addedQualityIds.contains(quality.id)) {
                                    qualityData.add(quality)
                                    addedQualityIds.add(quality.id) // Mark as added
                                }
                            }
                            if (qualityData.isNotEmpty()) {
                                setChartData(qualityData)
                            } else {
                                viewPerformanceProfileBinding.ProgressBar.visibility = View.GONE
                                Toast.makeText(
                                    this@ViewPerformanceProfileActivity,
                                    "No Data Found",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@ViewPerformanceProfileActivity)
                    } else {
                        Toast.makeText(
                            this@ViewPerformanceProfileActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<PerformanceQuality>, t: Throwable) {
                    viewPerformanceProfileBinding.ProgressBar.visibility = View.GONE
                    Toast.makeText(
                        this@ViewPerformanceProfileActivity,
                        "" + t.message,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    call.cancel()
                }
            })


        } catch (e: Exception) {
            Log.d("Exception :- ", "${e.message}")
        }
    }

    private fun loadPerformanceQualityAthlete(id: Int) {
        try {

            apiInterface.GetPerformanceQualityAthlete(performId = id)
                .enqueue(object : Callback<PerformanceQuality> {
                    override fun onResponse(
                        call: Call<PerformanceQuality>,
                        response: Response<PerformanceQuality>
                    ) {
                        CoroutineScope(Dispatchers.Main).launch {
                            if (response.isSuccessful) {
                                val data = response.body()?.data ?: mutableListOf()
                                Log.d(
                                    "PerformanceQuality",
                                    "Loaded qualities for ID $id: ${data.size} items"
                                )

                                synchronized(this@ViewPerformanceProfileActivity) {
                                    qualityData.addAll(data.distinctBy { it.id })
                                }

                                if (qualityData.isNotEmpty()) {
                                    setChartData(qualityData)
                                } else {
                                    viewPerformanceProfileBinding.ProgressBar.visibility = View.GONE
                                    Toast.makeText(
                                        this@ViewPerformanceProfileActivity,
                                        "No Data Found",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }


                            } else {
                                Log.e("PerformanceQuality", "Failed to load qualities for ID $id: ${response.message()}")
                            }
                        }
                    }

                    override fun onFailure(call: Call<PerformanceQuality>, t: Throwable) {
                        CoroutineScope(Dispatchers.Main).launch {
                            Log.e("PerformanceQuality", "Error loading qualities for ID $id: ${t.message}")
                        }
                    }
                })

        } catch (e: Exception) {
            Log.e("CATCH", "loadPerformanceQualityAthlete: ${e.message.toString()}")
        }
    }


    private fun setChartData(competitionProgress: MutableList<PerformanceQualityData>) {

        viewPerformanceProfileBinding.chartView.visibility = View.VISIBLE
        val options = HIOptions()
        viewPerformanceProfileBinding.chartView.addFont(R.font.poppins_medium)

        val chart = HIChart()
        chart.polar = true
        chart.height = "120%"
        options.chart = chart
        chartView.theme = "dark"

        val pane = HIPane()
        pane.startAngle = 0
        pane.endAngle = 360
        pane.background = arrayListOf(
            HIBackground().apply {
                backgroundColor = HIColor.initWithRGBA(255, 255, 255, 0.05)
                innerRadius = "0%"
                outerRadius = "100%"
                shape = "circle"
            }
        )
        options.pane = arrayListOf(pane)

        val xAxis = HIXAxis()
        xAxis.min = 0
        xAxis.max = competitionProgress.size
        xAxis.labels = HILabels()
        xAxis.labels.style = HICSSObject().apply {
            color = "#FFFFFF"
            fontFamily = "poppins_medium"
            color = "#FFFFFF"
            fontSize = "12px"
        }
        xAxis.categories =
            competitionProgress.mapIndexed { index, data -> data.name } as ArrayList
        xAxis.labels.distance = 2
        xAxis.labels.rotation = 10

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
        val yAxis = HIYAxis().apply {
            min = 0
            tickPositions = arrayListOf(0, 2.5, 5, 10) // Add tick positions
            labels = HILabels().apply {
                enabled = true
                style = HICSSObject().apply {
                    color = "#FFFFFF"
                    fontSize = "12px"
                    fontFamily = "poppins_medium"
                    color = "#FFFFFF"
                    fontSize = "12px"
                }
            }
            title = HITitle().apply {
                text = "" // No title
            }
        }
        options.yAxis = arrayListOf(yAxis)

        val plotOptions = HIPlotOptions()
        plotOptions.series = HISeries()
        plotOptions.series.pointStart = 0
        plotOptions.column = HIColumn()
        plotOptions.column.pointPadding = 0
        plotOptions.column.groupPadding = 0
        options.plotOptions = plotOptions
        chart.backgroundColor = HIColor.initWithRGB(0, 0, 0)

        val athleteData = competitionProgress.mapNotNull { it.athelet_score?.toFloat() }
        val coachData = competitionProgress.mapNotNull { it.coach_score?.toFloat() }

        Log.d("athlete data :-", "$athleteData")
        Log.d("coach data :-", "$coachData")
        val hiTitle = HITitle()
        hiTitle.text = ""
        options.title = hiTitle

        val series1_data = athleteData.map { it }
        val series1 = HIColumn()
        series1.name = "Athlete"
        series1.color = HIColor.initWithRGB(255, 0, 0)
        series1.data = ArrayList(series1_data)

        val series3_data = coachData.map { it }
        val series3 = HIColumn()
        series3.name = "Coach"
        series3.color = HIColor.initWithRGB(83, 83, 83)
        series3.data = ArrayList(series3_data)

//        val legend = HILegend()
//        legend.enabled = true
//        val itemstyle = HICSSObject()
//        itemstyle.fontSize = "14px"
//        itemstyle.fontWeight = "regular"
//        itemstyle.color = "#FFFFFF"
//        legend.itemStyle = itemstyle


        val legend = HILegend().apply {
            enabled = true
            itemStyle = HICSSObject().apply {
                fontSize = "14px"
                fontWeight = "regular"
                color = "#FFFFFF"
                fontFamily = "poppins_medium"
            }
        }
        options.legend = legend
        options.legend = legend
        options.series = ArrayList(Arrays.asList(series1, series3))

        val exporting = HIExporting()
        exporting.enabled = false
        options.exporting = exporting

        val credits = HICredits()
        credits.enabled = false
        options.credits = credits

        chartView.options = options
        chartView.invalidate() // Request a layout update
        chartView.requestLayout()



        viewPerformanceProfileBinding.ProgressBar.visibility = View.GONE
    }
}