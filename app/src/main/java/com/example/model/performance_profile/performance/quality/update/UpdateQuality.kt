package com.example.model.performance_profile.performance.quality.update

import com.example.model.performance_profile.performance.quality.add.Quality

data class UpdateQuality(
    val performance_quality_ids: List<Int>? = null,
    val athlete_id: Int? = null,
    val performance_category_id: Int? = null,
    val qualities: List<Quality>? = null
)