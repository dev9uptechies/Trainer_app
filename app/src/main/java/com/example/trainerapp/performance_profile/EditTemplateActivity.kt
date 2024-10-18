package com.example.trainerapp.performance_profile

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.Adapter.template.edit_template.EditTemplateAdapter
import com.example.OnItemClickListener
import com.example.model.performance_profile.PerformanceProfileData
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.PreferencesManager
import com.example.trainerapp.R
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.ActivityEditTemplateBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditTemplateActivity : AppCompatActivity(), OnItemClickListener.OnItemClickCallback {
    lateinit var editTemplateBinding: ActivityEditTemplateBinding
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    lateinit var preferenceManager: PreferencesManager
    var performData: MutableList<PerformanceProfileData.PerformanceProfile> = mutableListOf()
    lateinit var performanceData: MutableList<PerformanceProfileData.PerformanceProfile>
    lateinit var editTemplateAdapter: EditTemplateAdapter
    var templateId = ""

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
                        Utils.setUnAuthDialog(this@EditTemplateActivity)
                    } else {
                        Toast.makeText(
                            this@EditTemplateActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Toast.makeText(
                        this@EditTemplateActivity,
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
        editTemplateBinding = ActivityEditTemplateBinding.inflate(layoutInflater)
        setContentView(editTemplateBinding.root)
        initViews()
        getTemplateData()
        checkButtonTap()
        initRecycler()
    }

    private fun initRecycler() {
        editTemplateBinding.performanceRly.layoutManager = LinearLayoutManager(this)
        editTemplateAdapter = EditTemplateAdapter(performData, this)
        editTemplateBinding.performanceRly.adapter = editTemplateAdapter
    }

    private fun checkButtonTap() {
        editTemplateBinding.back.setOnClickListener {
            finish()
        }

        editTemplateBinding.createTemplate.setOnClickListener {
            editTemplateBinding.main.setBackgroundColor(resources.getColor(R.color.grey))
            try {
                openDialog()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun openDialog() {
        val dialog = Dialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.add_template_layout, null)
        dialog.setContentView(view)
        dialog.setCancelable(false)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val title = view.findViewById<TextView>(R.id.dialogTitle)
        val editTitle = view.findViewById<TextView>(R.id.editTextTitle)
        val editText = view.findViewById<EditText>(R.id.edtDialogText)
        val addButton = view.findViewById<TextView>(R.id.addButton)
        val cancelButton = view.findViewById<TextView>(R.id.cancelButton)

        title.text = "Add Performance Category"
        editTitle.text = "Performance Category Name :"
        editText.hint = "Enter Category"
        addButton.text = "Add"
        cancelButton.text = "Cancel"

        addButton.setOnClickListener {
            editTemplateBinding.main.setBackgroundColor(resources.getColor(R.color.black))
            if (editText.text.isNullOrEmpty()) {
                Toast.makeText(this, "Add Template Name First", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newPerformanceProfile = PerformanceProfileData.PerformanceProfile().apply {
                id = (performData.size + 1)
                name = editText.text.toString()
            }
            performData.add(newPerformanceProfile)
            Log.d("perform data :-", "$performData")
            dialog.dismiss()
        }

        cancelButton.setOnClickListener {
            editTemplateBinding.main.setBackgroundColor(resources.getColor(R.color.black))
            dialog.dismiss()
        }
        dialog.show()
    }


    private fun getTemplateData() {
        editTemplateBinding.ProgressBar.visibility = View.VISIBLE
        performanceData.clear()
        performData.clear()
        apiInterface.GetPerformanceTemplate().enqueue(object : Callback<PerformanceProfileData> {
            override fun onResponse(
                call: Call<PerformanceProfileData>,
                response: Response<PerformanceProfileData>
            ) {
                editTemplateBinding.ProgressBar.visibility = View.GONE
                Log.d("TAG", response.code().toString() + "")
                val code = response.code()
                if (code == 200) {
                    if (response.isSuccessful && response.body() != null) {
                        Log.d("Get Profile Data ", "${response.body()}")
                        val data = response.body()!!.data
                        for (i in data!!) {
                            performanceData.add(i)
                        }
                        performData.addAll(performanceData.filter { it.id == templateId.toInt() })
                        setData(performData)
                        initRecycler()
                    }
                } else if (code == 403) {
                    Utils.setUnAuthDialog(this@EditTemplateActivity)
                } else {
                    Toast.makeText(
                        this@EditTemplateActivity,
                        "" + response.message(),
                        Toast.LENGTH_SHORT
                    ).show()
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<PerformanceProfileData>, t: Throwable) {
                editTemplateBinding.ProgressBar.visibility = View.GONE
                Toast.makeText(
                    this@EditTemplateActivity,
                    "" + t.message,
                    Toast.LENGTH_SHORT
                )
                    .show()
                call.cancel()
            }

        })
    }

    private fun setData(performData: MutableList<PerformanceProfileData.PerformanceProfile>) {
        try {
            editTemplateBinding.edtAthletes.setText(performData[0].name ?: "")
        } catch (e: Exception) {
            Log.d("Error Exception :", "${e.message}")
        }
    }

    private fun initViews() {
        preferenceManager = PreferencesManager(this)
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        templateId = intent.getStringExtra("TempId")!!
        performanceData = mutableListOf()
    }

    override fun onItemClicked(view: View, position: Int, type: Long, string: String) {
    }
}