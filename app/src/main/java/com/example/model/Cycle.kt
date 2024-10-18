package com.example.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Cycle(
    var id: String,
    var exercise_id: String,
    var set: String,
    var time: String,
    var reps: String,
    var pause: String,
    var weight: String,
    var distance: String,
    var pause_cycle: String
) : Parcelable