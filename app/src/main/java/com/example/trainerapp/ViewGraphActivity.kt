package com.example.trainerapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.trainerapp.databinding.ActivityViewGraphBinding
import com.highsoft.highcharts.common.HIColor
import com.highsoft.highcharts.common.hichartsclasses.HIArea
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
import java.util.Arrays

class ViewGraphActivity : AppCompatActivity() {
    lateinit var viewGraphBinding: ActivityViewGraphBinding
    lateinit var chartView: HIChartView
    lateinit var categories: ArrayList<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewGraphBinding = ActivityViewGraphBinding.inflate(layoutInflater)
        setContentView(viewGraphBinding.root)
        chartView = findViewById(R.id.chartView)
        categories = ArrayList<String>()
        viewGraphBinding.back.setOnClickListener {
            finish()
        }
        categories.add("Strength")
        categories.add("Endurance")
        categories.add("Boxing")
        categories.add("Kick Boxing")
        categories.add("Running")
        categories.add("Swimming")

        val options = HIOptions()

        val chart = HIChart()
        chart.polar = true
        chart.height = "100%"
        options.chart = chart
        chartView.theme = "dark"

        val title = HITitle()
        title.text = ""
        options.title = title

        val pane = HIPane()
        pane.startAngle = 0
        pane.endAngle = 360
//        options.pane = pane

        val xAxis = HIXAxis()
//        xAxis.tickInterval = 45
        xAxis.min = 0
        xAxis.max = 6
        xAxis.labels = HILabels()
        xAxis.categories = categories
        xAxis.labels.distance = 10
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

        val yAxis = HIYAxis()
        yAxis.min = 0
        options.yAxis = object : ArrayList<HIYAxis?>() {
            init {
                add(yAxis)
            }
        }

        val plotOptions = HIPlotOptions()
        plotOptions.series = HISeries()
        plotOptions.series.pointStart = 0
        plotOptions.column = HIColumn()
        plotOptions.column.pointPadding = 0
        plotOptions.column.groupPadding = 0
        options.plotOptions = plotOptions
        chart.backgroundColor = HIColor.initWithRGB(0, 0, 0)

        val series1 = HIColumn()
        series1.name = "Athlete"
        series1.color = HIColor.initWithRGB(255, 0, 0)
        val series1_data = arrayOf<Number>(2, 4, 6, 8, 10, 12)
        series1.data = ArrayList(Arrays.asList(series1_data))
        series1.pointPlacement = "between"

        val series3 = HIArea()
        series3.name = "Coach"
        series1.color = HIColor.initWithRGB(3, 169, 244)
        val series3_data = arrayOf<Number>(6, 4, 3, 10, 13, 5)
        series3.data = ArrayList(Arrays.asList(series3_data))

        val legend = HILegend()
        legend.enabled = true
        val itemstyle = HICSSObject()
        itemstyle.fontSize = "14px"
        itemstyle.fontWeight = "regular"
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
}