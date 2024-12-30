package com.example

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.NumberPicker
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.Adapter.groups.SetAthleteInGroup
import com.example.Adapter.groups.SetEventInGroup
import com.example.Adapter.groups.SetLessonInGroup
import com.example.Adapter.groups.SetPlanningGroup
import com.example.Adapter.groups.SetTestInGroup
import com.example.model.DayTime
import com.example.model.SelectedValue
import com.example.model.newClass.athlete.AthleteData
import com.example.model.newClass.lesson.Lesson
import com.example.model.training_plan.TrainingPlanData
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.EventListData
import com.example.trainerapp.ApiClass.SportlistData
import com.example.trainerapp.HomeActivity
import com.example.trainerapp.R
import com.example.trainerapp.TestListData
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.ActivityCreateGropBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


@Suppress("DEPRECATION")
class CreateGropActivity : AppCompatActivity(), OnItemClickListener.OnItemClickCallback {
    lateinit var createGroupBinding: ActivityCreateGropBinding
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    lateinit var mon_linearLayour: LinearLayout
    lateinit var tue_linearLayout: LinearLayout
    lateinit var wed_linearLayout: LinearLayout
    lateinit var thu_linearLayout: LinearLayout
    lateinit var fri_linearLayout: LinearLayout
    lateinit var sat_linearLayout: LinearLayout
    lateinit var sun_linearLayout: LinearLayout
    lateinit var inflater: View
    lateinit var tv_start_time: TextView
    lateinit var tv_End_time: TextView
    lateinit var start_error: TextView
    lateinit var end_error: TextView
    var lessonId: IntArray = intArrayOf()
    var testId: IntArray = intArrayOf()  // Initialize as an empty array or a default value
    var eventId: IntArray = intArrayOf()  // Initialize as an empty array or a default value
    var planningId: IntArray = intArrayOf()  // Initialize as an empty array or a default value
    var athleteId: IntArray = intArrayOf()  // Initialize as an empty array or a default value
    var programId: IntArray = intArrayOf()  // Initialize as an empty array or a default value

    private var selectedImageUri: Uri? = null
    private lateinit var lessonAdapter: SetLessonInGroup
    private lateinit var eventAdapter: SetEventInGroup
    private lateinit var testAdapter: SetTestInGroup
    lateinit var adapterPlanning: SetPlanningGroup
    lateinit var adapterAthlete: SetAthleteInGroup
    private lateinit var lessonData: ArrayList<Lesson.LessonDatabase>
    private lateinit var TestData: ArrayList<TestListData.testData>
    private lateinit var EventData: ArrayList<EventListData.testData>
    lateinit var goalData: MutableList<SportlistData.sportlist>
    lateinit var plainngData: MutableList<TrainingPlanData.TrainingPlan>
    lateinit var athleteData: MutableList<AthleteData.Athlete>

    var Goal = ArrayList<String>()
    var sportlId = SelectedValue(null)

    private var shouldSaveData = true
    val selectedDays = mutableListOf<String>()

    // Data structure example:
    var dayTimes: MutableMap<String, MutableList<DayTime>> = mutableMapOf(
        "monday" to mutableListOf(),
        "tuesday" to mutableListOf(),
        "wednesday" to mutableListOf(),
        "thursday" to mutableListOf(),
        "friday" to mutableListOf(),
        "saturday" to mutableListOf(),
        "sunday" to mutableListOf()
    )

    private var mondayStartTime: String? = null
    private var mondayEndTime: String? = null

    private var tuesdayStartTime: String? = null
    private var tuesdayEndTime: String? = null

    private var wednesdayStartTime: String? = null
    private var wednesdayEndTime: String? = null

    private var thursdayStartTime: String? = null
    private var thursdayEndTime: String? = null

    private var fridayStartTime: String? = null
    private var fridayEndTime: String? = null

    private var saturdayStartTime: String? = null
    private var saturdayEndTime: String? = null

    private var sundayStartTime: String? = null
    private var sundayEndTime: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createGroupBinding = ActivityCreateGropBinding.inflate(layoutInflater)
        setContentView(createGroupBinding.root)
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        mon_linearLayour = findViewById(R.id.Mon_LinearLayout)
        tue_linearLayout = findViewById(R.id.Tue_LinearLayout)
        wed_linearLayout = findViewById(R.id.Wed_LinearLayout)
        thu_linearLayout = findViewById(R.id.Thu_LinearLayout)
        fri_linearLayout = findViewById(R.id.Fri_LinearLayout)
        sat_linearLayout = findViewById(R.id.Sat_LinearLayout)
        sun_linearLayout = findViewById(R.id.Sun_LinearLayout)


        loadIdsFromPreferences()
        loadGroupData()
        loadDayTimesFromPreferences()

        dayTimes.forEach { (day, times) ->
            val linearLayout = getLinearLayoutForDay(day)
            linearLayout.removeAllViews() // Clear any existing views

            for (time in times) {
                val inflater = LayoutInflater.from(this).inflate(R.layout.time_layout_group, null)
                linearLayout.addView(inflater)

                val tvStartTime: EditText = inflater.findViewById(R.id.tv_start_time)
                val tvEndTime: EditText = inflater.findViewById(R.id.tv_End_time)

                tvStartTime.setText(time.startTime)
                tvEndTime.setText(time.endTime)

                val tvStartTimeCard: CardView = inflater.findViewById(R.id.start_time_card)
                val tvEndTimeCard: CardView = inflater.findViewById(R.id.card_end_time)

                tvStartTimeCard.setOnClickListener {
                    SetDialog_start(tvStartTime)
                }
                tvEndTimeCard.setOnClickListener {
                    setDialogEnd(tvEndTime)
                }
            }
        }

        lessonId = intent.getIntArrayExtra("lessonId") ?: intArrayOf()
        Log.d("IDDDDDDDD", "lesson: ${lessonId.joinToString()}")

        testId = intent.getIntArrayExtra("testId") ?: intArrayOf()
        Log.d("IDDDDDDDD", "test: ${testId.joinToString()}")

        eventId = intent.getIntArrayExtra("eventId") ?: intArrayOf()
        Log.d("IDDDDDDDD", "event: ${eventId.joinToString()}")

        planningId = intent.getIntArrayExtra("planningId") ?: intArrayOf()
        Log.d("IDDDDDDDD", "planning: ${planningId.joinToString()}")

        athleteId = intent.getIntArrayExtra("athleteId") ?: intArrayOf()
        Log.d("IDDDDDDDD", "athlete: ${athleteId.joinToString()}")

        mergeIdsAndSave()


        lessonData = ArrayList()
        TestData = ArrayList()
        EventData = ArrayList()
        goalData = mutableListOf()
        plainngData = mutableListOf()
        athleteData = mutableListOf()
        getSportData()

        if (lessonId != null) {
            createGroupBinding.rlyLesson.visibility = View.VISIBLE
            GetLessonList(listOf(lessonId.map { it.toString() }))
        } else {
            createGroupBinding.rlyLesson.visibility = View.VISIBLE
        }

        if (testId != null) {
            createGroupBinding.rlyTest.visibility = View.VISIBLE
            GetTestList(listOf(testId.map { it.toString() }))
        } else {
            createGroupBinding.rlyLesson.visibility = View.VISIBLE
        }

        if (eventId != null) {
            createGroupBinding.rlyEvent.visibility = View.VISIBLE
            geteventlist(listOf(eventId.map { it.toString() }))
        } else {
            createGroupBinding.rlyEvent.visibility = View.VISIBLE
        }

        if (planningId != null) {
            createGroupBinding.rlyPlanning.visibility = View.VISIBLE
            getTrainingData(listOf(planningId.map { it.toString() }))
        } else {
            createGroupBinding.rlyPlanning.visibility = View.VISIBLE
        }

        if (athleteId != null) {
            createGroupBinding.rlyAthlete.visibility = View.VISIBLE
            getAthleteData(listOf(athleteId.map { it.toString() }))
        } else {
            createGroupBinding.rlyAthlete.visibility = View.GONE
        }

        createGroupBinding.lySport.setOnClickListener {
            showPopup(it, goalData, createGroupBinding.edtSport, Goal, sportlId)

            val groupName = createGroupBinding.edtName.text.toString()
            val sportName = createGroupBinding.edtSport.text.toString()
            val imageUri = selectedImageUri

            saveGroupData(groupName, imageUri, sportName, sportlId.id.toString())
        }

        createGroupBinding.edtSport.setOnClickListener {
            showPopup(it, goalData, createGroupBinding.edtSport, Goal, sportlId)

            val groupName = createGroupBinding.edtName.text.toString()
            val sportName = createGroupBinding.edtSport.text.toString()
            val imageUri = createGroupBinding.imageUpload.tag as? Uri

            saveGroupData(groupName, imageUri, sportName, sportlId.id.toString())
        }
        Log.d("FHHFHFHFH", "onCreate: ${sportlId.id}")


//        mon_addView()
        createGroupBinding.monAddScheduleTime.setOnClickListener {
//            if (isValidate){
//                start_error.visibility = View.GONE
//                end_error.visibility = View.GONE
//                mon_addView()
//            }
            mon_addView()
        }
        createGroupBinding.tueAddScheduleTime.setOnClickListener {
//            if (isValidate){
//                start_error.visibility = View.GONE
//                end_error.visibility = View.GONE
//                tue_addView()
//            }
            tue_addView()
        }
        createGroupBinding.wedAddScheduleTime.setOnClickListener {
//            if (isValidate){
//                start_error.visibility = View.GONE
//                end_error.visibility = View.GONE
//                wed_addView()
//            }
            wed_addView()

        }
        createGroupBinding.thuAddScheduleTime.setOnClickListener {
//            if (isValidate){
//                createGroupBinding..visibility = View.GONE
//                createGroupBinding.end_error.visibility = View.GONE
//
//
//            }
            thu_addView()
        }
        createGroupBinding.friAddScheduleTime.setOnClickListener {
//            if (isValidate){
//                start_error.visibility = View.GONE
//                end_error.visibility = View.GONE
//
//            }
            fri_addView()

        }
        createGroupBinding.satAddScheduleTime.setOnClickListener {
//            if (isValidate){
//                start_error.visibility = View.GONE
//                end_error.visibility = View.GONE
//
//            }
            sat_addView()

        }
        createGroupBinding.sunAddScheduleTime.setOnClickListener {
//            if (isValidate){
//                start_error.visibility = View.GONE
//                end_error.visibility = View.GONE
//
//            }
            sun_addView()
        }


        createGroupBinding.weekMon.setOnClickListener {

            toggleDay(
                "monday",
                createGroupBinding.weekMon,
                mon_linearLayour,
                createGroupBinding.monAddScheduleTime
            )


            mon_linearLayour.visibility = View.VISIBLE
            tue_linearLayout.visibility = View.GONE
            wed_linearLayout.visibility = View.GONE
            thu_linearLayout.visibility = View.GONE
            fri_linearLayout.visibility = View.GONE
            sat_linearLayout.visibility = View.GONE
            sun_linearLayout.visibility = View.GONE
            createGroupBinding.monAddScheduleTime.visibility = View.VISIBLE
            createGroupBinding.tueAddScheduleTime.visibility = View.GONE
            createGroupBinding.wedAddScheduleTime.visibility = View.GONE
            createGroupBinding.thuAddScheduleTime.visibility = View.GONE
            createGroupBinding.friAddScheduleTime.visibility = View.GONE
            createGroupBinding.satAddScheduleTime.visibility = View.GONE
            createGroupBinding.sunAddScheduleTime.visibility = View.GONE
            createGroupBinding.weekMon.setCardBackgroundColor(resources.getColor(R.color.splash_text_color))
            createGroupBinding.weekTue.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekWed.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekThu.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekFri.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekSat.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekSun.setCardBackgroundColor(resources.getColor(R.color.card_background))
        }
        createGroupBinding.weekTue.setOnClickListener {

            toggleDay(
                "tuesday",
                createGroupBinding.weekTue,
                tue_linearLayout,
                createGroupBinding.tueAddScheduleTime
            )

            mon_linearLayour.visibility = View.GONE
            tue_linearLayout.visibility = View.VISIBLE
            wed_linearLayout.visibility = View.GONE
            thu_linearLayout.visibility = View.GONE
            fri_linearLayout.visibility = View.GONE
            sat_linearLayout.visibility = View.GONE
            sun_linearLayout.visibility = View.GONE
            createGroupBinding.monAddScheduleTime.visibility = View.GONE
            createGroupBinding.tueAddScheduleTime.visibility = View.VISIBLE
            createGroupBinding.wedAddScheduleTime.visibility = View.GONE
            createGroupBinding.thuAddScheduleTime.visibility = View.GONE
            createGroupBinding.friAddScheduleTime.visibility = View.GONE
            createGroupBinding.satAddScheduleTime.visibility = View.GONE
            createGroupBinding.sunAddScheduleTime.visibility = View.GONE
            createGroupBinding.weekMon.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekTue.setCardBackgroundColor(resources.getColor(R.color.splash_text_color))
            createGroupBinding.weekWed.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekThu.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekFri.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekSat.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekSun.setCardBackgroundColor(resources.getColor(R.color.card_background))
        }
        createGroupBinding.weekWed.setOnClickListener {
            toggleDay(
                "wednesday",
                createGroupBinding.weekWed,
                wed_linearLayout,
                createGroupBinding.wedAddScheduleTime
            )

            mon_linearLayour.visibility = View.GONE
            tue_linearLayout.visibility = View.GONE
            wed_linearLayout.visibility = View.VISIBLE
            thu_linearLayout.visibility = View.GONE
            fri_linearLayout.visibility = View.GONE
            sat_linearLayout.visibility = View.GONE
            sun_linearLayout.visibility = View.GONE
            createGroupBinding.monAddScheduleTime.visibility = View.GONE
            createGroupBinding.tueAddScheduleTime.visibility = View.GONE
            createGroupBinding.wedAddScheduleTime.visibility = View.VISIBLE
            createGroupBinding.thuAddScheduleTime.visibility = View.GONE
            createGroupBinding.friAddScheduleTime.visibility = View.GONE
            createGroupBinding.satAddScheduleTime.visibility = View.GONE
            createGroupBinding.sunAddScheduleTime.visibility = View.GONE
            createGroupBinding.weekMon.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekTue.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekWed.setCardBackgroundColor(resources.getColor(R.color.splash_text_color))
            createGroupBinding.weekThu.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekFri.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekSat.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekSun.setCardBackgroundColor(resources.getColor(R.color.card_background))
        }
        createGroupBinding.weekThu.setOnClickListener {

            toggleDay(
                "thursday",
                createGroupBinding.weekThu,
                thu_linearLayout,
                createGroupBinding.thuAddScheduleTime
            )


            mon_linearLayour.visibility = View.GONE
            tue_linearLayout.visibility = View.GONE
            wed_linearLayout.visibility = View.GONE
            thu_linearLayout.visibility = View.VISIBLE
            fri_linearLayout.visibility = View.GONE
            sat_linearLayout.visibility = View.GONE
            sun_linearLayout.visibility = View.GONE
            createGroupBinding.monAddScheduleTime.visibility = View.GONE
            createGroupBinding.tueAddScheduleTime.visibility = View.GONE
            createGroupBinding.wedAddScheduleTime.visibility = View.GONE
            createGroupBinding.thuAddScheduleTime.visibility = View.VISIBLE
            createGroupBinding.friAddScheduleTime.visibility = View.GONE
            createGroupBinding.satAddScheduleTime.visibility = View.GONE
            createGroupBinding.sunAddScheduleTime.visibility = View.GONE
            createGroupBinding.weekMon.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekTue.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekWed.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekThu.setCardBackgroundColor(resources.getColor(R.color.splash_text_color))
            createGroupBinding.weekFri.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekSat.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekSun.setCardBackgroundColor(resources.getColor(R.color.card_background))
        }
        createGroupBinding.weekFri.setOnClickListener {
            toggleDay(
                "friday",
                createGroupBinding.weekFri,
                fri_linearLayout,
                createGroupBinding.friAddScheduleTime
            )

            mon_linearLayour.visibility = View.GONE
            tue_linearLayout.visibility = View.GONE
            wed_linearLayout.visibility = View.GONE
            thu_linearLayout.visibility = View.GONE
            fri_linearLayout.visibility = View.VISIBLE
            sat_linearLayout.visibility = View.GONE
            sun_linearLayout.visibility = View.GONE
            createGroupBinding.monAddScheduleTime.visibility = View.GONE
            createGroupBinding.tueAddScheduleTime.visibility = View.GONE
            createGroupBinding.wedAddScheduleTime.visibility = View.GONE
            createGroupBinding.thuAddScheduleTime.visibility = View.GONE
            createGroupBinding.friAddScheduleTime.visibility = View.VISIBLE
            createGroupBinding.satAddScheduleTime.visibility = View.GONE
            createGroupBinding.sunAddScheduleTime.visibility = View.GONE
            createGroupBinding.weekMon.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekTue.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekWed.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekThu.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekFri.setCardBackgroundColor(resources.getColor(R.color.splash_text_color))
            createGroupBinding.weekSat.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekSun.setCardBackgroundColor(resources.getColor(R.color.card_background))
        }
        createGroupBinding.weekSat.setOnClickListener {
            toggleDay(
                "saturday",
                createGroupBinding.weekSat,
                sat_linearLayout,
                createGroupBinding.satAddScheduleTime
            )
            mon_linearLayour.visibility = View.GONE
            tue_linearLayout.visibility = View.GONE
            wed_linearLayout.visibility = View.GONE
            thu_linearLayout.visibility = View.GONE
            fri_linearLayout.visibility = View.GONE
            sat_linearLayout.visibility = View.VISIBLE
            sun_linearLayout.visibility = View.GONE
            createGroupBinding.monAddScheduleTime.visibility = View.GONE
            createGroupBinding.tueAddScheduleTime.visibility = View.GONE
            createGroupBinding.wedAddScheduleTime.visibility = View.GONE
            createGroupBinding.thuAddScheduleTime.visibility = View.GONE
            createGroupBinding.friAddScheduleTime.visibility = View.GONE
            createGroupBinding.satAddScheduleTime.visibility = View.VISIBLE
            createGroupBinding.sunAddScheduleTime.visibility = View.GONE
            createGroupBinding.weekMon.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekTue.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekWed.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekThu.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekFri.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekSat.setCardBackgroundColor(resources.getColor(R.color.splash_text_color))
            createGroupBinding.weekSun.setCardBackgroundColor(resources.getColor(R.color.card_background))
        }
        createGroupBinding.weekSun.setOnClickListener {

            toggleDay(
                "sunday",
                createGroupBinding.weekSun,
                sun_linearLayout,
                createGroupBinding.sunAddScheduleTime
            )

            mon_linearLayour.visibility = View.GONE
            tue_linearLayout.visibility = View.GONE
            wed_linearLayout.visibility = View.GONE
            thu_linearLayout.visibility = View.GONE
            fri_linearLayout.visibility = View.GONE
            sat_linearLayout.visibility = View.GONE
            sun_linearLayout.visibility = View.VISIBLE
            createGroupBinding.monAddScheduleTime.visibility = View.GONE
            createGroupBinding.tueAddScheduleTime.visibility = View.GONE
            createGroupBinding.wedAddScheduleTime.visibility = View.GONE
            createGroupBinding.thuAddScheduleTime.visibility = View.GONE
            createGroupBinding.friAddScheduleTime.visibility = View.GONE
            createGroupBinding.satAddScheduleTime.visibility = View.GONE
            createGroupBinding.sunAddScheduleTime.visibility = View.VISIBLE
            createGroupBinding.weekMon.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekTue.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekWed.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekThu.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekFri.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekSat.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekSun.setCardBackgroundColor(resources.getColor(R.color.splash_text_color))
        }

        createGroupBinding.lyPlanning.setOnClickListener {
            val intent = Intent(this, LessonListActivity::class.java)
            intent.putExtra("Add", "planning")
            startActivity(intent)
        }

        createGroupBinding.btnAddLesson.setOnClickListener {
            val intent = Intent(this, LessonListActivity::class.java)
            intent.putExtra("Add", "lesson")
            startActivity(intent)
        }

        createGroupBinding.btnAddTest.setOnClickListener {
            val intent = Intent(this, LessonListActivity::class.java)
            intent.putExtra("Add", "test")
            startActivity(intent)
        }

        createGroupBinding.btnAddEvent.setOnClickListener {
            val intent = Intent(this, LessonListActivity::class.java)
            intent.putExtra("Add", "event")
            startActivity(intent)
        }

        createGroupBinding.btnAddAthlete.setOnClickListener {
            val intent = Intent(this, LessonListActivity::class.java)
            intent.putExtra("Add", "athlete")
            startActivity(intent)
        }

        createGroupBinding.scanQr.setOnClickListener {
            val dialog = Dialog(this, R.style.Theme_Dialog)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setCanceledOnTouchOutside(false)
            dialog.setContentView(R.layout.scan_qr_code_dailog)
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            val width = (displayMetrics.widthPixels * 0.9f).toInt()
            val height = WindowManager.LayoutParams.WRAP_CONTENT
            val window: Window = dialog.window!!
            val wlp = window.attributes
            wlp.gravity = Gravity.CENTER
            wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_BLUR_BEHIND
            window.attributes = wlp
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
//            dialog.window!!.setLayout(width, height)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


            val close = dialog.findViewById<CardView>(R.id.next_card)
            close.setOnClickListener { dialog.dismiss() }

            dialog.show()
        }


        createGroupBinding.selectImage.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1)
        }

        createGroupBinding.back.setOnClickListener {
            shouldSaveData = false
            onBackPressed()
            clearGroupDataOnBack()
            clearDayTimesOnBack()
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("group", "addGroup")
            startActivity(intent)

        }

        createGroupBinding.nextCard.setOnClickListener {
            val selectedImageUri: Uri? = selectedImageUri // Get this from your image picker logic

            if (selectedImageUri != null) {
                addGroup(selectedImageUri)
            } else {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addGroup(uri: Uri) {
        shouldSaveData = false
        val imageFile = getFileFromUri(uri)
        if (imageFile == null || !imageFile.exists()) {
            Log.e("ERROR", "Invalid file URI or file does not exist.")
            return
        }

        val timingFormatted = collectTimings().toString()
        val daysids = selectedDays.joinToString(prefix = "[", postfix = "]", separator = ", ") { "\"$it\"" }
        val sportids = sportlId.id.toString()
        val id = lessonId.joinToString(", ", prefix = "[", postfix = "]")
        val athleteids = athleteId.joinToString(", ", prefix = "[", postfix = "]")
        val testids = testId.joinToString(", ", prefix = "[", postfix = "]")
        val eventids = eventId.joinToString(", ", prefix = "[", postfix = "]")
        val planningids = planningId.joinToString(", ", prefix = "[", postfix = "]")


        Log.d("GBBGBG", "addGroup: $daysids")

        val sportId = RequestBody.create("text/plain".toMediaTypeOrNull(), sportids)
        val name = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            createGroupBinding.edtName.text.toString()
        )
        val lessonIds = RequestBody.create("text/plain".toMediaTypeOrNull(), id)
        val athleteIds = RequestBody.create("text/plain".toMediaTypeOrNull(), athleteids)
        val eventIds = RequestBody.create("text/plain".toMediaTypeOrNull(), eventids)
        val planningIds = RequestBody.create("text/plain".toMediaTypeOrNull(), planningids)
        val testIds = RequestBody.create("text/plain".toMediaTypeOrNull(), testids)
        val programIds = RequestBody.create("text/plain".toMediaTypeOrNull(), "[]")
        val days = RequestBody.create("text/plain".toMediaTypeOrNull(), daysids)
        val timing = RequestBody.create("application/json".toMediaTypeOrNull(), timingFormatted)

        val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), imageFile)
        val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)

        apiInterface.addGroup(
            sportId, name, imagePart, lessonIds, athleteIds, eventIds, planningIds,
            testIds, programIds, days, timing
        ).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                shouldSaveData = false
                onBackPressed()
                clearIdsFromPreferences()
                clearGroupDataOnBack()
                clearDayTimesOnBack()

                if (response.isSuccessful) {
                    Toast.makeText(this@CreateGropActivity, "Group Added", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@CreateGropActivity, HomeActivity::class.java)
                    intent.putExtra("group", "addGroup")
                    startActivity(intent)
                    finish()

                    Log.d("SUCCESS", "onResponse: Success: ${response.body()?.string()}")
                } else {
                    Log.e("ERROR", "onResponse: Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("ERROR", "onFailure: ${t.message}", t)
            }
        })
    }

    private fun getLinearLayoutForDay(day: String): LinearLayout {
        return when (day.lowercase()) { // Convert the input day to lowercase
            "monday" -> mon_linearLayour
            "tuesday" -> tue_linearLayout
            "wednesday" -> wed_linearLayout
            "thursday" -> thu_linearLayout
            "friday" -> fri_linearLayout
            "saturday" -> sat_linearLayout
            "sunday" -> sun_linearLayout
            else -> throw IllegalArgumentException("Invalid day: $day")
        }
    }


    private fun getFileFromUri(uri: Uri): File? {
        val filePath = getRealPathFromURI(uri)
        return if (filePath != null) File(filePath) else null
    }

    private fun getRealPathFromURI(uri: Uri): String? {
        val cursor = contentResolver.query(uri, null, null, null, null)
        return if (cursor != null && cursor.moveToFirst()) {
            val index = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
            if (index != -1) {
                val path = cursor.getString(index)
                cursor.close()
                path
            } else {
                cursor.close()
                null
            }
        } else {
            null
        }
    }

    private fun collectTimings(): String {
        // List to store timings as list of lists of maps
        val timingList = mutableListOf<List<Map<String, String>>>()

        val daysLayout = listOf(
            mon_linearLayour to "monday",
            tue_linearLayout to "tuesday",
            wed_linearLayout to "wednesday",
            thu_linearLayout to "thursday",
            fri_linearLayout to "friday",
            sat_linearLayout to "saturday",
            sun_linearLayout to "sunday"
        )

        // Collect timings for each day
        for ((layout, day) in daysLayout) {
            if (selectedDays.contains(day)) { // Only collect timings for the selected days
                val dailyTimings =
                    mutableListOf<Map<String, String>>() // Collect timings for the specific day

                for (i in 0 until layout.childCount) {
                    val childView = layout.getChildAt(i)
                    val tvStartTime = childView.findViewById<TextView>(R.id.tv_start_time)
                    val tvEndTime = childView.findViewById<TextView>(R.id.tv_End_time)

                    val startTime = formatTime(tvStartTime.text.toString().trim())
                    val endTime = formatTime(tvEndTime.text.toString().trim())

                    // Check if both start and end times are not empty
                    if (startTime.isNotEmpty() && endTime.isNotEmpty()) {
                        val timingMap = mapOf(
                            "start_time" to startTime,
                            "end_time" to endTime
                        )
                        dailyTimings.add(timingMap) // Add this timing to the day's list
                    }
                }

                // Only add the day's timings if there is at least one timing
                if (dailyTimings.isNotEmpty()) {
                    timingList.add(dailyTimings) // Add daily timings directly as a list of maps
                }
            }
        }

        val gson = Gson()
        Log.d("TIMINGS_DEBUG", "Timing List: $timingList")
        return gson.toJson(timingList)
    }


    private fun toggleDay(
        day: String,
        cardView: View,
        linearLayout: View,
        addScheduleTime: View
    ) {
        if (selectedDays.contains(day)) {
            selectedDays.remove(day)
            linearLayout.visibility = View.GONE
            addScheduleTime.visibility = View.GONE
        } else {
            // If not selected, select it
            selectedDays.add(day)
            linearLayout.visibility = View.VISIBLE
            addScheduleTime.visibility = View.VISIBLE
        }

        // Log the selected days
        Log.d("SelectedDays", selectedDays.toString())
    }

    private fun saveGroupData(
        groupName: String,
        imageUri: Uri?,
        sportName: String,
        sportId: String
    ) {
        val sharedPreferences = getSharedPreferences("GroupData", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        Log.d(
            "GroupData",
            "Saving data: groupName=$groupName, sportName=$sportName, sportId=$sportId"
        )

        editor.putString("groupName", groupName)

        imageUri?.let {
            Log.d("GroupData", "Saving imageUri: $imageUri")
            editor.putString("imageUri", it.toString())
        }

        editor.putString("sportName", sportName)
        editor.putString("sportId", sportId)

        editor.apply()
        Log.d("GroupData", "Data saved successfully")
    }

    private fun loadGroupData() {
        val sharedPreferences = getSharedPreferences("GroupData", MODE_PRIVATE)

        val groupName = sharedPreferences.getString("groupName", null)
        val imageUriString = sharedPreferences.getString("imageUri", null)
        val sportName = sharedPreferences.getString("sportName", null)
        val sportId = sharedPreferences.getString("sportId", null)

        Log.d("GroupData", "Loading data...")

        // Set group name
        groupName?.let {
            createGroupBinding.edtName.setText(it)
            Log.d("GroupData", "Loaded groupName: $it")
        }

        // Set image URI
        imageUriString?.let {
            val imageUri = Uri.parse(it)
            if (imageUri.scheme == "content" || imageUri.scheme == "file") {
                Glide.with(this)
                    .load(imageUri)
                    .into(createGroupBinding.imageUpload)

                createGroupBinding.selectUploadLy.visibility = View.GONE
                createGroupBinding.imageUpload.visibility = View.VISIBLE
                createGroupBinding.imageUpload.tag = imageUri
                selectedImageUri = imageUri
                Log.d("GroupData", "Image successfully loaded into ImageView")
            } else {
                Log.e("GroupData", "Invalid URI: $imageUri")
            }
        }

        // Set sport name
        sportName?.let {
            createGroupBinding.edtSport.setText(it)
            Log.d("GroupData", "Loaded sportName: $it")
        }

        // Set sport ID
        sportId?.let {
            try {
                sportlId.id = it.toInt() // Convert to int if valid
                Log.d("GroupData", "Loaded sportId: $it")
            } catch (e: NumberFormatException) {
                Log.e("GroupData", "Invalid sportId format: $it")
            }
        }

        Log.d("GroupData", "Data loaded successfully")
    }

    override fun onResume() {
        super.onResume()
        loadGroupData()
        loadDayTimesFromPreferences()
        clearGroupDataOnBack()
    }

    override fun onDestroy() {
        super.onDestroy()
        onBackPressed()
        clearIdsFromPreferences()
        clearGroupDataOnBack()
        clearDayTimesOnBack()
    }

    override fun onPause() {
        super.onPause()

        Log.d("FGFGFG", "onPause: $shouldSaveData")

        if (shouldSaveData == true) {
            val groupName = createGroupBinding.edtName.text.toString()
            val sportName = createGroupBinding.edtSport.text.toString()
            val imageUri = selectedImageUri
            saveGroupData(groupName, imageUri, sportName, sportlId.id.toString())
            saveDayTimesToPreferences()


            Log.d("GroupData", "Data saved in onPause")
        } else {
            Log.d("GroupData", "Data not saved in onPause because shouldSaveData is false")
        }
    }


    private fun clearGroupDataOnBack() {
        val sharedPreferences = getSharedPreferences("GroupData", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.remove("groupName")
        editor.remove("imageUri")
        editor.remove("sportName")
        editor.remove("sportId")

        editor.apply()

        Log.d("GroupData", "Data cleared successfully on back")
    }


    private fun saveIdsToPreferences() {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Save each ID array
        editor.putString("lessonId", lessonId.joinToString(","))
        editor.putString("testId", testId.joinToString(","))
        editor.putString("eventId", eventId.joinToString(","))
        editor.putString("planningId", planningId.joinToString(","))
        editor.putString("athleteId", athleteId.joinToString(","))

        editor.apply()
        Log.d("SharedPreferences", "IDs saved successfully")
    }

    private fun loadIdsFromPreferences() {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)

        lessonId =
            sharedPreferences.getString("lessonId", "")?.split(",")?.mapNotNull { it.toIntOrNull() }
                ?.toIntArray() ?: intArrayOf()
        testId =
            sharedPreferences.getString("testId", "")?.split(",")?.mapNotNull { it.toIntOrNull() }
                ?.toIntArray() ?: intArrayOf()
        eventId =
            sharedPreferences.getString("eventId", "")?.split(",")?.mapNotNull { it.toIntOrNull() }
                ?.toIntArray() ?: intArrayOf()
        planningId = sharedPreferences.getString("planningId", "")?.split(",")
            ?.mapNotNull { it.toIntOrNull() }?.toIntArray() ?: intArrayOf()
        athleteId = sharedPreferences.getString("athleteId", "")?.split(",")
            ?.mapNotNull { it.toIntOrNull() }?.toIntArray() ?: intArrayOf()

        Log.d("SharedPreferences", "Loaded lessonId: ${lessonId.joinToString()}")
        Log.d("SharedPreferences", "Loaded testId: ${testId.joinToString()}")
        Log.d("SharedPreferences", "Loaded eventId: ${eventId.joinToString()}")
        Log.d("SharedPreferences", "Loaded planningId: ${planningId.joinToString()}")
        Log.d("SharedPreferences", "Loaded athleteId: ${athleteId.joinToString()}")
    }

    private fun mergeIdsAndSave() {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)

        // Load existing IDs
        val existingLessonIds =
            sharedPreferences.getString("lessonId", "")?.split(",")?.mapNotNull { it.toIntOrNull() }
                ?.toSet() ?: emptySet()
        val existingTestIds =
            sharedPreferences.getString("testId", "")?.split(",")?.mapNotNull { it.toIntOrNull() }
                ?.toSet() ?: emptySet()
        val existingEventIds =
            sharedPreferences.getString("eventId", "")?.split(",")?.mapNotNull { it.toIntOrNull() }
                ?.toSet() ?: emptySet()
        val existingPlanningIds = sharedPreferences.getString("planningId", "")?.split(",")
            ?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet()
        val existingAthleteIds = sharedPreferences.getString("athleteId", "")?.split(",")
            ?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet()

        // Merge new IDs with existing IDs
        lessonId = (existingLessonIds + lessonId.toList()).toList().toIntArray()
        testId = (existingTestIds + testId.toList()).toList().toIntArray()
        eventId = (existingEventIds + eventId.toList()).toList().toIntArray()
        planningId = (existingPlanningIds + planningId.toList()).toList().toIntArray()
        athleteId = (existingAthleteIds + athleteId.toList()).toList().toIntArray()

        // Save merged IDs
        saveIdsToPreferences()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        shouldSaveData = false
        clearIdsFromPreferences()
        clearGroupDataOnBack()
        clearDayTimesOnBack()
        Log.d("Lifecycle", "IDs cleared on destroy")
    }

    private fun clearIdsFromPreferences() {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Clear all ID-related entries
        editor.remove("lessonId")
        editor.remove("testId")
        editor.remove("eventId")
        editor.remove("planningId")
        editor.remove("athleteId")

        editor.apply()
    }


    private fun getSportData() {
        try {
            apiInterface.sportlist()?.enqueue(object : Callback<SportlistData?> {
                override fun onResponse(
                    call: Call<SportlistData?>,
                    response: Response<SportlistData?>
                ) {
                    Log.d("TAG", response.code().toString() + "")
                    val code = response.code()
                    if (code == 200) {
                        val resource: SportlistData? = response.body()
                        val Success: Boolean = resource?.status!!
                        val Message: String = resource.message!!
                        if (resource.data != null) {
                            val data = resource.data
                            if (data != null) {
                                goalData.addAll(data.toMutableList())

                                for (i in goalData) {
                                    Goal.add(i.title!!)
                                }

                            } else {
                                Toast.makeText(
                                    this@CreateGropActivity,
                                    "No Data Found",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            createGroupBinding.progressBar.visibility = View.GONE
                        }
                    } else if (response.code() == 403) {
                        Utils.setUnAuthDialog(this@CreateGropActivity)

                    } else {
                        createGroupBinding.progressBar.visibility = View.GONE
                        val message = response.message()
                        Toast.makeText(
                            this@CreateGropActivity,
                            "" + message,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }


                override fun onFailure(call: Call<SportlistData?>, t: Throwable) {
                    Toast.makeText(this@CreateGropActivity, "" + t.message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                    createGroupBinding.progressBar.visibility = View.GONE
                }
            })
        } catch (e: Exception) {
            Log.d("Error Exception", "${e.message}")
        }
    }

    private fun showPopup(
        anchorView: View?,
        data: MutableList<SportlistData.sportlist>,
        editText: EditText,
        list: ArrayList<String>,
        selectedValue: SelectedValue
    ) {

        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.popup_list, null)
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true // Focusable to allow outside clicks to dismiss
        )
        popupWindow.setBackgroundDrawable(
            ContextCompat.getDrawable(
                this@CreateGropActivity,
                R.drawable.popup_background
            )
        )
        popupWindow.elevation = 10f
        val listView = popupView.findViewById<ListView>(R.id.listView)

        val adapter =
            object : ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list) {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val view = super.getView(position, convertView, parent) as TextView
                    view.setTextColor(Color.WHITE) // Set text color to white
                    return view
                }
            }
        listView.adapter = adapter
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = list[position]
            editText.setText(selectedItem)

            // Set the selected id to the sportlId object
            sportlId.id = data.filter { it.title == selectedItem }.first().id!!

            // Log only the id (not the whole SelectedValue object)
            Log.d("FGFGFGGF", "showPopup: ${sportlId.id}")

            popupWindow.dismiss()
        }


        popupWindow.showAsDropDown(anchorView)
        popupWindow.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                this,
                android.R.color.white
            )
        )
    }

    private fun getAthleteData(ids: List<List<String>>) {
        Log.d("GetAthleteData", "Started with IDs: $ids")

        val flatIds = ids.flatten()
        Log.d("GetAthleteData", "Flattened IDs: $flatIds")

        athleteData.clear() // Clear previous data
        createGroupBinding.progressBar.visibility = View.VISIBLE

        apiInterface.GetAthleteList()?.enqueue(object : Callback<AthleteData> {
            override fun onResponse(call: Call<AthleteData>, response: Response<AthleteData>) {
                createGroupBinding.progressBar.visibility = View.GONE
                Log.d("GetAthleteData", "Response received with code: ${response.code()}")

                if (response.isSuccessful) {
                    val resource = response.body()
                    val success = resource?.status ?: false
                    val message = resource?.message ?: ""

                    if (success) {
                        val data = resource!!.data
                        val filteredData: List<AthleteData.Athlete> =
                            data?.filter { it.id?.toString() in flatIds } ?: emptyList()

                        Log.d("GetAthleteData", "Filtered Data: $filteredData")
                        athleteData.clear()
                        athleteData.addAll(filteredData)

                        if (filteredData.isNotEmpty()) {
                            initrecyclerAthlete(filteredData.toMutableList())
                        } else {
                            Log.d("GetAthleteData", "No matching athletes found")
                        }

                        for (athlete in athleteData) {
                            Log.d("GetAthleteData", "Athlete ID: ${athlete.id}")
                        }

                    } else if (response.code() == 403) {
                        Utils.setUnAuthDialog(this@CreateGropActivity)
                    } else {
                        Toast.makeText(
                            this@CreateGropActivity,
                            response.message(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<AthleteData>, t: Throwable) {
                createGroupBinding.progressBar.visibility = View.GONE
                Log.d("GetAthleteData", "Failure: ${t.message}")
                Toast.makeText(this@CreateGropActivity, t.message, Toast.LENGTH_SHORT).show()
                call.cancel()
            }
        })
    }

    private fun getTrainingData(ids: List<List<String>>) {
        Log.d("GetTrainingData", "Started with IDs: $ids")

        val flatIds = ids.flatten()
        Log.d("GetTrainingData", "Flattened IDs: $flatIds")

        createGroupBinding.progressBar.visibility = View.VISIBLE

        apiInterface.GetTrainingPlan()?.enqueue(object : Callback<TrainingPlanData> {
            override fun onResponse(
                call: Call<TrainingPlanData>,
                response: Response<TrainingPlanData>
            ) {
                Log.d("GetTrainingData", "Response received with code: ${response.code()}")
                createGroupBinding.progressBar.visibility = View.GONE
                val code = response.code()
                if (code == 200) {
                    val resource = response.body()
                    if (resource != null) {
                        val data = resource.data
                        val filteredData: List<TrainingPlanData.TrainingPlan> =
                            data?.filter { it.id?.toString() in flatIds } ?: emptyList()
                        Log.d("GetTrainingData", "Filtered Data: $filteredData")
                        plainngData.clear()
                        plainngData.addAll(filteredData)
                        if (filteredData.isNotEmpty()) {
                            initrecyclerplanning(filteredData.toMutableList())
                        } else {
                            Log.d("GetTrainingData", "No matching plans found")
                        }
                    } else {
                        Log.d("GetTrainingData", "Response body is null")
                    }
                } else {
                    Log.d("GetTrainingData", "Unexpected response code: $code")
                }
            }

            override fun onFailure(call: Call<TrainingPlanData>, t: Throwable) {
                Log.d("GetTrainingData", "Failure: ${t.message}")
                Toast.makeText(this@CreateGropActivity, "Error: " + t.message, Toast.LENGTH_SHORT)
                    .show()
                createGroupBinding.progressBar.visibility = View.GONE
                call.cancel()
            }
        })
    }

    private fun GetLessonList(ids: List<List<String>>) {
        Log.d("GetLessonList", "Started with IDs: $ids")

        val flatIds = ids.flatten()
        Log.d("GetLessonList", "Flattened IDs: $flatIds")

        createGroupBinding.progressBar.visibility = View.VISIBLE

        apiInterface.GetLession1().enqueue(object : Callback<Lesson> {
            override fun onResponse(call: Call<Lesson>, response: Response<Lesson>) {
                Log.d("GetLessonList", "Response received with code: ${response.code()}")
                createGroupBinding.progressBar.visibility = View.GONE
                val code = response.code()
                if (code == 200) {
                    val resource = response.body()
                    if (resource != null) {
                        val data = resource.data
                        val filteredData: List<Lesson.LessonDatabase> =
                            data?.filter { it.id?.toString() in flatIds } ?: emptyList()
                        Log.d("GetLessonList", "Filtered Data: $filteredData")
                        lessonData.clear()
                        lessonData.addAll(filteredData)
                        if (filteredData.isNotEmpty()) {
                            initRecyclerview(filteredData)
                        } else {

                        }
                    } else {
                        Log.d("GetLessonList", "Response body is null")
                    }
                } else {
                    Log.d("GetLessonList", "Unexpected response code: $code")
                }
            }

            override fun onFailure(call: Call<Lesson>, t: Throwable) {
                Log.d("GetLessonList", "Failure: ${t.message}")
                Toast.makeText(this@CreateGropActivity, "Error: " + t.message, Toast.LENGTH_SHORT)
                    .show()
                call.cancel()
            }
        })
    }

    private fun GetTestList(ids: List<List<String>>) {
        createGroupBinding.progressBar.visibility = View.VISIBLE

        val flatIds = ids.flatten()
        Log.d("GetTestList", "Flattened IDs: $flatIds")

        apiInterface.GetTest()?.enqueue(object : Callback<TestListData?> {
            override fun onResponse(call: Call<TestListData?>, response: Response<TestListData?>) {
                Log.d("GetTestList", "Response received with code: ${response.code()}")
                createGroupBinding.progressBar.visibility = View.GONE
                val resource = response.body()
                val code = response.code()

                if (code == 200) {
                    val resource = response.body()
                    if (resource != null) {
                        val data = resource.data
                        val filteredData: List<TestListData.testData> =
                            data?.filter { it.id?.toString() in flatIds } ?: emptyList()
                        Log.d("GetLessonList", "Filtered Data: $filteredData")
                        TestData.clear()
                        TestData.addAll(filteredData)
                        if (filteredData.isNotEmpty()) {
                            initRecyclerTest(ArrayList(filteredData))  // Convert List to ArrayList if needed
                        } else {

                        }
                    } else {
                        Log.d("GetLessonList", "Response body is null")
                    }
                } else {
                    Log.d("GetLessonList", "Unexpected response code: $code")
                }
            }

            override fun onFailure(call: Call<TestListData?>, t: Throwable) {
                createGroupBinding.progressBar.visibility = View.GONE
                Toast.makeText(this@CreateGropActivity, "Error: ${t.message}", Toast.LENGTH_SHORT)
                    .show()
                call.cancel()
            }
        })
    }

    private fun geteventlist(ids: List<List<String>>) {
        createGroupBinding.progressBar.visibility = View.VISIBLE

        val flatIds = ids.flatten()
        Log.d("GetEventList", "Flattened IDs: $flatIds")

        apiInterface.GetEvent()?.enqueue(object : Callback<EventListData?> {
            override fun onResponse(
                call: Call<EventListData?>,
                response: Response<EventListData?>
            ) {
                Log.d("TAG", response.code().toString())
                val code = response.code()

                if (code == 200) {
                    val resource = response.body()
                    if (resource != null) {
                        val data = resource.data
                        val filteredData: List<EventListData.testData> =
                            data?.filter { it.id?.toString() in flatIds } ?: emptyList()
                        Log.d("GetLessonList", "Filtered Data: $filteredData")
                        EventData.clear()
                        EventData.addAll(filteredData)
                        if (filteredData.isNotEmpty()) {
                            initrecycler(ArrayList(filteredData))  // Convert List to ArrayList if needed
                        } else {

                        }
                    } else {
                        Log.d("GetLessonList", "Response body is null")
                    }
                } else {
                    Log.d("GetLessonList", "Unexpected response code: $code")
                }
            }

            override fun onFailure(call: Call<EventListData?>, t: Throwable) {
                createGroupBinding.progressBar.visibility = View.GONE
                Toast.makeText(this@CreateGropActivity, "Error: ${t.message}", Toast.LENGTH_SHORT)
                    .show()
                call.cancel()
            }
        })
    }

    private fun initrecyclerAthlete(testdatalist: MutableList<AthleteData.Athlete>?) {
        createGroupBinding.progressBar.visibility = View.GONE
        createGroupBinding.rlyAthlete.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        adapterAthlete = SetAthleteInGroup(testdatalist, this, this)
        createGroupBinding.rlyAthlete.adapter = adapterAthlete
    }

    private fun initrecyclerplanning(testdatalist: MutableList<TrainingPlanData.TrainingPlan>?) {
        createGroupBinding.progressBar.visibility = View.GONE
        createGroupBinding.rlyPlanning.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        adapterPlanning = SetPlanningGroup(testdatalist, this, this)
        createGroupBinding.rlyPlanning.adapter = adapterPlanning
    }

    private fun initrecycler(testdatalist: ArrayList<EventListData.testData>?) {
        createGroupBinding.progressBar.visibility = View.GONE
        eventAdapter = SetEventInGroup(testdatalist, this, this)
        createGroupBinding.rlyEvent.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        createGroupBinding.rlyEvent.adapter = eventAdapter

        eventAdapter?.notifyDataSetChanged()
    }

    private fun initRecyclerTest(testdatalist: ArrayList<TestListData.testData>) {
        createGroupBinding.progressBar.visibility = View.GONE
        createGroupBinding.rlyTest.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        testAdapter = SetTestInGroup(testdatalist, this, this)
        createGroupBinding.rlyTest.adapter = testAdapter
    }

    private fun initRecyclerview(data: List<Lesson.LessonDatabase>) {
        createGroupBinding.rlyLesson.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        lessonAdapter = SetLessonInGroup(data, this, this)
        createGroupBinding.rlyLesson.adapter = lessonAdapter
    }

//    private fun collectTimings(): String {
//        val timingList = mutableListOf<MutableList<Map<String, String>>>() // List to store timings for all days in separate lists
//
//        // Collect timings for each day
//        val daysLayout = listOf(
//            mon_linearLayour to "monday",
//            tue_linearLayout to "tuesday",
//            wed_linearLayout to "wednesday",
//            thu_linearLayout to "thursday",
//            fri_linearLayout to "friday",
//            sat_linearLayout to "saturday",
//            sun_linearLayout to "sunday"
//        )
//
//        // Iterate through the days
//        for ((layout, day) in daysLayout) {
//            val dailyTimings = mutableListOf<Map<String, String>>() // Collect timings for the specific day
//
//            for (i in 0 until layout.childCount) {
//                val childView = layout.getChildAt(i)
//                val tvStartTime = childView.findViewById<TextView>(R.id.tv_start_time)
//                val tvEndTime = childView.findViewById<TextView>(R.id.tv_End_time)
//
//                val startTime = formatTime(tvStartTime.text.toString().trim())
//                val endTime = formatTime(tvEndTime.text.toString().trim())
//
//                if (startTime.isNotEmpty() && endTime.isNotEmpty()) {
//                    val timingMap = mapOf(
//                        "start_time" to startTime,
//                        "end_time" to endTime
//                    )
//                    dailyTimings.add(timingMap) // Add this timing to the day's list
//                }
//            }
//
//            if (dailyTimings.isNotEmpty()) {
//                // Add this day's timings to the timing list (flattened)
//                timingList.add(dailyTimings)
//            }
//        }
//
//        val gson = Gson()
//        Log.d("TIMINGS_DEBUG", "Timing List: $timingList")
//        return gson.toJson(timingList) // Serialize the timings data into JSON format
//    }

    private fun formatTime(time: String): String {
        return if (time.isNotEmpty()) {
            // If the time doesn't have a leading zero, add it
            val parts = time.split(":")
            if (parts.size == 2) {
                val hour = parts[0].padStart(2, '0') // Pad the hour with a leading zero if needed
                val minute =
                    parts[1].padStart(2, '0') // Pad the minute with a leading zero if needed
                "$hour:$minute"
            } else {
                time
            }
        } else {
            time
        }
    }

    // Monday Add View Method
// Monday Add View Method
    fun mon_addView() {
        addViewForDay(mon_linearLayour, "monday")
    }

    fun tue_addView() {
        addViewForDay(tue_linearLayout, "tuesday")
    }

    // Similarly for other days
    fun wed_addView() {
        addViewForDay(wed_linearLayout, "wednesday")
    }

    fun thu_addView() {
        addViewForDay(thu_linearLayout, "thursday")
    }

    fun fri_addView() {
        addViewForDay(fri_linearLayout, "friday")
    }

    fun sat_addView() {
        addViewForDay(sat_linearLayout, "saturday")
    }

    fun sun_addView() {
        addViewForDay(sun_linearLayout, "sunday")
    }


    private val isStartTimeValidation: Boolean
        get() {
            start_error.visibility = View.GONE
            var start_time = tv_start_time.text.toString()
            if (start_time == "") {
                start_error.visibility = View.VISIBLE
                start_error.text = "Invalid Start Time"
                return false
            } else {
                start_error.visibility = View.GONE
            }
            return true
        }

    private val isEndTimeValidation: Boolean
        get() {
            end_error.visibility = View.GONE
            var end_time = tv_End_time.text.toString()
            if (end_time == "") {
                end_error.visibility = View.VISIBLE
                end_error.text = "Invalid End Time"
                return false
            } else {
                end_error.visibility = View.GONE
            }
            return true
        }

    private val isValidate: Boolean
        get() {
            var start_time = tv_start_time.text.toString()
            if (start_time == "") {
                start_error.visibility = View.VISIBLE
                start_error.text = "Invalid Start Time"
                return false
            } else {
                start_error.visibility = View.GONE
            }
            var end_time = tv_End_time.text.toString()
            if (end_time == "") {
                end_error.visibility = View.VISIBLE
                end_error.text = "Invalid End Time"
                return false
            } else {
                end_error.visibility = View.GONE
            }
            return true
        }

    fun setDialogEnd(targetTextView: EditText) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_set_time_picker)

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = (displayMetrics.widthPixels * 0.9f).toInt()
        val height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.setLayout(width, height)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val hourPicker = dialog.findViewById<NumberPicker>(R.id.hour_num)
        val minPicker = dialog.findViewById<NumberPicker>(R.id.mint_num)
        val btnApply = dialog.findViewById<Button>(R.id.btnApply)
        val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)

        hourPicker.minValue = 0
        hourPicker.maxValue = 23
        minPicker.minValue = 0
        minPicker.maxValue = 59

        btnCancel.setOnClickListener { dialog.dismiss() }
        btnApply.setOnClickListener {
            val selectedHour = hourPicker.value
            val selectedMinute = minPicker.value
            targetTextView.setText(String.format("%02d:%02d", selectedHour, selectedMinute))
            dialog.dismiss()
        }
        dialog.show()
    }

    fun SetDialog_start(targetTextView: EditText) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_set_time_picker)

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = (displayMetrics.widthPixels * 0.9f).toInt()
        val height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.setLayout(width, height)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val hourPicker = dialog.findViewById<NumberPicker>(R.id.hour_num)
        val minPicker = dialog.findViewById<NumberPicker>(R.id.mint_num)
        val btnApply = dialog.findViewById<Button>(R.id.btnApply)
        val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)

        // Set initial values for NumberPickers
        hourPicker.minValue = 1
        hourPicker.maxValue = 24
        minPicker.minValue = 0
        minPicker.maxValue = 59

        // Set up the cancel button to dismiss the dialog
        btnCancel.setOnClickListener { dialog.dismiss() }

        // Set up the apply button to set the selected time and dismiss the dialog
        btnApply.setOnClickListener {
            val selectedHour = hourPicker.value
            val selectedMinute = minPicker.value

            // Format the time and set it in the target TextView (EditText)
            targetTextView.setText(String.format("%02d:%02d", selectedHour, selectedMinute))

            // Dismiss the dialog
            dialog.dismiss()
        }

        // Show the dialog
        dialog.show()
    }


    // Save data for all days
    private fun addViewForDay(linearLayout: LinearLayout, dayKey: String) {
        val inflater = LayoutInflater.from(this).inflate(R.layout.time_layout_group, null)
        linearLayout.addView(inflater, linearLayout.childCount)

        val tvStartTime: EditText = inflater.findViewById(R.id.tv_start_time)
        val tvEndTime: EditText = inflater.findViewById(R.id.tv_End_time)
        val tvStartTimeCard: CardView = inflater.findViewById(R.id.start_time_card)
        val tvEndTimeCard: CardView = inflater.findViewById(R.id.card_end_time)

        // Generate a unique ID for this view (e.g., based on index or timestamp)
        val id = linearLayout.childCount.toString()

        // Add default entry for the new view
        updateDayTimes(dayKey, id, "", "")

        // Save state immediately after adding a new view
        saveDayTimesToPreferences()

        tvStartTimeCard.setOnClickListener {
            SetDialog_start(tvStartTime)
        }
        tvEndTimeCard.setOnClickListener {
            setDialogEnd(tvEndTime)
        }

        tvStartTime.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                updateDayTimes(dayKey, id, s.toString(), tvEndTime.text.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        tvEndTime.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                updateDayTimes(dayKey, id, tvStartTime.text.toString(), s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun updateDayTimes(dayKey: String, id: String, startTime: String, endTime: String) {
        val dayTime = DayTime(id, startTime, endTime)
        val times = dayTimes[dayKey]?.toMutableList() ?: mutableListOf()
        val index = times.indexOfFirst { it.id == id }

        if (index >= 0) {
            times[index] = dayTime
        } else {
            times.add(dayTime)
        }

        dayTimes[dayKey] = times
        saveDayTimesToPreferences()
    }

    fun clearDayTimesOnBack() {
        // Clear the data in SharedPreferences
        val sharedPreferences = getSharedPreferences("DayTimesPrefs", MODE_PRIVATE)
        sharedPreferences.edit().remove("day_times").apply()

        // Clear the in-memory data map
        dayTimes.clear()
    }


    // Save data for all days
    fun saveDayTimesToPreferences() {
        val sharedPreferences = getSharedPreferences("DayTimesPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()

        // Convert the dayTimes map to a JSON string
        val jsonString = gson.toJson(dayTimes)
        editor.putString("day_times", jsonString)
        editor.apply()
    }

    // Load data for all days
    fun loadDayTimesFromPreferences() {
        val sharedPreferences = getSharedPreferences("DayTimesPrefs", MODE_PRIVATE)
        val gson = Gson()
        val jsonString = sharedPreferences.getString("day_times", null)

        val type = object : TypeToken<Map<String, List<DayTime>>>() {}.type
        val loadedData: Map<String, List<DayTime>> = if (jsonString != null) {
            gson.fromJson(jsonString, type)
        } else {
            emptyMap()
        }

        // Merge loaded data into the default `dayTimes` map
        loadedData.forEach { (day, times) ->
            dayTimes[day] = times.toMutableList()
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            val uri: Uri? = data?.data
            if (uri != null) {
                Log.d("FGFGFGGF", "onActivityResult: $uri")
                selectedImageUri = uri

                val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("selectedImageUri", uri.toString())
                editor.apply()

                Log.d("SharedPrefsDebug", "Saved URI in CreateGroupActivity: $uri")


                createGroupBinding.selectUploadLy.visibility = View.GONE
                createGroupBinding.imageUpload.visibility = View.VISIBLE
                createGroupBinding.imageUpload.setImageURI(uri) // Display the selected image
            }
        }
    }


    override fun onItemClicked(view: View, position: Int, type: Long, string: String) {
    }
}