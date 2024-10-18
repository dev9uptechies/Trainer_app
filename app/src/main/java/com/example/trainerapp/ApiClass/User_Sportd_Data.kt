package com.example.trainerapp.ApiClass

import com.google.gson.annotations.SerializedName

class User_Sportd_Data {

    @SerializedName("id")
    var id: Int? = null
    @SerializedName("user_id")
    var user_id: String? = null
    @SerializedName("sport_id")
    var sport_id: String? = null
    @SerializedName("created_at")
    var created_at: String? = null
    @SerializedName("updated_at")
    var updated_at: String? = null
    @SerializedName("sport")
    var sport: Sport?= null

    class Sport {
        @SerializedName("id")
        var id: Int? = null

        @SerializedName("user_id")
        var user_id: String? = null

        @SerializedName("title")
        var title: String? = null

        @SerializedName("created_at")
        var created_at: String? = null

        @SerializedName("updated_at")
        var updated_at: String? = null

        @SerializedName("deleted_at")
        var deleted_at: String? = null

    }

}
