package com.example.model

data class SelectedEventModel(
    val status: Boolean,
    val message: String,
    val data: Data
) {
    data class Data(
        val count: List<Count>,
        val list: ListData
    )

    data class Count(
        val lessons: Int,
        val events: Int,
        val programs: Int,
        val tests: Int,
        val date: String
    )

    data class ListData(
        val lessons: List<Lesson>,
        val events: List<Event>,
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
        val lesson_programs: List<lessonPrograms>
    )

    data class lessonPrograms(
        val id: Int,
        val lesson_id: String,
        val program_id: String,
        val created_at: String,
        val updated_at: String,
        val program: program
    )

    data class program(
        val id: Int,
        val coach_id: String,
        val name: String,
        val goal_id: String,
        val time: String,
        val section_id: String,
        val date: String,
        val test_repeat: String,
        val created_at: String,
        val updated_at: String,
        val deleted_at: Any?,
        val is_favourite: Int,
        val goal: goal
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
        var is_favourite: Int,
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

    data class goal(
        val id: Int,
        val name: String,
        val athlete_id: String,
        val result: Any?,
        val created_at: String,
        val deleted_at: Any?
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
        val purchase_date: Any?,
        val plan_expire_date: Any?,
        val is_active: Int,
        val is_renewal: Int,
        val below: Any?,
        val athletes: String?,
        val baseline: Any?,
        val fat_mass: Any?,
        val language: String,
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
