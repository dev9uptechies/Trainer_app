package com.example.model.newClass.cycle

import com.google.gson.annotations.SerializedName

class AddCycle(
    @SerializedName("id")
    var id: Int? = null,
    @SerializedName("set")
    var set: String? = null,
    @SerializedName("time")
    var time: String? = null,
    @SerializedName("reps")
    var reps: String? = null,
    @SerializedName("pause")
    var pause: String? = null,
    @SerializedName("weight")
    var weight: String? = null,
    @SerializedName("distance")
    var distance: String? = null,
    @SerializedName("pause_timer")
    var pause_timer: String? = null
)