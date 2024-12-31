package com.example.trainerapp.competition

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.OnItemClickListener
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.EventListData
import com.example.trainerapp.PreferencesManager
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.ActivitySelectEventBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SelectEventActivity : AppCompatActivity(), OnItemClickListener.OnItemClickCallback {
    lateinit var selectEventBinding: ActivitySelectEventBinding
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    lateinit var preferenceManager: PreferencesManager
    lateinit var EventList: ArrayList<EventListData.testData>
    lateinit var eventListSelectAdapter: EventListSelectAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectEventBinding = ActivitySelectEventBinding.inflate(layoutInflater)
        setContentView(selectEventBinding.root)
        initViews()
        checkButtonClick()

        val userType = preferenceManager.GetFlage()

        if (userType == "Athlete"){
            getEventListAthlete()
        }else{
            getEventList()
        }

    }

    private fun checkButtonClick() {
        selectEventBinding.back.setOnClickListener {
            finish()
        }

        selectEventBinding.save.setOnClickListener {
            finish()
        }
    }

    private fun getEventListAthlete() {
        selectEventBinding.progressBar.visibility = View.VISIBLE
        apiInterface.GetEventAthlete()?.enqueue(object : Callback<EventListData?> {
            override fun onResponse(
                call: Call<EventListData?>,
                response: Response<EventListData?>
            ) {
                selectEventBinding.progressBar.visibility = View.GONE
                Log.d("TAG", response.code().toString())
                val code = response.code()
                if (code == 200) {
                    val resource: EventListData? = response.body()
                    val success: Boolean = resource?.status ?: false
                    val message: String = resource?.message ?: "Unknown error"
                    if (success) {
                        val eventList = resource!!.data
                        if (!eventList.isNullOrEmpty()) {
                            initRecycler(eventList)
                        } else {
                            Toast.makeText(
                                this@SelectEventActivity,
                                "No Data Found",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@SelectEventActivity,
                            message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else if (code == 403) {
                    Utils.setUnAuthDialog(this@SelectEventActivity)
                } else {
                    Toast.makeText(
                        this@SelectEventActivity,
                        response.message(),
                        Toast.LENGTH_SHORT
                    ).show()
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<EventListData?>, t: Throwable) {
                selectEventBinding.progressBar.visibility = View.GONE
                Toast.makeText(this@SelectEventActivity, t.message ?: "Unknown error", Toast.LENGTH_SHORT)
                    .show()
                call.cancel()
            }
        })
    }

    private fun getEventList() {
        selectEventBinding.progressBar.visibility = View.VISIBLE
        apiInterface.GetEvent()?.enqueue(object : Callback<EventListData?> {
            override fun onResponse(
                call: Call<EventListData?>,
                response: Response<EventListData?>
            ) {
                selectEventBinding.progressBar.visibility = View.GONE
                Log.d("TAG", response.code().toString())
                val code = response.code()
                if (code == 200) {
                    val resource: EventListData? = response.body()
                    val success: Boolean = resource?.status ?: false
                    val message: String = resource?.message ?: "Unknown error"
                    if (success) {
                        val eventList = resource!!.data
                        if (!eventList.isNullOrEmpty()) {
                            initRecycler(eventList)
                        } else {
                            Toast.makeText(
                                this@SelectEventActivity,
                                "No Data Found",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@SelectEventActivity,
                            message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else if (code == 403) {
                    Utils.setUnAuthDialog(this@SelectEventActivity)
                } else {
                    Toast.makeText(
                        this@SelectEventActivity,
                        response.message(),
                        Toast.LENGTH_SHORT
                    ).show()
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<EventListData?>, t: Throwable) {
                selectEventBinding.progressBar.visibility = View.GONE
                Toast.makeText(this@SelectEventActivity, t.message ?: "Unknown error", Toast.LENGTH_SHORT)
                    .show()
                call.cancel()
            }
        })
    }

    private fun initRecycler(eventList: ArrayList<EventListData.testData>) {
        selectEventBinding.rlyExerciseList.layoutManager = LinearLayoutManager(this)
        eventListSelectAdapter = EventListSelectAdapter(eventList, this, this)
        selectEventBinding.rlyExerciseList.adapter = eventListSelectAdapter
    }


    private fun initViews() {
        apiClient = APIClient(this)
        preferenceManager = PreferencesManager(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        EventList = arrayListOf()
        selectEventBinding.rlyExerciseList.layoutManager = LinearLayoutManager(this)
        eventListSelectAdapter = EventListSelectAdapter(EventList, this, this)
        selectEventBinding.rlyExerciseList.adapter = eventListSelectAdapter
    }

    override fun onItemClicked(view: View, position: Int, type: Long, string: String) {
    }
}