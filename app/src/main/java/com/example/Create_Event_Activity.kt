package com.example

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.EventListData
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.GetAthletesActivity
import com.example.trainerapp.PreferencesManager
import com.example.trainerapp.R
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.ActivityCreateEventBinding
import com.example.trainerapp.databinding.Example3CalendarDayBinding
import com.example.trainerapp.databinding.Example3CalendarHeaderBinding
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
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
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Date
import java.util.Locale
import kotlin.math.E

class Create_Event_Activity : AppCompatActivity(), OnItemClickListener.OnItemClickCallback {
    lateinit var createEventBinding: ActivityCreateEventBinding
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    lateinit var adapter: EventDataAdapter
    lateinit var EventList: ArrayList<EventListData.testData>
    lateinit var preferenceManager: PreferencesManager
    var calendarView: CalendarView? = null
    private lateinit var id: ArrayList<Int>
    private lateinit var Aid: ArrayList<Int>
    private lateinit var name: ArrayList<String>
    private lateinit var Aname: ArrayList<String>
    var eventId = ""

    var eventid: String = ""
    var title: String = ""
    var athletename: String = ""
    var date: String = ""
    var typed: String = ""
    var fromDay: Boolean = false

    var EventLPosition: Int? = null
    var EventLberyid: Int? = null

    var EventPositionGroup: Int? = null
    var EventIdGroup: Int? = null

    var type = "create"
    var From = "ViewOnly"
    private var types: String? = ""
    private var position1: Int? = 0

    @RequiresApi(Build.VERSION_CODES.O)
    private val today = LocalDate.now()
    var unitArray = ArrayList<String>()

    @RequiresApi(Build.VERSION_CODES.O)
    private val titleSameYearFormatter = DateTimeFormatter.ofPattern("MMMM")

    @RequiresApi(Build.VERSION_CODES.O)
    private val titleFormatter = DateTimeFormatter.ofPattern("MMM yyyy")
    private var selectedDate: LocalDate? = null

    @RequiresApi(Build.VERSION_CODES.O)
    private val selectionFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    private fun setUnitData() {
        unitArray.add("Competition")
        unitArray.add("Stage")
        unitArray.add("WorkShop")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createEventBinding = ActivityCreateEventBinding.inflate(layoutInflater)
        setContentView(createEventBinding.root)

//        position1 = intent.getIntExtra("position", 0)
//        types = intent.getStringExtra("type")

        val position1 = intent.getIntExtra("position", 0)
        val types = intent.getStringExtra("type")
        From = intent.getStringExtra("From").toString()

        Log.e("CXXCXCXCXC", "Position: $position1, Type: $types")
        Log.e("CXXCXCXCXC", "onCreate: " + types)

        initViews()
        loadData()

        if (From == "ViewOnly") {
            createEventBinding.eventName.isFocusable = false
            createEventBinding.eventName.isFocusableInTouchMode = false
            createEventBinding.eventName.isClickable = false

            createEventBinding.edtTest.isFocusable = false
            createEventBinding.edtTest.isFocusableInTouchMode = false
            createEventBinding.edtTest.isClickable = false

            createEventBinding.tvAthelate.isFocusable = false
            createEventBinding.tvAthelate.isFocusableInTouchMode = false
            createEventBinding.tvAthelate.isClickable = false

            createEventBinding.calenderView.isEnabled = false
            createEventBinding.calenderView.isClickable = false
            createEventBinding.calenderView.isFocusable = false

            createEventBinding.etEnterTest.isFocusable = false
            createEventBinding.etEnterTest.isFocusableInTouchMode = false
            createEventBinding.etEnterTest.isClickable = false

            createEventBinding.cardEvent.isFocusable = false
            createEventBinding.cardEvent.isFocusableInTouchMode = false
            createEventBinding.cardEvent.isClickable = false

            createEventBinding.cardTest.isFocusable = false
            createEventBinding.cardTest.isFocusableInTouchMode = false
            createEventBinding.cardTest.isClickable = false
            createEventBinding.cardTest.isEnabled = false


        }

        if (EventLberyid.toString() != "0" || EventLberyid != 0) {

            type = "edit"
            geteventlistLibaray()
//            Log.d("okokokok", "onCreate: $programData")
        } else {
            type = "create"
        }

        if (EventIdGroup.toString() != "0" || EventIdGroup != 0) {

            type = "edit"
            geteventlistGroup()
//            Log.d("okokokok", "onCreate: $programData")
        } else {
            type = "create"
        }




        Log.d("SDSBSBSBS", "onCreate: $eventid")

        if (types != null) {
            Log.e("HBBHBHBHBH", "onCreate: " + types)
            eventId = types.toString()
            setEventData(EventList[position1!!])
        }


        if (preferenceManager.getselectAthelete()) {
            name = getObject(this, "setAthleteName") as ArrayList<String>
            id = getObjectJson(this, "setAthlete") as ArrayList<Int>
        }

        textChangeListener()
        checkButtonClick()

        if (fromDay == false) {
            if (savedInstanceState == null) {
                calendarView!!.post {
                    selectDate(today)
                }
            }
        }


    }

    @SuppressLint("NewApi")
    private fun getData() {
        eventid = intent.getIntExtra("id", 0).toString()
        fromDay = intent.getBooleanExtra("fromday", false)
        title = intent.getStringExtra("name").toString()
        athletename = intent.getStringExtra("athlete").toString()
        date = intent.getStringExtra("date").toString().take(10)
        typed = intent.getStringExtra("typed").toString()
        Aid = intent.getIntegerArrayListExtra("athleteId") ?: arrayListOf()
        Aname = intent.getStringArrayListExtra("athlete") ?: arrayListOf()

        Log.d("SHSHSHSH", "getData: $date")

        if (fromDay == true) {
            Log.e("KIRTIIIIIIIIII", "initViews: " + date)
            createEventBinding.eventName.setText(title)
            createEventBinding.edtTest.setText(typed)
            val namesString = Aname.joinToString(", ")
            createEventBinding.tvAthelate.setText(namesString)

            val date = date!!.split("T")[0]

            selectDate(LocalDate.parse(date))

            updateUI(createEventBinding.saveEvent)

        }
    }


    private fun textChangeListener() {
        createEventBinding.eventName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateUI(createEventBinding.saveEvent)
            }

            override fun afterTextChanged(s: Editable?) {
                updateUI(createEventBinding.saveEvent)
            }
        })

        createEventBinding.edtTest.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateUI(createEventBinding.saveEvent)
            }

            override fun afterTextChanged(s: Editable?) {
                updateUI(createEventBinding.saveEvent)
            }
        })

        createEventBinding.tvAthelate.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    updateUI(createEventBinding.saveEvent)
                }

                override fun afterTextChanged(s: Editable?) {
                    updateUI(createEventBinding.saveEvent)
                }
            }
        )
    }


    private fun areAllFieldsFilled(): Boolean {
        return (createEventBinding.edtTest.text.isNotEmpty() &&
                createEventBinding.tvAthelate.text.trim() != "Enter Intrested Atheletes" &&
                !createEventBinding.eventName.text.isNullOrEmpty())
    }

    private fun updateUI(addButton: CardView) {
        if (areAllFieldsFilled()) {
            addButton.isEnabled = true
            addButton.setCardBackgroundColor(resources.getColor(R.color.splash_text_color))
        } else {
            addButton.isEnabled = true
            addButton.setCardBackgroundColor(resources.getColor(R.color.grey))
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadData() {
        resetData()
        setUnitData()
        geteventlist()
        setUpCalendar()
        getData()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun resetData() {
        type = "create"
        createEventBinding.edtTest.text.clear()
        createEventBinding.eventName.text.clear()
        createEventBinding.tvAthelate.text = "Enter Intrested Atheletes"
        createEventBinding.saveEvent.isEnabled = false
        createEventBinding.saveEvent.setCardBackgroundColor(resources.getColor(R.color.grey))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpCalendar() {
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
    }

    private fun checkButtonClick() {

        createEventBinding.back.setOnClickListener {
            finish()
        }

        createEventBinding.cardEvent.setOnClickListener {

            Log.d("type :-", "$type")
            if (type == "create") {
                val i = Intent(this, GetAthletesActivity::class.java).apply {
                    putExtra("type", "create")
                }
                startActivity(i)
            } else if (type == "edit") {
                Log.d("test Id:", "$id")
                val arrayList: ArrayList<Int> = ArrayList(id)
                val i = Intent(this, GetAthletesActivity::class.java).apply {
                    putExtra("type", "edit")
                    putIntegerArrayListExtra("testId", arrayList)
                }
                startActivity(i)
            }

//            val i = Intent(this, GetAthletesActivity::class.java)
//            startActivity(i)
        }

        createEventBinding.edtTest.setOnClickListener {
            showTypePopup(it)
//                updateUI(testBinding.btnSaveTest)
//            updateUI(testBinding.nextCard)
        }

        createEventBinding.saveEvent.setOnClickListener {
            when {
                type == "edit" || fromDay -> {
                    updateData()
                }

                type == "create" -> {
                    saveData()
                }

                else -> {
                    Toast.makeText(this, "Invalid operation", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    private fun updateData() {
        createEventBinding.progressBar.visibility = View.VISIBLE
        if (!createEventBinding.eventName.text.isEmpty() && !createEventBinding.tvAthelate.text.isEmpty()) {


            val AidToUse = if (id.isNullOrEmpty()) {
                Aid
            } else {
                id
            }

            val str = arrayOfNulls<Int>(AidToUse.size)
            val array = JsonArray()

            for (i in 0 until AidToUse.size) {
                str[i] = AidToUse.get(i)
                array.add(AidToUse.get(i))
            }

            val idToUse = if (eventId.isNullOrEmpty()) {
                eventid
            } else {
                eventId.toInt().toString()
            }

            val finalID =
                if (idToUse == null || idToUse.isNullOrEmpty() || idToUse == "0") EventLberyid else idToUse

            val finalGroupID =
                if (finalID == null || finalID == 0 || finalID == "0") EventIdGroup else finalID

            Log.d("KDKKKDK", "editData: $finalID    $idToUse")


            val jsonObject = JsonObject()
            jsonObject.addProperty("id", finalGroupID.toString())
            jsonObject.addProperty("name", createEventBinding.eventName.text.toString())
            jsonObject.addProperty("type", createEventBinding.edtTest.text.toString())
            jsonObject.add("athlete_ids", array)
            jsonObject.addProperty(
                "date",
                createEventBinding.exThreeSelectedDateText.text.toString()
            )

            apiInterface.UpdateEvent(
                jsonObject
            )?.enqueue(object : Callback<RegisterData?> {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(
                    call: Call<RegisterData?>,
                    response: Response<RegisterData?>
                ) {
                    Log.d("TAG", response.code().toString() + "")
                    val resource: RegisterData? = response.body()
                    val Success: Boolean = resource?.status!!
                    val Message: String = resource.message!!
                    createEventBinding.progressBar.visibility = View.GONE
                    if (Success) {
                        preferenceManager.setselectAthelete(false)
                        Toast.makeText(
                            this@Create_Event_Activity,
                            "" + Message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    loadData()
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    createEventBinding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        this@Create_Event_Activity,
                        "" + t.message,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    call.cancel()
                }
            })

        }
    }

    private fun saveData() {

        if (!areAllFieldsFilled()) {
            Log.d("DDDJ JNJNJ", "saveData: ")
            return
        }

        createEventBinding.progressBar.visibility = View.VISIBLE
        if (!createEventBinding.eventName.text.isEmpty() && !createEventBinding.tvAthelate.text.isEmpty()) {
            val str = arrayOfNulls<Int>(id.size)
            val array = JsonArray()

            for (i in 0 until id.size) {
                str[i] = id.get(i)
                array.add(id.get(i))
            }
            val jsonObject = JsonObject()
            jsonObject.addProperty("name", createEventBinding.eventName.text.toString())
            jsonObject.addProperty("type", createEventBinding.edtTest.text.toString())
            jsonObject.add("athlete_ids", array)
            jsonObject.addProperty(
                "date",
                createEventBinding.exThreeSelectedDateText.text.toString()
            )

            apiInterface.CreateEvent(
                jsonObject
            )?.enqueue(object : Callback<RegisterData?> {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(
                    call: Call<RegisterData?>,
                    response: Response<RegisterData?>
                ) {
                    Log.d("TAG", response.code().toString() + "")
                    val resource: RegisterData? = response.body()
                    val Success: Boolean = resource?.status!!
                    val Message: String = resource.message!!
                    createEventBinding.progressBar.visibility = View.GONE
                    if (Success) {
                        preferenceManager.setselectAthelete(false)
                        Toast.makeText(
                            this@Create_Event_Activity,
                            "" + Message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    loadData()
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    createEventBinding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        this@Create_Event_Activity,
                        "" + t.message,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    call.cancel()
                }
            })

        }
    }

    private fun showTypePopup(anchorView: View?) {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.popup_list, null)


        val weightInPixels = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, // The unit type (dp)
            330f, // The value in dp
            resources.displayMetrics // The display metrics
        ).toInt()

        val popupWindow = PopupWindow(
            popupView,
//            ViewGroup.LayoutParams.WRAP_CONTENT,
            weightInPixels,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true // Focusable to allow outside clicks to dismiss
        )


        popupWindow.setBackgroundDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.popup_background
            )
        )
        popupWindow.elevation = 10f
        val listView = popupView.findViewById<ListView>(R.id.listView)
        val adapter =
            object : ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, unitArray) {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val view = super.getView(position, convertView, parent) as TextView
                    val typeface =
                        ResourcesCompat.getFont(this@Create_Event_Activity, R.font.poppins_medium)
                    view.typeface = typeface
                    view.setTextColor(Color.WHITE) // Set text color to white
                    return view
                }
            }
        listView.adapter = adapter
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = unitArray[position]
            createEventBinding.edtTest.setText(selectedItem)
            println("Selected item: $selectedItem")
            popupWindow.dismiss()
        }
        popupWindow.showAsDropDown(anchorView)
        popupWindow.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                this,
                android.R.color.white
            )
        )
    }

    private fun initViews() {
        preferenceManager = PreferencesManager(this)
        id = ArrayList<Int>()
        Aid = ArrayList<Int>()
        name = ArrayList<String>()
        Aname = ArrayList<String>()
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        calendarView = findViewById(R.id.calenderView)
        EventList = ArrayList()
        EventLPosition = intent.getIntExtra("EventLibraryPosition", 0)
        EventLberyid = intent.getIntExtra("EventLibraryId", 0)
        EventPositionGroup = intent.getIntExtra("EventPositionGroup", 0)
        EventIdGroup = intent.getIntExtra("EventIdGroup", 0)
        Log.d("SSJSSJJS", "initView: $EventPositionGroup     $EventIdGroup")
    }

    private fun geteventlist() {
        createEventBinding.progressBar.visibility = View.VISIBLE
        apiInterface.GetEvent()?.enqueue(object : Callback<EventListData?> {
            override fun onResponse(
                call: Call<EventListData?>,
                response: Response<EventListData?>
            ) {
                Log.d("TAG", response.code().toString() + "")
                val code = response.code()
                if (code == 200) {
                    val resource: EventListData? = response.body()
                    val Success: Boolean = resource?.status!!
                    val Message: String = resource.message!!
                    if (Success == true) {
                        if (resource.data != null) {
                            EventList = resource.data!!
                            initrecycler(resource.data)
                        } else {
                            initrecycler(arrayListOf())
                        }
                    } else {
                        createEventBinding.progressBar.visibility = View.GONE
                    }
                } else if (code == 403) {
                    Utils.setUnAuthDialog(this@Create_Event_Activity)
                } else {
                    val Message = response.message()
                    Toast.makeText(
                        this@Create_Event_Activity,
                        "" + Message,
                        Toast.LENGTH_SHORT
                    ).show()
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<EventListData?>, t: Throwable) {
                createEventBinding.progressBar.visibility = View.GONE
                Toast.makeText(this@Create_Event_Activity, "" + t.message, Toast.LENGTH_SHORT)
                    .show()
                call.cancel()
            }
        })
    }

    private fun geteventlistLibaray() {
        createEventBinding.progressBar.visibility = View.VISIBLE
        apiInterface.GetEvent()?.enqueue(object : Callback<EventListData?> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(
                call: Call<EventListData?>,
                response: Response<EventListData?>
            ) {
                Log.d("TAG", response.code().toString() + "")
                val code = response.code()
                if (code == 200) {
                    val resource: EventListData? = response.body()
                    val Success: Boolean = resource?.status!!
                    val Message: String = resource.message!!
                    if (Success == true) {
                        if (resource.data != null) {
                            EventList = resource.data!!
                            initrecycler(resource.data)
                            setEventData(EventList[EventLPosition!!.toInt()])

                        } else {
                            initrecycler(arrayListOf())
                        }
                    } else {
                        createEventBinding.progressBar.visibility = View.GONE
                    }
                } else if (code == 403) {
                    Utils.setUnAuthDialog(this@Create_Event_Activity)
                } else {
                    val Message = response.message()
                    Toast.makeText(
                        this@Create_Event_Activity,
                        "" + Message,
                        Toast.LENGTH_SHORT
                    ).show()
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<EventListData?>, t: Throwable) {
                createEventBinding.progressBar.visibility = View.GONE
                Toast.makeText(this@Create_Event_Activity, "" + t.message, Toast.LENGTH_SHORT)
                    .show()
                call.cancel()
            }
        })
    }

    private fun geteventlistGroup() {
        createEventBinding.progressBar.visibility = View.VISIBLE
        apiInterface.GetEvent()?.enqueue(object : Callback<EventListData?> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(
                call: Call<EventListData?>,
                response: Response<EventListData?>
            ) {
                Log.d("TAG", response.code().toString() + "")
                val code = response.code()
                if (code == 200) {
                    val resource: EventListData? = response.body()
                    val Success: Boolean = resource?.status!!
                    val Message: String = resource.message!!
                    if (Success == true) {
                        if (resource.data != null) {
                            EventList = resource.data!!
                            initrecycler(resource.data)
                            setEventData(EventList[EventPositionGroup!!.toInt()])

                        } else {
                            initrecycler(arrayListOf())
                        }
                    } else {
                        createEventBinding.progressBar.visibility = View.GONE
                    }
                } else if (code == 403) {
                    Utils.setUnAuthDialog(this@Create_Event_Activity)
                } else {
                    val Message = response.message()
                    Toast.makeText(
                        this@Create_Event_Activity,
                        "" + Message,
                        Toast.LENGTH_SHORT
                    ).show()
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<EventListData?>, t: Throwable) {
                createEventBinding.progressBar.visibility = View.GONE
                Toast.makeText(this@Create_Event_Activity, "" + t.message, Toast.LENGTH_SHORT)
                    .show()
                call.cancel()
            }
        })
    }

    private fun initrecycler(testdatalist: ArrayList<EventListData.testData>?) {
        createEventBinding.progressBar.visibility = View.GONE
        createEventBinding.eventListRly.layoutManager = LinearLayoutManager(this)
        adapter = EventDataAdapter(testdatalist, this, this)
        createEventBinding.eventListRly.adapter = adapter
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun selectDate(date: LocalDate) {
        Log.e("SUJALLLLLLL", "selectDate: " + date)
        if (selectedDate != date) {

            val oldDate = selectedDate
            selectedDate = date

            Log.d(
                "Select Date month:-",
                "select date :-$selectedDate \n date :- $date \n old date :-$oldDate"
            )

            oldDate?.let { createEventBinding.calenderView.notifyDateChanged(it) }
            createEventBinding.calenderView.notifyDateChanged(date)
            createEventBinding.calenderView.scrollToMonth(YearMonth.from(date))
//            createEventBinding.calenderView.smoothScrollToDate(date)
            updateAdapterForDate(date)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateAdapterForDate(date: LocalDate) {
        createEventBinding.exThreeSelectedDateText.text = selectionFormatter.format(date)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getLocalDateFromDate(date: Date): LocalDate? {
        return LocalDate.from(Instant.ofEpochMilli(date.time).atZone(ZoneId.systemDefault()))
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

    fun getObjectJson(c: Context, key: String): List<Int> {
        val appSharedPrefs: SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(c.applicationContext)
        val json = appSharedPrefs.getString(key, "")
        val gson = Gson()
        val type = object : TypeToken<List<Int>>() {}.type
        val arrayList: List<Int> = gson.fromJson<List<Int>>(json, type)
        return arrayList
    }

    fun getObject(c: Context, key: String): List<String> {
        val appSharedPrefs: SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(c.applicationContext)
        val json = appSharedPrefs.getString(key, "")
        val gson = Gson()
        val type = object : TypeToken<List<String>>() {}.type
        val arrayList: List<String> = gson.fromJson<List<String>>(json, type)
        return arrayList
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onItemClicked(view: View, position: Int, type: Long, string: String) {
        if (string == "Delete") {
            var builder: AlertDialog.Builder
            builder = AlertDialog.Builder(this)
            builder.setMessage("Are you sure you want to delete Event?").setTitle("Delete")
            builder.setMessage("Are you sure you want to delete Event?")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialog, id ->
                    createEventBinding.progressBar.visibility = View.VISIBLE
                    apiInterface.DeleteEvent(type.toInt())
                        ?.enqueue(object : Callback<RegisterData?> {
                            override fun onResponse(
                                call: Call<RegisterData?>,
                                response: Response<RegisterData?>
                            ) {
                                Log.d("TAG", response.code().toString() + "")
                                val code = response.code()
                                if (code == 200) {
                                    val resource: RegisterData? = response.body()
                                    val Success: Boolean = resource?.status!!
                                    val Message: String = resource.message!!
                                    createEventBinding.progressBar.visibility = View.GONE
                                    Toast.makeText(
                                        this@Create_Event_Activity,
                                        "" + Message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    loadData()
                                } else if (code == 403) {
                                    Utils.setUnAuthDialog(this@Create_Event_Activity)
                                } else {
                                    val Message = response.message()
                                    Toast.makeText(
                                        this@Create_Event_Activity,
                                        "" + Message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    call.cancel()
                                }
                            }

                            override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                                Toast.makeText(
                                    this@Create_Event_Activity,
                                    "" + t.message,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                call.cancel()
                            }
                        })
                }
                .setNegativeButton(
                    "No"
                ) { dialog, id -> //  Action for 'NO' Button
                    dialog.cancel()
                }
            val alert = builder.create()
            alert.setTitle("Delete")
            alert.show()

        } else if (string == "fav") {
            createEventBinding.progressBar.visibility = View.VISIBLE
            val id: MultipartBody.Part =
                MultipartBody.Part.createFormData("id", type.toInt().toString())
            apiInterface.Favourite_Event(id)?.enqueue(object : Callback<RegisterData?> {
                override fun onResponse(
                    call: Call<RegisterData?>,
                    response: Response<RegisterData?>
                ) {
                    Log.d("TAG", response.code().toString() + "")
                    val code = response.code()
                    if (code == 200) {
                        val resource: RegisterData? = response.body()
                        val Success: Boolean = resource?.status!!
                        val Message: String = resource.message!!
                        if (Success) {
                            createEventBinding.progressBar.visibility = View.GONE
                            Toast.makeText(
                                this@Create_Event_Activity,
                                "" + Message,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            loadData()
                        } else {
                            createEventBinding.progressBar.visibility = View.GONE
                            Toast.makeText(
                                this@Create_Event_Activity,
                                "" + Message,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@Create_Event_Activity)
                    } else {
                        val Message = response.message()
                        Toast.makeText(
                            this@Create_Event_Activity,
                            "" + Message,
                            Toast.LENGTH_SHORT
                        ).show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    createEventBinding.progressBar.visibility = View.GONE
                    Toast.makeText(this@Create_Event_Activity, "" + t.message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }
            })

        } else if (string == "unfav") {
            createEventBinding.progressBar.visibility = View.VISIBLE
            apiInterface.DeleteFavourite_Event(type.toInt())
                ?.enqueue(object : Callback<RegisterData?> {
                    override fun onResponse(
                        call: Call<RegisterData?>,
                        response: Response<RegisterData?>
                    ) {
                        Log.d("TAG", response.code().toString() + "")
                        val code = response.code()
                        if (code == 200) {
                            val resource: RegisterData? = response.body()
                            val Success: Boolean = resource?.status!!
                            val Message: String = resource.message!!
                            if (Success) {
                                createEventBinding.progressBar.visibility = View.GONE
                                Toast.makeText(
                                    this@Create_Event_Activity,
                                    "" + Message,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                loadData()
                            } else {
                                createEventBinding.progressBar.visibility = View.GONE
                                Toast.makeText(
                                    this@Create_Event_Activity,
                                    "" + Message,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        } else if (code == 403) {
                            Utils.setUnAuthDialog(this@Create_Event_Activity)
                        } else {
                            val Message = response.message()
                            Toast.makeText(
                                this@Create_Event_Activity,
                                "" + Message,
                                Toast.LENGTH_SHORT
                            ).show()
                            call.cancel()
                        }
                    }

                    override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                        createEventBinding.progressBar.visibility = View.GONE
                        Toast.makeText(
                            this@Create_Event_Activity,
                            "" + t.message,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                })
        } else {

            Log.e("TRTRTRTTTTTRTRR", "onItemClicked: " + eventId)
            Log.e("TRTRTRTTTTTRTRR", "onItemClicked: " + EventList)
            this@Create_Event_Activity.type = "edit"
            eventId = EventList[position].id!!.toString()
            setEventData(EventList[position])
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setEventData(testData: EventListData.testData) {
        createEventBinding.eventName.setText(testData.title)
        createEventBinding.edtTest.setText(testData.type)
        createEventBinding.edtTest.setText(testData.type)
        id.clear()
        name.clear()
        for (i in testData.event_athletes!!) {
            id.add(i.athlete!!.id!!)
            name.add(i.athlete!!.name!!)
        }
        val str = arrayOfNulls<String>(name.size)
        val array = JsonArray()

        for (i in 0 until name.size) {
            str[i] = name.get(i)
            array.add(name.get(i))
        }
        val str1 = convertStringArrayToString(str, ",")
        createEventBinding.tvAthelate.text = str1

        val date = testData.date!!.split("T")[0]

        Log.d("SPSLSLS", "setEventData: $date")
        selectDate(LocalDate.parse(date))

//        createEventBinding.saveEvent.isEnabled = true
//        createEventBinding.saveEvent.setCardBackgroundColor(resources.getColor(R.color.splash_text_color))
    }


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
                        Utils.setUnAuthDialog(this@Create_Event_Activity)
                    } else {
                        Toast.makeText(
                            this@Create_Event_Activity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Toast.makeText(
                        this@Create_Event_Activity,
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
        super.onResume()
        checkUser()
        if (preferenceManager.getselectAthelete()) {
//            name = getObject(this, "setAthleteName") as ArrayList<String>
//            val str = arrayOfNulls<String?>(name.size)
//            val array = JsonArray()
//            for (i in 0 until name.size) {
//                str[i] = name.get(i)
//                array.add(name.get(i))
//            }
//            val str1 = convertStringArrayToString(str, ",")
//            createEventBinding.tvAthelate.text = str1
//            id = getObjectJson(this, "setAthlete") as ArrayList<Int>
            name = getObject(this, "setAthleteName") as ArrayList<String>
            id = getObjectJson(this, "setAthlete") as ArrayList<Int>
            if (!name.isNullOrEmpty()) {
                val str = arrayOfNulls<String>(name.size)
                val array = JsonArray()

                // Populate the array and log the progress
                for (i in 0 until name.size) {
                    str[i] = name[i]
                    array.add(name[i])
                }


                // Convert array to a comma-separated string
                val str1 = convertStringArrayToString(str, ",")
                createEventBinding.tvAthelate.text = str1
                updateUI(createEventBinding.saveEvent)
            } else {
                // Handle the case where name is null or empty
                Log.d("name", "Name list is empty or null")
                createEventBinding.tvAthelate.text =
                    "Enter Intrested Atheletes" // Clear the field if no data
            }
        }
    }

    private fun convertStringArrayToString(strArr: Array<String?>, delimiter: String): String? {
        val sb = StringBuilder()
        for (str in strArr) sb.append(str).append(delimiter)
        return sb.substring(0, sb.length - 1)
    }
}