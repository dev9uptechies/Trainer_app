package com.example.trainerapp.ApiClass

import com.example.GroupListData
import com.google.gson.annotations.SerializedName

class PlanningData {

    @SerializedName("status")
    var status: Boolean? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("data")
    var data: ArrayList<GroupListData.Planning>?= null

}
