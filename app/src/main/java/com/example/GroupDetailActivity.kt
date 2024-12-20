package com.example

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.EditGroupActivity
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.ActivityGroupDetailBinding
import com.example.trainerapp.training_plan.ViewTrainingPlanActivity
import com.makeramen.roundedimageview.RoundedTransformationBuilder
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GroupDetailActivity : AppCompatActivity(), OnItemClickListener.OnItemClickCallback {

    private lateinit var groupDetailBinding: ActivityGroupDetailBinding
    private var position: Int? = null
    private var group_id: Int? = null
    private var group_ids: Int? = null
    val planningIdList = mutableListOf<String>()

    var planningId: IntArray = intArrayOf()  // Initialize as an empty array or a default value
    private lateinit var apiInterface: APIInterface
    private lateinit var apiClient: APIClient
    private lateinit var adapter: PlanningAdapter
    private lateinit var lessonAdapter: LessonAdapter
    private lateinit var testAdapter: TestAdapter
    private lateinit var eventAdapter: EventAdapter
    private lateinit var athleteAdapter: AthleteAdapter


    private var groupListCall: Call<GroupListData>? = null


    override fun onResume() {
        checkUser()
        super.onResume()
    }

    private fun checkUser() {
        try {
            apiInterface.ProfileData()?.enqueue(object : Callback<RegisterData?> {
                override fun onResponse(
                    call: Call<RegisterData?>,
                    response: Response<RegisterData?>
                ) {
                    Log.d("TAG", response.code().toString() + "")
                    val code = response.code()
                    if (code == 200) {
                        loadData()
                        Log.d("Get Profile Data ", "${response.body()}")
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@GroupDetailActivity)
                    } else {
                        Toast.makeText(
                            this@GroupDetailActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Toast.makeText(
                        this@GroupDetailActivity,
                        "" + t.message,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    call.cancel()
                }
            })
        } catch (e: Exception) {
            Log.d("Exception", "${e.message}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        groupDetailBinding = ActivityGroupDetailBinding.inflate(layoutInflater)
        setContentView(groupDetailBinding.root)

        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)

        loadData()


        position = intent.getIntExtra("position", 0)
        group_id = intent.getIntExtra("group_id", 0)
        group_ids = intent.getIntExtra("id", 0)
        Log.d("group_id", "onCreate: $group_id")

        if (group_id == 0) {
            group_id = group_ids
            Log.d("GROUPIDD", "onCreate: $group_id")
        }


        groupDetailBinding.deleteGroup.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Delete Group")
                .setMessage("Are you sure you want to delete this group?")
                .setPositiveButton("Yes") { dialog, _ ->
                    deleteGroup(group_id!!)
                    finish()
                    dialog.dismiss()
                    loadData()

                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        val planningIdArray = planningIdList.mapNotNull { it.toIntOrNull() }.toIntArray()

        Log.d("VBVBBVBVBVBVB", "onCreate: $planningIdList")

        Log.d("FVVFVFV", "Sending planningIds: ${planningIdArray.joinToString()}")

        groupDetailBinding.cardView4.setOnClickListener {
            val intent = Intent(this, EditGroupActivity::class.java)
            intent.putExtra("id", group_id)
            startActivity(intent)
            finish()
        }


        groupDetailBinding.imgBack.setOnClickListener {
            finish()
        }
//        callGroupApi()
    }


    private fun loadData() {
        callGroupApi()
    }

    private fun deleteGroup(groupId: Int) {
        // Perform the deletion API call
        apiInterface.DeleteGroup(groupId)!!.enqueue(object : Callback<GroupListData> {
            override fun onResponse(call: Call<GroupListData>, response: Response<GroupListData>) {
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@GroupDetailActivity,
                        "Group deleted successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    loadData()  // Re-fetch the group data after deletion
                } else {
                    Toast.makeText(
                        this@GroupDetailActivity,
                        "Failed to delete group",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<GroupListData>, t: Throwable) {
                Toast.makeText(this@GroupDetailActivity, "Error deleting group", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun callGroupApi() {
        groupDetailBinding.progressDetail.visibility = View.VISIBLE

        groupListCall = apiInterface.GropList()
        groupListCall?.enqueue(object : Callback<GroupListData?> {
            override fun onResponse(
                call: Call<GroupListData?>,
                response: Response<GroupListData?>
            ) {
                if (isFinishing || isDestroyed) return

                groupDetailBinding.progressDetail.visibility = View.GONE
                if (response.isSuccessful) {
                    val resource = response.body()
                    if (resource?.status == true) {
                        val position = position ?: return
                        resource.data?.getOrNull(position)?.let { data ->
                            groupDetailBinding.tvGroupName.text = data.name
                            groupDetailBinding.tvMember.text =
                                "${data.group_members?.size ?: 0} Members"

                            data.group_plannings?.forEach { groupPlanning ->
                                groupPlanning.planning_id?.let {
                                    planningIdList.add(it)
                                }
                            }

                            Log.d("FVVFVFV", "Planning IDs: ${planningIdList.joinToString()}")
                            val transformation: Transformation = RoundedTransformationBuilder()
                                .borderColor(Color.WHITE)
                                .borderWidthDp(1f)
                                .cornerRadiusDp(10f)
                                .oval(false)
                                .build()

                            Picasso.get()
                                .load("https://trainers.codefriend.in${data.image}")
                                .fit()
                                .transform(transformation)
                                .into(groupDetailBinding.roundedImg)

                            initPlanningData(data.group_plannings)
                            initLessonData(data.group_lessions)
                            initEventData(data.group_events)
                            initTestData(data.group_tests)

                            Log.d("iddd", "onResponse:" + data.id)
                            val groupMembers = data.group_members
                            if (groupMembers != null) {
                                val allAthletes = arrayListOf<GroupListData.Athlete>()

                                groupMembers.forEach { member ->
                                    val athlete = member.athlete
                                    if (athlete != null) {
                                        allAthletes.add(athlete)
                                        Log.d("AthleteData", "Athlete: ${athlete.name}")
                                    } else {
                                        Log.d(
                                            "com.example.model.AthleteDataPackage.AthleteData",
                                            "Athlete is null for member: $member"
                                        )
                                    }
                                }

                                if (allAthletes.isNotEmpty()) {
                                    initAthleteData(allAthletes)
                                } else {
                                    Log.d(
                                        "com.example.model.AthleteDataPackage.AthleteData",
                                        "No athletes found in group members."
                                    )
                                }
                            } else {
                                Log.d(
                                    "com.example.model.AthleteDataPackage.AthleteData",
                                    "No group members found."
                                )
                            }

                        }
                    } else {
                        Toast.makeText(
                            this@GroupDetailActivity,
                            resource?.message ?: "Unknown error",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@GroupDetailActivity,
                        "Error: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<GroupListData?>, t: Throwable) {
                if (isFinishing || isDestroyed) return
                groupDetailBinding.progressDetail.visibility = View.GONE
                Toast.makeText(
                    this@GroupDetailActivity,
                    t.message ?: "Error fetching data",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun initEventData(eventData: ArrayList<GroupListData.GroupEvents>?) {

        val data = eventData ?: ArrayList<GroupListData.GroupEvents>()

        groupDetailBinding.eventRly.layoutManager = LinearLayoutManager(this)
        eventAdapter = EventAdapter(data, this, this)
        groupDetailBinding.eventRly.adapter = eventAdapter
    }

    private fun initAthleteData(athleteData: ArrayList<GroupListData.Athlete>?) {

        val data = athleteData ?: ArrayList<GroupListData.Athlete>()

        groupDetailBinding.atheleteRly.layoutManager = LinearLayoutManager(this)
        athleteAdapter = AthleteAdapter(data, this, this)
        groupDetailBinding.atheleteRly.adapter = athleteAdapter
    }

    private fun initLessonData(lessonData: ArrayList<GroupListData.GroupLesson>?) {
        val data = lessonData ?: ArrayList<GroupListData.GroupLesson>()

        groupDetailBinding.lessionRly.layoutManager = LinearLayoutManager(this)
        lessonAdapter = LessonAdapter(data, this, this) // No need to pass null values
        groupDetailBinding.lessionRly.adapter = lessonAdapter
    }

    private fun initTestData(testData: ArrayList<GroupListData.GroupTest>?) {
        val data = testData ?: ArrayList<GroupListData.GroupTest>()

        groupDetailBinding.testRly.layoutManager = LinearLayoutManager(this)
        testAdapter = TestAdapter(data, this, this) // No need to pass null values
        groupDetailBinding.testRly.adapter = testAdapter
    }

    private fun initPlanningData(data: ArrayList<GroupListData.GroupPlanning>?) {
        groupDetailBinding.planningRly.layoutManager = LinearLayoutManager(this)
        adapter = PlanningAdapter(data, this, this)
        groupDetailBinding.planningRly.adapter = adapter
    }


    override fun onItemClicked(view: View, position: Int, type: Long, string: String) {
        when (string) {
//            "planning" -> {
//                val intent = Intent(this, ViewTrainingPlanActivity::class.java)
//                intent.putExtra("Id",) // Example ID
//                startActivity(intent)
//            }
            "lesson" -> {
                val intent = Intent(this, ViewLessonActivity::class.java)
                intent.putExtra("Id", 214) // Example ID
                startActivity(intent)
            }

            "event" -> {
                Toast.makeText(this, string, Toast.LENGTH_SHORT).show()
            }
        }

        if (string == "fav") {
            groupDetailBinding.progressDetail.visibility = View.VISIBLE
            val id: MultipartBody.Part =
                MultipartBody.Part.createFormData("id", type.toInt().toString())
            apiInterface.Favourite_lession(id)?.enqueue(object : Callback<RegisterData?> {
                override fun onResponse(
                    call: Call<RegisterData?>,
                    response: Response<RegisterData?>
                ) {
                    groupDetailBinding.progressDetail.visibility = View.GONE
                    Log.d("TAG", response.code().toString() + "")
                    val code = response.code()
                    if (code == 200) {
                        val resource: RegisterData? = response.body()
                        val Success: Boolean = resource?.status!!
                        val Message: String = resource.message!!
                        if (Success) {
                            groupDetailBinding.progressDetail.visibility = View.GONE
                            callGroupApi()
                        } else {
                            groupDetailBinding.progressDetail.visibility = View.GONE
                            Toast.makeText(
                                this@GroupDetailActivity,
                                "" + Message,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@GroupDetailActivity)
                    } else {
                        groupDetailBinding.progressDetail.visibility = View.GONE
                        Toast.makeText(
                            this@GroupDetailActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    groupDetailBinding.progressDetail.visibility = View.GONE
                    Toast.makeText(this@GroupDetailActivity, "" + t.message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }
            })
        } else if (string == "unfav") {
            groupDetailBinding.progressDetail.visibility = View.VISIBLE
            val id: MultipartBody.Part =
                MultipartBody.Part.createFormData("id", type.toInt().toString())
            apiInterface.DeleteFavourite_lession(type.toInt())
                ?.enqueue(object : Callback<RegisterData?> {
                    override fun onResponse(
                        call: Call<RegisterData?>,
                        response: Response<RegisterData?>
                    ) {
                        Log.d("TAG", response.code().toString() + "")
                        groupDetailBinding.progressDetail.visibility = View.GONE
                        val code = response.code()
                        if (code == 200) {
                            val resource: RegisterData? = response.body()
                            val Success: Boolean = resource?.status!!
                            val Message: String = resource.message!!
                            if (Success) {
                                groupDetailBinding.progressDetail.visibility = View.GONE
                                callGroupApi()
                            } else {
                                groupDetailBinding.progressDetail.visibility = View.GONE
                                Toast.makeText(
                                    this@GroupDetailActivity,
                                    "" + Message,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        } else if (code == 403) {
                            Utils.setUnAuthDialog(this@GroupDetailActivity)
                        } else {
                            Toast.makeText(
                                this@GroupDetailActivity,
                                "" + response.message(),
                                Toast.LENGTH_SHORT
                            ).show()
                            call.cancel()
                        }
                    }

                    override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                        groupDetailBinding.progressDetail.visibility = View.GONE
                        Toast.makeText(
                            this@GroupDetailActivity,
                            "" + t.message,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                })
        } else if (string == "favevent") {
            groupDetailBinding.progressDetail.visibility = View.VISIBLE
            val id: MultipartBody.Part =
                MultipartBody.Part.createFormData("id", type.toInt().toString())
            apiInterface.Favourite_Event(id)?.enqueue(object : Callback<RegisterData?> {
                override fun onResponse(
                    call: Call<RegisterData?>,
                    response: Response<RegisterData?>
                ) {
                    groupDetailBinding.progressDetail.visibility = View.GONE
                    Log.d("TAG", response.code().toString() + "")
                    val code = response.code()
                    if (code == 200) {
                        val resource: RegisterData? = response.body()
                        val Success: Boolean = resource?.status!!
                        val Message: String = resource.message!!
                        if (Success) {
                            groupDetailBinding.progressDetail.visibility = View.GONE
                            callGroupApi()
                        } else {
                            groupDetailBinding.progressDetail.visibility = View.GONE
                            Toast.makeText(
                                this@GroupDetailActivity,
                                "" + Message,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@GroupDetailActivity)
                    } else {
                        groupDetailBinding.progressDetail.visibility = View.GONE
                        Toast.makeText(
                            this@GroupDetailActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    groupDetailBinding.progressDetail.visibility = View.GONE
                    Toast.makeText(this@GroupDetailActivity, "" + t.message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }
            })
        } else if (string == "unfavevent") {
            groupDetailBinding.progressDetail.visibility = View.VISIBLE
            val id: MultipartBody.Part =
                MultipartBody.Part.createFormData("id", type.toInt().toString())
            apiInterface.DeleteFavourite_Event(type.toInt())
                ?.enqueue(object : Callback<RegisterData?> {
                    override fun onResponse(
                        call: Call<RegisterData?>,
                        response: Response<RegisterData?>
                    ) {
                        Log.d("TAG", response.code().toString() + "")
                        groupDetailBinding.progressDetail.visibility = View.GONE
                        val code = response.code()
                        if (code == 200) {
                            val resource: RegisterData? = response.body()
                            val Success: Boolean = resource?.status!!
                            val Message: String = resource.message!!
                            if (Success) {
                                groupDetailBinding.progressDetail.visibility = View.GONE
                                callGroupApi()
                            } else {
                                groupDetailBinding.progressDetail.visibility = View.GONE
                                Toast.makeText(
                                    this@GroupDetailActivity,
                                    "" + Message,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        } else if (code == 403) {
                            Utils.setUnAuthDialog(this@GroupDetailActivity)
                        } else {
                            Toast.makeText(
                                this@GroupDetailActivity,
                                "" + response.message(),
                                Toast.LENGTH_SHORT
                            ).show()
                            call.cancel()
                        }
                    }

                    override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                        groupDetailBinding.progressDetail.visibility = View.GONE
                        Toast.makeText(
                            this@GroupDetailActivity,
                            "" + t.message,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                })
        } else if (string == "favtest") {
            groupDetailBinding.progressDetail.visibility = View.VISIBLE
            val id: MultipartBody.Part =
                MultipartBody.Part.createFormData("id", type.toInt().toString())
            apiInterface.Favourite_Test(id)?.enqueue(object : Callback<RegisterData?> {
                override fun onResponse(
                    call: Call<RegisterData?>,
                    response: Response<RegisterData?>
                ) {
                    groupDetailBinding.progressDetail.visibility = View.GONE
                    Log.d("TAG", response.code().toString() + "")
                    val code = response.code()
                    if (code == 200) {
                        val resource: RegisterData? = response.body()
                        val Success: Boolean = resource?.status!!
                        val Message: String = resource.message!!
                        if (Success) {
                            groupDetailBinding.progressDetail.visibility = View.GONE
                            callGroupApi()
                        } else {
                            groupDetailBinding.progressDetail.visibility = View.GONE
                            Toast.makeText(
                                this@GroupDetailActivity,
                                "" + Message,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@GroupDetailActivity)
                    } else {
                        groupDetailBinding.progressDetail.visibility = View.GONE
                        Toast.makeText(
                            this@GroupDetailActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    groupDetailBinding.progressDetail.visibility = View.GONE
                    Toast.makeText(this@GroupDetailActivity, "" + t.message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }
            })
        } else if (string == "unfavtest") {
            groupDetailBinding.progressDetail.visibility = View.VISIBLE
            var id: MultipartBody.Part =
                MultipartBody.Part.createFormData("id", type.toInt().toString())
            apiInterface.DeleteFavourite_Test(type.toInt())
                ?.enqueue(object : Callback<RegisterData?> {
                    override fun onResponse(
                        call: Call<RegisterData?>,
                        response: Response<RegisterData?>
                    ) {
                        Log.d("TAG", response.code().toString() + "")
                        groupDetailBinding.progressDetail.visibility = View.GONE
                        val code = response.code()
                        if (code == 200) {
                            val resource: RegisterData? = response.body()
                            val Success: Boolean = resource?.status!!
                            val Message: String = resource.message!!
                            if (Success) {
                                groupDetailBinding.progressDetail.visibility = View.GONE
                                callGroupApi()
                            } else {
                                groupDetailBinding.progressDetail.visibility = View.GONE
                                Toast.makeText(
                                    this@GroupDetailActivity,
                                    "" + Message,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        } else if (code == 403) {
                            Utils.setUnAuthDialog(this@GroupDetailActivity)
                        } else {
                            Toast.makeText(
                                this@GroupDetailActivity,
                                "" + response.message(),
                                Toast.LENGTH_SHORT
                            ).show()
                            call.cancel()
                        }
                    }

                    override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                        groupDetailBinding.progressDetail.visibility = View.GONE
                        Toast.makeText(
                            this@GroupDetailActivity,
                            "" + t.message,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                })
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        // Cancel ongoing API calls to prevent memory leaks
        groupListCall?.cancel()
    }
}
