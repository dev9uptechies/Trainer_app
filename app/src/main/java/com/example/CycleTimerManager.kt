package com.example

import android.graphics.Color
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.model.newClass.excercise.Exercise.Cycle
import com.example.model.newClass.timer.Timer
import com.example.model.newClass.timer.Timer.TimerX
import com.example.trainerapp.R
import com.example.trainerapp.databinding.ActivityViewTimerBinding

class CycleTimerManager(
    private val viewTimerDetailsBinding: ActivityViewTimerBinding
) {

    enum class TimerState { IDLE, RUNNING, PAUSED }
    private var finalPausePlayed = false // ✅ new flag

    private var cycles: List<Timer.TimerX> = listOf()
    private var currentCycleIndex = 0
    private var currentRoundIndex = 0
    private var countDownTimer: CountDownTimer? = null
    private var remainingTime: Long = 0L
    private var isPauseBetweenRounds = false
    private var isPauseBetweenCycles = false
    private var isCurrentTimerRed = false

    private var timerState = TimerState.IDLE

    fun loadCycles(data: List<Timer.TimerX>) {
        cycles = data
            Log.d("CycleTimer", "Loaded ${cycles.size} cycles -- ${cycles.map { it.pause_between_time_audio?.audio }}")

        if (cycles.isNotEmpty()) {

            val lastCycle = cycles.last()
            currentRoundIndex = getRoundsCount(lastCycle.set) - 1

            updateCycleUI(lastCycle)
        }

    }

    private fun playAudioFromUrl(url: String) {
        try {
            mediaPlayer?.release()

            mediaPlayer = MediaPlayer().apply {
                setDataSource(url)
                prepareAsync()
                setOnPreparedListener { start() }
                setOnCompletionListener {
                    release()
                    mediaPlayer = null
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private var mediaPlayer: MediaPlayer? = null

    fun resetToDefault() {
        // Stop and clear timer
        countDownTimer?.cancel()
        timerState = TimerState.IDLE
        remainingTime = 0L
        isPauseBetweenRounds = false
        isPauseBetweenCycles = false
        isCurrentTimerRed = false
//        finalPausePlayed = false

        // Reset indexes to last cycle & show last round
        if (cycles.isNotEmpty()) {
            currentCycleIndex = cycles.size - 1
            currentRoundIndex = getRoundsCount(cycles.last().set) - 1
            updateCycleUI(cycles.last())
        }

        // Reset timer display
        viewTimerDetailsBinding.time.text = "00:00"
        viewTimerDetailsBinding.time.setTextColor(Color.WHITE)

        // Reset play button icon
        setPlayIcon()

        Log.d("RESET", "Timer reset to default state")
    }


    fun play() {
        Log.d("JDJDJDJ", "play: $isCurrentTimerRed")

        // Set text color for the timer
        viewTimerDetailsBinding.time.setTextColor(
            if (isCurrentTimerRed) Color.RED else Color.WHITE
        )

        // 🔊 Play audio for the current cycle
        val currentCycle = cycles.getOrNull(currentCycleIndex)
        val audioUrl = "https://uat.4trainersapp.com/${currentCycle?.audio?.audio}"

        if (!audioUrl.isNullOrEmpty()) {
            try {
                mediaPlayer?.release()
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(audioUrl)
                    setAudioStreamType(AudioManager.STREAM_MUSIC)
                    prepareAsync()
                    setOnPreparedListener { start() }
                    setOnErrorListener { _, what, extra ->
                        Log.e("AUDIO_ERROR", "Error playing audio: $what, $extra")
                        true
                    }
                }
                Log.d("AUDIO", "Playing audio for cycle ${currentCycleIndex + 1}: $audioUrl")
            } catch (e: Exception) {
                Log.e("AUDIO_ERROR", "Exception: ${e.message}", e)
            }
        } else {
            Log.w("AUDIO", "No audio URL for cycle ${currentCycleIndex + 1}")
        }

        // Timer logic
        when (timerState) {
            TimerState.PAUSED -> {
                if (remainingTime > 0L) {
                    startTimer(remainingTime, false)
                } else {
                    startFromLastCycle()
                }
            }
            TimerState.IDLE -> startFromLastCycle()
            else -> {}
        }
    }

    fun pause() {
        countDownTimer?.cancel()
        timerState = TimerState.PAUSED
        setPlayIcon()

        val currentCycle = cycles.getOrNull(currentCycleIndex)
        val audioUrl = "https://uat.4trainersapp.com/${currentCycle?.audio?.audio}"

        if (!audioUrl.isNullOrEmpty()) {
            try {
                mediaPlayer?.release() // release previous player
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(audioUrl)
                    setAudioStreamType(AudioManager.STREAM_MUSIC)
                    prepareAsync()
                    setOnPreparedListener { start() }
                    setOnErrorListener { _, what, extra ->
                        Log.e("AUDIO_ERROR", "Error playing audio: $what, $extra")
                        true
                    }
                }
                Log.d("AUDIO", "Playing audio for cycle ${currentCycleIndex + 1}: $audioUrl")
            } catch (e: Exception) {
                Log.e("AUDIO_ERROR", "Exception: ${e.message}", e)
            }
        } else {
            Log.w("AUDIO", "No audio URL for cycle ${currentCycleIndex + 1}")
        }
    }

    fun stop() {
        countDownTimer?.cancel()
        timerState = TimerState.IDLE
        currentCycleIndex = 0
        currentRoundIndex = 0
        remainingTime = 0
        viewTimerDetailsBinding.time.text = "00:00"
    }

    fun reset() {
        stop()
        startFromLastCycle()
        isPauseBetweenRounds = false
        isPauseBetweenCycles = false
        finalPausePlayed = false
    }

    private fun startFromLastCycle() {
        currentCycleIndex = cycles.size - 1
        currentRoundIndex = 0 // ✅ Start at first index but display will show highest round number

        isPauseBetweenRounds = false
        isPauseBetweenCycles = false

        Log.d("StartCycle", "Starting from Cycle ${currentCycleIndex + 1}, Round ${getRoundsCount(cycles[currentCycleIndex].set)}")
        startNextRound()
    }

    fun getRoundsCount(setValue: String?): Int {
        return setValue?.filter { it.isDigit() }?.toIntOrNull() ?: 1
    }

    private fun handleTimerFinish() {

        if (finalPausePlayed) {
            resetToDefault()
            finalPausePlayed = false
            return
        }

            val currentCycle = cycles.getOrNull(currentCycleIndex) ?: return
        val totalRoundsInCycle = getRoundsCount(currentCycle.set)

        // ✅ Pause between rounds finished → go to next round
        if (isPauseBetweenRounds) {
            isPauseBetweenRounds = false
            isCurrentTimerRed = false
            startNextRound()
            return
        }

        // ✅ Pause between cycles finished → go to next cycle
        if (isPauseBetweenCycles) {
            isPauseBetweenCycles = false
            isCurrentTimerRed = false
            startNextRound()
            return
        }

        // ✅ Finished one round but more left
        if (currentRoundIndex < totalRoundsInCycle - 1) {
            currentRoundIndex++

            // Pause between rounds (white, not red)
            isPauseBetweenRounds = true
            isCurrentTimerRed = false
            viewTimerDetailsBinding.time.setTextColor(Color.WHITE)
            startPauseTimer(currentCycle.pause, isRed = true)

            // Play round pause audio if available
            currentCycle.pause_time_audio?.audio?.let { audioUrl ->
                if (audioUrl.isNotBlank()) {
                    playAudioFromUrl("https://uat.4trainersapp.com/$audioUrl")
                }
            }

        } else {
            // ✅ All rounds in this cycle are done
            if (currentCycleIndex > 0) {
                // Use the just-finished cycle's pause before moving to next
                isPauseBetweenCycles = true
                isCurrentTimerRed = true
                viewTimerDetailsBinding.time.setTextColor(Color.RED)

                startPauseTimer(currentCycle.pause_timer, isRed = true) {
                    // After pause ends → move to next cycle
                    isCurrentTimerRed = false
                    viewTimerDetailsBinding.time.setTextColor(Color.WHITE)

                    currentCycleIndex--   // now go to the next cycle
                    val newCycle = cycles[currentCycleIndex]
                    currentRoundIndex = 0
                    updateCycleUI(newCycle)
                    startNextRound()
                }

                // Play pause-between-cycle audio if available (from finished cycle)
                currentCycle.pause_between_time_audio?.audio?.let { audioUrl ->
                    if (audioUrl.isNotBlank()) {
                        playAudioFromUrl("https://uat.4trainersapp.com/$audioUrl")
                    }
                }

            } else {
                // ✅ LAST CYCLE → play its pause_timer ONCE, then stop
                if (!finalPausePlayed) {
                    finalPausePlayed = true // prevent replay
                    isPauseBetweenCycles = true
                    isCurrentTimerRed = true
                    viewTimerDetailsBinding.time.setTextColor(Color.RED)

                    startPauseTimer(currentCycle.pause_timer, isRed = true) {
                        isCurrentTimerRed = false
                        viewTimerDetailsBinding.time.setTextColor(Color.WHITE)
                        handleAllCyclesComplete()
                    }

                    currentCycle.pause_time_audio?.audio?.let { audioUrl ->
                        if (audioUrl.isNotBlank()) playAudioFromUrl("https://uat.4trainersapp.com/$audioUrl")
                    }
                }
            }
        }
    }

    private fun startNextRound() {
        val currentCycle = cycles.getOrNull(currentCycleIndex) ?: return

        val totalRoundsInCycle = getRoundsCount(currentCycle.set)
        val roundNumber = totalRoundsInCycle - currentRoundIndex
        val cycleNumber = currentCycleIndex + 1

        Log.d(
            "TimerFlow",
            "▶️ Cycle $cycleNumber - Round $roundNumber/$totalRoundsInCycle | pauseRound=$isPauseBetweenRounds | pauseCycle=$isPauseBetweenCycles"
        )

        val durationSeconds = when {
            isPauseBetweenRounds -> parseTimeToSeconds(currentCycle.pause)
            isPauseBetweenCycles -> parseTimeToSeconds(currentCycle.pause_timer)
            else -> parseTimeToSeconds(currentCycle.time)
        }


        if (durationSeconds <= 0) {
            handleTimerFinish()
            return
        }

        viewTimerDetailsBinding.tvCycle.text = "$cycleNumber"
        viewTimerDetailsBinding.tvRound.text = "$roundNumber Round"

        val isPause = isPauseBetweenRounds || isPauseBetweenCycles
        startTimer(durationSeconds * 1000, isPause)
    }

    private fun updateCycleUI(cycle: TimerX) {
        val totalCycles = cycles.size
        val cycleNumber = totalCycles - currentCycleIndex
        viewTimerDetailsBinding.tvCycle.text = "Cycle: $cycleNumber"

        // Show the current round number
        val totalRounds = getRoundsCount(cycle.set)
        val roundNumber = currentRoundIndex + 1
        viewTimerDetailsBinding.tvRound.text = "$roundNumber Round"

        // Other data
        viewTimerDetailsBinding.tvTimer.text = "${cycle.time ?: 0}"
        viewTimerDetailsBinding.tvPause.text = "${cycle.pause ?: 0}"
        viewTimerDetailsBinding.tvWeight.text = "${cycle.weight ?: 0}"
        viewTimerDetailsBinding.tvDistance.text = "${cycle.reps ?: 0}"
        viewTimerDetailsBinding.tvPauseBetween.text = "${cycle.pause_timer ?: 0}"
    }

    private fun startTimer(duration: Long, isPause: Boolean,    onFinish: (() -> Unit)? = null) {
        countDownTimer?.cancel()
        timerState = TimerState.RUNNING
        remainingTime = duration


        updateTimeDisplay((duration / 1000).toInt(), isPause)

        countDownTimer = object : CountDownTimer(duration + 50, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTime = millisUntilFinished
                updateTimeDisplay((millisUntilFinished / 1000).toInt(), isPause)
            }

            override fun onFinish() {
                remainingTime = 0L
                onFinish?.invoke()
                handleTimerFinish()
            }
        }.start()


        setPauseIcon()
    }

    private fun updateTimeDisplay(seconds: Int, isPause: Boolean) {
        val timeText = String.format("%02d:%02d", seconds / 60, seconds % 60)
        viewTimerDetailsBinding.time.text = timeText

        // ✅ Use isCurrentTimerRed to decide the color

        Log.d("FDIII", "updateTimeDisplay: $isCurrentTimerRed")
        viewTimerDetailsBinding.time.setTextColor(
            if (isCurrentTimerRed) Color.RED else Color.WHITE
        )
    }

//    private fun handleTimerFinish() {
//        val currentCycle = cycles.getOrNull(currentCycleIndex) ?: return
//
//        if (isPauseBetweenRounds) {
//            isPauseBetweenRounds = false
//            startNextRound()
//            return
//        }
//
//        if (isPauseBetweenCycles) {
//            isPauseBetweenCycles = false
//            startNextRound()
//            return
//        }
//
//        // Finished a round in the current cycle
//        if (currentRoundIndex > 0) {
//            // More rounds left → pause before next round
//            currentRoundIndex--
//            isPauseBetweenRounds = true
//            startPauseTimer(currentCycle.pause)
//        } else {
//            // All rounds in this cycle done → move to next cycle
//            if (currentCycleIndex > 0) {
//                currentCycleIndex--
//                val newCycle = cycles[currentCycleIndex]
//                currentRoundIndex = (newCycle.set?.toIntOrNull() ?: 1) - 1
//                isPauseBetweenCycles = true
//                startPauseTimer(newCycle.pause_timer)
//            } else {
//                handleAllCyclesComplete()
//            }
//        }
//    }


    private fun startPauseTimer(timeString: String?, isRed: Boolean = false, onFinish: (() -> Unit)? = null) {
        val seconds = parseTimeToSeconds(timeString)

        if (seconds <= 0) {
            handleTimerFinish()
            return
        }

        isCurrentTimerRed = isRed // ✅ Save current state

        if (isRed) {
            viewTimerDetailsBinding.time.setTextColor(Color.RED)
        } else {
            viewTimerDetailsBinding.time.setTextColor(Color.WHITE)
        }

        startTimer(seconds * 1000, isPause = true, onFinish = onFinish)
    }




    private fun handleAllCyclesComplete() {
        timerState = TimerState.IDLE

        Log.d("complete", "handleAllCyclesComplete: complete")
        Toast.makeText(
            viewTimerDetailsBinding.root.context,
            "All cycles complete",
            Toast.LENGTH_SHORT
        ).show()
//        setPlayIcon()
//        reset()
        resetToDefault()
    }

    private fun setPlayIcon() {
        viewTimerDetailsBinding.play.setImageDrawable(
            ResourcesCompat.getDrawable(
                viewTimerDetailsBinding.root.resources,
                R.drawable.ic_play_1,
                null
            )
        )
    }

    private fun setPauseIcon() {
        viewTimerDetailsBinding.play.setImageDrawable(
            ResourcesCompat.getDrawable(
                viewTimerDetailsBinding.root.resources,
                R.drawable.ic_pause_1,
                null
            )
        )
    }

    fun isPaused(): Boolean = timerState == TimerState.PAUSED
    fun isRunning(): Boolean = timerState == TimerState.RUNNING

}


private fun parseTimeToSeconds(timeStr: String?): Long {
    if (timeStr.isNullOrEmpty()) return 0L

    // Handle "00 min, 03 sec" style
    if (timeStr.contains("min") || timeStr.contains("sec")) {
        val minutes = Regex("(\\d+)\\s*min").find(timeStr)?.groupValues?.get(1)?.toLongOrNull() ?: 0L
        val seconds = Regex("(\\d+)\\s*sec").find(timeStr)?.groupValues?.get(1)?.toLongOrNull() ?: 0L
        return minutes * 60 + seconds
    }

    // Handle hh:mm:ss, mm:ss
    val parts = timeStr.split(":").mapNotNull { it.toLongOrNull() }
    return when (parts.size) {
        3 -> parts[0] * 3600 + parts[1] * 60 + parts[2]
        2 -> parts[0] * 60 + parts[1]
        1 -> parts[0]
        else -> 0L
    }
}
