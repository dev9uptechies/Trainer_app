package com.example.model.base_class

import com.example.model.performance_profile.performance.quality.PerformanceQualityData

data class ChartBase(
    var catId: Int? = null,
    var catName: String? = null,
    var catQuality: MutableList<QualityBase>? = null,
    var catData: MutableList<PerformanceQualityData2>? = null
)

data class QualityBase(
    var qualityName: String? = null,
    var athleteScore: Float?,
    var coachScore: Float?
)

data class PerformanceCategoryData(
    val id: Int,
    val name: String,
    val performanceQualityData: List<PerformanceQualityData2>? // This should match the API response
)

data class PerformanceQualityData2(
    var name: String? = null,
    var athleteScore: Float? = null,
    var coachScore: Float? = null
)