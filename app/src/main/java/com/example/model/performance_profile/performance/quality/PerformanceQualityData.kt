package com.example.model.performance_profile.performance.quality

import com.google.gson.annotations.SerializedName

data class PerformanceQualityData(
    @SerializedName("athelet_score") val athelet_score: String? = null,
    @SerializedName("athlete_id") val athlete_id: String? = null,
    @SerializedName("coach_score") val coach_score: String? = null,
    @SerializedName("created_at") val created_at: String? = null,
    @SerializedName("deleted_at") val deleted_at: String? = null,
    @SerializedName("id") val id: Int? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("performance_category") val performance_category: Category? = null,
    @SerializedName("performance_category_id") val performance_category_id: String? = null,
    @SerializedName("updated_at") val updated_at: String? = null
)