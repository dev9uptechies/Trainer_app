package com.example.trainerapp.ApiClass

data class RatingItem(
    val competition_progress_id: Int? = null,
    val name: String? = null,
    var coachRating: Int? = null,
    var athleteRating: Int? = null,
    val isByCoach: Boolean? = null,
    val isByAthlete: Boolean? = null
)