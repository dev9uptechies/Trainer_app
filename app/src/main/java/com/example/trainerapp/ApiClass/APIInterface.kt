package com.example.trainerapp.ApiClass

import com.example.ChateData
import com.example.GroupChateListData
import com.example.GroupListData
import com.example.LessonData
import com.example.model.base_class.BaseClass
import com.example.model.base_class.PerformanceBase
import com.example.model.competition.CompetitionData
import com.example.model.competition.create.AddCompetitionBody
import com.example.model.newClass.ProgramBody
import com.example.model.newClass.athlete.AthleteData
import com.example.model.newClass.audio.Audio
import com.example.model.newClass.competition.Competition
import com.example.model.newClass.competition.GetCompetition
import com.example.model.newClass.cycle.AddTimerBody
import com.example.model.newClass.delete.DeleteBase
import com.example.model.newClass.excercise.Exercise
import com.example.model.newClass.lesson.Lesson
import com.example.model.newClass.timer.Timer
import com.example.model.performance_profile.PerformanceProfileData
import com.example.model.performance_profile.performance.category.PerformanceCategory
import com.example.model.performance_profile.performance.category.add_cat_response.PerformanceCategoryAdd
import com.example.model.performance_profile.performance.quality.PerformanceQuality
import com.example.model.performance_profile.performance.quality.add.AddQuality
import com.example.model.performance_profile.performance.quality.add_qual_response.PerformanceQualityAdd
import com.example.model.performance_profile.performance.quality.update.UpdateQuality
import com.example.model.performance_profile.template.CreateTemplate
import com.example.model.personal_diary.GetDiaryDataForEdit
import com.example.model.personal_diary.GetPersonalDiary
import com.example.model.personal_diary.GetPersonalDiaryData
import com.example.model.personal_diary.TrainingAssessment
import com.example.model.personal_diary.TrainingSession
import com.example.model.trainer_plan.TrainingPlanSubClass
import com.example.model.training_plan.MicroCycle.AbilityData
import com.example.model.training_plan.MicroCycle.AddAblilityClass
import com.example.model.training_plan.MicroCycle.AddMicrocycleCompatitive
import com.example.model.training_plan.MicroCycle.AddMicrocyclePreCompatitive
import com.example.model.training_plan.MicroCycle.AddMicrocyclePreSeason
import com.example.model.training_plan.MicroCycle.AddMicrocycleTransition
import com.example.model.training_plan.MicroCycle.GetMicrocycle
import com.example.model.training_plan.TrainingPlanData
import com.example.model.training_plan.cycles.AddMesocycleCompatitive
import com.example.model.training_plan.cycles.AddMesocyclePreCompatitive
import com.example.model.training_plan.cycles.AddMesocyclePresession
import com.example.model.training_plan.cycles.AddMesocycleTransition
import com.example.model.training_plan.cycles.GetMessocyclePreSession
import com.example.trainerapp.TestListData
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface APIInterface {

    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name: String?,
        @Field("email") email: String?,
        @Field("password") password: String?,
        @Field("role") role: String?
    ): Call<RegisterData>?

    @FormUrlEncoded
    @POST("register")
    fun registerathlete(
        @Field("name") name: String?,
        @Field("email") email: String?,
        @Field("password") password: String?,
        @Field("ref_code") ref_code: String?,
        @Field("birthdate") birthdate: String?,
        @Field("address") address: String?,
        @Field("zipcode") zipcode: String?,
        @Field("role") role: String?
    ): Call<RegisterData>?

    @FormUrlEncoded
    @POST("login")
    fun Logoin(
        @Field("email") email: String?,
        @Field("password") password: String?,
        @Field("role") role: String?
    ): Call<RegisterData>?

    @POST("sport/select")
    fun SelectSport(
        @Body string: JsonObject
    ): Call<RegisterData>?

    @POST("cycle")
    fun CreateCycle(
        @Body jsonArray: JsonArray
    ): Call<CycleData>?

    @PUT("cycle")
    fun EditCycle(
        @Body jsonArray: JsonArray
    ): Call<CycleData>?

    @Multipart
    @POST("exercise")
    fun CreateExercise(
        //@Part video: MultipartBody.Part? = null,
        @Part image: MultipartBody.Part? = null,
        @Part name: MultipartBody.Part? = null,
        @Part section_id: MultipartBody.Part? = null,
        @Part goal_id: MultipartBody.Part? = null,
        @Part type: MultipartBody.Part? = null,
        @Part category_id: MultipartBody.Part? = null,
        @Part equipment_ids: MultipartBody.Part? = null,
        @Part notes: MultipartBody.Part? = null,
        @Part video_link: MultipartBody.Part? = null
    ): Call<CycleData>?

    @Multipart
    @POST("group")
    fun AddGroup(
        @Part sport_id: MultipartBody.Part,
        @Part name: MultipartBody.Part,
        @Part image: MultipartBody.Part,
        @Part lession_ids: MultipartBody.Part,
        @Part athlete_ids: MultipartBody.Part,
        @Part event_ids: MultipartBody.Part,
        @Part planning_ids: MultipartBody.Part,
        @Part test_ids: MultipartBody.Part,
        @Part program_ids: MultipartBody.Part,
        @Part schedule: MultipartBody.Part
    ): Call<CycleData>?

    @Multipart
    @POST("favourite/program")
    fun Favourite_Program(
        @Part id: MultipartBody.Part
    ): Call<RegisterData>?

    @Multipart
    @POST("favourite/lesson")
    fun Favourite_lession(
        @Part id: MultipartBody.Part
    ): Call<RegisterData>?


    @GET("favourite/program")
    fun get_fav_program(
        @QueryMap param: Map<String, String>
    ): Call<LessonData>?

    @GET("group")
    fun GropList(): Call<GroupListData>?


    @GET("chat/group_chat")
    fun GropChateList(
    ): Call<GroupChateListData>?

    @GET("chat")
    fun GetChate(
        @QueryMap param: Map<String, String>
    ): Call<ChateData>?


    @GET("favourite/lesson")
    fun get_fav_lesson(): Call<LessonData>?

    @GET("favourite/test")
    fun get_fav_test(): Call<LessonData>?

    @GET("favourite/exercise")
    fun get_fav_exercise(): Call<LessonData>?

    @GET("favourite/event")
    fun get_fav_event(): Call<LessonData>?

    @Multipart
    @POST("favourite/test")
    fun Favourite_Test(
        @Part id: MultipartBody.Part
    ): Call<RegisterData>?

    @Multipart
    @POST("favourite/event")
    fun Favourite_Event(
        @Part id: MultipartBody.Part
    ): Call<RegisterData>?

    @Multipart
    @POST("favourite/exercise")
    fun Favourite_Exercise(
        @Part id: MultipartBody.Part
    ): Call<RegisterData>?

    @DELETE("favourite/exercise")
    fun DeleteFavourite_Exercise(@Query("id") id: Int?): Call<RegisterData>?

    @DELETE("favourite/event")
    fun DeleteFavourite_Event(@Query("id") id: Int?): Call<RegisterData>?

    @DELETE("favourite/test")
    fun DeleteFavourite_Test(@Query("id") id: Int?): Call<RegisterData>?

    @DELETE("favourite/program")
    fun DeleteFavourite_Program(@Query("id") id: Int?): Call<RegisterData>?

    @DELETE("favourite/lesson")
    fun DeleteFavourite_lession(@Query("id") id: Int?): Call<RegisterData>?

    @Multipart
    @POST("exercise")
    fun EditExercise(
        @Part _method: MultipartBody.Part,
        @Part id: MultipartBody.Part,
        @Part image: MultipartBody.Part,
        @Part video: MultipartBody.Part,
        @Part name: MultipartBody.Part,
        @Part section_id: MultipartBody.Part,
        @Part goal_id: MultipartBody.Part,
        @Part type: MultipartBody.Part,
        @Part category_id: MultipartBody.Part,
        @Part equipment_ids: MultipartBody.Part,
        @Part notes: MultipartBody.Part
    ): Call<CycleData>?


    @DELETE("test")
    fun DeleteTest(@Query("id") id: Int?): Call<RegisterData>?

    @DELETE("exercise")
    fun Deleteexercise(@Query("id") id: Int?): Call<RegisterData>?

    @DELETE("program")
    fun DeleteProgram(@Query("id") id: Int?): Call<RegisterData>?

    @DELETE("event")
    fun DeleteEvent(@Query("id") id: Int?): Call<RegisterData>?

    @DELETE("cycle")
    fun DeleteCycle(
        @Query("id") id: Int?,
        @Query("exercise_id") exercise_id: Int?
    ): Call<RegisterData>?

    @FormUrlEncoded
    @POST("sport/add")
    fun AddSport(
        @Field("title") title: String?
    ): Call<SportlistData>?

    @FormUrlEncoded
    @POST("forgot-password")
    fun ForgetPassword(
        @Field("email") title: String?
    ): Call<SportlistData>?

    @GET("sports")
    fun sportlist(): Call<SportlistData>?

    @GET("logout")
    fun LogOut(): Call<RegisterData>?

    @GET("date_event")
    fun GetDateEvent(@QueryMap param: Map<String, String>)
            : Call<JsonObject>?


    @GET("goals")
    fun GetGoal(): Call<TestListData>?

    @GET("sections")
    fun GetSection1(): Call<TestListData>?

    @GET("categories")
    fun GetCategories(): Call<CategoriesData>?

    @GET("exercise")
    fun GetExercise(): Call<ExcerciseData>?

    @GET("equipment")
    fun GetEquipment(): Call<CategoriesData>?

    @GET("sections")
    fun GetSection(): Call<CategoriesData>?

    @GET("goals")
    fun getGoal(): Call<CycleData>?


    @GET("athlete")
    fun AthleteList(): Call<AthleteDatalist>?

    @GET("profile")
    fun ProfileData(): Call<RegisterData>?

    @GET("planning")
    fun Get_Planning(): Call<PlanningData>?

    @FormUrlEncoded
    @POST("profile")
    fun EditProfile(
        @Field("name") name: String?,
        @Field("email") email: String?
    ): Call<RegisterData>?


    //new functions

    @Multipart
    @POST("exercise")
    fun CreateExercise1(
        @Query("name") name: String? = null,
        @Query("section_id") sectionId: String? = null,
        @Query("timer_id") timerId: String? = null,
        @Query("goal_id") goalId: String? = null,
        @Query("type") type: String? = null,
        @Query("category_id") categoryId: String? = null,
        @Query("notes") notes: String? = null,
        @Part equipment_ids: MultipartBody.Part? = null,
        @Part video: MultipartBody.Part? = null,
        @Part videoLink: MultipartBody.Part? = null,
        @Part thumbImage: MultipartBody.Part? = null,
    ): Call<Exercise>

    @Multipart
    @POST("exercise")
    fun EditExercise1(
        @Query("_method") method: String? = null,
        @Query("id") id: String? = null,
        @Query("name") name: String? = null,
        @Query("section_id") sectionId: String? = null,
        @Query("timer_id") timerId: String? = null,
        @Query("goal_id") goalId: String? = null,
        @Query("type") type: String? = null,
        @Query("category_id") categoryId: String? = null,
        @Query("notes") notes: String? = null,
        @Part equipment_ids: MultipartBody.Part? = null,
        @Part video: MultipartBody.Part? = null,
        @Part thumbImage: MultipartBody.Part? = null,
        @Part videoLink: MultipartBody.Part? = null,
    ): Call<Exercise>

    @GET("exercise")
    fun GetExerciseData(): Call<Exercise>

    @GET("categories")
    fun GetCategoriesData(): Call<TestListData>?

    @GET("timer")
    fun GetTimerData(): Call<Timer>

    @DELETE("timer_delete")
    fun DeleteTimer(@Query("id") id: Int?): Call<DeleteBase>

    @GET("audio")
    fun GetAudioData(): Call<Audio>

    @POST("timer")
    fun CreateTimerData(
        @Body addTimerBody: AddTimerBody
    ): Call<Timer>?

    @PUT("timer")
    fun UpdateTimerData(
        @Body addTimerBody: AddTimerBody
    ): Call<Timer>

    @PUT("timer")
    fun UpdateTimer(
        @Body editTimer: EditTimer
//        @Query("id") id: Int? = null,
//        @Query("name") name: String? = null,
//        @Query("audio_id") audio_id: Int? = null,
//        @Query("pause_time_audio_id") pause_time_audio_id: Int? = null,
//        @Query("pause_between_time_audio_id") pause_between_time_audio_id: Int? = null,
//        @Query("data") data: List<AddCycle>? = null
    ): Call<Timer>

    @DELETE("timer")
    fun DeleteTimerCycle(@Query("id") id: Int?): Call<DeleteBase>

    @GET("program")
    fun GetProgam(): Call<ProgramListData>?

    @POST("program")
    fun CreateProgram(
        @Body jsonObject: JsonObject
    ): Call<CycleData>?

    @POST("program/duplicate")
    fun DuplicateProgram(
        @Query("id") id: Int? = null
    ): Call<CycleData>

    @PUT("program")
    fun UpdateProgram(
        @Body programBody: ProgramBody
    ): Call<CycleData>

    @POST("athlete")
    fun GetAthleteList(
        @Query("name") name: String? = null
    ): Call<AthleteData>?

    @GET("test")
    fun GetTest(): Call<TestListData>?

    @PUT("test")
    fun EditTest(
        @Body jsonObject: JsonObject
    ): Call<RegisterData>?

    @POST("test")
    fun CreateTest(
        @Body jsonObject: JsonObject
    ): Call<RegisterData>?

    @GET("event")
    fun GetEvent(): Call<EventListData>?

    @POST("event")
    fun CreateEvent(
        @Body jsonObject: JsonObject
    ): Call<RegisterData>?

    @PUT("event")
    fun UpdateEvent(
        @Body jsonObject: JsonObject
    ): Call<RegisterData>?

    @GET("lesson")
    fun GetLession(): Call<LessonData>?

    @GET("lesson")
    fun GetLession1(): Call<Lesson>

    @POST("lesson")
    fun CreateLesson(
        @Body jsonObject: JsonObject
    ): Call<LessonData>?

    @PUT("lesson")
    fun EditLesson(
        @Body jsonObject: JsonObject
    ): Call<LessonData>?

    @Multipart
    @POST("lesson/duplicate")
    fun Duplicate_lession(
        @Part id: MultipartBody.Part
    ): Call<LessonData>?

    @DELETE("lesson")
    fun DeleteLession(@Query("id") id: Int?): Call<RegisterData>?

    @GET("competition_analysis_area")
    fun GetCompetitionArea(): Call<CompetitionData>

    @POST("competition_analysis/add")
    fun CreateCompetitionAnalysisData(
        @Body addBody: AddCompetitionBody
    ): Call<Competition>?

    @GET("competition_analysis_star")
    fun GetCompetitionAnalysisData(): Call<GetCompetition>

    @GET("performance_template")
    fun GetPerformanceTemplate(): Call<PerformanceProfileData>

    @POST("performance_template")
    fun CreatePerformanceTemplate(@Body createTemplate: CreateTemplate): Call<PerformanceBase>

    @DELETE("performance_template")
    fun DeletePerformanceTemplate(@Query("performance_template_id") id: Int?): Call<BaseClass>

    //get performance data
    @POST("performance_category")
    fun GetPerformanceCategory(@Query("athlete_id") id: Int?): Call<PerformanceCategory>

    @POST("performance_quality")
    fun GetPerformanceQuality(
        @Query("athlete_id") id: Int? = null,
        @Query("performance_category_id") performId: Int? = null
    ): Call<PerformanceQuality>

    //category
    @POST("performance_category/add")
    fun AddPerformanceCategory(
        @Query("athlete_id") id: Int? = null,
        @Query("name") name: String? = null
    ): Call<PerformanceCategoryAdd>

    @DELETE("performance_category")
    fun DeletePerformanceCategory(
        @Query("id") id: Int? = null,
        @Query("athlete_id") athId: Int? = null
    ): Call<BaseClass>

    @PUT("performance_category")
    fun EditPerformanceCategory(
        @Query("id") id: Int? = null,
        @Query("athlete_id") athId: Int? = null,
        @Query("name") name: String? = null
    ): Call<BaseClass>

    //quality
    @POST("performance_quality/add")
    fun AddPerformanceQuality(
        @Body addQuality: AddQuality
    ): Call<PerformanceQualityAdd>

    @PUT("performance_quality")
    fun EditPerformanceQuality(
        @Body updateQuality: UpdateQuality
    ): Call<PerformanceQualityAdd>

    @DELETE("performance_quality")
    fun DeletePerformanceQuality(
        @Query("id") id: Int? = null,
        @Query("athlete_id") athId: Int? = null
    ): Call<BaseClass>

    //training plan

    @GET("planning")
    fun GetTrainingPlan(): Call<TrainingPlanData>?

    @DELETE("planning")
    fun DeletePlanning(@Query("id") id: Int?): Call<TrainingPlanData>

    @POST("planning")
    fun CreatePlanning(@Body trainingPlanClass: TrainingPlanSubClass): Call<TrainingPlanData>?

    @POST("planning/duplicate")
    fun DuplicatePlanning(@Query("id") id: Int?): Call<TrainingPlanData>

    @PUT("planning")
    fun EditeTrainingPlan(@Body editeTrainingPlan: TrainingPlanSubClass): Call<TrainingPlanData>?

    //planning cycles

    @GET("pre_season/mesocycle")
    fun GetMesocyclePreSession(@Query("planning_ps_id") id: Int): Call<GetMessocyclePreSession>

    @GET("pre_competitive/mesocycle")
    fun GetMesocyclePreCompatitive(@Query("planning_pc_id") id: Int): Call<GetMessocyclePreSession>

    @GET("competitive/mesocycle")
    fun GetMesocycleCompatitive(@Query("planning_c_id") id: Int): Call<GetMessocyclePreSession>

    @GET("get_transition/mesocycle")
    fun GetMesocycleTransition(@Query("planning_t_id") id: Int): Call<GetMessocyclePreSession>

    //add planning cycles

    @POST("pre_season/mesocycle")
    fun AddMesocyclePreSeeion(@Body trainingPlanClass: List<AddMesocyclePresession>): Call<GetMessocyclePreSession>?

    @POST("pre_competitive/mesocycle")
    fun AddMesocyclePreComptitive(@Body trainingPlanClass: List<AddMesocyclePreCompatitive>): Call<GetMessocyclePreSession>?

    @POST("competitive/mesocycle")
    fun AddMesocycleComptitive(@Body trainingPlanClass: List<AddMesocycleCompatitive>): Call<GetMessocyclePreSession>?

    @POST("transition/mesocycle")
    fun AddMesocycleTrainsition(@Body trainingPlanClass: List<AddMesocycleTransition>): Call<GetMessocyclePreSession>?

    //edit planning cycles

    @PUT("pre_season/mesocycle")
    fun EditeMesocyclePresession(@Body editeTrainingPlan: List<AddMesocyclePresession>): Call<GetMessocyclePreSession>?

    @PUT("pre_competitive/mesocycle")
    fun EditeMesocyclePreCompatitive(@Body editeTrainingPlan: List<AddMesocyclePreCompatitive>): Call<GetMessocyclePreSession>?

    @PUT("competitive/mesocycle")
    fun EditeMesocycleCompatitive(@Body editeTrainingPlan: List<AddMesocycleCompatitive>): Call<GetMessocyclePreSession>?

    @PUT("transition/mesocycle")
    fun EditeMesocycleTransition(@Body editeTrainingPlan: List<AddMesocycleTransition>): Call<GetMessocyclePreSession>?

    //delete planning cycles
    @DELETE("pre_season/mesocycle")
    fun delete_PreSession(
        @Query("id") id: Int?,
        @Query("planning_ps_id") psid: Int?,
    ): Call<GetMessocyclePreSession>

    @DELETE("pre_competitive/mesocycle")
    fun delete_PreCompatitive(
        @Query("id") id: Int?,
        @Query("planning_pc_id") psid: Int?,
    ): Call<GetMessocyclePreSession>

    @DELETE("competitive/mesocycle")
    fun delete_Compatitive(
        @Query("id") id: Int?,
        @Query("planning_c_id") psid: Int?,
    ): Call<GetMessocyclePreSession>

    @DELETE("transition/mesocycle")
    fun delete_Transition(
        @Query("id") id: Int?,
        @Query("planning_t_id") psid: Int?,
    ): Call<GetMessocyclePreSession>

    @POST("abilitie")
    fun Create_Abilities(@Query("name") name: String?): Call<AbilityData>?

    @GET("abilitie")
    fun Get_Abilitiees(): Call<AddAblilityClass>?

    @GET("pre_season_microcycle")
    fun GetMicrocyclePreSeason(@Query("ps_mesocycle_id") id: Int): Call<GetMicrocycle>

    @GET("pre_competitive_microcycle")
    fun GetMicrocyclePreCompatitive(@Query("pc_mesocycle_id") id: Int): Call<GetMicrocycle>

    @GET("competitive_microcycle")
    fun GetMicrocycleCompatitive(@Query("c_mesocycle_id") id: Int): Call<GetMicrocycle>

    @GET("get_transition_microcycle")
    fun GetMicrocycleTransition(@Query("pt_mesocycle_id") id: Int): Call<GetMicrocycle>

    @POST("pre_season_microcycle")
    fun AddMicrocyclePreSeeion(@Body trainingPlanClass: List<AddMicrocyclePreSeason>): Call<GetMicrocycle>?

    @POST("pre_competitive_microcycle")
    fun AddMicrocyclePreCompatitive(@Body trainingPlanClass: MutableList<AddMicrocyclePreCompatitive>): Call<GetMicrocycle>?

    @POST("competitive_microcycle")
    fun AddMicrocycleCompatitive(@Body trainingPlanClass: List<AddMicrocycleCompatitive>): Call<GetMicrocycle>?

    @POST("transition_microcycle")
    fun AddMicrocycleTransition(@Body trainingPlanClass: List<AddMicrocycleTransition>): Call<GetMicrocycle>?


    @PUT("pre_season_microcycle")
    fun EditeMicrocyclePresession(@Body editeTrainingPlan: List<AddMicrocyclePreSeason>): Call<GetMicrocycle>?

    @PUT("pre_competitive_microcycle")
    fun EditeMicrocyclePreCompatitive(@Body editeTrainingPlan: List<AddMicrocyclePreCompatitive>): Call<GetMicrocycle>?

    @PUT("competitive_microcycle")
    fun EditeMicrocycleCompatitive(@Body trainingPlanClass: List<AddMicrocycleCompatitive>): Call<GetMicrocycle>?

    @POST("transition_microcycle")
    fun EditeMicrocycleTransition(@Body trainingPlanClass: List<AddMicrocycleTransition>): Call<GetMicrocycle>?


    @DELETE("pre_season_microcycle")
    fun delete_MicrocyclePreSession(
        @Query("id") id: Int?,
        @Query("ps_mesocycle_id") psid: Int?,
    ): Call<GetMicrocycle>


    @DELETE("pre_competitive_microcycle ")
    fun delete_MicrocyclePreCompatitive(
        @Query("id") id: Int?,
        @Query("pc_mesocycle_id") psid: Int?,
    ): Call<GetMicrocycle>

    @DELETE("competitive_microcycle")
    fun delete_MicrocycleCompatitive(
        @Query("id") id: Int?,
        @Query("c_mesocycle_id") psid: Int?,
    ): Call<GetMicrocycle>

    @DELETE("transition_microcycle")
    fun delete_MicrocycleTransition(
        @Query("id") id: Int?,
        @Query("pt_mesocycle_id") psid: Int?,
    ): Call<GetMicrocycle>

    @POST("personal_diary/add")
    fun AddPersonalDIaryData(@Body personalDiaryData: TrainingSession): Call<GetPersonalDiaryData>?

    @GET("personal_diary/list")
    fun GetPersonalDiaryListData(): Call<GetPersonalDiary>?

    @GET("personal_diary")
    fun GetPersonalDiaryData(@Query("date") date: String): Call<GetDiaryDataForEdit>?

}
