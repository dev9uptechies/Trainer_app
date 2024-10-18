package com.example.model

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class LessonModel {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("coach_id")
    @Expose
    var coachId: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("goal_id")
    @Expose
    var goalId: String? = null

    @SerializedName("time")
    @Expose
    var time: String? = null

    @SerializedName("section_time")
    @Expose
    var sectionTime: String? = null

    @SerializedName("section_id")
    @Expose
    var sectionId: String? = null

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

    /**
     * No args constructor for use in serialization
     *
     */
    constructor()

    /**
     *
     * @param date
     * @param goalId
     * @param goal
     * @param sectionId
     * @param coachId
     * @param sectionTime
     * @param createdAt
     * @param deletedAt
     * @param name
     * @param id
     * @param time
     * @param isFavourite
     * @param updatedAt
     */
    constructor(
        id: Int?,
        coachId: String?,
        name: String?,
        goalId: String?,
        time: String?,
        sectionTime: String?,
        sectionId: String?,
        date: String?,
        createdAt: String?,
        updatedAt: String?,
        deletedAt: Any?,
        isFavourite: Int?
    ) : super() {
        this.id = id
        this.coachId = coachId
        this.name = name
        this.goalId = goalId
        this.time = time
        this.sectionTime = sectionTime
        this.sectionId = sectionId
        this.date = date
        this.createdAt = createdAt
        this.updatedAt = updatedAt
        this.deletedAt = deletedAt
        this.isFavourite = isFavourite
    }
}