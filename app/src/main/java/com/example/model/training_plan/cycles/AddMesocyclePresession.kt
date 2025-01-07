package com.example.model.training_plan.cycles

data class AddMesocyclePresession(
    val id: Int?,
    val planning_ps_id: String,
    val name: String,
    val start_date: String,
    val end_date: String,
)

data class AddMesocyclePreCompatitive(
    val id: Int?,
    val planning_pc_id: String,
    val name: String,
    val start_date: String,
    val end_date: String,
    val periods: String
)

data class AddMesocycleCompatitive(
    val id: Int?,
    val planning_c_id: String,
    val name: String,
    val start_date: String,
    val end_date: String,
    val periods: String
)

data class AddMesocycleTransition(
    val id: Int?,
    val planning_t_id: String,
    val name: String,
    val start_date: String,
    val end_date: String,
    val periods: String
)