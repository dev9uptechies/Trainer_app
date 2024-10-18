package com.example.trainerapp.ApiClass

import com.google.gson.annotations.SerializedName

class SportlistData {

    @SerializedName("status")
    var status: Boolean? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("data")
    var data: ArrayList<sportlist>? = null

    class sportlist{

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
