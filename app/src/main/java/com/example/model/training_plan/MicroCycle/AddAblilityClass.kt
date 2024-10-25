package com.example.model.training_plan.MicroCycle

data class AddAblilityClass(
    val data: List<AbilityData>,
    val message: String?
)

data class AbilityData(
    val id: Int,
    val name: String,
    var isSelected: Boolean = false,
    val message: String?
)
