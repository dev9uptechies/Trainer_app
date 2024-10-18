package com.example.model.performance_profile.performance.quality.add_qual_response

import com.example.model.performance_profile.performance.quality.PerformanceQualityData
import com.google.gson.annotations.SerializedName

data class PerformanceQualityAdd(
    @SerializedName("data") val data: List<PerformanceQualityData>? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("status") val status: Boolean? = null
)