package com.example.model.newClass.lesson

data class Timer(
    val audio: Audio,
    val audio_id: String,
    val created_at: String,
    val deleted_at: Any,
    val distance: String,
    val id: Int,
    val name: Any,
    val pause: String,
    val pause_between_time_audio: PauseBetweenTimeAudio,
    val pause_between_time_audio_id: String,
    val pause_time_audio: PauseTimeAudio,
    val pause_time_audio_id: String,
    val pause_timer: String,
    val reps: String,
    val `set`: String,
    val time: String,
    val timer_name_id: String,
    val updated_at: String,
    val weight: String
)