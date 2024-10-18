package com.example.trainerapp.ApiClass

import com.example.model.newClass.excercise.Exercise
import com.google.gson.annotations.SerializedName

class ProgramListData {
    @SerializedName("status")
    var status: Boolean? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("data")
    var data: ArrayList<testData>? = null

    class testData {

        @SerializedName("id")
        var id: Int? = null

        @SerializedName("is_favourite")
        var is_favourite: String? = null

        @SerializedName("coach_id")
        var coach_id: String? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("goal_id")
        var goal_id: String? = null

        @SerializedName("time")
        var time: String? = null

        @SerializedName("section_id")
        var section_id: String? = null

        @SerializedName("date")
        var date: String? = null

        @SerializedName("created_at")
        var created_at: String? = null

        @SerializedName("updated_at")
        var updated_at: String? = null

        @SerializedName("program_exercises")
        var program_exercises: ArrayList<Program>? = null

        @SerializedName("goal")
        var goal: Goal? = null

        @SerializedName("section")
        var section: Section? = null

    }

    class Program {

        @SerializedName("id")
        var id: Int? = null

        @SerializedName("program_id")
        var program_id: String? = null

        @SerializedName("exercise_id")
        var exercise_id: String? = null

        @SerializedName("created_at")
        var created_at: String? = null

        @SerializedName("updated_at")
        var updated_at: String? = null

        @SerializedName("exercise")
        var exercise: Exercise? = null

    }

    class Exercise {

        @SerializedName("id")
        var id: Int? = null

        @SerializedName("coach_id")
        var coach_id: String? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("image")
        var image: String? = null

        @SerializedName("goal_id")
        var goal_id: String? = null

        @SerializedName("is_favourite")
        var is_favourite: Int? = null

        @SerializedName("section_id")
        var section_id: String? = null

        @SerializedName("timer_id")
        var timer_id: String? = null

        @SerializedName("type")
        var type: String? = null

        @SerializedName("category_id")
        var category_id: String? = null

        @SerializedName("video")
        var video: String? = null

        @SerializedName("video_link")
        var video_link: String? = null

        @SerializedName("gif")
        var gif: String? = null

        @SerializedName("notes")
        var notes: String? = null

        @SerializedName("created_at")
        var created_at: String? = null

        @SerializedName("updated_at")
        var updated_at: String? = null

        @SerializedName("deleted_at")
        var deleted_at: String? = null

        @SerializedName("goal")
        var goal: Goal? = null

        @SerializedName("section")
        var section: Section? = null

        @SerializedName("cycles")
        var cycles: ArrayList<ExcerciseData.Cycles>? = null

        @SerializedName("exercise_equipments")
        var exercise_equipments: ArrayList<Exercise_Equipments>? = null

        @SerializedName("category")
        var category: com.example.model.newClass.excercise.Exercise.Category? = null

        @SerializedName("timer_name")
        var timer_name: TimerName? = null

    }

    class Exercise_Equipments {
        @SerializedName("id")
        var id: Int? = null

        @SerializedName("exercise_id")
        var exercise_id: String? = null

        @SerializedName("equipment_id")
        var equipment_id: String? = null

        @SerializedName("created_at")
        var created_at: String? = null

        @SerializedName("updated_at")
        var updated_at: String? = null

        @SerializedName("equipment")
        var equipment: Equipment? = null

    }

    class Equipment {
        @SerializedName("id")
        var id: Int? = null

        @SerializedName("coach_id")
        var coach_id: String? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("image")
        var image: String? = null

        @SerializedName("created_at")
        var created_at: String? = null

        @SerializedName("updated_at")
        var updated_at: String? = null

        @SerializedName("deleted_at")
        var deleted_at: String? = null
    }

    class Goal {

        @SerializedName("id")
        var id: Int? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("created_at")
        var created_at: String? = null

        @SerializedName("updated_at")
        var updated_at: String? = null

        @SerializedName("deleted_at")
        var deleted_at: String? = null

    }

    class Section {

        @SerializedName("id")
        var id: Int? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("created_at")
        var created_at: String? = null

        @SerializedName("updated_at")
        var is_login: String? = null

        @SerializedName("deleted_at")
        var deleted_at: String? = null


    }

    class TimerName {
        @SerializedName("id")
        var id: Int? = null

        @SerializedName("user_id")
        var user_id: String? = null

        @SerializedName("audio_id")
        var audio_id: String? = null

        @SerializedName("pause_time_audio_id")
        var pause_time_audio_id: String? = null

        @SerializedName("pause_between_time_audio_id")
        var pause_between_time_audio_id: String? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("created_at")
        var created_at: String? = null

        @SerializedName("updated_at")
        var updated_at: String? = null

        @SerializedName("timer")
        var timer: ArrayList<com.example.model.newClass.excercise.Exercise.Timer>? = null
    }
}
