package com.example.model.performance_profile.performance.quality

import com.google.gson.annotations.SerializedName

data class PerformanceQuality(
    @SerializedName("data") val data: List<PerformanceQualityData>? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("status") val status: Boolean? = null
)