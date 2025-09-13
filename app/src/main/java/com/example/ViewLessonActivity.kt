package com.example

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.Image
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import com.LessonOnlineActivity
import com.example.model.SelectedValue
import com.example.model.newClass.excercise.Exercise
import com.example.model.training_plan.TrainingPlanData
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.ProgramListData
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.PreferencesManager
import com.example.trainerapp.R
import com.example.trainerapp.Utils
import com.example.trainerapp.View_Exercise_Activity
import com.example.trainerapp.databinding.ActivityViewLessonBinding
import okhttp3.MultipartBody
import okio.IOException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.log

class ViewLessonActivity : AppCompatActivity(), OnItemClickListener.OnItemClickCallback, Excercise_list_Adapter.OnItemClickListener{
    lateinit var viewLessonBinding: ActivityViewLessonBinding
    lateinit var apiInterface: APIInterface
    lateinit var preferenceManager: PreferencesManager

    private lateinit var lessonData: ArrayList<LessonData.lessionData>
    lateinit var apiClient: APIClient
    lateinit var lessAdapter: ViewLessionAdapter
    lateinit var excAdapter: Excercise_list_Adapter
    var audioData: Exercise.Audio? = null

    var position: Int? = null
    var id: Int? = null
    var idLibrary: Int? = null
    lateinit var timerData: MutableList<Exercise.Timer>

    var idforviewlesson: Int? = null

    var lposition: Int? = null
    lateinit var txt: TextView
    var GroupAttends: String? = null
    var sectionName: String? = null

    var excId = SelectedValue(null)
    var proId = SelectedValue(null)

    private var isTimerRunning = false
    var roundSize = 0
    var cycleSize = 0

    private var isPaused = false
    private var timeRemainingInMillis: Long = 0
    private var currentCountdownPhase: String? = null
    private var countDownTimer: CountDownTimer? = null

    private var mediaPlayer: MediaPlayer? = null

    var cycleIndex = 1
    private var roundIndex: Int = 0
    private var totalRounds: Int = 0
    private var currentRep: Int = 0
    private var totalRep: Int = 0
    var currentData: Exercise.Timer? = null




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



        val userType = preferenceManager.GetFlage()

        if (userType == "Athlete") {
            viewLessonBinding.cardDuplicate.visibility = View.GONE
            getLessonListAthlete()
        } else {
            getLessonList()
        }

        if (sectionName != null) {
            Log.d("OSKD", "onCreate: $sectionName")


        }

    }

    private fun initViews() {
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        preferenceManager = PreferencesManager(this)
        timerData = mutableListOf()

        id = intent.getIntExtra("lessonId", 0)
        idforviewlesson = intent.getIntExtra("id", 0)
        position = intent.getIntExtra("position", 0)
        val totalTime = intent.getStringExtra("total_time")
        val sectionTime = intent.getStringExtra("section_time")
        val name = intent.getStringExtra("name")
        idLibrary = intent.getIntExtra("LessonLibraryId", 0)
        lposition = intent.getIntExtra("LessonLibraryPosition", 0)
        GroupAttends = intent.getStringExtra("GroupAttends")
//        sectionName = intent.getStringExtra("section_name")

        Log.d("JSJSJSJJS", "initViews: $GroupAttends")
        Log.d("JSJSJSJJS", "initViews: $idLibrary")
        Log.d("CCVCVCVVCVCV", "initViews: " + id)
        Log.d("CCVCVCVVCVCV", "initViews: " + position)
        Log.d("CCVCVCVVCVCV", "initViews: " + idforviewlesson)

        if (GroupAttends == "" || GroupAttends == "null" || GroupAttends == null) {
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

        val finalId =
            if (idforviewlesson == null || idforviewlesson == 0 || idforviewlesson.toString() == "0") idLibrary else idforviewlesson


        val fi = if (finalId == 0 || finalId == null || finalId.toString() == "0") id else finalId

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
            window?.setLayout(
                (displayMetrics.widthPixels * 0.9f).toInt(),
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        dialog.show()
        dialog.findViewById<CardView>(R.id.card_cancel).setOnClickListener {
            viewLessonBinding.main.setBackgroundColor(resources.getColor(R.color.black))
            dialog.dismiss()
        }
        dialog.findViewById<CardView>(R.id.card_apply).setOnClickListener {
            viewLessonBinding.main.setBackgroundColor(resources.getColor(R.color.black))

            dialog.dismiss()
            duplicateLesson()
        }
    }


    private fun populateLinearLayout(
        context: Context,
        container: LinearLayout,
        dataList: List<String>,
        allPrograms: List<LessonData.Lesson_Programs>,
        onSectionSelected: (List<LessonData.Lesson_Programs>) -> Unit,
        onExercisesSelected: (ArrayList<ProgramListData.Program>, Int?) -> Unit
    ) {
        container.removeAllViews()
        viewLessonBinding.timerLayout.removeAllViews()

        var selectedTextView: TextView? = null

        for ((index, sectionName) in dataList.withIndex()) {
            val itemView = LayoutInflater.from(context)
                .inflate(R.layout.view_select_section_layout, container, false)
            val textView = itemView.findViewById<TextView>(R.id.name)
            textView.text = sectionName
            textView.setBackgroundResource(R.drawable.card_not_select)

            val handleSelection = {
                val filteredPrograms = allPrograms.filter {
                    it.program?.section?.name == sectionName
                }

                onSectionSelected(filteredPrograms)

                val exercises = filteredPrograms.firstOrNull()?.program?.program_exercises.orEmpty()
                val selectedExerciseId = exercises.firstOrNull()?.id
                onExercisesSelected(ArrayList(exercises), selectedExerciseId)

                // Clear previous timers
                viewLessonBinding.timerLayout.removeAllViews()

                // Show timer name for the first exercise only
                exercises.firstOrNull()?.exercise?.timer_name?.let { timerName ->
                    addTimerNameView(
                        context = context,
                        parentLayout = viewLessonBinding.timerLayout,
                        allPrograms = timerName
                    )
                }

            }

            if (index == 0) {
                textView.setBackgroundResource(R.drawable.card_select_1)
                selectedTextView = textView
                handleSelection()
            }

            textView.setOnClickListener {
                selectedTextView?.setBackgroundResource(R.drawable.card_not_select)
                textView.setBackgroundResource(R.drawable.card_select_1)
                selectedTextView = textView
                handleSelection()
            }

            container.addView(itemView)
        }
    }





    private fun duplicateLesson() {
        viewLessonBinding.viewLessonProgress.visibility = View.VISIBLE

        val iddd = intent.getIntExtra("id", 0)

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

                        val idToUse = if ((id as? String)?.isNullOrEmpty() == true || id == 0) {
                            idforviewlesson
                        } else {
                            id
                        }

                        val FinalId = if (idToUse == 0 || idToUse == null) idLibrary else idToUse

                        Log.d("DJDJDJDJDJDJ", "onResponse: $idToUse")
                        Log.d("566556", "onResponse: ${data.get(0).section?.name}")

                        val filteredData = data.filter { it.id == FinalId }

                        val sectionNamesList = filteredData.flatMap { item ->
                            item.lesson_programs.orEmpty().mapNotNull { lessonProgram ->
                                lessonProgram.program?.section?.name
                            }
                        }


                        val sectionName = sectionNamesList.joinToString(", ")

                        val dataList = sectionNamesList.distinct()
                        val allPrograms = filteredData.flatMap { it.lesson_programs.orEmpty() }

                        populateLinearLayout(
                            this@ViewLessonActivity,
                            viewLessonBinding.linearLayout,
                            dataList,
                            allPrograms,
                            onSectionSelected = { filteredPrograms ->
                                val selectedProgramId = filteredPrograms.firstOrNull()?.id
                                initRecyclerview(ArrayList(filteredPrograms), selectedProgramId)
                            },
                            onExercisesSelected = { exerciseList, selectedExerciseId ->
                                initRecycler(exerciseList, selectedExerciseId)
                            }
                        )

//                        allPrograms.forEach { lessonProgram ->
//                            lessonProgram.program?.program_exercises.orEmpty().forEach { programExercise ->
//                                val timerName = programExercise.exercise?.timer_name
//                                    addTimerNameView(
//                                        context = this@ViewLessonActivity,
//                                        parentLayout = viewLessonBinding.timerLayout,
//                                        allPrograms = timerName
//                                    )
//
//                            }
//                        }

                        try {
                            if (position != null && position!! >= 0 && position!! < lessonData.size) {
                                val program = lessonData[position!!].lesson_programs
                                if (program != null && program.isNotEmpty()) {
                                    proId = SelectedValue(program.firstOrNull()?.id)

                                    Log.d("FFFFFFFFF", "onResponse: ${proId.id}")

                                    initRecyclerview(program, proId.id)
                                }
                            } else {
                                Log.d(
                                    "LessonData",
                                    "Position: $position, LessonData size: ${lessonData.size}"
                                )
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

    private fun addTimerNameView(
        context: Context,
        parentLayout: LinearLayout,
        allPrograms: ProgramListData.TimerName?
    ) {
        parentLayout.removeAllViews()

        allPrograms?.timer?.forEachIndexed { index, timer ->
            val view = LayoutInflater.from(context).inflate(R.layout.timer_layout, parentLayout, false)
            val txtTimerName = view.findViewById<TextView>(R.id.time)
            val txtTimerPause = view.findViewById<TextView>(R.id.tvTimer)
            val txtTimerWeight = view.findViewById<TextView>(R.id.tv_weight)
            val txtTimerCycleNo = view.findViewById<TextView>(R.id.tv_cycle)
            val txtTimerPauses = view.findViewById<TextView>(R.id.tv_pause)
            val txtTimerDistance = view.findViewById<TextView>(R.id.tv_distance)
            val txtTimerRound = view.findViewById<TextView>(R.id.tv_round)
            val play = view.findViewById<ImageView>(R.id.play)

            txt= view.findViewById<TextView>(R.id.time)
            txtTimerName.text = timer.timer_time ?: ""
            txtTimerRound.text = timer.timer_reps ?: ""
            txtTimerPause.text = timer.timer_pause ?: ""
            txtTimerWeight.text = timer.timer_weight ?: ""
            txtTimerCycleNo.text = (index + 1).toString()
            txtTimerPauses.text = timer.timer_pause_timer ?: ""
            txtTimerDistance.text = timer.timer_distance ?: ""

            timerData.addAll(listOf(timer))

            audioData = timer.timer_audio

            play.setOnClickListener {
                play.setImageDrawable(resources.getDrawable(R.drawable.ic_pause_1))
                try {

                    if (isTimerRunning) {
                        pauseCountdown(play)
                    } else {
                        if (isPaused) {
                            resumeCountdown(play,txtTimerName)
                        } else {
                            setupTimerData(mutableListOf(timer), txtTimerName)
                        }
                    }
                } catch (e: Exception) {
                    Log.d("TAG", "onResponse: $e")
                }
            }

            parentLayout.addView(view)
        }
    }

    private fun setupTimerData(timerData: MutableList<Exercise.Timer>, text: TextView) {
        cycleSize = timerData.size

        for (i in timerData) {
            startRound(cycleIndex, 1, roundSize, i)
        }
        startCycle(1)
    }

    fun startCycle(cycleIndex: Int) {
        if (cycleIndex <= cycleSize) {
            val currentTimer = timerData[cycleIndex - 1]
            val totalRounds =
                currentTimer.timer_set?.replace(Regex("[^\\d]"), "")?.toIntOrNull() ?: 0
            roundSize = totalRounds

            startRound(cycleIndex, 1, roundSize, currentTimer)
        }
    }


    fun startRound(
        cycleIndex: Int,
        roundIndex: Int,
        totalRounds: Int,
        timerData: Exercise.Timer,
        currentRep: Int = 1,
    ) {
        val tot = timerData.timer_reps?.replace(Regex("[^\\d]"), "")?.toIntOrNull() ?: 1
        this.cycleIndex = cycleIndex
        this.roundIndex = roundIndex
        this.totalRounds = totalRounds
        this.currentRep = currentRep
        this.currentData = timerData
        this.totalRep = tot

        Log.d(
            "Repet :-",
            "$cycleIndex \t $roundIndex \t $totalRounds \t$currentRep \t$currentData \t $totalRep"
        )

        val timeInMillis = timeToMillis(timerData.timer_time ?: "00:00:00")
        isTimerRunning = true
        currentCountdownPhase = "mainTimer"
        //currentTimer.set?.replace(Regex("[^\\d]"), "")?.toIntOrNull() ?: 0
        val totalRep = timerData.timer_reps!!.replace(Regex("[^\\d]"), "").toIntOrNull() ?: 1
        if (roundIndex <= totalRounds) {
//            viewTimerBinding.tvRound.text = "${roundIndex} Round"
//            viewTimerBinding.tvCycle.text = "$cycleIndex"


            if (timerData.timer_weight == "null" || timerData.timer_weight == "" || timerData.timer_weight == null) {
//                viewTimerBinding.tvWeight.text = "weight"
            } else {
//                viewTimerBinding.tvWeight.text = timerData.timer_weight
            }

            if (timerData.timer_distance == "null" || timerData.timer_distance == "" || timerData.timer_distance == null) {
//                viewTimerBinding.tvDistance.text = "distance"
            } else {
//                viewTimerBinding.tvDistance.text = timerData.timer_distance
            }
//            tvTimer.text = timerData.timer_pause
//            tvPause.text = timerData.timer_pause_timer
//            text = timerData.timer_pause_timer
//
//            viewTimerBinding.time.setTextColor(
//                ColorStateList.valueOf(
//                    resources.getColor(
//                        R.color.white
//                    )
//                )
//            )

            Log.d("HDHHDHDHD", "startRound:  Okayidh  ${audioData?.audio}")

            playMusic("https://uat.4trainersapp.com" + audioData!!.audio)

            countDownTimer = object : CountDownTimer(timeInMillis, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    timeRemainingInMillis = millisUntilFinished
                    val timeRemaining = formatMillisToTime(millisUntilFinished)
                    txt.text = timeRemaining
                }

                override fun onFinish() {
                    isTimerRunning = false
                    stopMusic()
//                    startPauseCountdown(
//                        cycleIndex,
//                        roundIndex,
//                        totalRounds,
//                        timerData,
//                        currentRep,
//                        totalRep
//                    )
                }
            }.start()
        }
    }

    private fun timeToMillis(time: String): Long {
        val parts = time.split(":").map { it.toIntOrNull() ?: 0 }
        val hours = parts.getOrNull(0) ?: 0
        val minutes = parts.getOrNull(1) ?: 0
        val seconds = parts.getOrNull(2) ?: 0
        return (hours * 3600 + minutes * 60 + seconds) * 1000L
    }


    private fun playMusic(musicPath: String?) {
        // Stop any currently playing music
        stopMusic()

        Log.d("KJDJHDGHDGuyvyu", "playMusic: $musicPath")
        if (musicPath.isNullOrEmpty()) return

        // Stop any currently playing music
        stopMusic()

        // Initialize MediaPlayer with the music path (URL or file path)
        mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(musicPath)  // Set the data source from the API path (URL or file)
                prepare()                 // Prepare the media player asynchronously
                isLooping = false         // Ensure the music doesn't loop
                start()                   // Start playing the music
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e("MediaPlayer", "Error playing music: $e")
            }
        }
    }

    fun resumeCountdown(play: ImageView, time: TextView) {
        // Resume the correct countdown phase based on the current phase (timer, pause, cycle pause)

        isPaused = false
        isTimerRunning = true

        play.setImageDrawable(resources.getDrawable(R.drawable.ic_pause_1))

//        if (currentCountdownPhase == "mainTimer") {
////            resumeMainTimerCountdown()
//        } else if (currentCountdownPhase == "pauseTimer") {
////            resumePauseCountdown()
//        } else if (currentCountdownPhase == "cyclePauseTimer") {
            resumeCyclePauseCountdown(time)

    }

    private fun resumeCyclePauseCountdown(time: TextView) {
        countDownTimer = object : CountDownTimer(timeRemainingInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeRemainingInMillis = millisUntilFinished
                time.text = formatMillisToTime(millisUntilFinished)
            }

            override fun onFinish() {
                stopMusic()
                startCycle(cycleIndex + 1)
            }
        }.start()


        mediaPlayer?.start()
    }


    private fun formatMillisToTime(millis: Long): String {
        val seconds = (millis / 1000) % 60
        val minutes = (millis / (1000 * 60)) % 60
        val hours = (millis / (1000 * 60 * 60)) % 24
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

//    fun startCycle(cycleIndex: Int) {
//        if (cycleIndex <= cycleSize) {
////            val currentTimer = timerData[cycleIndex - 1]
//            val totalRounds =
//                currentTimer.timer_set?.replace(Regex("[^\\d]"), "")?.toIntOrNull() ?: 0
//            roundSize = totalRounds
//
//            // Start the first round of the current cycle
//            startRound(cycleIndex, 1, roundSize, currentTimer)
//        }
//    }


    private fun stopMusic() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }
        mediaPlayer = null
    }
    fun pauseCountdown(image: ImageView) {
//        if (::countDownTimer.isInitialized) {
//            countDownTimer.cancel()  // Stop the countdown
//        }

        isPaused = true
        isTimerRunning = false

        countDownTimer?.cancel()
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()  // Pause the music
            }
        }
        image.setImageDrawable(resources.getDrawable(R.drawable.ic_play_1))

    }

    private fun getLessonListAthlete() {
        viewLessonBinding.viewLessonProgress.visibility = View.VISIBLE
        Log.d(":DJDJDDJJDJ", "getLessonListAthlete: $id")
        apiInterface.GetLessonAthlete(id)?.enqueue(object : Callback<LessonData?> {
            override fun onResponse(call: Call<LessonData?>, response: Response<LessonData?>) {
                viewLessonBinding.viewLessonProgress.visibility = View.GONE

                Log.d("APPPPPPPP", "onResponse: ${response.message().toString()}")
                Log.d("APPPPPPPP", "onResponse: ${response.body()}")
                Log.d("APPPPPPPP", "onResponse: ${response.errorBody().toString()}")
                Log.d("APPPPPPPP", "onResponse: ${response.code().toString()}")
                Log.d("APPPPPPPP", "onResponse: ${response}")

                if (response.isSuccessful) {
                    response.body()?.data?.let { data ->

                        val idToUse = if ((id as? String)?.isNullOrEmpty() == true || id == 0) {
                            idforviewlesson
                        } else {
                            id
                        }

                        val FinalId = if (idToUse == 0 || idToUse == null) idLibrary else idToUse

                        Log.d("DJDJDJDJDJDJ", "onResponse: $idToUse")

                        val filteredData = data.filter { it.id == FinalId }


                        val sectionNamesList = filteredData.flatMap { item ->
                            item.lesson_programs.orEmpty().mapNotNull { lessonProgram ->
                                lessonProgram.program?.section?.name
                            }
                        }

                        val sectionName = sectionNamesList.joinToString(", ")

                        val dataList = sectionNamesList.distinct()
                        val allPrograms = filteredData.flatMap { it.lesson_programs.orEmpty() }

                        populateLinearLayout(
                            this@ViewLessonActivity,
                            viewLessonBinding.linearLayout,
                            dataList,
                            allPrograms,
                            onSectionSelected = { filteredPrograms ->
                                val selectedProgramId = filteredPrograms.firstOrNull()?.id
                                initRecyclerview(ArrayList(filteredPrograms), selectedProgramId)
                            },
                            onExercisesSelected = { exerciseList, selectedExerciseId ->
                                initRecycler(exerciseList, selectedExerciseId)
                            }
                        )


                        try {
                            if (position != null && position!! >= 0 && position!! < lessonData.size) {
                                val program = lessonData[position!!].lesson_programs
                                if (program != null && program.isNotEmpty()) {
                                    proId = SelectedValue(program.firstOrNull()?.id)
                                    Log.d("FFFFFFFFF", "onResponse: ${proId.id}")
                                    initRecyclerview(program, proId.id)
                                }
                            } else {
                                Log.d(
                                    "LessonData",
                                    "Position: $position, LessonData size: ${lessonData.size}"
                                )
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
            Log.d(
                "ProgramData",
                "Program: ${program!!.firstOrNull()?.program}, ID: ${program.firstOrNull()?.id}"
            )

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
        viewLessonBinding.rlyExercise.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        excAdapter = Excercise_list_Adapter(data, this, this,this, initialSelectId)
        viewLessonBinding.rlyExercise.adapter = excAdapter
    }

    override fun onItemClicked(view: View, position: Int, type: Long, string: String) {
        if (string == "Click") {
            proId.id = type.toInt()
        }

        Log.d("XEXEXEXEXE", "onItemClick: ${string}")
        if (string == "Edit") {
        } else if (string == "fav") {
            Log.d("UNFAV", "onItemClick: FAV")

            viewLessonBinding.viewLessonProgress.visibility = View.VISIBLE
            val id: MultipartBody.Part = MultipartBody.Part.createFormData("id", type.toInt().toString())
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
                            viewLessonBinding.viewLessonProgress.visibility = View.GONE
                            val userType = preferenceManager.GetFlage()
                            if (userType == "Athlete"){
                                getLessonListAthlete()
                            }else{
                                getLessonList()
                            }
//                        Toast.makeText(this@New_Program_Activity, "" + Message, Toast.LENGTH_SHORT)
//                            .show()
//                        finish()
//                        startActivity(intent)
                        } else {
                            viewLessonBinding.viewLessonProgress.visibility = View.GONE
                            Toast.makeText(
                                this@ViewLessonActivity,
                                "" + Message,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }

                    } else if (response.code() == 403) {
                        Utils.setUnAuthDialog(this@ViewLessonActivity)
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
                        Toast.makeText(this@ViewLessonActivity, "" + message, Toast.LENGTH_SHORT)
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    viewLessonBinding.viewLessonProgress.visibility = View.GONE
                    Toast.makeText(this@ViewLessonActivity, "" + t.message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }
            })
        } else if (string == "unfav") {
            Log.d("UNFAV", "onItemClick: UNFAV")
            viewLessonBinding.viewLessonProgress.visibility = View.VISIBLE
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
                                viewLessonBinding.viewLessonProgress.visibility = View.GONE
                                val userType = preferenceManager.GetFlage()
                                if (userType == "Athlete"){
                                    getLessonListAthlete()
                                }else{
                                    getLessonList()
                                }
//                                    getData()
//                            Toast.makeText(
//                                this@New_Program_Activity,
//                                "" + Message,
//                                Toast.LENGTH_SHORT
//                            )
//                                .show()
//                            finish()
//                            startActivity(intent)
                            } else {
                                viewLessonBinding.viewLessonProgress.visibility = View.GONE
                                Toast.makeText(
                                    this@ViewLessonActivity,
                                    "" + Message,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        } else if (response.code() == 403) {
                            Utils.setUnAuthDialog(this@ViewLessonActivity)
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
                                this@ViewLessonActivity,
                                "" + message,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            call.cancel()
                        }

                    }

                    override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                        viewLessonBinding.viewLessonProgress.visibility = View.GONE
                        Toast.makeText(
                            this@ViewLessonActivity,
                            "" + t.message,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                })
        } else if (string == "favEX"){
            viewLessonBinding.viewLessonProgress.visibility = View.VISIBLE
            val id: MultipartBody.Part =
                MultipartBody.Part.createFormData("id", type.toInt().toString())
            apiInterface.Favourite_Exercise(id)?.enqueue(object : Callback<RegisterData?> {
                override fun onResponse(
                    call: Call<RegisterData?>,
                    response: Response<RegisterData?>
                ) {
                    Log.d("TAG", response.code().toString() + "")
                    viewLessonBinding.viewLessonProgress.visibility = View.GONE
                    val code = response.code()
                    if (code == 200) {
                        val resource: RegisterData? = response.body()
                        val Success: Boolean = resource?.status!!
                        val Message: String = resource.message!!
                        if (Success) {
                            viewLessonBinding.viewLessonProgress.visibility = View.GONE
                            val userType = preferenceManager.GetFlage()
                            if (userType == "Athlete"){
                                getLessonListAthlete()
                            }else{
                                getLessonList()
                            }
                        } else {
                            viewLessonBinding.viewLessonProgress.visibility = View.GONE

                        }
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
                    viewLessonBinding.viewLessonProgress.visibility = View.GONE
                    Toast.makeText(this@ViewLessonActivity, "" + t.message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }
            })

        } else if (string == "unfavEX"){
            viewLessonBinding.viewLessonProgress.visibility = View.VISIBLE
            apiInterface.DeleteFavourite_Exercise(type.toInt())
                ?.enqueue(object : Callback<RegisterData?> {
                    override fun onResponse(
                        call: Call<RegisterData?>,
                        response: Response<RegisterData?>
                    ) {
                        Log.d("TAG", response.code().toString() + "")
                        viewLessonBinding.viewLessonProgress.visibility = View.GONE
                        val code = response.code()
                        if (code == 200) {
                            val resource: RegisterData? = response.body()
                            val Success: Boolean = resource?.status!!
                            val Message: String = resource.message!!
                            if (Success) {
                                viewLessonBinding.viewLessonProgress.visibility = View.GONE
                                val userType = preferenceManager.GetFlage()
                                if (userType == "Athlete"){
                                    getLessonListAthlete()
                                }else{
                                    getLessonList()
                                }
                            } else {
                                viewLessonBinding.viewLessonProgress.visibility = View.GONE

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

                    override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                        viewLessonBinding.viewLessonProgress.visibility = View.GONE
                        Toast.makeText(this@ViewLessonActivity, "" + t.message, Toast.LENGTH_SHORT)
                            .show()
                        call.cancel()
                    }
                })
        }else{

//          ZZZZZ
        }

    }

    override fun onItemClick(id: Int, name: String, position: Int) {
        excAdapter?.updateSelectedId(id)

        viewLessonBinding.timerLayout.removeAllViews()

        val selectedExercise = excAdapter?.splist?.firstOrNull { it.exercise?.id == id }

        selectedExercise?.exercise?.timer_name?.let { timerName ->
            addTimerNameView(
                context = this@ViewLessonActivity,
                parentLayout = viewLessonBinding.timerLayout,
                allPrograms = timerName
            )

        }
    }

}
