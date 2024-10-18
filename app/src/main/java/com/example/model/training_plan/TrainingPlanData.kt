package com.example.model.training_plan

import com.google.gson.annotations.SerializedName

class TrainingPlanData {
    @SerializedName("data")
    val data: List<TrainingPlan>? = null

    @SerializedName("message")
    val message: String? = null

    @SerializedName("status")
    val status: Boolean? = null

    @SerializedName("id")
    val id: Boolean? = null


    class TrainingPlan {

        @SerializedName("coach_id")
        val coach_id: String? = null

        @SerializedName("competition_date")
        val competition_date: String? = null

        @SerializedName("competitive")
        val competitive: Competitive? = null

        @SerializedName("created_at")
        val created_at: String? = null

        @SerializedName("id")
        val id: Int? = null

        @SerializedName("mesocycle")
        val mesocycle: String? = null

        @SerializedName("name")
        val name: String? = null

        @SerializedName("pre_competitive")
        val pre_competitive: PreCompetitive? = null

        @SerializedName("pre_season")
        val pre_season: PreSeason? = null

        @SerializedName("start_date")
        val start_date: String? = null

        @SerializedName("transition")
        val transition: Transition? = null

        @SerializedName("updated_at")
        val updated_at: String? = null

    }

    class Competitive {

        @SerializedName("created_at")
        val created_at: String? = null

        @SerializedName("end_date")
        val end_date: String? = null

        @SerializedName("id")
        val id: Int? = null

        @SerializedName("mesocycle")
        val mesocycle: String? = null

        @SerializedName("name")
        val name: String? = null

        @SerializedName("planning_id")
        val planning_id: String? = null

        @SerializedName("start_date")
        val start_date: String? = null

        @SerializedName("updated_at")
        val updated_at: String? = null
    }

    class PreCompetitive {

        @SerializedName("created_at")
        val created_at: String? = null

        @SerializedName("end_date")
        val end_date: String? = null

        @SerializedName("id")
        val id: Int? = null

        @SerializedName("mesocycle")
        val mesocycle: String? = null

        @SerializedName("name")
        val name: String? = null

        @SerializedName("planning_id")
        val planning_id: String? = null

        @SerializedName("start_date")
        val start_date: String? = null

        @SerializedName("updated_at")
        val updated_at: String? = null
    }

    class PreSeason {

        @SerializedName("created_at")
        val created_at: String? = null

        @SerializedName("end_date")
        val end_date: String? = null

        @SerializedName("id")
        val id: Int? = null

        @SerializedName("mesocycle")
        val mesocycle: String? = null

        @SerializedName("name")
        val name: String? = null

        @SerializedName("planning_id")
        val planning_id: String? = null

        @SerializedName("start_date")
        val start_date: String? = null

        @SerializedName("updated_at")
        val updated_at: String? = null
    }

    class Transition {

        @SerializedName("created_at")
        val created_at: String? = null

        @SerializedName("end_date")
        val end_date: String? = null

        @SerializedName("id")
        val id: Int? = null

        @SerializedName("mesocycle")
        val mesocycle: String? = null

        @SerializedName("name")
        val name: String? = null

        @SerializedName("planning_id")
        val planning_id: String? = null

        @SerializedName("start_date")
        val start_date: String? = null

        @SerializedName("updated_at")
        val updated_at: String? = null
    }
}