package com.example.model.newClass.lesson

data class Program(
    val coach_id: String,
    val created_at: String,
    val date: String,
    val deleted_at: Any,
    val goal: GoalX,
    val goal_id: String,
    val id: Int,
    val is_favourite: Int,
    val name: String,
    val program_exercises: List<ProgramExercise>,
    val section: SectionXX,
    val section_id: String,
    val time: String,
    val updated_at: String
)