package com.example.model

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose
import com.example.model.EventAthlete

class EventsModel {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("coach_id")
    @Expose
    var coachId: String? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("type")
    @Expose
    var type: String? = null

    @SerializedName("sport_id")
    @Expose
    var sportId: Any? = null

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

    @SerializedName("event_athletes")
    @Expose
    var eventAthletes: List<EventAthlete>? = null

    /**
     * No args constructor for use in serialization
     *
     */
    constructor()

    /**
     *
     * @param date
     * @param createdAt
     * @param sportId
     * @param deletedAt
     * @param eventAthletes
     * @param id
     * @param title
     * @param type
     * @param coachId
     * @param isFavourite
     * @param updatedAt
     */
    constructor(
        id: Int?,
        coachId: String?,
        title: String?,
        type: String?,
        sportId: Any?,
        date: String?,
        createdAt: String?,
        updatedAt: String?,
        deletedAt: Any?,
        isFavourite: Int?,
        eventAthletes: List<EventAthlete>?
    ) : super() {
        this.id = id
        this.coachId = coachId
        this.title = title
        this.type = type
        this.sportId = sportId
        this.date = date
        this.createdAt = createdAt
        this.updatedAt = updatedAt
        this.deletedAt = deletedAt
        this.isFavourite = isFavourite
        this.eventAthletes = eventAthletes
    }
}