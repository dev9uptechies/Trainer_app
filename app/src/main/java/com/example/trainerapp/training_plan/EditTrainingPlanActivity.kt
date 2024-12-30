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
    private var trainingPlanCount = 0
    private var programData: MutableList<TrainingPlanData.TrainingPlan> = mutableListOf()
    private lateinit var trainingPlanContainer: LinearLayout
    private var trainingPlanLayouts = mutableListOf<View>()
    private var missingIndices = mutableListOf<Int>()
    private val dateRangeMap = mutableMapOf<Int, Pair<String, String>>()
    private var startDateMilliss: Long = 0
    private var endDateMilliss: Long = 0

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
            addButton.setCardBackgroundColor(resources.getColor(R.color.splash_text_color)) // Change to your desired color
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

    private fun checkButtonClick() {
        editTrainingPlanBinding.add.setOnClickListener {


            if (trainingPlanCount < 4) {
                val name = ""
                val startDate = ""
                val endDate = ""
                val mesocycle = ""
                val phaseType = ""
                addTrainingPlanView(name, startDate, endDate, mesocycle, phaseType)
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

            val currentLayoutIndex = activeLayoutIndex // Pass layoutIndex of current plan or null

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

            val currentLayoutIndex = activeLayoutIndex // Pass layoutIndex of current plan or null

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

            val currentLayoutIndex = activeLayoutIndex // Pass layoutIndex of current plan or null

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

            val currentLayoutIndex = activeLayoutIndex // Pass layoutIndex of current plan or null

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
                    )
                        .show()
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

        calendarView.addDecorator(object : DayViewDecorator {
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


        calendarView.addDecorator(object : DayViewDecorator {
            override fun shouldDecorate(day: CalendarDay?): Boolean {
                if (day == null || day.calendar == null) return false

                val dateInMillis = day.calendar.timeInMillis

                if (layoutIndex == null || layoutIndex == 0) return false

                if (selectedDateRanges == null) return false

                return selectedDateRanges.withIndex().any { (index, range) ->
                    val (start, end) = range
                    index < layoutIndex && dateInMillis in start..end
                }
            }

            override fun decorate(view: DayViewFacade?) {
                view?.addSpan(ForegroundColorSpan(Color.GRAY))
                view?.setDaysDisabled(true)
            }
        })

        // Disable overlapping dates with previous plans
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

        calendarView.addDecorator(object : DayViewDecorator {
            override fun shouldDecorate(day: CalendarDay?): Boolean {
                // Check if the day is part of the selected range (selected dates)
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
                // Sort the selected dates to ensure proper range order
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
                        // Update or add the selected range
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
        layoutIndex?.let {
            if (it < selectedDateRanges.size) {
                selectedDateRanges[it] = startMillis to endMillis
            } else {
                selectedDateRanges.add(startMillis to endMillis)
            }
        }
    }

    fun isConflictWithPreviousPlans(startMillis: Long, endMillis: Long, layoutIndex: Int): Boolean {
        try {


            for (i in 0 until layoutIndex) {
                val (planStart, planEnd) = selectedDateRanges[i]
                if ((startMillis in planStart..planEnd) || (endMillis in planStart..planEnd) ||
                    (startMillis < planStart && endMillis > planEnd)
                ) {
                    return true
                }
            }

        } catch (e: Exception) {
            Log.d("ERROROROROOR", "isConflictWithPreviousPlans: ${e.message.toString()}")
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
        return false // No conflict
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
        return start1 < end2 && start2 < end1
    }

    private fun addTrainingPlanView(
        name: String,
        startDate: String,
        endDate: String,
        mesocycle: String,
        phaseType: String
    ) {
        trainingPlanCount++

        val indexToAdd = if (missingIndices.isNotEmpty()) {
            val reusedIndex = missingIndices.removeAt(0)
            Log.d("AddTrainingPlan", "Reusing missing index: $reusedIndex")
            reusedIndex
        } else {
            trainingPlanLayouts.size
        }

        val trainingPlanView = layoutInflater.inflate(R.layout.add_training_plan_list, null)

        var nameEditText: AppCompatEditText = trainingPlanView.findViewById(R.id.ent_pre_sea_name)
        val startDateEditText: AppCompatEditText =
            trainingPlanView.findViewById(R.id.ent_start_date_liner)
        val endDateEditText: AppCompatEditText =
            trainingPlanView.findViewById(R.id.ent_ent_date_liner)
        val delete: ImageView = trainingPlanView.findViewById(R.id.img_delete)
        val startdatecard: CardView = trainingPlanView.findViewById(R.id.card_start_date_list)
        val enddatecard: CardView = trainingPlanView.findViewById(R.id.card_end_pick_list)
        val mesocycles: TextView = trainingPlanView.findViewById(R.id.linear_days_list)

        val startDateMillis = formatDateToMillis2(startDate)
        val formattedStartDate = formatMillisToDateString(startDateMillis)

        val endDateInMillis = formatDateToMillis2(endDate)
        val formattedEndDate = formatMillisToDateString(endDateInMillis)

        nameEditText.setText(name)
        startDateEditText.setText(formattedStartDate)
        endDateEditText.setText(formattedEndDate)
        mesocycles.setText(mesocycle)

        nameEditText = trainingPlanView.findViewById(R.id.ent_pre_sea_name)
        nameEditText.hint = getTrainingPlanDetails(indexToAdd)

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


        delete.setOnClickListener { removeTrainingPlan(trainingPlanView) }

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
            val layoutIndex = trainingPlanContainer.indexOfChild(trainingPlanView)
            activeLayoutIndex = layoutIndex

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

        startDateEditText.setOnFocusChangeListener { _, _ -> updateDaysTextViewlist() }
        endDateEditText.setOnFocusChangeListener { _, _ -> updateDaysTextViewlist() }
        startdatecard.setOnFocusChangeListener { _, _ -> updateDaysTextViewlist() }
        enddatecard.setOnFocusChangeListener { _, _ -> updateDaysTextViewlist() }
        mesocycles.setOnFocusChangeListener { _, _ -> updateDaysTextViewlist() }

        trainingPlanContainer.addView(trainingPlanView)
        trainingPlanLayouts.add(trainingPlanView)

        updateTrainingPlanIndices()
        Log.d("AddTrainingPlan", "Training plan added for phase: $phaseType")
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

                val name = nameEditText.text.toString().trim()
                val mesocycle = mesocycles.text.toString().trim()
                val startDate = startDateEditText.text.toString().trim()
                val endDate = endDateEditText.text.toString().trim()

                Log.d(
                    "DEBUGGGGGG",
                    "Retrieved start date: $startDate, end date: $endDate for layout $i"
                )

                val errorTextView: TextView = layout.findViewById(R.id.error_start_date_list)

                if (name.isEmpty() && startdatesentlist!!.isEmpty() && enddatesentlist!!.isEmpty()) {
                    continue
                }
                if (name.isEmpty()) {
                    Toast.makeText(
                        this,
                        "Please fill in all fields for plan ${i + 1}",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }

                val startDateMillis = formatDateToMillistest(startDate)
                val endDateMillis = formatDateToMillistest(endDate)

                val formattedStartDate = formatMillisToDateString(startDateMillis)
                val formattedEndDate = formatMillisToDateString(endDateMillis)

                val errorStartDateMillis = parseFormattedDateToMillis(formattedStartDate)
                val errorTestDateMillis = parseFormattedDateToMillis(formattedEndDate)

                val finalStartDate = formatDate(errorStartDateMillis)
                val finalEndDate = formatDate(errorTestDateMillis)


                Log.d("DEBUUUUUG", "Final formatted date: $finalStartDate    end:- $finalEndDate")



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

                val readableStartDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(stratDateMinMill))
                val readableEndDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(endDateMaxMill))
                Log.d("MLMLMLML", "Readable Start Date from Millis: $readableStartDate     $readableEndDate")

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
                Log.d(
                    "TYYTYTYYY",
                    "EditeProgramData: $errorstartdatelist    END:-  $errorenddatelist"
                )

                when (i) {
                    0 -> {
                        if (preSeason.name.isEmpty()) {
                            preSeason = PreSeason(name, finalStartDate, finalEndDate, mesocycle)
                        }
                    }

                    1 -> {
                        if (preCompetitive.name.isEmpty()) {
                            preCompetitive =
                                PreCompetitive(name, finalStartDate, finalEndDate, mesocycle)
                        }
                    }

                    2 -> {
                        if (competitive.name.isEmpty()) {
                            competitive = Competitive(name, finalStartDate, finalEndDate, mesocycle)
                        }
                    }

                    3 -> {
                        if (transition.name.isEmpty()) {
                            transition = Transition(name, finalStartDate, finalEndDate, mesocycle)
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
        val regex = Regex("^\\d{4}-\\d{2}-\\d{2}$")  // Matches YYYY-MM-DD
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

            Log.d(
                "DATE_CHECK",
                "Plan ${i + 1}: Start: $existingStartMillis, End: $existingEndMillis"
            )

            if (isOverlapping(startMillis, endMillis, existingStartMillis, existingEndMillis)) {
                Log.d("DATE_CHECK", "Conflict detected between Plan 1 and Plan ${i + 1}")
                return true
            }
        }
        Log.d("DATE_CHECK", "No conflict detected for Plan 1")
        return false
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

        val errorStartDateMillis = parseFormattedDateToMillis(formattedStartDate)
        val errorEndDateMillis = parseFormattedDateToMillis(formattedEndDate)

        errorstartdate = formatDate(errorStartDateMillis)
        errorenddate = formatDate(errorEndDateMillis)

        trainingPlanContainer.removeAllViews()
        var count = 0

        data.pre_season?.let { preSeason ->
            if (!preSeason.start_date.isNullOrEmpty() && !preSeason.end_date.isNullOrEmpty()) {
                count++
                addTrainingPlanView(
                    preSeason.name ?: "",
                    preSeason.start_date ?: "",
                    preSeason.end_date ?: "",
                    preSeason.mesocycle ?: "0 Days",
                    "Pre-Season"
                )
            }
        }

        data.pre_competitive?.let { preCompetitive ->
            if (!preCompetitive.name.isNullOrEmpty() && !preCompetitive.start_date.isNullOrEmpty() && !preCompetitive.end_date.isNullOrEmpty()) {
                count++
                addTrainingPlanView(
                    preCompetitive.name ?: "",
                    preCompetitive.start_date ?: "",
                    preCompetitive.end_date ?: "",
                    preCompetitive.mesocycle ?: "0 Days",
                    "Pre-Competitive"
                )
            }
        }

        data.competitive?.let { competitive ->
            if (!competitive.start_date.isNullOrEmpty() && !competitive.end_date.isNullOrEmpty()) {
                count++
                addTrainingPlanView(
                    competitive.name ?: "",
                    competitive.start_date ?: "",
                    competitive.end_date ?: "",
                    competitive.mesocycle ?: "0 Days",
                    "Competitive"
                )
            }
        }

        data.transition?.let { transition ->
            if (!transition.start_date.isNullOrEmpty() && !transition.end_date.isNullOrEmpty()) {
                count++
                addTrainingPlanView(
                    transition.name ?: "",
                    transition.start_date ?: "",
                    transition.end_date ?: "",
                    transition.mesocycle ?: "0 Days",
                    "Transition"
                )
            }
        }

        Log.d("Training Plan Count", "Total Plans: $count")
    }


    private fun updateTrainingPlanIndices() {
        val defaultNames = listOf("Pre Season", "Pre Competititve", "Compatitive", "Transition")

        for (i in trainingPlanLayouts.indices) {
            val layout = trainingPlanLayouts[i]
            val indexTextView: AppCompatEditText = layout.findViewById(R.id.ent_pre_sea_name)

            val currentName = indexTextView.text?.toString()

            if (currentName.isNullOrEmpty()) {
                val defaultName = if (i < defaultNames.size) defaultNames[i] else "Default Text"
                indexTextView.setText(defaultName)
            }

            indexTextView.hint = getTrainingPlanDetails(i + 1)
        }

        Log.d(
            "UpdateTrainingPlan",
            "Updated indices for ${trainingPlanLayouts.size} training plans."
        )
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

    private fun removeTrainingPlan(trainingPlanLayout: View) {
        trainingPlanContainer.removeView(trainingPlanLayout)
        trainingPlanLayouts.remove(trainingPlanLayout)
        trainingPlanCount--
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

            if (nameEditText.text.isNullOrEmpty()) {
                return false
            }

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