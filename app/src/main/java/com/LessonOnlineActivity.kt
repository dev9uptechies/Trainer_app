package com

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.trainerapp.databinding.ActivityLessonOnlineBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class LessonOnlineActivity : AppCompatActivity() {
    lateinit var lessonOnlineBinding: ActivityLessonOnlineBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lessonOnlineBinding = ActivityLessonOnlineBinding.inflate(layoutInflater)
        setContentView(lessonOnlineBinding.root)
        val total_time = intent.getStringExtra("total_time")
        lessonOnlineBinding.tvTTime.text = "Total Time : " + total_time

        setChartData()

        lessonOnlineBinding.back.setOnClickListener {
            finish()
        }
    }

    private fun setChartData() {
        val entries = listOf(
            Entry(0f, 12f), // Monday
            Entry(1f, 74f), // Tuesday
            Entry(2f, 60f), // Wednesday
            Entry(3f, 112f), // Thursday
            Entry(4f, 12f), // Friday
            Entry(5f, 70f), // Saturday
            Entry(6f, 105f)  // Sunday
        )
        val dataSet = LineDataSet(entries, "Heart Rate")
        dataSet.color = Color.BLACK // Line color
        dataSet.setCircleColor(Color.RED) // Circle color
        dataSet.circleRadius = 5f
        dataSet.setDrawCircleHole(false) // Remove hole inside circles
        dataSet.lineWidth = 2f
        dataSet.setDrawValues(false) // Hide values on data points

        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        dataSet.cubicIntensity = 0.2f

        val lineData = LineData(dataSet)
        lessonOnlineBinding.lineChart.data = lineData

        // X Axis
        val xAxis = lessonOnlineBinding.lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.setLabelCount(7, true) // Show all days
        xAxis.valueFormatter =
            IndexAxisValueFormatter(arrayOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"))
        xAxis.textColor = Color.LTGRAY

        // Y Axis
        val leftAxis = lessonOnlineBinding.lineChart.axisLeft
        leftAxis.axisMinimum = 0f // Minimum value for y axis
        leftAxis.axisMaximum = 125f // Maximum value for y axis
        leftAxis.textColor = Color.LTGRAY

        val rightAxis = lessonOnlineBinding.lineChart.axisRight
        rightAxis.isEnabled = false // Disable right y axis

        // General chart settings
        lessonOnlineBinding.lineChart.description.isEnabled = false // Disable description label
        lessonOnlineBinding.lineChart.setTouchEnabled(false) // Disable touch gestures
        lessonOnlineBinding.lineChart.setPinchZoom(false)
        lessonOnlineBinding.lineChart.setDrawGridBackground(false)
        lessonOnlineBinding.lineChart.legend.isEnabled = false // Hide the legend
        lessonOnlineBinding.lineChart.invalidate() // Refresh the chart
    }
}