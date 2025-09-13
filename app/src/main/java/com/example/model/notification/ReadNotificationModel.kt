package com.example.model.notification

data class ReadNotificationModel(
    val data: NotificationData?, // Change from List<NotificationData>? to a single NotificationData?
    val message: String,
    val status: Boolean
)

data class NotificationData(
    val id: Int,
    val event_id: Int?,
    val lesson_id: Int?,
    val exercise_id: Int?,
    val program_id: Int?,
    val test_id: Int?,
    val sender_id: Int,
    val title: String,
    val message: String,
    val status: Int,
    val created_at: String,
    val updated_at: String,
    val program: ProgramData?, // New nested object
    val lesson: Any?, // Keeping it generic since it's null in response
    val event: Any?, // Keeping it generic
    val exercise: Any?, // Keeping it generic
)

data class ProgramData(
    val id: Int,
    val coach_id: String,
    val name: String,
    val goal_id: String,
    val time: String,
    val section_id: String,
    val date: String?,
    val created_at: String,
    val updated_at: String,
    val deleted_at: String?,
    val is_favourite: Int,
    val goal: GoalData?,
    val section: SectionData?,
    val program_exercises: List<ProgramExerciseData>?
)

data class GoalData(
    val id: Int,
    val name: String,
    val created_at: String?,
    val updated_at: String?,
    val deleted_at: String?
)

data class SectionData(
    val id: Int,
    val name: String,
    val created_at: String?,
    val updated_at: String?,
    val deleted_at: String?
)

data class ProgramExerciseData(
    val id: Int,
    val program_id: String,
    val exercise_id: String,
    val created_at: String,
    val updated_at: String
)
