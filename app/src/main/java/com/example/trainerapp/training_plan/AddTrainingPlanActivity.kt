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
import com.google.android.material.snackbar.Snackbar
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
    var activePlanIndex = -1

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
                context = addTrainingPlanBinding.edtStartDate.context,
                minDateMillis = today,
                maxDateMillis = oneYearLater,
                layoutIndex = currentLayoutIndex
            ) { start, end ->
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

            val layoutIndex: Int? = null  // Pass null if not editing an existing plan

            showDateRangePickerDialogfor(
                context = addTrainingPlanBinding.edtStartDate.context,
                minDateMillis = today,            // Minimum date (e.g., today)
                maxDateMillis = oneYearLater,     // Maximum date (1 year later)
                layoutIndex = layoutIndex         // Pass null or a valid index
            ) { start, end ->
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

            val layoutIndex: Int? = null  // Pass null if adding a new plan

            showDateRangePickerDialogfor(
                context = addTrainingPlanBinding.edtStartDate.context,
                minDateMillis = today,            // Minimum date
                maxDateMillis = oneYearLater,     // Maximum date (1 year later)
                layoutIndex = layoutIndex         // Null or specific plan index
            ) { start, end ->
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

            // If start date has been selected, limit the end date selection to after the start date
            val minEndDate = if (startDateMillis != 0L) startDateMillis else today
            val layoutIndex: Int? = null  // Use null for new plans or the plan index for editing

            showDateRangePickerDialogfor(
                context = addTrainingPlanBinding.edtStartDate.context,
                minDateMillis = minEndDate,
                maxDateMillis = oneYearLater,
                layoutIndex = layoutIndex
            ) { start, end ->
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

                showDateRangePickerDialogfor(startDateEditText.context, minDateMillis, maxDateMillis, layoutIndex) { start, end ->
                    startDateMillis = start
                    endDateMillis = end
                    val formattedStartDate = formatDate(start)
                    val formattedStartDate2 = formatDate2(start)
                    val formattedEndDate = formatDate(end)
                    val formattedEndDate2 = formatDate2(end)

                    startDateEditText.setText(formattedStartDate2)
                    endDateEditText.setText(formattedEndDate2)

                    // Apply conflict check for Plan 2 and onwards
                    if (layoutIndex != 0 && isConflictWithOtherPlans(formattedStartDate2, formattedEndDate2)) {
                        Toast.makeText(this, "Date conflict detected. Please select different dates.", Toast.LENGTH_SHORT).show()
                        startDateEditText.setText("")
                        endDateEditText.setText("")
                    }
                }
            }
        }



        startdatecard.setOnClickListener {
            val layoutIndex = trainingPlanContainer.indexOfChild(newTrainingPlanLayout)
            activeLayoutIndex = layoutIndex

            if (addTrainingPlanBinding.edtStartDate.text.isNullOrEmpty()) {
                Toast.makeText(this, "Please select a start date first", Toast.LENGTH_SHORT).show()
            } else {
                val minDateMillis = formatDateToMillis(addTrainingPlanBinding.edtStartDate.text.toString())
                val maxDateMillis = formatDateToMillis(addTrainingPlanBinding.edtEndDate.text.toString())

                showDateRangePickerDialogfor(startDateEditText.context, minDateMillis, maxDateMillis, layoutIndex) { start, end ->
                    startDateMillis = start
                    endDateMillis = end
                    val formattedStartDate = formatDate(start)
                    val formattedStartDate2 = formatDate2(start)
                    val formattedEndDate = formatDate(end)
                    val formattedEndDate2 = formatDate2(end)

                    startDateEditText.setText(formattedStartDate2)
                    endDateEditText.setText(formattedEndDate2)

                    // Revalidate Plan 1 after date change
                    if (layoutIndex == 0 && isConflictWithOtherPlans(formattedStartDate2, formattedEndDate2)) {
                        Toast.makeText(this, "Plan 1 conflicts with another plan. Please adjust.", Toast.LENGTH_SHORT).show()
                        startDateEditText.setText("")
                        endDateEditText.setText("")
                    }
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

                showDateRangePickerDialogfor(startDateEditText.context, minDateMillis, maxDateMillis,layoutIndex) { start, end ->
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

                showDateRangePickerDialogfor(startDateEditText.context, minDateMillis, maxDateMillis,layoutIndex) { start, end ->
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

    fun showDateRangePickerDialogfor(
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

        calendarView.state().edit()
            .setCalendarDisplayMode(CalendarMode.MONTHS)
            .commit()

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

        // Disable overlapping dates with other plans, except Plan 1 (if it exists)
        calendarView.addDecorator(object : DayViewDecorator {
            override fun shouldDecorate(day: CalendarDay?): Boolean {
                val dateInMillis = day?.calendar?.timeInMillis ?: return false

                // If it's Plan 1 (index == 0), don't disable any dates
                if (layoutIndex == 0) return false

                // Check for overlap with other plans (Plan 2, Plan 3, etc.)
                return selectedDateRanges.any { (start, end) ->
                    (dateInMillis in start..end) && (layoutIndex != 0)
                }
            }

            override fun decorate(view: DayViewFacade?) {
                view?.addSpan(ForegroundColorSpan(Color.LTGRAY))
                view?.setDaysDisabled(true)
            }
        })

        // Confirm button click event
        confirmButton.setOnClickListener {
            val selectedDates = calendarView.selectedDates

            if (selectedDates.size >= 2) {
                val startDate = selectedDates.first().calendar
                val endDate = selectedDates.last().calendar

                startDate.set(Calendar.HOUR_OF_DAY, 0)
                startDate.set(Calendar.MINUTE, 0)
                startDate.set(Calendar.SECOND, 0)
                startDate.set(Calendar.MILLISECOND, 0)

                endDate.set(Calendar.HOUR_OF_DAY, 23)
                endDate.set(Calendar.MINUTE, 59)
                endDate.set(Calendar.SECOND, 59)
                endDate.set(Calendar.MILLISECOND, 999)

                val startMillis = startDate.timeInMillis
                val endMillis = endDate.timeInMillis

                // If editing Plan 1 (index == 0), allow date changes without restrictions
                if (layoutIndex == 0) {
                    // Update Plan 1 without any conflict check
                    if (layoutIndex != null && layoutIndex < selectedDateRanges.size) {
                        selectedDateRanges[layoutIndex] = startMillis to endMillis
                    } else {
                        selectedDateRanges.add(startMillis to endMillis)
                    }
                    callback(startMillis, endMillis)
                    dialog.dismiss()
                } else {
                    // For Plan 2 and beyond, check for conflicts with Plan 1 first
                    val conflictWithPlan1 = checkForConflictWithPlan1(startMillis, endMillis)
                    if (conflictWithPlan1) {
                        textView.text = "Selected dates conflict with Plan 1"
                        textView.setTextColor(Color.RED)
                    } else {
                        // Then check for conflicts with other plans (Plan 2, Plan 3, etc.)
                        val conflictWithOtherPlans = checkForConflictWithOtherPlans(startMillis, endMillis, layoutIndex)
                        if (conflictWithOtherPlans) {
                            textView.text = "Selected dates conflict with another plan"
                            textView.setTextColor(Color.RED)
                        } else {
                            // Update the selected range for the current plan
                            if (layoutIndex != null && layoutIndex < selectedDateRanges.size) {
                                selectedDateRanges[layoutIndex] = startMillis to endMillis
                            } else {
                                selectedDateRanges.add(startMillis to endMillis)
                            }
                            callback(startMillis, endMillis)
                            dialog.dismiss()
                        }
                    }
                }
            } else {
                textView.text = "Please select both start and end dates"
            }
        }

        dialog.show()
    }

    fun checkForConflictWithPlan1(startMillis: Long, endMillis: Long): Boolean {
        selectedDateRanges.forEachIndexed { index, (start, end) ->
            if (index == 0 && ((startMillis in start..end) || (endMillis in start..end) || (startMillis < start && endMillis > end))) {
                return true
            }
        }
        return false
    }

    fun checkForConflictWithOtherPlans(startMillis: Long, endMillis: Long, layoutIndex: Int?): Boolean {
        selectedDateRanges.forEachIndexed { index, (start, end) ->
            if (index != layoutIndex && (startMillis in start..end || endMillis in start..end || (startMillis < start && endMillis > end))) {
                return true // Conflict with another plan
            }
        }
        return false // No conflict with other plans
    }



    fun isConflict(startDate: String, endDate: String, layoutIndex: Int?): Boolean {
        val startMillis = convertDateToMillis(startDate)
        val endMillis = convertDateToMillis(endDate)

        for (i in 0 until trainingPlanContainer.childCount) {
            if (i != layoutIndex) {
                val layout = trainingPlanContainer.getChildAt(i)
                val existingStartDate: AppCompatEditText = layout.findViewById(R.id.ent_start_date_liner)
                val existingEndDate: AppCompatEditText = layout.findViewById(R.id.ent_ent_date_liner)

                val existingStartMillis = convertDateToMillis(existingStartDate.text.toString().trim())
                val existingEndMillis = convertDateToMillis(existingEndDate.text.toString().trim())

                // Check for overlap using the isOverlapping function
                if (isOverlapping(startMillis, endMillis, existingStartMillis, existingEndMillis)) {
                    return true // Conflict found
                }
            }
        }
        return false // No conflict
    }

    // Convert date in "yyyy-MM-dd" format to milliseconds
    fun convertDateToMillis(dateString: String): Long {
        return try {
            val sdf = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()) // Adjust format based on your date format
            val date = sdf.parse(dateString)
            date?.time ?: 0L
        } catch (e: Exception) {
            0L // Return 0 if parsing fails (invalid date)
        }
    }

    // Check if two date ranges overlap
    fun isOverlapping(start1: Long, end1: Long, start2: Long, end2: Long): Boolean {
        return start1 < end2 && start2 < end1
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


    fun revalidatePlans() {
        selectedDateRanges.forEachIndexed { index, (start, end) ->
            for (i in selectedDateRanges.indices) {
                if (i != index) {
                    val (otherStart, otherEnd) = selectedDateRanges[i]
                    if (start in otherStart..otherEnd || end in otherStart..otherEnd) {
                        // Show error on conflicting plan
                        showConflictError(i)
                        break
                    }
                }
            }
        }
    }


    fun showConflictError(layoutIndex: Int) {
        val layout = trainingPlanContainer.getChildAt(layoutIndex)
        val errorTextView = layout.findViewById<TextView>(R.id.linear_days_list)
        errorTextView.text = "Date conflict with another plan"
        errorTextView.setTextColor(Color.RED)
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
            var plan1StartDate = ""
            var plan1EndDate = ""

            for (i in 0 until trainingPlanContainer.childCount) {
                val layout = trainingPlanContainer.getChildAt(i)
                val nameEditText: AppCompatEditText = layout.findViewById(R.id.ent_pre_sea_name)
                val startDateEditText: AppCompatEditText = layout.findViewById(R.id.ent_start_date_liner)
                val endDateEditText: AppCompatEditText = layout.findViewById(R.id.ent_ent_date_liner)
                val mesocycleTextView: TextView = layout.findViewById(R.id.linear_days_list)

                val name = nameEditText.text.toString().trim()
                val mesocycleValue = mesocycleTextView.text.toString().split(" ")[0]
                val mesocycle = mesocycleValue.toIntOrNull()

                val startDate = startDateEditText.text.toString().trim()
                val endDate = endDateEditText.text.toString().trim()

                if (name.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
                    Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                    return
                }

                // Store Plan 1 dates for final validation
                if (i == 0) {
                    plan1StartDate = startDate
                    plan1EndDate = endDate
                }

                // Individual plan conflict check
                if (i != 0) {
                    if (isConflict(startDate, endDate, i)) {
                        Toast.makeText(this, "Date conflict detected for Plan ${i + 1}", Toast.LENGTH_SHORT).show()
                        return
                    }
                }

                // Assign plans to respective objects
                when (i) {
                    0 -> preSeason = PreSeason(name, startDate, endDate, mesocycle.toString())
                    1 -> preCompetitive = PreCompetitive(name, startDate, endDate, mesocycle.toString())
                    2 -> competitive = Competitive(name, startDate, endDate, mesocycle.toString())
                    3 -> transition = Transition(name, startDate, endDate, mesocycle.toString())
                }
            }

            Log.d("RTTRTRTRTRTR", "saveTrainingPlans: ${isConflictWithOtherPlans(plan1StartDate , plan1EndDate)}")
            // Final Plan 1 Conflict Check after all edits
            if (isConflictWithOtherPlans(plan1StartDate, plan1EndDate)) {
                Toast.makeText(this, "Plan 1 conflicts with another plan. Cannot save.", Toast.LENGTH_SHORT).show()
                return
            }

            // Prepare and send the request
            val trainingPlanRequest = TrainingPlanSubClass(
                id = id?.toInt() ?: 0,
                name = addTrainingPlanBinding.edtProgramName.text.toString(),
                competition_date = enddatesent.toString(),
                start_date = startdatesent.toString(),
                pre_season = preSeason,
                pre_competitive = preCompetitive,
                competitive = competitive,
                transition = transition,
                mesocycle = calculateDaysBetweenDates(startdatesent.toString(), enddatesent.toString()).toString()
            )

            apiInterface.CreatePlanning(trainingPlanRequest)?.enqueue(object : Callback<TrainingPlanData> {
                override fun onResponse(call: Call<TrainingPlanData>, response: Response<TrainingPlanData>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@AddTrainingPlanActivity, response.body()?.message ?: "Success", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@AddTrainingPlanActivity, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<TrainingPlanData>, t: Throwable) {
                    Toast.makeText(this@AddTrainingPlanActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } catch (e: Exception) {
            Log.e("SaveTrainingPlanError", e.message ?: "Error saving training plans")
            Toast.makeText(this, "An unexpected error occurred", Toast.LENGTH_SHORT).show()
        }
    }

    fun isConflictWithOtherPlans(startDate: String, endDate: String): Boolean {
        val startMillis = convertDateToMillis(startDate)
        val endMillis = convertDateToMillis(endDate)

        Log.d("DATE_CHECK", "Checking Plan 1: Start: $startMillis, End: $endMillis")

        for (i in 1 until trainingPlanContainer.childCount) {
            val layout = trainingPlanContainer.getChildAt(i)
            val existingStartDate: AppCompatEditText = layout.findViewById(R.id.ent_start_date_liner)
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
