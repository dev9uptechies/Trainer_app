package com.example

import android.annotation.SuppressLint
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.children
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.FavoriteActivity
import com.example.Adapter.SelectedEvent.SelectedEventEventAdapter
import com.example.Adapter.SelectedEvent.SelectedEventLessonAdapter
import com.example.Adapter.SelectedEvent.SelectedEventTestAdapter
import com.example.model.SelectedDaysModel
import com.example.model.SelectedEventModel
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.LibraryActivity
import com.example.trainerapp.MainActivity
import com.example.trainerapp.PerformanceProfileActivity
import com.example.trainerapp.PreferencesManager
import com.example.trainerapp.R
import com.example.trainerapp.RemindMe.ViewRemindMeActivity
import com.example.trainerapp.SettingActivity
import com.example.trainerapp.Utils
import com.example.trainerapp.competition.CompetitionActivity
import com.example.trainerapp.databinding.Example3CalendarDayBinding
import com.example.trainerapp.databinding.Example3CalendarHeaderBinding
import com.example.trainerapp.databinding.FragmentCalenderBinding
import com.example.trainerapp.notification.NotificationActivity
import com.example.trainerapp.personal_diary.ViewPersonalDiaryActivity
import com.example.trainerapp.privacy_policy.PrivacyPolicyActivity
import com.example.trainerapp.view_analysis.ViewAnalysisActivity
import com.google.android.material.navigation.NavigationView
import com.kizitonwose.calendarview.CalendarView
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.DayOfWeek
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale
import kotlinx.coroutines.*
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeParseException
import kotlin.math.log

class CalenderFragment : Fragment(), OnItemClickListener.OnItemClickCallback,
    NavigationView.OnNavigationItemSelectedListener {
    lateinit var preferenceManager: PreferencesManager
    lateinit var apiInterface: APIInterface
    private var isScrollAllowed = true // Flag to control scrolling

    lateinit var apiClient: APIClient
    private var receivedId: String = ""
    private var receivedGropu_Id: String = ""
    lateinit var lessonadapter: SelectedEventLessonAdapter
    lateinit var eventadapter: SelectedEventEventAdapter
    lateinit var testadapter: SelectedEventTestAdapter
    var date: String = ""
    var actionBarDrawerToggle: ActionBarDrawerToggle? = null

    var userType: String? = null
    var calendarView: CalendarView? = null
    private var selectedDate: LocalDate? = null

    @RequiresApi(Build.VERSION_CODES.O)
    private val today = LocalDate.now()

    @RequiresApi(Build.VERSION_CODES.O)
    private val titleSameYearFormatter = DateTimeFormatter.ofPattern("MMMM")

    @RequiresApi(Build.VERSION_CODES.O)
    private val titleFormatter = DateTimeFormatter.ofPattern("MMM yyyy")

    @RequiresApi(Build.VERSION_CODES.O)
    private val selectionFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    private lateinit var calenderBinding: FragmentCalenderBinding

    private val sharedPreferences by lazy {
        requireContext().getSharedPreferences("RemindMePrefs", MODE_PRIVATE)
    }

    private val datesWithDataTest = mutableSetOf<LocalDate>()
    private val datesWithDataLesson = mutableSetOf<LocalDate>()
    private val datesWithDataEvent = mutableSetOf<LocalDate>()

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
                        loadData()
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(requireContext())
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Toast.makeText(
                        requireContext(),
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

    @SuppressLint("NewApi")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        calenderBinding = FragmentCalenderBinding.inflate(layoutInflater)
        calendarView = calenderBinding.frgCalendarView

        initViews()
        setUpCalendar()
        setDrawerToggle()
        ButtonClick()
//        loadData()


        return calenderBinding.root
    }

    private fun ButtonClick() {
        calenderBinding.sidemenu.setOnClickListener {
            calenderBinding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    private fun setDrawerToggle() {
        actionBarDrawerToggle = ActionBarDrawerToggle(
            requireActivity(),
            calenderBinding.drawerLayout,
            R.string.nav_open,
            R.string.nav_close
        )

        calenderBinding.drawerLayout.addDrawerListener(actionBarDrawerToggle!!)
        actionBarDrawerToggle?.syncState()
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        calenderBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        calenderBinding.navigationView.setNavigationItemSelectedListener(this)
    }

    @SuppressLint("NewApi")
    private fun loadData() {
        selectDate(LocalDate.now())

        val userType = preferenceManager.GetFlage()

        if (userType == "Athlete") {
            fetchDayDataAthlete(LocalDate.now(), false)
        } else {
            fetchDayData(LocalDate.now(), false)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpCalendar() {
        try {

            val daysOfWeek = daysOfWeekFromLocale()
            val currentMonth = YearMonth.now()

            calendarView!!.apply {
                setup(
                    currentMonth.minusMonths(100),
                    currentMonth.plusMonths(100),
                    daysOfWeek.first()
                )
                scrollToMonth(currentMonth)
            }

//            fetchDataForCurrentMonth(currentMonth)

            class DayViewContainer(view: View) : ViewContainer(view) {
                lateinit var day: CalendarDay
                val binding = Example3CalendarDayBinding.bind(view)

                init {
                    view.setOnClickListener {
                        if (day.owner == DayOwner.THIS_MONTH) {
                            selectDate(day.date)
                        }
                    }
                }
            }

            class MonthViewContainer(view: View) : ViewContainer(view) {
                val legendLayout = Example3CalendarHeaderBinding.bind(view).legendLayout.root
            }

            calendarView!!.dayBinder = object : DayBinder<DayViewContainer> {
                override fun create(view: View) = DayViewContainer(view)
                override fun bind(container: DayViewContainer, day: CalendarDay) {
                    container.day = day
                    val textView = container.binding.exThreeDayText
                    textView.text = day.date.dayOfMonth.toString()

                    with(textView) {
                        setTextColor(resources.getColor(R.color.white))
                        setBackgroundColor(0x1A000000)
                    }

                    if (day.owner == DayOwner.THIS_MONTH) {
                        Log.d("select Date :- ", "${day.date} \t ${day.owner} \t ${selectedDate}")
                        when (day.date) {
                            today -> {
                                with(textView) {
                                    setTextColor(resources.getColor(R.color.white))
                                    setBackgroundResource(R.drawable.img_todaydate)
                                }
                            }

                            selectedDate -> {
                                with(textView) {
                                    setTextColor(resources.getColor(R.color.splash_text_color))
                                    setBackgroundResource(R.drawable.date_select)
                                }
                            }

                            else -> {
                                with(textView) {
                                    setTextColor(resources.getColor(R.color.white))
                                    setBackgroundColor(0x1A000000)
                                }
                            }
                        }

                        Log.d("DOTTTTT", "bind: $datesWithDataTest")
                        Log.d("DOTTTTT", "bind: $datesWithDataEvent")
                        Log.d("DOTTTTT", "bind: $datesWithDataLesson")

                        if (datesWithDataLesson.contains(day.date)) {
                            container.binding.dotLesson.visibility = View.VISIBLE
                        } else {
                            container.binding.dotLesson.visibility = View.GONE
                        }
                        if (datesWithDataTest.contains(day.date)) {
                            container.binding.dotTest.visibility = View.VISIBLE
                        } else {
                            container.binding.dotTest.visibility = View.GONE
                        }
                        if (datesWithDataEvent.contains(day.date)) {
                            container.binding.dotEvent.visibility = View.VISIBLE
                        } else {
                            container.binding.dotEvent.visibility = View.GONE
                        }

                    } else {
                        textView.setTextColor(resources.getColor(R.color.grey))
                    }
                }
            }


            calendarView!!.monthScrollListener = { it ->
                if (!isScrollAllowed) returnTransition // Stop scrolling if not allowed

                isScrollAllowed = false // Disable further scrolling
                Utils.showLoading(requireActivity())
                requireActivity().window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )

                if (it.year == today.year) {
                    titleSameYearFormatter.format(it.yearMonth)
                } else {
                    titleFormatter.format(it.yearMonth)
                }
                fetchDataForCurrentMonth(it.yearMonth)

                val firstDay = it.weekDays.firstOrNull()?.firstOrNull()?.date
                firstDay?.let { date ->
                    lifecycleScope.launch {
                        val fetch1 = async { fetchDayData(date, false) }
                        val fetch2 = async { fetchDayDataAthlete(date, false) }

                        fetch1.await()
                        fetch2.await()

                        delay(1500)

//                        calenderBinding.progressCalender2.visibility = View.GONE // Hide progress bar
                        Utils.hideLoading(requireActivity())
                        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE) // Enable touch
                        isScrollAllowed = true
                    }
                }
            }

            calendarView!!.monthHeaderBinder =
                object : MonthHeaderFooterBinder<MonthViewContainer> {
                    override fun create(view: View) = MonthViewContainer(view)

                    @SuppressLint("ResourceAsColor")
                    override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                        if (container.legendLayout.tag == null) {
                            container.legendLayout.tag = month.yearMonth
                            container.legendLayout.children.map { it as TextView }
                                .forEachIndexed { index, tv ->
                                    tv.text = daysOfWeek[index].name.first().toString()
                                }
                        }
                    }
                }

        } catch (e: Exception) {
            Log.e("error", "Catch:- ${e.message.toString()}")
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

            oldDate?.let { calenderBinding.frgCalendarView.notifyDateChanged(it) }
            calenderBinding.frgCalendarView.notifyDateChanged(date)
            calenderBinding.frgCalendarView.scrollToMonth(YearMonth.from(date))
            updateAdapterForDate(date)

            val data = SelectedDaysModel.Data(
                lessons = listOf(), // Provide a valid list of lessons
                events = listOf(),  // Provide a valid list of events
                tests = listOf(),    // Provide a valid list of tests
                programs = listOf(),    // Provide a valid list of tests
            )
//            checkDatesForMonth(LocalDate.now(), data)


            val userType = preferenceManager.GetFlage()
            if (userType == "Athlete") {
                fetchDayDataAthlete(date, true)
            } else {
                fetchDayData(date, true)
            }
        }
    }

//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun fetchDataForCurrentMonth(month: YearMonth) {
//        val startDate = month.atDay(1)
//        val endDate = month.atEndOfMonth()
//
//        var currentDate = startDate
//        while (!currentDate.isAfter(endDate)) {
//            fetchDayData(currentDate)
//            currentDate = currentDate.plusDays(1)
//        }
//    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchDataForCurrentMonth(month: YearMonth) {
        val startDate = month.atDay(1)
        val endDate = month.atEndOfMonth()

        val deferredList = mutableListOf<Deferred<Unit>>()

        CoroutineScope(Dispatchers.IO).launch {
            var currentDate = startDate
            while (!currentDate.isAfter(endDate)) {
                // Pass LocalDate directly
                deferredList.add(async {
//                    fetchDayData(currentDate)  // ✅ Pass LocalDate here
                })

                currentDate = currentDate.plusDays(1)
            }

            // Await all async calls to finish
            deferredList.awaitAll()

            // Update the calendar view after all data is collected
            withContext(Dispatchers.Main) {
                calendarView?.notifyCalendarChanged()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkDatesForMonth(selectedDate: LocalDate, data: SelectedDaysModel.Data) {
        val currentMonth = YearMonth.from(selectedDate)
        val daysInMonth = currentMonth.lengthOfMonth()

        for (day in 1..31) {
            if (day > daysInMonth) break

            val date = currentMonth.atDay(day)

            updateDatesList(date, data.lessons.map { it.date }, datesWithDataLesson, "Lesson")

            updateDatesList(date, data.events.map { it.date }, datesWithDataEvent, "Event")

            // Check and update tests
            updateDatesList(date, data.tests.map { it.date }, datesWithDataTest, "Test")
        }
    }

    private fun updateDatesList(
        date: LocalDate,
        dataDates: List<String>,
        dateSet: MutableSet<LocalDate>,
        type: String
    ) {
        val dateStr = date.toString()

        if (dataDates.contains(dateStr)) {
            if (!dateSet.contains(date)) {
                dateSet.add(date)
                calendarView?.notifyDateChanged(date)
                Log.d("CheckedDates", "$type date added: $date")
            }
        } else {
            if (dateSet.contains(date)) {
                dateSet.remove(date)
                calendarView?.notifyDateChanged(date)
                Log.d("CheckedDates", "$type date removed: $date")
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateAdapterForDate(date: LocalDate) {
        calenderBinding.dateTv.text = selectionFormatter.format(date)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun daysOfWeekFromLocale(): Array<DayOfWeek> {
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        var daysOfWeek = DayOfWeek.values()
        if (firstDayOfWeek != DayOfWeek.MONDAY) {
            val rhs = daysOfWeek.sliceArray(firstDayOfWeek.ordinal..daysOfWeek.indices.last)
            val lhs = daysOfWeek.sliceArray(0 until firstDayOfWeek.ordinal)
            daysOfWeek = rhs + lhs
        }
        return daysOfWeek
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("NewApi")
    private fun fetchDayData(selectedDate: LocalDate, ShowData: Boolean) {
        Utils.showLoading(requireActivity())

        try {
            val formattedDate = selectionFormatter.format(selectedDate)
            Log.d("CalendarFragment", "Fetching data for date: $formattedDate with ID: $receivedId")

            apiInterface.GetSelectedEvent(formattedDate, receivedId)!!
                .enqueue(object : Callback<SelectedEventModel> {
                    override fun onResponse(
                        call: Call<SelectedEventModel>,
                        response: Response<SelectedEventModel>
                    ) {
                        Log.d("RESPONCE'CODE", "onResponse: ${response.code()}")
                        Utils.hideLoading(requireActivity())

                        if (response.isSuccessful && response.body() != null) {
                            val selectedDaysModel = response.body()
                            Log.d("API Response", "Response: $selectedDaysModel")

                            val countData = selectedDaysModel?.data?.count
                            if (!countData.isNullOrEmpty()) {
                                processCountData(countData)
                            } else {
                                Log.e("API Response", "Count data is empty.")
                            }

                            val data = selectedDaysModel?.data
                            if (data != null) {
                                if (::eventadapter.isInitialized) {
                                    eventadapter.clearData()
                                }

                                Log.d("SSKSKSKS", "onResponse: $ShowData")
                                if (ShowData == true) {
                                    initEventRecyclerView(emptyList())
                                    initLessonRecyclerView(emptyList())
                                    initTestRecyclerView(emptyList())

                                    Log.d("SKKSKSKS", "onResponse: ${data.list.lessons.size}")
                                    if (data.list.lessons.isNotEmpty()) {
                                        initLessonRecyclerView(data.list.lessons)
                                    }
                                    if (data.list.events.isNotEmpty()) {
                                        initEventRecyclerView(data.list.events)
                                    }
                                    if (data.list.tests.isNotEmpty()) {
                                        initTestRecyclerView(data.list.tests)
                                    }
                                }

                            } else {
                                Log.e("API Response", "Data is null. No dot added.")
                            }
                        }
                    }

                    override fun onFailure(call: Call<SelectedEventModel>, t: Throwable) {
                        Utils.hideLoading(requireActivity())

                        Log.e("API Response", "Error: ${t.message}")
                    }
                })
        } catch (e: Exception) {
            Utils.hideLoading(requireActivity())
            Log.e("Catch", "CatchError :- ${e.message}")
        }
    }

    @SuppressLint("NewApi")
    private fun processCountData(countList: List<SelectedEventModel.Count>) {
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        countList.forEach { count ->
            try {
                val date = LocalDate.parse(count.date, dateFormatter)

                if (count.lessons > 0 && !datesWithDataLesson.contains(date)) {
                    datesWithDataLesson.add(date)
                    calendarView!!.notifyDateChanged(date)
                }

                if (count.events > 0 && !datesWithDataEvent.contains(date)) {
                    datesWithDataEvent.add(date)
                    calendarView!!.notifyDateChanged(date)
                }

                if (count.tests > 0 && !datesWithDataTest.contains(date)) {
                    datesWithDataTest.add(date)
                    calendarView!!.notifyDateChanged(date)
                }

            } catch (e: DateTimeParseException) {
                Log.e("DateParseError", "Invalid date format or non-leap year: ${count.date}")
            }
        }
    }

    @SuppressLint("NewApi")
    private fun fetchDayDataAthlete(selectedDate: LocalDate, ShowData: Boolean) {
        try {
            val formattedDate = selectionFormatter.format(selectedDate)
            Log.d(
                "CalendarFragmentF",
                "Fetching data for date: $formattedDate with ID: $receivedGropu_Id"
            )

            apiInterface.GetSelectedEventAthlete(formattedDate, receivedGropu_Id)!!
                .enqueue(object : Callback<SelectedEventModel> {
                    override fun onResponse(
                        call: Call<SelectedEventModel>,
                        response: Response<SelectedEventModel>
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            val selectedDaysModel = response.body()
                            Log.d("API Response", "Response: $selectedDaysModel")

                            val countData = selectedDaysModel?.data?.count
                            if (!countData.isNullOrEmpty()) {
                                processCountData(countData)
                            } else {
                                Log.e("API Response", "Count data is empty.")
                            }

                            val data = selectedDaysModel?.data
                            if (data != null) {
                                if (::eventadapter.isInitialized) {
                                    eventadapter.clearData()
                                }

                                if (ShowData == true) {
                                    initLessonRecyclerView(emptyList())
                                    initEventRecyclerView(emptyList())
                                    initTestRecyclerView(emptyList())

                                    if (data.list.lessons.isNotEmpty()) {
                                        initLessonRecyclerView(data.list.lessons)
                                    }
                                    if (data.list.events.isNotEmpty()) {
                                        initEventRecyclerView(data.list.events)
                                    }
                                    if (data.list.tests.isNotEmpty()) {
                                        initTestRecyclerView(data.list.tests)
                                    }
                                }


                            } else {
                                Log.e("API Response", "Data is null. No dot added.")
                            }
                        } else {
                            Log.e("API Response", "Failed to fetch data: ${response.message()}")
                        }
                    }

                    override fun onFailure(call: Call<SelectedEventModel>, t: Throwable) {
                        Log.e("API Response", "Error: ${t.message}")
                    }
                })
        } catch (e: Exception) {
            Log.e("Catch", "CatchError :- ${e.message}")
        }
    }


    private fun initLessonRecyclerView(programs: List<SelectedEventModel.Lesson>) {
        calenderBinding.rcvlesson.layoutManager = LinearLayoutManager(requireContext())
        lessonadapter = SelectedEventLessonAdapter(programs, requireContext(), this)
        calenderBinding.rcvlesson.adapter = lessonadapter
    }

    private fun initTestRecyclerView(tests: List<SelectedEventModel.Test>) {
        calenderBinding.rcvtest.layoutManager = LinearLayoutManager(requireContext())
        testadapter = SelectedEventTestAdapter(tests, requireContext(), this)
        calenderBinding.rcvtest.adapter = testadapter
    }

    private fun initEventRecyclerView(events: List<SelectedEventModel.Event>) {
        if (events.isNotEmpty()) {
            Log.d("Event RecyclerView", "Setting up RecyclerView with events.")
            calenderBinding.rcvevent.layoutManager = LinearLayoutManager(requireContext())
            eventadapter = SelectedEventEventAdapter(events, requireContext(), this)
            calenderBinding.rcvevent.adapter = eventadapter
        } else {
            Log.d("Event RecyclerView", "No events available.")
        }
    }

    private fun initViews() {
        apiClient = APIClient(requireContext())
        preferenceManager = PreferencesManager(requireContext())
        apiInterface = apiClient.client().create(APIInterface::class.java)

        val sharedPreferences =
            requireActivity().getSharedPreferences("AppPreferences", MODE_PRIVATE)
        receivedId = sharedPreferences.getString("id", "default_value") ?: ""
        receivedGropu_Id = sharedPreferences.getString("group_id", "default_value") ?: ""

        Log.d("CalenderFragmentR", "Received ID from SharedPreferences: $receivedId")
        Log.d("CalenderFragmentR", "Received ID from SharedPreferences: $receivedGropu_Id")

        val userType = preferenceManager.GetFlage()
        if (userType == "Athlete") {
            val menu = calenderBinding.navigationView.menu
            menu.clear() // Remove existing items

            menu.add(Menu.NONE, R.id.tv_notification, Menu.NONE, getString(R.string.notification)).setIcon(R.drawable.ic_notification)

            menu.add(Menu.NONE, R.id.tv_policy, Menu.NONE, getString(R.string.privacyPolicy)).setIcon(R.drawable.ic_privacy)

            menu.add(Menu.NONE, R.id.tv_favorite, Menu.NONE, getString(R.string.favourite)).setIcon(R.drawable.ic_favorite)

            menu.add(Menu.NONE, R.id.tv_profile, Menu.NONE, getString(R.string.performanceProfile)).setIcon(R.drawable.ic_perfomance)

            menu.add(Menu.NONE, R.id.tv_analysis, Menu.NONE, getString(R.string.competitionAnalysis)).setIcon(R.drawable.ic_competition)

            menu.add(Menu.NONE, R.id.tv_view_analysis, Menu.NONE, getString(R.string.viewAnalysis)).setIcon(R.drawable.ic_competition)

            menu.add(Menu.NONE, R.id.tv_personal_diary, Menu.NONE, getString(R.string.personalDiary)).setIcon(R.drawable.icd)

            menu.add(Menu.NONE, R.id.tv_setting, Menu.NONE, getString(R.string.personalDiary)).setIcon(R.drawable.ic_setting)

            menu.add(Menu.NONE, R.id.logout, Menu.NONE, getString(R.string.logout)).setIcon(R.drawable.logout)
        } else {
            // Default menu setup for other users
//            calenderBinding.navigationView.inflateMenu(R.menu.activity_main_drawer) // Load menu from XML
        }

    }

    override fun onItemClicked(view: View, position: Int, type: Long, string: String) {
        if (string == "fav") {
            Utils.showLoading(requireActivity())
            val id: MultipartBody.Part =
                MultipartBody.Part.createFormData("id", type.toInt().toString())
            apiInterface.Favourite_lession(id)?.enqueue(object : Callback<RegisterData?> {
                override fun onResponse(
                    call: Call<RegisterData?>,
                    response: Response<RegisterData?>
                ) {
                    Utils.hideLoading(requireActivity())
                    Log.d("TAG", response.code().toString() + "")
                    val code = response.code()
                    if (code == 200) {
                        val resource: RegisterData? = response.body()
                        val Success: Boolean = resource?.status!!
                        val Message: String = resource.message!!
                        if (Success) {
                            Utils.hideLoading(requireActivity())
                            loadData()
                        } else {
                            Utils.hideLoading(requireActivity())
                            Toast.makeText(
                                requireContext(),
                                "" + Message,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(requireContext())
                    } else {
                        Utils.hideLoading(requireActivity())
                        Toast.makeText(
                            requireContext(),
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Utils.hideLoading(requireActivity())
                    Toast.makeText(requireContext(), "" + t.message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }
            })
        } else if (string == "unfav") {
            Utils.showLoading(requireActivity())
            val id: MultipartBody.Part =
                MultipartBody.Part.createFormData("id", type.toInt().toString())
            apiInterface.DeleteFavourite_lession(type.toInt())
                ?.enqueue(object : Callback<RegisterData?> {
                    override fun onResponse(
                        call: Call<RegisterData?>,
                        response: Response<RegisterData?>
                    ) {
                        Log.d("TAG", response.code().toString() + "")
                        Utils.hideLoading(requireActivity())
                        val code = response.code()
                        if (code == 200) {
                            val resource: RegisterData? = response.body()
                            val Success: Boolean = resource?.status!!
                            val Message: String = resource.message!!
                            if (Success) {
                                Utils.hideLoading(requireActivity())
                                loadData()
                            } else {
                                Utils.hideLoading(requireActivity())
                                Toast.makeText(
                                    requireContext(),
                                    "" + Message,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        } else if (code == 403) {
                            Utils.setUnAuthDialog(requireContext())
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "" + response.message(),
                                Toast.LENGTH_SHORT
                            ).show()
                            call.cancel()
                        }
                    }

                    override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                        Utils.hideLoading(requireActivity())
                        Toast.makeText(
                            requireContext(),
                            "" + t.message,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                })
        } else if (string == "favevent") {
            Utils.showLoading(requireActivity())
            val id: MultipartBody.Part =
                MultipartBody.Part.createFormData("id", type.toInt().toString())
            apiInterface.Favourite_Event(id)?.enqueue(object : Callback<RegisterData?> {
                override fun onResponse(
                    call: Call<RegisterData?>,
                    response: Response<RegisterData?>
                ) {
                    Utils.hideLoading(requireActivity())
                    Log.d("TAG", response.code().toString() + "")
                    val code = response.code()
                    if (code == 200) {
                        val resource: RegisterData? = response.body()
                        val Success: Boolean = resource?.status!!
                        val Message: String = resource.message!!
                        if (Success) {
                            Utils.hideLoading(requireActivity())
                            loadData()
                        } else {
                            Utils.hideLoading(requireActivity())
                            Toast.makeText(
                                requireContext(),
                                "" + Message,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(requireContext())
                    } else {
                        Utils.hideLoading(requireActivity())
                        Toast.makeText(
                            requireContext(),
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Utils.hideLoading(requireActivity())
                    Toast.makeText(requireContext(), "" + t.message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }
            })
        } else if (string == "unfavevent") {
            Utils.showLoading(requireActivity())
            val id: MultipartBody.Part =
                MultipartBody.Part.createFormData("id", type.toInt().toString())
            apiInterface.DeleteFavourite_Event(type.toInt())
                ?.enqueue(object : Callback<RegisterData?> {
                    override fun onResponse(
                        call: Call<RegisterData?>,
                        response: Response<RegisterData?>
                    ) {
                        Log.d("TAG", response.code().toString() + "")
                        Utils.hideLoading(requireActivity())
                        val code = response.code()
                        if (code == 200) {
                            val resource: RegisterData? = response.body()
                            val Success: Boolean = resource?.status!!
                            val Message: String = resource.message!!
                            if (Success) {
                                Utils.hideLoading(requireActivity())
                                loadData()
                            } else {
                                Utils.hideLoading(requireActivity())
                                Toast.makeText(
                                    requireContext(),
                                    "" + Message,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        } else if (code == 403) {
                            Utils.setUnAuthDialog(requireContext())
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "" + response.message(),
                                Toast.LENGTH_SHORT
                            ).show()
                            call.cancel()
                        }
                    }

                    override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                        Utils.hideLoading(requireActivity())
                        Toast.makeText(
                            requireContext(),
                            "" + t.message,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                })
        } else if (string == "favtest") {
            Utils.showLoading(requireActivity())
            val id: MultipartBody.Part =
                MultipartBody.Part.createFormData("id", type.toInt().toString())
            apiInterface.Favourite_Test(id)?.enqueue(object : Callback<RegisterData?> {
                override fun onResponse(
                    call: Call<RegisterData?>,
                    response: Response<RegisterData?>
                ) {
                    Utils.hideLoading(requireActivity())
                    Log.d("TAG", response.code().toString() + "")
                    val code = response.code()
                    if (code == 200) {
                        val resource: RegisterData? = response.body()
                        val Success: Boolean = resource?.status!!
                        val Message: String = resource.message!!
                        if (Success) {
                            Utils.hideLoading(requireActivity())
                            loadData()
                        } else {
                            Utils.hideLoading(requireActivity())
                            Toast.makeText(
                                requireContext(),
                                "" + Message,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(requireContext())
                    } else {
                        Utils.hideLoading(requireActivity())
                        Toast.makeText(
                            requireContext(),
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {

                    Utils.hideLoading(requireActivity())
                    Toast.makeText(requireContext(), "" + t.message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }
            })
        } else if (string == "unfavtest") {
            Utils.showLoading(requireActivity())
            var id: MultipartBody.Part =
                MultipartBody.Part.createFormData("id", type.toInt().toString())
            apiInterface.DeleteFavourite_Test(type.toInt())
                ?.enqueue(object : Callback<RegisterData?> {
                    override fun onResponse(
                        call: Call<RegisterData?>,
                        response: Response<RegisterData?>
                    ) {
                        Log.d("TAG", response.code().toString() + "")
                        Utils.hideLoading(requireActivity())
                        val code = response.code()
                        if (code == 200) {
                            val resource: RegisterData? = response.body()
                            val Success: Boolean = resource?.status!!
                            val Message: String = resource.message!!
                            if (Success) {
                                Utils.hideLoading(requireActivity())
                                loadData()
                            } else {
                                Utils.hideLoading(requireActivity())
                                Toast.makeText(
                                    requireContext(),
                                    "" + Message,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        } else if (code == 403) {
                            Utils.setUnAuthDialog(requireContext())
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "" + response.message(),
                                Toast.LENGTH_SHORT
                            ).show()
                            call.cancel()
                        }
                    }

                    override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                        Utils.hideLoading(requireActivity())
                        Toast.makeText(
                            requireContext(),
                            "" + t.message,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                })
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.tv_notification) {
            calenderBinding.drawerLayout.closeDrawer(GravityCompat.START)
            val intent = Intent(requireContext(), NotificationActivity::class.java)
            startActivity(intent)
            return true
        } else if (item.itemId == R.id.tv_policy) {
            calenderBinding.drawerLayout.closeDrawer(GravityCompat.START)
            val intent = Intent(requireContext(), PrivacyPolicyActivity::class.java)
            startActivity(intent)
            return true
        }
        //        else if (item.itemId == R.id.tv_invited_friend) {
        //            calenderBinding.drawerLayout.closeDrawer(GravityCompat.START)
        //            val intent = Intent(requireContext(), Invite_friendActivity::class.java)
        //            startActivity(intent)
        //            return true
        //        }
        else if (item.itemId == R.id.tv_library) {
            calenderBinding.drawerLayout.closeDrawer(GravityCompat.START)
            val intent = Intent(requireContext(), LibraryActivity::class.java)
            startActivity(intent)
            return true
        } else if (item.itemId == R.id.tv_favorite) {
            calenderBinding.drawerLayout.closeDrawer(GravityCompat.START)
            val intent = Intent(requireContext(), FavoriteActivity::class.java)
            startActivity(intent)
            return true
        } else if (item.itemId == R.id.tv_athletes) {
            calenderBinding.drawerLayout.closeDrawer(GravityCompat.START)
            val intent = Intent(requireContext(), AthletesActivity::class.java)
            startActivity(intent)
            return true
        } else if (item.itemId == R.id.tv_view_analysis) {
            calenderBinding.drawerLayout.closeDrawer(GravityCompat.START)
            val intent = Intent(requireContext(), ViewAnalysisActivity::class.java)
            startActivity(intent)
            return true
        } else if (item.itemId == R.id.tv_profile) {
            val intent = Intent(requireContext(), PerformanceProfileActivity::class.java)
            startActivity(intent)
            calenderBinding.drawerLayout.closeDrawer(GravityCompat.START)
            return true
        } else if (item.itemId == R.id.tv_analysis) {
            calenderBinding.drawerLayout.closeDrawer(GravityCompat.START)
            preferenceManager.setSelectEvent(false)
            val intent = Intent(requireContext(), CompetitionActivity::class.java)
            startActivity(intent)
            //Toast.makeText(requireContext(), "Competition Analysis", Toast.LENGTH_SHORT).show()
            return true
        } else if (item.itemId == R.id.tv_personal_diary) {
            calenderBinding.drawerLayout.closeDrawer(GravityCompat.START)
            val intent = Intent(requireContext(), ViewPersonalDiaryActivity::class.java)
            startActivity(intent)
            //          Toast.makeText(requireContext(), "Personal Diary", Toast.LENGTH_SHORT).show()
            return true
        } else if (item.itemId == R.id.tv_setting) {
            calenderBinding.drawerLayout.closeDrawer(GravityCompat.START)
            val intent = Intent(requireContext(), SettingActivity::class.java)
            startActivity(intent)
            return true
        } else if (item.itemId == R.id.tv_remind) {
            calenderBinding.drawerLayout.closeDrawer(GravityCompat.START)
            val intent = Intent(requireContext(), ViewRemindMeActivity::class.java)
            startActivity(intent)
            return true
        } else if (item.itemId == R.id.logout) {
            calenderBinding.drawerLayout.closeDrawer(GravityCompat.START)

            Utils.showLoading(requireActivity())
            apiInterface.LogOut()?.enqueue(object : Callback<RegisterData?> {
                override fun onResponse(
                    call: Call<RegisterData?>,
                    response: Response<RegisterData?>
                ) {
                    Utils.hideLoading(requireActivity())
                    Log.d("TAG", response.code().toString() + "")
                    val resource: RegisterData? = response.body()
                    //                    val Success: Boolean = resource?.status!!

                    if (response.code() == 200) {
                        val Message: String = resource!!.message!!
                        Toast.makeText(requireContext(), "" + Message, Toast.LENGTH_SHORT).show()
                        preferenceManager.setUserLogIn(false)
                        with(sharedPreferences.edit()) {
                            clear()
                            apply()
                        }
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    } else if (response.code() == 403) {
                        Utils.setUnAuthDialog(requireContext())
                        //                        Toast.makeText(
                        //                            requireContext(),
                        //                            "" + response.message(),
                        //                            Toast.LENGTH_SHORT
                        //                        ).show()
                        //                        preferenceManager.setUserLogIn(false)
                        //                        val intent = Intent(requireContext(), MainActivity::class.java)
                        //                        startActivity(intent)
                        //                        requireActivity().finish()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        ).show()
                        call.cancel()
                        //                        preferenceManager.setUserLogIn(false)
                        //                        val intent = Intent(requireContext(), MainActivity::class.java)
                        //                        startActivity(intent)
                        //                        requireActivity().finish()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Toast.makeText(requireContext(), "" + t.message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                    Utils.hideLoading(requireActivity())
                }
            })


            return true
        } else {
            return false
        }
    }

}