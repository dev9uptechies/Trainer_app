package com.example

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatSpinner
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.model.Ecercise_data_list
import com.example.trainerapp.ApiClass.*
import com.example.trainerapp.PreferencesManager
import com.example.trainerapp.R
import com.example.trainerapp.Utils
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar


class New_Program_Activity : AppCompatActivity(), OnItemClickListener.OnItemClickCallback {
    lateinit var adapter: ProgramAdapter
    lateinit var adapter1: Exercise_select_Adapter
    lateinit var apiInterface: APIInterface
    var age = ArrayList<String>()
    private lateinit var exercise_list: ArrayList<Ecercise_data_list>
    lateinit var apiClient: APIClient
    var unitArray = ArrayList<String>()
    lateinit var preferenceManager: PreferencesManager
    private lateinit var id: ArrayList<Int>
    lateinit var etEnterGoal: AppCompatSpinner
    lateinit var spselect_lesson: AppCompatSpinner
    lateinit var etSelectTestDate: AppCompatEditText
    lateinit var reset: ImageView
    lateinit var back: ImageView
    lateinit var edt_program_name: EditText
    lateinit var edt_time: EditText
    lateinit var card_save: CardView
    lateinit var card_create_exercise: CardView
    lateinit var progres_bar: ProgressBar
    lateinit var exercise_recycler: RecyclerView
    lateinit var exercise_select_recycler: RecyclerView
    lateinit var error_program: TextView
    lateinit var error_date: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_program)
        preferenceManager = PreferencesManager(this)
        id = ArrayList()
        exercise_list = ArrayList()
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        etEnterGoal = findViewById(R.id.etEnterGoal)
        spselect_lesson = findViewById(R.id.spselect_lesson)

        age = ArrayList()
        GetGoal()

        etSelectTestDate.setOnClickListener {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                Utils.selectDate(this, etSelectTestDate)
//            }

            showDateRangePickerDialog(
                etSelectTestDate.context,
            ) { start ->


                etSelectTestDate.setText(start.toString())
            }

        }

        initView()

        reset.setOnClickListener {
            Utils.setSpinnerAdapter(applicationContext, unitArray, spselect_lesson)
            Utils.setSpinnerAdapter(applicationContext, age, etEnterGoal)
            edt_program_name.setText("")
            edt_time.setText("")
            etSelectTestDate.setText("")
        }

        card_save.setOnClickListener {
            if (isValidate) {
                progres_bar.visibility = View.VISIBLE
                for (i in 0 until exercise_list.size) {
                    id.add(exercise_list[i].id.toInt())
                }

                val str = arrayOfNulls<Int>(id.size)
                val array = JsonArray()

                for (i in 0 until id.size) {
                    str[i] = id.get(i)
                    array.add(id.get(i))
                }
                val jsonObject = JsonObject()
                jsonObject.addProperty("name", edt_program_name.text.toString())
                jsonObject.addProperty("goal_id", "1")
                jsonObject.addProperty("time", edt_time.text.toString())
                jsonObject.addProperty("section_id", "1")
                jsonObject.add("exercise_ids", array)
                jsonObject.addProperty("date", etSelectTestDate.text.toString())

                apiInterface.CreateProgram(
                    jsonObject
                )?.enqueue(object : Callback<CycleData?> {
                    override fun onResponse(
                        call: Call<CycleData?>?,
                        response: Response<CycleData?>
                    ) {
                        Log.d("TAG", response.code().toString() + "")
                        val resource: CycleData? = response.body()
                        val Success: Boolean = resource?.status!!
                        val Message: String = resource.message!!
                        preferenceManager.setexercisedata(false)
                        finish()
                        startActivity(
                            Intent(
                                this@New_Program_Activity,
                                New_Program_Activity::class.java
                            )
                        )
                    }

                    override fun onFailure(call: Call<CycleData?>, t: Throwable?) {
                        progres_bar.visibility = View.GONE
                        Toast.makeText(
                            this@New_Program_Activity,
                            "" + t!!.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        call.cancel()
                    }
                })
            }
        }

        card_create_exercise.setOnClickListener {
            startActivity(Intent(this, Select_ExerciseActivity::class.java))
        }

        back.setOnClickListener {
            finish()
        }
    }




    fun showDateRangePickerDialog(
        context: Context,
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


    private fun initrecyclerview(user: ArrayList<ProgramListData.testData>) {

        exercise_recycler.layoutManager = LinearLayoutManager(this)
        adapter =
            ProgramAdapter(user, this, this)
        exercise_recycler.adapter = adapter
    }

    private val isValidate: Boolean
        get() {
            var program_name = edt_program_name.text.toString()
            var date = etSelectTestDate.text.toString()
            var time = edt_time.text.toString()
            if (program_name == "") {
                error_program.visibility = View.VISIBLE
                return false
            } else {
                error_program.visibility = View.GONE
            }

            if (date == "") {
                error_date.visibility = View.VISIBLE
                return false
            } else {
                error_date.visibility = View.GONE
            }

            return true
        }

    private fun initView() {
        progres_bar.visibility = View.VISIBLE
        GetSection()
        apiInterface.GetProgam()?.enqueue(object : Callback<ProgramListData?> {
            override fun onResponse(
                call: Call<ProgramListData?>?,
                response: Response<ProgramListData?>
            ) {
                Log.d("TAG", response.code().toString() + "")
                val resource: ProgramListData? = response.body()
                val Success: Boolean = resource?.status!!
                val Message: String = resource.message!!
                progres_bar.visibility = View.GONE
                if (Success == true) {
                    initrecyclerview(resource!!.data!!)
                } else {
                    Toast.makeText(this@New_Program_Activity, "" + Message, Toast.LENGTH_SHORT)
                        .show()
                }

            }

            override fun onFailure(call: Call<ProgramListData?>, t: Throwable?) {
                progres_bar.visibility = View.GONE
                Toast.makeText(this@New_Program_Activity, "" + t!!.message, Toast.LENGTH_SHORT)
                    .show()
                call.cancel()
            }
        })

    }

    private fun GetGoal() {
        progres_bar.visibility = View.VISIBLE
        apiInterface.getGoal()?.enqueue(object : Callback<CycleData?> {
            override fun onResponse(
                call: Call<CycleData?>?,
                response: Response<CycleData?>
            ) {
                Log.d("TAG", response.code().toString() + "")
                val resource: CycleData? = response.body()
                val Success: Boolean = resource?.status!!
                val Message: String = resource.message!!
                if (resource.data!!.size != 0) {
                    for (i in 0 until resource.data!!.size) {
                        age.add(resource.data!![i].name!!)

                    }

                    val spinnerArrayAdapter = ArrayAdapter(
                        this@New_Program_Activity,
                        android.R.layout.simple_spinner_item,
                        age
                    )
                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    etEnterGoal.adapter = spinnerArrayAdapter
                    Utils.setSpinnerAdapter(applicationContext, age, etEnterGoal)
                }

            }

            override fun onFailure(call: Call<CycleData?>, t: Throwable?) {
                progres_bar.visibility = View.GONE
                Toast.makeText(
                    this@New_Program_Activity,
                    "" + t!!.message,
                    Toast.LENGTH_SHORT
                )
                    .show()
                call.cancel()
            }
        })
    }

    private fun GetSection() {
        progres_bar.visibility = View.VISIBLE
        apiInterface.GetSection()?.enqueue(object : Callback<CategoriesData?> {
            override fun onResponse(
                call: Call<CategoriesData?>?,
                response: Response<CategoriesData?>
            ) {
                Log.d("TAG", response.code().toString() + "")
                val resource: CategoriesData? = response.body()
                val Success: Boolean = resource?.status!!
                val Message: String = resource.message!!
                if (resource.data!!.size != 0) {
                    for (i in 0 until resource.data!!.size) {
                        unitArray.add(resource.data!![i].name!!)
                    }
                    Utils.setSpinnerAdapter(applicationContext, unitArray, spselect_lesson)
                }

            }

            override fun onFailure(call: Call<CategoriesData?>, t: Throwable?) {
                progres_bar.visibility = View.GONE
                Toast.makeText(
                    this@New_Program_Activity,
                    "" + t!!.message,
                    Toast.LENGTH_SHORT
                )
                    .show()
                call.cancel()
            }
        })
    }

    override fun onItemClicked(view: View, position: Int, type: Long, string: String) {
        if (string == "Edit") {
            startActivity(Intent(this, EditProgramActivity::class.java))
        } else if (string == "fav") {
            progres_bar.visibility = View.VISIBLE
            val id: MultipartBody.Part =
                MultipartBody.Part.createFormData("id", type.toInt()!!.toString())
            apiInterface.Favourite_Program(id)?.enqueue(object : Callback<RegisterData?> {
                override fun onResponse(
                    call: Call<RegisterData?>?,
                    response: Response<RegisterData?>
                ) {
                    Log.d("TAG", response.code().toString() + "")
                    val resource: RegisterData? = response.body()
                    val Success: Boolean = resource?.status!!
                    val Message: String = resource.message!!
                    if (Success) {
                        progres_bar.visibility = View.GONE
                        Toast.makeText(this@New_Program_Activity, "" + Message, Toast.LENGTH_SHORT)
                            .show()
                        finish()
                        startActivity(intent)
                    } else {
                        progres_bar.visibility = View.GONE
                        Toast.makeText(this@New_Program_Activity, "" + Message, Toast.LENGTH_SHORT)
                            .show()
                    }


                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable?) {
                    progres_bar.visibility = View.GONE
                    Toast.makeText(this@New_Program_Activity, "" + t!!.message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }
            })
        } else if (string == "unfav") {
            progres_bar.visibility = View.VISIBLE
            val id: MultipartBody.Part =
                MultipartBody.Part.createFormData("id", type.toInt()!!.toString())
            apiInterface.DeleteFavourite_Program(type.toInt())
                ?.enqueue(object : Callback<RegisterData?> {
                    override fun onResponse(
                        call: Call<RegisterData?>?,
                        response: Response<RegisterData?>
                    ) {
                        Log.d("TAG", response.code().toString() + "")
                        val resource: RegisterData? = response.body()
                        val Success: Boolean = resource?.status!!
                        val Message: String = resource.message!!
                        if (Success) {
                            progres_bar.visibility = View.GONE
                            Toast.makeText(
                                this@New_Program_Activity,
                                "" + Message,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            finish()
                            startActivity(intent)
                        } else {
                            progres_bar.visibility = View.GONE
                            Toast.makeText(
                                this@New_Program_Activity,
                                "" + Message,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }


                    }

                    override fun onFailure(call: Call<RegisterData?>, t: Throwable?) {
                        progres_bar.visibility = View.GONE
                        Toast.makeText(
                            this@New_Program_Activity,
                            "" + t!!.message,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                })
        } else {

            var builder: AlertDialog.Builder
            builder = AlertDialog.Builder(this)
            builder.setMessage("Are you sure you want to delete Program?").setTitle("Success")

            builder.setMessage("Are you sure you want to delete Program?")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialog, id ->
                    progres_bar.visibility = View.VISIBLE
                    apiInterface.DeleteProgram(type.toInt())
                        ?.enqueue(object : Callback<RegisterData?> {
                            override fun onResponse(
                                call: Call<RegisterData?>?,
                                response: Response<RegisterData?>
                            ) {
                                Log.d("TAG", response.code().toString() + "")
                                val resource: RegisterData? = response.body()
                                val Success: Boolean = resource?.status!!
                                val Message: String = resource.message!!
                                progres_bar.visibility = View.GONE
                                Toast.makeText(
                                    this@New_Program_Activity,
                                    "" + Message,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                finish()
                                startActivity(intent)

                            }

                            override fun onFailure(call: Call<RegisterData?>, t: Throwable?) {
                                progres_bar.visibility = View.GONE
                                Toast.makeText(
                                    this@New_Program_Activity,
                                    "" + t!!.message,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                call.cancel()
                            }
                        })
                }
                .setNegativeButton(
                    "No"
                ) { dialog, id -> //  Action for 'NO' Button
                    dialog.cancel()
                }

            val alert = builder.create()
            alert.setTitle("Success")
            alert.show()

        }

    }

    override fun onResume() {
        super.onResume()
        if (preferenceManager.getexercisedata()) {
            exercise_list = getObject(this, "Exercise") as ArrayList<Ecercise_data_list>
            edt_time.setText(exercise_list[0].time)
            exercise_select_recycler.visibility = View.VISIBLE
            exercise_select_recycler.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
//            adapter1 =
//                Exercise_select_Adapter(exercise_list, this)
//            exercise_select_recycler.adapter = adapter1

        }

    }

    fun getObject(c: Context, key: String): List<Ecercise_data_list> {
        val appSharedPrefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(
            c.applicationContext
        )
        val json = appSharedPrefs.getString(key, "")
        val gson = Gson()
        val type = object : TypeToken<List<Ecercise_data_list>>() {}.type
        val arrayList: List<Ecercise_data_list> =
            gson.fromJson<List<Ecercise_data_list>>(json, type)
        return arrayList
    }

}