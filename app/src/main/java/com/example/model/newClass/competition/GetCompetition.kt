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