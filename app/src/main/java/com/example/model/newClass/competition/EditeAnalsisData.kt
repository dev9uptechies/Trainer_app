package com.example.model.newClass.competition

data class EditeAnalsisData(
    val data: DataX,
    val message: String,
    val status: Boolean
){
    class DataX(
        val athlete_star: Int,
        val coach_star: Int,
        val competition_analysis_id: Int,
        val created_at: String,
        val id: Int,
        val title: String,
        val updated_at: String
    )
}