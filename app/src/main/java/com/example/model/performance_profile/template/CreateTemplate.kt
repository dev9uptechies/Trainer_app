package com.example.model.performance_profile.template

import com.google.gson.annotations.SerializedName

data class CreateTemplate(
    @SerializedName("category") val category: List<Category>,
    @SerializedName("name") val name: String
)