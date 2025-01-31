package com.example.model.newClass.lesson

data class LessonRequest(
    val lesson_id: Int,
    val athlete_ids: List<Int>
)
