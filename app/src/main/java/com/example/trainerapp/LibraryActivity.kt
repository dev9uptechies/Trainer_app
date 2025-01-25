package com.example.trainerapp

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.Create_Event_Activity
import com.example.EdiExerciseActivity
import com.example.EditProgramActivity
import com.example.ExerciseActivity
import com.example.LessonActivity
import com.example.LessonData
import com.example.Library_EventAdapter
import com.example.Library_ExerciseAdapter
import com.example.Library_LessonAdapter
import com.example.Library_TestAdapter
import com.example.Library_programAdapter
import com.example.OnItemClickListener
import com.example.TestActivity
import com.example.ViewLessonActivity
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.EventListData
import com.example.trainerapp.ApiClass.ExcerciseData
import com.example.trainerapp.ApiClass.ProgramListData
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.TestListData.testData
import com.example.trainerapp.databinding.ActivityLibraryBinding
import com.example.trainerapp.program_section.New_Program_Activity
import com.example.trainerapp.program_section.ViewProgramActivity
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LibraryActivity : AppCompatActivity(), OnItemClickListener.OnItemClickCallback {
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient

    private var lessonData: ArrayList<LessonData.lessionData> = ArrayList()
    private var programListData: ArrayList<ProgramListData.testData> = ArrayList()
    private var eventListData: ArrayList<EventListData.testData> = ArrayList()
    private var exerciseListData: ArrayList<ExcerciseData.Exercise> = ArrayList()
    private var testData: ArrayList<testData> = ArrayList()
    private var id: ArrayList<Int> = ArrayList()

    lateinit var adapter_lesson: Library_LessonAdapter
    lateinit var adapter_program: Library_programAdapter
    lateinit var adapter_event: Library_EventAdapter
    lateinit var adapter_exercise: Library_ExerciseAdapter
    lateinit var adapter_test: Library_TestAdapter

    lateinit var preferencesManager: PreferencesManager
    lateinit var libraryBinding: ActivityLibraryBinding
    var typeData = "create"

    private fun checkUser() {
        try {
            apiInterface.ProfileData()?.enqueue(object : Callback<RegisterData?> {
                override fun onResponse(
                    call: Call<RegisterData?>, response: Response<RegisterData?>
                ) {
                    Log.d("TAG", response.code().toString() + "")
                    val code = response.code()
                    if (code == 200) {
                        Log.d("Get Profile Data ", "${response.body()}")
                        GetProgram()
                        libraryBinding.cardProgram.setCardBackgroundColor(resources.getColor(R.color.splash_text_color))
                        libraryBinding.cardLesson.setCardBackgroundColor(resources.getColor(R.color.light_grey))
                        libraryBinding.cardTest.setCardBackgroundColor(resources.getColor(R.color.light_grey))
                        libraryBinding.cardEvent.setCardBackgroundColor(resources.getColor(R.color.light_grey))
                        libraryBinding.cardExercise.setCardBackgroundColor(resources.getColor(R.color.light_grey))

                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@LibraryActivity)
                    } else {
                        Toast.makeText(
                            this@LibraryActivity, "" + response.message(), Toast.LENGTH_SHORT
                        ).show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Toast.makeText(
                        this@LibraryActivity, "" + t.message, Toast.LENGTH_SHORT
                    ).show()
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

        lessonData = ArrayList()
        programListData = ArrayList()
        eventListData = ArrayList()
        exerciseListData = ArrayList()
        testData = ArrayList()

        libraryBinding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchQuery = s.toString().trim()
                if (searchQuery.isNotEmpty()) {
                    filterData1(searchQuery)
                } else {
                    resetData() // Reload original data if search query is empty

                }
                if (start == 0) {
                    resetData()
                }

            }

            override fun afterTextChanged(s: Editable?) {}
        })
        libraryBinding.back.setOnClickListener { finish() }



        try {

            apiClient = APIClient(this)
            apiInterface = apiClient.client().create(APIInterface::class.java)
            preferencesManager = PreferencesManager(this)

            libraryBinding.cardLesson.setOnClickListener {
                GetLessionList()
                libraryBinding.cardLesson.setCardBackgroundColor(resources.getColor(R.color.splash_text_color))
                libraryBinding.cardTest.setCardBackgroundColor(resources.getColor(R.color.light_grey))
                libraryBinding.cardProgram.setCardBackgroundColor(resources.getColor(R.color.light_grey))
                libraryBinding.cardEvent.setCardBackgroundColor(resources.getColor(R.color.light_grey))
                libraryBinding.cardExercise.setCardBackgroundColor(resources.getColor(R.color.light_grey))
            }
            libraryBinding.cardProgram.setOnClickListener {
                GetProgram()
                libraryBinding.cardProgram.setCardBackgroundColor(resources.getColor(R.color.splash_text_color))
                libraryBinding.cardLesson.setCardBackgroundColor(resources.getColor(R.color.light_grey))
                libraryBinding.cardTest.setCardBackgroundColor(resources.getColor(R.color.light_grey))
                libraryBinding.cardEvent.setCardBackgroundColor(resources.getColor(R.color.light_grey))
                libraryBinding.cardExercise.setCardBackgroundColor(resources.getColor(R.color.light_grey))
            }
            libraryBinding.cardEvent.setOnClickListener {
                getEvent()
                libraryBinding.cardEvent.setCardBackgroundColor(resources.getColor(R.color.splash_text_color))
                libraryBinding.cardLesson.setCardBackgroundColor(resources.getColor(R.color.light_grey))
                libraryBinding.cardProgram.setCardBackgroundColor(resources.getColor(R.color.light_grey))
                libraryBinding.cardTest.setCardBackgroundColor(resources.getColor(R.color.light_grey))
                libraryBinding.cardExercise.setCardBackgroundColor(resources.getColor(R.color.light_grey))

            }
            libraryBinding.cardExercise.setOnClickListener {
                GetExerciseList()
                libraryBinding.cardExercise.setCardBackgroundColor(resources.getColor(R.color.splash_text_color))
                libraryBinding.cardLesson.setCardBackgroundColor(resources.getColor(R.color.light_grey))
                libraryBinding.cardProgram.setCardBackgroundColor(resources.getColor(R.color.light_grey))
                libraryBinding.cardTest.setCardBackgroundColor(resources.getColor(R.color.light_grey))
                libraryBinding.cardEvent.setCardBackgroundColor(resources.getColor(R.color.light_grey))
            }
            libraryBinding.cardTest.setOnClickListener {
                GetTestList()
                libraryBinding.cardTest.setCardBackgroundColor(resources.getColor(R.color.splash_text_color))
                libraryBinding.cardProgram.setCardBackgroundColor(resources.getColor(R.color.light_grey))
                libraryBinding.cardLesson.setCardBackgroundColor(resources.getColor(R.color.light_grey))
                libraryBinding.cardEvent.setCardBackgroundColor(resources.getColor(R.color.light_grey))
                libraryBinding.cardExercise.setCardBackgroundColor(resources.getColor(R.color.light_grey))
            }

        } catch (e: Exception) {
            Log.d("Catch OnC", "Catch Run")
        }
    }


    private fun filterData1(query: String) {
        // Ensure the data lists are initialized (they are already initialized at the start)
        lessonData = lessonData ?: ArrayList()
        programListData = programListData ?: ArrayList()
        eventListData = eventListData ?: ArrayList()
        exerciseListData = exerciseListData ?: ArrayList()
        testData = testData ?: ArrayList()

        val searchTerms = query.split(" ")


        val filteredLessons = ArrayList(lessonData.filter { data ->
            searchTerms.all { term -> data.name!!.contains(term, ignoreCase = true) }
        })

        val filteredPrograms = ArrayList(programListData.filter { data ->
            searchTerms.all { term -> data.name!!.contains(term, ignoreCase = true) }
        })

        val filteredEvents = ArrayList(eventListData.filter { data ->
            searchTerms.all { term -> data.title!!.contains(term, ignoreCase = true) }
        })

        val filteredExercises = ArrayList(exerciseListData.filter { data ->
            searchTerms.all { term -> data.name!!.contains(term, ignoreCase = true) }
        })

        val filteredTest = ArrayList(testData.filter { data ->
            searchTerms.all { term -> data.title!!.contains(term, ignoreCase = true) }
        })


        // Update the adapter with filtered data
        if (filteredLessons.isNotEmpty()) {
            adapter_lesson = Library_LessonAdapter(filteredLessons, this, this)
            libraryBinding.rcvLibrarylist.adapter = adapter_lesson
        } else if (filteredPrograms.isNotEmpty()) {
            adapter_program = Library_programAdapter(filteredPrograms, this, this)
            libraryBinding.rcvLibrarylist.adapter = adapter_program
        } else if (filteredEvents.isNotEmpty()) {
            adapter_event = Library_EventAdapter(filteredEvents, this, this)
            libraryBinding.rcvLibrarylist.adapter = adapter_event
        } else if (filteredExercises.isNotEmpty()) {
            adapter_exercise = Library_ExerciseAdapter(filteredExercises, this, this)
            libraryBinding.rcvLibrarylist.adapter = adapter_exercise
        } else if (filteredTest.isNotEmpty()) {
            adapter_test = Library_TestAdapter(filteredTest, this, this)
            libraryBinding.rcvLibrarylist.adapter = adapter_test
        } else {
            // If no results, reset the data
            resetData()
        }
    }


    private fun resetData() {
        // Reset adapters with original data
        adapter_lesson = Library_LessonAdapter(lessonData, this, this)
        libraryBinding.rcvLibrarylist.adapter = adapter_lesson

        adapter_program = Library_programAdapter(programListData, this, this)
        libraryBinding.rcvLibrarylist.adapter = adapter_program

        adapter_event = Library_EventAdapter(eventListData, this, this)
        libraryBinding.rcvLibrarylist.adapter = adapter_event

        adapter_exercise = Library_ExerciseAdapter(exerciseListData, this, this)
        libraryBinding.rcvLibrarylist.adapter = adapter_exercise

        adapter_test = Library_TestAdapter(testData, this, this)
        libraryBinding.rcvLibrarylist.adapter = adapter_test


    }

    private fun GetTestList() {
        try {
            apiInterface.GetTest()?.enqueue(object : Callback<TestListData?> {
                override fun onResponse(
                    call: Call<TestListData?>, response: Response<TestListData?>
                ) {
                    Log.d("TAG", response.code().toString())
                    val code = response.code()
                    if (code == 200) {
                        val resource: TestListData? = response.body()
                        val Success: Boolean = resource?.status ?: false
                        val Message: String = resource?.message.orEmpty()

                        if (Success && resource?.data != null) {
                            testData = resource.data!!

                            if (testData.isNotEmpty()) {
                                // Hide "No data" message and show the RecyclerView
                                libraryBinding.tvNodata.visibility = View.GONE
                                libraryBinding.rcvLibrarylist.visibility = View.VISIBLE

                                libraryBinding.rcvLibrarylist.layoutManager =
                                    LinearLayoutManager(this@LibraryActivity)
                                adapter_test = Library_TestAdapter(
                                    testData, this@LibraryActivity, this@LibraryActivity
                                )
                                libraryBinding.rcvLibrarylist.adapter = adapter_test
                            } else {
                                // Show "No data" message and hide the RecyclerView
                                libraryBinding.tvNodata.visibility = View.VISIBLE
                                libraryBinding.rcvLibrarylist.visibility = View.GONE
                            }
                        } else {
                            Toast.makeText(this@LibraryActivity, Message, Toast.LENGTH_SHORT).show()
                        }
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@LibraryActivity)
                    } else {
                        val message = response.message()
                        Toast.makeText(this@LibraryActivity, message, Toast.LENGTH_SHORT).show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<TestListData?>, t: Throwable) {
                    Toast.makeText(this@LibraryActivity, t.message.orEmpty(), Toast.LENGTH_SHORT).show()
                    call.cancel()
                }
            })
        } catch (e: Exception) {
            Log.d("Catch", "Catch Run: ${e.message}")
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
                        val Success: Boolean = resource?.status ?: false
                        val Message: String = resource?.message.orEmpty()

                        if (Success && resource?.data != null) {
                            lessonData = resource.data!!

                            if (lessonData.isNotEmpty()) {
                                libraryBinding.tvNodata.visibility = View.GONE
                                libraryBinding.rcvLibrarylist.visibility = View.VISIBLE

                                libraryBinding.rcvLibrarylist.layoutManager = LinearLayoutManager(this@LibraryActivity)
                                adapter_lesson = Library_LessonAdapter(
                                    lessonData, this@LibraryActivity, this@LibraryActivity
                                )
                                Log.e("MMMMM", "onResponse: " + lessonData[0].user_id)
                                libraryBinding.rcvLibrarylist.adapter = adapter_lesson
                            } else {
                                // Show "No data found" message
                                libraryBinding.tvNodata.visibility = View.VISIBLE
                                libraryBinding.rcvLibrarylist.visibility = View.GONE
                            }
                        } else {
                            Toast.makeText(this@LibraryActivity, Message, Toast.LENGTH_SHORT).show()
                        }
                    } else if (response.code() == 403) {
                        Utils.setUnAuthDialog(this@LibraryActivity)
                    } else {
                        val message = response.message()
                        Toast.makeText(this@LibraryActivity, message, Toast.LENGTH_SHORT).show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<LessonData?>, t: Throwable) {
                    Toast.makeText(this@LibraryActivity, t.message, Toast.LENGTH_SHORT).show()
                    call.cancel()
                }
            })
        } catch (e: Exception) {
            Log.d("Catch", "Catch Run: ${e.message}")
        }
    }


    private fun GetProgram() {
        try {
            apiInterface.GetProgam()?.enqueue(object : Callback<ProgramListData?> {
                override fun onResponse(
                    call: Call<ProgramListData?>, response: Response<ProgramListData?>
                ) {
                    Log.d("TAG", response.code().toString())
                    val code = response.code()
                    if (code == 200) {
                        val resource: ProgramListData? = response.body()
                        if (resource != null && (resource.status ?: false)) {

                            val Message: String = resource.message ?: ""

                            // Check if resource.data is not null or empty
                            if (!resource.data.isNullOrEmpty()) {
                                programListData = resource.data!!

                                libraryBinding.tvNodata.visibility = View.GONE
                                libraryBinding.rcvLibrarylist.visibility = View.VISIBLE

                                libraryBinding.rcvLibrarylist.layoutManager = LinearLayoutManager(this@LibraryActivity)
                                adapter_program = Library_programAdapter(
                                    resource.data!!, this@LibraryActivity, this@LibraryActivity
                                )
                                libraryBinding.rcvLibrarylist.adapter = adapter_program
                            } else {
                                // Show "No data" message
                                libraryBinding.tvNodata.visibility = View.VISIBLE
                                libraryBinding.rcvLibrarylist.visibility = View.GONE
                            }
                        } else {
                            // Handle case where response status is not successful or resource is null
                            Log.e("TAG", "No Programs found or invalid response")
                            libraryBinding.tvNodata.visibility = View.VISIBLE
                            libraryBinding.rcvLibrarylist.visibility = View.GONE
                        }
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@LibraryActivity)
                    } else {
                        val message = response.message()
                        Toast.makeText(this@LibraryActivity, message, Toast.LENGTH_SHORT).show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<ProgramListData?>, t: Throwable) {
                    Toast.makeText(this@LibraryActivity, t.message, Toast.LENGTH_SHORT).show()
                    call.cancel()
                }
            })

        } catch (e: Exception) {
            Log.d("Catch", "Catch Run", e)
        }
    }


    private fun getEvent() {
        try {
            apiInterface.GetEvent()?.enqueue(object : Callback<EventListData?> {
                override fun onResponse(call: Call<EventListData?>, response: Response<EventListData?>) {

                    val code = response.code()
                    if (code == 200) {
                        val resource: EventListData? = response.body()
                        val Success: Boolean = resource?.status ?: false
                        val Message: String = resource?.message.orEmpty()

                        if (Success && resource?.data != null) {
                            eventListData = resource.data!!

                            if (eventListData.isNotEmpty()) {
                                // Show the RecyclerView and hide the "No data" message
                                libraryBinding.tvNodata.visibility = View.GONE
                                libraryBinding.rcvLibrarylist.visibility = View.VISIBLE

                                libraryBinding.rcvLibrarylist.layoutManager = LinearLayoutManager(this@LibraryActivity)
                                adapter_event = Library_EventAdapter(
                                    eventListData, this@LibraryActivity, this@LibraryActivity
                                )
                                libraryBinding.rcvLibrarylist.adapter = adapter_event
                            } else {
                                // Show "No data found" message and hide the RecyclerView
                                libraryBinding.tvNodata.visibility = View.VISIBLE
                                libraryBinding.rcvLibrarylist.visibility = View.GONE
                            }
                        } else {
                            Toast.makeText(this@LibraryActivity, Message, Toast.LENGTH_SHORT).show()
                        }
                    } else if (response.code() == 403) {
                        Utils.setUnAuthDialog(this@LibraryActivity)
                    } else {
                        val message = response.message()
                        Toast.makeText(this@LibraryActivity, message, Toast.LENGTH_SHORT).show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<EventListData?>, t: Throwable) {
                    Toast.makeText(this@LibraryActivity, t.message, Toast.LENGTH_SHORT).show()
                    call.cancel()
                }
            })

        } catch (e: Exception) {
            Log.d("Catch", "Catch Run: ${e.message}")
        }
    }


    private fun GetExerciseList() {
        try {
            apiInterface.GetExercise()?.enqueue(object : Callback<ExcerciseData?> {
                override fun onResponse(
                    call: Call<ExcerciseData?>, response: Response<ExcerciseData?>
                ) {
                    Log.d("TAG", response.code().toString())
                    val code = response.code()
                    if (code == 200) {
                        val resource: ExcerciseData? = response.body()
                        val Success: Boolean = resource?.status ?: false
                        val Message: String = resource?.message.orEmpty()

                        if (Success && resource?.data != null) {
                            exerciseListData = resource.data!!

                            if (exerciseListData.isNotEmpty()) {
                                // Show the RecyclerView and hide the "No data" message
                                libraryBinding.tvNodata.visibility = View.GONE
                                libraryBinding.rcvLibrarylist.visibility = View.VISIBLE

                                libraryBinding.rcvLibrarylist.layoutManager =
                                    LinearLayoutManager(this@LibraryActivity)
                                adapter_exercise = Library_ExerciseAdapter(
                                    exerciseListData, this@LibraryActivity, this@LibraryActivity
                                )
                                libraryBinding.rcvLibrarylist.adapter = adapter_exercise
                            } else {
                                // Show "No data found" message and hide the RecyclerView
                                libraryBinding.tvNodata.visibility = View.VISIBLE
                                libraryBinding.rcvLibrarylist.visibility = View.GONE
                            }
                        } else {
                            Toast.makeText(this@LibraryActivity, Message, Toast.LENGTH_SHORT).show()
                        }
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@LibraryActivity)
                    } else {
                        val message = response.message()
                        Toast.makeText(this@LibraryActivity, message, Toast.LENGTH_SHORT).show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<ExcerciseData?>, t: Throwable) {
                    Toast.makeText(this@LibraryActivity, t.message.orEmpty(), Toast.LENGTH_SHORT).show()
                    call.cancel()
                }
            })
        } catch (e: Exception) {
            Log.d("Catch", "Catch Run: ${e.message}")
        }
    }


    override fun onItemClicked(view: View, position: Int, type: Long, string: String) {
        if (string.toString().contains("EditTest")) {

            startActivity(Intent(this, TestActivity::class.java))


        }

        if (string.toString().contains("DeleteTest")) {


            val builder: AlertDialog.Builder
            builder = AlertDialog.Builder(this)
            builder.setMessage("Are you sure you want to delete Test?").setTitle("Delete")
                .setCancelable(false).setPositiveButton("Yes") { dialog, id ->
//                    libraryBinding.ProgressBar.visibility = View.VISIBLE
                    apiInterface.DeleteTest(type.toInt())
                        ?.enqueue(object : Callback<RegisterData?> {
                            override fun onResponse(
                                call: Call<RegisterData?>, response: Response<RegisterData?>
                            ) {
//                                exerciseBinding.ProgressBar.visibility = View.GONE
                                Log.d("TAG", response.code().toString() + "")
                                val code = response.code()
                                if (code == 200) {
                                    val resource: RegisterData? = response.body()
                                    val Message: String = resource!!.message!!
//                                    exerciseBinding.ProgressBar.visibility = View.GONE
                                    Toast.makeText(
                                        this@LibraryActivity, "" + Message, Toast.LENGTH_SHORT
                                    ).show()
//                                    initViews()
//                                    finish()
//                                    startActivity(
//                                        Intent(
//                                            this@ExerciseActivity,
//                                            ExerciseActivity::class.java
//                                        )
//                                    )
                                } else if (code == 403) {
                                    Utils.setUnAuthDialog(this@LibraryActivity)
                                } else {
                                    Toast.makeText(
                                        this@LibraryActivity,
                                        "" + response.message(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    call.cancel()
                                }
                            }

                            override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
//                                exerciseBinding.ProgressBar.visibility = View.GONE
                                Toast.makeText(
                                    this@LibraryActivity, "" + t.message, Toast.LENGTH_SHORT
                                ).show()
                                call.cancel()
                            }
                        })
                }.setNegativeButton(
                    "No"
                ) { dialog, id ->
                    dialog.cancel()
                }
            val alert = builder.create()
            alert.setTitle("Delete")
            alert.show()
        }

        if (string.toString().contains("EditExercise")) {

//            startActivity(Intent(this, ExerciseActivity::class.java))

            val intent = Intent(this, EdiExerciseActivity::class.java)
            intent.putExtra("position", position)
            intent.putExtra("exercise", type.toInt())
            startActivity(intent)
        }

        if (string.toString().contains("DeleteExercise")) {

            val builder: AlertDialog.Builder
            builder = AlertDialog.Builder(this)
            builder.setMessage("Are you sure you want to delete Exercise?").setTitle("Delete")
                .setCancelable(false).setPositiveButton("Yes") { dialog, id ->
//                    libraryBinding.ProgressBar.visibility = View.VISIBLE
                    apiInterface.Deleteexercise(type.toInt())
                        ?.enqueue(object : Callback<RegisterData?> {
                            override fun onResponse(
                                call: Call<RegisterData?>, response: Response<RegisterData?>
                            ) {
//                                exerciseBinding.ProgressBar.visibility = View.GONE
                                Log.d("TAG", response.code().toString() + "")
                                val code = response.code()
                                if (code == 200) {
                                    val resource: RegisterData? = response.body()
                                    val Message: String = resource!!.message!!
//                                    exerciseBinding.ProgressBar.visibility = View.GONE
                                    Toast.makeText(
                                        this@LibraryActivity, "" + Message, Toast.LENGTH_SHORT
                                    ).show()
//                                    initViews()
//                                    finish()
//                                    startActivity(
//                                        Intent(
//                                            this@ExerciseActivity,
//                                            ExerciseActivity::class.java
//                                        )
//                                    )
                                } else if (code == 403) {
                                    Utils.setUnAuthDialog(this@LibraryActivity)
                                } else {
                                    Toast.makeText(
                                        this@LibraryActivity,
                                        "" + response.message(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    call.cancel()
                                }
                            }

                            override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
//                                exerciseBinding.ProgressBar.visibility = View.GONE
                                Toast.makeText(
                                    this@LibraryActivity, "" + t.message, Toast.LENGTH_SHORT
                                ).show()
                                call.cancel()
                            }
                        })
                }.setNegativeButton(
                    "No"
                ) { dialog, id ->
                    dialog.cancel()
                }
            val alert = builder.create()
            alert.setTitle("Delete")
            alert.show()
        }

        if (string.toString().contains("EditEvent")) {

            Log.e(
                "LALALALALALA",
                "onItemClicked: " + position + "   " + type + "     " + view.id + "    " + string
            )

//            startActivity(Intent(this, Create_Event_Activity::class.java))
            val intent = Intent(this, Create_Event_Activity::class.java)
            intent.putExtra("position", position)
            intent.putExtra("type", type)
            startActivity(intent)

        }

        if (string.toString().contains("DeleteEvent")) {

            val builder: AlertDialog.Builder
            builder = AlertDialog.Builder(this)
            builder.setMessage("Are you sure you want to delete Event?").setTitle("Delete")
                .setCancelable(false).setPositiveButton("Yes") { dialog, id ->
//                    libraryBinding.ProgressBar.visibility = View.VISIBLE
                    apiInterface.DeleteEvent(type.toInt())
                        ?.enqueue(object : Callback<RegisterData?> {
                            override fun onResponse(
                                call: Call<RegisterData?>, response: Response<RegisterData?>
                            ) {
//                                exerciseBinding.ProgressBar.visibility = View.GONE
                                Log.d("TAG", response.code().toString() + "")
                                val code = response.code()
                                if (code == 200) {
                                    val resource: RegisterData? = response.body()
                                    val Message: String = resource!!.message!!
//                                    exerciseBinding.ProgressBar.visibility = View.GONE
                                    Toast.makeText(
                                        this@LibraryActivity, "" + Message, Toast.LENGTH_SHORT
                                    ).show()
//                                    initViews()
//                                    finish()
//                                    startActivity(
//                                        Intent(
//                                            this@ExerciseActivity,
//                                            ExerciseActivity::class.java
//                                        )
//                                    )
                                } else if (code == 403) {
                                    Utils.setUnAuthDialog(this@LibraryActivity)
                                } else {
                                    Toast.makeText(
                                        this@LibraryActivity,
                                        "" + response.message(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    call.cancel()
                                }
                            }

                            override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
//                                exerciseBinding.ProgressBar.visibility = View.GONE
                                Toast.makeText(
                                    this@LibraryActivity, "" + t.message, Toast.LENGTH_SHORT
                                ).show()
                                call.cancel()
                            }
                        })
                }.setNegativeButton(
                    "No"
                ) { dialog, id ->
                    dialog.cancel()
                }
            val alert = builder.create()
            alert.setTitle("Delete")
            alert.show()
        }

        if (string.toString().contains("EditProgram")) {

//            startActivity(Intent(this, ViewProgramActivity::class.java))
        }
        if (string.toString().contains("CopyProgram")) {
            Toast.makeText(this@LibraryActivity, "Copy Program!", Toast.LENGTH_SHORT)
        }

        if (string.toString().contains("DeleteProgram")) {

            val builder: AlertDialog.Builder
            builder = AlertDialog.Builder(this)
            builder.setMessage("Are you sure you want to delete Program?").setTitle("Delete")
                .setCancelable(false).setPositiveButton("Yes") { dialog, id ->
//                    libraryBinding.ProgressBar.visibility = View.VISIBLE
                    apiInterface.DeleteProgram(type.toInt())
                        ?.enqueue(object : Callback<RegisterData?> {
                            override fun onResponse(
                                call: Call<RegisterData?>, response: Response<RegisterData?>
                            ) {
//                                exerciseBinding.ProgressBar.visibility = View.GONE
                                Log.d("TAG", response.code().toString() + "")
                                val code = response.code()
                                if (code == 200) {
                                    val resource: RegisterData? = response.body()
                                    val Message: String = resource!!.message!!
//                                    exerciseBinding.ProgressBar.visibility = View.GONE
                                    Toast.makeText(
                                        this@LibraryActivity, "" + Message, Toast.LENGTH_SHORT
                                    ).show()
//                                    initViews()
//                                    finish()
//                                    startActivity(
//                                        Intent(
//                                            this@ExerciseActivity,
//                                            ExerciseActivity::class.java
//                                        )
//                                    )
                                } else if (code == 403) {
                                    Utils.setUnAuthDialog(this@LibraryActivity)
                                } else {
                                    Toast.makeText(
                                        this@LibraryActivity,
                                        "" + response.message(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    call.cancel()
                                }
                            }

                            override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
//                                exerciseBinding.ProgressBar.visibility = View.GONE
                                Toast.makeText(
                                    this@LibraryActivity, "" + t.message, Toast.LENGTH_SHORT
                                ).show()
                                call.cancel()
                            }
                        })
                }.setNegativeButton(
                    "No"
                ) { dialog, id ->
                    dialog.cancel()
                }
            val alert = builder.create()
            alert.setTitle("Delete")
            alert.show()
        }

        if (string.toString().contains("EditLession")) {

            val intent = Intent(this, ViewLessonActivity::class.java)
            intent.putExtra("position", position)
            startActivity(intent)


        }

        if (string.toString().contains("DeleteLession")) {

            val builder: AlertDialog.Builder
            builder = AlertDialog.Builder(this)
            builder.setMessage("Are you sure you want to delete Lession?").setTitle("Delete")
                .setCancelable(false).setPositiveButton("Yes") { dialog, id ->
//                    libraryBinding.ProgressBar.visibility = View.VISIBLE
                    apiInterface.DeleteLession(type.toInt())
                        ?.enqueue(object : Callback<RegisterData?> {
                            override fun onResponse(
                                call: Call<RegisterData?>, response: Response<RegisterData?>
                            ) {
//                                exerciseBinding.ProgressBar.visibility = View.GONE
                                Log.d("TAG", response.code().toString() + "")
                                val code = response.code()
                                if (code == 200) {
                                    val resource: RegisterData? = response.body()
                                    val Message: String = resource!!.message!!
//                                    exerciseBinding.ProgressBar.visibility = View.GONE
                                    Toast.makeText(
                                        this@LibraryActivity, "" + Message, Toast.LENGTH_SHORT
                                    ).show()
//                                    initViews()
//                                    finish()
//                                    startActivity(
//                                        Intent(
//                                            this@ExerciseActivity,
//                                            ExerciseActivity::class.java
//                                        )
//                                    )
                                } else if (code == 403) {
                                    Utils.setUnAuthDialog(this@LibraryActivity)
                                } else {
                                    Toast.makeText(
                                        this@LibraryActivity,
                                        "" + response.message(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    call.cancel()
                                }
                            }

                            override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
//                                exerciseBinding.ProgressBar.visibility = View.GONE
                                Toast.makeText(
                                    this@LibraryActivity, "" + t.message, Toast.LENGTH_SHORT
                                ).show()
                                call.cancel()
                            }
                        })
                }.setNegativeButton(
                    "No"
                ) { dialog, id ->
                    dialog.cancel()
                }
            val alert = builder.create()
            alert.setTitle("Delete")
            alert.show()
        }


    }
}