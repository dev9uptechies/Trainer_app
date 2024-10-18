package com.example.model.newClass.excercise

import com.google.gson.annotations.SerializedName

class Exercise {
    @SerializedName("data")
    val data: List<ExerciseData>? = null

    @SerializedName("message")
    val message: String? = null

    @SerializedName("status")
    val status: Boolean? = null

    class ExerciseData {
        @SerializedName("category")
        val category: Category? = null

        @SerializedName("category_id")
        val category_id: String? = null

        @SerializedName("coach_id")
        val coach_id: String? = null

        @SerializedName("created_at")
        val created_at: String? = null

        @SerializedName("cycles")
        val cycles: List<Cycle>? = null

        @SerializedName("date")
        val date: String? = null

        @SerializedName("deleted_at")
        val deleted_at: String? = null

        @SerializedName("exercise_equipments")
        val exercise_equipments: List<ExerciseEquipment>? = null

        @SerializedName("gif")
        val gif: String? = null

        @SerializedName("goal")
        val goal: Goal? = null

        @SerializedName("goal_id")
        val goal_id: String? = null

        @SerializedName("id")
        val id: Int? = null

        @SerializedName("image")
        val image: String? = null

        @SerializedName("is_favourite")
        val is_favourite: Int? = null

        @SerializedName("name")
        val name: String? = null

        @SerializedName("notes")
        val notes: String? = null

        @SerializedName("section")
        val section: Section? = null

        @SerializedName("section_id")
        val section_id: String? = null

        @SerializedName("timer_id")
        val timer_id: String? = null

        @SerializedName("timer_name")
        val timer_name: TimerName? = null

        @SerializedName("type")
        val type: String? = null

        @SerializedName("updated_at")
        val updated_at: String? = null

        @SerializedName("video")
        val video: String? = null

        @SerializedName("video_link")
        val video_link: String? = null
    }

    class Section {
        @SerializedName("created_at")
        val section_created_at: String? = null

        @SerializedName("deleted_at")
        val section_deleted_at: String? = null

        @SerializedName("id")
        val section_id: Int? = null

        @SerializedName("name")
        val section_name: String? = null

        @SerializedName("updated_at")
        val section_updated_at: String? = null
    }

    class Category {
        @SerializedName("created_at")
        val category_created_at: String? = null

        @SerializedName("deleted_at")
        val category_deleted_at: String? = null

        @SerializedName("id")
        val category_id: Int? = null

        @SerializedName("name")
        val category_name: String? = null

        @SerializedName("updated_at")
        val category_updated_at: String? = null
    }

    class Cycle {
        @SerializedName("created_at")
        val cycle_created_at: String? = null

        @SerializedName("deleted_at")
        val cycle_deleted_at: String? = null

        @SerializedName("distance")
        val cycle_distance: String? = null

        @SerializedName("exercise_id")
        val cycle_exercise_id: String? = null

        @SerializedName("id")
        val cycle_id: Int? = null

        @SerializedName("pause")
        val cycle_pause: String? = null

        @SerializedName("pause_cycle")
        val cycle_pause_cycle: String? = null

        @SerializedName("reps")
        val cycle_reps: String? = null

        @SerializedName("set")
        val cycle_set: String? = null

        @SerializedName("time")
        val cycle_time: String? = null

        @SerializedName("timer_id")
        val cycle_timer_id: String? = null

        @SerializedName("updated_at")
        val cycle_updated_at: String? = null

        @SerializedName("weight")
        val cycle_weight: String? = null
    }

    class ExerciseEquipment {
        @SerializedName("created_at")
        val exercise_created_at: String? = null

        @SerializedName("equipment")
        val exercise_equipment: Equipment? = null

        @SerializedName("equipment_id")
        val exercise_equipment_id: String? = null

        @SerializedName("exercise_id")
        val exercise_exercise_id: String? = null

        @SerializedName("id")
        val exercise_id: Int? = null

        @SerializedName("updated_at")
        val exercise_updated_at: String? = null
    }

    class Equipment {
        @SerializedName("coach_id")
        val equipment_coach_id: String? = null

        @SerializedName("created_at")
        val equipment_created_at: String? = null

        @SerializedName("deleted_at")
        val equipment_deleted_at: String? = null

        @SerializedName("id")
        val equipment_id: Int? = null

        @SerializedName("image")
        val equipment_image: String? = null

        @SerializedName("name")
        val equipment_name: String? = null

        @SerializedName("updated_at")
        val equipment_updated_at: String? = null
    }

    class Goal {
        @SerializedName("created_at")
        val goal_created_at: String? = null

        @SerializedName("deleted_at")
        val goal_deleted_at: String? = null

        @SerializedName("id")
        val goal_id: Int? = null

        @SerializedName("name")
        val goal_name: String? = null

        @SerializedName("updated_at")
        val goal_updated_at: String? = null
    }

    class TimerName {
        @SerializedName("audio_id")
        val timer_name_audio_id: String? = null

        @SerializedName("created_at")
        val timer_name_created_at: String? = null

        @SerializedName("id")
        val timer_name_id: Int? = null

        @SerializedName("name")
        val timer_name_name: String? = null

        @SerializedName("pause_between_time_audio_id")
        val timer_name_pause_between_time_audio_id: String? = null

        @SerializedName("pause_time_audio_id")
        val timer_name_pause_time_audio_id: String? = null

        @SerializedName("timer")
        val timer_name_timer: List<Timer>? = null

        @SerializedName("updated_at")
        val timer_name_updated_at: String? = null
    }

    class Timer {
        @SerializedName("audio")
        val timer_audio: Audio? = null

        @SerializedName("audio_id")
        val timer_audio_id: String? = null

        @SerializedName("created_at")
        val timer_created_at: String? = null

        @SerializedName("deleted_at")
        val timer_deleted_at: String? = null

        @SerializedName("distance")
        val timer_distance: String? = null

        @SerializedName("id")
        val timer_id: Int? = null

        @SerializedName("name")
        val timer_name: String? = null

        @SerializedName("pause")
        val timer_pause: String? = null

        @SerializedName("pause_between_time_audio")
        val timer_pause_between_time_audio: PauseBetweenTimeAudio? = null

        @SerializedName("pause_between_time_audio_id")
        val timer_pause_between_time_audio_id: String? = null

        @SerializedName("pause_time_audio")
        val timer_pause_time_audio: PauseTimeAudio? = null

        @SerializedName("pause_time_audio_id")
        val timer_pause_time_audio_id: String? = null

        @SerializedName("pause_timer")
        val timer_pause_timer: String? = null

        @SerializedName("reps")
        val timer_reps: String? = null

        @SerializedName("set")
        val timer_set: String? = null

        @SerializedName("time")
        val timer_time: String? = null

        @SerializedName("timer_name_id")
        val timer_timer_name_id: String? = null

        @SerializedName("updated_at")
        val timer_updated_at: String? = null

        @SerializedName("weight")
        val timer_weight: String? = null
    }

    class Audio {
        @SerializedName("audio")
        val audio: String? = null

        @SerializedName("created_at")
        val audio_created_at: String? = null

        @SerializedName("id")
        val audio_id: Int? = null

        @SerializedName("name")
        val audio_name: String? = null

        @SerializedName("updated_at")
        val audio_updated_at: String? = null
    }

    class PauseTimeAudio {
        @SerializedName("audio")
        val pause_time_audio: String? = null

        @SerializedName("created_at")
        val pause_time_created_at: String? = null

        @SerializedName("id")
        val pause_time_id: Int? = null

        @SerializedName("name")
        val pause_time_name: String? = null

        @SerializedName("updated_at")
        val pause_time_updated_at: String? = null
    }

    class PauseBetweenTimeAudio {
        @SerializedName("audio")
        val pause_between_audio: String? = null

        @SerializedName("created_at")
        val pause_between_created_at: String? = null

        @SerializedName("id")
        val pause_between_id: Int? = null

        @SerializedName("name")
        val pause_between_name: String? = null

        @SerializedName("updated_at")
        val pause_between_updated_at: String? = null
    }
}