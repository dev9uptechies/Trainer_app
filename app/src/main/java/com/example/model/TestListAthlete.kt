package com.example.model

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class TestListAthlete {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("coach_id")
    @Expose
    var coachId: Any? = null

    @SerializedName("sport_id")
    @Expose
    var sportId: Any? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("email")
    @Expose
    var email: String? = null

    @SerializedName("email_verified_at")
    @Expose
    var emailVerifiedAt: Any? = null

    @SerializedName("birthdate")
    @Expose
    var birthdate: String? = null

    @SerializedName("address")
    @Expose
    var address: String? = null

    @SerializedName("zipcode")
    @Expose
    var zipcode: String? = null

    @SerializedName("ref_code")
    @Expose
    var refCode: String? = null

    @SerializedName("ref_user_id")
    @Expose
    var refUserId: Any? = null

    @SerializedName("is_login")
    @Expose
    var isLogin: String? = null

    @SerializedName("device_token")
    @Expose
    var deviceToken: Any? = null

    @SerializedName("device_type")
    @Expose
    var deviceType: Any? = null

    @SerializedName("image")
    @Expose
    var image: Any? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

    @SerializedName("deleted_at")
    @Expose
    var deletedAt: Any? = null

    /**
     * No args constructor for use in serialization
     *
     */
    constructor()

    /**
     *
     * @param deviceType
     * @param image
     * @param refUserId
     * @param birthdate
     * @param address
     * @param coachId
     * @param deviceToken
     * @param zipcode
     * @param isLogin
     * @param createdAt
     * @param sportId
     * @param deletedAt
     * @param emailVerifiedAt
     * @param name
     * @param id
     * @param refCode
     * @param email
     * @param updatedAt
     */
    constructor(
        id: Int?,
        coachId: Any?,
        sportId: Any?,
        name: String?,
        email: String?,
        emailVerifiedAt: Any?,
        birthdate: String?,
        address: String?,
        zipcode: String?,
        refCode: String?,
        refUserId: Any?,
        isLogin: String?,
        deviceToken: Any?,
        deviceType: Any?,
        image: Any?,
        createdAt: String?,
        updatedAt: String?,
        deletedAt: Any?
    ) : super() {
        this.id = id
        this.coachId = coachId
        this.sportId = sportId
        this.name = name
        this.email = email
        this.emailVerifiedAt = emailVerifiedAt
        this.birthdate = birthdate
        this.address = address
        this.zipcode = zipcode
        this.refCode = refCode
        this.refUserId = refUserId
        this.isLogin = isLogin
        this.deviceToken = deviceToken
        this.deviceType = deviceType
        this.image = image
        this.createdAt = createdAt
        this.updatedAt = updatedAt
        this.deletedAt = deletedAt
    }
}