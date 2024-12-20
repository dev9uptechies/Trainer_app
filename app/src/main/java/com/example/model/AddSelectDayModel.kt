package com.example.model

data class AddSelectDayModel(
    val date: String,
    val group_id: Int,
    val event_ids: List<Int>?,
    val lession_ids: List<Int>?,
    val test_ids: List<Int>?
)
