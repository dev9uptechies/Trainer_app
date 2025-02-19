package com.example.trainerapp.training_plan.view_planning_cycle

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.Adapter.training_plan.view.ViewTraingPalnListAdapter
import com.example.OnItemClickListener
import com.example.model.training_plan.cycles.GetMessocyclePreSession
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.PreferencesManager
import com.example.trainerapp.R
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.ActivityViewTrainingPlanListBinding
import com.example.trainerapp.training_plan.micro.ViewMicroCycleActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

class ViewTrainingPlanListActivity : AppCompatActivity(), OnItemClickListener.OnItemClickCallback {
    lateinit var viewTrainingPlanBinding: ActivityViewTrainingPlanListBinding
    private lateinit var apiInterface: APIInterface
    private lateinit var preferenceManager: PreferencesManager
    private lateinit var apiClient: APIClient
    private var programData: MutableList<GetMessocyclePreSession.Data> = mutableListOf()
    lateinit var datas: MutableList<GetMessocyclePreSession.Data>

    private var mainId: Int = 0
    private var seasonId: Int = 0

    private var startDate: String? = null
    private var endDate: String? = null
    private var cardType: String? = null
    private lateinit var adapter: ViewTraingPalnListAdapter

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
                        Utils.setUnAuthDialog(this@ViewTrainingPlanListActivity)
                    } else {
                        Toast.makeText(
                            this@ViewTrainingPlanListActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Toast.makeText(
                        this@ViewTrainingPlanListActivity,
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
        viewTrainingPlanBinding = ActivityViewTrainingPlanListBinding.inflate(layoutInflater)
        setContentView(viewTrainingPlanBinding.root)
        initViews()
        refreshData()
        checkButtonClick()
    }

    private fun refreshData() {
        viewTrainingPlanBinding.swipeReferesh.setOnRefreshListener {
            loadData()
            viewTrainingPlanBinding.swipeReferesh.isRefreshing = false
        }
    }

    private fun checkButtonClick() {
        viewTrainingPlanBinding.back.setOnClickListener {
            finish()
        }

        viewTrainingPlanBinding.addLayout.setOnClickListener {
            val intent = Intent(this, AddMesocycleListActivity::class.java)
            intent.putExtra("MainId", mainId)
            intent.putExtra("SeasonId", seasonId)
            intent.putExtra("CardType", cardType)
            intent.putExtra("startDate", startDate)
            intent.putExtra("endDate", endDate)
            startActivity(intent)
        }

        viewTrainingPlanBinding.add.setOnClickListener {
            if (programData.isEmpty()) {
                showToast("Pleases Add Mesocycle")
                return@setOnClickListener
            } else {
                val intent =
                    Intent(this@ViewTrainingPlanListActivity, EditMesocycleListActivity::class.java)
                intent.putExtra("mainId", mainId)
                intent.putExtra("SeasonId", seasonId)
                intent.putExtra("StartDate", startDate)
                intent.putExtra("EndDate", endDate)
                intent.putExtra("CardType", cardType)
                startActivity(intent)
            }
        }
    }

    private fun loadData() {

        when (cardType) {

            "pre_session" -> {
                viewTrainingPlanBinding.subTitle.text = "Pre Session"
                programData.clear()
                getPreSessionData(seasonId)
            }

            "pre_competitive" -> {
                viewTrainingPlanBinding.subTitle.text = "Pre Competitive"
                programData.clear()
                getPreCompatitiveData(seasonId)
            }

            "competitive" -> {
                viewTrainingPlanBinding.subTitle.text = "Competitive"
                programData.clear()
                getCompatitiveData(seasonId)
            }

            "transition" -> {
                viewTrainingPlanBinding.subTitle.text = "Transition"
                programData.clear()
                getTransitionData(seasonId)
            }
        }
    }

    private fun initViews() {
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        preferenceManager = PreferencesManager(this)

        datas = mutableListOf()
        mainId = intent.getIntExtra("mainId", 0)
        seasonId = intent.getIntExtra("seasonId", 0)
        cardType = intent.getStringExtra("CardType")
        startDate = intent.getStringExtra("startDate")
        endDate = intent.getStringExtra("endDate")

        Log.d("viewmesocycledates:-", "startDate: $startDate, endDate: $endDate")

        Log.d("ViewTraining data :-", "main Id: $mainId, CardType: $cardType,season Id: $seasonId")

        val userType = preferenceManager.GetFlage()
        if (userType == "Athlete") {

            viewTrainingPlanBinding.add.visibility = View.GONE
            viewTrainingPlanBinding.swipeReferesh.visibility = View.GONE

            val jsonMesocycles = intent.getStringExtra("PreSeasonJson")
            val jsonPreCompetitiveMesocycles = intent.getStringExtra("PreCompetitiveJson")
            val jsonCompetitiveMesocycles = intent.getStringExtra("CompetitiveJson")
            val jsonTransitionMesocycles = intent.getStringExtra("TransitionJson")

            // pre season
            if (jsonMesocycles != null) {
                val type = object : TypeToken<List<Map<String, Any>>>() {}.type
                val mesocyclesList: List<Map<String, Any>> = Gson().fromJson(jsonMesocycles, type)
                viewTrainingPlanBinding.subTitle.text = "Pre Season"

                Log.d("LDLDLDLDLLD", "initViews: $mesocyclesList")

                SetAthleteData(mesocyclesList)
                for (mesocycle in mesocyclesList) {
                    Log.d("ReceivedMesocycles", "ID: ${mesocycle["id"]}, Name: ${mesocycle["name"]}")
                }
            } else {
                Log.d("ReceivedMesocycles", "No data received")
            }

            // pre competitive
            if (jsonPreCompetitiveMesocycles != null) {
                val type = object : TypeToken<List<Map<String, Any>>>() {}.type
                val mesocyclesList: List<Map<String, Any>> = Gson().fromJson(jsonPreCompetitiveMesocycles, type)
                viewTrainingPlanBinding.subTitle.text = "Pre Competitive"


                Log.d("LDLDLDLDLLD", "initViews: $mesocyclesList")

                SetAthleteData(mesocyclesList)
                for (mesocycle in mesocyclesList) {
                    Log.d(
                        "ReceivedMesocycles",
                        "ID: ${mesocycle["id"]}, Name: ${mesocycle["name"]}"
                    )
                }
            } else {
                Log.d("ReceivedMesocycles", "No data received")
            }

            // competitive
            if (jsonCompetitiveMesocycles != null) {
                val type = object : TypeToken<List<Map<String, Any>>>() {}.type
                val mesocyclesList: List<Map<String, Any>> = Gson().fromJson(jsonCompetitiveMesocycles, type)
                viewTrainingPlanBinding.subTitle.text = "Competitive"

                SetAthleteData(mesocyclesList)
                for (mesocycle in mesocyclesList) {
                    Log.d(
                        "ReceivedMesocycles",
                        "ID: ${mesocycle["id"]}, Name: ${mesocycle["name"]}"
                    )
                }
            } else {
                Log.d("ReceivedMesocycles", "No data received")
            }

            // transition
            if (jsonTransitionMesocycles != null) {
                val type = object : TypeToken<List<Map<String, Any>>>() {}.type
                val mesocyclesList: List<Map<String, Any>> = Gson().fromJson(jsonTransitionMesocycles, type)

                viewTrainingPlanBinding.subTitle.text = "Transition"

                SetAthleteData(mesocyclesList)
                for (mesocycle in mesocyclesList) {
                    Log.d(
                        "ReceivedMesocycles",
                        "ID: ${mesocycle["id"]}, Name: ${mesocycle["name"]}"
                    )
                }
            } else {
                Log.d("ReceivedMesocycles", "No data received")
            }

        }
    }

    fun formatDate(dateString: String?): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // Adjust input format if needed
            val outputFormat = SimpleDateFormat("dd, MMM yyyy", Locale.getDefault())
            val date = inputFormat.parse(dateString ?: "")
            date?.let { outputFormat.format(it) } ?: "Invalid Date"
        } catch (e: Exception) {
            "Invalid Date"
        }
    }

    private fun SetAthleteData(mesocyclesList: List<Map<String, Any>>) {
        val parentLayout = findViewById<LinearLayout>(R.id.liner_for_athlete)
        viewTrainingPlanBinding.textView3.text = "Periods"

        fun addAthleteView(
            phase: String,
            name: String?,
            startDate: String?,
            endDate: String?,
            mesocycle: String?,
            onClick: () -> Unit
        ) {
            val athleteView = LayoutInflater.from(this).inflate(R.layout.viewtrainingplanlist, parentLayout, false)

            val time_card_one = athleteView.findViewById<CardView>(R.id.card_one)
            val time_card_two = athleteView.findViewById<CardView>(R.id.card_two)
            val AthletePlanName = athleteView.findViewById<TextView>(R.id.training_name_two)
            val AthletePlanStartDate = athleteView.findViewById<TextView>(R.id.start_date_two)
            val AthletePlanEndDate = athleteView.findViewById<TextView>(R.id.end_date_two)
            val AthletePlanMesocycle = athleteView.findViewById<TextView>(R.id.mesocycle_two)

            time_card_one.visibility = View.GONE
            time_card_two.visibility = View.VISIBLE
            val start = formatDate(startDate)
            val end = formatDate(endDate)
            AthletePlanName.text = name ?: phase
            AthletePlanStartDate.text = "Start Date: $start"
            AthletePlanEndDate.text = "End Date: $end"
            AthletePlanMesocycle.text = "Micro No: " + (mesocycle ?: "0") + " Cycle"

            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                resources.getDimensionPixelSize(R.dimen._80sdp)
            ).apply {
                setMargins(0, 10, 0, 10)
            }

            athleteView.layoutParams = layoutParams

            athleteView.setOnClickListener { onClick() }

            parentLayout.addView(athleteView)
        }

        val gson = Gson()

        // **PreSeason**
        intent.getStringExtra("PreSeason")?.let { preSeason ->
            if (preSeason == "PreSeason") {
                for (mesocycle in mesocyclesList) {
                    val name = mesocycle["name"]?.toString()
                    val startDate = mesocycle["start_date"]?.toString()
                    val endDate = mesocycle["end_date"]?.toString()

                    val microcycleList = mesocycle["microcycles"] as? List<Map<String, Any>> ?: emptyList()
                    val jsonMicrocycle = gson.toJson(microcycleList)

                    addAthleteView("Mesocycle", name, startDate, endDate, "1 Cycle") {
                        Log.d("CLICK_EVENT", "Mesocycle $name clicked!")

                        val intent = Intent(this, ViewMicroCycleActivity::class.java)
                        intent.putExtra("PreSeasonJson", jsonMicrocycle)
                        intent.putExtra("PreSeason", "PreSeason")
                        intent.putExtra("MesocycleName", name)
                        startActivity(intent)
                    }
                }
            }
        }

        // **PreCompetitive**
        intent.getStringExtra("PreCompetitive")?.let { preCompetitive ->
            if (preCompetitive == "PreCompetitive") {
                for (mesocycle in mesocyclesList) {
                    val name = mesocycle["name"]?.toString()
                    val startDate = mesocycle["start_date"]?.toString()
                    val endDate = mesocycle["end_date"]?.toString()

                    val microcycleList = mesocycle["microcycles"] as? List<Map<String, Any>> ?: emptyList()
                    val jsonMicrocycle = gson.toJson(microcycleList)

                    addAthleteView("Mesocycle", name, startDate, endDate, "1 Cycle") {
                        Log.d("CLICK_EVENT", "Mesocycle $name clicked!")

                        val intent = Intent(this, ViewMicroCycleActivity::class.java)
                        intent.putExtra("PreCompetitiveJson", jsonMicrocycle)
                        intent.putExtra("PreCompetitive", "PreCompetitive")
                        intent.putExtra("MesocycleName", name) // **Sending Mesocycle Name**
                        startActivity(intent)
                    }
                }
            }
        }

        // **Competitive**
        intent.getStringExtra("Competitive")?.let { competitive ->
            if (competitive == "Competitive") {
                for (mesocycle in mesocyclesList) {
                    val name = mesocycle["name"]?.toString()
                    val startDate = mesocycle["start_date"]?.toString()
                    val endDate = mesocycle["end_date"]?.toString()

                    val microcycleList = mesocycle["microcycles"] as? List<Map<String, Any>> ?: emptyList()
                    val jsonMicrocycle = gson.toJson(microcycleList)

                    addAthleteView("Mesocycle", name, startDate, endDate, "1 Cycle") {
                        Log.d("CLICK_EVENT", "Mesocycle $name clicked!")

                        val intent = Intent(this, ViewMicroCycleActivity::class.java)
                        intent.putExtra("CompetitiveJson", jsonMicrocycle)
                        intent.putExtra("Competitive", "Competitive")
                        intent.putExtra("MesocycleName", name) // **Sending Mesocycle Name**
                        startActivity(intent)
                    }
                }
            }
        }

        // **Transition**
        intent.getStringExtra("Transition")?.let { transition ->
            if (transition == "Transition") {
                for (mesocycle in mesocyclesList) {
                    val name = mesocycle["name"]?.toString()
                    val startDate = mesocycle["start_date"]?.toString()
                    val endDate = mesocycle["end_date"]?.toString()

                    val microcycleList = mesocycle["microcycles"] as? List<Map<String, Any>> ?: emptyList()
                    val jsonMicrocycle = gson.toJson(microcycleList)

                    addAthleteView("Mesocycle", name, startDate, endDate, "1 Cycle") {
                        Log.d("CLICK_EVENT", "Mesocycle $name clicked!")

                        val intent = Intent(this, ViewMicroCycleActivity::class.java)
                        intent.putExtra("TransitionJson", jsonMicrocycle)
                        intent.putExtra("Transition", "Transition")
                        intent.putExtra("MesocycleName", name) // **Sending Mesocycle Name**
                        startActivity(intent)
                    }
                }
            }
        }
    }


    private fun getTransitionData(id: Int) {
        viewTrainingPlanBinding.progresBar.visibility = View.VISIBLE
        apiInterface.GetMesocycleTransition(id)
            .enqueue(object : Callback<GetMessocyclePreSession> {
                override fun onResponse(
                    call: Call<GetMessocyclePreSession>,
                    response: Response<GetMessocyclePreSession>
                ) {

                    viewTrainingPlanBinding.progresBar.visibility = View.GONE

                    Log.d("TAG", response.code().toString() + "")
                    val code = response.code()
                    if (code == 200) {
                        if (response.isSuccessful && response.body() != null) {
                            val data = response.body()?.data ?: mutableListOf()

                            for (item in data) {
                                Log.d("DATA_ITEM", "Id: ${item.id}")
                                Log.d("DATA_ITEM", "Name: ${item.name}")
                                Log.d("DATA_ITEM", "Start Date: ${item.start_date}")
                                Log.d("DATA_ITEM", "End Date: ${item.end_date}")
                                Log.d("DATA_ITEM", "Mesocycle: ${item.periods}")
                                Log.d("DATA_ITEM", "Created At: ${item.created_at}")
                                Log.d("DATA_ITEM", "Updated At: ${item.updated_at}")
                            }

                            if (data.isNotEmpty()) {
                                programData.addAll(data)
                                setAdapter()
                                Log.d("firstId", "$seasonId")
                            } else {
                                Log.e("DATA_TAG", "No Data Available")
                                viewTrainingPlanBinding.addLayout.visibility = View.VISIBLE
                                viewTrainingPlanBinding.recyclerView.visibility = View.GONE

                            }
                        } else {
                            Log.d("DATA_TAG", "No Data Available")
                            showToast("Error: ${response.code()} - ${response.message()}")
                        }
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@ViewTrainingPlanListActivity)
                    } else {
                        Toast.makeText(
                            this@ViewTrainingPlanListActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<GetMessocyclePreSession>, t: Throwable) {
                    Log.d("TAG Category", t.message.toString())
                    showToast("API Call Failed: ${t.message}")
                    call.cancel()
                }
            })
    }

    private fun getCompatitiveData(id: Int) {
        apiInterface.GetMesocycleCompatitive(id)
            .enqueue(object : Callback<GetMessocyclePreSession> {
                override fun onResponse(
                    call: Call<GetMessocyclePreSession>,
                    response: Response<GetMessocyclePreSession>
                ) {

                    viewTrainingPlanBinding.progresBar.visibility = View.GONE

                    Log.d("TAG", response.code().toString() + "")
                    val code = response.code()
                    if (code == 200) {
                        if (response.isSuccessful && response.body() != null) {
                            val data = response.body()?.data ?: mutableListOf()

                            for (item in data) {
                                Log.d("DATA_ITEM", "Id: ${item.id}")
                                Log.d("DATA_ITEM", "Name: ${item.name}")
                                Log.d("DATA_ITEM", "Start Date: ${item.start_date}")
                                Log.d("DATA_ITEM", "End Date: ${item.end_date}")
                                Log.d("DATA_ITEM", "Mesocycle: ${item.periods}")
                                Log.d("DATA_ITEM", "Created At: ${item.created_at}")
                                Log.d("DATA_ITEM", "Updated At: ${item.updated_at}")
                            }

                            if (data.isNotEmpty()) {
                                programData.addAll(data)
                                setAdapter()
                                //seasonId = data[0].id!!
                                Log.d("firstId", "$seasonId")

                            } else {
                                Log.e("DATA_TAG", "No Data Available")
                                viewTrainingPlanBinding.addLayout.visibility = View.VISIBLE
                                viewTrainingPlanBinding.recyclerView.visibility = View.GONE
//                                viewTrainingPlanBinding.add.setOnClickListener {
//                                    showToast("Pleases Add Messocycle")
//                                }
                            }
                        } else {
                            Log.d("DATA_TAG", "No Data Available")
                            showToast("Error: ${response.code()} - ${response.message()}")
                        }
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@ViewTrainingPlanListActivity)
                    } else {
                        Toast.makeText(
                            this@ViewTrainingPlanListActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<GetMessocyclePreSession>, t: Throwable) {
                    Log.d("TAG Category", t.message.toString())
                    showToast("API Call Failed: ${t.message}")
                    call.cancel()
                }
            })
        viewTrainingPlanBinding.progresBar.visibility = View.GONE
    }

    private fun getPreCompatitiveData(id: Int) {
        apiInterface.GetMesocyclePreCompatitive(id)
            .enqueue(object : Callback<GetMessocyclePreSession> {
                override fun onResponse(
                    call: Call<GetMessocyclePreSession>,
                    response: Response<GetMessocyclePreSession>
                ) {

                    viewTrainingPlanBinding.progresBar.visibility = View.GONE

                    Log.d("TAG", response.code().toString() + "")
                    val code = response.code()
                    if (code == 200) {
                        if (response.isSuccessful && response.body() != null) {
                            val data = response.body()?.data ?: mutableListOf()

                            for (item in data) {
                                Log.d("DATA_ITEM", "Id: ${item.id}")
                                Log.d("DATA_ITEM", "Name: ${item.name}")
                                Log.d("DATA_ITEM", "Start Date: ${item.start_date}")
                                Log.d("DATA_ITEM", "End Date: ${item.end_date}")
                                Log.d("DATA_ITEM", "Mesocycle: ${item.periods}")
                                Log.d("DATA_ITEM", "Created At: ${item.created_at}")
                                Log.d("DATA_ITEM", "Updated At: ${item.updated_at}")
                            }

                            if (data.isNotEmpty()) {
                                programData.addAll(data)
                                setAdapter()
                                //seasonId = data[0].id!!
                                Log.d("firstId", "$seasonId")


                            } else {
                                Log.e("DATA_TAG", "No Data Available")
                                viewTrainingPlanBinding.addLayout.visibility = View.VISIBLE
                                viewTrainingPlanBinding.recyclerView.visibility = View.GONE
//                                viewTrainingPlanBinding.add.setOnClickListener {
//                                    showToast("Pleases Add Messocycle")
//                                }
                            }
                        } else {
                            Log.d("DATA_TAG", "No Data Available")
                            showToast("Error: ${response.code()} - ${response.message()}")
                        }
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@ViewTrainingPlanListActivity)
                    } else {
                        Toast.makeText(
                            this@ViewTrainingPlanListActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<GetMessocyclePreSession>, t: Throwable) {
                    Log.d("TAG Category", t.message.toString())
                    showToast("API Call Failed: ${t.message}")
                    call.cancel()
                }
            })

        viewTrainingPlanBinding.progresBar.visibility = View.GONE
    }

    private fun getPreSessionData(id: Int) {
        try {


        viewTrainingPlanBinding.progresBar.visibility = View.VISIBLE

        programData.clear()
        datas.clear()

        apiInterface.GetMesocyclePreSession(id)
            .enqueue(object : Callback<GetMessocyclePreSession> {
                override fun onResponse(
                    call: Call<GetMessocyclePreSession>,
                    response: Response<GetMessocyclePreSession>
                ) {
                    viewTrainingPlanBinding.progresBar.visibility =
                        View.GONE

                    Log.d("TAG", response.code().toString() + "")
                    val code = response.code()
                    if (code == 200) {
                        if (response.isSuccessful && response.body() != null) {
                            val data = response.body()?.data ?: mutableListOf()

                            for (item in data) {
                                Log.d("DATA_ITEM", "Id: ${item.id}")
                                Log.d("DATA_ITEM", "Name: ${item.name}")
                                Log.d("DATA_ITEM", "Start Date: ${item.start_date}")
                                Log.d("DATA_ITEM", "End Date: ${item.end_date}")
                                Log.d("DATA_ITEM", "Mesocycle: ${item.periods}")
                                Log.d("DATA_ITEM", "Created At: ${item.created_at}")
                                Log.d("DATA_ITEM", "Updated At: ${item.updated_at}")
                            }

                            if (data.isNotEmpty()) {
                                programData.addAll(data)
                                datas.addAll(data)

                                //seasonId = data[0].id!!
                                Log.d("firstId", "$seasonId")
                                setAdapter()

                            } else {
                                Log.e("DATA_TAG", "No Data Available")
                                viewTrainingPlanBinding.addLayout.visibility = View.VISIBLE
                                viewTrainingPlanBinding.recyclerView.visibility = View.GONE
//                                viewTrainingPlanBinding.add.setOnClickListener {
//                                    showToast("Please Add Mesocycle")
//                                }
                            }
                        } else {
                            Log.d("DATA_TAG", "No Data Available")
                            showToast("Error: ${response.code()} - ${response.message()}")
                        }
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@ViewTrainingPlanListActivity)
                    } else {
                        Toast.makeText(
                            this@ViewTrainingPlanListActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<GetMessocyclePreSession>, t: Throwable) {
                    Log.d("TAG Category", t.message.toString())
                    showToast("API Call Failed: ${t.message}")
                    call.cancel()
                }
            })
        }catch (e:Exception){
            Log.d("error",e.message.toString())
        }
    }

    private fun setAdapter() {
        try {

        if (programData.isNotEmpty()) {
            viewTrainingPlanBinding.addLayout.visibility = View.GONE
            viewTrainingPlanBinding.recyclerView.visibility = View.VISIBLE
            viewTrainingPlanBinding.recyclerView.layoutManager = LinearLayoutManager(this)
            adapter = ViewTraingPalnListAdapter(programData, this, this, cardType, mainId,startDate,endDate)
            viewTrainingPlanBinding.recyclerView.adapter = adapter
        } else {
            viewTrainingPlanBinding.addLayout.visibility = View.VISIBLE
            viewTrainingPlanBinding.recyclerView.visibility = View.GONE
            showToast("No data available for the selected training plan.")
        }
        }catch (e:Exception){
            Log.d("Exss",e.message.toString())
        }
    }



    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onItemClicked(view: View, position: Int, type: Long, string: String) {

    }
}