package com.example

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.marginBottom
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.Adapter.groups.GetAthleteListAdapterGroup
import com.example.Adapter.groups.GetEventListAdapterGroup
import com.example.Adapter.groups.GetLessonListAdapterGroup
import com.example.Adapter.groups.GetPlanningListAdapterGroup
import com.example.Adapter.groups.GetTestListAdapterGroup
import com.example.model.newClass.athlete.AthleteData
import com.example.model.newClass.lesson.GetLessonRequest
import com.example.model.newClass.lesson.Lesson
import com.example.model.newClass.lesson.LessonRequest
import com.example.model.training_plan.TrainingPlanData
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.EventListData
import com.example.trainerapp.ApiClass.ProgramListData
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.EditGroupActivity
import com.example.trainerapp.R
import com.example.trainerapp.TestListData
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.ActivityLessonListBinding
import com.example.trainerapp.training_plan.AddTrainingPlanActivity
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LessonListActivity : AppCompatActivity(), OnItemClickListener.OnItemClickCallback {

    private lateinit var binding: ActivityLessonListBinding
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    private lateinit var lessonData: ArrayList<Lesson.LessonDatabase>
    lateinit var adapter: GetLessonListAdapterGroup
    lateinit var adapterProgram: GetProgramListAdapterGroup
    var selectedLessonID: String = ""
    var type: String = ""

    val selectedAthletes = mutableListOf<Int>()


    var typeEdit: String = ""
    lateinit var EventList: ArrayList<EventListData.testData>
    lateinit var programData: MutableList<ProgramListData.testData>
    lateinit var plainngData: MutableList<TrainingPlanData.TrainingPlan>
    lateinit var athleteData: MutableList<AthleteData.Athlete>
    lateinit var adapterEvent: GetEventListAdapterGroup
    lateinit var adapterTest: GetTestListAdapterGroup
    lateinit var adapterPlanning: GetPlanningListAdapterGroup
    lateinit var adapterAthlete: GetAthleteListAdapterGroup

    var LessonID: Int? = null
    var AthleteIDS: String? = null


    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLessonListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        lordData()
        ButtonClick()
        GetLessonAttendeances()
    }

    private fun lordData() {
        when (type) {
            "lesson" -> {
                binding.tvCredit.text = getString(R.string.lessonList)
                GetLessonList()
            }

            "test" -> {
                binding.tvCredit.text = getString(R.string.testList)
                GetTestList()
            }

            "event" -> {
                binding.tvCredit.text = getString(R.string.eventList)
                geteventlist()
            }

            "planning" -> {
                binding.tvCredit.text = getString(R.string.planningList)
                binding.addLayout.visibility = View.VISIBLE
                getTrainingData()
            }

            "athlete" -> {
                binding.tvCredit.text = getString(R.string.athleteList)
                getAthleteData()
            }

            "ConfirmAttendance" -> {
                binding.tvCredit.text = getString(R.string.selectAthlete)
                getAthleteData()
            }
        }

        when (typeEdit) {
            "lesson" -> {
                binding.tvCredit.text = getString(R.string.lessonList)
                GetLessonList()
            }

            "test" -> {
                binding.tvCredit.text = getString(R.string.testList)
                GetTestList()
            }

            "event" -> {
                binding.tvCredit.text = getString(R.string.eventList)
                geteventlist()
            }

            "planning" -> {
                binding.tvCredit.text = getString(R.string.planningList)
                binding.addLayout.visibility = View.VISIBLE
                getTrainingData()
            }

            "athlete" -> {
                binding.tvCredit.text = getString(R.string.athleteList)
                getAthleteData()
            }

            "ConfirmAttendance" -> {
                binding.tvCredit.text = getString(R.string.selectAthlete)
                getAthleteData()
            }

        }
    }

    private fun ButtonClick() {
        binding.back.setOnClickListener { finish() }

        binding.addLayout.setOnClickListener {
            val intent = Intent(this, AddTrainingPlanActivity::class.java)
            startActivity(intent)
        }

        binding.saveBtn.setOnClickListener {
            when (type) {
                "test" -> {
                    val selecteTestIds = adapterTest.getSelectedTestData()
                    Log.d("SelectedTestIds", "Selected IDs: $selecteTestIds")

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

                "lesson" -> {
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

                    // Safely convert to array only if data exists
                    val lessonIdArray = selectedLessonIds.mapNotNull { it }.toIntArray()

                    val intent = Intent(this, CreateGropActivity::class.java)
                    intent.putExtra("lessonId", lessonIdArray)
                    intent.putExtra("load", "load")
                    startActivity(intent)
                    finish()
                }


                "event" -> {
                    val selecteTestIds = adapterEvent.getSelectedEventData()
                    Log.d("SelectedEventIds", "Selected IDs: $selecteTestIds")

                    if (selecteTestIds.isNullOrEmpty()) {
                        Toast.makeText(
                            this,
                            "Please select a valid event before saving",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }

                    val intent = Intent(this, CreateGropActivity::class.java)
                    intent.putExtra("eventId", selecteTestIds.toIntArray())
                    startActivity(intent)
                    finish()
                }

                "planning" -> {
                    val selecteTestIds = adapterPlanning.getSelectedPlanningData()
                    Log.d("SelectedPlanningIds", "Selected IDs: $selecteTestIds")

                    if (selecteTestIds.isNullOrEmpty()) {
                        Toast.makeText(
                            this,
                            "Please select a valid planning before saving",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }

                    val intent = Intent(this, CreateGropActivity::class.java)
                    intent.putExtra("planningId", selecteTestIds.toIntArray())
                    startActivity(intent)
                    finish()
                }

                "athlete" -> {
                    val selecteTestIds = adapterAthlete.getSelectedAthleteData()
                    Log.d("SelectedAthleteIds", "Selected IDs: $selecteTestIds")

                    if (selecteTestIds.isNullOrEmpty()) {
                        Toast.makeText(
                            this,
                            "Please select a valid athlete before saving",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }

                    val intent = Intent(this, CreateGropActivity::class.java)
                    intent.putExtra("athleteId", selecteTestIds.toIntArray())
                    startActivity(intent)
                    finish()
                }

                "ConfirmAttendance" -> {
                    val selecteTestIds = adapterAthlete.getSelectedAthleteData()
                    Log.d("SelectedAthleteIds", "Selected IDs: $selecteTestIds")

                    AthleteIDS = adapterAthlete.getSelectedAthleteData().toString()

                    Log.d("DD:D:D::", "ButtonClick: $AthleteIDS")

                    if (selecteTestIds.isNullOrEmpty()) {
                        Toast.makeText(
                            this,
                            "Please select a valid athlete before saving",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }

                    val cleanedAthleteIDS = AthleteIDS?.replace("\\s".toRegex(), "")
                    Log.d("DEBUG", "Raw AthleteIDS: $AthleteIDS")
                    Log.d("DEBUG", "Cleaned AthleteIDS: $cleanedAthleteIDS")

                    val cleanedIDsWithoutBrackets = cleanedAthleteIDS?.removePrefix("[")?.removeSuffix("]")

                    val athleteIDList: List<Int>? = cleanedIDsWithoutBrackets
                        ?.split(",")
                        ?.mapNotNull { it.trim().toIntOrNull() }  // trim any extra spaces before converting to Int

                    Log.d("DEBUG", "Converted Athlete ID List: $athleteIDList")

                    if (!athleteIDList.isNullOrEmpty()) {
                        Log.d("DEBUG", "Sending Lesson Data with IDs: $athleteIDList")
                        sendLessonData(LessonID!!.toInt(), athleteIDList)
                    } else {
                        Toast.makeText(this, "Invalid athlete IDs. Please check the input.", Toast.LENGTH_SHORT).show()
                    }

                }
            }

            when (typeEdit) {
                "lesson" -> {
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
                    intent.putExtra("load", "load")
                    startActivity(intent)
                    finish()
                }

                "event" -> {
                    val selecteTestIds = adapterEvent.getSelectedEventData()
                    Log.d("SelectedEventIds", "Selected IDs: $selecteTestIds")

                    if (selecteTestIds.isNullOrEmpty()) {
                        Toast.makeText(
                            this,
                            "Please select a valid event before saving",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }

                    val intent = Intent(this, EditGroupActivity::class.java)
                    intent.putExtra("eventId", selecteTestIds.toIntArray())
                    startActivity(intent)
                    finish()
                }

                "test" -> {
                    val selecteTestIds = adapterTest.getSelectedTestData()
                    Log.d("SelectedTestIds", "Selected IDs: $selecteTestIds")

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

                "planning" -> {
                    val selecteTestIds = adapterPlanning.getSelectedPlanningData()
                    Log.d("SelectedPlanningIds", "Selected IDs: $selecteTestIds")

                    if (selecteTestIds.isNullOrEmpty()) {
                        Toast.makeText(
                            this,
                            "Please select a valid planning before saving",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }

                    val intent = Intent(this, EditGroupActivity::class.java)
                    intent.putExtra("planningId", selecteTestIds.toIntArray())
                    startActivity(intent)
                    finish()
                }

                "athlete" -> {
                    val selecteTestIds = adapterAthlete.getSelectedAthleteData()
                    Log.d("SelectedAthleteIds", "Selected IDs: $selecteTestIds")

                    if (selecteTestIds.isNullOrEmpty()) {
                        Toast.makeText(
                            this,
                            "Please select a valid athlete before saving",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }

                    val intent = Intent(this, EditGroupActivity::class.java)
                    intent.putExtra("athleteId", selecteTestIds.toIntArray())
                    startActivity(intent)
                    finish()
                }
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
        LessonID = intent.getIntExtra("LessonID", 0)


        Log.d("GHGHHG", "initView: $LessonID")
        Log.d("GHGHHG", "initView: $type")

        Log.d("type", "IDDD:-    $typeEdit")
    }

    fun sendLessonData(lessonID: Int?, athleteIDs:  List<Int>) {
        val request = LessonRequest(
            lesson_id = lessonID ?: 0,
            athlete_ids = athleteIDs
        )

        Log.d("DTRRRTRYY", "sendLessonData: $request")

        apiInterface.sendLessonAttendance(request).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d("Retrofit", "Success: ${response.body()?.string()}")
                    finish()
                } else {
                    Log.e("Retrofit", "Errordd: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Retrofit", "Failure: ${t.message}")
            }
        })
    }

    private fun GetLessonAttendeances() {
        binding.progresBar.visibility = View.VISIBLE
        apiInterface.GetLessonAttendance(LessonID.toString()).enqueue(object : Callback<GetLessonRequest> {
            @SuppressLint("NewApi")
            override fun onResponse(call: Call<GetLessonRequest>, response: Response<GetLessonRequest>) {
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
                                binding.saveBtn.isEnabled = true
                                Log.d("KDKDKDK", "onResponse: $data")
                                Log.d("KDKDKDK", "onResponse: Athlete IDs")
                                data?.forEach { item ->
                                    Log.d("KDKDKDK", "onResponse: ${item.athlete_id}")
                                    item.athlete_id?.let {
                                        selectedAthletes.add(it.toInt())
                                    }
                                }

                            } else {
                                binding.saveBtn.isEnabled = false
                                Toast.makeText(
                                    this@LessonListActivity,
                                    "No lessons available",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
//                            Toast.makeText(
//                                this@LessonListActivity,
//                                "Error: $message",
//                                Toast.LENGTH_SHORT
//                            ).show()

                            Log.d("ERRRORRR:", "onResponse: $message")
                        }
                    } else {
                        Toast.makeText(
                            this@LessonListActivity,
                            "Empty response body",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else if (code == 403) {
                    Utils.setUnAuthDialog(this@LessonListActivity)
                } else {
                    Toast.makeText(this@LessonListActivity, response.message(), Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<GetLessonRequest>, t: Throwable) {
                Log.d("LDLDLLDLDL", "onFailure: ${t.message}")
                call.cancel()
            }
        })
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
                                binding.saveBtn.isEnabled = true
                                plainngData = data.toMutableList()
//                                setAdapter(trainingData)
                                initrecyclerplanning(plainngData)

                            } else {

                            }
                        } else {
                            binding.saveBtn.isEnabled = false
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
                            binding.saveBtn.isEnabled = true

                            EventList = resource.data!!
                            resource.data!!.forEach {
                                Log.d("EventTitle", it.title ?: "No title")
                            }
                            initrecycler(resource.data)
                        } else {
                            binding.saveBtn.isEnabled = false
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
                                binding.saveBtn.isEnabled = true
                                Log.d("Lesson Data :-", "$success $message \t $data")
                                lessonData.addAll(data)
                                initRecyclerview(data)
                            } else {
                                binding.saveBtn.isEnabled = false
                                Toast.makeText(
                                    this@LessonListActivity,
                                    "No lessons available",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                this@LessonListActivity,
                                "Error: $message",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@LessonListActivity,
                            "Empty response body",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else if (code == 403) {
                    Utils.setUnAuthDialog(this@LessonListActivity)
                } else {
                    Toast.makeText(this@LessonListActivity, response.message(), Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<Lesson>, t: Throwable) {
                Toast.makeText(this@LessonListActivity, "Error: " + t.message, Toast.LENGTH_SHORT)
                    .show()
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
                                binding.saveBtn.isEnabled = true

                                initrecyclerTest(resource.data)
                            } else {
                                binding.saveBtn.isEnabled = false
                                binding.saveBtn.isClickable = false

                                Toast.makeText(
                                    this@LessonListActivity,
                                    "No Test available",
                                    Toast.LENGTH_SHORT
                                ).show()
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
                            binding.saveBtn.isEnabled = true

                            if (data != null) {
                                athleteData.addAll(data)

                                Log.d("KFOKKOKOJUGYTFTF", "onResponse: $selectedAthletes")
                                initrecyclerAthlete(athleteData, selectedAthletes.toMutableSet())
                            }
                        } else {
                            binding.saveBtn.isEnabled = false
                            Toast.makeText(
                                this@LessonListActivity,
                                "No Athlete available",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        for (datas in athleteData) {
                            Log.d("sujals", "ID:- " + datas.id)
                        }
                    } else if (response.code() == 403) {
                        Utils.setUnAuthDialog(this@LessonListActivity)
                    } else {
                        Toast.makeText(
                            this@LessonListActivity,
                            response.message(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<AthleteData>, t: Throwable) {
                binding.progresBar.visibility = View.GONE
                Toast.makeText(this@LessonListActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun initrecyclerAthlete(testdatalist: MutableList<AthleteData.Athlete>, selectedAthletes: MutableSet<Int>) {
        binding.progresBar.visibility = View.GONE
        binding.recycler.layoutManager = LinearLayoutManager(this)

        Log.d("OOOUHYGF", "initrecyclerAthlete: $selectedAthletes")

        // Pass selectedAthletes to the adapter so the checkboxes can be pre-selected
        adapterAthlete = GetAthleteListAdapterGroup(testdatalist, this, this,null ,selectedAthletes)

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

    override fun onResume() {
        super.onResume()
        lordData()
    }

    override fun onItemClicked(view: View, position: Int, type: Long, string: String) {

        if (string == "DeletePlanning") {
            val dialog = Dialog(this)
            val view = LayoutInflater.from(this).inflate(R.layout.delete_layout, null)
            dialog.setContentView(view)
            dialog.setCancelable(false)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val title = view.findViewById<TextView>(R.id.dialogTitle)
            val editTitle = view.findViewById<TextView>(R.id.editTextTitle)
            val addButton = view.findViewById<TextView>(R.id.okButton)
            val cancelButton = view.findViewById<TextView>(R.id.cancelButton)

            title.text = "Alert"
            editTitle.text = "Are You Sure you want to Delete ?"
            addButton.text = "Ok"
            cancelButton.text = "Cancel"

            addButton.setOnClickListener {
                binding.main.setBackgroundColor(resources.getColor(R.color.black))
                deleteData(type)
                dialog.dismiss()
            }

            cancelButton.setOnClickListener {
                binding.main.setBackgroundColor(resources.getColor(R.color.black))
                dialog.dismiss()
            }
            dialog.show()
        }

        if (string == "DeleteLesson") {
            val dialog = Dialog(this)
            val view = LayoutInflater.from(this).inflate(R.layout.delete_layout, null)
            dialog.setContentView(view)
            dialog.setCancelable(false)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val title = view.findViewById<TextView>(R.id.dialogTitle)
            val editTitle = view.findViewById<TextView>(R.id.editTextTitle)
            val addButton = view.findViewById<TextView>(R.id.okButton)
            val cancelButton = view.findViewById<TextView>(R.id.cancelButton)

            title.text = "Alert"
            editTitle.text = "Are You Sure you want to Delete ?"
            addButton.text = "Ok"
            cancelButton.text = "Cancel"

            addButton.setOnClickListener {
                binding.main.setBackgroundColor(resources.getColor(R.color.black))
                deleteLessonData(type)
                dialog.dismiss()
            }

            cancelButton.setOnClickListener {
                binding.main.setBackgroundColor(resources.getColor(R.color.black))
                dialog.dismiss()
            }
            dialog.show()
        }

        if (string == "DeleteTest") {
            val dialog = Dialog(this)
            val view = LayoutInflater.from(this).inflate(R.layout.delete_layout, null)
            dialog.setContentView(view)
            dialog.setCancelable(false)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val title = view.findViewById<TextView>(R.id.dialogTitle)
            val editTitle = view.findViewById<TextView>(R.id.editTextTitle)
            val addButton = view.findViewById<TextView>(R.id.okButton)
            val cancelButton = view.findViewById<TextView>(R.id.cancelButton)

            title.text = "Alert"
            editTitle.text = "Are You Sure you want to Delete ?"
            addButton.text = "Ok"
            cancelButton.text = "Cancel"

            addButton.setOnClickListener {
                binding.main.setBackgroundColor(resources.getColor(R.color.black))
                deleteTestData(type)
                dialog.dismiss()
            }

            cancelButton.setOnClickListener {
                binding.main.setBackgroundColor(resources.getColor(R.color.black))
                dialog.dismiss()
            }
            dialog.show()
        }

        if (string == "DeleteEvent") {
            val dialog = Dialog(this)
            val view = LayoutInflater.from(this).inflate(R.layout.delete_layout, null)
            dialog.setContentView(view)
            dialog.setCancelable(false)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val title = view.findViewById<TextView>(R.id.dialogTitle)
            val editTitle = view.findViewById<TextView>(R.id.editTextTitle)
            val addButton = view.findViewById<TextView>(R.id.okButton)
            val cancelButton = view.findViewById<TextView>(R.id.cancelButton)

            title.text = "Alert"
            editTitle.text = "Are You Sure you want to Delete ?"
            addButton.text = "Ok"
            cancelButton.text = "Cancel"

            addButton.setOnClickListener {
                binding.main.setBackgroundColor(resources.getColor(R.color.black))
                deleteEventData(type)
                dialog.dismiss()
            }

            cancelButton.setOnClickListener {
                binding.main.setBackgroundColor(resources.getColor(R.color.black))
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    private fun deleteData(type: Long) {
        val id = type.toInt()
        if (id == null) {
            Toast.makeText(this, "Invalid ID", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            binding.progresBar.visibility = View.VISIBLE
            apiInterface.DeletePlanning(id).enqueue(object : Callback<TrainingPlanData> {
                override fun onResponse(
                    call: Call<TrainingPlanData>,
                    response: Response<TrainingPlanData>
                ) {
                    binding.progresBar.visibility = View.GONE
                    Log.d("TAG", response.code().toString() + "")
                    val code = response.code()
                    if (code == 200) {
                        if (response.isSuccessful && response.body() != null) {
                            val message = response.body()?.message ?: "Item deleted"
                            Toast.makeText(this@LessonListActivity, message, Toast.LENGTH_SHORT)
                                .show()
                            lordData()
                        } else {
                            val message = response.body()?.message ?: "Failed to delete"
                            Log.d("delete_tag", "Failed to delete: ${response.code()}")
                            Toast.makeText(
                                this@LessonListActivity,
                                "" + message,
                                Toast.LENGTH_SHORT
                            ).show()
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
                    Log.d("delete_tag", "Error: ${t.message}")
                    Toast.makeText(this@LessonListActivity, t.message, Toast.LENGTH_SHORT).show()
                    call.cancel()
                }
            })
        } catch (e: Exception) {
            binding.progresBar.visibility = View.GONE
            Log.d("Exception", "${e.message}")
        }
    }

    private fun deleteLessonData(type: Long) {
        val id = type.toInt()
        if (id == null) {
            Toast.makeText(this, "Invalid ID", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            binding.progresBar.visibility = View.VISIBLE
            apiInterface.DeleteLession(type.toInt())
                ?.enqueue(object : Callback<RegisterData?> {
                    override fun onResponse(
                        call: Call<RegisterData?>,
                        response: Response<RegisterData?>
                    ) {
                        Log.d("TAG", response.code().toString() + "")
                        binding.progresBar.visibility = View.GONE
                        val code = response.code()
                        if (code == 200) {
                            val resource: RegisterData? = response.body()
                            val Success: Boolean = resource?.status!!
                            val Message: String = resource.message!!
                            binding.progresBar.visibility = View.GONE
                            Toast.makeText(
                                this@LessonListActivity,
                                "" + Message,
                                Toast.LENGTH_SHORT
                            )
                                .show()

                            lordData()

                        } else if (code == 403) {
                            Utils.setUnAuthDialog(this@LessonListActivity)
                        } else {
                            binding.progresBar.visibility = View.GONE
                            Toast.makeText(
                                this@LessonListActivity,
                                "" + response.message(),
                                Toast.LENGTH_SHORT
                            ).show()
                            call.cancel()
                        }

                    }

                    override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                        binding.progresBar.visibility = View.GONE
                        Toast.makeText(
                            this@LessonListActivity,
                            "" + t.message,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                })
        } catch (e: Exception) {
            binding.progresBar.visibility = View.GONE
            Log.d("Exception", "${e.message}")
        }
    }

    private fun deleteTestData(type: Long) {
        val id = type.toInt()
        if (id == null) {
            Toast.makeText(this, "Invalid ID", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            binding.progresBar.visibility = View.VISIBLE
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
                            binding.progresBar.visibility = View.GONE
                            Toast.makeText(
                                this@LessonListActivity,
                                "" + Message,
                                Toast.LENGTH_SHORT
                            )
                                .show()


                            lordData()
                        } else if (response.code() == 403) {
                            Utils.setUnAuthDialog(this@LessonListActivity)
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
                                this@LessonListActivity,
                                "" + message,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            call.cancel()
                        }
                    }

                    override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                        Toast.makeText(
                            this@LessonListActivity,
                            "" + t.message,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                })
        } catch (e: Exception) {
            binding.progresBar.visibility = View.GONE
            Log.d("Exception", "${e.message}")
        }
    }

    private fun deleteEventData(type: Long) {
        val id = type.toInt()
        if (id == null) {
            Toast.makeText(this, "Invalid ID", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            binding.progresBar.visibility = View.VISIBLE
            apiInterface.DeleteEvent(type.toInt())
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
                            binding.progresBar.visibility = View.GONE
                            Toast.makeText(
                                this@LessonListActivity,
                                "" + Message,
                                Toast.LENGTH_SHORT
                            ).show()
                            lordData()
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

                    override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                        Toast.makeText(
                            this@LessonListActivity,
                            "" + t.message,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                })
        } catch (e: Exception) {
            binding.progresBar.visibility = View.GONE
            Log.d("Exception", "${e.message}")
        }
    }


}