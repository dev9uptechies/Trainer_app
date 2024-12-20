package com.example.model.notification

data class NotificationModel(
    val data: List<Data>?= null,
    val message: String,
    val status: Boolean
){
    data class Data(
        val created_at: String,
        val event_id: Int,
        val exercise_id: Int,
        val id: Int,
        val lesson_id: Int,
        val message: String,
        val program_id: Int,
        val sender_id: Int,
        val status: Int,
        val test_id: Int,
        val title: String,
        val updated_at: String
    )
}