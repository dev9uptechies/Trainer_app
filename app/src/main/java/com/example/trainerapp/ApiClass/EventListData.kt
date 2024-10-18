package com.example.trainerapp.ApiClass

import com.google.gson.annotations.SerializedName

class EventListData {

    @SerializedName("status")
    var status: Boolean? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("data")
    var data: ArrayList<testData>? = null

    class testData {

        @SerializedName("id")
        var id: Int? = null

        @SerializedName("coach_id")
        var coach_id: String? = null

        @SerializedName("title")
        var title: String? = null

        @SerializedName("type")
        var type: String? = null

        @SerializedName("sport_id")
        var sport_id: String? = null

        @SerializedName("date")
        var date: String? = null

        @SerializedName("created_at")
        var created_at: String? = null

        @SerializedName("updated_at")
        var updated_at: String? = null

        @SerializedName("deleted_at")
        var deleted_at: String? = null

        @SerializedName("is_favourite")
        var is_favourite: String? = null

        @SerializedName("event_athletes")
        var event_athletes: ArrayList<test_athletes>? = null

    }

    class test_athletes {

        @SerializedName("id")
        var id: Int? = null

        @SerializedName("event_id")
        var event_id: String? = null

        @SerializedName("athlete_id")
        var athlete_id: String? = null

        @SerializedName("created_at")
        var created_at: String? = null

        @SerializedName("updated_at")
        var is_login: String? = null

        @SerializedName("athlete")
        var athlete: Athlete? = null

    }

    class Athlete {

        @SerializedName("id")
        val id: Int? = null

        @SerializedName("coach_id")
        val coach_id: String? = null

        @SerializedName("sport_id")
        val sport_id: String? = null

        @SerializedName("plan_id")
        val plan_id: String? = null

        @SerializedName("name")
        val name: String? = null

        @SerializedName("email")
        val email: String? = null

        @SerializedName("email_verified_at")
        val email_verified_at: String? = null

        @SerializedName("birthdate")
        val birthdate: String? = null

        @SerializedName("address")
        val address: String? = null

        @SerializedName("zipcode")
        val zipcode: String? = null

        @SerializedName("ref_code")
        val ref_code: String? = null

        @SerializedName("ref_user_id")
        val ref_user_id: String? = null

        @SerializedName("is_login")
        val is_login: String? = null

        @SerializedName("device_token")
        val device_token: String? = null

        @SerializedName("device_type")
        val device_type: String? = null

        @SerializedName("image")
        val image: String? = null

        @SerializedName("plan_expire_date")
        val plan_expire_date: String? = null

        @SerializedName("below")
        val below: String? = null

        @SerializedName("athletes")
        val athletes: String? = null

        @SerializedName("baseline")
        val baseline: String? = null

        @SerializedName("fat_mass")
        val fat_mass: String? = null

        @SerializedName("created_at")
        val created_at: String? = null

        @SerializedName("deleted_at")
        val deleted_at: String? = null
        
        @SerializedName("updated_at")
        val updated_at: String? = null

    }
}
