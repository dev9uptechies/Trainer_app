package com.example

import com.example.trainerapp.ApiClass.ProgramListData
import com.google.gson.annotations.SerializedName

class LessonData {
    @SerializedName("status")
    var status: Boolean? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("data")
    var data: ArrayList<lessionData>? = null

    class lessionData {

        @SerializedName("id")
        var id: Int? = null

        @SerializedName("coach_id")
        var coach_id: String? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("lesson_id")
        var lesson_id: String? = null

        @SerializedName("user_id")
        var user_id: String? = null


        @SerializedName("goal_id")
        var goal_id: Int? = null

        @SerializedName("time")
        var time: String? = null

        @SerializedName("section_time")
        var section_time: String? = null

        @SerializedName("section_id")
        var section_id: String? = null

        @SerializedName("date")
        var date: String? = null

        @SerializedName("is_favourite")
        var is_favourite: String? = null

        @SerializedName("updated_at")
        var updated_at: String? = null

        @SerializedName("lesson_goal")
        var goal: ArrayList<Goals>? = null

        @SerializedName("lesson_programs")
        var lesson_programs: ArrayList<Lesson_Programs>? = null

        @SerializedName("section")
        var section: Section? = null

        @SerializedName("lesson")
        var lesson: Lesson? = null

        @SerializedName("event")
        var event: Event? = null

        @SerializedName("test")
        var test: Test? = null

        @SerializedName("exercise")
        var exercise: Lesson? = null

        @SerializedName("program")
        var program: ProgramListData.testData? = null
    }

    class Event {
        @SerializedName("id")
         val id: Int? = null

        @SerializedName("coach_id")
         val coachId: String? = null

        @SerializedName("title")
         val title: String? = null

        @SerializedName("type")
         val type: String? = null

        @SerializedName("sport_id")
         val sportId: String? = null

        @SerializedName("date")
         val date: String? = null

        @SerializedName("created_at")
         val createdAt: String? = null

        @SerializedName("updated_at")
         val updatedAt: String? = null

        @SerializedName("deleted_at")
         val deletedAt: String? = null

        @SerializedName("is_favourite")
         val isFavourite: Int? = null

        @SerializedName("event_athletes")
         val eventAthletes: List<EventAthlete>? = null
    }

    class EventAthlete {
        @SerializedName("id")
         val id: Int? = null

        @SerializedName("event_id")
         val eventId: String? = null

        @SerializedName("athlete_id")
         val athleteId: String? = null

        @SerializedName("created_at")
         val createdAt: String? = null

        @SerializedName("updated_at")
         val updatedAt: String? = null

        @SerializedName("athlete")
         val athlete: Athlete? = null
    }

    class Lesson_Programs {

        @SerializedName("id")
        var id: Int? = null

        @SerializedName("program_id")
        var program_id: String? = null

        @SerializedName("lesson_id")
        var lesson_id: String? = null

        @SerializedName("created_at")
        var created_at: String? = null

        @SerializedName("updated_at")
        var updated_at: String? = null

        @SerializedName("program")
        var program: ProgramListData.testData? = null

    }

    class Test(
        @SerializedName("id")
        val id: Int?,

        @SerializedName("coach_id")
        val coachId: String?,

        @SerializedName("title")
        val title: String?,

        @SerializedName("goal")
        val goal: String?,

        @SerializedName("unit")
        val unit: String?,

        @SerializedName("date")
        val date: String?,

        @SerializedName("test_repeat")
        val testRepeat: String?,

        @SerializedName("created_at")
        val createdAt: String?,

        @SerializedName("updated_at")
        val updatedAt: String?,

        @SerializedName("deleted_at")
        val deletedAt: String?,

        @SerializedName("is_favourite")
        val isFavourite: Int?,

        @SerializedName("test_athletes")
        val testAthletes: List<TestAthlete>?
    )

    class TestAthlete{

        @SerializedName("id")
        val id: Int? = null

        @SerializedName("test_id")
        val testId: String? = null

        @SerializedName("athlete_id")
        val athleteId: String? = null

        @SerializedName("result")
        val result: Any? = null

        @SerializedName("created_at")
        val createdAt: String? = null

        @SerializedName("updated_at")
        val updatedAt: String? = null

        @SerializedName("athlete")
        val athlete: Athlete? = null
    }

    class Lesson {

        @SerializedName("id")
        var id: Int? = null;

        @SerializedName("coach_id")
        var coach_id: String? = null;

        @SerializedName("name")
        var name: String? = null;

        @SerializedName("time")
        var time: String? = null;

        @SerializedName("section_time")
        var section_time: String? = null;

        @SerializedName("section_id")
        var section_id: String? = null;

        @SerializedName("date")
        var date: String? = null;

        @SerializedName("created_at")
        var created_at: String? = null;

        @SerializedName("updated_at")
        var updated_at: String? = null;

        @SerializedName("deleted_at")
        var deleted_at: String? = null;

        @SerializedName("is_favourite")
        var is_favourite: Int? = null;

        @SerializedName("goal")
        var goal: Goals? = null;

        @SerializedName("section")
        var section: Section? = null;

        @SerializedName("lesson_programs")
        var lessonPrograms: List<LessonProgram>? = null;
    }

    class Goals {
        @SerializedName("id")
        var id: Int? = null;

        @SerializedName("name")
        var name: String? = null;

        @SerializedName("created_at")
        var created_at: String? = null;

        @SerializedName("updated_at")
        var updated_at: String? = null;

        @SerializedName("deleted_at")
        var deleted_at: String? = null;
    }

    class Section {
        @SerializedName("id")
        var id: Int? = null;

        @SerializedName("name")
        var name: String? = null;
    }

    class LessonProgram {
        @SerializedName("id")
        var id: Int? = null;

        @SerializedName("lesson_id")
        var lesson_id: String? = null;

        @SerializedName("program_id")
        var program_id: String? = null;

        @SerializedName("created_at")
        var created_at: String? = null;

        @SerializedName("updated_at")
        var updated_at: String? = null;

        @SerializedName("program")
        var program: Program? = null;
    }

    class Program {
        @SerializedName("id")
        var id: Int? = null;

        @SerializedName("coach_id")
        var coach_id: String? = null;

        @SerializedName("name")
        var name: String? = null;

        @SerializedName("goal_id")
        var goal_id: String? = null;

        @SerializedName("time")
        var time: String? = null;

        @SerializedName("section_id")
        var section_id: String? = null;

        @SerializedName("date")
        var date: String? = null;

        @SerializedName("created_at")
        var created_at: String? = null;

        @SerializedName("updated_at")
        var updated_at: String? = null;

        @SerializedName("deleted_at")
        var deleted_at: String? = null;

        @SerializedName("is_favourite")
        var is_favourite: Int? = null;

        @SerializedName("goal")
        var goal: Goals? = null;

        @SerializedName("section")
        var section: Section? = null;

        @SerializedName("program_exercises")
        var programExercises: List<ProgramExercise>? = null;
    }

    class ProgramExercise {
        @SerializedName("id")
        var id: Int? = null;

        @SerializedName("program_id")
        var program_id: String? = null;

        @SerializedName("exercise_id")
        var exercise_id: String? = null;

        @SerializedName("created_at")
        var created_at: String? = null;

        @SerializedName("updated_at")
        var updated_at: String? = null;

        @SerializedName("exercise")
        var exercise: Exercise? = null;
    }

    class Exercise {
        @SerializedName("id")
        var id: Int? = null;

        @SerializedName("coach_id")
        var coach_id: String? = null;

        @SerializedName("name")
        var name: String? = null;

        @SerializedName("image")
        var image: String? = null;

        @SerializedName("goal_id")
        var goal_id: String? = null;

        @SerializedName("section_id")
        var section_id: String? = null;

        @SerializedName("type")
        var type: String? = null;

        @SerializedName("category_id")
        var category_id: String? = null;

        @SerializedName("created_at")
        var created_at: String? = null;

        @SerializedName("updated_at")
        var updated_at: String? = null;

        @SerializedName("deleted_at")
        var deleted_at: String? = null;

        @SerializedName("goal")
        var goal: Goals? = null;

        @SerializedName("section")
        var section: Section? = null;

        @SerializedName("cycles")
        var cycles: List<Cycle>? = null;

        @SerializedName("category")
        var category: Category? = null;
    }

    class Cycle {
        @SerializedName("id")
        var id: Int? = null;

        @SerializedName("exercise_id")
        var exercise_id: String? = null;

        @SerializedName("set")
        var set: String? = null;

        @SerializedName("time")
        var time: String? = null;

        @SerializedName("reps")
        var reps: String? = null;

        @SerializedName("pause")
        var pause: String? = null;

        @SerializedName("weight")
        var weight: String? = null;

        @SerializedName("distance")
        var distance: String? = null;
    }

    class Category {
        @SerializedName("id")
        var id: Int? = null;

        @SerializedName("name")
        var name: String? = null;
    }


    class Athlete {

        @SerializedName("id")
        var id: Int? = null

        @SerializedName("coach_id")
        var coach_id: String? = null

        @SerializedName("sport_id")
        var sport_id: String? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("email")
        var email: String? = null

        @SerializedName("birthdate")
        var birthdate: String? = null

        @SerializedName("created_at")
        var created_at: String? = null

        @SerializedName("updated_at")
        var updated_at: String? = null

        @SerializedName("is_login")
        var is_login: String? = null

    }
}
