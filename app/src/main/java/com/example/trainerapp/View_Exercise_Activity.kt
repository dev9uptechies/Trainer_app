package com.example.trainerapp

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.MediaController
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.CycleAdapter
import com.example.EdiExerciseActivity
import com.example.OnItemClickListener
import com.example.model.newClass.excercise.Exercise
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.ExcerciseData
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.databinding.ActivityViewExerciseBinding
import com.makeramen.roundedimageview.RoundedTransformationBuilder
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class View_Exercise_Activity : AppCompatActivity(), OnItemClickListener.OnItemClickCallback {
    lateinit var viewExerciseBinding: ActivityViewExerciseBinding
    var position: Int? = null
    var id: Int? = null
    var EXID: String? = null
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    lateinit var Progress: ProgressBar

    //    lateinit var generallist: ArrayList<ExcerciseData.Exercise>
    lateinit var specificlist: ArrayList<ExcerciseData.Exercise>
    lateinit var adapter: CycleAdapter
    lateinit var bitmap: Bitmap
    lateinit var exerciselist: ArrayList<ExcerciseData.Exercise>
    lateinit var generallist: MutableList<Exercise.ExerciseData>

    var imageString = ""
    var videoString = ""
    var youtube = false

    var ExerciseLberyid:Int ?= null

    lateinit var mediaControls: MediaController

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
                        Utils.setUnAuthDialog(this@View_Exercise_Activity)
                    } else {
                        Toast.makeText(
                            this@View_Exercise_Activity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Toast.makeText(
                        this@View_Exercise_Activity,
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
        viewExerciseBinding = ActivityViewExerciseBinding.inflate(layoutInflater)
        setContentView(viewExerciseBinding.root)
        viewExerciseBinding.exerciseImage.visibility = View.VISIBLE
        initViews()
        generallist = mutableListOf()
        exerciselist = ArrayList()
        specificlist = ArrayList()
        GetExercise()


        try {

        }catch (e:Exception){
            Log.d("GDGDDHGD", "onCreate: ${e.message}")
        }
        viewExerciseBinding.cardViewTimer.setOnClickListener {
            val intent = Intent(this, ViewTimerActivity::class.java)
            intent.putExtra("position", position)
            intent.putExtra("id", id)
            startActivity(intent)
        }
        viewExerciseBinding.back.setOnClickListener {
            finish()
        }
    }

    fun initViews() {
        apiClient = APIClient(this)
        Progress = findViewById(R.id.Progress)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        position = intent.getIntExtra("position", 0)
        id = intent.getIntExtra("id", 0)
        EXID = intent.getStringExtra("EXID").toString()
        ExerciseLberyid = intent.getIntExtra("ExerciseId",0)


        Log.d("PSPSPSPSP", "initViews: $ExerciseLberyid")
    }

    private fun isYouTubeUrl(url: String): Boolean {
        return url.contains("youtube.com") || url.contains("youtu.be")
    }

    private fun GetExercise() {

        Progress.visibility = View.VISIBLE
        try {
            apiInterface.GetExerciseData().enqueue(
                object : Callback<Exercise> {
                    override fun onResponse(call: Call<Exercise>, response: Response<Exercise>) {
                        Progress.visibility = View.GONE

                        Log.d("Response :- ", "${response.body()}")
                        Log.d("Response :- ", "${response.code()}")
                        Log.d("Response :- ", "${response.body()!!.data!!.size}")
                        Log.d("Response :- ", "${response.isSuccessful}")
                        val code = response.code()
                        if (code == 200) {
                            if (response.isSuccessful) {

                                if (response.body()!!.data!!.isNotEmpty()) {

                                    val idTouse = if (id == 0 || id == null) {
                                        EXID?.toIntOrNull() ?: 0 // Convert EXID to Int if valid, else default to 0
                                    } else id

                                    Log.d("FINALIDD", "onResponse: $idTouse")

                                    val finalID = if(idTouse == null || idTouse == 0) ExerciseLberyid else idTouse

                                    val data = response.body()!!.data?.filter {
                                        it.id == finalID
                                    }

                                    if (data!!.isNotEmpty()) {

                                        val transformation: Transformation =
                                            RoundedTransformationBuilder()
                                                .borderColor(resources.getColor(R.color.splash_text_color))
                                                .borderWidthDp(1f)
                                                .cornerRadiusDp(10f)
                                                .oval(false)
                                                .build()
                                        imageString = "https://trainers.codefriend.in" + data[0].image

                                        if (data[0].video != null) {
                                            videoString = "https://trainers.codefriend.in" + data[0].video
                                            playNormalVideo(videoString)
                                            Log.d(
                                                "Video Type :-",
                                                "https://trainers.codefriend.in" + data[0].video!!
                                            )
                                        } else if (data[0].video_link != null) {
                                            videoString = data[0].video_link!!
                                            if (isYouTubeUrl(videoString)) {
                                                playYouTubeVideo(videoString)
                                            } else {
                                                playNormalVideo(videoString)
                                            }
                                        }

                                        Picasso.get()
                                            .load(imageString)
                                            .placeholder(R.drawable.video_background)
                                            .fit()
                                            .transform(transformation)
                                            .into(viewExerciseBinding.exerciseImage)

                                        viewExerciseBinding.exerciseName.text = data[0].name
                                        viewExerciseBinding.exerciseNotes.text = data[0].notes
                                        viewExerciseBinding.tvSection.text =
                                            data[0].section!!.section_name
                                        viewExerciseBinding.tvGoal.text =
                                            data[0].goal!!.goal_name
                                        val namesString = data[0].exercise_equipments!!.map {
                                            it.exercise_equipment!!.equipment_name
                                        }.joinToString(separator = ", ")
                                        viewExerciseBinding.tvEqupment.text = namesString
                                        if (data[0].cycles!!.isNotEmpty()) {
                                            initRecycler(data[0].cycles!!.toMutableList())
                                        }
                                        viewExerciseBinding.cardEditExercise.setOnClickListener {
                                            startActivity(
                                                Intent(
                                                    this@View_Exercise_Activity,
                                                    EdiExerciseActivity::class.java
                                                ).putExtra("position", position)
                                            )
                                        }
                                    }
                                }

                            }
                        } else if (code == 403) {
                            Utils.setUnAuthDialog(this@View_Exercise_Activity)
                        } else {
                            Toast.makeText(
                                this@View_Exercise_Activity,
                                "" + response.message(),
                                Toast.LENGTH_SHORT
                            ).show()
                            call.cancel()
                        }
                    }


                    override fun onFailure(call: Call<Exercise>, t: Throwable) {
                        Log.d("Response :- ", "${t.message}")
                    }

                }
            )
        } catch (e: Exception) {
            Progress.visibility = View.GONE
            Log.d("Response :- ", "${e.message}")
        }

    }

    private fun playYouTubeVideo(videoString: String) {
        val unencodedHtml = """
            <html>
            <body>
            <iframe width="100%" height="100%" src="$videoString" frameborder="0"></iframe>
            </body>
            </html>
        """.trimIndent()
        viewExerciseBinding.webView.settings.javaScriptEnabled = true
        viewExerciseBinding.webView.settings.loadWithOverviewMode = true
        viewExerciseBinding.webView.loadData(unencodedHtml, "text/html", "utf-8")
        viewExerciseBinding.webView.visibility = View.VISIBLE
        viewExerciseBinding.exerciseImage.visibility = View.GONE
        viewExerciseBinding.videoUpload.visibility = View.GONE
    }

    private fun playNormalVideo(videoString: String) {
        viewExerciseBinding.videoUpload.stopPlayback() // Stop any previous playback
        viewExerciseBinding.videoUpload.setVideoURI(null) // Clear the previous video URI
        viewExerciseBinding.videoUpload.setVideoURI(Uri.parse(videoString)) // Set new video URI
        viewExerciseBinding.videoUpload.requestLayout()
        viewExerciseBinding.videoUpload.invalidate()
        mediaControls = MediaController(this)
        mediaControls.setAnchorView(viewExerciseBinding.videoUpload)
        viewExerciseBinding.videoUpload.setMediaController(mediaControls)
        viewExerciseBinding.videoUpload.requestFocus()
        viewExerciseBinding.exerciseImage.visibility = View.GONE
        viewExerciseBinding.videoUpload.visibility = View.VISIBLE
    }

    private fun initRecycler(testDataList: MutableList<Exercise.Cycle>?) {
        Progress.visibility = View.GONE
        viewExerciseBinding.cycleRecycler.layoutManager = LinearLayoutManager(this)
        adapter =
            CycleAdapter(testDataList, this, this)
        viewExerciseBinding.cycleRecycler.adapter = adapter
    }

    override fun onItemClicked(view: View, position: Int, type: Long, string: String) {

    }

}