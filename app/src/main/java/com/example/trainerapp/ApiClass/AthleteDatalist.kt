package com.example.trainerapp.ApiClass

import com.google.gson.annotations.SerializedName

class AthleteDatalist {


    @SerializedName("status")
    var status: Boolean? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("data")
    lateinit var Atheletedata: Atheletedatalist

    class Atheletedatalist {

        @SerializedName("first_page_url")
        var first_page_url: String? = null

        @SerializedName("from")
        var from: Int? = null

        @SerializedName("last_page")
        var last_page: Int? = null

        @SerializedName("current_page")
        var current_page: Int? = null

        @SerializedName("next_page_url")
        var next_page_url: String? = null

        @SerializedName("path")
        var path: String? = null

        @SerializedName("per_page")
        var per_page: Int? = null

        @SerializedName("prev_page_url")
        var prev_page_url: Int? = null

        @SerializedName("to")
        var to: Int? = null

        @SerializedName("total")
        var total: Int? = null

        @SerializedName("data")
        var data: ArrayList<AtheleteList>? = null

    }

    class AtheleteList {

        @SerializedName("id")
        var id: Int? = null

        @SerializedName("coach_id")
        var coach_id: Int? = null

        @SerializedName("sport_id")
        var sport_id: Int? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("email")
        var email: String? = null

        @SerializedName("birthdate")
        var birthdate: String? = null

        @SerializedName("is_login")
        var is_login: String? = null

        @SerializedName("created_at")
        var created_at: String? = null

        @SerializedName("updated_at")
        var updated_at: String? = null

        @SerializedName("total")
        var total: Int? = null

        @SerializedName("deleted_at")
        var deleted_at: String? = null

    }
}
