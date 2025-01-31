package com.example

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import com.LessonOnlineActivity
import com.example.model.SelectedValue
import com.example.model.training_plan.TrainingPlanData
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.ProgramListData
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.R
import com.example.trainerapp.Utils
import com.example.trainerapp.View_Exercise_Activity
import com.example.trainerapp.databinding.ActivityViewLessonBinding
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewLessonActivity : AppCompatActivity(), OnItemClickListener.OnItemClickCallback,
    Excercise_list_Adapter.OnItemClickListener {
    lateinit var viewLessonBinding: ActivityViewLessonBinding
    lateinit var apiInterface: APIInterface
    private lateinit var lessonData: ArrayList<LessonData.lessionData>
    lateinit var apiClient: APIClient
    lateinit var lessAdapter: ViewLessionAdapter
    lateinit var excAdapter: Excercise_list_Adapter
    var position: Int? = null
    var id: Int? = null
    var idLibrary: Int? = null
    var idforviewlesson: Int? = null

    var lposition:Int ?= null
    var GroupAttends:String ?= null

    var excId = SelectedValue(null)
    var proId = SelectedValue(null)

    override fun onResume() {
        super.onResume()
        checkUser()
    }

    private fun checkUser() {
        apiInterface.ProfileData()?.enqueue(object : Callback<RegisterData?> {
            override fun onResponse(call: Call<RegisterData?>, response: Response<RegisterData?>) {
                handleProfileResponse(response)
            }

            override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                showToast(t.message)
                call.cancel()
            }
        })
    }

    private fun handleProfileResponse(response: Response<RegisterData?>) {
        when (response.code()) {
            200 -> Log.d("Get Profile Data", "${response.body()}")
            403 -> Utils.setUnAuthDialog(this)
            else -> showToast(response.message())
        }
    }

    private fun showToast(message: String?) {
        Toast.makeText(this, message ?: "An error occurred", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewLessonBinding = ActivityViewLessonBinding.inflate(layoutInflater)
        setContentView(viewLessonBinding.root)
        initViews()
        getLessonList()
    }

    private fun initViews() {
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)

        id = intent.getIntExtra("lessonId", 0)
        idforviewlesson = intent.getIntExtra("id", 0)
        position = intent.getIntExtra("position", 0)
        val totalTime = intent.getStringExtra("total_time")
        val sectionTime = intent.getStringExtra("section_time")
        val name = intent.getStringExtra("name")
        idLibrary = intent.getIntExtra("LessonLibraryId", 0)
        lposition = intent.getIntExtra("LessonLibraryPosition", 0)
        GroupAttends = intent.getStringExtra("GroupAttends")

        Log.d("JSJSJSJJS", "initViews: $GroupAttends")
        Log.d("JSJSJSJJS", "initViews: $idLibrary")
        Log.d("CCVCVCVVCVCV", "initViews: "+ id)
        Log.d("CCVCVCVVCVCV", "initViews: "+ position)
        Log.d("CCVCVCVVCVCV", "initViews: "+ idforviewlesson)

        if (GroupAttends == "" || GroupAttends == "null" || GroupAttends == null){
            viewLessonBinding.cardAttendenecs.isClickable = false
            viewLessonBinding.cardAttendenecs.isActivated = false
            viewLessonBinding.cardAttendenecs.isEnabled = false
        }

        lessonData = ArrayList()

        viewLessonBinding.lessonName.text = name
        viewLessonBinding.lessonTTime.text = "Total Time : $totalTime"
        viewLessonBinding.lessonSTime.text = "Section Time : $sectionTime"

        viewLessonBinding.back.setOnClickListener { finish() }

        viewLessonBinding.cardOnline.visibility = View.GONE
        viewLessonBinding.cardOnline.setOnClickListener {
            startActivity(Intent(this@ViewLessonActivity, LessonOnlineActivity::class.java).apply {
                putExtra("total_time", totalTime)
            })
        }

        Log.d("DKKDKDKD", "initViews: $idforviewlesson")

        val finalId = if (idforviewlesson == null || idforviewlesson == 0|| idforviewlesson.toString() == "0") idLibrary else idforviewlesson


        val fi = if (finalId == 0|| finalId == null || finalId.toString() == "0") id else finalId

        Log.d("DKKDKDKDDDDDDDDD", "initViews: $fi")


        viewLessonBinding.cardAttendenecs.setOnClickListener {
            val intent = Intent(this, LessonListActivity::class.java)
            intent.putExtra("Add", "ConfirmAttendance")
            intent.putExtra("LessonID", fi!!.toInt())
            startActivity(intent)
        }

        viewLessonBinding.cardDuplicate.setOnClickListener { showDuplicateDialog() }
    }

    private fun showDuplicateDialog() {
        viewLessonBinding.main.setBackgroundColor(resources.getColor(R.color.grey))

        val dialog = Dialog(this, R.style.Theme_Dialog).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(true)
            setCanceledOnTouchOutside(false)
            setContentView(R.layout.dialog_number_picker)
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            window?.setLayout((displayMetrics.widthPixels * 0.9f).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        dialog.show()
        dialog.findViewById<CardView>(R.id.card_cancel).setOnClickListener {
            viewLessonBinding.main.setBackgroundColor(resources.getColor(R.color.black))
            dialog.dismiss() }
        dialog.findViewById<CardView>(R.id.card_apply).setOnClickListener {
            viewLessonBinding.main.setBackgroundColor(resources.getColor(R.color.black))

            dialog.dismiss()
            duplicateLesson()
        }
    }

    private fun duplicateLesson() {
        viewLessonBinding.viewLessonProgress.visibility = View.VISIBLE

        val iddd = intent.getIntExtra("id",0)
        
        val idPart = MultipartBody.Part.createFormData("id", iddd.toString())
        apiInterface.Duplicate_lession(idPart)?.enqueue(object : Callback<LessonData?> {
            override fun onResponse(call: Call<LessonData?>, response: Response<LessonData?>) {
                viewLessonBinding.viewLessonProgress.visibility = View.GONE
                handleDuplicateLessonResponse(response)
            }

            override fun onFailure(call: Call<LessonData?>, t: Throwable) {
                showToast(t.message)
                viewLessonBinding.viewLessonProgress.visibility = View.GONE
            }
        })
    }

    private fun handleDuplicateLessonResponse(response: Response<LessonData?>) {
        if (response.code() == 200 && response.body()?.status == true) {
            finish()
        } else {
            showToast(response.message())
        }
    }

    private fun getLessonList() {
        viewLessonBinding.viewLessonProgress.visibility = View.VISIBLE
        apiInterface.GetLession()?.enqueue(object : Callback<LessonData?> {
            override fun onResponse(call: Call<LessonData?>, response: Response<LessonData?>) {
                viewLessonBinding.viewLessonProgress.visibility = View.GONE
                if (response.isSuccessful) {
                    response.body()?.data?.let { data ->

                        val idToUse = if ((id as? String)?.isNullOrEmpty() == true || id == 0 ) {
                            idforviewlesson
                        } else {
                            id
                        }

                        val FinalId = if (idToUse == 0 || idToUse == null) idLibrary else idToUse

                        Log.d("DJDJDJDJDJDJ", "onResponse: $idToUse")

                        val filteredData = data.filter { it.id == FinalId }

                        try {
                            if (position != null && position!! >= 0 && position!! < lessonData.size) {
                                val program = lessonData[position!!].lesson_programs
                                if (program != null && program.isNotEmpty()) {
                                    proId = SelectedValue(program.firstOrNull()?.id)
                                    Log.d("FFFFFFFFF", "onResponse: ${proId.id}")
                                    initRecyclerview(program, proId.id)
                                }
                            } else {
                                Log.d("LessonData", "Position: $position, LessonData size: ${lessonData.size}")
                            }
                        } catch (e: Exception) {
                            Log.d("cat", "onResponse: ERROR:___" + e.message.toString())
                        }

                        if (filteredData.isNotEmpty()) {
                            setProgramDataset(filteredData)
                        } else {
                            showToast("No lesson data found for this lesson ID.")
                        }
                    } ?: showToast("Lesson data is empty.")
                } else {
                    showToast("Failed to fetch lesson data: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<LessonData?>, t: Throwable) {
                showToast(t.message)
                viewLessonBinding.viewLessonProgress.visibility = View.GONE
            }
        })
    }

    private fun setProgramDataset(data: List<LessonData.lessionData>) {
        if (data.isNotEmpty()) {
            val lesson = data[0]
            viewLessonBinding.lessonName.text = lesson.name
            viewLessonBinding.lessonTTime.text = lesson.time
            viewLessonBinding.lessonSTime.text = lesson.section_time

            Log.d("LessonData", "Lesson: ${lesson.name}, Programs: ${lesson.lesson_programs?.size}")

            val program = lesson.lesson_programs
            Log.d("ProgramData", "Program: ${program!!.firstOrNull()?.program}, ID: ${program.firstOrNull()?.id}")

            if (program != null && program.isNotEmpty()) {

                proId = SelectedValue(program.firstOrNull()?.id)

                Log.d("SSSSSSSS", "setProgramDataset: ${proId.id}")
                initRecyclerview(program, proId.id)

                val exc = program[0].program?.program_exercises
                if (exc != null && exc.isNotEmpty()) {
                    excId = SelectedValue(exc.firstOrNull()?.id)
                    initRecycler(exc, excId.id)
                }

            } else {
                Toast.makeText(
                    this@ViewLessonActivity,
                    "Program Data Not Found",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            // If the data list is empty, show a Toast message
            Toast.makeText(
                this@ViewLessonActivity,
                "Lesson Data Not Found",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun initRecyclerview(
        data: ArrayList<LessonData.Lesson_Programs>,
        initialSelectId: Int?
    ) {
        viewLessonBinding.rlyProgram.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        lessAdapter =
            ViewLessionAdapter(data, this, this, initialSelectId)
        viewLessonBinding.rlyProgram.adapter = lessAdapter
    }


    private fun initRecycler(
        data: ArrayList<ProgramListData.Program>,
        initialSelectId: Int?
    ) {
        viewLessonBinding.rlyExercise.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        excAdapter = Excercise_list_Adapter(data, this, this, initialSelectId)
        viewLessonBinding.rlyExercise.adapter = excAdapter
    }

    override fun onItemClicked(view: View, position: Int, type: Long, string: String) {
        if (string == "Click") {
            proId.id = type.toInt()
        }
    }

    override fun onItemClick(id: Int, name: String, position: Int) {
        excId.id = id
        startActivity(Intent(this, View_Exercise_Activity::class.java).apply {
            putExtra("id", excId.id)
        })
    }
}
