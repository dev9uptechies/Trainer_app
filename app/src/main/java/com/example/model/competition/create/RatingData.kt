package com.example.model.competition.create

import com.google.gson.annotations.SerializedName

class RatingData(
    @SerializedName("title") val title: String,
    @SerializedName("coach_star") val coachStar: Int,
)