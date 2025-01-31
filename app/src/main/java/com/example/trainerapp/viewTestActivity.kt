package com.example.trainerapp

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.Adapter.AthleteResultTestAdapter
import com.example.TestActivity
import com.example.model.newClass.test.ApiResponse
import com.example.model.newClass.test.TestRequest
import com.example.model.newClass.test.TestResultRequest
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
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
import com.prolificinteractive.materialcalendarview.CalendarMode
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Calendar

class viewTestActivity : AppCompatActivity() {

    lateinit var viewTestBinding: ActivityViewTestBinding

    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    lateinit var preferenceManager: PreferencesManager
    lateinit var adapter: AthleteResultTestAdapter
    var athleteIds: List<String>? = null
    var athleteResult: List<String>? = null

    // calender
    @RequiresApi(Build.VERSION_CODES.O)
    private var selectedDate: LocalDate? = null

    @RequiresApi(Build.VERSION_CODES.O)
    private val titleSameYearFormatter = DateTimeFormatter.ofPattern("MMMM")

    @RequiresApi(Build.VERSION_CODES.O)
    private val titleFormatter = DateTimeFormatter.ofPattern("MMM yyyy")

    // get test data
    lateinit var TestList: ArrayList<TestListData.testData>

    //ID Get
    var testId: Int? = null


    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewTestBinding = ActivityViewTestBinding.inflate(layoutInflater)
        setContentView(viewTestBinding.root)

        initView()
        setUpCalendar()
        ButtonCLick()
        loadData()



    }

    private fun initView() {
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        preferenceManager = PreferencesManager(this)
        TestList = ArrayList()


        testId = intent.getIntExtra("TestId", 0)

        Log.d("KSSKKSKSK", "initView: $testId")

    }

    private fun loadData() {
        GetTestList(testId)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun ButtonCLick() {

        viewTestBinding.exSevenCalendar.setOnTouchListener { _, _ -> true } // This disables touch events

        viewTestBinding.back.setOnClickListener { finish() }

        viewTestBinding.nextCard.setOnClickListener {
            submitTestResults()
        }

        viewTestBinding.cbRepeat.setOnClickListener {
            showDateRangePickerDialog { start ->
                viewTestBinding.cbRepeat.isChecked = true
            }
        }

//        viewTestBinding.exSevenCalendar!!.monthScrollListener = {
//            updateMonthYearText()
//        }



        viewTestBinding.right.setOnClickListener {
            val currentDate = viewTestBinding.exSevenCalendar!!.findFirstVisibleDay()?.date ?: LocalDate.now()
            val nextWeekDate = currentDate.plusWeeks(1)
            val formattedMonthYear = DateTimeFormatter.ofPattern("MMMM yyyy").format(currentDate)
            Log.d("CalendarLog", "Current month and year: $formattedMonthYear")
            viewTestBinding.month.text = formattedMonthYear

            viewTestBinding.exSevenCalendar!!.smoothScrollToDate(nextWeekDate)

        }

        viewTestBinding.left.setOnClickListener {
            val currentDate = viewTestBinding.exSevenCalendar!!.findFirstVisibleDay()?.date ?: LocalDate.now()
            val previousWeekDate = currentDate.minusWeeks(1)
            val formattedMonthYear = DateTimeFormatter.ofPattern("MMMM yyyy").format(currentDate)
            Log.d("CalendarLog", "Current month and year: $formattedMonthYear")
            viewTestBinding.month.text = formattedMonthYear

            viewTestBinding.exSevenCalendar!!.smoothScrollToDate(previousWeekDate)
        }

    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateMonthYearText() {
        val firstVisibleMonth = viewTestBinding.exSevenCalendar!!.findFirstVisibleMonth()?.yearMonth ?: YearMonth.now()
        val formattedMonthYear = DateTimeFormatter.ofPattern("MMMM yyyy").format(firstVisibleMonth)
        viewTestBinding.month.text = formattedMonthYear
    }

    fun showDateRangePickerDialog(
        callback: (start: Long) -> Unit
    ) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.date_range_picker_dialog)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#80000000")))
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setGravity(Gravity.CENTER)

        val calendarView = dialog.findViewById<MaterialCalendarView>(R.id.calendarView)
        val textView = dialog.findViewById<TextView>(R.id.textView)
        val confirmButton = dialog.findViewById<Button>(R.id.confirmButton)
        val cancelButton = dialog.findViewById<Button>(R.id.cancelButton)

        calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE)

        cancelButton.setOnClickListener { dialog.dismiss() }

        calendarView.state().edit()
            .setCalendarDisplayMode(CalendarMode.MONTHS)
            .commit()

        val today = com.prolificinteractive.materialcalendarview.CalendarDay.today()

        // Decorator to disable past dates
        calendarView.addDecorator(object : DayViewDecorator {
            override fun shouldDecorate(day: com.prolificinteractive.materialcalendarview.CalendarDay?): Boolean {
                return day != null && day.isBefore(today)
            }

            override fun decorate(view: DayViewFacade?) {
                view?.setDaysDisabled(true) // Disable past dates
            }
        })

        // Decorator to highlight today's date
        calendarView.addDecorator(object : DayViewDecorator {
            override fun shouldDecorate(day: com.prolificinteractive.materialcalendarview.CalendarDay?): Boolean {
                return day == today
            }

            override fun decorate(view: DayViewFacade?) {
                view?.addSpan(ForegroundColorSpan(Color.WHITE)) // Text color for today
                ContextCompat.getDrawable(this@viewTestActivity, R.drawable.todays_date_selecte)
                    ?.let {
                        view?.setBackgroundDrawable(it)
                    }
            }
        })

        confirmButton.setOnClickListener {
            val selectedDates = calendarView.selectedDates

            if (selectedDates.isNotEmpty()) {
                val selectedDate = selectedDates.first().calendar

                selectedDate.set(Calendar.HOUR_OF_DAY, 0)
                selectedDate.set(Calendar.MINUTE, 0)
                selectedDate.set(Calendar.SECOND, 0)
                selectedDate.set(Calendar.MILLISECOND, 0)

                val selectedDates = selectedDates.first()
                val formattedDate = String.format(
                    "%04d-%02d-%02d",
                    selectedDates.year,
                    selectedDates.month + 1,
                    selectedDates.day
                )

                sendTestRequest(formattedDate)  // Call API with formatted date

                callback(selectedDate.timeInMillis)

                dialog.dismiss()
            } else {
                textView.text = "Please select a date"
                textView.setTextColor(Color.RED)
            }
        }

        dialog.show()
    }

    private fun submitTestResults() {
        // Collect the results from the adapter
        val results = adapter.getTestResults()


        Log.d("KDKDKKD", "submitTestResults: $results")

        val AthleteResult = if (results.isNullOrEmpty()) athleteResult else results


        Log.d("SLSSLSLLSLSL", "submitTestResults: $AthleteResult")

        val request = TestResultRequest(
            testId = testId!!.toInt(),
            athleteIds = athleteIds!!,
            results = AthleteResult!!
        )

        apiInterface.TestResults(request).enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                if (response.isSuccessful) {
                    response.body()?.let { data ->
                        Log.d("API_SUCCESS", "Response: $data")
                        finish()
                    }
                } else {
                    Log.e("API_ERROR", "Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                // Handle failure
                Log.e("API_FAILURE", "Error: ${t.message}")
            }
        })
    }

    fun sendTestRequest(date: String) {
        val requestBody = TestRequest(test_id = testId.toString(), date = date)

        Log.d("T%%RR%R%R%R", "sendTestRequest: $requestBody")

        apiInterface.sendTestData(requestBody).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    Log.d("API_SUCCESS", "Response: ${response.body()?.message}")

                    val data = response.body()?.data
                    val athleteIDList =
                        data?.testAthletes?.mapNotNull { it.athleteId?.toIntOrNull() }
                            ?: emptyList()

                    if (athleteIDList.isNotEmpty()) {
                        Log.d("DEBUGDDDD", "Converted Athlete ID List: $athleteIDList")
                    } else {
                    }


                    val intent = Intent(this@viewTestActivity, TestActivity::class.java)
                    intent.putExtra("RepeatTestId", testId.toString())
                    intent.putExtra("RepeatTitle", data?.title)
                    intent.putExtra("RepeatGoal", data?.goal)
                    intent.putExtra("RepeatUnit", data?.unit)
                    intent.putExtra("RepeatAthleteId", athleteIDList.toString())
                    intent.putExtra("RepeatDate", date)
                    intent.putExtra("RepeatTrue", true)
                    startActivity(intent)
                } else {
                    Log.e("API_ERROR", "Response error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Log.e("API_FAILURE", "Request failed: ${t.message}")
            }
        })
    }

    private fun GetTestList(filterId: Int?) {
        try {
            TestList.clear()
            viewTestBinding.progressbar.visibility = View.VISIBLE
            apiInterface.GetTest()?.enqueue(object : Callback<TestListData?> {
                override fun onResponse(
                    call: Call<TestListData?>,
                    response: Response<TestListData?>
                ) {
                    viewTestBinding.progressbar.visibility = View.GONE
                    if (response.isSuccessful) {
                        response.body()?.let { resource ->
                            if (resource.status == true) {
                                resource.data?.let { data ->
                                    // Filter data based on the provided filterId
                                    val filteredData = if (filterId != null) {
                                        data.filter { it.id == filterId }
                                    } else {
                                        data
                                    }

                                    if (filteredData.isNotEmpty()) {
                                        TestList = ArrayList(filteredData) // Convert to ArrayList
                                        initrecycler(TestList)
                                        setData(TestList)

                                        athleteIds = TestList.flatMap {
                                            it.data?.mapNotNull { dataItem -> dataItem.athlete_id }
                                                ?: emptyList()
                                        }
                                        athleteResult = TestList.flatMap {
                                            it.data?.mapNotNull { dataItem -> dataItem.result }
                                                ?: emptyList()
                                        }

                                        Log.d("DDLDLDLD", "All Athlete IDs: $athleteIds")
                                        Log.d("DDLDLDLD", "All Athlete IDs: $athleteResult")

                                    } else {
                                        showToast("No matching data available")
                                    }
                                } ?: showToast("No data available")
                            } else {
                                showToast(resource.message)
                            }
                        }
                    } else {
                        showToast(response.message())
                    }
                }

                override fun onFailure(call: Call<TestListData?>, t: Throwable) {
                    viewTestBinding.progressbar.visibility = View.GONE
                    showToast(t.message)
                }
            })
        } catch (e: Exception) {
            Log.d("DODDDK", "GetTestList: ${e.message.toString()}")
        }
    }

    private fun setData(testList: ArrayList<TestListData.testData>) {
        viewTestBinding.goalTxt.text = "Goal: " + testList.getOrNull(0)?.goal ?: ""
        viewTestBinding.unitTxt.text = "Unit: " + testList.getOrNull(0)?.unit ?: ""
    }

    private fun showToast(message: String?) {
        Toast.makeText(this, message ?: "An error occurred", Toast.LENGTH_SHORT).show()
    }

    private fun initrecycler(testdatalist: ArrayList<TestListData.testData>?) {
        viewTestBinding.progressbar.visibility = View.GONE
        viewTestBinding.resultRly.layoutManager = LinearLayoutManager(this)
        adapter = AthleteResultTestAdapter(testdatalist, this)
        viewTestBinding.resultRly.adapter = adapter
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

//                            day.date == selectedDate -> {
//                                with(textView) {
//                                    setTextColor(resources.getColor(R.color.splash_text_color)) // Custom style for selected date
//                                }
//                            }

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

//            val formattedMonthYear = DateTimeFormatter.ofPattern("MMMM yyyy").format(month.yearMonth)
//            viewTestBinding.month.text = formattedMonthYear
            val currentDate = viewTestBinding.exSevenCalendar!!.findFirstVisibleDay()?.date ?: LocalDate.now()
            val formattedMonthYear = DateTimeFormatter.ofPattern("MMMM yyyy").format(currentDate)
            Log.d("CalendarLog", "Current month and year: $formattedMonthYear")
            viewTestBinding.month.text = formattedMonthYear

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