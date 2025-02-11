package com.example.trainerapp.training_plan.micro

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.Adapter.training_plan.view.ViewMicrocycleListAdapter
import com.example.OnItemClickListener
import com.example.model.training_plan.MicroCycle.GetMicrocycle
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.PreferencesManager
import com.example.trainerapp.R
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.ActivityViewMicroCycleBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewMicroCycleActivity : AppCompatActivity(), OnItemClickListener.OnItemClickCallback {

    private lateinit var viewMicroCycleBinding: ActivityViewMicroCycleBinding
    private lateinit var apiInterface: APIInterface
    private lateinit var preferenceManager: PreferencesManager

    private var programData: MutableList<GetMicrocycle.Data> = mutableListOf()
    private var mainId: Int = 0
    private var seasonId: Int = 0
    private var cardType: String? = null
    private var startDate: String? = null
    private var endDate: String? = null

    private lateinit var adapter: ViewMicrocycleListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewMicroCycleBinding = ActivityViewMicroCycleBinding.inflate(layoutInflater)
        setContentView(viewMicroCycleBinding.root)

        startDate = intent.getStringExtra("startDate")
        endDate = intent.getStringExtra("endDate")

        Log.d("DHDHDHDHDH", "onCreate: $startDate     $endDate")

        initViews()
        setupListeners()
        refreshData()

        viewMicroCycleBinding.back.setOnClickListener {
            finish()
        }


        viewMicroCycleBinding.EditMicrocycle.setOnClickListener {
            if (programData.isEmpty()) {
                showToast("Pleases Add Mesocycle")
                return@setOnClickListener
            } else {
                Log.d("SUJALDATE", "onCreate: $startDate  End : $endDate")
                val intent = Intent(this@ViewMicroCycleActivity, EditMicroCycleActivity::class.java)
                intent.putExtra("mainId", mainId)
                intent.putExtra("SeasonId", seasonId)
                intent.putExtra("StartDate", startDate)
                intent.putExtra("EndDate", endDate)
                intent.putExtra("CardType", cardType)
                startActivity(intent)
            }
        }
    }

    private fun initViews() {
        val apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        preferenceManager = PreferencesManager(this)

        mainId = intent.getIntExtra("mainId", 0)
        seasonId = intent.getIntExtra("seasonId", 0)
        cardType = intent.getStringExtra("CardType")


        Log.d("KKKKKKKKKK:-", "startDate: $startDate, endDate: $endDate  cardType: $cardType")

        Log.d("viewmicro", "$mainId     $seasonId    $cardType")



        val jsonMesocycles = intent.getStringExtra("PreSeasonJson")
        val jsonPreCompetitiveMesocycles = intent.getStringExtra("PreCompetitiveJson")
        val jsonCompetitiveMesocycles = intent.getStringExtra("CompetitiveJson")
        val jsonTransitionMesocycles = intent.getStringExtra("TransitionJson")

        Log.d("DDKKDKDK", "initViews: $jsonPreCompetitiveMesocycles")


        // pre competitive
        if (jsonPreCompetitiveMesocycles != null) {
            val type = object : TypeToken<List<Map<String, Any>>>() {}.type
            val mesocyclesList: List<Map<String, Any>> = Gson().fromJson(jsonPreCompetitiveMesocycles, type)
            viewMicroCycleBinding.subTitle.text = "Pre Season"

            SetAthleteData(mesocyclesList)

        } else {
            Log.d("ReceivedMesocycles", "No data received")
        }


    }

    private fun SetAthleteData(mesocyclesList: List<Map<String, Any>>) {
        val parentLayout = findViewById<LinearLayout>(R.id.liner_for_athlete)
        viewMicroCycleBinding.subTitle.text = "Periods"

        fun addAthleteView(
            phase: String,
            name: String?,
            startDate: String?,
            endDate: String?,
            workload: String?,
            onClick: () -> Unit
        ) {
            val athleteView = LayoutInflater.from(this).inflate(R.layout.view_microcycle_list, parentLayout, false)

            val AthletePlanName = athleteView.findViewById<TextView>(R.id.training_name_one)
            val AthletePlanStartDate = athleteView.findViewById<TextView>(R.id.start_date_one)
            val AthletePlanEndDate = athleteView.findViewById<TextView>(R.id.end_date_one)
            val seekbar_workload = athleteView.findViewById<SeekBar>(R.id.seekbar_workload)

            seekbar_workload.isEnabled = false
            seekbar_workload.isClickable = false
            seekbar_workload.isActivated = false

            AthletePlanName.text = name ?: phase
            AthletePlanStartDate.text = startDate ?: "Invalid Date"
            AthletePlanEndDate.text = endDate ?: "Invalid Date"
            workload?.toIntOrNull()?.let {
                seekbar_workload.progress = it
            } ?: run {
                Log.e("ERROR", "Workload is null or not a valid number")
                seekbar_workload.progress = 0
            }

            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 10, 0, 10)
            }
            athleteView.layoutParams = layoutParams

            athleteView.setOnClickListener {
                onClick()
            }

            parentLayout.addView(athleteView)
        }

        val preSeason = intent.getStringExtra("PreSeason")
        if (preSeason == "PreSeason") {
            for (microCycle in mesocyclesList) {
                val id = microCycle["id"]?.toString()
                val name = microCycle["name"]?.toString()
                val startDate = microCycle["start_date"]?.toString()
                val endDate = microCycle["end_date"]?.toString()
                val workload = microCycle["workload"]?.toString()

                viewMicroCycleBinding.textViewList.text = "Pre Season"


                addAthleteView("microcycles", name, startDate, endDate, "1 Cycle") {
                    Log.d("CLICK_EVENT", "Mesocycle $name clicked!")

                }
            }
        }

        val PreCompetitive = intent.getStringExtra("PreCompetitive")

        if (PreCompetitive == "PreCompetitive") {
            for (mesocycle in mesocyclesList) {
                val id = mesocycle["id"]?.toString()
                val name = mesocycle["name"]?.toString()
                val startDate = mesocycle["start_date"]?.toString()
                val endDate = mesocycle["end_date"]?.toString()
                val workload = mesocycle["workload"]?.toString()

                Log.d("SPPSPSPSPSPPSP", "SetAthleteData: $mesocyclesList")
                Log.d("SPPSPSPSPSPPSP", "SetAthleteData: $workload")

                viewMicroCycleBinding.textViewList.text = "Pre Competitive"

                addAthleteView("Mesocycle", name, startDate, endDate, workload ?: "") {
                    Log.d("CLICK_EVENT", "Mesocycle $name clicked!")
                }
            }
        }

        Log.d("()()()()()", "SetAthleteData: $preSeason    $PreCompetitive")

        val Competitive = intent.getStringExtra("Competitive")
        if (Competitive == "Competitive") {
            for (mesocycle in mesocyclesList) {
                val id = mesocycle["id"]?.toString()
                val name = mesocycle["name"]?.toString()
                val startDate = mesocycle["start_date"]?.toString()
                val endDate = mesocycle["end_date"]?.toString()

                viewMicroCycleBinding.textViewList.text = "Competitive"

                addAthleteView("Mesocycle", name, startDate, endDate, "1 Cycle") {
                    Log.d("CLICK_EVENT", "Mesocycle $name clicked!")

                }
            }
        }

        val Transition = intent.getStringExtra("Transition")
        if (Transition == "Transition") {
            for (mesocycle in mesocyclesList) {
                val id = mesocycle["id"]?.toString()
                val name = mesocycle["name"]?.toString()
                val startDate = mesocycle["start_date"]?.toString()
                val endDate = mesocycle["end_date"]?.toString()

                viewMicroCycleBinding.textViewList.text = "Transition"

                addAthleteView("Mesocycle", name, startDate, endDate, "1 Cycle") {
                    Log.d("CLICK_EVENT", "Mesocycle $name clicked!")

                }
            }
        }


    }


    private fun setupListeners() {
        viewMicroCycleBinding.addLayout.setOnClickListener {
            val intent = Intent(this, AddMicroCycleActivity::class.java)
            intent.putExtra("MainId", seasonId)
            intent.putExtra("StartDate", startDate)
            intent.putExtra("EndDate", endDate)
            intent.putExtra("CardType", cardType)
            startActivity(intent)
        }
    }


    override fun onResume() {
        super.onResume()
        checkUser()
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
                        Utils.setUnAuthDialog(this@ViewMicroCycleActivity)
                    } else {
                        Toast.makeText(
                            this@ViewMicroCycleActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Toast.makeText(
                        this@ViewMicroCycleActivity,
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

    private fun loadData() {

        when (cardType) {

            "pre_session" -> {
                programData.clear()
                getPreSessionData(seasonId)
            }

            "pre_competitive" -> {
                programData.clear()
                getPreCompatitiveData(seasonId)
            }

            "competitive" -> {
                programData.clear()
                getCompatitiveData(seasonId)

            }

            "transition" -> {
                programData.clear()
                getTrainstionData(seasonId)

            }
        }
    }

    private fun getPreSessionData(id: Int) {

        try {
            viewMicroCycleBinding.progresBar.visibility = View.VISIBLE
            programData.clear()

            apiInterface.GetMicrocyclePreSeason(id).enqueue(object : Callback<GetMicrocycle> {
                override fun onResponse(
                    call: Call<GetMicrocycle>,
                    response: Response<GetMicrocycle>
                ) {
                    viewMicroCycleBinding.progresBar.visibility = View.GONE

                    if (response.isSuccessful && response.body() != null) {
                        val data = response.body()?.data ?: emptyList()

                        if (data.isNotEmpty()) {
                            programData.addAll(data)
                            setAdapter()
                            Log.d("firstId", "$seasonId")

                        } else {
                            showEmptyState()
                        }
                    } else {
                        handleError(response)
                    }
                }

                override fun onFailure(call: Call<GetMicrocycle>, t: Throwable) {
                    viewMicroCycleBinding.progresBar.visibility = View.GONE
                    Log.e("GetMicrocycle Error", t.message.toString())
                    showToast(t.message ?: "Failed to fetch data")
                    call.cancel()
                }
            })
        } catch (e: Exception) {
            Log.d("Exception", "${e.message}")
        }
    }

    private fun getPreCompatitiveData(id: Int) {

        try {
            viewMicroCycleBinding.progresBar.visibility = View.VISIBLE
            programData.clear()

            apiInterface.GetMicrocyclePreCompatitive(id).enqueue(object : Callback<GetMicrocycle> {
                override fun onResponse(
                    call: Call<GetMicrocycle>,
                    response: Response<GetMicrocycle>
                ) {
                    viewMicroCycleBinding.progresBar.visibility = View.GONE

                    if (response.isSuccessful && response.body() != null) {
                        val data = response.body()?.data ?: emptyList()

                        if (data.isNotEmpty()) {
                            programData.addAll(data)
                            setAdapter()
                            Log.d("firstId", "$seasonId")

                        } else {
                            showEmptyState()
                        }
                    } else {
                        handleError(response)
                    }
                }

                override fun onFailure(call: Call<GetMicrocycle>, t: Throwable) {
                    viewMicroCycleBinding.progresBar.visibility = View.GONE
                    Log.e("GetMicrocycle Error", t.message.toString())
                    showToast(t.message ?: "Failed to fetch data")
                    call.cancel()
                }
            })
        } catch (e: Exception) {
            Log.d("Exception", "${e.message}")
        }
    }

    private fun getCompatitiveData(id: Int) {

        try {
            viewMicroCycleBinding.progresBar.visibility = View.VISIBLE
            programData.clear()

            apiInterface.GetMicrocycleCompatitive(id).enqueue(object : Callback<GetMicrocycle> {
                override fun onResponse(
                    call: Call<GetMicrocycle>,
                    response: Response<GetMicrocycle>
                ) {
                    viewMicroCycleBinding.progresBar.visibility = View.GONE

                    if (response.isSuccessful && response.body() != null) {
                        val data = response.body()?.data ?: emptyList()

                        if (data.isNotEmpty()) {
                            programData.addAll(data)
                            setAdapter()
                            Log.d("firstId", "$seasonId")

                        } else {
                            showEmptyState()
                        }
                    } else {
                        handleError(response)
                    }
                }

                override fun onFailure(call: Call<GetMicrocycle>, t: Throwable) {
                    viewMicroCycleBinding.progresBar.visibility = View.GONE
                    Log.e("GetMicrocycle Error", t.message.toString())
                    showToast(t.message ?: "Failed to fetch data")
                    call.cancel()
                }
            })
        } catch (e: Exception) {
            Log.d("Exception", "${e.message}")
        }
    }

    private fun getTrainstionData(id: Int) {

        try {
            viewMicroCycleBinding.progresBar.visibility = View.VISIBLE
            programData.clear()

            apiInterface.GetMicrocycleTransition(id).enqueue(object : Callback<GetMicrocycle> {
                override fun onResponse(
                    call: Call<GetMicrocycle>,
                    response: Response<GetMicrocycle>
                ) {
                    viewMicroCycleBinding.progresBar.visibility = View.GONE

                    if (response.isSuccessful && response.body() != null) {
                        val data = response.body()?.data ?: emptyList()

                        if (data.isNotEmpty()) {
                            programData.addAll(data)
                            setAdapter()
                            Log.d("firstId", "$seasonId")

                        } else {
                            showEmptyState()
                        }
                    } else {
                        handleError(response)
                    }
                }

                override fun onFailure(call: Call<GetMicrocycle>, t: Throwable) {
                    viewMicroCycleBinding.progresBar.visibility = View.GONE
                    Log.e("GetMicrocycle Error", t.message.toString())
                    showToast(t.message ?: "Failed to fetch data")
                    call.cancel()
                }
            })
        } catch (e: Exception) {
            Log.d("Exception", "${e.message}")
        }
    }

    private fun setAdapter() {
        if (programData.isNotEmpty()) {
            viewMicroCycleBinding.addLayout.visibility = View.GONE
            viewMicroCycleBinding.recyclerView.visibility = View.VISIBLE
            viewMicroCycleBinding.recyclerView.layoutManager = LinearLayoutManager(this)
            adapter = ViewMicrocycleListAdapter(programData, this, this, cardType, mainId)
            viewMicroCycleBinding.recyclerView.adapter = adapter
        } else {
            viewMicroCycleBinding.addLayout.visibility = View.VISIBLE
            viewMicroCycleBinding.recyclerView.visibility = View.GONE
            showToast("No data available for the selected training plan.")
        }

    }

    private fun showEmptyState() {
        Log.e("DATA_TAG", "No Data Available")
        viewMicroCycleBinding.addLayout.visibility = View.VISIBLE
        viewMicroCycleBinding.recyclerView.visibility = View.GONE
    }

    private fun handleError(response: Response<*>) {
        when (response.code()) {
            403 -> Utils.setUnAuthDialog(this@ViewMicroCycleActivity)
            else -> showToast(response.message())
        }
    }

    private fun refreshData() {
        viewMicroCycleBinding.swipeReferesh.setOnRefreshListener {
            loadData()
            viewMicroCycleBinding.swipeReferesh.isRefreshing = false
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onItemClicked(view: View, position: Int, type: Long, string: String) {
        TODO("Not yet implemented")
    }
}
