package com.example.model.newClass.athlete

import com.google.gson.annotations.SerializedName

class AthleteData {
    @SerializedName("message")
    val message: String? = null

    @SerializedName("status")
    val status: Boolean? = null

    @SerializedName("data")
    val data: ArrayList<Athlete>? = null

    class Athlete {
        @SerializedName("address")
        val address: String? = null

        @SerializedName("athletes")
        val athletes: String? = null

        @SerializedName("baseline")
        val baseline: String? = null

        @SerializedName("below")
        val below: String? = null

        @SerializedName("birthdate")
        val birthdate: String? = null

        @SerializedName("coach_id")
        val coach_id: String? = null

        @SerializedName("created_at")
        val created_at: String? = null

        @SerializedName("deleted_at")
        val deleted_at: String? = null

        @SerializedName("device_token")
        val device_token: String? = null

        @SerializedName("device_type")
        val device_type: String? = null

        @SerializedName("email")
        val email: String? = null

        @SerializedName("email_verified_at")
        val email_verified_at: String? = null

        @SerializedName("fat_mass")
        val fat_mass: String? = null

        @SerializedName("id")
        val id: Int? = null

        @SerializedName("image")
        val image: String? = null

        @SerializedName("is_login")
        val is_login: String? = null

        @SerializedName("name")
        val name: String? = null

        @SerializedName("plan_expire_date")
        val plan_expire_date: String? = null

        @SerializedName("plan_id")
        val plan_id: String? = null

        @SerializedName("ref_code")
        val ref_code: String? = null

        @SerializedName("ref_user_id")
        val ref_user_id: String? = null

        @SerializedName("sport_id")
        val sport_id: String? = null

        @SerializedName("updated_at")
        val updated_at: String? = null

        @SerializedName("zipcode")
        val zipcode: String? = null
    }
}