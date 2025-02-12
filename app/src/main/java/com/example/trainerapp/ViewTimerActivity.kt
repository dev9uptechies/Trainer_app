package com.example.trainerapp

import android.content.res.ColorStateList
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.CycleAdapter
import com.example.OnItemClickListener
import com.example.model.newClass.excercise.Exercise
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.databinding.ActivityViewTimerBinding
import okio.IOException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewTimerActivity : AppCompatActivity(), OnItemClickListener.OnItemClickCallback {
    lateinit var viewTimerBinding: ActivityViewTimerBinding
    var id: Int? = null
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    lateinit var Progress: ProgressBar
    lateinit var adapter: CycleAdapter

    var timer: Exercise.TimerName? = null
    lateinit var timerData: MutableList<Exercise.Timer>
    var currentData: Exercise.Timer? = null
    var audioData: Exercise.Audio? = null
    var pauseAudioData: Exercise.PauseTimeAudio? = null
    var pauseBetweenAudioData: Exercise.PauseBetweenTimeAudio? = null
    var roundSize = 0
    var cycleSize = 0
    var time = 0
    var cycleIndex = 1

    private var countDownTimer: CountDownTimer? = null
    private var mediaPlayer: MediaPlayer? = null

    private var isPaused = false
    private var timeRemainingInMillis: Long = 0
    private var currentCountdownPhase: String? = null


    private var roundIndex: Int = 0
    private var totalRounds: Int = 0
    private var currentRep: Int = 0
    private var totalRep: Int = 0
    private var isTimerRunning = false

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
                        Utils.setUnAuthDialog(this@ViewTimerActivity)
                    } else {
                        Toast.makeText(
                            this@ViewTimerActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Toast.makeText(
                        this@ViewTimerActivity,
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
        viewTimerBinding = ActivityViewTimerBinding.inflate(layoutInflater)
        setContentView(viewTimerBinding.root)
        initViews()
        GetExercise()

        this.onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
                stopMusic()
                countDownTimer?.cancel()
            }

        })
        viewTimerBinding.play.setOnClickListener {
            viewTimerBinding.play.setImageDrawable(resources.getDrawable(R.drawable.ic_pause_1))
            try {
                Log.d("Current :-", "$currentCountdownPhase")
                Log.d("time running :-", "$isTimerRunning")
                Log.d("is pause running :-", "$isPaused")
                if (isTimerRunning) {
                    // If timer is running, pause it
                    pauseCountdown()
                } else {
                    // If timer is paused, resume it
                    if (isPaused) {
                        resumeCountdown()
                    } else {
                        setupTimerData(timerData)  // Start the countdown from the beginning
                    }
                }
            } catch (e: Exception) {
                Log.d("TAG", "onResponse: $e")
            }
        }
        viewTimerBinding.reset.setOnClickListener {
            resetData()
        }
        viewTimerBinding.back.setOnClickListener {
            finish()
            stopMusic()
            countDownTimer?.cancel()
        }
    }

    fun initViews() {
        apiClient = APIClient(this)
        Progress = viewTimerBinding.Progress
        apiInterface = apiClient.client().create(APIInterface::class.java)
        id = intent.getIntExtra("id", 0)
        timerData = mutableListOf()
    }

    private fun setupTimerData(timerData: MutableList<Exercise.Timer>) {
        cycleSize = timerData.size

        for (i in timerData) {
            startRound(cycleIndex, 1, roundSize, i)
        }
        startCycle(1)
    }

    fun startRound(
        cycleIndex: Int,
        roundIndex: Int,
        totalRounds: Int,
        timerData: Exercise.Timer,
        currentRep: Int = 1
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
//        val totalRep = timerData.reps!!.replace(Regex("[^\\d]"), "").toIntOrNull() ?: 1
        if (roundIndex <= totalRounds) {
            viewTimerBinding.tvRound.text = "${roundIndex} Round"
            viewTimerBinding.tvCycle.text = "$cycleIndex"

            if (timerData.timer_weight == "null" || timerData.timer_weight == "" || timerData.timer_weight == null) {
                viewTimerBinding.tvWeight.text = "weight"
            } else {
                viewTimerBinding.tvWeight.text = timerData.timer_weight
            }

            if (timerData.timer_distance == "null" || timerData.timer_distance == "" || timerData.timer_distance == null) {
                viewTimerBinding.tvDistance.text = "distance"
            } else {
                viewTimerBinding.tvDistance.text = timerData.timer_distance
            }
            viewTimerBinding.tvTimer.text = timerData.timer_time
            viewTimerBinding.tvPause.text = timerData.timer_pause
            viewTimerBinding.tvPauseBetween.text = timerData.timer_pause_timer

            viewTimerBinding.time.setTextColor(
                ColorStateList.valueOf(
                    resources.getColor(
                        R.color.white
                    )
                )
            )

            playMusic("https://4trainersapp.com" + audioData!!.audio)

            countDownTimer = object : CountDownTimer(timeInMillis, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    timeRemainingInMillis = millisUntilFinished
                    val timeRemaining = formatMillisToTime(millisUntilFinished)
                    viewTimerBinding.time.text = timeRemaining
                }

                override fun onFinish() {
                    isTimerRunning = false
                    stopMusic()
                    startPauseCountdown(
                        cycleIndex,
                        roundIndex,
                        totalRounds,
                        timerData,
                        currentRep,
                        totalRep
                    )
                }
            }.start()
        }
    }

    private fun startPauseCountdown(
        cycleIndex: Int,
        roundIndex: Int,
        totalRounds: Int,
        timerData: Exercise.Timer,
        currentRep: Int,
        totalRep: Int
    ) {
        this.cycleIndex = cycleIndex
        this.roundIndex = roundIndex
        this.totalRounds = totalRounds
        this.currentRep = currentRep
        this.totalRep = totalRep
        isTimerRunning = true

        val pauseInMillis = pauseTimeToMillis(timerData.timer_pause ?: "00 min, 00 sec")
        currentCountdownPhase = "pauseTimer"
        if (pauseInMillis > 0) {
            // Change text color to red during pause
            viewTimerBinding.time.setTextColor(
                ColorStateList.valueOf(
                    resources.getColor(
                        R.color.red
                    )
                )
            )
            //viewTimerDetailsBinding.time.setTextColor(Color.RED)
            playMusic("https://4trainersapp.com" + pauseAudioData!!.pause_time_audio)
            // Start the pause countdown
            countDownTimer = object : CountDownTimer(pauseInMillis, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    timeRemainingInMillis = millisUntilFinished
                    val pauseRemaining = formatMillisToTime(millisUntilFinished)
                    viewTimerBinding.time.text = pauseRemaining
                }

                override fun onFinish() {
                    viewTimerBinding.time.text = "done!"
                    isTimerRunning = false
                    stopMusic()
                    // Reset text color to white
                    viewTimerBinding.time.setTextColor(
                        ColorStateList.valueOf(
                            resources.getColor(
                                R.color.white
                            )
                        )
                    )

                    if (roundIndex < totalRounds) {
                        startRound(cycleIndex, roundIndex + 1, totalRounds, timerData)
                    } else if (cycleIndex < cycleSize) {
                        //this@ViewTimerDetailsActivity.cycleIndex = 1
                        startPauseBetweenCycles(cycleIndex, totalRounds, timerData)
//                            startCycle(cycleIndex + 1)
                    } else {
                        resetData()
                        Log.d("TAG", "All cycles completed!")
                    }

                    // Continue with next rep, round, or cycle
//                    if (currentRep < totalRep) {
//                        // Repeat the current round for the next repetition
//                        startRound(cycleIndex, roundIndex, totalRounds, timerData, currentRep + 1)
//                    } else {
//                        // Move to the next round or cycle after completing the reps
//                        if (roundIndex < totalRounds) {
//                            startRound(cycleIndex, roundIndex + 1, totalRounds, timerData)
//                        } else if (cycleIndex < cycleSize) {
//                            //this@ViewTimerDetailsActivity.cycleIndex = 1
//                            startPauseBetweenCycles(cycleIndex, totalRounds, timerData)
////                            startCycle(cycleIndex + 1)
//                        } else {
//                            resetData()
//                            Log.d("TAG", "All cycles completed!")
//                        }
//                    }
                }
            }.start()
        } else {
            // If no pause time, move directly to next rep, round, or cycle
            if (roundIndex < totalRounds) {
                startRound(cycleIndex, roundIndex + 1, totalRounds, timerData)
            } else if (cycleIndex < cycleSize) {
                //this@ViewTimerDetailsActivity.cycleIndex = 1
                startPauseBetweenCycles(cycleIndex, totalRounds, timerData)
//                    startCycle(cycleIndex + 1)
            } else {
                resetData()
                Log.d("TAG", "All cycles completed!")
            }
//            if (currentRep < totalRep) {
//                startRound(cycleIndex, roundIndex, totalRounds, timerData, currentRep + 1)
//            } else {
//                if (roundIndex < totalRounds) {
//                    startRound(cycleIndex, roundIndex + 1, totalRounds, timerData)
//                } else if (cycleIndex < cycleSize) {
//                    //this@ViewTimerDetailsActivity.cycleIndex = 1
//                    startPauseBetweenCycles(cycleIndex, totalRounds, timerData)
////                    startCycle(cycleIndex + 1)
//                } else {
//                    resetData()
//                    Log.d("TAG", "All cycles completed!")
//                }
//            }
        }
    }

    private fun startPauseBetweenCycles(
        currentCycleIndex: Int,
        totalRounds: Int,
        timerData: Exercise.Timer
    ) {
        // Pause between cycles time from API
        val pauseBetweenInMillis =
            pauseTimeToMillis(timerData.timer_pause_timer ?: "00 min, 00 sec")
        isTimerRunning = true
        currentCountdownPhase = "cyclePauseTimer"
        if (pauseBetweenInMillis > 0) {
            // Set the timer text to the pause time and change the color to red
            //viewTimerDetailsBinding.tvTimer.text = timerData.pause_timer
            viewTimerBinding.time.setTextColor(
                ColorStateList.valueOf(
                    resources.getColor(
                        R.color.red
                    )
                )
            )
            playMusic("https://4trainersapp.com" + pauseBetweenAudioData!!.pause_between_audio)

            countDownTimer = object : CountDownTimer(pauseBetweenInMillis, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    timeRemainingInMillis = millisUntilFinished
                    // Format the remaining time to 00:00:00
                    val formattedTime = formatMillisToTime(millisUntilFinished)
                    viewTimerBinding.time.text = formattedTime
                }

                override fun onFinish() {
                    isTimerRunning = false
                    // Reset the color to white and move to the next cycle
                    stopMusic()
                    viewTimerBinding.time.setTextColor(
                        ColorStateList.valueOf(
                            resources.getColor(
                                R.color.white
                            )
                        )
                    )

                    // Start the next cycle
                    val nextCycleIndex = currentCycleIndex + 1
                    if (nextCycleIndex <= cycleSize) {
                        startCycle(nextCycleIndex)
                    } else {
                        // All cycles completed
                        Log.d("TAG", "All cycles and rounds completed!")
                    }
                }
            }.start()
        } else {
            // If no pause between cycles, directly move to the next cycle
            val nextCycleIndex = currentCycleIndex + 1
            if (nextCycleIndex <= cycleSize) {
                startCycle(nextCycleIndex)
            } else {
                // All cycles completed
                Log.d("TAG", "All cycles and rounds completed!")
            }
        }
    }

    fun startCycle(cycleIndex: Int) {
        if (cycleIndex <= cycleSize) {
            val currentTimer = timerData[cycleIndex - 1]
            val totalRounds =
                currentTimer.timer_set?.replace(Regex("[^\\d]"), "")?.toIntOrNull() ?: 0
            roundSize = totalRounds

            // Start the first round of the current cycle
            startRound(cycleIndex, 1, roundSize, currentTimer)
        }
    }

    private fun playMusic(musicPath: String?) {
        // Stop any currently playing music
        stopMusic()

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

    private fun timeToMillis(time: String): Long {
        val parts = time.split(":").map { it.toIntOrNull() ?: 0 }
        val hours = parts.getOrNull(0) ?: 0
        val minutes = parts.getOrNull(1) ?: 0
        val seconds = parts.getOrNull(2) ?: 0
        return (hours * 3600 + minutes * 60 + seconds) * 1000L
    }

    private fun formatMillisToTime(millis: Long): String {
        val seconds = (millis / 1000) % 60
        val minutes = (millis / (1000 * 60)) % 60
        val hours = (millis / (1000 * 60 * 60)) % 24
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    private fun pauseTimeToMillis(pauseTime: String): Long {
        val regex = Regex("(\\d+) min, (\\d+) sec")
        val matchResult = regex.find(pauseTime)

        return if (matchResult != null) {
            val (minutes, seconds) = matchResult.destructured
            ((minutes.toIntOrNull() ?: 0) * 60 + (seconds.toIntOrNull() ?: 0)) * 1000L
        } else {
            0L
        }
    }


    fun pauseCountdown() {
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
        viewTimerBinding.play.setImageDrawable(resources.getDrawable(R.drawable.ic_play_1))

    }

    fun resumeCountdown() {
        // Resume the correct countdown phase based on the current phase (timer, pause, cycle pause)

        isPaused = false
        isTimerRunning = true

        viewTimerBinding.play.setImageDrawable(resources.getDrawable(R.drawable.ic_pause_1))

        if (currentCountdownPhase == "mainTimer") {
            resumeMainTimerCountdown()
        } else if (currentCountdownPhase == "pauseTimer") {
            resumePauseCountdown()
        } else if (currentCountdownPhase == "cyclePauseTimer") {
            resumeCyclePauseCountdown()
        }
    }

    private fun resumeMainTimerCountdown() {
        countDownTimer = object : CountDownTimer(timeRemainingInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeRemainingInMillis = millisUntilFinished
                viewTimerBinding.time.text = formatMillisToTime(millisUntilFinished)
            }

            override fun onFinish() {
                stopMusic()
                startPauseCountdown(
                    cycleIndex,
                    roundIndex,
                    totalRounds,
                    currentData!!,
                    currentRep,
                    totalRep
                )
            }
        }.start()

        // Resume playing main timer music
        mediaPlayer?.start()
    }

    private fun resumePauseCountdown() {
        countDownTimer = object : CountDownTimer(timeRemainingInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeRemainingInMillis = millisUntilFinished
                viewTimerBinding.time.text = formatMillisToTime(millisUntilFinished)
            }

            override fun onFinish() {
                stopMusic()
                if (currentRep < totalRep) {
                    startRound(cycleIndex, roundIndex, totalRounds, currentData!!, currentRep + 1)
                } else {
                    if (roundIndex < totalRounds) {
                        startRound(cycleIndex, roundIndex + 1, totalRounds, currentData!!)
                    } else {
                        startPauseBetweenCycles(cycleIndex, totalRounds, currentData!!)
                    }
                }
            }
        }.start()

        // Resume playing pause timer music
        mediaPlayer?.start()
    }

    private fun resumeCyclePauseCountdown() {
        countDownTimer = object : CountDownTimer(timeRemainingInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeRemainingInMillis = millisUntilFinished
                viewTimerBinding.time.text = formatMillisToTime(millisUntilFinished)
            }

            override fun onFinish() {
                stopMusic()
                startCycle(cycleIndex + 1)
            }
        }.start()

        // Resume playing cycle pause music
        mediaPlayer?.start()
    }

    private fun stopMusic() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }
        mediaPlayer = null
    }

    private fun GetExercise() {

        Progress.visibility = View.VISIBLE
        try {
            apiInterface.GetExerciseData().enqueue(
                object : Callback<Exercise> {
                    override fun onResponse(call: Call<Exercise>, response: Response<Exercise>) {
                        val code = response.code()
                        Progress.visibility = View.GONE
                        if (code == 200) {
                            Log.d("Response :- ", "${response.body()}")
                            Log.d("Response :- ", "${response.code()}")
                            Log.d("Response :- ", "${response.body()!!.data!!.size}")
                            Log.d("Response :- ", "${response.isSuccessful}")
                            if (response.isSuccessful) {
                                val data = response.body()!!.data?.filter {
                                    it.id == id?.toInt()
                                }
                                if (data!!.isNotEmpty()) {
                                    initRecycler(data[0].cycles!!.toMutableList())
                                    setUpData(data)
                                    timer = data[0].timer_name
                                    setData(timer)
                                }
                                Progress.visibility = View.GONE
                            }
                        } else if (code == 403) {
                            Utils.setUnAuthDialog(this@ViewTimerActivity)
                        } else {
                            Toast.makeText(
                                this@ViewTimerActivity,
                                "" + response.message(),
                                Toast.LENGTH_SHORT
                            ).show()
                            call.cancel()
                        }
                    }

                    override fun onFailure(call: Call<Exercise>, t: Throwable) {
                        Progress.visibility = View.GONE
                        Log.d("Response :- ", "${t.message}")
                    }

                }
            )
        } catch (e: Exception) {
            Progress.visibility = View.GONE
            Log.d("Response :- ", "${e.message}")
        }

    }

    private fun setData(timer: Exercise.TimerName?) {
        // Safely access timer_name_timer
        val timerNameList = timer?.timer_name_timer
        if (!timerNameList.isNullOrEmpty()) {
            timerData.addAll(timerNameList) // Add data to timerData safely
            for (i in timerNameList) {
                setupTimerDataBasic(timerData) // Use timerData (not null)
                audioData = i.timer_audio
                pauseAudioData = i.timer_pause_time_audio
                pauseBetweenAudioData = i.timer_pause_between_time_audio
            }
        } else {
            Log.e("setData", "timer_name_timer is null or empty")
        }
    }


    private fun setUpData(data: List<Exercise.ExerciseData>?) {
        viewTimerBinding.tvSection.text =
            data!![0].section!!.section_name
        viewTimerBinding.tvGoal.text =
            data[0].goal!!.goal_name

        val namesString = data[0].exercise_equipments!!.map {
            it.exercise_equipment!!.equipment_name
        }.joinToString(separator = ", ")
        viewTimerBinding.tvEqupment.text = namesString
    }

    private fun setupTimerDataBasic(timerData: MutableList<Exercise.Timer>) {
        // Check if the list is empty before proceeding
        if (timerData.isNotEmpty()) {
            // Proceed with setting up the timer data only if the list has elements
            viewTimerBinding.tvRound.text = "01 Round"
            viewTimerBinding.tvCycle.text = "1"
            viewTimerBinding.tvTimer.text = timerData[0].timer_time
            viewTimerBinding.time.text = timerData[0].timer_time

            // Check if the weight field is empty or "null"
            if (timerData[0].timer_weight == "null" || timerData[0].timer_weight == "" || timerData[0].timer_weight == null) {
                viewTimerBinding.tvWeight.text = "weight"
            } else {
                viewTimerBinding.tvWeight.text = timerData[0].timer_weight
            }

            // Check if the distance field is empty or "null"
            if (timerData[0].timer_distance == "null" || timerData[0].timer_distance == "" || timerData[0].timer_distance == null) {
                viewTimerBinding.tvDistance.text = "distance"
            } else {
                viewTimerBinding.tvDistance.text = timerData[0].timer_distance
            }

            viewTimerBinding.tvPause.text = timerData[0].timer_pause
            viewTimerBinding.tvPauseBetween.text = timerData[0].timer_pause_timer
        } else {
            // Handle the case where the timerData list is empty
            Log.e("Error", "Timer data is empty!")
            // Optionally, display a message to the user or hide the timer-related views
            viewTimerBinding.tvRound.text = "00 Round"
            viewTimerBinding.tvCycle.text = "0"
            viewTimerBinding.tvTimer.text = "00:00:00"
            viewTimerBinding.time.text = "00:0000"
            viewTimerBinding.tvWeight.text = "0"
            viewTimerBinding.tvDistance.text = "0"
            viewTimerBinding.tvPause.text = "00 min,00 sec"
            viewTimerBinding.tvPauseBetween.text = "time"
        }
    }

    fun resetData() {
        viewTimerBinding.play.setImageDrawable(resources.getDrawable(R.drawable.ic_play_1))
        countDownTimer?.cancel()
        setupTimerDataBasic(timerData)
        cycleSize = timerData.size
        cycleIndex = 1
        roundSize = 0
        cycleSize = 0
        stopMusic()
        viewTimerBinding.time.setTextColor(
            ColorStateList.valueOf(
                resources.getColor(
                    R.color.white
                )
            )
        )
        isTimerRunning = false
        isPaused = false
    }

    private fun initRecycler(testDataList: MutableList<Exercise.Cycle>?) {
        Progress.visibility = View.GONE
        viewTimerBinding.cycleRecycler.layoutManager = LinearLayoutManager(this)
        adapter =
            CycleAdapter(testDataList, this, this)
        viewTimerBinding.cycleRecycler.adapter = adapter
    }

    override fun onItemClicked(view: View, position: Int, type: Long, string: String) {

    }
}