package com.example.model.AthleteDataPackage

data class AthleteDatass(
    val status: Boolean,
    val message: String,
    val data: Any // Can accept both List<AthleteData> and AthleteData
)

data class AthleteData(
    val id: Int,
    val athlete_id: String,
    val weight: String?,
    val base_line: String?,
    val fat_data: String?,
    val fat_mass: String?,
    val date: String?,
    val created_at: String?,
    val updated_at: String?
)
