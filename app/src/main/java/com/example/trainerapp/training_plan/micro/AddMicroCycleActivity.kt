package com.example.trainerapp.training_plan.micro

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.Adapter.training_plan.view.AbilitiesAdapter
import com.example.OnItemClickListener
import com.example.model.training_plan.MicroCycle.AbilityData
import com.example.model.training_plan.MicroCycle.AddAblilityClass
import com.example.model.training_plan.MicroCycle.AddMicrocycleCompatitive
import com.example.model.training_plan.MicroCycle.AddMicrocyclePreCompatitive
import com.example.model.training_plan.MicroCycle.AddMicrocyclePreSeason
import com.example.model.training_plan.MicroCycle.AddMicrocycleTransition
import com.example.model.training_plan.MicroCycle.GetMicrocycle
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.PreferencesManager
import com.example.trainerapp.R
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.ActivityAddMicroCycleBinding
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


class AddMicroCycleActivity : AppCompatActivity() {

    private lateinit var addMicroCycleBinding: ActivityAddMicroCycleBinding
    private lateinit var trainingPlanContainer: LinearLayout
    private lateinit var ablilityContainer: LinearLayout
    private var trainingPlanCount = 0
    private var id: Int = 0
    private var cardType: String? = null

    private val dateRangeMap = mutableMapOf<Int, Pair<String, String>>()
    private val dateRangeMapend = mutableMapOf<Int, Pair<String, String>>()
    private var activeLayoutIndex: Int? = null
    private val selectedDateRanges = mutableListOf<Pair<Long, Long>>()

    private lateinit var apiInterface: APIInterface
    private lateinit var preferenceManager: PreferencesManager
    private lateinit var apiClient: APIClient
    private var splist: MutableList<AbilityData> = mutableListOf()

    private val trainingPlanLayouts = mutableListOf<View>()
    private val selectedAbilityIds = mutableListOf<Int>()

    private val missingIndices = mutableListOf<Int>()
    private lateinit var adapter: AbilitiesAdapter
    private var startdate: String? = null
    private var endDate: String? = null

    private var startdatesent:String? = null
    private var enddatesent:String? = null

    private var preSeason: MutableList<AddMicrocyclePreSeason> = mutableListOf()
    private var preCompetitive: MutableList<AddMicrocyclePreCompatitive> = mutableListOf()
    private var competitive: MutableList<AddMicrocycleCompatitive> = mutableListOf()
    private var transient: MutableList<AddMicrocycleTransition> = mutableListOf()

    private var startDateMillis: Long = 0
    override fun onResume() {
        checkUser()
        super.onResume()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addMicroCycleBinding = ActivityAddMicroCycleBinding.inflate(layoutInflater)
        setContentView(addMicroCycleBinding.root)
        buttonclick()
        intialize()
    }

    private fun intialize() {
        trainingPlanContainer = addMicroCycleBinding.linerAddTraining
        ablilityContainer = addMicroCycleBinding.chvvp
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        preferenceManager = PreferencesManager(this)

        id = intent.getIntExtra("MainId", 0)
        startdate = intent.getStringExtra("StartDate")
        endDate = intent.getStringExtra("EndDate")
        cardType = intent.getStringExtra("CardType")
        Log.d("id", id.toString())
        Log.d("cardtype", cardType.toString())

        addMicroCycleBinding.startDate.text = startdate
        addMicroCycleBinding.endDate.text = endDate
    }

    private fun buttonclick() {

        addMicroCycleBinding.add.setOnClickListener { addTrainingPlan() }

        addMicroCycleBinding.cardAdd.setOnClickListener { addTrainingPlan() }

        addMicroCycleBinding.back.setOnClickListener { finish() }

        addMicroCycleBinding.cardAddAbility.setOnClickListener {
            showAbilityDialog()
        }

        addMicroCycleBinding.cardSave.setOnClickListener { loadData() }
    }

    private fun loadData() {
        Log.d("Card Type :-", "$cardType")
        when (cardType) {
            "pre_session" -> {
                SaveMicrocycle()
            }

            "pre_competitive" -> {
                SaveMicrocyclePreCompatitive()
            }

            "competitive" -> {
                SaveMicrocycleCompatitive()
            }

            "transition" -> {
                SaveMicrocycleTransition()
            }
        }
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

    // Convert date in "yyyy-MM-dd" format to milliseconds
    fun convertDateToMillis(dateString: String): Long {
        return try {
            val sdf = SimpleDateFormat(
                "dd MMM, yyyy",
                Locale.getDefault()
            ) // Adjust format based on your date format
            val date = sdf.parse(dateString)
            date?.time ?: 0L
        } catch (e: Exception) {
            0L // Return 0 if parsing fails (invalid date)
        }
    }


    fun isOverlapping(start1: Long, end1: Long, start2: Long, end2: Long): Boolean {
        return start1 <= end2 && start2 <= end1  // Changed `<` to `<=`
    }

    private fun SaveMicrocycle() {
        try {
            preSeason.clear()

            for (i in 0 until trainingPlanContainer.childCount) {
                val layout = trainingPlanContainer.getChildAt(i)
                val nameEditText: AppCompatEditText = layout.findViewById(R.id.ent_pre_sea_name)
                val startDate: AppCompatEditText = layout.findViewById(R.id.ent_start_date_liner)
                val endDate: AppCompatEditText = layout.findViewById(R.id.ent_ent_date_liner)
                val seekBar: SeekBar = layout.findViewById(R.id.seekbar_workload_layout)
                val workloadProgress = seekBar.progress
                val (colorCode, _) = getWorkloadColorAndString(workloadProgress)

                Log.d(
                    "TAG",
                    "Name: ${nameEditText.text} \n StartDate: ${startDate.text} \n enddate: ${endDate.text}"
                )
                Log.d("TAG", "Selected Ability IDs: $selectedAbilityIds")
                Log.d("TAG", "Selected Color Code: $colorCode")
                Log.d("TAG", "Selected Workload Progress: $workloadProgress")

                val start = startDate.text.toString().trim()
                val end = endDate.text.toString().trim()
                var isss = selectedAbilityIds.toString()

                Log.d("RTTTRTRTRTR", "SaveMicrocycleTransition: $isss")

                val startDateMillis = formatDateToMillistest(start)
                val endDateMillis = formatDateToMillistest(end)

                val formattedStartDate = formatMillisToDateString(startDateMillis)
                val formattedEndDate = formatMillisToDateString(endDateMillis)

                val errorStartDateMillis = parseFormattedDateToMillis(formattedStartDate)
                val errorTestDateMillis = parseFormattedDateToMillis(formattedEndDate)

                val finalStartDates = formatDate(errorStartDateMillis)
                val finalEndDates = formatDate(errorTestDateMillis)

                val name = nameEditText?.text.toString().trim()
                val errorTextView: TextView = layout.findViewById(R.id.error_start_date_list)
                val EndDateErrorTextView: TextView = layout.findViewById(R.id.error_end_date_list)

                if (name.isEmpty()) {
                    Toast.makeText(this, "Please fill name fields.", Toast.LENGTH_SHORT).show()
                    return
                }

                if (start.isNullOrEmpty()){
                    errorTextView.visibility = View.VISIBLE
                    return
                }else{
                    errorTextView.visibility = View.GONE
                }

                if (end.isNullOrEmpty()){
                    EndDateErrorTextView.visibility = View.VISIBLE
                    return
                }else{
                    EndDateErrorTextView.visibility = View.GONE
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


                if (nameEditText.text!!.isNotEmpty() && startDate.text!!.isNotEmpty() && endDate.text!!.isNotEmpty() && isss.isNotEmpty()  && isss != "[]") {
                    preSeason.add(
                        AddMicrocyclePreSeason(
                            id = 0,
                            ps_mesocycle_id = id,
                            name = nameEditText.text.toString(),
                            start_date = finalStartDates.toString(),
                            end_date = finalEndDates.toString(),
                            workload = workloadProgress,
                            workload_color = colorCode,
                            ability_ids = selectedAbilityIds
                        )
                    )
                } else {
                    Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
                    return
                }
            }

            if (preSeason.isNotEmpty()) {
                apiInterface.AddMicrocyclePreSeeion(preSeason)!!
                    .enqueue(object : Callback<GetMicrocycle> {
                        override fun onResponse(
                            call: Call<GetMicrocycle>,
                            response: Response<GetMicrocycle>
                        ) {
                            Log.d("Success Code :-", "${response.code()}")
                            Log.d("Success Message :-", "${response.message()}")

                            if (response.isSuccessful && response.code() == 200) {
                                Log.d("Response Body :-", "${response.body()}")
                                val message = response.body()!!.message ?: "Data added successfully"
                                Toast.makeText(
                                    this@AddMicroCycleActivity,
                                    message,
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            } else {
                                Log.d("Error Code :-", "${response.code()} - ${response.message()}")
                                Log.d("Error Body :-", "${response.errorBody()}")
                                Log.d("Error Message :-", "${response.message()}")
                                Toast.makeText(
                                    this@AddMicroCycleActivity,
                                    "Error: ${response.message()}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(p0: Call<GetMicrocycle>, p1: Throwable) {
                            // Handle failure
                        }
                    })
            }

        } catch (e: Exception) {
            Log.d("Exception", "${e.message}")
            Toast.makeText(this, "Exception: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private val isValidate: Boolean
        get() {
            var email = addMicroCycleBinding.startDate.text.toString()
            var password = addMicroCycleBinding.endDate.text.toString()
            if (email == "") {

                return false
            } else {
            }

            return true
        }

    private fun SaveMicrocyclePreCompatitive() {
        try {
            preCompetitive.clear()

            for (i in 0 until trainingPlanContainer.childCount) {
                val layout = trainingPlanContainer.getChildAt(i)
                val nameEditText: AppCompatEditText = layout.findViewById(R.id.ent_pre_sea_name)
                val startDate: AppCompatEditText = layout.findViewById(R.id.ent_start_date_liner)
                val endDate: AppCompatEditText = layout.findViewById(R.id.ent_ent_date_liner)
                val seekBar: SeekBar = layout.findViewById(R.id.seekbar_workload_layout)
                val workloadProgress = seekBar.progress
                val (colorCode, _) = getWorkloadColorAndString(workloadProgress)

                Log.d(
                    "TAG",
                    "Name: ${nameEditText.text} \n StartDate: ${startDate.text} \n enddate: ${endDate.text}"
                )
                Log.d("TAG", "Selected Ability IDs: $selectedAbilityIds")
                Log.d("TAG", "Selected Color Code: $colorCode")
                Log.d("TAG", "Selected Workload Progress: $workloadProgress")

                val start = startDate.text.toString().trim()
                val end = endDate.text.toString().trim()

                val isss = selectedAbilityIds.toString()


                val startDateMillis = formatDateToMillistest(start)
                val endDateMillis = formatDateToMillistest(end)

                val formattedStartDate = formatMillisToDateString(startDateMillis)
                val formattedEndDate = formatMillisToDateString(endDateMillis)

                val errorStartDateMillis = parseFormattedDateToMillis(formattedStartDate)
                val errorTestDateMillis = parseFormattedDateToMillis(formattedEndDate)

                val finalStartDates = formatDate(errorStartDateMillis)
                val finalEndDates = formatDate(errorTestDateMillis)

                val name = nameEditText?.text.toString().trim()
                val errorTextView: TextView = layout.findViewById(R.id.error_start_date_list)
                val EndDateErrorTextView: TextView = layout.findViewById(R.id.error_end_date_list)

                if (name.isEmpty()) {
                    Toast.makeText(this, "Please fill name fields.", Toast.LENGTH_SHORT).show()
                    return
                }

                if (start.isNullOrEmpty()){
                    errorTextView.visibility = View.VISIBLE
                    return
                }else{
                    errorTextView.visibility = View.GONE
                }

                if (end.isNullOrEmpty()){
                    EndDateErrorTextView.visibility = View.VISIBLE
                    return
                }else{
                    EndDateErrorTextView.visibility = View.GONE
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




                Log.d("FFHHFHFHH", "SaveMicrocyclePreCompatitive: $isss")

                if (nameEditText.text!!.isNotEmpty() && startDate.text!!.isNotEmpty() && endDate.text!!.isNotEmpty()  && isss.isNotEmpty() && isss != "[]") {
                    preCompetitive.add(
                        AddMicrocyclePreCompatitive(
                            id = 0,
                            pc_mesocycle_id = id,
                            name = nameEditText.text.toString(),
                            start_date = finalStartDates.toString(),
                            end_date = finalEndDates.toString(),
                            workload = workloadProgress,
                            workload_color = colorCode,
                            ability_ids = selectedAbilityIds
                        )
                    )
                } else {
                    addMicroCycleBinding.cardSave.setCardBackgroundColor(ContextCompat.getColor(this, R.color.grey))
                    Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
                    return
                }
            }

            if (preCompetitive.isNotEmpty()) {
                apiInterface.AddMicrocyclePreCompatitive(preCompetitive)!!
                    .enqueue(object : Callback<GetMicrocycle> {
                        override fun onResponse(
                            call: Call<GetMicrocycle>,
                            response: Response<GetMicrocycle>
                        ) {
                            Log.d("Success Code :-", "${response.code()}")
                            Log.d("Success Message :-", "${response.message()}")

                            if (response.isSuccessful && response.code() == 200) {
                                Log.d("Response Body :-", "${response.body()}")
                                val message = response.body()!!.message ?: "Data added successfully"
                                Toast.makeText(
                                    this@AddMicroCycleActivity,
                                    message,
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            } else {
                                Log.d("Error Code :-", "${response.code()} - ${response.message()}")
                                Log.d("Error Body :-", "${response.errorBody()}")
                                Log.d("Error Message :-", "${response.message()}")
                                Toast.makeText(
                                    this@AddMicroCycleActivity,
                                    "Error: ${response.message()}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(p0: Call<GetMicrocycle>, p1: Throwable) {
                            // Handle failure
                        }
                    })
            }

        } catch (e: Exception) {
            Log.d("Exception", "${e.message}")
            Toast.makeText(this, "Exception: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun SaveMicrocycleCompatitive() {
        try {
            competitive.clear()

            for (i in 0 until trainingPlanContainer.childCount) {
                val layout = trainingPlanContainer.getChildAt(i)
                val nameEditText: AppCompatEditText = layout.findViewById(R.id.ent_pre_sea_name)
                val startDate: AppCompatEditText = layout.findViewById(R.id.ent_start_date_liner)
                val endDate: AppCompatEditText = layout.findViewById(R.id.ent_ent_date_liner)
                val seekBar: SeekBar = layout.findViewById(R.id.seekbar_workload_layout)
                val workloadProgress = seekBar.progress
                val (colorCode, _) = getWorkloadColorAndString(workloadProgress)

                Log.d(
                    "TAG",
                    "Name: ${nameEditText.text} \n StartDate: ${startDate.text} \n enddate: ${endDate.text}"
                )
                Log.d("TAG", "Selected Ability IDs: $selectedAbilityIds")
                Log.d("TAG", "Selected Color Code: $colorCode")
                Log.d("TAG", "Selected Workload Progress: $workloadProgress")

                val start = startDate.text.toString().trim()
                val end = endDate.text.toString().trim()
                var isss = selectedAbilityIds.toString()


                val startDateMillis = formatDateToMillistest(start)
                val endDateMillis = formatDateToMillistest(end)

                val formattedStartDate = formatMillisToDateString(startDateMillis)
                val formattedEndDate = formatMillisToDateString(endDateMillis)

                val errorStartDateMillis = parseFormattedDateToMillis(formattedStartDate)
                val errorTestDateMillis = parseFormattedDateToMillis(formattedEndDate)

                val finalStartDates = formatDate(errorStartDateMillis)
                val finalEndDates = formatDate(errorTestDateMillis)

                val name = nameEditText?.text.toString().trim()
                val errorTextView: TextView = layout.findViewById(R.id.error_start_date_list)
                val EndDateErrorTextView: TextView = layout.findViewById(R.id.error_end_date_list)

                if (name.isEmpty()) {
                    Toast.makeText(this, "Please fill name fields.", Toast.LENGTH_SHORT).show()
                    return
                }

                if (start.isNullOrEmpty()){
                    errorTextView.visibility = View.VISIBLE
                    return
                }else{
                    errorTextView.visibility = View.GONE
                }

                if (end.isNullOrEmpty()){
                    EndDateErrorTextView.visibility = View.VISIBLE
                    return
                }else{
                    EndDateErrorTextView.visibility = View.GONE
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




                if (nameEditText.text!!.isNotEmpty() && startDate.text!!.isNotEmpty() && endDate.text!!.isNotEmpty() && isss.isNotEmpty() && isss != "[]") {
                    competitive.add(
                        AddMicrocycleCompatitive(
                            id = 0,
                            c_mesocycle_id = id,
                            name = nameEditText.text.toString(),
                            start_date = finalStartDates.toString(),
                            end_date = finalEndDates.toString(),
                            workload = workloadProgress,
                            workload_color = colorCode,
                            ability_ids = selectedAbilityIds
                        )
                    )
                } else {
                    Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
                    return
                }
            }

            if (competitive.isNotEmpty()) {
                apiInterface.AddMicrocycleCompatitive(competitive)!!
                    .enqueue(object : Callback<GetMicrocycle> {
                        override fun onResponse(
                            call: Call<GetMicrocycle>,
                            response: Response<GetMicrocycle>
                        ) {
                            Log.d("Success Code :-", "${response.code()}")
                            Log.d("Success Message :-", "${response.message()}")

                            if (response.isSuccessful && response.code() == 200) {
                                Log.d("Response Body :-", "${response.body()}")
                                val message = response.body()!!.message ?: "Data added successfully"
                                Toast.makeText(
                                    this@AddMicroCycleActivity,
                                    message,
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            } else {
                                Log.d("Error Code :-", "${response.code()} - ${response.message()}")
                                Log.d("Error Body :-", "${response.errorBody()}")
                                Log.d("Error Message :-", "${response.message()}")
                                Toast.makeText(
                                    this@AddMicroCycleActivity,
                                    "Error: ${response.message()}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(p0: Call<GetMicrocycle>, p1: Throwable) {
                            // Handle failure
                        }
                    })
            }

        } catch (e: Exception) {
            Log.d("Exception", "${e.message}")
            Toast.makeText(this, "Exception: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun SaveMicrocycleTransition() {
        try {
            transient.clear()

            for (i in 0 until trainingPlanContainer.childCount) {
                val layout = trainingPlanContainer.getChildAt(i)
                val nameEditText: AppCompatEditText = layout.findViewById(R.id.ent_pre_sea_name)
                val startDate: AppCompatEditText = layout.findViewById(R.id.ent_start_date_liner)
                val endDate: AppCompatEditText = layout.findViewById(R.id.ent_ent_date_liner)
                val seekBar: SeekBar = layout.findViewById(R.id.seekbar_workload_layout)
                val workloadProgress = seekBar.progress
                val (colorCode, _) = getWorkloadColorAndString(workloadProgress)

                Log.d("TAGGGGGG", "Name: ${nameEditText.text} \n StartDate: ${startDate.text} \n enddate: ${endDate.text}")

                Log.d("TAG", "Selected Ability IDs: $selectedAbilityIds")
                Log.d("TAG", "Selected Color Code: $colorCode")
                Log.d("TAG", "Selected Workload Progress: $workloadProgress")

                val start = startDate.text.toString().trim()
                val end = endDate.text.toString().trim()
                val isss = selectedAbilityIds.toString()


                val startDateEditText: AppCompatEditText = layout.findViewById(R.id.ent_start_date_liner)
                val endDateEditText: AppCompatEditText = layout.findViewById(R.id.ent_ent_date_liner)

                val startDateMillis = formatDateToMillistest(start)
                val endDateMillis = formatDateToMillistest(end)

                val formattedStartDate = formatMillisToDateString(startDateMillis)
                val formattedEndDate = formatMillisToDateString(endDateMillis)

                val errorStartDateMillis = parseFormattedDateToMillis(formattedStartDate)
                val errorTestDateMillis = parseFormattedDateToMillis(formattedEndDate)

                val finalStartDates = formatDate(errorStartDateMillis)
                val finalEndDates = formatDate(errorTestDateMillis)

                val name = nameEditText?.text.toString().trim()
                val errorTextView: TextView = layout.findViewById(R.id.error_start_date_list)
                val EndDateErrorTextView: TextView = layout.findViewById(R.id.error_end_date_list)

                if (name.isEmpty()) {
                    Toast.makeText(this, "Please fill name fields.", Toast.LENGTH_SHORT).show()
                    return
                }

                if (start.isNullOrEmpty()){
                    errorTextView.visibility = View.VISIBLE
                    return
                }else{
                    errorTextView.visibility = View.GONE
                }

                if (end.isNullOrEmpty()){
                    EndDateErrorTextView.visibility = View.VISIBLE
                    return
                }else{
                    EndDateErrorTextView.visibility = View.GONE
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






                Log.d("RTTTRTRTRTR", "SaveMicrocycleTransition: $isss")

                if (nameEditText.text!!.isNotEmpty() && startDate.text!!.isNotEmpty() && endDate.text!!.isNotEmpty() && isss.isNotEmpty() && isss != "[]" ) {
                    transient.add(
                        AddMicrocycleTransition(
                            id = 0,
                            pt_mesocycle_id = id,
                            name = nameEditText.text.toString(),
                            start_date = finalStartDates.toString(),
                            end_date = finalEndDates.toString(),
                            workload = workloadProgress,
                            workload_color = colorCode,
                            ability_ids = selectedAbilityIds
                        )
                    )
                } else {
                    Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
                    return
                }
            }

            if (transient.isNotEmpty()) {
                apiInterface.AddMicrocycleTransition(transient)!!
                    .enqueue(object : Callback<GetMicrocycle> {
                        override fun onResponse(
                            call: Call<GetMicrocycle>,
                            response: Response<GetMicrocycle>
                        ) {
                            Log.d("Success Code :-", "${response.code()}")
                            Log.d("Success Message :-", "${response.message()}")

                            if (response.isSuccessful && response.code() == 200) {
                                Log.d("Response Body :-", "${response.body()}")
                                val message = response.body()!!.message ?: "Data added successfully"
                                Toast.makeText(
                                    this@AddMicroCycleActivity,
                                    message,
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            } else {
                                Log.d("Error Code :-", "${response.code()} - ${response.message()}")
                                Log.d("Error Body :-", "${response.errorBody()}")
                                Log.d("Error Message :-", "${response.message()}")
                                Toast.makeText(
                                    this@AddMicroCycleActivity,
                                    "Error: ${response.message()}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(p0: Call<GetMicrocycle>, p1: Throwable) {
                            Toast.makeText(this@AddMicroCycleActivity, "Error:", Toast.LENGTH_SHORT).show()
                        }
                    })
            }

        } catch (e: Exception) {
            Log.d("Exception", "${e.message}")
            Toast.makeText(this, "Exception: ${e.message}", Toast.LENGTH_SHORT).show()
        }
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
    private fun formatMillisToDateString(millis: Long): String {
        val format = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
        return format.format(millis)
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


    private fun addTrainingPlan() {
        Log.d("Selected Abilities IDs", "Selected Ability IDs: $selectedAbilityIds")

        addMicroCycleBinding.linerAdd.visibility = View.GONE
        addMicroCycleBinding.scrollView2.visibility = View.VISIBLE

        trainingPlanCount++

        val indexToAdd = if (missingIndices.isNotEmpty()) {
            val reusedIndex = missingIndices.removeAt(0)
            Log.d("AddTrainingPlan", "Reusing missing index: $reusedIndex")
            reusedIndex
        } else {
            trainingPlanLayouts.size
        }

        val newTrainingPlanLayout = LayoutInflater.from(this).inflate(R.layout.add_microcycle_layout, trainingPlanContainer, false)

        val nameEditText: AppCompatEditText = newTrainingPlanLayout.findViewById(R.id.ent_pre_sea_name)
        nameEditText.setText("Microcycle ${indexToAdd + 1}")

        val startDateEditText: AppCompatEditText = newTrainingPlanLayout.findViewById(R.id.ent_start_date_liner)

        val startDateCard: CardView = newTrainingPlanLayout.findViewById(R.id.card_start_date_list)

        val endDateEditText: AppCompatEditText = newTrainingPlanLayout.findViewById(R.id.ent_ent_date_liner)

        val endDateCard: CardView = newTrainingPlanLayout.findViewById(R.id.card_end_pick_list)

        startDateEditText.setOnClickListener {
            val layoutIndex = trainingPlanContainer.indexOfChild(newTrainingPlanLayout)
            activeLayoutIndex = layoutIndex

            val minDateMillis =
                formatDateToMillis(addMicroCycleBinding.startDate.text.toString())
            val maxDateMillis =
                formatDateToMillis(addMicroCycleBinding.endDate.text.toString())

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
                formatDateToMillis(addMicroCycleBinding.startDate.text.toString())
            val maxDateMillis =
                formatDateToMillis(addMicroCycleBinding.endDate.text.toString())

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

        startDateCard.setOnClickListener {
            // Get the layout index based on the active layout in the container
            val layoutIndex = trainingPlanContainer.indexOfChild(newTrainingPlanLayout)
            activeLayoutIndex = layoutIndex

            val minDateMillis =
                formatDateToMillis(addMicroCycleBinding.startDate.text.toString())
            val maxDateMillis =
                formatDateToMillis(addMicroCycleBinding.endDate.text.toString())

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
                formatDateToMillis(addMicroCycleBinding.startDate.text.toString())
            val maxDateMillis =
                formatDateToMillis(addMicroCycleBinding.endDate.text.toString())

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

        val seekBar: SeekBar = newTrainingPlanLayout.findViewById(R.id.seekbar_workload_layout)
        seekBar.isEnabled = false

        val workloadTextView: TextView = newTrainingPlanLayout.findViewById(R.id.edit_worklord_txt)
        workloadTextView.setOnClickListener {
            Log.d("ClickEvent", "TextView clicked, showing dialog")


            val currentProgress = seekBar.progress
            showWorkloadDialog(object : OnItemClickListener.WorkloadDialogListener {
                override fun onWorkloadProgressSelected(progress: Int, colorCode: String) {
                    seekBar.progress = progress
                    Log.d(
                        "ProgressSet",
                        "Progress: $progress, Color: $colorCode, ids: $selectedAbilityIds"
                    )
                }
            }, currentProgress)
        }

        val start = startDateEditText.text.toString()
        val end = endDateEditText.text.toString()

//        if(startdatesent == enddatesent){
//            addMicroCycleBinding.cardSave.isEnabled = false
//        }else{
//            addMicroCycleBinding.cardSave.isEnabled = true
//        }



        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            intArrayOf(
                Color.parseColor("#F3F3F3"),
                Color.parseColor("#10E218"),
                Color.parseColor("#E2C110"),
                Color.parseColor("#F17A0B"),
                Color.parseColor("#FF0000")
            )
        )

        gradientDrawable.gradientType = GradientDrawable.LINEAR_GRADIENT
        gradientDrawable.cornerRadius = 8f

        seekBar.progressDrawable = gradientDrawable
        seekBar.isEnabled = false


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
                        Utils.setUnAuthDialog(this@AddMicroCycleActivity)
                    } else {
                        Toast.makeText(
                            this@AddMicroCycleActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Toast.makeText(
                        this@AddMicroCycleActivity,
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




    private fun selectTrainingPlanStartDate(editText: AppCompatEditText) {
        // Get the main start and max end dates from the binding
        val mainStartDate = addMicroCycleBinding.startDate.text.toString()
        val maxStartDate = addMicroCycleBinding.endDate.text.toString()

        // Check if mainStartDate and maxStartDate are valid dates
        val minDateMillis =
            if (mainStartDate.isNotEmpty()) formatDateToMillis(mainStartDate) else System.currentTimeMillis()
        val maxDateMillis =
            if (maxStartDate.isNotEmpty()) formatDateToMillis(maxStartDate) else Long.MAX_VALUE

        Utils.selectDate3(
            this,
            editText,
            minDateMillis, // Minimum date for selection
            maxDateMillis  // Maximum date for selection
        ) { dateMillis ->
            // When a date is selected, update startDateMillis and display the formatted date
            startDateMillis = dateMillis

            startdatesent = formatDate(dateMillis)
            val formattedDate = formatDate2(dateMillis)
            editText.setText(formattedDate)
        }
    }

    private fun selectTrainingPlanEndDate(editText: AppCompatEditText) {

        val mainStartDate = addMicroCycleBinding.startDate.text.toString()
        val maxStartDate = addMicroCycleBinding.endDate.text.toString()

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
            maxDateMillis  // Maximum date for selection
        ) { dateMillis ->
            // When a date is selected, update startDateMillis and display the formatted date
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


    // Utility function to format the date to a readable format
    private fun formatDate(dateMillis: Long): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date(dateMillis))
    }


    private fun formatDate2(dateMillis: Long): String {
        val format = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
        return format.format(Date(dateMillis))
    }

    private fun showWorkloadDialog(
        listener: OnItemClickListener.WorkloadDialogListener,
        currentProgress: Int
    ) {
        try {
            Log.d("Dialog", "Preparing to show dialog")

            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.dialog_workload)
            dialog.window?.apply {
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                val displayMetrics = DisplayMetrics()
                windowManager.defaultDisplay.getMetrics(displayMetrics)
                val width = (displayMetrics.widthPixels * 0.9f).toInt()
                setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)
            }

            val seekBar: SeekBar = dialog.findViewById(R.id.seekbar_workload)
            seekBar.progress = currentProgress

            val applyButton: Button = dialog.findViewById(R.id.btn_apply)
            val closeButton: Button = dialog.findViewById(R.id.btn_cancel)

            val gradientDrawable = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(
                    Color.parseColor("#F3F3F3"), // Yellow
                    Color.parseColor("#10E218"), // Orange
                    Color.parseColor("#E2C110"), // Red
                    Color.parseColor("#F17A0B"), // Purple
                    Color.parseColor("#FF0000")  // Blue
                )
            )

            gradientDrawable.gradientType = GradientDrawable.LINEAR_GRADIENT
            gradientDrawable.cornerRadius = 8f

            seekBar.progressDrawable = gradientDrawable


            applyButton.setOnClickListener {
                val progress = seekBar.progress
                val (colorCode, _) = getWorkloadColorAndString(progress)
                listener.onWorkloadProgressSelected(progress, colorCode)
                Log.d("Dialog", "Progress: $progress, Color: $colorCode")
                dialog.dismiss()
            }

            closeButton.setOnClickListener { dialog.dismiss() }

            dialog.show()
            Log.d("Dialog", "Dialog displayed successfully")

        } catch (e: Exception) {
            Log.e("Exception", "Error showing dialog: ${e.message}")
        }
    }

    // Helper function to return color code and workload string
    private fun getWorkloadColorAndString(progress: Int): Pair<String, String> {
        return when {
            progress in 82..100 -> Pair("#FF0000", "$progress") // Red
            progress in 60..81 -> Pair("#F1790B", "$progress") // Orange
            progress in 45..59 -> Pair("#E2C110", "$progress") // Yellow
            progress in 10..44 -> Pair("#10E218", "$progress") // Green
            progress in 0..9 -> Pair("#F3F3F3", "$progress") // Light Grey
            else -> Pair("#000000", "Invalid") // Default Black
        }
    }

        private fun showAbilityDialog() {
            try {
                Log.d("Dialog", "Preparing to show dialog")

                val dialog = Dialog(this)
                val displayMetrics = DisplayMetrics()
                windowManager.defaultDisplay.getMetrics(displayMetrics)
                dialog.window?.apply {
                    setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    val width = (displayMetrics.widthPixels * 0.9f).toInt()
                    val height = WindowManager.LayoutParams.WRAP_CONTENT
                    setLayout(width, height)
                }

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setCancelable(true)
                dialog.setContentView(R.layout.dialog_abilitie)
                dialog.show()

                val save = dialog.findViewById<Button>(R.id.appCompatButton)
                save.setOnClickListener {
                    saveSelectedAbility()
                    dialog.cancel()
                }

                val add = dialog.findViewById<LinearLayout>(R.id.add_layout)
                val recyclerView: RecyclerView = dialog.findViewById(R.id.recyclerView)
                recyclerView.layoutManager = LinearLayoutManager(this)

                // Fetch abilities initially
                fetchAbilities(recyclerView)

                // Add button click to show `AddAbilitiesDialog` and refresh on success
                add.setOnClickListener {
                    showAddAbilitiesDialog {
                        // Refresh RecyclerView after a new ability is added
                        fetchAbilities(recyclerView)
                    }
                }

                Log.d("Dialog", "Dialog displayed successfully")
            } catch (e: Exception) {
                Log.e("Exception", "Error showing dialog: ${e.message}")
            }
        }

        private fun fetchAbilities(recyclerView: RecyclerView) {
            apiInterface.Get_Abilitiees()?.enqueue(object : Callback<AddAblilityClass> {
                override fun onResponse(
                    call: Call<AddAblilityClass>,
                    response: Response<AddAblilityClass>
                ) {
                    Log.d("TAG", "Response code: ${response.code()}")

                    when (response.code()) {
                        200 -> {
                            val abilityDataList = response.body()?.data
                            if (!abilityDataList.isNullOrEmpty()) {
                                splist.clear() // Clear previous data
                                splist.addAll(abilityDataList) // Add new data

                                Log.d("success", "${splist}")

                                recyclerView.adapter =
                                    AbilitiesAdapter(splist, this@AddMicroCycleActivity)
                            } else {
                                Log.d("DATA_TAG", "Response body is null or empty")
                            }
                        }

                        403 -> {
                            Utils.setUnAuthDialog(this@AddMicroCycleActivity)
                        }

                        else -> {
                            Toast.makeText(
                                this@AddMicroCycleActivity,
                                response.message(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                override fun onFailure(call: Call<AddAblilityClass>, t: Throwable) {
                    Log.d("TAG Category", "Error: ${t.message}")
                }
            })
        }

        private fun saveSelectedAbility() {
            try {
                Log.d("splist Contents", splist.toString())

                val selectedAbilities = splist.filter { it.isSelected }

                ablilityContainer.removeAllViews()

                Log.d("Selected Abilities", "Count: ${selectedAbilities.size}")

                selectedAbilityIds.clear()
                selectedAbilityIds.addAll(selectedAbilities.map { it.id })

                addMicroCycleBinding.card.visibility = if (selectedAbilities.isNotEmpty()) View.VISIBLE else View.GONE
                val params = addMicroCycleBinding.linearLayout6.layoutParams as? ViewGroup.MarginLayoutParams
                params?.let {
                    it.bottomMargin = 0
                    addMicroCycleBinding.linearLayout6.layoutParams = it
                }

                for (ability in selectedAbilities) {
                    val abilityLayout = LayoutInflater.from(this)
                        .inflate(R.layout.ability_item, ablilityContainer, false)

                    val textView: TextView = abilityLayout.findViewById(R.id.ability_txt)
                    textView.text = ability.name

                    ablilityContainer.addView(abilityLayout)
                }

                Log.d("Abilities Added", "Number of abilities added: ${selectedAbilities.size}")

                if (selectedAbilities.isEmpty()) {
                    Log.d("Selection Info", "No abilities were selected.")
                }
            } catch (e: Exception) {
                Log.e("Exception", "Error: ${e.message}")
            }
        }

    private fun showAddAbilitiesDialog(onAbilityAdded: () -> Unit) {
        try {
            Log.d("Dialog", "Preparing to show dialog")

            val dialog = Dialog(this)
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)

            dialog.window?.apply {
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                val width = (displayMetrics.widthPixels * 0.9f).toInt()
                val height = WindowManager.LayoutParams.WRAP_CONTENT
                setLayout(width, height)
            }

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.dialog_add_abilities)
            dialog.show()

            val abilityName: EditText = dialog.findViewById(R.id.etAbilities)
            val addButton: TextView = dialog.findViewById(R.id.btnAdd)
            val cancelButton: TextView = dialog.findViewById(R.id.btnCancel)

            cancelButton.setOnClickListener {
                dialog.cancel()
            }

            addButton.setOnClickListener {
                val name = abilityName.text.toString().trim()

                if (name.isNotEmpty()) {
                    apiInterface.Create_Abilities(name)
                        ?.enqueue(object : Callback<AbilityData> {
                            override fun onResponse(
                                call: Call<AbilityData>,
                                response: Response<AbilityData>
                            ) {
                                Log.d("APIResponse", "Response: ${response.code()}")

                                if (response.isSuccessful) {
                                    Toast.makeText(this@AddMicroCycleActivity, "Ability Added", Toast.LENGTH_SHORT).show()
                                    dialog.dismiss()

                                    // Notify that an ability has been added
                                    onAbilityAdded()
                                } else {
                                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                                    Log.e("API Error", "Response: ${response.code()} - $errorBody")
                                    Toast.makeText(
                                        this@AddMicroCycleActivity,
                                        "Error: $errorBody",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            override fun onFailure(call: Call<AbilityData>, t: Throwable) {
                                Log.e("API Error", "Error: ${t.message}")
                                Toast.makeText(
                                    this@AddMicroCycleActivity,
                                    "Network error: ${t.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                } else {
                    Toast.makeText(this, "Ability name cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Log.e("Exception", "Error showing dialog: ${e.message}")
        }
    }

}