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
        val psMesocycleId: String? = null

        @SerializedName("name")
        val name: String? = null

        @SerializedName("start_date")
        val startDate: String? = null

        @SerializedName("end_date")
        val endDate: String? = null

        @SerializedName("workload")
        val workload: Int? = null

        @SerializedName("workload_color")
        val workloadColor: String? = null

        @SerializedName("created_at")
        val createdAt: String? = null

        @SerializedName("updated_at")
        val updatedAt: String? = null

        @SerializedName("mesocycle")
        val mesocycle: Mesocycle? = null

        @SerializedName("ps_microcycle_ability")
        val psMicrocycleAbility: List<AbilityData>? = null
    }

    class Mesocycle {
        @SerializedName("id")
        val id: Int? = null

        @SerializedName("planning_ps_id")
        val planningPsId: String? = null

        @SerializedName("name")
        val name: String? = null

        @SerializedName("start_date")
        val startDate: String? = null

        @SerializedName("end_date")
        val endDate: String? = null

        @SerializedName("periods")
        val periods: String? = null

        @SerializedName("created_at")
        val createdAt: String? = null

        @SerializedName("updated_at")
        val updatedAt: String? = null
    }

    class AbilityData {
        @SerializedName("id")
        val id: Int? = null

        @SerializedName("ps_microcycle_id")
        val psMicrocycleId: String? = null

        @SerializedName("ability_id")
        val abilityId: String? = null

        @SerializedName("created_at")
        val createdAt: String? = null

        @SerializedName("updated_at")
        val updatedAt: String? = null

        @SerializedName("ability")
        val ability: Ability? = null
    }

    class Ability {
        @SerializedName("id")
        val id: Int? = null

        @SerializedName("name")
        val name: String? = null

        @SerializedName("created_at")
        val createdAt: String? = null

        @SerializedName("updated_at")
        val updatedAt: String? = null

        @SerializedName("deleted_at")
        val deletedAt: String? = null
    }
}
