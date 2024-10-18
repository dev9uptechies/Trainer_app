package com.example.model

import com.example.ChateData
import com.example.GroupListData
import com.google.gson.annotations.SerializedName

class Date_Event_model {
    @SerializedName("status")
    var status: Boolean? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("data")
    var data: Data? = null



    class Data {
        @SerializedName("count")
        var count: ArrayList<Count>? = null

        @SerializedName("list")
        var list: List? = null

    }

    class List{
        @SerializedName("lessons")
        var lessons: ArrayList<GroupListData.Lesson>? = null

        @SerializedName("events")
        var events: ArrayList<GroupListData.Event>? = null

        @SerializedName("programs")
        var programs: ArrayList<GroupListData.Program>? = null

        @SerializedName("tests")
        var tests: ArrayList<GroupListData.Test>? = null
    }


    class Count{

        @SerializedName("lessons")
        var lessons: Int? = null

        @SerializedName("events")
        var events: Int? = null

        @SerializedName("programs")
        var programs: Int? = null

        @SerializedName("tests")
        var tests: Int? = null

        @SerializedName("date")
        var date: String? = null

    }
}