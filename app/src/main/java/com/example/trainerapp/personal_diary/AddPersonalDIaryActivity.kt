package com.example.trainerapp.personal_diary

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import com.example.model.personal_diary.GetPersonalDiaryData
import com.example.model.personal_diary.TrainingAssessment
import com.example.model.personal_diary.TrainingSession
import com.example.model.training_plan.TrainingPlanData
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.PreferencesManager
import com.example.trainerapp.R
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.ActivityAddPersonalDiaryBinding
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class AddPersonalDIaryActivity : AppCompatActivity() {

    private lateinit var addPersonalDiaryBinding: ActivityAddPersonalDiaryBinding
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    lateinit var Progress: ProgressBar
    lateinit var preferenceManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPersonalDiaryBinding = ActivityAddPersonalDiaryBinding.inflate(layoutInflater)
        setContentView(addPersonalDiaryBinding.root)

        initView()
        setupButtonClickListeners()
        setUpEnergyTextWatcher(addPersonalDiaryBinding.EnergyBT)
        setUpEnergyTextWatcher(addPersonalDiaryBinding.EnergyAT)
        setUpEnergyTextWatcher(addPersonalDiaryBinding.EnergyDT)

        setUpEnergyTextWatcher(addPersonalDiaryBinding.SatisfationBT)
        setUpEnergyTextWatcher(addPersonalDiaryBinding.SatisfationDT)
        setUpEnergyTextWatcher(addPersonalDiaryBinding.SatisfationAT)

        setUpEnergyTextWatcher(addPersonalDiaryBinding.HapinessBT)
        setUpEnergyTextWatcher(addPersonalDiaryBinding.HapinessDT)
        setUpEnergyTextWatcher(addPersonalDiaryBinding.HapinessAT)

        setUpEnergyTextWatcher(addPersonalDiaryBinding.IrritabilityBT)
        setUpEnergyTextWatcher(addPersonalDiaryBinding.IrritabilityDT)
        setUpEnergyTextWatcher(addPersonalDiaryBinding.IrritabilityAT)

        setUpEnergyTextWatcher(addPersonalDiaryBinding.DeterminationBT)
        setUpEnergyTextWatcher(addPersonalDiaryBinding.DeterminationDT)
        setUpEnergyTextWatcher(addPersonalDiaryBinding.DeterminationAT)

        setUpEnergyTextWatcher(addPersonalDiaryBinding.AnxietyBT)
        setUpEnergyTextWatcher(addPersonalDiaryBinding.AnxietyDT)
        setUpEnergyTextWatcher(addPersonalDiaryBinding.AnxietyAT)

        setUpEnergyTextWatcher(addPersonalDiaryBinding.TirednessBT)
        setUpEnergyTextWatcher(addPersonalDiaryBinding.TirednessDT)
        setUpEnergyTextWatcher(addPersonalDiaryBinding.TirednessAT)

        addPersonalDiaryBinding.back.setOnClickListener { finish() }


    }

    fun setUpEnergyTextWatcher(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                // Convert the input to an integer, default to 0 if it's not a valid integer
                val input = s.toString().toIntOrNull() ?: 0
                if (input > 5) {
                    // Clear the text if the input exceeds 5
                    editText.setText("")
                    // Move the cursor to the end of the EditText
                    editText.setSelection(editText.text.length)
                }
            }
        })
    }

    private fun setupButtonClickListeners() {
        addPersonalDiaryBinding.editDate.setOnClickListener {
                showDatePickerDialog {dayOfMonth, month, year ->
                    val selectedDate = "$dayOfMonth-${month + 1}-$year"
                    addPersonalDiaryBinding.dateTextView.text = selectedDate
                }
        }

        addPersonalDiaryBinding.editTime.setOnClickListener { setTimerDialog() }


        addPersonalDiaryBinding.cardSave.setOnClickListener { saveDiary() }
    }

    private fun saveDiary() {
        try {
            val date = addPersonalDiaryBinding.dateTextView.text.toString()
            val sleepHours = addPersonalDiaryBinding.sleepHoursTextView.text.toString()
            val nutritionAndHydration = addPersonalDiaryBinding.NutritionAndHydration.text.toString()
            val notes = addPersonalDiaryBinding.Notes.text.toString()

            Log.d("Data", "$date, $sleepHours, $nutritionAndHydration, $notes")

            val trainingAssessments = mutableListOf(
                TrainingAssessment(
                    assess_your_level_of = "Energy",
                    before_training = addPersonalDiaryBinding.EnergyBT.text.toString() ?: "",
                    during_training = addPersonalDiaryBinding.EnergyDT.text.toString() ?: "",
                    after_training = addPersonalDiaryBinding.EnergyAT.text.toString() ?: ""
                ),
                TrainingAssessment(
                    assess_your_level_of = "Satisfaction",
                    before_training = addPersonalDiaryBinding.SatisfationBT.text.toString() ?: "",
                    during_training = addPersonalDiaryBinding.SatisfationDT.text.toString() ?: "",
                    after_training = addPersonalDiaryBinding.SatisfationAT.text.toString() ?: ""
                ),
                TrainingAssessment(
                    assess_your_level_of = "Happiness",
                    before_training = addPersonalDiaryBinding.HapinessBT.text.toString() ?: "",
                    during_training = addPersonalDiaryBinding.HapinessDT.text.toString() ?: "",
                    after_training = addPersonalDiaryBinding.HapinessAT.text.toString() ?: ""
                ),
                TrainingAssessment(
                    assess_your_level_of = "Irritability",
                    before_training = addPersonalDiaryBinding.IrritabilityBT.text.toString() ?: "",
                    during_training = addPersonalDiaryBinding.IrritabilityDT.text.toString() ?: "",
                    after_training = addPersonalDiaryBinding.IrritabilityAT.text.toString() ?: ""
                ),
                TrainingAssessment(
                    assess_your_level_of = "Determination",
                    before_training = addPersonalDiaryBinding.DeterminationBT.text.toString() ?: "",
                    during_training = addPersonalDiaryBinding.DeterminationDT.text.toString() ?: "",
                    after_training = addPersonalDiaryBinding.DeterminationAT.text.toString() ?: ""
                ),
                TrainingAssessment(
                    assess_your_level_of = "Anxiety",
                    before_training = addPersonalDiaryBinding.AnxietyBT.text.toString() ?: "",
                    during_training = addPersonalDiaryBinding.AnxietyDT.text.toString() ?: "",
                    after_training = addPersonalDiaryBinding.AnxietyAT.text.toString() ?: ""
                ),
                TrainingAssessment(
                    assess_your_level_of = "Tiredness",
                    before_training = addPersonalDiaryBinding.TirednessBT.text.toString() ?: "",
                    during_training = addPersonalDiaryBinding.TirednessDT.text.toString() ?: "",
                    after_training = addPersonalDiaryBinding.TirednessAT.text.toString() ?: ""
                )
            )

            val personalDiaryData = TrainingSession(
                id = 0,
                date = date.ifEmpty { "" },
                sleep_hours = sleepHours.ifEmpty { "00:00:00" },
                nutrition_and_hydration = nutritionAndHydration.ifEmpty { "" },
                notes = notes.ifEmpty { "" },
                share = 0,
                data = trainingAssessments
            )

            Log.d("Request Payload", Gson().toJson(personalDiaryData))

            apiInterface.AddPersonalDIaryData(personalDiaryData)?.enqueue(object : Callback<GetPersonalDiaryData> {
                override fun onResponse(call: Call<GetPersonalDiaryData>, response: Response<GetPersonalDiaryData>) {
                    Log.d("APIResponse", "Response: ${response.code()} - ${response.errorBody()}")

                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        responseBody?.let {
                            it.data?.personalDairieDetails?.forEach { assessment ->
                                Log.d(
                                    "Diary Assessment",
                                    "ID: ${assessment.id}, Level: ${assessment.assessYourLevelOf}, Before: ${assessment.beforeTraining}, During: ${assessment.duringTraining}, After: ${assessment.afterTraining}, Created At: ${assessment.createdAt}, Updated At: ${assessment.updatedAt}"
                                )
                            }
                            Toast.makeText(this@AddPersonalDIaryActivity, "Diary saved successfully!", Toast.LENGTH_SHORT).show()
                            finish()
                        } ?: run {
                            Log.e("API Error", "Response body is null")
                        }
                    } else {
                        val errorBody = response.errorBody()?.string() ?: "Unknown error"
                        Log.e("API Error", "Response: ${response.code()} - $errorBody")
                        Toast.makeText(this@AddPersonalDIaryActivity, "Error: $errorBody", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<GetPersonalDiaryData>, t: Throwable) {
                    Log.e("API Error", "Error: ${t.message}")
                    Toast.makeText(
                        this@AddPersonalDIaryActivity,
                        "API call failed: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } catch (e: Exception) {
            Log.e("Exception", "Error: ${e.message}", e)
            Toast.makeText(this, "Exception: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initView() {
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        preferenceManager = PreferencesManager(this)

    }

    private fun showDatePickerDialog(onDateSelected: (year: Int, month: Int, dayOfMonth: Int) -> Unit) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_date_picker)

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = (displayMetrics.widthPixels * 0.9f).toInt()
        dialog.window?.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val datePicker = dialog.findViewById<DatePicker>(R.id.datePicker)
        val cancelButton = dialog.findViewById<Button>(R.id.btnCancel)
        val applyButton = dialog.findViewById<Button>(R.id.btnApply)


        // Default date (today)
        val calendar = Calendar.getInstance()
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), null)

        cancelButton.setOnClickListener { dialog.dismiss() }

        applyButton.setOnClickListener {
            val selectedYear = datePicker.year
            val selectedMonth = datePicker.month
            val selectedDay = datePicker.dayOfMonth
            onDateSelected(selectedYear, selectedMonth, selectedDay)
            dialog.dismiss()
        }

        dialog.show()
    }

    // Function to show the custom timer dialog
    private fun setTimerDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_set_full_time_picker)

        // Set dialog width to 90% of screen width
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = (displayMetrics.widthPixels * 0.9f).toInt()
        dialog.window?.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val title = dialog.findViewById<AppCompatTextView>(R.id.tvTitle)
        val hourPicker = dialog.findViewById<NumberPicker>(R.id.hour_num)
        val minutePicker = dialog.findViewById<NumberPicker>(R.id.mint_num)
        val secondPicker = dialog.findViewById<NumberPicker>(R.id.second_num)
        val btnApply = dialog.findViewById<Button>(R.id.btnApply)
        val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)

        if (title != null) title.text = "Time Picker"

        var hourNumber = 0
        var minuteNumber = 0
        var secondNumber = 0

        // Initialize the pickers
        hourPicker?.apply {
            minValue = 0
            maxValue = 12
            wrapSelectorWheel = true
            setOnValueChangedListener { _, _, newVal -> hourNumber = newVal }
        }

        minutePicker?.apply {
            minValue = 0
            maxValue = 59
            wrapSelectorWheel = true
            setOnValueChangedListener { _, _, newVal -> minuteNumber = newVal }
        }

        secondPicker?.apply {
            minValue = 0
            maxValue = 59
            wrapSelectorWheel = true
            setOnValueChangedListener { _, _, newVal -> secondNumber = newVal }
        }

        // Cancel button
        btnCancel?.setOnClickListener {
            dialog.dismiss()
        }

        // Apply button
        btnApply?.setOnClickListener {
            val formattedTime = String.format("%02d:%02d:%02d", hourNumber, minuteNumber, secondNumber)
            addPersonalDiaryBinding.sleepHoursTextView.text = formattedTime
            dialog.dismiss()
        }

        dialog.show()
    }
}
