package com.example

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.NumberPicker
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.size
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.Adapter.lesson.SectionLessonAdapter
import com.example.Adapter.selected_day.programAdapter
import com.example.model.SelectedValue
import com.example.model.newClass.lesson.Lesson
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.ProgramListData
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.PreferencesManager
import com.example.trainerapp.R
import com.example.trainerapp.TestListData
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.ActivityLessonBinding
import com.google.android.datatransport.runtime.firebase.transport.LogEventDropped
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class LessonActivity : AppCompatActivity(), OnItemClickListener.OnItemClickCallback,
    SectionLessonAdapter.OnItemClickListener {
    lateinit var lessonBinding: ActivityLessonBinding
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    private lateinit var id: ArrayList<Int>
    private lateinit var Goalid: ArrayList<String>
    private lateinit var lession_data: ArrayList<LessonData.lessionData>
    private lateinit var lession_data1: ArrayList<Lesson.LessonDatabase>
    lateinit var adapter1: Exercise_select_Adapter
    lateinit var adapter: LessionAdapter
    lateinit var sectionAdapter: SectionLessonAdapter
    private lateinit var exercise_list: ArrayList<ProgramListData.testData>
    var age = ArrayList<String>()
    lateinit var preferenceManager: PreferencesManager
    lateinit var goalData: MutableList<TestListData.testData>
    lateinit var sectionData: MutableList<TestListData.testData>
    var Goal = ArrayList<String>()
    var section = ArrayList<String>()
    var goalId = SelectedValue(null)
    var goalIds = SelectedValue(null)
    var sectionId = SelectedValue(null)
    var lessonType = "create"
    var lessonId = ""
    var LessonLibraryId:Int? = 0
    var LessonLibraryPosition:Int? = 0
    var LessonIdGroup:Int? = 0
    var LessonPositionGroup:Int? = 0
    var sectionName = "General Warm-up"

    private val sectionDataMap: MutableMap<String, ArrayList<ProgramListData.testData>> =
        mutableMapOf()

    private lateinit var Lid: ArrayList<Int>
    private lateinit var Lname: ArrayList<String>
    var lessonid: String = ""
    var title: String = ""
    var goal: String = ""
    var date: String = ""
    var ATime: String = ""
    var PTime: String = ""
    var fromDay: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lessonBinding = ActivityLessonBinding.inflate(layoutInflater)
        setContentView(lessonBinding.root)
        initViews()
        loadData()
        checkButtonTap()
        checkUpdateUI()

        if (LessonLibraryId.toString() != "0" && LessonLibraryId!!.toInt() != 0 ) {

            lessonType = "edit"
            GetLessionListLibaray()
        }else{
            lessonType = "create"
        }

        Log.d("SLLSLSLLSLSLSLSL", "onCreate: $LessonLibraryPosition     $LessonLibraryId")

        if (LessonIdGroup.toString() != "0" || LessonIdGroup != 0) {

            lessonType = "edit"
            GetLessionListGroup(LessonIdGroup!!.toInt())
        }else{
            lessonType = "create"
        }

        Log.d("lessonDataatatatata", "onCreate: $lession_data1")

    }

    private fun getData() {

        lessonid = intent.getIntExtra("id", 0).toString()
        fromDay = intent.getBooleanExtra("fromday", false)
        title = intent.getStringExtra("name").toString()
        goal = intent.getStringExtra("goal").toString()
        date = intent.getStringExtra("date").toString().take(10)
        ATime = intent.getStringExtra("AvailableTime").toString()
        PTime = intent.getStringExtra("PortialTime").toString()
        Lid = intent.getIntegerArrayListExtra("lessonId") ?: arrayListOf()
        Lname = intent.getStringArrayListExtra("lesson") ?: arrayListOf()

        if (fromDay == true) {
            Log.e("KIRTIIIIIIIIII", "initViews: " + lessonid)
            lessonBinding.edtLessonName.setText(title)
            val namesString = Lname.joinToString(", ")
            lessonBinding.edtGoal.setText(namesString)
            lessonBinding.tvSelectionTime.setText(ATime)
            lessonBinding.edtTime.setText(PTime)
            lessonBinding.etSelectTestDate.setText(date)
            updateUI(lessonBinding.cardSave)
        }
    }

    private fun checkUpdateUI() {
        lessonBinding.edtLessonName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateUI(lessonBinding.cardSave)
            }

            override fun afterTextChanged(s: Editable?) {
                updateUI(lessonBinding.cardSave)
            }
        })

        lessonBinding.edtGoal.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateUI(lessonBinding.cardSave)
            }

            override fun afterTextChanged(s: Editable?) {
                updateUI(lessonBinding.cardSave)
            }

        })

        lessonBinding.edtTime.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateUI(lessonBinding.cardSave)
            }

            override fun afterTextChanged(s: Editable?) {
                updateUI(lessonBinding.cardSave)
            }
        })

        lessonBinding.tvSelectionTime.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateUI(lessonBinding.cardSave)
            }

            override fun afterTextChanged(s: Editable?) {
                updateUI(lessonBinding.cardSave)
            }
        })

        lessonBinding.etSelectTestDate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateUI(lessonBinding.cardSave)
            }

            override fun afterTextChanged(s: Editable?) {
                updateUI(lessonBinding.cardSave)
            }
        })
    }

    private fun areAllFieldsFilled(): Boolean {
        return (lessonBinding.edtTime.text.toString() != "Available Time" &&
                lessonBinding.edtGoal.text.toString() != "Select Goal" &&
                lessonBinding.tvSelectionTime.text.toString() != "Partial Time" &&
                lessonBinding.etSelectTestDate.text.toString() != "Enter Lesson Date" &&
                !lessonBinding.edtLessonName.text.isNullOrEmpty())
    }

    private fun updateUI(addButton: CardView) {
        if (areAllFieldsFilled()) {
            addButton.isEnabled = true
            addButton.setCardBackgroundColor(resources.getColor(R.color.splash_text_color)) // Change to your desired color
        } else {
            addButton.isEnabled = false
            addButton.setCardBackgroundColor(resources.getColor(R.color.grey)) // Disabled color
        }
    }

    private fun loadData() {
        resetData()
        getSection()
        getGoalData()
        GetLessionList()
        getData()
    }

    private fun resetData() {
        lessonType = "create"
        lessonBinding.edtLessonName.text.clear()
        lessonBinding.edtTime.text.clear()
        lessonBinding.tvSelectionTime.text.clear()
        lessonBinding.etSelectTestDate.text!!.clear()
        lessonBinding.edtGoal.text!!.clear()
        goalId = SelectedValue(null)
        sectionId = SelectedValue(null)
        lessonBinding.cardSave.isEnabled = false
        lessonBinding.cardSave.setCardBackgroundColor(resources.getColor(R.color.grey))
        exercise_list.clear()
        clearAllSectionsData()
        lessonBinding.programRecycler.visibility = View.GONE

    }

    private fun checkButtonTap() {

        lessonBinding.cardLoadExercise.setOnClickListener {

            Log.d("lesson type :-", "$lessonType")
            Log.d("lesson Name :-", "$sectionName")
            if (lessonType == "create") {
                val i = Intent(this, LoadProgramActivity::class.java).apply {
                    putExtra("type", "create")
                    putExtra("SectionName", sectionName)
                }
                startActivity(i)
            } else if (lessonType == "edit") {
                Log.d("test Id:", "$id")
                val arrayList: ArrayList<Int> = ArrayList(id)
                val i = Intent(this, LoadProgramActivity::class.java).apply {
                    putExtra("type", "edit")
                    putExtra("SectionName", sectionName)
                    putIntegerArrayListExtra("lessonId", arrayList)
                }
                startActivity(i)
            }
        }
        lessonBinding.etSelectTestDate.setOnClickListener {
            showDateRangePickerDialog(
                lessonBinding.etSelectTestDate.context,
            ) { start ->
                val formattedDate = formatDate(start)
                val formattedStartDate = formatDate(start)

                lessonBinding.etSelectTestDate.setText(formattedDate)
            }
        }
        lessonBinding.cardDuplicate.setOnClickListener {
            goalDialog()
        }

        lessonBinding.cardSave.setOnClickListener {
            Log.d("lesson type :-", "$lessonType")
            when {
                lessonType == "edit" || fromDay -> {
                    editData()
                }

                lessonType == "create" -> {
                    saveData()
                }

                else -> {
                    Toast.makeText(this, "Invalid operation", Toast.LENGTH_SHORT).show()
                }
            }
        }

        lessonBinding.back.setOnClickListener {
            clearAllSectionsData()
            finish()
        }

        lessonBinding.cardTimeSelect.setOnClickListener {
//            SetDialog()
        }

        lessonBinding.edtGoal.setOnClickListener {
            showPopup(it, goalData, lessonBinding.edtGoal, Goal, goalId)
        }

        lessonBinding.reset.setOnClickListener {
            loadData()
        }

        lessonBinding.tvSelectionTime.setOnClickListener {
            setTimerDialog()
        }

        lessonBinding.edtTime.setOnClickListener {
            Log.d("Text :-", lessonBinding.edtTime.text.toString())
            if (lessonBinding.edtTime.hint.toString() == "Partial Time" && lessonBinding.edtTime.text.toString() == "") {
                setDialog()
            }
        }
    }

    fun showDateRangePickerDialog(
        context: Context,
        callback: (start: Long) -> Unit
    ) {
        val dialog = Dialog(context)
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

        val today = CalendarDay.today()

        // Decorator to disable past dates
        calendarView.addDecorator(object : DayViewDecorator {
            override fun shouldDecorate(day: CalendarDay?): Boolean {
                return day != null && day.isBefore(today)
            }

            override fun decorate(view: DayViewFacade?) {
                view?.setDaysDisabled(true) // Disable past dates
            }
        })

        // Decorator to highlight today's date
        calendarView.addDecorator(object : DayViewDecorator {
            override fun shouldDecorate(day: CalendarDay?): Boolean {
                return day == today
            }

            override fun decorate(view: DayViewFacade?) {
                view?.addSpan(ForegroundColorSpan(Color.WHITE)) // Text color for today
                ContextCompat.getDrawable(context, R.drawable.todays_date_selecte)?.let {
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

                callback(selectedDate.timeInMillis)

                dialog.dismiss()
            } else {
                textView.text = "Please select a date"
                textView.setTextColor(Color.RED)
            }
        }

        dialog.show()
    }

    private fun formatDate2(dateMillis: Long): String {
        val format = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
        return format.format(Date(dateMillis))
    }

    private fun formatDate(dateMillis: Long): String {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return format.format(Date(dateMillis))
    }

    private fun editData() {

        if (isValidate) {
//            id.clear()

//            val LidToUse = if (goalId.id.isNullOrEmpty()) {
//                goalId
//            } else {
//                goalId
//            }

            for (i in 0 until exercise_list.size) {
                id.add(exercise_list[i].id!!.toInt())
            }

            val array = JsonArray()
            for (i in 0 until id.size) {
                array.add(id.get(i))
            }

            Log.d("DKDKDKKKDKDDKD", "editData: $array")
            Log.d("DKDKDKKKDKDDKD", "editData: $id")

            val goalArray = JsonArray()

            for (i in 0..0) {
                goalArray.add(goalId.id)
            }

            val idToUse = if (lessonId.isNullOrEmpty()) {
                lessonid
            } else {
                lessonId.toInt().toString()
            }

            val finalID = if(idToUse == null || idToUse.isNullOrEmpty() || idToUse == "0") LessonLibraryId else idToUse

            val finalIDGroup = if(finalID == null || finalID == "" || finalID == 0) LessonIdGroup else finalID

            Log.d("KDKKKDK", "editData: $finalID    $idToUse     $finalIDGroup")

            val jsonObject = JsonObject()
            jsonObject.addProperty("id", finalIDGroup.toString())
            jsonObject.addProperty("name", lessonBinding.edtLessonName.text.toString())
            jsonObject.add("goal_ids", goalArray)
            jsonObject.addProperty("time", lessonBinding.edtTime.text.toString())
            jsonObject.addProperty("section_time", lessonBinding.tvSelectionTime.text.toString())
            jsonObject.addProperty("section_id", sectionId.id)
            jsonObject.add("program_ids", array)  // Add program_ids from id
            jsonObject.addProperty("date", lessonBinding.etSelectTestDate.text.toString())

            Log.d("Lesson Data :- ", jsonObject.toString())

            lessonBinding.lessionProgress.visibility = View.VISIBLE
            apiInterface.EditLesson(jsonObject)?.enqueue(object : Callback<LessonData?> {
                override fun onResponse(call: Call<LessonData?>, response: Response<LessonData?>) {
                    Log.d("TAG", response.code().toString() + "")
                    val code = response.code()
                    if (code == 200) {
                        val resource: LessonData? = response.body()
                        val Success: Boolean = resource?.status!!
                        val Message: String = resource.message!!
                        if (Success) {
                            lessonBinding.lessionProgress.visibility = View.GONE
                            preferenceManager.setexercisedata(false)
                            Toast.makeText(this@LessonActivity, Message, Toast.LENGTH_SHORT).show()
                            loadData()
                        } else {
                            lessonBinding.lessionProgress.visibility = View.GONE
                            Toast.makeText(this@LessonActivity, Message, Toast.LENGTH_SHORT).show()
                        }
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@LessonActivity)
                    } else {
                        Toast.makeText(
                            this@LessonActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        ).show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<LessonData?>, t: Throwable) {
                    lessonBinding.lessionProgress.visibility = View.GONE
                    Toast.makeText(
                        this@LessonActivity,
                        "" + t.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    call.cancel()
                }
            })
        }
    }

    private fun saveData() {
        if (isValidate) {
            lessonBinding.lessionProgress.visibility = View.VISIBLE
            for (i in 0 until exercise_list.size) {
                id.add(exercise_list[i].id!!.toInt())
            }

            val str = arrayOfNulls<Int>(id.size)
            val array = JsonArray()

            for (i in 0 until id.size) {
                str[i] = id.get(i)
                array.add(id.get(i))
            }

            val goalArray = JsonArray()

            for (i in 0..0) {
                goalArray.add(goalId.id)
            }
            val jsonObject = JsonObject()
            jsonObject.addProperty("name", lessonBinding.edtLessonName.text.toString())
            jsonObject.add("goal_ids", goalArray)
            jsonObject.addProperty("time", lessonBinding.edtTime.text.toString())
            jsonObject.addProperty(
                "section_time",
                lessonBinding.tvSelectionTime.text.toString()
            )
            jsonObject.addProperty("section_id", sectionId.id)
            jsonObject.add("program_ids", array)
            jsonObject.addProperty("date", lessonBinding.etSelectTestDate.text.toString())

            Log.d("Lesson Data :- ", jsonObject.toString())

            apiInterface.CreateLesson(
                jsonObject
            )?.enqueue(object : Callback<LessonData?> {
                override fun onResponse(
                    call: Call<LessonData?>,
                    response: Response<LessonData?>
                ) {
                    Log.d(
                        "Lesson Data TAG :-",
                        response.code().toString() + "" + response.body()!!.message
                    )
                    val code = response.code()
                    if (code == 200) {
                        val resource: LessonData? = response.body()
                        val Success: Boolean = resource?.status!!
                        val Message: String = resource.message!!
                        if (Success) {
                            lessonBinding.lessionProgress.visibility = View.GONE
                            preferenceManager.setexercisedata(false)
                            Toast.makeText(this@LessonActivity, Message, Toast.LENGTH_SHORT).show()
                            loadData()
                        } else {
                            lessonBinding.lessionProgress.visibility = View.GONE
                            Toast.makeText(this@LessonActivity, Message, Toast.LENGTH_SHORT).show()
                        }
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@LessonActivity)
                    } else {
                        Toast.makeText(
                            this@LessonActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        ).show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<LessonData?>, t: Throwable) {
                    lessonBinding.lessionProgress.visibility = View.GONE
                    Toast.makeText(
                        this@LessonActivity,
                        "" + t.message,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    call.cancel()
                }
            })

        }
    }

    private fun setDialog() {
        val dialog = AlertDialog.Builder(this)

        val titleTextView = TextView(this).apply {
            text = "Alert"
            typeface = ResourcesCompat.getFont(this@LessonActivity, R.font.poppins_medium)
            textSize = 20f
            setPadding(50, 50, 50, 10)
            setTextColor(Color.BLACK)
        }

        dialog.setCustomTitle(titleTextView)

        dialog.setMessage("Please Select Program")
        dialog.setPositiveButton("Ok") { dialog, which ->
            dialog.dismiss()
        }

        val alert = dialog.create()

        val typeface = ResourcesCompat.getFont(this, R.font.poppins_medium)

        alert.setOnShowListener {
            val messageTextView = alert.findViewById<TextView>(android.R.id.message)
            messageTextView?.typeface = typeface

            // Set the font for the buttons
            val positiveButton = alert.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton?.typeface = typeface
        }

        // Show the dialog
        alert.show()
    }

    private fun setTimerDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_set_full_time_picker)
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = (displayMetrics.widthPixels * 0.9f).toInt()
        val height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.setLayout(width, height)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        var title = dialog.findViewById<AppCompatTextView>(R.id.tvTitle)
        val hour = dialog.findViewById<NumberPicker>(R.id.hour_num)
        val mint = dialog.findViewById<NumberPicker>(R.id.mint_num)
        val second = dialog.findViewById<NumberPicker>(R.id.second_num)
        val btnApply = dialog.findViewById<Button>(R.id.btnApply)
        val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)
        val hourText = dialog.findViewById<TextView>(R.id.hours)
        val mintText = dialog.findViewById<TextView>(R.id.mint)
        val secondText = dialog.findViewById<TextView>(R.id.second)

        hour.visibility = View.GONE
        second.visibility = View.GONE
        title.visibility = View.GONE
        hourText.visibility = View.GONE
        mintText.visibility = View.GONE
        secondText.visibility = View.GONE

        title.text = "Time Picker"

        var mintNumber = 0


        mint.minValue = 0
        mint.maxValue = 180
        mint.wrapSelectorWheel = true

        mint.setOnValueChangedListener { picker, oldVal, newVal ->
            mintNumber = newVal
        }


        btnCancel.setOnClickListener { v: View? ->
            dialog.dismiss()
        }
        btnApply.setOnClickListener { view: View? ->

            lessonBinding.tvSelectionTime.setText("$mintNumber min")
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun initViews() {
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        preferenceManager = PreferencesManager(this)
        lessonBinding.cardSave.visibility = View.VISIBLE
        lessonBinding.cardEdit.visibility = View.GONE

        age = ArrayList()
        id = ArrayList()
        exercise_list = ArrayList()
        lession_data1 = ArrayList()


        LessonLibraryId = intent.getIntExtra("LessonLibraryId",0)
        LessonLibraryPosition = intent.getIntExtra("LessonLibraryPosition",0)
        LessonIdGroup = intent.getIntExtra("LessonIdGroup",0)
        LessonPositionGroup = intent.getIntExtra("LessonPositionGroup",0)
        Log.d("DDJJJJDJJJ", "initViews: $LessonIdGroup    $LessonPositionGroup")


    }

    private fun showPopup(
        anchorView: View?,
        data: MutableList<TestListData.testData>,
        editText: EditText,
        list: ArrayList<String>,
        selectedValue: SelectedValue
    ) {

        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.popup_list, null)

        val weightInPixels = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, // The unit type (dp)
            330f, // The value in dp
            resources.displayMetrics // The display metrics
        ).toInt()

        val popupWindow = PopupWindow(
            popupView,
//            ViewGroup.LayoutParams.WRAP_CONTENT, // Wrap content width
            weightInPixels,
            ViewGroup.LayoutParams.WRAP_CONTENT, // Fixed height (100dp)
            true
        )
        popupWindow.setBackgroundDrawable(
            ContextCompat.getDrawable(
                this@LessonActivity,
                R.drawable.popup_background
            )
        )
        popupWindow.elevation = 10f
        val listView = popupView.findViewById<ListView>(R.id.listView)

        val adapter =
            object : ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list) {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val view = super.getView(position, convertView, parent) as TextView
                    val typeface =
                        ResourcesCompat.getFont(this@LessonActivity, R.font.poppins_medium)
                    view.typeface = typeface
                    view.setTextColor(Color.WHITE) // Set text color to white
                    return view
                }
            }
        listView.adapter = adapter
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = list[position]
            editText.setText(selectedItem)
            selectedValue.id = data.filter { it.name == selectedItem }.first().id!!
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

//    private fun getGoalData() {
//        goalData = mutableListOf()
//        goalData.clear()
//        apiInterface.GetGoal()?.enqueue(object : Callback<TestListData> {
//            override fun onResponse(call: Call<TestListData>, response: Response<TestListData>) {
//                val code = response.code()
//                if (code == 200) {
//                    if (response.isSuccessful) {
//                        val data = response.body()!!.data
//                        if (data!! != null) {
//                            goalData.addAll(data.toMutableList())
//                            for (i in goalData) {
//                                Goal.add(i.name!!)
//                            }
//                        }
//                    }
//                } else if (code == 403) {
//                    Utils.setUnAuthDialog(this@LessonActivity)
//                } else {
//                    Toast.makeText(this@LessonActivity, "" + response.message(), Toast.LENGTH_SHORT)
//                        .show()
//                    call.cancel()
//                }
//            }
//
//            override fun onFailure(call: Call<TestListData>, t: Throwable) {
//                Log.d("TAG Goal", t.message.toString() + "")
//            }
//
//        })
//    }


    private fun getGoalData() {
        goalData = mutableListOf()
        goalData.clear()
        apiInterface.GetGoal()?.enqueue(object : Callback<TestListData> {
            override fun onResponse(call: Call<TestListData>, response: Response<TestListData>) {
                val code = response.code()
                if (code == 200 && response.isSuccessful) {
                    val data = response.body()?.data
                    if (data != null && data.isNotEmpty()) {
                        goalData.addAll(data)
                        for (i in goalData) {
                            Goal.add(i.name!!)
                        }
                        lessonBinding.tvNodata.visibility = View.GONE
                    } else {
                        lessonBinding.tvNodata.visibility = View.VISIBLE
                    }
                } else {
                    lessonBinding.tvNodata.visibility = View.VISIBLE
                    Toast.makeText(this@LessonActivity, response.message(), Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<TestListData>, t: Throwable) {
                lessonBinding.tvNodata.visibility = View.VISIBLE
                Log.d("TAG Goal", t.message.toString())
            }
        })
    }


//    private fun GetLessionList() {
//        lessonBinding.lessionProgress.visibility = View.VISIBLE
//        apiInterface.GetLession1().enqueue(object : Callback<Lesson> {
//            override fun onResponse(call: Call<Lesson>, response: Response<Lesson>) {
//                Log.d("TAG Lesson:", response.code().toString() + "")
//                lessonBinding.lessionProgress.visibility = View.GONE
//                val code = response.code()
//                if (code == 200) {
//                    val resource = response.body()!!
//                    val Success: Boolean = resource.status!!
//                    val Message: String = resource.message!!
//                    if (Success) {
//                        if (resource.data!! != null) {
//                            Log.d("Lesson Data :-", "$Success $Message \t ${resource.data}")
//                            lession_data1.addAll(resource.data!!)
//                            initRecyclerview(resource.data!!)
//                        }
//                    }
//                } else if (code == 403) {
//                    Utils.setUnAuthDialog(this@LessonActivity)
//                } else {
//                    Toast.makeText(this@LessonActivity, "" + response.message(), Toast.LENGTH_SHORT)
//                        .show()
//                    call.cancel()
//                }
//            }
//
//            override fun onFailure(call: Call<Lesson>, t: Throwable) {
//                Toast.makeText(
//                    this@LessonActivity,
//                    "" + t.message,
//                    Toast.LENGTH_SHORT
//                ).show()
//                call.cancel)
//            }
//
//        })
//
//    }

    private fun GetLessionList() {
        lessonBinding.lessionProgress.visibility = View.VISIBLE
        apiInterface.GetLession1().enqueue(object : Callback<Lesson> {
            override fun onResponse(call: Call<Lesson>, response: Response<Lesson>) {
                Log.d("TAG Lesson:", response.code().toString() + "")
                lessonBinding.lessionProgress.visibility = View.GONE
                val code = response.code()
                if (code == 200) {
                    val resource = response.body()
                    if (resource != null) {
                        val success: Boolean = resource.status ?: false
                        val message: String = resource.message ?: "No message"
                        if (success) {
                            // Safe null check for 'data'
                            val data = resource.data
                            if (!data.isNullOrEmpty()) {
                                Log.d("Lesson Data :-", "$success $message \t $data")

                                lession_data1.clear()
                                lession_data1.addAll(data)
                                initRecyclerview(lession_data1)
                            } else {
                                // Handle empty or null data
                                Toast.makeText(
                                    this@LessonActivity,
                                    "No lessons available",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            // Handle unsuccessful response if needed
                            Toast.makeText(
                                this@LessonActivity,
                                "Error: $message",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        // Handle null response body
                        Toast.makeText(
                            this@LessonActivity,
                            "Empty response body",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else if (code == 403) {
                    Utils.setUnAuthDialog(this@LessonActivity)
                } else {
                    Toast.makeText(this@LessonActivity, response.message(), Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<Lesson>, t: Throwable) {
                Toast.makeText(this@LessonActivity, "Error: " + t.message, Toast.LENGTH_SHORT)
                    .show()
                call.cancel()
            }
        })
    }

    private fun GetLessionListLibaray() {
        lessonBinding.lessionProgress.visibility = View.VISIBLE
        apiInterface.GetLession1().enqueue(object : Callback<Lesson> {
            override fun onResponse(call: Call<Lesson>, response: Response<Lesson>) {
                Log.d("TAG Lesson:", response.code().toString() + "")
                lessonBinding.lessionProgress.visibility = View.GONE
                val code = response.code()
                if (code == 200) {
                    val resource = response.body()
                    if (resource != null) {
                        val success: Boolean = resource.status ?: false
                        val message: String = resource.message ?: "No message"
                        if (success) {
                            // Safe null check for 'data'
                            val data = resource.data
                            if (!data.isNullOrEmpty()) {
                                Log.d("Lesson Data :-", "$success $message \t $data")

                                lession_data1.clear()
                                lession_data1.addAll(data)
                                initRecyclerview(lession_data1)

                                setLessonData(lession_data1[LessonLibraryPosition!!])

                            } else {
                                // Handle empty or null data
                                Toast.makeText(
                                    this@LessonActivity,
                                    "No lessons available",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            // Handle unsuccessful response if needed
                            Toast.makeText(
                                this@LessonActivity,
                                "Error: $message",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        // Handle null response body
                        Toast.makeText(
                            this@LessonActivity,
                            "Empty response body",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else if (code == 403) {
                    Utils.setUnAuthDialog(this@LessonActivity)
                } else {
                    Toast.makeText(this@LessonActivity, response.message(), Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<Lesson>, t: Throwable) {
                Toast.makeText(this@LessonActivity, "Error: " + t.message, Toast.LENGTH_SHORT)
                    .show()
                call.cancel()
            }
        })
    }

    private fun GetLessionListGroup(targetId: Int) {
        Log.d("KDKDKDKDK", "GetLessionListGroup: OKOOOK")
        lessonBinding.lessionProgress.visibility = View.VISIBLE
        apiInterface.GetLession1().enqueue(object : Callback<Lesson> {
            override fun onResponse(call: Call<Lesson>, response: Response<Lesson>) {
                Log.d("TAG Lesson:", response.code().toString() + "")
                lessonBinding.lessionProgress.visibility = View.GONE
                val code = response.code()
                if (code == 200) {
                    val resource = response.body()
                    if (resource != null) {
                        val success: Boolean = resource.status ?: false
                        val message: String = resource.message ?: "No message"
                        if (success) {
                            val data = resource.data
                            if (!data.isNullOrEmpty()) {
                                Log.d("Lesson Data :-", "$success $message \t $data")

                                // Filter the lesson data to find a matching ID
                                val matchedLesson = data.find { it.id == targetId }

                                if (matchedLesson != null) {
                                    // Set the matched data
                                    Log.d("Matched Lesson", "$matchedLesson")
                                    setLessonDataGroup(matchedLesson)

                                    // Update RecyclerView if necessary
                                    lession_data1.clear()
                                    lession_data1.addAll(data)
                                    initRecyclerview(lession_data1)
                                } else {
                                    Toast.makeText(
                                        this@LessonActivity,
                                        "No lesson found with ID: $targetId",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                // Handle empty or null data
                                Toast.makeText(
                                    this@LessonActivity,
                                    "No lessons available",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            // Handle unsuccessful response if needed
                            Toast.makeText(
                                this@LessonActivity,
                                "Error: $message",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        // Handle null response body
                        Toast.makeText(
                            this@LessonActivity,
                            "Empty response body",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else if (code == 403) {
                    Utils.setUnAuthDialog(this@LessonActivity)
                } else {
                    Toast.makeText(this@LessonActivity, response.message(), Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<Lesson>, t: Throwable) {
                Toast.makeText(this@LessonActivity, "Error: " + t.message, Toast.LENGTH_SHORT)
                    .show()
                call.cancel()
            }
        })
    }



    private fun initRecyclerview(data: ArrayList<Lesson.LessonDatabase>) {
        lessonBinding.exerciseRecycler.layoutManager = LinearLayoutManager(this)
        adapter = LessionAdapter(data, this, this)
        lessonBinding.exerciseRecycler.adapter = adapter
    }

    private fun getSection() {
        sectionData = mutableListOf()
        sectionData.clear()
        apiInterface.GetSection1()?.enqueue(object : Callback<TestListData?> {
            override fun onResponse(
                call: Call<TestListData?>,
                response: Response<TestListData?>
            ) {
                Log.d("TAG", response.code().toString() + "")
                val code = response.code()
                if (code == 200) {
                    val resource = response.body()
                    val Success: Boolean = resource?.status!!
                    val Message: String = resource.message!!
                    val Data = resource.data!!
                    if (Success == true) {
                        if (Data != null) {
                            sectionData.addAll(Data)
                            sectionId = SelectedValue(Data.firstOrNull()?.id)
                            for (i in 0 until Data.size) {
                                section.add(Data[i].name!!)
                            }
                            initRecycler(sectionData, sectionId.id)
                        }
                    }
                } else if (code == 403) {
                    Utils.setUnAuthDialog(this@LessonActivity)
                } else {
                    Toast.makeText(this@LessonActivity, "" + response.message(), Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<TestListData?>, t: Throwable) {
                Toast.makeText(this@LessonActivity, "" + t.message, Toast.LENGTH_SHORT)
                    .show()
                call.cancel()
            }
        })
    }

    private fun initRecycler(
        sectionData: MutableList<TestListData.testData>,
        initialSelectId: Int?
    ) {
        lessonBinding.sectionRecycler.layoutManager =
            LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        sectionAdapter = SectionLessonAdapter(sectionData, this, this, initialSelectId)
        lessonBinding.sectionRecycler.adapter = sectionAdapter
    }


    private val isValidate: Boolean
        get() {
            var name = lessonBinding.edtLessonName.text.toString()
            var notes = lessonBinding.edtTime.text.toString()

            if (name == "") {
                lessonBinding.errorProgram.visibility = View.VISIBLE
                lessonBinding.errorProgram.text = "Please Enter Name"
                return false
            } else {
                lessonBinding.errorProgram.visibility = View.GONE
            }

            if (lessonBinding.edtGoal.text.toString() == "") {
                lessonBinding.errorGoal.visibility = View.VISIBLE
                lessonBinding.errorGoal.text = "Please Select Goal"
                return false
            } else {
                lessonBinding.errorGoal.visibility = View.GONE
            }

            if (lessonBinding.tvSelectionTime.text.toString() == "") {
                lessonBinding.errorTTime.visibility = View.VISIBLE
                lessonBinding.errorTTime.text = "Please Select Time"
                return false
            } else {
                lessonBinding.errorTTime.visibility = View.GONE
            }

            if (lessonBinding.edtTime.text.toString() == "") {
                lessonBinding.errorSTime.visibility = View.VISIBLE
                lessonBinding.errorSTime.text = "Please Select Time"
                return false
            } else {
                lessonBinding.errorSTime.visibility = View.GONE
            }

            if (lessonBinding.etSelectTestDate.equals("")) {
                lessonBinding.errorDate.visibility = View.VISIBLE
                lessonBinding.errorDate.text = "Please Enter Date"
                return false
            } else {
                lessonBinding.errorDate.visibility = View.GONE
            }

            return true
        }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun SetDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_set_number_picker)
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = (displayMetrics.widthPixels * 0.9f).toInt()
        val height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.setLayout(width, height)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val numberPicker = dialog.findViewById<NumberPicker>(R.id.npGoal)
        val btnApply = dialog.findViewById<Button>(R.id.btnApply)
        val tvReps = dialog.findViewById<TextView>(R.id.tvReps)
        val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)
        tvReps.text = "Time Picker"
        var selectedNumber = 0
        numberPicker.minValue = 1
        numberPicker.maxValue = 180
        numberPicker.wrapSelectorWheel = true
        numberPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            selectedNumber = newVal
        }

        btnCancel.setOnClickListener { v: View? ->
            dialog.dismiss()
        }
        btnApply.setOnClickListener { view: View? ->
            lessonBinding.tvSelectionTime.setText(selectedNumber.toString() + ":00:00 min")
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun goalDialog() {
        val dialog = Dialog(this, R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(R.layout.dialog_number_picker)
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = (displayMetrics.widthPixels * 0.9f).toInt()
        val height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.setLayout(width, height)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

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
//                        exercise_list.clear()
                        Log.d("Get Profile Data ", "${response.body()}")
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@LessonActivity)
                    } else {
                        Toast.makeText(
                            this@LessonActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Toast.makeText(
                        this@LessonActivity,
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


    fun getObject(c: Context, key: String): List<ProgramListData.testData>? {
        val appSharedPrefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(
            c.applicationContext
        )
        val json = appSharedPrefs.getString(key, null)

        if (json.isNullOrEmpty()) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<List<ProgramListData.testData>>() {}.type
        return gson.fromJson<List<ProgramListData.testData>>(json, type)
    }

    override fun onItemClicked(view: View, position: Int, type: Long, string: String) {
        if (string == "Edit") {
            lessonType = "edit"
            lessonId = lession_data1[position].id.toString()
            setLessonData(lession_data1[position])

            Log.d("HSHSHSHHS", "onItemClicked: $position")
        } else if (string == "fav") {
            lessonBinding.lessionProgress.visibility = View.VISIBLE
            val id: MultipartBody.Part =
                MultipartBody.Part.createFormData("id", type.toInt().toString())
            apiInterface.Favourite_lession(id)?.enqueue(object : Callback<RegisterData?> {
                override fun onResponse(
                    call: Call<RegisterData?>,
                    response: Response<RegisterData?>
                ) {
                    lessonBinding.lessionProgress.visibility = View.GONE
                    Log.d("TAG", response.code().toString() + "")
                    val code = response.code()
                    if (code == 200) {
                        val resource: RegisterData? = response.body()
                        val Success: Boolean = resource?.status!!
                        val Message: String = resource.message!!
                        if (Success) {
                            lessonBinding.lessionProgress.visibility = View.GONE
                            Toast.makeText(this@LessonActivity, "" + Message, Toast.LENGTH_SHORT)
                                .show()
                            loadData()
                        } else {
                            lessonBinding.lessionProgress.visibility = View.GONE
                            Toast.makeText(this@LessonActivity, "" + Message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@LessonActivity)
                    } else {
                        lessonBinding.lessionProgress.visibility = View.GONE
                        Toast.makeText(
                            this@LessonActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    lessonBinding.lessionProgress.visibility = View.GONE
                    Toast.makeText(this@LessonActivity, "" + t.message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }
            })
        } else if (string == "unfav") {
            lessonBinding.lessionProgress.visibility = View.VISIBLE
            val id: MultipartBody.Part =
                MultipartBody.Part.createFormData("id", type.toInt().toString())
            apiInterface.DeleteFavourite_lession(type.toInt())
                ?.enqueue(object : Callback<RegisterData?> {
                    override fun onResponse(
                        call: Call<RegisterData?>,
                        response: Response<RegisterData?>
                    ) {
                        Log.d("TAG", response.code().toString() + "")
                        lessonBinding.lessionProgress.visibility = View.GONE
                        val code = response.code()
                        if (code == 200) {
                            val resource: RegisterData? = response.body()
                            val Success: Boolean = resource?.status!!
                            val Message: String = resource.message!!
                            if (Success) {
                                lessonBinding.lessionProgress.visibility = View.GONE
                                Toast.makeText(
                                    this@LessonActivity,
                                    "" + Message,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                loadData()
                            } else {
                                lessonBinding.lessionProgress.visibility = View.GONE
                                Toast.makeText(
                                    this@LessonActivity,
                                    "" + Message,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        } else if (code == 403) {
                            Utils.setUnAuthDialog(this@LessonActivity)
                        } else {
                            Toast.makeText(
                                this@LessonActivity,
                                "" + response.message(),
                                Toast.LENGTH_SHORT
                            ).show()
                            call.cancel()
                        }
                    }

                    override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                        lessonBinding.lessionProgress.visibility = View.GONE
                        Toast.makeText(this@LessonActivity, "" + t.message, Toast.LENGTH_SHORT)
                            .show()
                        call.cancel()
                    }
                })
        } else if (string == "View") {
            startActivity(
                Intent(this, ViewLessonActivity::class.java)
                    .putExtra("id", lession_data1[position].id)
                    .putExtra("type", type.toString())
                    .putExtra("total_time", lession_data1[position].time)
                    .putExtra("section_time", lession_data1[position].section_time)
                    .putExtra("name", lession_data1[position].name)
                    .putExtra("position", position)
            )
        } else {
            val builder: AlertDialog.Builder
            builder = AlertDialog.Builder(this)
            builder.setMessage("Are you sure you want to delete Lesson?").setTitle("Success")

            builder.setMessage("Are you sure you want to delete Lesson?")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialog, id ->
                    lessonBinding.lessionProgress.visibility = View.VISIBLE
                    apiInterface.DeleteLession(type.toInt())
                        ?.enqueue(object : Callback<RegisterData?> {
                            override fun onResponse(
                                call: Call<RegisterData?>,
                                response: Response<RegisterData?>
                            ) {
                                Log.d("TAG", response.code().toString() + "")
                                lessonBinding.lessionProgress.visibility = View.GONE
                                val code = response.code()
                                if (code == 200) {
                                    val resource: RegisterData? = response.body()
                                    val Success: Boolean = resource?.status!!
                                    val Message: String = resource.message!!
                                    lessonBinding.lessionProgress.visibility = View.GONE
                                    Toast.makeText(
                                        this@LessonActivity,
                                        "" + Message,
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    lession_data1.removeAll { it.id == type.toInt() }
                                    lession_data1.clear()
                                    adapter.notifyDataSetChanged()

                                    loadData()

                                } else if (code == 403) {
                                    Utils.setUnAuthDialog(this@LessonActivity)
                                } else {
                                    lessonBinding.lessionProgress.visibility = View.GONE
                                    Toast.makeText(
                                        this@LessonActivity,
                                        "" + response.message(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    call.cancel()
                                }

                            }

                            override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                                lessonBinding.lessionProgress.visibility = View.GONE
                                Toast.makeText(
                                    this@LessonActivity,
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

            val titleTextView = TextView(this).apply {
                text = "Success"
                typeface =
                    ResourcesCompat.getFont(
                        this@LessonActivity,
                        R.font.poppins_medium
                    ) // Set the font
                textSize = 20f
                setPadding(50, 50, 50, 5) // Optional: add padding
                setTextColor(Color.BLACK)
            }

            alert.setCustomTitle(titleTextView)

            val typeface = ResourcesCompat.getFont(this, R.font.poppins_medium)

            alert.setOnShowListener {
                val titleTextView = alert.findViewById<TextView>(android.R.id.title)
                titleTextView?.typeface = typeface

                val messageTextView = alert.findViewById<TextView>(android.R.id.message)
                messageTextView?.typeface = typeface

                val positiveButton = alert.getButton(AlertDialog.BUTTON_POSITIVE)
                positiveButton?.typeface = typeface

                val negativeButton = alert.getButton(AlertDialog.BUTTON_NEGATIVE)
                negativeButton?.typeface = typeface
            }

            alert.setTitle("Success")
            alert.show()

        }
    }

    private fun setLessonData(lessonDatabase: Lesson.LessonDatabase) {
        Log.d("DATA BASE :-", "${lessonDatabase.lesson_programs?.size ?: 0}")
        lessonDatabase.lesson_programs?.forEach {
            Log.d("Lesson Database :-", "${it.program?.name} \t ${it.program?.id}")
        }

        id.clear()
        lessonDatabase.lesson_programs?.forEach {
            it.program?.id?.let { programId -> id.add(programId) }
        }

//        lessonDatabase.lesson_goal?.forEach {
//            it.goal_id?.let { GoalId -> Goalid.add(GoalId) }
//        }



        Log.d("DLDLDLLDLDLL", "setLessonData: $id")
        lessonBinding.edtLessonName.setText(lessonDatabase.name ?: "")
        lessonBinding.edtTime.setText(lessonDatabase.time ?: "")
        lessonBinding.tvSelectionTime.setText(lessonDatabase.section_time ?: "")

        try {
            val lessonGoal = lessonDatabase.lesson_goal?.getOrNull(0)
            lessonBinding.edtGoal.setText(lessonGoal?.goal?.name ?: "")
            goalId.id = lessonGoal?.goal_id?.toInt() ?: 0
            sectionId.id = lessonDatabase.section_id?.toInt() ?: 0
            sectionAdapter.updateSelectedId(sectionId.id!!)
        } catch (e: Exception) {
            Log.d("Exception :-", "$e 	 ${e.message}")
        }

        lessonBinding.etSelectTestDate.setText(lessonDatabase.date ?: "")
    }

    private fun setLessonDataGroup(lessonDatabase: Lesson.LessonDatabase) {
        Log.d("DATA BASE :-", "${lessonDatabase.lesson_programs?.size ?: 0}")
        lessonDatabase.lesson_programs?.forEach {
            Log.d("Lesson Database :-", "${it.program?.name} \t ${it.program?.id}")
        }

        id.clear()
        lessonDatabase.lesson_programs?.forEach {
            it.program?.id?.let { programId -> id.add(programId) }
        }

//        lessonDatabase.lesson_goal?.forEach {
//            it.goal_id?.let { GoalId -> Goalid.add(GoalId) }
//        }



        Log.d("DLDLDLLDLDLL", "setLessonData: $id")
        lessonBinding.edtLessonName.setText(lessonDatabase.name ?: "")
        lessonBinding.edtTime.setText(lessonDatabase.time ?: "")
        lessonBinding.tvSelectionTime.setText(lessonDatabase.section_time ?: "")

        try {
            val lessonGoal = lessonDatabase.lesson_goal?.getOrNull(0)
            lessonBinding.edtGoal.setText(lessonGoal?.goal?.name ?: "")
            goalId.id = lessonGoal?.goal_id?.toInt() ?: 0
            sectionId.id = lessonDatabase.section_id?.toInt() ?: 0
            sectionAdapter.updateSelectedId(sectionId.id!!)
        } catch (e: Exception) {
            Log.d("Exception :-", "$e 	 ${e.message}")
        }

        lessonBinding.etSelectTestDate.setText(lessonDatabase.date ?: "")
    }


    private fun saveSectionDataToPreferences(
        sectionName: String,
        sectionId: Int,
        newData: ArrayList<ProgramListData.testData>
    ) {
        val sharedPreferences = getSharedPreferences("SectionData", Context.MODE_PRIVATE)
        val existingData = loadSectionDataFromPreferences(sectionName)

        val finalData = if (existingData != null) {
            // Merge existing and new data while avoiding duplicates
            val combinedList = ArrayList(existingData.second)
            for (item in newData) {
                if (!combinedList.contains(item)) { // Avoid duplicates
                    combinedList.add(item)
                }
            }
            combinedList
        } else {
            newData
        }

        val sectionData = mapOf(
            "sectionId" to sectionId,
            "data" to finalData
        )

        val jsonString = Gson().toJson(sectionData)
        sharedPreferences.edit().putString(sectionName, jsonString).apply()
        Log.d("SharedPreferences", "Data saved for section: $sectionName with ID: $sectionId")
    }

    private fun loadSectionDataFromPreferences(sectionName: String): Pair<Int, ArrayList<ProgramListData.testData>>? {
        val sharedPreferences = getSharedPreferences("SectionData", Context.MODE_PRIVATE)
        val jsonString = sharedPreferences.getString(sectionName, null)

        return if (!jsonString.isNullOrEmpty()) {
            val type = object : TypeToken<Map<String, Any>>() {}.type
            val sectionMap: Map<String, Any> = Gson().fromJson(jsonString, type)

            val sectionIdx = (sectionMap["sectionId"] as? Double)?.toInt() ?: return null
            sectionId.id = sectionIdx  // Wrap the sectionIdx in an ArrayList

            val dataType = object : TypeToken<ArrayList<ProgramListData.testData>>() {}.type
            val data = Gson().fromJson<ArrayList<ProgramListData.testData>>(
                Gson().toJson(sectionMap["data"]),
                dataType
            )

            println("Saved Section ID: ${sectionId.id}") // Debugging output

            Pair(sectionIdx, data)
        } else {
            null
        }
    }


    private fun clearAllSectionsData() {
        val sharedPreferences = getSharedPreferences("SectionData", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
        Log.d("clearAllSectionsData", "Cleared all section data")
    }

    override fun onResume() {
        super.onResume()
        Log.d("onResume", "Called onResume()")

        if (preferenceManager.getexercisedata()) {
            Log.d("onResume", "Preference manager indicates exercise data exists")

            var sectionExercises = sectionDataMap[sectionName]
            Log.d(
                "onResume",
                "Section name: '$sectionName', Existing data in map: ${sectionExercises?.getOrNull(0)?.section?.name}"
            )

            if (sectionExercises == null || sectionExercises.isEmpty()) {
                val exerciseData =
                    getObject(this, "Exercise") as? ArrayList<ProgramListData.testData>
                Log.d("onResume", "Loaded exercise data from storage: $exerciseData")
                if (exerciseData != null) {
                    sectionExercises = exerciseData
                    sectionDataMap[sectionName] = sectionExercises // Cache the loaded data
                    Log.d("onResume", "Data cached for section: $sectionName")
                }
            }

            if (!sectionExercises.isNullOrEmpty()) {
                exercise_list = sectionExercises
                Log.d("onResume", "Setting exercise list: $exercise_list")

                // Update UI
                val time = exercise_list[0].time
                lessonBinding.edtTime.setText(time)
                lessonBinding.programRecycler.visibility = View.VISIBLE
                lessonBinding.programRecycler.layoutManager = LinearLayoutManager(
                    this, LinearLayoutManager.HORIZONTAL, false
                )
                adapter1 = Exercise_select_Adapter(exercise_list, sectionName, this)
                lessonBinding.programRecycler.adapter = adapter1

//                id = arrayListOf(exercise_list.getOrNull(0)?.id ?: 0)  // Only the first id
                val uniqueIds = HashSet<Int>() // Use a set to track unique IDs


                exercise_list.forEach { exercise ->
                    if (uniqueIds.add(exercise.id!!)) { // `add()` returns false if already present
                        id.add(exercise.id!!)
                    }
                }

                Log.d("onResumerrrr", "All ids added: $id")  // Log all the ids in the list


            } else {
                lessonBinding.programRecycler.visibility = View.VISIBLE
                lessonBinding.edtTime.setText("")
                Log.d("onResume", "No data found. RecyclerView hidden, EditText cleared")
            }
        } else {
            Log.d("onResume", "Preference manager indicates no exercise data")
        }

        checkUser()

    }

    override fun onItemClick(ids: Int, name: String, position: Int) {
        if (sectionName.isNotEmpty() && exercise_list.isNotEmpty()) {
            saveSectionDataToPreferences(sectionName, ids, ArrayList(exercise_list)) // Save data
            Log.d("onItemClick", "Saved and cleared data for section: $sectionName")
        }

        sectionId.id = ids
        sectionName = name
        Log.d("onItemClick", "Switched to section: $sectionName")

        val sectionData = loadSectionDataFromPreferences(sectionName)
        val exercise_list = sectionData?.second ?: arrayListOf()

        if (exercise_list.isNotEmpty()) {
            lessonBinding.programRecycler.visibility = View.VISIBLE
            lessonBinding.edtTime.setText(exercise_list[0].time)
            lessonBinding.programRecycler.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            adapter1 = Exercise_select_Adapter(exercise_list, sectionName, this)
            lessonBinding.programRecycler.adapter = adapter1

            Log.d("onItemClick}}}}", "RecyclerView updated with data for section: $id")
        } else {
            lessonBinding.programRecycler.visibility = View.GONE
            lessonBinding.edtTime.setText("")
            Log.d("onItemClick", "No data found for section: $sectionName")
        }
    }

}