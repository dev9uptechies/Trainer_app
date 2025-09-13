package com.example.model.base_class

import com.google.gson.annotations.SerializedName

data class PerformanceProfileBase(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("titleName")
    val titleName: String? = null,

    @SerializedName("subTitleNames")
    var subTitleName: MutableList<PerformanceStar>? = null

)

data class PerformanceStar(

    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("title")
    var title: String? = null,

    @SerializedName("coachStar")
    val coachStar: String? = null,

    @SerializedName("athleteStar")
    val athleteStar: String? = null
)