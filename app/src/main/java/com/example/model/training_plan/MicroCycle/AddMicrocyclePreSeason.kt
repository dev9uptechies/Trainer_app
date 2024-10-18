package com.example.model.training_plan.MicroCycle

data class AddMicrocyclePreSeason(
    val id: Int?,
    val ps_mesocycle_id: Int,
    val name: String,
    val start_date: String,
    val end_date: String,
    var workload: Int,
    var workload_color: String,
    val ability_ids: List<Int>
)

data class AddMicrocyclePreCompatitive(
    val id: Int?,
    val pc_mesocycle_id: Int,
    val name: String,
    val start_date: String,
    val end_date: String,
    var workload: Int,
    var workload_color: String,
    val ability_ids: List<Int>
)

data class AddMicrocycleCompatitive(
    val id: Int?,
    val c_mesocycle_id: Int,
    val name: String,
    val start_date: String,
    val end_date: String,
    var workload: Int,
    var workload_color: String,
    val ability_ids: List<Int>
)

data class AddMicrocycleTransition(
    val id: Int?,
    val pt_mesocycle_id: Int,
    val name: String,
    val start_date: String,
    val end_date: String,
    var workload: Int,
    var workload_color: String,
    val ability_ids: List<Int>
)
