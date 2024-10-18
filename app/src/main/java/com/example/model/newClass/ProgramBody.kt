package com.example.model.newClass

import com.google.gson.annotations.SerializedName

data class ProgramBody(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("goal_id") val goal_id: Int,
    @SerializedName("time") val time: String,
    @SerializedName("section_id") val section_id: Int,
    @SerializedName("exercise_ids") val data: List<Int>
)
