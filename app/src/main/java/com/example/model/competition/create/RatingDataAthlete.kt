package com.example.model.competition.create

import com.google.gson.annotations.SerializedName

class RatingDataAthlete(
    @SerializedName("title") val title: String,
    @SerializedName("athlete_star") val athleteStar: Int,
)