package com.example.model.training_plan.MicroCycle

data class AddAblilityClass(
    val message: String,
    val data: List<AbilityData>
)

data class AbilityData(
    val id: Int,
    val name: String,
    var isSelected: Boolean = false
)
