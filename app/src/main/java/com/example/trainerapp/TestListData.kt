package com.example.trainerapp

import com.google.gson.annotations.SerializedName

class TestListData {


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

        @SerializedName("goal")
        var goal: String? = null

        @SerializedName("unit")
        var unit: String? = null

        @SerializedName("date")
        var date: String? = null

        @SerializedName("test_repeat")
        var test_repeat: String? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("created_at")
        var created_at: String? = null

        @SerializedName("updated_at")
        var updated_at: String? = null

        @SerializedName("deleted_at")
        var deleted_at: String? = null

        @SerializedName("is_favourite")
        var is_favourite: Int? = null

        @SerializedName("test_athletes")
        var data: ArrayList<test_athletes>? = null

    }

    class test_athletes {

        @SerializedName("id")
        var id: Int? = null

        @SerializedName("test_id")
        var test_id: String? = null

        @SerializedName("athlete_id")
        var athlete_id: String? = null

        @SerializedName("result")
        var result: String? = null

        @SerializedName("created_at")
        var created_at: String? = null

        @SerializedName("updated_at")
        var is_login: String? = null

        @SerializedName("athlete")
        var athlete: Athlete? = null

    }

    class Athlete {

        @SerializedName("id")
        var id: Int? = null

        @SerializedName("coach_id")
        var coach_id: String? = null

        @SerializedName("sport_id")
        var sport_id: String? = null

        @SerializedName("plan_id")
        var plan_id: String? = null

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

        @SerializedName("is_login")
        var is_login: String? = null

        @SerializedName("device_token")
        var device_token: String? = null

        @SerializedName("device_type")
        var device_type: String? = null

        @SerializedName("image")
        var image: String? = null

        @SerializedName("plan_expire_date")
        var plan_expire_date: String? = null

        @SerializedName("below")
        var below: String? = null

        @SerializedName("athletes")
        var athletes: String? = null

        @SerializedName("baseline")
        var baseline: String? = null

        @SerializedName("fat_mass")
        var fat_mass: String? = null

        @SerializedName("created_at")
        var created_at: String? = null

        @SerializedName("updated_at")
        var updated_at: String? = null

        @SerializedName("deleted_at")
        var deleted_at: String? = null

    }
}