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

        val mainStartDate = startdatesent.toString()
        val maxStartDate = enddatesent.toString()

        val minDateMillis =
            if (mainStartDate.isNotEmpty()) formatDateToMillis(mainStartDate) else System.currentTimeMillis()
        val maxDateMillis =
            if (maxStartDate.isNotEmpty()) formatDateToMillis(maxStartDate) else Long.MAX_VALUE

        // Call Utils.selectDate3 with the min and max dates
        Utils.selectDate3(
            this,
            editText,
            minDateMillis,
            maxDateMillis
        ) { dateMillis ->
            // When a date is selected, update startDateMillis and display the formatted date
            startDateMillis = dateMillis
            val formattedDate = formatDate(dateMillis)
            val formattedDateset = formatDate2(dateMillis)

            startdatesentlist = formattedDate

//            Log.e("SSSSSSSSS", "selectTrainingPlanStartDate: "+convertDateFormat(formattedDate) )
//            Log.e("SSSSSSSSS", "selectTrainingPlanStartDate: "+formattedDate)

            editText.setText(formattedDateset)

        }
    }

    private fun selectTrainingPlanEndDate(editText: AppCompatEditText) {

        val mainEndDate = enddatesent.toString()

        Utils.selectDate3(this, editText, if (startDateMillis > 0) startDateMillis else System.currentTimeMillis(),
            if (mainEndDate.isNotEmpty()) formatDateToMillis(mainEndDate) else Long.MAX_VALUE) { dateMillis ->
            endDateMillis = dateMillis
            val formattedDate = formatDate(dateMillis)
            val formattedDateset = formatDate2(dateMillis)

            enddatesentlist = formattedDate

//            Log.e("SSSSSSSSSEEEEE", "selectTrainingPlanStartDate: "+convertDateFormat(formattedDate) )
//            Log.e("SSSSSSSSSEEEEE", "selectTrainingPlanStartDate: "+formattedDate)

            Log.d("error :- ",editText.text.toString())
            editText.setText(formattedDateset)
//            convertDateFormat(formattedDate)
        }
    }

    private fun formatDateToMillis(dateString: String): Long {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return format.parse(dateString)?.time
            ?: Long.MAX_VALUE
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