package com.example.model.personal_diary

import com.google.gson.annotations.SerializedName

class GetDiaryDataForEdit {

    @SerializedName("status")
    val status: Boolean? = null

    @SerializedName("message")
    val message: String? = null

    @SerializedName("data")
    val data: Data? = null

    class Data {
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
        val personalDiaryDetails: List<PersonalDiaryDetail>? = null
    }

    class PersonalDiaryDetail {
        @SerializedName("id")
        val id: Int? = null

        @SerializedName("personal_dairie_id")
        val personalDiaryId: String? = null

        @SerializedName("assess_your_level_of")
        val assessYourLevelOf: String? = null

        @SerializedName("before_training")
        val beforeTraining: String? = null

        @SerializedName("during_training")
        val duringTraining: String? = null

        @SerializedName("after_training")
        val afterTraining: String? = null

        @SerializedName("created_at")
        val createdAt: String? = null

        @SerializedName("updated_at")
        val updatedAt: String? = null
    }
}
