package com.example.trainerapp.view_analysis

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.Adapter.competition.ViewAnalysisAdapter
import com.example.model.SelectedValue
import com.example.model.newClass.athlete.AthleteData
import com.example.model.newClass.competition.Competition
import com.example.model.newClass.competition.GetCompetition
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.PreferencesManager
import com.example.trainerapp.R
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.ActivityViewAnalysisBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewAnalysisActivity : AppCompatActivity() {
    lateinit var viewAnalysisBinding: ActivityViewAnalysisBinding
    var athleteId = SelectedValue(null)
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    lateinit var preferenceManager: PreferencesManager
    lateinit var athleteData: ArrayList<AthleteData.Athlete>
    lateinit var competitionData: MutableList<Competition.CompetitionData>
    lateinit var viewAnalysisAdapter: ViewAnalysisAdapter

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
                        Log.d("Get Profile Data ", "${response.body()}")
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@ViewAnalysisActivity)
                    } else {
                        Toast.makeText(
                            this@ViewAnalysisActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Toast.makeText(
                        this@ViewAnalysisActivity,
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

    override fun onResume() {
        checkUser()
        super.onResume()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewAnalysisBinding = ActivityViewAnalysisBinding.inflate(layoutInflater)
        setContentView(viewAnalysisBinding.root)
        initViews()
        loadData()
        setDefaultRecycler()
        checkButtonClick()
    }

    private fun setDefaultRecycler() {
        viewAnalysisBinding.recViewAnalysis.layoutManager = LinearLayoutManager(this)
        viewAnalysisAdapter = ViewAnalysisAdapter(competitionData, this)
        viewAnalysisBinding.recViewAnalysis.adapter = viewAnalysisAdapter
    }

    private

    fun checkButtonClick() {
        viewAnalysisBinding.edtAthletes.setOnClickListener {
            val list = athleteData.map { it.name }
            val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val popupView = inflater.inflate(R.layout.popup_list, null)
            val popupWindow = PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true // Focusable to allow outside clicks to dismiss
            )
            popupWindow.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    this@ViewAnalysisActivity,
                    R.drawable.popup_background
                )
            )
            popupWindow.elevation = 10f

            val listView = popupView.findViewById<ListView>(R.id.listView)

            val adapter =
                object : ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list) {
                    override fun getView(
                        position: Int,
                        convertView: View?,
                        parent: ViewGroup
                    ): View {
                        val view = super.getView(position, convertView, parent) as TextView
                        view.setTextColor(Color.WHITE) // Set text color to white
                        return view
                    }
                }
            listView.adapter = adapter
            listView.setOnItemClickListener { _, _, position, _ ->
                val selectedItem = list[position]
                viewAnalysisBinding.edtAthletes.setText(selectedItem)
                athleteId.id = athleteData.filter { it.name == selectedItem }.first().id!!
                println("Selected item: $selectedItem")
                popupWindow.dismiss()
                setRecyclerView(athleteId.id!!)
            }
            popupWindow.showAsDropDown(it)
            popupWindow.setBackgroundDrawable(
                AppCompatResources.getDrawable(
                    this,
                    android.R.color.white
                )
            )
        }
    }

    private fun setRecyclerView(id: Int) {
        viewAnalysisBinding.recViewAnalysis.visibility = View.VISIBLE
        val data = competitionData.filter { it.athlete_id!!.toInt() == id }
        viewAnalysisAdapter = ViewAnalysisAdapter(data.toMutableList(), this)
        viewAnalysisBinding.recViewAnalysis.adapter = viewAnalysisAdapter
    }

    private fun initViews() {
        preferenceManager = PreferencesManager(this)
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        athleteData = arrayListOf()
        competitionData = mutableListOf()
    }

    private fun loadData() {
        resetData()
        getCompetitionData()
        getAthleteData()
    }

    private fun getCompetitionData() {
        try {
            competitionData.clear()
            apiInterface.GetCompetitionAnalysisData().enqueue(object : Callback<GetCompetition> {
                override fun onResponse(
                    call: Call<GetCompetition>,
                    response: Response<GetCompetition>
                ) {
                    viewAnalysisBinding.ProgressBar.visibility = View.GONE
                    Log.d("TAG", response.code().toString() + "")

                    val code = response.code()
                    if (code == 200) {
                        val success: Boolean = response.body()!!.status!!
                        if (success) {
                            val data = response.body()!!
                            Log.d("Athlete :- Data ", "${data}")
                            val message = data.message ?: "Success"
                            val compData = data.data!!
                            if (compData != null) {
                                for (i in compData) {
                                    Log.d(
                                        "Competition Data :-",
                                        "${i.category} \n ${i.athlete!!.name}"
                                    )
                                    competitionData.add(i)
                                }
                                Log.d("Competition Data :-", "${competitionData}")
                            }
                        }
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@ViewAnalysisActivity)
                    } else {
                        Toast.makeText(
                            this@ViewAnalysisActivity,
                            "Failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(p0: Call<GetCompetition>, p1: Throwable) {
                    viewAnalysisBinding.ProgressBar.visibility = View.GONE
                }

            })
        } catch (e: Exception) {
            Log.d("Error :-", "${e.message}")
        }
    }

    private fun getAthleteData() {
        athleteData.clear()
        viewAnalysisBinding.ProgressBar.visibility = View.VISIBLE
        apiInterface.GetAthleteList()!!.enqueue(
            object : Callback<AthleteData> {
                override fun onResponse(
                    call: Call<AthleteData>,
                    response: Response<AthleteData>
                ) {
                    viewAnalysisBinding.ProgressBar.visibility = View.GONE
                    Log.d("Athlete :- Tag ", response.code().toString() + "")
                    val code = response.code()
                    if (code == 200) {
                        val data = response.body()!!
                        if (data.data != null) {
                            Log.d("Athlete :- Data ", "${data}")
                            for (i in data.data) {
                                Log.d("Athlete $i", "${i.name} \t ${i.id} \t ${i.athletes}")
                            }
                            val success: Boolean = data.status!!
                            if (success) {
                                athleteData.addAll(data.data)
                            }
                        }
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@ViewAnalysisActivity)
                    } else {
                        Toast.makeText(
                            this@ViewAnalysisActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        ).show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<AthleteData>, t: Throwable) {
                    viewAnalysisBinding.ProgressBar.visibility = View.GONE
                    Toast.makeText(this@ViewAnalysisActivity, "" + t.message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }

            }
        )
    }

    private fun resetData() {
        viewAnalysisBinding.edtAthletes.text.clear()
        viewAnalysisBinding.recViewAnalysis.visibility = View.GONE
        athleteId.id = null
    }
}