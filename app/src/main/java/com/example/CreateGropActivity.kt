package com.example

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.NumberPicker
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.Adapter.groups.SetAthleteInGroup
import com.example.Adapter.groups.SetEventInGroup
import com.example.Adapter.groups.SetLessonInGroup
import com.example.Adapter.groups.SetPlanningGroup
import com.example.Adapter.groups.SetTestInGroup
import com.example.model.DayTime
import com.example.model.ScheduleItem
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
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
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
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.log


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
    lateinit var tv_start_time: AppCompatEditText
    lateinit var tv_End_time: AppCompatEditText
    lateinit var start_error: AppCompatTextView
    lateinit var end_error: TextView
    var lessonId: IntArray = intArrayOf()

    private var isMonViewAdded = false
    private var firstview = false

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


        val inflater = LayoutInflater.from(this).inflate(R.layout.time_layout_group, null)
        tv_start_time = inflater.findViewById(R.id.tv_start_time)
        tv_End_time = inflater.findViewById(R.id.tv_End_time)


        start_error = inflater.findViewById(R.id.start_error)
        end_error = inflater.findViewById(R.id.end_error)



        loadIdsFromPreferences()
//        loadGroupData()
//        loadDayTimesFromPreferences()
        setupTextWatcher()



        dayTimes.forEach { (day, times) ->
            val linearLayout = getLinearLayoutForDay(day)
            linearLayout.removeAllViews()

            for (time in times) {
                val inflater = LayoutInflater.from(this).inflate(R.layout.time_layout_group, null)
                linearLayout.addView(inflater)

                val tv_start_time: EditText = inflater.findViewById(R.id.tv_start_time)
                val tv_End_time: EditText = inflater.findViewById(R.id.tv_End_time)

                tv_start_time.setText(time.startTime)
                tv_End_time.setText(time.endTime)

                val tv_start_timeCard: CardView = inflater.findViewById(R.id.start_time_card)
                val tv_End_timeCard: CardView = inflater.findViewById(R.id.card_end_time)



                tv_start_timeCard.setOnClickListener {
                    SetDialog_start(tv_start_time)
                }
                tv_End_timeCard.setOnClickListener {
                    setDialogEnd(tv_End_time)
                }
            }
        }

        if (!isMonViewAdded) {
            toggleDay(
                "monday",
                createGroupBinding.weekMon,
                mon_linearLayour,
                createGroupBinding.monAddScheduleTime
            )
            firstview = true // Set the flag to true


            mon_addView() // Add the view only if it hasn't been added yet
            isMonViewAdded = true // Set the flag to true
            firstview = false

            Log.d("TYTYTYT","GDGDGDGG :- $firstview")
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
            val linearLayout = createGroupBinding.MonLinearLayout

            val isValid = validateAllFields(linearLayout)
            if (!isValid) {
                start_error.visibility = View.VISIBLE
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            } else {
                mon_addView()
            }


        }
        createGroupBinding.tueAddScheduleTime.setOnClickListener {
//            if (isValidate){
//                start_error.visibility = View.GONE
//                end_error.visibility = View.GONE
//                tue_addView()
//            }

            val linearLayout = createGroupBinding.TueLinearLayout

            val isValid = validateAllFields(linearLayout)
            if (!isValid) {
                start_error.visibility = View.VISIBLE
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            } else {
                tue_addView()
            }

//            tue_addView()
        }
        createGroupBinding.wedAddScheduleTime.setOnClickListener {
//            if (isValidate){
//                start_error.visibility = View.GONE
//                end_error.visibility = View.GONE
//                wed_addView()
//            }
            val linearLayout = createGroupBinding.WedLinearLayout

            val isValid = validateAllFields(linearLayout)
            if (!isValid) {
                start_error.visibility = View.VISIBLE
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            } else {
                wed_addView()
            }

//            wed_addView()

        }
        createGroupBinding.thuAddScheduleTime.setOnClickListener {
//            if (isValidate){
//                createGroupBinding..visibility = View.GONE
//                createGroupBinding.end_error.visibility = View.GONE
//
//
//            }
            val linearLayout = createGroupBinding.ThuLinearLayout

            val isValid = validateAllFields(linearLayout)
            if (!isValid) {
                start_error.visibility = View.VISIBLE
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            } else {
                thu_addView()
            }
        }
        createGroupBinding.friAddScheduleTime.setOnClickListener {
//            if (isValidate){
//                start_error.visibility = View.GONE
//                end_error.visibility = View.GONE
//
//            }
            val linearLayout = createGroupBinding.FriLinearLayout

            val isValid = validateAllFields(linearLayout)
            if (!isValid) {
                start_error.visibility = View.VISIBLE
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            } else {
                fri_addView()
            }

        }
        createGroupBinding.satAddScheduleTime.setOnClickListener {
//            if (isValidate){
//                start_error.visibility = View.GONE
//                end_error.visibility = View.GONE
//
//            }
            val linearLayout = createGroupBinding.SatLinearLayout

            val isValid = validateAllFields(linearLayout)
            if (!isValid) {
                start_error.visibility = View.VISIBLE
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            } else {
                sat_addView()
            }

        }
        createGroupBinding.sunAddScheduleTime.setOnClickListener {
//            if (isValidate){
//                start_error.visibility = View.GONE
//                end_error.visibility = View.GONE
//
//            }
            val linearLayout = createGroupBinding.SunLinearLayout

            val isValid = validateAllFields(linearLayout)
            if (!isValid) {
                start_error.visibility = View.VISIBLE
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            } else {
                sun_addView()
            }
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

            createGroupBinding.main.setBackgroundColor(resources.getColor(R.color.grey))
            createGroupBinding.rlyPlanning.visibility = View.GONE
            createGroupBinding.rlyLesson.visibility = View.GONE
            createGroupBinding.rlyTest.visibility = View.GONE
            createGroupBinding.rlyEvent.visibility = View.GONE
            createGroupBinding.rlyAthlete.visibility = View.GONE

            createGroupBinding.MonLinearLayout.visibility = View.GONE
            createGroupBinding.TueLinearLayout.visibility = View.GONE
            createGroupBinding.WedLinearLayout.visibility = View.GONE
            createGroupBinding.ThuLinearLayout.visibility = View.GONE
            createGroupBinding.FriLinearLayout.visibility = View.GONE
            createGroupBinding.SatLinearLayout.visibility = View.GONE
            createGroupBinding.SunLinearLayout.visibility = View.GONE

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
            dialog.window!!.setLayout(width, height)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


            val close = dialog.findViewById<CardView>(R.id.next_card)
            close.setOnClickListener {
                createGroupBinding.main.setBackgroundColor(resources.getColor(R.color.black))
                createGroupBinding.MonLinearLayout.visibility = View.VISIBLE
                createGroupBinding.TueLinearLayout.visibility = View.VISIBLE
                createGroupBinding.WedLinearLayout.visibility = View.VISIBLE
                createGroupBinding.ThuLinearLayout.visibility = View.VISIBLE
                createGroupBinding.FriLinearLayout.visibility = View.VISIBLE
                createGroupBinding.SatLinearLayout.visibility = View.VISIBLE
                createGroupBinding.SunLinearLayout.visibility = View.VISIBLE

                createGroupBinding.rlyPlanning.visibility = View.VISIBLE
                createGroupBinding.rlyLesson.visibility = View.VISIBLE
                createGroupBinding.rlyTest.visibility = View.VISIBLE
                createGroupBinding.rlyEvent.visibility = View.VISIBLE
                createGroupBinding.rlyAthlete.visibility = View.VISIBLE

                dialog.dismiss()
            }

            dialog.show()
        }


        createGroupBinding.selectImage.setOnClickListener {
//            val intent = Intent()
//            intent.type = "image/*"
//            intent.action = Intent.ACTION_GET_CONTENT
//            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1)

            getContent.launch("image/*")

        }

        createGroupBinding.back.setOnClickListener {
            shouldSaveData = false
            onBackPressed()
            clearGroupDataOnBack()
            clearDayTimesOnBack()
            clearIdsFromPreferences()
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("group", "addGroup")
            startActivity(intent)
        }

        createGroupBinding.nextButtonText.setOnClickListener {

            val start = tv_start_time.text.toString()
            val end = tv_End_time.text.toString()

            Log.d("GHHGHGHGHGHGH", "onCreate: $start    $end")
            Log.d("GHHGHGHGHGHGH", "onCreate: ${start_error.text.toString()}    $end")

            if (start == null || start == "") {
                start_error.visibility = View.VISIBLE
                Toast.makeText(this, "Start field is required", Toast.LENGTH_SHORT)
                    .show() // Toast for start field
                return@setOnClickListener
            }

            if (end == null || end == "") {
                end_error.visibility = View.VISIBLE
                Toast.makeText(this, "End field is required", Toast.LENGTH_SHORT).show() // Toast for end field
                return@setOnClickListener
            }


            createGroupBinding.progressBar.visibility = View.VISIBLE
            val selectedImageUri: Uri? = selectedImageUri

            if (selectedImageUri != null) {
                addGroup(this, selectedImageUri)
            } else {
                createGroupBinding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun areAllFieldsFilled(): Boolean {
        return !(createGroupBinding.edtName.text.isNullOrEmpty() || createGroupBinding.edtSport.text.isNullOrEmpty())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateUI(addButton: AppCompatButton) {
        if (areAllFieldsFilled()) {
            addButton.isEnabled = true
            addButton.setBackgroundResource(R.drawable.active_save_btn)
        } else {
            addButton.isEnabled = false
            addButton.setBackgroundResource(R.drawable.save_buttton)
        }
    }

    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedImageUri = it
                Log.d("SUJALLLLLL", "Selected Image URI: $it")
                Log.d("SUJALLLLLL", "Selected Image URI: $selectedImageUri")
                createGroupBinding.selectUploadLy.visibility = View.GONE
                createGroupBinding.imageUpload.visibility = View.VISIBLE

                val sharedPreferences =
                    createGroupBinding.root.context.getSharedPreferences(
                        "appPrefs",
                        Context.MODE_PRIVATE
                    )
                sharedPreferences.edit().putString("imageUrll", uri.toString()).apply()

                Picasso.get()
                    .load(uri)
                    .error(R.drawable.app_icon)
                    .into(createGroupBinding.imageUpload, object : com.squareup.picasso.Callback {
                        override fun onSuccess() {
                            Log.d("Picasso", "Image loaded successfully")

                            val imagePart = processImage(createGroupBinding.root.context, uri)

                            if (imagePart != null) {
                                Log.d(
                                    "ImagePart",
                                    "Image file created: ${imagePart.body.contentType()}"
                                )
                                // Call the API with the image part
                                selectedImageUri = uri
//                                editGroupWithImageApiCall(binding.root.context, uri)
                            } else {
                                Log.e("ImagePart", "Failed to create image file from URI")
                            }
                        }

                        override fun onError(e: Exception?) {
                            Log.e("Picasso", "Error loading image", e)
                        }
                    })
            } ?: run {
                Log.e("ImageUri", "No image selected")
            }
        }

    private fun setupTextWatcher() {
        createGroupBinding.edtName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun afterTextChanged(s: Editable?) {
                updateUI(createGroupBinding.nextButtonText)

            }

        })
        createGroupBinding.edtSport.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            @RequiresApi(Build.VERSION_CODES.O)
            override fun afterTextChanged(s: Editable?) {
                updateUI(createGroupBinding.nextButtonText)
            }
        })

    }

    private fun addGroup(context: Context, uri: Uri) {
        shouldSaveData = false


        Log.d("FNFNFNFN", "onResponse: GRoup Added")


        val timingFormatted = collectTimings().toString()
        val daysids =
            selectedDays.joinToString(prefix = "[", postfix = "]", separator = ", ") { "\"$it\"" }
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

        val imagePart = processImage(context, uri)
        if (imagePart == null) {
            Log.d("NULLLLLLLL", "addGroup: $imagePart")
            return
        }


//        val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), imageFile)
//        val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)

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
                Log.d("FNFNFNFN", "onResponse: GRoup Added")


                if (response.isSuccessful) {
                    Toast.makeText(this@CreateGropActivity, "Group Added", Toast.LENGTH_SHORT)
                        .show()
                    Log.d("FNFNFNFN", "onResponse: GRoup Added success")

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

    fun processImage(context: Context, imageUri: Uri?): MultipartBody.Part? {
        Log.d("SUJALLLLLLGGGGG", "processImage: $imageUri")

        // Use the passed image URI (from gallery)
        val imageFile = imageUri?.let { createFileFromContentUri(context, it) }
        if (imageFile != null && imageFile.exists()) {
            val imageRequestBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            return MultipartBody.Part.createFormData("image", imageFile.name, imageRequestBody)
        }

        // Fallback to the image in SharedPreferences if no new image is selected
        val imageUrl = getImageUriFromPreferences(context)?.toString()
        Log.e("ProcessImage", "Image URL from SharedPreferences: $imageUrl")

        if (!imageUrl.isNullOrEmpty()) {
            val imageFileFromUrl = runBlocking { convertUrlToFile(context, imageUrl) }
            if (imageFileFromUrl != null && imageFileFromUrl.exists()) {
                val imageRequestBody = imageFileFromUrl.asRequestBody("image/*".toMediaTypeOrNull())
                return MultipartBody.Part.createFormData(
                    "image",
                    imageFileFromUrl.name,
                    imageRequestBody
                )
            } else {
                Log.e("ProcessImage", "Error creating image file from URL.")
            }
        }

        Log.e("ProcessImage", "No valid image found. Returning null.")
        return null
    }


    fun getImageUriFromPreferences(context: Context): Uri? {
        val sharedPreferences = context.getSharedPreferences("appPrefs", Context.MODE_PRIVATE)
        val imageUrl = sharedPreferences.getString("imageUrll", null)
        return imageUrl?.let { Uri.parse(it) }
    }

    suspend fun convertUrlToFile(context: Context, imageUrl: String): File? {
        return withContext(Dispatchers.IO) {
            try {
                val fileName = imageUrl.substringAfterLast("/")
                val file = File(context.cacheDir, fileName)

                Log.d("TAG", "convertUrlToFile: ")
                // Check if the file already exists
                if (file.exists()) {
                    Log.d("ConvertUrlToFile", "File already exists: ${file.absolutePath}")
                    return@withContext file
                }

                // Download and save the file
                val url = URL(imageUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.connect()

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    connection.inputStream.use { input ->
                        FileOutputStream(file).use { output ->
                            input.copyTo(output)
                        }
                    }
                    Log.d("ConvertUrlToFile", "File downloaded and saved: ${file.absolutePath}")
                    return@withContext file
                } else {
                    Log.e(
                        "ConvertUrlToFile",
                        "Failed to download file. Response code: ${connection.responseCode}"
                    )
                }
            } catch (e: Exception) {
                Log.e("ConvertUrlToFile", "Error converting URL to file: ${e.message}", e)
            }

            return@withContext null
        }
    }

    fun createFileFromContentUri(context: Context, contentUri: Uri?): File? {
        Log.d("SUJALLLLLL", "createFileFromContentUri: $contentUri")

        if (contentUri == null) {
            Log.e("FileCopy", "No image selected or URI is null")
            return null
        }

        return try {
            val fileName =
                getFileNameFromUri(context, contentUri) ?: "temp_file_${System.currentTimeMillis()}"
            val cacheDir = context.cacheDir

            // Ensure cache directory exists
            if (!cacheDir.exists()) {
                cacheDir.mkdirs()
            }

            val tempFile = File(cacheDir, fileName)

            // Delete existing file with the same name
            if (tempFile.exists()) {
                tempFile.delete()
            }

            // Resolve the URI
            val resolvedUri = if (contentUri.scheme == null) {
                Uri.parse("file://${contentUri.path}")
            } else {
                contentUri
            }

            Log.d(
                "FileDebug",
                "Resolved URI: $resolvedUri, Scheme: ${resolvedUri.scheme}, Path: ${resolvedUri.path}"
            )

            when (resolvedUri.scheme) {
                "file" -> {
                    val file = File(resolvedUri.path ?: return null)
                    if (file.exists()) {
                        file.copyTo(tempFile, overwrite = true)
                        Log.d("FileCopy", "File copied to cache: ${tempFile.absolutePath}")
                    } else {
                        Log.e("FileCopy", "Source file does not exist: ${resolvedUri.path}")
                        return null
                    }
                }

                "content" -> {
                    context.contentResolver.openInputStream(resolvedUri)?.use { inputStream ->
                        FileOutputStream(tempFile).use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
                    Log.d("FileCopy", "Content URI copied to cache: ${tempFile.absolutePath}")
                }

                else -> {
                    Log.e("FileCopy", "Unsupported URI scheme: ${resolvedUri.scheme}")
                    return null
                }
            }
            tempFile
        } catch (e: Exception) {
            Log.e("FileCopy", "Error creating file from URI: ${e.message}")
            e.printStackTrace()
            null
        }
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

    fun getFileNameFromUri(context: Context, uri: Uri): String? {
        return if (uri.scheme == "content") {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (cursor.moveToFirst() && nameIndex >= 0) cursor.getString(nameIndex) else null
            }
        } else {
            uri.path?.let { path ->
                File(path).name
            }
        }
    }

    private fun getFileFromUri(uri: Uri): File? {

        Log.d("FHFFHHFHF", "getFileFromUri: $uri")
        val filePath = getRealPathFromURI(uri)
        Log.d("FHFFHHFHF", "getFileFromUri: $filePath")
        return if (filePath != null) File(filePath) else null
    }

    private fun getRealPathFromURI(uri: Uri): String? {
        Log.d("FHFFHHFHF", "getRealPathFromURI: $uri")

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
                    val tv_start_time = childView.findViewById<TextView>(R.id.tv_start_time)
                    val tv_End_time = childView.findViewById<TextView>(R.id.tv_End_time)

                    val startTime = formatTime(tv_start_time.text.toString().trim())
                    val endTime = formatTime(tv_End_time.text.toString().trim())

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
//        restoreViewsForAllDays()
        clearGroupDataOnBack()

        Log.d("+++++++++++++", "onResume: ")
        restoreLastSelectedDay()
    }

    private fun restoreLastSelectedDay() {
        val sharedPreferences = getSharedPreferences("view_state_prefs", MODE_PRIVATE)
        val lastSelectedDay =
            sharedPreferences.getString("last_selected_day", "monday") // Default to "monday"

        Log.d("RestoreDayyyyyyyyy", "Retrieved last_selected_day: $lastSelectedDay")

        if (!lastSelectedDay.isNullOrEmpty()) {
            showDataForDay(lastSelectedDay)
        } else {
            Log.d("RestoreDay", "No last_selected_day found. Defaulting to Monday.")
//            showDataForDay("monday") // Default if still null
        }
    }


    private fun saveLastSelectedDay(selectedDay: String) { // Default to "monday"
        val sharedPreferences = getSharedPreferences("view_state_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("last_selected_day", selectedDay)
        editor.apply()
        Log.d("SaveDay", "Saved last_selected_day: $selectedDay")
    }

    private fun clearLastSelectedDay() {
        val sharedPreferences = getSharedPreferences("view_state_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("last_selected_day") // Remove the specific key
        editor.apply()
        Log.d("ClearDay", "Cleared last_selected_day from SharedPreferences")
    }


    private fun saveViewStateForAllDays() {
        val sharedPreferences = getSharedPreferences("view_state_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Save state for each day
        editor.putBoolean("monday_view_added", true)
        editor.putBoolean("tuesday_view_added", true)
        editor.putBoolean("wednesday_view_added", true)
        editor.putBoolean("thursday_view_added", true)
        editor.putBoolean("friday_view_added", true)
        editor.putBoolean("saturday_view_added", true)
        editor.putBoolean("sunday_view_added", true)

        editor.apply()
    }


    private fun restoreViewsForDay(container: LinearLayout, day: String) {
        val schedules = loadSchedules(day)
        for (schedule in schedules) {
            val newView =
                LayoutInflater.from(this).inflate(R.layout.time_layout_group, container, false)
            newView.findViewById<AppCompatEditText>(R.id.tv_start_time).setText(schedule.startTime)
            newView.findViewById<AppCompatEditText>(R.id.tv_End_time).setText(schedule.endTime)
            container.addView(newView)
        }
    }

    private fun loadSchedules(day: String): List<ScheduleItem> {
        val sharedPreferences = getSharedPreferences("schedule_prefs", MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString(day, null) ?: return emptyList()
        val type = object : TypeToken<List<ScheduleItem>>() {}.type
        return gson.fromJson(json, type)
    }


    private fun clearAllSchedules() {
        val sharedPreferences = getSharedPreferences("schedule_prefs", MODE_PRIVATE)
        sharedPreferences.edit().clear().apply() // Clears all saved data in SharedPreferences

        Log.d("56565656", "clearAllSchedules: Cler")
    }

    override fun onDestroy() {
        super.onDestroy()
        onBackPressed()
        clearIdsFromPreferences()
        clearGroupDataOnBack()
        clearDayTimesOnBack()
        clearAllSchedules()
        clearLastSelectedDay()
        resetCardBackgroundColors()

    }

    private fun resetCardBackgroundColors() {
        val defaultColor = resources.getColor(R.color.card_background)

        createGroupBinding.weekMon.setCardBackgroundColor(defaultColor)
        createGroupBinding.weekTue.setCardBackgroundColor(defaultColor)
        createGroupBinding.weekWed.setCardBackgroundColor(defaultColor)
        createGroupBinding.weekThu.setCardBackgroundColor(defaultColor)
        createGroupBinding.weekFri.setCardBackgroundColor(defaultColor)
        createGroupBinding.weekSat.setCardBackgroundColor(defaultColor)
        createGroupBinding.weekSun.setCardBackgroundColor(defaultColor)

        Log.d("%%$%$%%$%$%$%%$", "Card backgrounds reset.")
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
            saveViewStateForAllDays()
            saveIdsToPreferences()
//            saveLastSelectedDay()

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
        resetCardBackgroundColors()
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
        val weightInPixels = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, // The unit type (dp)
            330f, // The value in dp
            resources.displayMetrics // The display metrics
        ).toInt()

        val popupWindow = PopupWindow(
            popupView,
//            ViewGroup.LayoutParams.WRAP_CONTENT,
            weightInPixels,
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
                    val typeface = ResourcesCompat.getFont(this@CreateGropActivity, R.font.poppins_medium)
                    view.typeface = typeface
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
//                val tv_start_time = childView.findViewById<TextView>(R.id.tv_start_time)
//                val tv_End_time = childView.findViewById<TextView>(R.id.tv_End_time)
//
//                val startTime = formatTime(tv_start_time.text.toString().trim())
//                val endTime = formatTime(tv_End_time.text.toString().trim())
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

    // Save view state for all days

//
//    private fun restoreViewsForAllDays() {
//        val sharedPreferences = getSharedPreferences("view_state_prefs", MODE_PRIVATE)
//
//        // Hide all layouts to prevent overlap
//        hideAllLayouts()
//
//        // Show only Monday's layout by default
//        if (sharedPreferences.getBoolean("monday_view_added", false)) {
//            mon_linearLayour.visibility = View.VISIBLE
//            restoreViewsForDay(mon_linearLayour, "monday")
//        }
//    }

//    private fun restoreLastSelectedDay() {
//        val sharedPreferences = getSharedPreferences("view_state_prefs", MODE_PRIVATE)
//        val lastSelectedDay = sharedPreferences.getString("last_selected_day", null)
//
//        Log.d("RestoreDay", "Retrieved last_selected_day: $lastSelectedDay")
//
//        if (!lastSelectedDay.isNullOrEmpty()) {
//            showDataForDay(lastSelectedDay)
//        } else {
//            Log.d("RestoreDay", "No last_selected_day found. Defaulting to Monday.")
//            showDataForDay("monday") // Default to Monday if no day is stored
//        }
//    }

    private fun showDataForDay(selectedDay: String) {
        val sharedPreferences = getSharedPreferences("view_state_prefs", MODE_PRIVATE)

        val days =
            listOf("monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday")

        val startIndex = days.indexOf(selectedDay).takeIf { it >= 0 } ?: 0
        val dayToShow = days.drop(startIndex).firstOrNull { day ->
            sharedPreferences.getBoolean("${day}_view_added", false)
        } ?: days.firstOrNull { day ->
            sharedPreferences.getBoolean("${day}_view_added", false)
        } ?: selectedDay

        hideAllLayouts()

        Log.d("ShowDay", "Showing data for day: $dayToShow")

        when (dayToShow) {
            "monday" -> {
                mon_linearLayour.visibility = View.VISIBLE
                restoreViewsForDay(mon_linearLayour, "monday")
                createGroupBinding.monAddScheduleTime.visibility = View.VISIBLE
                resetCardBackgroundsExcept("monday")
            }

            "tuesday" -> {
                tue_linearLayout.visibility = View.VISIBLE
                restoreViewsForDay(tue_linearLayout, "tuesday")
                createGroupBinding.tueAddScheduleTime.visibility = View.VISIBLE
                resetCardBackgroundsExcept("tuesday")
            }

            "wednesday" -> {
                wed_linearLayout.visibility = View.VISIBLE
                restoreViewsForDay(wed_linearLayout, "wednesday")
                createGroupBinding.wedAddScheduleTime.visibility = View.VISIBLE
                resetCardBackgroundsExcept("wednesday")
            }

            "thursday" -> {
                thu_linearLayout.visibility = View.VISIBLE
                restoreViewsForDay(thu_linearLayout, "thursday")
                createGroupBinding.thuAddScheduleTime.visibility = View.VISIBLE
                resetCardBackgroundsExcept("thursday")
            }

            "friday" -> {
                fri_linearLayout.visibility = View.VISIBLE
                restoreViewsForDay(fri_linearLayout, "friday")
                createGroupBinding.friAddScheduleTime.visibility = View.VISIBLE
                resetCardBackgroundsExcept("friday")
            }

            "saturday" -> {
                sat_linearLayout.visibility = View.VISIBLE
                restoreViewsForDay(sat_linearLayout, "saturday")
                createGroupBinding.satAddScheduleTime.visibility = View.VISIBLE
                resetCardBackgroundsExcept("saturday")
            }

            "sunday" -> {
                sun_linearLayout.visibility = View.VISIBLE
                restoreViewsForDay(sun_linearLayout, "sunday")
                createGroupBinding.sunAddScheduleTime.visibility = View.VISIBLE
                resetCardBackgroundsExcept("sunday")
            }
        }

        val editor = sharedPreferences.edit()
        editor.putString("last_selected_day", dayToShow)
        editor.apply()
        Log.d("SaveDay", "Saved last_selected_day: $dayToShow")
    }

    private fun resetCardBackgroundsExcept(selectedDay: String) {
        val defaultColor = resources.getColor(R.color.card_background)
        val splashColor = resources.getColor(R.color.splash_text_color)

        createGroupBinding.monAddScheduleTime.visibility = View.GONE
        createGroupBinding.tueAddScheduleTime.visibility = View.GONE
        createGroupBinding.wedAddScheduleTime.visibility = View.GONE
        createGroupBinding.thuAddScheduleTime.visibility = View.GONE
        createGroupBinding.friAddScheduleTime.visibility = View.GONE
        createGroupBinding.satAddScheduleTime.visibility = View.GONE
        createGroupBinding.sunAddScheduleTime.visibility = View.GONE

        createGroupBinding.weekMon.setCardBackgroundColor(defaultColor)
        createGroupBinding.weekTue.setCardBackgroundColor(defaultColor)
        createGroupBinding.weekWed.setCardBackgroundColor(defaultColor)
        createGroupBinding.weekThu.setCardBackgroundColor(defaultColor)
        createGroupBinding.weekFri.setCardBackgroundColor(defaultColor)
        createGroupBinding.weekSat.setCardBackgroundColor(defaultColor)
        createGroupBinding.weekSun.setCardBackgroundColor(defaultColor)

        when (selectedDay) {
            "monday" -> createGroupBinding.weekMon.setCardBackgroundColor(splashColor)
            "tuesday" -> createGroupBinding.weekTue.setCardBackgroundColor(splashColor)
            "wednesday" -> createGroupBinding.weekWed.setCardBackgroundColor(splashColor)
            "thursday" -> createGroupBinding.weekThu.setCardBackgroundColor(splashColor)
            "friday" -> createGroupBinding.weekFri.setCardBackgroundColor(splashColor)
            "saturday" -> createGroupBinding.weekSat.setCardBackgroundColor(splashColor)
            "sunday" -> createGroupBinding.weekSun.setCardBackgroundColor(splashColor)
        }
    }

    private fun hideAllLayouts() {
        mon_linearLayour.visibility = View.GONE
        tue_linearLayout.visibility = View.GONE
        wed_linearLayout.visibility = View.GONE
        thu_linearLayout.visibility = View.GONE
        fri_linearLayout.visibility = View.GONE
        sat_linearLayout.visibility = View.GONE
        sun_linearLayout.visibility = View.GONE
    }

    fun mon_addView() {
        addViewForDay(mon_linearLayour, "monday")
//        saveViewStateForAllDays()
        saveLastSelectedDay("monday")
    }

    fun tue_addView() {
        addViewForDay(tue_linearLayout, "tuesday")
//        saveViewStateForAllDays()
        saveLastSelectedDay("tuesday")

    }

    fun wed_addView() {
        addViewForDay(wed_linearLayout, "wednesday")
//        saveViewStateForAllDays()
        saveLastSelectedDay("wednesday")

    }

    fun thu_addView() {
        addViewForDay(thu_linearLayout, "thursday")
//        saveViewStateForAllDays()
        saveLastSelectedDay("thursday")

    }

    fun fri_addView() {
        addViewForDay(fri_linearLayout, "friday")
//        saveViewStateForAllDays()
        saveLastSelectedDay("friday")

    }

    fun sat_addView() {
        addViewForDay(sat_linearLayout, "saturday")
//        saveViewStateForAllDays()
        saveLastSelectedDay("saturday")

    }

    fun sun_addView() {
        addViewForDay(sun_linearLayout, "sunday")
//        saveViewStateForAllDays()
        saveLastSelectedDay("sunday")

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

    private fun validateAllFields(linearLayout: LinearLayout): Boolean {
        for (i in 0 until linearLayout.childCount) {
            val childView = linearLayout.getChildAt(i)
            val startTime = childView.findViewById<TextView>(R.id.tv_start_time).text.toString()
            val endTime = childView.findViewById<TextView>(R.id.tv_End_time).text.toString()

            if (startTime.isBlank() || endTime.isBlank()) {
                return false // Validation failed
            }
        }
        return true // All fields are valid
    }


    // Save data for all days
    private fun addViewForDay(linearLayout: LinearLayout, dayKey: String) {
        val inflater = LayoutInflater.from(this).inflate(R.layout.time_layout_group, null)
        linearLayout.addView(inflater, linearLayout.childCount)

        tv_start_time = inflater.findViewById(R.id.tv_start_time)
        tv_End_time = inflater.findViewById(R.id.tv_End_time)
        val tv_start_timeCard: CardView = inflater.findViewById(R.id.start_time_card)
        val tv_End_timeCard: CardView = inflater.findViewById(R.id.card_end_time)
        val delete: ImageView = inflater.findViewById(R.id.img_delete)

        val id = linearLayout.childCount.toString()
        updateDayTimes(dayKey, id, "", "")

        saveDayTimesToPreferences()

        tv_start_time.setOnClickListener {
            Log.d("CLICK", "Start time clicked")
            SetDialog_start(tv_start_time)
        }

        tv_End_time.setOnClickListener {
            Log.d("CLICK", "End time clicked")
            setDialogEnd(tv_End_time)
        }

        tv_start_timeCard.setOnClickListener {
            SetDialog_start(tv_start_time)
        }
        tv_End_timeCard.setOnClickListener {
            setDialogEnd(tv_End_time)
        }

        tv_start_time.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                updateDayTimes(dayKey, id, s.toString(), tv_End_time.text.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        tv_End_time.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                updateDayTimes(dayKey, id, tv_start_time.text.toString(), s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        Log.d("TYTYTYT", "addViewForDay: $firstview")

        delete.setOnClickListener {
            val currentCount = linearLayout.childCount

            if (currentCount > 1) {
                // Remove the specified child view
                val indexToRemove = linearLayout.indexOfChild(inflater)
                linearLayout.removeView(inflater)
            } else if (currentCount == 1) {
                // If only one child is present, clear its data
                val remainingView = linearLayout.getChildAt(0)
                val remainingStartTime: TextView = remainingView.findViewById(R.id.tv_start_time)
                val remainingEndTime: TextView = remainingView.findViewById(R.id.tv_End_time)

                remainingStartTime.text = ""
                remainingEndTime.text = ""
            }

            val updatedCount = linearLayout.childCount
            Log.d("LINEAR_LAYOUT", "Updated child count: $updatedCount")

            // Update the state of the delete button
            if (updatedCount == 0) {
                delete.isEnabled = false
                delete.alpha = 0.5f
            } else {
                delete.isEnabled = true
                delete.alpha = 1.0f
            }
        }




//        if (firstview == true){
//            delete.setOnClickListener {
//                tv_start_time.setText("")
//                tv_End_time.setText("")
//            }
//        }else{
//            delete.setOnClickListener {
//                linearLayout.removeView(inflater)
//            }
//        }

    }

    private fun updateDayTimes(dayKey: String, id: String, startTime: String, endTime: String) {
        val dayTime = DayTime(id, startTime, endTime)
        val times = dayTimes[dayKey]?.toMutableList() ?: mutableListOf()
        val index = times.indexOfFirst { it.id == id }

        if (index >= 0) {
            // Update existing dayTime
            times[index] = dayTime
        } else {
            // Add new dayTime
            times.add(dayTime)
        }

        // Update the dayTimes map
        dayTimes[dayKey] = times
        Log.e("BKKKKKKAKAKAKAK", "updateDayTimes: " + dayTimes[dayKey])

        // Call save method to persist the entire dayTimes map
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

        // Convert the entire dayTimes map to a JSON string
        val jsonString = gson.toJson(dayTimes)

        // Save the entire map to shared preferences
        editor.putString("day_times", jsonString)
        editor.apply()  // Commit the changes
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
        Log.e("KKTESTING", "loadDayTimesFromPreferences: "+dayTimes )
    }

    fun saveDayTimesToPreferences(dayTimes: Map<String, List<DayTime>>) {
        val sharedPreferences = getSharedPreferences("DayTimesPrefs", MODE_PRIVATE)
        val gson = Gson()
        val jsonString = gson.toJson(dayTimes)  // Serialize the map

        sharedPreferences.edit().putString("day_times", jsonString).apply()  // Save serialized data
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