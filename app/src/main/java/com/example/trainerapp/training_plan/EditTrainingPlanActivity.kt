package com.example.trainerapp.training_plan

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.example.model.trainer_plan.Competitive
import com.example.model.trainer_plan.PreCompetitive
import com.example.model.trainer_plan.PreSeason
import com.example.model.trainer_plan.TrainingPlanSubClass
import com.example.model.trainer_plan.Transition
import com.example.model.training_plan.TrainingPlanData
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.ApiClass.Training_Plan.create_training.EditeTrainingPlanAdapter
import com.example.trainerapp.PreferencesManager
import com.example.trainerapp.R
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.ActivityEditTrainingPlanBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.log

class EditTrainingPlanActivity : AppCompatActivity() {
    lateinit var editTrainingPlanBinding: ActivityEditTrainingPlanBinding
    private lateinit var apiInterface: APIInterface
    private lateinit var preferenceManager: PreferencesManager
    private lateinit var apiClient: APIClient
    private var id: Int? = null
    private var psid: Int? = null
    private var PlanningIdGroup: Int? = null
    private var trainingPlanCount = 0
    private var programData: MutableList<TrainingPlanData.TrainingPlan> = mutableListOf()
    private lateinit var trainingPlanContainer: LinearLayout
    private var trainingPlanLayouts = mutableListOf<View>()
    private var missingIndices = mutableListOf<Int>()
    private val dateRangeMap = mutableMapOf<Int, Pair<String, String>>()
    private var startDateMilliss: Long = 0
    private var endDateMilliss: Long = 0
    val indexxx = mutableListOf<String>()

    lateinit var adapter: EditeTrainingPlanAdapter

    private var startdatesent: String? = null
    private var enddatesent: String? = null

    private var startdatesentlist: String? = null
    private var enddatesentlist: String? = null

    private val selectedDateRanges = mutableListOf<Pair<Long, Long>>()
    private var activeLayoutIndex: Int? = null

    private var competitive: Competitive = Competitive("", "", "", "")
    private var preSeason = PreSeason("", "", "", "")
    private var transition = Transition("", "", "", "")
    private var preCompetitive: PreCompetitive = PreCompetitive("", "", "", "")

    var errorstartdate: String? = null
    var errorenddate: String? = null
    var errorstartdatelist: String? = null
    var errorenddatelist: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        editTrainingPlanBinding = ActivityEditTrainingPlanBinding.inflate(layoutInflater)
        setContentView(editTrainingPlanBinding.root)
        initView()
        loadData()
        checkButtonClick()
        textWatcher()
        setupTextWatcher()
        updateDaysTextView()

    }

    private fun areAllFieldsFilled(): Boolean {
        return !(editTrainingPlanBinding.edtProgramName.text.isNullOrEmpty() || editTrainingPlanBinding.edtStartDate.text.isNullOrEmpty() || editTrainingPlanBinding.edtEndDate.text.isNullOrEmpty())
    }

    private fun updateUI(addButton: CardView) {
        if (areAllFieldsFilled()) {
            addButton.isEnabled = true
            addButton.setCardBackgroundColor(resources.getColor(R.color.splash_text_color)) // Change to your desired colo
        } else {
            addButton.isEnabled = false
            addButton.setCardBackgroundColor(resources.getColor(R.color.grey)) // Disabled color
        }
    }


    private fun textWatcher() {

        editTrainingPlanBinding.edtProgramName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                updateDaysTextView()
                updateUI(editTrainingPlanBinding.cardSave)
            }
        })
        editTrainingPlanBinding.edtStartDate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateDaysTextView()
                updateUI(editTrainingPlanBinding.cardSave)
            }
        })

        editTrainingPlanBinding.edtEndDate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateDaysTextView()
                updateUI(editTrainingPlanBinding.cardSave)
            }
        })
    }

    private fun findMissingIndex(indexList: MutableList<String>): Int? {
        val existingIndexes = indexList.mapNotNull { it.toIntOrNull() }.toSet()
        return (0..3).firstOrNull { it !in existingIndexes } // Find the first missing index
    }

    private fun checkButtonClick() {

        editTrainingPlanBinding.add.setOnClickListener {
            if (indexxx.size < 4) {
                val missingIndex = findMissingIndex(indexxx)
                if (missingIndex != null) {
                    Log.d("DDKKDKDKDK", "Before Add: $indexxx")

                    indexxx.add(missingIndex.toString()) // ✅ Always append to avoid shifting indexes

                    Log.d("DDKKDKDKDK", "After Add: $indexxx")

                    addTrainingPlanView("", "", "", "", "")
                    updateTrainingPlanIndices() // ✅ Maintain UI stability
                } else {
                    Log.d("DDKKDKDKDK", "No missing index found. Cannot add more.")
                }
            }
        }



        editTrainingPlanBinding.cardSave.setOnClickListener { EditeProgramData() }

        editTrainingPlanBinding.cardDate.setOnClickListener {
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val oneYearLater = Calendar.getInstance().apply {
                add(Calendar.YEAR, 1)
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }.timeInMillis

            val currentLayoutIndex = activeLayoutIndex

            showDateRangePickerDialogfor(
                context = editTrainingPlanBinding.edtStartDate.context,
                minDateMillis = today,
                maxDateMillis = oneYearLater,
                layoutIndex = currentLayoutIndex
            ) { start, end ->
                startDateMilliss = start
                endDateMilliss = end
                val formattedStartDate = formatDate(start)
                val formattedStartDate2 = formatDate2(start)
                val formattedEndDate = formatDate(end)
                val formattedEndDate2 = formatDate2(end)
                editTrainingPlanBinding.edtStartDate.setText(formattedStartDate2)
                editTrainingPlanBinding.edtEndDate.setText(formattedEndDate2)
                startdatesent = formattedStartDate
                enddatesent = formattedEndDate
                updateDaysTextView()
            }
        }

        editTrainingPlanBinding.edtStartDate.setOnClickListener {
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val oneYearLater = Calendar.getInstance().apply {
                add(Calendar.YEAR, 1)
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }.timeInMillis

            val currentLayoutIndex = activeLayoutIndex

            showDateRangePickerDialogfor(
                context = editTrainingPlanBinding.edtStartDate.context,
                minDateMillis = today,
                maxDateMillis = oneYearLater,
                layoutIndex = currentLayoutIndex
            ) { start, end ->
                startDateMilliss = start
                endDateMilliss = end
                val formattedStartDate = formatDate(start)
                val formattedStartDate2 = formatDate2(start)
                val formattedEndDate = formatDate(end)
                val formattedEndDate2 = formatDate2(end)
                editTrainingPlanBinding.edtStartDate.setText(formattedStartDate2)
                editTrainingPlanBinding.edtEndDate.setText(formattedEndDate2)
                startdatesent = formattedStartDate
                enddatesent = formattedEndDate
                updateDaysTextView()
            }
        }
        editTrainingPlanBinding.cardEndDate.setOnClickListener {
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val oneYearLater = Calendar.getInstance().apply {
                add(Calendar.YEAR, 1)
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }.timeInMillis

            val currentLayoutIndex = activeLayoutIndex

            showDateRangePickerDialogfor(
                context = editTrainingPlanBinding.edtStartDate.context,
                minDateMillis = today,
                maxDateMillis = oneYearLater,
                layoutIndex = currentLayoutIndex
            ) { start, end ->
                startDateMilliss = start
                endDateMilliss = end
                val formattedStartDate = formatDate(start)
                val formattedStartDate2 = formatDate2(start)
                val formattedEndDate = formatDate(end)
                val formattedEndDate2 = formatDate2(end)
                editTrainingPlanBinding.edtStartDate.setText(formattedStartDate2)
                editTrainingPlanBinding.edtEndDate.setText(formattedEndDate2)
                startdatesent = formattedStartDate
                enddatesent = formattedEndDate
                updateDaysTextView()
            }
        }
        editTrainingPlanBinding.edtEndDate.setOnClickListener {
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val oneYearLater = Calendar.getInstance().apply {
                add(Calendar.YEAR, 1)
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }.timeInMillis

            val currentLayoutIndex = activeLayoutIndex

            showDateRangePickerDialogfor(
                context = editTrainingPlanBinding.edtStartDate.context,
                minDateMillis = today,
                maxDateMillis = oneYearLater,
                layoutIndex = currentLayoutIndex
            ) { start, end ->
                startDateMilliss = start
                endDateMilliss = end
                val formattedStartDate = formatDate(start)
                val formattedStartDate2 = formatDate2(start)
                val formattedEndDate = formatDate(end)
                val formattedEndDate2 = formatDate2(end)
                editTrainingPlanBinding.edtStartDate.setText(formattedStartDate2)
                editTrainingPlanBinding.edtEndDate.setText(formattedEndDate2)
                startdatesent = formattedStartDate
                enddatesent = formattedEndDate
                updateDaysTextView()
            }
        }

        editTrainingPlanBinding.back.setOnClickListener { finish() }
    }

    private fun loadData() {
        GetProgramData()
    }

    private fun initView() {
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        preferenceManager = PreferencesManager(this)

        trainingPlanContainer = editTrainingPlanBinding.linerAddTraining
        programData = mutableListOf()
        trainingPlanLayouts = mutableListOf()
        missingIndices = mutableListOf()

        id = intent.getIntExtra("Id", 0)
        psid = intent.getIntExtra("MainId", 0)


        Log.d("DKDKDKDDKDK", "initView: $id")

        if (id == 0 || id == null) {
            id = intent.getIntExtra("PlanningIdGroup", 0)
            Log.d("DKDKDKDDKDK", "initView: $id")
        }

    }

    private fun GetProgramData() {
        editTrainingPlanBinding.progresBar.visibility = View.VISIBLE
        programData.clear()
        apiInterface.GetTrainingPlan()?.enqueue(object : Callback<TrainingPlanData?> {
            override fun onResponse(
                call: Call<TrainingPlanData?>,
                response: Response<TrainingPlanData?>
            ) {

                Log.d("TAG", response.code().toString() + "")
                val code = response.code()
                if (code == 200) {
                    Log.d(
                        "get data:",
                        "${response.code()} ${response.body()?.message ?: ""} ${response.body()?.status} \n ${response.body()?.data}"
                    )

                    editTrainingPlanBinding.progresBar.visibility = View.GONE
                    val resource: TrainingPlanData? = response.body()
                    val success: Boolean = resource?.status ?: false
                    val message: String = resource?.message ?: ""

                    if (success) {
                        try {
                            val data = resource!!.data?.find { it.id == id }
                            if (data != null) {
                                programData.add(data)
                                setProgramDataset(data)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        Toast.makeText(this@EditTrainingPlanActivity, message, Toast.LENGTH_SHORT)
                            .show()
                    }
                } else if (code == 403) {
                    Utils.setUnAuthDialog(this@EditTrainingPlanActivity)
                } else {
                    Toast.makeText(
                        this@EditTrainingPlanActivity,
                        "" + response.message(),
                        Toast.LENGTH_SHORT
                    ).show()
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<TrainingPlanData?>, t: Throwable) {
                Toast.makeText(this@EditTrainingPlanActivity, t.message, Toast.LENGTH_SHORT).show()
                call.cancel()
            }
        })
    }

    fun showDateRangePickerDialogfor(
        context: Context,
        minDateMillis: Long,
        maxDateMillis: Long,
        layoutIndex: Int?,
        callback: (start: Long, end: Long) -> Unit
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

        calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_RANGE)

        cancelButton.setOnClickListener { dialog.dismiss() }

        calendarView.state().edit()
            .setCalendarDisplayMode(CalendarMode.MONTHS)
            .commit()

        calendarView.addDecorator (object : DayViewDecorator {
            val today = CalendarDay.today()

            override fun shouldDecorate(day: CalendarDay?): Boolean {
                // Only decorate today
                return day == today
            }

            override fun decorate(view: DayViewFacade?) {
                ContextCompat.getDrawable(context, R.drawable.todays_date_selecte)?.let {
                    view?.setBackgroundDrawable(it)
                }
                view?.addSpan(ForegroundColorSpan(Color.WHITE)) // Text color for today (non-selected)

            }
        })

        // Disable dates outside the min-max range
        calendarView.addDecorator(object : DayViewDecorator {
            override fun shouldDecorate(day: CalendarDay?): Boolean {
                val dateInMillis = day?.calendar?.timeInMillis ?: return false
                return dateInMillis < minDateMillis || dateInMillis > maxDateMillis
            }

            override fun decorate(view: DayViewFacade?) {
                view?.addSpan(ForegroundColorSpan(Color.GRAY))
                view?.setDaysDisabled(true)
            }
        })

        Log.d("455444545", "showDateRangePickerDialogfor: $layoutIndex")
        val uniqueDateRanges = selectedDateRanges.toSet().toList() // Remove duplicates

        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        uniqueDateRanges.forEachIndexed { index, pair ->
            if (index >= 0) {
                val startDate = dateFormatter.format(Date(pair.first))
                val endDate = dateFormatter.format(Date(pair.second))
                Log.d("SDSDSDSDSDSD", "Date Range $index: ($startDate - $endDate)")
            }
        }

        Log.d("DKKDKKDKKDK", "showDateRangePickerDialogfor: $uniqueDateRanges")
        Log.d("DKKDKKDKKDK", "showDateRangePickerDialogfor: $selectedDateRanges")


        calendarView.addDecorator(object : DayViewDecorator {
            override fun shouldDecorate(day: CalendarDay?): Boolean {
                if (day == null || day.calendar == null) return false

                val dateInMillis = day.calendar.timeInMillis

                if (layoutIndex == null || layoutIndex == 0) return false

                if (selectedDateRanges == null) return false

                // First decorator logic (Disable dates in selectedDateRanges)
                if (selectedDateRanges.withIndex().any { (index, range) ->
                        val (start, end) = range
                        index < layoutIndex && dateInMillis in start..end
                    }) {
                    return true
                }

                // Second decorator logic (Disable dates before the previous range's start date)
                if (layoutIndex != null && layoutIndex > 0 && selectedDateRanges.size >= layoutIndex) {
                    val (prevStart, _) = selectedDateRanges[layoutIndex - 1]

                    // Disable dates before the previous range's start date
                    if (dateInMillis < prevStart) {
                        return true
                    }
                }

                return false
            }

            override fun decorate(view: DayViewFacade?) {
                view?.addSpan(ForegroundColorSpan(Color.GRAY))
                view?.setDaysDisabled(true)
            }
        })


        confirmButton.setOnClickListener {
            val selectedDates = calendarView.selectedDates

            if (selectedDates.size >= 2) {
                // Sort the selected dates to ensure proper range order
                val sortedDates = selectedDates.sortedBy { it.calendar.timeInMillis }

                // Get the first and second selected dates
                val startDate = sortedDates.first().calendar
                val endDate = sortedDates[1].calendar // Adjusted logic for the end date

                // Set the start date to the start of the day
                startDate.set(Calendar.HOUR_OF_DAY, 0)
                startDate.set(Calendar.MINUTE, 0)
                startDate.set(Calendar.SECOND, 0)
                startDate.set(Calendar.MILLISECOND, 0)

                // If the selected dates are non-contiguous, set the end date to the day before the second date
                if (selectedDates.size > 2 || sortedDates.last() != sortedDates[1]) {
                    endDate.add(Calendar.DAY_OF_MONTH, -1)
                }

                // Set the end date to the end of the day
                endDate.set(Calendar.HOUR_OF_DAY, 23)
                endDate.set(Calendar.MINUTE, 59)
                endDate.set(Calendar.SECOND, 59)
                endDate.set(Calendar.MILLISECOND, 999)

                val startMillis = startDate.timeInMillis
                val endMillis = endDate.timeInMillis

                if (layoutIndex == 0) {
                    // Update or add the selected range
                    updateOrAddDateRange(layoutIndex, startMillis, endMillis)
                    callback(startMillis, endMillis)
                    dialog.dismiss()
                } else {
                    // Check for conflicts with other plans
                    val conflict = isConflictWithPreviousPlans(startMillis, endMillis, layoutIndex ?: 0)

                    if (conflict) {
                        textView.text = "Selected dates conflict with another plan"
                        textView.setTextColor(Color.RED)
                        Toast.makeText(
                            context,
                            "Plan $layoutIndex conflicts with earlier plans",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        updateOrAddDateRange(layoutIndex, startMillis, endMillis)
                        callback(startMillis, endMillis)
                        dialog.dismiss()
                    }
                }
            } else {
                textView.text = "Please select both start and end dates"
                textView.setTextColor(Color.RED)
            }
        }

        calendarView.addDecorator(object : DayViewDecorator {
            override fun shouldDecorate(day: CalendarDay?): Boolean {
                return calendarView.selectedDates.contains(day)
            }

            override fun decorate(view: DayViewFacade?) {
                view?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
                view?.addSpan(ForegroundColorSpan(Color.BLACK))
            }
        })


        confirmButton.setOnClickListener {
            val selectedDates = calendarView.selectedDates

            if (selectedDates.size >= 2) {
                val sortedDates = selectedDates.sortedBy { it.calendar.timeInMillis }

                // Get the first and last selected dates
                val startDate = sortedDates.first().calendar
                val endDate = sortedDates.last().calendar

                // Set the start date to the start of the day
                startDate.set(Calendar.HOUR_OF_DAY, 0)
                startDate.set(Calendar.MINUTE, 0)
                startDate.set(Calendar.SECOND, 0)
                startDate.set(Calendar.MILLISECOND, 0)

                // Adjust the end date for non-contiguous selections
                if (endDate.timeInMillis - startDate.timeInMillis > 24 * 60 * 60 * 1000) {
                    endDate.add(Calendar.DAY_OF_MONTH, -0)
                }

                // Set the end date to the end of the day
                endDate.set(Calendar.HOUR_OF_DAY, 23)
                endDate.set(Calendar.MINUTE, 59)
                endDate.set(Calendar.SECOND, 59)
                endDate.set(Calendar.MILLISECOND, 999)

                val startMillis = startDate.timeInMillis
                val endMillis = endDate.timeInMillis

                if (layoutIndex == 0) {
                    // Update or add the selected range
                    updateOrAddDateRange(layoutIndex, startMillis, endMillis)
                    callback(startMillis, endMillis)
                    dialog.dismiss()
                } else {
                    // Check for conflicts with other plans
                    val conflict =
                        isConflictWithPreviousPlans(startMillis, endMillis, layoutIndex ?: 0)

                    if (conflict) {
                        textView.text = "Selected dates conflict with another plan"
                        textView.setTextColor(Color.RED)
                        Toast.makeText(
                            context,
                            "Plan $layoutIndex conflicts with earlier plans",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        updateOrAddDateRange(layoutIndex, startMillis, endMillis)
                        callback(startMillis, endMillis)
                        dialog.dismiss()
                    }
                }
            } else {
                textView.text = "Please select both start and end dates"
                textView.setTextColor(Color.RED)
            }
        }

        dialog.show()
    }

    fun updateOrAddDateRange(layoutIndex: Int?, startMillis: Long, endMillis: Long) {
        val uniqueDateRanges = selectedDateRanges.toSet().toList()

        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        uniqueDateRanges.forEachIndexed { index, pair ->
            if (index >= 0) {
                val startDate = dateFormatter.format(Date(pair.first))
                val endDate = dateFormatter.format(Date(pair.second))
                Log.d("SDSDSDSDSDSD", "Date Range $index: ($startDate - $endDate)")
            }
        }


        layoutIndex?.let {
            if (it < selectedDateRanges.size) {
                selectedDateRanges[it] = startMillis to endMillis
            } else {
                selectedDateRanges.add(startMillis to endMillis)
            }
        }
    }

    fun isConflictWithPreviousPlans(startMillis: Long, endMillis: Long, layoutIndex: Int): Boolean {

        for (i in 0 until layoutIndex) {
            if (i >= selectedDateRanges.size) {
                break
            }
            val (planStart, planEnd) = selectedDateRanges[i]
            if ((startMillis in planStart..planEnd) || (endMillis in planStart..planEnd) ||
                (startMillis < planStart && endMillis > planEnd)
            ) {
                return true
            }
        }
        return false
    }

    fun isConflict(startDate: String, endDate: String, layoutIndex: Int?): Boolean {
        val startMillis = convertDateToMillis(startDate)
        val endMillis = convertDateToMillis(endDate)

        for (i in 0 until trainingPlanContainer.childCount) {
            if (i != layoutIndex) {
                val layout = trainingPlanContainer.getChildAt(i)
                val existingStartDate: AppCompatEditText =
                    layout.findViewById(R.id.ent_start_date_liner)
                val existingEndDate: AppCompatEditText =
                    layout.findViewById(R.id.ent_ent_date_liner)

                val existingStartMillis =
                    convertDateToMillis(existingStartDate.text.toString().trim())
                val existingEndMillis = convertDateToMillis(existingEndDate.text.toString().trim())

                // Check for overlap using the isOverlapping function
                if (isOverlapping(startMillis, endMillis, existingStartMillis, existingEndMillis)) {
                    return true // Conflict found
                }
            }
        }
        return false
    }

    fun convertDateToMillis(dateString: String): Long {
        return try {
            val sdf = SimpleDateFormat(
                "dd MMM, yyyy",
                Locale.getDefault()
            )
            val date = sdf.parse(dateString)
            date?.time ?: 0L
        } catch (e: Exception) {
            0L
        }
    }

    fun isOverlapping(start1: Long, end1: Long, start2: Long, end2: Long): Boolean {
        return start1 <= end2 && start2 <= end1  // Changed `<` to `<=`
    }

    private fun saveTrainingPlansToPreferences(index: Int) {
        val sharedPreferences = getSharedPreferences("TrainingPlansPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val trainingPlanList = trainingPlanLayouts.map { layout ->
            val nameEditText: AppCompatEditText = layout.findViewById(R.id.ent_pre_sea_name)
            val startDateEditText: AppCompatEditText =
                layout.findViewById(R.id.ent_start_date_liner)
            val endDateEditText: AppCompatEditText = layout.findViewById(R.id.ent_ent_date_liner)

            val phaseType = nameEditText.hint?.toString() ?: "Unknown Phase"
            val name = nameEditText.text.toString()
            val startDate = startDateEditText.text.toString()
            val endDate = endDateEditText.text.toString()

            mapOf(
                "1" to "1",
                "2" to "2",
                "3" to "3",
                "4" to "4",
            )
        }

        // Convert the list to JSON
        val json = Gson().toJson(trainingPlanList)
        editor.putString("savedTrainingPlans", json)
        editor.apply()

        Log.d("MDKMMKKKKKKKMK", "saveTrainingPlansToPreferences: $json")
    }

    private fun addTrainingPlanView(
        name: String,
        startDate: String,
        endDate: String,
        mesocycle: String,
        phaseType: String,
    ) {

        Log.d("LLLPLPLPPLPPPL", "addTrainingPlanView: INDEX:-  $indexxx    TYPE:- $phaseType")

        trainingPlanCount++

        val indexToAdd = if (missingIndices.isNotEmpty()) {
            val reusedIndex = missingIndices.removeAt(0)
            Log.d("AddTrainingPlan", "Reusing missing index: $reusedIndex")

            if (!indexxx.contains(reusedIndex.toString())) {
                indexxx.add(reusedIndex.toString())
            }

            reusedIndex
        } else {
            trainingPlanLayouts.size
        }

        Log.d("SPSPSPSPP", "addTrainingPlanView: $indexToAdd")

        val sortedIndices = indexxx.mapNotNull { it.toIntOrNull() }.sorted()

        val indexForView = if (sortedIndices.isNotEmpty()) {

            (1..sortedIndices.maxOrNull()!!).firstOrNull { !sortedIndices.contains(it) } ?: sortedIndices.size -1
        } else {
            indexToAdd
        }

        Log.d("AddTrainingPlannnnnnnnn", "Using index: $indexForView")

        Log.d("DBBBHBHBHB", "addTrainingPlanView: $indexToAdd")

        val trainingPlanView = layoutInflater.inflate(R.layout.add_training_plan_list, null)

        var nameEditText: AppCompatEditText = trainingPlanView.findViewById(R.id.ent_pre_sea_name)
        val startDateEditText: AppCompatEditText = trainingPlanView.findViewById(R.id.ent_start_date_liner)
        val endDateEditText: AppCompatEditText = trainingPlanView.findViewById(R.id.ent_ent_date_liner)
        val delete: ImageView = trainingPlanView.findViewById(R.id.img_delete)
        val startdatecard: CardView = trainingPlanView.findViewById(R.id.card_start_date_list)
        val enddatecard: CardView = trainingPlanView.findViewById(R.id.card_end_pick_list)
        val mesocycles: TextView = trainingPlanView.findViewById(R.id.linear_days_list)

        val startDateMillis = if (!startDate.isNullOrEmpty()) formatDateToMillis2(startDate) else null
        val formattedStartDate = startDateMillis?.let { formatMillisToDateString(it) } ?: ""

        val endDateInMillis = if (!endDate.isNullOrEmpty()) formatDateToMillis2(endDate) else null
        val formattedEndDate = endDateInMillis?.let { formatMillisToDateString(it) } ?: ""



        nameEditText.setText(name)
        startDateEditText.setText(formattedStartDate)
        endDateEditText.setText(formattedEndDate)
        mesocycles.setText(mesocycle)


//        val nameEditText: AppCompatEditText = newTrainingPlanLayout.findViewById(R.id.ent_pre_sea_name)
//        nameEditText.hint = getTrainingPlanDetails(indexToAdd)

        val startDateMillisssss = formatDateToMillis2(startDate)
        val endDateMillisssss = formatDateToMillis2(endDate)

        val formattedStartDatesss = formatMillisToDateString(startDateMillisssss)
        val formattedEndDatesss = formatMillisToDateString(endDateMillisssss)

        selectedDateRanges.add(startDateMillisssss to endDateMillisssss)

        val errorStartDateMillis = parseFormattedDateToMillis(formattedStartDate)
        val errorEndDateMillis = parseFormattedDateToMillis(formattedEndDate)

        errorstartdatelist = formatDate(errorStartDateMillis)
        errorenddatelist = formatDate(errorEndDateMillis)

        Log.d(
            "TYYTYTYYY",
            "Formatted Start Date: $errorstartdatelist, Formatted End Date: $errorenddatelist"
        )

        val updateDaysTextViewlist = {
            val startDate = startDateEditText.text.toString()
            val endDate = endDateEditText.text.toString()

            Log.d("GHBGBGBGB", "addTrainingPlanView: $startDate   $endDate")

            val days = calculateDaysBetweenDates(
                startDate.toString(),
                endDate.toString()
            )
            mesocycles.text = "$days days"

        }

        updateDaysTextViewlist()

        delete.setOnClickListener {
            missingIndices.add(indexToAdd)
            removeTrainingPlan(trainingPlanView)
        }

        startDateEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateDaysTextViewlist()
                updateUI(editTrainingPlanBinding.cardSave)
            }
        })

        endDateEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateDaysTextViewlist()
                updateUI(editTrainingPlanBinding.cardSave)
            }
        })

        startDateEditText.setOnClickListener {
            // Get the index of trainingPlanView inside trainingPlanContainer
            val layoutIndex = trainingPlanContainer.indexOfChild(trainingPlanView)
            val savedIndex = if (layoutIndex in indexxx.indices) indexxx[layoutIndex] else null

            Log.d("DEBUG", "Layout Index: $layoutIndex")
            Log.d("DEBUG", "Saved Index from indexxx: $savedIndex")

            activeLayoutIndex = savedIndex?.toInt()

            if (editTrainingPlanBinding.edtStartDate.text.isNullOrEmpty()) {
                Toast.makeText(this, "Please select a start date first", Toast.LENGTH_SHORT).show()
            } else {
                val minDateMillis =
                    formatDateToMillis(editTrainingPlanBinding.edtStartDate.text.toString())
                val maxDateMillis =
                    formatDateToMillis(editTrainingPlanBinding.edtEndDate.text.toString())

                showDateRangePickerDialogfor(
                    startDateEditText.context,
                    minDateMillis,


                    maxDateMillis,
                    savedIndex?.toInt()
                ) { start, end ->
                    startDateMilliss = start
                    endDateMilliss = end
                    val formattedStartDate = formatDate(start)
                    val formattedStartDate2 = formatDate2(start)
                    val formattedEndDate = formatDate(end)
                    val formattedEndDate2 = formatDate2(end)

                    startDateEditText.setText(formattedStartDate2)
                    endDateEditText.setText(formattedEndDate2)
                    updateDaysTextView()
                    startdatesentlist = formattedStartDate
                    enddatesentlist = formattedEndDate
                    dateRangeMap[savedIndex?.toInt() ?: 0] = Pair(formattedStartDate, formattedEndDate)
                    updateDaysTextView()
                }
            }
        }

        startdatecard.setOnClickListener {
            val layoutIndex = trainingPlanContainer.indexOfChild(trainingPlanView)
            activeLayoutIndex = layoutIndex

            if (editTrainingPlanBinding.edtStartDate.text.isNullOrEmpty()) {
                Toast.makeText(this, "Please select a start date first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                val minDateMillis =
                    formatDateToMillis(editTrainingPlanBinding.edtStartDate.text.toString())
                val maxDateMillis =
                    formatDateToMillis(editTrainingPlanBinding.edtEndDate.text.toString())

                showDateRangePickerDialogfor(
                    startDateEditText.context,
                    minDateMillis,
                    maxDateMillis,
                    layoutIndex
                ) { start, end ->

                    val sharedPreferences =
                        getSharedPreferences("mysharedprefs", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putBoolean("isFromStartDate", true)
                    editor.apply()

                    startDateMilliss = start
                    endDateMilliss = end
                    val formattedStartDate = formatDate(start)
                    val formattedStartDate2 = formatDate2(start)
                    val formattedEndDate = formatDate(end)
                    val formattedEndDate2 = formatDate2(end)
                    startDateEditText.setText(formattedStartDate2)
                    endDateEditText.setText(formattedEndDate2)
                    updateDaysTextView()
                    startdatesentlist = formattedStartDate
                    enddatesentlist = formattedEndDate
                    dateRangeMap[layoutIndex] = Pair(formattedStartDate, formattedEndDate)
                    selectedDateRanges.add(startDateMilliss to endDateMilliss)
                }
            }
        }

        endDateEditText.setOnClickListener {
            val layoutIndex = trainingPlanContainer.indexOfChild(trainingPlanView)
            activeLayoutIndex = layoutIndex
            if (editTrainingPlanBinding.edtEndDate.text.isNullOrEmpty()) {
                Toast.makeText(this, "Please select a start date first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                val minDateMillis =
                    formatDateToMillis(editTrainingPlanBinding.edtStartDate.text.toString())
                val maxDateMillis =
                    formatDateToMillis(editTrainingPlanBinding.edtEndDate.text.toString())

                showDateRangePickerDialogfor(
                    startDateEditText.context,
                    minDateMillis,
                    maxDateMillis,
                    layoutIndex
                ) { start, end ->
                    startDateMilliss = start
                    endDateMilliss = end
                    val formattedStartDate = formatDate(start)
                    val formattedStartDate2 = formatDate2(start)
                    val formattedEndDate = formatDate(end)
                    val formattedEndDate2 = formatDate2(end)

                    startDateEditText.setText(formattedStartDate2)
                    endDateEditText.setText(formattedEndDate2)
                    updateDaysTextView()
                    startdatesentlist = formattedStartDate
                    enddatesentlist = formattedEndDate
                    dateRangeMap[layoutIndex] = Pair(formattedStartDate, formattedEndDate)
                    selectedDateRanges.add(startDateMilliss to endDateMilliss)
                }
            }
        }

        enddatecard.setOnClickListener {
            val layoutIndex = trainingPlanContainer.indexOfChild(trainingPlanView)
            val savedIndex = if (layoutIndex in indexxx.indices) indexxx[layoutIndex] else null

            Log.d("DEBUG", "Layout Index: $layoutIndex")
            Log.d("DEBUGGGGGGGGG", "Saved Index from indexxx: $savedIndex")

            activeLayoutIndex = savedIndex?.toInt()


            if (editTrainingPlanBinding.edtEndDate.text.isNullOrEmpty()) {
                Toast.makeText(this, "Please select a start date first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                val minDateMillis =
                    formatDateToMillis(editTrainingPlanBinding.edtStartDate.text.toString())
                val maxDateMillis =
                    formatDateToMillis(editTrainingPlanBinding.edtEndDate.text.toString())

                showDateRangePickerDialogfor(
                    startDateEditText.context,
                    minDateMillis,
                    maxDateMillis,
                    savedIndex?.toIntOrNull()
                ) { start, end ->
                    startDateMilliss = start
                    endDateMilliss = end
                    val formattedStartDate = formatDate(start)
                    val formattedStartDate2 = formatDate2(start)
                    val formattedEndDate = formatDate(end)
                    val formattedEndDate2 = formatDate2(end)

                    startDateEditText.setText(formattedStartDate2)
                    endDateEditText.setText(formattedEndDate2)
                    updateDaysTextView()
                    startdatesentlist = formattedStartDate
                    enddatesentlist = formattedEndDate
                    dateRangeMap[layoutIndex] = Pair(formattedStartDate, formattedEndDate)
                    selectedDateRanges.add(startDateMilliss to endDateMilliss)
                }
            }
        }

        startDateEditText.setOnFocusChangeListener { _, _ -> updateDaysTextViewlist() }
        endDateEditText.setOnFocusChangeListener { _, _ -> updateDaysTextViewlist() }
        startdatecard.setOnFocusChangeListener { _, _ -> updateDaysTextViewlist() }
        enddatecard.setOnFocusChangeListener { _, _ -> updateDaysTextViewlist() }
        mesocycles.setOnFocusChangeListener { _, _ -> updateDaysTextViewlist() }

        trainingPlanContainer.addView(trainingPlanView, indexForView)
        trainingPlanLayouts.add(indexForView, trainingPlanView)

        updateTrainingPlanIndices()
//        nameEditText = trainingPlanView.findViewById(R.id.ent_pre_sea_name)
//        nameEditText.hint = getTrainingPlanDetails(indexToAdd)

        Log.d("AddTrainingPlan", "Training plan added for phase: $phaseType")
    }

    private fun getPlanNameForIndex(view: View?): String {
        val nameEditText: AppCompatEditText? = view?.findViewById(R.id.ent_pre_sea_name)
        val nameString = nameEditText?.text?.toString()?.trim() ?: ""

        Log.d("PLAN_NAME_DEBUG", "Retrieved Plan Name: $nameString")

        return nameString
    }

    private fun getMesocycleForIndex(view: View?): String {
        val mesocycleTextView: TextView? = view?.findViewById(R.id.linear_days_list)
        val mesocycleString = mesocycleTextView?.text?.toString()?.trim() ?: ""

        Log.d("MESOCYCLE_DEBUG", "Retrieved Mesocycle: $mesocycleString")

        return mesocycleString
    }


    private fun getStartDateForIndex(view: View?, fallbackStart: String): String {
        val startDateEditText: AppCompatEditText? = view?.findViewById(R.id.ent_start_date_liner)
        val startDateString = startDateEditText?.text?.toString()?.trim() ?: return fallbackStart

        Log.d("DEBUDKKDKDKDKDG", "Retrieved Start Date String: $startDateString    $view")

        val formattedStartDate = formatDateToString(startDateString)

        Log.d("SLPLSLSLSPLSPLSLS", "Formatted Start Date: $formattedStartDate")

        return formattedStartDate ?: fallbackStart
    }

    private fun getEndDateForIndex(view: View?, fallbackEnd: String): String {
        val endDateEditText: AppCompatEditText? = view?.findViewById(R.id.ent_ent_date_liner)
        val endDateString = endDateEditText?.text?.toString()?.trim() ?: return fallbackEnd

        Log.d("SLPLSLSLSPLSPLSLS", "Retrieved End Date String: $endDateString")

        val formattedEndDate = formatDateToString(endDateString)

        Log.d("SLPLSLSLSPLSPLSLS", "Formatted End Date: $formattedEndDate")

        return formattedEndDate ?: fallbackEnd
    }


    private fun formatDateToString(dateString: String): String? {
        return try {
            val inputDateFormat = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
            val parsedDate = inputDateFormat.parse(dateString)

            val outputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formattedDate = outputDateFormat.format(parsedDate)

            // Log the final formatted date
            Log.d("DEBUG", "Formatted Date: $formattedDate")
            formattedDate
        } catch (e: Exception) {
            Log.e("DateError", "Error formatting date: ${e.message}")
            null
        }
    }




    private fun EditeProgramData() {
        if (!isValidate()) return

        try {
            var plan1StartDate = ""
            var plan1EndDate = ""

            Log.d(
                "EditeData",
                "Child count in trainingPlanContainer: ${trainingPlanContainer.childCount}"
            )

            for (i in 0 until trainingPlanContainer.childCount) {
                val layout = trainingPlanContainer.getChildAt(i)
                Log.d("DEBUG", "Layout $i: ${layout}")
                val nameEditText: AppCompatEditText? = layout.findViewById(R.id.ent_pre_sea_name)
                val startDateEditText: AppCompatEditText? =
                    layout.findViewById(R.id.ent_start_date_liner)
                val endDateEditText: AppCompatEditText? =
                    layout.findViewById(R.id.ent_ent_date_liner)
                val mesocycles: TextView? = layout.findViewById(R.id.linear_days_list)

                if (nameEditText == null || startDateEditText == null || endDateEditText == null || mesocycles == null) {
                    continue
                }
                Log.d(
                    "45545545455",
                    "EditeProgramData: $errorstartdatelist    END:-  $errorenddatelist"
                )

                var name = nameEditText.text.toString().trim()
                val mesocycle = mesocycles.text.toString().trim()
                val startDate = startDateEditText.text.toString().trim()
                val endDate = endDateEditText.text.toString().trim()

                Log.d(
                    "DEBUGGGGGG",
                    "Retrieved start date: $startDate, end date: $endDate for layout $i"
                )

                val errorTextView: TextView = layout.findViewById(R.id.error_start_date_list)

//                if (startdatesentlist.isEmpty() && enddatesentlist!!.isEmpty()) {
//                    continue
//                }
//                if (name.isEmpty()) {
//                    Toast.makeText(
//                        this,
//                        "Please fill in all fields for plan ${i + 1}",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    return
//                }

                val startDateMillis = formatDateToMillistest(startDate)
                val endDateMillis = formatDateToMillistest(endDate)

                val formattedStartDate = formatMillisToDateString(startDateMillis)
                val formattedEndDate = formatMillisToDateString(endDateMillis)

                val errorStartDateMillis = parseFormattedDateToMillis(formattedStartDate)
                val errorTestDateMillis = parseFormattedDateToMillis(formattedEndDate)

                val finalStartDates = formatDate(errorStartDateMillis)
                val finalEndDates = formatDate(errorTestDateMillis)

                Log.d("DEBUUUUUG", "Final formatted date: $finalStartDates    end:- $finalEndDates")

                val finalStartDate = if (finalStartDates.isNullOrEmpty()) errorstartdatelist.toString() else finalStartDates
                val finalEndDate = if (finalEndDates.isNullOrEmpty()) errorenddatelist.toString() else finalEndDates


                Log.d("HFHFHFHFH", "EditeProgramData   START: $finalStartDate")
                Log.d("HFHFHFHFH", "EditeProgramData   END: $finalEndDate")

                if (finalStartDate.isEmpty() || finalEndDate.isEmpty()) {
                    Toast.makeText(
                        this,
                        "Please fill in valid dates for Plan ${i + 1}",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }


                val startDateMin = editTrainingPlanBinding.edtStartDate.text.toString()
                val stratDateMinMill = formatDateToMillistest(startDateMin)

                val endDateMax = editTrainingPlanBinding.edtEndDate.text.toString()
                val endDateMaxMill = formatDateToMillistest(endDateMax)

                val readableStartDate = SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.getDefault()
                ).format(Date(stratDateMinMill))
                val readableEndDate =
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(endDateMaxMill))
                Log.d(
                    "MLMLMLML",
                    "Readable Start Date from Millis: $readableStartDate     $readableEndDate"
                )

                if (finalStartDate < readableStartDate.toString() || finalEndDate > readableEndDate.toString()) {
                    errorTextView.visibility = View.VISIBLE
                    errorTextView.text = "Invalid Date"
                    errorTextView.setTextColor(Color.RED)
                    return
                }

                if (i == 0) {
                    plan1StartDate = startDate
                    plan1EndDate = endDate
                }

                if (i != 0) {
                    if (isConflict(startDate, endDate, i)) {
                        errorTextView.visibility = View.VISIBLE
                        errorTextView.text = "Date conflict detected for Plan ${i + 1}"
                        errorTextView.setTextColor(Color.RED)
                        return
                    } else {
                        errorTextView.visibility = View.GONE
                    }
                }
                Log.d("TYYTYTYYY", "EditeProgramData: $errorstartdatelist    END:-  $errorenddatelist")

                val handledIndexes = HashSet<String>()

                Log.d("REMOVEINDEXXXXXXX", "removeTrainingPlan: $indexxx")

                val childViewsMap = mutableMapOf<String, View>()
                val originalIndexes = indexxx.toSet().toMutableList() // Convert to Set to remove duplicates

                for ((pos, i) in originalIndexes.withIndex()) {
                    trainingPlanContainer.getChildAt(pos)?.let { view ->
                        childViewsMap[i] = view
                    }
                }

                for (i in originalIndexes) {
                    if (handledIndexes.contains(i)) continue
                    handledIndexes.add(i)

                    Log.d("JHDHDHHDUUUUUUU", "checkButtonClick: $originalIndexes")
                    Log.d("JHDHDHHDUUUUUUU", "checkButtonClick: $i")

                    val name = getPlanNameForIndex(childViewsMap[i])
                    val finalStartDate = getStartDateForIndex(childViewsMap[i], finalStartDate)
                    val finalEndDate = getEndDateForIndex(childViewsMap[i], finalEndDate)
                    val mesocycle = getMesocycleForIndex(childViewsMap[i])

                    Log.d("DDKKDKDKDK", "checkButtonClick: $i")
                    Log.d("DDKKDKDKDKOOOO", "checkButtonClick: $handledIndexes")
                    Log.d("DDKKDKDKDKOOOOMMMMMM", "checkButtonClick: $finalStartDate    $finalEndDate     $mesocycle")

                    when (i) {
                        "0" -> if (preSeason.name.isEmpty()) {
                            preSeason = PreSeason(name.ifEmpty { "Pre Season" }, finalStartDate, finalEndDate, mesocycle)
                        }
                        "1" -> if (preCompetitive.name.isEmpty()) {
                            preCompetitive = PreCompetitive(name.ifEmpty { "Pre Competitive" }, finalStartDate, finalEndDate, mesocycle)
                        }
                        "2" -> if (competitive.name.isEmpty()) {
                            competitive = Competitive(name.ifEmpty { "Competitive" }, finalStartDate, finalEndDate, mesocycle)
                        }
                        "3" -> if (transition.name.isEmpty()) {
                            transition = Transition(name.ifEmpty { "Transition" }, finalStartDate, finalEndDate, mesocycle)
                        }
                    }
                }



            }

            val programName = editTrainingPlanBinding.edtProgramName.text.toString().trim()
            if (programName.isEmpty()) {
                Toast.makeText(
                    this,
                    "Please fill in the program name, start date, and end date",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            // Check for conflicts with the first plan's date range
            if (isConflictWithOtherPlans(plan1StartDate, plan1EndDate)) {
                Toast.makeText(
                    this,
                    "Plan 1 conflicts with another plan. Cannot save.",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            val finalStartDate =
                startdatesent?.takeIf { it.isNotEmpty() } ?: errorstartdate.toString()
            val finalEndDate = enddatesent?.takeIf { it.isNotEmpty() } ?: errorenddate.toString()

            Log.d("final", "EditeProgramData: $finalEndDate   $finalStartDate")

            // Create the request for the API
            val trainingPlanRequest = TrainingPlanSubClass(
                id = id!!,
                name = programName,
                competition_date = finalEndDate,
                start_date = finalStartDate,
                pre_season = preSeason,
                pre_competitive = preCompetitive,
                competitive = competitive,
                transition = transition,
                mesocycle = editTrainingPlanBinding.days.text.toString()
            )

            // Make the API request to edit the training plan
            apiInterface.EditeTrainingPlan(editeTrainingPlan = trainingPlanRequest)
                ?.enqueue(object : Callback<TrainingPlanData> {
                    override fun onResponse(
                        call: Call<TrainingPlanData>,
                        response: Response<TrainingPlanData>
                    ) {
                        if (response.isSuccessful && response.body()?.status == true) {
                            Toast.makeText(
                                this@EditTrainingPlanActivity,
                                "Success: ${response.body()?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        } else {
                            val errorMessage = response.body()?.message ?: "Something went wrong"
                            Toast.makeText(
                                this@EditTrainingPlanActivity,
                                errorMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<TrainingPlanData>, t: Throwable) {
                        Log.e("TAG3", "onResponse failure: ${t.message}", t)
                        Toast.makeText(
                            this@EditTrainingPlanActivity,
                            "Network error: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })

            Log.d("EditTraining :-", "$trainingPlanRequest")

        } catch (e: Exception) {
            Log.e("Exception", e.message.toString(), e)
        }
    }

    private fun isValidDate(date: String): Boolean {
        val regex = Regex("^\\d{4}-\\d{2}-\\d{2}$")
        return date.matches(regex)
    }

    fun isConflictWithOtherPlans(startDate: String, endDate: String): Boolean {
        val startMillis = convertDateToMillis(startDate)
        val endMillis = convertDateToMillis(endDate)

        Log.d("DATE_CHECK", "Checking Plan 1: Start: $startMillis, End: $endMillis")

        for (i in 1 until trainingPlanContainer.childCount) {
            val layout = trainingPlanContainer.getChildAt(i)
            val existingStartDate: AppCompatEditText =
                layout.findViewById(R.id.ent_start_date_liner)
            val existingEndDate: AppCompatEditText = layout.findViewById(R.id.ent_ent_date_liner)

            val existingStartMillis = convertDateToMillis(existingStartDate.text.toString().trim())
            val existingEndMillis = convertDateToMillis(existingEndDate.text.toString().trim())

            Log.d("DATE_CHECK", "Plan ${i + 1}: Start: $existingStartMillis, End: $existingEndMillis")

            if (isOverlapping(startMillis, endMillis, existingStartMillis, existingEndMillis)) {
                Log.d("DATE_CHECK", "Conflict detected between Plan 1 and Plan ${i + 1}")
                return true
            }
        }
        Log.d("DATE_CHECK", "No conflict detected for Plan 1")
        return false
    }


    fun formatDate1(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("d MMM yyyy", Locale.ENGLISH)
        val date = Date(timestamp)
        return dateFormat.format(date)
    }
    private fun setProgramDataset(data: TrainingPlanData.TrainingPlan) {

        val startDateMillis = formatDateToMillis2(data.start_date)
        val formattedStartDate = formatMillisToDateString(startDateMillis)

        val endDateInMillis = formatDateToMillis2(data.competition_date)
        val formattedEndDate = formatMillisToDateString(endDateInMillis)

        editTrainingPlanBinding.edtProgramName.setText(data.name)
        editTrainingPlanBinding.edtStartDate.setText(formattedStartDate)
        editTrainingPlanBinding.edtEndDate.setText(formattedEndDate)
        editTrainingPlanBinding.days.text = data.mesocycle

        updateDaysTextView()

//        selectedDateRanges.clear() // Clear any existing date ranges
//        selectedDateRanges.add(startDateMillis to endDateInMillis) // Add the main program date range

//        Log.d("TYTYYTYTY", "setProgramDataset: $startDateMillis     $endDateInMillis")
//        Log.d("TYTYYTYTY", "setProgramDataset: ${selectedDateRanges.first().toString()}   ${selectedDateRanges.toString()}")


// Accessing the first pair and formatting the first element (start date)


        Log.d("QWQWQWQW", "setProgramDataset: ${formatDate1(startDateMillis)}   ${formatDate1(endDateInMillis)}")

        val errorStartDateMillis = parseFormattedDateToMillis(formattedStartDate)
        val errorEndDateMillis = parseFormattedDateToMillis(formattedEndDate)

        errorstartdate = formatDate(errorStartDateMillis)
        errorenddate = formatDate(errorEndDateMillis)

        trainingPlanContainer.removeAllViews()
        var count = 0

        data.pre_season?.let { preSeason ->
            Log.d("FKFKFKK()", "setProgramDataset: ${preSeason.name}")
            if (!preSeason.start_date.isNullOrEmpty() && !preSeason.end_date.isNullOrEmpty()) {
                count++
                indexxx.add("0")
                addTrainingPlanView(
                    preSeason.name ?: "",
                    preSeason.start_date ?: "",
                    preSeason.end_date ?: "",
                    preSeason.mesocycle ?: "0 Days",
                    "Pre-Season"
                )

                val preSeasonStartMillis = formatDateToMillis2(preSeason.start_date)
                val preSeasonEndMillis = formatDateToMillis2(preSeason.end_date)
//                selectedDateRanges.add(preSeasonStartMillis to preSeasonEndMillis)
            }
        }

        data.pre_competitive?.let { preCompetitive ->
            Log.d("FKFKFKK()", "PreCompetitive: ${preCompetitive.name}")
            if (!preCompetitive.name.isNullOrEmpty() && !preCompetitive.start_date.isNullOrEmpty() && !preCompetitive.end_date.isNullOrEmpty()) {
                count++
                indexxx.add("1")
                addTrainingPlanView(
                    preCompetitive.name ?: "",
                    preCompetitive.start_date ?: "",
                    preCompetitive.end_date ?: "",
                    preCompetitive.mesocycle ?: "0 Days",
                    "Pre-Competitive"
                )

                // Add date range for Pre-Competitive
                val preCompetitiveStartMillis = formatDateToMillis2(preCompetitive.start_date)
                val preCompetitiveEndMillis = formatDateToMillis2(preCompetitive.end_date)
//                selectedDateRanges.add(preCompetitiveStartMillis to preCompetitiveEndMillis)
            }
        }

        data.competitive?.let { competitive ->
            Log.d("FKFKFKK()", "Competitive: ${competitive.name}")
            if (!competitive.start_date.isNullOrEmpty() && !competitive.end_date.isNullOrEmpty()) {
                count++
                indexxx.add("2")
                addTrainingPlanView(
                    competitive.name ?: "",
                    competitive.start_date ?: "",
                    competitive.end_date ?: "",
                    competitive.mesocycle ?: "0 Days",
                    "Competitive"
                )

                // Add date range for Competitive
                val competitiveStartMillis = formatDateToMillis2(competitive.start_date)
                val competitiveEndMillis = formatDateToMillis2(competitive.end_date)
//                selectedDateRanges.add(competitiveStartMillis to competitiveEndMillis)
            }
        }

        data.transition?.let { transition ->
            Log.d("FKFKFKK()", "Transition: ${transition.name}")
            if (!transition.start_date.isNullOrEmpty() && !transition.end_date.isNullOrEmpty()) {
                count++
                indexxx.add("3")
                addTrainingPlanView(
                    transition.name ?: "",
                    transition.start_date ?: "",
                    transition.end_date ?: "",
                    transition.mesocycle ?: "0 Days",
                    "Transition"
                )

                // Add date range for Transition
                val transitionStartMillis = formatDateToMillis2(transition.start_date)
                val transitionEndMillis = formatDateToMillis2(transition.end_date)
//                selectedDateRanges.add(transitionStartMillis to transitionEndMillis)
            }
        }

    }

    private fun updateTrainingPlanIndices() {
        val defaultNames = listOf("Enter Pre Season", "Enter Pre Competitive", "Enter Competitive", "Enter Transition")

        Log.d("DPPDPSPPSPAA", "updateTrainingPlanIndices: $indexxx")


        indexxx.forEachIndexed { listIndex, actualIndexStr ->
            val actualIndex = actualIndexStr.toIntOrNull() ?: return@forEachIndexed

            if (listIndex < trainingPlanLayouts.size) {
                val layout = trainingPlanLayouts[listIndex]
                val nameEditText: AppCompatEditText = layout.findViewById(R.id.ent_pre_sea_name)

                Log.d(")(((((((", "updateTrainingPlanIndices: $actualIndex")

                val nameToSet = defaultNames.getOrNull(actualIndex) ?: nameEditText.hint
                nameEditText.setHint(nameToSet)

                Log.d("UpdateTrainingPlan", "Index: $actualIndex, Name: ${nameEditText.hint}")
            }
        }
    }

    private fun getTrainingPlanDetails(planNumber: Int): String {
        return when (planNumber) {
            1 -> "Enter Pre Season Name"
            2 -> "Enter Pre Competitive Name"
            3 -> "Enter Competitive Name"
            4 -> "Enter Transition Name"
            else -> "Training Plan #:"
        }
    }

    private fun removeTrainingPlan(trainingPlanLayout: View) {
        val index = trainingPlanLayouts.indexOf(trainingPlanLayout)
        if (index != -1) {
            trainingPlanContainer.removeView(trainingPlanLayout)
            trainingPlanLayouts.removeAt(index)
            trainingPlanCount--
            dateRangeMap.remove(index)
            indexxx.removeAt(index)
            saveTrainingPlansToPreferences(index)

            Log.d("REMOVEINDEXXXXXXX", "removeTrainingPlan: $indexxx")
            if (activeLayoutIndex == index) {
                activeLayoutIndex = null
            } else if (activeLayoutIndex != null && activeLayoutIndex!! > index) {
                activeLayoutIndex = activeLayoutIndex?.minus(1)
            }

            // Move the removed index to missingIndices
            missingIndices.add(index)

            // Log the removal
            Log.d("RemoveTrainingPlan", "Index $index added to missing indices.")

            // Adjust the indices for the remaining plans
            adjustIndicesAfterRemoval(index)
        }
    }

    private fun adjustIndicesAfterRemoval(removedIndex: Int) {
        // Adjust the indices in trainingPlanLayouts to maintain the correct order
        for (i in removedIndex until trainingPlanLayouts.size) {
            // Shift the index of each subsequent item
            dateRangeMap[i]?.let {
                dateRangeMap[i] = Pair(it.first, it.second)
            }
        }

        // You can also update saved data if necessary, here we'll just handle UI state
        Log.d("AdjustIndices", "Reindexed plans after removal. Current layouts: $trainingPlanLayouts")
    }


    private fun formatDate(dateMillis: Long): String {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return format.format(Date(dateMillis))
    }

    private fun formatDate2(dateMillis: Long): String {
        val format = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
        return format.format(Date(dateMillis))
    }

    private fun formatDateToMillis(dateString: String): Long {
        val format = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
        return try {
            format.parse(dateString)?.time ?: Long.MAX_VALUE
        } catch (e: Exception) {
            Long.MAX_VALUE
        }
    }


    // Method to convert date strings to milliseconds
    private fun formatDateToMillis2(dateString: String?): Long {
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = format.parse(dateString)
            date?.time ?: System.currentTimeMillis() // Fallback to current time if parsing fails
        } catch (e: Exception) {
            Log.e("DateConversion", "Error converting date: ${e.message}")
            System.currentTimeMillis() // Fallback to current time
        }
    }

    private fun formatDateToMillistest(dateString: String?): Long {
        return try {
            Log.d("VBBVBBBBB", "formatDateToMillistest: ok")
            val format = SimpleDateFormat(
                "dd MMM, yyyy",
                Locale.getDefault()
            ) // Updated format to match "02 Jan, 2025"
            val date = format.parse(dateString)
            date?.time ?: System.currentTimeMillis() // Fallback to current time if parsing fails
        } catch (e: Exception) {
            Log.e("DateConversion009990", "Error converting date: ${e.message}")
            System.currentTimeMillis() // Fallback to current time
        }
    }


    // Method to format milliseconds into the desired date format
    private fun formatMillisToDateString(millis: Long): String {
        val format = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
        return format.format(millis)
    }

    // Method to convert formatted date strings back to milliseconds
    private fun parseFormattedDateToMillis(dateString: String?): Long {
        return try {
            val format = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
            val date = format.parse(dateString)
            date?.time ?: System.currentTimeMillis() // Fallback to current time if parsing fails
        } catch (e: Exception) {
            Log.e("DateConversion", "Error parsing formatted date: ${e.message}")
            System.currentTimeMillis() // Fallback to current time
        }
    }


    private fun setupTextWatcher() {
        editTrainingPlanBinding.edtProgramName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        editTrainingPlanBinding.edtStartDate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateDaysTextView()
//                updateUI(editTrainingPlanBinding.cardSave)
            }
        })

        editTrainingPlanBinding.edtEndDate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateDaysTextView()
//                updateUI(editTrainingPlanBinding.cardSave)
            }
        })
    }

    private fun updateDaysTextView() {
        val startDate = editTrainingPlanBinding.edtStartDate.text.toString()
        val endDate = editTrainingPlanBinding.edtEndDate.text.toString()

        val days = calculateDaysBetweenDates(startDate.toString(), endDate.toString())
        editTrainingPlanBinding.days.text = "$days Days"
    }

    private fun calculateDaysBetweenDates(start: String, end: String): Int {
        return try {
            val format = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
            val startDate = format.parse(start)
            val endDate = format.parse(end)
            val difference = endDate.time - startDate.time
            (difference / (1000 * 60 * 60 * 24)).toInt()  // Convert milliseconds to days
        } catch (e: Exception) {
            Log.e("DateCalculation", "Error calculating days: ${e.message}")
            0  // Return 0 if there's an error to avoid crashes
        }
    }


    private fun isValidate(): Boolean {

        if (editTrainingPlanBinding.edtProgramName.text.isNullOrEmpty()) {
            editTrainingPlanBinding.errorProgram.visibility = View.VISIBLE
            return false

        } else {
            editTrainingPlanBinding.errorProgram.visibility = View.GONE
        }

        if (editTrainingPlanBinding.edtStartDate.text.isNullOrEmpty()) {
            editTrainingPlanBinding.errorStartDate.visibility = View.VISIBLE
            return false
        } else {
            editTrainingPlanBinding.errorStartDate.visibility = View.GONE
        }

        if (editTrainingPlanBinding.edtEndDate.text!!.isEmpty()) {
            editTrainingPlanBinding.errorEndDate.visibility = View.VISIBLE
            return false
        } else {
            editTrainingPlanBinding.errorEndDate.visibility = View.GONE

        }

        for (i in 0 until trainingPlanContainer.childCount) {
            val layout = trainingPlanContainer.getChildAt(i)
            val nameEditText: AppCompatEditText = layout.findViewById(R.id.ent_pre_sea_name)
            val startDateEditText: AppCompatEditText =
                layout.findViewById(R.id.ent_start_date_liner)
            val endDateEditText: AppCompatEditText = layout.findViewById(R.id.ent_ent_date_liner)

//            if (nameEditText.text.isNullOrEmpty()) {
//                return false
//            }

            if (startDateEditText.text.isNullOrEmpty()) {
                layout.findViewById<TextView>(R.id.error_start_date_list).visibility = View.VISIBLE
                return false
            } else {
                layout.findViewById<TextView>(R.id.error_start_date_list).visibility = View.GONE
            }
            if (endDateEditText.text.isNullOrEmpty()) {
                layout.findViewById<TextView>(R.id.error_end_date_list).visibility = View.VISIBLE
                return false
            } else {
                layout.findViewById<TextView>(R.id.error_end_date_list).visibility = View.GONE
            }
        }
        return true
    }

    // Method to select the start date for the training plan
    private fun selectStartDate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Utils.selectDate3(
                this,
                editTrainingPlanBinding.edtStartDate,
                System.currentTimeMillis(), // Minimum date: now
                Long.MAX_VALUE // Maximum date: infinity
            ) { dateMillis ->
                startDateMilliss = dateMillis
                val formattedDateDisplay = formatDate2(dateMillis)

                startdatesent = formatDate(dateMillis)
                editTrainingPlanBinding.edtStartDate.setText(formattedDateDisplay)
                endDateMilliss = 0
            }
        }
    }

    // Method to select the end date for the training plan
    private fun selectEndDate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val maxDateMillis = Long.MAX_VALUE // Set max date to infinity
            val minDateMillis =
                if (startDateMilliss > 0) startDateMilliss else System.currentTimeMillis() // Use the start date or current time

            Utils.selectDate3(
                this,
                editTrainingPlanBinding.edtEndDate,
                minDateMillis,
                maxDateMillis
            ) { dateMillis ->
                endDateMilliss = dateMillis
                val formattedDateDisplay = formatDate2(dateMillis)

                enddatesent = formatDate(dateMillis)
                editTrainingPlanBinding.edtEndDate.setText(formattedDateDisplay)
            }
        }
    }

    private fun selectTrainingPlanStartDate(editText: AppCompatEditText) {
        val minDateMillis =
            if (startDateMilliss > 0) startDateMilliss else System.currentTimeMillis()
        Utils.selectDate3(this, editText, minDateMillis, Long.MAX_VALUE) { dateMillis ->
            startDateMilliss = dateMillis
            val formattedDateDisplay = formatDate2(dateMillis)
            startdatesentlist = formatDate(dateMillis)
            editText.setText(formattedDateDisplay)
        }
    }

    private fun selectTrainingPlanEndDate(editText: AppCompatEditText) {
        val minDateMillis =
            if (startDateMilliss > 0) startDateMilliss else System.currentTimeMillis()
        Utils.selectDate3(this, editText, minDateMillis, Long.MAX_VALUE) { dateMillis ->
            endDateMilliss = dateMillis
            val formattedDateDisplay = formatDate2(dateMillis)
            enddatesentlist = formatDate(dateMillis)
            editText.setText(formattedDateDisplay)
        }
    }


    override fun onResume() {
        checkUser()
        super.onResume()
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
                        Utils.setUnAuthDialog(this@EditTrainingPlanActivity)
                    } else {
                        Toast.makeText(
                            this@EditTrainingPlanActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Toast.makeText(
                        this@EditTrainingPlanActivity,
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
}