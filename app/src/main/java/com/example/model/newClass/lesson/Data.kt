package com.example.model.newClass.lesson

data class Data(
    val coach_id: String,
    val created_at: String,
    val date: String,
    val deleted_at: Any,
    val id: Int,
    val is_favourite: Int,
    val lesson_goal: List<LessonGoal>,
    val lesson_programs: List<LessonProgram>,
    val name: String,
    val section: SectionXX,
    val section_id: String,
    val section_time: String,
    val time: String,
    val updated_at: String
)