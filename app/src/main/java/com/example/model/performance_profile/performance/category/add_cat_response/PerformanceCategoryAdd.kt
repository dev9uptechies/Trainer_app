package com.example.model.performance_profile.performance.category.add_cat_response

import com.example.model.performance_profile.performance.category.PerformanceCategoryData
import com.google.gson.annotations.SerializedName

data class PerformanceCategoryAdd(
    @SerializedName("data") val data: PerformanceCategoryData? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("status") val status: Boolean? = null
)