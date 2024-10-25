package com.example.model.personal_diary


data class TrainingSession(
    val date: String,
    val sleepHours: String,
    val nutritionAndHydration: String,
    val notes: String,
    val share: Int,
    val data: List<TrainingAssessment>
)


data class TrainingAssessment(
    var assess_your_level_of: String,
    var before_training: Int,
    var during_training: Int,
    var after_training: Int
)

