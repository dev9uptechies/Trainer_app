package com.example.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Ecercise_list_data(
    var id: String,
    var coach_id: String,
    var name: String,
    var goal_id: String,
    var time: String,
    var image: String,
    var weight: String,
    var reps: String
) : Parcelable