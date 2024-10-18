package com.example.model.newClass.lesson

data class Exercise(
    val category: Category,
    val category_id: String,
    val coach_id: String,
    val created_at: String,
    val cycles: List<Cycle>,
    val date: String,
    val deleted_at: Any,
    val exercise_equipments: List<ExerciseEquipment>,
    val gif: Any,
    val goal: GoalX,
    val goal_id: String,
    val id: Int,
    val image: String,
    val is_favourite: Int,
    val name: String,
    val notes: String,
    val section: SectionXX,
    val section_id: String,
    val timer_id: String,
    val timer_name: TimerName,
    val type: String,
    val updated_at: String,
    val video: String,
    val video_link: String
)