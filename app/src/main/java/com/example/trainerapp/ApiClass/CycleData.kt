package com.example.trainerapp.ApiClass

import com.google.gson.annotations.SerializedName

class CycleData {
    @SerializedName("status")
    var status: Boolean? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("data")
    var data: ArrayList<cycleData>?= null


    class cycleData{

        @SerializedName("id")
        var id: Int? = null

        @SerializedName("coach_id")
        var coach_id: String? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("image")
        var image: String? = null

        @SerializedName("goal_id")
        var goal_id: String? = null

        @SerializedName("section_id")
        var section_id: String? = null

        @SerializedName("type")
        var type: String? = null

        @SerializedName("category_id")
        var category_id : String? = null

        @SerializedName("video")
        var video: String? = null

        @SerializedName("notes")
        var notes: String? = null

    }
}
