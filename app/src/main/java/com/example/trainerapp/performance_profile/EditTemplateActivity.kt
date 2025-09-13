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
import com.example.Adapter.template.profile.PerformanceProfileAdapterCreate
import com.example.OnItemClickListener
import com.example.model.base_class.PerformanceProfileBase
import com.example.model.base_class.PerformanceStar
import com.example.model.newClass.test.ApiResponse
import com.example.model.performance_profile.PerformanceProfileData
import com.example.model.performance_profile.performance.quality.update.TemplateRequest
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.PreferencesManager
import com.example.trainerapp.R
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.ActivityEditTemplateBinding
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditTemplateActivity : AppCompatActivity(), OnItemClickListener.OnItemClickCallback,
    EditTemplateAdapter.OnQualityClick {
    lateinit var editTemplateBinding: ActivityEditTemplateBinding
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    lateinit var preferenceManager: PreferencesManager
    var performData: MutableList<PerformanceProfileData.PerformanceProfile> = mutableListOf()
    lateinit var performanceData: MutableList<PerformanceProfileData.PerformanceProfile>
    lateinit var editTemplateAdapter: EditTemplateAdapter
    private var performanceDataList: MutableList<PerformanceProfileBase> = mutableListOf()
    var templateId = ""
    var athlete_id:Int ?= null
    var From:String = ""
    var tampId = 0
    var starId = 0
    private var qualityIds: List<Int> = emptyList()  // Global variable


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

    }

    private fun checkButtonTap() {
        editTemplateBinding.back.setOnClickListener {
            finish()
        }


        editTemplateBinding.saveCard.setOnClickListener {
            if (editTemplateBinding.edtAthletes.text.isNullOrEmpty()) {
                Toast.makeText(this, "Add Template Name First", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val name = editTemplateBinding.edtAthletes.text.toString().trim()
            val categoryList = performanceDataList.map { category ->
                com.example.model.performance_profile.template.Category(
                    category_name = category.titleName.toString(),
                    qualitiy = category.subTitleName?.map { subTitle ->
                        com.example.model.performance_profile.template.Qualitiy(quality_name = subTitle.title.toString())
                    } ?: emptyList()
                )
            }

            updateTemplateData(name, categoryList)
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

    private fun initRecyclerView(qulityIds:List<Int>) {
        editTemplateBinding.performanceRly.layoutManager = LinearLayoutManager(this)
        editTemplateAdapter = EditTemplateAdapter(performanceDataList, this,  qulityIds,this,this)
        editTemplateBinding.performanceRly.adapter = editTemplateAdapter
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

            val newCategory = PerformanceProfileBase(
                id = tampId,
                titleName = editText.text.toString(),
                subTitleName = mutableListOf()
            )

            tampId++

            val existingCategory = performanceDataList.find { it.titleName == newCategory.titleName }
            if (existingCategory == null) {
                performanceDataList.add(newCategory)
            } else {
                Toast.makeText(this, "Category already exists!", Toast.LENGTH_SHORT).show()
            }

            initRecyclerView(qualityIds)
            Log.d("PERFORMANCE_LIST", "Updated Categories: ${performanceDataList.size}")
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
        performanceDataList.clear()

        apiInterface.GetPerformanceTemplate().enqueue(object : Callback<PerformanceProfileData> {
            override fun onResponse(call: Call<PerformanceProfileData>, response: Response<PerformanceProfileData>) {
                editTemplateBinding.ProgressBar.visibility = View.GONE
                if (response.isSuccessful && response.body() != null) {
                    Log.d("Get Profile Data", "${response.body()}")
                    response.body()?.data?.let { data ->
                        performanceData.addAll(data)
                    }
                    val data = response.body()!!.data
                    performData.addAll(performanceData.filter { it.id == templateId.toInt() })
                    val filteredData = performanceData.filter { it.id == templateId.toInt() }
                    filteredData.forEach { profile ->
                        profile.performanceTemplateCategory?.forEach { category ->
                            performanceDataList.add(
                                PerformanceProfileBase(
                                    id = tampId++,
                                    titleName = category.name ?: "Unknown",
                                    subTitleName = category.performanceTemplateQuality?.map { quality ->
                                        PerformanceStar(
                                            title = quality.name ?: "Unknown" // Map correctly to `title`
                                        )
                                    }?.toMutableList() ?: mutableListOf()
                                )
                            )
                        }

                    }
//                    initRecycler()

                    qualityIds = performData.flatMap { profile ->
                        profile.performanceTemplateCategory?.flatMap { category ->
                            category.performanceTemplateQuality?.mapNotNull { quality -> quality.id } ?: emptyList()
                        } ?: emptyList()
                    }

                    Log.d("SKSKSKSSK", "All Quality IDs: $qualityIds")

                    initRecyclerView(qualityIds)
                    setData(filteredData.toMutableList())  // Convert List to MutableList

                } else {
                    handleError(response.code(), response.message())
                }
            }

            override fun onFailure(call: Call<PerformanceProfileData>, t: Throwable) {
                editTemplateBinding.ProgressBar.visibility = View.GONE
                Log.e("API_ERROR", "Error fetching template data: ${t.message}")
                Toast.makeText(this@EditTemplateActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun handleError(code: Int, message: String) {
        when (code) {
            403 -> Utils.setUnAuthDialog(this@EditTemplateActivity)
            else -> {
                Toast.makeText(this@EditTemplateActivity, "Error: $message", Toast.LENGTH_SHORT).show()
            }
        }
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
        athlete_id = intent.getIntExtra("athlete_id",0)
        From = intent.getStringExtra("From").toString()
        performanceData = mutableListOf()

        Log.d("XVXVXVXVX", "initViews: $athlete_id")
    }

    private fun updateTemplateData(
        name: String,
        list: List<com.example.model.performance_profile.template.Category>
    ) {
        editTemplateBinding.ProgressBar.visibility = View.VISIBLE

        val requestBody = TemplateRequest(
            template_id = templateId.toInt(),
            name = name,
            category = list
        )

        Log.d("REQUEST_BODY", Gson().toJson(requestBody))

        apiInterface.updateTemplate(requestBody).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                editTemplateBinding.ProgressBar.visibility = View.GONE

                if (response.isSuccessful) {
                    Log.d("API_SUCCESS", "Template updated successfully")
                    Toast.makeText(this@EditTemplateActivity, "Updated successfully", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Log.e("API_ERROR", "Update failed: ${response.errorBody()?.string()}")
                    Toast.makeText(this@EditTemplateActivity, "Update failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                editTemplateBinding.ProgressBar.visibility = View.GONE
                Log.e("API_FAILURE", "Network error: ${t.message}")
                Toast.makeText(this@EditTemplateActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onItemClicked(view: View, position: Int, type: Long, string: String) {
        when (string) {
            "add_program" -> {
                Log.d("position :-", "$position \t List Size: ${performanceDataList.size}")

                // Prevent crash by checking list bounds
                if (position < 0 || position >= performanceDataList.size) {
                    Log.e("Error", "Invalid position: $position")
                    return
                }

                try {
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

                    title.text = "Add Quality"
                    editTitle.text = "Performance Quality Name:"
                    editText.hint = "Enter Quality"
                    addButton.text = "Add"
                    cancelButton.text = "Cancel"

                    addButton.setOnClickListener {
                        editTemplateBinding.main.setBackgroundColor(resources.getColor(R.color.black))

                        if (editText.text.isNullOrEmpty()) {
                            Toast.makeText(this, "Add Template Name First", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }

                        val performanceProfile = performanceDataList[position]

                        // Ensure subTitleName is initialized
                        if (performanceProfile.subTitleName == null) {
                            performanceProfile.subTitleName = mutableListOf()
                        }

                        val newStar = PerformanceStar(
                            id = starId,
                            title = editText.text.toString()
                        )

                        // Add new star safely
                        performanceProfile.subTitleName!!.add(newStar)
                        starId++

                        // Notify adapter of the change
                    editTemplateAdapter.notifyItemChanged(position)
                        dialog.dismiss()
                    }

                    cancelButton.setOnClickListener {
                        editTemplateBinding.main.setBackgroundColor(resources.getColor(R.color.black))
                        dialog.dismiss()
                    }

                    dialog.show()
                } catch (e: Exception) {
                    Log.e("Exception", "Error in onItemClicked: ${e.message}")
                }
            }

            "del_program" -> {
                Log.d("postiton :-", "$position \t ${performanceDataList[position]}")
                if (position < performanceDataList.size) { // Check if position is valid
                    // Remove the item from the list
                    performanceDataList.removeAt(position)

                    // Notify the adapter that an item has been removed
                    editTemplateAdapter.notifyItemRemoved(position)
                    editTemplateAdapter.notifyItemRangeChanged(position, performanceDataList.size)
                } else {
                    Log.d("Delete Error", "Invalid position: $position")
                }
            }

            "edit_program" -> {
                Log.d("postiton :-", "$position \t ${performanceDataList[position]}")
                try {
                    val performanceProfile = performanceDataList[position]
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

                    title.text = "Edit Performance Category"
                    editTitle.text = "Performance Category Name :"
                    editText.hint = "Enter Category"
                    editText.setText(performanceProfile.titleName)
                    addButton.text = "Save"
                    cancelButton.text = "Cancel"

                    addButton.setOnClickListener {
                        editTemplateBinding.main.setBackgroundColor(resources.getColor(R.color.black))
                        if (editText.text.isNullOrEmpty()) {
                            Toast.makeText(this, "Add Template Name First", Toast.LENGTH_SHORT)
                                .show()
                            return@setOnClickListener
                        }
                        val updatedProfile =
                            performanceProfile.copy(titleName = editText.text.toString())

                        // Update the list with the new profile
                        performanceDataList[position] = updatedProfile
//                        performanceDataList.add(
//                            PerformanceProfileBase(
//                                id = tampId,
//                                titleName = editText.text.toString()
//                            )
//                        )
                        editTemplateAdapter.notifyItemChanged(position)
                        dialog.dismiss()
                    }

                    cancelButton.setOnClickListener {
                        editTemplateBinding.main.setBackgroundColor(resources.getColor(R.color.black))
                        dialog.dismiss()
                    }
                    dialog.show()
                } catch (e: Exception) {
                    Log.d("Error :-", " " + e.message)
                }
            }
        }
    }

    override fun onQualityClick(position: Int, catPosition: Int?, qualId: Long, type: String, catId: Long) {
        when (type) {
            "edit_quality" -> {
                Log.d("EditQuality", "Editing quality at position $position in category at $catPosition")

                try {
                    editTemplateBinding.main.setBackgroundColor(resources.getColor(R.color.grey))

                    // Get category by position instead of ID
                    val performance = performanceDataList.getOrNull(catPosition ?: -1)
                    if (performance == null) {
                        Log.e("Error", "Category not found at position $catPosition")
                        return
                    }

                    val quility = performance.subTitleName ?: mutableListOf()
                    if (position >= quility.size) {
                        Log.e("Error", "Quality position $position is out of bounds")
                        return
                    }

                    val performanceProfile = quility[position]

                    val dialog = Dialog(this)
                    val view = LayoutInflater.from(this).inflate(R.layout.add_template_layout, null)
                    dialog.setContentView(view)
                    dialog.setCancelable(false)
                    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                    val editText = view.findViewById<EditText>(R.id.edtDialogText)
                    val addButton = view.findViewById<TextView>(R.id.addButton)
                    val cancelButton = view.findViewById<TextView>(R.id.cancelButton)

                    editText.setText(performanceProfile.title)
                    addButton.text = "Save"
                    cancelButton.text = "Cancel"

                    addButton.setOnClickListener {
                        editTemplateBinding.main.setBackgroundColor(resources.getColor(R.color.black))

                        if (editText.text.isNullOrEmpty()) {
                            Toast.makeText(this, "Enter quality name", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }

                        performanceProfile.title = editText.text.toString()
                        quility[position] = performanceProfile
                        performance.subTitleName = quility

                        editTemplateAdapter.notifyItemChanged(catPosition!!)
                        dialog.dismiss()
                    }

                    cancelButton.setOnClickListener {
                        editTemplateBinding.main.setBackgroundColor(resources.getColor(R.color.black))
                        dialog.dismiss()
                    }
                    dialog.show()
                } catch (e: Exception) {
                    Log.d("Exception", "onQualityClick: ${e.message}")
                    e.printStackTrace()
                }
            }

            "delete_quality" -> {
                Log.d("DeleteQuality", "Deleting quality at position $position in category at $catPosition")

                try {
                    editTemplateBinding.main.setBackgroundColor(resources.getColor(R.color.grey))

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
                    editTitle.text = "Are you sure you want to delete this quality?"
                    addButton.text = "Ok"
                    cancelButton.text = "Cancel"

                    addButton.setOnClickListener {
                        val category = performanceDataList.getOrNull(catPosition ?: -1)
                        if (category == null) {
                            Log.e("Error", "Category not found at position $catPosition")
                            return@setOnClickListener
                        }

                        val quility = category.subTitleName ?: mutableListOf()
                        if (position >= quility.size) {
                            Log.e("Error", "Quality position $position is out of bounds")
                            return@setOnClickListener
                        }

                        // Remove quality by position
                        quility.removeAt(position)
                        category.subTitleName = quility

                        editTemplateAdapter.notifyItemChanged(catPosition!!)
                        Log.d("Success", "Quality deleted successfully")

                        editTemplateBinding.main.setBackgroundColor(resources.getColor(R.color.black))
                        dialog.dismiss()
                    }

                    cancelButton.setOnClickListener {
                        editTemplateBinding.main.setBackgroundColor(resources.getColor(R.color.black))
                        dialog.dismiss()
                    }

                    dialog.show()
                } catch (e: Exception) {
                    Log.d("Exception", "Error deleting quality: ${e.message}")
                }
            }
        }
    }
}