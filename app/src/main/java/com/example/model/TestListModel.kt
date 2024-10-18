package com.example.model

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose
import com.example.model.TestAthlete

class TestListModel {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("coach_id")
    @Expose
    var coachId: String? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("goal")
    @Expose
    var goal: String? = null

    @SerializedName("unit")
    @Expose
    var unit: String? = null

    @SerializedName("date")
    @Expose
    var date: String? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

    @SerializedName("deleted_at")
    @Expose
    var deletedAt: Any? = null

    @SerializedName("is_favourite")
    @Expose
    var isFavourite: Int? = null

    @SerializedName("test_athletes")
    @Expose
    var testAthletes: List<TestAthlete>? = null

    /**
     * No args constructor for use in serialization
     *
     */
    constructor()

    /**
     *
     * @param date
     * @param createdAt
     * @param unit
     * @param deletedAt
     * @param goal
     * @param testAthletes
     * @param id
     * @param title
     * @param coachId
     * @param isFavourite
     * @param updatedAt
     */
    constructor(
        id: Int?,
        coachId: String?,
        title: String?,
        goal: String?,
        unit: String?,
        date: String?,
        createdAt: String?,
        updatedAt: String?,
        deletedAt: Any?,
        isFavourite: Int?,
        testAthletes: List<TestAthlete>?
    ) : super() {
        this.id = id
        this.coachId = coachId
        this.title = title
        this.goal = goal
        this.unit = unit
        this.date = date
        this.createdAt = createdAt
        this.updatedAt = updatedAt
        this.deletedAt = deletedAt
        this.isFavourite = isFavourite
        this.testAthletes = testAthletes
    }
}