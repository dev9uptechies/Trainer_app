package com.example.model.newClass.lesson

data class TimerName(
    val audio_id: String,
    val created_at: String,
    val id: Int,
    val name: String,
    val pause_between_time_audio_id: String,
    val pause_time_audio_id: String,
    val timer: List<Timer>,
    val updated_at: String,
    val user_id: Any
)