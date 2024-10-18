package com.example.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Ecercise_data_list(
    var id: String,
    var coach_id: String,
    var name: String,
    var goal_id: String,
    var time: String,
    var image: String,
) : Parcelable