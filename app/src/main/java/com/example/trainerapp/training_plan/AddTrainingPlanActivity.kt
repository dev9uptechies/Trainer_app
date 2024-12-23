package com.example.trainerapp.training_plan

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ParseException
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.cardview.widget.CardView
import androidx.core.graphics.toColor
import com.example.model.trainer_plan.Competitive
import com.example.model.trainer_plan.PreCompetitive
import com.example.model.trainer_plan.PreSeason
import com.example.model.trainer_plan.TrainingPlanSubClass
import com.example.model.trainer_plan.Transition
import com.example.model.training_plan.TrainingPlanData
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.PreferencesManager
import com.example.trainerapp.R
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.ActivityAddTrainingPlanBinding
import com.google.gson.Gson
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

class AddTrainingPlanActivity : AppCompatActivity() {
    lateinit var addTrainingPlanBinding: ActivityAddTrainingPlanBinding
    private lateinit var apiInterface: APIInterface
    private lateinit var preferenceManager: PreferencesManager
    private lateinit var apiClient: APIClient
    private val selectedDateRanges = mutableListOf<Pair<Long, Long>>()

    private lateinit var trainingPlanContainer: LinearLayout
    private var trainingPlanCount = 0
    private val gson = Gson()
    private var id: Int? = null
    private val trainingPlanLayouts = mutableListOf<View>()
    private val missingIndices = mutableListOf<Int>()
    private var competitive: Competitive = Competitive("", "", "", "")
    private var preSeason: PreSeason = PreSeason("", "", "", "")
    private var transition: Transition = Transition("", "", "", "")
    private var preCompetitive: PreCompetitive = PreCompetitive("", "", "", "")

    private var startDateMillis: Long = 0
    private var endDateMillis: Long = 0
    private var activeLayoutIndex: Int? = null

    var hyy:Boolean = false

    private var startdatesent:String? = null
    private var enddatesent:String? = null

    private var startdatesentlist:String? = null
    private var enddatesentlist:String? = null

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
                        Utils.setUnAuthDialog(this@AddTrainingPlanActivity)
                    } else {
                        Toast.makeText(this@AddTrainingPlanActivity, "" + response.message(), Toast.LENGTH_SHORT).show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Toast.makeText(
                        this@AddTrainingPlanActivity,
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addTrainingPlanBinding = ActivityAddTrainingPlanBinding.inflate(layoutInflater)
        setContentView(addTrainingPlanBinding.root)

        initViews()
        checkButtonTap()
        addTrainingPlan()
        setupTextWatcher()
    }

    private fun setupTextWatcher() {
        addTrainingPlanBinding.edtProgramName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
        addTrainingPlanBinding.edtStartDate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateDaysTextView()
                updateUI(addTrainingPlanBinding.cardSave)
            }
        })

        addTrainingPlanBinding.edtEndDate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateDaysTextView()
                updateUI(addTrainingPlanBinding.cardSave)
            }
        })
    }

    private fun areAllFieldsFilled(): Boolean {
        return !(addTrainingPlanBinding.edtProgramName.text.isNullOrEmpty() || addTrainingPlanBinding.edtStartDate.text.isNullOrEmpty() || addTrainingPlanBinding.edtEndDate.text.isNullOrEmpty())
    }

    private fun updateUI(addButton: CardView) {
        if (areAllFieldsFilled()) {
            addButton.isEnabled = true
            addButton.setCardBackgroundColor(resources.getColor(R.color.splash_text_color))
        } else {
            addButton.setCardBackgroundColor(resources.getColor(R.color.grey))
        }
    }

    private fun checkButtonTap() {
        addTrainingPlanBinding.add.setOnClickListener {
            if (trainingPlanCount < 4) {
                addTrainingPlan()
            }
        }

        addTrainingPlanBinding.back.setOnClickListener {
            finish()
        }

        addTrainingPlanBinding.cardDate.setOnClickListener {
            showDateRangePickerDialogfor(addTrainingPlanBinding.edtStartDate.context) { start, end ->
                startDateMillis = start
                endDateMillis = end
                val formattedStartDate = formatDate(start)
                val formattedStartDate2 = formatDate2(start)
                val formattedEndDate = formatDate(end)
                val formattedEndDate2 = formatDate2(end)
                addTrainingPlanBinding.edtStartDate.setText(formattedStartDate2)
                addTrainingPlanBinding.edtEndDate.setText(formattedEndDate2)
                startdatesent = formattedStartDate
                enddatesent = formattedEndDate
            }
        }
        addTrainingPlanBinding.edtStartDate.setOnClickListener {
            showDateRangePickerDialogfor(addTrainingPlanBinding.edtStartDate.context) { start, end ->
                startDateMillis = start
                endDateMillis = end
                val formattedStartDate = formatDate(start)
                val formattedStartDate2 = formatDate2(start)
                val formattedEndDate = formatDate(end)
                val formattedEndDate2 = formatDate2(end)
                addTrainingPlanBinding.edtStartDate.setText(formattedStartDate2)
                addTrainingPlanBinding.edtEndDate.setText(formattedEndDate2)
                startdatesent = formattedStartDate
                enddatesent = formattedEndDate

            }
        }
        addTrainingPlanBinding.cardEndDate.setOnClickListener {
            showDateRangePickerDialogfor(addTrainingPlanBinding.edtStartDate.context) { start, end ->
                startDateMillis = start
                endDateMillis = end
                val formattedStartDate = formatDate(start)
                val formattedStartDate2 = formatDate2(start)
                val formattedEndDate = formatDate(end)
                val formattedEndDate2 = formatDate2(end)
                addTrainingPlanBinding.edtStartDate.setText(formattedStartDate2)
                addTrainingPlanBinding.edtEndDate.setText(formattedEndDate2)
                startdatesent = formattedStartDate
                enddatesent = formattedEndDate
            }
        }
        addTrainingPlanBinding.edtEndDate.setOnClickListener {
            showDateRangePickerDialogfor(addTrainingPlanBinding.edtStartDate.context) { start, end ->
                startDateMillis = start
                endDateMillis = end
                val formattedStartDate = formatDate(start)
                val formattedStartDate2 = formatDate2(start)
                val formattedEndDate = formatDate(end)
                val formattedEndDate2 = formatDate2(end)
                addTrainingPlanBinding.edtStartDate.setText(formattedStartDate2)
                addTrainingPlanBinding.edtEndDate.setText(formattedEndDate2)
                startdatesent = formattedStartDate
                enddatesent = formattedEndDate

            }
        }


        addTrainingPlanBinding.cardSave.setOnClickListener { saveTrainingPlans() }
    }

    private fun initViews() {
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        preferenceManager = PreferencesManager(this)
        trainingPlanContainer = addTrainingPlanBinding.linerAddTraining
    }

    private fun selectStartDate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            Utils.selectDate2(
                this,
                addTrainingPlanBinding.edtStartDate,
                System.currentTimeMillis(),
                Long.MAX_VALUE
            ) { dateMillis ->
                startDateMillis = dateMillis
                val formattedDate = formatDate(dateMillis)
                val formattedDatset = formatDate2(dateMillis)

                startdatesent = formatDate(dateMillis)

                addTrainingPlanBinding.edtStartDate.setText(formattedDatset)


                Log.d("startdateendt", "selectStartDate: " + startdatesent)

                endDateMillis = 0
                addTrainingPlanBinding.edtEndDate.setText("")
            }
        }
    }


    private fun selectEndDate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // Set max date for the end date (optional, can be left open)
            val maxDate = Long.MAX_VALUE  // No limit for the max date
            Utils.selectDate2(
                this,
                addTrainingPlanBinding.edtEndDate,  // Pass the EditText for end date
                startDateMillis,  // The start date acts as the minimum selectable date
                maxDate  // No limit for the max date
            ) { dateMillis ->
                endDateMillis = dateMillis  // Update the selected end date
                val formattedDate = formatDate(dateMillis)
                val formatttedDateset = formatDate2(dateMillis)

                enddatesent = formattedDate

                addTrainingPlanBinding.edtEndDate.setText(formatttedDateset)
            }
        }
    }


    private fun addTrainingPlan() {
        trainingPlanCount++

        val indexToAdd = if (missingIndices.isNotEmpty()) {
            val reusedIndex = missingIndices.removeAt(0) // Reuse the first missing index
            Log.d("AddTrainingPlan", "Reusing missing index: $reusedIndex")
            reusedIndex
        } else {
            trainingPlanLayouts.size // Append at the end if no missing indices
        }

        val newTrainingPlanLayout = LayoutInflater.from(this)
            .inflate(R.layout.add_training_plan_list, trainingPlanContainer, false)

        val nameEditText: AppCompatEditText =
            newTrainingPlanLayout.findViewById(R.id.ent_pre_sea_name)
        nameEditText.hint = getTrainingPlanDetails(indexToAdd) // Use the correct index

        val startDateEditText: AppCompatEditText =
            newTrainingPlanLayout.findViewById(R.id.ent_start_date_liner)
        val endDateEditText: AppCompatEditText =
            newTrainingPlanLayout.findViewById(R.id.ent_ent_date_liner)

        val startdatecard: CardView = newTrainingPlanLayout.findViewById(R.id.card_start_date_list)
        val enddatecard: CardView = newTrainingPlanLayout.findViewById(R.id.card_end_pick_list)
        val mesocycles: TextView = newTrainingPlanLayout.findViewById(R.id.linear_days_list)

        val updateDaysTextView = {
            val startDate = startDateEditText.text.toString()
            val endDate = endDateEditText.text.toString()

            if (startDate.isNotEmpty() && endDate.isNotEmpty()) {
                val days = calculateDaysBetweenDates(startdatesentlist.toString(), enddatesentlist.toString())
//                addTrainingPlanBinding.days.text = "$days Days"
                mesocycles.text = "$days days"
            } else {
//                addTrainingPlanBinding.days.text = "0 Days"
                mesocycles.text = "Select dates"
            }

//            if (startDate.isNotEmpty() && endDate.isNotEmpty()) {
//                try {
//                    val days = calculateDaysBetweenDates(startDate, endDate)
//                    mesocycles.requestFocus()
//                    mesocycles.text = "$days days"
//                } catch (e: Exception) {
//                    mesocycles.text = "Invalid dates"
//                    Log.e("DateError", "Error calculating days: ${e.message}")
//                }
//            } else {
//                mesocycles.text = "Select dates" // Default message
//            }
        }



        startDateEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateDaysTextView()
                updateUI(addTrainingPlanBinding.cardSave)
            }
        })

        endDateEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateDaysTextView()
                updateUI(addTrainingPlanBinding.cardSave)
            }
        })

        startDateEditText.setOnClickListener {
            val layoutIndex = trainingPlanContainer.indexOfChild(newTrainingPlanLayout)
            activeLayoutIndex = layoutIndex

            if (addTrainingPlanBinding.edtStartDate.text.isNullOrEmpty()) {
                Toast.makeText(this, "Please select a start date first", Toast.LENGTH_SHORT).show()
            } else {
                val minDateMillis = formatDateToMillis(addTrainingPlanBinding.edtStartDate.text.toString())
                val maxDateMillis = formatDateToMillis(addTrainingPlanBinding.edtEndDate.text.toString())

                showDateRangePickerDialog(startDateEditText.context, minDateMillis, maxDateMillis, layoutIndex) { start, end ->
                    startDateMillis = start
                    endDateMillis = end
                    val formattedStartDate = formatDate(start)
                    val formattedStartDate2 = formatDate2(start)
                    val formattedEndDate = formatDate(end)
                    val formattedEndDate2 = formatDate2(end)

                    startDateEditText.setText(formattedStartDate2)
                    endDateEditText.setText(formattedEndDate2)
                    startdatesentlist = formattedStartDate
                    enddatesentlist = formattedEndDate
                    updateDaysTextView()
                }
            }
        }

        startdatecard.setOnClickListener {
            val layoutIndex = trainingPlanContainer.indexOfChild(newTrainingPlanLayout)
            activeLayoutIndex = layoutIndex

            if (addTrainingPlanBinding.edtStartDate.text.isNullOrEmpty()) {
                Toast.makeText(this, "Please select a start date first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
//                selectTrainingPlanStartDate(startDateEditText)
//                selectTrainingPlanStartAndEndDates(startDateEditText,endDateEditText)
                val minDateMillis = formatDateToMillis(addTrainingPlanBinding.edtStartDate.text.toString())
                val maxDateMillis = formatDateToMillis(addTrainingPlanBinding.edtEndDate.text.toString())

                showDateRangePickerDialog(startDateEditText.context, minDateMillis, maxDateMillis, layoutIndex) { start, end ->

                    startDateMillis = start
                    endDateMillis = end
                    val formattedStartDate = formatDate(start)
                    val formattedStartDate2 = formatDate2(start)
                    val formattedEndDate = formatDate(end)
                    val formattedEndDate2 = formatDate2(end)

                    startDateEditText.setText(formattedStartDate2)
                    endDateEditText.setText(formattedEndDate2)
                    startdatesentlist = formattedStartDate
                    enddatesentlist = formattedEndDate
                    selectedDateRanges.add(startDateMillis to endDateMillis)
                    updateDaysTextView()

                }

            }

        }

        endDateEditText.setOnClickListener {
            val layoutIndex = trainingPlanContainer.indexOfChild(newTrainingPlanLayout)
            activeLayoutIndex = layoutIndex
            if (addTrainingPlanBinding.edtEndDate.text.isNullOrEmpty()) {
                Toast.makeText(this, "Please select a start date first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                val minDateMillis = formatDateToMillis(addTrainingPlanBinding.edtStartDate.text.toString())
                val maxDateMillis = formatDateToMillis(addTrainingPlanBinding.edtEndDate.text.toString())

                showDateRangePickerDialog(startDateEditText.context, minDateMillis, maxDateMillis,layoutIndex) { start, end ->
                    startDateMillis = start
                    endDateMillis = end
                    val formattedStartDate = formatDate(start)
                    val formattedStartDate2 = formatDate2(start)
                    val formattedEndDate = formatDate(end)
                    val formattedEndDate2 = formatDate2(end)

                    startDateEditText.setText(formattedStartDate2)
                    endDateEditText.setText(formattedEndDate2)
                    startdatesentlist = formattedStartDate
                    enddatesentlist = formattedEndDate
                    selectedDateRanges.add(startDateMillis to endDateMillis)
                    updateDaysTextView()
                }
            }
        }

        enddatecard.setOnClickListener {
            val layoutIndex = trainingPlanContainer.indexOfChild(newTrainingPlanLayout)
            activeLayoutIndex = layoutIndex
            if (addTrainingPlanBinding.edtEndDate.text.isNullOrEmpty()) {
                Toast.makeText(this, "Please select a start date first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                val minDateMillis = formatDateToMillis(addTrainingPlanBinding.edtStartDate.text.toString())
                val maxDateMillis = formatDateToMillis(addTrainingPlanBinding.edtEndDate.text.toString())

                showDateRangePickerDialog(startDateEditText.context, minDateMillis, maxDateMillis,layoutIndex) { start, end ->
                    startDateMillis = start
                    endDateMillis = end
                    val formattedStartDate = formatDate(start)
                    val formattedStartDate2 = formatDate2(start)
                    val formattedEndDate = formatDate(end)
                    val formattedEndDate2 = formatDate2(end)

                    startDateEditText.setText(formattedStartDate2)
                    endDateEditText.setText(formattedEndDate2)
                    startdatesentlist = formattedStartDate
                    enddatesentlist = formattedEndDate
                    selectedDateRanges.add(startDateMillis to endDateMillis)
                    updateDaysTextView()

                }
            }
        }

        startDateEditText.setOnFocusChangeListener { _, _ -> updateDaysTextView() }
        endDateEditText.setOnFocusChangeListener { _, _ -> updateDaysTextView() }
        startdatecard.setOnFocusChangeListener { _, _ -> updateDaysTextView() }
        enddatecard.setOnFocusChangeListener { _, _ -> updateDaysTextView() }
        mesocycles.setOnFocusChangeListener { _, _ -> updateDaysTextView() }

        trainingPlanLayouts.add(indexToAdd, newTrainingPlanLayout)
        trainingPlanContainer.addView(newTrainingPlanLayout, indexToAdd)
        Log.d("AddTrainingPlan", "Added training plan at index: $indexToAdd")
        val delete: ImageView = newTrainingPlanLayout.findViewById(R.id.img_delete)
        delete.setOnClickListener {
            removeTrainingPlan(newTrainingPlanLayout)
        }
        updateTrainingPlanIndices()
    }

    private fun removeTrainingPlan(trainingPlanLayout: View) {
        val index = trainingPlanLayouts.indexOf(trainingPlanLayout)
        trainingPlanContainer.removeView(trainingPlanLayout)
        trainingPlanLayouts.remove(trainingPlanLayout)
        trainingPlanCount--
        Log.d("RemoveTrainingPlan", "Removed training plan at index: $index")

        if (index != -1) {
            missingIndices.add(index)
            Log.d("RemoveTrainingPlan", "Index $index added to missing indices.")
        }

    }

    private fun updateTrainingPlanIndices() {
        hyy = true
        // Define a list of default names
        val defaultNames = listOf("Pre Season", "Pre Competititve", "Compatitive","Transition")

        for (i in trainingPlanLayouts.indices) {
            val layout = trainingPlanLayouts[i]
            val indexTextView: AppCompatEditText = layout.findViewById(R.id.ent_pre_sea_name)

            // Get the current name from the EditText
            val currentName = indexTextView.text?.toString()

            // Check if the name is null or empty
            if (currentName.isNullOrEmpty()) {
                // Set the corresponding default name, or a fallback if out of range
                val defaultName = if (i < defaultNames.size) defaultNames[i] else "Default Text"
                indexTextView.setText(defaultName)
            }

            // Update the hint with training plan details
            indexTextView.hint = getTrainingPlanDetails(i + 1)
        }

        Log.d(
            "UpdateTrainingPlan",
            "Updated indices for ${trainingPlanLayouts.size} training plans."
        )
    }

    @SuppressLint("NewApi")
    fun showDateRangePickerDialogfor(
        context: Context,
        callback: (start: Long, end: Long) -> Unit
    ) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.date_range_picker_dialog)

        val calendarView = dialog.findViewById<MaterialCalendarView>(R.id.calendarView)
        val textView = dialog.findViewById<TextView>(R.id.textView)
        val confirmButton = dialog.findViewById<Button>(R.id.confirmButton)
        val cancelButton = dialog.findViewById<Button>(R.id.cancelButton)

        calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_RANGE)

        cancelButton.setOnClickListener { dialog.dismiss() }

        val currentDate = Calendar.getInstance()
        currentDate.set(Calendar.HOUR_OF_DAY, 0)
        currentDate.set(Calendar.MINUTE, 0)
        currentDate.set(Calendar.SECOND, 0)
        currentDate.set(Calendar.MILLISECOND, 0)
        val today = currentDate.time

        calendarView.setCurrentDate(CalendarDay.from(currentDate))

        // Set the calendar display mode
        calendarView.state().edit()
            .setCalendarDisplayMode(CalendarMode.MONTHS)
            .commit()

        // Disable dates before today
        calendarView.addDecorator(object : DayViewDecorator {
            override fun shouldDecorate(day: CalendarDay?): Boolean {
                return day != null && day.date.before(today) // Disable dates strictly before today
            }

            override fun decorate(view: DayViewFacade?) {
                view?.addSpan(ForegroundColorSpan(Color.GRAY)) // Make past dates appear gray
                view?.setDaysDisabled(true) // Disable past dates
            }
        })

        confirmButton.setOnClickListener {
            val selectedDates = calendarView.selectedDates

            if (selectedDates.size >= 2) {
                val startDate = selectedDates.first().calendar
                val endDate = selectedDates.last().calendar

                // Normalize start and end dates
                startDate.set(Calendar.HOUR_OF_DAY, 0)
                startDate.set(Calendar.MINUTE, 0)
                startDate.set(Calendar.SECOND, 0)
                startDate.set(Calendar.MILLISECOND, 0)

                endDate.set(Calendar.HOUR_OF_DAY, 23)
                endDate.set(Calendar.MINUTE, 59)
                endDate.set(Calendar.SECOND, 59)
                endDate.set(Calendar.MILLISECOND, 999)

                callback(startDate.timeInMillis, endDate.timeInMillis)

                dialog.dismiss()
            } else {
                textView.text = "Please select both start and end dates"
            }
        }

        dialog.show()
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

    fun showDateRangePickerDialog(
        context: Context,
        minDateMillis: Long,
        maxDateMillis: Long,
        layoutIndex: Int?,
        callback: (start: Long, end: Long) -> Unit
    ) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.date_range_picker_dialog)

        val calendarView = dialog.findViewById<MaterialCalendarView>(R.id.calendarView)
        val textView = dialog.findViewById<TextView>(R.id.textView)
        val confirmButton = dialog.findViewById<Button>(R.id.confirmButton)
        val cancelButton = dialog.findViewById<Button>(R.id.cancelButton)

        calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_RANGE)

        cancelButton.setOnClickListener { dialog.dismiss() }

        val minCalendar = Calendar.getInstance().apply {
            timeInMillis = minDateMillis
        }

        val maxCalendar = Calendar.getInstance().apply {
            timeInMillis = maxDateMillis
        }

        calendarView.state().edit()
            .setCalendarDisplayMode(CalendarMode.MONTHS)
            .commit()

        calendarView.addDecorator(object : DayViewDecorator {
            override fun shouldDecorate(day: CalendarDay?): Boolean {
                return day?.let {
                    val dateInMillis = it.calendar.timeInMillis
                    dateInMillis < minDateMillis || dateInMillis > maxDateMillis
                } ?: false
            }

            override fun decorate(view: DayViewFacade?) {
                view?.addSpan(ForegroundColorSpan(Color.GRAY))
                view?.setDaysDisabled(true)
            }
        })

        calendarView.addDecorator(object : DayViewDecorator {
            override fun shouldDecorate(day: CalendarDay?): Boolean {
                return day?.let {
                    val dateInMillis = it.calendar.timeInMillis
                    selectedDateRanges.forEachIndexed { index, range ->
                        if (layoutIndex != null && layoutIndex != index) {
                            if (dateInMillis in range.first..range.second) {
                                return true
                            }
                        }
                    }
                    return false
                } ?: false
            }

            override fun decorate(view: DayViewFacade?) {
                view?.addSpan(ForegroundColorSpan(Color.GRAY))
                view?.setDaysDisabled(true)
            }
        })

        confirmButton.setOnClickListener {
            val selectedDates = calendarView.selectedDates

            if (selectedDates.size >= 2) {
                val startDate = selectedDates.first().calendar
                val endDate = selectedDates.last().calendar

                // Set time for start and end date
                startDate.set(Calendar.HOUR_OF_DAY, 0)
                startDate.set(Calendar.MINUTE, 0)
                startDate.set(Calendar.SECOND, 0)
                startDate.set(Calendar.MILLISECOND, 0)

                endDate.set(Calendar.HOUR_OF_DAY, 23)
                endDate.set(Calendar.MINUTE, 59)
                endDate.set(Calendar.SECOND, 59)
                endDate.set(Calendar.MILLISECOND, 999)

                // If this is an existing plan, update its date range
                if (layoutIndex != null && layoutIndex < selectedDateRanges.size) {
                    selectedDateRanges[layoutIndex] = startDate.timeInMillis to endDate.timeInMillis
                } else {
                    // Add new plan's date range
                    selectedDateRanges.add(startDate.timeInMillis to endDate.timeInMillis)
                }

                callback(startDate.timeInMillis, endDate.timeInMillis)
                dialog.dismiss()
            } else {
                textView.text = "Please select both start and end dates"
            }
        }

        dialog.show()
    }




    private fun disablePreviouslySelectedRanges(
        calendarView: MaterialCalendarView,
        selectedDateRanges: MutableList<Pair<Long, Long>>
    ) {
        calendarView.addDecorator(object : DayViewDecorator {
            override fun shouldDecorate(day: CalendarDay?): Boolean {
                day?.let {
                    selectedDateRanges.forEach { range ->
                        if (it.dateInRange(range.first, range.second)) {

                            return true
                        }
                    }
                }
                return false
            }

            override fun decorate(view: DayViewFacade?) {
                view?.apply {


                    view.addSpan(setDaysDisabled(true))
                    view.addSpan(areDaysDisabled())
                    view.addSpan(ForegroundColorSpan(Color.GRAY))
                }
            }
        })
    }

    fun CalendarDay.dateInRange(startMillis: Long, endMillis: Long): Boolean {
        val selectedDateMillis = this.calendar.timeInMillis
        return selectedDateMillis in startMillis..endMillis
    }

    private fun selectTrainingPlanStartDate(editText: AppCompatEditText) {
        val mainStartDate = startdatesent.toString()
        val maxStartDate = enddatesent.toString()

        val minDateMillis =
            if (mainStartDate.isNotEmpty()) formatDateToMillis(mainStartDate) else System.currentTimeMillis()
        val maxDateMillis =
            if (maxStartDate.isNotEmpty()) formatDateToMillis(maxStartDate) else Long.MAX_VALUE

        Utils.selectDate4(this, editText, minDateMillis, maxDateMillis, selectedDateRanges) { dateMillis ->
            startDateMillis = dateMillis
            val formattedDate = formatDate(dateMillis)
            val formattedDateset = formatDate2(dateMillis)
            startdatesentlist = formattedDate
            editText.setText(formattedDateset)
        }
    }

    private fun selectTrainingPlanEndDate(editText: AppCompatEditText) {
        val mainEndDate = enddatesent.toString()

        Utils.selectDate4(
            this,
            editText,
            if (startDateMillis > 0) startDateMillis else System.currentTimeMillis(),
            if (mainEndDate.isNotEmpty()) formatDateToMillis(mainEndDate) else Long.MAX_VALUE,
            selectedDateRanges
        ) { dateMillis ->
            endDateMillis = dateMillis
            val formattedDate = formatDate(dateMillis)
            val formattedDateset = formatDate2(dateMillis)
            enddatesentlist = formattedDate
            editText.setText(formattedDateset)
            selectedDateRanges.add(startDateMillis to endDateMillis)


        }
    }

    private fun formatDateToMillis(dateString: String): Long {
        val format = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
        return try {
            format.parse(dateString)?.time ?: Long.MAX_VALUE
        } catch (e: Exception) {
            Long.MAX_VALUE // Return max value in case of error
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

    private fun saveTrainingPlans() {
        if (!isValidate()) return
        try {
            for (i in 0 until trainingPlanContainer.childCount) {
                val layout = trainingPlanContainer.getChildAt(i)
                val nameEditText: AppCompatEditText = layout.findViewById(R.id.ent_pre_sea_name)
                val startDateEditText: AppCompatEditText =
                    layout.findViewById(R.id.ent_start_date_liner)
                val endDateEditText: AppCompatEditText =
                    layout.findViewById(R.id.ent_ent_date_liner)
                val mesocycleTextView: TextView = layout.findViewById(R.id.linear_days_list)

                val name = nameEditText.text.toString().trim()

                val mesocycleValue = mesocycleTextView.text.toString().split(" ")[0]

                val mesocycle = mesocycleValue.toIntOrNull()

                if (name.isEmpty() || startdatesentlist.equals(null) || enddatesentlist!!.isEmpty()) {
                    Toast.makeText(
                        this,
                        "Please fill in all fields",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }

                when (i) {
                    0 -> preSeason = PreSeason(
                        name = name,
                        start_date = startdatesentlist.toString(),
                        end_date = enddatesentlist.toString(),
                        mesocycle = mesocycle.toString()
                    )

                    1 -> preCompetitive = PreCompetitive(
                        name = name,
                        start_date = startdatesentlist.toString(),
                        end_date = enddatesentlist.toString(),
                        mesocycle = mesocycle.toString()
                    )

                    2 -> competitive = Competitive(
                        name = name,
                        start_date = startdatesentlist.toString(),
                        end_date = enddatesentlist.toString(),
                        mesocycle = mesocycle.toString()
                    )

                    3 -> transition = Transition(
                        name = name,
                        start_date = startdatesentlist.toString(),
                        end_date = enddatesentlist.toString(),
                        mesocycle = mesocycle.toString()
                    )
                }
            }

            val trainingPlanRequest = TrainingPlanSubClass(
                id = id?.toInt() ?: 0,
                name = addTrainingPlanBinding.edtProgramName.text.toString(),
                competition_date = enddatesent.toString(),
                start_date = startdatesent.toString(),
                pre_season = preSeason,
                pre_competitive = preCompetitive,
                competitive = competitive,
                transition = transition,
                mesocycle = calculateDaysBetweenDates(
                    startdatesent.toString(),
                    enddatesent.toString()
                ).toString()
            )
            Log.d("TrainingPlan", "Request: ${gson.toJson(trainingPlanRequest)}")
            apiInterface.CreatePlanning(trainingPlanRequest)
                ?.enqueue(object : Callback<TrainingPlanData> {
                    override fun onResponse(
                        call: Call<TrainingPlanData>,
                        response: Response<TrainingPlanData>) {
                        Log.d("APIResponse", "Response: ${response.code()} - ${response.errorBody()}")
                        Log.d("TAG", response.code().toString() + "")
                        val code = response.code()
                        if (code == 200) {
                            if (response.isSuccessful) {
                                Toast.makeText(
                                    this@AddTrainingPlanActivity,
                                    response.body()?.message ?: "Success",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            } else {
                                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                                Log.e("API Error", "Response: ${response.code()} - $errorBody")
                                Toast.makeText(
                                    this@AddTrainingPlanActivity,
                                    "Error: $errorBody",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else if (code == 403) {
                            Utils.setUnAuthDialog(this@AddTrainingPlanActivity)
                        } else {
                            Toast.makeText(
                                this@AddTrainingPlanActivity,
                                "" + response.message(),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            call.cancel()
                        }
                    }

                    override fun onFailure(call: Call<TrainingPlanData>, t: Throwable) {
                        Log.e("API Error", "Error: ${t.message}")
                        Toast.makeText(
                            this@AddTrainingPlanActivity,
                            "Network error: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })

        } catch (e: Exception) {
            Log.e("SaveTrainingPlanError", e.message ?: "Error saving training plans")
            Toast.makeText(this, "An unexpected error occurred", Toast.LENGTH_SHORT).show()
        }
    }


    private fun calculateDaysBetweenDates(start: String, end: String): Int {
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val startDate = format.parse(start)
            val endDate = format.parse(end)
            val difference = endDate.time - startDate.time
            (difference / (1000 * 60 * 60 * 24)).toInt()
        } catch (e: Exception) {
            Log.e("DateCalculation", "Error calculating days: ${e.message}")
            0
        }
    }

    private fun updateDaysTextView() {
        val startDate = addTrainingPlanBinding.edtStartDate.text.toString()
        val endDate = addTrainingPlanBinding.edtEndDate.text.toString()

        if (startDate.isNotEmpty() && endDate.isNotEmpty()) {
            val days = calculateDaysBetweenDates(startdatesent.toString(), enddatesent.toString())
            addTrainingPlanBinding.days.text = "$days Days"
        } else {
            addTrainingPlanBinding.days.text = "0 Days"
        }
    }

    private fun isValidate(): Boolean {
        val programName = addTrainingPlanBinding.edtProgramName.text.toString()
        val StartDate = addTrainingPlanBinding.edtStartDate.text.toString().trim()
        val EndDate = addTrainingPlanBinding.edtEndDate.text.toString()

        if (programName == "") {
            addTrainingPlanBinding.errorProgram.visibility = View.VISIBLE
            return false

        } else {
            addTrainingPlanBinding.errorProgram.visibility = View.GONE
        }

        if (StartDate == "") {
            addTrainingPlanBinding.errorStartDate.visibility = View.VISIBLE
            return false
        } else {
            addTrainingPlanBinding.errorStartDate.visibility = View.GONE
        }

        if (EndDate == "") {
            addTrainingPlanBinding.errorEndDate.visibility = View.VISIBLE
            return false
        } else {
            addTrainingPlanBinding.errorEndDate.visibility = View.GONE

        }

        for (i in 0 until trainingPlanContainer.childCount) {
            val layout = trainingPlanContainer.getChildAt(i)
            val nameEditText: AppCompatEditText = layout.findViewById(R.id.ent_pre_sea_name)
            val startDateEditText: AppCompatEditText =
                layout.findViewById(R.id.ent_start_date_liner)
            val endDateEditText: AppCompatEditText = layout.findViewById(R.id.ent_ent_date_liner)

            val statdateList = startDateEditText.text.toString()
            val enddateList = endDateEditText.text.toString()

            if (nameEditText == null || nameEditText.equals("")){

            }

            if (statdateList == "") {
                layout.findViewById<TextView>(R.id.error_start_date_list).visibility = View.VISIBLE
                return false
            } else {
                layout.findViewById<TextView>(R.id.error_start_date_list).visibility = View.GONE
            }

            if (enddateList == "") {
                layout.findViewById<TextView>(R.id.error_end_date_list).visibility = View.VISIBLE
                return false
            } else {
                layout.findViewById<TextView>(R.id.error_end_date_list).visibility = View.GONE
            }
        }
        return true
    }
}
