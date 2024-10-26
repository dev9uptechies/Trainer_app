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

    private var startdatesent:String? = null
    private var enddatesent:String? = null

    private var startdatesentlist:String? = null
    private var enddatesentlist:String? = null

    private var competitive: Competitive = Competitive("", "", "", "")
    private var preSeason = PreSeason("", "", "", "")
    private var transition = Transition("", "", "", "")
    private var preCompetitive: PreCompetitive = PreCompetitive("", "", "", "")


    var errorstartdate:String?= null
    var errorenddate:String?= null
    var errorstartdatelist:String?= null
    var errorenddatelist:String?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        editTrainingPlanBinding = ActivityEditTrainingPlanBinding.inflate(layoutInflater)
        setContentView(editTrainingPlanBinding.root)
        initView()
        loadData()
        checkButtonClick()
        textWatcher()
        setupTextWatcher()
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
                val mesocycle = mesocycles.text.toString().trim()

                if (name.isEmpty() && startdatesentlist!!.isEmpty() && enddatesentlist!!.isEmpty()) {
                    continue
                }
                if (name.isEmpty()) {
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

                val finalStartDatelist = if (startdatesentlist.isNullOrEmpty()) errorstartdatelist.toString() else startdatesentlist
                val finalEndDatelist = if (enddatesentlist.isNullOrEmpty()) errorenddatelist.toString() else enddatesentlist

                when (i) {
                    0 -> preSeason = PreSeason(
                        name = name,
                        start_date = finalStartDatelist.toString(),
                        end_date = finalEndDatelist.toString(),
                        mesocycle = mesocycle.toString()
                    )

                    1 -> preCompetitive = PreCompetitive(
                        name = name,
                        start_date = finalStartDatelist.toString(),
                        end_date = finalEndDatelist.toString(),
                        mesocycle = mesocycle.toString()
                    )

                    2 -> competitive = Competitive(
                        name = name,
                        start_date = finalStartDatelist.toString(),
                        end_date = finalEndDatelist.toString(),
                        mesocycle = mesocycle.toString()
                    )

                    3 -> transition = Transition(
                        name = name,
                        start_date = finalStartDatelist.toString(),
                        end_date = finalEndDatelist.toString(),
                        mesocycle = mesocycle.toString()
                    )
                }


                val programName = editTrainingPlanBinding.edtProgramName.text.toString().trim()
//                val startDate = editTrainingPlanBinding.edtStartDate.text.toString().trim()
//                val endDate = editTrainingPlanBinding.edtEndDate.text.toString().trim()

                if (programName.isEmpty()) {
                    Toast.makeText(
                        this,
                        "Please fill in the program name, start date, and end date",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }


                val finalStartDate = if (startdatesent.isNullOrEmpty()) errorstartdate.toString() else startdatesent
                val finalEndDate = if (enddatesent.isNullOrEmpty()) errorenddate.toString() else enddatesent

                Log.d("final", "EditeProgramData: " + finalEndDate +"   " + finalStartDate)
                Log.d("finaldate", "EditeProgramData: " + errorstartdate +"   " + errorenddate)

                val trainingPlanRequest = TrainingPlanSubClass(
                    id = id!!,
                    name = editTrainingPlanBinding.edtProgramName.text.toString(),
                    competition_date = finalEndDate.toString(),
                    start_date = finalStartDate.toString(),
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

        val startDateMillis = formatDateToMillis2(data.start_date)
        val formattedStartDate = formatMillisToDateString(startDateMillis)

        val endDateInMillis = formatDateToMillis2(data.competition_date)
        val formattedEndDate = formatMillisToDateString(endDateInMillis)

        editTrainingPlanBinding.edtProgramName.setText(data.name)
        editTrainingPlanBinding.edtStartDate.setText(formattedStartDate)
        editTrainingPlanBinding.edtEndDate.setText(formattedEndDate)
        editTrainingPlanBinding.days.text = data.mesocycle

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

    private fun addTrainingPlanView(
        name: String,
        startDate: String,
        endDate: String,
        mesocycle: String,
        phaseType: String
    ) {
        trainingPlanCount++
        val trainingPlanView = layoutInflater.inflate(R.layout.add_training_plan_list, null)

        val nameEditText: AppCompatEditText = trainingPlanView.findViewById(R.id.ent_pre_sea_name)
        val startDateEditText: AppCompatEditText = trainingPlanView.findViewById(R.id.ent_start_date_liner)
        val endDateEditText: AppCompatEditText = trainingPlanView.findViewById(R.id.ent_ent_date_liner)
        val delete: ImageView = trainingPlanView.findViewById(R.id.img_delete)
        val startdatecard: CardView = trainingPlanView.findViewById(R.id.card_start_date_list)
        val enddatecard: CardView = trainingPlanView.findViewById(R.id.card_end_pick_list)
        val mesocycles: TextView = trainingPlanView.findViewById(R.id.linear_days_list)


        // Initialize fields safely

        val startDateMillis = formatDateToMillis2(startDate)
        val formattedStartDate = formatMillisToDateString(startDateMillis)

        val endDateInMillis = formatDateToMillis2(endDate)
        val formattedEndDate = formatMillisToDateString(endDateInMillis)

        nameEditText.setText(name)
        startDateEditText.setText(formattedStartDate)
        endDateEditText.setText(formattedEndDate)
        mesocycles.setText(mesocycle)

        val errorStartDateMillis = parseFormattedDateToMillis(formattedStartDate)
        val errorEndDateMillis = parseFormattedDateToMillis(formattedEndDate)

        errorstartdatelist = formatDate(errorStartDateMillis)
        errorenddatelist = formatDate(errorEndDateMillis)

        Log.d("startdass", "addTrainingPlanView: "+ errorstartdatelist +"   "+ errorenddatelist)

        val updateDaysTextViewlist = {
            val startDate = startDateEditText.text.toString()
            val endDate = endDateEditText.text.toString()

            if (startDate.isNotEmpty() && endDate.isNotEmpty()) {
                val days = calculateDaysBetweenDates(startdatesentlist.toString(), enddatesentlist.toString())
                mesocycles.text = "$days days"
            } else {
                mesocycles.text = "Select dates"
            }
        }

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
            selectTrainingPlanStartDate(startDateEditText)
            updateDaysTextViewlist()
        }

        endDateEditText.setOnClickListener {
            selectTrainingPlanEndDate(endDateEditText)
            updateDaysTextViewlist()
        }

        startDateEditText.setOnFocusChangeListener { _, _ -> updateDaysTextViewlist() }
        endDateEditText.setOnFocusChangeListener { _, _ -> updateDaysTextViewlist() }
        startdatecard.setOnFocusChangeListener { _, _ -> updateDaysTextViewlist() }
        enddatecard.setOnFocusChangeListener { _, _ -> updateDaysTextViewlist() }
        mesocycles.setOnFocusChangeListener { _, _ -> updateDaysTextViewlist() }

        trainingPlanContainer.addView(trainingPlanView)
        trainingPlanLayouts.add(trainingPlanView)

        Log.d("AddTrainingPlan", "Training plan added for phase: $phaseType")
    }

    private fun formatDate(dateMillis: Long): String {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return format.format(Date(dateMillis))
    }

    private fun formatDate2(dateMillis: Long): String {
        val format = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
        return format.format(Date(dateMillis))
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
//                updateUI(addTrainingPlanBinding.cardSave)
            }
        })

        editTrainingPlanBinding.edtEndDate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateDaysTextView()
//                updateUI(addTrainingPlanBinding.cardSave)
            }
        })
    }
    private fun updateDaysTextView() {
        val startDate = editTrainingPlanBinding.edtStartDate.text.toString()
        val endDate = editTrainingPlanBinding.edtEndDate.text.toString()

        if (startDate.isNotEmpty() && endDate.isNotEmpty()) {
            val days = calculateDaysBetweenDates(startdatesent.toString(), enddatesent.toString())
            editTrainingPlanBinding.days.text = "$days Days"
        } else {
            editTrainingPlanBinding.days.text = "0 Days"
        }
    }
    private fun calculateDaysBetweenDates(start: String, end: String): Int {
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
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
                startDateMillis = dateMillis
                val formattedDateDisplay = formatDate2(dateMillis)

                startdatesent = formatDate(dateMillis)
                editTrainingPlanBinding.edtStartDate.setText(formattedDateDisplay)
                endDateMillis = 0
            }
        }
    }

    // Method to select the end date for the training plan
    private fun selectEndDate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val maxDateMillis = Long.MAX_VALUE // Set max date to infinity
            val minDateMillis = if (startDateMillis > 0) startDateMillis else System.currentTimeMillis() // Use the start date or current time

            Utils.selectDate3(
                this,
                editTrainingPlanBinding.edtEndDate,
                minDateMillis,
                maxDateMillis
            ) { dateMillis ->
                endDateMillis = dateMillis
                val formattedDateDisplay = formatDate2(dateMillis)

                enddatesent = formatDate(dateMillis)
                editTrainingPlanBinding.edtEndDate.setText(formattedDateDisplay)
            }
        }
    }

    private fun selectTrainingPlanStartDate(editText: AppCompatEditText) {
        val minDateMillis = if (startDateMillis > 0) startDateMillis else System.currentTimeMillis()
        Utils.selectDate3(this, editText, minDateMillis, Long.MAX_VALUE) { dateMillis ->
            startDateMillis = dateMillis
            val formattedDateDisplay = formatDate2(dateMillis)
            startdatesentlist = formatDate(dateMillis)
            editText.setText(formattedDateDisplay)
        }
    }

    private fun selectTrainingPlanEndDate(editText: AppCompatEditText) {
        val minDateMillis = if (startDateMillis > 0) startDateMillis else System.currentTimeMillis()
        Utils.selectDate3(this, editText, minDateMillis, Long.MAX_VALUE) { dateMillis ->
            endDateMillis = dateMillis
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