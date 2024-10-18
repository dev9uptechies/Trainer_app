package com.example.model.performance_profile.performance.category

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PerformanceCategoryData(
    @SerializedName("athlete") val athlete: Athlete? = null,
    @SerializedName("athlete_id") val athlete_id: String? = null,
    @SerializedName("coach_id") val coach_id: String? = null,
    @SerializedName("created_at") val created_at: String? = null,
    @SerializedName("deleted_at") val deleted_at: String? = null,
    @SerializedName("id") val id: Int? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("updated_at") val updated_at: String? = null
) : Parcelable