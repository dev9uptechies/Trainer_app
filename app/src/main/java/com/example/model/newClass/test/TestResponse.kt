package com.example.model.newClass.test

import com.google.gson.annotations.SerializedName

data class TestRequest(
    val test_id: String,
    val date: String
)

data class ApiResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: TestData?
)

data class TestData(
    @SerializedName("id") val id: Int,
    @SerializedName("coach_id") val coachId: String,
    @SerializedName("title") val title: String,
    @SerializedName("goal") val goal: String,
    @SerializedName("unit") val unit: String,
    @SerializedName("date") val date: String,
    @SerializedName("test_repeat") val testRepeat: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("deleted_at") val deletedAt: String?,
    @SerializedName("is_favourite") val isFavourite: Int,
    @SerializedName("test_athletes") val testAthletes: List<TestAthlete>
)

data class TestAthlete(
    @SerializedName("id") val id: Int,
    @SerializedName("test_id") val testId: String,
    @SerializedName("athlete_id") val athleteId: String,
    @SerializedName("result") val result: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)
