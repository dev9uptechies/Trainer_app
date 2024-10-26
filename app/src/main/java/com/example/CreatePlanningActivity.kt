package com.example

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.PlanningData
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.ActivityCreatePlanningBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreatePlanningActivity : AppCompatActivity() {
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    lateinit var createPlanningBinding: ActivityCreatePlanningBinding

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createPlanningBinding = ActivityCreatePlanningBinding.inflate(layoutInflater)
        setContentView(createPlanningBinding.root)
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        callApi()

        createPlanningBinding.tvSDate.setOnClickListener {
            Utils.select(this, createPlanningBinding.tvSDate)
        }

        createPlanningBinding.imgBack.setOnClickListener {
            finish()
        }
        createPlanningBinding.cardPreSeason.setOnClickListener {
            startActivity(Intent(this, CreateMesoCycleActivity::class.java))
        }

        createPlanningBinding.cardPreCompetitive.setOnClickListener {

        }

        createPlanningBinding.cardCompetitive.setOnClickListener {

        }

        createPlanningBinding.tvCompetitionDate.setOnClickListener {
            Utils.select(this, createPlanningBinding.tvCompetitionDate)
        }

        createPlanningBinding.edtPreSDate.setOnClickListener {
            Utils.select(this, createPlanningBinding.edtPreSDate)
        }

        createPlanningBinding.edtPreEDate.setOnClickListener {
            Utils.select(this, createPlanningBinding.edtPreEDate)
        }

        createPlanningBinding.edtPreCompetitiveSDate.setOnClickListener {
            Utils.select(this, createPlanningBinding.edtPreCompetitiveSDate)
        }

        createPlanningBinding.edtPreCompetitiveEDate.setOnClickListener {
            Utils.select(this, createPlanningBinding.edtPreCompetitiveEDate)
        }

        createPlanningBinding.edtCompetitiveSDate.setOnClickListener {
            Utils.select(this, createPlanningBinding.edtCompetitiveSDate)
        }

        createPlanningBinding.edtCompetitiveEDate.setOnClickListener {
            Utils.select(this, createPlanningBinding.edtCompetitiveEDate)
        }

        createPlanningBinding.nextCard.setOnClickListener {
            startActivity(Intent(this, AddPeriodActivity::class.java))
        }

    }

    private fun callApi() {
        createPlanningBinding.planningProgress.visibility = View.VISIBLE
        apiInterface.Get_Planning()?.enqueue(object : Callback<PlanningData> {
            override fun onResponse(
                call: Call<PlanningData>,
                response: Response<PlanningData>
            ) {
                if (response.code() == 200) {
                    if (response.body()!!.data!!.size == 0) {
                        createPlanningBinding.listLy.visibility = View.GONE
                        createPlanningBinding.editPlaningLy.visibility = View.VISIBLE
                    } else {
                        createPlanningBinding.listLy.visibility = View.VISIBLE
                        createPlanningBinding.editPlaningLy.visibility = View.GONE
                        updateUi(response.body())
                    }

                }
            }

            override fun onFailure(call: Call<PlanningData>, t: Throwable) {
                createPlanningBinding.planningProgress.visibility = View.GONE
                Toast.makeText(this@CreatePlanningActivity, "" + t.message, Toast.LENGTH_SHORT)
                    .show()
            }

        })

    }

    private fun updateUi(body: PlanningData?) {
        createPlanningBinding.planningProgress.visibility = View.GONE
        createPlanningBinding.edtName.text = body!!.data!![0].name.toString()
        createPlanningBinding.tvSDate.setText(body.data!![0].start_date.toString())
        createPlanningBinding.totalDay.text = body.data!![0].mesocycle
        createPlanningBinding.atheleteName.text = body.data!![0].name
        createPlanningBinding.tvCompetitionDate.setText(body.data!![0].competition_date.toString())
        createPlanningBinding.tvStartDate.text = body.data!![0].pre_season!!.start_date
        createPlanningBinding.tvEndDate.text = body.data!![0].pre_season!!.end_date
        createPlanningBinding.tvMesocycle.text = body.data!![0].pre_season!!.mesocycle
        createPlanningBinding.tvPcStartDate.text = body.data!![0].pre_competitive!!.start_date
        createPlanningBinding.tvPcEndDate.text = body.data!![0].pre_competitive!!.end_date
        createPlanningBinding.tvPcMesocycle.text = body.data!![0].pre_competitive!!.mesocycle
        createPlanningBinding.tvCStartDate.text = body.data!![0].pre_competitive!!.start_date
        createPlanningBinding.tvPcEndDate.text = body.data!![0].pre_competitive!!.end_date
        createPlanningBinding.tvCMesocycle.text = body.data!![0].pre_competitive!!.mesocycle
    }
}