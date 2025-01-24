package com.example.trainerapp.training_plan.micro

import android.app.Dialog
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.Adapter.training_plan.view.AbilitiesAdapter
import com.example.Adapter.training_plan.view.ViewMicrocycleListAdapter
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
import com.example.trainerapp.PreferencesManager
import com.example.trainerapp.R
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.ActivityEditMicroCycleBinding
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class EditMicroCycleActivity : AppCompatActivity(), OnItemClickListener.OnItemClickCallback {

    lateinit var editMicroCycleActivity: ActivityEditMicroCycleBinding

    private lateinit var apiInterface: APIInterface
    private lateinit var preferenceManager: PreferencesManager
    private lateinit var apiClient: APIClient

    private val dateRangeMap = mutableMapOf<Int, Pair<String, String>>()
    private var activeLayoutIndex: Int? = null
    private val selectedDateRanges = mutableListOf<Pair<Long, Long>>()
    private lateinit var ablilityContainer: LinearLayout
    private var splist: MutableList<AbilityData> = mutableListOf()
    private lateinit var trainingPlanContainer: LinearLayout
    private var programData: MutableList<GetMicrocycle.Data> = mutableListOf()
    private var AbilityData: MutableList<AddMicrocyclePreSeason> = mutableListOf()
    private var seasonid: Int = 0
    private var mainId: Int = 0
    private var startdate: String? = null
    private var endDate: String? = null
    private var cardType: String? = null
    private val missingIndices = mutableListOf<Int>()
    var size = 0

    var abilityid:String?= null
    var errorstartdate:String?= null
    var errorenddate:String?= null

    private var recyclerView2 :RecyclerView?= null
    private var preSeason: MutableList<AddMicrocyclePreSeason> = mutableListOf()
    private var preCompetitive: MutableList<AddMicrocyclePreCompatitive> = mutableListOf()
    private var competitive: MutableList<AddMicrocycleCompatitive> = mutableListOf()
    private var transient: MutableList<AddMicrocycleTransition> = mutableListOf()


    private var startdatesent:String? = null
    private var enddatesent:String? = null
    private val trainingPlanLayouts = mutableListOf<View>()
    private val selectedAbilityIds = mutableListOf<Int>()
    private var startDateMillis: Long = 0

    val arryList = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        editMicroCycleActivity = ActivityEditMicroCycleBinding.inflate(layoutInflater)
        setContentView(editMicroCycleActivity.root)
        initViews()
        buttonCLick()
        loadData()

        editMicroCycleActivity.recycler.layoutManager = LinearLayoutManager(this)
    }

    private fun buttonCLick() {
        editMicroCycleActivity.cardSave.setOnClickListener {
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

        editMicroCycleActivity.add.setOnClickListener {
            addTrainingPlan()
        }

        editMicroCycleActivity.back.setOnClickListener {
            finish()
        }

        editMicroCycleActivity.cardAddAbility.setOnClickListener {
            showAbilityDialog()
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
        ablilityContainer = editMicroCycleActivity.chvvp
        trainingPlanContainer = editMicroCycleActivity.linerAddTraining

        Log.d(
            "EditeMesocycle",
            "Id: $seasonid, MainId: $mainId, StatDate: $startdate, EndDate: $endDate, CardType: $cardType"
        )

        editMicroCycleActivity.startDate.text = startdate
        editMicroCycleActivity.endDate.text = endDate

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

    private fun getPreSessionData(id: Int) {
        try {
            editMicroCycleActivity.progresBar.visibility = View.VISIBLE
            apiInterface.GetMicrocyclePreSeason(id).enqueue(object : Callback<GetMicrocycle> {
                override fun onResponse(
                    call: Call<GetMicrocycle>,
                    response: Response<GetMicrocycle>
                ) {
                    editMicroCycleActivity.progresBar.visibility = View.GONE
                    Log.d("TAG", response.code().toString() + "")
                    Log.d("TAG", response.message().toString() + "")

                    val code = response.code()
                    if (code == 200) {
                        if (response.isSuccessful) {
                            response.body()?.let { responseBody ->
                                programData = responseBody.data!!.toMutableList()
                                for (mesocycles in programData) {

                                    Log.d("mesocycles.pre_season", "mesocycles.pre_season: ${programData.get(0).psMicrocycleAbility!!.get(0).abilityId}")
                                    Log.d("mesocycles.pre_season", "mesocycles.pre_season: ${programData.get(0)}")



                                    addTrainingPlan(mesocycles)

                                }
                            }
                        } else {
                            Log.e("Error", "Failed to fetch data")
                        }
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@EditMicroCycleActivity)
                    } else {
                        Toast.makeText(
                            this@EditMicroCycleActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<GetMicrocycle>, t: Throwable) {
                    editMicroCycleActivity.progresBar.visibility = View.GONE
                    Log.e("Error", "Network Error: ${t.message}")
                }
            })
        } catch (e: Exception) {
            Log.d("Excation", e.message.toString())
        }
    }


    private fun getPreCompatiiveData(id: Int) {
        try {
            editMicroCycleActivity.progresBar.visibility = View.VISIBLE
            apiInterface.GetMicrocyclePreCompatitive(id).enqueue(object : Callback<GetMicrocycle> {
                override fun onResponse(
                    call: Call<GetMicrocycle>,
                    response: Response<GetMicrocycle>
                ) {
                    editMicroCycleActivity.progresBar.visibility = View.GONE
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
                        Utils.setUnAuthDialog(this@EditMicroCycleActivity)
                    } else {
                        Toast.makeText(
                            this@EditMicroCycleActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<GetMicrocycle>, t: Throwable) {
                    editMicroCycleActivity.progresBar.visibility = View.GONE
                    Log.e("Error", "Network Error: ${t.message}")
                }
            })
        } catch (e: Exception) {
            Log.d("Excation", e.message.toString())
        }
    }

    private fun getTransitionData(id: Int) {
        try {
            editMicroCycleActivity.progresBar.visibility = View.VISIBLE
            apiInterface.GetMicrocycleTransition(id).enqueue(object : Callback<GetMicrocycle> {
                override fun onResponse(
                    call: Call<GetMicrocycle>,
                    response: Response<GetMicrocycle>
                ) {
                    editMicroCycleActivity.progresBar.visibility = View.GONE
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
                        Utils.setUnAuthDialog(this@EditMicroCycleActivity)
                    } else {
                        Toast.makeText(
                            this@EditMicroCycleActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<GetMicrocycle>, t: Throwable) {
                    editMicroCycleActivity.progresBar.visibility = View.GONE
                    Log.e("Error", "Network Error: ${t.message}")
                }
            })
        } catch (e: Exception) {
            Log.d("Excation", e.message.toString())
        }
    }

    private fun getCompatiiveData(id: Int) {
        try {
            editMicroCycleActivity.progresBar.visibility = View.VISIBLE
            apiInterface.GetMicrocycleCompatitive(id).enqueue(object : Callback<GetMicrocycle> {
                override fun onResponse(
                    call: Call<GetMicrocycle>,
                    response: Response<GetMicrocycle>
                ) {
                    editMicroCycleActivity.progresBar.visibility = View.GONE
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
                        Utils.setUnAuthDialog(this@EditMicroCycleActivity)
                    } else {
                        Toast.makeText(
                            this@EditMicroCycleActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<GetMicrocycle>, t: Throwable) {
                    editMicroCycleActivity.progresBar.visibility = View.GONE
                    Log.e("Error", "Network Error: ${t.message}")
                }
            })
        } catch (e: Exception) {
            Log.d("Excation", e.message.toString())
        }
    }

    private fun addTrainingPlan(mesocycle: GetMicrocycle.Data? = null) {
        try {
            Log.d("TAG", "Mesocycle: $mesocycle")

            val indexToAdd = if (missingIndices.isNotEmpty()) {
                val reusedIndex = missingIndices.removeAt(0)
                Log.d("AddTrainingPlan", "Reusing missing index: $reusedIndex")
                reusedIndex
            } else {
                trainingPlanLayouts.size
            }

            val newTrainingPlanLayout = LayoutInflater.from(this)
                .inflate(R.layout.add_microcycle_layout, trainingPlanContainer, false)
            val nameEditText: AppCompatEditText = newTrainingPlanLayout.findViewById(R.id.ent_pre_sea_name)
            nameEditText.setText("Mesocycle ${indexToAdd + 1}")

            val startDateEditText: AppCompatEditText = newTrainingPlanLayout.findViewById(R.id.ent_start_date_liner)
            val endDateEditText: AppCompatEditText = newTrainingPlanLayout.findViewById(R.id.ent_ent_date_liner)
            val seekBar: SeekBar = newTrainingPlanLayout.findViewById(R.id.seekbar_workload_layout)

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


            val startDateCard: CardView = newTrainingPlanLayout.findViewById(R.id.card_start_date_list)
            val endDateCard: CardView = newTrainingPlanLayout.findViewById(R.id.card_end_pick_list)


            startDateEditText.setOnClickListener {
                // Get the layout index based on the active layout in the container
                val layoutIndex = trainingPlanContainer.indexOfChild(newTrainingPlanLayout)
                activeLayoutIndex = layoutIndex

                val minDateMillis =
                    formatDateToMillis(editMicroCycleActivity.startDate.text.toString())
                val maxDateMillis =
                    formatDateToMillis(editMicroCycleActivity.endDate.text.toString())

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
                    formatDateToMillis(editMicroCycleActivity.startDate.text.toString())
                val maxDateMillis =
                    formatDateToMillis(editMicroCycleActivity.endDate.text.toString())

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
                    formatDateToMillis(editMicroCycleActivity.startDate.text.toString())
                val maxDateMillis =
                    formatDateToMillis(editMicroCycleActivity.endDate.text.toString())

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
                    formatDateToMillis(editMicroCycleActivity.startDate.text.toString())
                val maxDateMillis =
                    formatDateToMillis(editMicroCycleActivity.endDate.text.toString())

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


            val delete: ImageView = newTrainingPlanLayout.findViewById(R.id.img_delete)
            delete.setOnClickListener {
                trainingPlanContainer.removeView(newTrainingPlanLayout)
                if (indexToAdd in trainingPlanLayouts.indices) {
                    trainingPlanLayouts.removeAt(indexToAdd)
                    Log.d("EditMicroCycleActivity", "Removed layout at index: $indexToAdd")
                } else {
                    Log.e(
                        "EditMicroCycleActivity",
                        "Index $indexToAdd is out of bounds for trainingPlanLayouts of size ${trainingPlanLayouts.size}"
                    )
                    Toast.makeText(
                        this,
                        "Error: Unable to remove training plan. Invalid index.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                if (mesocycle != null && mesocycle.id != null) {
                    removeTrainingPlan(newTrainingPlanLayout, mesocycle.id)
                } else {
                    Log.e("EditMicroCycleActivity", "Mesocycle or its ID is null")
                }
            }

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

//
//            if(startdatesent == enddatesent){
//                editMicroCycleActivity.cardSave.isEnabled = false
//            }else{
//                editMicroCycleActivity.cardSave.isEnabled = true
//            }


            gradientDrawable.gradientType = GradientDrawable.LINEAR_GRADIENT
            gradientDrawable.cornerRadius = 8f

            seekBar.progressDrawable = gradientDrawable
            seekBar.isEnabled = false

            if (mesocycle != null) {
                val startDateMillis = formatDateToMillis2(mesocycle.startDate)
                val formattedStartDate = formatMillisToDateString(startDateMillis)

                val endDateInMillis = formatDateToMillis2(mesocycle.endDate)
                val formattedEndDate = formatMillisToDateString(endDateInMillis)

                startDateEditText.setText(formattedStartDate)
                endDateEditText.setText(formattedEndDate)
                seekBar.progress = mesocycle.workload ?: 0

                val errorStartDateMillis = parseFormattedDateToMillis(formattedStartDate)
                val errorEndDateMillis = parseFormattedDateToMillis(formattedEndDate)

                selectedDateRanges.add(startDateMillis to endDateInMillis)


                errorstartdate = formatDate(errorStartDateMillis)
                errorenddate = formatDate(errorEndDateMillis)

                Log.d("just", "Checking Ability IDs")
                mesocycle.psMicrocycleAbility?.let { preSeason ->

                    ablilityContainer.removeAllViews()

                    val addedAbilityIds = mutableSetOf<Int>()

                    val abilityIds = preSeason.mapNotNull { it.abilityId }
                    Log.d("TAG", "Pre-Season: $preSeason, Ability IDs: $abilityIds")

                    preSeason.forEach { abilityData ->
                        val abilityId = abilityData.abilityId
                        val abilityName = abilityData.ability?.name

                        abilityid = abilityId.toString()

                        // Check if the ability ID is not already added
                        if (abilityId != null && addedAbilityIds.add(abilityId.toInt())) {
                            val abilityLayout = LayoutInflater.from(this)
                                .inflate(R.layout.ability_item, ablilityContainer, false)

                            val textView: TextView = abilityLayout.findViewById(R.id.ability_txt)
                            editMicroCycleActivity.card.visibility = View.VISIBLE
                            textView.text = abilityName

                            ablilityContainer.addView(abilityLayout) // Add the layout to the container
                            Log.d("TAG", "Added TextView for Ability Name: $abilityName")
                        }
                    }
                } ?: run {
                    Log.d("TAG", "Pre-season is null for Mesocycle ID: ${mesocycle.id}")
                }



            } else {
                Log.d("TAG", "mesocycle is null.")
            }

            trainingPlanLayouts.add(newTrainingPlanLayout)
            trainingPlanContainer.addView(newTrainingPlanLayout)

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
                val sortedDates = selectedDates.sortedBy { it.calendar.timeInMillis }

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

    fun updateOrAddDateRange(layoutIndex: Int?, startMillis: Long, endMillis: Long, remove: Boolean = false) {
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
        return start1 <= end2 && start2 <= end1
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
    private fun removeTrainingPlan(trainingPlanLayout: View, cycleId: Int) {

        when (cardType) {
            "pre_session" -> {
                editMicroCycleActivity.progresBar.visibility = View.VISIBLE
                apiInterface.delete_MicrocyclePreSession(id = cycleId, psid = seasonid)
                    .enqueue(object : Callback<GetMicrocycle> {
                        override fun onResponse(
                            call: Call<GetMicrocycle>,
                            response: Response<GetMicrocycle>
                        ) {
                            editMicroCycleActivity.progresBar.visibility = View.GONE
                            Log.d("delete_tag", "Invalid ID ${response.code()} ")
                            Log.d("delete_tag", "Invalid ID ${response.body()} ")
                            Log.d("delete_tag", "Invalid ID ${response.errorBody()} ")

                            Log.d("TAG", response.code().toString() + "")
                            val code = response.code()
                            if (code == 200) {
                                if (response.isSuccessful && response.body() != null) {
                                    val message = response.body()?.message ?: "Item deleted"
                                    Toast.makeText(
                                        this@EditMicroCycleActivity,
                                        message,
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    finish()
                                } else {
                                    Log.d("delete_tag", "Failed to delete: ${response.code()}")
                                    Toast.makeText(
                                        this@EditMicroCycleActivity,
                                        "Failed to delete",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    loadData()
                                }
                            } else if (code == 403) {
                                Utils.setUnAuthDialog(this@EditMicroCycleActivity)
                            } else {
                                Toast.makeText(
                                    this@EditMicroCycleActivity,
                                    "" + response.message(),
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                call.cancel()
                            }


                        }

                        override fun onFailure(call: Call<GetMicrocycle>, t: Throwable) {
                            editMicroCycleActivity.progresBar.visibility = View.GONE
                            Log.d("delete_tag", "Error: ${t.message}")
                            Toast.makeText(
                                this@EditMicroCycleActivity,
                                t.message,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            call.cancel()
                        }
                    })
            }

            "pre_competitive" -> {
                editMicroCycleActivity.progresBar.visibility = View.VISIBLE
                apiInterface.delete_MicrocyclePreCompatitive(id = cycleId, psid = seasonid)
                    .enqueue(object : Callback<GetMicrocycle> {
                        override fun onResponse(
                            call: Call<GetMicrocycle>,
                            response: Response<GetMicrocycle>
                        ) {
                            editMicroCycleActivity.progresBar.visibility = View.GONE
                            Log.d("delete_tag", "Invalid ID ${response.code()} ")
                            Log.d("delete_tag", "Invalid ID ${response.body()} ")
                            Log.d("delete_tag", "Invalid ID ${response.errorBody()} ")

                            Log.d("TAG", response.code().toString() + "")
                            val code = response.code()
                            if (code == 200) {
                                if (response.isSuccessful && response.body() != null) {
                                    val message = response.body()?.message ?: "Item deleted"
                                    Toast.makeText(
                                        this@EditMicroCycleActivity,
                                        message,
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    finish()
                                } else {
                                    Log.d("delete_tag", "Failed to delete: ${response.code()}")
                                    Toast.makeText(
                                        this@EditMicroCycleActivity,
                                        "Failed to delete",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    loadData()
                                }
                            } else if (code == 403) {
                                Utils.setUnAuthDialog(this@EditMicroCycleActivity)
                            } else {
                                Toast.makeText(
                                    this@EditMicroCycleActivity,
                                    "" + response.message(),
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                call.cancel()
                            }


                        }

                        override fun onFailure(call: Call<GetMicrocycle>, t: Throwable) {
                            editMicroCycleActivity.progresBar.visibility = View.GONE
                            Log.d("delete_tag", "Error: ${t.message}")
                            Toast.makeText(
                                this@EditMicroCycleActivity,
                                t.message,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            call.cancel()
                        }
                    })
            }

            "competitive" -> {
                editMicroCycleActivity.progresBar.visibility = View.VISIBLE
                apiInterface.delete_MicrocycleCompatitive(id = cycleId, psid = seasonid)
                    .enqueue(object : Callback<GetMicrocycle> {
                        override fun onResponse(
                            call: Call<GetMicrocycle>,
                            response: Response<GetMicrocycle>
                        ) {

                            Log.d("delete_tag", "Invalid ID ${response.code()} ")
                            Log.d("delete_tag", "Invalid ID ${response.body()} ")
                            Log.d("delete_tag", "Invalid ID ${response.errorBody()} ")
                            editMicroCycleActivity.progresBar.visibility = View.GONE
                            Log.d("TAG", response.code().toString() + "")
                            val code = response.code()
                            if (code == 200) {
                                if (response.isSuccessful && response.body() != null) {
                                    val message = response.body()?.message ?: "Item deleted"
                                    Toast.makeText(
                                        this@EditMicroCycleActivity,
                                        message,
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    finish()
                                } else {
                                    Log.d("delete_tag", "Failed to delete: ${response.code()}")
                                    Toast.makeText(
                                        this@EditMicroCycleActivity,
                                        "Failed to delete",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    loadData()
                                }
                            } else if (code == 403) {
                                Utils.setUnAuthDialog(this@EditMicroCycleActivity)
                            } else {
                                Toast.makeText(
                                    this@EditMicroCycleActivity,
                                    "" + response.message(),
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                call.cancel()
                            }


                        }

                        override fun onFailure(call: Call<GetMicrocycle>, t: Throwable) {
                            Log.d("delete_tag", "Error: ${t.message}")
                            editMicroCycleActivity.progresBar.visibility = View.GONE
                            Toast.makeText(
                                this@EditMicroCycleActivity,
                                t.message,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            call.cancel()
                        }
                    })
            }

            "transition" -> {
                editMicroCycleActivity.progresBar.visibility = View.VISIBLE
                apiInterface.delete_MicrocycleTransition(id = cycleId, psid = seasonid)
                    .enqueue(object : Callback<GetMicrocycle> {
                        override fun onResponse(
                            call: Call<GetMicrocycle>,
                            response: Response<GetMicrocycle>
                        ) {
                            editMicroCycleActivity.progresBar.visibility = View.GONE
                            Log.d("delete_tag", "Invalid ID ${response.code()} ")
                            Log.d("delete_tag", "Invalid ID ${response.body()} ")
                            Log.d("delete_tag", "Invalid ID ${response.errorBody()} ")

                            Log.d("TAG", response.code().toString() + "")
                            val code = response.code()
                            if (code == 200) {
                                if (response.isSuccessful && response.body() != null) {
                                    val message = response.body()?.message ?: "Item deleted"
                                    Toast.makeText(
                                        this@EditMicroCycleActivity,
                                        message,
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    finish()
                                } else {
                                    Log.d("delete_tag", "Failed to delete: ${response.code()}")
                                    Toast.makeText(
                                        this@EditMicroCycleActivity,
                                        "Failed to delete",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    loadData()
                                }
                            } else if (code == 403) {
                                Utils.setUnAuthDialog(this@EditMicroCycleActivity)
                            } else {
                                Toast.makeText(
                                    this@EditMicroCycleActivity,
                                    "" + response.message(),
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                call.cancel()
                            }


                        }

                        override fun onFailure(call: Call<GetMicrocycle>, t: Throwable) {
                            Log.d("delete_tag", "Error: ${t.message}")
                            editMicroCycleActivity.progresBar.visibility = View.GONE
                            Toast.makeText(
                                this@EditMicroCycleActivity,
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

    private fun editMesocyclePresession() {
        try {
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
                val seekBar: SeekBar = layout.findViewById(R.id.seekbar_workload_layout)
                val workloadProgress = seekBar.progress
                val (colorCode, _) = getWorkloadColorAndString(workloadProgress)
                val errorTextView: TextView = layout.findViewById(R.id.error_start_date_list)
                val EndDateErrorTextView: TextView = layout.findViewById(R.id.error_end_date_list)



                val name = nameEditText?.text.toString().trim()
                val start = startDateEditText?.text.toString().trim()
                val end = endDateEditText?.text.toString().trim()

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

                val newName = "Microcycle ${i + 1}"

                if (preSeason.any { it.name == newName }) {
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

                Log.d("DEBUUUUUG", "Final formatted date: $finalStartDates    end:- $finalEndDates")



                    if (i != 0) {
                        if (isConflict(start, end, i)) {
                            errorTextView.visibility = View.VISIBLE
                            errorTextView.text = "Date conflict detected for Plan ${i + 1}"
                            errorTextView.setTextColor(Color.RED)

//                            Toast.makeText(this, "Date conflict detected for Plan ${i + 1}", Toast.LENGTH_SHORT).show()
                            return
                        } else {
//                            errorTextView.visibility = View.GONE
                        }
                    }


                    Log.d("Data", "Name: $name, Start: $start, End: $end")

                val finalStartDate = if (finalStartDates.isNullOrEmpty()) errorstartdate.toString() else finalStartDates
                val finalEndDate = if (finalEndDates.isNullOrEmpty()) errorenddate.toString() else finalEndDates
                val finalAbility: List<Int> = if (selectedAbilityIds.isNullOrEmpty()) { listOfNotNull(abilityid?.toIntOrNull()) } else { selectedAbilityIds }





                if (finalStartDate == finalEndDate){
                    return
                }else{
                }


                preSeason.add(
                    AddMicrocyclePreSeason(
                        id = if (i < programData.size) programData[i].id else null,
                        ps_mesocycle_id = seasonid,
                        name = newName,
                        start_date = finalStartDate.toString(),
                        end_date = finalEndDate.toString(),
                        workload = workloadProgress,
                        workload_color = colorCode,
                        ability_ids = finalAbility
                    )
                )
            }
            editMicroCycleActivity.progresBar.visibility = View.VISIBLE
            apiInterface.EditeMicrocyclePresession(preSeason)
                ?.enqueue(object : Callback<GetMicrocycle> {
                    override fun onResponse(
                        call: Call<GetMicrocycle>,
                        response: Response<GetMicrocycle>
                    ) {
                        Log.d("TAG", "Response Code: ${response.code()}")
                        Log.d("TAG1", "Response Message: ${response.message()}")
                        editMicroCycleActivity.progresBar.visibility = View.GONE
                        Log.d("TAG", response.code().toString() + "")
                        val code = response.code()
                        if (code == 200) {
                            if (response.isSuccessful) {
                                response.body()?.let { responseBody ->
                                    if (responseBody.status == true) {
                                        Toast.makeText(
                                            this@EditMicroCycleActivity,
                                            "Success: ${responseBody.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        finish()
                                    } else {
                                        val errorMessage =
                                            responseBody.message ?: "Something went wrong"
                                        Log.e("TAG2", "Error: $errorMessage")
                                        Toast.makeText(
                                            this@EditMicroCycleActivity,
                                            errorMessage,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } ?: run {
                                    Log.e("TAG2", "Response body is null")
                                    Toast.makeText(
                                        this@EditMicroCycleActivity,
                                        "Unexpected response",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                                Log.e("TAG4", "Error Response: $errorBody")
                                Toast.makeText(
                                    this@EditMicroCycleActivity,
                                    "Error: $errorBody",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else if (code == 403) {
                            Utils.setUnAuthDialog(this@EditMicroCycleActivity)
                        } else {
                            Toast.makeText(
                                this@EditMicroCycleActivity,
                                "" + response.message(),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            call.cancel()
                        }
                    }

                    override fun onFailure(call: Call<GetMicrocycle>, t: Throwable) {
                        editMicroCycleActivity.progresBar.visibility = View.GONE
                        Log.e("TAG3", "onResponse failure: ${t.message}", t)
                        Toast.makeText(
                            this@EditMicroCycleActivity,
                            "Network error: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })

        } catch (e: Exception) {
            Log.d("TAG", "editMesocyclePresession: ${e.message}")
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
            preCompetitive.clear()

            for (i in 0 until trainingPlanContainer.childCount) {
                val layout = trainingPlanContainer.getChildAt(i)
                val nameEditText: AppCompatEditText? = layout.findViewById(R.id.ent_pre_sea_name)
                val startDateEditText: AppCompatEditText? =
                    layout.findViewById(R.id.ent_start_date_liner)
                val endDateEditText: AppCompatEditText? =
                    layout.findViewById(R.id.ent_ent_date_liner)
                val seekBar: SeekBar = layout.findViewById(R.id.seekbar_workload_layout)
                val workloadProgress = seekBar.progress
                val (colorCode, _) = getWorkloadColorAndString(workloadProgress)


                val name = nameEditText?.text.toString().trim()
                val start = startDateEditText?.text.toString().trim()
                val end = endDateEditText?.text.toString().trim()

                if (name.isEmpty() || start.isEmpty() || end.isEmpty()) {
                    Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show()
                    return
                }

                val newName = "Microcycle ${i + 1}"

                if (preCompetitive.any { it.name == newName }) {
                    Toast.makeText(
                        this,
                        "Name already exists, please use a different name.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }


                val errorTextView: TextView = layout.findViewById(R.id.error_start_date_list)


                val startDateMillis = formatDateToMillistest(start)
                val endDateMillis = formatDateToMillistest(end)

                val formattedStartDate = formatMillisToDateString(startDateMillis)
                val formattedEndDate = formatMillisToDateString(endDateMillis)

                val errorStartDateMillis = parseFormattedDateToMillis(formattedStartDate)
                val errorTestDateMillis = parseFormattedDateToMillis(formattedEndDate)

                val finalStartDates = formatDate(errorStartDateMillis)
                val finalEndDates = formatDate(errorTestDateMillis)


                Log.d("DEBUUUUUG", "Final formatted date: $finalStartDates    end:- $finalEndDates")



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


                Log.d("Data", "Name: $name, Start: $start, End: $end")

                val finalStartDate = if (finalStartDates.isNullOrEmpty()) errorstartdate.toString() else finalStartDates
                val finalEndDate = if (finalEndDates.isNullOrEmpty()) errorenddate.toString() else finalEndDates
                val finalAbility: List<Int> = if (selectedAbilityIds.isNullOrEmpty()) { listOfNotNull(abilityid?.toIntOrNull()) } else { selectedAbilityIds }


                if (finalStartDate.isNullOrEmpty()){
                    Toast.makeText(this, "Start Date Filed is Required", Toast.LENGTH_SHORT).show()
                    return
                }

                if (finalEndDates.isNullOrEmpty()){
                    Toast.makeText(this, "End Date Filed is Required", Toast.LENGTH_SHORT).show()
                    return
                }


                if (finalStartDate == finalEndDate){
                    return
                }else{
                }


                preCompetitive.add(
                    AddMicrocyclePreCompatitive(
                        id = if (i < programData.size) programData[i].id else null,
                        pc_mesocycle_id = seasonid,
                        name = newName,
                        start_date = finalStartDate.toString(),
                        end_date = finalEndDate.toString(),
                        workload = workloadProgress,
                        workload_color = colorCode,
                        ability_ids = finalAbility
                    )
                )
            }
            editMicroCycleActivity.progresBar.visibility = View.VISIBLE
            apiInterface.EditeMicrocyclePreCompatitive(preCompetitive)
                ?.enqueue(object : Callback<GetMicrocycle> {
                    override fun onResponse(
                        call: Call<GetMicrocycle>,
                        response: Response<GetMicrocycle>
                    ) {
                        Log.d("TAG", "Response Code: ${response.code()}")
                        Log.d("TAG1", "Response Message: ${response.message()}")
                        editMicroCycleActivity.progresBar.visibility = View.GONE
                        Log.d("TAG", response.code().toString() + "")
                        val code = response.code()
                        if (code == 200) {
                            if (response.isSuccessful) {
                                response.body()?.let { responseBody ->
                                    if (responseBody.status == true) {
                                        Toast.makeText(
                                            this@EditMicroCycleActivity,
                                            "Success: ${responseBody.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        finish()
                                    } else {
                                        val errorMessage =
                                            responseBody.message ?: "Something went wrong"
                                        Log.e("TAG2", "Error: $errorMessage")
                                        Toast.makeText(
                                            this@EditMicroCycleActivity,
                                            errorMessage,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } ?: run {
                                    Log.e("TAG2", "Response body is null")
                                    Toast.makeText(
                                        this@EditMicroCycleActivity,
                                        "Unexpected response",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                                Log.e("TAG4", "Error Response: $errorBody")
                                Toast.makeText(
                                    this@EditMicroCycleActivity,
                                    "Error: $errorBody",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else if (code == 403) {
                            Utils.setUnAuthDialog(this@EditMicroCycleActivity)
                        } else {
                            Toast.makeText(
                                this@EditMicroCycleActivity,
                                "" + response.message(),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            call.cancel()
                        }
                    }

                    override fun onFailure(call: Call<GetMicrocycle>, t: Throwable) {
                        editMicroCycleActivity.progresBar.visibility = View.GONE
                        Log.e("TAG3", "onResponse failure: ${t.message}", t)
                        Toast.makeText(
                            this@EditMicroCycleActivity,
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
            competitive.clear()

            for (i in 0 until trainingPlanContainer.childCount) {
                val layout = trainingPlanContainer.getChildAt(i)
                val nameEditText: AppCompatEditText? = layout.findViewById(R.id.ent_pre_sea_name)
                val startDateEditText: AppCompatEditText? =
                    layout.findViewById(R.id.ent_start_date_liner)
                val endDateEditText: AppCompatEditText? =
                    layout.findViewById(R.id.ent_ent_date_liner)
                val seekBar: SeekBar = layout.findViewById(R.id.seekbar_workload_layout)
                val workloadProgress = seekBar.progress
                val (colorCode, _) = getWorkloadColorAndString(workloadProgress)


                val name = nameEditText?.text.toString().trim()
                val start = startDateEditText?.text.toString().trim()
                val end = endDateEditText?.text.toString().trim()

                if (name.isEmpty() || start.isEmpty() || end.isEmpty()) {
                    Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show()
                    return
                }

                val newName = "Microcycle ${i + 1}"

                if (competitive.any { it.name == newName }) {
                    Toast.makeText(
                        this,
                        "Name already exists, please use a different name.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }

                val errorTextView: TextView = layout.findViewById(R.id.error_start_date_list)


                val startDateMillis = formatDateToMillistest(start)
                val endDateMillis = formatDateToMillistest(end)

                val formattedStartDate = formatMillisToDateString(startDateMillis)
                val formattedEndDate = formatMillisToDateString(endDateMillis)

                val errorStartDateMillis = parseFormattedDateToMillis(formattedStartDate)
                val errorTestDateMillis = parseFormattedDateToMillis(formattedEndDate)

                val finalStartDates = formatDate(errorStartDateMillis)
                val finalEndDates = formatDate(errorTestDateMillis)


                Log.d("DEBUUUUUG", "Final formatted date: $finalStartDates    end:- $finalEndDates")



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


                Log.d("Data", "Name: $name, Start: $start, End: $end")

                val finalStartDate = if (finalStartDates.isNullOrEmpty()) errorstartdate.toString() else finalStartDates
                val finalEndDate = if (finalEndDates.isNullOrEmpty()) errorenddate.toString() else finalEndDates
                val finalAbility: List<Int> = if (selectedAbilityIds.isNullOrEmpty()) { listOfNotNull(abilityid?.toIntOrNull()) } else { selectedAbilityIds }


                if (finalStartDate.isNullOrEmpty()){
                    Toast.makeText(this, "Start Date Filed is Required", Toast.LENGTH_SHORT).show()
                    return
                }

                if (finalEndDates.isNullOrEmpty()){
                    Toast.makeText(this, "End Date Filed is Required", Toast.LENGTH_SHORT).show()
                    return
                }


                if (finalStartDate == finalEndDate){
                    return
                }else{
                }

                competitive.add(
                    AddMicrocycleCompatitive(
                        id = if (i < programData.size) programData[i].id else null,
                        c_mesocycle_id = seasonid,
                        name = newName,
                        start_date = finalStartDate.toString(),
                        end_date = finalEndDate.toString(),
                        workload = workloadProgress,
                        workload_color = colorCode,
                        ability_ids = finalAbility
                    )
                )
            }
            editMicroCycleActivity.progresBar.visibility = View.VISIBLE
            apiInterface.EditeMicrocycleCompatitive(competitive)
                ?.enqueue(object : Callback<GetMicrocycle> {
                    override fun onResponse(
                        call: Call<GetMicrocycle>,
                        response: Response<GetMicrocycle>
                    ) {
                        Log.d("TAG", "Response Code: ${response.code()}")
                        Log.d("TAG1", "Response Message: ${response.message()}")
                        editMicroCycleActivity.progresBar.visibility = View.GONE
                        Log.d("TAG", response.code().toString() + "")
                        val code = response.code()
                        if (code == 200) {
                            if (response.isSuccessful) {
                                response.body()?.let { responseBody ->
                                    if (responseBody.status == true) {
                                        Toast.makeText(
                                            this@EditMicroCycleActivity,
                                            "Success: ${responseBody.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        finish()
                                    } else {
                                        val errorMessage =
                                            responseBody.message ?: "Something went wrong"
                                        Log.e("TAG2", "Error: $errorMessage")
                                        Toast.makeText(
                                            this@EditMicroCycleActivity,
                                            errorMessage,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } ?: run {
                                    Log.e("TAG2", "Response body is null")
                                    Toast.makeText(
                                        this@EditMicroCycleActivity,
                                        "Unexpected response",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                                Log.e("TAG4", "Error Response: $errorBody")
                                Toast.makeText(
                                    this@EditMicroCycleActivity,
                                    "Error: $errorBody",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else if (code == 403) {
                            Utils.setUnAuthDialog(this@EditMicroCycleActivity)
                        } else {
                            Toast.makeText(
                                this@EditMicroCycleActivity,
                                "" + response.message(),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            call.cancel()
                        }
                    }

                    override fun onFailure(call: Call<GetMicrocycle>, t: Throwable) {
                        editMicroCycleActivity.progresBar.visibility = View.GONE
                        Log.e("TAG3", "onResponse failure: ${t.message}", t)
                        Toast.makeText(
                            this@EditMicroCycleActivity,
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
            transient.clear()

            for (i in 0 until trainingPlanContainer.childCount) {
                val layout = trainingPlanContainer.getChildAt(i)
                val nameEditText: AppCompatEditText? = layout.findViewById(R.id.ent_pre_sea_name)
                val startDateEditText: AppCompatEditText? =
                    layout.findViewById(R.id.ent_start_date_liner)
                val endDateEditText: AppCompatEditText? =
                    layout.findViewById(R.id.ent_ent_date_liner)
                val seekBar: SeekBar = layout.findViewById(R.id.seekbar_workload_layout)
                val workloadProgress = seekBar.progress
                val (colorCode, _) = getWorkloadColorAndString(workloadProgress)


                val name = nameEditText?.text.toString().trim()
                val start = startDateEditText?.text.toString().trim()
                val end = endDateEditText?.text.toString().trim()

                if (name.isEmpty() || start.isEmpty() || end.isEmpty()) {
                    Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show()
                    return
                }

                val newName = "Microcycle ${i + 1}"

                if (transient.any { it.name == newName }) {
                    Toast.makeText(
                        this,
                        "Name already exists, please use a different name.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }

                val errorTextView: TextView = layout.findViewById(R.id.error_start_date_list)


                val startDateMillis = formatDateToMillistest(start)
                val endDateMillis = formatDateToMillistest(end)

                val formattedStartDate = formatMillisToDateString(startDateMillis)
                val formattedEndDate = formatMillisToDateString(endDateMillis)

                val errorStartDateMillis = parseFormattedDateToMillis(formattedStartDate)
                val errorTestDateMillis = parseFormattedDateToMillis(formattedEndDate)

                val finalStartDates = formatDate(errorStartDateMillis)
                val finalEndDates = formatDate(errorTestDateMillis)


                Log.d("DEBUUUUUG", "Final formatted date: $finalStartDates    end:- $finalEndDates")



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


                Log.d("Data", "Name: $name, Start: $start, End: $end")

                val finalStartDate = if (finalStartDates.isNullOrEmpty()) errorstartdate.toString() else finalStartDates
                val finalEndDate = if (finalEndDates.isNullOrEmpty()) errorenddate.toString() else finalEndDates
                val finalAbility: List<Int> = if (selectedAbilityIds.isNullOrEmpty()) { listOfNotNull(abilityid?.toIntOrNull()) } else { selectedAbilityIds }


                if (finalStartDate.isNullOrEmpty()){
                    Toast.makeText(this, "Start Date Filed is Required", Toast.LENGTH_SHORT).show()
                    return
                }

                if (finalEndDates.isNullOrEmpty()){
                    Toast.makeText(this, "End Date Filed is Required", Toast.LENGTH_SHORT).show()
                    return
                }


                if (finalStartDate == finalEndDate){
                    return
                }else{
                }

                transient.add(
                    AddMicrocycleTransition(
                        id = if (i < programData.size) programData[i].id else null,
                        pt_mesocycle_id = seasonid,
                        name = newName,
                        start_date = finalStartDate.toString(),
                        end_date = finalEndDate.toString(),
                        workload = workloadProgress,
                        workload_color = colorCode,
                        ability_ids = finalAbility
                    )
                )
            }
            editMicroCycleActivity.progresBar.visibility = View.VISIBLE
            apiInterface.EditeMicrocycleTransition(transient)
                ?.enqueue(object : Callback<GetMicrocycle> {
                    override fun onResponse(
                        call: Call<GetMicrocycle>,
                        response: Response<GetMicrocycle>
                    ) {
                        Log.d("TAG", "Response Code: ${response.code()}")
                        Log.d("TAG1", "Response Message: ${response.message()}")
                        editMicroCycleActivity.progresBar.visibility = View.GONE
                        Log.d("TAG", response.code().toString() + "")
                        val code = response.code()
                        if (code == 200) {
                            if (response.isSuccessful) {
                                response.body()?.let { responseBody ->
                                    if (responseBody.status == true) {
                                        Toast.makeText(
                                            this@EditMicroCycleActivity,
                                            "Success: ${responseBody.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        finish()
                                    } else {
                                        val errorMessage =
                                            responseBody.message ?: "Something went wrong"
                                        Log.e("TAG2", "Error: $errorMessage")
                                        Toast.makeText(
                                            this@EditMicroCycleActivity,
                                            errorMessage,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } ?: run {
                                    Log.e("TAG2", "Response body is null")
                                    Toast.makeText(
                                        this@EditMicroCycleActivity,
                                        "Unexpected response",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                                Log.e("TAG4", "Error Response: $errorBody")
                                Toast.makeText(
                                    this@EditMicroCycleActivity,
                                    "Error: $errorBody",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else if (code == 403) {
                            Utils.setUnAuthDialog(this@EditMicroCycleActivity)
                        } else {
                            Toast.makeText(
                                this@EditMicroCycleActivity,
                                "" + response.message(),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            call.cancel()
                        }
                    }

                    override fun onFailure(call: Call<GetMicrocycle>, t: Throwable) {
                        editMicroCycleActivity.progresBar.visibility = View.GONE
                        Log.e("TAG3", "onResponse failure: ${t.message}", t)
                        Toast.makeText(
                            this@EditMicroCycleActivity,
                            "Network error: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })

        } catch (e: Exception) {
            Log.d("TAG", "editMesocyclePresession: ${e.message}")
        }
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



            val applyButton: Button = dialog.findViewById(R.id.btn_apply)
            val closeButton: Button = dialog.findViewById(R.id.btn_cancel)

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
                            splist.clear()
                            splist.addAll(abilityDataList)

                            Log.d("success", "${splist}")

                            recyclerView.adapter = AbilitiesAdapter(splist, this@EditMicroCycleActivity)
                        } else {
                            Log.d("DATA_TAG", "Response body is null or empty")
                        }
                    }

                    403 -> {
                        Utils.setUnAuthDialog(this@EditMicroCycleActivity)
                    }

                    else -> {
                        Toast.makeText(
                            this@EditMicroCycleActivity,
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


            editMicroCycleActivity.card.visibility =
                if (selectedAbilities.isNotEmpty()) View.VISIBLE else View.GONE

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
                                    Toast.makeText(this@EditMicroCycleActivity, "Ability Added", Toast.LENGTH_SHORT).show()
                                    dialog.dismiss()

                                    // Notify that an ability has been added
                                    onAbilityAdded()
                                } else {
                                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                                    Log.e("API Error", "Response: ${response.code()} - $errorBody")
                                    Toast.makeText(
                                        this@EditMicroCycleActivity,
                                        "Error: $errorBody",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            override fun onFailure(call: Call<AbilityData>, t: Throwable) {
                                Log.e("API Error", "Error: ${t.message}")
                                Toast.makeText(
                                    this@EditMicroCycleActivity,
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





    private fun selectTrainingPlanStartDate(editText: AppCompatEditText) {
        val mainStartDate = editMicroCycleActivity.startDate.text.toString()
        val maxStartDate = editMicroCycleActivity.endDate.text.toString()

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

//
//    private fun parseFormattedDateToMillis(dateString: String?): Long {
//        return try {
//            val format = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
//            val date = format.parse(dateString)
//            date?.time ?: System.currentTimeMillis() // Fallback to current time if parsing fails
//        } catch (e: Exception) {
//            Log.e("DateConversion", "Error parsing formatted date: ${e.message}")
//            System.currentTimeMillis() // Fallback to current time
//        }
//    }

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
//    private fun formatMillisToDateString(millis: Long): String {
//        val format = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
//        return format.format(millis)
//    }



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


    private fun selectTrainingPlanEndDate(editText: AppCompatEditText) {

        val mainStartDate = editMicroCycleActivity.startDate.text.toString()
        val maxStartDate = editMicroCycleActivity.endDate.text.toString()

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

    override fun onItemClicked(view: View, position: Int, type: Long, string: String) {
        TODO("Not yet implemented")
    }
}