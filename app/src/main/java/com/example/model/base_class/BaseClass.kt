package com.example.model.base_class

import com.google.gson.annotations.SerializedName

class BaseClass {
    @SerializedName("message")
    val message: String? = null

    @SerializedName("status")
    val status: Boolean? = null

    @SerializedName("data")
    val data: Data? = null

    class Data {
        @SerializedName("id")
        val id: Int? = null
    }
}