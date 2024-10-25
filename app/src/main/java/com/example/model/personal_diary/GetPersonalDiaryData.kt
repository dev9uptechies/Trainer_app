package com.example.model.personal_diary

import com.google.gson.annotations.SerializedName

class GetPersonalDiaryData {

    @SerializedName("status")
    val status: Boolean? = null

    @SerializedName("message")
    val message: String? = null

    @SerializedName("data")
    val data: PersonalDiaryData? = null // `data` is an object, not a list

    // Inner class to represent the `data` object
    class PersonalDiaryData {

        @SerializedName("id")
        val id: Int? = null

        @SerializedName("user_id")
        val userId: String? = null

        @SerializedName("date")
        val date: String? = null

        @SerializedName("sleep_hours")
        val sleepHours: String? = null

        @SerializedName("nutrition_and_hydration")
        val nutritionAndHydration: String? = null

        @SerializedName("notes")
        val notes: String? = null

        @SerializedName("share")
        val share: String? = null

        @SerializedName("created_at")
        val createdAt: String? = null

        @SerializedName("updated_at")
        val updatedAt: String? = null

        @SerializedName("personal_dairie_detaile")
        val personalDairieDetails: List<TrainingAssessment>? = null // This is the list of assessments
    }

    // Inner class to represent the training assessment data
    class TrainingAssessment {

        @SerializedName("id")
        var id: Int? = null

        @SerializedName("personal_dairie_id")
        var personalDairieId: String? = null

        @SerializedName("assess_your_level_of")
        var assessYourLevelOf: String? = null

        @SerializedName("before_training")
        var beforeTraining: String? = null

        @SerializedName("during_training")
        var duringTraining: String? = null

        @SerializedName("after_training")
        var afterTraining: String? = null

        @SerializedName("created_at")
        var createdAt: String? = null

        @SerializedName("updated_at")
        var updatedAt: String? = null
    }
}
