package com.example.model

data class DayTime(val id: String, var startTime: String, var endTime: String) {
    // Override toString method to customize the log output format
    override fun toString(): String {
        return "DayTime(id=$id, startTime=\"$startTime\", endTime=\"$endTime\")"
    }
}
