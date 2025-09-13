package com.example.model.AthleteDataPackage

import com.example.trainerappAthlete.model.GroupListAthlete.Test_Athletes
import com.google.gson.annotations.SerializedName

class AthleteDetails {
    @SerializedName("status")
    val status: Boolean? = null

    @SerializedName("message")
    val message: String? = null

    @SerializedName("percentage")
    val percentage: Double? = null

    @SerializedName("data")
    val data: Athlete? = null // Changed to a single object instead of a list

    class Athlete {
        @SerializedName("id")
        val id: Int? = null

        @SerializedName("coach_id")
        val coachId: Int? = null

        @SerializedName("sport_id")
        val sportId: Int? = null

        @SerializedName("plan_id")
        val planId: Int? = null

        @SerializedName("name")
        val name: String? = null

        @SerializedName("email")
        val email: String? = null

        @SerializedName("email_verified_at")
        val emailVerifiedAt: String? = null

        @SerializedName("birthdate")
        val birthdate: String? = null

        @SerializedName("address")
        val address: String? = null

        @SerializedName("zipcode")
        val zipcode: String? = null

        @SerializedName("ref_code")
        val refCode: String? = null

        @SerializedName("device_token")
        val deviceToken: String? = null

        @SerializedName("device_type")
        val deviceType: String? = null

        @SerializedName("image")
        val image: String? = null

        @SerializedName("plan_expire_date")
        val planExpireDate: String? = null

        @SerializedName("user_sports")
        val userSports: List<UserSport>? = null

        @SerializedName("event_athletes")
        val eventAthletes: List<EventAthlete>? = null

        @SerializedName("test_athletes")
        val testAthletes: List<TestAthlete>? = null

        class UserSport {
            @SerializedName("id")
            val id: Int? = null

            @SerializedName("user_id")
            val userId: String? = null

            @SerializedName("sport_id")
            val sportId: String? = null

            @SerializedName("sport")
            val sport: Sport? = null

            class Sport {
                @SerializedName("id")
                val id: Int? = null

                @SerializedName("title")
                val title: String? = null
            }
        }

        class EventAthlete {
            @SerializedName("id")
            val id: Int? = null

            @SerializedName("event_id")
            val event_id: Int? = null

            @SerializedName("athlete_id")
            val athlete_id: Int? = null

            @SerializedName("created_at")
            val created_at: String? = null

            @SerializedName("updated_at")
            val updated_at: String? = null

            @SerializedName("event")
            val event: Event? = null

            @SerializedName("athlete")
            val athlete: Athlete? = null

            class Event {
                @SerializedName("id")
                val id: Int? = null

                @SerializedName("coach_id")
                val coach_id: Int? = null

                @SerializedName("title")
                val title: String? = null

                @SerializedName("type")
                val type: String? = null

                @SerializedName("sport_id")
                val sport_id: Int? = null

                @SerializedName("date")
                val date: String? = null

                @SerializedName("updated_at")
                val updated_at: String? = null

                @SerializedName("deleted_at")
                val deleted_at: String? = null

                @SerializedName("is_favourite")
                val is_favourite: String? = null

            }

            class Athlete {
                @SerializedName("id")
                val id: Int? = null

                @SerializedName("name")
                val name: String? = null
            }
        }

        class TestAthlete {

            @SerializedName("id")
            val id: Int? = null

            @SerializedName("test_id")
            val testId: String? = null

            @SerializedName("athlete_id")
            val athleteId: String? = null

            @SerializedName("result")
            val result: String? = null

            @SerializedName("created_at")
            val createdAt: String? = null

            @SerializedName("updated_at")
            val updatedAt: String? = null

            @SerializedName("test")
            val test: Test? = null

            @SerializedName("athlete")
            val athlete: Athlete? = null

            class Test {
                @SerializedName("id")
                val id: Int? = null

                @SerializedName("coach_id")
                val coach_id: Int? = null

                @SerializedName("title")
                val title: String? = null

                @SerializedName("goal")
                val goal: String? = null

                @SerializedName("unit")
                val unit: String? = null

                @SerializedName("date")
                val date: String? = null

                @SerializedName("test_repeat")
                val test_repeat: String? = null

                @SerializedName("created_at")
                val created_at: String? = null

                @SerializedName("updated_at")
                val updated_at: String? = null

                @SerializedName("deleted_at")
                val deleted_at: String? = null

                @SerializedName("is_favourite")
                val is_favourite: String? = null

                @SerializedName("test_athletes")
                var test_athletes: ArrayList<Test_Athletes>? = null

            }

            class Athlete {
                @SerializedName("id")
                val id: Int? = null

                @SerializedName("name")
                val name: String? = null
            }
        }
    }
}
