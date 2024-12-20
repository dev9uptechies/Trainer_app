package com.example.model

data class SelectedDaysModel(
    val status: Boolean,
    val message: String,
    val data: Data
) {
    data class Data(
        val events: List<Event>,
        val lessons: List<Lesson>,
        val programs: List<Any?>,
        val tests: List<Test>
    )

    data class Lesson(
        val id: Int,
        val coach_id: String,
        val name: String,
        val time: String,
        val section_time: String,
        val section_id: String,
        val date: String,
        val created_at: String,
        val updated_at: String,
        val deleted_at: Any?,
        val is_favourite: Int,
        val lesson_programs: List<LessonProgram>
    )

    data class LessonProgram(
        val id: Int,
        val lesson_id: String,
        val program_id: String,
        val created_at: String,
        val updated_at: String,
        val program: Program
    )

    data class Program(
        val id: Int,
        val coach_id: String,
        val name: String,
        val goal_id: String,
        val time: String,
        val section_id: String,
        val date: Any?,
        val created_at: String,
        val updated_at: String,
        val deleted_at: Any?,
        val is_favourite: Int,
        val goal: Goal,
        val section: Section,
        val program_exercises: List<ProgramExercise>
    )

    data class Goal(
        val id: Int,
        val name: String,
        val created_at: Any?,
        val updated_at: Any?,
        val deleted_at: Any?
    )

    data class Section(
        val id: Int,
        val name: String,
        val created_at: Any?,
        val updated_at: Any?,
        val deleted_at: Any?
    )

    data class ProgramExercise(
        val id: Int,
        val program_id: String,
        val exercise_id: String,
        val created_at: String,
        val updated_at: String,
        val exercise: Exercise
    )

    data class Exercise(
        val id: Int,
        val coach_id: String,
        val name: String,
        val image: String,
        val goal_id: String,
        val section_id: String,
        val timer_id: String,
        val type: String,
        val category_id: String,
        val video: String,
        val video_link: Any?,
        val gif: Any?,
        val notes: Any?,
        val date: String,
        val created_at: String,
        val updated_at: String,
        val deleted_at: Any?,
        val is_favourite: Int,
        val goal: Goal,
        val section: Section,
        val cycles: List<Cycle>,
        val exercise_equipments: List<ExerciseEquipment>,
        val category: Category
    )

    data class Cycle(
        val id: Int,
        val exercise_id: String,
        val timer_id: String,
        val set: String,
        val time: String,
        val reps: String,
        val pause: String,
        val weight: String,
        val distance: String,
        val pause_cycle: String,
        val created_at: String,
        val updated_at: String,
        val deleted_at: Any?
    )

    data class ExerciseEquipment(
        val id: Int,
        val exercise_id: String,
        val equipment_id: String,
        val created_at: String,
        val updated_at: String,
        val equipment: Equipment
    )

    data class Equipment(
        val id: Int,
        val coach_id: String,
        val name: String,
        val image: String,
        val created_at: Any?,
        val updated_at: Any?,
        val deleted_at: Any?
    )

    data class Category(
        val id: Int,
        val name: String,
        val created_at: Any?,
        val updated_at: Any?,
        val deleted_at: Any?
    )

    data class Event(
        val id: Int,
        val coach_id: String,
        val title: String,
        val type: String,
        val sport_id: Any?,
        val date: String,
        val created_at: String,
        val updated_at: String,
        val deleted_at: Any?,
        val is_favourite: Int,
        val event_athletes: List<EventAthlete>
    )

    data class EventAthlete(
        val id: Int,
        val event_id: String,
        val athlete_id: String,
        val created_at: String,
        val updated_at: String,
        val athlete: Athlete
    )

    data class Athlete(
        val id: Int,
        val coach_id: Any?,
        val sport_id: Any?,
        val plan_id: Any?,
        val name: String,
        val email: String,
        val email_verified_at: Any?,
        val birthdate: String,
        val address: String,
        val zipcode: String,
        val ref_code: String,
        val ref_user_id: Any?,
        val is_login: String,
        val device_token: Any?,
        val device_type: Any?,
        val image: Any?,
        val plan_expire_date: Any?,
        val below: Any?,
        val athletes: Any?,
        val baseline: Any?,
        val fat_mass: Any?,
        val created_at: String,
        val updated_at: String,
        val deleted_at: Any?
    )

    data class Test(
        val id: Int,
        val coach_id: String,
        val title: String,
        val goal: String,
        val unit: String,
        val date: String,
        val test_repeat: String,
        val created_at: String,
        val updated_at: String,
        val deleted_at: Any?,
        val is_favourite: Int,
        val test_athletes: List<TestAthlete>
    )

    data class TestAthlete(
        val id: Int,
        val test_id: String,
        val athlete_id: String,
        val result: Any?,
        val created_at: String,
        val updated_at: String,
        val athlete: Athlete
    )
}
