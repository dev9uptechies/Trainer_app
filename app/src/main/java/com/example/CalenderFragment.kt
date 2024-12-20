package com.example

import android.annotation.SuppressLint
import android.content.Context.MODE_PRIVATE
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.Adapter.selected_day.LessonAdapter
import com.example.Adapter.selected_day.eventAdapter
import com.example.Adapter.selected_day.testAdapter
import com.example.model.SelectedDaysModel
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.PreferencesManager
import com.example.trainerapp.R
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.Example3CalendarDayBinding
import com.example.trainerapp.databinding.Example3CalendarHeaderBinding
import com.example.trainerapp.databinding.FragmentCalenderBinding
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
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale

class CalenderFragment : Fragment(), OnItemClickListener.OnItemClickCallback {
    lateinit var preferenceManager: PreferencesManager
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    private var receivedId: String = ""
    lateinit var lessonadapter: LessonAdapter
    lateinit var eventadapter: eventAdapter
    lateinit var testadapter: testAdapter
    var date: String = ""
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

    private val datesWithData = mutableSetOf<LocalDate>() // Set to track dates with data



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

        return calenderBinding.root
    }

    @SuppressLint("NewApi")
    private fun loadData() {
        selectDate(LocalDate.now())
        fetchDayData(LocalDate.now())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpCalendar() {
        try {

        val daysOfWeek = daysOfWeekFromLocale()
        val currentMonth = YearMonth.now()

        calendarView!!.apply {
            setup(currentMonth.minusMonths(10), currentMonth.plusMonths(10), daysOfWeek.first())
            scrollToMonth(currentMonth)
        }

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
                        if (datesWithData.contains(day.date)) {
                            container.binding.dotLinear.visibility = View.VISIBLE
                        } else {
                            container.binding.dotLinear.visibility = View.GONE
                        }

                    } else {
                        textView.setTextColor(resources.getColor(R.color.grey))
                    }
                }
            }

        calendarView!!.monthScrollListener = {
            if (it.year == today.year) {
                titleSameYearFormatter.format(it.yearMonth)
            } else {
                titleFormatter.format(it.yearMonth)
            }
//            selectDate(it.yearMonth.atDay(1))
        }

        calendarView!!.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
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

        }catch (e:Exception){
            Log.e("error","Catch:- ${e.message.toString()}")
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

            fetchDayData(date)
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
    private fun fetchDayData(selectedDate: LocalDate) {
        try {
            val formattedDate = selectionFormatter.format(selectedDate)
            Log.d("CalendarFragment", "Fetching data for date: $formattedDate with ID: $receivedId")

            apiInterface.GetSelectedDays(formattedDate, receivedId)!!.enqueue(object : Callback<SelectedDaysModel> {
                override fun onResponse(call: Call<SelectedDaysModel>, response: Response<SelectedDaysModel>) {
                    if (response.isSuccessful && response.body() != null) {
                        val selectedDaysModel = response.body()
                        Log.d("API Response", "Response: $selectedDaysModel")

                        val data = selectedDaysModel?.data
                        if (data != null) {
                            if (::eventadapter.isInitialized) {
                                eventadapter.clearData()
                            }
                            initTestRecyclerView(data.tests)
                            initLessonRecyclerView(data.lessons)
                            initEventRecyclerView(data.events)

                            // Add or remove the dot based on the data
                            if (data.tests.isNotEmpty() || data.lessons.isNotEmpty() || data.events.isNotEmpty()) {
                                if (!datesWithData.contains(selectedDate)) {
                                    datesWithData.add(selectedDate)
                                    // Notify the calendar view to update this date
                                    calendarView!!.notifyDateChanged(selectedDate)
                                }
                            } else {
                                if (datesWithData.contains(selectedDate)) {
                                    datesWithData.remove(selectedDate)
                                    // Notify the calendar view to remove the dot
                                    calendarView!!.notifyDateChanged(selectedDate)
                                }
                            }
                        } else {
                            Log.e("API Response", "Data is null. No dot added.")
                            // Remove any dots for this date if data is null
                            datesWithData.remove(selectedDate)
                            calendarView!!.notifyDateChanged(selectedDate)
                        }
                    } else {
                        Log.e("API Response", "Failed to fetch data: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<SelectedDaysModel>, t: Throwable) {
                    Log.e("API Response", "Error: ${t.message}")
                }
            })
        } catch (e: Exception) {
            Log.e("Catch", "CatchError :- ${e.message}")
        }
    }




    private fun initLessonRecyclerView(programs: List<SelectedDaysModel.Lesson>) {
        calenderBinding.rcvlesson.layoutManager = LinearLayoutManager(requireContext())
        lessonadapter = LessonAdapter(programs, requireContext(), this)
        calenderBinding.rcvlesson.adapter = lessonadapter
    }

    private fun initTestRecyclerView(tests: List<SelectedDaysModel.Test>) {
        calenderBinding.rcvtest.layoutManager = LinearLayoutManager(requireContext())
        testadapter = testAdapter(tests, requireContext(), this)
        calenderBinding.rcvtest.adapter = testadapter
    }

    private fun initEventRecyclerView(events: List<SelectedDaysModel.Event>) {
        if (events.isNotEmpty()) {
            Log.d("Event RecyclerView", "Setting up RecyclerView with events.")
            calenderBinding.rcvevent.layoutManager = LinearLayoutManager(requireContext())
            eventadapter = eventAdapter(events, requireContext(), this)
            calenderBinding.rcvevent.adapter = eventadapter
        } else {
            Log.d("Event RecyclerView", "No events available.")
        }
    }

    private fun initViews() {
        apiClient = APIClient(requireContext())
        preferenceManager = PreferencesManager(requireContext())
        apiInterface = apiClient.client().create(APIInterface::class.java)

        val sharedPreferences = requireActivity().getSharedPreferences("AppPreferences", MODE_PRIVATE)
        receivedId = sharedPreferences.getString("id", "default_value") ?: ""

        Log.d("CalenderFragment", "Received ID from SharedPreferences: $receivedId")
    }

    override fun onItemClicked(view: View, position: Int, type: Long, string: String) {
        if (string == "fav") {
            calenderBinding.progressCalender.visibility = View.VISIBLE
            val id: MultipartBody.Part =
                MultipartBody.Part.createFormData("id", type.toInt().toString())
            apiInterface.Favourite_lession(id)?.enqueue(object : Callback<RegisterData?> {
                override fun onResponse(
                    call: Call<RegisterData?>,
                    response: Response<RegisterData?>
                ) {
                    calenderBinding.progressCalender.visibility = View.GONE
                    Log.d("TAG", response.code().toString() + "")
                    val code = response.code()
                    if (code == 200) {
                        val resource: RegisterData? = response.body()
                        val Success: Boolean = resource?.status!!
                        val Message: String = resource.message!!
                        if (Success) {
                            calenderBinding.progressCalender.visibility = View.GONE
                            loadData()
                        } else {
                            calenderBinding.progressCalender.visibility = View.GONE
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
                        calenderBinding.progressCalender.visibility = View.GONE
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
                    calenderBinding.progressCalender.visibility = View.GONE
                    Toast.makeText(requireContext(), "" + t.message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }
            })
        } else if (string == "unfav") {
            calenderBinding.progressCalender.visibility = View.VISIBLE
            val id: MultipartBody.Part =
                MultipartBody.Part.createFormData("id", type.toInt().toString())
            apiInterface.DeleteFavourite_lession(type.toInt())
                ?.enqueue(object : Callback<RegisterData?> {
                    override fun onResponse(
                        call: Call<RegisterData?>,
                        response: Response<RegisterData?>
                    ) {
                        Log.d("TAG", response.code().toString() + "")
                        calenderBinding.progressCalender.visibility = View.GONE
                        val code = response.code()
                        if (code == 200) {
                            val resource: RegisterData? = response.body()
                            val Success: Boolean = resource?.status!!
                            val Message: String = resource.message!!
                            if (Success) {
                                calenderBinding.progressCalender.visibility = View.GONE
                                loadData()
                            } else {
                                calenderBinding.progressCalender.visibility = View.GONE
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
                        calenderBinding.progressCalender.visibility = View.GONE
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
            calenderBinding.progressCalender.visibility = View.VISIBLE
            val id: MultipartBody.Part =
                MultipartBody.Part.createFormData("id", type.toInt().toString())
            apiInterface.Favourite_Event(id)?.enqueue(object : Callback<RegisterData?> {
                override fun onResponse(
                    call: Call<RegisterData?>,
                    response: Response<RegisterData?>
                ) {
                    calenderBinding.progressCalender.visibility = View.GONE
                    Log.d("TAG", response.code().toString() + "")
                    val code = response.code()
                    if (code == 200) {
                        val resource: RegisterData? = response.body()
                        val Success: Boolean = resource?.status!!
                        val Message: String = resource.message!!
                        if (Success) {
                            calenderBinding.progressCalender.visibility = View.GONE
                            loadData()
                        } else {
                            calenderBinding.progressCalender.visibility = View.GONE
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
                        calenderBinding.progressCalender.visibility = View.GONE
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
                    calenderBinding.progressCalender.visibility = View.GONE
                    Toast.makeText(requireContext(), "" + t.message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }
            })
        } else if (string == "unfavevent") {
            calenderBinding.progressCalender.visibility = View.VISIBLE
            val id: MultipartBody.Part =
                MultipartBody.Part.createFormData("id", type.toInt().toString())
            apiInterface.DeleteFavourite_Event(type.toInt())
                ?.enqueue(object : Callback<RegisterData?> {
                    override fun onResponse(
                        call: Call<RegisterData?>,
                        response: Response<RegisterData?>
                    ) {
                        Log.d("TAG", response.code().toString() + "")
                        calenderBinding.progressCalender.visibility = View.GONE
                        val code = response.code()
                        if (code == 200) {
                            val resource: RegisterData? = response.body()
                            val Success: Boolean = resource?.status!!
                            val Message: String = resource.message!!
                            if (Success) {
                                calenderBinding.progressCalender.visibility = View.GONE
                                loadData()
                            } else {
                                calenderBinding.progressCalender.visibility = View.GONE
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
                        calenderBinding.progressCalender.visibility = View.GONE
                        Toast.makeText(
                            requireContext(),
                            "" + t.message,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                })
        } else if (string == "favtest"){
            calenderBinding.progressCalender.visibility = View.VISIBLE
            val id: MultipartBody.Part =
                MultipartBody.Part.createFormData("id", type.toInt().toString())
            apiInterface.Favourite_Test(id)?.enqueue(object : Callback<RegisterData?> {
                override fun onResponse(
                    call: Call<RegisterData?>,
                    response: Response<RegisterData?>
                ) {
                    calenderBinding.progressCalender.visibility = View.GONE
                    Log.d("TAG", response.code().toString() + "")
                    val code = response.code()
                    if (code == 200) {
                        val resource: RegisterData? = response.body()
                        val Success: Boolean = resource?.status!!
                        val Message: String = resource.message!!
                        if (Success) {
                            calenderBinding.progressCalender.visibility = View.GONE
                            loadData()
                        } else {
                            calenderBinding.progressCalender.visibility = View.GONE
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
                        calenderBinding.progressCalender.visibility = View.GONE
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
                    calenderBinding.progressCalender.visibility = View.GONE
                    Toast.makeText(requireContext(), "" + t.message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }
            })
        } else if (string == "unfavtest") {
            calenderBinding.progressCalender.visibility = View.VISIBLE
            var id: MultipartBody.Part =
                MultipartBody.Part.createFormData("id", type.toInt().toString())
            apiInterface.DeleteFavourite_Test(type.toInt())
                ?.enqueue(object : Callback<RegisterData?> {
                    override fun onResponse(
                        call: Call<RegisterData?>,
                        response: Response<RegisterData?>
                    ) {
                        Log.d("TAG", response.code().toString() + "")
                        calenderBinding.progressCalender.visibility = View.GONE
                        val code = response.code()
                        if (code == 200) {
                            val resource: RegisterData? = response.body()
                            val Success: Boolean = resource?.status!!
                            val Message: String = resource.message!!
                            if (Success) {
                                calenderBinding.progressCalender.visibility = View.GONE

                            } else {
                                calenderBinding.progressCalender.visibility = View.GONE
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
                        calenderBinding.progressCalender.visibility = View.GONE
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