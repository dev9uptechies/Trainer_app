package com.example.model.competition.create

import com.google.gson.annotations.SerializedName

data class AddCompetitionBodyAthlete(
    @SerializedName("athlete_id") val athleteId: Int,
    @SerializedName("event_id") val eventId: Int,
    @SerializedName("category") val categoryName: String,
    @SerializedName("date") val date: String,
    @SerializedName("area_id") val areaId: Int,
    @SerializedName("data") val data: List<RatingDataAthlete>
)
