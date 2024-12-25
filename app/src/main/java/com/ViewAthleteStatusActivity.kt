package com

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.AthleteDataAdapter
import com.example.OnItemClickListener
import com.example.model.AthleteDataPackage.AthleteDatas
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.ActivityViewAthleteStatusBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewAthleteStatusActivity : AppCompatActivity(), OnItemClickListener.OnItemClickCallback  {

    private lateinit var binding: ActivityViewAthleteStatusBinding
    private lateinit var apiInterface: APIInterface
    private var athleteData: MutableList<AthleteDatas.AthleteList> = mutableListOf()
    private lateinit var adapter: AthleteDataAdapter

    private var mainId: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewAthleteStatusBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeComponents()
        setListeners()
        fetchAthleteDetails(mainId)

    }

    private fun initializeComponents() {
        apiInterface = APIClient(this).client().create(APIInterface::class.java)
        mainId = intent.getIntExtra("MainId", 0)
        Log.d("MainId","MainID:- "+ mainId)
    }

    private fun setListeners() {
        binding.back.setOnClickListener { finish() }
    }

    private fun fetchAthleteDetails(id: Int) {
        try {

            binding.progressbar.visibility = View.VISIBLE

            athleteData.clear()

            apiInterface.GetAthleteData(id)
                .enqueue(object : Callback<AthleteDatas> {
                    override fun onResponse(
                        call: Call<AthleteDatas>,
                        response: Response<AthleteDatas>
                    ) {
                        binding.progressbar.visibility = View.GONE


                        Log.d("TAG", response.code().toString() + "")
                        val code = response.code()
                        if (code == 200) {
                            if (response.isSuccessful && response.body() != null) {
                                val data = response.body()?.athleteData

                                if (data != null) {
                                    for (item in data) {
                                        Log.d("DATA_ITEM", "Id: ${item.id}")
                                        Log.d("DATA_ITEM", "Name: ${item.athleteId}")
                                        Log.d("DATA_ITEM", "Start Date: ${item.fatMass}")
                                        Log.d("DATA_ITEM", "End Date: ${item.athleteId}")
                                        Log.d("DATA_ITEM", "Mesocycle: ${item.baseline}")
                                    }
                                }

                                if (data!!.isNotEmpty()) {
                                    if (data != null) {
                                        athleteData.addAll(data)
                                    }
                                    //seasonId = data[0].id!!
                                    setAdapter()

                                } else {
                                    Log.e("DATA_TAG", "No Data Available")
//                                viewTrainingPlanBinding.add.setOnClickListener {
//                                    showToast("Please Add Mesocycle")
//                                }
                                }
                            } else {
                                Log.d("DATA_TAG", "No Data Available")
                                Toast.makeText(this@ViewAthleteStatusActivity, "Error: ${response.code()} - ${response.message()}", Toast.LENGTH_SHORT).show()
                            }
                        } else if (code == 403) {
                            Utils.setUnAuthDialog(this@ViewAthleteStatusActivity)
                        } else {
                            Toast.makeText(
                                this@ViewAthleteStatusActivity,
                                "" + response.message(),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            call.cancel()
                        }
                    }

                    override fun onFailure(call: Call<AthleteDatas>, t: Throwable) {
                        Log.d("TAG Category", t.message.toString())
                        Toast.makeText(this@ViewAthleteStatusActivity, "Api Call Failed: ${t.message.toString()}", Toast.LENGTH_SHORT).show()
                        call.cancel()
                    }
                })
        }catch (e:Exception){
            Log.d("error",e.message.toString())
        }
    }

    private fun setAdapter() {
        try {

            if (athleteData.isNotEmpty()) {
                binding.recyclerView.visibility = View.VISIBLE
                binding.recyclerView.layoutManager = LinearLayoutManager(this)
                adapter = AthleteDataAdapter(athleteData, this, this)
                binding.recyclerView.adapter = adapter
            } else {
                binding.recyclerView.visibility = View.GONE
                Toast.makeText(this, "No data available for the selected training plan.", Toast.LENGTH_SHORT).show()
            }
        }catch (e:Exception){
            Log.d("Exss",e.message.toString())
        }
    }

    override fun onItemClicked(view: View, position: Int, type: Long, string: String) {
        TODO("Not yet implemented")
    }

}
