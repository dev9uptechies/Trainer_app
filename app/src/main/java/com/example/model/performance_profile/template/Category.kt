package com.example.model.performance_profile.template

import com.google.gson.annotations.SerializedName

data class Category(
    @SerializedName("category_name") val category_name: String,
    @SerializedName("qualitiy") val qualitiy: List<Qualitiy>
)