package com.example.model.competition.create

import com.google.gson.annotations.SerializedName

data class RatingDataEdite(
    @SerializedName("competition_progress_id") val competitionProgressId: Int,
    @SerializedName("coach_star") val coachStar: Int
)