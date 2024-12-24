package com.example

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.Adapter.groups.GetAthleteListAdapterGroup
import com.example.Adapter.groups.GetEventListAdapterGroup
import com.example.Adapter.groups.GetLessonListAdapterGroup
import com.example.Adapter.groups.GetPlanningListAdapterGroup
import com.example.Adapter.groups.GetTestListAdapterGroup
import com.example.Adapter.selected_day.AddSelectedDayEventApater
import com.example.Adapter.selected_day.AddSelectedDayTestAdapter
import com.example.model.newClass.athlete.AthleteData
import com.example.model.newClass.lesson.Lesson
import com.example.model.training_plan.TrainingPlanData
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.EventListData
import com.example.trainerapp.ApiClass.ProgramListData
import com.example.trainerapp.EditGroupActivity
import com.example.trainerapp.TestListData
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.ActivityLessonListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LessonListActivity : AppCompatActivity(),OnItemClickListener.OnItemClickCallback {

    private lateinit var binding: ActivityLessonListBinding
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    private lateinit var lessonData: ArrayList<Lesson.LessonDatabase>
    lateinit var adapter: GetLessonListAdapterGroup
    lateinit var adapterProgram: GetProgramListAdapterGroup
    var selectedLessonID: String = ""
    var type : String = ""
    var typeEdit : String = ""
    lateinit var EventList: ArrayList<EventListData.testData>
    lateinit var programData: MutableList<ProgramListData.testData>
    lateinit var plainngData: MutableList<TrainingPlanData.TrainingPlan>
    lateinit var athleteData: MutableList<AthleteData.Athlete>
    lateinit var adapterEvent: GetEventListAdapterGroup
    lateinit var adapterTest: GetTestListAdapterGroup
    lateinit var adapterPlanning: GetPlanningListAdapterGroup
    lateinit var adapterAthlete: GetAthleteListAdapterGroup

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLessonListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        ButtonClick()
        lordData()

    }

    private fun lordData() {
        when(type){
            "lesson" -> {
                binding.tvCredit.text = "Lesson List"
                GetLessonList()
            }
            "test" -> {
                binding.tvCredit.text = "Test List"
                GetTestList()
            }
            "event" -> {
                binding.tvCredit.text = "Event List"
                geteventlist()
            }
            "planning" -> {
                binding.tvCredit.text = "planning List"
                getTrainingData()
            }
            "athlete" -> {
                binding.tvCredit.text = "Athlete List"
                getAthleteData()
            }
        }

        when(typeEdit){
            "lesson" -> {
                binding.tvCredit.text = "Lesson List"
                GetLessonList()
            }
            "test" -> {
                binding.tvCredit.text = "Test List"
                GetTestList()
            }
            "event" -> {
                binding.tvCredit.text = "Event List"
                geteventlist()
            }
            "planning" -> {
                binding.tvCredit.text = "planning List"
                getTrainingData()
            }
            "athlete" -> {
                binding.tvCredit.text = "Athlete List"
                getAthleteData()
            }
        }
    }

    private fun ButtonClick() {

        binding.back.setOnClickListener { finish() }

        when(type) {

            "lesson" -> {
                binding.cardSave.setOnClickListener {
                    val selectedLessonIds = adapter.getSelectedLessonData()
                    Log.d("SelectedLessonIds", "Selected IDs: $selectedLessonIds")

                    if (selectedLessonIds.isNullOrEmpty()) {
                        Toast.makeText(
                            this,
                            "Please select a valid lesson before saving",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }

                    val intent = Intent(this, CreateGropActivity::class.java)
                    intent.putExtra("lessonId", selectedLessonIds.toIntArray())
                    intent.putExtra("load","load")
                    startActivity(intent)
                    finish()

                }

//                binding.edtSearch.addTextChangedListener { text ->
//                    val query = text?.toString() ?: ""
//                    adapter.filter(query)
//                }

            }

            "event" -> {
                binding.cardSave.setOnClickListener {
                    val selecteTestIds = adapterEvent.getSelectedEventData()
                    Log.d("SelectedLessonIds", "Selected IDs: $selecteTestIds")

                    if (selecteTestIds.isNullOrEmpty()) {
                        Toast.makeText(
                            this,
                            "Please select a valid test before saving",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }

                    val intent = Intent(this, CreateGropActivity::class.java)
                    intent.putExtra("eventId", selecteTestIds.toIntArray())
                    startActivity(intent)
                    finish()
                }

//                binding.edtSearch.addTextChangedListener { text ->
//                    val query = text?.toString() ?: ""
//                    adapterEvent.filter(query)
//                }
            }

            "test" -> {
                binding.cardSave.setOnClickListener {
                    binding.cardSave.setOnClickListener {
                        val selecteTestIds = adapterTest.getSelectedTestData()
                        Log.d("SelectedLessonIds", "Selected IDs: $selecteTestIds")

                        if (selecteTestIds.isNullOrEmpty()) {
                            Toast.makeText(
                                this,
                                "Please select a valid test before saving",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@setOnClickListener
                        }

                        val intent = Intent(this, CreateGropActivity::class.java)
                        intent.putExtra("testId", selecteTestIds.toIntArray())
                        startActivity(intent)
                        finish()
                    }
                }

//                binding.edtSearch.addTextChangedListener { text ->
//                    val query = text?.toString() ?: ""
//                    adapterTest.filter(query)
//                }
            }
            "planning" -> {
                binding.cardSave.setOnClickListener {
                    binding.cardSave.setOnClickListener {
                        val selecteTestIds = adapterPlanning.getSelectedPlanningData()
                        Log.d("SelectedLessonIds", "Selected IDs: $selecteTestIds")

                        if (selecteTestIds.isNullOrEmpty()) {
                            Toast.makeText(
                                this,
                                "Please select a valid test before saving",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@setOnClickListener
                        }

                        val intent = Intent(this, CreateGropActivity::class.java)
                        intent.putExtra("planningId", selecteTestIds.toIntArray())
                        startActivity(intent)
                        finish()
                    }
                }

//                binding.edtSearch.addTextChangedListener { text ->
//                    val query = text?.toString() ?: ""
//                    adapterTest.filter(query)
//                }
            }
            "athlete" -> {
                binding.cardSave.setOnClickListener {
                    binding.cardSave.setOnClickListener {
                        val selecteTestIds = adapterAthlete.getSelectedAthleteData()
                        Log.d("SelectedLessonIds", "Selected IDs: $selecteTestIds")

                        if (selecteTestIds.isNullOrEmpty()) {
                            Toast.makeText(
                                this,
                                "Please select a valid test before saving",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@setOnClickListener
                        }

                        val intent = Intent(this, CreateGropActivity::class.java)
                        intent.putExtra("athleteId", selecteTestIds.toIntArray())
                        startActivity(intent)
                    }
                }

//                binding.edtSearch.addTextChangedListener { text ->
//                    val query = text?.toString() ?: ""
//                    adapterTest.filter(query)
//                }
            }
        }

        when(typeEdit) {

            "lesson" -> {
                binding.cardSave.setOnClickListener {
                    val selectedLessonIds = adapter.getSelectedLessonData()
                    Log.d("SelectedLessonIds", "Selected IDs: $selectedLessonIds")

                    if (selectedLessonIds.isNullOrEmpty()) {
                        Toast.makeText(
                            this,
                            "Please select a valid lesson before saving",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }

                    val intent = Intent(this, EditGroupActivity::class.java)
                    intent.putExtra("lessonId", selectedLessonIds.toIntArray())
                    intent.putExtra("load","load")
                    startActivity(intent)
                    finish()

                }

//                binding.edtSearch.addTextChangedListener { text ->
//                    val query = text?.toString() ?: ""
//                    adapter.filter(query)
//                }

            }

            "event" -> {
                binding.cardSave.setOnClickListener {
                    val selecteTestIds = adapterEvent.getSelectedEventData()
                    Log.d("SelectedLessonIds", "Selected IDs: $selecteTestIds")

                    if (selecteTestIds.isNullOrEmpty()) {
                        Toast.makeText(
                            this,
                            "Please select a valid test before saving",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }

                    val intent = Intent(this, EditGroupActivity::class.java)
                    intent.putExtra("eventId", selecteTestIds.toIntArray())
                    startActivity(intent)
                    finish()
                }

//                binding.edtSearch.addTextChangedListener { text ->
//                    val query = text?.toString() ?: ""
//                    adapterEvent.filter(query)
//                }
            }

            "test" -> {
                binding.cardSave.setOnClickListener {
                    binding.cardSave.setOnClickListener {
                        val selecteTestIds = adapterTest.getSelectedTestData()
                        Log.d("SelectedLessonIds", "Selected IDs: $selecteTestIds")

                        if (selecteTestIds.isNullOrEmpty()) {
                            Toast.makeText(
                                this,
                                "Please select a valid test before saving",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@setOnClickListener
                        }

                        val intent = Intent(this, EditGroupActivity::class.java)
                        intent.putExtra("testId", selecteTestIds.toIntArray())
                        startActivity(intent)
                        finish()
                    }
                }

//                binding.edtSearch.addTextChangedListener { text ->
//                    val query = text?.toString() ?: ""
//                    adapterTest.filter(query)
//                }
            }
            "planning" -> {
                binding.cardSave.setOnClickListener {
                    binding.cardSave.setOnClickListener {
                        val selecteTestIds = adapterPlanning.getSelectedPlanningData()
                        Log.d("SelectedLessonIds", "Selected IDs: $selecteTestIds")

                        if (selecteTestIds.isNullOrEmpty()) {
                            Toast.makeText(
                                this,
                                "Please select a valid test before saving",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@setOnClickListener
                        }

                        val intent = Intent(this, EditGroupActivity::class.java)
                        intent.putExtra("planningId", selecteTestIds.toIntArray())
                        startActivity(intent)
                        finish()
                    }
                }

//                binding.edtSearch.addTextChangedListener { text ->
//                    val query = text?.toString() ?: ""
//                    adapterTest.filter(query)
//                }
            }
            "athlete" -> {
                binding.cardSave.setOnClickListener {
                    binding.cardSave.setOnClickListener {
                        val selecteTestIds = adapterAthlete.getSelectedAthleteData()
                        Log.d("SelectedLessonIds", "Selected IDs: $selecteTestIds")

                        if (selecteTestIds.isNullOrEmpty()) {
                            Toast.makeText(
                                this,
                                "Please select a valid test before saving",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@setOnClickListener
                        }

                        val intent = Intent(this, EditGroupActivity::class.java)
                        intent.putExtra("athleteId", selecteTestIds.toIntArray())
                        startActivity(intent)
                    }
                }

//                binding.edtSearch.addTextChangedListener { text ->
//                    val query = text?.toString() ?: ""
//                    adapterTest.filter(query)
//                }
            }
        }
    }

    private fun initView() {
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        lessonData = ArrayList()
        programData = mutableListOf()
        plainngData = mutableListOf()
        athleteData = mutableListOf()

        type = intent.getStringExtra("Add").toString()
        typeEdit = intent.getStringExtra("Edit").toString()

        Log.d("GHGHHG", "initView: $type")

        Log.d("type","IDDD:-    $typeEdit")
    }

    private fun getTrainingData() {
        try {
            plainngData = mutableListOf()
            plainngData.clear()
            apiInterface.GetTrainingPlan()?.enqueue(object : Callback<TrainingPlanData> {
                @SuppressLint("NewApi")
                override fun onResponse(
                    call: Call<TrainingPlanData>,
                    response: Response<TrainingPlanData>
                ) {
                    binding.progresBar.visibility = View.GONE
                    Log.d("TAG", response.code().toString() + "")
                    val code = response.code()
                    if (code == 200) {
                        if (response.isSuccessful && response.body() != null) {
                            val data = response.body()?.data ?: mutableListOf()
                            if (data.isNotEmpty()) {
                                plainngData = data.toMutableList()
//                                setAdapter(trainingData)
                                initrecyclerplanning(plainngData)

                            } else{
                                binding.cardSave.focusable
                                binding.cardSave.isClickable = false

                            }
                        } else {
                            Log.d("DATA_TAG", "No Data Available")
                        }
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@LessonListActivity)
                    } else {
                        Toast.makeText(
                            this@LessonListActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<TrainingPlanData>, t: Throwable) {
                    binding.progresBar.visibility = View.GONE
                    Log.d("TAG Category", t.message.toString())
                    call.cancel()
                }
            })

            binding.progresBar.visibility = View.VISIBLE

        } catch (e: Exception) {
            Log.d("Exception", e.toString())
        }
    }

    private fun geteventlist() {
        binding.progresBar.visibility = View.VISIBLE
        apiInterface.GetEvent()?.enqueue(object : Callback<EventListData?> {
            @SuppressLint("NewApi")
            override fun onResponse(
                call: Call<EventListData?>,
                response: Response<EventListData?>
            ) {
                Log.d("TAG", response.code().toString() + "")
                val code = response.code()
                if (code == 200) {
                    val resource: EventListData? = response.body()
                    val Success: Boolean = resource?.status!!
                    val Message: String = resource.message!!
                    if (Success == true) {
                        if (resource.data != null) {
                            EventList = resource.data!!
                            resource.data!!.forEach {
                                Log.d("EventTitle", it.title ?: "No title")
                            }
                            initrecycler(resource.data)
                        } else {
                            binding.cardSave.focusable
                            binding.cardSave.isClickable = false
                            initrecycler(arrayListOf())
                        }
                    } else {
                        binding.progresBar.visibility = View.GONE
                    }
                } else if (code == 403) {
                    Utils.setUnAuthDialog(this@LessonListActivity)
                } else {
                    val Message = response.message()
                    Toast.makeText(
                        this@LessonListActivity,
                        "" + Message,
                        Toast.LENGTH_SHORT
                    ).show()
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<EventListData?>, t: Throwable) {
                binding.progresBar.visibility = View.GONE
                Toast.makeText(this@LessonListActivity, "" + t.message, Toast.LENGTH_SHORT)
                    .show()
                call.cancel()
            }
        })
    }

    private fun GetLessonList() {
        binding.progresBar.visibility = View.VISIBLE
        apiInterface.GetLession1().enqueue(object : Callback<Lesson> {
            @SuppressLint("NewApi")
            override fun onResponse(call: Call<Lesson>, response: Response<Lesson>) {
                Log.d("TAG Lesson:", response.code().toString())
                binding.progresBar.visibility = View.GONE
                val code = response.code()
                if (code == 200) {
                    val resource = response.body()
                    if (resource != null) {
                        val success: Boolean = resource.status ?: false
                        val message: String = resource.message ?: "No message"
                        if (success) {
                            val data = resource.data
                            if (!data.isNullOrEmpty()) {
                                Log.d("Lesson Data :-", "$success $message \t $data")
                                lessonData.addAll(data)
                                initRecyclerview(data)
                            } else {
                                binding.cardSave.focusable
                                binding.cardSave.isClickable = false
                                Toast.makeText(this@LessonListActivity, "No lessons available", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this@LessonListActivity, "Error: $message", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@LessonListActivity, "Empty response body", Toast.LENGTH_SHORT).show()
                    }
                } else if (code == 403) {
                    Utils.setUnAuthDialog(this@LessonListActivity)
                } else {
                    Toast.makeText(this@LessonListActivity, response.message(), Toast.LENGTH_SHORT).show()
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<Lesson>, t: Throwable) {
                Toast.makeText(this@LessonListActivity, "Error: " + t.message, Toast.LENGTH_SHORT).show()
                call.cancel()
            }
        })
    }

    private fun GetTestList() {
        binding.progresBar.visibility = View.VISIBLE
        apiInterface.GetTest()?.enqueue(object : Callback<TestListData?> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<TestListData?>, response: Response<TestListData?>) {
                Log.d("TAG", response.code().toString() + "")
                val code = response.code()
                if (code == 200) {
                    val resource: TestListData? = response.body()
                    val Success: Boolean = resource?.status!!
                    val Message: String = resource.message!!
                    if (Success == true) {
                        try {
                            if (resource.data!! != null) {
                                initrecyclerTest(resource.data)
                            }else{
                                binding.cardSave.focusable
                                binding.cardSave.isClickable = false
                                Toast.makeText(this@LessonListActivity, "No Test available", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            binding.progresBar.visibility = View.GONE
                            Log.d("TESTCATCH", "onResponse: ${e.message}")
                            e.printStackTrace()
                        }
                    } else {
                        binding.progresBar.visibility = View.GONE
                    }
                } else if (response.code() == 403) {
                    Utils.setUnAuthDialog(this@LessonListActivity)

                } else {
                    binding.progresBar.visibility = View.GONE
                    val message = response.message()
                    Toast.makeText(this@LessonListActivity, "" + message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<TestListData?>, t: Throwable) {
                binding.progresBar.visibility = View.GONE
                Toast.makeText(this@LessonListActivity, "" + t.message, Toast.LENGTH_SHORT)
                    .show()
                call.cancel()
            }
        })

    }

    private fun GetProgramData() {
        binding.progresBar.visibility = View.VISIBLE
        programData.clear()
        apiInterface.GetProgam()?.enqueue(object : Callback<ProgramListData?> {
            override fun onResponse(
                call: Call<ProgramListData?>,
                response: Response<ProgramListData?>
            ) {
                Log.d("TAG", response.code().toString())
                val code = response.code()
                if (code == 200) {
                    val resource: ProgramListData? = response.body()
                    val Success: Boolean = resource?.status!!
                    val Message: String = resource.message!!
                    binding.progresBar.visibility = View.GONE
                    Log.d("TAG", resource.data.toString())

                    if (Success) {
                        try {
                            if (resource.data == null || resource.data!!.isEmpty()) {
                                initRecyclerviewProgram(arrayListOf())
                            } else {
                                programData.addAll(resource.data!!)
                                initRecyclerviewProgram(resource.data!!)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        Toast.makeText(this@LessonListActivity, Message, Toast.LENGTH_SHORT)
                            .show()
                    }
                } else if (response.code() == 403) {
                    Utils.setUnAuthDialog(this@LessonListActivity)
                } else {
                    val message = response.message()
                    Toast.makeText(this@LessonListActivity, message, Toast.LENGTH_SHORT).show()
                    call.cancel()
                }
            }


            override fun onFailure(call: Call<ProgramListData?>, t: Throwable) {
                binding.progresBar.visibility = View.GONE
                Toast.makeText(this@LessonListActivity, "" + t.message, Toast.LENGTH_SHORT)
                    .show()
                call.cancel()
            }
        })
    }

    private fun getAthleteData() {
        athleteData.clear()
        binding.progresBar.visibility = View.VISIBLE

        apiInterface.GetAthleteList()!!.enqueue(object : Callback<AthleteData> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<AthleteData>, response: Response<AthleteData>) {
                binding.progresBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val resource = response.body()
                    val success = resource?.status ?: false
                    val message = resource?.message ?: ""

                    if (success) {
                        val data = resource?.data

                        if (resource?.data!! != null) {
                            if (data != null) {
                                athleteData.addAll(data)
                            }
                            initrecyclerAthlete(athleteData)
                        }else{
                            binding.cardSave.focusable
                            binding.cardSave.isClickable = false
                            Toast.makeText(this@LessonListActivity, "No Athlete available", Toast.LENGTH_SHORT).show()
                        }

                        for (datas in athleteData){
                            Log.d("sujals","ID:- " + datas.id)
                        }

                    } else if (response.code() == 403) {
                        Utils.setUnAuthDialog(this@LessonListActivity)
                    } else {
                        Toast.makeText(this@LessonListActivity, response.message(), Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<AthleteData>, t: Throwable) {
                binding.progresBar.visibility = View.GONE
                Toast.makeText(this@LessonListActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun initrecyclerAthlete(testdatalist: MutableList<AthleteData.Athlete>) {
        binding.progresBar.visibility = View.GONE
        binding.recycler.layoutManager = LinearLayoutManager(this)
        adapterAthlete = GetAthleteListAdapterGroup(testdatalist, this, this)
        binding.recycler.adapter = adapterAthlete
    }


    private fun initrecyclerplanning(testdatalist: MutableList<TrainingPlanData.TrainingPlan>?) {
        binding.progresBar.visibility = View.GONE
        binding.recycler.layoutManager = LinearLayoutManager(this)
        adapterPlanning = GetPlanningListAdapterGroup(testdatalist, this, this)
        binding.recycler.adapter = adapterPlanning
    }


    private fun initRecyclerviewProgram(user: ArrayList<ProgramListData.testData>) {

        binding.recycler.layoutManager = LinearLayoutManager(this)
        adapterProgram = GetProgramListAdapterGroup(user, this, this)
        binding.recycler.adapter = adapter
    }

    private fun initRecyclerview(data: ArrayList<Lesson.LessonDatabase>) {
        binding.recycler.layoutManager = LinearLayoutManager(this)
        adapter = GetLessonListAdapterGroup(data, this, this)
        binding.recycler.adapter = adapter
    }

    private fun initrecyclerTest(testdatalist: ArrayList<TestListData.testData>?) {
        binding.progresBar.visibility = View.GONE
        binding.recycler.layoutManager = LinearLayoutManager(this)
        adapterTest = GetTestListAdapterGroup(testdatalist, this, this)
        binding.recycler.adapter = adapterTest
    }


    private fun initrecycler(testdatalist: ArrayList<EventListData.testData>?) {
        binding.progresBar.visibility = View.GONE
            adapterEvent = GetEventListAdapterGroup(testdatalist, this, this)
            binding.recycler.layoutManager = LinearLayoutManager(this)
            binding.recycler.adapter = adapterEvent

        adapterEvent?.notifyDataSetChanged()
    }

    override fun onItemClicked(view: View, position: Int, type: Long, string: String) {

    }

}
