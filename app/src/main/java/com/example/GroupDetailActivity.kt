package com.example

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.databinding.ActivityGroupDetailBinding
import com.makeramen.roundedimageview.RoundedTransformationBuilder
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GroupDetailActivity : AppCompatActivity(), OnItemClickListener.OnItemClickCallback {
    lateinit var groupDetailBinding: ActivityGroupDetailBinding
    var position: Int? = null
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    lateinit var adapter: PlanningAdapter
    lateinit var lessonadapter: LessonAdapter
    lateinit var eventdapter: EventAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        groupDetailBinding = ActivityGroupDetailBinding.inflate(layoutInflater)
        setContentView(groupDetailBinding.root)
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        position = intent.getIntExtra("position", 0)
        groupDetailBinding.imgBack.setOnClickListener {
            finish()
        }
        callGroupApi()
    }

    private fun callGroupApi() {
        groupDetailBinding.progressDetail.visibility = View.VISIBLE
        apiInterface.GropList()?.enqueue(object : Callback<GroupListData?> {
            override fun onResponse(
                call: Call<GroupListData?>,
                response: Response<GroupListData?>
            ) {
                Log.d("TAG", response.code().toString() + "")
                val code = response.code()
                if (code == 200) {
                    val resource: GroupListData? = response.body()
                    val Success: Boolean = resource?.status!!
                    val Message: String = resource.message!!
                    groupDetailBinding.progressDetail.visibility = View.GONE
                    if (Success) {
                        groupDetailBinding.tvGroupName.text = resource.data!![position!!].name
                        groupDetailBinding.tvMember.text =
                            resource.data!![position!!].group_members!!.size.toString() + " Members"
                        val transformation: Transformation = RoundedTransformationBuilder()
                            .borderColor(Color.WHITE)
                            .borderWidthDp(1f)
                            .cornerRadiusDp(10f)
                            .oval(false)
                            .build()

                        Picasso.get()
                            .load("https://trainers.codefriend.in" + resource.data!![position!!].image)
                            .fit()
                            .transform(transformation)
                            .into(groupDetailBinding.roundedImg)

                        val planningdata = resource.data!![position!!].group_plannings
                        val lessondata = resource.data!![position!!].group_lessions
                        val eventdata = resource.data!![position!!].group_events
                        initPlanningData(planningdata)
                        initLessonData(lessondata)
                        initEventData(eventdata)

                    } else {
                        groupDetailBinding.progressDetail.visibility = View.GONE
                        Toast.makeText(this@GroupDetailActivity, "" + Message, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }

            override fun onFailure(call: Call<GroupListData?>, t: Throwable) {
                groupDetailBinding.progressDetail.visibility = View.GONE
                Toast.makeText(this@GroupDetailActivity, "" + t.message, Toast.LENGTH_SHORT)
                    .show()
                call.cancel()
            }
        })
    }

    private fun initEventData(eventdata: ArrayList<GroupListData.GroupEvents>?) {
        groupDetailBinding.eventRly.layoutManager = LinearLayoutManager(this)
        eventdapter =
            EventAdapter(eventdata, this, this)
        groupDetailBinding.eventRly.adapter = eventdapter
    }

    private fun initLessonData(lessondata: ArrayList<GroupListData.GroupLesson>?) {
        groupDetailBinding.lessionRly.layoutManager = LinearLayoutManager(this)
        lessonadapter =
            LessonAdapter(lessondata, this, this)
        groupDetailBinding.lessionRly.adapter = lessonadapter
    }

    private fun initPlanningData(data: ArrayList<GroupListData.GroupPlanning>?) {
        groupDetailBinding.planningRly.layoutManager = LinearLayoutManager(this)
        adapter =
            PlanningAdapter(data, this, this)
        groupDetailBinding.planningRly.adapter = adapter
    }

    override fun onItemClicked(view: View, position: Int, type: Long, string: String) {
        if (string == "planning") {
            startActivity(Intent(this, CreatePlanningActivity::class.java))
        } else if (string == "lesson") {
            Toast.makeText(this, "" + string, Toast.LENGTH_SHORT).show()
        } else if (string == "event") {
            Toast.makeText(this, "" + string, Toast.LENGTH_SHORT).show()
        }
    }

}