package com.example.trainerapp.ApiClass

import com.example.Pre_season
import com.google.gson.annotations.SerializedName

class MesoCycleData {
    @SerializedName("status")
    var status: Boolean? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("data")
    var data: ArrayList<sportlist>? = null

    class sportlist{

        @SerializedName("id")
        var id: Int? = null

        @SerializedName("planning_ps_id")
        var planning_ps_id: String? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("start_date")
        var start_date: String? = null

        @SerializedName("end_date")
        var end_date: String? = null

        @SerializedName("periods")
        var periods: String? = null

        @SerializedName("created_at")
        var created_at: String? = null

        @SerializedName("updated_at")
        var updated_at: String? = null

        @SerializedName("pre_season")
        var pre_season: PreSeason? = null

    }

    class PreSeason {

        @SerializedName("id")
        var id: Int? = null

        @SerializedName("planning_id")
        var planning_id: String? = null

        @SerializedName("mesocycle")
        var mesocycle: String? = null

        @SerializedName("start_date")
        var start_date: String? = null

        @SerializedName("end_date")
        var end_date: String? = null

        @SerializedName("created_at")
        var created_at: String? = null

        @SerializedName("updated_at")
        var updated_at: String? = null
    }
}
