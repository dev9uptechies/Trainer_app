package com.example.model.newClass.cycle

import com.google.gson.annotations.SerializedName

data class AddTimerBody(
    @SerializedName("user_id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("audio_id") val audioId: Int,
    @SerializedName("pause_time_audio_id") val pauseTimeAudioId: Int,
    @SerializedName("pause_between_time_audio_id") val pauseBetweenTimeAudioId: Int,
    @SerializedName("data") val data: List<AddCycle>
)
