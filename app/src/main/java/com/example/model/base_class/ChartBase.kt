package com.example.model.base_class

data class ChartBase(
    var catId: Int? = null,
    var catName: String? = null,
    var catQuality: MutableList<QualityBase>? = null
)

data class QualityBase(
    var qualityName: String? = null,
    var athleteScore: Float? = null,
    var coachScore: Float? = null
)