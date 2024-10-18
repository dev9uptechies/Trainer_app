package com.example.model.newClass.lesson

data class LessonProgram(
    val created_at: String,
    val id: Int,
    val lesson_id: String,
    val program: Program,
    val program_id: String,
    val updated_at: String
)