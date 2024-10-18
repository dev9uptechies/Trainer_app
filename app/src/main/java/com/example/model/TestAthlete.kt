package com.example.model

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose
import com.example.model.TestListAthlete

class TestAthlete {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("test_id")
    @Expose
    var testId: String? = null

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
    var athlete: TestListAthlete? = null

    /**
     * No args constructor for use in serialization
     *
     */
    constructor()

    /**
     *
     * @param createdAt
     * @param athleteId
     * @param athlete
     * @param testId
     * @param id
     * @param updatedAt
     */
    constructor(
        id: Int?,
        testId: String?,
        athleteId: String?,
        createdAt: String?,
        updatedAt: String?,
        athlete: Athlete?
    ) : super() {
        this.id = id
        this.testId = testId
        this.athleteId = athleteId
        this.createdAt = createdAt
        this.updatedAt = updatedAt
    }
}