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
import com.highsoft.highcharts.common.hichartsclasses.HITooltip
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
    var qualityBase: MutableList<QualityBase> = mutableListOf()
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
        ButtonClick()

        if (athleteId.toString() != "null" || athleteId.toString() != "") {
            loadPerformance(athleteId.toInt())
        }



    }

    private fun ButtonClick() {
        viewAllPerformanceProfileBinding.back.setOnClickListener {
            finish()
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
            qualityData.clear()
            viewAllPerformanceProfileBinding.ProgressBar.visibility = View.VISIBLE
            apiInterface.GetPerformanceCategory(id = id)
                .enqueue(object : Callback<PerformanceCategory> {
                    override fun onResponse(
                        call: Call<PerformanceCategory>,
                        response: Response<PerformanceCategory>
                    ) {
                        viewAllPerformanceProfileBinding.ProgressBar.visibility = View.GONE
                        Log.d("TAG", response.code().toString())
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
                                    // Load performance qualities for all categories
                                    loadAllPerformanceQualities(id)
                                } else {
                                    viewAllPerformanceProfileBinding.chartView.visibility = View.GONE
                                }
                            }
                        } else if (code == 403) {
                            Utils.setUnAuthDialog(this@ViewAllPerformanceProfileActivity)
                        } else {
                            Toast.makeText(
                                this@ViewAllPerformanceProfileActivity,
                                "" + response.message(),
                                Toast.LENGTH_SHORT
                            ).show()
                            call.cancel()
                        }
                    }

                    override fun onFailure(call: Call<PerformanceCategory>, t: Throwable) {
                        viewAllPerformanceProfileBinding.ProgressBar.visibility = View.GONE
                        Toast.makeText(
                            this@ViewAllPerformanceProfileActivity,
                            "" + t.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        call.cancel()
                    }
                })
        } catch (e: Exception) {
            viewAllPerformanceProfileBinding.ProgressBar.visibility = View.GONE
            Log.d("Exception :- ", "${e.message}")
        }
    }

    // Load all performance qualities for all categories
    private fun loadAllPerformanceQualities(athleteId: Int) {
        var loadedCount = 0
        val totalCategories = categoryData.size

        categoryData.forEach { category ->
            loadPerformanceQuality(athleteId, category.id) {
                loadedCount++
                // Once all performance qualities are loaded, set the chart data
                if (loadedCount == totalCategories) {
                    setChartData() // Call setChartData only after all categories are loaded
                }
            }
        }
    }

    // Modified loadPerformanceQuality to accept a callback
    private fun loadPerformanceQuality(athleteId: Int, categoryId: Int?, onComplete: () -> Unit) {
        try {
            apiInterface.GetPerformanceQuality(id = athleteId, performId = categoryId)
                .enqueue(object : Callback<PerformanceQuality> {
                    override fun onResponse(
                        call: Call<PerformanceQuality>,
                        response: Response<PerformanceQuality>
                    ) {
                        if (response.isSuccessful) {
                            val data = response.body()!!.data ?: mutableListOf()

                            val addedQualityIds = qualityData.map { it.id }.toMutableSet()

                            for (quality in data) {
                                if (!addedQualityIds.contains(quality.id)) {
                                    qualityData.add(quality)
                                    addedQualityIds.add(quality.id) // Mark as added
                                }
                            }
                        }
                        onComplete() // Notify that this category's data is loaded
                    }

                    override fun onFailure(call: Call<PerformanceQuality>, t: Throwable) {
                        Toast.makeText(
                            this@ViewAllPerformanceProfileActivity,
                            "" + t.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        call.cancel()
                        onComplete() // Notify to proceed even on failure
                    }
                })
        } catch (e: Exception) {
            Log.d("Exception :- ", "${e.message}")
            onComplete() // Notify to proceed even in case of an exception
        }
    }

    // Use setChartData() as provided earlier
    private fun setChartData() {
        chartBase.clear()

        // Create a ChartBase entry for each category
        categoryData.forEach { category ->
            chartBase.add(
                ChartBase(
                    catId = category.id,
                    catName = category.name,
                    catQuality = mutableListOf()
                )
            )
        }

        // Assign qualities to their respective categories
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

        // List to store athlete and coach series
        val athleteSeriesList: MutableList<Float> = mutableListOf()
        val coachSeriesList: MutableList<Float> = mutableListOf()
        val categoriesList: MutableList<String> = mutableListOf()

        // For each category, create athlete and coach data for each quality
        chartBase.forEach { category ->
            category.catQuality?.forEach { quality ->
                // Add quality name to x-axis categories
                quality.qualityName?.let { categoriesList.add(it) }

                // Add scores to respective lists
                athleteSeriesList.add(quality.athleteScore?.takeIf { !it.isNaN() } ?: 0f)
                coachSeriesList.add(quality.coachScore?.takeIf { !it.isNaN() } ?: 0f)
            }
        }

        // Set up the chart options
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

        // Set quality names as xAxis categories
        val xAxis = HIXAxis().apply {
            categories = ArrayList(categoriesList) // Quality names
            labels = HILabels().apply {
                style = HICSSObject().apply { color = "#FFFFFF" }
                distance = 2
                rotation = 10
            }
            title = HITitle().apply { text = "" }
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

        // Create series for athlete and coach data
        val athleteSeries = HIColumn().apply {
            name = "Athlete"
            color = HIColor.initWithRGB(255, 0, 0) // Red for athlete
            data = ArrayList(athleteSeriesList)
        }

        val coachSeries = HIColumn().apply {
            name = "Coach"
            color = HIColor.initWithRGB(83, 83, 83) // Grey for coach
            data = ArrayList(coachSeriesList)
        }

        // Set series in chart options
        options.series = arrayListOf(athleteSeries, coachSeries)


        // Other chart options
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
        options.exporting = HIExporting().apply { enabled = false }
        options.credits = HICredits().apply { enabled = false }

        // Set the chart options to the view
        chartView.options = options
        chartView.invalidate()
        chartView.requestLayout()

        viewAllPerformanceProfileBinding.ProgressBar.visibility = View.GONE
    }

}