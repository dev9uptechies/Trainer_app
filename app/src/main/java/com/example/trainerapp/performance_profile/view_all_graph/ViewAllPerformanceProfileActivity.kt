package com.example.trainerapp.performance_profile.view_all_graph

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.model.base_class.ChartBase
import com.example.model.base_class.QualityBase
import com.example.model.performance_profile.performance.category.PerformanceCategory
import com.example.model.performance_profile.performance.category.PerformanceCategoryData
import com.example.model.performance_profile.performance.quality.PerformanceQuality
import com.example.model.performance_profile.performance.quality.PerformanceQualityData
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.PreferencesManager
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.ActivityViewAllPerformanceProfileBinding
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
import com.highsoft.highcharts.core.HIChartView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewAllPerformanceProfileActivity : AppCompatActivity() {
    lateinit var viewAllPerformanceProfileBinding: ActivityViewAllPerformanceProfileBinding
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    lateinit var preferenceManager: PreferencesManager
    lateinit var qualityData: MutableList<PerformanceQualityData>
    lateinit var categoryData: MutableList<PerformanceCategoryData>
    var chartBase: MutableList<ChartBase> = mutableListOf()
    var title = ""
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
                        Utils.setUnAuthDialog(this@ViewAllPerformanceProfileActivity)
                    } else {
                        Toast.makeText(
                            this@ViewAllPerformanceProfileActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Toast.makeText(
                        this@ViewAllPerformanceProfileActivity,
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
        viewAllPerformanceProfileBinding =
            ActivityViewAllPerformanceProfileBinding.inflate(layoutInflater)
        setContentView(viewAllPerformanceProfileBinding.root)
        initView()

        if (athleteId.toString() != "null" || athleteId.toString() != "") {
            loadPerformance(athleteId.toInt())
        }

    }

    private fun initView() {
        preferenceManager = PreferencesManager(this)
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        qualityData = mutableListOf()
        categoryData = mutableListOf()

        athleteId = intent.getStringExtra("athleteId").toString()
        Log.d("Athlete Id :-", "$athleteId")
        title = intent.getStringExtra("catName").toString()
        viewAllPerformanceProfileBinding.title.text = title

        chartView = viewAllPerformanceProfileBinding.chartView
    }

    private fun loadPerformance(id: Int) {
        try {
            categoryData.clear()
            qualityData.clear()
            viewAllPerformanceProfileBinding.ProgressBar.visibility = View.VISIBLE
            apiInterface.GetPerformanceCategory(id = id)
                .enqueue(object : Callback<PerformanceCategory> {
                    override fun onResponse(
                        call: Call<PerformanceCategory>,
                        response: Response<PerformanceCategory>
                    ) {
                        viewAllPerformanceProfileBinding.ProgressBar.visibility = View.GONE
                        Log.d("TAG", response.code().toString() + "")
                        val code = response.code()
                        if (code == 200) {
                            if (response.isSuccessful) {
                                val data = response.body()!!.data ?: mutableListOf()

                                val addedCategoryIds = categoryData.map { it.id }.toMutableSet()

                                for (i in data) {
                                    if (!addedCategoryIds.contains(i.id)) {
                                        Log.d("Category Data :-", "${i.id} \t ${i.name}")
                                        categoryData.add(i)
                                        addedCategoryIds.add(i.id) // Mark as added
                                    }
                                }

                                if (categoryData.isNotEmpty()) {
                                    var loadedCount = 1
                                    categoryData.forEach { category ->
                                        loadPerformanceQuailty(id, category.id, loadedCount)
                                        loadedCount++
                                    }
                                } else {
                                    viewAllPerformanceProfileBinding.chartView.visibility =
                                        View.GONE
                                }
                            }
                        } else if (code == 403) {
                            Utils.setUnAuthDialog(this@ViewAllPerformanceProfileActivity)
                        } else {
                            Toast.makeText(
                                this@ViewAllPerformanceProfileActivity,
                                "" + response.message(),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            call.cancel()
                        }
                    }

                    override fun onFailure(call: Call<PerformanceCategory>, t: Throwable) {
                        viewAllPerformanceProfileBinding.ProgressBar.visibility = View.GONE
                        Toast.makeText(
                            this@ViewAllPerformanceProfileActivity,
                            "" + t.message,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                })
        } catch (e: Exception) {
            viewAllPerformanceProfileBinding.ProgressBar.visibility = View.GONE
            Log.d("Exception :- ", "${e.message}")
        }
    }

    private fun loadPerformanceQuailty(
        id: Int,
        performId: Int?,
        count: Int?,
    ) {
        try {
            viewAllPerformanceProfileBinding.ProgressBar.visibility = View.VISIBLE
            apiInterface.GetPerformanceQuality(id = id, performId = performId)
                .enqueue(object : Callback<PerformanceQuality> {
                    override fun onResponse(
                        call: Call<PerformanceQuality>,
                        response: Response<PerformanceQuality>
                    ) {
                        viewAllPerformanceProfileBinding.ProgressBar.visibility = View.GONE
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
                                setChartData()
                            }
                        } else if (code == 403) {
                            Utils.setUnAuthDialog(this@ViewAllPerformanceProfileActivity)
                        } else {
                            Toast.makeText(
                                this@ViewAllPerformanceProfileActivity,
                                "" + response.message(),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            call.cancel()
                        }
                    }

                    override fun onFailure(call: Call<PerformanceQuality>, t: Throwable) {
                        viewAllPerformanceProfileBinding.ProgressBar.visibility = View.GONE
                        Toast.makeText(
                            this@ViewAllPerformanceProfileActivity,
                            "" + t.message,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                })
        } catch (e: Exception) {
            viewAllPerformanceProfileBinding.ProgressBar.visibility = View.GONE
            Log.d("Exception :- ", "${e.message}")
        }
    }


    private fun setChartData() {

        chartBase.clear()
        categoryData.forEach { category ->
            chartBase.add(
                ChartBase(
                    catId = category.id,
                    catName = category.name,
                    catQuality = mutableListOf()
                )
            )
        }

        qualityData.forEach { quality ->
            quality.performance_category_id?.toInt()?.let { categoryId ->
                chartBase.find { it.catId == categoryId }?.catQuality?.add(
                    QualityBase(
                        qualityName = quality.name,
                        athleteScore = quality.athelet_score?.toFloat(),
                        coachScore = quality.coach_score?.toFloat()
                    )
                )
            }
        }

        val athleteData: MutableList<Float> = mutableListOf()
        val coachData: MutableList<Float> = mutableListOf()

        chartBase.forEach { category ->
            val qualities = category.catQuality ?: emptyList()

            qualities.forEach { quality ->
                athleteData.add((quality.athleteScore?.takeIf { !it.isNaN() } ?: 0f))
                coachData.add((quality.coachScore?.takeIf { !it.isNaN() } ?: 0f))
            }

        }

        Log.d("Athlete & Coach Data :-", "${chartBase} \n ${qualityData}")
        Log.d("Athlete & Coach Data :-", "${athleteData} \n ${coachData}")

        viewAllPerformanceProfileBinding.chartView.visibility = View.VISIBLE
        val options = HIOptions()

        val chart = HIChart().apply {
            polar = true
            height = "100%"
        }
        options.chart = chart
        chartView.theme = "dark"

        val pane = HIPane().apply {
            startAngle = 0
            endAngle = 360
            background = arrayListOf(
                HIBackground().apply {
                    backgroundColor = HIColor.initWithRGBA(255, 255, 255, 0.05)
                    innerRadius = "0%"
                    outerRadius = "100%"
                    shape = "circle"
                }
            )
        }
        options.pane = arrayListOf(pane)

        val cat = mutableListOf<String>()

        chartBase.forEach { chartItem ->
            chartItem.catName?.let { cat.add(it) }
        }

        val xAxis = HIXAxis().apply {
            min = 0
            max = categoryData.size
            labels = HILabels().apply {
                style = HICSSObject().apply { color = "#FFFFFF" }
                distance = 2
                rotation = 10
            }
            categories = ArrayList(cat)
        }
        options.xAxis = arrayListOf(xAxis)

        val yAxis = HIYAxis().apply {
            min = 0
            tickPositions = arrayListOf(0, 2.5, 5, 10)
            labels = HILabels().apply {
                enabled = true
                style = HICSSObject().apply {
                    color = "#FFFFFF"
                    fontSize = "12px"
                }
            }
            title = HITitle().apply { text = "" }
        }
        options.yAxis = arrayListOf(yAxis)

        val plotOptions = HIPlotOptions().apply {
            series = HISeries().apply { pointStart = 0 }
            column = HIColumn().apply {
                pointPadding = 0
                groupPadding = 0
            }
        }
        options.plotOptions = plotOptions
        chart.backgroundColor = HIColor.initWithRGB(0, 0, 0)

        val series1 = HIColumn().apply {
            name = "Athlete"
            color = HIColor.initWithRGB(255, 0, 0)
            data = ArrayList(athleteData)
        }

        val series2 = HIColumn().apply {
            name = "Coach"
            color = HIColor.initWithRGB(83, 83, 83)
            data = ArrayList(coachData)
        }

        val hiTitle = HITitle()
        hiTitle.text = ""
        options.title = hiTitle

        val legend = HILegend().apply {
            enabled = true
            itemStyle = HICSSObject().apply {
                fontSize = "14px"
                fontWeight = "regular"
                color = "#FFFFFF"
            }
        }
        options.legend = legend
        options.series = arrayListOf(series1, series2)

        options.exporting = HIExporting().apply { enabled = false }
        options.credits = HICredits().apply { enabled = false }

        chartView.options = options
        chartView.invalidate()
        chartView.requestLayout()

        viewAllPerformanceProfileBinding.ProgressBar.visibility = View.GONE
    }

}