package com.example.model.trainer_plan

data class PreSeason(
    val name: String,
    val start_date: String,
    val end_date: String,
    val mesocycle: String
)

data class PreCompetitive(
    val name: String,
    val start_date: String,
    val end_date: String,
    val mesocycle: String
)

data class Competitive(
    val name: String,
    val start_date: String,
    val end_date: String,
    val mesocycle: String
)

data class Transition(
    val name: String,
    val start_date: String,
    val end_date: String,
    val mesocycle: String
)

data class TrainingPlanSubClass(
    val id: Int,
    val name: String,
    val start_date: String,
    val competition_date: String,
    val mesocycle: String,
    val pre_season: PreSeason,
    val pre_competitive: PreCompetitive,
    val competitive: Competitive,
    val transition: Transition
)