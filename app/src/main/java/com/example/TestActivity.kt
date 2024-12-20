package com.example

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
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
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.model.SelectedValue
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.EventListData
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.GetAthletesActivity
import com.example.trainerapp.PreferencesManager
import com.example.trainerapp.R
import com.example.trainerapp.TestListData
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.ActivityTestBinding
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Locale


class TestActivity : AppCompatActivity(), View.OnClickListener,
    OnItemClickListener.OnItemClickCallback {
    lateinit var testBinding: ActivityTestBinding
    var unitArray = ArrayList<String>()
    var age = ArrayList<String>()
    lateinit var TestList: ArrayList<TestListData.testData>
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    lateinit var preferenceManager: PreferencesManager
    private lateinit var id: ArrayList<Int>
    private lateinit var name: ArrayList<String>
    lateinit var adapter: TestDataAdapter
    var timeId = ""
    var testid: String = ""
    var nameFirst: String = ""
    var athletename: String = ""
    var athleteid: String = ""
    var date: String = ""
    var goal: String = ""
    var unit: String = ""
    var fromDay: Boolean = false

    lateinit var AthleteId: ArrayList<Int>
    lateinit var AthleteName: ArrayList<String>

    lateinit var goalData: MutableList<TestListData.testData>
    var Goal = ArrayList<String>()
    var goalId = SelectedValue(null)

    var type = "create"
//    var unitId = SelectedValue(null)

    private fun setUnitData() {
        unitArray.add("Unit")
        unitArray.add("kg")
        unitArray.add("cm")
        unitArray.add("meter")
        unitArray.add("km")
        unitArray.add("min")
        unitArray.add("second")
    }

    private fun getGoalData() {
        testBinding.progressbar.visibility = View.VISIBLE
        goalData = mutableListOf()
        apiInterface.GetGoal()?.enqueue(object : Callback<TestListData> {
            override fun onResponse(call: Call<TestListData>, response: Response<TestListData>) {
                val code = response.code()
                if (code == 200) {
                    testBinding.progressbar.visibility = View.GONE
                    if (response.isSuccessful) {
                        val data = response.body()!!.data
                        if (data!! != null) {
                            goalData.addAll(data.toMutableList())
                            for (i in goalData) {
                                Goal.add(i.name!!)
                            }
                        }
                    }
                } else if (response.code() == 403) {
                    Utils.setUnAuthDialog(this@TestActivity)
//                    val message = response.message()
//                    Toast.makeText(
//                        this@TestActivity,
//                        "" + message,
//                        Toast.LENGTH_SHORT
//                    )
//                        .show()
//                    call.cancel()
//                    startActivity(
//                        Intent(
//                            this@TestActivity,
//                            SignInActivity::class.java
//                        )
//                    )
//                    finish()
                } else {
                    testBinding.progressbar.visibility = View.GONE
                    val message = response.message()
                    Toast.makeText(this@TestActivity, "" + message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<TestListData>, t: Throwable) {
                testBinding.progressbar.visibility = View.GONE
                Log.d("TAG Goal", t.message.toString() + "")
            }

        })
    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        testBinding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(testBinding.root)

        initViews()
        loadData()

        if (preferenceManager.getselectAthelete()) {
            name = getObject(this, "setAthleteName") as ArrayList<String>
            id = getObjectJson(this, "setAthlete") as ArrayList<Int>
        }
        checkChangeValue()

        fromDay = intent.getBooleanExtra("fromday", false)
        nameFirst = intent.getStringExtra("name").toString()
        athletename = intent.getStringExtra("athletename1").toString()
        date = intent.getStringExtra("date").toString()
        goal = intent.getStringExtra("goal").toString()
        unit = intent.getStringExtra("unit").toString()
        athleteid = intent.getStringExtra("athleteId").toString()

        if (fromDay == true) {
            Log.e("KIRTIIIIIIIIII", "initViews: " + athletename)
            testBinding.etTestName.setText(nameFirst)
            testBinding.edtGoal.setText(goal)
            testBinding.edtUnits.setText(unit)
            testBinding.etInterestedAtheletes.setText(athletename)
            testBinding.etSelectTestDate.setText(date.take(10))
        }

//        for (i in 0..100) {
//            if (i == 0) {
//                age.add("Enter Goal")
//            } else {
//                age.add(i.toString())
//            }
//        }

//        val spinnerArrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, age)
//        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        testBinding.etEnterGoal.adapter = spinnerArrayAdapter
//        Utils.setSpinnerAdapter(applicationContext, age, testBinding.etEnterGoal)
//        initView()

    }

    private fun checkChangeValue() {
        testBinding.etSelectTestDate.setOnClickListener(this)

        testBinding.etTestName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateUI(testBinding.btnSaveTest)
            }

            override fun afterTextChanged(s: Editable?) {
                updateUI(testBinding.btnSaveTest)
            }
        })

        testBinding.etSelectTestDate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateUI(testBinding.btnSaveTest)
            }

            override fun afterTextChanged(s: Editable?) {
                updateUI(testBinding.btnSaveTest)
            }
        })

        testBinding.etInterestedAtheletes.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateUI(testBinding.btnSaveTest)
            }

            override fun afterTextChanged(s: Editable?) {
                updateUI(testBinding.btnSaveTest)
            }
        })

        testBinding.back.setOnClickListener {
            finish()
        }

        testBinding.edtGoal.setOnClickListener {
            showPopup(it, goalData, testBinding.edtGoal, Goal, goalId)
            updateUI(testBinding.btnSaveTest)
//            updateUI(testBinding.nextCard)
        }

        testBinding.edtUnits.setOnClickListener {
            showTypePopup(it)
            updateUI(testBinding.btnSaveTest)
//            updateUI(testBinding.nextCard)
        }


        testBinding.btnSaveTest.setOnClickListener {
            when {
                type == "edit" || fromDay -> {
                    updateData()
                }

                type == "create" -> {
                    // Save new data
                    saveData()
                }

                else -> {
                    Toast.makeText(this, "Invalid operation", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun updateData() {
        try {
            testBinding.progressbar.visibility = View.VISIBLE
            val str = arrayOfNulls<Int>(id.size)
            val array = JsonArray()

            for (i in 0 until id.size) {
                str[i] = id.get(i)
                array.add(id.get(i))
            }
            val goal = testBinding.edtGoal.text.toString()
            val unit = testBinding.edtUnits.text.toString()

            val idToUse = if (timeId.isNullOrEmpty()) {
                testid.toString()
            } else {
                timeId.toInt().toString()
            }

            val jsonObject = JsonObject()
            jsonObject.addProperty("id", idToUse)
            jsonObject.addProperty("name", testBinding.etTestName.text.toString())
            jsonObject.addProperty("goal", goal)
            jsonObject.addProperty("unit", unit)
            jsonObject.add("athlete_ids", array)
            jsonObject.addProperty("date", testBinding.etSelectTestDate.text.toString())
            apiInterface.EditTest(
                jsonObject
            )?.enqueue(object : Callback<RegisterData?> {
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
                            testBinding.progressbar.visibility = View.GONE
                            preferenceManager.setselectAthelete(false)
                            Toast.makeText(this@TestActivity, "" + Message, Toast.LENGTH_SHORT)
                                .show()
                            id.clear()
                            name.clear()
                            loadData()
                        } else {
                            Toast.makeText(this@TestActivity, "" + Message, Toast.LENGTH_SHORT)
                                .show()
                            testBinding.progressbar.visibility = View.GONE
                        }
                    } else if (response.code() == 403) {
                        Utils.setUnAuthDialog(this@TestActivity)
//                        val message = response.message()
//                        Toast.makeText(
//                            this@TestActivity,
//                            "" + message,
//                            Toast.LENGTH_SHORT
//                        )
//                            .show()
//                        call.cancel()
//                        startActivity(
//                            Intent(
//                                this@TestActivity,
//                                SignInActivity::class.java
//                            )
//                        )
//                        finish()
                    } else {
                        val message = response.message()
                        Toast.makeText(this@TestActivity, "" + message, Toast.LENGTH_SHORT)
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Toast.makeText(
                        this@TestActivity,
                        "" + t.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    call.cancel()
                }
            })
        } catch (e: Exception) {
            Log.d("TAG", "Exception : " + e.message.toString() + "")
        }
    }

    private fun saveData() {
        if (!testBinding.etTestName.text.toString()
                .isEmpty() && !testBinding.etSelectTestDate.text.toString().isEmpty()
        ) {
            val str = arrayOfNulls<Int>(id.size)
            val array = JsonArray()

            for (i in 0 until id.size) {
                str[i] = id.get(i)
                array.add(id.get(i))
            }
            val goal = testBinding.edtGoal.text.toString()
            val unit = testBinding.edtUnits.text.toString()

            val jsonObject = JsonObject()
            jsonObject.addProperty("name", testBinding.etTestName.text.toString())
            jsonObject.addProperty("goal", goal)
            jsonObject.addProperty("unit", unit)
            jsonObject.add("athlete_ids", array)
            jsonObject.addProperty("date", testBinding.etSelectTestDate.text.toString())
            testBinding.progressbar.visibility = View.VISIBLE
            apiInterface.CreateTest(
                jsonObject
            )?.enqueue(object : Callback<RegisterData?> {
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
                            testBinding.progressbar.visibility = View.GONE
                            preferenceManager.setselectAthelete(false)
                            id.clear()
                            name.clear()
                            loadData()
                            Toast.makeText(this@TestActivity, "" + Message, Toast.LENGTH_SHORT)
                                .show()

//                            finish()
//                            startActivity(intent)
                        } else {
                            Toast.makeText(this@TestActivity, "" + Message, Toast.LENGTH_SHORT)
                                .show()
                            testBinding.progressbar.visibility = View.GONE
                        }
                    } else if (response.code() == 403) {
                        Utils.setUnAuthDialog(this@TestActivity)
//                        val message = response.message()
//                        Toast.makeText(
//                            this@TestActivity,
//                            "" + message,
//                            Toast.LENGTH_SHORT
//                        )
//                            .show()
//                        call.cancel()
//                        startActivity(
//                            Intent(
//                                this@TestActivity,
//                                SignInActivity::class.java
//                            )
//                        )
//                        finish()
                    } else {
                        val message = response.message()
                        Toast.makeText(this@TestActivity, "" + message, Toast.LENGTH_SHORT)
                            .show()
                        call.cancel()
                    }

                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    testBinding.progressbar.visibility = View.GONE
                    Toast.makeText(
                        this@TestActivity,
                        "" + t.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    call.cancel()
                }
            })
        }
    }

    private fun loadData() {
        resetData()
        GetTestList()
        getGoalData()
        setUnitData()
    }

    private fun resetData() {
        type = "create"
        testBinding.btnSaveTest.isEnabled = false
        testBinding.btnSaveTest.setCardBackgroundColor(resources.getColor(R.color.grey))
        testBinding.etTestName.text!!.clear()
        testBinding.edtGoal.text!!.clear()
        testBinding.edtUnits.text!!.clear()
        testBinding.etSelectTestDate.text!!.clear()
        testBinding.etInterestedAtheletes.text!!.clear()
    }

    private fun areAllFieldsFilled(): Boolean {
        return (testBinding.edtGoal.text.toString() != "Select Goal" &&
                testBinding.edtUnits.text.toString() != "Select Unit" &&
                testBinding.etSelectTestDate.text.toString() != "Select Test Date" &&
                testBinding.etInterestedAtheletes.text.toString() != "Enter Interested Athletes" &&
                !testBinding.etTestName.text.isNullOrEmpty())
    }

    private fun updateUI(addButton: CardView) {
        if (areAllFieldsFilled()) {
            addButton.isEnabled = true
            addButton.setCardBackgroundColor(resources.getColor(R.color.splash_text_color)) // Change to your desired color
        } else {
            addButton.isEnabled = false
            addButton.setCardBackgroundColor(resources.getColor(R.color.grey)) // Disabled color
        }
    }

    private fun showTypePopup(anchorView: View?) {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.popup_list, null)
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        popupWindow.setBackgroundDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.popup_background
            )
        )
        popupWindow.elevation = 10f
        val listView = popupView.findViewById<ListView>(R.id.listView)
        val adapter =
            object : ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, unitArray) {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val view = super.getView(position, convertView, parent) as TextView
                    view.setTextColor(Color.WHITE) // Set text color to white
                    return view
                }
            }
        listView.adapter = adapter
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = unitArray[position]
            testBinding.edtUnits.setText(selectedItem)
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
                this,
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

    private fun initViews() {
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        preferenceManager = PreferencesManager(this)
        id = ArrayList<Int>()
        TestList = ArrayList()
        name = ArrayList()
        AthleteId = ArrayList()
        AthleteName = ArrayList()

        testid = intent.getStringExtra("id") ?: ""
        AthleteId = intent.getIntegerArrayListExtra("athleteid") ?: arrayListOf()
        AthleteName = intent.getStringArrayListExtra("athletename") ?: arrayListOf()

        Log.e("KIRTIIIIIIIIII", "initViews: " + intent.getBooleanExtra("fromday", false))

        Log.d("TestActivity", "Received testId: $testid")
        Log.d("TestActivity", "Received Athlete IDs: $AthleteId")
        Log.d("TestActivity", "Received Athlete Names: $AthleteName")

    }

    private fun GetTestList() {
        try {
            TestList.clear()
            testBinding.progressbar.visibility = View.VISIBLE
            apiInterface.GetTest()?.enqueue(object : Callback<TestListData?> {
                override fun onResponse(
                    call: Call<TestListData?>,
                    response: Response<TestListData?>
                ) {
                    Log.d("TAG", response.code().toString() + "")
                    val code = response.code()
                    if (code == 200) {
                        val resource: TestListData? = response.body()
                        val Success: Boolean = resource?.status!!
                        val Message: String = resource.message!!
                        if (Success == true) {
                            try {
                                if (resource.data!! != null) {
                                    TestList = resource.data!!
                                    initrecycler(resource.data)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else {
                            testBinding.progressbar.visibility = View.GONE
                        }
                    } else if (response.code() == 403) {
                        Utils.setUnAuthDialog(this@TestActivity)
//                    val message = response.message()
//                    Toast.makeText(
//                        this@TestActivity,
//                        "" + message,
//                        Toast.LENGTH_SHORT
//                    )
//                        .show()
//                    call.cancel()
//                    startActivity(
//                        Intent(
//                            this@TestActivity,
//                            SignInActivity::class.java
//                        )
//                    )
//                    finish()
                    } else {
                        testBinding.progressbar.visibility = View.GONE
                        val message = response.message()
                        Toast.makeText(this@TestActivity, "" + message, Toast.LENGTH_SHORT)
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<TestListData?>, t: Throwable) {
                    testBinding.progressbar.visibility = View.GONE
                    Toast.makeText(this@TestActivity, "" + t.message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }
            })
        } catch (e: Exception) {
            testBinding.progressbar.visibility = View.GONE
            Toast.makeText(this, "ERROR:- " + e.message.toString(), Toast.LENGTH_SHORT).show()
            Log.d("ERROR", "GetTestList: ${e.message.toString()}")
        }

    }

    private fun initrecycler(testdatalist: ArrayList<TestListData.testData>?) {
        testBinding.progressbar.visibility = View.GONE
        testBinding.textListRly.layoutManager = LinearLayoutManager(this)
        adapter = TestDataAdapter(testdatalist, this, this)
        testBinding.textListRly.adapter = adapter
    }

//    private fun initView() {
//        unitArray.add("Unit")
//        unitArray.add("kg")
//        unitArray.add("cm")
//        unitArray.add("meter")
//        unitArray.add("km")
//        unitArray.add("min")
//        unitArray.add("second")
//        Utils.setSpinnerAdapter(applicationContext, unitArray, testBinding.spUnits)
//    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.etSelectTestDate -> {
                val calendar = Calendar.getInstance()

                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

                // Create a DatePickerDialog
                val datePickerDialog = DatePickerDialog(
                    this,
                    { _, selectedYear, selectedMonth, selectedDay ->
                        calendar.set(selectedYear, selectedMonth, selectedDay)

                        // Format the date in yyyy-MM-dd format
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val formattedDate = dateFormat.format(calendar.time)

                        // Set the formatted date to the EditText
                        testBinding.etSelectTestDate.setText(formattedDate)
                    },
                    year, month, dayOfMonth
                )

                datePickerDialog.show()
            }

        }
    }

    fun atheleteclick(view: View) {
        Log.d("type :-", "$type")
        if (type == "create") {
            val i = Intent(this, GetAthletesActivity::class.java).apply {
                putExtra("type", "create")
            }
            startActivity(i)
        } else if (type == "edit") {
            Log.d("test Id:", "$id")
            val arrayList: ArrayList<Int> = ArrayList(id)
            val i = Intent(this, GetAthletesActivity::class.java).apply {
                putExtra("type", "edit")
                putIntegerArrayListExtra("testId", arrayList)
            }
            startActivity(i)
        }
    }

    fun getObjectJson(c: Context, key: String): List<Int> {
        val appSharedPrefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(
            c.applicationContext
        )
        val json = appSharedPrefs.getString(key, "")
        val gson = Gson()
        val type = object : TypeToken<List<Int>>() {}.type
        val arrayList: List<Int> = gson.fromJson<List<Int>>(json, type)
        return arrayList
    }


    fun getObject(c: Context, key: String): List<String> {
        val appSharedPrefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(
            c.applicationContext
        )
        val json = appSharedPrefs.getString(key, "")
        val gson = Gson()
        val type = object : TypeToken<List<String>>() {}.type
        val arrayList: List<String> = gson.fromJson<List<String>>(json, type)
        return arrayList
    }

    override fun onItemClicked(view: View, position: Int, type: Long, string: String) {
        if (string == "Delete") {
            var builder: AlertDialog.Builder
            builder = AlertDialog.Builder(this)
            builder.setMessage("Are you sure you want to delete Test?").setTitle("Success")
            builder.setMessage("Are you sure you want to delete Test?")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialog, id ->
                    testBinding.progressbar.visibility = View.VISIBLE
                    apiInterface.DeleteTest(type.toInt())
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
                                    testBinding.progressbar.visibility = View.GONE
                                    Toast.makeText(
                                        this@TestActivity,
                                        "" + Message,
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    loadData()
                                } else if (response.code() == 403) {
                                    Utils.setUnAuthDialog(this@TestActivity)
//                                    val message = response.message()
//                                    Toast.makeText(
//                                        this@TestActivity,
//                                        "" + message,
//                                        Toast.LENGTH_SHORT
//                                    )
//                                        .show()
//                                    call.cancel()
//                                    startActivity(
//                                        Intent(
//                                            this@TestActivity,
//                                            SignInActivity::class.java
//                                        )
//                                    )
//                                    finish()
                                } else {
                                    val message = response.message()
                                    Toast.makeText(
                                        this@TestActivity,
                                        "" + message,
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    call.cancel()
                                }
                            }

                            override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                                Toast.makeText(
                                    this@TestActivity,
                                    "" + t.message,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                call.cancel()
                            }
                        })
                }.setNegativeButton(
                    "No"
                ) { dialog, id -> //  Action for 'NO' Button
                    dialog.cancel()
                }

            val alert = builder.create()
            alert.setTitle("Success")
            alert.show()

        } else if (string == "fav") {
            testBinding.progressbar.visibility = View.VISIBLE
            val id: MultipartBody.Part =
                MultipartBody.Part.createFormData("id", type.toInt().toString())
            apiInterface.Favourite_Test(id)?.enqueue(object : Callback<RegisterData?> {
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
                            testBinding.progressbar.visibility = View.GONE
                            loadData()
//                        Toast.makeText(this@TestActivity, "" + Message, Toast.LENGTH_SHORT)
//                            .show()
//                        finish()
//                        startActivity(intent)
                        } else {
                            testBinding.progressbar.visibility = View.GONE
                            Toast.makeText(this@TestActivity, "" + Message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else if (response.code() == 403) {
                        Utils.setUnAuthDialog(this@TestActivity)
//                        val message = response.message()
//                        Toast.makeText(
//                            this@TestActivity,
//                            "" + message,
//                            Toast.LENGTH_SHORT
//                        )
//                            .show()
//                        call.cancel()
//                        startActivity(
//                            Intent(
//                                this@TestActivity,
//                                SignInActivity::class.java
//                            )
//                        )
//                        finish()
                    } else {
                        val message = response.message()
                        Toast.makeText(this@TestActivity, "" + message, Toast.LENGTH_SHORT)
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    testBinding.progressbar.visibility = View.GONE
                    Toast.makeText(this@TestActivity, "" + t.message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }
            })
        } else if (string == "unfav") {
            testBinding.progressbar.visibility = View.VISIBLE
            apiInterface.DeleteFavourite_Test(type.toInt())
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
                                testBinding.progressbar.visibility = View.GONE
                                loadData()
                            } else {
                                testBinding.progressbar.visibility = View.GONE
                                Toast.makeText(this@TestActivity, "" + Message, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        } else if (response.code() == 403) {
                            Utils.setUnAuthDialog(this@TestActivity)
//                            val message = response.message()
//                            Toast.makeText(
//                                this@TestActivity,
//                                "" + message,
//                                Toast.LENGTH_SHORT
//                            )
//                                .show()
//                            call.cancel()
//                            startActivity(
//                                Intent(
//                                    this@TestActivity,
//                                    SignInActivity::class.java
//                                )
//                            )
//                            finish()
                        } else {
                            val message = response.message()
                            Toast.makeText(this@TestActivity, "" + message, Toast.LENGTH_SHORT)
                                .show()
                            call.cancel()
                        }
                    }

                    override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                        testBinding.progressbar.visibility = View.GONE
                        Toast.makeText(this@TestActivity, "" + t.message, Toast.LENGTH_SHORT)
                            .show()
                        call.cancel()
                    }
                })
        } else {
            this@TestActivity.type = "edit"
            timeId = TestList[position].id.toString()
            testBinding.etTestName.setText(TestList[position].title)
            testBinding.edtGoal.setText(TestList[position].goal.toString())
            testBinding.edtUnits.setText(TestList[position].unit.toString())
            val date = TestList[position].updated_at!!.split("T")[0]
            id.clear()
            name.clear()

            Log.d("TestActivityAAAA", "Received Athlete Names: $timeId")
            Log.d("TestActivityAAAA", "Received Athlete Names: $testid")

            for (i in TestList[position].data!!) {
                id.add(i.athlete!!.id!!)
                name.add(i.athlete!!.name!!)
            }
            val str = arrayOfNulls<String>(name.size)
            val array = JsonArray()

            for (i in 0 until name.size) {
                str[i] = name.get(i)
                array.add(name.get(i))
            }

            val str1 = convertStringArrayToString(str, ",")
            testBinding.etInterestedAtheletes.setText(str1)
            testBinding.etSelectTestDate.setText(date)
        }
    }


    private fun EditData(testId: String) {
        Log.d("EditData", "Looking for test with ID: $testId")

        val selectedTest = TestList.find { it.id == testId.toInt() }

        if (selectedTest != null) {
            testid = selectedTest.id!!.toString()

            testBinding.etTestName.setText(selectedTest.title)
            testBinding.edtGoal.setText(selectedTest.goal?.toString() ?: "")
            testBinding.edtUnits.setText(selectedTest.unit?.toString() ?: "")

            val date = selectedTest.updated_at?.split("T")?.get(0) ?: ""
            testBinding.etSelectTestDate.setText(date)

            AthleteId.clear()
            AthleteName.clear()

            selectedTest.data?.forEach { dataItem ->
                dataItem.athlete?.let { athlete ->
                    AthleteId.add(athlete.id!!)
                    AthleteName.add(athlete.name!!)
                }
            }

            val str = arrayOfNulls<String>(AthleteName.size)
            val array = JsonArray()

            for (i in 0 until AthleteName.size) {
                str[i] = AthleteName.get(i)
                array.add(AthleteName.get(i))
            }

            val str1 = convertStringArrayToString(str, ",")
            testBinding.etInterestedAtheletes.setText(str1)
            testBinding.etSelectTestDate.setText(date)
        } else {
            Log.d("EditData", "No test found with ID: $testId")
            Toast.makeText(this, "Test with ID $testId not found", Toast.LENGTH_SHORT).show()
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
                        Utils.setUnAuthDialog(this@TestActivity)
                    } else {
                        Toast.makeText(
                            this@TestActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Toast.makeText(
                        this@TestActivity,
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

    override fun onResume() {
        super.onResume()
        checkUser()
        if (preferenceManager.getselectAthelete()) {
            name = getObject(this, "setAthleteName") as ArrayList<String>
            id = getObjectJson(this, "setAthlete") as ArrayList<Int>
            Log.d("name :-", "$name \n $id")

            if (!name.isNullOrEmpty()) {
                val str = arrayOfNulls<String>(name.size)
                val array = JsonArray()

                // Populate the array and log the progress
                for (i in 0 until name.size) {
                    str[i] = name[i]
                    array.add(name[i])
                }

                // Convert array to a comma-separated string
                val str1 = convertStringArrayToString(str, ",")
                testBinding.etInterestedAtheletes.setText(str1)
            } else {
                // Handle the case where name is null or empty
                Log.d("name", "Name list is empty or null")
                testBinding.etInterestedAtheletes.text!!.clear() // Clear the field if no data
            }

//            val str = arrayOfNulls<String>(name.size)
//            val array = JsonArray()
//
//            for (i in 0 until name.size) {
//                str[i] = name.get(i)
//                array.add(name.get(i))
//            }
//            val str1 = convertStringArrayToString(str, ",")
//            testBinding.etInterestedAtheletes.setText(str1)
        }
    }

    private fun convertStringArrayToString(strArr: Array<String?>, delimiter: String): String? {
        val sb = StringBuilder()
        for (str in strArr) sb.append(str).append(delimiter)
        return sb.substring(0, sb.length - 1)
    }
}