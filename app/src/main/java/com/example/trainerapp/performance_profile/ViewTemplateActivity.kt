package com.example.trainerapp.performance_profile

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.Adapter.template.view.ViewTemplateAdapter
import com.example.model.performance_profile.PerformanceProfileData
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.PreferencesManager
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.ActivityViewTemplateBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewTemplateActivity : AppCompatActivity() {
    lateinit var viewTemplateBinding: ActivityViewTemplateBinding
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    lateinit var preferenceManager: PreferencesManager
    var tempId = ""
    lateinit var performanceData: MutableList<PerformanceProfileData.PerformanceProfile>
    lateinit var viewTemplateAdapter: ViewTemplateAdapter
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
                        Log.d("Get Profile Data ", "${response.body()}")
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@ViewTemplateActivity)
                    } else {
                        Toast.makeText(
                            this@ViewTemplateActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Toast.makeText(
                        this@ViewTemplateActivity,
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
        viewTemplateBinding = ActivityViewTemplateBinding.inflate(layoutInflater)
        setContentView(viewTemplateBinding.root)
        initViews()
        checkButtonTap()

    }

    private fun checkButtonTap() {
        viewTemplateBinding.back.setOnClickListener {
            finish()
        }
    }

    private fun initViews() {
        preferenceManager = PreferencesManager(this)
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        tempId = intent.getStringExtra("TempId")!!
        performanceData = mutableListOf()
        getTemplatesData()

    }

    private fun getTemplatesData() {
        viewTemplateBinding.ProgressBar.visibility = View.VISIBLE
        performanceData.clear()
        apiInterface.GetPerformanceTemplate().enqueue(object : Callback<PerformanceProfileData> {
            override fun onResponse(
                call: Call<PerformanceProfileData>,
                response: Response<PerformanceProfileData>
            ) {
                viewTemplateBinding.ProgressBar.visibility = View.GONE
                Log.d("TAG", response.code().toString() + "")
                val code = response.code()
                if (code == 200) {
                    if (response.isSuccessful && response.body() != null) {
                        Log.d("Get Profile Data ", "${response.body()}")
                        val data = response.body()!!.data
                        for (i in data!!) {
                            performanceData.add(i)
                        }
                        setProfileTemplateRecycler(performanceData.filter { it.id == tempId.toInt() }
                            .toMutableList())
                    }
                } else if (code == 403) {
                    Utils.setUnAuthDialog(this@ViewTemplateActivity)
                } else {
                    Toast.makeText(
                        this@ViewTemplateActivity,
                        "" + response.message(),
                        Toast.LENGTH_SHORT
                    ).show()
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<PerformanceProfileData>, t: Throwable) {
                viewTemplateBinding.ProgressBar.visibility = View.GONE
                Toast.makeText(
                    this@ViewTemplateActivity,
                    "" + t.message,
                    Toast.LENGTH_SHORT
                )
                    .show()
                call.cancel()
            }

        })
    }

    fun setProfileTemplateRecycler(data: MutableList<PerformanceProfileData.PerformanceProfile>) {
        viewTemplateBinding.edtAthletes.setText(data[0].name ?: "")
        viewTemplateBinding.performanceRly.layoutManager = LinearLayoutManager(this)
        viewTemplateAdapter = ViewTemplateAdapter(data, this)
        viewTemplateBinding.performanceRly.adapter = viewTemplateAdapter
    }

}