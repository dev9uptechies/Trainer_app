package com.example.trainerapp.training_plan.view_planning_cycle

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.example.model.training_plan.cycles.AddMesocycleCompatitive
import com.example.model.training_plan.cycles.AddMesocyclePreCompatitive
import com.example.model.training_plan.cycles.AddMesocyclePresession
import com.example.model.training_plan.cycles.AddMesocycleTransition
import com.example.model.training_plan.cycles.GetMessocyclePreSession
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.PreferencesManager
import com.example.trainerapp.R
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.ActivityEditMesocycleListBinding
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

class EditMesocycleListActivity : AppCompatActivity() {
    lateinit var editMesocycleListBinding: ActivityEditMesocycleListBinding
    private lateinit var apiInterface: APIInterface
    private lateinit var preferenceManager: PreferencesManager
    private lateinit var apiClient: APIClient


    private val dateRangeMap = mutableMapOf<Int, Pair<String, String>>()
    private var activeLayoutIndex: Int? = null
    private val selectedDateRanges = mutableListOf<Pair<Long, Long>>()
    private lateinit var trainingPlanContainer: LinearLayout
    private var programData: MutableList<GetMessocyclePreSession.Data> = mutableListOf()
    private var id: Int = 0
    private var seasonid: Int = 0
    private var mainId: Int = 0
    private var startdate: String? = null
    private var endDate: String? = null
    private var cardType: String? = null
    private val missingIndices = mutableListOf<Int>()
    var size = 0

    private val trainingPlanLayouts = mutableListOf<View>()

    private var preSeason: MutableList<AddMesocyclePresession> = mutableListOf()
    private var preCompatitive: MutableList<AddMesocyclePreCompatitive> = mutableListOf()
    private var Compatitive: MutableList<AddMesocycleCompatitive> = mutableListOf()
    private var Transition: MutableList<AddMesocycleTransition> = mutableListOf()

    private var startDateMillis: Long = 0
    private var endDateMillis: Long = 0

    private var startdatesent: String? = null
    private var enddatesent: String? = null

    var startDateInMillis: String? = null
    var formattedStartDate: String? = null

    var errorstartdate: String? = null
    var errorenddate: String? = null


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
                        Utils.setUnAuthDialog(this@EditMesocycleListActivity)
                    } else {
                        Toast.makeText(
                            this@EditMesocycleListActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Toast.makeText(
                        this@EditMesocycleListActivity,
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
        editMesocycleListBinding = ActivityEditMesocycleListBinding.inflate(layoutInflater)
        setContentView(editMesocycleListBinding.root)
        initViews()
        loadData()
        checkButtonClick()
    }

    private fun loadData() {
        try {
            programData.clear()
            when (cardType) {

                "pre_session" -> {
                    getPreSessionData(seasonid)
                }

                "pre_competitive" -> {
                    getPreCompatiiveData(seasonid)
                }

                "competitive" -> {
                    getCompatiiveData(seasonid)
                }

                "transition" -> {
                    getTransitionData(seasonid)
                }

                else -> {
                    Toast.makeText(this, "invalid card type", Toast.LENGTH_SHORT).show()
                }
            }


        } catch (e: Exception) {
            Log.d("errror", e.message.toString())
        }
    }

    private fun checkButtonClick() {
        editMesocycleListBinding.add.setOnClickListener {
            addTrainingPlan()
        }

        editMesocycleListBinding.back.setOnClickListener {
            finish()
        }

        editMesocycleListBinding.cardSave.setOnClickListener {

            try {
                programData.clear()

                when (cardType) {

                    "pre_session" -> {
                        editMesocyclePresession()
                    }

                    "pre_competitive" -> {
                        editMesocyclePreCompatitive()
                    }

                    "competitive" -> {
                        editMesocycleCompatitive()
                    }

                    "transition" -> {
                        editMesocycleTransition()
                    }

                    else -> {
                        Toast.makeText(this, "invalid card type", Toast.LENGTH_SHORT).show()
                    }
                }


            } catch (e: Exception) {
                Log.d("errror", e.message.toString())
            }

        }
    }

    private fun initViews() {
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        preferenceManager = PreferencesManager(this)

        seasonid = intent.getIntExtra("SeasonId", 0)
        mainId = intent.getIntExtra("mainId", 0)
        startdate = intent.getStringExtra("StartDate")
        endDate = intent.getStringExtra("EndDate")
        cardType = intent.getStringExtra("CardType")

        Log.d(
            "EditeMesocycle",
            "Id: $seasonid, MainId: $mainId, StatDate: $startdate, EndDate: $endDate, CardType: $cardType"
        )
        editMesocycleListBinding.startDate.text = startdate
        editMesocycleListBinding.endDate.text = endDate

        trainingPlanContainer = editMesocycleListBinding.linerAddTraining
    }

    private fun editMesocyclePresession() {
        try {

            var plan1StartDate = ""
            var plan1EndDate = ""

            Log.d(
                "EditeData",
                "Child count in trainingPlanContainer: ${trainingPlanContainer.childCount}"
            )

            if (trainingPlanContainer.childCount == 0) {
                Toast.makeText(this, "No data to save.", Toast.LENGTH_SHORT).show()
                return
            }
            preSeason.clear()

            for (i in 0 until trainingPlanContainer.childCount) {
                val layout = trainingPlanContainer.getChildAt(i)
                val nameEditText: AppCompatEditText? = layout.findViewById(R.id.ent_pre_sea_name)
                val startDateEditText: AppCompatEditText? =
                    layout.findViewById(R.id.ent_start_date_liner)
                val endDateEditText: AppCompatEditText? =
                    layout.findViewById(R.id.ent_ent_date_liner)
                val mesocycles: TextView? = layout.findViewById(R.id.linear_days_list)
                val errorTextView: TextView = layout.findViewById(R.id.error_start_date_list)


                val name = nameEditText?.text.toString().trim()
                val start = startDateEditText?.text.toString().trim()
                val end = endDateEditText?.text.toString().trim()

                if (name.isEmpty()) {
                    Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show()
                    return
                }

                val newName = "Mesocycle ${i + 1}"

                if (preSeason.any { it.name == newName }) {
                    Toast.makeText(
                        this,
                        "Name already exists, please use a different name.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }

                Log.d("Data", "Name: $name, Start: $startdatesent, End: $enddatesent")



                val startDateMillis = formatDateToMillistest(start)
                val endDateMillis = formatDateToMillistest(end)

                val formattedStartDate = formatMillisToDateString(startDateMillis)
                val formattedEndDate = formatMillisToDateString(endDateMillis)

                val errorStartDateMillis = parseFormattedDateToMillis(formattedStartDate)
                val errorTestDateMillis = parseFormattedDateToMillis(formattedEndDate)

                val finalStartDates = formatDate(errorStartDateMillis)
                val finalEndDates = formatDate(errorTestDateMillis)


                Log.d("DEBUUUUUG", "Final formatted date: $finalStartDates    end:- $finalEndDates")


                try {
                    val finalStartDate = if (finalStartDates.isNullOrEmpty()) errorstartdate.toString() else finalStartDates
                    val finalEndDate = if (finalEndDates.isNullOrEmpty()) errorenddate.toString() else finalEndDates

                    Log.d("FKFKFKFKKK", "editMesocyclePresession: $finalStartDate    $finalEndDate")


                    if (i == 0) {
                        plan1StartDate = start
                        plan1EndDate = end
                    }

                    if (i != 0) {
                        if (isConflict(start, end, i)) {
                            errorTextView.visibility = View.VISIBLE
                            errorTextView.text = "Date conflict detected for Plan ${i + 1}"
                            errorTextView.setTextColor(Color.RED)
                            return
                        } else {
                            errorTextView.visibility = View.GONE
                        }
                    }


                    if (start == "" || start.isNullOrEmpty()){
                        Toast.makeText(this, "Please Fill All Filed", Toast.LENGTH_SHORT).show()
                        return
                    }
                    if (end == "" || end.isNullOrEmpty()){
                        Toast.makeText(this, "Please Fill All Filed", Toast.LENGTH_SHORT).show()
                        return
                    }
                    if (finalStartDate == finalEndDate){
                        return
                    }else{
                    }


                    preSeason.add(
                        AddMesocyclePresession(
                            id = if (i < programData.size) programData[i].id else null,
                            planning_ps_id = seasonid.toString(),
                            name = newName,
                            start_date = finalStartDate.toString(),
                            end_date = finalEndDate.toString(),
//                            periods = ""
                        )
                    )

                } catch (e: Exception) {
                    Log.d("ErrorForCrash", e.message.toString())
                }
            }

            editMesocycleListBinding.progresBar.visibility = View.VISIBLE
            apiInterface.EditeMesocyclePresession(preSeason)
                ?.enqueue(object : Callback<GetMessocyclePreSession> {
                    override fun onResponse(
                        call: Call<GetMessocyclePreSession>,
                        response: Response<GetMessocyclePreSession>
                    ) {
                        Log.d("TAG", "Response Code: ${response.code()}")
                        Log.d("TAG1", "Response Message: ${response.message()}")
                        editMesocycleListBinding.progresBar.visibility = View.GONE
                        Log.d("TAG", response.code().toString() + "")
                        val code = response.code()
                        if (code == 200) {
                            if (response.isSuccessful) {
                                response.body()?.let { responseBody ->
                                    if (responseBody.status == true) {
                                        Toast.makeText(
                                            this@EditMesocycleListActivity,
                                            "Success: ${responseBody.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        finish()
                                    } else {
                                        val errorMessage =
                                            responseBody.message ?: "Something went wrong"
                                        Log.e("TAG2", "Error: $errorMessage")
                                        Toast.makeText(
                                            this@EditMesocycleListActivity,
                                            errorMessage,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } ?: run {
                                    Log.e("TAG2", "Response body is null")
                                    Toast.makeText(
                                        this@EditMesocycleListActivity,
                                        "Unexpected response",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                                Log.e("TAG4", "Error Response: $errorBody")
                                Toast.makeText(
                                    this@EditMesocycleListActivity,
                                    "Error: $errorBody",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else if (code == 403) {
                            Utils.setUnAuthDialog(this@EditMesocycleListActivity)
                        } else {
                            Toast.makeText(
                                this@EditMesocycleListActivity,
                                "" + response.message(),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            call.cancel()
                        }
                    }

                    override fun onFailure(call: Call<GetMessocyclePreSession>, t: Throwable) {
                        editMesocycleListBinding.progresBar.visibility = View.GONE
                        Log.e("TAG3", "onResponse failure: ${t.message}", t)
                        Toast.makeText(
                            this@EditMesocycleListActivity,
                            "Network error: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })

        } catch (e: Exception) {
            Log.d("TAG", "editMesocyclePresession: ${e.message}")
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



    private fun editMesocyclePreCompatitive() {
        try {
            Log.d(
                "EditeData",
                "Child count in trainingPlanContainer: ${trainingPlanContainer.childCount}"
            )

            if (trainingPlanContainer.childCount == 0) {
                Toast.makeText(this, "No data to save.", Toast.LENGTH_SHORT).show()
                return
            }
            preCompatitive.clear()

            // Loop through each child in trainingPlanContainer
            for (i in 0 until trainingPlanContainer.childCount) {
                val layout = trainingPlanContainer.getChildAt(i)
                val nameEditText: AppCompatEditText? = layout.findViewById(R.id.ent_pre_sea_name)
                val startDateEditText: AppCompatEditText? =
                    layout.findViewById(R.id.ent_start_date_liner)
                val endDateEditText: AppCompatEditText? =
                    layout.findViewById(R.id.ent_ent_date_liner)
                val mesocycles: TextView? = layout.findViewById(R.id.linear_days_list)

                val name = nameEditText?.text.toString().trim()
                val start = startDateEditText?.text.toString().trim()
                val end = endDateEditText?.text.toString().trim()




                if (name.isEmpty() || start.isEmpty() || end.isEmpty()) {
                    Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show()
                    return
                }

                val newName = "Mesocycle ${i + 1}" // This will format names as Mesocycle 1, 2, 3...

                // Ensure the newName is unique
                if (preCompatitive.any { it.name == newName }) {
                    Toast.makeText(
                        this,
                        "Name already exists, please use a different name.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }


                val startDateMillis = formatDateToMillistest(start)
                val endDateMillis = formatDateToMillistest(end)

                val formattedStartDate = formatMillisToDateString(startDateMillis)
                val formattedEndDate = formatMillisToDateString(endDateMillis)

                val errorStartDateMillis = parseFormattedDateToMillis(formattedStartDate)
                val errorTestDateMillis = parseFormattedDateToMillis(formattedEndDate)

                val finalStartDates = formatDate(errorStartDateMillis)
                val finalEndDates = formatDate(errorTestDateMillis)
                val errorTextView: TextView = layout.findViewById(R.id.error_start_date_list)



                Log.d("DEBUUUUUG", "Final formatted date: $finalStartDates    end:- $finalEndDates")


                    val finalStartDate = if (finalStartDates.isNullOrEmpty()) errorstartdate.toString() else finalStartDates
                    val finalEndDate = if (finalEndDates.isNullOrEmpty()) errorenddate.toString() else finalEndDates

                    Log.d("FKFKFKFKKK", "editMesocyclePresession: $finalStartDate    $finalEndDate")


                    if (i != 0) {
                        if (isConflict(start, end, i)) {
                            errorTextView.visibility = View.VISIBLE
                            errorTextView.text = "Date conflict detected for Plan ${i + 1}"
                            errorTextView.setTextColor(Color.RED)
                            return
                        } else {
                            errorTextView.visibility = View.GONE
                        }
                    }


                if (start == "" || start.isNullOrEmpty()){
                    Toast.makeText(this, "Please Fill All Filed", Toast.LENGTH_SHORT).show()
                    return
                }
                if (end == "" || end.isNullOrEmpty()){
                    Toast.makeText(this, "Please Fill All Filed", Toast.LENGTH_SHORT).show()
                    return
                }

                    if (finalStartDate == finalEndDate){
                        return
                    }else{
                    }
                preCompatitive.add(
                    AddMesocyclePreCompatitive(
                        id = if (i < programData.size) programData[i].id else null,
                        planning_pc_id = seasonid.toString(),
                        name = newName,
                        start_date = finalStartDate.toString(),
                        end_date = finalEndDate.toString(),
                        periods = "test"
                    )
                )
            }

            editMesocycleListBinding.progresBar.visibility = View.VISIBLE
            apiInterface.EditeMesocyclePreCompatitive(preCompatitive)
                ?.enqueue(object : Callback<GetMessocyclePreSession> {
                    override fun onResponse(
                        call: Call<GetMessocyclePreSession>,
                        response: Response<GetMessocyclePreSession>
                    ) {
                        Log.d("TAG", "Response Code: ${response.code()}")
                        Log.d("TAG1", "Response Message: ${response.message()}")
                        editMesocycleListBinding.progresBar.visibility = View.GONE

                        Log.d("TAG", response.code().toString() + "")
                        val code = response.code()
                        if (code == 200) {
                            if (response.isSuccessful) {
                                response.body()?.let { responseBody ->
                                    if (responseBody.status == true) {
                                        Toast.makeText(
                                            this@EditMesocycleListActivity,
                                            "Success: ${responseBody.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        finish()

                                    } else {
                                        val errorMessage =
                                            responseBody.message ?: "Something went wrong"
                                        Log.e("TAG2", "Error: $errorMessage")
                                        Toast.makeText(
                                            this@EditMesocycleListActivity,
                                            errorMessage,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } ?: run {
                                    Log.e("TAG2", "Response body is null")
                                    Toast.makeText(
                                        this@EditMesocycleListActivity,
                                        "Unexpected response",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                                Log.e("TAG4", "Error Response: $errorBody")
                                Toast.makeText(
                                    this@EditMesocycleListActivity,
                                    "Error: $errorBody",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else if (code == 403) {
                            Utils.setUnAuthDialog(this@EditMesocycleListActivity)
                        } else {
                            Toast.makeText(
                                this@EditMesocycleListActivity,
                                "" + response.message(),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            call.cancel()
                        }


                    }

                    override fun onFailure(call: Call<GetMessocyclePreSession>, t: Throwable) {
                        editMesocycleListBinding.progresBar.visibility = View.GONE
                        Log.e("TAG3", "onResponse failure: ${t.message}", t)
                        Toast.makeText(
                            this@EditMesocycleListActivity,
                            "Network error: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })

        } catch (e: Exception) {
            Log.d("TAG", "editMesocyclePresession: ${e.message}")
        }
    }

    private fun editMesocycleCompatitive() {
        try {
            Log.d(
                "EditeData",
                "Child count in trainingPlanContainer: ${trainingPlanContainer.childCount}"
            )

            if (trainingPlanContainer.childCount == 0) {
                Toast.makeText(this, "No data to save.", Toast.LENGTH_SHORT).show()
                return
            }
            Compatitive.clear()



            // Loop through each child in trainingPlanContainer
            for (i in 0 until trainingPlanContainer.childCount) {
                val layout = trainingPlanContainer.getChildAt(i)
                val nameEditText: AppCompatEditText? = layout.findViewById(R.id.ent_pre_sea_name)
                val startDateEditText: AppCompatEditText? =
                    layout.findViewById(R.id.ent_start_date_liner)
                val endDateEditText: AppCompatEditText? =
                    layout.findViewById(R.id.ent_ent_date_liner)
                val mesocycles: TextView? = layout.findViewById(R.id.linear_days_list)

                val name = nameEditText?.text.toString().trim()
                val start = startDateEditText?.text.toString().trim()
                val end = endDateEditText?.text.toString().trim()

                if (name.isEmpty() || start.isEmpty() || end.isEmpty()) {
                    Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show()
                    return
                }


                val newName = "Mesocycle ${i + 1}"

                if (Compatitive.any { it.name == newName }) {
                    Toast.makeText(
                        this,
                        "Name already exists, please use a different name.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                val startDateMillis = formatDateToMillistest(start)
                val endDateMillis = formatDateToMillistest(end)

                val formattedStartDate = formatMillisToDateString(startDateMillis)
                val formattedEndDate = formatMillisToDateString(endDateMillis)

                val errorStartDateMillis = parseFormattedDateToMillis(formattedStartDate)
                val errorTestDateMillis = parseFormattedDateToMillis(formattedEndDate)

                val finalStartDates = formatDate(errorStartDateMillis)
                val finalEndDates = formatDate(errorTestDateMillis)
                val errorTextView: TextView = layout.findViewById(R.id.error_start_date_list)



                Log.d("DEBUUUUUG", "Final formatted date: $finalStartDates    end:- $finalEndDates")


                val finalStartDate = if (finalStartDates.isNullOrEmpty()) errorstartdate.toString() else finalStartDates
                val finalEndDate = if (finalEndDates.isNullOrEmpty()) errorenddate.toString() else finalEndDates

                Log.d("FKFKFKFKKK", "editMesocyclePresession: $finalStartDate    $finalEndDate")


                if (i != 0) {
                    if (isConflict(start, end, i)) {
                        errorTextView.visibility = View.VISIBLE
                        errorTextView.text = "Date conflict detected for Plan ${i + 1}"
                        errorTextView.setTextColor(Color.RED)
                        return
                    } else {
                        errorTextView.visibility = View.GONE
                    }
                }


                if (start == "" || start.isNullOrEmpty()){
                    Toast.makeText(this, "Please Fill All Filed", Toast.LENGTH_SHORT).show()
                    return
                }
                if (end == "" || end.isNullOrEmpty()){
                    Toast.makeText(this, "Please Fill All Filed", Toast.LENGTH_SHORT).show()
                    return
                }

                if (finalStartDate == finalEndDate){
                    return
                }else{
                }

                Compatitive.add(
                    AddMesocycleCompatitive(
                        id = if (i < programData.size) programData[i].id else null,
                        planning_c_id = seasonid.toString(),
                        name = newName,
                        start_date = finalStartDate.toString(),
                        end_date = finalEndDate.toString(),
                        periods = "test"
                    )
                )
            }
            editMesocycleListBinding.progresBar.visibility = View.VISIBLE
            apiInterface.EditeMesocycleCompatitive(Compatitive)
                ?.enqueue(object : Callback<GetMessocyclePreSession> {
                    override fun onResponse(
                        call: Call<GetMessocyclePreSession>,
                        response: Response<GetMessocyclePreSession>
                    ) {
                        Log.d("TAG", "Response Code: ${response.code()}")
                        Log.d("TAG1", "Response Message: ${response.message()}")
                        editMesocycleListBinding.progresBar.visibility = View.GONE

                        Log.d("TAG", response.code().toString() + "")
                        val code = response.code()
                        if (code == 200) {
                            if (response.isSuccessful) {
                                response.body()?.let { responseBody ->
                                    if (responseBody.status == true) {
                                        Toast.makeText(
                                            this@EditMesocycleListActivity,
                                            "Success: ${responseBody.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    } else {
                                        val errorMessage =
                                            responseBody.message ?: "Something went wrong"
                                        Log.e("TAG2", "Error: $errorMessage")
                                        Toast.makeText(
                                            this@EditMesocycleListActivity,
                                            errorMessage,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } ?: run {
                                    Log.e("TAG2", "Response body is null")
                                    Toast.makeText(
                                        this@EditMesocycleListActivity,
                                        "Unexpected response",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                                Log.e("TAG4", "Error Response: $errorBody")
                                Toast.makeText(
                                    this@EditMesocycleListActivity,
                                    "Error: $errorBody",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else if (code == 403) {
                            Utils.setUnAuthDialog(this@EditMesocycleListActivity)
                        } else {
                            Toast.makeText(
                                this@EditMesocycleListActivity,
                                "" + response.message(),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            call.cancel()
                        }
                    }

                    override fun onFailure(call: Call<GetMessocyclePreSession>, t: Throwable) {
                        editMesocycleListBinding.progresBar.visibility = View.GONE
                        Log.e("TAG3", "onResponse failure: ${t.message}", t)
                        Toast.makeText(
                            this@EditMesocycleListActivity,
                            "Network error: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })

        } catch (e: Exception) {
            Log.d("TAG", "editMesocyclePresession: ${e.message}")
        }
    }

    private fun editMesocycleTransition() {
        try {
            Log.d(
                "EditeData",
                "Child count in trainingPlanContainer: ${trainingPlanContainer.childCount}"
            )

            if (trainingPlanContainer.childCount == 0) {
                Toast.makeText(this, "No data to save.", Toast.LENGTH_SHORT).show()
                return
            }
            Transition.clear()

            // Loop through each child in trainingPlanContainer
            for (i in 0 until trainingPlanContainer.childCount) {
                val layout = trainingPlanContainer.getChildAt(i)
                val nameEditText: AppCompatEditText? = layout.findViewById(R.id.ent_pre_sea_name)
                val startDateEditText: AppCompatEditText? = layout.findViewById(R.id.ent_start_date_liner)
                val endDateEditText: AppCompatEditText? = layout.findViewById(R.id.ent_ent_date_liner)
                val mesocycles: TextView? = layout.findViewById(R.id.linear_days_list)

                val name = nameEditText?.text.toString().trim()
                val start = startDateEditText?.text.toString().trim()
                val end = endDateEditText?.text.toString().trim()

                // Check for empty fields
                if (name.isEmpty() || startdatesent!!.isEmpty() || enddatesent!!.isEmpty()) {
                    Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show()
                    return
                }


                val newName = "Mesocycle ${i + 1}"

                // Ensure the newName is unique
                if (Transition.any { it.name == newName }) {
                    Toast.makeText(
                        this,
                        "Name already exists, please use a different name.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }

                Log.d("Data", "Name: $name, Start: $startdatesent, End: $enddatesent")


                val startDateMillis = formatDateToMillistest(start)
                val endDateMillis = formatDateToMillistest(end)

                val formattedStartDate = formatMillisToDateString(startDateMillis)
                val formattedEndDate = formatMillisToDateString(endDateMillis)

                val errorStartDateMillis = parseFormattedDateToMillis(formattedStartDate)
                val errorTestDateMillis = parseFormattedDateToMillis(formattedEndDate)

                val finalStartDates = formatDate(errorStartDateMillis)
                val finalEndDates = formatDate(errorTestDateMillis)
                val errorTextView: TextView = layout.findViewById(R.id.error_start_date_list)


                Log.d("DEBUUUUUG", "Final formatted date: $finalStartDates    end:- $finalEndDates")


                val finalStartDate = if (finalStartDates.isNullOrEmpty()) errorstartdate.toString() else finalStartDates
                val finalEndDate = if (finalEndDates.isNullOrEmpty()) errorenddate.toString() else finalEndDates

                Log.d("FKFKFKFKKK", "editMesocyclePresession: $finalStartDate    $finalEndDate")


                if (i != 0) {
                    if (isConflict(start, end, i)) {
                        errorTextView.visibility = View.VISIBLE
                        errorTextView.text = "Date conflict detected for Plan ${i + 1}"
                        errorTextView.setTextColor(Color.RED)
                        return
                    } else {
                        errorTextView.visibility = View.GONE
                    }
                }

                if (start == "" || start.isNullOrEmpty()){
                    Toast.makeText(this, "Please Fill All Filed", Toast.LENGTH_SHORT).show()
                    return
                }
                if (end == "" || end.isNullOrEmpty()){
                    Toast.makeText(this, "Please Fill All Filed", Toast.LENGTH_SHORT).show()
                    return
                }

                if (finalStartDate == finalEndDate){
                    return
                }else{
                }


                Transition.add(
                    AddMesocycleTransition(
                        id = if (i < programData.size) programData[i].id else null,
                        planning_t_id = seasonid.toString(),
                        name = newName,
                        start_date = finalStartDate.toString(),
                        end_date = finalEndDate.toString(),
                        periods = "test"
                    )
                )
            }
            editMesocycleListBinding.progresBar.visibility = View.VISIBLE
            apiInterface.EditeMesocycleTransition(Transition)
                ?.enqueue(object : Callback<GetMessocyclePreSession> {
                    override fun onResponse(
                        call: Call<GetMessocyclePreSession>,
                        response: Response<GetMessocyclePreSession>
                    ) {
                        Log.d("TAG", "Response Code: ${response.code()}")
                        Log.d("TAG1", "Response Message: ${response.message()}")

                        editMesocycleListBinding.progresBar.visibility = View.GONE
                        Log.d("TAG", response.code().toString() + "")
                        val code = response.code()
                        if (code == 200) {
                            if (response.isSuccessful) {
                                response.body()?.let { responseBody ->
                                    if (responseBody.status == true) {
                                        Toast.makeText(
                                            this@EditMesocycleListActivity,
                                            "Success: ${responseBody.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        finish()
//                                    startActivity(
//                                        Intent(
//                                            this@EditMesocycleListActivity,
//                                            ViewTraingPlanList::class.java
//                                        )
//                                    )

                                    } else {
                                        val errorMessage =
                                            responseBody.message ?: "Something went wrong"
                                        Log.e("TAG2", "Error: $errorMessage")
                                        Toast.makeText(
                                            this@EditMesocycleListActivity,
                                            errorMessage,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } ?: run {
                                    Log.e("TAG2", "Response body is null")
                                    Toast.makeText(
                                        this@EditMesocycleListActivity,
                                        "Unexpected response",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                                Log.e("TAG4", "Error Response: $errorBody")
                                Toast.makeText(
                                    this@EditMesocycleListActivity,
                                    "Error: $errorBody",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else if (code == 403) {
                            Utils.setUnAuthDialog(this@EditMesocycleListActivity)
                        } else {
                            Toast.makeText(
                                this@EditMesocycleListActivity,
                                "" + response.message(),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            call.cancel()
                        }

                    }

                    override fun onFailure(call: Call<GetMessocyclePreSession>, t: Throwable) {
                        editMesocycleListBinding.progresBar.visibility = View.GONE
                        Log.e("TAG3", "onResponse failure: ${t.message}", t)
                        Toast.makeText(
                            this@EditMesocycleListActivity,
                            "Network error: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })

        } catch (e: Exception) {
            Log.d("TAG", "editMesocyclePresession: ${e.message}")
        }
    }

    private fun addTrainingPlan(mesocycle: GetMessocyclePreSession.Data? = null) {
        try {
            val indexToAdd = if (missingIndices.isNotEmpty()) {
                val reusedIndex = missingIndices.removeAt(0)
                Log.d("AddTrainingPlan", "Reusing missing index: $reusedIndex")
                reusedIndex
            } else {
                trainingPlanLayouts.size
            }

            val newTrainingPlanLayout = LayoutInflater.from(this)
                .inflate(R.layout.editetriningplanactivityadapter, trainingPlanContainer, false)

            val nameEditText: AppCompatEditText =
                newTrainingPlanLayout.findViewById(R.id.ent_pre_sea_name)

            nameEditText.setText("Mesocycle ${indexToAdd + 1}")

            val startDateEditText: AppCompatEditText =
                newTrainingPlanLayout.findViewById(R.id.ent_start_date_liner)
            val endDateEditText: AppCompatEditText =
                newTrainingPlanLayout.findViewById(R.id.ent_ent_date_liner)
            val mesocycles: TextView = newTrainingPlanLayout.findViewById(R.id.linear_days_list)

            val endDateCard: CardView = newTrainingPlanLayout.findViewById(R.id.card_end_pick_list)
            val StartDateCard: CardView =
                newTrainingPlanLayout.findViewById(R.id.card_start_date_list)


            mesocycles.text = ""

            if (mesocycle != null) {
                val startDateMillis = formatDateToMillis2(mesocycle.start_date)
                val formattedStartDate = formatMillisToDateString(startDateMillis)

                val endDateInMillis = formatDateToMillis2(mesocycle.end_date)
                val formattedEndDate = formatMillisToDateString(endDateInMillis)


                selectedDateRanges.add(startDateMillis to endDateInMillis)


                Log.d("startdaaaaaa", "Formatted Start Date: $formattedStartDate")

                startDateEditText.setText(formattedStartDate)
                endDateEditText.setText(formattedEndDate)
                mesocycles.text = mesocycle.periods ?: ""

                val errorStartDateMillis = parseFormattedDateToMillis(formattedStartDate)
                val errorEndDateMillis = parseFormattedDateToMillis(formattedEndDate)

                errorstartdate = formatDate(errorStartDateMillis)
                errorenddate = formatDate(errorEndDateMillis)

            }
            // Date selection listeners


            val startDateCard: CardView =
                newTrainingPlanLayout.findViewById(R.id.card_start_date_list)


            startDateEditText.setOnClickListener {
                // Get the layout index based on the active layout in the container
                val layoutIndex = trainingPlanContainer.indexOfChild(newTrainingPlanLayout)
                activeLayoutIndex = layoutIndex

                val minDateMillis =
                    formatDateToMillis(editMesocycleListBinding.startDate.text.toString())
                val maxDateMillis =
                    formatDateToMillis(editMesocycleListBinding.endDate.text.toString())

                showDateRangePickerDialogfor(
                    startDateEditText.context,
                    minDateMillis,
                    maxDateMillis,
                    layoutIndex
                ) { start, end ->
                    val formattedStartDate = formatDate(start)
                    val formattedStartDate2 = formatDate2(start)
                    val formattedEndDate = formatDate(end)
                    val formattedEndDate2 = formatDate2(end)

                    startDateEditText.setText(formattedStartDate2)
                    endDateEditText.setText(formattedEndDate2)
                    startdatesent = formattedStartDate
                    enddatesent = formattedEndDate
                    dateRangeMap[layoutIndex] = Pair(formattedStartDate, formattedEndDate)

//                        updateDaysTextView()
                }
            }

            endDateEditText.setOnClickListener {
                val layoutIndex = trainingPlanContainer.indexOfChild(newTrainingPlanLayout)
                activeLayoutIndex = layoutIndex


                val minDateMillis =
                    formatDateToMillis(editMesocycleListBinding.startDate.text.toString())
                val maxDateMillis =
                    formatDateToMillis(editMesocycleListBinding.endDate.text.toString())

                showDateRangePickerDialogfor(
                    startDateEditText.context,
                    minDateMillis,
                    maxDateMillis,
                    layoutIndex
                ) { start, end ->
                    val formattedStartDate = formatDate(start)
                    val formattedStartDate2 = formatDate2(start)
                    val formattedEndDate = formatDate(end)
                    val formattedEndDate2 = formatDate2(end)

                    startDateEditText.setText(formattedStartDate2)
                    endDateEditText.setText(formattedEndDate2)
                    dateRangeMap[layoutIndex] = Pair(formattedStartDate, formattedEndDate)
//                        updateDaysTextView()
                }
            }

            StartDateCard.setOnClickListener {
                // Get the layout index based on the active layout in the container
                val layoutIndex = trainingPlanContainer.indexOfChild(newTrainingPlanLayout)
                activeLayoutIndex = layoutIndex

                val minDateMillis =
                    formatDateToMillis(editMesocycleListBinding.startDate.text.toString())
                val maxDateMillis =
                    formatDateToMillis(editMesocycleListBinding.endDate.text.toString())

                showDateRangePickerDialogfor(
                    startDateEditText.context,
                    minDateMillis,
                    maxDateMillis,
                    layoutIndex
                ) { start, end ->
                    val formattedStartDate = formatDate(start)
                    val formattedStartDate2 = formatDate2(start)
                    val formattedEndDate = formatDate(end)
                    val formattedEndDate2 = formatDate2(end)

                    startDateEditText.setText(formattedStartDate2)
                    endDateEditText.setText(formattedEndDate2)
                    startdatesent = formattedStartDate
                    enddatesent = formattedEndDate
                    dateRangeMap[layoutIndex] = Pair(formattedStartDate, formattedEndDate)

//                        updateDaysTextView()
                }
            }

            endDateCard.setOnClickListener {
                val layoutIndex = trainingPlanContainer.indexOfChild(newTrainingPlanLayout)
                activeLayoutIndex = layoutIndex


                val minDateMillis =
                    formatDateToMillis(editMesocycleListBinding.startDate.text.toString())
                val maxDateMillis =
                    formatDateToMillis(editMesocycleListBinding.endDate.text.toString())

                showDateRangePickerDialogfor(
                    startDateEditText.context,
                    minDateMillis,
                    maxDateMillis,
                    layoutIndex
                ) { start, end ->
                    val formattedStartDate = formatDate(start)
                    val formattedStartDate2 = formatDate2(start)
                    val formattedEndDate = formatDate(end)
                    val formattedEndDate2 = formatDate2(end)

                    startDateEditText.setText(formattedStartDate2)
                    endDateEditText.setText(formattedEndDate2)
                    dateRangeMap[layoutIndex] = Pair(formattedStartDate, formattedEndDate)
//                        updateDaysTextView()
                }
            }


            trainingPlanLayouts.add(newTrainingPlanLayout)
            trainingPlanContainer.addView(newTrainingPlanLayout)

            // Delete button listener
            val delete: ImageView = newTrainingPlanLayout.findViewById(R.id.img_delete)
            delete.setOnClickListener {
                trainingPlanContainer.removeView(newTrainingPlanLayout)
                trainingPlanLayouts.removeAt(indexToAdd)
                if (mesocycle != null && mesocycle!!.id != null) {
                    removeTrainingPlan(newTrainingPlanLayout, mesocycle!!.id!!)
                } else {
                }

            }

        } catch (e: Exception) {
            Log.d("TAG", "addTrainingPlan Error: ${e.message}")
        }
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

    fun updateOrAddDateRange(
        layoutIndex: Int?,
        startMillis: Long,
        endMillis: Long,
        remove: Boolean = false
    ) {
        layoutIndex?.let {
            if (remove) {
                // Remove the date range if it exists
                if (it < selectedDateRanges.size) {
                    selectedDateRanges.removeAt(it)
                    Log.d("UpdateOrAddDateRange", "Date range at index $it removed.")
                } else {

                }
            } else {
                // Add or update the date range
                if (it < selectedDateRanges.size) {
                    selectedDateRanges[it] = startMillis to endMillis
                    Log.d("UpdateOrAddDateRange", "Date range at index $it updated.")
                } else {
                    selectedDateRanges.add(startMillis to endMillis)
                    Log.d("UpdateOrAddDateRange", "Date range added at index $it.")
                }
            }
        }
    }


    fun isConflictWithPreviousPlans(startMillis: Long, endMillis: Long, layoutIndex: Int): Boolean {
        if (layoutIndex <= 0 || selectedDateRanges.isEmpty()) {
            return false
        }

        for (i in 0 until layoutIndex) {
            if (i >= selectedDateRanges.size) {
                break // Prevent out-of-bounds access
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
        return start1 <= end2 && start2 <= end1  // Changed `<` to `<=`
    }


    private fun parseFormattedDateToMillis(dateString: String?): Long {
        return try {
            val format = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
            val date = format.parse(dateString)
            date?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {
            Log.e("DateConversion", "Error parsing formatted date: ${e.message}")
            System.currentTimeMillis()
        }
    }

    private fun removeTrainingPlan(trainingPlanLayout: View, cycleId: Int) {

        when (cardType) {
            "pre_session" -> {
                editMesocycleListBinding.progresBar.visibility = View.VISIBLE
                apiInterface.delete_PreSession(id = cycleId, psid = seasonid)
                    .enqueue(object : Callback<GetMessocyclePreSession> {
                        override fun onResponse(
                            call: Call<GetMessocyclePreSession>,
                            response: Response<GetMessocyclePreSession>
                        ) {
                            editMesocycleListBinding.progresBar.visibility = View.GONE
                            Log.d("delete_tag", "Invalid ID ${response.code()} ")
                            Log.d("delete_tag", "Invalid ID ${response.body()} ")
                            Log.d("delete_tag", "Invalid ID ${response.errorBody()} ")

                            Log.d("TAG", response.code().toString() + "")
                            val code = response.code()
                            if (code == 200) {
                                if (response.isSuccessful && response.body() != null) {
                                    val message = response.body()?.message ?: "Item deleted"
                                    Toast.makeText(
                                        this@EditMesocycleListActivity,
                                        message,
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    finish()
                                } else {
                                    Log.d("delete_tag", "Failed to delete: ${response.code()}")
                                    Toast.makeText(
                                        this@EditMesocycleListActivity,
                                        "Failed to delete",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    loadData()
                                }
                            } else if (code == 403) {
                                Utils.setUnAuthDialog(this@EditMesocycleListActivity)
                            } else {
                                Toast.makeText(
                                    this@EditMesocycleListActivity,
                                    "" + response.message(),
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                call.cancel()
                            }


                        }

                        override fun onFailure(call: Call<GetMessocyclePreSession>, t: Throwable) {
                            editMesocycleListBinding.progresBar.visibility = View.GONE
                            Log.d("delete_tag", "Error: ${t.message}")
                            Toast.makeText(
                                this@EditMesocycleListActivity,
                                t.message,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            call.cancel()
                        }
                    })
            }

            "pre_competitive" -> {
                editMesocycleListBinding.progresBar.visibility = View.VISIBLE
                apiInterface.delete_PreCompatitive(id = cycleId, psid = seasonid)
                    .enqueue(object : Callback<GetMessocyclePreSession> {
                        override fun onResponse(
                            call: Call<GetMessocyclePreSession>,
                            response: Response<GetMessocyclePreSession>
                        ) {
                            editMesocycleListBinding.progresBar.visibility = View.GONE
                            Log.d("delete_tag", "Invalid ID ${response.code()} ")
                            Log.d("delete_tag", "Invalid ID ${response.body()} ")
                            Log.d("delete_tag", "Invalid ID ${response.errorBody()} ")

                            Log.d("TAG", response.code().toString() + "")
                            val code = response.code()
                            if (code == 200) {
                                if (response.isSuccessful && response.body() != null) {
                                    val message = response.body()?.message ?: "Item deleted"
                                    Toast.makeText(
                                        this@EditMesocycleListActivity,
                                        message,
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    finish()
                                } else {
                                    Log.d("delete_tag", "Failed to delete: ${response.code()}")
                                    Toast.makeText(
                                        this@EditMesocycleListActivity,
                                        "Failed to delete",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    loadData()
                                }
                            } else if (code == 403) {
                                Utils.setUnAuthDialog(this@EditMesocycleListActivity)
                            } else {
                                Toast.makeText(
                                    this@EditMesocycleListActivity,
                                    "" + response.message(),
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                call.cancel()
                            }


                        }

                        override fun onFailure(call: Call<GetMessocyclePreSession>, t: Throwable) {
                            editMesocycleListBinding.progresBar.visibility = View.GONE
                            Log.d("delete_tag", "Error: ${t.message}")
                            Toast.makeText(
                                this@EditMesocycleListActivity,
                                t.message,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            call.cancel()
                        }
                    })
            }

            "competitive" -> {
                editMesocycleListBinding.progresBar.visibility = View.VISIBLE
                apiInterface.delete_Compatitive(id = cycleId, psid = seasonid)
                    .enqueue(object : Callback<GetMessocyclePreSession> {
                        override fun onResponse(
                            call: Call<GetMessocyclePreSession>,
                            response: Response<GetMessocyclePreSession>
                        ) {

                            Log.d("delete_tag", "Invalid ID ${response.code()} ")
                            Log.d("delete_tag", "Invalid ID ${response.body()} ")
                            Log.d("delete_tag", "Invalid ID ${response.errorBody()} ")
                            editMesocycleListBinding.progresBar.visibility = View.GONE
                            Log.d("TAG", response.code().toString() + "")
                            val code = response.code()
                            if (code == 200) {
                                if (response.isSuccessful && response.body() != null) {
                                    val message = response.body()?.message ?: "Item deleted"
                                    Toast.makeText(
                                        this@EditMesocycleListActivity,
                                        message,
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    finish()
                                } else {
                                    Log.d("delete_tag", "Failed to delete: ${response.code()}")
                                    Toast.makeText(
                                        this@EditMesocycleListActivity,
                                        "Failed to delete",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    loadData()
                                }
                            } else if (code == 403) {
                                Utils.setUnAuthDialog(this@EditMesocycleListActivity)
                            } else {
                                Toast.makeText(
                                    this@EditMesocycleListActivity,
                                    "" + response.message(),
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                call.cancel()
                            }


                        }

                        override fun onFailure(call: Call<GetMessocyclePreSession>, t: Throwable) {
                            Log.d("delete_tag", "Error: ${t.message}")
                            editMesocycleListBinding.progresBar.visibility = View.GONE
                            Toast.makeText(
                                this@EditMesocycleListActivity,
                                t.message,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            call.cancel()
                        }
                    })
            }

            "transition" -> {
                editMesocycleListBinding.progresBar.visibility = View.VISIBLE
                apiInterface.delete_Transition(id = cycleId, psid = seasonid)
                    .enqueue(object : Callback<GetMessocyclePreSession> {
                        override fun onResponse(
                            call: Call<GetMessocyclePreSession>,
                            response: Response<GetMessocyclePreSession>
                        ) {
                            editMesocycleListBinding.progresBar.visibility = View.GONE
                            Log.d("delete_tag", "Invalid ID ${response.code()} ")
                            Log.d("delete_tag", "Invalid ID ${response.body()} ")
                            Log.d("delete_tag", "Invalid ID ${response.errorBody()} ")

                            Log.d("TAG", response.code().toString() + "")
                            val code = response.code()
                            if (code == 200) {
                                if (response.isSuccessful && response.body() != null) {
                                    val message = response.body()?.message ?: "Item deleted"
                                    Toast.makeText(
                                        this@EditMesocycleListActivity,
                                        message,
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    finish()
                                } else {
                                    Log.d("delete_tag", "Failed to delete: ${response.code()}")
                                    Toast.makeText(
                                        this@EditMesocycleListActivity,
                                        "Failed to delete",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    loadData()
                                }
                            } else if (code == 403) {
                                Utils.setUnAuthDialog(this@EditMesocycleListActivity)
                            } else {
                                Toast.makeText(
                                    this@EditMesocycleListActivity,
                                    "" + response.message(),
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                call.cancel()
                            }


                        }

                        override fun onFailure(call: Call<GetMessocyclePreSession>, t: Throwable) {
                            Log.d("delete_tag", "Error: ${t.message}")
                            editMesocycleListBinding.progresBar.visibility = View.GONE
                            Toast.makeText(
                                this@EditMesocycleListActivity,
                                t.message,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            call.cancel()
                        }
                    })
            }
        }
    }

    private fun getPreSessionData(id: Int) {
        try {

            editMesocycleListBinding.progresBar.visibility = View.VISIBLE
            apiInterface.GetMesocyclePreSession(id)
                .enqueue(object : Callback<GetMessocyclePreSession> {
                    override fun onResponse(
                        call: Call<GetMessocyclePreSession>,
                        response: Response<GetMessocyclePreSession>
                    ) {
                        editMesocycleListBinding.progresBar.visibility = View.GONE
                        Log.d("TAG", response.code().toString() + "")
                        val code = response.code()
                        if (code == 200) {
                            if (response.isSuccessful) {
                                response.body()?.let { responseBody ->
                                    programData = responseBody.data!!.toMutableList()
                                    for (mesocycles in programData) {
                                        addTrainingPlan(mesocycles)
                                    }
                                }
                            } else {
                                Log.e("Error", "Failed to fetch data")
                            }
                        } else if (code == 403) {
                            Utils.setUnAuthDialog(this@EditMesocycleListActivity)
                        } else {
                            Toast.makeText(
                                this@EditMesocycleListActivity,
                                "" + response.message(),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            call.cancel()
                        }


                    }

                    override fun onFailure(call: Call<GetMessocyclePreSession>, t: Throwable) {
                        editMesocycleListBinding.progresBar.visibility = View.GONE
                        Log.e("Error", "Network Error: ${t.message}")
                    }
                })

        } catch (e: Exception) {
            Log.d("error ", e.message.toString())
        }
    }


    private fun getPreCompatiiveData(id: Int) {
        editMesocycleListBinding.progresBar.visibility = View.VISIBLE
        apiInterface.GetMesocyclePreCompatitive(id)
            .enqueue(object : Callback<GetMessocyclePreSession> {
                override fun onResponse(
                    call: Call<GetMessocyclePreSession>,
                    response: Response<GetMessocyclePreSession>
                ) {
                    Log.d("TAG", response.code().toString() + "")
                    editMesocycleListBinding.progresBar.visibility = View.GONE
                    val code = response.code()
                    if (code == 200) {
                        if (response.isSuccessful) {
                            response.body()?.let { responseBody ->
                                programData = responseBody.data!!.toMutableList()
                                for (mesocycles in programData) {
                                    addTrainingPlan(mesocycles)
                                }
                            }
                        } else {
                            Log.e("Error", "Failed to fetch data")
                        }
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@EditMesocycleListActivity)
                    } else {
                        Toast.makeText(
                            this@EditMesocycleListActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }

                }

                override fun onFailure(call: Call<GetMessocyclePreSession>, t: Throwable) {
                    editMesocycleListBinding.progresBar.visibility = View.GONE
                    Log.e("Error", "Network Error: ${t.message}")
                }
            })
    }


    private fun getCompatiiveData(id: Int) {
        editMesocycleListBinding.progresBar.visibility = View.VISIBLE
        apiInterface.GetMesocycleCompatitive(id)
            .enqueue(object : Callback<GetMessocyclePreSession> {
                override fun onResponse(
                    call: Call<GetMessocyclePreSession>,
                    response: Response<GetMessocyclePreSession>
                ) {
                    editMesocycleListBinding.progresBar.visibility = View.GONE
                    Log.d("TAG", response.code().toString() + "")
                    val code = response.code()
                    if (code == 200) {
                        if (response.isSuccessful) {
                            response.body()?.let { responseBody ->
                                programData = responseBody.data!!.toMutableList()
                                for (mesocycles in programData) {
                                    addTrainingPlan(mesocycles)
                                }
                            }
                        } else {
                            Log.e("Error", "Failed to fetch data")
                        }
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@EditMesocycleListActivity)
                    } else {
                        Toast.makeText(
                            this@EditMesocycleListActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }

                }

                override fun onFailure(call: Call<GetMessocyclePreSession>, t: Throwable) {
                    editMesocycleListBinding.progresBar.visibility = View.GONE
                    Log.e("Error", "Network Error: ${t.message}")
                }
            })
    }

    private fun getTransitionData(id: Int) {
        editMesocycleListBinding.progresBar.visibility = View.VISIBLE
        apiInterface.GetMesocycleTransition(id)
            .enqueue(object : Callback<GetMessocyclePreSession> {
                override fun onResponse(
                    call: Call<GetMessocyclePreSession>,
                    response: Response<GetMessocyclePreSession>
                ) {
                    editMesocycleListBinding.progresBar.visibility = View.GONE
                    Log.d("TAG", response.code().toString() + "")
                    val code = response.code()
                    if (code == 200) {
                        if (response.isSuccessful) {
                            response.body()?.let { responseBody ->
                                programData = responseBody.data!!.toMutableList()
                                for (mesocycles in programData) {
                                    addTrainingPlan(mesocycles)
                                }
                            }
                        } else {
                            Log.e("Error", "Failed to fetch data")
                        }
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@EditMesocycleListActivity)
                    } else {
                        Toast.makeText(
                            this@EditMesocycleListActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }

                }

                override fun onFailure(call: Call<GetMessocyclePreSession>, t: Throwable) {
                    editMesocycleListBinding.progresBar.visibility = View.GONE
                    Log.e("Error", "Network Error: ${t.message}")
                }
            })
    }


    private fun selectTrainingPlanStartDate(editText: AppCompatEditText) {
        val mainStartDate = editMesocycleListBinding.startDate.text.toString()
        val maxStartDate = editMesocycleListBinding.endDate.text.toString()

        val minDateMillis =
            if (mainStartDate.isNotEmpty()) formatDateToMillis(mainStartDate) else System.currentTimeMillis()
        val maxDateMillis =
            if (maxStartDate.isNotEmpty()) formatDateToMillis(maxStartDate) else Long.MAX_VALUE

        Utils.selectDate3(
            this,
            editText,
            minDateMillis,
            maxDateMillis
        ) { dateMillis ->
            // When a date is selected, update startDateMillis and display the formatted date
            startDateMillis = dateMillis

            startdatesent = formatDate(dateMillis)
            val formattedDate = formatDate2(dateMillis)
            editText.setText(formattedDate)
        }
    }

    private fun selectTrainingPlanEndDate(editText: AppCompatEditText) {

        val mainStartDate = editMesocycleListBinding.startDate.text.toString()
        val maxStartDate = editMesocycleListBinding.endDate.text.toString()

        val minDateMillis =
            if (mainStartDate.isNotEmpty()) formatDateToMillis(mainStartDate) else System.currentTimeMillis()
        val maxDateMillis =
            if (maxStartDate.isNotEmpty()) formatDateToMillis(maxStartDate) else Long.MAX_VALUE

        Utils.selectDate3(
            this,
            editText,
            minDateMillis,
            maxDateMillis
        ) { dateMillis ->

            startDateMillis = dateMillis

            enddatesent = formatDate(dateMillis)
            val formattedDate = formatDate2(dateMillis)
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

}