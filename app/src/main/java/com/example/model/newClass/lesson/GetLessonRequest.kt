package com.example.model.newClass.lesson

data class GetLessonRequest(
    val data: List<Data>,
    val message: String,
    val status: Boolean
){
    class Data(
        val athlete_id: String,
        val created_at: String,
        val id: Int,
        val lesson_id: String,
        val updated_at: String
    )
}

