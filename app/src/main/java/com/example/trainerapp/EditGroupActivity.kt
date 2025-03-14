    package com.example.trainerapp

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
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
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children
import androidx.core.view.size
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.Adapter.EditGroup.SetEditAthleteAdapter
import com.example.Adapter.EditGroup.SetEditEventAdapter
import com.example.Adapter.EditGroup.SetEditGroupLessonAdapter
import com.example.Adapter.EditGroup.SetEditGroupPlanningAdapter
import com.example.Adapter.groups.SetAthleteInGroup
import com.example.Adapter.groups.SetEditGroupTestAdapter
import com.example.Adapter.groups.SetEventInGroup
import com.example.Adapter.groups.SetLessonInGroup
import com.example.Adapter.groups.SetPlanningGroup
import com.example.Adapter.groups.SetTestInGroup
import com.example.CreateGropActivity
import com.example.GroupAdapter
import com.example.GroupDetailActivity
import com.example.GroupListData
import com.example.LessonListActivity
import com.example.OnItemClickListener
import com.example.model.DayTime
import com.example.model.SelectedValue
import com.example.model.newClass.athlete.AthleteData
import com.example.model.newClass.lesson.Lesson
import com.example.model.training_plan.TrainingPlanData
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.EventListData
import com.example.trainerapp.ApiClass.SportlistData
import com.example.trainerapp.databinding.ActivityCreateGropBinding
import com.example.trainerapp.databinding.ActivityEditGroupBinding
import com.example.trainerapp.databinding.ActivityLessonListBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.makeramen.roundedimageview.RoundedTransformationBuilder
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.log

class EditGroupActivity : AppCompatActivity(), OnItemClickListener.OnItemClickCallback {

    private lateinit var binding: ActivityEditGroupBinding
    val selectedDays = mutableListOf<String>()
    private var selectedImageUri: Uri? = null
    private var selectedImageUri2: Uri? = null
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    lateinit var preferenceManager: PreferencesManager
    lateinit var groupadapter: GroupAdapter
    private var receivedId: Int = 0
    private var groupList: ArrayList<GroupListData.groupData> = ArrayList()
    lateinit var goalData: MutableList<SportlistData.sportlist>
    var Goal = ArrayList<String>()
    private var firstTimeId: Int = -1
    var datecheckstart:Boolean = false
    var datecheckend:Boolean = false


    private val PREF_NAME = "MyPreferences"
    val KEY_SELECTED_IMAGE_URI = "selectedImageUri"

    private lateinit var lessonAdapteradd: SetLessonInGroup
    private lateinit var eventAdapteradd: SetEventInGroup
    private lateinit var testAdapteradd: SetTestInGroup
    lateinit var adapterPlanningadd: SetPlanningGroup
    lateinit var adapterAthleteadd: SetAthleteInGroup


    private lateinit var lessonData: ArrayList<Lesson.LessonDatabase>
    private lateinit var TestData: ArrayList<TestListData.testData>
    private lateinit var EventData: ArrayList<EventListData.testData>
    lateinit var plainngData: MutableList<TrainingPlanData.TrainingPlan>
    lateinit var athleteData: MutableList<AthleteData.Athlete>

    private lateinit var lessonAdapter: SetEditGroupLessonAdapter
    private lateinit var eventAdapter: SetEditEventAdapter
    private lateinit var testAdapter: SetEditGroupTestAdapter
    lateinit var adapterPlanning: SetEditGroupPlanningAdapter
    lateinit var adapterAthlete: SetEditAthleteAdapter

    private var shouldSaveData = true

    lateinit var sharedPreferences: SharedPreferences


    // ids
    var sportlId = SelectedValue(null)
    var lessonId: IntArray = intArrayOf()
    var testId: IntArray = intArrayOf()
    var eventId: IntArray = intArrayOf()
    var planningId: IntArray = intArrayOf()
    var pid: IntArray = intArrayOf()
    var athleteId: IntArray = intArrayOf()
    var programId: IntArray = intArrayOf()
    var SportsId: IntArray = intArrayOf()

    //
    lateinit var inflater: View
    lateinit var mon_linearLayour: LinearLayout
    lateinit var tue_linearLayout: LinearLayout
    lateinit var wed_linearLayout: LinearLayout
    lateinit var thu_linearLayout: LinearLayout
    lateinit var fri_linearLayout: LinearLayout
    lateinit var sat_linearLayout: LinearLayout
    lateinit var sun_linearLayout: LinearLayout


    lateinit var tv_start_time: TextView
    lateinit var tv_End_time: TextView
    lateinit var start_error: TextView
    lateinit var end_error: TextView

    var uriString: String = ""

    var hasChanges = false

    // Declare the filteredGroupList as a member variable
    private var filteredGroupList: List<GroupListData.groupData> = emptyList()


    //
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
        binding = ActivityEditGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val inflater = LayoutInflater.from(this).inflate(R.layout.time_layout_group, null)
        tv_start_time = inflater.findViewById(R.id.tv_start_time)
        tv_End_time = inflater.findViewById(R.id.tv_End_time)


        start_error = inflater.findViewById(R.id.start_error)
        end_error = inflater.findViewById(R.id.end_error)


        loadIdsFromPreferences()
        getFirstTimeIdFromSharedPreferences(this)

        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        uriString = sharedPreferences.getString(KEY_SELECTED_IMAGE_URI, null).toString()


        initView()
        getSportData()
        ButtonClick()
        callGroupApi(receivedId.toString())
        setSavedDayTimes()
        setupTextWatcher()

    }

    private fun areAllFieldsFilled(): Boolean {
        return !(binding.edtName.text.isNullOrEmpty() || binding.edtSport.text.isNullOrEmpty())
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

    private fun setupTextWatcher() {
        binding.edtName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun afterTextChanged(s: Editable?) {
                updateUI(binding.nextButtonText)

            }

        })
        binding.edtSport.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            @RequiresApi(Build.VERSION_CODES.O)
            override fun afterTextChanged(s: Editable?) {
                updateUI(binding.nextButtonText)
            }
        })

    }

    private fun ButtonClick() {

        binding.back.setOnClickListener {

            shouldSaveData = false
            clearIdsFromPreferences()
            clearAllPreferences()

            Log.d("FGGFGFGFG", "ButtonClick: $receivedId")
            clearIdFromSharedPreferences(this)
            val intent = Intent(this@EditGroupActivity, HomeActivity::class.java)
            intent.putExtra("group", "addGroup")
            startActivity(intent)
            finish()

        }

        binding.lySport.setOnClickListener {
            showPopup(it, goalData, binding.edtSport, Goal, sportlId)
        }

        binding.edtSport.setOnClickListener {
            showPopup(it, goalData, binding.edtSport, Goal, sportlId)

            val groupName = binding.edtName.text.toString()
            val sportName = binding.edtSport.text.toString()
            val imageUri = binding.imageUpload.tag as? Uri

            saveGroupData(groupName, imageUri, sportName, sportlId.id.toString())

        }

        binding.monAddScheduleTime.setOnClickListener {
//            if (isValidate){
//                start_error.visibility = View.GONE
//                end_error.visibility = View.GONE
//                mon_addView()
//            }
            val linearLayout = binding.MonLinearLayout

            val isValid = validateAllFields(linearLayout)
            if (!isValid) {
                start_error.visibility = View.VISIBLE
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            } else {
                mon_addView()
            }


        }
        binding.tueAddScheduleTime.setOnClickListener {
//            if (isValidate){
//                start_error.visibility = View.GONE
//                end_error.visibility = View.GONE
//                tue_addView()
//            }

            val linearLayout = binding.TueLinearLayout

            val isValid = validateAllFields(linearLayout)
            if (!isValid) {
                start_error.visibility = View.VISIBLE
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            } else {
                tue_addView()
            }

//            tue_addView()
        }
        binding.wedAddScheduleTime.setOnClickListener {
//            if (isValidate){
//                start_error.visibility = View.GONE
//                end_error.visibility = View.GONE
//                wed_addView()
//            }
            val linearLayout = binding.WedLinearLayout

            val isValid = validateAllFields(linearLayout)
            if (!isValid) {
                start_error.visibility = View.VISIBLE
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            } else {
                wed_addView()
            }

//            wed_addView()

        }
        binding.thuAddScheduleTime.setOnClickListener {
//            if (isValidate){
//                binding..visibility = View.GONE
//                binding.end_error.visibility = View.GONE
//
//
//            }
            val linearLayout = binding.ThuLinearLayout

            val isValid = validateAllFields(linearLayout)
            if (!isValid) {
                start_error.visibility = View.VISIBLE
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            } else {
                thu_addView()
            }
        }
        binding.friAddScheduleTime.setOnClickListener {
//            if (isValidate){
//                start_error.visibility = View.GONE
//                end_error.visibility = View.GONE
//
//            }
            val linearLayout = binding.FriLinearLayout

            val isValid = validateAllFields(linearLayout)
            if (!isValid) {
                start_error.visibility = View.VISIBLE
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            } else {
                fri_addView()
            }

        }
        binding.satAddScheduleTime.setOnClickListener {
//            if (isValidate){
//                start_error.visibility = View.GONE
//                end_error.visibility = View.GONE
//
//            }
            val linearLayout = binding.SatLinearLayout

            val isValid = validateAllFields(linearLayout)
            if (!isValid) {
                start_error.visibility = View.VISIBLE
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            } else {
                sat_addView()
            }

        }
        binding.sunAddScheduleTime.setOnClickListener {
//            if (isValidate){
//                start_error.visibility = View.GONE
//                end_error.visibility = View.GONE
//
//            }
            val linearLayout = binding.SunLinearLayout

            val isValid = validateAllFields(linearLayout)
            if (!isValid) {
                start_error.visibility = View.VISIBLE
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            } else {
                sun_addView()
            }
        }


        binding.weekMon.setOnClickListener {

            toggleDay(
                "monday",
                binding.weekMon,
                mon_linearLayour,
                binding.monAddScheduleTime
            )


            mon_linearLayour.visibility = View.VISIBLE
            tue_linearLayout.visibility = View.GONE
            wed_linearLayout.visibility = View.GONE
            thu_linearLayout.visibility = View.GONE
            fri_linearLayout.visibility = View.GONE
            sat_linearLayout.visibility = View.GONE
            sun_linearLayout.visibility = View.GONE
            binding.monAddScheduleTime.visibility = View.VISIBLE
            binding.tueAddScheduleTime.visibility = View.GONE
            binding.wedAddScheduleTime.visibility = View.GONE
            binding.thuAddScheduleTime.visibility = View.GONE
            binding.friAddScheduleTime.visibility = View.GONE
            binding.satAddScheduleTime.visibility = View.GONE
            binding.sunAddScheduleTime.visibility = View.GONE
            binding.weekMon.setCardBackgroundColor(resources.getColor(R.color.splash_text_color))
            binding.weekTue.setCardBackgroundColor(resources.getColor(R.color.card_background))
            binding.weekWed.setCardBackgroundColor(resources.getColor(R.color.card_background))
            binding.weekThu.setCardBackgroundColor(resources.getColor(R.color.card_background))
            binding.weekFri.setCardBackgroundColor(resources.getColor(R.color.card_background))
            binding.weekSat.setCardBackgroundColor(resources.getColor(R.color.card_background))
            binding.weekSun.setCardBackgroundColor(resources.getColor(R.color.card_background))
        }
        binding.weekTue.setOnClickListener {

            toggleDay(
                "tuesday",
                binding.weekTue,
                tue_linearLayout,
                binding.tueAddScheduleTime
            )

            mon_linearLayour.visibility = View.GONE
            tue_linearLayout.visibility = View.VISIBLE
            wed_linearLayout.visibility = View.GONE
            thu_linearLayout.visibility = View.GONE
            fri_linearLayout.visibility = View.GONE
            sat_linearLayout.visibility = View.GONE
            sun_linearLayout.visibility = View.GONE
            binding.monAddScheduleTime.visibility = View.GONE
            binding.tueAddScheduleTime.visibility = View.VISIBLE
            binding.wedAddScheduleTime.visibility = View.GONE
            binding.thuAddScheduleTime.visibility = View.GONE
            binding.friAddScheduleTime.visibility = View.GONE
            binding.satAddScheduleTime.visibility = View.GONE
            binding.sunAddScheduleTime.visibility = View.GONE
            binding.weekMon.setCardBackgroundColor(resources.getColor(R.color.card_background))
            binding.weekTue.setCardBackgroundColor(resources.getColor(R.color.splash_text_color))
            binding.weekWed.setCardBackgroundColor(resources.getColor(R.color.card_background))
            binding.weekThu.setCardBackgroundColor(resources.getColor(R.color.card_background))
            binding.weekFri.setCardBackgroundColor(resources.getColor(R.color.card_background))
            binding.weekSat.setCardBackgroundColor(resources.getColor(R.color.card_background))
            binding.weekSun.setCardBackgroundColor(resources.getColor(R.color.card_background))
        }
        binding.weekWed.setOnClickListener {
            toggleDay(
                "wednesday",
                binding.weekWed,
                wed_linearLayout,
                binding.wedAddScheduleTime
            )

            mon_linearLayour.visibility = View.GONE
            tue_linearLayout.visibility = View.GONE
            wed_linearLayout.visibility = View.VISIBLE
            thu_linearLayout.visibility = View.GONE
            fri_linearLayout.visibility = View.GONE
            sat_linearLayout.visibility = View.GONE
            sun_linearLayout.visibility = View.GONE
            binding.monAddScheduleTime.visibility = View.GONE
            binding.tueAddScheduleTime.visibility = View.GONE
            binding.wedAddScheduleTime.visibility = View.VISIBLE
            binding.thuAddScheduleTime.visibility = View.GONE
            binding.friAddScheduleTime.visibility = View.GONE
            binding.satAddScheduleTime.visibility = View.GONE
            binding.sunAddScheduleTime.visibility = View.GONE
            binding.weekMon.setCardBackgroundColor(resources.getColor(R.color.card_background))
            binding.weekTue.setCardBackgroundColor(resources.getColor(R.color.card_background))
            binding.weekWed.setCardBackgroundColor(resources.getColor(R.color.splash_text_color))
            binding.weekThu.setCardBackgroundColor(resources.getColor(R.color.card_background))
            binding.weekFri.setCardBackgroundColor(resources.getColor(R.color.card_background))
            binding.weekSat.setCardBackgroundColor(resources.getColor(R.color.card_background))
            binding.weekSun.setCardBackgroundColor(resources.getColor(R.color.card_background))
        }
        binding.weekThu.setOnClickListener {

            toggleDay(
                "thursday",
                binding.weekThu,
                thu_linearLayout,
                binding.thuAddScheduleTime
            )


            mon_linearLayour.visibility = View.GONE
            tue_linearLayout.visibility = View.GONE
            wed_linearLayout.visibility = View.GONE
            thu_linearLayout.visibility = View.VISIBLE
            fri_linearLayout.visibility = View.GONE
            sat_linearLayout.visibility = View.GONE
            sun_linearLayout.visibility = View.GONE
            binding.monAddScheduleTime.visibility = View.GONE
            binding.tueAddScheduleTime.visibility = View.GONE
            binding.wedAddScheduleTime.visibility = View.GONE
            binding.thuAddScheduleTime.visibility = View.VISIBLE
            binding.friAddScheduleTime.visibility = View.GONE
            binding.satAddScheduleTime.visibility = View.GONE
            binding.sunAddScheduleTime.visibility = View.GONE
            binding.weekMon.setCardBackgroundColor(resources.getColor(R.color.card_background))
            binding.weekTue.setCardBackgroundColor(resources.getColor(R.color.card_background))
            binding.weekWed.setCardBackgroundColor(resources.getColor(R.color.card_background))
            binding.weekThu.setCardBackgroundColor(resources.getColor(R.color.splash_text_color))
            binding.weekFri.setCardBackgroundColor(resources.getColor(R.color.card_background))
            binding.weekSat.setCardBackgroundColor(resources.getColor(R.color.card_background))
            binding.weekSun.setCardBackgroundColor(resources.getColor(R.color.card_background))
        }
        binding.weekFri.setOnClickListener {
            toggleDay(
                "friday",
                binding.weekFri,
                fri_linearLayout,
                binding.friAddScheduleTime
            )

            mon_linearLayour.visibility = View.GONE
            tue_linearLayout.visibility = View.GONE
            wed_linearLayout.visibility = View.GONE
            thu_linearLayout.visibility = View.GONE
            fri_linearLayout.visibility = View.VISIBLE
            sat_linearLayout.visibility = View.GONE
            sun_linearLayout.visibility = View.GONE
            binding.monAddScheduleTime.visibility = View.GONE
            binding.tueAddScheduleTime.visibility = View.GONE
            binding.wedAddScheduleTime.visibility = View.GONE
            binding.thuAddScheduleTime.visibility = View.GONE
            binding.friAddScheduleTime.visibility = View.VISIBLE
            binding.satAddScheduleTime.visibility = View.GONE
            binding.sunAddScheduleTime.visibility = View.GONE
            binding.weekMon.setCardBackgroundColor(resources.getColor(R.color.card_background))
            binding.weekTue.setCardBackgroundColor(resources.getColor(R.color.card_background))
            binding.weekWed.setCardBackgroundColor(resources.getColor(R.color.card_background))
            binding.weekThu.setCardBackgroundColor(resources.getColor(R.color.card_background))
            binding.weekFri.setCardBackgroundColor(resources.getColor(R.color.splash_text_color))
            binding.weekSat.setCardBackgroundColor(resources.getColor(R.color.card_background))
            binding.weekSun.setCardBackgroundColor(resources.getColor(R.color.card_background))
        }
        binding.weekSat.setOnClickListener {
            toggleDay(
                "saturday",
                binding.weekSat,
                sat_linearLayout,
                binding.satAddScheduleTime
            )
            mon_linearLayour.visibility = View.GONE
            tue_linearLayout.visibility = View.GONE
            wed_linearLayout.visibility = View.GONE
            thu_linearLayout.visibility = View.GONE
            fri_linearLayout.visibility = View.GONE
            sat_linearLayout.visibility = View.VISIBLE
            sun_linearLayout.visibility = View.GONE
            binding.monAddScheduleTime.visibility = View.GONE
            binding.tueAddScheduleTime.visibility = View.GONE
            binding.wedAddScheduleTime.visibility = View.GONE
            binding.thuAddScheduleTime.visibility = View.GONE
            binding.friAddScheduleTime.visibility = View.GONE
            binding.satAddScheduleTime.visibility = View.VISIBLE
            binding.sunAddScheduleTime.visibility = View.GONE
            binding.weekMon.setCardBackgroundColor(resources.getColor(R.color.card_background))
            binding.weekTue.setCardBackgroundColor(resources.getColor(R.color.card_background))
            binding.weekWed.setCardBackgroundColor(resources.getColor(R.color.card_background))
            binding.weekThu.setCardBackgroundColor(resources.getColor(R.color.card_background))
            binding.weekFri.setCardBackgroundColor(resources.getColor(R.color.card_background))
            binding.weekSat.setCardBackgroundColor(resources.getColor(R.color.splash_text_color))
            binding.weekSun.setCardBackgroundColor(resources.getColor(R.color.card_background))
        }
        binding.weekSun.setOnClickListener {

            toggleDay(
                "sunday",
                binding.weekSun,
                sun_linearLayout,
                binding.sunAddScheduleTime
            )

            mon_linearLayour.visibility = View.GONE
            tue_linearLayout.visibility = View.GONE
            wed_linearLayout.visibility = View.GONE
            thu_linearLayout.visibility = View.GONE
            fri_linearLayout.visibility = View.GONE
            sat_linearLayout.visibility = View.GONE
            sun_linearLayout.visibility = View.VISIBLE
            binding.monAddScheduleTime.visibility = View.GONE
            binding.tueAddScheduleTime.visibility = View.GONE
            binding.wedAddScheduleTime.visibility = View.GONE
            binding.thuAddScheduleTime.visibility = View.GONE
            binding.friAddScheduleTime.visibility = View.GONE
            binding.satAddScheduleTime.visibility = View.GONE
            binding.sunAddScheduleTime.visibility = View.VISIBLE
            binding.weekMon.setCardBackgroundColor(resources.getColor(R.color.card_background))
            binding.weekTue.setCardBackgroundColor(resources.getColor(R.color.card_background))
            binding.weekWed.setCardBackgroundColor(resources.getColor(R.color.card_background))
            binding.weekThu.setCardBackgroundColor(resources.getColor(R.color.card_background))
            binding.weekFri.setCardBackgroundColor(resources.getColor(R.color.card_background))
            binding.weekSat.setCardBackgroundColor(resources.getColor(R.color.card_background))
            binding.weekSun.setCardBackgroundColor(resources.getColor(R.color.splash_text_color))
        }

        binding.lyPlanning.setOnClickListener {
            val intent = Intent(this, LessonListActivity::class.java)
            intent.putExtra("Edit", "planning")
            startActivity(intent)
        }

        binding.btnAddLesson.setOnClickListener {
            val intent = Intent(this, LessonListActivity::class.java)
            intent.putExtra("Edit", "lesson")
            startActivity(intent)
        }

        binding.btnAddTest.setOnClickListener {
            val intent = Intent(this, LessonListActivity::class.java)
            intent.putExtra("Edit", "test")
            startActivity(intent)
        }

        binding.btnAddEvent.setOnClickListener {
            val intent = Intent(this, LessonListActivity::class.java)
            intent.putExtra("Edit", "event")
            startActivity(intent)
        }

        binding.btnAddAthlete.setOnClickListener {
            val intent = Intent(this, LessonListActivity::class.java)
            intent.putExtra("Edit", "athlete")
            startActivity(intent)
        }

        binding.scanQr.setOnClickListener {

            binding.main.setBackgroundColor(resources.getColor(R.color.grey))

            binding.rlyPlanning.visibility = View.GONE
            binding.rlyLesson.visibility = View.GONE
            binding.rlyTest.visibility = View.GONE
            binding.rlyEvent.visibility = View.GONE
            binding.rlyAthlete.visibility = View.GONE

            binding.MonLinearLayout.visibility = View.GONE
            binding.TueLinearLayout.visibility = View.GONE
            binding.WedLinearLayout.visibility = View.GONE
            binding.ThuLinearLayout.visibility = View.GONE
            binding.FriLinearLayout.visibility = View.GONE
            binding.SatLinearLayout.visibility = View.GONE
            binding.SunLinearLayout.visibility = View.GONE

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
                binding.main.setBackgroundColor(resources.getColor(R.color.black))
                binding.MonLinearLayout.visibility = View.VISIBLE
                binding.TueLinearLayout.visibility = View.VISIBLE
                binding.WedLinearLayout.visibility = View.VISIBLE
                binding.ThuLinearLayout.visibility = View.VISIBLE
                binding.FriLinearLayout.visibility = View.VISIBLE
                binding.SatLinearLayout.visibility = View.VISIBLE
                binding.SunLinearLayout.visibility = View.VISIBLE


                binding.main.setBackgroundColor(resources.getColor(R.color.black))

                binding.rlyPlanning.visibility = View.VISIBLE
                binding.rlyLesson.visibility = View.VISIBLE
                binding.rlyTest.visibility = View.VISIBLE
                binding.rlyEvent.visibility = View.VISIBLE
                binding.rlyAthlete.visibility = View.VISIBLE

                dialog.dismiss() }

            dialog.show()
        }

        binding.selectImage.setOnClickListener {
//            val intent = Intent()
//            intent.type = "image/*"
//            intent.action = Intent.ACTION_GET_CONTENT
//            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1)

            getContent.launch("image/*")

        }

        binding.nextButtonText.setOnClickListener {



            val start = tv_start_time.text.toString()
            val end = tv_End_time.text.toString()

            Log.d("GHHGHGHGHGHGH", "onCreate: $start    $end")
            Log.d("GHHGHGHGHGHGH", "onCreate: ${start_error.text.toString()}    $end")

            Log.d("FGFGGFGFGFGG", "ButtonClick: $datecheckstart    $datecheckend")

            if (datecheckstart == false) {
                start_error.visibility = View.VISIBLE
                Toast.makeText(this, "Start field is required", Toast.LENGTH_SHORT)
                    .show() // Toast for start field
                return@setOnClickListener
            }

            if (datecheckend == false) {
                end_error.visibility = View.VISIBLE
                Toast.makeText(this, "End field is required", Toast.LENGTH_SHORT)
                    .show() // Toast for end field
                return@setOnClickListener
            }

            if (selectedImageUri != null) {
                selectedImageUri
            }

            val selectedImageUri: Uri? = selectedImageUri
            val selectedImageUri2: Uri = Uri.parse(uriString)

            if (selectedImageUri != null) {
                editGroupWithImageApiCall(this, selectedImageUri)
            } else {
                if (selectedImageUri2 != null) {
                    editGroupWithImageApiCall(this, selectedImageUri2)
                }
                Log.d("NULL", "ButtonClick: Null:- $selectedImageUri")
            }
        }
    }

//    private val getContent =
//        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
//            uri?.let {
//                selectedImageUri = it
//                binding.selectUploadLy.visibility = View.GONE
//                binding.imageUpload.visibility = View.VISIBLE
//                binding.imageUpload.setImageURI(uri) // Display the selected image
//            }
//        }

    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedImageUri = it
                Log.d("SUJALLLLLL", "Selected Image URI: $it")
                Log.d("SUJALLLLLL", "Selected Image URI: $selectedImageUri")
                binding.selectUploadLy.visibility = View.GONE
                binding.imageUpload.visibility = View.VISIBLE

                val sharedPreferences =
                    binding.root.context.getSharedPreferences("appPrefs", Context.MODE_PRIVATE)
                sharedPreferences.edit().putString("imageUrll", uri.toString()).apply()

                Picasso.get()
                    .load(uri)
                    .error(R.drawable.group_chate_boarder)
                    .into(binding.imageUpload, object : com.squareup.picasso.Callback {
                        override fun onSuccess() {
                            Log.d("Picasso", "Image loaded successfully")

                            val imagePart = processImage(binding.root.context, uri)

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


    fun saveImageUriToPreferences(uri: Uri) {
        val sharedPreferences =
            applicationContext.getSharedPreferences("appPrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("imageUrll", uri.toString())
            apply()
        }
    }

    private fun initView() {
        apiClient = APIClient(this)
        preferenceManager = PreferencesManager(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)

        binding.rlyLesson.visibility = View.VISIBLE
        binding.rlyPlanning.visibility = View.VISIBLE
        binding.rlyTest.visibility = View.VISIBLE
        binding.rlyEvent.visibility = View.VISIBLE
        binding.rlyAthlete.visibility = View.VISIBLE

        mon_linearLayour = findViewById(R.id.Mon_LinearLayout)
        tue_linearLayout = findViewById(R.id.Tue_LinearLayout)
        wed_linearLayout = findViewById(R.id.Wed_LinearLayout)
        thu_linearLayout = findViewById(R.id.Thu_LinearLayout)
        fri_linearLayout = findViewById(R.id.Fri_LinearLayout)
        sat_linearLayout = findViewById(R.id.Sat_LinearLayout)
        sun_linearLayout = findViewById(R.id.Sun_LinearLayout)


        lessonData = ArrayList()
        TestData = ArrayList()
        EventData = ArrayList()
        goalData = mutableListOf()
        plainngData = mutableListOf()
        athleteData = mutableListOf()

        receivedId = intent.getIntExtra("id", 0)
        Log.d("IOIOOIOOIO", "initView: $receivedId")

        firstTimeId = getFirstTimeIdFromSharedPreferences(this)


        if (firstTimeId == -1) {
            firstTimeId = receivedId
            saveIdToSharedPreferences(this, firstTimeId)
        }
        Log.d("FIRSTTIMEID", "initView: $firstTimeId")



        lessonId = intent.getIntArrayExtra("lessonId") ?: intArrayOf()
        Log.d("IDDDDDDDD", "lesson: ${lessonId.joinToString()}")

        testId = intent.getIntArrayExtra("testId") ?: intArrayOf()
        Log.d("IDDDDDDDD", "test: ${testId.joinToString()}")

        eventId = intent.getIntArrayExtra("eventId") ?: intArrayOf()
        Log.d("IDDDDDDDD", "event: ${eventId.joinToString()}")

//        pid = intent.getIntArrayExtra("planningIds") ?: intArrayOf()
//        Log.d("UIUIIUIUI", "initView: ${pid!!.joinToString ()}")
//
        val pid: IntArray = intent.getIntArrayExtra("planningIds") ?: intArrayOf()
        Log.d("UIUIIUIUI", "Received planningIds: ${pid.joinToString()}")

        planningId = intent.getIntArrayExtra("planningId") ?: intArrayOf()
        Log.d("GHGHGHHG", "planning: ${planningId.joinToString()}")


        athleteId = intent.getIntArrayExtra("athleteId") ?: intArrayOf()
        Log.d("IDDDDDDDD", "athlete: ${athleteId.joinToString()}")

        mergeIdsAndSave()

        if (lessonId != null) {
            binding.rlyLesson.visibility = View.VISIBLE
            GetLessonList(listOf(lessonId.map { it.toString() }))
        } else {
            binding.rlyLesson.visibility = View.VISIBLE
        }

        if (testId != null) {
            binding.rlyTest.visibility = View.VISIBLE
            GetTestList(listOf(testId.map { it.toString() }))
        } else {
            binding.rlyLesson.visibility = View.VISIBLE
        }

        if (eventId != null) {
            binding.rlyEvent.visibility = View.VISIBLE
            geteventlist(listOf(eventId.map { it.toString() }))
        } else {
            binding.rlyEvent.visibility = View.VISIBLE
        }

        if (planningId != null) {
            binding.rlyPlanning.visibility = View.VISIBLE
            getTrainingData(listOf(planningId.map { it.toString() }))
        } else {
            binding.rlyPlanning.visibility = View.VISIBLE
        }

        if (pid != null) {
            binding.rlyPlanning.visibility = View.VISIBLE
            getTrainingData(listOf(pid.map { it.toString() }))
        } else {
            binding.rlyPlanning.visibility = View.VISIBLE
        }

        if (athleteId != null) {
            binding.rlyAthlete.visibility = View.VISIBLE
            getAthleteData(listOf(athleteId.map { it.toString() }))
        } else {
            binding.rlyAthlete.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        clearAllPreferences()
        clearIdsFromPreferences()
        clearIdFromSharedPreferences(this)
    }

    override fun onPause() {
        super.onPause()

        Log.d("FGFGFG", "onPause: $shouldSaveData")

        if (shouldSaveData == true) {
            val groupName = binding.edtName.text.toString()
            val sportName = binding.edtSport.text.toString()
            val imageUri = selectedImageUri ?: selectedImageUri2
            saveGroupData(groupName, imageUri, sportName, sportlId.id.toString())
            saveDayTimesToPreferences()
            saveIdsToPreferences()
            mergeIdsAndSave()


            Log.d("GroupData", "Data saved in onPause")
        } else {
            Log.d("GroupData", "Data not saved in onPause because shouldSaveData is false")
        }
    }

    override fun onResume() {
        super.onResume()
        loadDayTimesFromPreferences()
        loadGroupData()
        firstTimeId = getFirstTimeIdFromSharedPreferences(this)
        receivedId = firstTimeId
        Log.d("UserId", "Retrieved first time ID from SharedPreferences: $firstTimeId")
    }


    fun saveIdToSharedPreferences(context: Context, id: Int) {
        val sharedPref = context.getSharedPreferences("MySharedPrefs", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putInt("first_time_id", id)
        editor.apply()

        Log.d("SAVEEEVEVEVV", "First time ID saved to SharedPreferences: $id")
    }

    fun getFirstTimeIdFromSharedPreferences(context: Context): Int {
        val sharedPref = context.getSharedPreferences("MySharedPrefs", Context.MODE_PRIVATE)
        val savedId =
            sharedPref.getInt("first_time_id", -1)  // Default to -1 if 'first_time_id' is not found
        Log.d("SAVEEEVEVEVV", "Retrieved first time ID from SharedPreferences: $savedId")
        return savedId
    }


    fun clearIdFromSharedPreferences(context: Context) {
        val sharedPref = context.getSharedPreferences("MySharedPrefs", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.remove("first_time_id")  // Remove the 'user_id' entry
        editor.apply()  // Apply the changes asynchronously

        Log.d("CCCCCCC", "clearIdFromSharedPreferences: ")

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

    private fun callGroupApi(ids: String) {
        binding.progressBar.visibility = View.VISIBLE

        apiInterface.GropList()?.enqueue(object : Callback<GroupListData?> {
            override fun onResponse(
                call: Call<GroupListData?>,
                response: Response<GroupListData?>
            ) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val resource: GroupListData? = response.body()
                    if (resource?.status == true) {

                        // Filter the group list based on the provided `ids`
                        filteredGroupList =
                            resource.data?.filter { group -> group.id?.toString() == ids }
                                ?: emptyList()

                        // Ensure the list is not empty
                        if (filteredGroupList.isNotEmpty()) {
                            // Get the group object for the selected ID
                            val selectedGroup = filteredGroupList[0] // Based on the filtered group

                            // Log details of the selected group
                            Log.e("SelectedGroup", "onResponse: " + selectedGroup.image)
                            Log.e("SelectedGroup", "onResponse: " + selectedGroup.coach_id)

                            val imageUrl = "https://4trainersapp.com" + (selectedGroup.image ?: "")
                            Log.d("ImageURL", "URL: $imageUrl")

                            // Save the image URL in SharedPreferences
                            val sharedPreferences =
                                getSharedPreferences("appPrefs", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("imageUrll", imageUrl)
                            editor.apply()

                            binding.imageUpload.visibility = View.VISIBLE
                            binding.selectUploadLy.visibility = View.GONE

                            selectedImageUri2 = Uri.parse(imageUrl)

                            val transformation: Transformation = RoundedTransformationBuilder()
                                .borderColor(Color.BLACK)
                                .borderWidthDp(1f)
                                .cornerRadiusDp(10f)
                                .oval(false)
                                .build()

                            Picasso.get()
                                .load(imageUrl)
                                .fit()
                                .transform(transformation)
                                .error(R.drawable.group_chate_boarder)
                                .into(binding.imageUpload, object : com.squareup.picasso.Callback {
                                    override fun onSuccess() {
                                        Log.d("Picasso", "Image loaded successfully")
                                    }

                                    override fun onError(e: Exception?) {
                                        Log.e("Picasso", "Error loading image", e)
                                    }
                                })

                            selectedGroup.schedule?.forEach { schedule ->
                                schedule.day?.let { day ->
                                    when (day.lowercase()) {
                                        "monday" -> {
                                            showDefaultDay()

                                            toggleDay(
                                                "monday",
                                                binding.weekMon,
                                                mon_linearLayour,
                                                binding.monAddScheduleTime
                                            )

                                            addViewForDay(
                                                binding.MonLinearLayout,
                                                schedule.day ?: "",
                                                schedule.start_time?.toString() ?: "",
                                                schedule.end_time?.toString() ?: ""
                                            )
                                            datecheckstart = true
                                            datecheckend = true
                                        }

                                        "tue", "tuesday" -> {
                                            toggleDay(
                                                "tuesday",
                                                binding.weekMon,
                                                mon_linearLayour,
                                                binding.monAddScheduleTime
                                            )
                                            binding.TueLinearLayout.visibility =
                                                View.GONE // Hide initially
                                            addViewForDay(
                                                binding.TueLinearLayout,
                                                schedule.day ?: "",
                                                schedule.start_time?.toString() ?: "",
                                                schedule.end_time?.toString() ?: ""
                                            )
                                            datecheckstart = true
                                            datecheckend = true                                        }

                                        "wed", "wednesday" -> {
                                            toggleDay(
                                                "wednesday",
                                                binding.weekMon,
                                                mon_linearLayour,
                                                binding.monAddScheduleTime
                                            )
                                            binding.WedLinearLayout.visibility =
                                                View.GONE
                                            addViewForDay(
                                                binding.WedLinearLayout,
                                                schedule.day ?: "",
                                                schedule.start_time?.toString() ?: "",
                                                schedule.end_time?.toString() ?: ""
                                            )
                                            datecheckstart = true
                                            datecheckend = true
                                        }

                                        "thu", "thursday" -> {
                                            toggleDay(
                                                "thursday",
                                                binding.weekMon,
                                                mon_linearLayour,
                                                binding.monAddScheduleTime
                                            )
                                            binding.ThuLinearLayout.visibility =
                                                View.GONE // Hide initially
                                            addViewForDay(
                                                binding.ThuLinearLayout,
                                                schedule.day ?: "",
                                                schedule.start_time?.toString() ?: "",
                                                schedule.end_time?.toString() ?: ""
                                            )
                                            datecheckstart = true
                                            datecheckend = true
                                        }

                                        "fri", "friday" -> {
                                            toggleDay(
                                                "friday",
                                                binding.weekMon,
                                                mon_linearLayour,
                                                binding.monAddScheduleTime
                                            )
                                            binding.FriLinearLayout.visibility =
                                                View.GONE // Hide initially
                                            addViewForDay(
                                                binding.FriLinearLayout,
                                                schedule.day ?: "",
                                                schedule.start_time?.toString() ?: "",
                                                schedule.end_time?.toString() ?: ""
                                            )
                                            datecheckstart = true
                                            datecheckend = true
                                        }

                                        "sat", "saturday" -> {
                                            toggleDay(
                                                "saturday",
                                                binding.weekMon,
                                                mon_linearLayour,
                                                binding.monAddScheduleTime
                                            )
                                            binding.SatLinearLayout.visibility =
                                                View.GONE // Hide initially
                                            addViewForDay(
                                                binding.SatLinearLayout,
                                                schedule.day ?: "",
                                                schedule.start_time?.toString() ?: "",
                                                schedule.end_time?.toString() ?: ""
                                            )
                                            datecheckstart = true
                                            datecheckend = true
                                        }

                                        "sun", "sunday" -> {
                                            toggleDay(
                                                "sunday",
                                                binding.weekMon,
                                                mon_linearLayour,
                                                binding.monAddScheduleTime
                                            )
                                            binding.SunLinearLayout.visibility =
                                                View.GONE
                                            addViewForDay(
                                                binding.SunLinearLayout,
                                                schedule.day ?: "",
                                                schedule.start_time?.toString() ?: "",
                                                schedule.end_time?.toString() ?: ""
                                            )
                                            datecheckstart = true
                                            datecheckend = true
                                        }

                                        else -> {
                                            Log.d("UnknownDay", "Unknown day: $day")
                                        }
                                    }
                                }
                            }

                            // Set other data
                            setData(filteredGroupList)

                        } else {
                            Log.d("FilteredGroups", "No matching groups found")
                        }
                    }
                }
            }

            override fun onFailure(call: Call<GroupListData?>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@EditGroupActivity, t.message, Toast.LENGTH_SHORT).show()
                Log.e("GroupAPIError", t.message.orEmpty(), t)
            }
        })
    }


    private fun showDefaultDay() {
        binding.MonLinearLayout.visibility = View.VISIBLE
        binding.TueLinearLayout.visibility = View.GONE
        binding.WedLinearLayout.visibility = View.GONE
        binding.ThuLinearLayout.visibility = View.GONE
        binding.FriLinearLayout.visibility = View.GONE
        binding.SatLinearLayout.visibility = View.GONE
        binding.SunLinearLayout.visibility = View.GONE

        // Optional: Update background color for Monday
        binding.weekMon.setCardBackgroundColor(resources.getColor(R.color.splash_text_color))
    }


    private fun getAthleteData(ids: List<List<String>>) {
        Log.d("GetAthleteData", "Started with IDs: $ids")

        val flatIds = ids.flatten()
        Log.d("GetAthleteData", "Flattened IDs: $flatIds")

        athleteData.clear()
        binding.progressBar.visibility = View.VISIBLE

        apiInterface.GetAthleteList()?.enqueue(object : Callback<AthleteData> {
            override fun onResponse(call: Call<AthleteData>, response: Response<AthleteData>) {
                binding.progressBar.visibility = View.GONE
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
                            initrecyclerAthleteAdd(filteredData.toMutableList())
                        } else {
                            Log.d("GetAthleteData", "No matching athletes found")
                        }

                        for (athlete in athleteData) {
                            Log.d("GetAthleteData", "Athlete ID: ${athlete.id}")
                        }

                    } else if (response.code() == 403) {
                        Utils.setUnAuthDialog(this@EditGroupActivity)
                    } else {
                        Toast.makeText(
                            this@EditGroupActivity,
                            response.message(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<AthleteData>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Log.d("GetAthleteData", "Failure: ${t.message}")
                Toast.makeText(this@EditGroupActivity, t.message, Toast.LENGTH_SHORT).show()
                call.cancel()
            }
        })
    }

    private fun getTrainingData(ids: List<List<String>>) {
        Log.d("GetTrainingData", "Started with IDs: $ids")

        val flatIds = ids.flatten()
        Log.d("GetTrainingData", "Flattened IDs: $flatIds")

        binding.progressBar.visibility = View.VISIBLE

        apiInterface.GetTrainingPlan()?.enqueue(object : Callback<TrainingPlanData> {
            override fun onResponse(
                call: Call<TrainingPlanData>,
                response: Response<TrainingPlanData>
            ) {
                Log.d("GetTrainingData", "Response received with code: ${response.code()}")
                binding.progressBar.visibility = View.GONE
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
                            initrecyclerplanningAdd(filteredData.toMutableList())
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
                Toast.makeText(this@EditGroupActivity, "Error: " + t.message, Toast.LENGTH_SHORT)
                    .show()
                binding.progressBar.visibility = View.GONE
                call.cancel()
            }
        })
    }

    private fun GetLessonList(ids: List<List<String>>) {
        Log.d("GetLessonList", "Started with IDs: $ids")

        val flatIds = ids.flatten()
        Log.d("GetLessonList", "Flattened IDs: $flatIds")

        binding.progressBar.visibility = View.VISIBLE

        apiInterface.GetLession1().enqueue(object : Callback<Lesson> {
            override fun onResponse(call: Call<Lesson>, response: Response<Lesson>) {
                Log.d("GetLessonList", "Response received with code: ${response.code()}")
                binding.progressBar.visibility = View.GONE
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
                            initRecyclerviewAdd(filteredData)
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
                Toast.makeText(this@EditGroupActivity, "Error: " + t.message, Toast.LENGTH_SHORT)
                    .show()
                call.cancel()
            }
        })
    }

    private fun GetTestList(ids: List<List<String>>) {
        binding.progressBar.visibility = View.VISIBLE

        val flatIds = ids.flatten()
        Log.d("GetTestList", "Flattened IDs: $flatIds")

        apiInterface.GetTest()?.enqueue(object : Callback<TestListData?> {
            override fun onResponse(call: Call<TestListData?>, response: Response<TestListData?>) {
                Log.d("GetTestList", "Response received with code: ${response.code()}")
                binding.progressBar.visibility = View.GONE
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
                            initRecyclerTestAdd(ArrayList(filteredData))  // Convert List to ArrayList if needed
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
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@EditGroupActivity, "Error: ${t.message}", Toast.LENGTH_SHORT)
                    .show()
                call.cancel()
            }
        })
    }

    private fun geteventlist(ids: List<List<String>>) {
        binding.progressBar.visibility = View.VISIBLE

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
                            initrecyclerAdd(ArrayList(filteredData))
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
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@EditGroupActivity, "Error: ${t.message}", Toast.LENGTH_SHORT)
                    .show()
                call.cancel()
            }
        })
    }


    fun editGroupWithImageApiCall(context: Context, imageUri: Uri?) {

        binding.progressBar.visibility = View.VISIBLE

        val timingFormatted = collectTimings().toString()


        Log.d("DDHHDHDHHD", "editGroupWithImageApiCall: $timingFormatted")

        val daysids = selectedDays.joinToString(prefix = "[", postfix = "]", separator = ", ") { "\"$it\"" }
        val sportids = sportlId.id.toString()
        val lessonids = lessonId.joinToString(", ", prefix = "[", postfix = "]")
        val athleteids = athleteId.joinToString(", ", prefix = "[", postfix = "]")
        val testids = testId.joinToString(", ", prefix = "[", postfix = "]")
        val eventids = eventId.joinToString(", ", prefix = "[", postfix = "]")
        val planningids = planningId.joinToString(", ", prefix = "[", postfix = "]")

        val method = RequestBody.create("text/plain".toMediaTypeOrNull(), "PUT")
        val idbody = RequestBody.create("text/plain".toMediaTypeOrNull(), receivedId.toString())
        val sport_id = RequestBody.create("text/plain".toMediaTypeOrNull(), sportids)
        val name = RequestBody.create("text/plain".toMediaTypeOrNull(), binding.edtName.text.toString())
        val lession_ids = RequestBody.create("application/json".toMediaTypeOrNull(), lessonids)
        val athlete_ids = RequestBody.create("application/json".toMediaTypeOrNull(), athleteids)
        val event_ids = RequestBody.create("application/json".toMediaTypeOrNull(), eventids)
        val planning_ids = RequestBody.create("application/json".toMediaTypeOrNull(), planningids)
        val test_ids = RequestBody.create("application/json".toMediaTypeOrNull(), testids)
        val program_ids = RequestBody.create("application/json".toMediaTypeOrNull(), "[]")
        val days = RequestBody.create("application/json".toMediaTypeOrNull(), daysids)
        val timing = RequestBody.create("application/json".toMediaTypeOrNull(), timingFormatted)

        // Process the image
        val imagePart = processImage(context, imageUri)

        if (imagePart == null) {
            Log.e("EditGroup", "Image part is null. Image might not be processed correctly.")
        } else {
            Log.e("EditGroup", "Image part created successfully: ${imagePart.body.contentType()}")
        }

        apiInterface.editGroupWithImage(
            method, idbody, sport_id, name, lession_ids, athlete_ids,
            event_ids, planning_ids, test_ids, program_ids, days, timing, imagePart
        ).enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                binding.progressBar.visibility = View.GONE
                Log.e("EditGroup", "onResponse: " + response.body())
                if (response.isSuccessful) {
                    Log.e("KLKLKLKLKLKL", "onResponse: " + response.body())
                    Toast.makeText(context, "Group Edited Successfully", Toast.LENGTH_SHORT).show()

                    Log.d("TYTYYTYT","RRERER :-  ${response.body().toString()}")
                    Log.d("TYTYYTYT","RRERER :-  ${response.errorBody().toString()}")
                    Log.d("TYTYYTYT","RRERER :-  ${response.code().toString()}")
                    Log.d("TYTYYTYT","RRERER :-  ${response.toString()}")


                    val intent = Intent(context, HomeActivity::class.java)
                    intent.putExtra("group", "addGroup")
                    context.startActivity(intent)
                    finish()
                } else {
                    Log.e(
                        "EditGroup",
                        "Failed: ${response.code()} - ${response.errorBody()?.string()}"
                    )
                }
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Log.e("EditGroup", "API Call failed: ${t.message}")
            }
        })
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

    fun processImage(context: Context, imageUri: Uri?): MultipartBody.Part? {
        Log.d("SUJALLLLLLGGGGG", "processImage: $imageUri")

        val imageFile = imageUri?.let { createFileFromContentUri(context, it) }
        if (imageFile != null && imageFile.exists()) {
            val imageRequestBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            return MultipartBody.Part.createFormData("image", imageFile.name, imageRequestBody)
        }

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

//    fun createFileFromLocalPath(context: Context, localPath: String): File? {
//        val correctedPath = if (localPath.startsWith("/")) {
//            "file://$localPath"
//        } else {
//            localPath
//        }
//        val uri = Uri.parse(correctedPath)
//
//        return try {
//            createFileFromContentUri(context, uri)
//        } catch (e: Exception) {
//            Log.e("LocalPath", "Error resolving local file: ${e.message}")
//            e.printStackTrace()
//            null
//        }
//    }

    private fun saveGroupData(
        groupName: String,
        imageUri: Uri?,
        sportName: String,
        sportId: String
    ) {
        val sharedPreferences = getSharedPreferences("GroupDataEdit", MODE_PRIVATE)
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
        val sharedPreferences = getSharedPreferences("GroupDataEdit", MODE_PRIVATE)

        val groupName = sharedPreferences.getString("groupName", null)
        val imageUriString = sharedPreferences.getString("imageUri", null)
        val sportName = sharedPreferences.getString("sportName", null)
        val sportId = sharedPreferences.getString("sportId", null)

        Log.d("GroupData", "Loading data...")

        // Set group name
        groupName?.let {
            binding.edtName.setText(it)
            Log.d("GroupData", "Loaded groupName: $it")
        }

        // Only load the image if no image is currently selected
        if (selectedImageUri == null && imageUriString != null) {
            val imageUri = Uri.parse(imageUriString)
            if (imageUri.scheme == "content" || imageUri.scheme == "file") {
                Glide.with(this)
                    .load(imageUri)
                    .into(binding.imageUpload)

                binding.selectUploadLy.visibility = View.GONE
                binding.imageUpload.visibility = View.VISIBLE
                Log.d("GroupData999", "Image successfully loaded into ImageView")
            } else if (!imageUri.isAbsolute) {
                val completeUrl = "https://4trainersapp.com$imageUriString"
                Picasso.get()
                    .load(completeUrl)
                    .error(R.drawable.app_icon) // Fallback image in case of error
                    .into(binding.imageUpload, object : com.squareup.picasso.Callback {
                        override fun onSuccess() {
                            Log.d("Picasso", "Image loaded successfully.")
                        }

                        override fun onError(e: Exception?) {
                            Log.e("PicassoError", "Image load failed: ${e?.message}")
                        }
                    })


                binding.selectUploadLy.visibility = View.GONE
                binding.imageUpload.visibility = View.VISIBLE
                Log.d("GroupData999", "Relative path successfully loaded into ImageView $imageUri")
            } else {
                Log.e("GroupData999", "Invalid URI: $imageUri")
            }
        } else {
            Log.d("GroupData", "Skipping image loading as selectedImageUri is already set: $selectedImageUri")
        }

        sportName?.let {
            binding.edtSport.setText(it)
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

//    fun editGroup(uri: Uri) {
//        val imageFile = getFileFromUri(uri)
//        if (imageFile == null || !imageFile.exists()) {
//            Log.e("ERROR", "Invalid file URI or file does not exist.")
//            return
//        }
//
//        val timingFormatted = collectTimings().toString() // Assuming this returns the proper JSON format
//        val daysFormatted = selectedDays.joinToString(prefix = "[", postfix = "]", separator = ",") { "\"$it\"" }
//        val sportIdFormatted = sportlId.id.toString()
//        val lessonIdsFormatted = lessonId.joinToString(prefix = "[", postfix = "]", separator = ",")
//        val athleteIdsFormatted = athleteId.joinToString(prefix = "[", postfix = "]", separator = ",")
//        val eventIdsFormatted = eventId.joinToString(prefix = "[", postfix = "]", separator = ",")
//        val planningIdsFormatted = planningId.joinToString(prefix = "[", postfix = "]", separator = ",")
//        val testIdsFormatted = testId.joinToString(prefix = "[", postfix = "]", separator = ",")
//        val programIdsFormatted = "[60,61]"
//
//        Log.d("VBVBBVBV", "Received ID: $receivedId")
//
//        val idPart = RequestBody.create("text/plain".toMediaTypeOrNull(), receivedId.toString())
//        val sportIdBody = RequestBody.create("text/plain".toMediaTypeOrNull(), sportIdFormatted)
//        val nameBody = RequestBody.create("text/plain".toMediaTypeOrNull(), binding.edtName.text.toString())
//        val lessonIdsBody = RequestBody.create("application/json".toMediaTypeOrNull(), lessonIdsFormatted)
//        val athleteIdsBody = RequestBody.create("application/json".toMediaTypeOrNull(), athleteIdsFormatted)
//        val eventIdsBody = RequestBody.create("application/json".toMediaTypeOrNull(), eventIdsFormatted)
//        val planningIdsBody = RequestBody.create("application/json".toMediaTypeOrNull(), planningIdsFormatted)
//        val testIdsBody = RequestBody.create("application/json".toMediaTypeOrNull(), testIdsFormatted)
//        val programIdsBody = RequestBody.create("application/json".toMediaTypeOrNull(), programIdsFormatted)
//        val daysBody = RequestBody.create("application/json".toMediaTypeOrNull(), daysFormatted)
//        val timingBody = RequestBody.create("application/json".toMediaTypeOrNull(), timingFormatted)
//
//        val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), imageFile)
//        val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)
//
//        Log.d("VBVBBVBV", "idPart: $idPart")
//        Log.d("VBVBBVBV", "sportIdBody: $sportIdBody")
//        Log.d("VBVBBVBV", "nameBody: $nameBody")
//
//        apiInterface.editGroup(
//            idPart,"PUT", sportIdBody, nameBody, imagePart, lessonIdsBody, athleteIdsBody,
//            eventIdsBody, planningIdsBody, testIdsBody, programIdsBody, daysBody, timingBody
//        ).enqueue(object : Callback<ResponseBody> {
//            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                if (response.isSuccessful) {
//                    finish()
//                    Log.d("SUCCESS", "onResponse: Success: ${response.body()?.string()}")
//                    Log.e("SUCCESS", "onResponse: Error: ${response.errorBody()?.string()}")
//
//                } else {
//                    Log.e("ERROR", "onResponse: Error: ${response.errorBody()?.string()}")
//                }
//            }
//
//            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                Log.e("ERROR", "onFailure: ${t.message}", t)
//            }
//        })
//    }

    private fun collectTimings(): String {
        if (!hasChanges) {
            return loadDayTimesFromPreferences()
        }

        val timingList = mutableListOf<List<Map<String, String>>>()

        // Iterate through the days
        val daysLayout = listOf(
            mon_linearLayour to "monday",
            tue_linearLayout to "tuesday",
            wed_linearLayout to "wednesday",
            thu_linearLayout to "thursday",
            fri_linearLayout to "friday",
            sat_linearLayout to "saturday",
            sun_linearLayout to "sunday"
        )

        for ((layout, day) in daysLayout) {
            if (selectedDays.contains(day)) { // Only collect timings for the selected days
                val dailyTimings = mutableListOf<Map<String, String>>() // Collect timings for the specific day

                for (i in 0 until layout.childCount) {
                    val childView = layout.getChildAt(i)
                    val tvStartTime = childView.findViewById<TextView>(R.id.tv_start_time)
                    val tvEndTime = childView.findViewById<TextView>(R.id.tv_End_time)

                    val startTime = formatTime(tvStartTime.text.toString().trim())
                    val endTime = formatTime(tvEndTime.text.toString().trim())

                    Log.d("TIMINGS_DEBUG", "collectTimings: Start Time: '$startTime'  End Time: '$endTime'")

                    // If both startTime and endTime are empty, still add them as empty strings
                    val timingMap = mapOf(
                        "start_time" to startTime.ifEmpty { "" }, // If startTime is empty, use ""
                        "end_time" to endTime.ifEmpty { "" }      // If endTime is empty, use ""
                    )
                    dailyTimings.add(timingMap) // Add this timing to the day's list
                }

                // Only add the day's timings if there is at least one timing (even if empty)
                timingList.add(dailyTimings) // Add daily timings directly as a list of maps
            }
        }

        // If timingList is empty, log as an empty string and return a JSON array with empty objects
        if (timingList.isEmpty()) {
            Log.d("TIMINGS_DEBUG", "Timing List: [[]]")  // Log empty array if no timings are collected
            return "[[]]" // Return a JSON array with an empty object for each day
        } else {
            val gson = Gson()
            Log.d("TIMINGS_DEBUG", "Timing List: $timingList")
            return gson.toJson(timingList) // Return the serialized list as JSON
        }
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
            hasChanges = true
        } else {
            selectedDays.add(day)
            linearLayout.visibility = View.VISIBLE
            addScheduleTime.visibility = View.VISIBLE
            hasChanges = true // Track changes when selecting a day
        }

        // Log the selected days
        Log.d("SelectedDays", selectedDays.toString())
    }


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


//
//    private fun isStartTimeValidation(startTime: String) {
//        if (startTime.isEmpty()) {
//            start_error.visibility = View.VISIBLE
//        } else {
//            start_error.visibility = View.GONE
//        }
//    }
//
//    private fun isEndTimeValidation(endTime: String) {
//        if (endTime.isEmpty()) {
//            end_error.visibility = View.VISIBLE
//        } else {
//            end_error.visibility = View.GONE
//        }
//    }

    fun mon_addView() {
        addViewForDay(mon_linearLayour, "monday", "", "")
    }

    fun tue_addView() {
        addViewForDay(tue_linearLayout, "tuesday", "", "")
    }

    // Similarly for other days
    fun wed_addView() {
        addViewForDay(wed_linearLayout, "wednesday", "", "")
    }

    fun thu_addView() {
        addViewForDay(thu_linearLayout, "thursday", "", "")
    }

    fun fri_addView() {
        addViewForDay(fri_linearLayout, "friday", "", "")
    }

    fun sat_addView() {
        addViewForDay(sat_linearLayout, "saturday", "", "")
    }

    fun sun_addView() {
        addViewForDay(sun_linearLayout, "sunday", "", "")
    }

    // Initialize the layout with saved times
    private fun setSavedDayTimes() {
        val savedData = loadDayTimesFromPreferences()

        val gson = Gson()
        val type = object : TypeToken<Map<String, List<DayTime>>>() {}.type
        val dayTimesData: Map<String, List<DayTime>> = gson.fromJson(savedData, type)

        dayTimesData.forEach { (dayKey, times) ->
            val linearLayout = when (dayKey.lowercase()) {
                "monday" -> binding.MonLinearLayout
                "tuesday" -> binding.TueLinearLayout
                "wednesday" -> binding.WedLinearLayout
                "thursday" -> binding.ThuLinearLayout
                "friday" -> binding.FriLinearLayout
                "saturday" -> binding.SatLinearLayout
                "sunday" -> binding.SunLinearLayout
                else -> null
            }

            linearLayout?.let {
                times.forEach { dayTime ->
                    addViewForDay(it, dayKey, dayTime.startTime, dayTime.endTime)
                }
            }
        }
    }


    private fun addViewForDay(
        linearLayout: LinearLayout,
        dayKey: String,
        startTime: String?,
        endTime: String?
    ) {
        val inflater = LayoutInflater.from(this).inflate(R.layout.time_layout_group, null)
        linearLayout.addView(inflater, linearLayout.childCount)

        val tvStartTime: AppCompatEditText = inflater.findViewById(R.id.tv_start_time)
        val tvEndTime: AppCompatEditText = inflater.findViewById(R.id.tv_End_time)
        val tvStartTimeCard: CardView = inflater.findViewById(R.id.start_time_card)
        val tvEndTimeCard: CardView = inflater.findViewById(R.id.card_end_time)
        val delete: ImageView = inflater.findViewById(R.id.img_delete)

        val id = linearLayout.childCount
        Log.d("HGHGHGHGH", "addViewForDay: $id")

        tvStartTime.setText(startTime)
        tvEndTime.setText(endTime)

        updateDayTimes(dayKey, id.toString(), startTime ?: "", endTime ?: "")
        saveDayTimesToPreferences()

        // Set listeners for the time fields
        tvStartTimeCard.setOnClickListener {
            SetDialog_start(tvStartTime)
            checkDateValidity(tvStartTime, tvEndTime)
        }

        tvEndTimeCard.setOnClickListener {
            setDialogEnd(tvEndTime)
            checkDateValidity(tvStartTime, tvEndTime)
        }

        tvStartTime.setOnClickListener {
            Log.d("CLICK", "Start time clicked")
            SetDialog_start(tvStartTime)
            checkDateValidity(tvStartTime, tvEndTime)
        }

        tvEndTime.setOnClickListener {
            Log.d("CLICK", "End time clicked")
            setDialogEnd(tvEndTime)
            checkDateValidity(tvStartTime, tvEndTime)
        }

        // TextChangedListener for the start time field
        tvStartTime.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                updateDayTimes(dayKey, id.toString(), s.toString(), tvEndTime.text.toString())
                checkDateValidity(tvStartTime, tvEndTime)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // TextChangedListener for the end time field
        tvEndTime.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                updateDayTimes(dayKey, id.toString(), tvStartTime.text.toString(), s.toString())
                checkDateValidity(tvStartTime, tvEndTime)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        Log.d("OPOOOOOO", "addViewForDay: ${linearLayout.size}")
        Log.d("OPOOOOOO", "addViewForDay: ${linearLayout.childCount}")

        delete.setOnClickListener {
            val currentCount = linearLayout.childCount

            if (currentCount > 1) {
                // Find the index of the view to remove
                val indexToRemove = linearLayout.indexOfChild(inflater)

                // Remove the current view from the layout
                linearLayout.removeView(inflater)

                if (dayTimes.containsKey(dayKey)) {
                    val times = dayTimes[dayKey]?.toMutableList() ?: mutableListOf()
                    if (indexToRemove >= 0 && indexToRemove < times.size) {
                        times.removeAt(indexToRemove)
                        dayTimes[dayKey] = times
                        Log.d("DayTimesPrefsffffff", "Updated timings for $dayKey: $times")
                    }
                }
                clearAllPreferences()
                saveDayTimesToPreferences()
            } else if (currentCount == 1) {
                // Clear the text fields of the last remaining view instead of removing it
                val remainingView = linearLayout.getChildAt(0)
                val remainingStartTime: TextView = remainingView.findViewById(R.id.tv_start_time)
                val remainingEndTime: TextView = remainingView.findViewById(R.id.tv_End_time)

                remainingStartTime.text = ""
                remainingEndTime.text = ""

                // Set start and end time as empty strings for all entries in the dayTimes list
                if (dayTimes.containsKey(dayKey)) {
                    val times = dayTimes[dayKey]?.toMutableList() ?: mutableListOf()

                    // Create a list of DayTime objects with empty start and end times
                    val updatedTimes = times.map {
                        DayTime(id = it.id, startTime = "", endTime = "") // Create new DayTime objects with empty times
                    }

                    // Update the dayTimes map with the modified list of DayTime objects
                    dayTimes[dayKey] = updatedTimes.toMutableList()  // Save the updated times

                    // Log the output in the desired format
                    val formattedTimes = updatedTimes.joinToString(", ") {
                        "{start_time=\"${it.startTime}\", end_time=\"${it.endTime}\"}"
                    }
                    Log.d("DayTimesPrefsfffffdddf", "Cleared start and end times for $dayKey: [$formattedTimes]")
                }

                // Clear the preferences and save updated data
                clearAllPreferences()
                saveDayTimesToPreferences()

                datecheckstart = true
                datecheckend = true
                Log.d("LINEAR_LAYOUT", "Cleared text fields of the last remaining view    $datecheckstart    $datecheckend")
            }

            val updatedCount = linearLayout.childCount
            Log.d("LINEAR_LAYOUT", "Updated child count: $updatedCount")

            // Enable or disable delete button based on child count
            if (updatedCount > 1) {
                delete.isEnabled = true
                delete.alpha = 1.0f
            } else {
                delete.isEnabled = false
                delete.alpha = 0.5f
            }
        }


    }


    private fun checkDateValidity(tvStartTime: AppCompatEditText, tvEndTime: AppCompatEditText) {
        val start = tvStartTime.text.toString()
        val end = tvEndTime.text.toString()

        if (start.isEmpty() || end.isEmpty()) {
            datecheckstart = false
            datecheckend = false
        } else {
            datecheckstart = true
            datecheckend = true
        }

        Log.d("DATECHECK", "Date check: $datecheckstart    $datecheckend")
    }

    // Save dayTimes to SharedPreferences
    fun saveDayTimesToPreferences() {
        val sharedPreferences = getSharedPreferences("DayTimesPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()

        val jsonString = gson.toJson(dayTimes)
        editor.putString("day_times", jsonString)
        editor.apply()

        // Log the data being saved
        Log.d("DayTimesPrefssssss", "Saved dayTimes: $jsonString")
    }

    // Load dayTimes from SharedPreferences
    fun loadDayTimesFromPreferences(): String {
        val sharedPreferences = getSharedPreferences("DayTimesPrefs", MODE_PRIVATE)
        val gson = Gson()
        val jsonString = sharedPreferences.getString("day_times", null)

        val type = object : TypeToken<Map<String, List<DayTime>>>() {}.type
        val loadedData: Map<String, List<DayTime>> = if (jsonString != null) {
            gson.fromJson(jsonString, type)
        } else {
            emptyMap()
        }

        // Log the loaded data
        Log.d("DayTimesPrefs", "Loaded dayTimes (JSON): $jsonString")
        Log.d("DayTimesPrefs", "Loaded dayTimes (Parsed): $loadedData")

        // Return the loaded data as a JSON string
        return gson.toJson(loadedData)
    }


    fun clearAllPreferences() {
        val sharedPreferences = getSharedPreferences("DayTimesPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.clear()
        editor.apply()

        // Log the data cleared
        Log.d("DayTimesPrefs", "Cleared all data from SharedPreferences.")
    }


    private fun saveIdsToPreferences() {
        val sharedPreferences = getSharedPreferences("MyAppPrefsforEdit", MODE_PRIVATE)
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
        val sharedPreferences = getSharedPreferences("MyAppPrefsforEdit", MODE_PRIVATE)

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
        val sharedPreferences = getSharedPreferences("MyAppPrefsforEdit", MODE_PRIVATE)

        val existingLessonIds = sharedPreferences.getString("lessonId", "")?.split(",")?.mapNotNull { it.toIntOrNull() }
            ?.toSet() ?: emptySet()
        val existingTestIds = sharedPreferences.getString("testId", "")?.split(",")?.mapNotNull { it.toIntOrNull() }
            ?.toSet() ?: emptySet()
        val existingEventIds = sharedPreferences.getString("eventId", "")?.split(",")?.mapNotNull { it.toIntOrNull() }
            ?.toSet() ?: emptySet()
        val existingPlanningIds = sharedPreferences.getString("planningId", "")?.split(",")
            ?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet()
        val existingAthleteIds = sharedPreferences.getString("athleteId", "")?.split(",")
            ?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet()

        planningId = if (planningId.isNotEmpty()) planningId else existingPlanningIds.toIntArray()

        // Keep other IDs unchanged, even if planningId is updated
        lessonId = if (lessonId.isNotEmpty()) lessonId else existingLessonIds.toIntArray()
        testId = if (testId.isNotEmpty()) testId else existingTestIds.toIntArray()
        eventId = if (eventId.isNotEmpty()) eventId else existingEventIds.toIntArray()
        athleteId = if (athleteId.isNotEmpty()) athleteId else existingAthleteIds.toIntArray()

        Log.d("MergedIDs", "lessonId: ${lessonId.joinToString()}")
        Log.d("MergedIDs", "testId: ${testId.joinToString()}")
        Log.d("MergedIDs", "eventId: ${eventId.joinToString()}")
        Log.d("MergedIDs", "planningId: ${planningId.joinToString()}")
        Log.d("MergedIDs", "athleteId: ${athleteId.joinToString()}")

        // Save merged IDs to SharedPreferences
        saveIdsToPreferences()
    }

    private fun clearIdsFromPreferences() {
        val sharedPreferences = getSharedPreferences("MyAppPrefsforEdit", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.remove("lessonId")
        editor.remove("testId")
        editor.remove("eventId")
        editor.remove("planningId")
        editor.remove("athleteId")

        editor.apply()
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


    // Method to set saved dayTimes to views


    private val isStartTimeValidation: Boolean
        get() {
            val inflater = LayoutInflater.from(this).inflate(R.layout.time_layout_group, null)

            start_error = inflater.findViewById(R.id.start_error)
            end_error = findViewById(R.id.end_error)
            tv_start_time = findViewById(R.id.tv_start_time)
            tv_End_time = findViewById(R.id.tv_End_time)

            start_error.visibility = View.GONE
            val start_time = tv_start_time.text.toString()
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

            val inflater = LayoutInflater.from(this).inflate(R.layout.time_layout_group, null)

            start_error = inflater.findViewById(R.id.start_error)
            end_error = findViewById(R.id.end_error)
            tv_start_time = findViewById(R.id.tv_start_time)
            tv_End_time = findViewById(R.id.tv_End_time)

            end_error.visibility = View.GONE
            val end_time = tv_End_time.text.toString()
            if (end_time == "") {
                end_error.visibility = View.VISIBLE
                end_error.text = "Invalid End Time"
                return false
            } else {
                end_error.visibility = View.GONE
            }
            return true
        }

    private fun setData(filteredGroupList: List<GroupListData.groupData>) {
        if (filteredGroupList.isNotEmpty()) {
            val group = filteredGroupList.first()
            Log.d("SetData", "Group Name: ${group.name}, Sport: ${group.sport?.title}")
            binding.edtName.setText(group.name ?: "")
            binding.edtSport.setText(group.sport?.title ?: "")

            val sport = group.sport_id
            if (!sport.isNullOrEmpty()) {
                sportlId = SelectedValue(sport.toInt())  // Wrap the string in a SelectedValue
                Log.d(
                    "QWQWQWQWQWQW",
                    "setData sportlId: ${sportlId.id}"
                ) // Assuming SelectedValue has a 'value' property
            } else {
            }

            val image = group.image
            Log.d("IMAGEEEEEEEEEE", "setData: $image")
            selectedImageUri = image?.let { Uri.parse(it) }
            Log.d("IMAGEEEEEEEEEE", "setData: $image")
            Log.d("IMAGEEEEEEEEEE", "setData: $selectedImageUri")


            val groupPlannings = group.group_plannings
            if (groupPlannings!!.isNotEmpty()) {
                planningId = groupPlannings.map { it.planning_id?.toIntOrNull() ?: 0 }.toIntArray()
                Log.d("QWQWQWQWQWQW", "setData planningId: ${planningId.joinToString()}")
            } else {

            }

            if (groupPlannings != null && groupPlannings.isNotEmpty()) {
                initrecyclerplanning(groupPlannings.toMutableList())
            } else {
                Log.d("SetData", "No group_plannings available")
            }

            val groupLesson = group.group_lessions

            if (groupLesson!!.isNotEmpty()) {
                lessonId = groupLesson.map { it.lession_id?.toIntOrNull() ?: 0 }.toIntArray()
                Log.d("QWQWQWQWQWQW", "setData lessonId: ${lessonId.joinToString()}")
            } else {

            }
            if (groupLesson != null) {
                initRecyclerview(groupLesson)
            }

            val groupTest = group.group_tests

            if (groupTest!!.isNotEmpty()) {
                testId = groupTest.map { it.test_id?.toIntOrNull() ?: 0 }.toIntArray()
                Log.d("QWQWQWQWQWQW", "setData testId: ${testId.joinToString()}")
            } else {

            }
            if (groupTest != null) {
                initRecyclerTest(groupTest)
            }

            val groupEvent = group.group_events
            if (groupEvent!!.isNotEmpty()) {
                eventId = groupEvent.map { it.event_id?.toIntOrNull() ?: 0 }.toIntArray()
                Log.d("QWQWQWQWQWQW", "setData eventId: ${eventId.joinToString()}")
            } else {

            }
            if (groupEvent != null) {
                initrecycler(groupEvent)
            }

            val groupAthlete = group.group_members

            if (groupAthlete!!.isNotEmpty()) {
                athleteId = groupAthlete.map { it.athlete_id?.toIntOrNull() ?: 0 }.toIntArray()
                Log.d("QWQWQWQWQWQW", "setData testId: ${testId.joinToString()}")
            } else {

            }
            if (groupAthlete != null) {
                initrecyclerAthlete(groupAthlete)
            }

            mergeIdsAndSave()

        } else {
            Log.d("SetData", "Filtered group list is empty")
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
                this@EditGroupActivity,
                R.drawable.popup_background
            )
        )
        popupWindow.elevation = 10f
        val listView = popupView.findViewById<ListView>(R.id.listView)

        val adapter =
            object : ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list) {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val view = super.getView(position, convertView, parent) as TextView
                    val typeface = ResourcesCompat.getFont(this@EditGroupActivity, R.font.poppins_medium)
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

    private fun initrecyclerAthlete(testdatalist: MutableList<GroupListData.GroupMembar>?) {
        binding.progressBar.visibility = View.GONE
        binding.rlyAthlete.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        adapterAthlete = SetEditAthleteAdapter(testdatalist, this, this)
        binding.rlyAthlete.adapter = adapterAthlete
    }


    private fun initrecycler(testdatalist: ArrayList<GroupListData.GroupEvents>?) {
        binding.progressBar.visibility = View.GONE
        eventAdapter = SetEditEventAdapter(testdatalist, this, this)
        binding.rlyEvent.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rlyEvent.adapter = eventAdapter

        eventAdapter?.notifyDataSetChanged()
    }

    private fun initRecyclerTest(testdatalist: ArrayList<GroupListData.GroupTest>) {
        binding.progressBar.visibility = View.GONE
        binding.rlyTest.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        testAdapter = SetEditGroupTestAdapter(testdatalist, this, this)
        binding.rlyTest.adapter = testAdapter
    }

    private fun initRecyclerview(data: List<GroupListData.GroupLesson>) {
        binding.rlyLesson.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        lessonAdapter = SetEditGroupLessonAdapter(data, this, this)
        binding.rlyLesson.adapter = lessonAdapter
    }

    private fun initrecyclerplanning(trainingPlans: MutableList<GroupListData.GroupPlanning>?) {
        binding.progressBar.visibility = View.GONE

        if (trainingPlans != null && trainingPlans.isNotEmpty()) {
            Log.d("RecyclerViewsss", "Initializing RecyclerView with ${trainingPlans.size} items.")
            binding.rlyPlanning.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

            adapterPlanning = SetEditGroupPlanningAdapter(trainingPlans, this, this)

            binding.rlyPlanning.adapter = adapterPlanning

            adapterPlanning.notifyDataSetChanged()
        } else {
            Log.d("RecyclerViewssss", "No training plans available.")
        }
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
                                    this@EditGroupActivity,
                                    "No Data Found",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            binding.progressBar.visibility = View.GONE
                        }
                    } else if (response.code() == 403) {
                        Utils.setUnAuthDialog(this@EditGroupActivity)

                    } else {
                        binding.progressBar.visibility = View.GONE
                        val message = response.message()
                        Toast.makeText(
                            this@EditGroupActivity,
                            "" + message,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }


                override fun onFailure(call: Call<SportlistData?>, t: Throwable) {
                    Toast.makeText(this@EditGroupActivity, "" + t.message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                    binding.progressBar.visibility = View.GONE
                }
            })
        } catch (e: Exception) {
            Log.d("Error Exception", "${e.message}")
        }
    }


    private fun initrecyclerAthleteAdd(testdatalist: MutableList<AthleteData.Athlete>?) {
        binding.progressBar.visibility = View.GONE
        binding.rlyAthlete.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        adapterAthleteadd = SetAthleteInGroup(testdatalist, this, this)
        binding.rlyAthlete.adapter = adapterAthleteadd
    }

    private fun initrecyclerplanningAdd(testdatalist: MutableList<TrainingPlanData.TrainingPlan>?) {
        binding.progressBar.visibility = View.GONE
        binding.rlyPlanning.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        adapterPlanningadd = SetPlanningGroup(testdatalist, this, this)
        binding.rlyPlanning.adapter = adapterPlanningadd
    }

    private fun initrecyclerAdd(testdatalist: ArrayList<EventListData.testData>?) {
        binding.progressBar.visibility = View.GONE
        eventAdapteradd = SetEventInGroup(testdatalist, this, this)
        binding.rlyEvent.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rlyEvent.adapter = eventAdapteradd

    }

    private fun initRecyclerTestAdd(testdatalist: ArrayList<TestListData.testData>) {
        binding.progressBar.visibility = View.GONE
        binding.rlyTest.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        testAdapteradd = SetTestInGroup(testdatalist, this, this)
        binding.rlyTest.adapter = testAdapteradd
    }

    private fun initRecyclerviewAdd(data: List<Lesson.LessonDatabase>) {
        binding.rlyLesson.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        lessonAdapteradd = SetLessonInGroup(data, this, this)
        binding.rlyLesson.adapter = lessonAdapteradd
    }


    override fun onItemClicked(view: View, position: Int, type: Long, string: String) {

    }
}
