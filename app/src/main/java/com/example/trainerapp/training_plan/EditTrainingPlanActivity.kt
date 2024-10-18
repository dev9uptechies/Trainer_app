package com.example.trainerapp.training_plan

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import com.example.trainerapp.ApiClass.Training_Plan.create_training.EditeTrainingPlanAdapter
import com.example.trainerapp.PreferencesManager
import com.example.trainerapp.R
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.ActivityEditTrainingPlanBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
    private var startDateMillis: Long = 0
    private var endDateMillis: Long = 0

    lateinit var adapter: EditeTrainingPlanAdapter

    private var competitive: Competitive = Competitive("", "", "", "")
    private var preSeason = PreSeason("", "", "", "")
    private var transition = Transition("", "", "", "")
    private var preCompetitive: PreCompetitive = PreCompetitive("", "", "", "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        editTrainingPlanBinding = ActivityEditTrainingPlanBinding.inflate(layoutInflater)
        setContentView(editTrainingPlanBinding.root)
        initView()
        loadData()
        checkButtonClick()
        textWatcher()
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
                updateUI(editTrainingPlanBinding.cardSave)
            }
        })
        editTrainingPlanBinding.edtStartDate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateUI(editTrainingPlanBinding.cardSave)
            }
        })

        editTrainingPlanBinding.edtEndDate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
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
        editTrainingPlanBinding.cardDate.setOnClickListener { selectStartDate() }
        editTrainingPlanBinding.cardEndDate.setOnClickListener { selectEndDate() }
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


    private fun EditeProgramData() {
        if (!isValidate()) return
        try {
            Log.d(
                "EditeData",
                "Child count in trainingPlanContainer: ${trainingPlanContainer.childCount}"
            )

            for (i in 0 until trainingPlanContainer.childCount) {
                val layout = trainingPlanContainer.getChildAt(i)
                val nameEditText: AppCompatEditText? = layout.findViewById(R.id.ent_pre_sea_name)
                val startDateEditText: AppCompatEditText? =
                    layout.findViewById(R.id.ent_start_date_liner)
                val endDateEditText: AppCompatEditText? =
                    layout.findViewById(R.id.ent_ent_date_liner)
                val mesocycles: TextView? = layout.findViewById(R.id.linear_days_list)
                if (nameEditText == null || startDateEditText == null || endDateEditText == null || mesocycles == null) {
                    continue
                }

                val name = nameEditText.text.toString().trim()
                val start = startDateEditText.text.toString().trim()
                val end = endDateEditText.text.toString().trim()
                val mesocycle = mesocycles.text.toString().trim()

                if (name.isEmpty() && start.isEmpty() && end.isEmpty()) {
                    continue
                }
                if (name.isEmpty() || start.isEmpty() || end.isEmpty()) {
                    Log.e(
                        "EditeData",
                        "Validation failed for plan ${i + 1}: Please fill in all fields."
                    )
                    Toast.makeText(
                        this,
                        "Please fill in all fields for plan ${i + 1}",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
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


                val programName = editTrainingPlanBinding.edtProgramName.text.toString().trim()
                val startDate = editTrainingPlanBinding.edtStartDate.text.toString().trim()
                val endDate = editTrainingPlanBinding.edtEndDate.text.toString().trim()

                if (programName.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
                    Toast.makeText(
                        this,
                        "Please fill in the program name, start date, and end date",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }

                val trainingPlanRequest = TrainingPlanSubClass(
                    id = id!!,
                    name = editTrainingPlanBinding.edtProgramName.text.toString(),
                    competition_date = editTrainingPlanBinding.edtEndDate.text.toString(),
                    start_date = editTrainingPlanBinding.edtStartDate.text.toString(),
                    pre_season = preSeason,
                    pre_competitive = preCompetitive,
                    competitive = competitive,
                    transition = transition,
                    mesocycle = editTrainingPlanBinding.days.text.toString()
                )

                apiInterface.EditeTrainingPlan(editeTrainingPlan = trainingPlanRequest)
                    ?.enqueue(object : Callback<TrainingPlanData> {
                        override fun onResponse(
                            call: Call<TrainingPlanData>,
                            response: Response<TrainingPlanData>
                        ) {

                            Log.d("TAG", response.code().toString() + "")
                            val code = response.code()
                            if (code == 200) {
                                Log.d("TAG", "Response Code: ${response.code()}")
                                Log.d("TAG1", "Response Message: ${response.message()}")
                                Log.d("TAG2", "Response Body: ${response.body()}")

                                if (response.isSuccessful) {
                                    val responseBody = response.body()
                                    if (responseBody != null && responseBody.status == true) {
                                        Toast.makeText(
                                            this@EditTrainingPlanActivity,
                                            "Success: ${responseBody.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        finish()
                                    } else {
                                        val errorMessage =
                                            responseBody?.message ?: "Something went wrong"
                                        Log.e("TAG2", "Error: $errorMessage")
                                        Toast.makeText(
                                            this@EditTrainingPlanActivity,
                                            errorMessage,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } else {
                                    val errorBody =
                                        response.errorBody()?.string() ?: "Unknown error"
                                    Log.e("TAG4", "Error Response: $errorBody")
                                    Toast.makeText(
                                        this@EditTrainingPlanActivity,
                                        "Error: $errorBody",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    finish()
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

            }

        } catch (e: Exception) {
            Log.e("Exception", e.message.toString(), e)
        }
    }

    private fun setProgramDataset(data: TrainingPlanData.TrainingPlan) {
        editTrainingPlanBinding.edtProgramName.setText(data.name)
        editTrainingPlanBinding.edtStartDate.setText(data.start_date)
        editTrainingPlanBinding.edtEndDate.setText(data.competition_date)
        editTrainingPlanBinding.days.text = data.mesocycle

        trainingPlanContainer.removeAllViews()
        var count = 0

        data.pre_season?.takeIf { !it.start_date.isNullOrEmpty() && !it.end_date.isNullOrEmpty() }
            ?.let {
                count++
                addTrainingPlanView(
                    it.name ?: "",
                    it.start_date ?: "",
                    it.end_date ?: "",
                    it.mesocycle ?: "0 Days",
                    "Pre-Season"
                )
            }

        data.pre_competitive?.takeIf { !it.name.isNullOrEmpty() && !it.start_date.isNullOrEmpty() && !it.end_date.isNullOrEmpty() }
            ?.let {
                count++
                addTrainingPlanView(
                    it.name.toString(),
                    it.start_date.toString(),
                    it.end_date.toString(),
                    it.mesocycle ?: "0 Days",
                    "Pre-Competitive"
                )
            }

        data.competitive?.takeIf { !it.start_date.isNullOrEmpty() && !it.end_date.isNullOrEmpty() }
            ?.let {
                count++
                addTrainingPlanView(
                    it.name ?: "",
                    it.start_date.toString(),
                    it.end_date.toString(),
                    it.mesocycle ?: "0 Days",
                    "Competitive"
                )
            }

        data.transition?.takeIf { !it.start_date.isNullOrEmpty() && !it.end_date.isNullOrEmpty() }
            ?.let {
                count++
                addTrainingPlanView(
                    it.name ?: "",
                    it.start_date.toString(),
                    it.end_date.toString(),
                    it.mesocycle ?: "0 Days",
                    "Transition"
                )
            }

        Log.d("Training Plan Count", "Total Plans: $count")
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

        val nameEditText: AppCompatEditText = trainingPlanView.findViewById(R.id.ent_pre_sea_name)
        val startDateEditText: AppCompatEditText =
            trainingPlanView.findViewById(R.id.ent_start_date_liner)
        val endDateEditText: AppCompatEditText =
            trainingPlanView.findViewById(R.id.ent_ent_date_liner)
        val mesocyclesTextView: TextView = trainingPlanView.findViewById(R.id.linear_days_list)
        val delete: ImageView = trainingPlanView.findViewById(R.id.img_delete)

        // Initialize fields
        nameEditText.hint = getTrainingPlanDetails(indexToAdd)
        nameEditText.setText(name)
        startDateEditText.setText(startDate)
        endDateEditText.setText(endDate)
        mesocyclesTextView.text = mesocycle

        // Delete button functionality
        delete.setOnClickListener {
            removeTrainingPlan(trainingPlanView)
        }

        // Function to update the days in the TextView
        val updateDaysTextView = {
            val startDate = startDateEditText.text.toString()
            val endDate = endDateEditText.text.toString()

            if (startDate.isNotEmpty() && endDate.isNotEmpty()) {
                try {
                    val days = calculateDaysBetweenDates2(startDate, endDate)
                    mesocyclesTextView.text = "$days days"
                } catch (e: Exception) {
                    mesocyclesTextView.text = "Invalid dates"
                    Log.e("DateError", "Error calculating days: ${e.message}")
                }
            } else {
                mesocyclesTextView.text = "Select dates"
            }
        }

        // Date selection listeners
        val startdatecard: CardView = trainingPlanView.findViewById(R.id.card_start_date_list)
        val enddatecard: CardView = trainingPlanView.findViewById(R.id.card_end_pick_list)

        startDateEditText.setOnClickListener {
            selectTrainingPlanStartDate(startDateEditText)
            updateDaysTextView() // Update days after selecting the start date
        }

        endDateEditText.setOnClickListener {
            selectTrainingPlanEndDate(endDateEditText)
            updateDaysTextView() // Update days after selecting the end date
        }

        startdatecard.setOnClickListener {
            selectTrainingPlanStartDate(startDateEditText)
            updateDaysTextView() // Update days after selecting the start date
        }

        enddatecard.setOnClickListener {
            selectTrainingPlanEndDate(endDateEditText)
            updateDaysTextView() // Update days after selecting the end date
        }

        // Focus change listeners
        startDateEditText.setOnFocusChangeListener { _, _ -> updateDaysTextView() }
        endDateEditText.setOnFocusChangeListener { _, _ -> updateDaysTextView() }

        // Add the view to the container
        trainingPlanContainer.addView(trainingPlanView)
        trainingPlanLayouts.add(trainingPlanView)

        Log.d("AddTrainingPlan", "Training plan added for phase: $phaseType")
    }


    private fun removeTrainingPlan(trainingPlanLayout: View) {
        trainingPlanContainer.removeView(trainingPlanLayout)
        trainingPlanLayouts.remove(trainingPlanLayout)
        trainingPlanCount--
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

    private fun getTrainingPlanDetails(planNumber: Int): String {
        return when (planNumber) {
            1 -> "Enter Pre Season Name"
            2 -> "Enter Pre Competitive Name"
            3 -> "Enter Competitive Name"
            4 -> "Enter Transition Name"
            else -> "Training Plan #:"
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

    private fun selectStartDate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Utils.selectDate3(
                this,
                editTrainingPlanBinding.edtStartDate,
                System.currentTimeMillis(),
                Long.MAX_VALUE
            ) { dateMillis ->
                startDateMillis = dateMillis
                val formattedDate = formatDate(dateMillis)
                editTrainingPlanBinding.edtStartDate.setText(formattedDate)
                endDateMillis = 0
            }
        }
    }

    private fun selectEndDate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val maxDate = Long.MAX_VALUE
            Utils.selectDate3(
                this,
                editTrainingPlanBinding.edtEndDate,
                startDateMillis,
                maxDate
            ) { dateMillis ->
                endDateMillis = dateMillis
                val formattedDate = formatDate(dateMillis)
                editTrainingPlanBinding.edtEndDate.setText(formattedDate)
            }
        }
    }

    private fun selectTrainingPlanStartDate(editText: AppCompatEditText) {

        val mainStartDate = editTrainingPlanBinding.edtStartDate.text.toString()
        val maxStartDate = editTrainingPlanBinding.edtEndDate.text.toString()

        val maxStartDateMillis =
            if (maxStartDate.isNotEmpty()) formatDateToMillis(maxStartDate) else Long.MAX_VALUE

        val minDateMillis =
            if (mainStartDate.isNotEmpty()) formatDateToMillis(mainStartDate) else System.currentTimeMillis()

        Utils.selectDate3(this, editText, minDateMillis, maxStartDateMillis) { dateMillis ->
            startDateMillis = dateMillis
            val formattedDate = formatDate(dateMillis)
            editText.setText(formattedDate)
        }
    }

    private fun selectTrainingPlanEndDate(editText: AppCompatEditText) {
        val mainEndDate = editTrainingPlanBinding.edtEndDate.text.toString()

        Utils.selectDate3(
            this,
            editText,
            if (startDateMillis > 0) startDateMillis else System.currentTimeMillis(),
            if (mainEndDate.isNotEmpty()) formatDateToMillis(mainEndDate) else Long.MAX_VALUE
        ) { dateMillis ->
            endDateMillis = dateMillis
            val formattedDate = formatDate(dateMillis)
            editText.setText(formattedDate)
        }
    }

    private fun formatDateToMillis(dateString: String): Long {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return format.parse(dateString)?.time ?: Long.MAX_VALUE
    }

    private fun formatDate(dateMillis: Long): String {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return format.format(Date(dateMillis))
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