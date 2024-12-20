package com.example.trainerapp.training_plan.micro

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
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
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.ActivityViewMicroCycleBinding
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
