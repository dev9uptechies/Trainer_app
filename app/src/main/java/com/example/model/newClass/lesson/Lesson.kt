package com.example.model.newClass.lesson

import com.example.model.newClass.excercise.Exercise
import com.google.gson.annotations.SerializedName

class Lesson {
    @SerializedName("status")
    var status: Boolean? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("data")
    var data: ArrayList<LessonDatabase>? = null

    class LessonDatabase {
        @SerializedName("coach_id")
        val coach_id: String? = null

        @SerializedName("created_at")
        val created_at: String? = null

        @SerializedName("date")
        val date: String? = null

        @SerializedName("deleted_at")
        val deleted_at: String? = null

        @SerializedName("id")
        val id: Int? = null

        @SerializedName("is_favourite")
        val is_favourite: Int? = null

        @SerializedName("lesson_goal")
        val lesson_goal: List<LessonGoal>? = null

        @SerializedName("lesson_programs")
        val lesson_programs: List<LessonProgram>? = null

        @SerializedName("name")
        val name: String? = null

        @SerializedName("section")
        val section: SectionXX? = null

        @SerializedName("section_id")
        val section_id: String? = null

        @SerializedName("section_time")
        val section_time: String? = null

        @SerializedName("time")
        val time: String? = null

        @SerializedName("updated_at")
        val updated_at: String? = null
    }

    class LessonGoal {
        @SerializedName("created_at")
        val created_at: String? = null

        @SerializedName("goal")
        val goal: Exercise.Goal? = null

        @SerializedName("goal_id")
        val goal_id: String? = null

        @SerializedName("id")
        val id: Int? = null

        @SerializedName("lesson_id")
        val lesson_id: String? = null

        @SerializedName("updated_at")
        val updated_at: String? = null
    }

    class LessonProgram {
        @SerializedName("created_at")
        val created_at: String? = null

        @SerializedName("id")
        val id: Int? = null

        @SerializedName("lesson_id")
        val lesson_id: String? = null

        @SerializedName("program")
        val program: Program? = null

        @SerializedName("program_id")
        val program_id: String? = null

        @SerializedName("updated_at")
        val updated_at: String? = null
    }

    class Program {
        @SerializedName("coach_id")
        val coach_id: String? = null

        @SerializedName("created_at")
        val created_at: String? = null

        @SerializedName("date")
        val date: String? = null

        @SerializedName("deleted_at")
        val deleted_at: String? = null

        @SerializedName("goal")
        val goal: Exercise.Goal? = null

        @SerializedName("goal_id")
        val goal_id: String? = null

        @SerializedName("id")
        val id: Int? = null

        @SerializedName("is_favourite")
        val is_favourite: Int? = null

        @SerializedName("name")
        val name: String? = null

        @SerializedName("program_exercises")
        val program_exercises: List<ProgramExercise>? = null

        @SerializedName("section")
        val section: Exercise.Section? = null

        @SerializedName("section_id")
        val section_id: String? = null

        @SerializedName("time")
        val time: String? = null

        @SerializedName("updated_at")
        val updated_at: String? = null
    }

    class ProgramExercise {
        @SerializedName("created_at")
        val created_at: String? = null

        @SerializedName("exercise")
        val exercise: Exercise.ExerciseData? = null

        @SerializedName("exercise_id")
        val exercise_id: String? = null

        @SerializedName("id")
        val id: Int? = null

        @SerializedName("program_id")
        val program_id: String? = null

        @SerializedName("updated_at")
        val updated_at: String? = null
    }

}