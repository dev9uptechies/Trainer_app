package com.example.model.training_plan.MicroCycle

import com.example.model.training_plan.cycles.AddMesocycleCompatitive
import com.example.model.training_plan.cycles.GetMessocyclePreSession.Data
import com.google.gson.annotations.SerializedName

class GetMicrocycle {

    @SerializedName("data")
    val data: List<Data>? = null

    @SerializedName("message")
    val message: String? = null

    @SerializedName("status")
    val status: Boolean? = null

    class Data {

        @SerializedName("id")
        val id: Int? = null

        @SerializedName("ps_mesocycle_id")
        val ps_mesocycle_id: String? = null

        @SerializedName("name")
        val name: String? = null

        @SerializedName("start_date")
        val start_date: String? = null

        @SerializedName("end_date")
        val end_date: String? = null

        @SerializedName("workload")
        val workload: Int? = null

        @SerializedName("workload_color")
        val workload_color: String? = null

        @SerializedName("created_at")
        val created_at: String? = null

        @SerializedName("updated_at")
        val updated_at: String? = null

        @SerializedName("pre_season")
        val pre_season: AddMicrocyclePreSeason? = null

        @SerializedName("pre_competitive")
        val pre_competitive: AddMicrocyclePreCompatitive? = null

        @SerializedName("Competitive")
        val compatitive: AddMesocycleCompatitive? = null

        @SerializedName("Transition")
        val transition: AddMicrocycleTransition? = null
    }
}