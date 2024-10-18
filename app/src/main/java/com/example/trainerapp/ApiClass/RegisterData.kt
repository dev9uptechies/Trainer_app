package com.example.trainerapp.ApiClass

import com.google.gson.annotations.SerializedName

class RegisterData {

    @SerializedName("status")
    var status: Boolean? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("access_token")
    var token: String? = null

    @SerializedName("data")
    var data: Data?= null


    class Data {

        @SerializedName("id")
        var id: Int? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("email")
        var email: String? = null

        @SerializedName("is_login")
        var is_login: Int? = null

        @SerializedName("created_at")
        var created_at: String? = null

        @SerializedName("updated_at")
        var updated_at: String? = null

        @SerializedName("user_sports")
        var user_sports: ArrayList<User_Sportd_Data>?= null
    }
}
