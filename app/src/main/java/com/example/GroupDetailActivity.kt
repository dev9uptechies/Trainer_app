package com.example

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.EditGroupActivity
import com.example.trainerapp.PreferencesManager
import com.example.trainerapp.R
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.ActivityGroupDetailBinding
import com.example.trainerapp.training_plan.ViewTrainingPlanActivity
import com.example.trainerappAthlete.AthleteAdapterAthlete
import com.example.trainerappAthlete.model.EventAdapterAthlete
import com.example.trainerappAthlete.model.GroupListAthlete
import com.example.trainerappAthlete.model.LessonAdapterAthlete
import com.example.trainerappAthlete.model.PlanningAdapterAthlete
import com.example.trainerappAthlete.model.TestAdapterAthlete
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
    private var selectedImageUri: String? = null

    var planningId: IntArray = intArrayOf()  // Initialize as an empty array or a default value
    private lateinit var apiInterface: APIInterface
    private lateinit var apiClient: APIClient
    private lateinit var adapter: PlanningAdapter
    private lateinit var adapterAthelete: PlanningAdapterAthlete
    private lateinit var lessonAdapter: LessonAdapter
    private lateinit var lessonAdapterAthlete: LessonAdapterAthlete
    private lateinit var testAdapter: TestAdapter
    private lateinit var testAdapterAthlete: TestAdapterAthlete
    private lateinit var eventAdapter: EventAdapter
    private lateinit var eventAdapterAthlete: EventAdapterAthlete
    private lateinit var athleteAdapter: AthleteAdapter
    private lateinit var athleteAdapterAthlete: AthleteAdapterAthlete
    lateinit var preferenceManager: PreferencesManager
    var userType: String? = null

    private var groupListCall: Call<GroupListData>? = null
    private var groupListCallAthlete: Call<GroupListAthlete>? = null

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
        preferenceManager = PreferencesManager(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)

        userType = preferenceManager.GetFlage()

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

        val userType = preferenceManager.GetFlage()
        if (userType == "Athlete"){
            groupDetailBinding.cardView4.visibility = View.GONE
            groupDetailBinding.deleteGroup.visibility = View.GONE

            groupDetailBinding.createGroup.setOnClickListener {
                groupDetailBinding.main.setBackgroundColor(resources.getColor(R.color.grey))

                val dialog = Dialog(this, R.style.Theme_Dialog)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setCancelable(true)
                dialog.setCanceledOnTouchOutside(false)
                dialog.setContentView(R.layout.scan_qr_code_dailog)
                val displayMetrics = DisplayMetrics()
                windowManager.defaultDisplay.getMetrics(displayMetrics)
                val width = (displayMetrics.widthPixels * 0.9f).toInt()
                val height = WindowManager.LayoutParams.WRAP_CONTENT
                val window: Window = dialog.window!!
                val wlp = window.attributes
                wlp.gravity = Gravity.CENTER
                wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_BLUR_BEHIND
                window.attributes = wlp
                dialog.window!!.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            dialog.window!!.setLayout(width, height)
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


                val close = dialog.findViewById<CardView>(R.id.next_card)
                val scanQr = dialog.findViewById<ImageView>(R.id.scan_qr)
                val scanQrprogress = dialog.findViewById<ProgressBar>(R.id.progressBarImages)

                Log.d("FFHHF", "onCreate: $selectedImageUri")

                scanQrprogress.visibility = View.VISIBLE

                Picasso.get()
                    .load("https://trainers.codefriend.in" +selectedImageUri)
                    .fit()
                    .error(R.drawable.group_chate_boarder)
                    .into(scanQr, object : com.squareup.picasso.Callback {
                        override fun onSuccess() {
                            scanQrprogress.visibility = View.GONE
                            Log.d("Picasso", "Image loaded successfully.")
                        }

                        override fun onError(e: Exception?) {
                            Log.e("PicassoError", "Image load failed: ${e?.message}")
                        }
                    })

                close.setOnClickListener {
                    groupDetailBinding.main.setBackgroundColor(resources.getColor(R.color.black))
                    dialog.dismiss() }

                dialog.show()
            }

        }else{
            groupDetailBinding.cardView4.visibility = View.VISIBLE
            groupDetailBinding.deleteGroup.visibility = View.VISIBLE

            groupDetailBinding.createGroup.setOnClickListener {
                groupDetailBinding.main.setBackgroundColor(resources.getColor(R.color.grey))
                val dialog = Dialog(this, R.style.Theme_Dialog)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setCancelable(true)
                dialog.setCanceledOnTouchOutside(false)
                dialog.setContentView(R.layout.scan_qr_code_dailog)
                val displayMetrics = DisplayMetrics()
                windowManager.defaultDisplay.getMetrics(displayMetrics)
                val width = (displayMetrics.widthPixels * 0.9f).toInt()
                val height = WindowManager.LayoutParams.WRAP_CONTENT
                val window: Window = dialog.window!!
                val wlp = window.attributes
                wlp.gravity = Gravity.CENTER
                wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_BLUR_BEHIND
                window.attributes = wlp
                dialog.window!!.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                dialog.window!!.setLayout(width, height)
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


                val close = dialog.findViewById<CardView>(R.id.next_card)
                val scanQr = dialog.findViewById<ImageView>(R.id.scan_qr)

                groupDetailBinding.progressBarImage.visibility = View.VISIBLE

                Picasso.get()
                    .load("https://trainers.codefriend.in" + selectedImageUri)
                    .fit()
                    .into(scanQr, object : com.squareup.picasso.Callback {
                        override fun onSuccess() {
                            groupDetailBinding.progressBarImage.visibility = View.GONE
                            Log.d("Picasso", "Image loaded successfully.")
                        }

                        override fun onError(e: Exception?) {
                            groupDetailBinding.progressBarImage.visibility = View.GONE
                            Log.e("PicassoError", "Image load failed: ${e?.message}")
                        }
                    })

                close.setOnClickListener {
                    groupDetailBinding.main.setBackgroundColor(resources.getColor(R.color.black))
                    dialog.dismiss()
                }

                dialog.show()
            }

        }
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
        if (userType == "Athlete"){
            groupDetailBinding.planningAdd.visibility = View.GONE
            groupDetailBinding.LessonAdd.visibility = View.GONE
            groupDetailBinding.EventAdd.visibility = View.GONE
            groupDetailBinding.TestAdd.visibility = View.GONE
            callGroupApiAthlete()
        }else {
            callGroupApi()
        }
    }

    private fun deleteGroup(groupId: Int) {
        try {
            // Perform the deletion API call
            apiInterface.DeleteGroup(groupId)!!.enqueue(object : Callback<GroupListData> {
                override fun onResponse(
                    call: Call<GroupListData>,
                    response: Response<GroupListData>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@GroupDetailActivity,
                            "Group deleted successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        loadData()  // Re-fetch the group data after deletion
                    }else if (response.code() == 429) {
                        Toast.makeText(this@GroupDetailActivity, "Too Many Request", Toast.LENGTH_SHORT).show()
                    }else {
                        Toast.makeText(
                            this@GroupDetailActivity,
                            "Failed to delete group",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<GroupListData>, t: Throwable) {
                    Toast.makeText(
                        this@GroupDetailActivity,
                        "Error deleting group",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            })
        } catch (e: Exception) {
            Log.d("catch", "callGroupApiAthlete: ${e.message.toString()}")
        }
    }

    private fun callGroupApi() {
        try {

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

                                groupDetailBinding.progressBarImage.visibility = View.GONE

                                Picasso.get()
                                    .load("https://trainers.codefriend.in${data.image}")
                                    .fit()
                                    .transform(transformation)
                                    .into(groupDetailBinding.roundedImg, object : com.squareup.picasso.Callback {
                                        override fun onSuccess() {
                                            groupDetailBinding.progressBarImage.visibility = View.GONE
                                            Log.d("Picasso", "Image loaded successfully.")
                                        }

                                        override fun onError(e: Exception?) {
                                            groupDetailBinding.progressBarImage.visibility = View.GONE
                                            Log.e("PicassoError", "Image load failed: ${e?.message}")
                                        }
                                    })

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
        } catch (e: Exception) {
            Log.d("catch", "callGroupApiAthlete: ${e.message.toString()}")
        }
    }

    private fun callGroupApiAthlete() {
        try {

            groupDetailBinding.progressDetail.visibility = View.VISIBLE

            groupListCallAthlete = apiInterface.GropListAthlete()
            groupListCallAthlete?.enqueue(object : Callback<GroupListAthlete?> {
                override fun onResponse(
                    call: Call<GroupListAthlete?>,
                    response: Response<GroupListAthlete?>
                ) {
                    if (isFinishing || isDestroyed) return

                    groupDetailBinding.progressDetail.visibility = View.GONE
                    if (response.isSuccessful) {
                        val resource = response.body()
                        if (resource?.status == true) {
                            val position = position ?: return
                            resource.data?.getOrNull(position)?.let { data ->
                                groupDetailBinding.tvGroupName.text = data.group!!.name
                                groupDetailBinding.tvMember.text =
                                    "${data.group!!.group_members?.size ?: 0} Members"

                                data.group!!.group_plannings?.forEach { groupPlanning ->
                                    groupPlanning.planning_id?.let {
                                        planningIdList.add(it)
                                    }
                                }

                                Log.d("FVVFVFV", "Planning IDs: ${group_id}")
                                val transformation: Transformation = RoundedTransformationBuilder()
                                    .borderColor(Color.WHITE)
                                    .borderWidthDp(1f)
                                    .cornerRadiusDp(10f)
                                    .oval(false)
                                    .build()

                                groupDetailBinding.progressBarImage.visibility = View.VISIBLE

                                Picasso.get()
                                    .load("https://trainers.codefriend.in${data.group!!.image}")
                                    .fit()
                                    .transform(transformation)
                                    .into(groupDetailBinding.roundedImg, object : com.squareup.picasso.Callback {
                                        override fun onSuccess() {
                                            groupDetailBinding.progressBarImage.visibility = View.GONE
                                            Log.d("Picasso", "Image loaded successfully.")
                                        }

                                        override fun onError(e: Exception?) {
                                            groupDetailBinding.progressBarImage.visibility = View.GONE
                                            Log.e("PicassoError", "Image load failed: ${e?.message}")
                                        }
                                    })

                                selectedImageUri = data.group!!.qr_code

                                initPlanningDataAthlete(data.group!!.group_plannings)
                                initLessonDataAthlete(data.group!!.group_lessions)
                                initEventDataAthlete(data.group!!.group_events)
                                initTestDataAthlete(data.group!!.group_tests)

                                Log.d("iddd", "onResponse:" + data.id)
                                val groupMembers = data.group!!.group_members
                                if (groupMembers != null) {
                                    val allAthletes = arrayListOf<GroupListAthlete.Athlete>()

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
                                        initAthleteDataAthlete(allAthletes)
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

                override fun onFailure(call: Call<GroupListAthlete?>, t: Throwable) {
                    if (isFinishing || isDestroyed) return
                    groupDetailBinding.progressDetail.visibility = View.GONE
                    Toast.makeText(
                        this@GroupDetailActivity,
                        t.message ?: "Error fetching data",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } catch (e: Exception) {
            Log.d("catch", "callGroupApiAthlete: ${e.message.toString()}")
        }
    }

    private fun initEventData(eventData: ArrayList<GroupListData.GroupEvents>?) {
        try {
            val data = eventData ?: ArrayList<GroupListData.GroupEvents>()

            groupDetailBinding.eventRly.layoutManager = LinearLayoutManager(this)
            eventAdapter = EventAdapter(data, this, this)
            groupDetailBinding.eventRly.adapter = eventAdapter
        } catch (e: Exception) {
            Log.d("catch", "callGroupApiAthlete: ${e.message.toString()}")
        }
    }

    private fun initAthleteData(athleteData: ArrayList<GroupListData.Athlete>?) {

        try {
            val data = athleteData ?: ArrayList<GroupListData.Athlete>()

            groupDetailBinding.atheleteRly.layoutManager = LinearLayoutManager(this)
            athleteAdapter = AthleteAdapter(data, this, this)
            groupDetailBinding.atheleteRly.adapter = athleteAdapter
        } catch (e: Exception) {
            Log.d("catch", "callGroupApiAthlete: ${e.message.toString()}")
        }
    }

    private fun initLessonData(lessonData: ArrayList<GroupListData.GroupLesson>?) {
        try {
            val data = lessonData ?: ArrayList<GroupListData.GroupLesson>()

            groupDetailBinding.lessionRly.layoutManager = LinearLayoutManager(this)
            lessonAdapter = LessonAdapter(data, this, this) // No need to pass null values
            groupDetailBinding.lessionRly.adapter = lessonAdapter
        } catch (e: Exception) {
            Log.d("catch", "callGroupApiAthlete: ${e.message.toString()}")
        }
    }

    private fun initTestData(testData: ArrayList<GroupListData.GroupTest>?) {
        try {

            val data = testData ?: ArrayList<GroupListData.GroupTest>()
            groupDetailBinding.testRly.layoutManager = LinearLayoutManager(this)
            testAdapter = TestAdapter(data, this, this) // No need to pass null values
            groupDetailBinding.testRly.adapter = testAdapter
        } catch (e: Exception) {
            Log.d("catch", "callGroupApiAthlete: ${e.message.toString()}")
        }
    }

    private fun initPlanningData(data: ArrayList<GroupListData.GroupPlanning>?) {
        try {

            groupDetailBinding.planningRly.layoutManager = LinearLayoutManager(this)
            adapter = PlanningAdapter(data, this, this)
            groupDetailBinding.planningRly.adapter = adapter
        } catch (e: Exception) {
            Log.d("catch", "callGroupApiAthlete: ${e.message.toString()}")
        }
    }

    //////////////   Athlete    ////////////

    private fun initAthleteDataAthlete(athleteData: ArrayList<GroupListAthlete.Athlete>?) {

        try {
            val data = athleteData ?: ArrayList<GroupListAthlete.Athlete>()

            groupDetailBinding.atheleteRly.layoutManager = LinearLayoutManager(this)
            athleteAdapterAthlete = AthleteAdapterAthlete(data, this, this)
            groupDetailBinding.atheleteRly.adapter = athleteAdapterAthlete
        } catch (e: Exception) {
            Log.d("catch", "callGroupApiAthlete: ${e.message.toString()}")
        }
    }

    private fun initTestDataAthlete(testData: ArrayList<GroupListAthlete.GroupTest>?) {
        try {
            val data = testData ?: ArrayList<GroupListAthlete.GroupTest>()
            groupDetailBinding.testRly.layoutManager = LinearLayoutManager(this)
            testAdapterAthlete = TestAdapterAthlete(data, this, this)
            groupDetailBinding.testRly.adapter = testAdapterAthlete
        } catch (e: Exception) {
            Log.d("catch", "callGroupApiAthlete: ${e.message.toString()}")
        }
    }

    private fun initEventDataAthlete(eventData: ArrayList<GroupListAthlete.GroupEvents>?) {
        try {
            val data = eventData ?: ArrayList<GroupListAthlete.GroupEvents>()
            groupDetailBinding.eventRly.layoutManager = LinearLayoutManager(this)
            eventAdapterAthlete = EventAdapterAthlete(data, this, this)
            groupDetailBinding.eventRly.adapter = eventAdapterAthlete
        } catch (e: Exception) {
            Log.d("catch", "callGroupApiAthlete: ${e.message.toString()}")
        }
    }

    private fun initPlanningDataAthlete(data: ArrayList<GroupListAthlete.GroupPlanning>?) {
        try {
            groupDetailBinding.planningRly.layoutManager = LinearLayoutManager(this)
            adapterAthelete = PlanningAdapterAthlete(data, this, this)
            groupDetailBinding.planningRly.adapter = adapterAthelete
        } catch (e: Exception) {
            Log.d("catch", "callGroupApiAthlete: ${e.message.toString()}")
        }
    }

    private fun initLessonDataAthlete(lessonData: ArrayList<GroupListAthlete.GroupLesson>?) {
        try {
            val data = lessonData ?: ArrayList<GroupListAthlete.GroupLesson>()

            groupDetailBinding.lessionRly.layoutManager = LinearLayoutManager(this)
            lessonAdapterAthlete = LessonAdapterAthlete(data, this, this)
            groupDetailBinding.lessionRly.adapter = lessonAdapterAthlete
        } catch (e: Exception) {
            Log.d("catch", "callGroupApiAthlete: ${e.message.toString()}")
        }
    }

    override fun onItemClicked(view: View, position: Int, type: Long, string: String) {
        when (string) {
//            "planning" -> {
//                val intent = Intent(this, ViewTrainingPlanActivity::class.java)
//                intent.putExtra("Id",) // Example ID
//                startActivity(intent)
//            }
            "lesson" -> {
//                val intent = Intent(this, ViewLessonActivity::class.java)
//                intent.putExtra("Id", 214) // Example ID
//                startActivity(intent)
            }

            "event" -> {
                Toast.makeText(this, string, Toast.LENGTH_SHORT).show()
            }
        }

        if (string == "fav") {
            try {
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
                            loadData()
                        } else {
                            groupDetailBinding.progressDetail.visibility = View.GONE
                            Toast.makeText(this@GroupDetailActivity, "" + Message, Toast.LENGTH_SHORT).show()
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
            }catch (e:Exception){
                Log.d("catch", "callGroupApiAthlete: ${e.message.toString()}")
            }
        } else if (string == "unfav") {
            try{
            groupDetailBinding.progressDetail.visibility = View.VISIBLE
            val id: MultipartBody.Part = MultipartBody.Part.createFormData("id", type.toInt().toString())
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
                                loadData()
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
            }catch (e:Exception){
                Log.d("catch", "callGroupApiAthlete: ${e.message.toString()}")
            }
        } else if (string == "favevent") {
            try {
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
                            loadData()
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
            }catch (e:Exception){
                Log.d("catch", "callGroupApiAthlete: ${e.message.toString()}")
            }
        } else if (string == "unfavevent") {
            try {
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
                                loadData()
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
            }catch (e:Exception){
                Log.d("catch", "callGroupApiAthlete: ${e.message.toString()}")
            }
        } else if (string == "favtest") {
            try {
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
                            loadData()
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
            }catch (e:Exception){
                Log.d("catch", "callGroupApiAthlete: ${e.message.toString()}")
            }
        } else if (string == "unfavtest") {
            try {
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
                                loadData()
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
            }catch (e:Exception){
                Log.d("catch", "callGroupApiAthlete: ${e.message.toString()}")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        // Cancel ongoing API calls to prevent memory leaks
        groupListCall?.cancel()
    }
}
