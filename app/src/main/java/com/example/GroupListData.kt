package com.example

import com.example.model.newClass.lesson.LessonGoal
import com.example.model.newClass.lesson.LessonProgram
import com.example.trainerapp.ApiClass.ProgramListData
import com.google.gson.annotations.SerializedName
import org.jetbrains.annotations.Async.Schedule

class GroupListData {
    @SerializedName("status")
    var status: Boolean? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("data")
    var data: ArrayList<groupData>? = null

    class groupData {

        @SerializedName("id")
        var id: Int? = null

        @SerializedName("coach_id")
        var coach_id: String? = null

        @SerializedName("sport_id")
        var sport_id: String? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("image")
        var image: String? = null

        @SerializedName("cycles")
        var cycles: String? = null

        @SerializedName("days")
        var days: String? = null

        @SerializedName("schedule")
        var schedule: ArrayList<Schedule>? = null

        @SerializedName("qr_code")
        var qr_code: String? = null

        @SerializedName("created_at")
        var created_at: String? = null

        @SerializedName("updated_at")
        var updated_at: String? = null

        @SerializedName("group_events")
        var group_events: ArrayList<GroupEvents>? = null

        @SerializedName("group_lessions")
        var group_lessions: ArrayList<GroupLesson>? = null

        @SerializedName("group_members")
        var group_members: ArrayList<GroupMembar>? = null

        @SerializedName("group_tests")
        var group_tests: ArrayList<GroupTest>? = null

        @SerializedName("group_plannings")
        var group_plannings: ArrayList<GroupPlanning>? = null

        @SerializedName("group_programs")
        var group_programs: ArrayList<GroupProgram>? = null

        @SerializedName("sport")
        var sport: Sport? = null
    }

    class GroupEvents {

        @SerializedName("id")
        var id: Int? = null

        @SerializedName("group_id")
        var group_id: String? = null

        @SerializedName("event_id")
        var event_id: String? = null

        @SerializedName("created_at")
        var created_at: String? = null

        @SerializedName("updated_at")
        var updated_at: String? = null

        @SerializedName("event")
        var event: Event? = null

    }

    class Event {
        @SerializedName("id")
        var id: Int? = null

        @SerializedName("coach_id")
        var coach_id: String? = null

        @SerializedName("title")
        var title: String? = null

        @SerializedName("type")
        var type: String? = null

        @SerializedName("sport_id")
        var sport_id: String? = null

        @SerializedName("date")
        var date: String? = null

        @SerializedName("created_at")
        var created_at: String? = null

        @SerializedName("updated_at")
        var updated_at: String? = null

        @SerializedName("deleted_at")
        var deleted_at: String? = null

        @SerializedName("is_favourite")
        var is_favourite: Int? = null

        @SerializedName("event_athletes")
        var event_athletes: ArrayList<Event_Athletes>? = null
    }

    class Event_Athletes {
        @SerializedName("id")
        var id: Int? = null

        @SerializedName("event_id")
        var event_id: String? = null

        @SerializedName("athlete_id")
        var athlete_id: String? = null

        @SerializedName("created_at")
        var created_at: String? = null

        @SerializedName("updated_at")
        var updated_at: String? = null

        @SerializedName("athlete")
        var athlete: Athlete? = null

    }

    class GroupLesson {
        @SerializedName("id")
        var id: Int? = null

        @SerializedName("group_id")
        var group_id: String? = null

        @SerializedName("lession_id")
        var lession_id: String? = null

        @SerializedName("created_at")
        var created_at: String? = null

        @SerializedName("updated_at")
        var updated_at: String? = null

        @SerializedName("lession")
        var lession: Lesson? = null

//        @SerializedName("lession")
//        var lession: ArrayList<Lesson>? = null
    }


    class Schedule {
        @SerializedName("id")
        var id: Int? = null

        @SerializedName("group_id")
        var group_id: String? = null

        @SerializedName("day")
        var day: String? = null

        @SerializedName("start_time")
        var start_time: String? = null

        @SerializedName("end_time")
        var end_time: String? = null

        @SerializedName("created_at")
        var created_at: String? = null

        @SerializedName("updated_at")
        var updated_at: String? = null

        @SerializedName("deleted_at")
        var deleted_at: String? = null


    }

    class Lesson {
        @SerializedName("id")
        var id: Int? = null

        @SerializedName("coach_id")
        var coach_id: String? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("time")
        var time: String? = null

        @SerializedName("section_time")
        var section_time: String? = null

        @SerializedName("section_id")
        var section_id: String? = null

        @SerializedName("date")
        var date: String? = null

        @SerializedName("created_at")
        var created_at: String? = null

        @SerializedName("updated_at")
        var updated_at: String? = null

        @SerializedName("deleted_at")
        var deleted_at: String? = null

        @SerializedName("is_favourite")
        var is_favourite: Int? = null


        @SerializedName("lesson_goal")
        var lesson_goal: ArrayList<LessonGoal>? = null

        @SerializedName("lesson_programs")
        var lesson_programs: ArrayList<LessonProgram>? = null


    }

    class Goal {

        @SerializedName("id")
        var id: Int? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("created_at")
        var created_at: String? = null

        @SerializedName("updated_at")
        var updated_at: String? = null

    }

    class GroupMembar {
        @SerializedName("id")
        var id: Int? = null

        @SerializedName("group_id")
        var group_id: String? = null

        @SerializedName("athlete_id")
        var athlete_id: String? = null

        @SerializedName("created_at")
        var created_at: String? = null

        @SerializedName("updated_at")
        var updated_at: String? = null

        @SerializedName("athlete")
        var athlete: Athlete? = null
    }

    class Athlete {

        @SerializedName("id")
        var id: Int? = null

        @SerializedName("coach_id")
        var coach_id: String? = null

        @SerializedName("sport_id")
        var sport_id: String? = null

        @SerializedName("plan_id")
        var plan_id: String? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("email")
        var email: String? = null

        @SerializedName("email_verified_at")
        var email_verified_at: String? = null

        @SerializedName("birthdate")
        var birthdate: String? = null

        @SerializedName("address")
        var address: String? = null

        @SerializedName("zipcode")
        var zipcode: String? = null

        @SerializedName("ref_code")
        var ref_code: String? = null

        @SerializedName("device_token")
        var device_token: String? = null

        @SerializedName("device_type")
        var device_type: String? = null

        @SerializedName("ref_user_id")
        var ref_user_id: String? = null

        @SerializedName("image")
        var image: String? = null

        @SerializedName("plan_expire_date")
        var plan_expire_date: String? = null

        @SerializedName("below")
        var below: String? = null

        @SerializedName("created_at")
        var created_at: String? = null

        @SerializedName("updated_at")
        var updated_at: String? = null

        @SerializedName("deleted_at")
        var deleted_at: String? = null

        @SerializedName("is_login")
        var is_login: String? = null

        @SerializedName("baseline")
        var baseline: String? = null

        @SerializedName("fat_mass")
        var fat_mass: String? = null

        @SerializedName("athletes")
        var athletes: String? = null

    }

    class GroupTest {
        @SerializedName("id")
        var id: Int? = null

        @SerializedName("group_id")
        var group_id: String? = null

        @SerializedName("test_id")
        var test_id: String? = null

        @SerializedName("created_at")
        var created_at: String? = null

        @SerializedName("updated_at")
        var updated_at: String? = null

        @SerializedName("test")
        var test: Test? = null
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

        @SerializedName("date")
        var date: String? = null

        @SerializedName("is_favourite")
        var is_favourite: Int? = null

        @SerializedName("test_athletes")
        var test_athletes: ArrayList<Test_Athletes>? = null

    }

    class Test_Athletes {
        @SerializedName("id")
        var id: Int? = null

        @SerializedName("test_id")
        var test_id: String? = null

        @SerializedName("athlete_id")
        var athlete_id: String? = null

        @SerializedName("created_at")
        var created_at: String? = null

        @SerializedName("updated_at")
        var updated_at: String? = null

        @SerializedName("athlete")
        var athlete: Athlete? = null
    }

    class GroupPlanning {
        @SerializedName("id")
        var id: Int? = null

        @SerializedName("group_id")
        var group_id: String? = null

        @SerializedName("planning_id")
        var planning_id: String? = null

        @SerializedName("created_at")
        var created_at: String? = null

        @SerializedName("updated_at")
        var updated_at: String? = null

        @SerializedName("planning")
        var planning: Planning? = null
    }

    class Planning {
        @SerializedName("id")
        var id: Int? = null

        @SerializedName("coach_id")
        var coach_id: String? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("start_date")
        var start_date: String? = null

        @SerializedName("competition_date")
        var competition_date: String? = null

        @SerializedName("mesocycle")
        var mesocycle: String? = null

        @SerializedName("created_at")
        var created_at: String? = null

        @SerializedName("updated_at")
        var updated_at: String? = null

        @SerializedName("pre_season")
        var pre_season: Pre_Season? = null

        @SerializedName("pre_competitive")
        var pre_competitive: Pre_Competitive? = null

        @SerializedName("competitive")
        var competitive: Competitive? = null

    }

    class Pre_Season {
        @SerializedName("id")
        var id: Int? = null

        @SerializedName("planning_id")
        var planning_id: String? = null

        @SerializedName("mesocycle")
        var mesocycle: String? = null

        @SerializedName("start_date")
        var start_date: String? = null

        @SerializedName("end_date")
        var end_date: String? = null

        @SerializedName("mesocycles")
        var mesocycles: ArrayList<Mesocycles>? = null
    }

    class Mesocycles {
        @SerializedName("id")
        var id: Int? = null

        @SerializedName("planning_ps_id")
        var planning_ps_id: String? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("start_date")
        var start_date: String? = null

        @SerializedName("end_date")
        var end_date: String? = null

        @SerializedName("periods")
        var periods: String? = null
    }

    class Pre_Competitive {
        @SerializedName("id")
        var id: Int? = null

        @SerializedName("planning_id")
        var planning_id: String? = null

        @SerializedName("mesocycle")
        var mesocycle: String? = null

        @SerializedName("start_date")
        var start_date: String? = null

        @SerializedName("end_date")
        var end_date: String? = null

        @SerializedName("mesocycles")
        var mesocycles: ArrayList<PreMesocycles>? = null
    }

    class PreMesocycles {
        @SerializedName("id")
        var id: Int? = null

        @SerializedName("planning_pc_id")
        var planning_pc_id: String? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("start_date")
        var start_date: String? = null

        @SerializedName("end_date")
        var end_date: String? = null

        @SerializedName("periods")
        var periods: String? = null
    }

    class Competitive {
        @SerializedName("id")
        var id: Int? = null

        @SerializedName("planning_id")
        var planning_id: String? = null

        @SerializedName("mesocycle")
        var mesocycle: String? = null

        @SerializedName("start_date")
        var start_date: String? = null

        @SerializedName("end_date")
        var end_date: String? = null

        @SerializedName("mesocycles")
        var mesocycles: ArrayList<ComMesocycles>? = null
    }

    class ComMesocycles {
        @SerializedName("id")
        var id: Int? = null

        @SerializedName("planning_c_id")
        var planning_pc_id: String? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("start_date")
        var start_date: String? = null

        @SerializedName("end_date")
        var end_date: String? = null

        @SerializedName("periods")
        var periods: String? = null
    }

    class GroupProgram {
        @SerializedName("id")
        var id: Int? = null

        @SerializedName("group_id")
        var group_id: String? = null

        @SerializedName("program_id")
        var program_id: String? = null

        @SerializedName("created_at")
        var created_at: String? = null

        @SerializedName("updated_at")
        var updated_at: String? = null

        @SerializedName("program")
        var program: Program? = null
    }

    class Program {

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

        @SerializedName("section_id")
        var section_id: String? = null

        @SerializedName("date")
        var date: String? = null

        @SerializedName("created_at")
        var created_at: String? = null

        @SerializedName("updated_at")
        var updated_at: String? = null

        @SerializedName("is_favourite")
        var is_favourite: Int? = null

        @SerializedName("goal")
        var goal: ProgramListData.Goal? = null

        @SerializedName("section")
        var section: ProgramListData.Section? = null

        @SerializedName("program_exercises")
        var program_exercises: ArrayList<Program_Exercise>? = null

    }

    class Program_Exercise {

        @SerializedName("id")
        var id: Int? = null

        @SerializedName("program_id")
        var program_id: String? = null

        @SerializedName("exercise_id")
        var exercise_id: String? = null

        @SerializedName("created_at")
        var created_at: String? = null

        @SerializedName("updated_at")
        var updated_at: String? = null

    }

    class Sport {
        @SerializedName("id")
        var id: Int? = null

        @SerializedName("user_id")
        var user_id: String? = null

        @SerializedName("title")
        var title: String? = null

        @SerializedName("created_at")
        var created_at: String? = null

        @SerializedName("updated_at")
        var updated_at: String? = null
    }
}

