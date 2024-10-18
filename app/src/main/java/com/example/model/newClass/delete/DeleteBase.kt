package com.example.model.newClass.delete

import com.google.gson.annotations.SerializedName

class DeleteBase {
    @SerializedName("status")
    var status: Boolean? = null

    @SerializedName("message")
    var message: String? = null
}