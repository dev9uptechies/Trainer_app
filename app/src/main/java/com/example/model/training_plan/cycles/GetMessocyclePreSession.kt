package com.example.model.training_plan.cycles

import com.google.gson.annotations.SerializedName

class GetMessocyclePreSession {


    @SerializedName("data")
    val data: List<Data>? = null

    @SerializedName("message")
    val message: String? = null

    @SerializedName("status")
    val status: Boolean? = null


    class Data {

        @SerializedName("id")
        val id: Int? = null

        @SerializedName("name")
        val name: String? = null

        @SerializedName("planning_id")
        val planning_id: String? = null

        @SerializedName("start_date")
        val start_date: String? = null

        @SerializedName("end_date")
        val end_date: String? = null

        @SerializedName("periods")
        val periods: String? = null

        @SerializedName("created_at")
        val created_at: String? = null

        @SerializedName("updated_at")
        val updated_at: String? = null

        @SerializedName("pre_season")
        val pre_season: AddMesocyclePresession? = null

        @SerializedName("pre_competitive")
        val pre_competitive: AddMesocyclePreCompatitive? = null

        @SerializedName("Competitive")
        val compatitive: AddMesocyclePreCompatitive? = null

        @SerializedName("Transition")
        val transition: AddMesocycleTransition? = null

    }

}