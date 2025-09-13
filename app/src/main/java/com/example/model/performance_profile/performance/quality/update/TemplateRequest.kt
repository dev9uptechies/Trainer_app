package com.example.model.performance_profile.performance.quality.update

data class TemplateRequest(
    val template_id: Int,
    val name: String,
    val category: List<com. example. model. performance_profile. template. Category>
)

data class Category(
    val category_name: String,
    val qualitiy: List<Quality>
)

data class Quality(
    val quality_name: String
)
