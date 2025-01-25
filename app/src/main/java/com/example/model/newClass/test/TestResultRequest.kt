package com.example.model.newClass.test

import com.google.gson.annotations.SerializedName

data class TestResultRequest(
    @SerializedName("test_id") val testId: Int,
    @SerializedName("athlete_ids") val athleteIds: List<String>,
    @SerializedName("result") val results: List<String?>
)