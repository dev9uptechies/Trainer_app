package com.example.trainerapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.LessonData
import com.example.Library_EventAdapter
import com.example.Library_ExerciseAdapter
import com.example.Library_LessonAdapter
import com.example.Library_TestAdapter
import com.example.Library_programAdapter
import com.example.OnItemClickListener
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.EventListData
import com.example.trainerapp.ApiClass.ExcerciseData
import com.example.trainerapp.ApiClass.ProgramListData
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.TestListData.testData
import com.example.trainerapp.databinding.ActivityLibraryBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LibraryActivity : AppCompatActivity(), OnItemClickListener.OnItemClickCallback {
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    private lateinit var id: ArrayList<Int>
    private lateinit var lessonData: ArrayList<LessonData.lessionData>
    private lateinit var programListData: ArrayList<ProgramListData.testData>
    private lateinit var eventListData: ArrayList<EventListData.testData>
    private lateinit var exerciseListData: ArrayList<ExcerciseData.Exercise>
    private lateinit var testData: ArrayList<testData>
    lateinit var adapter_lesson: Library_LessonAdapter
    lateinit var adapter_program: Library_programAdapter
    lateinit var adapter_event: Library_EventAdapter
    lateinit var adapter_exercise: Library_ExerciseAdapter
    lateinit var adapter_test: Library_TestAdapter
    lateinit var preferencesManager: PreferencesManager
    lateinit var libraryBinding: ActivityLibraryBinding

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
//                        GetProgram()
//                        libraryBinding.cardProgram.setCardBackgroundColor(resources.getColor(R.color.splash_text_color))
//                        libraryBinding.cardLesson.setCardBackgroundColor(resources.getColor(R.color.light_grey))
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@LibraryActivity)
                    } else {
                        Toast.makeText(
                            this@LibraryActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Toast.makeText(
                        this@LibraryActivity,
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
        checkUser()
        super.onResume()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        libraryBinding = ActivityLibraryBinding.inflate(layoutInflater)
        setContentView(libraryBinding.root)

        try {

        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        preferencesManager = PreferencesManager(this)

        libraryBinding.cardLesson.setOnClickListener {
            GetLessionList()
            libraryBinding.cardLesson.setCardBackgroundColor(resources.getColor(R.color.splash_text_color))
            libraryBinding.cardLesson.setCardBackgroundColor(resources.getColor(R.color.light_grey))
        }
        libraryBinding.cardProgram.setOnClickListener {
            GetProgram()
            libraryBinding.cardProgram.setCardBackgroundColor(resources.getColor(R.color.splash_text_color))
            libraryBinding.cardLesson.setCardBackgroundColor(resources.getColor(R.color.light_grey))
        }
        libraryBinding.cardEvent.setOnClickListener {
            getEvent()
            libraryBinding.cardEvent.setCardBackgroundColor(resources.getColor(R.color.splash_text_color))
            libraryBinding.cardLesson.setCardBackgroundColor(resources.getColor(R.color.light_grey))

        }
        libraryBinding.cardExercise.setOnClickListener {
            GetExerciseList()
            libraryBinding.cardExercise.setCardBackgroundColor(resources.getColor(R.color.splash_text_color))
            libraryBinding.cardLesson.setCardBackgroundColor(resources.getColor(R.color.light_grey))
        }
        libraryBinding.cardTest.setOnClickListener {
            GetTestList()
            libraryBinding.cardExercise.setCardBackgroundColor(resources.getColor(R.color.splash_text_color))
            libraryBinding.cardLesson.setCardBackgroundColor(resources.getColor(R.color.light_grey))
        }

        }catch (e:Exception){
            Log.d("Catch OnC","Catch Run")
        }
    }

    private fun GetTestList() {
        try {

        apiInterface.GetTest()?.enqueue(object : Callback<TestListData?> {
            override fun onResponse(call: Call<TestListData?>, response: Response<TestListData?>) {
                Log.d("TAG", response.code().toString() + "")
                val code = response.code()
                if (code == 200) {
                    val resource: TestListData? = response.body()
                    val Success: Boolean = resource?.status!!
                    val Message: String = resource.message!!
                    if (Success) {
                        testData = resource.data!!
                        if (resource.data!!.isNotEmpty()) {
                            libraryBinding.rcvLibrarylist.layoutManager =
                                LinearLayoutManager(this@LibraryActivity)
                            adapter_test = Library_TestAdapter(
                                resource.data!!,
                                this@LibraryActivity,
                                this@LibraryActivity
                            )
                            libraryBinding.rcvLibrarylist.adapter = adapter_test
                        } else {
                            Toast.makeText(
                                this@LibraryActivity,
                                "No data found",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else if (response.code() == 403) {
                    Utils.setUnAuthDialog(this@LibraryActivity)
                } else {
                    val message = response.message()
                    Toast.makeText(this@LibraryActivity, "" + message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<TestListData?>, t: Throwable) {
                Toast.makeText(
                    this@LibraryActivity,
                    "" + t.message,
                    Toast.LENGTH_SHORT
                ).show()
                call.cancel()
            }

        })

        }catch (e:Exception){
            Log.d("Catch","Catch Run")
        }
    }

    private fun GetLessionList() {
        try {
        apiInterface.GetLession()?.enqueue(object : Callback<LessonData?> {
            override fun onResponse(call: Call<LessonData?>, response: Response<LessonData?>) {
                Log.d("TAG", response.code().toString() + "")
                val code = response.code()
                if (code == 200) {
                    val resource: LessonData? = response.body()
                    val Success: Boolean = resource?.status!!
                    val Message: String = resource.message!!
                    if (Success) {
                        lessonData = resource.data!!

                        if (resource.data!!.isNotEmpty()) {


                            libraryBinding.rcvLibrarylist.layoutManager =
                                LinearLayoutManager(this@LibraryActivity)
                            adapter_lesson =
                                Library_LessonAdapter(
                                    resource.data!!,
                                    this@LibraryActivity,
                                    this@LibraryActivity
                                )
                            Log.e("MMMMM", "onResponse: "+resource.data!!.get(0).user_id )
                            libraryBinding.rcvLibrarylist.adapter = adapter_lesson
                        } else {
                            Toast.makeText(
                                this@LibraryActivity,
                                "No data found",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else if (response.code() == 403) {
                    Utils.setUnAuthDialog(this@LibraryActivity)
                } else {
                    val message = response.message()
                    Toast.makeText(this@LibraryActivity, "" + message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<LessonData?>, t: Throwable) {
                Toast.makeText(
                    this@LibraryActivity,
                    "" + t.message,
                    Toast.LENGTH_SHORT
                ).show()
                call.cancel()
            }
        })

        }catch (e:Exception){
            Log.d("Catch","Catch Run")
        }
    }

    private fun GetProgram() {
        try {
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
                    if (Success) {
                        programListData = resource.data!!
                        if (resource.data!!.isNotEmpty()) {
                            libraryBinding.rcvLibrarylist.layoutManager =
                                LinearLayoutManager(this@LibraryActivity)
                            adapter_program =
                                Library_programAdapter(
                                    resource.data!!,
                                    this@LibraryActivity,
                                    this@LibraryActivity
                                )
                            libraryBinding.rcvLibrarylist.adapter = adapter_program
                        } else {
                            Toast.makeText(
                                this@LibraryActivity,
                                "No data found",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else if (response.code() == 403) {
                    Utils.setUnAuthDialog(this@LibraryActivity)
                } else {
                    val message = response.message()
                    Toast.makeText(this@LibraryActivity, "" + message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<ProgramListData?>, t: Throwable) {
                Toast.makeText(
                    this@LibraryActivity,
                    "" + t.message,
                    Toast.LENGTH_SHORT
                ).show()
                call.cancel()
            }
        })

        }catch (e:Exception){
            Log.d("Catch","Catch Run")
        }
    }

    private fun getEvent() {
        try {
        apiInterface.GetEvent()?.enqueue(object : Callback<EventListData?> {
            override fun onResponse(
                call: Call<EventListData?>,
                response: Response<EventListData?>
            ) {

                val code = response.code()
                if (code == 200) {
                    val resource: EventListData? = response.body()
                    val Success: Boolean = resource?.status!!
                    val Message: String = resource.message!!
                    if (Success) {
                        eventListData = resource.data!!
                        if (resource.data!!.isNotEmpty()) {
                            libraryBinding.rcvLibrarylist.layoutManager =
                                LinearLayoutManager(this@LibraryActivity)
                            adapter_event =
                                Library_EventAdapter(
                                    resource.data!!,
                                    this@LibraryActivity,
                                    this@LibraryActivity
                                )
                            libraryBinding.rcvLibrarylist.adapter = adapter_event
                        } else {
                            Toast.makeText(
                                this@LibraryActivity,
                                "No data found",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else if (response.code() == 403) {
                    Utils.setUnAuthDialog(this@LibraryActivity)
                } else {
                    val message = response.message()
                    Toast.makeText(this@LibraryActivity, "" + message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<EventListData?>, t: Throwable) {
                Toast.makeText(
                    this@LibraryActivity,
                    "" + t.message,
                    Toast.LENGTH_SHORT
                ).show()
                call.cancel()
            }
        })

        }catch (e:Exception){
            Log.d("Catch","Catch Run")
        }
    }

    private fun GetExerciseList() {
        try {

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
                    if (Success) {
                        exerciseListData = resource.data!!
                        if (resource.data!!.isNotEmpty()) {
                            libraryBinding.rcvLibrarylist.layoutManager =
                                LinearLayoutManager(this@LibraryActivity)
                            adapter_exercise =
                                Library_ExerciseAdapter(
                                    resource.data!!,
                                    this@LibraryActivity,
                                    this@LibraryActivity
                                )
                            libraryBinding.rcvLibrarylist.adapter = adapter_exercise
                        } else {
                            Toast.makeText(
                                this@LibraryActivity,
                                "No data found",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else if (response.code() == 403) {
                    Utils.setUnAuthDialog(this@LibraryActivity)
                } else {
                    val message = response.message()
                    Toast.makeText(this@LibraryActivity, "" + message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<ExcerciseData?>, t: Throwable) {

                Toast.makeText(
                    this@LibraryActivity,
                    "" + t.message,
                    Toast.LENGTH_SHORT
                ).show()
                call.cancel()
            }
        })

        }catch (e:Exception){
            Log.d("Catch","Catch Run")
        }
    }

    override fun onItemClicked(view: View, position: Int, type: Long, string: String) {
    }
}