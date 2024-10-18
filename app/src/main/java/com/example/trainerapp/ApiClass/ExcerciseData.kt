package com.example.trainerapp.ApiClass

import com.google.gson.annotations.SerializedName

class ExcerciseData {

    @SerializedName("status")
    var status: Boolean? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("data")
    var data: ArrayList<Exercise>? = null

    class Exercise {

        @SerializedName("id")
        var id: Int? = null

        @SerializedName("coach_id")
        var coach_id: String? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("is_favourite")
        var is_favourite: String? = null

        @SerializedName("image")
        var image: String? = null

        @SerializedName("goal_id")
        var goal_id: String? = null

        @SerializedName("section_id")
        var section_id: String? = null

        @SerializedName("video")
        var video: String? = null

        @SerializedName("type")
        var type: String? = null

        @SerializedName("notes")
        var notes: String? = null

        @SerializedName("created_at")
        var created_at: String? = null

        @SerializedName("updated_at")
        var updated_at: String? = null

        @SerializedName("exercise_equipments")
        var exercise_equipments: ArrayList<Exercise_Equipments>? = null

        @SerializedName("cycles")
        var cycles: ArrayList<Cycles>? = null

        @SerializedName("goal")
        var goal: Goal? = null

        @SerializedName("section")
        var section: Section? = null

    }

    class Goal {

        @SerializedName("id")
        var id: Int? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("updated_at")
        var updated_at: String? = null
    }

    class Section {

        @SerializedName("id")
        var id: Int? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("updated_at")
        var updated_at: String? = null
    }

    class Cycles {

        @SerializedName("id")
        var id: Int? = null

        @SerializedName("set")
        var set: String? = null

        @SerializedName("time")
        var time: String? = null

        @SerializedName("reps")
        var reps: String? = null

        @SerializedName("pause")
        var pause: String? = null

        @SerializedName("weight")
        var weight: String? = null

        @SerializedName("distance")
        var distance: String? = null

        @SerializedName("created_at")
        var created_at: String? = null

        @SerializedName("updated_at")
        var updated_at: String? = null

        @SerializedName("deleted_at")
        var deleted_at: String? = null

        @SerializedName("exercise_id")
        var exercise_id: String? = null

        @SerializedName("timer_id")
        var timer_id: String? = null

        @SerializedName("pause_cycle")
        var pause_cycle: String? = null

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

    }

}
