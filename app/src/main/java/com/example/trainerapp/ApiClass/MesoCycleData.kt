package com.example.trainerapp.ApiClass

import com.google.gson.annotations.SerializedName

class MesoCycleData {
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


    // ✅ Add the missing microcycles list
    @SerializedName("microcycles")
    var microcycles: List<MicroCycleData>? = null
}

// ✅ Define the MicroCycleData model
class MicroCycleData {
    @SerializedName("id")
    var id: Int? = null

    @SerializedName("ps_mesocycle_id")
    var ps_mesocycle_id: String? = null

    @SerializedName("name")
    var name: String? = null

    @SerializedName("start_date")
    var start_date: String? = null

    @SerializedName("end_date")
    var end_date: String? = null

    @SerializedName("workload_color")
    var workload_color: String? = null
}
