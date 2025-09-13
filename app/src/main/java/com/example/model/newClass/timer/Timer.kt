package com.example.model.newClass.timer

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class Timer : Parcelable {
    @SerializedName("status")
    var status: Boolean? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("data")
    var data: ArrayList<TimerData>? = null

    @Parcelize
    class TimerData : Parcelable {
        @SerializedName("audio")
        val audio: Audio? = null

        @SerializedName("audio_id")
        val audio_id: String? = null

        @SerializedName("created_at")
        val created_at: String? = null

        @SerializedName("id")
        val id: Int? = null

        @SerializedName("name")
        val name: String? = null

        @SerializedName("pause_between_time_audio")
        val pause_between_time_audio: PauseBetweenTimeAudio? = null

        @SerializedName("pause_between_time_audio_id")
        val pause_between_time_audio_id: String? = null

        @SerializedName("pause_time_audio")
        val pause_time_audio: PauseTimeAudio? = null

        @SerializedName("pause_time_audio_id")
        val pause_time_audio_id: String? = null

        @SerializedName("timer")
        val timer: List<TimerX>? = null

        @SerializedName("updated_at")
        val updated_at: String? = null
    }

    @Parcelize
    class Audio : Parcelable {
        @SerializedName("audio")
        val audio: String? = null

        @SerializedName("created_at")
        val created_at: String? = null

        @SerializedName("id")
        val id: Int? = null

        @SerializedName("name")
        val name: String? = null

        @SerializedName("updated_at")
        val updated_at: String? = null
    }

    @Parcelize
    class PauseBetweenTimeAudio : Parcelable {
        @SerializedName("audio")
        val audio: String? = null

        @SerializedName("created_at")
        val created_at: String? = null

        @SerializedName("id")
        val id: Int? = null

        @SerializedName("name")
        val name: String? = null

        @SerializedName("updated_at")
        val updated_at: String? = null
    }

    @Parcelize
    class PauseTimeAudio : Parcelable {
        @SerializedName("audio")
        val audio: String? = null

        @SerializedName("created_at")
        val created_at: String? = null

        @SerializedName("id")
        val id: Int? = null

        @SerializedName("name")
        val name: String? = null

        @SerializedName("updated_at")
        val updated_at: String? = null
    }

    @Parcelize
    class TimerX : Parcelable {
        @SerializedName("audio_id")
        val audio_id: String? = null

        @SerializedName("created_at")
        val created_at: String? = null

        @SerializedName("deleted_at")
        val deleted_at: String? = null

        @SerializedName("distance")
        val distance: String? = null

        @SerializedName("id")
        val id: Int? = null

        @SerializedName("name")
        val name: String? = null

        @SerializedName("pause")
        val pause: String? = null

        @SerializedName("audio")
        val audio: Audio? = null

        @SerializedName("pause_between_time_audio")
        val pause_between_time_audio: PauseBetweenTimeAudio? = null

        @SerializedName("pause_time_audio")
        val pause_time_audio: PauseTimeAudio? = null

        @SerializedName("pause_between_time_audio_id")
        val pause_between_time_audio_id: String? = null

        @SerializedName("pause_time_audio_id")
        val pause_time_audio_id: String? = null

        @SerializedName("pause_timer")
        val pause_timer: String? = null

        @SerializedName("reps")
        val reps: String? = null

        @SerializedName("set")
        val set: String? = null

        @SerializedName("time")
        val time: String? = null

        @SerializedName("timer_name_id")
        val timer_name_id: String? = null

        @SerializedName("updated_at")
        val updated_at: String? = null

        @SerializedName("weight")
        val weight: String? = null
    }
}