package com.example

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Group_Data_List(
    var name: String,
    var start_date: String,
    var competition_date: String,
    var mesocycle: String,
    var pre_season: Pre_season,
    var pre_competitive: Pre_Competitive,
    var competitive: Competitive
) : Parcelable