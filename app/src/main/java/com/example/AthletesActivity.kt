package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.addTextChangedListener
import com.ViewAetleteActivity
import com.example.model.newClass.athlete.AthleteData
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.PreferencesManager
import com.example.trainerapp.R
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.ActivityAthletesBinding
import com.example.trainerapp.training_plan.view_planning_cycle.AddMesocycleListActivity
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AthletesActivity : AppCompatActivity() {

    private lateinit var athleteActivitybinding: ActivityAthletesBinding
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    lateinit var preferencesManager: PreferencesManager
    lateinit var athleteData: ArrayList<AthleteData.Athlete>
    private lateinit var athletelist: LinearLayout
    private var trainingPlanCount = 0
    private var trainingPlanLayouts = mutableListOf<View>()
    private var mainId: Int = 0
    private var Name: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        athleteActivitybinding = ActivityAthletesBinding.inflate(layoutInflater)
        setContentView(athleteActivitybinding.root)
        initview()
        lordData()
        buttonClick()
        setupSearch()


    }

    private fun lordData() {
        getAthleteData()
    }

    private fun buttonClick() {
        athleteActivitybinding.back.setOnClickListener { finish() }
        athleteActivitybinding.linerAddAtlete.setOnClickListener {
//            startActivity(Intent(this,ViewAetleteActivity::class.java))
        }

    }

    private fun initview() {
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        preferencesManager = PreferencesManager(this)
        athleteData = arrayListOf()
        athletelist = athleteActivitybinding.linerAddAtlete
        trainingPlanLayouts = mutableListOf()
    }


    private fun setupSearch() {
        val searchEditText = athleteActivitybinding.edtSearch

        searchEditText.addTextChangedListener { text ->
            val query = text?.toString()?.trim() ?: ""
            filterAthletes(query)
        }
    }

    private fun filterAthletes(query: String) {
        val filteredList = if (query.isEmpty()) {
            athleteData

        } else {
            athleteData.filter { athlete ->
                athlete.name?.contains(query, ignoreCase = true) == true
            }
        }
        addAthleteNames(ArrayList(filteredList))
    }

    private fun getAthleteData() {
        try {

        athleteData.clear() // Clear previous data
        athleteActivitybinding.progresBar.visibility = View.VISIBLE

        apiInterface.GetAthleteList()!!.enqueue(object : Callback<AthleteData> {
            override fun onResponse(call: Call<AthleteData>, response: Response<AthleteData>) {
                athleteActivitybinding.progresBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val resource = response.body()
                    val success = resource?.status ?: false
                    val message = resource?.message ?: ""

                    if (success) {
                        val data = resource?.data

                        if (data != null) {
                            athleteData.addAll(data)
                            addAthleteNames(athleteData)
                        }

                        for (datas in athleteData){
                            Log.d("sujals","ID:- " + datas.id)
                        }

                    } else if (response.code() == 403) {
                        Utils.setUnAuthDialog(this@AthletesActivity)
                    } else {
                        Toast.makeText(this@AthletesActivity, response.message(), Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<AthleteData>, t: Throwable) {
                athleteActivitybinding.progresBar.visibility = View.GONE
                Toast.makeText(this@AthletesActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
        }catch (e:Exception){
            Log.d("GHGHG", "getAthleteData: ${e.message.toString()}")
            Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun addAthleteNames(data: ArrayList<AthleteData.Athlete>) {
        try {

        athletelist.removeAllViews()

        for (athlete in data) {
            val trainingPlanView = layoutInflater.inflate(R.layout.athlete_list_item, null)

            var name: TextView = trainingPlanView.findViewById(R.id.tv_program_name)
            val image: ImageView = trainingPlanView.findViewById(R.id.imageView2)

            name.text = athlete.name ?: "No Name"

            val imageUrl = athlete.image
            if (!imageUrl.isNullOrEmpty()) {
                Log.d("AthletesActivity", "Attempting to load image: $imageUrl")

                Picasso.get()
                    .load("https://uat.4trainersapp.com$imageUrl")
                    .fit()
                    .placeholder(R.drawable.app_icon)
                    .error(R.drawable.background_splash_image)
                    .into(image, object : com.squareup.picasso.Callback {
                        override fun onSuccess() {
                            Log.d("AthletesActivity", "Image loaded successfully: $imageUrl")
                        }

                        override fun onError(e: Exception?) {
                            Log.e("AthletesActivity", "Error loading image: $imageUrl", e)
                        }
                    })
            } else {
                Log.e("AthletesActivity", "Invalid or empty image URL for athlete: ${athlete.name}")
                image.setImageResource(R.drawable.app_icon)
            }

            trainingPlanView.setOnClickListener {
                mainId = athlete.id ?: 0
                Name = athlete.name ?: ""

                Log.d("QWQWWQWQ", "addAthleteNames: $mainId")
                Log.d("name","Name:- ${Name}")
                val intent = Intent(this, ViewAetleteActivity::class.java)
                intent.putExtra("MainId", mainId)
                intent.putExtra("Name", Name)
                startActivity(intent)
            }

            athletelist.addView(trainingPlanView)
            trainingPlanLayouts.add(trainingPlanView)
        }
        }catch (e:Exception){
            Log.d("GHGHG", "getAthleteData: ${e.message.toString()}")
            Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }
}