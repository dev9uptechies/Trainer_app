package com.example.model.competition.create

data class RatingItemEdit(
    val competition_progress_id: String? = null,
    val name: String? = null,
    var coachRating: Int? = null,
    var athleteRating: Int? = null,
    val isByCoach: Boolean? = null,
    val isByAthlete: Boolean? = null
)