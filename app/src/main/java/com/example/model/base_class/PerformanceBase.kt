package com.example.model.base_class

import com.example.model.performance_profile.PerformanceProfileData.PerformanceProfile
import com.google.gson.annotations.SerializedName

class PerformanceBase {
    @SerializedName("data")
    val data: PerformanceProfile? = null

    @SerializedName("message")
    val message: String? = null

    @SerializedName("status")
    val status: Boolean? = null
}