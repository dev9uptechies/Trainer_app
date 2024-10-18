package com.example.model.newClass.competition

import com.example.trainerapp.ApiClass.EventListData
import com.example.trainerapp.TestListData
import com.google.gson.annotations.SerializedName

class Competition {
    @SerializedName("data")
    val data: CompetitionData? = null

    @SerializedName("message")
    val message: String? = null

    @SerializedName("status")
    val status: Boolean? = null

    class CompetitionData {
        @SerializedName("id")
        val id: Int? = null

        @SerializedName("coach_id")
        val coach_id: String? = null

        @SerializedName("athlete_id")
        val athlete_id: String? = null

        @SerializedName("event_id")
        val event_id: String? = null

        @SerializedName("competition_analysis_area_id")
        val competition_analysis_area_id: String? = null

        @SerializedName("category")
        val category: String? = null

        @SerializedName("date")
        val date: String? = null

        @SerializedName("area")
        val area: String? = null

        @SerializedName("created_at")
        val created_at: String? = null

        @SerializedName("updated_at")
        val updated_at: String? = null

        @SerializedName("coach")
        val coach: Coach? = null

        @SerializedName("athlete")
        val athlete: TestListData.Athlete? = null

        @SerializedName("event")
        val event: EventListData.testData? = null

        @SerializedName("competition_analysis_area")
        val competition_analysis_area: CompetitionAnalysisArea? = null

        @SerializedName("competition_progress")
        val competition_progress: List<CompetitionProgress>? = null
    }

    class Coach {
        @SerializedName("id")
        val id: Int? = null

        @SerializedName("coach_id")
        val coach_id: String? = null

        @SerializedName("sport_id")
        val sport_id: String? = null

        @SerializedName("name")
        val name: String? = null

        @SerializedName("email")
        val email: String? = null

        @SerializedName("email_verified_at")
        val email_verified_at: String? = null

        @SerializedName("birthdate")
        val birthdate: String? = null

        @SerializedName("address")
        val address: String? = null

        @SerializedName("zipcode")
        val zipcode: String? = null

        @SerializedName("ref_code")
        val ref_code: String? = null

        @SerializedName("ref_user_id")
        val ref_user_id: String? = null

        @SerializedName("is_login")
        val is_login: Int? = null

        @SerializedName("device_token")
        val device_token: String? = null

        @SerializedName("device_type")
        val device_type: String? = null

        @SerializedName("plan_expire_date")
        val plan_expire_date: String? = null

        @SerializedName("below")
        val below: String? = null

        @SerializedName("athletes")
        val athletes: String? = null

        @SerializedName("baseline")
        val baseline: String? = null

        @SerializedName("fat_mass")
        val fat_mass: String? = null

        @SerializedName("created_at")
        val created_at: String? = null

        @SerializedName("updated_at")
        val updated_at: String? = null

        @SerializedName("deleted_at")
        val deleted_at: String? = null
    }

    class CompetitionAnalysisArea {
        @SerializedName("id")
        val id: Int? = null

        @SerializedName("title")
        val title: String? = null

        @SerializedName("created_at")
        val created_at: String? = null

        @SerializedName("updated_at")
        val updated_at: String? = null
    }

    class CompetitionProgress {
        @SerializedName("id")
        val id: Int? = null

        @SerializedName("competition_analysis_id")
        val competition_analysis_id: String? = null

        @SerializedName("title")
        val title: String? = null

        @SerializedName("coach_star")
        val coach_star: String? = null

        @SerializedName("athlete_star")
        val athlete_star: String? = null

        @SerializedName("created_at")
        val created_at: String? = null

        @SerializedName("updated_at")
        val updated_at: String? = null
    }
}


//        "competition_analysis_area": {
//            "id": 2,
//            "title": "Mental Area",
//            "created_at": "2023-04-18T11:14:55.000000Z",
//            "updated_at": "2023-04-18T11:14:55.000000Z"
//        },
//        "competition_progress": [
//            {
//                "id": 3212,
//                "competition_analysis_id": "505",
//                "title": "test1",
//                "coach_star": "1",
//                "athlete_star": null,
//                "created_at": "2024-09-30T05:18:47.000000Z",
//                "updated_at": "2024-09-30T05:18:47.000000Z"
//            },
//            {
//                "id": 3213,
//                "competition_analysis_id": "505",
//                "title": "test2",
//                "coach_star": "2",
//                "athlete_star": null,
//                "created_at": "2024-09-30T05:18:47.000000Z",
//                "updated_at": "2024-09-30T05:18:47.000000Z"
//            }
//        ]
//    }
//}