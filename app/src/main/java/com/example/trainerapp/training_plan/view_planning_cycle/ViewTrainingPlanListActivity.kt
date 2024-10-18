package com.example.trainerapp.training_plan.view_planning_cycle

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.Adapter.training_plan.view.ViewTraingPalnListAdapter
import com.example.OnItemClickListener
import com.example.model.training_plan.cycles.GetMessocyclePreSession
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.PreferencesManager
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.ActivityViewTrainingPlanListBinding
import com.example.trainerapp.performance_profile.mesocycle.AddMesocycleListActivity
import com.example.trainerapp.training_plan.micro.ViewMicroCycleActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

        Log.d("dates:-", "startDate: $startDate, endDate: $endDate")

        Log.d("ViewTraining data :-", "main Id: $mainId, CardType: $cardType,season Id: $seasonId")
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
    }

    private fun setAdapter() {
        if (programData.isNotEmpty()) {
            viewTrainingPlanBinding.addLayout.visibility = View.GONE
            viewTrainingPlanBinding.recyclerView.visibility = View.VISIBLE
            viewTrainingPlanBinding.recyclerView.layoutManager = LinearLayoutManager(this)
            adapter = ViewTraingPalnListAdapter(programData, this, this, cardType, mainId)
            viewTrainingPlanBinding.recyclerView.adapter = adapter
        } else {
            viewTrainingPlanBinding.addLayout.visibility = View.VISIBLE
            viewTrainingPlanBinding.recyclerView.visibility = View.GONE
            showToast("No data available for the selected training plan.")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onItemClicked(view: View, position: Int, type: Long, string: String) {
        val intent = Intent(this, ViewMicroCycleActivity::class.java)

        val trainingData = programData.get(position)

        when (string) {
            "pre_session" -> {
                intent.putExtra("seasonId", trainingData.id)
                intent.putExtra("mainId", mainId)
                intent.putExtra("CardType", cardType)
                intent.putExtra("startDate", startDate)
                intent.putExtra("endDate", endDate)
            }

            "pre_competitive" -> {
                intent.putExtra("seasonId", seasonId)
                intent.putExtra("mainId", mainId)
                intent.putExtra("CardType", cardType)
                intent.putExtra("startDate", startDate)
                intent.putExtra("endDate", endDate)
            }

            "competitive" -> {
                intent.putExtra("seasonId", seasonId)
                intent.putExtra("mainId", trainingData.id)
                intent.putExtra("CardType", cardType)
                intent.putExtra("startDate", startDate)
                intent.putExtra("endDate", endDate)
            }

            "transition" -> {
                intent.putExtra("seasonId", seasonId)
                intent.putExtra("mainId", mainId)
                intent.putExtra("CardType", cardType)
                intent.putExtra("startDate", startDate)
                intent.putExtra("endDate", endDate)
            }
        }
        intent.putExtra("CardType", string)
        startActivity(intent)
    }
}