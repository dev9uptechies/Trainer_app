package com.example

import com.example.trainerapp.ApiClass.ProgramListData
import com.google.gson.annotations.SerializedName

class ChateData {
    @SerializedName("status")
    var status: Boolean? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("data")
    var data: ArrayList<Data>? = null

    class Data {

        @SerializedName("id")
        var id: Long? = null

        @SerializedName("group_id")
        var group_id: String? = null

        @SerializedName("sender_id")
        var sender_id: String? = null

        @SerializedName("message")
        var message: String? = null

        @SerializedName("created_at")
        var created_at : String? = null

        @SerializedName("updated_at")
        var updated_at: String? = null

        @SerializedName("sender")
        var sender: Sender? = null
    }

    class Sender {

        @SerializedName("id")
        var id: Long? = null

        @SerializedName("coach_id")
        var coach_id: String? = null

        @SerializedName("sport_id")
        var sport_id: String? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("email")
        var email: String? = null

        @SerializedName("email_verified_at")
        var email_verified_at: String? = null

        @SerializedName("birthdate")
        var birthdate: String? = null

        @SerializedName("address")
        var address: String? = null

        @SerializedName("zipcode")
        var zipcode: String? = null

        @SerializedName("ref_code")
        var ref_code: String? = null

        @SerializedName("ref_user_id")
        var ref_user_id: String? = null

        @SerializedName("is_login")
        var is_login: String? = null

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

