package com.example.trainerapp.ApiClass

data class EditTimer(
    val audio_id: Int? = null,
    val data: List<Data>? = null,
    val id: Int? = null,
    val name: String? = null,
    val pause_between_time_audio_id: Int? = null,
    val pause_time_audio_id: Int? = null
)