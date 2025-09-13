package com.example

import com.example.trainerapp.ApiClass.ProgramListData
import com.google.gson.annotations.SerializedName

class GroupChateListData {
    @SerializedName("status")
    var status: Boolean? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("data")
    var data: ArrayList<groupData>? = null

    class groupData {

        @SerializedName("id")
        var id: Int? = null

        @SerializedName("coach_id")
        var coach_id: String? = null

        @SerializedName("sport_id")
        var sport_id: String? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("image")
        var image : String? = null

        @SerializedName("cycles")
        var cycles: String? = null

        @SerializedName("days")
        var days: String? = null

        @SerializedName("schedule")
        var schedule: String? = null

        @SerializedName("qr_code")
        var qr_code: String? = null

        @SerializedName("created_at")
        var created_at: String? = null

        @SerializedName("updated_at")
        var updated_at: String? = null

        @SerializedName("group_members")
        var group_members: ArrayList<GroupMembers>? = null

        @SerializedName("last_message")
        var last_message: LastMessage? = null
    }

    class LastMessage{
        @SerializedName("id")
        var id: Int? = null

        @SerializedName("group_id")
        var group_id: String? = null

        @SerializedName("sender_id")
        var sender_id: String? = null

        @SerializedName("message")
        var message: String? = null

        @SerializedName("created_at")
        var created_at: String? = null

        @SerializedName("updated_at")
        var updated_at: String? = null

        @SerializedName("sender")
        var sender: Sender? = null

    }

    class Sender{
        @SerializedName("name")
        var name: String? = null
    }

    class GroupMembers {

        @SerializedName("id")
        var id: Int? = null

        @SerializedName("group_id")
        var group_id: String? = null

        @SerializedName("athlete_id")
        var athlete_id: String? = null

        @SerializedName("created_at")
        var created_at: String? = null

        @SerializedName("updated_at")
        var updated_at: String? = null

    }
}

