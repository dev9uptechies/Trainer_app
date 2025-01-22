package com.example.trainerapp.training_plan.view_planning_cycle

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

    private val selectedDateRanges = mutableListOf<Pair<Long, Long>>()
    private val trainingPlanLayouts = mutableListOf<View>()
    private val missingIndices = mutableListOf<Int>()

    private val dateRangeMap = mutableMapOf<Int, Pair<String, String>>()
    private val dateRangeMapend = mutableMapOf<Int, Pair<String, String>>()
    private var activeLayoutIndex: Int? = null
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


    private var startdatesent:String? = null
    private var enddatesent:String? = null


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
                        end_date = endDateMillis.toString(),
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
//                val startDate: AppCompatEditText = layout.findViewById(R.id.ent_start_date_liner)
//                val endDate: AppCompatEditText = layout.findViewById(R.id.ent_ent_date_liner)

                if (nameEditText.text!!.isNotEmpty() && startdatesent!!.isNotEmpty() && enddatesent!!.isNotEmpty()) {
                    preSeason.add(
                        AddMesocyclePresession(
                            id = id,
                            planning_ps_id = seasonId.toString(),
                            name = nameEditText.text.toString(),
                            start_date = startdatesent.toString(),
                            end_date = enddatesent.toString(),

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

        val nameEditText: AppCompatEditText =
            newTrainingPlanLayout.findViewById(R.id.ent_pre_sea_name)
        nameEditText.setText("Mesocycle ${indexToAdd + 1}")

        val startDateEditText: AppCompatEditText =
            newTrainingPlanLayout.findViewById(R.id.ent_start_date_liner)

        val endDateEditText: AppCompatEditText = newTrainingPlanLayout.findViewById(R.id.ent_ent_date_liner)
        val endDateCard: CardView = newTrainingPlanLayout.findViewById(R.id.card_end_pick_list)
        val StartDateCard: CardView = newTrainingPlanLayout.findViewById(R.id.card_start_date_list)


        startDateEditText.setOnClickListener {
            // Get the layout index based on the active layout in the container
            val layoutIndex = trainingPlanContainer.indexOfChild(newTrainingPlanLayout)
            activeLayoutIndex = layoutIndex

            val minDateMillis =
                formatDateToMillis(addMesoCycleBinding.startDate.text.toString())
            val maxDateMillis =
                formatDateToMillis(addMesoCycleBinding.endDate.text.toString())

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
                formatDateToMillis(addMesoCycleBinding.startDate.text.toString())
            val maxDateMillis =
                formatDateToMillis(addMesoCycleBinding.endDate.text.toString())

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
                formatDateToMillis(addMesoCycleBinding.startDate.text.toString())
            val maxDateMillis =
                formatDateToMillis(addMesoCycleBinding.endDate.text.toString())

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
                formatDateToMillis(addMesoCycleBinding.startDate.text.toString())
            val maxDateMillis =
                formatDateToMillis(addMesoCycleBinding.endDate.text.toString())

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


        if(startdatesent == enddatesent){
            addMesoCycleBinding.cardSave.isEnabled = false
        }else{
            addMesoCycleBinding.cardSave.isEnabled = true
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

                return selectedDateRanges.withIndex().any { (index, range) ->
                    val (start, end) = range
                    index < layoutIndex && dateInMillis in start..end
                }
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
        val mainStartDate = addMesoCycleBinding.startDate.text.toString()
        val maxStartDate = addMesoCycleBinding.endDate.text.toString()

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

            startdatesent = formattedDate

            val formattedDateSet = formatDate2(dateMillis)
            editText.setText(formattedDateSet)
        }
    }

    private fun formatDate2(dateMillis: Long): String {
        val format = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
        return format.format(Date(dateMillis))
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