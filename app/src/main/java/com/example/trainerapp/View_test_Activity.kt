package com.example.trainerapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children
import com.SelectDayActivity
import com.example.trainerapp.databinding.ActivityViewExerciseBinding
import com.example.trainerapp.databinding.ActivityViewTestBinding
import com.example.trainerapp.databinding.Example3CalendarDayBinding
import com.example.trainerapp.databinding.Example3CalendarHeaderBinding
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import com.kizitonwose.calendarview.utils.yearMonth
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class View_test_Activity : AppCompatActivity() {

    lateinit var viewTestBinding: ActivityViewTestBinding


    // calender
    @RequiresApi(Build.VERSION_CODES.O)
    private var selectedDate: LocalDate? = null



    @RequiresApi(Build.VERSION_CODES.O)
    private val titleSameYearFormatter = DateTimeFormatter.ofPattern("MMMM")

    @RequiresApi(Build.VERSION_CODES.O)
    private val titleFormatter = DateTimeFormatter.ofPattern("MMM yyyy")


    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewTestBinding = ActivityViewTestBinding.inflate(layoutInflater)
        setContentView(viewTestBinding.root)

        setUpCalendar()
        ButtonCLick()
    }

    private fun ButtonCLick() {
        viewTestBinding.back.setOnClickListener { finish() }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpCalendar() {
        val daysOfWeek = daysOfWeekFromLocale()
        val currentMonth = YearMonth.now()
        val today = LocalDate.now()

        viewTestBinding.exSevenCalendar!!.apply {
            setup(currentMonth.minusMonths(100), currentMonth.plusMonths(100), daysOfWeek.first())
            scrollToMonth(currentMonth)
        }

        viewTestBinding.exSevenCalendar!!.post {
            viewTestBinding.exSevenCalendar!!.scrollToDate(today)
        }

        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay
            lateinit var month: YearMonth
            val binding = Example3CalendarDayBinding.bind(view)

            init {

                val formattedMonth = DateTimeFormatter.ofPattern("MMM,yyyy").format(currentMonth)
//                viewTestBinding.tvDate.text = formattedMonth

                view.setOnClickListener {

                    Log.d("HFGFGFFGGFFG", "setUpCalendar: $month")

                    if (day.owner == DayOwner.THIS_MONTH) {
                        selectDate(day.date)
//                        formattedDate = day.date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

                    }
                }
            }
        }

        class MonthViewContainer(view: View) : ViewContainer(view) {
            val legendLayout = Example3CalendarHeaderBinding.bind(view).legendLayout.root
        }

        viewTestBinding.exSevenCalendar!!.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)

            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day
                val textView = container.binding.exThreeDayText
                textView.text = day.date.dayOfMonth.toString()

                with(textView) {
                    setTextColor(resources.getColor(R.color.white))
                    setBackgroundColor(0x1A000000)
                }

                // Apply custom styling based on date comparison
                when {
                    day.owner == DayOwner.THIS_MONTH -> {
                        when {
                            day.date == today -> {
                                with(textView) {
                                    setTextColor(resources.getColor(R.color.white))
                                    setBackgroundResource(R.drawable.bg_red_round_corner_10)
                                }
                            }

                            day.date == selectedDate -> {
                                with(textView) {
                                    setTextColor(resources.getColor(R.color.splash_text_color)) // Custom style for selected date
                                }
                            }

                            else -> {
                                with(textView) {
                                    setTextColor(resources.getColor(R.color.white))
                                    setBackgroundColor(0x1A000000)
                                }
                            }
                        }
                    }

                    else -> {
                        textView.setTextColor(resources.getColor(R.color.grey)) // For days outside this month
                    }
                }

                container.view.setOnClickListener {
                    if (day.owner == DayOwner.THIS_MONTH) {
                        selectDate(day.date)

//                        formattedDate = day.date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

//                        val receivedIdInt = receivedIds.toIntOrNull()

//                        Log.e("DJDJDJDJDJJDJDJDJ", "bind: $receivedIdInt")

//                        val userType = preferenceManager.GetFlage()


                    }
                }
            }
        }

        viewTestBinding.exSevenCalendar!!.monthScrollListener = { month ->
            // Format the month as "Month Year" (e.g., "January 2025")
//                val formattedMonth = DateTimeFormatter.ofPattern("MMMM yyyy").format(month.yearMonth)
//
//                // Set the TextView with the formatted month and year
//                viewTestBinding.tvDate.text = formattedMonth

            val formattedMonth = DateTimeFormatter.ofPattern("MMM yyyy").format(today.yearMonth)
//            viewTestBinding.tvDate.text = formattedMonth

            // Optionally, apply custom formatting logic
            if (month.year == today.year) {
                titleSameYearFormatter.format(month.yearMonth)
            } else {
                titleFormatter.format(month.yearMonth)
            }

            // Optionally, you can select the first day of the current month if needed
            selectDate(month.yearMonth.atDay(1))
        }


        // Update the month header to show abbreviated day names (Mon, Tue, Wed)
        viewTestBinding.exSevenCalendar.monthHeaderBinder =
            object : MonthHeaderFooterBinder<MonthViewContainer> {
                override fun create(view: View) = MonthViewContainer(view)

                @SuppressLint("ResourceAsColor")
                override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                    if (container.legendLayout.tag == null) {
                        container.legendLayout.tag = month.yearMonth

                        val formattedMonth =
                            DateTimeFormatter.ofPattern("MMM,yyyy").format(today.yearMonth)
//                        viewTestBinding.tvDate.text = formattedMonth
                        // Get short day names like "Mon", "Tue", "Wed"
                        val dayNames =
                            daysOfWeek.map { it.name.take(3) }  // Get first 3 letters of each day name
                        container.legendLayout.children.map { it as TextView }
                            .forEachIndexed { index, tv ->
                                tv.text = dayNames[index]
                            }
                    }
                }
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun selectDate(date: LocalDate) {
        if (selectedDate != date) {
            val oldDate = selectedDate
            selectedDate = date

            Log.d(
                "Select Date month:-",
                "select date :-$selectedDate \n date :- $date \n old date :-$oldDate"
            )

            oldDate?.let { viewTestBinding.exSevenCalendar.notifyDateChanged(it) }
            viewTestBinding.exSevenCalendar.notifyDateChanged(date)
//                viewTestBinding.exSevenCalendar.scrollToMonth(YearMonth.from(date))
//                viewTestBinding.exSevenCalendar.smoothScrollToDate(date)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun daysOfWeekFromLocale(): List<DayOfWeek> {
        val firstDayOfWeek = DayOfWeek.MONDAY
        val daysOfWeek = DayOfWeek.values()
        return daysOfWeek.slice(firstDayOfWeek.ordinal..daysOfWeek.lastIndex) +
                daysOfWeek.slice(0 until firstDayOfWeek.ordinal)
    }

}