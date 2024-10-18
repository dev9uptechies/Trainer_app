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
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.Adapter.CycleTimerAdapter
import com.example.OnItemClickListener
import com.example.model.newClass.timer.Timer
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.databinding.ActivityViewTimerBinding
import okio.IOException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewTimerDetailsActivity : AppCompatActivity(), OnItemClickListener.OnItemClickCallback {
    lateinit var viewTimerDetailsBinding: ActivityViewTimerBinding
    var id: Int? = null
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    lateinit var Progress: ProgressBar
    lateinit var adapter: CycleTimerAdapter
    lateinit var timerData: MutableList<Timer.TimerX>
    var currentData: Timer.TimerX? = null
    var audioData: Timer.Audio? = null
    var pauseAudioData: Timer.PauseTimeAudio? = null
    var pauseBetweenAudioData: Timer.PauseBetweenTimeAudio? = null
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
                        Utils.setUnAuthDialog(this@ViewTimerDetailsActivity)
                    } else {
                        Toast.makeText(
                            this@ViewTimerDetailsActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Toast.makeText(
                        this@ViewTimerDetailsActivity,
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
        viewTimerDetailsBinding = ActivityViewTimerBinding.inflate(layoutInflater)
        setContentView(viewTimerDetailsBinding.root)
        initViews()
        GetTimerData()

        this.onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
                stopMusic()
                countDownTimer?.cancel()
            }

        })

        viewTimerDetailsBinding.back.setOnClickListener {
            countDownTimer?.cancel()
            stopMusic()
            finish()
        }

        viewTimerDetailsBinding.play.setOnClickListener {
            viewTimerDetailsBinding.play.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_pause_1,
                    null
                )
            )

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

//            try {
////                play = true
////                if (play) {
////                    viewTimerDetailsBinding.play.setImageDrawable(resources.getDrawable(R.drawable.pause))
////                    setupTimerData(timerData)
////                } else {
////
////                }
//                setupTimerData(timerData)
//                //startWorkout(timerData)
//            } catch (e: Exception) {
//                Log.d("TAG", "onResponse:$e")
//            }
//        }

        viewTimerDetailsBinding.reset.setOnClickListener {
            resetData()
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

        viewTimerDetailsBinding.play.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_pause_1,
                null
            )
        )
    }

    fun resumeCountdown() {
        // Resume the correct countdown phase based on the current phase (timer, pause, cycle pause)

        isPaused = false
        isTimerRunning = true

        viewTimerDetailsBinding.play.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_pause_1,
                null
            )
        )

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
                viewTimerDetailsBinding.time.text = formatMillisToTime(millisUntilFinished)
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
                viewTimerDetailsBinding.time.text = formatMillisToTime(millisUntilFinished)
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
                viewTimerDetailsBinding.time.text = formatMillisToTime(millisUntilFinished)
            }

            override fun onFinish() {
                stopMusic()
                startCycle(cycleIndex + 1)
            }
        }.start()

        // Resume playing cycle pause music
        mediaPlayer?.start()
    }

    private fun GetTimerData() {
        Progress.visibility = View.VISIBLE
        //timerData.clear()

        try {
            apiInterface.GetTimerData().enqueue(
                object : Callback<Timer> {
                    override fun onResponse(call: Call<Timer>, response: Response<Timer>) {
                        Progress.visibility = View.GONE
                        val code = response.code()
                        if (code == 200) {
                            if (response.isSuccessful) {
                                Progress.visibility = View.GONE
                                val data = response.body()!!.data!!.filter {
                                    it.id == id
                                }
                                if (data != null) {
                                    audioData = data[0].audio!!
                                    pauseAudioData = data[0].pause_time_audio!!
                                    pauseBetweenAudioData = data[0].pause_between_time_audio!!

                                    for (i in data) {
                                        for (j in i.timer!!) {
                                            timerData.add(j)
                                            Log.d(
                                                "TAG",
                                                "onResponse: ${j.set} \t ${j.time} \t ${j.reps} \t ${j.pause_timer} \t ${j.weight} \t ${j.distance} \t ${j.pause}"
                                            )
                                        }
                                    }
                                    initRecycler()
                                    setupTimerDataBasic(timerData)
                                }
                                //setupTimerData(timerData)

                            }
                        } else if (code == 403) {
                            Utils.setUnAuthDialog(this@ViewTimerDetailsActivity)
                        } else {
                            Toast.makeText(
                                this@ViewTimerDetailsActivity,
                                "" + response.message(),
                                Toast.LENGTH_SHORT
                            ).show()
                            call.cancel()
                        }
                    }

                    override fun onFailure(call: Call<Timer>, t: Throwable) {
                        Progress.visibility = View.GONE
                        call.cancel()
                        Log.d("TAG Category", t.message.toString() + "")
                    }
                }
            )
        } catch (e: Exception) {
            Log.d("Error :-", "${e.message}")
        }
    }

    private fun setupTimerDataBasic(timerData: MutableList<Timer.TimerX>) {
        viewTimerDetailsBinding.tvRound.text = "01 Round"
        viewTimerDetailsBinding.tvCycle.text = "1"
        viewTimerDetailsBinding.tvTimer.text = timerData[0].time
        viewTimerDetailsBinding.time.text = timerData[0].time
        if (timerData[0].weight == "null" || timerData[0].weight == "" || timerData[0].weight == null) {
            viewTimerDetailsBinding.tvWeight.text = "weight"
        } else {
            viewTimerDetailsBinding.tvWeight.text = timerData[0].weight
        }

        if (timerData[0].distance == "null" || timerData[0].distance == "" || timerData[0].distance == null) {
            viewTimerDetailsBinding.tvDistance.text = "distance"
        } else {
            viewTimerDetailsBinding.tvDistance.text = timerData[0].distance
        }
        viewTimerDetailsBinding.tvPause.text = timerData[0].pause
        viewTimerDetailsBinding.tvPauseBetween.text = timerData[0].pause_timer
    }

    fun resetData() {
        viewTimerDetailsBinding.play.setImageDrawable(
            ResourcesCompat.getDrawable(resources, R.drawable.ic_play_1, null)
        )
        countDownTimer?.cancel()
        setupTimerDataBasic(timerData)
        cycleSize = timerData.size
        cycleIndex = 1
        roundSize = 0
        cycleSize = 0
        stopMusic()
        viewTimerDetailsBinding.time.setTextColor(
            ColorStateList.valueOf(
                resources.getColor(
                    R.color.white
                )
            )
        )
        isTimerRunning = false
        isPaused = false
    }

    private fun setupTimerData(timerData: MutableList<Timer.TimerX>) {
        cycleSize = timerData.size

        for (i in timerData) {
            startRound(cycleIndex, 1, roundSize, i)
        }
        startCycle(1)
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

    private fun stopMusic() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }
        mediaPlayer = null
    }

    fun startRound(
        cycleIndex: Int,
        roundIndex: Int,
        totalRounds: Int,
        timerData: Timer.TimerX,
        currentRep: Int = 1
    ) {
        this.cycleIndex = cycleIndex
        this.roundIndex = roundIndex
        this.totalRounds = totalRounds
        this.currentRep = currentRep
        this.currentData = timerData
        this.totalRep = timerData.reps?.replace(Regex("[^\\d]"), "")?.toIntOrNull() ?: 1

        Log.d(
            "Repet :-",
            "$cycleIndex \t $roundIndex \t $totalRounds \t$currentRep \t$currentData \t $totalRep"
        )

        val timeInMillis = timeToMillis(timerData.time ?: "00:00:00")
        val initialFormattedTime = formatMillisToTime(timeInMillis)
        viewTimerDetailsBinding.time.text = initialFormattedTime
        isTimerRunning = true
        currentCountdownPhase = "mainTimer"
        //currentTimer.set?.replace(Regex("[^\\d]"), "")?.toIntOrNull() ?: 0
//        val totalRep = timerData.reps!!.replace(Regex("[^\\d]"), "").toIntOrNull() ?: 1
        if (roundIndex <= totalRounds) {
            viewTimerDetailsBinding.tvRound.text = "${roundIndex} Round"
            viewTimerDetailsBinding.tvCycle.text = "$cycleIndex"

            if (timerData.weight == "null" || timerData.weight == "" || timerData.weight == null) {
                viewTimerDetailsBinding.tvWeight.text = "weight"
            } else {
                viewTimerDetailsBinding.tvWeight.text = timerData.weight
            }

            if (timerData.distance == "null" || timerData.distance == "" || timerData.distance == null) {
                viewTimerDetailsBinding.tvDistance.text = "distance"
            } else {
                viewTimerDetailsBinding.tvDistance.text = timerData.distance
            }
            viewTimerDetailsBinding.tvTimer.text = timerData.time
            viewTimerDetailsBinding.tvPause.text = timerData.pause
            viewTimerDetailsBinding.tvPauseBetween.text = timerData.pause_timer



            viewTimerDetailsBinding.time.setTextColor(
                ColorStateList.valueOf(
                    resources.getColor(
                        R.color.white
                    )
                )
            )

            playMusic("https://trainers.codefriend.in" + audioData!!.audio)

            countDownTimer = object : CountDownTimer(timeInMillis, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    timeRemainingInMillis = millisUntilFinished
                    val timeRemaining = formatMillisToTime(millisUntilFinished)
                    viewTimerDetailsBinding.time.text = timeRemaining
                }

                override fun onFinish() {
                    isTimerRunning = false
                    viewTimerDetailsBinding.time.text = "done!"
                    stopMusic()
                    startPauseCountdown(
                        cycleIndex,
                        roundIndex,
                        totalRounds,
                        timerData,
                        currentRep,
                        totalRep
                    )

//                    val totalReps = timerData.reps.toIntOrNull() ?: 1
//                    Log.d("TAG", "startRound: $totalRep \t $currentRep")
//                    if (currentRep < totalRep) {
//                        // Repeat the current round until the number of reps is completed
//                        startRound(cycleIndex, roundIndex, totalRounds, timerData, currentRep + 1)
//                    } else {
//                        // Move to the next round or cycle after completing the reps
//                        if (roundIndex < totalRounds) {
//                            // Start the next round within the same cycle
//                            startRound(cycleIndex, roundIndex + 1, totalRounds, timerData)
//                        } else if (cycleIndex < cycleSize) {
//                            // Move to the next cycle
//                            this@ViewTimerDetailsActivity.cycleIndex = 1
//                            startCycle(cycleIndex + 1)
//                        } else {
//                            // All cycles and rounds are completed
//                            Log.d("TAG", "All cycles completed!")
//                        }
//                    }

//                    if (timerData.reps!!.toInt() == 1) {
//                        if (roundIndex < totalRounds) {
//                            // Start the next round within the same cycle
//                            startRound(cycleIndex, roundIndex + 1, totalRounds, timerData)
//                        } else if (cycleIndex < cycleSize) {
//                            // If all rounds are done, move to the next cycle
//                            this@ViewTimerDetailsActivity.cycleIndex = 1
//                            startCycle(cycleIndex + 1)
//                        } else {
//                            // All cycles are completed
//                            Log.d("TAG", "All cycles completed!")
//                        }
//                    }
                }
            }.start()
        }
    }

    private fun startPauseCountdown(
        cycleIndex: Int,
        roundIndex: Int,
        totalRounds: Int,
        timerData: Timer.TimerX,
        currentRep: Int,
        totalRep: Int
    ) {
        this.cycleIndex = cycleIndex
        this.roundIndex = roundIndex
        this.totalRounds = totalRounds
        this.currentRep = currentRep
        this.totalRep = totalRep
        isTimerRunning = true

        val pauseInMillis = pauseTimeToMillis(timerData.pause ?: "00 min, 00 sec")
        val initialFormattedTime = formatMillisToTime(pauseInMillis)
        viewTimerDetailsBinding.time.text = initialFormattedTime
        currentCountdownPhase = "pauseTimer"
        if (pauseInMillis > 0) {
            // Change text color to red during pause
            viewTimerDetailsBinding.time.setTextColor(
                ColorStateList.valueOf(
                    resources.getColor(
                        R.color.red
                    )
                )
            )


            //viewTimerDetailsBinding.time.setTextColor(Color.RED)
            playMusic("https://trainers.codefriend.in" + pauseAudioData!!.audio)
            // Start the pause countdown
            countDownTimer = object : CountDownTimer(pauseInMillis, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    timeRemainingInMillis = millisUntilFinished
                    val pauseRemaining = formatMillisToTime(millisUntilFinished)
                    viewTimerDetailsBinding.time.text = pauseRemaining
                }

                override fun onFinish() {
                    //viewTimerDetailsBinding.time.text = "done!"
                    isTimerRunning = false
                    stopMusic()
                    // Reset text color to white
                    viewTimerDetailsBinding.time.setTextColor(
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

            // If no pause time, move directly to next rep, round, or cycle
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
        timerData: Timer.TimerX
    ) {
        // Pause between cycles time from API
        val pauseBetweenInMillis = pauseTimeToMillis(timerData.pause_timer ?: "00 min, 00 sec")
        val initialFormattedTime = formatMillisToTime(pauseBetweenInMillis)
        viewTimerDetailsBinding.time.text = initialFormattedTime
        isTimerRunning = true
        currentCountdownPhase = "cyclePauseTimer"
        if (pauseBetweenInMillis > 0) {
            // Set the timer text to the pause time and change the color to red
            //viewTimerDetailsBinding.tvTimer.text = timerData.pause_timer
            viewTimerDetailsBinding.time.setTextColor(
                ColorStateList.valueOf(
                    resources.getColor(
                        R.color.red
                    )
                )
            )

            playMusic("https://trainers.codefriend.in" + pauseBetweenAudioData!!.audio)

            countDownTimer = object : CountDownTimer(pauseBetweenInMillis, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    timeRemainingInMillis = millisUntilFinished
                    // Format the remaining time to 00:00:00
                    val formattedTime = formatMillisToTime(millisUntilFinished)
                    viewTimerDetailsBinding.time.text = formattedTime
                }

                override fun onFinish() {
                    isTimerRunning = false
                    // Reset the color to white and move to the next cycle
                    stopMusic()
                    viewTimerDetailsBinding.time.setTextColor(
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
            val totalRounds = currentTimer.set?.replace(Regex("[^\\d]"), "")?.toIntOrNull() ?: 0
            roundSize = totalRounds

            // Start the first round of the current cycle
            startRound(cycleIndex, 1, roundSize, currentTimer)
        }
    }
//        for (i in timerData) {
//            val timeInMillis = timeToMillis(i.time ?: "00:00:00")
//            val numericValue = i.set?.replace(Regex("[^\\d]"), "")?.toIntOrNull() ?: 0
//            roundSize += numericValue
//            for (round in 0..roundSize) {
//                Log.d("Round :-", "$round == $roundSize")
//                viewTimerDetailsBinding.tvRound.text = "${round + 1} Round"
//                viewTimerDetailsBinding.tvCycle.text = "$cycleIndex"
//                object : CountDownTimer(timeInMillis, 1000) {
//
//                    // Callback function, fired on regular interval
//                    override fun onTick(millisUntilFinished: Long) {
////                        viewTimerDetailsBinding.time.text =
////                            "seconds remaining: " + millisUntilFinished / 1000
//
//                        val timeRemaining = formatMillisToTime(millisUntilFinished)
//                        viewTimerDetailsBinding.time.text = timeRemaining
//                    }
//
//                    // Callback function, fired
//                    // when the time is up
//                    override fun onFinish() {
//                        viewTimerDetailsBinding.time.text = "done!"
//                        if (cycleIndex + 1 <= cycleSize) {
//                            cycleIndex++
//                        } else {
//                            // All cycles are completed
//                            Log.d("TAG", "All cycles completed!")
//                        }
//                    }
//                }.start()
//
//                //round++
//
//            }
////            val timeInMillis = timeToMillis(i.time ?: "00:00:00")
////            val pauseTimeInMillis = timeToMillis(i.pause ?: "00 min, 00 sec")
//        }
//    }

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

//        fun startSet(setIndex: Int) {
//            if (setIndex < cycleSize) {
//                val currentTimer = timerData[setIndex]
//                val timeInMillis = timeToMillis(currentTimer.time ?: "00:00:00")
//                val pauseTimeInMillis = timeToMillis(currentTimer.pause ?: "00 min, 00 sec")
//
//                // Show the current set number
//                viewTimerDetailsBinding.tvRound.text = (setIndex + 1).toString()
//
//                // Start the countdown timer
//                object : CountDownTimer(timeInMillis, 1000) {
//                    override fun onTick(millisUntilFinished: Long) {
//                        val timeRemaining = formatMillisToTime(millisUntilFinished)
//                        viewTimerDetailsBinding.tvTimer.text = timeRemaining // Update timer UI
//                    }
//
//                    override fun onFinish() {
//                        // Set finished, move to the next one
//                        // Handle pause time between sets if any
//                        if (pauseTimeInMillis > 0) {
//                            startPauseTimer(pauseTimeInMillis, setIndex + 1)
//                        } else {
//                            // No pause, directly move to next set
//                            startSet(setIndex + 1)
//                        }
//                    }
//                }.start()
//            }
//        }

    // Function to handle pause between sets

//    }
//
//    fun startPauseTimer(pauseTimeInMillis: Long, nextSetIndex: Int) {
//        object : CountDownTimer(pauseTimeInMillis, 1000) {
//            override fun onTick(millisUntilFinished: Long) {
//                val pauseRemaining = formatMillisToTime(millisUntilFinished)
//                viewTimerDetailsBinding.tvTimer.text = pauseRemaining // Show pause time
//            }
//
//            override fun onFinish() {
////                 Start the next set after pause
//                viewTimerDetailsBinding.tvTimer.text = "" // Clear pause time UI
//                startSet(nextSetIndex)
//            }
//        }.start()
//    }
//
//    // Start the first set
////    startSet(roundSize)
//
//    private fun timeToMillis(time: String): Long {
//        val parts = time.split(":").map { it.toIntOrNull() ?: 0 }
//        val hours = parts.getOrNull(0) ?: 0
//        val minutes = parts.getOrNull(1) ?: 0
//        val seconds = parts.getOrNull(2) ?: 0
//        return (hours * 3600 + minutes * 60 + seconds) * 1000L
//    }
//
//    // Helper function to format milliseconds to time string
//    private fun formatMillisToTime(millis: Long): String {
//        val seconds = (millis / 1000) % 60
//        val minutes = (millis / (1000 * 60)) % 60
//        val hours = (millis / (1000 * 60 * 60)) % 24
//        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
//    }


//    fun startWorkout(timerData: MutableList<Timer.TimerX>) {
//        var currentCycle = 0
//
//        fun startNextCycle() {
//            if (currentCycle < timerData.size) {
//                val currentTimer = timerData[currentCycle]
//                val rounds = currentTimer.set?.replace(Regex("[^\\d]"), "")?.toIntOrNull() ?: 1
//                val reps = currentTimer.reps?.replace(Regex("[^\\d]"), "")?.toIntOrNull() ?: 1
//                val timeInMillis = timeToMillis(currentTimer.time ?: "00:00:00")
//                val pauseTimeInMillis = timeToMillis(currentTimer.pause ?: "00 min, 00 sec")
//
//                startRounds(rounds, reps, timeInMillis, pauseTimeInMillis) {
//                    Log.d("count", "$currentCycle $rounds $reps")
//                    currentCycle++
//                    startNextCycle()
//                }
//            } else {
//                // All cycles are complete
//                Toast.makeText(this, "Workout complete!", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        startNextCycle() // Start the first cycle
//    }
//
//    fun startRounds(
//        rounds: Int,
//        reps: Int,
//        timeInMillis: Long,
//        pauseInMillis: Long,
//        onCycleComplete: () -> Unit
//    ) {
//        var currentRound = 1
//
//        fun startReps() {
//            var currentRep = 1
//
//            fun startTimer() {
//                object : CountDownTimer(timeInMillis, 1000) {
//                    override fun onTick(millisUntilFinished: Long) {
//
//                        Log.d("start timer :-", "$millisUntilFinished")
//                        // Update the UI with remaining time
//                    }
//
//                    override fun onFinish() {
//                        if (currentRep < reps) {
//                            currentRep++
//                            startTimer() // Repeat the same timer
//                        } else {
//                            if (currentRound < rounds) {
//                                currentRound++
//                                startPause(pauseInMillis) {
//                                    startTimer() // Start the next round after pause
//                                }
//                            } else {
//                                onCycleComplete() // Move to the next cycle
//                            }
//                        }
//                    }
//                }.start()
//            }
//
//            startTimer() // Start the first rep
//        }
//
//        startReps() // Start the rounds
//    }
//
//    fun startPause(pauseInMillis: Long, onPauseFinish: () -> Unit) {
//        object : CountDownTimer(pauseInMillis, 1000) {
//            override fun onTick(millisUntilFinished: Long) {
//                Log.d("start pause timer :-", "$millisUntilFinished")
//            }
//
//            override fun onFinish() {
//                onPauseFinish() // Start the next round after pause
//            }
//        }.start()
//    }

//    fun timeToMillis(time: String): Long {
//        val parts = time.split(":")
//        val minutes = parts[1].toLongOrNull() ?: 0
//        val seconds = parts[2].toLongOrNull() ?: 0
//        return (minutes * 60 + seconds) * 1000
//    }


    private fun initRecycler() {
        Progress.visibility = View.GONE
        viewTimerDetailsBinding.cycleRecycler.layoutManager = LinearLayoutManager(this)
        adapter =
            CycleTimerAdapter(timerData, this, this)
        viewTimerDetailsBinding.cycleRecycler.adapter = adapter
    }

    private fun initViews() {
        viewTimerDetailsBinding.sectionLiner.visibility = View.GONE
        viewTimerDetailsBinding.goalLiner.visibility = View.GONE
        viewTimerDetailsBinding.equipmentLiner.visibility = View.GONE
        apiClient = APIClient(this)
        Progress = viewTimerDetailsBinding.Progress
        apiInterface = apiClient.client().create(APIInterface::class.java)
        id = intent.getIntExtra("timerId", 0)
        timerData = mutableListOf()
        mediaPlayer = MediaPlayer()
    }

    override fun onItemClicked(view: View, position: Int, type: Long, string: String) {

    }


}