package com.example.trainerapp.performance_profile

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.Adapter.template.profile.PerformanceProfileAdapterCreate
import com.example.OnItemClickListener
import com.example.model.base_class.PerformanceBase
import com.example.model.base_class.PerformanceProfileBase
import com.example.model.base_class.PerformanceStar
import com.example.model.performance_profile.template.Category
import com.example.model.performance_profile.template.CreateTemplate
import com.example.model.performance_profile.template.Qualitiy
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.PreferencesManager
import com.example.trainerapp.R
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.ActivityCreateTemplateBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateTemplateActivity : AppCompatActivity(), OnItemClickListener.OnItemClickCallback,
    PerformanceProfileAdapterCreate.OnQualityClick {
    lateinit var createTemplateBinding: ActivityCreateTemplateBinding
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    lateinit var preferenceManager: PreferencesManager
    lateinit var performanceAdapter: PerformanceProfileAdapterCreate
    private var performanceDataList: MutableList<PerformanceProfileBase> = mutableListOf()
    var tampId = 0
    var starId = 0

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
                        Utils.setUnAuthDialog(this@CreateTemplateActivity)
                    } else {
                        Toast.makeText(
                            this@CreateTemplateActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Toast.makeText(
                        this@CreateTemplateActivity,
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
        createTemplateBinding = ActivityCreateTemplateBinding.inflate(layoutInflater)
        setContentView(createTemplateBinding.root)
        initViews()
        updateAddButtonState(createTemplateBinding.saveCard)
        checkButtonTap()
        checkTextWatcher()
        initRecyclerView()
    }

    private fun checkTextWatcher() {
        createTemplateBinding.edtAthletes.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                updateAddButtonState(createTemplateBinding.saveCard)
            }
        })
    }

    private fun initRecyclerView() {
        createTemplateBinding.performanceRly.layoutManager = LinearLayoutManager(this)
        performanceAdapter = PerformanceProfileAdapterCreate(performanceDataList, this, this, this)
        createTemplateBinding.performanceRly.adapter = performanceAdapter
    }

    private fun checkButtonTap() {
        createTemplateBinding.createTemplate.setOnClickListener {
            createTemplateBinding.main.setBackgroundColor(resources.getColor(R.color.grey))
            try {
                openDialog()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        createTemplateBinding.back.setOnClickListener { finish() }

        createTemplateBinding.saveCard.setOnClickListener {
            if (createTemplateBinding.edtAthletes.text.isNullOrEmpty()) {
                Toast.makeText(this, "Add Template Name First", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                val name = createTemplateBinding.edtAthletes.text.toString().trim()
                val list: MutableList<Category> = mutableListOf()

                for (i in performanceDataList) {
                    val qualityList = mutableListOf<Qualitiy>()
                    i.subTitleName?.forEach { k ->
                        qualityList.add(Qualitiy(quality_name = k.title.toString()))
                    }
                    list.add(
                        Category(
                            category_name = i.titleName.toString(),
                            qualitiy = qualityList
                        )
                    )
                }
                try {
                    saveTemplate(name, list)
                } catch (e: Exception) {
                    Log.d("Error Create :-", "${e.message}")
                }
            }
        }

    }

    private fun saveTemplate(name: String, list: MutableList<Category>) {
        createTemplateBinding.ProgressBar.visibility = View.VISIBLE
        val createTemplate = CreateTemplate(
            name = name,
            category = list
        )
        apiInterface.CreatePerformanceTemplate(createTemplate)
            .enqueue(object : Callback<PerformanceBase> {
                override fun onResponse(
                    call: Call<PerformanceBase>,
                    response: Response<PerformanceBase>
                ) {
                    createTemplateBinding.ProgressBar.visibility = View.GONE
                    Log.d("TAG", response.code().toString() + "")
                    Log.d("Get Template Data ", response.body().toString() + "")
                    Log.d("Get Template Data ", response.message() + "")
                    val code = response.code()
                    if (code == 200) {
                        Log.d("Get Template Data ", "${response.body()}")
                        Log.d("Get Template Data ", "${response.code()}")
                        Log.d("Get Template Data ", "${response.message()}")

                        if (response.isSuccessful) {
                            Toast.makeText(
                                this@CreateTemplateActivity,
                                "Template Created Successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        }
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@CreateTemplateActivity)
                    } else {
                        Toast.makeText(
                            this@CreateTemplateActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<PerformanceBase>, t: Throwable) {
                    createTemplateBinding.ProgressBar.visibility = View.GONE
                    Log.d("Get Template Data", "" + t.message)
                    Toast.makeText(
                        this@CreateTemplateActivity,
                        "" + t.message,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    call.cancel()
                }

            })
    }

    private fun areAllFieldsFilled(): Boolean {
        return !(createTemplateBinding.edtAthletes.text.isNullOrEmpty())
    }

    private fun updateAddButtonState(addButton: CardView) {
        if (areAllFieldsFilled()) {
            addButton.isEnabled = true
            addButton.setCardBackgroundColor(resources.getColor(R.color.splash_text_color)) // Change to your desired color
        } else {
            addButton.isEnabled = false
            addButton.setCardBackgroundColor(resources.getColor(R.color.grey))
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
            createTemplateBinding.main.setBackgroundColor(resources.getColor(R.color.black))
            if (editText.text.isNullOrEmpty()) {
                Toast.makeText(this, "Add Template Name First", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            performanceDataList.add(
                PerformanceProfileBase(
                    id = tampId,
                    titleName = editText.text.toString()
                )
            )
            tampId++
            initRecyclerView()
            dialog.dismiss()
        }

        cancelButton.setOnClickListener {
            createTemplateBinding.main.setBackgroundColor(resources.getColor(R.color.black))
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun initViews() {
        preferenceManager = PreferencesManager(this)
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
    }

    override fun onItemClicked(view: View, position: Int, type: Long, string: String) {
        Log.d("On Item Click :- ", "$string")
        when (string) {
            "add_program" -> {
                Log.d("postiton :-", "$position \t ${performanceDataList[position]}")
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

                    title.text = "Add Quailty"
                    editTitle.text = "Performance Quailty Name :"
                    editText.hint = "Enter Quailty"
                    addButton.text = "Add"
                    cancelButton.text = "Cancel"

                    addButton.setOnClickListener {
                        createTemplateBinding.main.setBackgroundColor(resources.getColor(R.color.black))
                        if (editText.text.isNullOrEmpty()) {
                            Toast.makeText(this, "Add Template Name First", Toast.LENGTH_SHORT)
                                .show()
                            return@setOnClickListener
                        }
                        val performanceProfile = performanceDataList[position]
                        if (performanceProfile.subTitleName == null) {
                            performanceProfile.subTitleName = mutableListOf()
                        }
//                        performanceProfile.subTitleName.add()
                        val newStar = PerformanceStar(
                            id = starId,
                            title = editText.text.toString(), // Use the entered title
                        )

                        // If subTitleName is not null, add the new star, else create a list
                        try {
                            if (newStar != null) {
                                performanceProfile.subTitleName!!.add(newStar)
                            }
                        } catch (e: Exception) {
                            Log.d("Exception :-", "${e.message}")
                        }
                        starId++

                        // Notify the adapter of the change
                        performanceAdapter.notifyItemChanged(position)
                        dialog.dismiss()
//                        initRecyclerView()
//                        dialog.dismiss()
                    }

                    cancelButton.setOnClickListener {
                        createTemplateBinding.main.setBackgroundColor(resources.getColor(R.color.black))
                        dialog.dismiss()
                    }
                    dialog.show()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            "del_program" -> {
                Log.d("postiton :-", "$position \t ${performanceDataList[position]}")
                if (position < performanceDataList.size) { // Check if position is valid
                    // Remove the item from the list
                    performanceDataList.removeAt(position)

                    // Notify the adapter that an item has been removed
                    performanceAdapter.notifyItemRemoved(position)
                    performanceAdapter.notifyItemRangeChanged(position, performanceDataList.size)
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
                        createTemplateBinding.main.setBackgroundColor(resources.getColor(R.color.black))
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
                        performanceAdapter.notifyItemChanged(position)
                        dialog.dismiss()
                    }

                    cancelButton.setOnClickListener {
                        createTemplateBinding.main.setBackgroundColor(resources.getColor(R.color.black))
                        dialog.dismiss()
                    }
                    dialog.show()
                } catch (e: Exception) {
                    Log.d("Error :-", " " + e.message)
                }
            }

        }
    }

    override fun onQualityClick(
        position: Int,
        catPosition: Int?,
        qualId: Long,
        type: String,
        catId: Long
    ) {
        when (type) {
            "edit_quality" -> {
                Log.d(
                    "Quality Data :-",
                    "quality Id - ${qualId}\n category Id - ${catId}}"
                )

                try {
                    createTemplateBinding.main.setBackgroundColor(resources.getColor(R.color.grey))
                    val performance = performanceDataList.firstOrNull { it.id == catId.toInt() }
                    val quility = performance?.subTitleName ?: mutableListOf()
                    val qualityIndex = quility.indexOfFirst { it.id == qualId.toInt() }
                    if (qualityIndex == -1) {
                        Log.e("Error", "Quality not found")
                        return@onQualityClick
                    }

                    val performanceProfile = quility[qualityIndex]

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

                    title.text = "Edit Quailty"
                    editTitle.text = "Performance Quailty Name :"
                    editText.hint = "Enter Quailty"
                    editText.setText(performanceProfile.title)
                    addButton.text = "Save"
                    cancelButton.text = "Cancel"

                    addButton.setOnClickListener {

                        createTemplateBinding.main.setBackgroundColor(resources.getColor(R.color.black))
                        if (editText.text.isNullOrEmpty()) {
                            Toast.makeText(this, "Add Template Name First", Toast.LENGTH_SHORT)
                                .show()
                            return@setOnClickListener
                        }

                        val updatedProfile =
                            performanceProfile.copy(title = editText.text.toString())

                        quility[qualityIndex] = updatedProfile
                        performance!!.subTitleName = quility

                        performanceAdapter.notifyItemChanged(catPosition!!)
                        dialog.dismiss()
                    }

                    cancelButton.setOnClickListener {
                        createTemplateBinding.main.setBackgroundColor(resources.getColor(R.color.black))
                        dialog.dismiss()
                    }
                    dialog.show()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            "delete_quality" -> {
                Log.d("Quality Data :-", "quality Id - ${qualId}\n category Id - ${catId}")
                try {
                    createTemplateBinding.main.setBackgroundColor(resources.getColor(R.color.grey))
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

                        val category = performanceDataList.firstOrNull { it.id == catId.toInt() }
                        if (category == null) {
                            Log.e("Error", "Category not found")
                            return@setOnClickListener
                        }

                        val qualityIndex =
                            category.subTitleName?.indexOfFirst { it.id == qualId.toInt() } ?: -1
                        if (qualityIndex == -1) {
                            Log.e("Error", "Quality not found")
                            return@setOnClickListener
                        }

                        category.subTitleName?.removeAt(qualityIndex)

                        performanceAdapter.notifyItemChanged(catPosition!!)

                        Log.d("Success", "Quality deleted successfully")

                        createTemplateBinding.main.setBackgroundColor(resources.getColor(R.color.black))
                        dialog.dismiss()
                    }

                    cancelButton.setOnClickListener {
                        createTemplateBinding.main.setBackgroundColor(resources.getColor(R.color.black))
                        dialog.dismiss()
                    }
                    dialog.show()
                } catch (e: Exception) {
                    Log.d("Exception :- ", "${e.message}")
                }
            }
        }
    }
}