package com.example.model.performance_profile.performance.category

import com.google.gson.annotations.SerializedName

data class PerformanceCategory(
    @SerializedName("data") val data: List<PerformanceCategoryData>? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("status") val status: Boolean? = null
)