package com.example.trainerapp.training_plan

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.cardview.widget.CardView
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddTrainingPlanActivity : AppCompatActivity() {
    lateinit var addTrainingPlanBinding: ActivityAddTrainingPlanBinding
    private lateinit var apiInterface: APIInterface
    private lateinit var preferenceManager: PreferencesManager
    private lateinit var apiClient: APIClient


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
                        Toast.makeText(
                            this@AddTrainingPlanActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
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
                updateUI(addTrainingPlanBinding.cardSave)
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
            addButton.setCardBackgroundColor(resources.getColor(R.color.splash_text_color)) // Change to your desired color
        } else {
            addButton.isEnabled = false
            addButton.setCardBackgroundColor(resources.getColor(R.color.grey)) // Disabled color
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

        addTrainingPlanBinding.cardDate.setOnClickListener { selectStartDate() }
        addTrainingPlanBinding.cardEndDate.setOnClickListener { selectEndDate() }
        addTrainingPlanBinding.edtStartDate.setOnClickListener { selectStartDate() }
        addTrainingPlanBinding.edtEndDate.setOnClickListener { selectEndDate() }

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
                addTrainingPlanBinding.edtStartDate,  // Pass the TextView (or EditText)
                System.currentTimeMillis(),  // Current time as the minimum selectable date
                Long.MAX_VALUE  // No limit for the max date
            ) { dateMillis ->
                startDateMillis = dateMillis
                val formattedDate = formatDate(dateMillis)
                addTrainingPlanBinding.edtStartDate.setText(formattedDate)


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
                addTrainingPlanBinding.edtEndDate.setText(formattedDate)
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
                try {
                    val days = calculateDaysBetweenDates2(startDate, endDate)
                    mesocycles.text = "$days days"
                } catch (e: Exception) {
                    mesocycles.text = "Invalid dates"
                    Log.e("DateError", "Error calculating days: ${e.message}")
                }
            } else {
                mesocycles.text = "Select dates" // Default message
            }
        }

        startDateEditText.setOnClickListener {

            if (addTrainingPlanBinding.edtStartDate.text.isNullOrEmpty()) {
                Toast.makeText(this, "Please select a start date first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                selectTrainingPlanStartDate(startDateEditText)
                updateDaysTextView()
            }

        }


        startdatecard.setOnClickListener {

            if (addTrainingPlanBinding.edtStartDate.text.isNullOrEmpty()) {
                Toast.makeText(this, "Please select a start date first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                selectTrainingPlanStartDate(startDateEditText)
                updateDaysTextView()
            }

        }

        endDateEditText.setOnClickListener {

            if (addTrainingPlanBinding.edtEndDate.text.isNullOrEmpty()) {
                Toast.makeText(this, "Please select a start date first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                selectTrainingPlanEndDate(endDateEditText)
                updateDaysTextView()
            }
        }

        enddatecard.setOnClickListener {

            if (addTrainingPlanBinding.edtEndDate.text.isNullOrEmpty()) {
                Toast.makeText(this, "Please select a start date first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                selectTrainingPlanEndDate(endDateEditText)
                updateDaysTextView()
            }
        }

        startDateEditText.setOnFocusChangeListener { _, _ -> updateDaysTextView() }
        endDateEditText.setOnFocusChangeListener { _, _ -> updateDaysTextView() }

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
        for (i in trainingPlanLayouts.indices) {
            val layout = trainingPlanLayouts[i]
            val indexTextView: AppCompatEditText = layout.findViewById(R.id.ent_pre_sea_name)
            indexTextView.hint = ""
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


    private fun selectTrainingPlanStartDate(editText: AppCompatEditText) {
        // Get the main start and max end dates from the binding
        val mainStartDate = addTrainingPlanBinding.edtStartDate.text.toString()
        val maxStartDate = addTrainingPlanBinding.edtEndDate.text.toString()

        // Check if mainStartDate and maxStartDate are valid dates
        val minDateMillis =
            if (mainStartDate.isNotEmpty()) formatDateToMillis(mainStartDate) else System.currentTimeMillis()
        val maxDateMillis =
            if (maxStartDate.isNotEmpty()) formatDateToMillis(maxStartDate) else Long.MAX_VALUE

        // Call Utils.selectDate3 with the min and max dates
        Utils.selectDate3(
            this,
            editText,
            minDateMillis, // Minimum date for selection
            maxDateMillis
        ) { dateMillis ->
            // When a date is selected, update startDateMillis and display the formatted date
            startDateMillis = dateMillis
            val formattedDate = formatDate(dateMillis)
            editText.setText(formattedDate)
        }
    }


    private fun selectTrainingPlanEndDate(editText: AppCompatEditText) {

        val mainEndDate = addTrainingPlanBinding.edtEndDate.text.toString()

        Utils.selectDate3(
            this,
            editText,
            if (startDateMillis > 0) startDateMillis else System.currentTimeMillis(), // Set limit based on main start date
            if (mainEndDate.isNotEmpty()) formatDateToMillis(mainEndDate) else Long.MAX_VALUE // Set limit based on main end date
        ) { dateMillis ->
            endDateMillis = dateMillis
            val formattedDate = formatDate(dateMillis)
            editText.setText(formattedDate)
        }
    }

    private fun formatDateToMillis(dateString: String): Long {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return format.parse(dateString)?.time
            ?: Long.MAX_VALUE // Return millis or a very high value if parsing fails
    }

    private fun formatDate(dateMillis: Long): String {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
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
                val start = startDateEditText.text.toString().trim()
                val end = endDateEditText.text.toString().trim()
                val mesocycleValue = mesocycleTextView.text.toString().split(" ")[0]
                val mesocycle = mesocycleValue.toIntOrNull()

                // Check for empty fields
                if (name.isEmpty() || start.isEmpty() || end.isEmpty()) {
                    Toast.makeText(
                        this,
                        "Please fill in all fields for plan ${i + 1}",
                        Toast.LENGTH_SHORT
                    ).show()
                    return // Exit if validation fails
                }

                when (i) {
                    0 -> preSeason = PreSeason(
                        name = name,
                        start_date = start,
                        end_date = end,
                        mesocycle = mesocycle.toString()
                    )

                    1 -> preCompetitive = PreCompetitive(
                        name = name,
                        start_date = start,
                        end_date = end,
                        mesocycle = mesocycle.toString()
                    )

                    2 -> competitive = Competitive(
                        name = name,
                        start_date = start,
                        end_date = end,
                        mesocycle = mesocycle.toString()
                    )

                    3 -> transition = Transition(
                        name = name,
                        start_date = start,
                        end_date = end,
                        mesocycle = mesocycle.toString()
                    )
                }
            }
            val trainingPlanRequest = TrainingPlanSubClass(
                id = id?.toInt() ?: 0,
                name = addTrainingPlanBinding.edtProgramName.text.toString(),
                competition_date = addTrainingPlanBinding.edtEndDate.text.toString(),
                start_date = addTrainingPlanBinding.edtStartDate.text.toString(),
                pre_season = preSeason,
                pre_competitive = preCompetitive,
                competitive = competitive,
                transition = transition,
                mesocycle = calculateDaysBetweenDates(
                    addTrainingPlanBinding.edtStartDate.text.toString(),
                    addTrainingPlanBinding.edtEndDate.text.toString()
                ).toString()
            )
            Log.d("TrainingPlan", "Request: ${gson.toJson(trainingPlanRequest)}")
            apiInterface.CreatePlanning(trainingPlanRequest)
                ?.enqueue(object : Callback<TrainingPlanData> {
                    override fun onResponse(
                        call: Call<TrainingPlanData>,
                        response: Response<TrainingPlanData>
                    ) {
                        Log.d(
                            "APIResponse",
                            "Response: ${response.code()} - ${response.errorBody()}"
                        )

                        Log.d("TAG", response.code().toString() + "")
                        val code = response.code()
                        if (code == 200) {
                            if (response.isSuccessful) {
                                Toast.makeText(
                                    this@AddTrainingPlanActivity,
                                    response.body()?.message ?: "Success",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish() // Close the activity
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
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val startDate = format.parse(start)
        val endDate = format.parse(end)
        val difference = endDate.time - startDate.time
        return (difference / (1000 * 60 * 60 * 24)).toInt()
    }

    private fun calculateDaysBetweenDates2(start: String, end: String): Int {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val startDate = format.parse(start)
        val endDate = format.parse(end)

        if (startDate == null || endDate == null) {
            throw IllegalArgumentException("Invalid date format")
        }

        val difference = endDate.time - startDate.time
        return (difference / (1000 * 60 * 60 * 24)).toInt()
    }

    private fun updateDaysTextView() {
        val startDate = addTrainingPlanBinding.edtStartDate.text.toString()
        val endDate = addTrainingPlanBinding.edtEndDate.text.toString()

        if (startDate.isNotEmpty() && endDate.isNotEmpty()) {
            val days = calculateDaysBetweenDates(startDate, endDate)
            addTrainingPlanBinding.days.text = "$days Days"
        } else {
            addTrainingPlanBinding.days.text = "0 Days"
        }
    }

    private fun updateDaysTextViewlist() {
        val newTrainingPlanLayout = LayoutInflater.from(this)
            .inflate(R.layout.add_training_plan_list, trainingPlanContainer, false)


        val startDateEditText: AppCompatEditText =
            newTrainingPlanLayout.findViewById(R.id.ent_start_date_liner)
        val endDateEditText: AppCompatEditText =
            newTrainingPlanLayout.findViewById(R.id.ent_start_date_liner)


        val startDate = startDateEditText.text.toString()
        val endDate = endDateEditText.text.toString()

        if (startDate.isNotEmpty() && endDate.isNotEmpty()) {
            val days = calculateDaysBetweenDates2(startDate, endDate)
            addTrainingPlanBinding.days.text = "$days Days"
        } else {
            addTrainingPlanBinding.days.text = "0 Days"
        }
    }


    private fun isValidate(): Boolean {
        // Validate the training plan name
        if (addTrainingPlanBinding.edtProgramName.text.isNullOrEmpty()) {
            addTrainingPlanBinding.errorProgram.visibility = View.VISIBLE
            return false

        } else {
            addTrainingPlanBinding.errorProgram.visibility = View.GONE
        }

        if (addTrainingPlanBinding.edtStartDate.text.isNullOrEmpty()) {
            addTrainingPlanBinding.errorStartDate.visibility = View.VISIBLE
            return false
        } else {
            addTrainingPlanBinding.errorStartDate.visibility = View.GONE
        }

        if (addTrainingPlanBinding.edtEndDate.text.isNullOrEmpty()) {
            addTrainingPlanBinding.errorEndDate.visibility = View.VISIBLE
            return false
        } else {
            addTrainingPlanBinding.errorEndDate.visibility = View.GONE

        }

        // Validate the training plan containers
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
                Toast.makeText(this, "startDate", Toast.LENGTH_SHORT).show()
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
}