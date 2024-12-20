package com.example.model.AthleteDataPackage

data class AthltepersonaldiaryModel(
    val data: Datas,
    val message: String,
    val status: Boolean
)

data class Datas(
    val id: Int,
    val user_id: String,
    val date: String,
    val sleep_hours: String,
    val nutrition_and_hydration: String?,
    val notes: String?,
    val share: String,
    val created_at: String,
    val updated_at: String
)
