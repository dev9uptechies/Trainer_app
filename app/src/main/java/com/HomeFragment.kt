package com

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.core.text.HtmlCompat
import androidx.core.view.GravityCompat
import androidx.core.view.children
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.Adapter.selected_day.LessonAdapter
import com.example.Adapter.selected_day.eventAdapter
import com.example.Adapter.selected_day.testAdapter
import com.example.AthletesActivity
import com.example.GroupListData
import com.example.NewsAdapter
import com.example.OnItemClickListener
import com.example.SelectGroupActivity
import com.example.model.HomeFragment.NewsModel
import com.example.model.PrivacyPolicy.privacypolicy
import com.example.model.SelectedDaysModel
import com.example.model.personal_diary.GetDiaryDataForEdit
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.ApiClass.WorkOutAdapter
import com.example.trainerapp.HomeAdapter
import com.example.trainerapp.LibraryActivity
import com.example.trainerapp.MainActivity
import com.example.trainerapp.PerformanceProfileActivity
import com.example.trainerapp.PreferencesManager
import com.example.trainerapp.R
import com.example.trainerapp.RemindMe.ViewRemindMeActivity
import com.example.trainerapp.SettingActivity
import com.example.trainerapp.Sport_list
import com.example.trainerapp.Utils
import com.example.trainerapp.Work_Out
import com.example.trainerapp.competition.CompetitionActivity
import com.example.trainerapp.databinding.Example3CalendarDayBinding
import com.example.trainerapp.databinding.Example3CalendarHeaderBinding
import com.example.trainerapp.databinding.FragmentHomeBinding
import com.example.trainerapp.notification.NotificationActivity
import com.example.trainerapp.personal_diary.ViewPersonalDiaryActivity
import com.example.trainerapp.privacy_policy.PrivacyPolicyActivity
import com.example.trainerapp.view_analysis.ViewAnalysisActivity
import com.example.trainerappAthlete.model.EventAdapterAthlete
import com.example.trainerappAthlete.model.GroupListAthlete
import com.example.trainerappAthlete.model.LessonAdapterAthlete
import com.example.trainerappAthlete.model.PlanningAdapterAthlete
import com.example.trainerappAthlete.model.PlanningAdapterAthleteHome
import com.example.trainerappAthlete.model.TestAdapterAthlete
import com.google.android.material.navigation.NavigationView
import com.kizitonwose.calendarview.CalendarView
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import com.kizitonwose.calendarview.utils.yearMonth
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

class HomeFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener,
    OnItemClickListener.OnItemClickCallback {
    lateinit var homeFragmentBinding: FragmentHomeBinding
    private lateinit var Sportlist: java.util.ArrayList<Sport_list>
    private lateinit var WorkOutlist: java.util.ArrayList<Work_Out>
    lateinit var preferenceManager: PreferencesManager
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    var calendarView: CalendarView? = null
    var actionBarDrawerToggle: ActionBarDrawerToggle? = null
    lateinit var adapter: HomeAdapter
    lateinit var newsadapter: NewsAdapter
    lateinit var workoutadapter: WorkOutAdapter
    private var instractionData: privacypolicy.Data? = null
    private lateinit var testAdapterAthlete: TestAdapterAthlete
    private lateinit var eventAdapterAthlete: EventAdapterAthlete
    private lateinit var lessonAdapterAthlete: LessonAdapterAthlete
    private lateinit var adapterAthelete: PlanningAdapterAthleteHome
    private var newsData: MutableList<NewsModel.Data> = mutableListOf()

    // Received Data
    private var receivedIds: String = ""
    private var receivedGroup_Ids: String = ""
    private var receivedname: String? = null
    private var receivedstartDate: String? = null
    private var receivedendDate: String? = null
    private var receivedmesocycle: String? = null
    private var receivedworkloadColor: String? = null

    private var receivedPreCompetitiveName: String? = null
    private var receivedPreCompetitiveStartDate: String? = null
    private var receivedPreCompetitiveEndDate: String? = null
    private var receivedPreCompetitiveMesocycle: String? = null
    private var receivedPreCompetitiveWorkloadColor: String? = null

    private var receivedCommpetitiveName: String? = null
    private var receivedCompetitiveStartDate: String? = null
    private var receivedCompetitiveEndDate: String? = null
    private var receivedCompetitiveMesocycle: String? = null
    private var receivedCompetitiveWorkloadColor: String? = null

    private var receivedTransitionName: String? = null
    private var receivedTransitionStartDate: String? = null
    private var receivedTransitionEndDate: String? = null
    private var receivedTransitionMesocycle: String? = null
    private var receivedTransitionWorkloadColor: String? = null




    var formattedDate: String? = null
    var LessonData: MutableList<SelectedDaysModel.Lesson> = mutableListOf()
    var TestData: MutableList<SelectedDaysModel.Test> = mutableListOf()
    var EventData: MutableList<SelectedDaysModel.Event> = mutableListOf()
    private val datesWithDataTest = mutableSetOf<LocalDate>() // Set to track dates with data


    lateinit var lessonadapter: LessonAdapter
    lateinit var eventadapter: eventAdapter
    lateinit var testadapter: testAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    private val selectionFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    private val sharedPreferences by lazy {
        requireContext().getSharedPreferences("RemindMePrefs", MODE_PRIVATE)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private val today = LocalDate.now()
    private var selectedDate: LocalDate? = null

    @RequiresApi(Build.VERSION_CODES.O)
    private val titleSameYearFormatter = DateTimeFormatter.ofPattern("MMMM")

    @RequiresApi(Build.VERSION_CODES.O)
    private val titleFormatter = DateTimeFormatter.ofPattern("MMM yyyy")

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeFragmentBinding = FragmentHomeBinding.inflate(inflater, container, false)
        homeFragmentBinding.exSevenCalendar.setBackgroundResource(0)

        homeFragmentBinding.nextTv
        homeFragmentBinding.viewRecycler
        homeFragmentBinding.linerAthlete.visibility = View.VISIBLE
        calendarView = homeFragmentBinding.exSevenCalendar

        initViews()
        setDrawerToggle()
        getInstraction()
        GetNews()
        setUpCalendar()
        val currentDate = homeFragmentBinding.exSevenCalendar!!.findFirstVisibleDay()?.date ?: LocalDate.now()
        val formattedMonthYear = DateTimeFormatter.ofPattern("MMMM yyyy").format(currentDate)
        Log.d("CalendarLog", "Current month and year: $formattedMonthYear")
        homeFragmentBinding.tvDate.text = formattedMonthYear

        val receivedIdInt = receivedIds.toIntOrNull()
        Log.d("QOQOQOQOQOOQOQO", "onCreateView: $receivedIds")

        val userType = preferenceManager.GetFlage()

        if (userType == "Athlete") {
            val menu = homeFragmentBinding.navigationView.menu
            menu.clear()

            menu.add(Menu.NONE, R.id.tv_notification, Menu.NONE, "Notification").setIcon(R.drawable.ic_notification)

            menu.add(Menu.NONE, R.id.tv_policy, Menu.NONE, "Privacy Policy").setIcon(R.drawable.ic_privacy)

            menu.add(Menu.NONE, R.id.tv_favorite, Menu.NONE, "Favorites").setIcon(R.drawable.ic_favorite)

            menu.add(Menu.NONE, R.id.tv_profile, Menu.NONE, "Performance Profile").setIcon(R.drawable.ic_perfomance)

            menu.add(Menu.NONE, R.id.tv_analysis, Menu.NONE, "Competition Analysis").setIcon(R.drawable.ic_competition)

            menu.add(Menu.NONE, R.id.tv_view_analysis, Menu.NONE, "View Analysis").setIcon(R.drawable.ic_competition)

            menu.add(Menu.NONE, R.id.tv_personal_diary, Menu.NONE, "Personal Diary").setIcon(R.drawable.icd)

            menu.add(Menu.NONE, R.id.tv_setting, Menu.NONE, "Settings").setIcon(R.drawable.ic_setting)

            menu.add(Menu.NONE, R.id.logout, Menu.NONE, "Logout").setIcon(R.drawable.logout)
        } else {
            val menu = homeFragmentBinding.navigationView.menu

            menu.clear()
            homeFragmentBinding.navigationView.inflateMenu(R.menu.activity_main_drawer)
        }


        if (userType == "Athlete") {

            homeFragmentBinding.startTv.text = "START YOUR"
            homeFragmentBinding.mainTv.text = "NEXT WORKOUT"
            homeFragmentBinding.linerAthlete.visibility = View.VISIBLE


            Log.d("SSLLSLSLSL", "onCreateView: $receivedGroup_Ids")


            if (receivedIdInt != null) {
                callGroupApiAthlete(receivedIdInt)
            } else {
                Log.e("API Call", "Invalid receivedId: $receivedIds")
                clearSavedGroupData()
            }

            if (!receivedGroup_Ids.isNullOrEmpty()) {

                Log.d("SSSJSJSJSJSJJr", "onCreateView: $receivedCommpetitiveName   $receivedstartDate ")

//                if (!receivedname.isNullOrEmpty()) {
//                    homeFragmentBinding.seasonLy.visibility = View.VISIBLE
//                    homeFragmentBinding.titleTv.text = receivedname
//                    homeFragmentBinding.edtStartDate.text = receivedstartDate
//                    homeFragmentBinding.edtEndDate.text = receivedendDate
//                    homeFragmentBinding.edtMesocycle.text = receivedmesocycle
//                }else {
//                    homeFragmentBinding.seasonLy.visibility = View.GONE
//                }
//
//                Log.d("AQQAAQQAQA", "onCreateView: $receivedPreCompetitiveName")
//
//                if (!receivedPreCompetitiveName.isNullOrEmpty()) {
//                    homeFragmentBinding.PreCompetitiveLy.visibility = View.GONE
//                    homeFragmentBinding.titleTvPreCompetitive.text = receivedPreCompetitiveName
//                    homeFragmentBinding.edtStartDatePreCompetitive.text = receivedPreCompetitiveStartDate
//                    homeFragmentBinding.edtEndDatePreCompetitive.text = receivedPreCompetitiveEndDate
//                    homeFragmentBinding.edtMesocyclePreCompetitive.text = receivedPreCompetitiveMesocycle
//                }else {
//                    homeFragmentBinding.PreCompetitiveLy.visibility = View.GONE
//                }
//
//                if (!receivedCommpetitiveName.isNullOrEmpty()) {
//                    homeFragmentBinding.CompetitiveLy.visibility = View.GONE
//                    homeFragmentBinding.titleTvCompetitive.text = receivedCommpetitiveName
//                    homeFragmentBinding.edtStartDateCompetitive.text = receivedCompetitiveStartDate
//                    homeFragmentBinding.edtEndDateCompetitive.text = receivedCompetitiveEndDate
//                    homeFragmentBinding.edtMesocycleCompetitive.text = receivedCompetitiveMesocycle
//                } else {
//                    homeFragmentBinding.CompetitiveLy.visibility = View.GONE
//                }

                Log.d("SSSJSJSJSJSJJr", "onCreateView: $receivedCommpetitiveName   $receivedendDate ")
            }

        } else {
            homeFragmentBinding.linerAthlete.visibility = View.GONE
            if (receivedIdInt != null) {
                callGroupApi(receivedIdInt)
            } else {
                Log.e("API Call", "Invalid receivedId: $receivedIds")
                clearSavedGroupData()
            }

            homeFragmentBinding.navigationView.menu.findItem(R.id.tv_library).isVisible = true
            homeFragmentBinding.navigationView.menu.findItem(R.id.tv_athletes).isVisible = true
        }

        val (savedGroupName, savedStartDate) = getSavedGroupData()

        if (savedGroupName != null && savedStartDate != null) {
            homeFragmentBinding.selectGroupTxt.text = savedGroupName
            homeFragmentBinding.edtStartDate.text = savedStartDate
        } else {
            homeFragmentBinding.edtStartDate.text = ""
        }

        homeFragmentBinding.dialogButton.setOnClickListener {
//                showWorkoutInfoDialog(requireContext())
            homeFragmentBinding.dialog.visibility = View.VISIBLE
        }

        homeFragmentBinding.colseButton.setOnClickListener {
            homeFragmentBinding.dialog.visibility = View.GONE
        }

        homeFragmentBinding.sidemenu.setOnClickListener {
            homeFragmentBinding.drawerLayout.openDrawer(GravityCompat.START)
        }

        homeFragmentBinding.linerSelectGroup.setOnClickListener {
            val intent = Intent(requireContext(), SelectGroupActivity::class.java)
            startActivity(intent)
        }

        setContent()

        return homeFragmentBinding.root
    }

        @SuppressLint("NewApi")
        private fun fetchDayDataRecycler(selectedDate: LocalDate) {
            try {
                val formattedDate = selectionFormatter.format(selectedDate)
                Log.d("CalendarFragment", "Fetching data for date: $formattedDate with ID: $receivedGroup_Ids")
                homeFragmentBinding.homeProgress.visibility = View.VISIBLE
                apiInterface.GetSelectedDaysAthlete(formattedDate, receivedGroup_Ids)!!
                    .enqueue(object : Callback<SelectedDaysModel> {
                        override fun onResponse(
                            call: Call<SelectedDaysModel>,
                            response: Response<SelectedDaysModel>
                        ) {
                            homeFragmentBinding.homeProgress.visibility = View.GONE

                            if (response.isSuccessful && response.body() != null) {

                                val selectedDaysModel = response.body()
                                Log.d("API Response", "Response: $selectedDaysModel")

                                val data = selectedDaysModel?.data
                                if (data != null) {
                                    if (::eventadapter.isInitialized) {
                                        eventadapter.clearData()
                                    }

                                    if (data.lessons.isNotEmpty()) {
                                        LessonData.clear()
                                        LessonData.addAll(data.lessons)
                                        initLessonRecyclerView(LessonData)

                                        if (!datesWithDataTest.contains(selectedDate)) {
                                            datesWithDataTest.add(selectedDate)
                                            calendarView!!.notifyDateChanged(selectedDate)
                                        }
                                    } else {

                                    }

                                    if (data.events.isNotEmpty()) {
                                        EventData.clear()
                                        EventData.addAll(data.events)
                                        initEventRecyclerView(EventData)
                                        if (!datesWithDataTest.contains(selectedDate)) {
                                            datesWithDataTest.add(selectedDate)
                                            calendarView!!.notifyDateChanged(selectedDate)
                                        }
                                    } else {

                                    }

                                    if (data.tests.isNotEmpty()) {
                                        TestData.clear()
                                        TestData.addAll(data.tests)
                                        initTestRecyclerView(TestData)

                                        if (!datesWithDataTest.contains(selectedDate)) {
                                            datesWithDataTest.add(selectedDate)
                                            calendarView!!.notifyDateChanged(selectedDate)
                                        }
                                    } else {

                                    }

                                    Log.d("?????", "onResponse: ${TestData.getOrNull(0)?.title} \n ${EventData.getOrNull(0)?.title} \n ${LessonData.getOrNull(0)?.name}")

                                    Log.d("SKSKKSKSKSK", "onResponse: $datesWithDataTest")

                                } else {
                                    Log.e("API Response", "Data is null. No dot added.")
                                }



                                Log.d("MDKMKDMKDKDKK", "onResponse: $data")
                            } else if (response.code() == 429) {
                                homeFragmentBinding.homeProgress.visibility = View.GONE
                                Toast.makeText(requireContext(), "Too Many Request", Toast.LENGTH_SHORT)
                                    .show()
                            } else {
                                Log.e("API Response", "Failed to fetch data: ${response.message()}")
                            }
                        }

                        override fun onFailure(call: Call<SelectedDaysModel>, t: Throwable) {
                            homeFragmentBinding.homeProgress.visibility = View.GONE

                            Log.e("API Response", "Error: ${t.message}")
                        }
                    })
            } catch (e: Exception) {
                homeFragmentBinding.homeProgress.visibility = View.GONE
                Log.e("Catch", "CatchError :- ${e.message}")
            }
        }

    private fun initLessonRecyclerView(programs: List<SelectedDaysModel.Lesson>) {
        homeFragmentBinding.favLessonRly.layoutManager = LinearLayoutManager(requireContext())
        lessonadapter = LessonAdapter(programs, requireContext(), this)
        homeFragmentBinding.favLessonRly.adapter = lessonadapter
    }

    private fun initTestRecyclerView(tests: List<SelectedDaysModel.Test>) {
        homeFragmentBinding.favTestRly.layoutManager = LinearLayoutManager(requireContext())
        testadapter = testAdapter(tests, requireContext(), this)
        homeFragmentBinding.favTestRly.adapter = testadapter
    }

    private fun initEventRecyclerView(events: List<SelectedDaysModel.Event>) {
        if (events.isNotEmpty()) {
            Log.d("Event RecyclerView", "Setting up RecyclerView with events.")
            homeFragmentBinding.favEventRly.layoutManager = LinearLayoutManager(requireContext())
            eventadapter = eventAdapter(events, requireContext(), this)
            homeFragmentBinding.favEventRly.adapter = eventadapter
        } else {
            Log.d("Event RecyclerView", "No events available.")
        }
    }

    private fun setContent() {
        val workout = arrayOf("Instruction", "Information", "News")

        for (item in workout.indices) {
            WorkOutlist.add(Work_Out(workout[item]))
        }

        val language = arrayOf("LUN", "USR", "UFN", "LUN", "USR", "UFN")

        for (item in language.indices) {
            Sportlist.add(Sport_list(language[item]))
        }

        initRecyclerView(Sportlist)
        initView(WorkOutlist)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpCalendar() {
            val daysOfWeek = daysOfWeekFromLocale()
            val currentMonth = YearMonth.now()
            val today = LocalDate.now()

            homeFragmentBinding.exSevenCalendar!!.apply {
                setup(currentMonth.minusMonths(100), currentMonth.plusMonths(100), daysOfWeek.first())
                scrollToMonth(currentMonth)
            }

            homeFragmentBinding.exSevenCalendar!!.post {
                homeFragmentBinding.exSevenCalendar!!.scrollToDate(today)
                fetchCurrentWeekDates()
            }

            class DayViewContainer(view: View) : ViewContainer(view) {
                lateinit var day: CalendarDay
                lateinit var month: YearMonth
                val binding = Example3CalendarDayBinding.bind(view)

                init {

                    val formattedMonth = DateTimeFormatter.ofPattern("MMM,yyyy").format(currentMonth)
                    homeFragmentBinding.tvDate.text = formattedMonth

                    view.setOnClickListener {

                        Log.d("HFGFGFFGGFFG", "setUpCalendar: $month")

                        if (day.owner == DayOwner.THIS_MONTH) {
                            selectDate(day.date)
                            formattedDate = day.date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

                        }
                    }
                }
            }

            class MonthViewContainer(view: View) : ViewContainer(view) {
                val legendLayout = Example3CalendarHeaderBinding.bind(view).legendLayout.root
            }

            homeFragmentBinding.exSevenCalendar.dayBinder = object : DayBinder<DayViewContainer> {
                override fun create(view: View) = DayViewContainer(view)

                override fun bind(container: DayViewContainer, day: CalendarDay) {
                    container.day = day
                    val textView = container.binding.exThreeDayText
                    textView.text = day.date.dayOfMonth.toString()

                    with(textView) {
                        setTextColor(resources.getColor(R.color.white))
                        setBackgroundColor(0x1A000000)
                    }

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
                                        setTextColor(resources.getColor(R.color.splash_text_color))
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
                            textView.setTextColor(resources.getColor(R.color.grey))
                        }
                    }


    //                Log.d("DOTTTTT", "bind: $datesWithDataTest")
    //
    //                if (datesWithDataTest.contains(day.date)) {
    //                    container.binding.dotLesson.backgroundTintList = ColorStateList.valueOf(Color.RED)
    //                    container.binding.dotLesson.visibility = View.VISIBLE
    //                } else {
    //                    container.binding.dotLesson.visibility = View.GONE
    //                }

                    val currentDate = LocalDate.now()
                    val firstDayOfWeek = currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
                    val lastDayOfWeek = currentDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY))

                    fun isWithinCurrentWeek(date: LocalDate): Boolean {
                        return !date.isBefore(firstDayOfWeek) && !date.isAfter(lastDayOfWeek)
                    }

                    fun applyDotIfWithinRange(
                        startDateStr: String?,
                        endDateStr: String?,
                        workloadColor: String?,
                        dotView: View,
                        defaultColor: Int
                    ) {
                        if (!startDateStr.isNullOrEmpty() && !endDateStr.isNullOrEmpty()) {
                            try {
                                val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                val startDate = LocalDate.parse(startDateStr, dateFormatter)
                                val endDate = LocalDate.parse(endDateStr, dateFormatter)

                                if (!day.date.isBefore(startDate) && !day.date.isAfter(endDate) && isWithinCurrentWeek(day.date)) {
                                    val color = try {
                                        if (!workloadColor.isNullOrEmpty()) Color.parseColor(workloadColor) else defaultColor
                                    } catch (e: IllegalArgumentException) {
                                        Log.e("ColorError", "Invalid color value: $workloadColor", e)
                                        defaultColor
                                    }
                                    dotView.backgroundTintList = ColorStateList.valueOf(color)
                                    dotView.visibility = View.VISIBLE
                                } else {
                                    dotView.visibility = View.GONE
                                }
                            } catch (e: Exception) {
                                Log.e("DateParsingError", "Error parsing dates: $startDateStr - $endDateStr", e)
                            }
                        } else {
                            dotView.visibility = View.GONE
                        }
                    }

                    Log.d("SLLSLSLSLS", "bind: $receivedworkloadColor     $receivedPreCompetitiveWorkloadColor   $receivedPreCompetitiveWorkloadColor")
//
//                    if (!receivedstartDate.isNullOrEmpty() && !receivedendDate.isNullOrEmpty()) {
//                        try {
//                            val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
//                            val startDate = LocalDate.parse(receivedstartDate, dateFormatter)
//                            val endDate = LocalDate.parse(receivedendDate, dateFormatter)
//
//                            if (!day.date.isBefore(startDate) && !day.date.isAfter(endDate)) {
//                                val color = try {
//                                    if (!receivedworkloadColor.isNullOrEmpty()) Color.parseColor(receivedworkloadColor) else Color.RED
//                                } catch (e: IllegalArgumentException) {
//                                    Log.e("ColorError", "Invalid color value: $receivedworkloadColor", e)
//                                    Color.RED
//                                }
//                                container.binding.dotLesson.backgroundTintList = ColorStateList.valueOf(color)
//                                container.binding.dotLesson.visibility = View.VISIBLE
//                            }
//                        } catch (e: Exception) {
//                            Log.e("DateParsingError", "Error parsing dates: $receivedstartDate - $receivedendDate", e)
//                        }
//                    }
//
//                    if (!receivedPreCompetitiveStartDate.isNullOrEmpty() && !receivedPreCompetitiveEndDate.isNullOrEmpty()) {
//                        try {
//                            val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
//                            val startDate = LocalDate.parse(receivedPreCompetitiveStartDate, dateFormatter)
//                            val endDate = LocalDate.parse(receivedPreCompetitiveEndDate, dateFormatter)
//
//                            if (!day.date.isBefore(startDate) && !day.date.isAfter(endDate)) {
//                                val color = try {
//                                    if (!receivedPreCompetitiveWorkloadColor.isNullOrEmpty()) Color.parseColor(receivedPreCompetitiveWorkloadColor) else Color.RED
//                                } catch (e: IllegalArgumentException) {
//                                    Log.e("ColorError", "Invalid color value: $receivedPreCompetitiveWorkloadColor", e)
//                                    Color.RED
//                                }
//                                container.binding.dotTest.backgroundTintList = ColorStateList.valueOf(color)
//                                container.binding.dotTest.visibility = View.VISIBLE
//                            }
//                        } catch (e: Exception) {
//                            Log.e("DateParsingError", "Error parsing dates: $receivedPreCompetitiveStartDate - $receivedPreCompetitiveEndDate", e)
//                        }
//                    }
//
//                    if (!receivedCompetitiveStartDate.isNullOrEmpty() && !receivedCompetitiveEndDate.isNullOrEmpty()) {
//                        try {
//                            val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
//                            val startDate = LocalDate.parse(receivedCompetitiveStartDate, dateFormatter)
//                            val endDate = LocalDate.parse(receivedCompetitiveEndDate, dateFormatter)
//
//                            if (!day.date.isBefore(startDate) && !day.date.isAfter(endDate)) {
//                                val color = try {
//                                    if (!receivedCompetitiveWorkloadColor.isNullOrEmpty()) Color.parseColor(receivedCompetitiveWorkloadColor) else Color.GRAY
//                                } catch (e: IllegalArgumentException) {
//                                    Log.e("ColorError", "Invalid color value: $receivedCompetitiveWorkloadColor", e)
//                                    Color.GRAY
//                                }
//                                container.binding.dotEvent.backgroundTintList = ColorStateList.valueOf(color)
//                                container.binding.dotEvent.visibility = View.VISIBLE
//                            }
//                        } catch (e: Exception) {
//                            Log.e("DateParsingError", "Error parsing dates: $receivedCompetitiveStartDate - $receivedCompetitiveEndDate", e)
//                        }
//                    }
//
//                    if (!receivedTransitionStartDate.isNullOrEmpty() && !receivedTransitionEndDate.isNullOrEmpty()) {
//                        try {
//                            val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
//                            val startDate = LocalDate.parse(receivedTransitionStartDate, dateFormatter)
//                            val endDate = LocalDate.parse(receivedTransitionEndDate, dateFormatter)
//
//                            if (!day.date.isBefore(startDate) && !day.date.isAfter(endDate)) {
//                                val color = try {
//                                    if (!receivedTransitionWorkloadColor.isNullOrEmpty()) Color.parseColor(receivedTransitionWorkloadColor) else Color.YELLOW
//                                } catch (e: IllegalArgumentException) {
//                                    Log.e("ColorError", "Invalid color value: $receivedTransitionWorkloadColor", e)
//                                    Color.YELLOW
//                                }
//                                container.binding.dotProgram.backgroundTintList = ColorStateList.valueOf(color)
//                                container.binding.dotProgram.visibility = View.VISIBLE
//                            }
//                        } catch (e: Exception) {
//                            Log.e("DateParsingError", "Error parsing dates: $receivedTransitionStartDate - $receivedTransitionEndDate", e)
//                        }
//                    }


                    val preSeasonStartDate = receivedstartDate?.toLocalDate()
                    val preSeasonEndDate = receivedendDate?.toLocalDate()
                    val preCompStartDate = receivedPreCompetitiveStartDate?.toLocalDate()
                    val preCompEndDate = receivedPreCompetitiveEndDate?.toLocalDate()
                    val CompStartDate = receivedCompetitiveStartDate?.toLocalDate()
                    val CompEndDate = receivedCompetitiveEndDate?.toLocalDate()

                    if (preSeasonStartDate == today || preSeasonEndDate == today) {
                        applyDotIfWithinRange(receivedstartDate, receivedendDate, receivedworkloadColor, container.binding.dotLesson, Color.GRAY)
                    }
                    if (preCompStartDate == today || preCompEndDate == today) {
                        Log.d("$$$$$$$$", "onResponse: $$$$$")
                        applyDotIfWithinRange(receivedPreCompetitiveStartDate, receivedPreCompetitiveEndDate, receivedPreCompetitiveWorkloadColor, container.binding.dotTest, Color.GRAY)
                    }

                    if (CompStartDate == today || CompEndDate == today) {
                        applyDotIfWithinRange(receivedCompetitiveStartDate, receivedCompetitiveEndDate, receivedCompetitiveWorkloadColor, container.binding.dotEvent, Color.GRAY)
                    }


//                    applyDotIfWithinRange(receivedTransitionStartDate, receivedTransitionEndDate, receivedTransitionWorkloadColor, container.binding.dotProgram, Color.GRAY)

                    container.view.setOnClickListener {
                        if (day.owner == DayOwner.THIS_MONTH) {
                            selectDate(day.date)

                            formattedDate = day.date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

                            val receivedIdInt = receivedIds.toIntOrNull()

                            Log.e("DJDJDJDJDJJDJDJDJ", "bind: $receivedIdInt")

                            val userType = preferenceManager.GetFlage()

                            if (userType == "Athlete") {
                                if (receivedIdInt != null && receivedIdInt != 0) {
                                    homeFragmentBinding.linerAthlete.visibility = View.VISIBLE

                                    Log.d("FORMATTEDDATE", "bind: ${formattedDate.toString()}")

                                    getPersonalDiaryData(formattedDate.toString())

//                                    fetchDayDataRecycler(day.date)

                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        "Please Select Group First",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            } else {
                                if (receivedIdInt != null && receivedIdInt != 0) {
                                    val intent =
                                        Intent(requireContext(), SelectDayActivity::class.java).apply {
                                            putExtra("id", receivedIdInt)
                                            putExtra("date", formattedDate)
                                        }
                                    context!!.startActivity(intent)
                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        "Please Select Group First",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                        }
                    }
                }
            }

            homeFragmentBinding.exSevenCalendar!!.monthScrollListener = { month ->
                // Format the month as "Month Year" (e.g., "January 2025")
    //                val formattedMonth = DateTimeFormatter.ofPattern("MMMM yyyy").format(month.yearMonth)
    //
    //                // Set the TextView with the formatted month and year
    //                homeFragmentBinding.tvDate.text = formattedMonth

                val currentDate = homeFragmentBinding.exSevenCalendar!!.findFirstVisibleDay()?.date ?: LocalDate.now()
                val formattedMonthYear = DateTimeFormatter.ofPattern("MMMM yyyy").format(currentDate)
                Log.d("CalendarLog", "Current month and year: $formattedMonthYear")
                homeFragmentBinding.tvDate.text = formattedMonthYear

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
            homeFragmentBinding.exSevenCalendar.monthHeaderBinder =
                object : MonthHeaderFooterBinder<MonthViewContainer> {
                    override fun create(view: View) = MonthViewContainer(view)

                    @SuppressLint("ResourceAsColor")
                    override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                        if (container.legendLayout.tag == null) {
                            container.legendLayout.tag = month.yearMonth

                            val formattedMonth =
                                DateTimeFormatter.ofPattern("MMM,yyyy").format(today.yearMonth)
                            homeFragmentBinding.tvDate.text = formattedMonth
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
    private fun fetchCurrentWeekDates() {
        val today = LocalDate.now()
        val firstDayOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val lastDayOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val weekDates = mutableListOf<String>()

        var date = firstDayOfWeek
        while (!date.isAfter(lastDayOfWeek)) {
            weekDates.add(date.format(formatter))
//            fetchDayDataRecycler(date)
            date = date.plusDays(1)
        }

        Log.d("WEEK_DATES", "Current week dates: $weekDates")
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

            val data = SelectedDaysModel.Data(
                lessons = listOf(), // Provide a valid list of lessons
                events = listOf(),  // Provide a valid list of events
                tests = listOf(),    // Provide a valid list of tests
                programs = listOf(),    // Provide a valid list of tests
            )
            checkDatesForMonth(LocalDate.now(), data)

            oldDate?.let { homeFragmentBinding.exSevenCalendar.notifyDateChanged(it) }
            homeFragmentBinding.exSevenCalendar.notifyDateChanged(date)
//                homeFragmentBinding.exSevenCalendar.scrollToMonth(YearMonth.from(date))
//                homeFragmentBinding.exSevenCalendar.smoothScrollToDate(date)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun daysOfWeekFromLocale(): List<DayOfWeek> {
        val firstDayOfWeek = DayOfWeek.SUNDAY
        val daysOfWeek = DayOfWeek.values()
        return daysOfWeek.slice(firstDayOfWeek.ordinal..daysOfWeek.lastIndex) +
                daysOfWeek.slice(0 until firstDayOfWeek.ordinal)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkDatesForMonth(selectedDate: LocalDate, data: SelectedDaysModel.Data) {
        val currentMonth = YearMonth.from(selectedDate)
        val daysInMonth = currentMonth.lengthOfMonth()

        Log.d("SSKSKSKKS", "checkDatesForMonth: $datesWithDataTest")
        for (day in 1..31) {
            if (day > daysInMonth) break

            val date = currentMonth.atDay(day)

            // Combine all date lists into one
            val allDates = mutableSetOf<String>().apply {
                addAll(data.lessons.map { it.date })
                addAll(data.events.map { it.date })
                addAll(data.tests.map { it.date })
            }

            // Update dateSet for the calendar
            updateDatesList(date, allDates, datesWithDataTest)
        }
    }

    private fun updateDatesList(
        date: LocalDate,
        dataDates: Set<String>,
        dateSet: MutableSet<LocalDate>
    ) {
        val dateStr = date.toString()

        Log.d("SSKSKSKKS", "checkDatesForMonth: $datesWithDataTest")

        if (dataDates.contains(dateStr)) {
            if (!dateSet.contains(date)) {
                dateSet.add(date)
                calendarView?.notifyDateChanged(date)
                Log.d("CheckedDates", "Date added: $date")
            }
        } else {

        }
    }

    private fun callGroupApi(receivedId: Int) {
        homeFragmentBinding.homeProgress.visibility = View.VISIBLE
        Log.d("API Call", "Received ID: $receivedId")

        apiInterface.GropList()?.enqueue(object : Callback<GroupListData?> {
            override fun onResponse(
                call: Call<GroupListData?>,
                response: Response<GroupListData?>
            ) {
                val code = response.code()
                if (code == 200) {
                    val resource = response.body()
                    val success = resource?.status ?: false
                    val message = resource?.message ?: "No message available"

                    if (success) {
                        homeFragmentBinding.homeProgress.visibility = View.GONE
                        val group = resource!!.data?.find { it.id == receivedId }

                        if (group != null) {
                            Log.d(
                                "API Response",
                                "Group found: ${group.name}, Group ID: ${group.id}"
                            )

                            saveSelectedGroupData(
                                group.name.toString(),
                                group.group_plannings?.firstOrNull()?.planning?.start_date ?: "",
                                group.id.toString()
                            )

                            homeFragmentBinding.selectGroupTxt.text = group.name

                            val planning = group.group_plannings?.firstOrNull()
                            if (planning != null && planning.planning != null) {
                                homeFragmentBinding.edtStartDate.text =
                                    planning.planning?.start_date
                            } else {
                                homeFragmentBinding.edtStartDate.text = ""
                            }
                        } else {
                            homeFragmentBinding.selectGroupTxt.text =
                                "Group not found for ID: $receivedId"
                        }
                    } else {
                        homeFragmentBinding.homeProgress.visibility = View.GONE
                        homeFragmentBinding.selectGroupTxt.text = "Failed to fetch data: $message"
                    }
                } else if (code == 403) {
                    homeFragmentBinding.homeProgress.visibility = View.GONE
                    Log.e("API Error", "Access forbidden: ${response.message()}")
                    call.cancel()
                    preferenceManager.setUserLogIn(false)
                } else {
                    homeFragmentBinding.homeProgress.visibility = View.GONE
                    Log.e("API Error", "Error code: $code, ${response.message()}")
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<GroupListData?>, t: Throwable) {
                Log.e("GROOOOOOOP", "API call failed: ${t.message}")
                homeFragmentBinding.homeProgress.visibility = View.GONE
                call.cancel()
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun String.toLocalDate(): LocalDate? {
        return try {
            LocalDate.parse(this)
        } catch (e: Exception) {
            null
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun callGroupApiAthlete(receivedId: Int) {
        homeFragmentBinding.homeProgress.visibility = View.VISIBLE
        Log.d("API Call", "Received ID: $receivedId")

        apiInterface.GropListAthlete()?.enqueue(object : Callback<GroupListAthlete?> {
            override fun onResponse(
                call: Call<GroupListAthlete?>,
                response: Response<GroupListAthlete?>
            ) {
                val code = response.code()
                if (code == 200) {
                    val resource = response.body()
                    val success = resource?.status ?: false
                    val message = resource?.message ?: "No message available"

                    if (success) {
                        homeFragmentBinding.homeProgress.visibility = View.GONE
                        val group = resource!!.data?.find { it.id == receivedId }

                        if (group != null) {
                            Log.d(
                                "API Response",
                                "Group found: ${group.group!!.name}, Group ID: ${group.id}"
                            )
                            saveSelectedGroupData(
                                group.group!!.name.toString(),
                                group.group!!.group_plannings?.firstOrNull()?.planning?.start_date ?: "",
                                group.id.toString()
                            )

                            homeFragmentBinding.selectGroupTxt.text = group.group!!.name

                            val planning = group.group!!.group_plannings?.firstOrNull()
                            if (planning != null && planning.planning != null) {
                            } else {
                                homeFragmentBinding.edtStartDate.text = ""
                            }


                            Log.d("SKSKSKSK", "onResponse: $today -- $receivedPreCompetitiveStartDate -- $receivedPreCompetitiveEndDate")

                            val preSeasonStartDate = receivedstartDate?.toLocalDate()
                            val preSeasonEndDate = receivedendDate?.toLocalDate()
                            val preCompStartDate = receivedPreCompetitiveStartDate?.toLocalDate()
                            val preCompEndDate = receivedPreCompetitiveEndDate?.toLocalDate()
                            val CompStartDate = receivedCompetitiveStartDate?.toLocalDate()
                            val CompEndDate = receivedCompetitiveEndDate?.toLocalDate()

                            Log.d("SKSKSKSK", "onResponse: $today -- $receivedPreCompetitiveStartDate -- $receivedPreCompetitiveEndDate")
                            if (preSeasonStartDate == today || preSeasonEndDate == today) {
                                initPlanningDataAthlete(group.group!!.group_plannings,"ToDayPreSeason")
                            }
                            if (preCompStartDate == today || preCompEndDate == today) {
                                Log.d("$$$$$$$$", "onResponse: $$$$$")
                                initPlanningDataAthlete(group.group!!.group_plannings, "ToDayPreCompetitive")
                            }

                            if (CompStartDate == today || CompEndDate == today) {
                                initPlanningDataAthlete(group.group!!.group_plannings,"ToDayCompetitive")
                            }

                            initLessonDataAthlete(group.group!!.group_lessions)
                            initEventDataAthlete(group.group!!.group_events)
                            initTestDataAthlete(group.group!!.group_tests)

                        } else {
                            homeFragmentBinding.selectGroupTxt.text = "Group not found for ID: $receivedId"
                        }
                    } else {
                        homeFragmentBinding.homeProgress.visibility = View.GONE
                        homeFragmentBinding.selectGroupTxt.text = "Failed to fetch data: $message"
                    }
                } else if (code == 403) {
                    homeFragmentBinding.homeProgress.visibility = View.GONE
                    Log.e("API Error", "Access forbidden: ${response.message()}")
                    call.cancel()
                    preferenceManager.setUserLogIn(false)
                } else {
                    homeFragmentBinding.homeProgress.visibility = View.GONE
                    Log.e("API Error", "Error code: $code, ${response.message()}")
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<GroupListAthlete?>, t: Throwable) {
                Log.e("GROOOOOOOP", "API call failed: ${t.message}")
                homeFragmentBinding.homeProgress.visibility = View.GONE
                call.cancel()
            }
        })
    }


    private fun saveSelectedGroupData(groupName: String, startDate: String, id: String) {
        Log.d("Save Group Data", "Saving group data: $groupName, $startDate, $id")
        val sharedPreferences =
            requireContext().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("selectedGroupName", groupName)
        editor.putString("selectedStartDate", startDate)
        editor.putString("selectedId", id)
        editor.apply()

        val savedId = sharedPreferences.getString("selectedId", null)
        Log.d("Saved Data", "Saved Group ID: $savedId")
    }

    private fun getSavedGroupData(): Triple<String?, String?, String?> {
        val sharedPreferences =
            requireContext().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val groupName = sharedPreferences.getString("selectedGroupName", null)
        val startDate = sharedPreferences.getString("selectedStartDate", null)
        val id = sharedPreferences.getString("selectedId", null)  // Retrieve the id
        return Triple(groupName, startDate, id)
    }

    private fun clearSavedGroupData() {
        Log.d("Clear Data", "Clearing saved group data")
        val sharedPreferences =
            requireContext().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("selectedGroupName")
        editor.remove("selectedStartDate")
        editor.remove("selectedId")
        editor.apply()
    }


    private fun setDrawerToggle() {
        actionBarDrawerToggle = ActionBarDrawerToggle(
            requireActivity(),
            homeFragmentBinding.drawerLayout,
            R.string.nav_open,
            R.string.nav_close
        )

        homeFragmentBinding.drawerLayout.addDrawerListener(actionBarDrawerToggle!!)
        actionBarDrawerToggle?.syncState()
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        homeFragmentBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        homeFragmentBinding.navigationView.setNavigationItemSelectedListener(this)

    }

    private fun initTestDataAthlete(testData: ArrayList<GroupListAthlete.GroupTest>?) {
        try {
            val data = testData ?: ArrayList<GroupListAthlete.GroupTest>()
            homeFragmentBinding.favTestRly.layoutManager = LinearLayoutManager(requireContext())
            testAdapterAthlete = TestAdapterAthlete(data, requireContext(), this,"Home")
            homeFragmentBinding.favTestRly.adapter = testAdapterAthlete
        } catch (e: Exception) {
            Log.d("catch", "callGroupApiAthlete: ${e.message.toString()}")
        }
    }

    private fun initEventDataAthlete(eventData: ArrayList<GroupListAthlete.GroupEvents>?) {
        try {
            val data = eventData ?: ArrayList<GroupListAthlete.GroupEvents>()
            homeFragmentBinding.favEventRly.layoutManager = LinearLayoutManager(requireContext())
            eventAdapterAthlete = EventAdapterAthlete(data, requireContext(), this)
            homeFragmentBinding.favEventRly.adapter = eventAdapterAthlete
        } catch (e: Exception) {
            Log.d("catch", "callGroupApiAthlete: ${e.message.toString()}")
        }
    }

    private fun initLessonDataAthlete(lessonData: ArrayList<GroupListAthlete.GroupLesson>?) {
        try {
            val data = lessonData ?: ArrayList<GroupListAthlete.GroupLesson>()

            homeFragmentBinding.favLessonRly.layoutManager = LinearLayoutManager(requireContext())
            lessonAdapterAthlete = LessonAdapterAthlete(data, requireContext(), this,"Home")
            homeFragmentBinding.favLessonRly.adapter = lessonAdapterAthlete
        } catch (e: Exception) {
            Log.d("catch", "callGroupApiAthlete: ${e.message.toString()}")
        }
    }

    private fun initPlanningDataAthlete(data: ArrayList<GroupListAthlete.GroupPlanning>?,type:String) {
        try {
            Log.d("ARARARR", "initPlanningDataAthlete: $type")
            homeFragmentBinding.favPlanningRly.layoutManager = LinearLayoutManager(requireContext())
            adapterAthelete = PlanningAdapterAthleteHome(data, requireContext(), this, type)
            homeFragmentBinding.favPlanningRly.adapter = adapterAthelete
        } catch (e: Exception) {
            Log.d("ARARARR", "initPlanningDataAthlete: $type")

            Log.d("catch", "callGroupApiAthlete: ${e.message.toString()}")
        }
    }

    private fun initViews() {
        apiClient = APIClient(requireContext())
        preferenceManager = PreferencesManager(requireContext())
        Log.d("Login Token", preferenceManager.getToken()!!)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        Sportlist = ArrayList()
        WorkOutlist = ArrayList()
        LessonData = ArrayList()

        val sharedPreferences = requireActivity().getSharedPreferences("AppPreferences", MODE_PRIVATE)
        receivedIds = sharedPreferences.getString("id", "default_value") ?: ""

        receivedGroup_Ids = sharedPreferences.getString("group_id", "default_value") ?: ""

        receivedname = sharedPreferences.getString("name", null)
        receivedstartDate = sharedPreferences.getString("start_date", null)
        receivedendDate = sharedPreferences.getString("end_date", null)
        receivedmesocycle = sharedPreferences.getString("mesocycle", null)
        receivedworkloadColor = sharedPreferences.getString("workload_color", null)

        receivedPreCompetitiveName = sharedPreferences.getString("preCompetitiveName", null)
        receivedPreCompetitiveStartDate = sharedPreferences.getString("preCompetitiveStartDate", null)
        receivedPreCompetitiveEndDate = sharedPreferences.getString("preCompetitiveEndDate", null)
        receivedPreCompetitiveMesocycle = sharedPreferences.getString("preCompetitiveMesocycle", null)
        receivedPreCompetitiveWorkloadColor = sharedPreferences.getString("preCompetitiveWorkloadColor", null)


        receivedCommpetitiveName = sharedPreferences.getString("CompetitiveName", null)
        receivedCompetitiveStartDate = sharedPreferences.getString("CompetitiveStartDate", null)
        receivedCompetitiveEndDate = sharedPreferences.getString("CompetitiveEndDate", null)
        receivedCompetitiveMesocycle = sharedPreferences.getString("CompetitiveMesocycle", null)
        receivedCompetitiveWorkloadColor = sharedPreferences.getString("CompetitiveWorkloadColor", null)

        receivedTransitionName = sharedPreferences.getString("TransitionName", null)
        receivedTransitionStartDate = sharedPreferences.getString("TransitionStartDate", null)
        receivedTransitionEndDate = sharedPreferences.getString("TransitionEndDate", null)
        receivedTransitionMesocycle = sharedPreferences.getString("TransitionMesocycle", null)
        receivedTransitionWorkloadColor = sharedPreferences.getString("TransitionWorkloadColor", null)

        Log.d("CalenderFragment", "Received ID from SharedPreferences: $receivedIds")
    }

    private fun initRecyclerView(user: ArrayList<Sport_list>) {
        homeFragmentBinding.viewRecycler.layoutManager = LinearLayoutManager(requireActivity())
        adapter = HomeAdapter(user, requireContext())
        homeFragmentBinding.viewRecycler.adapter = adapter
    }

    private fun initView(workOutlist: ArrayList<Work_Out>) {

        homeFragmentBinding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, true)

        val customOrder = listOf(2, 1, 3)

        val reorderedList = ArrayList<Work_Out>().apply {
            for (index in customOrder) {
                if (index - 1 in workOutlist.indices) { // Adjust index for 0-based indexing
                    add(workOutlist[index - 1])
                }
            }
        }

        workoutadapter = WorkOutAdapter(reorderedList, requireContext(), this)
        homeFragmentBinding.recyclerView.adapter = workoutadapter


//
//            newsadapter = NewsAdapter(newsData, requireContext(), this)
//            homeFragmentBinding.recyclerNews.layoutManager = LinearLayoutManager(requireContext())
//            homeFragmentBinding.recyclerView.adapter = adapter2
    }

    private fun GetNews() {
        try {
            apiInterface.GetNews()?.enqueue(object : Callback<NewsModel> {
                override fun onResponse(call: Call<NewsModel>, response: Response<NewsModel>) {
                    Log.d("APIResponse", "Response Code: ${response.code()}")

                    when (response.code()) {
                        200 -> {
                            newsData.clear()
                            response.body()?.data?.let {
                                newsData.addAll(it)
                                Log.d("APIResponse", "Data added: $it")
                            } ?: run {
                                Log.d("APIResponse", "Response data is null")
                                Toast.makeText(requireContext(), "No data found", Toast.LENGTH_SHORT).show()
                            }

                            if (newsData.isNotEmpty()) {
                                setAdapter()
                            } else {
                                Log.d("APIResponse", "Response data is empty")
                                Toast.makeText(
                                    requireContext(),
                                    "No data found",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        403 -> {
                            Utils.setUnAuthDialog(requireContext())
                        }

                        else -> {
                            val errorMessage = response.message() ?: "Unknown error occurred"
                            Log.d("APIResponse", "Error: $errorMessage")
                            Log.d("connection", "canction:- $errorMessage")
                        }
                    }
                }

                override fun onFailure(call: Call<NewsModel>, t: Throwable) {
                    Log.e("APIError", "Network error: ${t.message}")
                    Log.d("connection", "canction:- ${t.message}")

                }
            })
        } catch (e: Exception) {
            Log.e("APIException", "Exception: ${e.message}")
        }
    }

    private fun setAdapter() {
        context?.let { context ->
            newsadapter = NewsAdapter(newsData, context, this)
            homeFragmentBinding.recyclerNews.layoutManager = LinearLayoutManager(context)
            homeFragmentBinding.recyclerNews.adapter = newsadapter
        }
    }

    private fun getInstraction() {
        try {
            apiInterface.GetInstraction()?.enqueue(object : Callback<privacypolicy> {
                override fun onResponse(
                    call: Call<privacypolicy>,
                    response: Response<privacypolicy>
                ) {
                    Log.d("APIResponse", "Response Code: ${response.code()}")

                    when (response.code()) {
                        200 -> {
                            instractionData = response.body()?.data
                            if (instractionData != null) {
                                bindDataToUI(instractionData!!)
                            } else {
                                Log.d("APIResponse", "Response data is null")
                                Toast.makeText(requireContext(), "No data found", Toast.LENGTH_SHORT).show()
                            }
                        }

                        429 -> {
                            Toast.makeText(requireContext(), "Too Many Request", Toast.LENGTH_SHORT).show()
                        }

                        403 -> {
                            Utils.setUnAuthDialog(requireContext())
                        }

                        else -> {
                            val errorMessage = response.message() ?: "Unknown error occurred"
                            Log.d("APIResponses", "Error: $errorMessage")
                            Log.d("connections", "canction in er:- $errorMessage")

                        }
                    }
                }

                override fun onFailure(call: Call<privacypolicy>, t: Throwable) {
                    Log.e("APIErrorss", "Network error: ${t.message}")
                    Log.d("connectionss", "canction in fai:- ${t.message}")

                }
            })
        } catch (e: Exception) {
            Log.e("APIException", "Exception: ${e.message}")

        }
    }

    private fun bindDataToUI(data: privacypolicy.Data) {
        val descriptionHtml = data.description ?: "No Description Available"

        val descriptionPlainText =
            HtmlCompat.fromHtml(descriptionHtml, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()

        homeFragmentBinding.descriptionPp.text = descriptionPlainText
    }


    private fun getPersonalDiaryData(date: String) {
        try {
            apiInterface.GetPersonalDiaryData(date)
                ?.enqueue(object : Callback<GetDiaryDataForEdit> {
                    override fun onResponse(
                        call: Call<GetDiaryDataForEdit>,
                        response: Response<GetDiaryDataForEdit>
                    ) {
                        Log.d("TAG", "Response code: ${response.code()}")

                        when (response.code()) {
                            200 -> {
                                response.body()?.let { responseBody ->
                                    val diaryData = responseBody.data

                                    if (diaryData != null) {
                                        Log.d("DATA", "Date: ${diaryData.date}")

                                        // Access the personal_dairie_detaile list
                                        val personalDiaryDetails = diaryData.personalDiaryDetails
                                        personalDiaryDetails?.forEach { detail ->
                                            Log.d(
                                                "DETAIL",
                                                "Assess Level: ${detail.assessYourLevelOf}, " +
                                                        "Before: ${detail.beforeTraining}, " +
                                                        "During: ${detail.duringTraining}, " +
                                                        "After: ${detail.afterTraining}"
                                            )
                                        }

                                        SetData(diaryData)
                                    } else {
                                        Log.e("ERROR", "Data is null")

                                    }
                                } ?: run {
                                    Log.e("ERROR", "Response body is null: $response")
                                }
                            }

                            403 -> {
                                Utils.setUnAuthDialog(requireContext())
                            }

                            429 -> {
                                Toast.makeText(
                                    requireContext(),
                                    "Too Many Request",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            else -> {
                                Log.e("ERROR", "Error: ${response.message()}")
                                Toast.makeText(
                                    requireContext(),
                                    "Error: ${response.message()}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                call.cancel()
                            }
                        }
                    }

                    override fun onFailure(call: Call<GetDiaryDataForEdit>, t: Throwable) {
                        Log.e("Error", "Network Error: ${t.message}")
                        Toast.makeText(
                            requireContext(),
                            "Network error: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        } catch (e: Exception) {
            Log.e("error", "Exception: ${e.message}")
            Toast.makeText(
                requireContext(),
                "Unexpected error: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun SetData(data: GetDiaryDataForEdit.Data) {
        // Set basic details
//        binding.dateTextView.text = data.date ?: ""
//        binding.sleepHoursTextView.text = data.sleepHours ?: ""
        homeFragmentBinding.NutritionAndHydration.setText(data.nutritionAndHydration ?: "")
        homeFragmentBinding.Tiredness.setText(data.notes ?: "")

        if (data.personalDiaryDetails.isNullOrEmpty() || data.personalDiaryDetails.equals("0")) {
            // Set all values to empty string if no details available
            homeFragmentBinding.EnergyBT.setText("")
            homeFragmentBinding.EnergyDT.setText("")
            homeFragmentBinding.EnergyAT.setText("")

            homeFragmentBinding.SatisfationBT.setText("")
            homeFragmentBinding.SatisfationDT.setText("")
            homeFragmentBinding.SatisfationAT.setText("")

            homeFragmentBinding.HapinessBT.setText("")
            homeFragmentBinding.HapinessDT.setText("")
            homeFragmentBinding.HapinessAT.setText("")

            homeFragmentBinding.IrritabilityBT.setText("")
            homeFragmentBinding.IrritabilityDT.setText("")
            homeFragmentBinding.IrritabilityAT.setText("")

            homeFragmentBinding.DeterminationBT.setText("")
            homeFragmentBinding.DeterminationDT.setText("")
            homeFragmentBinding.DeterminationAT.setText("")

            homeFragmentBinding.AnxietyBT.setText("")
            homeFragmentBinding.AnxietyDT.setText("")
            homeFragmentBinding.AnxietyAT.setText("")

            homeFragmentBinding.TirednessBT.setText("")
            homeFragmentBinding.TirednessDT.setText("")
            homeFragmentBinding.TirednessAT.setText("")
        } else {
            // Iterate through personal diary details list and set each value as needed
            data.personalDiaryDetails.forEach { detail ->
                when (detail.assessYourLevelOf) {
                    "Energy" -> {
                        homeFragmentBinding.EnergyBT.setText(
                            detail.beforeTraining ?: ""
                        ) // Use setText for EditText
                        homeFragmentBinding.EnergyDT.setText(
                            detail.duringTraining ?: ""
                        ) // Use setText for EditText
                        homeFragmentBinding.EnergyAT.setText(
                            detail.afterTraining ?: ""
                        ) // Use setText for EditText
                    }

                    "Satisfaction" -> {
                        homeFragmentBinding.SatisfationBT.setText(
                            detail.beforeTraining ?: ""
                        ) // Use setText for EditText
                        homeFragmentBinding.SatisfationDT.setText(
                            detail.duringTraining ?: ""
                        ) // Use setText for EditText
                        homeFragmentBinding.SatisfationAT.setText(
                            detail.afterTraining ?: ""
                        ) // Use setText for EditText
                    }

                    "Happiness" -> {
                        homeFragmentBinding.HapinessBT.setText(
                            detail.beforeTraining ?: ""
                        ) // Use setText for EditText
                        homeFragmentBinding.HapinessDT.setText(
                            detail.duringTraining ?: ""
                        ) // Use setText for EditText
                        homeFragmentBinding.HapinessAT.setText(
                            detail.afterTraining ?: ""
                        ) // Use setText for EditText
                    }

                    "Irritability" -> {
                        homeFragmentBinding.IrritabilityBT.setText(
                            detail.beforeTraining ?: ""
                        ) // Use setText for EditText
                        homeFragmentBinding.IrritabilityDT.setText(
                            detail.duringTraining ?: ""
                        ) // Use setText for EditText
                        homeFragmentBinding.IrritabilityAT.setText(
                            detail.afterTraining ?: ""
                        ) // Use setText for EditText
                    }

                    "Determination" -> {
                        homeFragmentBinding.DeterminationBT.setText(
                            detail.beforeTraining ?: ""
                        ) // Use setText for EditText
                        homeFragmentBinding.DeterminationDT.setText(
                            detail.duringTraining ?: ""
                        ) // Use setText for EditText
                        homeFragmentBinding.DeterminationAT.setText(
                            detail.afterTraining ?: ""
                        ) // Use setText for EditText
                    }

                    "Anxiety" -> {
                        homeFragmentBinding.AnxietyBT.setText(
                            detail.beforeTraining ?: ""
                        ) // Use setText for EditText
                        homeFragmentBinding.AnxietyDT.setText(
                            detail.duringTraining ?: ""
                        ) // Use setText for EditText
                        homeFragmentBinding.AnxietyAT.setText(
                            detail.afterTraining ?: ""
                        ) // Use setText for EditText
                    }

                    "Tiredness" -> {
                        homeFragmentBinding.TirednessBT.setText(
                            detail.beforeTraining ?: ""
                        ) // Use setText for EditText
                        homeFragmentBinding.TirednessDT.setText(
                            detail.duringTraining ?: ""
                        ) // Use setText for EditText
                        homeFragmentBinding.TirednessAT.setText(
                            detail.afterTraining ?: ""
                        ) // Use setText for EditText
                    }
                }
            }
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.tv_notification) {
            homeFragmentBinding.drawerLayout.closeDrawer(GravityCompat.START)
            val intent = Intent(requireContext(), NotificationActivity::class.java)
            startActivity(intent)
            return true
        } else if (item.itemId == R.id.tv_policy) {
            homeFragmentBinding.drawerLayout.closeDrawer(GravityCompat.START)
            val intent = Intent(requireContext(), PrivacyPolicyActivity::class.java)
            startActivity(intent)
            return true
        }
        //        else if (item.itemId == R.id.tv_invited_friend) {
        //            homeFragmentBinding.drawerLayout.closeDrawer(GravityCompat.START)
        //            val intent = Intent(requireContext(), Invite_friendActivity::class.java)
        //            startActivity(intent)
        //            return true
        //        }
        else if (item.itemId == R.id.tv_library) {
            homeFragmentBinding.drawerLayout.closeDrawer(GravityCompat.START)
            val intent = Intent(requireContext(), LibraryActivity::class.java)
            startActivity(intent)
            return true
        } else if (item.itemId == R.id.tv_favorite) {
            homeFragmentBinding.drawerLayout.closeDrawer(GravityCompat.START)
            val intent = Intent(requireContext(), FavoriteActivity::class.java)
            startActivity(intent)
            return true
        } else if (item.itemId == R.id.tv_athletes) {
            homeFragmentBinding.drawerLayout.closeDrawer(GravityCompat.START)
            val intent = Intent(requireContext(), AthletesActivity::class.java)
            startActivity(intent)
            return true
        } else if (item.itemId == R.id.tv_view_analysis) {
            homeFragmentBinding.drawerLayout.closeDrawer(GravityCompat.START)
            val intent = Intent(requireContext(), ViewAnalysisActivity::class.java)
            startActivity(intent)
            return true
        } else if (item.itemId == R.id.tv_profile) {
            val intent = Intent(requireContext(), PerformanceProfileActivity::class.java)
            startActivity(intent)
            homeFragmentBinding.drawerLayout.closeDrawer(GravityCompat.START)
            return true
        } else if (item.itemId == R.id.tv_analysis) {
            homeFragmentBinding.drawerLayout.closeDrawer(GravityCompat.START)
            preferenceManager.setSelectEvent(false)
            val intent = Intent(requireContext(), CompetitionActivity::class.java)
            startActivity(intent)
            //Toast.makeText(requireContext(), "Competition Analysis", Toast.LENGTH_SHORT).show()
            return true
        } else if (item.itemId == R.id.tv_personal_diary) {
            homeFragmentBinding.drawerLayout.closeDrawer(GravityCompat.START)
            val intent = Intent(requireContext(), ViewPersonalDiaryActivity::class.java)
            startActivity(intent)
            //          Toast.makeText(requireContext(), "Personal Diary", Toast.LENGTH_SHORT).show()
            return true
        } else if (item.itemId == R.id.tv_setting) {
            homeFragmentBinding.drawerLayout.closeDrawer(GravityCompat.START)
            val intent = Intent(requireContext(), SettingActivity::class.java)
            startActivity(intent)
            return true
        } else if (item.itemId == R.id.tv_remind) {
            homeFragmentBinding.drawerLayout.closeDrawer(GravityCompat.START)
            val intent = Intent(requireContext(), ViewRemindMeActivity::class.java)
            startActivity(intent)
            return true
        } else if (item.itemId == R.id.logout) {
            homeFragmentBinding.drawerLayout.closeDrawer(GravityCompat.START)

            homeFragmentBinding.homeProgress.visibility = View.VISIBLE
            apiInterface.LogOut()?.enqueue(object : Callback<RegisterData?> {
                override fun onResponse(
                    call: Call<RegisterData?>,
                    response: Response<RegisterData?>
                ) {
                    homeFragmentBinding.homeProgress.visibility = View.GONE
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
                            Toast.LENGTH_SHORT).show()
                        call.cancel()
                        //                        preferenceManager.setUserLogIn(false)
                        //                        val intent = Intent(requireContext(), MainActivity::class.java)
                        //                        st artActivity(intent)
                        //                        requireActivity().finish()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Toast.makeText(requireContext(), "" + t.message, Toast.LENGTH_SHORT).show()
                    call.cancel()
                    homeFragmentBinding.homeProgress.visibility = View.GONE
                }
            })

            return true
        } else {
            return false
        }
    }

    @SuppressLint("NewApi")
    private fun loadData() {
        selectDate(LocalDate.now())

        val userType = preferenceManager.GetFlage()

        if (userType == "Athlete") {
//            fetchDayDataRecycler(LocalDate.now())
            val receivedIdInt = receivedIds.toIntOrNull()
            if (receivedIdInt != null) {
                callGroupApiAthlete(receivedIdInt)
                Log.e("APIII Call", "Invalid receivedId: $receivedIds")

            } else {
                Log.e("API Call", "Invalid receivedId: $receivedIds")
                clearSavedGroupData()
            }
        }else {
        }
    }

    override fun onItemClicked(view: View, position: Int, type: Long, string: String) {

        val userType = preferenceManager.GetFlage()

        Log.d("SSKKSKS", "onItemClicked: ${string}")

        if (string == "click") {

            if (userType == "Athlete" && string == "click") {
                homeFragmentBinding.linerAthlete.visibility = View.VISIBLE

                Log.d("SSKKSKS", "onItemClicked: $position")

                when (position) {
                    0 -> {
                        homeFragmentBinding.informationLinear.visibility = View.GONE
                        homeFragmentBinding.InstructionLinera.visibility = View.GONE
                        homeFragmentBinding.NewsLinera.visibility = View.VISIBLE
                        homeFragmentBinding.linerAthlete.visibility = View.GONE
                    }

                    1 -> {
                        homeFragmentBinding.informationLinear.visibility = View.GONE
                        homeFragmentBinding.InstructionLinera.visibility = View.VISIBLE
                        homeFragmentBinding.NewsLinera.visibility = View.GONE
                        homeFragmentBinding.linerAthlete.visibility = View.GONE
                    }

                    2 -> {
                        homeFragmentBinding.informationLinear.visibility = View.VISIBLE
                        homeFragmentBinding.InstructionLinera.visibility = View.GONE
                        homeFragmentBinding.NewsLinera.visibility = View.GONE
                        homeFragmentBinding.linerAthlete.visibility = View.VISIBLE
                    }
                }
            } else {
                homeFragmentBinding.linerAthlete.visibility = View.GONE

                when (position) {
                    0 -> {
                        homeFragmentBinding.informationLinear.visibility = View.GONE
                        homeFragmentBinding.InstructionLinera.visibility = View.GONE
                        homeFragmentBinding.NewsLinera.visibility = View.VISIBLE
                        homeFragmentBinding.linerAthlete.visibility = View.GONE
                    }

                    1 -> {
                        homeFragmentBinding.informationLinear.visibility = View.GONE
                        homeFragmentBinding.InstructionLinera.visibility = View.VISIBLE
                        homeFragmentBinding.NewsLinera.visibility = View.GONE
                        homeFragmentBinding.linerAthlete.visibility = View.GONE
                    }

                    2 -> {
                        homeFragmentBinding.informationLinear.visibility = View.VISIBLE
                        homeFragmentBinding.InstructionLinera.visibility = View.GONE
                        homeFragmentBinding.NewsLinera.visibility = View.GONE
                        homeFragmentBinding.linerAthlete.visibility = View.VISIBLE
                    }
                }
            }
        }


        if (string == "fav") {
            try {
                homeFragmentBinding.homeProgress.visibility = View.VISIBLE
                val id: MultipartBody.Part =
                    MultipartBody.Part.createFormData("id", type.toInt().toString())
                apiInterface.Favourite_lession(id)?.enqueue(object : Callback<RegisterData?> {
                    @RequiresApi(Build.VERSION_CODES.O)
                    override fun onResponse(
                        call: Call<RegisterData?>,
                        response: Response<RegisterData?>
                    ) {
                        homeFragmentBinding.homeProgress.visibility = View.GONE
                        Log.d("TAG", response.code().toString() + "")
                        val code = response.code()
                        if (code == 200) {
                            val resource: RegisterData? = response.body()
                            val Success: Boolean = resource?.status!!
                            val Message: String = resource.message!!
                            if (Success) {
                                homeFragmentBinding.homeProgress.visibility = View.GONE
                                loadData()
                            } else {
                                homeFragmentBinding.homeProgress.visibility = View.GONE
                                Toast.makeText(requireContext(), "" + Message, Toast.LENGTH_SHORT).show()
                            }
                        } else if (code == 403) {
                            Utils.setUnAuthDialog(requireContext())
                        } else {
                            homeFragmentBinding.homeProgress.visibility = View.GONE
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
                        homeFragmentBinding.homeProgress.visibility = View.GONE
                        Toast.makeText(requireContext(), "" + t.message, Toast.LENGTH_SHORT)
                            .show()
                        call.cancel()
                    }
                })
            }catch (e:Exception){
                Log.d("catch", "callGroupApiAthlete: ${e.message.toString()}")
            }
        } else if (string == "unfav") {
            try{
                homeFragmentBinding.homeProgress.visibility = View.VISIBLE
                val id: MultipartBody.Part = MultipartBody.Part.createFormData("id", type.toInt().toString())
                apiInterface.DeleteFavourite_lession(type.toInt())
                    ?.enqueue(object : Callback<RegisterData?> {
                        @RequiresApi(Build.VERSION_CODES.O)
                        override fun onResponse(
                            call: Call<RegisterData?>,
                            response: Response<RegisterData?>
                        ) {
                            Log.d("TAG", response.code().toString() + "")
                            homeFragmentBinding.homeProgress.visibility = View.GONE
                            val code = response.code()
                            if (code == 200) {
                                val resource: RegisterData? = response.body()
                                val Success: Boolean = resource?.status!!
                                val Message: String = resource.message!!
                                if (Success) {
                                    loadData()
                                    homeFragmentBinding.homeProgress.visibility = View.GONE
                                } else {
                                    homeFragmentBinding.homeProgress.visibility = View.GONE
                                    Toast.makeText(requireContext(), "" + Message, Toast.LENGTH_SHORT).show()
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
                            homeFragmentBinding.homeProgress.visibility = View.GONE
                            Toast.makeText(
                                requireContext(),
                                "" + t.message,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            call.cancel()
                        }
                    })
            }catch (e:Exception){
                Log.d("catch", "callGroupApiAthlete: ${e.message.toString()}")
            }
        } else if (string == "favtest"){
            homeFragmentBinding.homeProgress.visibility = View.VISIBLE
            val id: MultipartBody.Part = MultipartBody.Part.createFormData("id", type.toInt().toString())
            apiInterface.Favourite_Test(id)?.enqueue(object : Callback<RegisterData?> {
                override fun onResponse(
                    call: Call<RegisterData?>,
                    response: Response<RegisterData?>
                ) {
                    homeFragmentBinding.homeProgress.visibility = View.GONE
                    Log.d("TAG", response.code().toString() + "")
                    val code = response.code()
                    if (code == 200) {
                        val resource: RegisterData? = response.body()
                        val Success: Boolean = resource?.status!!
                        val Message: String = resource.message!!
                        if (Success) {
                            homeFragmentBinding.homeProgress.visibility = View.GONE
                            loadData()
                        } else {
                            homeFragmentBinding.homeProgress.visibility = View.GONE
                            Toast.makeText(requireContext(), "" + Message, Toast.LENGTH_SHORT).show()
                        }
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(requireContext())
                    } else {
                        homeFragmentBinding.homeProgress.visibility = View.GONE
                        Toast.makeText(
                            requireContext(),
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        ).show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    homeFragmentBinding.homeProgress.visibility = View.GONE
                    Toast.makeText(requireContext(), "" + t.message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }
            })
        } else if (string == "unfavtest") {
            homeFragmentBinding.homeProgress.visibility = View.VISIBLE
            var id: MultipartBody.Part =
                MultipartBody.Part.createFormData("id", type.toInt().toString())
            apiInterface.DeleteFavourite_Test(type.toInt())
                ?.enqueue(object : Callback<RegisterData?> {
                    override fun onResponse(
                        call: Call<RegisterData?>,
                        response: Response<RegisterData?>
                    ) {
                        Log.d("TAG", response.code().toString() + "")
                        homeFragmentBinding.homeProgress.visibility = View.GONE
                        val code = response.code()
                        if (code == 200) {
                            val resource: RegisterData? = response.body()
                            val Success: Boolean = resource?.status!!
                            val Message: String = resource.message!!
                            if (Success) {
                                homeFragmentBinding.homeProgress.visibility = View.GONE
                                loadData()
                            } else {
                                homeFragmentBinding.homeProgress.visibility = View.GONE
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
                        homeFragmentBinding.homeProgress.visibility = View.GONE
                        Toast.makeText(
                            requireContext(),
                            "" + t.message,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                })
        } else if (string == "favevent"){
            homeFragmentBinding.homeProgress.visibility = View.VISIBLE
            val id: MultipartBody.Part =
                MultipartBody.Part.createFormData("id", type.toInt().toString())
            apiInterface.Favourite_Event(id)?.enqueue(object : Callback<RegisterData?> {
                override fun onResponse(
                    call: Call<RegisterData?>,
                    response: Response<RegisterData?>
                ) {
                    homeFragmentBinding.homeProgress.visibility = View.GONE
                    Log.d("TAG", response.code().toString() + "")
                    val code = response.code()
                    if (code == 200) {
                        val resource: RegisterData? = response.body()
                        val Success: Boolean = resource?.status!!
                        val Message: String = resource.message!!
                        if (Success) {
                            homeFragmentBinding.homeProgress.visibility = View.GONE
                            loadData()
                        } else {
                            homeFragmentBinding.homeProgress.visibility = View.GONE
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
                        homeFragmentBinding.homeProgress.visibility = View.GONE
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
                    homeFragmentBinding.homeProgress.visibility = View.GONE
                    Toast.makeText(requireContext(), "" + t.message, Toast.LENGTH_SHORT).show()
                    call.cancel()
                }
            })
        } else if (string == "unfavevent") {
            homeFragmentBinding.homeProgress.visibility = View.VISIBLE
            val id: MultipartBody.Part =
                MultipartBody.Part.createFormData("id", type.toInt().toString())
            apiInterface.DeleteFavourite_Event(type.toInt())
                ?.enqueue(object : Callback<RegisterData?> {
                    override fun onResponse(
                        call: Call<RegisterData?>,
                        response: Response<RegisterData?>
                    ) {
                        Log.d("TAG", response.code().toString() + "")
                        homeFragmentBinding.homeProgress.visibility = View.GONE
                        val code = response.code()
                        if (code == 200) {
                            val resource: RegisterData? = response.body()
                            val Success: Boolean = resource?.status!!
                            val Message: String = resource.message!!
                            if (Success) {
                                homeFragmentBinding.homeProgress.visibility = View.GONE
                                loadData()
                            } else {
                                homeFragmentBinding.homeProgress.visibility = View.GONE
                                Toast.makeText(requireContext(), "" + Message, Toast.LENGTH_SHORT).show()
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
                        homeFragmentBinding.homeProgress.visibility = View.GONE
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
}