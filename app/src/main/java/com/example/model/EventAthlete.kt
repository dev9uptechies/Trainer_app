package com.example.model

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class EventAthlete {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("event_id")
    @Expose
    var eventId: String? = null

    @SerializedName("athlete_id")
    @Expose
    var athleteId: String? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

    @SerializedName("athlete")
    @Expose
    var athlete: Athlete? = null

    /**
     * No args constructor for use in serialization
     *
     */
    constructor()

    /**
     *
     * @param eventId
     * @param createdAt
     * @param athleteId
     * @param athlete
     * @param id
     * @param updatedAt
     */
    constructor(
        id: Int?,
        eventId: String?,
        athleteId: String?,
        createdAt: String?,
        updatedAt: String?,
        athlete: Athlete?
    ) : super() {
        this.id = id
        this.eventId = eventId
        this.athleteId = athleteId
        this.createdAt = createdAt
        this.updatedAt = updatedAt
        this.athlete = athlete
    }
}