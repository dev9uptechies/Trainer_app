package com.example.model.newClass.competition

import com.google.gson.annotations.SerializedName

class GetCompetition {

    @SerializedName("message")
    val message: String? = null

    @SerializedName("status")
    val status: Boolean? = null

    @SerializedName("data")
    val data: List<Competition.CompetitionData>? = null
}

class GetCompetitionAll {

    @SerializedName("message")
    val message: String? = null

    @SerializedName("status")
    val status: Boolean? = null

    @SerializedName("data")
    val data: Competition.CompetitionData? = null
}

data class GetCompetitionRequest(
    @SerializedName("event_id") val eventId: Int,
    @SerializedName("category") val category: String,
    @SerializedName("date") val date: String,
    @SerializedName("area_id") val areaId: Int
)