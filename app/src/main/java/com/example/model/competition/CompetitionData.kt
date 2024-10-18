package com.example.model.competition

import com.google.gson.annotations.SerializedName

class CompetitionData {
    @SerializedName("data")
    val data: List<CompetitionAreaData>? = null

    @SerializedName("message")
    val message: String? = null

    @SerializedName("status")
    val status: Boolean? = null

    class CompetitionAreaData {
        @SerializedName("created_at")
        val created_at: String? = null

        @SerializedName("id")
        val id: Int? = null

        @SerializedName("title")
        val title: String? = null

        @SerializedName("updated_at")
        val updated_at: String? = null
    }
}