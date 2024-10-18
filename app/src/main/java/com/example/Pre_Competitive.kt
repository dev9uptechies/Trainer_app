package com.example

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Pre_Competitive(
    var start_date: String,
    var end_date: String,
    var mesocycle: String,
) : Parcelable
