package com.example

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
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
import android.widget.Button
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.CycleData
import com.example.trainerapp.R
import com.example.trainerapp.databinding.ActivityCreateGropBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@Suppress("DEPRECATION")
class CreateGropActivity : AppCompatActivity() {
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
            mon_linearLayour.visibility = View.VISIBLE
            tue_linearLayout.visibility = View.INVISIBLE
            wed_linearLayout.visibility = View.INVISIBLE
            thu_linearLayout.visibility = View.INVISIBLE
            fri_linearLayout.visibility = View.INVISIBLE
            sat_linearLayout.visibility = View.INVISIBLE
            sun_linearLayout.visibility = View.INVISIBLE
            createGroupBinding.monAddScheduleTime.visibility = View.VISIBLE
            createGroupBinding.tueAddScheduleTime.visibility = View.INVISIBLE
            createGroupBinding.wedAddScheduleTime.visibility = View.INVISIBLE
            createGroupBinding.thuAddScheduleTime.visibility = View.INVISIBLE
            createGroupBinding.friAddScheduleTime.visibility = View.INVISIBLE
            createGroupBinding.satAddScheduleTime.visibility = View.INVISIBLE
            createGroupBinding.sunAddScheduleTime.visibility = View.INVISIBLE
            createGroupBinding.weekMon.setCardBackgroundColor(resources.getColor(R.color.splash_text_color))
            createGroupBinding.weekTue.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekWed.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekThu.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekFri.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekSat.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekSun.setCardBackgroundColor(resources.getColor(R.color.card_background))
        }
        createGroupBinding.weekTue.setOnClickListener {
            mon_linearLayour.visibility = View.INVISIBLE
            tue_linearLayout.visibility = View.VISIBLE
            wed_linearLayout.visibility = View.INVISIBLE
            thu_linearLayout.visibility = View.INVISIBLE
            fri_linearLayout.visibility = View.INVISIBLE
            sat_linearLayout.visibility = View.INVISIBLE
            sun_linearLayout.visibility = View.INVISIBLE
            createGroupBinding.monAddScheduleTime.visibility = View.INVISIBLE
            createGroupBinding.tueAddScheduleTime.visibility = View.VISIBLE
            createGroupBinding.wedAddScheduleTime.visibility = View.INVISIBLE
            createGroupBinding.thuAddScheduleTime.visibility = View.INVISIBLE
            createGroupBinding.friAddScheduleTime.visibility = View.INVISIBLE
            createGroupBinding.satAddScheduleTime.visibility = View.INVISIBLE
            createGroupBinding.sunAddScheduleTime.visibility = View.INVISIBLE
            createGroupBinding.weekMon.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekTue.setCardBackgroundColor(resources.getColor(R.color.splash_text_color))
            createGroupBinding.weekWed.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekThu.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekFri.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekSat.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekSun.setCardBackgroundColor(resources.getColor(R.color.card_background))
        }
        createGroupBinding.weekWed.setOnClickListener {
            mon_linearLayour.visibility = View.INVISIBLE
            tue_linearLayout.visibility = View.INVISIBLE
            wed_linearLayout.visibility = View.VISIBLE
            thu_linearLayout.visibility = View.INVISIBLE
            fri_linearLayout.visibility = View.INVISIBLE
            sat_linearLayout.visibility = View.INVISIBLE
            sun_linearLayout.visibility = View.INVISIBLE
            createGroupBinding.monAddScheduleTime.visibility = View.INVISIBLE
            createGroupBinding.tueAddScheduleTime.visibility = View.INVISIBLE
            createGroupBinding.wedAddScheduleTime.visibility = View.VISIBLE
            createGroupBinding.thuAddScheduleTime.visibility = View.INVISIBLE
            createGroupBinding.friAddScheduleTime.visibility = View.INVISIBLE
            createGroupBinding.satAddScheduleTime.visibility = View.INVISIBLE
            createGroupBinding.sunAddScheduleTime.visibility = View.INVISIBLE
            createGroupBinding.weekMon.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekTue.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekWed.setCardBackgroundColor(resources.getColor(R.color.splash_text_color))
            createGroupBinding.weekThu.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekFri.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekSat.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekSun.setCardBackgroundColor(resources.getColor(R.color.card_background))
        }
        createGroupBinding.weekThu.setOnClickListener {
            mon_linearLayour.visibility = View.INVISIBLE
            tue_linearLayout.visibility = View.INVISIBLE
            wed_linearLayout.visibility = View.INVISIBLE
            thu_linearLayout.visibility = View.VISIBLE
            fri_linearLayout.visibility = View.INVISIBLE
            sat_linearLayout.visibility = View.INVISIBLE
            sun_linearLayout.visibility = View.INVISIBLE
            createGroupBinding.monAddScheduleTime.visibility = View.INVISIBLE
            createGroupBinding.tueAddScheduleTime.visibility = View.INVISIBLE
            createGroupBinding.wedAddScheduleTime.visibility = View.INVISIBLE
            createGroupBinding.thuAddScheduleTime.visibility = View.VISIBLE
            createGroupBinding.friAddScheduleTime.visibility = View.INVISIBLE
            createGroupBinding.satAddScheduleTime.visibility = View.INVISIBLE
            createGroupBinding.sunAddScheduleTime.visibility = View.INVISIBLE
            createGroupBinding.weekMon.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekTue.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekWed.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekThu.setCardBackgroundColor(resources.getColor(R.color.splash_text_color))
            createGroupBinding.weekFri.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekSat.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekSun.setCardBackgroundColor(resources.getColor(R.color.card_background))
        }
        createGroupBinding.weekFri.setOnClickListener {
            mon_linearLayour.visibility = View.INVISIBLE
            tue_linearLayout.visibility = View.INVISIBLE
            wed_linearLayout.visibility = View.INVISIBLE
            thu_linearLayout.visibility = View.INVISIBLE
            fri_linearLayout.visibility = View.VISIBLE
            sat_linearLayout.visibility = View.INVISIBLE
            sun_linearLayout.visibility = View.INVISIBLE
            createGroupBinding.friAddScheduleTime.visibility = View.VISIBLE
            createGroupBinding.weekMon.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekTue.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekWed.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekThu.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekFri.setCardBackgroundColor(resources.getColor(R.color.splash_text_color))
            createGroupBinding.weekSat.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekSun.setCardBackgroundColor(resources.getColor(R.color.card_background))
        }
        createGroupBinding.weekSat.setOnClickListener {
            mon_linearLayour.visibility = View.INVISIBLE
            tue_linearLayout.visibility = View.INVISIBLE
            wed_linearLayout.visibility = View.INVISIBLE
            thu_linearLayout.visibility = View.INVISIBLE
            fri_linearLayout.visibility = View.INVISIBLE
            sat_linearLayout.visibility = View.VISIBLE
            sun_linearLayout.visibility = View.INVISIBLE
            createGroupBinding.monAddScheduleTime.visibility = View.INVISIBLE
            createGroupBinding.tueAddScheduleTime.visibility = View.INVISIBLE
            createGroupBinding.wedAddScheduleTime.visibility = View.INVISIBLE
            createGroupBinding.thuAddScheduleTime.visibility = View.INVISIBLE
            createGroupBinding.friAddScheduleTime.visibility = View.INVISIBLE
            createGroupBinding.satAddScheduleTime.visibility = View.INVISIBLE
            createGroupBinding.sunAddScheduleTime.visibility = View.VISIBLE
            createGroupBinding.weekMon.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekTue.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekWed.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekThu.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekFri.setCardBackgroundColor(resources.getColor(R.color.card_background))
            createGroupBinding.weekSat.setCardBackgroundColor(resources.getColor(R.color.splash_text_color))
            createGroupBinding.weekSun.setCardBackgroundColor(resources.getColor(R.color.card_background))
        }
        createGroupBinding.weekSun.setOnClickListener {
            mon_linearLayour.visibility = View.INVISIBLE
            tue_linearLayout.visibility = View.INVISIBLE
            wed_linearLayout.visibility = View.INVISIBLE
            thu_linearLayout.visibility = View.INVISIBLE
            fri_linearLayout.visibility = View.INVISIBLE
            sat_linearLayout.visibility = View.INVISIBLE
            sun_linearLayout.visibility = View.VISIBLE
            createGroupBinding.monAddScheduleTime.visibility = View.INVISIBLE
            createGroupBinding.tueAddScheduleTime.visibility = View.INVISIBLE
            createGroupBinding.wedAddScheduleTime.visibility = View.INVISIBLE
            createGroupBinding.thuAddScheduleTime.visibility = View.INVISIBLE
            createGroupBinding.friAddScheduleTime.visibility = View.INVISIBLE
            createGroupBinding.satAddScheduleTime.visibility = View.INVISIBLE
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
            startActivity(Intent(this, CreatePlanningActivity::class.java))
        }

        createGroupBinding.btnAddLesson.setOnClickListener {
            startActivity(Intent(this, LessonListActivity::class.java))
        }

        createGroupBinding.btnAddTest.setOnClickListener {
            startActivity(Intent(this, LessonTestActivity::class.java))
        }

        createGroupBinding.btnAddEvent.setOnClickListener {
            startActivity(Intent(this, LessonEventActivity::class.java))
        }

        createGroupBinding.btnAddAthlete.setOnClickListener {
            startActivity(Intent(this, LessonAthleteActivity::class.java))
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
            dialog.show()
        }
        createGroupBinding.selectImage.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1)
        }

        createGroupBinding.back.setOnClickListener {
            finish()
        }

        createGroupBinding.nextCard.setOnClickListener {
            val requestFile: RequestBody =
                RequestBody.create("image/*".toMediaTypeOrNull(), "")
            val image =
                MultipartBody.Part.createFormData("image", "image", requestFile)
            val sport_id: MultipartBody.Part =
                MultipartBody.Part.createFormData("sport_id", "1")
            val name: MultipartBody.Part =
                MultipartBody.Part.createFormData("name", "test1")
            val lession_ids: MultipartBody.Part =
                MultipartBody.Part.createFormData(
                    "lession_ids",
                    createGroupBinding.edtName.text.toString()
                )
            val athlete_ids: MultipartBody.Part =
                MultipartBody.Part.createFormData(
                    "athlete_ids",
                    createGroupBinding.edtName.text.toString()
                )
            val event_ids: MultipartBody.Part =
                MultipartBody.Part.createFormData(
                    "event_ids",
                    createGroupBinding.edtName.text.toString()
                )
            val planning_ids: MultipartBody.Part =
                MultipartBody.Part.createFormData(
                    "planning_ids",
                    createGroupBinding.edtName.text.toString()
                )
            val test_ids: MultipartBody.Part =
                MultipartBody.Part.createFormData(
                    "test_ids",
                    createGroupBinding.edtName.text.toString()
                )
            val program_ids: MultipartBody.Part =
                MultipartBody.Part.createFormData(
                    "program_ids",
                    createGroupBinding.edtName.text.toString()
                )
            val schedule: MultipartBody.Part =
                MultipartBody.Part.createFormData(
                    "schedule",
                    "monday,tuesday,wednesday,thursday,friday,saturday"
                )

            apiInterface.AddGroup(
                sport_id,
                name,
                image,
                lession_ids,
                athlete_ids,
                event_ids,
                planning_ids,
                test_ids,
                program_ids,
                schedule
            )?.enqueue(object : Callback<CycleData?> {
                override fun onResponse(call: Call<CycleData?>, response: Response<CycleData?>) {
                    Log.d("TAG", response.code().toString() + "")
                    val code = response.code()
                    if (code == 200) {
                        val resource: CycleData? = response.body()
                        val Success: Boolean = resource?.status!!
                        val Message: String = resource.message!!

                        for (i in 0 until response.body()!!.data!!.size) {

                        }
                    }
                }

                override fun onFailure(call: Call<CycleData?>, t: Throwable) {
                    Toast.makeText(this@CreateGropActivity, "" + t.message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }
            })
        }
    }

    private fun mon_addView() {
        inflater = LayoutInflater.from(this).inflate(R.layout.time_layout_group, null)
        mon_linearLayour.addView(inflater, mon_linearLayour.childCount)
        tv_start_time = inflater.findViewById(R.id.tv_start_time)
        tv_End_time = inflater.findViewById(R.id.tv_End_time)


        tv_start_time.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {

            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                isStartTimeValidation

            }


        })

        tv_End_time.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {

            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                isEndTimeValidation

            }
        })


    }

    private fun tue_addView() {
        inflater = LayoutInflater.from(this).inflate(R.layout.time_layout_group, null)
        tue_linearLayout.addView(inflater, tue_linearLayout.childCount)
        tv_start_time = inflater.findViewById(R.id.tv_start_time)
        tv_End_time = inflater.findViewById(R.id.tv_End_time)

        tv_start_time.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {

            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                isStartTimeValidation

            }


        })

        tv_End_time.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {

            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                isEndTimeValidation

            }
        })


    }

    private fun wed_addView() {
        inflater = LayoutInflater.from(this).inflate(R.layout.time_layout_group, null)
        wed_linearLayout.addView(inflater, wed_linearLayout.childCount)
        tv_start_time = inflater.findViewById(R.id.tv_start_time)
        tv_End_time = inflater.findViewById(R.id.tv_End_time)

        tv_start_time.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {

            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                isStartTimeValidation

            }


        })

        tv_End_time.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {

            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                isEndTimeValidation

            }
        })


    }

    private fun thu_addView() {
        inflater = LayoutInflater.from(this).inflate(R.layout.time_layout_group, null)
        thu_linearLayout.addView(inflater, thu_linearLayout.childCount)
        tv_start_time = inflater.findViewById(R.id.tv_start_time)
        tv_End_time = inflater.findViewById(R.id.tv_End_time)

        tv_start_time.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {

            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                isStartTimeValidation

            }


        })

        tv_End_time.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {

            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                isEndTimeValidation

            }
        })


    }

    private fun fri_addView() {
        inflater = LayoutInflater.from(this).inflate(R.layout.time_layout_group, null)
        fri_linearLayout.addView(inflater, fri_linearLayout.childCount)
        tv_start_time = inflater.findViewById(R.id.tv_start_time)
        tv_End_time = inflater.findViewById(R.id.tv_End_time)

        tv_start_time.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {

            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                isStartTimeValidation

            }


        })

        tv_End_time.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {

            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                isEndTimeValidation

            }
        })


    }

    private fun sat_addView() {
        inflater = LayoutInflater.from(this).inflate(R.layout.time_layout_group, null)
        sat_linearLayout.addView(inflater, sat_linearLayout.childCount)
        tv_start_time = inflater.findViewById(R.id.tv_start_time)
        tv_End_time = inflater.findViewById(R.id.tv_End_time)

        tv_start_time.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {

            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                isStartTimeValidation

            }


        })

        tv_End_time.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {

            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                isEndTimeValidation

            }
        })


    }

    private fun sun_addView() {
        inflater = LayoutInflater.from(this).inflate(R.layout.time_layout_group, null)
        sun_linearLayout.addView(inflater, sun_linearLayout.childCount)
        tv_start_time = inflater.findViewById(R.id.tv_start_time)
        tv_End_time = inflater.findViewById(R.id.tv_End_time)

        tv_start_time.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {

            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                isStartTimeValidation

            }


        })

        tv_End_time.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {

            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                isEndTimeValidation

            }
        })


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

    fun SetDialog_end(view: View) {
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
        val Hour_Picker = dialog.findViewById<NumberPicker>(R.id.hour_num)
        val Min_Picker = dialog.findViewById<NumberPicker>(R.id.mint_num)
        val btnApply = dialog.findViewById<Button>(R.id.btnApply)
        val hour_num = dialog.findViewById<TextView>(R.id.hours)
        val min_num = dialog.findViewById<TextView>(R.id.mint)
        val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)
        hour_num.text = "Hour"
        min_num.text = "Minute"
        var selectedHour = 1
        Hour_Picker.minValue = 1
        Hour_Picker.maxValue = 24
        Hour_Picker.wrapSelectorWheel = true
        Hour_Picker.setOnValueChangedListener { picker, oldVal, newVal ->
            selectedHour = newVal
        }
        var selectMinute = 0
        Min_Picker.minValue = 0
        Min_Picker.maxValue = 59
        Min_Picker.wrapSelectorWheel = true
        Min_Picker.setOnValueChangedListener { picker, oldVal, newVal ->
            selectMinute = newVal
        }

        btnCancel.setOnClickListener { v: View? ->
            dialog.dismiss()
        }
        btnApply.setOnClickListener { view: View? ->
            tv_End_time.text = selectedHour.toString() + ":" + selectMinute.toString()
            dialog.dismiss()
        }
        dialog.show()
    }

    fun SetDialog_start(view: View) {
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
        val Hour_Picker = dialog.findViewById<NumberPicker>(R.id.hour_num)
        val Min_Picker = dialog.findViewById<NumberPicker>(R.id.mint_num)
        val btnApply = dialog.findViewById<Button>(R.id.btnApply)
        val hour_num = dialog.findViewById<TextView>(R.id.hours)
        val min_num = dialog.findViewById<TextView>(R.id.mint)
        val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)
        hour_num.text = "Hour"
        min_num.text = "Minute"
        var selectedHour = 1
        Hour_Picker.minValue = 1
        Hour_Picker.maxValue = 24
        Hour_Picker.wrapSelectorWheel = true
        Hour_Picker.setOnValueChangedListener { picker, oldVal, newVal ->
            selectedHour = newVal
        }
        var selectMinute = 0
        Min_Picker.minValue = 0
        Min_Picker.maxValue = 59
        Min_Picker.wrapSelectorWheel = true
        Min_Picker.setOnValueChangedListener { picker, oldVal, newVal ->
            selectMinute = newVal
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        btnApply.setOnClickListener {
            tv_start_time.text = "$selectedHour:$selectMinute"
            dialog.dismiss()
        }
        dialog.show()
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                val selectedImageUri: Uri = data!!.data!!
                createGroupBinding.selectUploadLy.visibility = View.GONE
                createGroupBinding.imageUpload.visibility = View.VISIBLE
                createGroupBinding.imageUpload.setImageURI(selectedImageUri)

            }
        }
    }
}