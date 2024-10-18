package com.example.trainerapp.ApiClass

import com.google.gson.annotations.SerializedName

class CategoriesData {

    @SerializedName("status")
    var status: Boolean? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("data")
    var data: ArrayList<Categoty>? = null

    class Categoty {

        @SerializedName("id")
        var id: Int? = null

        @SerializedName("coach_id")
        var coach_id: String? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("image")
        var image: String? = null

        @SerializedName("created_at")
        var created_at: String? = null

        @SerializedName("updated_at")
        var updated_at: String? = null

        @SerializedName("deleted_at")
        var deleted_at: String? = null

    }
}
