package com.example.trainerapp.program_section

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.Exercise_select_Adapter1
import com.example.OnItemClickListener
import com.example.ProgramAdapter
import com.example.Select_ExerciseActivity
import com.example.model.Ecercise_data_list
import com.example.model.Ecercise_list_data
import com.example.model.SelectedValue
import com.example.model.newClass.ProgramBody
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.CategoriesData
import com.example.trainerapp.ApiClass.CycleData
import com.example.trainerapp.ApiClass.ExcerciseData
import com.example.trainerapp.ApiClass.ProgramListData
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.PreferencesManager
import com.example.trainerapp.R
import com.example.trainerapp.TestListData
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.ActivityNewProgramBinding
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class New_Program_Activity : AppCompatActivity(), OnItemClickListener.OnItemClickCallback {
    lateinit var newProgramBinding: ActivityNewProgramBinding
    lateinit var adapter: ProgramAdapter
    lateinit var adapter1: Exercise_select_Adapter1
    lateinit var apiInterface: APIInterface
    var age = ArrayList<String>()
    private lateinit var exercise_list: ArrayList<Ecercise_data_list>
    private lateinit var exercise_list1: ArrayList<ExcerciseData.Exercise>
    private lateinit var exerciseDataList: ArrayList<Ecercise_list_data>
    private lateinit var exerciseDataList1: ArrayList<ExcerciseData.Exercise>
    private lateinit var exerciseData: ArrayList<ExcerciseData.Exercise>
    lateinit var apiClient: APIClient
    var unitArray = ArrayList<String>()
    lateinit var preferenceManager: PreferencesManager
    private lateinit var id: ArrayList<Int>
    lateinit var etEnterGoal: AppCompatSpinner
    lateinit var spselect_lesson: AppCompatSpinner

    lateinit var programData: MutableList<ProgramListData.testData>
    lateinit var goalData: MutableList<TestListData.testData>
    lateinit var sectionData: MutableList<TestListData.testData>
    var Goal = ArrayList<String>()
    var section = ArrayList<String>()
    var goalId = SelectedValue(null)
    var sectionId = SelectedValue(null)
    var programId: Int? = null
    var excId: MutableList<Int> = mutableListOf()

    var typeData = "create"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        newProgramBinding = ActivityNewProgramBinding.inflate(layoutInflater)
        setContentView(newProgramBinding.root)

        initView()
        getData()

        newProgramBinding.edtSection.setOnClickListener {
            showPopup(it, sectionData, newProgramBinding.edtSection, section, sectionId)
        }

        newProgramBinding.edtGoal.setOnClickListener {
            showPopup(it, goalData, newProgramBinding.edtGoal, Goal, goalId)
        }

        newProgramBinding.etSelectTestDate.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Utils.selectDate(this, newProgramBinding.etSelectTestDate)
            }
        }

        newProgramBinding.reset.setOnClickListener {
            setRefresh()
            //refreshData()
        }

        newProgramBinding.cardSave.setOnClickListener {
            saveData()
        }

        newProgramBinding.cardCreateExercise.setOnClickListener {
            //startActivity(Intent(this, Select_ExerciseActivity::class.java))
            when (typeData) {
                "create" -> {
                    startActivity(Intent(this, Select_ExerciseActivity::class.java).apply {
                        putExtra("type", "create")
                    })
                }

                "edit" -> {
                    Log.d("Program Id:", "$excId")
                    val arrayList: ArrayList<Int> = ArrayList(excId)
                    startActivity(Intent(this, Select_ExerciseActivity::class.java).apply {
                        putIntegerArrayListExtra("programId", arrayList)
                        putExtra("type", "edit")
                    })
                }
            }
        }

        newProgramBinding.back.setOnClickListener {
            finish()
        }
    }

    private fun showPopup(
        anchorView: View?,
        data: MutableList<TestListData.testData>,
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
                this@New_Program_Activity,
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
            selectedValue.id = data.filter { it.name == selectedItem }.first().id!!
            println("Selected item: $selectedItem")
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

    private fun getGoalData() {
        goalData = mutableListOf()
        goalData.clear()
        apiInterface.GetGoal()?.enqueue(object : Callback<TestListData> {
            override fun onResponse(call: Call<TestListData>, response: Response<TestListData>) {
                val code = response.code()
                if (code == 200) {
                    if (response.isSuccessful) {
                        val data = response.body()!!.data
                        if (data != null) {
                            goalData.addAll(data.toMutableList())

                            for (i in goalData) {
                                Goal.add(i.name!!)
                            }
                        }
                    }
                } else if (response.code() == 403) {
                    Utils.setUnAuthDialog(this@New_Program_Activity)
//                    val message = response.message()
//                    Toast.makeText(
//                        this@New_Program_Activity,
//                        "" + message,
//                        Toast.LENGTH_SHORT
//                    )
//                        .show()
//                    call.cancel()
//                    startActivity(
//                        Intent(
//                            this@New_Program_Activity,
//                            SignInActivity::class.java
//                        )
//                    )
//                    finish()
                } else {
                    val message = response.message()
                    Toast.makeText(this@New_Program_Activity, "" + message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<TestListData>, t: Throwable) {
                Log.d("TAG Goal", t.message.toString() + "")
            }

        })
    }

    private fun getSection() {
        sectionData = mutableListOf()
        sectionData.clear()
        apiInterface.GetSection1()?.enqueue(object : Callback<TestListData?> {
            override fun onResponse(
                call: Call<TestListData?>,
                response: Response<TestListData?>
            ) {
                Log.d("TAG", response.code().toString() + "")
                val code = response.code()
                if (code == 200) {
                    val resource = response.body()
                    val Success: Boolean = resource?.status!!
                    val Message: String = resource.message!!
                    val Data = resource.data!!
                    if (Success == true) {
                        //section.add("Select Section")
                        if (Data != null) {
                            sectionData.addAll(Data)
                            for (i in 0 until Data.size) {
                                section.add(Data[i].name!!)
                            }
                        }
//                    setSpinner(age)
//                    Utils.setSpinnerAdapter(
//                        applicationContext,
//                        section,
//                        createExerciseBinding.spSection,
//                        "Select Section"
//                    )
                    }
                } else if (response.code() == 403) {
                    Utils.setUnAuthDialog(this@New_Program_Activity)
//                    val message = response.message()
//                    Toast.makeText(
//                        this@New_Program_Activity,
//                        "" + message,
//                        Toast.LENGTH_SHORT
//                    )
//                        .show()
//                    call.cancel()
//                    startActivity(
//                        Intent(
//                            this@New_Program_Activity,
//                            SignInActivity::class.java
//                        )
//                    )
//                    finish()
                } else {
                    val message = response.message()
                    Toast.makeText(this@New_Program_Activity, "" + message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<TestListData?>, t: Throwable) {
                //newProgramBinding.progresBar.visibility = View.GONE
                Toast.makeText(this@New_Program_Activity, "" + t.message, Toast.LENGTH_SHORT)
                    .show()
                call.cancel()
            }
        })
    }

    private fun saveData() {
        if (isValidate) {
            Log.d("Datatype :", typeData)
//            newProgramBinding.progresBar.visibility = View.VISIBLE
//            for (i in 0 until exercise_list.size) {
//                Log.d("List :-", "${exercise_list[i].id}")
//                id.add(exercise_list[i].id.toInt())
//            }
//
//            val str = arrayOfNulls<Int>(id.size)
//            val array = JsonArray()
//
//            for (i in 0 until id.size) {
//                str[i] = id[i]
//                array.add(id[i])
//                Log.d("List :- Array :-", "${id[i]} \t ${array[i]}")
//            }

            when (typeData) {
                "create" -> {
                    for (i in 0 until exerciseDataList1.size) {
                        Log.d("List :-", "${exerciseDataList1[i].id}")
                        id.add(exerciseDataList1[i].id!!.toInt())
                    }

                    val str = arrayOfNulls<Int>(id.size)
                    val array = JsonArray()

                    for (i in 0 until id.size) {
                        str[i] = id[i]
                        array.add(id[i])
                        Log.d("List :- Array :-", "${id[i]} \t ${array[i]}")
                    }
                    val jsonObject = JsonObject()
                    jsonObject.addProperty("name", newProgramBinding.edtProgramName.text.toString())
                    jsonObject.addProperty("goal_id", goalId.id.toString())
                    jsonObject.addProperty("time", newProgramBinding.edtTime.text.toString())
                    jsonObject.addProperty("section_id", sectionId.id.toString())
                    jsonObject.add("exercise_ids", array)
                    jsonObject.addProperty(
                        "date",
                        newProgramBinding.etSelectTestDate.text.toString()
                    )

                    apiInterface.CreateProgram(
                        jsonObject
                    )?.enqueue(object : Callback<CycleData?> {
                        override fun onResponse(
                            call: Call<CycleData?>,
                            response: Response<CycleData?>
                        ) {
                            Log.d("TAG", response.code().toString() + "")
                            val code = response.code()
                            if (code == 200) {
                                val resource: CycleData? = response.body()
                                val Success: Boolean = resource?.status!!
                                val Message: String = resource.message!!
                                newProgramBinding.progresBar.visibility = View.GONE
                                preferenceManager.setexercisedata(false)
                                Toast.makeText(
                                    this@New_Program_Activity,
                                    Message,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                setRefresh()
                                refreshData()
//                        finish()
//                        startActivity(
//                            Intent(
//                                this@New_Program_Activity,
//                                New_Program_Activity::class.java
//                            )
//                        )
                            } else if (response.code() == 403) {
                                Utils.setUnAuthDialog(this@New_Program_Activity)
//                                val message = response.message()
//                                Toast.makeText(
//                                    this@New_Program_Activity,
//                                    "" + message,
//                                    Toast.LENGTH_SHORT
//                                )
//                                    .show()
//                                call.cancel()
//                                startActivity(
//                                    Intent(
//                                        this@New_Program_Activity,
//                                        SignInActivity::class.java
//                                    )
//                                )
//                                finish()
                            } else {
                                val message = response.message()
                                Toast.makeText(
                                    this@New_Program_Activity,
                                    "" + message,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                call.cancel()
                            }
                        }

                        override fun onFailure(call: Call<CycleData?>, t: Throwable) {
                            newProgramBinding.progresBar.visibility = View.GONE
                            Toast.makeText(
                                this@New_Program_Activity,
                                "" + t.message,
                                Toast.LENGTH_SHORT
                            ).show()
                            call.cancel()
                        }
                    })
                }

                "edit" -> {
                    for (i in 0 until exerciseDataList1.size) {
                        Log.d("List :-", "${exerciseDataList1[i].id}")
                        id.add(exerciseDataList1[i].id!!.toInt())
                    }

                    val str = arrayOfNulls<Int>(id.size)
                    val array = JsonArray()
                    val array1: MutableList<Int> = mutableListOf()
                    array1.clear()

                    for (i in 0 until id.size) {
                        str[i] = id[i]
                        array1.add(i, id[i])
                    }

                    Log.d("Tag Id :-", "${exerciseDataList1[0].id}")
                    val jsonObject = JsonObject()
                    jsonObject.addProperty("id", exerciseDataList1[0].id.toString())
                    jsonObject.addProperty("name", newProgramBinding.edtProgramName.text.toString())
                    jsonObject.addProperty("goal_id", goalId.id.toString())
                    jsonObject.addProperty("time", newProgramBinding.edtTime.text.toString())
                    jsonObject.addProperty("section_id", sectionId.id.toString())
                    jsonObject.add("exercise_ids", array)
                    jsonObject.addProperty(
                        "date",
                        newProgramBinding.etSelectTestDate.text.toString()
                    )

                    Log.d(
                        "Database :-",
                        "${exerciseDataList1[0].id.toString()} \n ${newProgramBinding.edtProgramName.text} \n ${goalId.id.toString()} \n" +
                                "${newProgramBinding.edtTime.text} \n ${sectionId.id.toString()} \n ${array1}"
                    )

                    apiInterface.UpdateProgram(
                        ProgramBody(
                            id = programId!!,
                            name = newProgramBinding.edtProgramName.text.toString(),
                            goal_id = goalId.id!!,
                            time = newProgramBinding.edtTime.text.toString(),
                            section_id = sectionId.id!!,
                            data = array1
                        )
                    ).enqueue(object : Callback<CycleData?> {
                        override fun onResponse(
                            call: Call<CycleData?>,
                            response: Response<CycleData?>
                        ) {
                            Log.d(
                                "TAG",
                                response.code()
                                    .toString() + "" + response.body()!!.status + "" + response.body()!!.message
                            )
                            val code = response.code()
                            if (code == 200) {
                                val resource: CycleData? = response.body()
                                val Success: Boolean = resource?.status!!
                                val Message: String = resource.message!!
                                newProgramBinding.progresBar.visibility = View.GONE
                                preferenceManager.setexercisedata(false)
                                Toast.makeText(
                                    this@New_Program_Activity,
                                    Message,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                setRefresh()
                                refreshData()
//                        finish()
//                        startActivity(
//                            Intent(
//                                this@New_Program_Activity,
//                                New_Program_Activity::class.java
//                            )
//                        )
                            } else if (response.code() == 403) {
                                Utils.setUnAuthDialog(this@New_Program_Activity)
//                                val message = response.message()
//                                Toast.makeText(
//                                    this@New_Program_Activity,
//                                    "" + message,
//                                    Toast.LENGTH_SHORT
//                                )
//                                    .show()
//                                call.cancel()
//                                startActivity(
//                                    Intent(
//                                        this@New_Program_Activity,
//                                        SignInActivity::class.java
//                                    )
//                                )
//                                finish()
                            } else {
                                val message = response.message()
                                Toast.makeText(
                                    this@New_Program_Activity,
                                    "" + message,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                call.cancel()
                            }
                        }

                        override fun onFailure(call: Call<CycleData?>, t: Throwable) {
                            newProgramBinding.progresBar.visibility = View.GONE
                            Toast.makeText(
                                this@New_Program_Activity,
                                "" + t.message,
                                Toast.LENGTH_SHORT
                            ).show()
                            call.cancel()
                        }
                    })
                }
            }
        }
    }

    private fun getData() {
        newProgramBinding.progresBar.visibility = View.VISIBLE
        getGoalData()
        getSection()
        GetProgramData()
    }

    private fun GetProgramData() {
        newProgramBinding.progresBar.visibility = View.VISIBLE
        programData.clear()
        apiInterface.GetProgam()?.enqueue(object : Callback<ProgramListData?> {
            override fun onResponse(
                call: Call<ProgramListData?>,
                response: Response<ProgramListData?>
            ) {
                Log.d("TAG", response.code().toString() + "")
                val code = response.code()
                if (code == 200) {
                    val resource: ProgramListData? = response.body()
                    val Success: Boolean = resource?.status!!
                    val Message: String = resource.message!!
                    newProgramBinding.progresBar.visibility = View.GONE
                    Log.d("TAG", resource.data.toString())
                    if (Success) {
                        try {
                            if (resource.data == null) {
                                initRecyclerview(arrayListOf())
                            } else {
                                programData.addAll(resource.data!!)
                                initRecyclerview(resource.data!!)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        Toast.makeText(this@New_Program_Activity, "" + Message, Toast.LENGTH_SHORT)
                            .show()
                    }
                } else if (response.code() == 403) {
                    Utils.setUnAuthDialog(this@New_Program_Activity)
//                    val message = response.message()
//                    Toast.makeText(
//                        this@New_Program_Activity,
//                        "" + message,
//                        Toast.LENGTH_SHORT
//                    )
//                        .show()
//                    call.cancel()
//                    startActivity(
//                        Intent(
//                            this@New_Program_Activity,
//                            SignInActivity::class.java
//                        )
//                    )
//                    finish()
                } else {
                    val message = response.message()
                    Toast.makeText(this@New_Program_Activity, "" + message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<ProgramListData?>, t: Throwable) {
                newProgramBinding.progresBar.visibility = View.GONE
                Toast.makeText(this@New_Program_Activity, "" + t.message, Toast.LENGTH_SHORT)
                    .show()
                call.cancel()
            }
        })
    }

    private fun refreshData() {
        GetProgramData()
    }

    private fun setRefresh() {
//        Utils.setSpinnerAdapter(applicationContext, unitArray, spselect_lesson)
//        Utils.setSpinnerAdapter(applicationContext, age, etEnterGoal)
        newProgramBinding.edtProgramName.setText("")
        newProgramBinding.edtTime.setText("")
        newProgramBinding.etSelectTestDate.setText("")
        newProgramBinding.edtGoal.setText("")
        newProgramBinding.edtSection.setText("")
        exercise_list.clear()
        exercise_list1.clear()
        exerciseDataList.clear()
        exerciseDataList1.clear()
        excId.clear()
        adapter1 =
            Exercise_select_Adapter1(exerciseDataList1, this)
        newProgramBinding.exerciseSelectRecycler.adapter = adapter1
        preferenceManager.setexercisedata(false)
        refreshData()
    }


    private fun initRecyclerview(user: ArrayList<ProgramListData.testData>) {

        newProgramBinding.exerciseRecycler.layoutManager = LinearLayoutManager(this)
        adapter =
            ProgramAdapter(user, this, this)
        newProgramBinding.exerciseRecycler.adapter = adapter
    }

    private val isValidate: Boolean
        get() {
            val program_name = newProgramBinding.edtProgramName.text.toString()
            val date = newProgramBinding.etSelectTestDate.text.toString()
            val time = newProgramBinding.edtTime.text.toString()
            val goal = newProgramBinding.edtGoal.text.toString()
            val section = newProgramBinding.edtSection.text.toString()
            if (program_name == "") {
                newProgramBinding.errorProgram.visibility = View.VISIBLE
                return false
            } else {
                newProgramBinding.errorProgram.visibility = View.GONE
            }

            if (time == "") {
                newProgramBinding.errorTime.visibility = View.VISIBLE
                return false
            } else {
                newProgramBinding.errorTime.visibility = View.GONE
            }

            if (goal == "") {
                newProgramBinding.errorGoal.visibility = View.VISIBLE
                return false
            } else {
                newProgramBinding.errorGoal.visibility = View.GONE
            }

            if (section == "") {
                newProgramBinding.errorSection.visibility = View.VISIBLE
                return false
            } else {
                newProgramBinding.errorSection.visibility = View.GONE
            }

            if (date == "") {
                newProgramBinding.errorDate.visibility = View.VISIBLE
                return false
            } else {
                newProgramBinding.errorDate.visibility = View.GONE
            }

            return true
        }

    private fun initView() {
        preferenceManager = PreferencesManager(this)
        id = ArrayList()
        exercise_list = ArrayList()
        exercise_list1 = ArrayList()
        exerciseDataList = ArrayList()
        exerciseDataList1 = ArrayList()
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        etEnterGoal = findViewById(R.id.etEnterGoal)
        spselect_lesson = findViewById(R.id.spselect_lesson)
        programData = mutableListOf()
        exerciseData = ArrayList()
        age = ArrayList()
        excId.clear()
        getExerciseData()
    }

    private fun getExerciseData() {
        exerciseData.clear()
        apiInterface.GetExercise()?.enqueue(object : Callback<ExcerciseData?> {
            override fun onResponse(
                call: Call<ExcerciseData?>,
                response: Response<ExcerciseData?>
            ) {
                Log.d("TAG", response.code().toString() + "")
                val code = response.code()
                if (code == 200) {
                    val resource: ExcerciseData? = response.body()
                    val Success: Boolean = resource?.status!!
                    val Message: String = resource.message!!

                    if (Success == true) {
                        if (resource.data!! != null) {
                            exerciseData = resource.data!!
                        }
                    }
                } else if (response.code() == 403) {
                    Utils.setUnAuthDialog(this@New_Program_Activity)
//                    val message = response.message()
//                    Toast.makeText(
//                        this@New_Program_Activity,
//                        "" + message,
//                        Toast.LENGTH_SHORT
//                    )
//                        .show()
//                    call.cancel()
//                    startActivity(
//                        Intent(
//                            this@New_Program_Activity,
//                            SignInActivity::class.java
//                        )
//                    )
//                    finish()
                } else {
                    val message = response.message()
                    Toast.makeText(this@New_Program_Activity, "" + message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<ExcerciseData?>, t: Throwable) {
                Toast.makeText(this@New_Program_Activity, "" + t.message, Toast.LENGTH_SHORT)
                    .show()
                call.cancel()
            }
        })
    }

    private fun GetGoal() {
        newProgramBinding.progresBar.visibility = View.VISIBLE
        apiInterface.getGoal()?.enqueue(object : Callback<CycleData?> {
            override fun onResponse(call: Call<CycleData?>, response: Response<CycleData?>) {
                Log.d("TAG", response.code().toString() + "")
                val code = response.code()
                if (code == 200) {
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
                } else if (response.code() == 403) {
                    Utils.setUnAuthDialog(this@New_Program_Activity)
//                    val message = response.message()
//                    Toast.makeText(
//                        this@New_Program_Activity,
//                        "" + message,
//                        Toast.LENGTH_SHORT
//                    )
//                        .show()
//                    call.cancel()
//                    startActivity(
//                        Intent(
//                            this@New_Program_Activity,
//                            SignInActivity::class.java
//                        )
//                    )
//                    finish()
                } else {
                    val message = response.message()
                    Toast.makeText(this@New_Program_Activity, "" + message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<CycleData?>, t: Throwable) {
                newProgramBinding.progresBar.visibility = View.GONE
                Toast.makeText(
                    this@New_Program_Activity,
                    "" + t.message,
                    Toast.LENGTH_SHORT
                )
                    .show()
                call.cancel()
            }
        })
    }

    private fun GetSection() {
        newProgramBinding.progresBar.visibility = View.VISIBLE
        apiInterface.GetSection()?.enqueue(object : Callback<CategoriesData?> {
            override fun onResponse(
                call: Call<CategoriesData?>,
                response: Response<CategoriesData?>
            ) {
                Log.d("TAG", response.code().toString() + "")
                val code = response.code()
                if (code == 200) {
                    val resource: CategoriesData? = response.body()
                    val Success: Boolean = resource?.status!!
                    val Message: String = resource.message!!
                    if (resource.data!!.size != 0) {
                        for (i in 0 until resource.data!!.size) {
                            unitArray.add(resource.data!![i].name!!)
                        }
                        Utils.setSpinnerAdapter(applicationContext, unitArray, spselect_lesson)
                    }
                } else if (response.code() == 403) {
                    Utils.setUnAuthDialog(this@New_Program_Activity)
//                    val message = response.message()
//                    Toast.makeText(
//                        this@New_Program_Activity,
//                        "" + message,
//                        Toast.LENGTH_SHORT
//                    )
//                        .show()
//                    call.cancel()
//                    startActivity(
//                        Intent(
//                            this@New_Program_Activity,
//                            SignInActivity::class.java
//                        )
//                    )
//                    finish()
                } else {
                    val message = response.message()
                    Toast.makeText(this@New_Program_Activity, "" + message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<CategoriesData?>, t: Throwable) {
                newProgramBinding.progresBar.visibility = View.GONE
                Toast.makeText(
                    this@New_Program_Activity,
                    "" + t.message,
                    Toast.LENGTH_SHORT
                )
                    .show()
                call.cancel()
            }
        })
    }

    override fun onItemClicked(view: View, position: Int, type: Long, string: String) {
        if (string == "Edit") {
            typeData = "edit"
            setProgramData(position, type)
//            startActivity(Intent(this, EditProgramActivity::class.java))
        } else if (string == "fav") {
            newProgramBinding.progresBar.visibility = View.VISIBLE
            val id: MultipartBody.Part =
                MultipartBody.Part.createFormData("id", type.toInt().toString())
            apiInterface.Favourite_Program(id)?.enqueue(object : Callback<RegisterData?> {
                override fun onResponse(
                    call: Call<RegisterData?>,
                    response: Response<RegisterData?>
                ) {
                    Log.d("TAG", response.code().toString() + "")
                    val code = response.code()
                    if (code == 200) {
                        val resource: RegisterData? = response.body()
                        val Success: Boolean = resource?.status!!
                        val Message: String = resource.message!!
                        if (Success) {
                            newProgramBinding.progresBar.visibility = View.GONE
                            getData()
//                        Toast.makeText(this@New_Program_Activity, "" + Message, Toast.LENGTH_SHORT)
//                            .show()
//                        finish()
//                        startActivity(intent)
                        } else {
                            newProgramBinding.progresBar.visibility = View.GONE
                            Toast.makeText(
                                this@New_Program_Activity,
                                "" + Message,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }

                    } else if (response.code() == 403) {
                        Utils.setUnAuthDialog(this@New_Program_Activity)
//                        val message = response.message()
//                        Toast.makeText(
//                            this@New_Program_Activity,
//                            "" + message,
//                            Toast.LENGTH_SHORT
//                        )
//                            .show()
//                        call.cancel()
//                        startActivity(
//                            Intent(
//                                this@New_Program_Activity,
//                                SignInActivity::class.java
//                            )
//                        )
//                        finish()
                    } else {
                        val message = response.message()
                        Toast.makeText(this@New_Program_Activity, "" + message, Toast.LENGTH_SHORT)
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    newProgramBinding.progresBar.visibility = View.GONE
                    Toast.makeText(this@New_Program_Activity, "" + t.message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }
            })
        } else if (string == "unfav") {
            newProgramBinding.progresBar.visibility = View.VISIBLE
            val id: MultipartBody.Part =
                MultipartBody.Part.createFormData("id", type.toInt().toString())
            apiInterface.DeleteFavourite_Program(type.toInt())
                ?.enqueue(object : Callback<RegisterData?> {
                    override fun onResponse(
                        call: Call<RegisterData?>,
                        response: Response<RegisterData?>
                    ) {
                        Log.d("TAG", response.code().toString() + "")
                        val code = response.code()
                        if (code == 200) {
                            val resource: RegisterData? = response.body()
                            val Success: Boolean = resource?.status!!
                            val Message: String = resource.message!!
                            if (Success) {
                                newProgramBinding.progresBar.visibility = View.GONE
                                getData()
//                            Toast.makeText(
//                                this@New_Program_Activity,
//                                "" + Message,
//                                Toast.LENGTH_SHORT
//                            )
//                                .show()
//                            finish()
//                            startActivity(intent)
                            } else {
                                newProgramBinding.progresBar.visibility = View.GONE
                                Toast.makeText(
                                    this@New_Program_Activity,
                                    "" + Message,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        } else if (response.code() == 403) {
                            Utils.setUnAuthDialog(this@New_Program_Activity)
//                            val message = response.message()
//                            Toast.makeText(
//                                this@New_Program_Activity,
//                                "" + message,
//                                Toast.LENGTH_SHORT
//                            )
//                                .show()
//                            call.cancel()
//                            startActivity(
//                                Intent(
//                                    this@New_Program_Activity,
//                                    SignInActivity::class.java
//                                )
//                            )
//                            finish()
                        } else {
                            val message = response.message()
                            Toast.makeText(
                                this@New_Program_Activity,
                                "" + message,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            call.cancel()
                        }

                    }

                    override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                        newProgramBinding.progresBar.visibility = View.GONE
                        Toast.makeText(
                            this@New_Program_Activity,
                            "" + t.message,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                })
        } else if (string == "View") {
            var data = programData.find { it.id == type.toInt() }

            startActivity(Intent(this, ViewProgramActivity::class.java).apply {
                putExtra("position", position)
                putExtra("id", id)
            })
        } else {
            val builder: AlertDialog.Builder
            builder = AlertDialog.Builder(this)
            builder.setMessage("Are you sure you want to delete Program?").setTitle("Success")

            builder.setMessage("Are you sure you want to delete Program?")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialog, id ->
                    newProgramBinding.progresBar.visibility = View.VISIBLE
                    apiInterface.DeleteProgram(type.toInt())
                        ?.enqueue(object : Callback<RegisterData?> {
                            override fun onResponse(
                                call: Call<RegisterData?>,
                                response: Response<RegisterData?>
                            ) {
                                Log.d("TAG", response.code().toString() + "")
                                val code = response.code()
                                if (code == 200) {
                                    val resource: RegisterData? = response.body()
                                    val Success: Boolean = resource?.status!!
                                    val Message: String = resource.message!!
                                    newProgramBinding.progresBar.visibility = View.GONE
                                    Toast.makeText(
                                        this@New_Program_Activity,
                                        "" + Message,
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    setRefresh()
                                    refreshData()
                                } else if (response.code() == 403) {
                                    Utils.setUnAuthDialog(this@New_Program_Activity)
//                                    val message = response.message()
//                                    Toast.makeText(
//                                        this@New_Program_Activity,
//                                        "" + message,
//                                        Toast.LENGTH_SHORT
//                                    )
//                                        .show()
//                                    call.cancel()
//                                    startActivity(
//                                        Intent(
//                                            this@New_Program_Activity,
//                                            SignInActivity::class.java
//                                        )
//                                    )
//                                    finish()
                                } else {
                                    val message = response.message()
                                    Toast.makeText(
                                        this@New_Program_Activity,
                                        "" + message,
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    call.cancel()
                                }
                            }

                            override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                                newProgramBinding.progresBar.visibility = View.GONE
                                Toast.makeText(
                                    this@New_Program_Activity,
                                    "" + t.message,
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

    private fun setProgramData(position: Int, type: Long) {
        exerciseDataList1.clear()
        excId.clear()
        try {
            setExcersiceData()

            var data = programData.find { it.id == type.toInt() }
            programId = data!!.id
            newProgramBinding.edtProgramName.setText(data.name)
            newProgramBinding.edtGoal.setText(data.goal!!.name)
            goalId = SelectedValue(data.goal_id!!.toInt())
            sectionId = SelectedValue(data.section_id!!.toInt())
            newProgramBinding.edtTime.setText(data.time)
            newProgramBinding.edtSection.setText(data.section!!.name)
            for (i in data.program_exercises!!) {
                excId.add(i.exercise_id!!.toInt())
            }
            Log.d("TimeData =", "${excId}")
            for (k in exerciseData) {
                Log.d("Exercise data = ", "${k.name} \t ${k.id} \t $excId")
            }

            for (i in excId) {
                exerciseDataList1.add(exerciseData.find { it.id == i }!!)
            }

            for (i in exerciseDataList1) {
                Log.d("Exercise data List =", "${i.name}")
            }
            Log.d("Exercise data size=", "${exerciseDataList1.size}")

            newProgramBinding.exerciseSelectRecycler.visibility = View.VISIBLE
            newProgramBinding.exerciseSelectRecycler.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            adapter1 =
                Exercise_select_Adapter1(exerciseDataList1, this)
            newProgramBinding.exerciseSelectRecycler.adapter = adapter1

        } catch (e: Exception) {
            Log.d("Error :-", e.message.toString())
        }
    }

    override fun onResume() {
        super.onResume()
        checkUser()
        setExcersiceData()
        //setExcersice()

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
                        Utils.setUnAuthDialog(this@New_Program_Activity)
                    } else {
                        Toast.makeText(
                            this@New_Program_Activity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Toast.makeText(
                        this@New_Program_Activity,
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

    private fun setExcersiceData() {
//        if (preferenceManager.getexercisedata()) {
//            exerciseDataList = getObject(this, "Exercise_list") as ArrayList<Ecercise_list_data>
//            newProgramBinding.edtTime.setText(exerciseDataList[0].time)
//            newProgramBinding.exerciseSelectRecycler.visibility = View.VISIBLE
//            newProgramBinding.exerciseSelectRecycler.layoutManager =
//                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
//            adapter1 =
//                Exercise_select_Adapter1(exerciseDataList1, this)
//            newProgramBinding.exerciseSelectRecycler.adapter = adapter1
//        }
        if (preferenceManager.getexercisedata()) {
            exerciseDataList1.clear()
            exerciseDataList1 =
                getObject(this, "Exercise_list") as ArrayList<ExcerciseData.Exercise>
            Log.d("Exercise Data:", "${exerciseDataList1.size}")
            newProgramBinding.edtTime.setText(exerciseDataList1[0].cycles!![0].time)
            newProgramBinding.exerciseSelectRecycler.visibility = View.VISIBLE
            newProgramBinding.exerciseSelectRecycler.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            adapter1 =
                Exercise_select_Adapter1(exerciseDataList1, this)
            newProgramBinding.exerciseSelectRecycler.adapter = adapter1
        }
    }

//    fun setExcersice() {
//
//        if (preferenceManager.getexercisedata()) {
//            exercise_list = getObject(this, "Exercise") as ArrayList<Ecercise_data_list>
//            newProgramBinding.edtTime.setText(exercise_list[0].time)
//            newProgramBinding.exerciseSelectRecycler.visibility = View.VISIBLE
//            newProgramBinding.exerciseSelectRecycler.layoutManager =
//                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
//            adapter1 =
//                Exercise_select_Adapter(exercise_list, this)
//            newProgramBinding.exerciseSelectRecycler.adapter = adapter1
//
//        }
//
//    }

//    fun getObject(c: Context, key: String): List<Ecercise_list_data> {
//        val appSharedPrefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(
//            c.applicationContext
//        )
//        val json = appSharedPrefs.getString(key, "")
//        val gson = Gson()
//        val type = object : TypeToken<List<Ecercise_list_data>>() {}.type
//        val arrayList: List<Ecercise_list_data> =
//            gson.fromJson<List<Ecercise_list_data>>(json, type)
//        return arrayList
//    }

    fun getObject(c: Context, key: String): List<ExcerciseData.Exercise> {
        val appSharedPrefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(
            c.applicationContext
        )
        val json = appSharedPrefs.getString(key, "")
        val gson = Gson()
        val type = object : TypeToken<List<ExcerciseData.Exercise>>() {}.type
        val arrayList: List<ExcerciseData.Exercise> =
            gson.fromJson<List<ExcerciseData.Exercise>>(json, type)
        return arrayList
    }

}