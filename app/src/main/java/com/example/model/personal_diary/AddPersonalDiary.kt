package com.example.model.personal_diary

data class TrainingSession(
    val date: String? = null,
    val sleep_hours: String? = null,
    val nutrition_and_hydration: String? = null,
    val notes: String,
    val share: Int,
    val data: List<TrainingAssessment> // This is for the request, where data is a list
)

data class TrainingAssessment(
    var assess_your_level_of: String,
    var before_training: String,
    var during_training: String,
    var after_training: String
)
