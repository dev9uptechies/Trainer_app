package com.example.trainerapp.performance_profile

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.Adapter.template.ProfileTemplateAdapter
import com.example.OnItemClickListener
import com.example.model.base_class.BaseClass
import com.example.model.performance_profile.PerformanceProfileData
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.PreferencesManager
import com.example.trainerapp.R
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.ActivitySelectTemplateBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SelectTemplateActivity : AppCompatActivity(), OnItemClickListener.OnItemClickCallback,
    ProfileTemplateAdapter.OnCheckboxCheckedListener {
    lateinit var selectTemplateBinding: ActivitySelectTemplateBinding
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    lateinit var preferenceManager: PreferencesManager
    lateinit var performanceData: MutableList<PerformanceProfileData.PerformanceProfile>
    lateinit var profileTemplateAdapter: ProfileTemplateAdapter
    private var selectedOptionId: Long? = null
    var athlete_id:Int ?= null
    var From:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectTemplateBinding = ActivitySelectTemplateBinding.inflate(layoutInflater)
        setContentView(selectTemplateBinding.root)
        initViews()
        setProfileTemplateRecycler()
        getTemplatesData()
        checkButtonTap()
    }

    private fun checkButtonTap() {
        selectTemplateBinding.back.setOnClickListener {
            finish()
        }
        selectTemplateBinding.saveCard.setOnClickListener {
            Log.d("SElection ID :-", "$selectedOptionId")
            if (selectedOptionId != null) {
                val cat: ArrayList<PerformanceProfileData.PerformanceProfile> =
                    performanceData.filter { it.id == selectedOptionId!!.toInt() }
                        .toMutableList() as ArrayList<PerformanceProfileData.PerformanceProfile>
                Log.d("Array List :- ", "${cat}")
                for (i in cat) {
                    Log.d("Array List :- ", "${i.name}")
                }
                val resultIntent = Intent()
                resultIntent.putExtra("selected_option_id", selectedOptionId)
                setResult(RESULT_OK, resultIntent)
                finish() // Close the second activity and return the result
            }
        }
    }

    private fun setProfileTemplateRecycler() {
        selectTemplateBinding.performanceRly.layoutManager = LinearLayoutManager(this)
        profileTemplateAdapter = ProfileTemplateAdapter(performanceData, this, this, this)
        selectTemplateBinding.performanceRly.adapter = profileTemplateAdapter
    }

    private fun getTemplatesData() {
        selectTemplateBinding.ProgressBar.visibility = View.VISIBLE
        performanceData.clear()
        apiInterface.GetPerformanceTemplate().enqueue(object : Callback<PerformanceProfileData> {
            override fun onResponse(
                call: Call<PerformanceProfileData>,
                response: Response<PerformanceProfileData>
            ) {
                selectTemplateBinding.ProgressBar.visibility = View.GONE
                Log.d("TAG", response.code().toString())
                val code = response.code()
                if (code == 200) {
                    if (response.isSuccessful && response.body() != null) {
                        Log.d("Get Profile Data ", "${response.body()}")
                        val data = response.body()!!.data
                        if (data != null) { // Check if data is not null
                            for (i in data) {
                                performanceData.add(i)
                            }
                            setProfileTemplateRecycler()
                        } else {
                            // Handle the case where 'data' is null
                            Log.e("getTemplatesData", "Data is null.")
                            Toast.makeText(
                                this@SelectTemplateActivity,
                                "No data available.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else if (code == 403) {
                    Utils.setUnAuthDialog(this@SelectTemplateActivity)
                } else {
                    Toast.makeText(
                        this@SelectTemplateActivity,
                        "" + response.message(),
                        Toast.LENGTH_SHORT
                    ).show()
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<PerformanceProfileData>, t: Throwable) {
                selectTemplateBinding.ProgressBar.visibility = View.GONE
                Toast.makeText(
                    this@SelectTemplateActivity,
                    "" + t.message,
                    Toast.LENGTH_SHORT
                ).show()
                call.cancel()
            }

        })
    }

    private fun initViews() {
        preferenceManager = PreferencesManager(this)
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        performanceData = mutableListOf()
        if (selectedOptionId.toString().isNotEmpty()) {
            updateUI(selectTemplateBinding.saveCard)
        }

        athlete_id = intent.getIntExtra("athlete_id",0)
        From =  intent.getStringExtra("From").toString()

        Log.d("SMSMMSMSMS", "initViews: $athlete_id")

    }

    fun resetData() {
        performanceData.clear()
        setProfileTemplateRecycler()
        getTemplatesData()
    }

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
                        Utils.setUnAuthDialog(this@SelectTemplateActivity)
                    } else {
                        Toast.makeText(
                            this@SelectTemplateActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Toast.makeText(
                        this@SelectTemplateActivity,
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

    override fun onItemClicked(view: View, position: Int, type: Long, string: String) {
        when (string) {
            "View" -> {
                val intent = Intent(this, ViewTemplateActivity::class.java).apply {
                    putExtra("TempId", type.toString())
                }
                startActivity(intent)
            }

            "Edit" -> {
                val intent = Intent(this, EditTemplateActivity::class.java).apply {
                    putExtra("TempId", type.toString())
                    putExtra("CatId", selectedOptionId.toString())
                    putExtra("From", From)
                    putExtra("athlete_id",athlete_id)
                }
                startActivity(intent)
            }

            "Delete" -> {
                openDeleteDialog(type.toInt())
            }

            "AddTemplate" -> {
                Log.d("Temp Id :- ", "$type")
            }
        }
    }

    private fun openDeleteDialog(id: Int) {
        val dialog = Dialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.delete_layout, null)
        dialog.setContentView(view)
        dialog.setCancelable(false)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val title = view.findViewById<TextView>(R.id.dialogTitle)
        val editTitle = view.findViewById<TextView>(R.id.editTextTitle)
        val addButton = view.findViewById<TextView>(R.id.okButton)
        val cancelButton = view.findViewById<TextView>(R.id.cancelButton)

        title.text = "Alert"
        editTitle.text = "Are You Sure you want to Delete ?"
        addButton.text = "Ok"
        cancelButton.text = "Cancel"

        addButton.setOnClickListener {
            selectTemplateBinding.main.setBackgroundColor(resources.getColor(R.color.black))
            deleteTemplate(id)
            dialog.dismiss()
        }

        cancelButton.setOnClickListener {
            selectTemplateBinding.main.setBackgroundColor(resources.getColor(R.color.black))
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun deleteTemplate(id: Int) {
        try {
            selectTemplateBinding.ProgressBar.visibility = View.VISIBLE
            apiInterface.DeletePerformanceTemplate(id).enqueue(object : Callback<BaseClass> {
                override fun onResponse(call: Call<BaseClass>, response: Response<BaseClass>) {
                    selectTemplateBinding.ProgressBar.visibility = View.GONE
                    Log.d("TAG", response.code().toString() + "")
                    val code = response.code()
                    if (code == 200) {
                        if (response.isSuccessful && response.body() != null) {
                            val message = response.body()!!.message
                            Toast.makeText(
                                this@SelectTemplateActivity,
                                "" + message,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            resetData()
                        }
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@SelectTemplateActivity)
                    } else {
                        Toast.makeText(
                            this@SelectTemplateActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<BaseClass>, t: Throwable) {
                    Toast.makeText(
                        this@SelectTemplateActivity,
                        "" + t.message,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    call.cancel()
                }
            })
        } catch (e: Exception) {
            Log.d("Exception :-", "${e.message}")
        }
    }

    override fun onItemChecked(id: Long, name: String, position: Int) {
        Log.d("SElection ID :-", "$id \t $name")
        selectedOptionId = id
        if (selectedOptionId.toString().isNotEmpty()) {
            updateUI(selectTemplateBinding.saveCard)
        }
    }

    private fun updateUI(saveCard: CardView) {
        saveCard.isEnabled = true
        saveCard.setBackgroundResource(R.drawable.card_select_1)
    }


}