    package com
    
    import android.annotation.SuppressLint
    import android.app.Dialog
    import android.content.Context
    import android.content.Intent
    import android.os.Build
    import android.os.Bundle
    import android.util.Log
    import android.view.Gravity
    import android.view.LayoutInflater
    import android.view.MenuItem
    import android.view.View
    import android.view.ViewGroup
    import android.view.Window
    import android.view.WindowManager
    import android.widget.Button
    import android.widget.TextView
    import android.widget.Toast
    import androidx.annotation.RequiresApi
    import androidx.appcompat.app.ActionBarDrawerToggle
    import androidx.appcompat.app.AppCompatActivity
    import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
    import androidx.appcompat.widget.AppCompatButton
    import androidx.core.text.HtmlCompat
    import androidx.core.view.GravityCompat
    import androidx.core.view.children
    import androidx.drawerlayout.widget.DrawerLayout
    import androidx.fragment.app.Fragment
    import androidx.recyclerview.widget.LinearLayoutManager
    import com.example.AthletesActivity
    import com.example.GroupListData
    import com.example.NewsAdapter
    import com.example.OnItemClickListener
    import com.example.SelectGroupActivity
    import com.example.model.HomeFragment.NewsModel
    import com.example.model.PrivacyPolicy.privacypolicy
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
    import com.example.trainerapp.databinding.AddSlotCalendarDayBinding
    import com.example.trainerapp.databinding.Example3CalendarDayBinding
    import com.example.trainerapp.databinding.Example3CalendarHeaderBinding
    import com.example.trainerapp.databinding.FragmentHomeBinding
    import com.example.trainerapp.notification.NotificationActivity
    import com.example.trainerapp.personal_diary.ViewPersonalDiaryActivity
    import com.example.trainerapp.privacy_policy.PrivacyPolicyActivity
    import com.example.trainerapp.view_analysis.ViewAnalysisActivity
    import com.google.android.material.navigation.NavigationView
    import com.kizitonwose.calendarview.model.CalendarDay
    import com.kizitonwose.calendarview.model.CalendarMonth
    import com.kizitonwose.calendarview.model.DayOwner
    import com.kizitonwose.calendarview.model.ScrollMode
    import com.kizitonwose.calendarview.ui.DayBinder
    import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
    import com.kizitonwose.calendarview.ui.ViewContainer
    import retrofit2.Call
    import retrofit2.Callback
    import retrofit2.Response
    import java.time.DayOfWeek
    import java.time.LocalDate
    import java.time.YearMonth
    import java.time.format.DateTimeFormatter
    
    class HomeFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener, OnItemClickListener.OnItemClickCallback {
        lateinit var homeFragmentBinding: FragmentHomeBinding
        private lateinit var Sportlist: java.util.ArrayList<Sport_list>
        private lateinit var WorkOutlist: java.util.ArrayList<Work_Out>
        lateinit var preferenceManager: PreferencesManager
        lateinit var apiInterface: APIInterface
        lateinit var apiClient: APIClient
        var actionBarDrawerToggle: ActionBarDrawerToggle? = null
        lateinit var adapter: HomeAdapter
        lateinit var newsadapter: NewsAdapter
        lateinit var workoutadapter: WorkOutAdapter
        private var instractionData: privacypolicy.Data? = null
        private var newsData: MutableList<NewsModel.Data> = mutableListOf()
        private var receivedIds: String = ""
        var formattedDate:String?= null


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

            homeFragmentBinding.seasonLy
            homeFragmentBinding.nextTv
            homeFragmentBinding.viewRecycler

            initViews()
            setDrawerToggle()
            getInstraction()
            GetNews()
            setUpCalendar()



            val userType = preferenceManager.GetFlage()


            if (userType == "Athlete") {
                homeFragmentBinding.navigationView.menu.findItem(R.id.tv_library).isVisible = false
                homeFragmentBinding.navigationView.menu.findItem(R.id.tv_athletes).isVisible = false


            } else {
                homeFragmentBinding.navigationView.menu.findItem(R.id.tv_library).isVisible = true
                homeFragmentBinding.navigationView.menu.findItem(R.id.tv_athletes).isVisible = true
            }


            val receivedIdInt = receivedIds.toIntOrNull()
            if (receivedIdInt != null) {
                callGroupApi(receivedIdInt)
            } else {
                Log.e("API Call", "Invalid receivedId: $receivedIds")
                clearSavedGroupData()
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
                setup(currentMonth.minusMonths(10), currentMonth.plusMonths(10), daysOfWeek.first())
                scrollToMonth(currentMonth)
            }

            homeFragmentBinding.exSevenCalendar!!.post {
                homeFragmentBinding.exSevenCalendar!!.scrollToDate(today)
            }

            class DayViewContainer(view: View) : ViewContainer(view) {
                lateinit var day: CalendarDay
                val binding = Example3CalendarDayBinding.bind(view)

                init {
                    view.setOnClickListener {
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

            homeFragmentBinding.exSevenCalendar!!.dayBinder = object : DayBinder<DayViewContainer> {
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
                            formattedDate = day.date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

                            val receivedIdInt = receivedIds.toIntOrNull()

                            Log.e("DJDJDJDJDJJDJDJDJ", "bind: $receivedIdInt")

                            val userType = preferenceManager.GetFlage()

                            if (userType == "Athlete") {
                                if (receivedIdInt != null && receivedIdInt != 0) {
                                    homeFragmentBinding.linerAthlete.visibility = View.VISIBLE

                                    Log.d("FORMATTEDDATE", "bind: ${formattedDate.toString()}")

                                    getPersonalDiaryData(formattedDate.toString())
                                } else {
                                    Toast.makeText(requireContext(), "Please Select Group First", Toast.LENGTH_SHORT).show()
                                }

                            }else{
                                if (receivedIdInt != null && receivedIdInt != 0) {
                                    val intent = Intent(requireContext(), SelectDayActivity::class.java).apply {
                                        putExtra("id", receivedIdInt)
                                        putExtra("date", formattedDate)
                                    }
                                    context!!.startActivity(intent)
                                } else {
                                    Toast.makeText(requireContext(), "Please Select Group First", Toast.LENGTH_SHORT).show()
                                }
                            }

                        }
                    }
                }
            }

            homeFragmentBinding.exSevenCalendar!!.monthScrollListener = {
                if (it.year == today.year) {
                    titleSameYearFormatter.format(it.yearMonth)
                } else {
                    titleFormatter.format(it.yearMonth)
                }
                selectDate(it.yearMonth.atDay(1))
            }

            // Update the month header to show abbreviated day names (Mon, Tue, Wed)
            homeFragmentBinding.exSevenCalendar.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
                override fun create(view: View) = MonthViewContainer(view)

                @SuppressLint("ResourceAsColor")
                override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                    if (container.legendLayout.tag == null) {
                        container.legendLayout.tag = month.yearMonth

                        // Get short day names like "Mon", "Tue", "Wed"
                        val dayNames = daysOfWeek.map { it.name.take(3) }  // Get first 3 letters of each day name
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

                Log.d("Select Date month:-", "select date :-$selectedDate \n date :- $date \n old date :-$oldDate")

                oldDate?.let { homeFragmentBinding.exSevenCalendar.notifyDateChanged(it) }
                homeFragmentBinding.exSevenCalendar.notifyDateChanged(date)
//                homeFragmentBinding.exSevenCalendar.scrollToMonth(YearMonth.from(date))
//                homeFragmentBinding.exSevenCalendar.smoothScrollToDate(date)
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun daysOfWeekFromLocale(): List<DayOfWeek> {
            val firstDayOfWeek = DayOfWeek.MONDAY
            val daysOfWeek = DayOfWeek.values()
            return daysOfWeek.slice(firstDayOfWeek.ordinal..daysOfWeek.lastIndex) +
                    daysOfWeek.slice(0 until firstDayOfWeek.ordinal)
        }

        private fun callGroupApi(receivedId: Int) {
            homeFragmentBinding.homeProgress.visibility = View.VISIBLE
            Log.d("API Call", "Received ID: $receivedId")

            apiInterface.GropList()?.enqueue(object : Callback<GroupListData?> {
                override fun onResponse(call: Call<GroupListData?>, response: Response<GroupListData?>) {
                    val code = response.code()
                    if (code == 200) {
                        val resource = response.body()
                        val success = resource?.status ?: false
                        val message = resource?.message ?: "No message available"

                        if (success) {
                            homeFragmentBinding.homeProgress.visibility = View.GONE
                            val group = resource!!.data?.find { it.id == receivedId }

                            if (group != null) {
                                Log.d("API Response", "Group found: ${group.name}, Group ID: ${group.id}")
                                saveSelectedGroupData(
                                    group.name.toString(),
                                    group.group_plannings?.firstOrNull()?.planning?.start_date ?: "",
                                    group.id.toString()
                                )

                                homeFragmentBinding.selectGroupTxt.text = group.name

                                val planning = group.group_plannings?.firstOrNull()
                                if (planning != null && planning.planning != null) {
                                    homeFragmentBinding.edtStartDate.text = planning.planning?.start_date
                                } else {
                                    homeFragmentBinding.edtStartDate.text = ""
                                }
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

                override fun onFailure(call: Call<GroupListData?>, t: Throwable) {
                    Log.e("GROOOOOOOP", "API call failed: ${t.message}")
                    homeFragmentBinding.homeProgress.visibility = View.GONE
                    call.cancel()
                }
            })
        }

        private fun saveSelectedGroupData(groupName: String, startDate: String, id: String) {
            Log.d("Save Group Data", "Saving group data: $groupName, $startDate, $id")
            val sharedPreferences = requireContext().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("selectedGroupName", groupName)
            editor.putString("selectedStartDate", startDate)
            editor.putString("selectedId", id)
            editor.apply()

            val savedId = sharedPreferences.getString("selectedId", null)
            Log.d("Saved Data", "Saved Group ID: $savedId")
        }

        private fun getSavedGroupData(): Triple<String?, String?, String?> {
            val sharedPreferences = requireContext().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
            val groupName = sharedPreferences.getString("selectedGroupName", null)
            val startDate = sharedPreferences.getString("selectedStartDate", null)
            val id = sharedPreferences.getString("selectedId", null)  // Retrieve the id
            return Triple(groupName, startDate, id)
        }

        private fun clearSavedGroupData() {
            Log.d("Clear Data", "Clearing saved group data")
            val sharedPreferences = requireContext().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.remove("selectedGroupName")
            editor.remove("selectedStartDate")
            editor.remove("selectedId") // Clear the saved ID
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
            preferenceManager = PreferencesManager(requireContext())
            Log.d("Login Token", preferenceManager.getToken()!!)
            apiInterface = apiClient.client().create(APIInterface::class.java)
            Sportlist = ArrayList()
            WorkOutlist = ArrayList()

            val sharedPreferences = requireActivity().getSharedPreferences("AppPreferences", MODE_PRIVATE)
            receivedIds = sharedPreferences.getString("id", "default_value") ?: ""

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
                                    Toast.makeText(requireContext(), "No data found", Toast.LENGTH_SHORT).show()
                                }
                            }

                            403 -> {
                                Utils.setUnAuthDialog(requireContext())
                            }

                            else -> {
                                val errorMessage = response.message() ?: "Unknown error occurred"
                                Log.d("APIResponse", "Error: $errorMessage")
                                Log.d("connection","canction:- $errorMessage")
                            }
                        }
                    }

                    override fun onFailure(call: Call<NewsModel>, t: Throwable) {
                        Log.e("APIError", "Network error: ${t.message}")
                        Log.d("connection","canction:- ${t.message}")

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
                    override fun onResponse(call: Call<privacypolicy>, response: Response<privacypolicy>) {
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

                            403 -> {
                                Utils.setUnAuthDialog(requireContext())
                            }

                            else -> {
                                val errorMessage = response.message() ?: "Unknown error occurred"
                                Log.d("APIResponses", "Error: $errorMessage")
                                Log.d("connections","canction in er:- $errorMessage")

                            }
                        }
                    }

                    override fun onFailure(call: Call<privacypolicy>, t: Throwable) {
                        Log.e("APIErrorss", "Network error: ${t.message}")
                        Log.d("connectionss","canction in fai:- ${t.message}")

                    }
                })
            } catch (e: Exception) {
                Log.e("APIException", "Exception: ${e.message}")

            }
        }

        private fun bindDataToUI(data: privacypolicy.Data) {
            val descriptionHtml = data.description ?: "No Description Available"

            val descriptionPlainText = HtmlCompat.fromHtml(descriptionHtml, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()

            homeFragmentBinding.descriptionPp.text = descriptionPlainText
        }



        private fun getPersonalDiaryData(date: String) {
            try {
                apiInterface.GetPersonalDiaryData(date)?.enqueue(object : Callback<GetDiaryDataForEdit> {
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
            homeFragmentBinding.Notes.setText(data.notes ?: "")

            // Check if personalDiaryDetails is empty or null
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
                            homeFragmentBinding.EnergyBT.setText(detail.beforeTraining ?: "") // Use setText for EditText
                            homeFragmentBinding.EnergyDT.setText(detail.duringTraining ?: "") // Use setText for EditText
                            homeFragmentBinding.EnergyAT.setText(detail.afterTraining ?: "") // Use setText for EditText
                        }
                        "Satisfaction" -> {
                            homeFragmentBinding.SatisfationBT.setText(detail.beforeTraining ?: "") // Use setText for EditText
                            homeFragmentBinding.SatisfationDT.setText(detail.duringTraining ?: "") // Use setText for EditText
                            homeFragmentBinding.SatisfationAT.setText(detail.afterTraining ?: "") // Use setText for EditText
                        }
                        "Happiness" -> {
                            homeFragmentBinding.HapinessBT.setText(detail.beforeTraining ?: "") // Use setText for EditText
                            homeFragmentBinding.HapinessDT.setText(detail.duringTraining ?: "") // Use setText for EditText
                            homeFragmentBinding.HapinessAT.setText(detail.afterTraining ?: "") // Use setText for EditText
                        }
                        "Irritability" -> {
                            homeFragmentBinding.IrritabilityBT.setText(detail.beforeTraining ?: "") // Use setText for EditText
                            homeFragmentBinding.IrritabilityDT.setText(detail.duringTraining ?: "") // Use setText for EditText
                            homeFragmentBinding.IrritabilityAT.setText(detail.afterTraining ?: "") // Use setText for EditText
                        }
                        "Determination" -> {
                            homeFragmentBinding.DeterminationBT.setText(detail.beforeTraining ?: "") // Use setText for EditText
                            homeFragmentBinding.DeterminationDT.setText(detail.duringTraining ?: "") // Use setText for EditText
                            homeFragmentBinding.DeterminationAT.setText(detail.afterTraining ?: "") // Use setText for EditText
                        }
                        "Anxiety" -> {
                            homeFragmentBinding.AnxietyBT.setText(detail.beforeTraining ?: "") // Use setText for EditText
                            homeFragmentBinding.AnxietyDT.setText(detail.duringTraining ?: "") // Use setText for EditText
                            homeFragmentBinding.AnxietyAT.setText(detail.afterTraining ?: "") // Use setText for EditText
                        }
                        "Tiredness" -> {
                            homeFragmentBinding.TirednessBT.setText(detail.beforeTraining ?: "") // Use setText for EditText
                            homeFragmentBinding.TirednessDT.setText(detail.duringTraining ?: "") // Use setText for EditText
                            homeFragmentBinding.TirednessAT.setText(detail.afterTraining ?: "") // Use setText for EditText
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
                        homeFragmentBinding.homeProgress.visibility = View.GONE
                    }
                })


                return true
            } else {
                return false
            }
        }

        override fun onItemClicked(view: View, position: Int, type: Long, string: String) {

            WorkOutlist
            when (position) {
                0 -> {
                    homeFragmentBinding.informationLinear.visibility = View.GONE
                    homeFragmentBinding.InstructionLinera.visibility = View.GONE
                    homeFragmentBinding.NewsLinera.visibility = View.VISIBLE

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