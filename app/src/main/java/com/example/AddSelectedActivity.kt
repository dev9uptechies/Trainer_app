package com.example

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.Adapter.selected_day.AddSelectedDayAdapter
import com.example.Adapter.selected_day.AddSelectedDayEventApater
import com.example.Adapter.selected_day.AddSelectedDayTestAdapter
import com.example.model.AddSelectDayModel
import com.example.model.SelectedDaysModel
import com.example.model.newClass.lesson.Lesson
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.EventListData
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.R
import com.example.trainerapp.TestListData
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.ActivityAddSelectedBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddSelectedActivity : AppCompatActivity(),OnItemClickListener.OnItemClickCallback {
    lateinit var binding: ActivityAddSelectedBinding
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    private lateinit var lession_data1: ArrayList<Lesson.LessonDatabase>
    lateinit var adapter: AddSelectedDayAdapter
    lateinit var adapterEvent: AddSelectedDayEventApater
    lateinit var adapterTest: AddSelectedDayTestAdapter
    lateinit var EventList: ArrayList<EventListData.testData>
    var selectedLessonID:String = ""
    var selectedLessonDATE:String = ""
    var selectedEventID:String = ""
    var selectedEventDATE:String = ""
    var selectedTestID:String = ""
    var selectedTestDATE:String = ""
    var type : String = ""
    var date : String = ""
    var groupId:Int = 0


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
                        lordData()
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@AddSelectedActivity)
                    } else {
                        Toast.makeText(
                            this@AddSelectedActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Toast.makeText(
                        this@AddSelectedActivity,
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
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddSelectedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        ButtonClick()
        lordData()
    }

    private fun lordData() {
        when(type){
            "lesson" -> {
                binding.tvCredit.text = getString(R.string.lessonList)
                GetLessionList()
            }
            "test" -> {
                binding.tvCredit.text = getString(R.string.testList)
                GetTestList()
            }
            "event" -> {
                binding.tvCredit.text = getString(R.string.eventList)
                geteventlist()
            }
        }
    }

    private fun ButtonClick() {

        binding.back.setOnClickListener { finish() }

        when(type){

            "lesson" -> {
                binding.cardSave.setOnClickListener {
                    selectedLessonID = adapter.getSelectedLessonData().first.toString()
                    selectedLessonDATE = adapter.getSelectedLessonData().second.toString().take(10)
                    Log.d("SelectedGroup", "Selected Group ID: $selectedLessonID ---  $selectedLessonDATE")

                    AddSelectDayData()

                }


                binding.edtSearch.addTextChangedListener { text ->
                    val query = text?.toString() ?: ""
                    adapter.filter(query)
                }

            }

            "event" -> {
                binding.cardSave.setOnClickListener {
                    selectedEventID = adapterEvent.getSelectedEVentData().first.toString()
                    selectedEventDATE = adapterEvent.getSelectedEVentData().second.toString().take(10)

                    Log.d("SelectedGroup", "Selected Group ID: $selectedEventID")
                    AddSelectDayData()


                }

                binding.edtSearch.addTextChangedListener { text ->
                    val query = text?.toString() ?: ""
                    adapterEvent.filter(query)
                }
            }

            "test" -> {
                binding.cardSave.setOnClickListener {
                    selectedTestID = adapterTest.getSelectedTestData().first.toString()
                    selectedTestDATE = adapterTest.getSelectedTestData().second.toString().take(10)

                    Log.d("ASSBBDBDBDBDBBDBDBD", "Selected Group ID: $selectedTestID")
                    Log.d("ASSBBDBDBDBDBBDBDBD", "Selected Group ID: $selectedTestDATE")
                    AddSelectDayData()

                }

                binding.edtSearch.addTextChangedListener { text ->
                    val query = text?.toString() ?: ""
                    adapterTest.filter(query)
                }
            }

        }
    }

    private fun initView() {
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        lession_data1 = ArrayList()

        type = intent.getStringExtra("Add").toString()

        date = intent.getStringExtra("Date").toString()
        groupId = intent.getIntExtra("GroupId",0)

        Log.d("idok","IDDD:-    $date")
        Log.d("idok","IDDD:-    $groupId")

    }

    private fun GetLessionList() {
        binding.progresBar.visibility = View.VISIBLE
        apiInterface.GetLession1().enqueue(object : Callback<Lesson> {
            override fun onResponse(call: Call<Lesson>, response: Response<Lesson>) {
                Log.d("TAG Lesson:", response.code().toString() + "")
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
                                lession_data1.addAll(data)
                                initRecyclerview(data)
                            } else {
                                Toast.makeText(this@AddSelectedActivity, "No lessons available", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this@AddSelectedActivity, "Error: $message", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@AddSelectedActivity, "Empty response body", Toast.LENGTH_SHORT).show()
                    }
                } else if (code == 403) {
                    Utils.setUnAuthDialog(this@AddSelectedActivity)
                } else {
                    Toast.makeText(this@AddSelectedActivity, response.message(), Toast.LENGTH_SHORT).show()
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<Lesson>, t: Throwable) {
                Toast.makeText(this@AddSelectedActivity, "Error: " + t.message, Toast.LENGTH_SHORT).show()
                call.cancel()
            }
        })
    }

    private fun geteventlist() {
        binding.progresBar.visibility = View.VISIBLE
        apiInterface.GetEvent()?.enqueue(object : Callback<EventListData?> {
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
                            initrecycler(resource.data)
                        } else {
                            initrecycler(arrayListOf())
                        }
                    } else {
                        binding.progresBar.visibility = View.GONE
                    }
                } else if (code == 403) {
                    Utils.setUnAuthDialog(this@AddSelectedActivity)
                } else {
                    val Message = response.message()
                    Toast.makeText(
                        this@AddSelectedActivity,
                        "" + Message,
                        Toast.LENGTH_SHORT
                    ).show()
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<EventListData?>, t: Throwable) {
                binding.progresBar.visibility = View.GONE
                Toast.makeText(this@AddSelectedActivity, "" + t.message, Toast.LENGTH_SHORT)
                    .show()
                call.cancel()
            }
        })
    }

    private fun GetTestList() {
        binding.progresBar.visibility = View.VISIBLE
        apiInterface.GetTest()?.enqueue(object : Callback<TestListData?> {
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
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        binding.progresBar.visibility = View.GONE
                    }
                } else if (response.code() == 403) {
                    Utils.setUnAuthDialog(this@AddSelectedActivity)

                } else {
                    val message = response.message()
                    Toast.makeText(this@AddSelectedActivity, "" + message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<TestListData?>, t: Throwable) {
                binding.progresBar.visibility = View.GONE
                Toast.makeText(this@AddSelectedActivity, "" + t.message, Toast.LENGTH_SHORT)
                    .show()
                call.cancel()
            }
        })

    }

    private fun AddSelectDayData() {
        try {

            if (date != selectedTestDATE && date != selectedEventDATE && date != selectedLessonDATE) {
                Toast.makeText(this@AddSelectedActivity, "Date Not Match", Toast.LENGTH_SHORT).show()
                return
            }

            Log.d("APIRequest", "selectedEventID: $selectedEventID, selectedLessonID: $selectedLessonID, selectedTestID: $selectedTestID")

            val eventIds = if (selectedEventID.isNotEmpty()) listOf(selectedEventID.toInt()) else null
            val lessonIds = if (selectedLessonID.isNotEmpty()) listOf(selectedLessonID.toInt()) else null
            val testIds = if (selectedTestID.isNotEmpty()) listOf(selectedTestID.toInt()) else null

            Log.d("APIRequest", "Post Request: date = $date, group_id = $groupId, event_ids = $eventIds, lesson_ids = $lessonIds, test_ids = $testIds")

            val postRequest = AddSelectDayModel(
                date = date,
                group_id = groupId,
                event_ids = eventIds,
                lession_ids = lessonIds,
                test_ids = testIds
            )

            apiInterface.AddSelectedDayData(postRequest)?.enqueue(object : Callback<SelectedDaysModel> {
                override fun onResponse(call: Call<SelectedDaysModel>, response: Response<SelectedDaysModel>) {
                    Log.d("APIResponse", "Response code: ${response.code()} - ${response.errorBody()?.string()}")

                    if (response.isSuccessful) {
                        response.body()?.let { responseBody ->
                            Log.d("ASASASASASASASASS", "Added data: $responseBody")
                        }

                        Toast.makeText(this@AddSelectedActivity, "Data Added Successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        val errorBody = response.errorBody()?.string() ?: "Unknown error"
                        Log.e("API Error", "Error response: ${response.code()} - $errorBody")
                        Toast.makeText(this@AddSelectedActivity, "Error: $errorBody", Toast.LENGTH_SHORT).show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<SelectedDaysModel>, t: Throwable) {
                    Log.e("API Error", "Request failed: ${t.message}")
                    Toast.makeText(this@AddSelectedActivity, "API call failed: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } catch (e: Exception) {
            Log.e("API Error", "Exception during API request: ${e.message}")
            Toast.makeText(this@AddSelectedActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }


    private fun initrecycler(testdatalist: ArrayList<EventListData.testData>?) {
        binding.progresBar.visibility = View.GONE
        binding.lessonRly.layoutManager = LinearLayoutManager(this)
        adapterEvent = AddSelectedDayEventApater(testdatalist, this, this)
        binding.lessonRly.adapter = adapterEvent
    }

    private fun initRecyclerview(data: ArrayList<Lesson.LessonDatabase>) {
        binding.lessonRly.layoutManager = LinearLayoutManager(this)
        adapter = AddSelectedDayAdapter(data, this, this)
        binding.lessonRly.adapter = adapter
    }

    private fun initrecyclerTest(testdatalist: ArrayList<TestListData.testData>?) {
        binding.progresBar.visibility = View.GONE
        binding.lessonRly.layoutManager = LinearLayoutManager(this)
        adapterTest = AddSelectedDayTestAdapter(testdatalist, this, this)
        binding.lessonRly.adapter = adapterTest
    }

    override fun onItemClicked(view: View, position: Int, type: Long, string: String) {
    }

}