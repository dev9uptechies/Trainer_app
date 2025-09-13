package com.example.model.newClass.competition

import com.google.gson.annotations.SerializedName



data class GetCompetitionAthlete(
    @SerializedName("athlete_id") val athlete_id: Int,
    @SerializedName("event_id") val eventId: Int,
    @SerializedName("category") val category: String,
    @SerializedName("date") val date: String,
    @SerializedName("area_id") val areaId: Int
)