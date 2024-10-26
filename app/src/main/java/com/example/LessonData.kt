package com.example

import com.example.model.newClass.lesson.Goal
import com.example.trainerapp.ApiClass.ProgramListData
import com.google.gson.annotations.SerializedName

class LessonData {
    @SerializedName("status")
    var status: Boolean? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("data")
    var data: ArrayList<lessionData>? = null

    class lessionData {

        @SerializedName("id")
        var id: Int? = null

        @SerializedName("coach_id")
        var coach_id: String? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("lesson_id")
        var lesson_id: String? = null

        @SerializedName("user_id")
        var user_id: String? = null


        @SerializedName("goal_id")
        var goal_id: Int? = null

        @SerializedName("time")
        var time: String? = null

        @SerializedName("section_time")
        var section_time: String? = null

        @SerializedName("section_id")
        var section_id: String? = null

        @SerializedName("date")
        var date: String? = null

        @SerializedName("is_favourite")
        var is_favourite: String? = null

        @SerializedName("updated_at")
        var updated_at: String? = null

        @SerializedName("lesson_goal")
        var goal: ArrayList<Goals>? = null

        @SerializedName("lesson_programs")
        var lesson_programs: ArrayList<Lesson_Programs>? = null

        @SerializedName("section")
        var section: Section? = null

        @SerializedName("lesson")
        var lesson: Lesson? = null

        @SerializedName("event")
        var event: Test? = null

        @SerializedName("test")
        var test: Test? = null

        @SerializedName("exercise")
        var exercise: Lesson? = null

        @SerializedName("program")
        var program: ProgramListData.testData? = null
    }

    class Lesson_Programs {

        @SerializedName("id")
        var id: Int? = null

        @SerializedName("program_id")
        var program_id: String? = null

        @SerializedName("lesson_id")
        var lesson_id: String? = null

        @SerializedName("created_at")
        var created_at: String? = null

        @SerializedName("updated_at")
        var updated_at: String? = null

        @SerializedName("program")
        var program: ProgramListData.testData? = null

    }

    class Test {

        @SerializedName("id")
        var id: Int? = null

        @SerializedName("title")
        var title: String? = null

        @SerializedName("coach_id")
        var coach_id: String? = null

        @SerializedName("goal")
        var goal: String? = null

        @SerializedName("unit")
        var unit: String? = null
    }

    class Lesson {

        @SerializedName("id")
        var id: Int? = null

        @SerializedName("coach_id")
        var coach_id: String? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("goal_id")
        var goal_id: String? = null

        @SerializedName("time")
        var time: String? = null

        @SerializedName("section_time")
        var section_time: String? = null

        @SerializedName("section_id")
        var section_id: String? = null

        @SerializedName("is_favourite")
        var is_favourite: Int? = null

        @SerializedName("goal")
        var goal: Goals? = null

    }


    class Section {

        @SerializedName("id")
        var id: Int? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("created_at")
        var created_at: String? = null

        @SerializedName("updated_at")
        var is_login: String? = null

    }

    class Goals {

        @SerializedName("id")
        var id: Int? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("created_at")
        var created_at: String? = null

        @SerializedName("updated_at")
        var updated_at: String? = null

        @SerializedName("goal")
        var goal: Goal? = null

    }


    class Athlete {

        @SerializedName("id")
        var id: Int? = null

        @SerializedName("coach_id")
        var coach_id: String? = null

        @SerializedName("sport_id")
        var sport_id: String? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("email")
        var email: String? = null

        @SerializedName("birthdate")
        var birthdate: String? = null

        @SerializedName("created_at")
        var created_at: String? = null

        @SerializedName("updated_at")
        var updated_at: String? = null

        @SerializedName("is_login")
        var is_login: String? = null

    }
}
