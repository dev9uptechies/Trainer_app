package com.example.model.AthleteDataPackage

import com.google.gson.annotations.SerializedName

class AthleteDatas {

    @SerializedName("status")
    var status: Boolean? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("data")
    var athleteData: List<AthleteList>? = null // Make sure this is a List

    // Nested class for the athlete data list
    class AthleteList {

        @SerializedName("id")
        var id: Int? = null

        @SerializedName("athlete_id")
        var athleteId: String? = null

        @SerializedName("weight")
        var weight: String? = null

        @SerializedName("base_line")
        var baseline: String? = null

        @SerializedName("fat_data")
        var fatData: String? = null

        @SerializedName("fat_mass")
        var fatMass: String? = null

        @SerializedName("date")
        var date: String? = null

        @SerializedName("created_at")
        var createdAt: String? = null

        @SerializedName("updated_at")
        var updatedAt: String? = null
    }
}
