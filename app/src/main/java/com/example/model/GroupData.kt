package com.example.model

data class GroupListData(
    val groupDatas: List<groupData> // Assuming groupData is the name of the nested class
) {
    data class groupData(
        val groupName: String,
        val schedule: List<ScheduleData>?
    )
}

data class ScheduleData(
    val day: String?,
    val start_time: String?,
    val end_time: String?
)
