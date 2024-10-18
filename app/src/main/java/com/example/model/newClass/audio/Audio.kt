package com.example.model.newClass.audio

import com.google.gson.annotations.SerializedName

class Audio {
    @SerializedName("status")
    var status: Boolean? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("data")
    var data: List<AudioData>? = null

    class AudioData {
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
}