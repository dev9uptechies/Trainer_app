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
    var data: Data? = null

    class Data {

        @SerializedName("id")
        var id: Int? = null

        @SerializedName("coach_id")
        var coachId: String? = null

        @SerializedName("sport_id")
        var sportId: String? = null

        @SerializedName("plan_id")
        var planId: String? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("email")
        var email: String? = null

        @SerializedName("email_verified_at")
        var emailVerifiedAt: String? = null

        @SerializedName("birthdate")
        var birthdate: String? = null

        @SerializedName("address")
        var address: String? = null

        @SerializedName("zipcode")
        var zipcode: String? = null

        @SerializedName("ref_code")
        var refCode: String? = null

        @SerializedName("ref_user_id")
        var refUserId: String? = null

        @SerializedName("is_login")
        var isLogin: String? = null

        @SerializedName("device_token")
        var deviceToken: String? = null

        @SerializedName("device_type")
        var deviceType: String? = null

        @SerializedName("image")
        var image: String? = null

        @SerializedName("plan_expire_date")
        var planExpireDate: String? = null

        @SerializedName("below")
        var below: String? = null

        @SerializedName("athletes")
        var athletes: String? = null

        @SerializedName("baseline")
        var baseline: String? = null

        @SerializedName("fat_mass")
        var fatMass: String? = null

        @SerializedName("created_at")
        var createdAt: String? = null

        @SerializedName("updated_at")
        var updatedAt: String? = null

        @SerializedName("deleted_at")
        var deletedAt: String? = null

        @SerializedName("user_sports")
        var userSports: ArrayList<UserSportData>? = null
    }

    class UserSportData {

        @SerializedName("id")
        var id: Int? = null

        @SerializedName("user_id")
        var userId: String? = null

        @SerializedName("sport_id")
        var sportId: String? = null

        @SerializedName("created_at")
        var createdAt: String? = null

        @SerializedName("updated_at")
        var updatedAt: String? = null

        @SerializedName("sport")
        var sport: Sport? = null
    }

    class Sport {

        @SerializedName("id")
        var id: Int? = null

        @SerializedName("user_id")
        var userId: String? = null

        @SerializedName("title")
        var title: String? = null

        @SerializedName("created_at")
        var createdAt: String? = null

        @SerializedName("updated_at")
        var updatedAt: String? = null

        @SerializedName("deleted_at")
        var deletedAt: String? = null
    }
}
