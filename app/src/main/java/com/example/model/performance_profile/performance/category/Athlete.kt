package com.example.model.performance_profile.performance.category

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Athlete(
    @SerializedName("address") val address: String,
    @SerializedName("athletes") val athletes: String,
    @SerializedName("baseline") val baseline: String,
    @SerializedName("below") val below: String,
    @SerializedName("birthdate") val birthdate: String,
    @SerializedName("coach_id") val coach_id: String,
    @SerializedName("created_at") val created_at: String,
    @SerializedName("deleted_at") val deleted_at: String,
    @SerializedName("device_token") val device_token: String,
    @SerializedName("device_type") val device_type: String,
    @SerializedName("email") val email: String,
    @SerializedName("email_verified_at") val email_verified_at: String,
    @SerializedName("fat_mass") val fat_mass: String,
    @SerializedName("id") val id: Int,
    @SerializedName("image") val image: String,
    @SerializedName("is_login") val is_login: String,
    @SerializedName("name") val name: String,
    @SerializedName("plan_expire_date") val plan_expire_date: String,
    @SerializedName("plan_id") val plan_id: String,
    @SerializedName("ref_code") val ref_code: String,
    @SerializedName("ref_user_id") val ref_user_id: String,
    @SerializedName("sport_id") val sport_id: String,
    @SerializedName("updated_at") val updated_at: String,
    @SerializedName("zipcode") val zipcode: String
) : Parcelable