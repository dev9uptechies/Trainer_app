package com.example.model.newClass.competition

data class AddCompetition(
    val area_id: Int? = null,
    val athlete_id: Int? = null,
    val category: String? = null,
    val data: List<Data>? = null,
    val date: String? = null,
    val event_id: Int? = null
)