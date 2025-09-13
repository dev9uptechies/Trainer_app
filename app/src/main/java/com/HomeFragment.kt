package com

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.icu.text.DateFormatSymbols
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import androidx.cardview.widget.CardView
import androidx.core.text.HtmlCompat
import androidx.core.view.GravityCompat
import androidx.core.view.children
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.Adapter.SelectedEvent.SelectedEventEventAdapter
import com.example.Adapter.SelectedEvent.SelectedEventLessonAdapter
import com.example.Adapter.SelectedEvent.SelectedEventTestAdapter
import com.example.AthletesActivity
import com.example.GroupDetailActivity
import com.example.GroupListData
import com.example.NewsAdapter
import com.example.OnItemClickListener
import com.example.SelectGroupActivity
import com.example.model.HomeFragment.NewsModel
import com.example.model.PrivacyPolicy.privacypolicy
import com.example.model.SelectedDaysModel
import com.example.model.SelectedEventModel
import com.example.model.personal_diary.GetDiaryDataForEdit
import com.example.model.training_plan.TrainingPlanData
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.MesoCycleData
import com.example.trainerapp.ApiClass.MicroCycleData
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
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.RequestBuilder.post
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.kizitonwose.calendarview.CalendarView
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
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import com.kizitonwose.calendarview.model.CalendarDay
import com.makeramen.roundedimageview.RoundedTransformationBuilder
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import java.util.Date

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
    private var newsData: MutableList<NewsModel.Data> = mutableListOf()

    private var eventsData: List<SelectedEventModel.Event> = emptyList()
    private var groupIndex: Int = -1


    private var receivedPreSeasonMesocycles: List<MesoCycleData>? = null
    private var receivedPreCompetitiveMesocycles: List<MesoCycleData>? = null
    private var receivedCompetitiveMesocycles: List<MesoCycleData>? = null
    private var receivedTransitionMesocycles: List<MesoCycleData>? = null


    private var preSeason: GroupListData.Pre_Season? = null
    private var preCompetitive: GroupListData.Pre_Competitive? = null
    private var competitive: GroupListData.Competitive? = null
    private var transition: GroupListData.Transition? = null

    private var scdule: List<GroupListData.Schedule>? = null


    // Received Data
    private var receivedIds: String = ""
    private var receivedGroup_Ids: String = ""
    private var receivedname: String? = null
    private var receivedstartDate: String? = null
    private var receivedendDate: String? = null
    private var receivedmesocycle: String? = null
    private var receivedworkloadColor: String? = null


    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    var formattedDate: String = sdf.format(Date())   // 👈 defaults to today

    private val datesWithDataTest = mutableSetOf<LocalDate>()

    lateinit var lessonadapter: SelectedEventLessonAdapter
    lateinit var eventadapter: SelectedEventEventAdapter
    lateinit var testadapter: SelectedEventTestAdapter

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

        preferenceManager = PreferencesManager(requireContext())
        Log.d("Login Token", preferenceManager.getToken()!!)

        val sharedPreferences1 = requireActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE)


        receivedPreSeasonMesocycles = Gson().fromJson(
            sharedPreferences1.getString("pre_season_mesocycles", "[]"),
            object : TypeToken<List<MesoCycleData>>() {}.type
        )

        receivedPreCompetitiveMesocycles = Gson().fromJson(
            sharedPreferences1.getString("pre_competitive_mesocycles", "[]"),
            object : TypeToken<List<MesoCycleData>>() {}.type
        )

        receivedCompetitiveMesocycles = Gson().fromJson(
            sharedPreferences1.getString("competitive_mesocycles", "[]"),
            object : TypeToken<List<MesoCycleData>>() {}.type
        )

        receivedTransitionMesocycles = Gson().fromJson(
            sharedPreferences1.getString("transition_mesocycles", "[]"),
            object : TypeToken<List<MesoCycleData>>() {}.type
        )

        val sharedPreferences =
            requireActivity().getSharedPreferences("AppPreferences", MODE_PRIVATE)
        val shouldSave = sharedPreferences.getBoolean("ShouldSave", false)

        Log.d("SHOULDSAVE", "onCreate: $shouldSave")

        receivedGroup_Ids = sharedPreferences.getString("group_id", "default_value") ?: ""

        receivedIds = sharedPreferences.getString("id", "default_value") ?: ""
        receivedGroup_Ids = sharedPreferences.getString("group_id", "default_value") ?: ""
        Log.e("USERIDDDDDDDDDDD", "onCreateView: " + receivedIds + receivedGroup_Ids)

        Log.e("USERIDDDDDDDDDDD", "onCreateView: " + preferenceManager.getUserId()!!.toInt())


        receivedname = sharedPreferences.getString("name", null)
        receivedstartDate = sharedPreferences.getString("start_date", null)
        receivedendDate = sharedPreferences.getString("end_date", null)
        receivedmesocycle = sharedPreferences.getString("mesocycle", null)
        receivedworkloadColor = sharedPreferences.getString("workload_color", null)

        initViews()
        setDrawerToggle()
        getInstraction()
        GetNews()
        setUpCalendar()

        val userType = preferenceManager.GetFlage()
        if (userType == "Athlete") {
            GetProfileAthlete()
        } else {
            GetProfile()
        }

        val currentDate =
            homeFragmentBinding.exSevenCalendar!!.findFirstVisibleDay()?.date ?: LocalDate.now()
        val formattedMonthYear = DateTimeFormatter.ofPattern("MMMM yyyy").format(currentDate)
        Log.d("CalendarLog", "Current month and year: $formattedMonthYear")
        homeFragmentBinding.tvDate.text = formattedMonthYear

        val receivedIdInt = receivedIds.toIntOrNull()


        if (userType == "Athlete") {
            val menu = homeFragmentBinding.navigationView.menu
            menu.clear()

            menu.add(Menu.NONE, R.id.tv_notification, Menu.NONE, getString(R.string.notification))
                .setIcon(R.drawable.ic_notification)

            menu.add(Menu.NONE, R.id.tv_policy, Menu.NONE, getString(R.string.privacyPolicy))
                .setIcon(R.drawable.ic_privacy)

            menu.add(Menu.NONE, R.id.tv_favorite, Menu.NONE, getString(R.string.favourite))
                .setIcon(R.drawable.ic_favorite)

            menu.add(Menu.NONE, R.id.tv_profile, Menu.NONE, getString(R.string.performanceProfile))
                .setIcon(R.drawable.ic_perfomance)

            menu.add(
                Menu.NONE,
                R.id.tv_analysis,
                Menu.NONE,
                getString(R.string.competitionAnalysis)
            ).setIcon(R.drawable.ic_competition)

            menu.add(Menu.NONE, R.id.tv_view_analysis, Menu.NONE, getString(R.string.viewAnalysis))
                .setIcon(R.drawable.ic_competition)

            menu.add(
                Menu.NONE,
                R.id.tv_personal_diary,
                Menu.NONE,
                getString(R.string.personalDiary)
            ).setIcon(R.drawable.icd)

            menu.add(Menu.NONE, R.id.tv_setting, Menu.NONE, getString(R.string.settings))
                .setIcon(R.drawable.ic_setting)

            menu.add(Menu.NONE, R.id.logout, Menu.NONE, getString(R.string.logout))
                .setIcon(R.drawable.logout)
        } else {
//            val menu = homeFragmentBinding.navigationView.menu

//            homeFragmentBinding.navigationView.inflateMenu(R.menu.activity_main_drawer)
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
//            homeFragmentBinding.edtStartDate.text = savedStartDate
            homeFragmentBinding.seeAllAboutToday.visibility = View.VISIBLE
        } else {
//            homeFragmentBinding.edtStartDate.text = ""
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

    private fun initLessonRecyclerView(programs: List<SelectedEventModel.Lesson>) {
        homeFragmentBinding.favLessonRly.layoutManager = LinearLayoutManager(requireContext())
        lessonadapter = SelectedEventLessonAdapter(programs, requireContext(), this)
        homeFragmentBinding.favLessonRly.adapter = lessonadapter
    }

    private fun initTestRecyclerView(tests: List<SelectedEventModel.Test>) {
        homeFragmentBinding.favTestRly.layoutManager = LinearLayoutManager(requireContext())
        testadapter = SelectedEventTestAdapter(tests, requireContext(), this)
        homeFragmentBinding.favTestRly.adapter = testadapter
    }

    private fun initEventRecyclerView(events: List<SelectedEventModel.Event>) {
        if (events.isNotEmpty()) {
            Log.d("Event RecyclerView", "Setting up RecyclerView with events.")
            homeFragmentBinding.favEventRly.layoutManager = LinearLayoutManager(requireContext())
            eventadapter = SelectedEventEventAdapter(events, requireContext(), this)
            homeFragmentBinding.favEventRly.adapter = eventadapter

        } else {
            Log.d("Event RecyclerView", "No events available.")
        }
    }

    private fun setContent() {
        val workout = arrayOf(
            getString(R.string.instruction),
            getString(R.string.information),
            getString(R.string.news)
        )

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

    private var mesocycles: List<MesoCycleData> = emptyList()

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpCalendar() {

        val txt = homeFragmentBinding.selectGroupTxt.text.toString()
        Log.d("00000", "onCreateView: $txt")
        if (txt != "Select Group" && txt != "Seleccionar Grupo") {
            val sharedPreferences = requireActivity().getSharedPreferences("MyPrefss", MODE_PRIVATE)
            val mesocyclesJson = sharedPreferences.getString("mesocycles", "[]")
            mesocycles =
                Gson().fromJson(mesocyclesJson, object : TypeToken<List<MesoCycleData>>() {}.type)
        }


        val daysOfWeek = daysOfWeekFromLocale()
        val currentMonth = YearMonth.now()
        val today = LocalDate.now()
        val oneWeekBefore = YearMonth.from(LocalDate.now().minusWeeks(1))

        homeFragmentBinding.exSevenCalendar.apply {
            setup(currentMonth.minusMonths(100), currentMonth.plusMonths(100), daysOfWeek.first())
            scrollToMonth(currentMonth)
        }

        homeFragmentBinding.exSevenCalendar.post {
            homeFragmentBinding.exSevenCalendar.scrollToDate(today)
            fetchCurrentWeekDates()
            val firstVisibleDay =
                homeFragmentBinding.exSevenCalendar.findFirstVisibleDay()?.date ?: today

            homeFragmentBinding.exSevenCalendar.post {
                val firstVisibleMonth = homeFragmentBinding.exSevenCalendar.findFirstVisibleMonth()
                if (firstVisibleMonth != null) {
                    homeFragmentBinding.exSevenCalendar.monthScrollListener?.invoke(
                        firstVisibleMonth
                    )
                }
            }

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

                val currentDate = LocalDate.now()
                val firstDayOfWeek =
                    currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
                val lastDayOfWeek =
                    currentDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY))

                fun isWithinCurrentWeek(date: LocalDate): Boolean {
                    return !date.isBefore(firstDayOfWeek) && !date.isAfter(lastDayOfWeek)
                }

                val matchingMicrocycle = mesocycles.flatMap { it.microcycles ?: emptyList() }
                    .find { micro ->
                        try {
                            val startDate = LocalDate.parse(
                                micro.start_date,
                                DateTimeFormatter.ofPattern("yyyy-MM-dd")
                            )
                            val endDate = LocalDate.parse(
                                micro.end_date,
                                DateTimeFormatter.ofPattern("yyyy-MM-dd")
                            )
                            val isMatching =
                                !day.date.isBefore(startDate) && !day.date.isAfter(endDate)

                            if (isMatching) {
                                Log.d(
                                    "COLOR_DEBUG",
                                    "Matched Date: ${day.date}, Color: ${micro.workload_color}"
                                )
                            }

                            isMatching
                        } catch (e: Exception) {
                            Log.e(
                                "DateParsingError",
                                "Error parsing microcycle date: ${micro.start_date} - ${micro.end_date}",
                                e
                            )
                            false
                        }
                    }

                val dotView = container.binding.dotLesson

                if (matchingMicrocycle != null) {
                    val workloadColor = matchingMicrocycle.workload_color

                    val selectedColor = try {
                        if (!workloadColor.isNullOrEmpty()) {
                            Color.parseColor("#" + workloadColor.replace("#", ""))
                        } else Color.GRAY
                    } catch (e: IllegalArgumentException) {
                        Log.e("ColorError", "Invalid color: $workloadColor", e)
                        Color.GRAY
                    }

                    dotView.backgroundTintList = ColorStateList.valueOf(selectedColor)
                    dotView.visibility = View.VISIBLE

                    Log.d("DotApplied", "Applied color: $workloadColor for ${day.date}")
                } else {
                    dotView.visibility = View.GONE
                }


                val preSeasonStartDate = receivedstartDate?.toLocalDate()
                val preSeasonEndDate = receivedendDate?.toLocalDate()


                container.view.setOnClickListener {
                    if (day.owner == DayOwner.THIS_MONTH) {
                        selectDate(day.date)

                        formattedDate = day.date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

                        val receivedIdInt = receivedIds.toIntOrNull()
                        val receivedIdIntAthlete = receivedGroup_Ids.toIntOrNull()

                        Log.e("DJDJDJDJDJJDJDJDJ", "bind: $receivedIdIntAthlete")

                        val userType = preferenceManager.GetFlage()

                        if (userType == "Athlete") {
                            if (receivedIdIntAthlete != null && receivedIdIntAthlete != 0) {
                                homeFragmentBinding.linerAthlete.visibility = View.VISIBLE

                                Log.d("FORMATTEDDATE", "bind: ${formattedDate.toString()}")

                                checkAndDisplayWeekData(formattedDate)

                                getPersonalDiaryData(formattedDate.toString())
//
//                                var isPaused = false
//
//                                if (!isPaused) {
//                                    homeFragmentBinding.homeProgress.visibility = View.VISIBLE
//
//                                    fetchDayDataAthleteDate(selectedDate!!, true)
//
//                                    isPaused = true
//                                    Handler(Looper.getMainLooper()).postDelayed({
//                                        isPaused = false
//                                        homeFragmentBinding.homeProgress.visibility = View.GONE
//                                    }, 2000)
//                                }

//                                fetchDayDataAthlete(day.date,true)

                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Please Select Group First",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        } else {
                            if (receivedIdInt != null && receivedIdInt != 0) {

                                checkAndDisplayWeekData(formattedDate)


//                                val intent =
//                                    Intent(requireContext(), SelectDayActivity::class.java).apply {
//                                        putExtra("id", receivedIdInt)
//                                        putExtra("date", formattedDate)
//                                    }
//                                context!!.startActivity(intent)
                            } else if (receivedIdIntAthlete != null && receivedIdIntAthlete != 0) {
                                Log.d("DNENEN", "bind: $receivedIdIntAthlete")
//                                checkAndDisplayWeekData(formattedDate)

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

        homeFragmentBinding.exSevenCalendar.monthScrollListener = { month ->
            // Format the month as "Month Year" (e.g., "January 2025")
            //                val formattedMonth = DateTimeFormatter.ofPattern("MMMM yyyy").format(month.yearMonth)
            //
            //                // Set the TextView with the formatted month and year
            //                homeFragmentBinding.tvDate.text = formattedMonth

            val currentDate =
                homeFragmentBinding.exSevenCalendar.findFirstVisibleDay()?.date ?: LocalDate.now()
            val formattedMonthYear = DateTimeFormatter.ofPattern("MMMM yyyy").format(currentDate)
            Log.d("CalendarLog", "Current month and year: $formattedMonthYear")
            homeFragmentBinding.tvDate.text = formattedMonthYear

            Log.d("SLSLSLLS", "setUpCalendar: ${month.weekDays}")

            if (month.year == today.year) {
                titleSameYearFormatter.format(month.yearMonth)
            } else {
                titleFormatter.format(month.yearMonth)
            }

            val firstVisibleDate =
                homeFragmentBinding.exSevenCalendar!!.findFirstVisibleDay()?.date ?: today

            val visibleWeekDays = month.weekDays.flatten()
            Log.d("SCROLL_LISTENER", "Scroll Triggered: Visible Dates -> $visibleWeekDays")

            selectDate(month.yearMonth.atDay(1))

//            checkAndDisplayWeekData(visibleWeekDays.toString())
            val userType = preferenceManager.GetFlage()

            if (userType == "Athlete") {
                homeFragmentBinding.nextLessonContainer.visibility = View.VISIBLE
                checkAndDisplayWeekDataaa(visibleWeekDays)

            } else {
                if (scdule != null) {
                    checkAndDisplayWeekDataaa(visibleWeekDays)
                }

            }

        }

        homeFragmentBinding.exSevenCalendar.monthHeaderBinder =
            object : MonthHeaderFooterBinder<MonthViewContainer> {
                override fun create(view: View) = MonthViewContainer(view)

                override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                    if (container.legendLayout.tag == null) {
                        container.legendLayout.tag = month.yearMonth


                        val sharedPreferences =
                            homeFragmentBinding.root.context.getSharedPreferences(
                                "AppSettings",
                                Context.MODE_PRIVATE
                            )
                        val selectedLanguage =
                            sharedPreferences.getString("Selected_Language", "en") ?: "en"
                        val locale = Locale(selectedLanguage)

                        // Get localized month name
                        val monthFormatter = DateTimeFormatter.ofPattern("MMM, yyyy", locale)
                        val formattedMonth = monthFormatter.format(month.yearMonth)
                        homeFragmentBinding.tvDate.text =
                            formattedMonth.uppercase(locale) // Ensure month is also uppercase

                        // Get localized short day names, ensuring Sunday is first
                        val dayNames =
                            DateFormatSymbols(locale).shortWeekdays.drop(1) // Remove empty index 0
                        val orderedDayNames =
                            listOf(dayNames[0]) + dayNames.slice(1..6) // Start from Sunday

                        // Apply localized day names to the header in uppercase
                        container.legendLayout.children.filterIsInstance<TextView>()
                            .forEachIndexed { index, tv ->
                                tv.text =
                                    orderedDayNames[index].uppercase(locale) // Convert to uppercase
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
        val weekDates = mutableListOf<LocalDate>()

        var date = firstDayOfWeek
        while (!date.isAfter(lastDayOfWeek)) {
            weekDates.add(date)
            date = date.plusDays(1)
        }

        Log.d("WEEK_DATES", "Checking dates: $weekDates")

        // ✅ Ensure the list has 7 elements (Monday-Sunday)
        if (weekDates.size == 7) {
            checkNextAvailableDate(weekDates, 0)  // Safe call
        } else {
            Log.e("WEEK_DATES_ERROR", "Expected 7 dates but got: ${weekDates.size}")
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkNextAvailableDate(weekDates: List<LocalDate>, index: Int) {
        if (index >= weekDates.size) {
            Log.d("WEEK_DATES", "No available data found in the selected week.")
            return
        }

        val selectedDate = weekDates[index]

        fetchDayDataAthlete(selectedDate, true) { hasData ->
            if (!hasData) {
                checkNextAvailableDate(weekDates, index + 1)
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

            val data = SelectedDaysModel.Data(
                lessons = listOf(),
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

    @SuppressLint("NewApi")
    private fun fetchDayDataAthlete(
        selectedDate: LocalDate,
        showData: Boolean,
        onComplete: (Boolean) -> Unit
    ) {
        try {
            val formattedDate = selectionFormatter.format(selectedDate)
            Log.d(
                "CalendarFragmentF",
                "Fetching data for date: $formattedDate with ID: $receivedGroup_Ids"
            )

            apiInterface.GetSelectedEventAthlete(formattedDate, receivedGroup_Ids)!!
                .enqueue(object : Callback<SelectedEventModel> {
                    override fun onResponse(
                        call: Call<SelectedEventModel>,
                        response: Response<SelectedEventModel>
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            val selectedDaysModel = response.body()
                            Log.d(
                                "API Response",
                                "Response for date $formattedDate: $selectedDaysModel"
                            )

                            val data = selectedDaysModel?.data
                            if (data != null && (data.list.lessons.isNotEmpty() || data.list.events.isNotEmpty() || data.list.tests.isNotEmpty())) {
                                if (::eventadapter.isInitialized) {
                                    eventadapter.clearData()
                                }

                                if (showData) {
                                    initLessonRecyclerView(emptyList())
                                    initEventRecyclerView(emptyList())
                                    initTestRecyclerView(emptyList())

                                    if (data.list.lessons.isNotEmpty()) {
                                        initLessonRecyclerView(data.list.lessons)
                                    }
                                    if (data.list.events.isNotEmpty()) {
                                        eventsData = data.list.events
                                        initEventRecyclerView(eventsData)
                                    }
                                    if (data.list.tests.isNotEmpty()) {
                                        initTestRecyclerView(data.list.tests)
                                    }
                                }

                                // Data found, no need to check further
                                onComplete(true)
                            } else {
                                Log.e(
                                    "API Response",
                                    "No data for date $formattedDate. Checking next."
                                )
                                onComplete(false)  // No data found, check next date
                            }
                        } else {
                            Log.e("API Response", "Failed to fetch data: ${response.message()}")
                            onComplete(false)  // API call failed, check next date
                        }
                    }

                    override fun onFailure(call: Call<SelectedEventModel>, t: Throwable) {
                        Log.e("API Response", "Error: ${t.message}")
                        onComplete(false)  // API request failed, check next date
                    }
                })
        } catch (e: Exception) {
            Log.e("Catch", "CatchError :- ${e.message}")
            onComplete(false)  // Exception occurred, check next date
        }
    }


    @SuppressLint("NewApi")
    private fun fetchDayDataAthleteDate(selectedDate: LocalDate, ShowData: Boolean) {
        try {
            val formattedDate = selectionFormatter.format(selectedDate)
            Log.d(
                "CalendarFragmentF",
                "Fetching data for date: $formattedDate with ID: $receivedGroup_Ids"
            )

            apiInterface.GetSelectedEventAthlete(formattedDate, receivedGroup_Ids)!!
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
                                        eventsData = data.list.events
                                        initEventRecyclerView(eventsData)
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


    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkDatesForMonth(selectedDate: LocalDate, data: SelectedDaysModel.Data) {
        val currentMonth = YearMonth.from(selectedDate)
        val daysInMonth = currentMonth.lengthOfMonth()

        Log.d("SSKSKSKKS", "checkDatesForMonth: $datesWithDataTest")
        for (day in 1..31) {
            if (day > daysInMonth) break

            val date = currentMonth.atDay(day)

            val allDates = mutableSetOf<String>().apply {
                addAll(data.lessons.map { it.date })
                addAll(data.events.map { it.date })
                addAll(data.tests.map { it.date })
            }

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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun callGroupApi(receivedId: Int) {
        Utils.showLoading(requireActivity())

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
                        Utils.hideLoading(requireActivity())

                        val group = resource!!.data?.find { it.id == receivedId }
                        val groupList = resource?.data ?: emptyList()
                        groupIndex = groupList.indexOfFirst { it.id == receivedId }

                        Log.d("SHSHSHSHS", "onResponse: $groupIndex")


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
                            val planning = group.group_plannings?.firstOrNull()?.planning
                            homeFragmentBinding.seeAllAboutToday.visibility = View.VISIBLE


                            Log.d("SSJJSJ", "onResponse: ${group.schedule}")

                            val gson = GsonBuilder().setPrettyPrinting().create()
                            val jsonString = gson.toJson(group?.group_plannings)
                            val jsonString2 = gson.toJson(planning?.pre_season)

                            scdule = group?.schedule
                            Log.d("()()", "onResponse: $jsonString")

                            val mesocycles = mutableListOf<MesoCycleData>()

                            Log.d(
                                "SJKJSJSJ",
                                "onResponse: ${
                                    planning?.pre_season?.mesocycles?.get(0)?.microcycles?.get(0)?.pcMicrocycleAbility?.get(
                                        0
                                    )?.ability?.name
                                }"
                            )
                            preSeason = planning?.pre_season
                            preCompetitive = planning?.pre_competitive
                            competitive = planning?.competitive
                            transition = planning?.transition

                            planning?.pre_season?.mesocycles?.map { convertPreSeasonMesoCycle1(it) }
                                ?.let { mesocycles.addAll(it) }
                            planning?.pre_competitive?.mesocycles?.map {
                                convertPreCompetitiveMesoCycle1(
                                    it
                                )
                            }?.let { mesocycles.addAll(it) }
                            planning?.competitive?.mesocycles?.map { convertCompetitiveMesoCycle1(it) }
                                ?.let { mesocycles.addAll(it) }
                            planning?.transition?.mesocycles?.map { convertTranstionMesoCycle1(it) }
                                ?.let { mesocycles.addAll(it) }

                            Log.d(
                                "BVBVVB",
                                "onResponse: ${planning?.pre_season?.start_date}\n ${planning?.pre_season?.end_date}"
                            )

//                            homeFragmentBinding.seasonLy.visibility = View.VISIBLE
//                            homeFragmentBinding.titleTv.text = planning?.pre_season?.name ?: ""
//                            homeFragmentBinding.edtStartDate.text = planning?.pre_season?.start_date
//                            homeFragmentBinding.edtEndDate.text = planning?.pre_season?.end_date
//                            homeFragmentBinding.edtMesocycle.text = planning?.pre_season?.mesocycle

                            if (mesocycles.isNotEmpty()) {
                                Log.d(
                                    "API_RESPONSEEEGG",
                                    "Extracted mesocycles: ${Gson().toJson(mesocycles)}"
                                )

                                val sharedPreferences =
                                    requireActivity().getSharedPreferences("MyPrefss", MODE_PRIVATE)
                                sharedPreferences.edit()
                                    .putString("mesocycles", Gson().toJson(mesocycles)).apply()

                                setUpCalendar()
                            } else {
                                Log.e(
                                    "API_RESPONSEEEGG",
                                    "No mesocycles found, clearing calendar dots!"
                                )
                                clearCalendarDots()
                            }

                        } else {
                            homeFragmentBinding.selectGroupTxt.text =
                                "Group not found for ID: $receivedId"
                            clearCalendarDots()
                        }
                    } else {
                        Utils.hideLoading(requireActivity())
                        homeFragmentBinding.selectGroupTxt.text = "Failed to fetch data: $message"
                        clearCalendarDots()
                    }
                } else if (code == 403) {
                    Utils.hideLoading(requireActivity())
                    Log.e("API Error", "Access forbidden: ${response.message()}")
                    call.cancel()
                    preferenceManager.setUserLogIn(false)
                } else {
                    Utils.hideLoading(requireActivity())
                    Log.e("API Error", "Error code: $code, ${response.message()}")
                    call.cancel()
                    clearCalendarDots()
                }
            }

            override fun onFailure(call: Call<GroupListData?>, t: Throwable) {
                Log.e("GROOOOOOOP", "API call failed: ${t.message}")
                Utils.hideLoading(requireActivity())
                call.cancel()
                clearCalendarDots()
            }
        })
    }


    @SuppressLint("NewApi")
    private fun checkAndDisplayWeekData(weekDays: String) {
        val weekDates = weekDays
        homeFragmentBinding.planningContainer.removeAllViews()
        homeFragmentBinding.planningContainer.visibility = View.VISIBLE

        fun addPlanningData(
            title: String,
            startDate: String?,
            endDate: String?,
            mesocycle: String?
        ) {
            val planningView =
                LayoutInflater.from(context).inflate(R.layout.viewtrainingplanlist, null)
            planningView.findViewById<TextView>(R.id.training_name_one).text = title
            planningView.findViewById<TextView>(R.id.start_date_one).text = "start: " + startDate
            planningView.findViewById<TextView>(R.id.end_date_one).text = "end: " + endDate
            planningView.findViewById<TextView>(R.id.mesocycle_one).text = "mesocycle: " + mesocycle

            homeFragmentBinding.planningContainer.addView(planningView)
        }

        fun addAbilityData(abilities: List<String>) {
            // Remove duplicates
            val uniqueAbilities = abilities.distinct()

            if (uniqueAbilities.isNotEmpty()) {
                val joinedAbilities = uniqueAbilities.joinToString(separator = ", ")

                Log.d("DJIND", "addAbilityData: $joinedAbilities")

                for (i in 0 until homeFragmentBinding.planningContainer.childCount) {
                    val child = homeFragmentBinding.planningContainer.getChildAt(i)
                    if (child.tag == "abilityCard") {
                        homeFragmentBinding.planningContainer.removeView(child)
                        break
                    }
                }

                val abilityCardView =
                    LayoutInflater.from(context).inflate(R.layout.view_ability_card, null)
                abilityCardView.tag = "abilityCard"
                val abilitiesTextView = abilityCardView.findViewById<TextView>(R.id.ability_txt)
                abilitiesTextView.text = joinedAbilities
                homeFragmentBinding.planningContainer.addView(abilityCardView)
            }
        }

        val firstDayOfWeek = weekDates


        preSeason?.let { season ->
            val start = season.start_date
            val end = season.end_date
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

//            val selectedDate = LocalDate.parse(firstDayOfWeek, formatter)

            Log.d(
                "SMDHB  GDVFV",
                "checkAndDisplayWeekData: $start -- $end ---- $firstDayOfWeek -- $selectedDate"
            )
            if (start != null && end != null && firstDayOfWeek in start..end) {
                addPlanningData(
                    season.name ?: "PreSeason",
                    season.start_date,
                    season.end_date,
                    season.mesocycle
                )



                val abilities = season.mesocycles?.flatMap { meso ->
                    meso.microcycles?.flatMap { micro ->
                        micro.pcMicrocycleAbility?.mapNotNull { it.ability?.name } ?: emptyList()
                    } ?: emptyList()
                } ?: emptyList()

                Log.d(
                    "SLSLLSssssL",
                    "${
                        preSeason!!.mesocycles?.get(0)?.microcycles?.get(0)?.pcMicrocycleAbility?.get(
                            0
                        )?.ability?.name
                    }"
                )

                if (abilities.isNotEmpty()) {
                    addAbilityData(abilities)
                } else {
                    Log.d("SLSLLSL", "No ability data found for PreSeason")
                }
            }
        }


        preCompetitive?.let { season ->
            val start = season.start_date
            val end = season.end_date
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

//            val selectedDate = LocalDate.parse(firstDayOfWeek, formatter)

            Log.d(
                "SMDHB  GDVFV",
                "checkAndDisplayWeekData: $start -- $end ---- $firstDayOfWeek -- $selectedDate"
            )
            if (start != null && end != null && firstDayOfWeek in start..end) {
                addPlanningData(
                    season.name ?: "PreCompetitive",
                    season.start_date,
                    season.end_date,
                    season.mesocycle
                )

                val abilities = season.mesocycles?.flatMap { meso ->
                    meso.microcycles?.flatMap { micro ->
                        micro.pcMicrocycleAbility?.mapNotNull { it.ability?.name } ?: emptyList()
                    } ?: emptyList()
                } ?: emptyList()

                if (abilities.isNotEmpty()) {
                    addAbilityData(abilities)
                } else {
                    Log.d("SLSLLSL", "No ability data found for PreCompetitive")
                }
            }
        }

        competitive?.let { season ->
            val start = season.start_date
            val end = season.end_date
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

//            val selectedDate = LocalDate.parse(firstDayOfWeek, formatter)

            Log.d(
                "SMDHB  GDVFV",
                "checkAndDisplayWeekData: $start -- $end ---- $firstDayOfWeek -- $selectedDate"
            )
            if (start != null && end != null && firstDayOfWeek in start..end) {
                addPlanningData(
                    season.name ?: "Competitive",
                    season.start_date,
                    season.end_date,
                    season.mesocycle
                )

                val abilities = season.mesocycles?.flatMap { meso ->
                    meso.microcycles?.flatMap { micro ->
                        micro.pcMicrocycleAbility?.mapNotNull { it.ability?.name } ?: emptyList()
                    } ?: emptyList()
                } ?: emptyList()

                if (abilities.isNotEmpty()) {
                    addAbilityData(abilities)
                } else {
                    Log.d("SLSLLSL", "No ability data found for Competitive")
                }
            }
        }

        transition?.let { season ->
            val start = season.start_date
            val end = season.end_date
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

//            val selectedDate = LocalDate.parse(firstDayOfWeek, formatter)

            Log.d(
                "SMDHB  GDVFV",
                "checkAndDisplayWeekData: $start -- $end ---- $firstDayOfWeek -- $selectedDate"
            )
            if (start != null && end != null && firstDayOfWeek in start..end) {
                addPlanningData(
                    season.name ?: "Transition",
                    season.start_date,
                    season.end_date,
                    season.mesocycle
                )

                val abilities = season.mesocycles?.flatMap { meso ->
                    meso.microcycles?.flatMap { micro ->
                        micro.pcMicrocycleAbility?.mapNotNull { it.ability?.name } ?: emptyList()
                    } ?: emptyList()
                } ?: emptyList()

                if (abilities.isNotEmpty()) {
                    addAbilityData(abilities)
                } else {
                    Log.d("SLSLLSL", "No ability data found for Transition")
                }
            }
        }



    }

    @SuppressLint("NewApi")
    private fun checkAndDisplayWeekDataaa(weekDays: List<CalendarDay>) {
        val weekDates = weekDays.map { it.date }
        homeFragmentBinding.nextLessonContainer.removeAllViews()

        fun addScheduleData(
            day: String,
            count: String,
            firstStartTime: String,
            firstEndTime: String
        ) {
            homeFragmentBinding.nextTv.visibility = View.VISIBLE

            val scheduleView =
                LayoutInflater.from(context).inflate(R.layout.next_lession_rly_item, null)
            scheduleView.findViewById<TextView>(R.id.name).text =
                day.take(3).replaceFirstChar { it.uppercase() }
            scheduleView.findViewById<TextView>(R.id.count).text = count.ifEmpty { "0" }
            scheduleView.findViewById<TextView>(R.id.time).text = firstStartTime.ifEmpty { "" }
            scheduleView.findViewById<TextView>(R.id.endtime).text = firstEndTime.ifEmpty { "" }
            scheduleView.findViewById<CardView>(R.id.card_view).setOnClickListener {
                startActivity(
                    Intent(requireContext(), GroupDetailActivity::class.java).apply {
                        putExtra("group_id", receivedIds)
                        putExtra("position", groupIndex)
                    }
                )
            }
            homeFragmentBinding.nextLessonContainer.addView(scheduleView)
        }

        val dayToDateMap = listOf(
            "Sun" to DayOfWeek.SUNDAY,
            "Mon" to DayOfWeek.MONDAY,
            "Tue" to DayOfWeek.TUESDAY,
            "Wed" to DayOfWeek.WEDNESDAY,
            "Thu" to DayOfWeek.THURSDAY,
            "Fri" to DayOfWeek.FRIDAY,
            "Sat" to DayOfWeek.SATURDAY
        )

        val today = LocalDate.now().dayOfWeek

        // 🔥 Filter only today and upcoming days (skip previous days)
        val validDays = dayToDateMap.map { it.first }  // ✅ All week days (Sun–Sat)

        val groupedSchedules = scdule?.groupBy { it.day?.trim() } ?: emptyMap()

        validDays.forEach { day ->
            val schedules = groupedSchedules[day] ?: return@forEach  // 🔥 Skip if no data
            val correspondingDay = dayToDateMap.find { it.first == day }?.second ?: return@forEach
            val matchingDate = weekDates.find { it.dayOfWeek == correspondingDay } ?: return@forEach

            val count = schedules.size.toString()
            val firstStartTime = schedules.firstOrNull()?.start_time?.trim() ?: ""
            val lastEndTime = schedules.lastOrNull()?.end_time?.trim() ?: ""

            addScheduleData(day, count, firstStartTime, lastEndTime)
        }

        homeFragmentBinding.nextLessonContainer.requestLayout()

    }


    private fun convertPreSeasonMesoCycle1(meso: GroupListData.Mesocycles): MesoCycleData {
        return MesoCycleData().apply {
            id = meso.id
            planning_ps_id = meso.planning_ps_id
            name = meso.name
            start_date = meso.start_date
            end_date = meso.end_date

            microcycles = meso.microcycles?.map { micro ->
                MicroCycleData().apply {
                    id = micro.id
                    planning_ps_id = micro.pc_mesocycle_id
                    name = micro.name
                    start_date = micro.startDate  // ✅ Use correct attribute
                    end_date = micro.endDate
                    workload_color = micro.workloadColor  // ✅ Use correct attribute
                }
            } ?: emptyList()
        }
    }


    /** ✅ Convert Pre-Competitive Mesocycles */
    private fun convertPreCompetitiveMesoCycle1(meso: GroupListData.PreMesocycles): MesoCycleData {
        return MesoCycleData().apply {
            id = meso.id
            planning_ps_id = meso.planning_pc_id
            name = meso.name
            start_date = meso.start_date
            end_date = meso.end_date

            microcycles = meso.microcycles?.map { micro ->
                MicroCycleData().apply {
                    id = micro.id
                    planning_ps_id = micro.pc_mesocycle_id
                    name = micro.name
                    start_date = micro.startDate
                    end_date = micro.endDate
                    workload_color = micro.workloadColor
                }
            } ?: emptyList()
        }
    }

    /** ✅ Convert Competitive Mesocycles */
    private fun convertCompetitiveMesoCycle1(meso: GroupListData.ComMesocycles): MesoCycleData {
        return MesoCycleData().apply {
            id = meso.id
            planning_ps_id = meso.planning_pc_id
            name = meso.name
            start_date = meso.start_date
            end_date = meso.end_date

            microcycles = meso.microcycles?.map { micro ->
                MicroCycleData().apply {
                    id = micro.id
                    planning_ps_id = micro.pc_mesocycle_id




                    name = micro.name
                    start_date = micro.startDate
                    end_date = micro.endDate
                    workload_color = micro.workloadColor
                }
            } ?: emptyList()
        }
    }


    private fun convertTranstionMesoCycle1(meso: GroupListData.TraMesocycles): MesoCycleData {
        return MesoCycleData().apply {
            id = meso.id
            planning_ps_id = meso.planning_pc_id
            name = meso.name
            start_date = meso.start_date
            end_date = meso.end_date

            microcycles = meso.microcycles?.map { micro ->
                MicroCycleData().apply {
                    id = micro.id
                    planning_ps_id = micro.pc_mesocycle_id
                    name = micro.name
                    start_date = micro.startDate
                    end_date = micro.endDate
                    workload_color = micro.workloadColor
                }
            } ?: emptyList()
        }
    }

    private fun convertTranstionMesoCycle(meso: GroupListData.TraMesocycles): MesoCycleData {
        return MesoCycleData().apply {
            id = meso.id
            planning_ps_id = meso.planning_pc_id
            name = meso.name
            start_date = meso.start_date
            end_date = meso.end_date

            microcycles = meso.microcycles?.map { micro ->
                MicroCycleData().apply {
                    id = micro.id
                    planning_ps_id = micro.pc_mesocycle_id
                    name = micro.name
                    start_date = micro.startDate
                    end_date = micro.endDate
                    workload_color = micro.workloadColor
                }
            } ?: emptyList()
        }
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
        Utils.showLoading(requireActivity())

        Log.d("API Call", "Received ID: $receivedId")

        apiInterface.GropListAthlete()?.enqueue(object : Callback<GroupListData?> {
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
                        Utils.hideLoading(requireActivity())
                        val group = resource!!.data?.find { it.id == receivedId }


                        if (group != null) {
                            Log.d("API Response", "Group found: ${group.name}, Group ID: ${group.id}")

                            saveSelectedGroupData(
                                group.name.toString(),
                                group.group_plannings?.firstOrNull()?.planning?.start_date
                                    ?: "",
                                group.id.toString()
                            )

                            homeFragmentBinding.selectGroupTxt.text = group.name
                            homeFragmentBinding.seeAllAboutToday.visibility = View.VISIBLE

                            val planning = group.group_plannings?.firstOrNull()?.planning

                            val mesocycles = mutableListOf<MesoCycleData>()

                            preSeason = planning?.pre_season
                            preCompetitive = planning?.pre_competitive
                            competitive = planning?.competitive
                            transition = planning?.transition


                            planning?.pre_season?.mesocycles?.map { convertPreSeasonMesoCycle(it) }
                                ?.let { mesocycles.addAll(it) }
                            planning?.pre_competitive?.mesocycles?.map {
                                convertPreCompetitiveMesoCycle(
                                    it
                                )
                            }?.let { mesocycles.addAll(it) }
                            planning?.competitive?.mesocycles?.map { convertCompetitiveMesoCycle(it) }
                                ?.let { mesocycles.addAll(it) }
                            planning?.transition?.mesocycles?.map { convertTranstionMesoCycle(it) }
                                ?.let { mesocycles.addAll(it) }

                            scdule = group?.schedule

                            if (mesocycles.isNotEmpty()) {
                                Log.d(
                                    "API_RESPONSEEEGG",
                                    "Extracted mesocycles: ${Gson().toJson(mesocycles)}"
                                )

                                val sharedPreferences =
                                    requireActivity().getSharedPreferences("MyPrefss", MODE_PRIVATE)
                                sharedPreferences.edit()
                                    .putString("mesocycles", Gson().toJson(mesocycles)).apply()

                                Log.d("OOOOOSO", "clearCalendarDots: SAVE")

                                setUpCalendar()
                            } else {
                                Log.e(
                                    "API_RESPONSEEEGG",
                                    "No mesocycles found, clearing calendar dots!"
                                )
                                clearCalendarDots()
                            }

                        } else {
                            homeFragmentBinding.selectGroupTxt.text =
                                "Group not found for ID: $receivedId"
                            clearCalendarDots()
                        }
                    } else {
                        Utils.hideLoading(requireActivity())
                        homeFragmentBinding.selectGroupTxt.text = "Failed to fetch data: $message"
                        clearCalendarDots()
                    }
                } else if (code == 403) {
                    Utils.hideLoading(requireActivity())
                    Log.e("API Error", "Access forbidden: ${response.message()}")
                    call.cancel()
                    preferenceManager.setUserLogIn(false)
                } else {
                    Utils.hideLoading(requireActivity())
                    Log.e("API Error", "Error code: $code, ${response.message()}")
                    call.cancel()
                    clearCalendarDots()
                }
            }

            override fun onFailure(call: Call<GroupListData?>, t: Throwable) {
                Log.e("GROOOOOOOP", "API call failed: ${t.message}")
                Utils.hideLoading(requireActivity())
                call.cancel()
                clearCalendarDots()
            }
        })
    }

    /** ✅ Convert Pre-Season Mesocycles */
    private fun convertPreSeasonMesoCycle(meso: GroupListData.Mesocycles): MesoCycleData {
        return MesoCycleData().apply {
            id = meso.id
            planning_ps_id = meso.planning_ps_id
            name = meso.name
            start_date = meso.start_date
            end_date = meso.end_date

            microcycles = meso.microcycles?.map { micro ->
                MicroCycleData().apply {
                    id = micro.id
                    planning_ps_id = micro.pc_mesocycle_id
                    name = micro.name
                    start_date = micro.startDate
                    end_date = micro.endDate
                    workload_color = micro.workloadColor
                }
            } ?: emptyList()
        }
    }

    /** ✅ Convert Pre-Competitive Mesocycles */
    private fun convertPreCompetitiveMesoCycle(meso: GroupListData.PreMesocycles): MesoCycleData {
        return MesoCycleData().apply {
            id = meso.id
            planning_ps_id = meso.planning_pc_id
            name = meso.name
            start_date = meso.start_date
            end_date = meso.end_date

            microcycles = meso.microcycles?.map { micro ->
                MicroCycleData().apply {
                    id = micro.id
                    planning_ps_id = micro.pc_mesocycle_id
                    name = micro.name
                    start_date = micro.startDate
                    end_date = micro.endDate
                    workload_color = micro.workloadColor
                }
            } ?: emptyList()
        }
    }

    private fun convertCompetitiveMesoCycle(meso: GroupListData.ComMesocycles): MesoCycleData {
        return MesoCycleData().apply {
            id = meso.id
            planning_ps_id = meso.planning_pc_id
            name = meso.name
            start_date = meso.start_date
            end_date = meso.end_date

            microcycles = meso.microcycles?.map { micro ->
                MicroCycleData().apply {
                    id = micro.id
                    planning_ps_id = micro.pc_mesocycle_id
                    name = micro.name
                    start_date = micro.startDate
                    end_date = micro.endDate
                    workload_color = micro.workloadColor
                }
            } ?: emptyList()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        clearCalendarDots()
    }

    private fun clearCalendarDots() {
        homeFragmentBinding.exSevenCalendar.notifyCalendarChanged()
        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefss", MODE_PRIVATE)
        sharedPreferences.edit().remove("mesocycles").apply()

        Log.d("OOOOOSO", "clearCalendarDots: CLAER")
    }

    private class DayViewContainer(view: View) : ViewContainer(view) {
        lateinit var day: CalendarDay
        val binding =
            Example3CalendarDayBinding.bind(view)

        init {
            view.setOnClickListener {
                Log.d("CalendarClick", "Clicked on date: ${day.date}")
            }
        }
    }


    private fun saveSelectedGroupData(groupName: String, startDate: String, id: String) {
        Log.d("Save Group Data", "Saving group data: $groupName, $startDate, $id")
        val sharedPreferences =
            requireContext().getSharedPreferences("AppPreferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("selectedGroupName", groupName)
        editor.putString("selectedStartDate", startDate)
        editor.putString("selectedId", id)
        editor.apply()

        val savedId = sharedPreferences.getString("selectedId", null)
        Log.d("Saved Data", "Saved Group ID: $savedId")
    }

    private fun getSavedGroupData(): Triple<String?, String?, String?> {
        Log.d("Get Dataaa", "Clearing saved group data")

        val sharedPreferences =
            requireContext().getSharedPreferences("AppPreferences", MODE_PRIVATE)
        val groupName = sharedPreferences.getString("selectedGroupName", null)
        val startDate = sharedPreferences.getString("selectedStartDate", null)
        val id = sharedPreferences.getString("selectedId", null)  // Retrieve the id
        val receivedGroup_Ids = sharedPreferences.getString("group_id", "default_value") ?: ""

        return Triple(groupName, startDate, id)
    }

    private fun clearSavedGroupData() {
        Log.d("Clear Data", "Clearing saved group data")
        val sharedPreferences =
            requireContext().getSharedPreferences("AppPreferences", MODE_PRIVATE)
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

    private fun initViews() {
        apiClient = APIClient(requireContext())

        apiInterface = apiClient.client().create(APIInterface::class.java)
        Sportlist = ArrayList()
        WorkOutlist = ArrayList()

        val receivedIdInt = receivedIds.toIntOrNull()
        val receivedIdIntAthlete = receivedGroup_Ids.toIntOrNull()

        val userType = preferenceManager.GetFlage()

        homeFragmentBinding.seeAllAboutToday.setOnClickListener {
            if (userType == "Athlete") {
                val intent = Intent(requireContext(), SelectDayActivity::class.java).apply {
                    putExtra("id", receivedIdIntAthlete)
                    putExtra("date", formattedDate)
                }
                requireContext().startActivity(intent)

            } else {
                val intent = Intent(requireContext(), SelectDayActivity::class.java).apply {
                    putExtra("id", receivedIdInt)
                    putExtra("date", formattedDate)
                }
                requireContext().startActivity(intent)

            }


        }

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
                                Toast.makeText(
                                    requireContext(),
                                    "No data found",
                                    Toast.LENGTH_SHORT
                                ).show()
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
                                Toast.makeText(
                                    requireContext(),
                                    "No data found",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        429 -> {
                            Toast.makeText(requireContext(), "Too Many Request", Toast.LENGTH_SHORT)
                                .show()
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
        homeFragmentBinding.NutritionAndHydration.setText(data.notes ?: "")
        homeFragmentBinding.Tiredness.setText(data.nutritionAndHydration ?: "")

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
                        //                        st artActivity(intent)
                        //                        requireActivity().finish()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Toast.makeText(requireContext(), "" + t.message, Toast.LENGTH_SHORT).show()
                    call.cancel()
                    Utils.hideLoading(requireActivity())
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
//                clearSavedGroupData()
            }
        } else {

        }
    }

    private fun GetProfileAthlete() {
        try {

            Utils.showLoading(requireActivity())

            apiInterface.ProfileDataAthlete()?.enqueue(object : Callback<RegisterData?> {
                override fun onResponse(
                    call: Call<RegisterData?>,
                    response: Response<RegisterData?>
                ) {
                    Utils.hideLoading(requireActivity())

                    if (response.isSuccessful) {
                        val resource = response.body()

                        val data = resource?.data
                        if (data != null) {

                            val transformation: Transformation = RoundedTransformationBuilder()
                                .borderColor(Color.BLACK)
                                .borderWidthDp(1f)
                                .cornerRadiusDp(10f)
                                .oval(false)
                                .build()

                            Picasso.get()
                                .load("https://uat.4trainersapp.com" + data?.image)
                                .fit()
                                .transform(transformation)
                                .error(R.drawable.app_icon)
                                .into(homeFragmentBinding.img)

                        } else {
                            Toast.makeText(
                                requireContext(),
                                "No profile data available",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                    } else {
                        Log.e("TAG", "Error: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Utils.hideLoading(requireActivity())
                    Log.d("FERROR", "onFailure: ${t.message}")
                    Toast.makeText(requireContext(), "Failed to load profile", Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }
            })
        } catch (e: Exception) {

        }
    }

    private fun GetProfile() {
        try {

            Utils.showLoading(requireActivity())

            apiInterface.ProfileData()?.enqueue(object : Callback<RegisterData?> {

                override fun onResponse(
                    call: Call<RegisterData?>,
                    response: Response<RegisterData?>
                ) {
                    activity?.let {
                        Utils.hideLoading(it)
                    }
                    val resource: RegisterData? = response.body()
                    val success: Boolean = resource?.status ?: false
                    val message: String = resource?.message ?: "No message"

                    if (success) {
                        val imageUrl = resource?.data?.image  // Check if image exists

                        if (!imageUrl.isNullOrEmpty()) {
                            val transformation: Transformation = RoundedTransformationBuilder()
                                .borderColor(Color.BLACK)
                                .borderWidthDp(1f)
                                .cornerRadiusDp(10f)
                                .oval(false)
                                .build()

                            Picasso.get()
                                .load("https://uat.4trainersapp.com/$imageUrl")
                                .fit()
                                .transform(transformation)
                                .error(R.drawable.app_icon)
                                .into(homeFragmentBinding.img)

                            Log.d("GetProfile", "Image URL: $imageUrl")
                        } else {
                            Log.e("GetProfile", "Image URL is null or empty")
                        }
                    } else {
                        Log.e("GetProfile", "Profile fetch failed: $message")
                    }

                    Log.d("GetProfile", "Response Code: ${response.code()}")
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    call.cancel()
                    Utils.hideLoading(requireActivity())
                }
            })

        } catch (e: Exception) {

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
                        homeFragmentBinding.linerAthlete.visibility = View.GONE
                    }
                }
            }
        }


        if (string == "fav") {
            try {
                Utils.showLoading(requireActivity())

                val id: MultipartBody.Part =
                    MultipartBody.Part.createFormData("id", type.toInt().toString())
                apiInterface.Favourite_lession(id)?.enqueue(object : Callback<RegisterData?> {
                    @RequiresApi(Build.VERSION_CODES.O)
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
                                selectedDate?.let { fetchDayDataAthleteDate(it, true) }
                            } else {
                                Utils.hideLoading(requireActivity())
                                Toast.makeText(requireContext(), "" + Message, Toast.LENGTH_SHORT)
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
            } catch (e: Exception) {
                Log.d("catch", "callGroupApiAthlete: ${e.message.toString()}")
            }
        } else if (string == "unfav") {
            try {
                Utils.showLoading(requireActivity())

                val id: MultipartBody.Part =
                    MultipartBody.Part.createFormData("id", type.toInt().toString())
                apiInterface.DeleteFavourite_lession(type.toInt())
                    ?.enqueue(object : Callback<RegisterData?> {
                        @RequiresApi(Build.VERSION_CODES.O)
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
                                    selectedDate?.let { fetchDayDataAthleteDate(it, true) }
                                    Utils.hideLoading(requireActivity())
                                } else {
                                    Utils.hideLoading(requireActivity())
                                    Toast.makeText(
                                        requireContext(),
                                        "" + Message,
                                        Toast.LENGTH_SHORT
                                    ).show()
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
            } catch (e: Exception) {
                Log.d("catch", "callGroupApiAthlete: ${e.message.toString()}")
            }
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
                            selectedDate?.let { fetchDayDataAthleteDate(it, true) }
                        } else {
                            Utils.hideLoading(requireActivity())
                            Toast.makeText(requireContext(), "" + Message, Toast.LENGTH_SHORT)
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
                        ).show()
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
                                selectedDate?.let { fetchDayDataAthleteDate(it, true) }
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
                            selectedDate?.let { fetchDayDataAthleteDate(it, true) }

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
                    Toast.makeText(requireContext(), "" + t.message, Toast.LENGTH_SHORT).show()
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
                                selectedDate?.let { fetchDayDataAthleteDate(it, true) }
                                eventadapter.notifyDataSetChanged()
                            } else {
                                Utils.hideLoading(requireActivity())
                                Toast.makeText(requireContext(), "" + Message, Toast.LENGTH_SHORT)
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
}