    package com.example.trainerapp.performance_profile.view_all_graph
    
    import android.os.Bundle
    import android.util.Log
    import android.view.View
    import android.widget.Toast
    import androidx.appcompat.app.AppCompatActivity
    import androidx.core.content.res.ResourcesCompat
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
    import com.example.trainerapp.R
    import com.example.trainerapp.Utils
    import com.example.trainerapp.databinding.ActivityViewAllPerformanceProfileBinding
    import com.highsoft.highcharts.common.HIColor
    import com.highsoft.highcharts.common.hichartsclasses.HIBackground
    import com.highsoft.highcharts.common.hichartsclasses.HICSSObject
    import com.highsoft.highcharts.common.hichartsclasses.HIChart
    import com.highsoft.highcharts.common.hichartsclasses.HIColumn
    import com.highsoft.highcharts.common.hichartsclasses.HICredits
    import com.highsoft.highcharts.common.hichartsclasses.HIExporting
    import com.highsoft.highcharts.common.hichartsclasses.HIGlobal
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
    import kotlinx.coroutines.CoroutineScope
    import kotlinx.coroutines.Dispatchers
    import kotlinx.coroutines.delay
    import kotlinx.coroutines.launch
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
    
                            call.cancel()
                        }
                    }
    
                    override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
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
    
            val userType = preferenceManager.GetFlage()
    
            if (userType == "Athlete") {
                loadPerformanceAthlete()
            } else {
                if (athleteId != null && athleteId.toString()
                        .isNotEmpty() && athleteId.toString() != "null"
                ) {
                    loadPerformance(athleteId.toInt())
                }
    
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
                                        viewAllPerformanceProfileBinding.chartView.visibility =
                                            View.GONE
                                    }
                                }
                            } else if (code == 403) {
                                Utils.setUnAuthDialog(this@ViewAllPerformanceProfileActivity)
                            } else {
    
                                call.cancel()
                            }
                        }
    
                        override fun onFailure(call: Call<PerformanceCategory>, t: Throwable) {
                            viewAllPerformanceProfileBinding.ProgressBar.visibility = View.GONE
    
                            call.cancel()
                        }
                    })
            } catch (e: Exception) {
                viewAllPerformanceProfileBinding.ProgressBar.visibility = View.GONE
                Log.d("Exception :- ", "${e.message}")
            }
        }
    
        private fun loadAllPerformanceQualities(athleteId: Int) {
            var loadedCount = 0
            val totalCategories = categoryData.size
    
            categoryData.forEach { category ->
                loadPerformanceQuality(athleteId, category.id) {
                    loadedCount++
                    if (loadedCount == totalCategories) {
                        setChartData() // Call setChartData only after all categories are loaded
                    }
                }
            }
        }
    
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
                            call.cancel()
                            onComplete() // Notify to proceed even on failure
                        }
                    })
            } catch (e: Exception) {
                Log.d("Exception :- ", "${e.message}")
                onComplete() // Notify to proceed even in case of an exception
            }
        }
    
    
        private fun loadPerformanceAthlete() {
            viewAllPerformanceProfileBinding.ProgressBar.visibility = View.VISIBLE
            apiInterface.GetPerformanceCategoryAthlete()
                .enqueue(object : Callback<PerformanceCategory> {
                    override fun onResponse(
                        call: Call<PerformanceCategory>,
                        response: Response<PerformanceCategory>
                    ) {
                        viewAllPerformanceProfileBinding.ProgressBar.visibility = View.GONE
                        if (response.isSuccessful) {
                            val data = response.body()?.data ?: mutableListOf()
                            val addedCategoryIds = categoryData.map { it.id }.toMutableSet()
    
                            for (category in data) {
                                if (!addedCategoryIds.contains(category.id)) {
                                    categoryData.add(category)
                                    addedCategoryIds.add(category.id)
                                }
                            }
    
                            if (categoryData.isNotEmpty()) {
                                loadPerformanceQualityAthleteThrottled(categoryData.map { it.id!!.toInt() })
                                loadAllPerformanceQualitiesAthlete()
                            } else {
                                Log.d("Performance", "No categories found")
                            }
                        } else if (response.code() == 429) {
                            Toast.makeText(
                                this@ViewAllPerformanceProfileActivity,
                                "Rate limit exceeded. Please try again later.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else if (response.code() == 403) {
                            Utils.setUnAuthDialog(this@ViewAllPerformanceProfileActivity)
                        } else {
                            Toast.makeText(
                                this@ViewAllPerformanceProfileActivity,
                                response.message(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
    
                    override fun onFailure(call: Call<PerformanceCategory>, t: Throwable) {
                        viewAllPerformanceProfileBinding.ProgressBar.visibility = View.GONE
                        Toast.makeText(
                            this@ViewAllPerformanceProfileActivity,
                            t.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
    
        private fun loadPerformanceQualityAthleteThrottled(ids: List<Int>) {
            CoroutineScope(Dispatchers.IO).launch {
                ids.forEach { id ->
                    delay(1000) // Adjust delay based on API rate limits
    //                loadPerformanceQualityAthlete(id)
                }
            }
        }
    
        private fun loadAllPerformanceQualitiesAthlete() {
            var loadedCount = 0
            val totalCategories = categoryData.size
    
            if (totalCategories == 0) {
                Log.d("PerformanceQuality", "No categories found to load qualities.")
                return
            }
    
            Log.d("PerformanceQuality", "Total categories to load: $totalCategories")
    
            categoryData.forEach { category ->
                val categoryId = category.id
                if (categoryId != null) {
                    loadPerformanceQualityAthlete(categoryId.toInt()) {
                        synchronized(this) { // Synchronize access to shared resources
                            loadedCount++
                        }
    
                        Log.d("PerformanceQuality", "Loaded category ID: $categoryId")
                        Log.d("PerformanceQuality", "Loaded count: $loadedCount / $totalCategories")
    
                        if (loadedCount == totalCategories) {
                            Log.d("PerformanceQuality", "All categories loaded. Updating chart data.")
                            setChartData()
                        }
                    }
                } else {
                    Log.e("PerformanceQuality", "Category ID is null. Skipping.")
                }
            }
        }
    
        private fun loadPerformanceQualityAthlete(id: Int, onComplete: () -> Unit) {
            apiInterface.GetPerformanceQualityAthlete(performId = id)
                .enqueue(object : Callback<PerformanceQuality> {
                    override fun onResponse(call: Call<PerformanceQuality>, response: Response<PerformanceQuality>) {
                        CoroutineScope(Dispatchers.Main).launch {
                            if (response.isSuccessful) {
                                val data = response.body()?.data ?: mutableListOf()
                                Log.d("PerformanceQuality", "Loaded qualities for ID $id: ${data.size} items")
    
                                synchronized(this@ViewAllPerformanceProfileActivity) { // Prevent concurrency issues
                                    qualityData.addAll(data.distinctBy { it.id })
                                }
    
                            } else {
                                Log.e("PerformanceQuality", "Failed to load qualities for ID $id: ${response.message()}")
                            }
                            onComplete()
                        }
                    }
    
                    override fun onFailure(call: Call<PerformanceQuality>, t: Throwable) {
                        CoroutineScope(Dispatchers.Main).launch {
                            Log.e("PerformanceQuality", "Error loading qualities for ID $id: ${t.message}")
                            onComplete() // Notify completion even on failure
                        }
                    }
                })
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

            viewAllPerformanceProfileBinding.chartView.addFont(R.font.poppins_medium)
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

            val athleteSeriesList: MutableList<Float> = mutableListOf()
            val coachSeriesList: MutableList<Float> = mutableListOf()
            val categoriesList: MutableList<String> = mutableListOf()

            chartBase.forEach { category ->
                category.catQuality?.forEach { quality ->
                    quality.qualityName?.let { categoriesList.add(it) }
                    athleteSeriesList.add(quality.athleteScore?.takeIf { !it.isNaN() } ?: 0f)
                    coachSeriesList.add(quality.coachScore?.takeIf { !it.isNaN() } ?: 0f)
                }
            }

            viewAllPerformanceProfileBinding.chartView.visibility = View.VISIBLE
            val options = HIOptions()

            // Set chart configuration
            options.chart = HIChart().apply {
                polar = true
                height = "120%"
                style = HICSSObject().apply {
                    fontFamily = "poppins_medium"
                    fontSize = "12px"
                    color = "#FFFFFF"
                }
            }

            options.chart?.backgroundColor = HIColor.initWithRGB(0, 0, 0)

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

            val xAxis = HIXAxis().apply {
                categories = ArrayList(categoriesList)
                labels = HILabels().apply {
                    style = HICSSObject().apply {
                        fontFamily = "poppins_medium"
                        fontSize = "12px"
                        fontWeight = "bold"
                        color = "#FFFFFF"
                    }
                    distance = 2
                    rotation = 0
                }
                title = HITitle().apply {
                    text = ""
                    style = HICSSObject().apply {
                        fontFamily = "poppins_medium"
                        fontSize = "16px"
                        color = "#333333"
                    }
                }
            }
            options.xAxis = arrayListOf(xAxis)

            val yAxis = HIYAxis().apply {
                min = 0
                tickPositions = arrayListOf(0, 2.5, 5, 10)
                labels = HILabels().apply {
                    enabled = true
                    style = HICSSObject().apply {
                        fontFamily = "poppins_medium"
                        fontSize = "12px"
                        color = "#FFFFFF"
                    }
                }
                title = HITitle().apply {
                    text = ""
                    style = HICSSObject().apply {
                        fontFamily = "poppins_medium"
                        fontSize = "16px"
                        color = "#333333"
                    }
                }
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

            options.series = arrayListOf(athleteSeries, coachSeries)

            val hiTitle = HITitle()
            hiTitle.text = ""
            options.title = hiTitle


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

            options.exporting = HIExporting().apply { enabled = false }
            options.credits = HICredits().apply { enabled = false }

            chartView.options = options
            chartView.invalidate()
            chartView.requestLayout()

            viewAllPerformanceProfileBinding.ProgressBar.visibility = View.GONE
        }


    }