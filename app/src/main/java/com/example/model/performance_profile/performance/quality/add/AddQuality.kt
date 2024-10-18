package com.example.model.performance_profile.performance.quality.add

data class AddQuality(
    val athlete_id: Int? = null,
    val performance_category_id: Int? = null,
    val qualities: List<Quality>? = null
)