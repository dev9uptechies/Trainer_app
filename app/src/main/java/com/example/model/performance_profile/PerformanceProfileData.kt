package com.example.model.performance_profile

import com.example.model.newClass.competition.Competition
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class PerformanceProfileData : Serializable {
    @SerializedName("data")
    var data: List<PerformanceProfile>? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("status")
    var status: Boolean? = null

    class PerformanceProfile : Serializable {
        @SerializedName("id")
        var id: Int? = null

        @SerializedName("athlete_id")
        var athleteId: String? = null

        @SerializedName("coach_id")
        var coachId: String? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("created_at")
        var created_at: String? = null

        @SerializedName("updated_at")
        var updated_at: String? = null

        @SerializedName("coach")
        var coach: Competition.Coach? = null

        @SerializedName("performance_template_category")
        var performanceTemplateCategory: List<PerformanceTemplateCategory>? = null
    }

    class PerformanceTemplateCategory : Serializable {
        @SerializedName("id")
        var id: Int? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("created_at")
        var created_at: String? = null

        @SerializedName("updated_at")
        var updated_at: String? = null

        @SerializedName("performance_template_id")
        var performanceTemplateId: String? = null

        @SerializedName("performance_template_quality")
        var performanceTemplateQuality: List<PerformanceTemplateQuality>? = null
    }

    class PerformanceTemplateQuality : Serializable {
        @SerializedName("id")
        var id: Int? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("created_at")
        var created_at: String? = null

        @SerializedName("updated_at")
        var updated_at: String? = null

        @SerializedName("athelet_score")
        var atheletScore: Int? = null

        @SerializedName("coach_score")
        var coachScore: Int? = null

        @SerializedName("performance_template_category_id")
        var performanceTemplateCategoryId: String? = null
    }
}
