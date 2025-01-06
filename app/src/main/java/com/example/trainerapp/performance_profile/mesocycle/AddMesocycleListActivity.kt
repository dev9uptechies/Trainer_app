package com.example.trainerapp.performance_profile.mesocycle

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.example.model.training_plan.TrainingPlanData
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
import com.example.trainerapp.databinding.ActivityAddMesocycleListBinding
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

class AddMesocycleListActivity : AppCompatActivity() {
    lateinit var addMesoCycleBinding: ActivityAddMesocycleListBinding
    private lateinit var apiInterface: APIInterface
    private lateinit var preferenceManager: PreferencesManager
    private lateinit var apiClient: APIClient

    private lateinit var trainingPlanContainer: LinearLayout
    private var trainingPlanCount = 0

    private val trainingPlanLayouts = mutableListOf<View>()
    private val missingIndices = mutableListOf<Int>()

    private var startdatesent:String? = null
    private var enddatesent:String? = null
    lateinit var programData: MutableList<TrainingPlanData.TrainingPlan>

    private var preSeason: MutableList<AddMesocyclePresession> = mutableListOf()
    private var preCompetitive: MutableList<AddMesocyclePreCompatitive> = mutableListOf()
    private var competitive: MutableList<AddMesocycleCompatitive> = mutableListOf()
    private var transition: MutableList<AddMesocycleTransition> = mutableListOf()

    private var id: Int? = 0
    private var mainid: Int = 0
    private var seasonId: Int = 0

    private var startDate: String? = null
    private var endDate: String? = null
    private var planning_ps_id: Int = 0
    private var cardType: String? = null

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
                        Utils.setUnAuthDialog(this@AddMesocycleListActivity)
                    } else {
                        Toast.makeText(
                            this@AddMesocycleListActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Toast.makeText(
                        this@AddMesocycleListActivity,
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
        addMesoCycleBinding = ActivityAddMesocycleListBinding.inflate(layoutInflater)
        setContentView(addMesoCycleBinding.root)
        initViews()
        checkButtonClick()
    }

    private fun checkButtonClick() {
        addMesoCycleBinding.cardSave.setOnClickListener {
            loadData()
        }

        addMesoCycleBinding.cardAdd.setOnClickListener {
            addTrainingPlan()
        }

        addMesoCycleBinding.add.setOnClickListener {
            addTrainingPlan()

        }

        addMesoCycleBinding.back.setOnClickListener {
            finish()
        }
    }

    private fun loadData() {
        Log.d("Card Type :-", "$cardType")
        when (cardType) {
            "pre_session" -> {
                savePresession()
            }

            "pre_competitive" -> {
                savePreCompetitive()
            }

            "competitive" -> {
                saveCompetitive()
            }

            "transition" -> {
                saveTransition()
            }
        }
    }

    private fun initViews() {
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        preferenceManager = PreferencesManager(this)

        mainid = intent.getIntExtra("MainId", 0)
        seasonId = intent.getIntExtra("SeasonId", 0)
        startDate = intent.getStringExtra("startDate") ?: "N/A"
        endDate = intent.getStringExtra("endDate") ?: "N/A"
        cardType = intent.getStringExtra("CardType")
        trainingPlanContainer = addMesoCycleBinding.linerAddTraining

        Log.d("Pre seson data :- ", "$mainid    $id   $startDate   $endDate   $cardType")

        programData = mutableListOf()

        addMesoCycleBinding.startDate.text = startDate
        addMesoCycleBinding.endDate.text = endDate
    }

    private fun saveTransition() {
        try {
            transition.clear()
            for (i in 0 until trainingPlanContainer.childCount) {
                val layout = trainingPlanContainer.getChildAt(i)
                val nameEditText: AppCompatEditText = layout.findViewById(R.id.ent_pre_sea_name)
                val startDate: AppCompatEditText = layout.findViewById(R.id.ent_start_date_liner)
                val endDate: AppCompatEditText = layout.findViewById(R.id.ent_ent_date_liner)
                val delete: ImageView = layout.findViewById(R.id.img_delete)

                val start = startDate.text.toString()
                val end = endDate.text.toString()


                Log.d("TAG", "$start   $end")

                transition.add(
                    AddMesocycleTransition(
                        id = 0,
                        planning_t_id = seasonId.toString(),
                        name = nameEditText.text.toString(),
                        start_date = startdatesent.toString(),
                        end_date = enddatesent.toString(),
                        periods = "0"

                    )
                )
            }

            if (transition != null) {
                apiInterface.AddMesocycleTrainsition(transition)!!
                    .enqueue(object : Callback<GetMessocyclePreSession> {
                        override fun onResponse(
                            call: Call<GetMessocyclePreSession>,
                            response: Response<GetMessocyclePreSession>
                        ) {
                            Log.d("Success Code :-", "${response.code()}")
                            if (response.code() == 200) {
                                if (response.isSuccessful) {
                                    Toast.makeText(
                                        this@AddMesocycleListActivity,
                                        "Data Added sucessfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    finish()
                                    Log.d("Success Code :-", "${response.body()}")
                                } else {
                                    Log.d("Error Code :-", "${response.message()}")

                                }
                            } else {
                                Log.d("Error Code :-", "${response.code()}")
                                Log.d("error", "${response.errorBody()}")
                            }
                        }

                        override fun onFailure(
                            call: Call<GetMessocyclePreSession>,
                            t: Throwable
                        ) {
                            Log.e("API Error", "Failure: ${t.message}")
                            Toast.makeText(
                                this@AddMesocycleListActivity,
                                " " + t.message,
                                Toast.LENGTH_SHORT
                            ).show()
                            call.cancel()
                        }

                    })
            }

        } catch (e: Exception) {
            Log.d("Exception", "${e.message}")
        }
    }

    private fun saveCompetitive() {
        try {
            competitive.clear()
            for (i in 0 until trainingPlanContainer.childCount) {
                val layout = trainingPlanContainer.getChildAt(i)
                val nameEditText: AppCompatEditText = layout.findViewById(R.id.ent_pre_sea_name)
                val startDate: AppCompatEditText = layout.findViewById(R.id.ent_start_date_liner)
                val endDate: AppCompatEditText = layout.findViewById(R.id.ent_ent_date_liner)
                val delete: ImageView = layout.findViewById(R.id.img_delete)

                competitive.add(
                    AddMesocycleCompatitive(
                        id = 0,
                        planning_c_id = seasonId.toString(),
                        name = nameEditText.text.toString(),
                        start_date = startdatesent.toString(),
                        end_date = enddatesent.toString(),
                        periods = "0"
                    )
                )
            }

            if (competitive != null) {
                apiInterface.AddMesocycleComptitive(competitive)!!
                    .enqueue(object : Callback<GetMessocyclePreSession> {
                        override fun onResponse(
                            call: Call<GetMessocyclePreSession>,
                            response: Response<GetMessocyclePreSession>
                        ) {
                            Log.d("Success     Code :-", "${response.code()}")
                            if (response.code() == 200) {
                                if (response.isSuccessful) {
                                    Toast.makeText(
                                        this@AddMesocycleListActivity,
                                        "Data Added sucessfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    finish()
                                    Log.d("Success Code :-", "${response.body()}")
                                } else {
                                    Log.d("Error Code :-", "${response.message()}")
                                }
                            } else {
                                Log.d("Error Code :-", "${response.code()}")
                            }
                        }

                        override fun onFailure(
                            call: Call<GetMessocyclePreSession>,
                            t: Throwable
                        ) {
                            Log.e("API Error", "Failure: ${t.message}")
                            Toast.makeText(
                                this@AddMesocycleListActivity,
                                " " + t.message,
                                Toast.LENGTH_SHORT
                            ).show()
                            call.cancel()
                        }

                    })
            }

        } catch (e: Exception) {
            Log.d("Exception", "${e.message}")
        }
    }

    private fun savePreCompetitive() {
        try {
            preCompetitive.clear()
            for (i in 0 until trainingPlanContainer.childCount) {
                val layout = trainingPlanContainer.getChildAt(i)
                val nameEditText: AppCompatEditText = layout.findViewById(R.id.ent_pre_sea_name)
                val startDate: AppCompatEditText = layout.findViewById(R.id.ent_start_date_liner)
                val endDate: AppCompatEditText = layout.findViewById(R.id.ent_ent_date_liner)
                val delete: ImageView = layout.findViewById(R.id.img_delete)

                preCompetitive.add(
                    AddMesocyclePreCompatitive(
                        id = 0,
                        planning_pc_id = seasonId.toString(),
                        name = nameEditText.text.toString(),
                        start_date = startdatesent.toString(),
                        end_date = enddatesent.toString(),
                        periods = "0"

                    )
                )
            }

            if (preCompetitive != null) {
                apiInterface.AddMesocyclePreComptitive(preCompetitive)!!
                    .enqueue(object : Callback<GetMessocyclePreSession> {
                        override fun onResponse(
                            call: Call<GetMessocyclePreSession>,
                            response: Response<GetMessocyclePreSession>
                        ) {

                            Log.d("Success Code :-", "${response.code()}")
                            Log.d("Success Code :-", " " + response.message())
                            if (response.code() == 200) {
                                if (response.isSuccessful) {
                                    Toast.makeText(
                                        this@AddMesocycleListActivity,
                                        "Data Added sucessfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    finish()
                                    Log.d("Success Code :-", "${response.body()}")
                                } else {
                                    Log.d("Error Code :-", "${response.message()}")
                                }
                            } else {
                                Log.d("Error Code :-", "${response.code()}")
                            }
                        }

                        override fun onFailure(
                            call: Call<GetMessocyclePreSession>,
                            t: Throwable
                        ) {
                            Log.e("API Error", "Failure: ${t.message}")
                            Toast.makeText(
                                this@AddMesocycleListActivity,
                                " " + t.message,
                                Toast.LENGTH_SHORT
                            ).show()
                            call.cancel()
                        }

                    })
            }

        } catch (e: Exception) {
            Log.d("Exception", "${e.message}")
        }
    }

    private fun savePresession() {
        try {
            preSeason.clear()

            for (i in 0 until trainingPlanContainer.childCount) {
                val layout = trainingPlanContainer.getChildAt(i)
                val nameEditText: AppCompatEditText = layout.findViewById(R.id.ent_pre_sea_name)
                val startDate: AppCompatEditText = layout.findViewById(R.id.ent_start_date_liner)
                val endDate: AppCompatEditText = layout.findViewById(R.id.ent_ent_date_liner)

                if (nameEditText.text!!.isNotEmpty() && startDate.text!!.isNotEmpty() && endDate.text!!.isNotEmpty()) {
                    preSeason.add(
                        AddMesocyclePresession(
                            id = id,
                            planning_ps_id = seasonId.toString(),
                            name = nameEditText.text.toString(),
                            start_date = startdatesent.toString(),
                            end_date = enddatesent.toString(),
                            periods = "test"

                        )
                    )
                } else {
                    Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
                    return
                }
            }

            if (preSeason.isNotEmpty()) {
                apiInterface.AddMesocyclePreSeeion(preSeason)!!
                    .enqueue(object : Callback<GetMessocyclePreSession> {
                        override fun onResponse(
                            call: Call<GetMessocyclePreSession>,
                            response: Response<GetMessocyclePreSession>
                        ) {
                            Log.d("Success Code :-", "${response.code()}")
                            if (response.isSuccessful && response.code() == 200) {
                                Log.d("Response Body :-", "${response.body()}")
                                val message = response.body()!!.message ?: "Data added successfully"
                                Toast.makeText(
                                    this@AddMesocycleListActivity,
                                    message,
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
//
                            } else {
                                Log.d("Error Code :-", "${response.code()} - ${response.message()}")
                                Log.d("error", response.message())
                                Log.d("error", response.errorBody().toString())

                                Toast.makeText(
                                    this@AddMesocycleListActivity,
                                    "Error: ${response.message()}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<GetMessocyclePreSession>, t: Throwable) {
                            Log.e("API Error", "Failure: ${t.message}")
                            Toast.makeText(
                                this@AddMesocycleListActivity,
                                "API Error: ${t.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
            } else {
                Toast.makeText(this, "No data to send.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.d("Exception", "${e.message}")
            Toast.makeText(this, "Exception: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addTrainingPlan() {

        addMesoCycleBinding.linerAdd.visibility = View.GONE
        addMesoCycleBinding.scrollView2.visibility = View.VISIBLE

        val indexToAdd = if (missingIndices.isNotEmpty()) {
            val reusedIndex = missingIndices.removeAt(0)
            Log.d("AddTrainingPlan", "Reusing missing index: $reusedIndex")
            reusedIndex
        } else {
            trainingPlanLayouts.size
        }

        val newTrainingPlanLayout = LayoutInflater.from(this)
            .inflate(R.layout.editetriningplanactivityadapter, trainingPlanContainer, false)

        val nameEditText: AppCompatEditText = newTrainingPlanLayout.findViewById(R.id.ent_pre_sea_name)
        nameEditText.setText("Mesocycle ${indexToAdd + 1}")

        val startDateEditText: AppCompatEditText = newTrainingPlanLayout.findViewById(R.id.ent_start_date_liner)
        startDateEditText.setOnClickListener {
            val minDateMillis = formatDateToMillis(addMesoCycleBinding.startDate.text.toString())
            val maxDateMillis = formatDateToMillis(addMesoCycleBinding.endDate.text.toString())

            showDateRangePickerDialog(startDateEditText.context, minDateMillis, maxDateMillis) { start ->
                val formattedStartDate = formatDate(start)
                val formattedStartDate2 = formatDate2(start)

                startDateEditText.setText(formattedStartDate2)
                startdatesent = formattedStartDate
            }
        }

        val startDateCard: CardView = newTrainingPlanLayout.findViewById(R.id.card_start_date_list)
        startDateCard.setOnClickListener {
            val minDateMillis = formatDateToMillis(addMesoCycleBinding.startDate.text.toString())
            val maxDateMillis = formatDateToMillis(addMesoCycleBinding.endDate.text.toString())

            showDateRangePickerDialog(startDateEditText.context, minDateMillis, maxDateMillis) { start ->
                val formattedStartDate = formatDate(start)
                val formattedStartDate2 = formatDate2(start)

                startDateEditText.setText(formattedStartDate2)
                startdatesent = formattedStartDate
            }
        }

        val endDateEditText: AppCompatEditText = newTrainingPlanLayout.findViewById(R.id.ent_ent_date_liner)
        endDateEditText.setOnClickListener {
            val minDateMillis = formatDateToMillis(addMesoCycleBinding.startDate.text.toString())
            val maxDateMillis = formatDateToMillis(addMesoCycleBinding.endDate.text.toString())

            showDateRangePickerDialog(startDateEditText.context, minDateMillis, maxDateMillis) { start ->
                val formattedStartDate = formatDate(start)
                val formattedStartDate2 = formatDate2(start)

                endDateEditText.setText(formattedStartDate2)
                enddatesent = formattedStartDate
            }
        }

        val endDateCard: CardView = newTrainingPlanLayout.findViewById(R.id.card_end_pick_list)
        endDateCard.setOnClickListener {
            val minDateMillis = formatDateToMillis(addMesoCycleBinding.startDate.text.toString())
            val maxDateMillis = formatDateToMillis(addMesoCycleBinding.endDate.text.toString())

            showDateRangePickerDialog(startDateEditText.context, minDateMillis, maxDateMillis) { start ->
                val formattedStartDate = formatDate(start)
                val formattedStartDate2 = formatDate2(start)

                endDateEditText.setText(formattedStartDate2)
                enddatesent = formattedStartDate
            }
        }

        trainingPlanLayouts.add(indexToAdd, newTrainingPlanLayout)
        trainingPlanContainer.addView(newTrainingPlanLayout, indexToAdd)

        Log.d("AddTrainingPlan", "Added training plan at index: $indexToAdd")
        val delete: ImageView = newTrainingPlanLayout.findViewById(R.id.img_delete)
        delete.setOnClickListener {
            removeTrainingPlan(newTrainingPlanLayout)
        }
        updateTrainingPlanIndices()
    }

    fun showDateRangePickerDialog(
        context: Context,
        minDateMillis: Long,
        maxDateMillis: Long,
        callback: (start: Long) -> Unit
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

        calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE)

        cancelButton.setOnClickListener { dialog.dismiss() }

        calendarView.state().edit()
            .setCalendarDisplayMode(CalendarMode.MONTHS)
            .commit()

        calendarView.addDecorator(object : DayViewDecorator {
            val today = CalendarDay.today()
            override fun shouldDecorate(day: CalendarDay?): Boolean {
                return day == today
            }

            override fun decorate(view: DayViewFacade?) {
                view?.addSpan(ForegroundColorSpan(Color.WHITE)) // Text color for today
                ContextCompat.getDrawable(context, R.drawable.todays_date_selecte)?.let {
                    view?.setBackgroundDrawable(it)
                }
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

        confirmButton.setOnClickListener {
            val selectedDates = calendarView.selectedDates

            if (selectedDates.isNotEmpty()) {
                val selectedDate = selectedDates.first().calendar

                selectedDate.set(Calendar.HOUR_OF_DAY, 0)
                selectedDate.set(Calendar.MINUTE, 0)
                selectedDate.set(Calendar.SECOND, 0)
                selectedDate.set(Calendar.MILLISECOND, 0)

                callback(selectedDate.timeInMillis)

                dialog.dismiss()
            } else {
                textView.text = "Please select a date"
                textView.setTextColor(Color.RED)
            }
        }

        dialog.show()
    }

    private fun selectTrainingPlanStartDate(editText: AppCompatEditText) {
        // Get the main start and max end dates from the binding
        val mainStartDate = addMesoCycleBinding.startDate.text.toString()
        val maxStartDate = addMesoCycleBinding.endDate.text.toString()

        // Check if mainStartDate and maxStartDate are valid dates
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
            val formattedDate = formatDate(dateMillis)
            editText.setText(formattedDate)
        }
    }

    private fun selectTrainingPlanEndDate(editText: AppCompatEditText) {

        val mainStartDate = addMesoCycleBinding.startDate.text.toString()
        val maxStartDate = addMesoCycleBinding.endDate.text.toString()

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
            val formattedDate = formatDate(dateMillis)
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
}