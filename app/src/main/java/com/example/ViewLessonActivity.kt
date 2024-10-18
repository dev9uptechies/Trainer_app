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
    private lateinit var lession_data: ArrayList<LessonData.lessionData>
    lateinit var apiClient: APIClient
    lateinit var lessAdapter: ViewLessionAdapter
    lateinit var excAdapter: Excercise_list_Adapter
    var position: Int? = null

    var excId = SelectedValue(null)
    var proId = SelectedValue(null)

    override fun onResume() {
        checkUser()
        super.onResume()
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
                        Utils.setUnAuthDialog(this@ViewLessonActivity)
                    } else {
                        Toast.makeText(
                            this@ViewLessonActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Toast.makeText(
                        this@ViewLessonActivity,
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewLessonBinding = ActivityViewLessonBinding.inflate(layoutInflater)
        setContentView(viewLessonBinding.root)
        initViews()
        GetLessionList()
    }

    private fun initViews() {
        lession_data = ArrayList()
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)

        excId = SelectedValue(null)
        proId = SelectedValue(null)

        val id_lesson = intent.getStringExtra("id")
        position = intent.getIntExtra("position", 0)
        val total_time = intent.getStringExtra("total_time")
        val section_time = intent.getStringExtra("section_time")
        val name = intent.getStringExtra("name")

        viewLessonBinding.lessonName.text = name
        viewLessonBinding.lessonTTime.text = "Total Time : " + total_time
        viewLessonBinding.lessonSTime.text = "Section Time : " + section_time

        viewLessonBinding.back.setOnClickListener {
            finish()
        }

        viewLessonBinding.cardOnline.setOnClickListener {
            val intent = Intent(this@ViewLessonActivity, LessonOnlineActivity::class.java)
            intent.putExtra("total_time", total_time)
            startActivity(intent)
        }

        viewLessonBinding.cardDuplicate.setOnClickListener {
            val dialog = Dialog(this, R.style.Theme_Dialog)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setCanceledOnTouchOutside(false)
            dialog.setContentView(R.layout.dialog_number_picker)
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            val width = (displayMetrics.widthPixels * 0.9f).toInt()
            val height = WindowManager.LayoutParams.WRAP_CONTENT
            dialog.window!!.setLayout(width, height)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
            val cancel = dialog.findViewById<CardView>(R.id.card_cancel)
            val card_apply = dialog.findViewById<CardView>(R.id.card_apply)

            cancel.setOnClickListener {
                dialog.hide()
            }

            card_apply.setOnClickListener {
                dialog.hide()
                viewLessonBinding.viewLessonProgress.visibility = View.VISIBLE
                val id: MultipartBody.Part =
                    MultipartBody.Part.createFormData("id", id_lesson!!)
                apiInterface.Duplicate_lession(id)
                    ?.enqueue(object : Callback<LessonData?> {
                        override fun onResponse(
                            call: Call<LessonData?>,
                            response: Response<LessonData?>
                        ) {
                            viewLessonBinding.viewLessonProgress.visibility = View.GONE
                            val code = response.code()
                            if (code == 200) {
                                val resource: LessonData? = response.body()
                                val Success: Boolean = resource?.status!!
                                val Message: String = resource.message!!
                                viewLessonBinding.viewLessonProgress.visibility = View.GONE
                                finish()
                            } else if (code == 403) {
                                Utils.setUnAuthDialog(this@ViewLessonActivity)
                            } else {
                                Toast.makeText(
                                    this@ViewLessonActivity,
                                    "" + response.message(),
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                call.cancel()
                            }
                        }

                        override fun onFailure(call: Call<LessonData?>, t: Throwable) {
                            Toast.makeText(
                                this@ViewLessonActivity,
                                "" + t.message,
                                Toast.LENGTH_SHORT
                            ).show()
                            call.cancel()
                        }
                    })
            }

        }

    }

    private fun GetLessionList() {
        viewLessonBinding.viewLessonProgress.visibility = View.VISIBLE
        apiInterface.GetLession()?.enqueue(object : Callback<LessonData?> {
            override fun onResponse(call: Call<LessonData?>, response: Response<LessonData?>) {
                Log.d("TAG", response.code().toString() + "")
                viewLessonBinding.viewLessonProgress.visibility = View.GONE
                val code = response.code()
                if (code == 200) {
                    val resource: LessonData? = response.body()
                    val Success: Boolean = resource?.status!!
                    val Message: String = resource.message!!
                    if (Success) {
                        lession_data = resource.data!!
                        if (lession_data != null) {
                            viewLessonBinding.tvSection.text =
                                lession_data[position!!].section!!.name

                            if (lession_data[position!!].lesson_programs != null) {
                                val program = lession_data[position!!].lesson_programs
                                if (program != null && program.size != 0) {
                                    proId = SelectedValue(program.firstOrNull()?.id)
                                    initRecyclerview(program, proId.id)

                                    val exc = program[0].program!!.program_exercises!!
                                    if (exc.size != 0 && exc != null) {
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
                                Toast.makeText(
                                    this@ViewLessonActivity,
                                    "Program Data Not Found",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                this@ViewLessonActivity,
                                "Program Data Not Found",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else if (code == 403) {
                    Utils.setUnAuthDialog(this@ViewLessonActivity)
                } else {
                    Toast.makeText(
                        this@ViewLessonActivity,
                        "" + response.message(),
                        Toast.LENGTH_SHORT
                    ).show()
                    call.cancel()
                }

            }

            override fun onFailure(call: Call<LessonData?>, t: Throwable) {
                viewLessonBinding.viewLessonProgress.visibility = View.GONE
                Toast.makeText(
                    this@ViewLessonActivity,
                    "" + t.message,
                    Toast.LENGTH_SHORT
                ).show()
                call.cancel()
            }
        })

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

    override fun onItemClicked(view: View, position: Int, type: Long, string: String) {
        if (string == "Click") {
            proId.id = type.toInt()
        }
    }

    private fun initRecycler(
        programExercises: ArrayList<ProgramListData.Program>,
        initialSelectId: Int?
    ) {
        viewLessonBinding.rlyExercise.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        excAdapter =
            Excercise_list_Adapter(programExercises, this, this, initialSelectId)
        viewLessonBinding.rlyExercise.adapter = excAdapter
    }

    override fun onItemClick(id: Int, name: String, position: Int) {
        excId.id = id

        val intent = Intent(this, View_Exercise_Activity::class.java)
        intent.putExtra("position", position)
        intent.putExtra("id", id)
        startActivity(intent)
    }

}