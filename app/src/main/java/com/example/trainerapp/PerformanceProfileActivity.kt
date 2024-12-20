package com.example.trainerapp

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ListView
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.Adapter.template.profile.PerformanceProfileAdapter
import com.example.OnItemClickListener
import com.example.model.SelectedValue
import com.example.model.base_class.BaseClass
import com.example.model.newClass.athlete.AthleteData
import com.example.model.performance_profile.PerformanceProfileData
import com.example.model.performance_profile.performance.category.PerformanceCategory
import com.example.model.performance_profile.performance.category.PerformanceCategoryData
import com.example.model.performance_profile.performance.category.add_cat_response.PerformanceCategoryAdd
import com.example.model.performance_profile.performance.quality.PerformanceQuality
import com.example.model.performance_profile.performance.quality.PerformanceQualityData
import com.example.model.performance_profile.performance.quality.add.AddQuality
import com.example.model.performance_profile.performance.quality.add.Quality
import com.example.model.performance_profile.performance.quality.add_qual_response.PerformanceQualityAdd
import com.example.model.performance_profile.performance.quality.update.UpdateQuality
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.databinding.ActivityPerformanceProfileBinding
import com.example.trainerapp.performance_profile.CreateTemplateActivity
import com.example.trainerapp.performance_profile.SelectTemplateActivity
import com.example.trainerapp.performance_profile.view_all_graph.ViewAllPerformanceProfileActivity
import com.example.trainerapp.performance_profile.view_average_graph.ViewPerformanesProfileAverageActivity
import com.example.trainerapp.performance_profile.view_graph.ViewPerformanceProfileActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PerformanceProfileActivity : AppCompatActivity(), OnItemClickListener.OnItemClickCallback,
    PerformanceProfileAdapter.OnQualityClick {
    lateinit var performanceProfileBinding: ActivityPerformanceProfileBinding
    lateinit var adapter: PerformanceProfileAdapter
    var athleteId = SelectedValue(null)
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    lateinit var preferenceManager: PreferencesManager
    lateinit var athleteData: ArrayList<AthleteData.Athlete>
    lateinit var performanceData: MutableList<PerformanceProfileData.PerformanceProfile>
    lateinit var performanceTempData: MutableList<PerformanceProfileData.PerformanceProfile>
    lateinit var tempData: MutableList<PerformanceProfileData.PerformanceProfile>
    lateinit var categoryData: MutableList<PerformanceCategoryData>
    lateinit var qualityData: MutableList<PerformanceQualityData>

    private var selectedOptionId: Long? = null
    var aid:Int = 0

    private lateinit var secondActivityLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        performanceProfileBinding = ActivityPerformanceProfileBinding.inflate(layoutInflater)
        setContentView(performanceProfileBinding.root)

        try {
        initViews()
        resetData()
        checkButtonClick()

        secondActivityLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    selectedOptionId = result.data?.getLongExtra("selected_option_id", -1L)
                    Log.d("select Option Id - ", "$selectedOptionId")
                    if (selectedOptionId != null && selectedOptionId != -1L) {
                        tempData =
                            performanceTempData.filter { it.id == selectedOptionId!!.toInt() }
                                .toMutableList()
                        for (i in tempData) {
                            Log.d("Temp data = ", "${i.name}")
                        }
                        for (i in tempData) {
                            for (j in i.performanceTemplateCategory!!) {
                                addCategorySelect(j.name!!, j.performanceTemplateQuality!!)
                            }
                        }
                    }
                }
            }

        performanceProfileBinding.back.setOnClickListener {
            finish()
        }

        performanceProfileBinding.createPerformance.setOnClickListener {
            openBottomSheet()
        }

        performanceProfileBinding.viewQualityGraph.setOnClickListener {
            startActivity(Intent(this, ViewAllPerformanceProfileActivity::class.java).apply {
                putExtra("athleteId", "${athleteId.id}")
                putExtra("catName", "All Area Performance")
            })
        }
        performanceProfileBinding.viewAverageGraph.setOnClickListener {
            startActivity(Intent(this, ViewPerformanesProfileAverageActivity::class.java).apply {
                putExtra("athleteId", "${athleteId.id}")
                putExtra("catName", "All Area Performance")
            })
        }
        }catch (e:Exception){
            Log.d("errroOnCreate",e.message.toString())
        }
    }

    private fun addCategorySelect(
        catName: String,
        quality: List<PerformanceProfileData.PerformanceTemplateQuality>
    ) {
        try {
            val quality1 = mutableListOf<Quality>()
            for (i in quality) {
                quality1.add(
                    Quality(
                        athelet_score = i.atheletScore ?: 0,
                        coach_score = i.coachScore ?: 0,
                        quality_name = i.name
                    )
                )
            }

            Log.d("Quality :-", "$quality1")
            performanceProfileBinding.ProgressBar.visibility = View.VISIBLE
            apiInterface.AddPerformanceCategory(athleteId.id, catName)
                .enqueue(object : Callback<PerformanceCategoryAdd> {
                    override fun onResponse(
                        call: Call<PerformanceCategoryAdd>,
                        response: Response<PerformanceCategoryAdd>
                    ) {
                        performanceProfileBinding.ProgressBar.visibility = View.GONE
                        Log.d("TAG", response.code().toString() + "")
                        val code = response.code()
                        if (code == 200) {
                            if (response.isSuccessful) {
                                val data = response.body()!!.data
                                Log.d("Category Id :-", "$data")

                                var addQuality = AddQuality()
                                addQuality = AddQuality(
                                    athlete_id = athleteId.id,
                                    performance_category_id = data!!.id,
                                    qualities = quality1
                                )
                                try {
                                    apiInterface.AddPerformanceQuality(
                                        addQuality
                                    ).enqueue(
                                        object : Callback<PerformanceQualityAdd> {
                                            override fun onResponse(
                                                call: Call<PerformanceQualityAdd>,
                                                response: Response<PerformanceQualityAdd>
                                            ) {
                                                performanceProfileBinding.ProgressBar.visibility =
                                                    View.GONE
                                                Log.d("TAG", response.code().toString() + "")
                                                val code = response.code()
                                                if (code == 200) {
                                                    if (response.isSuccessful) {

                                                        loadPerformance(athleteId.id!!)
                                                    }
                                                } else if (code == 403) {
                                                    Utils.setUnAuthDialog(this@PerformanceProfileActivity)
                                                } else {

                                                    call.cancel()
                                                }
                                            }

                                            override fun onFailure(
                                                call: Call<PerformanceQualityAdd>,
                                                t: Throwable
                                            ) {
                                                performanceProfileBinding.ProgressBar.visibility =
                                                    View.GONE
                                                Toast.makeText(
                                                    this@PerformanceProfileActivity,
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
                        } else if (code == 403) {
                            Utils.setUnAuthDialog(this@PerformanceProfileActivity)
                        } else {
                            Toast.makeText(
                                this@PerformanceProfileActivity,
                                "" + response.message(),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            call.cancel()
                        }
                    }

                    override fun onFailure(call: Call<PerformanceCategoryAdd>, t: Throwable) {
                        performanceProfileBinding.ProgressBar.visibility = View.GONE
                        Toast.makeText(
                            this@PerformanceProfileActivity,
                            "" + t.message,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }

                })
        } catch (e: Exception) {
            Log.d("Error :-", "${e.message}")
        }
    }

    private fun openBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_layout, null)

        // Set the view to the bottom sheet dialog
        bottomSheetDialog.setContentView(view)

        bottomSheetDialog.behavior.isDraggable = true
        (bottomSheetDialog.findViewById<View>(R.id.design_bottom_sheet) as? FrameLayout)?.background =
            ContextCompat.getDrawable(this, R.drawable.rounded_bottom_sheet_background)

        // Initialize the views
        val createPerformanceTemplate =
            view.findViewById<TextView>(R.id.createPerformanceTemplate)
        val selectTemplate = view.findViewById<TextView>(R.id.selectTemplate)
        val createCustomProfile = view.findViewById<TextView>(R.id.createCustomProfile)
        val cancelButton = view.findViewById<TextView>(R.id.cancelButton)

        // Set onClick listeners for the options
        createPerformanceTemplate.setOnClickListener {
            // Handle the click for "Create Performance Template"
            bottomSheetDialog.dismiss()
            startActivity(
                Intent(
                    this@PerformanceProfileActivity,
                    CreateTemplateActivity::class.java
                )
            )
        }

        selectTemplate.setOnClickListener {
            // Handle the click for "Select Template"
            Log.d("TAG", "Select Template :- ${performanceProfileBinding.edtAthletes.text}")
            bottomSheetDialog.dismiss()
            if (performanceProfileBinding.edtAthletes.text.isNullOrEmpty()) {
                Toast.makeText(this, "Please Select Athlete", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(this, SelectTemplateActivity::class.java)
            secondActivityLauncher.launch(intent)

        }

        createCustomProfile.setOnClickListener {
            openDialog()
            bottomSheetDialog.dismiss()
        }

        cancelButton.setOnClickListener {
            // Handle the click for "Cancel"
            bottomSheetDialog.dismiss()
        }

        // Show the bottom sheet dialog
        bottomSheetDialog.show()
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
            performanceProfileBinding.main.setBackgroundColor(resources.getColor(R.color.black))
            if (editText.text.isNullOrEmpty()) {
                Toast.makeText(this, "Add Template Name First", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            addCategory(editText.text.toString())
            dialog.dismiss()
        }

        cancelButton.setOnClickListener {
            performanceProfileBinding.main.setBackgroundColor(resources.getColor(R.color.black))
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun addCategory(catName: String) {
        try {
            performanceProfileBinding.ProgressBar.visibility = View.VISIBLE
            apiInterface.AddPerformanceCategory(athleteId.id, catName)
                .enqueue(object : Callback<PerformanceCategoryAdd> {
                    override fun onResponse(
                        call: Call<PerformanceCategoryAdd>,
                        response: Response<PerformanceCategoryAdd>
                    ) {
                        performanceProfileBinding.ProgressBar.visibility = View.GONE
                        Log.d("TAG", response.code().toString() + "")
                        val code = response.code()
                        if (code == 200) {
                            if (response.isSuccessful) {
                                Toast.makeText(
                                    this@PerformanceProfileActivity,
                                    "" + response.message(),
                                    Toast.LENGTH_SHORT
                                ).show()
                                loadPerformance(athleteId.id!!)
                            }
                        } else if (code == 403) {
                            Utils.setUnAuthDialog(this@PerformanceProfileActivity)
                        } else {
                            Toast.makeText(
                                this@PerformanceProfileActivity,
                                "" + response.message(),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            call.cancel()
                        }
                    }

                    override fun onFailure(call: Call<PerformanceCategoryAdd>, t: Throwable) {
                        performanceProfileBinding.ProgressBar.visibility = View.GONE
                        Toast.makeText(
                            this@PerformanceProfileActivity,
                            "" + t.message,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }

                })
        } catch (e: Exception) {
            Log.d("Error :-", "${e.message}")
        }
    }

    private fun checkButtonClick() {
        performanceProfileBinding.edtAthletes.setOnClickListener {
            val list = athleteData.map { it.name }
            Log.d("ida","${it.id}")
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
                    this@PerformanceProfileActivity,
                    R.drawable.popup_background
                )
            )
            popupWindow.elevation = 10f

            val listView = popupView.findViewById<ListView>(R.id.listView)

            val adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list) {
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
                performanceProfileBinding.edtAthletes.setText(selectedItem)
                updateUI(
                    performanceProfileBinding.viewAverageGraph,
                    performanceProfileBinding.viewQualityGraph
                )
                athleteId.id = athleteData.filter { it.name == selectedItem }.first().id!!
                Log.d("idl","${it.id}")
                println("Selected item: $selectedItem")
                popupWindow.dismiss()
                categoryData.clear()
                qualityData.clear()
                adapter.notifyDataSetChanged()
                loadPerformance(athleteId.id!!)

                //setRecyclerView(athleteId.id!!)
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

    private fun updateUI(qualityCard: CardView, averageGraph: CardView) {
        if (performanceProfileBinding.edtAthletes.text.toString() != "") {
            qualityCard.isEnabled = true
            qualityCard.setBackgroundResource(R.drawable.card_select_1)

            averageGraph.isEnabled = true
            averageGraph.setBackgroundResource(R.drawable.card_select_1)

            performanceProfileBinding.edtAthletes.visibility = View.VISIBLE

        }
    }
    private fun updateUI2(qualityCard: CardView, averageGraph: CardView) {
            qualityCard.isEnabled = true
            qualityCard.setBackgroundResource(R.drawable.card_select_1)

            averageGraph.isEnabled = true
            averageGraph.setBackgroundResource(R.drawable.card_select_1)

        performanceProfileBinding.edtAthletes.visibility = View.GONE
    }

    private fun loadPerformance(id: Int) {
        try {
//            tempData.clear()
//            categoryData.clear()
//            qualityData.clear()
            performanceProfileBinding.ProgressBar.visibility = View.VISIBLE
            apiInterface.GetPerformanceCategory(id = id)
                .enqueue(object : Callback<PerformanceCategory> {
                    override fun onResponse(
                        call: Call<PerformanceCategory>,
                        response: Response<PerformanceCategory>
                    ) {
                        performanceProfileBinding.ProgressBar.visibility = View.GONE
                        Log.d("TAG", response.code().toString() + "")
                        val code = response.code()
                        if (code == 200) {
                            if (response.isSuccessful) {
                                val data = response.body()!!.data ?: mutableListOf()

                                val addedCategoryIds = categoryData.map { it.id }.toMutableSet()

                                for (i in data) {
                                    // Only add if the category is not already added
                                    if (!addedCategoryIds.contains(i.id)) {
                                        Log.d("Category Data :-", "${i.id} \t ${i.name}")
                                        categoryData.add(i)
                                        addedCategoryIds.add(i.id) // Mark as added
                                    }
                                }

                                if (categoryData.isNotEmpty()) {
                                    initRecyclerView()

                                    // Now load performance quality only after categories are loaded
                                    categoryData.forEach {
                                        Log.d("idp","${it.id}")
                                        loadPerformanceQuality(id, it.id)
                                    }
                                } else {
                                    performanceProfileBinding.performanceRly.visibility = View.GONE
                                }
                            }
                        } else if (code == 403) {
                            Utils.setUnAuthDialog(this@PerformanceProfileActivity)
                        } else {
                            Toast.makeText(
                                this@PerformanceProfileActivity,
                                "" + response.message(),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            call.cancel()
                        }
                    }

                    override fun onFailure(call: Call<PerformanceCategory>, t: Throwable) {
                        performanceProfileBinding.ProgressBar.visibility = View.GONE
                        Toast.makeText(
                            this@PerformanceProfileActivity,
                            "" + t.message,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                })
        } catch (e: Exception) {
            performanceProfileBinding.ProgressBar.visibility = View.GONE
            Log.d("Exception :- ", "${e.message}")
        }
    }




//    private fun initRecyclerView() {
//        performanceProfileBinding.performanceRly.layoutManager = LinearLayoutManager(this)
//        if (categoryData.isNotEmpty()) {
//            performanceProfileBinding.performanceRly.visibility = View.VISIBLE
//            adapter = PerformanceProfileAdapter(categoryData, qualityData, this, this, this)
//            performanceProfileBinding.performanceRly.adapter = adapter
//        } else {
//            performanceProfileBinding.performanceRly.visibility = View.GONE
//            adapter = PerformanceProfileAdapter(mutableListOf(), mutableListOf(), this, this, this)
//            performanceProfileBinding.performanceRly.adapter = adapter
//        }
//    }

    private fun initRecyclerView() {
        performanceProfileBinding.performanceRly.layoutManager = LinearLayoutManager(this)

        if (categoryData.isNotEmpty()) {
            // Show RecyclerView and hide "No Data Found" message
            performanceProfileBinding.performanceRly.visibility = View.VISIBLE
            performanceProfileBinding.tvNodata.visibility = View.GONE

            adapter = PerformanceProfileAdapter(categoryData, qualityData, this, this, this)
            performanceProfileBinding.performanceRly.adapter = adapter
        } else {
            // Hide RecyclerView and show "No Data Found" message
            performanceProfileBinding.performanceRly.visibility = View.GONE
            performanceProfileBinding.tvNodata.visibility = View.VISIBLE
        }
    }


//    private fun loadPerformanceQuailty(id: Int, performId: Int?) {
//        try {
//            performanceProfileBinding.ProgressBar.visibility = View.VISIBLE
//            apiInterface.GetPerformanceQuality(id = id, performId = performId)
//                .enqueue(object : Callback<PerformanceQuality> {
//                    override fun onResponse(
//                        call: Call<PerformanceQuality>,
//                        response: Response<PerformanceQuality>
//                    ) {
//                        performanceProfileBinding.ProgressBar.visibility = View.GONE
//                        Log.d("TAG", response.code().toString() + "")
//                        val code = response.code()
//                        if (code == 200) {
//                            if (response.isSuccessful) {
//                                val data = response.body()!!.data ?: mutableListOf()
//
//                                val addedQualityIds = qualityData.map { it.id }.toMutableSet()
//
//                                for (quality in data) {
//                                    if (!addedQualityIds.contains(quality.id)) {
//                                        qualityData.add(quality)
//                                        addedQualityIds.add(quality.id) // Mark as added
//                                    }
//                                }
//
//                                initRecyclerView()
//
////                                qualityData.addAll(data)
//                            }
////                            if (qualityData.isNotEmpty()) {
////                                adapter.notifyDataSetChanged()
////                            }
//                        } else if (code == 403) {
//                            Utils.setUnAuthDialog(this@PerformanceProfileActivity)
//                        } else {
//                            Toast.makeText(
//                                this@PerformanceProfileActivity,
//                                "" + response.message(),
//                                Toast.LENGTH_SHORT
//                            )
//                                .show()
//                            call.cancel()
//                        }
//                    }
//
//                    override fun onFailure(call: Call<PerformanceQuality>, t: Throwable) {
//                        performanceProfileBinding.ProgressBar.visibility = View.GONE
//                        Toast.makeText(
//                            this@PerformanceProfileActivity,
//                            "" + t.message,
//                            Toast.LENGTH_SHORT
//                        )
//                            .show()
//                        call.cancel()
//                    }
//                })
//        } catch (e: Exception) {
//            performanceProfileBinding.ProgressBar.visibility = View.GONE
//            Log.d("Exception :- ", "${e.message}")
//        }
//    }

    private fun loadPerformanceQuality(id: Int, performId: Int?) {
        performanceProfileBinding.ProgressBar.visibility = View.VISIBLE
        apiInterface.GetPerformanceQuality(id = id, performId = performId)
            .enqueue(object : Callback<PerformanceQuality> {
                override fun onResponse(call: Call<PerformanceQuality>, response: Response<PerformanceQuality>) {
                    performanceProfileBinding.ProgressBar.visibility = View.GONE
                    if (response.isSuccessful) {
                        val data = response.body()?.data ?: mutableListOf()
                        qualityData.clear()
                        qualityData.addAll(data)
                        initRecyclerView() // Update visibility
                    } else if (response.code() == 403) {
                        Utils.setUnAuthDialog(this@PerformanceProfileActivity)
                    } else {
                        Toast.makeText(this@PerformanceProfileActivity, response.message(), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<PerformanceQuality>, t: Throwable) {
                    performanceProfileBinding.ProgressBar.visibility = View.GONE
                    Toast.makeText(this@PerformanceProfileActivity, t.message, Toast.LENGTH_SHORT).show()
                }
            })
    }


//    private fun getPerformanceTempData() {
//        try {
//            performanceTempData.clear()
//            apiInterface.GetPerformanceTemplate()
//                .enqueue(object : Callback<PerformanceProfileData> {
//                    override fun onResponse(
//                        call: Call<PerformanceProfileData>,
//                        response: Response<PerformanceProfileData>
//                    ) {
//                        performanceProfileBinding.ProgressBar.visibility = View.GONE
//                        Log.d("TAG", response.code().toString() + "")
//                        val code = response.code()
//                        if (code == 200) {
//                            if (response.isSuccessful && response.body() != null) {
//                                Log.d("Get Profile Data ", "${response.body()}")
//                                val data = response.body()!!.data
//                                if (data != null) {
//                                    for (i in data) {
//                                        performanceTempData.add(i)
//                                    }
//                                }
//                            }
//                        } else if (code == 403) {
//                            Utils.setUnAuthDialog(this@PerformanceProfileActivity)
//                        } else {
//                            Toast.makeText(
//                                this@PerformanceProfileActivity,
//                                "" + response.message(),
//                                Toast.LENGTH_SHORT
//                            ).show()
//                            call.cancel()
//                        }
//                    }
//
//                    override fun onFailure(call: Call<PerformanceProfileData>, t: Throwable) {
//                        performanceProfileBinding.ProgressBar.visibility = View.GONE
//                        Toast.makeText(
//                            this@PerformanceProfileActivity,
//                            "" + t.message,
//                            Toast.LENGTH_SHORT
//                        )
//                            .show()
//                        call.cancel()
//                    }
//
//                })
//        } catch (e: Exception) {
//            Log.d("Exception :-", "${e.message}")
//        }
//    }

    private fun getPerformanceTempData() {
        performanceTempData.clear()
        apiInterface.GetPerformanceTemplate()
            .enqueue(object : Callback<PerformanceProfileData> {
                override fun onResponse(call: Call<PerformanceProfileData>, response: Response<PerformanceProfileData>) {
                    if (response.isSuccessful) {
                        val data = response.body()?.data ?: listOf()
                        performanceTempData.addAll(data)
                        initRecyclerView() // Update visibility
                    } else if (response.code() == 403) {
                        Utils.setUnAuthDialog(this@PerformanceProfileActivity)
                    } else {
                        Toast.makeText(this@PerformanceProfileActivity, response.message(), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<PerformanceProfileData>, t: Throwable) {
                    Toast.makeText(this@PerformanceProfileActivity, t.message, Toast.LENGTH_SHORT).show()
                }
            })
    }


    private fun resetData() {
        getAthleteData()
        getPerformanceTempData()
        performanceProfileBinding.edtAthletes.text.clear()
        athleteId.id = null
        performanceTempData.clear()
        categoryData.clear()
        qualityData.clear()
        tempData.clear()
        performanceProfileBinding.performanceRly.visibility = View.GONE
    }

//    private fun getAthleteData() {
//        athleteData.clear()
//        performanceProfileBinding.ProgressBar.visibility = View.VISIBLE
//        try {
//            apiInterface.GetAthleteList()!!.enqueue(
//                object : Callback<com.example.model.AthleteDataPackage.AthleteData> {
//                    override fun onResponse(
//                        call: Call<com.example.model.AthleteDataPackage.AthleteData>,
//                        response: Response<com.example.model.AthleteDataPackage.AthleteData>
//                    ) {
//                        performanceProfileBinding.ProgressBar.visibility = View.GONE
//                        Log.d("Athlete :- Tag ", response.code().toString() + "")
//                        val code = response.code()
//                        if (code == 200) {
//                            val data = response.body()!!
//                            if (data.data != null) {
//                                Log.d("Athlete :- Data ", "${data}")
//                                for (i in data.data) {
//                                    Log.d("Athlete $i", "${i.name} \t ${i.id} \t ${i.athletes}")
//                                }
//                                val success: Boolean = data.status!!
//                                if (success) {
//                                    athleteData.addAll(data.data)
//                                }
//                            }
//                        } else if (code == 403) {
//                            Utils.setUnAuthDialog(this@PerformanceProfileActivity)
//                        } else {
//                            Toast.makeText(
//                                this@PerformanceProfileActivity,
//                                "" + response.message(),
//                                Toast.LENGTH_SHORT
//                            ).show()
//                            call.cancel()
//                        }
//                    }
//
//                    override fun onFailure(call: Call<com.example.model.AthleteDataPackage.AthleteData>, t: Throwable) {
//                        performanceProfileBinding.ProgressBar.visibility = View.GONE
//                        Toast.makeText(
//                            this@PerformanceProfileActivity,
//                            "" + t.message,
//                            Toast.LENGTH_SHORT
//                        )
//                            .show()
//                        call.cancel()
//                    }
//
//                }
//            )
//        } catch (e: Exception) {
//            Log.d("Error :-", "${e.message}")
//        }
//    }

    private fun getAthleteData() {
        athleteData.clear()
        performanceProfileBinding.ProgressBar.visibility = View.VISIBLE
        apiInterface.GetAthleteList()!!.enqueue(object : Callback<AthleteData> {
            override fun onResponse(call: Call<AthleteData>, response: Response<AthleteData>) {
                performanceProfileBinding.ProgressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    athleteData.addAll(response.body()?.data ?: listOf())
                    initRecyclerView() // Update visibility
                } else if (response.code() == 403) {
                    Utils.setUnAuthDialog(this@PerformanceProfileActivity)
                } else {
                    Toast.makeText(this@PerformanceProfileActivity, response.message(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AthleteData>, t: Throwable) {
                performanceProfileBinding.ProgressBar.visibility = View.GONE
                Toast.makeText(this@PerformanceProfileActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun initViews() {
        preferenceManager = PreferencesManager(this)
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)

        athleteData = arrayListOf()
        performanceData = mutableListOf()
        performanceTempData = mutableListOf()
        tempData = mutableListOf()
        categoryData = mutableListOf()
        qualityData = mutableListOf()
        aid = intent.getIntExtra("athleteId",0)
        performanceProfileBinding.edtAthletes.visibility = View.VISIBLE

        Log.d("aid","aid:-  $aid")

        if (aid != 0) {
            updateUI2(
                performanceProfileBinding.viewAverageGraph,
                performanceProfileBinding.viewQualityGraph
            )
            loadPerformance(aid)
        }
        else{
            updateUI(
                performanceProfileBinding.viewAverageGraph,
                performanceProfileBinding.viewQualityGraph
            )
        }

    }

    override fun onItemClicked(view: View, position: Int, type: Long, string: String) {
        when (string) {
            "view_chart" -> {
                try {
                    val category = categoryData.firstOrNull { it.id == type.toInt() }
                    Log.d("catData:","${category!!.id}")
                    startActivity(
                        Intent(
                            this@PerformanceProfileActivity,
                            ViewPerformanceProfileActivity::class.java
                        ).apply {
                            putExtra("athleteId", "${athleteId.id}")
                            putExtra("catId", "${type.toInt()}")
                            putExtra("catName", "${category?.name}")
                        }
                    )
                } catch (e: Exception) {
                    Log.d("Exception :-", "${e.message}")
                }
            }

            "add_program" -> {
                try {
                    var addQuality = AddQuality()
                    performanceProfileBinding.main.setBackgroundColor(resources.getColor(R.color.grey))
                    val dialog = Dialog(this)
                    val view = LayoutInflater.from(this).inflate(R.layout.add_quality_layout, null)
                    dialog.setCancelable(false)
                    dialog.setContentView(view)
                    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                    val qualityText = view.findViewById<EditText>(R.id.edtQualityText)
                    val coachText = view.findViewById<EditText>(R.id.edtCoachText)
                    val athleteText = view.findViewById<EditText>(R.id.edtAthleteText)
                    val addButton = view.findViewById<TextView>(R.id.addButton)
                    val cancelButton = view.findViewById<TextView>(R.id.cancelButton)

                    addButton.setOnClickListener {
                        if (qualityText.text.isNullOrEmpty() || coachText.text.isNullOrEmpty() || athleteText.text.isNullOrEmpty()) {
                            Toast.makeText(this, "Please Fill All Fields", Toast.LENGTH_SHORT)
                                .show()
                            return@setOnClickListener
                        }
                        performanceProfileBinding.main.setBackgroundColor(resources.getColor(R.color.black))
                        val quality = qualityText.text.toString()
                        val coach = coachText.text.toString()
                        val athlete = athleteText.text.toString()
                        addQuality = AddQuality(
                            athlete_id = athleteId.id,
                            performance_category_id = type.toInt(),
                            qualities = listOf(
                                Quality(
                                    athelet_score = athlete.toInt(),
                                    coach_score = coach.toInt(),
                                    quality_name = quality
                                )
                            )
                        )
                        Log.d("Add Quality :-", "${addQuality}")

                        performanceProfileBinding.main.setBackgroundColor(resources.getColor(R.color.black))
                        performanceProfileBinding.ProgressBar.visibility = View.VISIBLE
                        apiInterface.AddPerformanceQuality(
                            addQuality
                        ).enqueue(
                            object : Callback<PerformanceQualityAdd> {
                                override fun onResponse(
                                    call: Call<PerformanceQualityAdd>,
                                    response: Response<PerformanceQualityAdd>
                                ) {
                                    performanceProfileBinding.ProgressBar.visibility = View.GONE
                                    Log.d("TAG", response.code().toString() + "")
                                    val code = response.code()
                                    if (code == 200) {
                                        if (response.isSuccessful) {
                                            Toast.makeText(
                                                this@PerformanceProfileActivity,
                                                "" + response.message(),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            loadPerformance(athleteId.id!!)
                                        }
                                    } else if (code == 403) {
                                        Utils.setUnAuthDialog(this@PerformanceProfileActivity)
                                    } else {
                                        Toast.makeText(
                                            this@PerformanceProfileActivity,
                                            "" + response.message(),
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                        call.cancel()
                                    }
                                }

                                override fun onFailure(
                                    call: Call<PerformanceQualityAdd>,
                                    t: Throwable
                                ) {
                                    performanceProfileBinding.ProgressBar.visibility = View.GONE
                                    Toast.makeText(
                                        this@PerformanceProfileActivity,
                                        "" + t.message,
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    call.cancel()
                                }

                            })
                        dialog.dismiss()
                    }
                    cancelButton.setOnClickListener {
                        performanceProfileBinding.main.setBackgroundColor(resources.getColor(R.color.black))
                        dialog.dismiss()
                    }

                    dialog.show()
                } catch (e: Exception) {
                    Log.d("Error :-", "${e.message}")
                }
            }

            "del_program" -> {
                try {
                    performanceProfileBinding.main.setBackgroundColor(resources.getColor(R.color.grey))
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
                        performanceProfileBinding.main.setBackgroundColor(resources.getColor(R.color.black))
                        performanceProfileBinding.ProgressBar.visibility = View.VISIBLE
                        apiInterface.DeletePerformanceCategory(
                            id = type.toInt(),
                            athId = athleteId.id
                        )
                            .enqueue(
                                object : Callback<BaseClass> {
                                    override fun onResponse(
                                        call: Call<BaseClass>,
                                        response: Response<BaseClass>
                                    ) {
                                        performanceProfileBinding.ProgressBar.visibility = View.GONE
                                        Log.d("TAG", response.code().toString() + "")
                                        val code = response.code()
                                        if (code == 200) {
                                            if (response.isSuccessful) {
                                                Toast.makeText(
                                                    this@PerformanceProfileActivity,
                                                    "" + response.message(),
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                loadPerformance(athleteId.id!!)
                                            }
                                        } else if (code == 403) {
                                            Utils.setUnAuthDialog(this@PerformanceProfileActivity)
                                        } else {
                                            Toast.makeText(
                                                this@PerformanceProfileActivity,
                                                "" + response.message(),
                                                Toast.LENGTH_SHORT
                                            )
                                                .show()
                                            call.cancel()
                                        }
                                    }

                                    override fun onFailure(call: Call<BaseClass>, t: Throwable) {
                                        performanceProfileBinding.ProgressBar.visibility = View.GONE
                                        Toast.makeText(
                                            this@PerformanceProfileActivity,
                                            "" + t.message,
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                        call.cancel()
                                    }

                                })
                        dialog.dismiss()
                    }

                    cancelButton.setOnClickListener {
                        performanceProfileBinding.main.setBackgroundColor(resources.getColor(R.color.black))
                        dialog.dismiss()
                    }
                    dialog.show()
                } catch (e: Exception) {
                    Log.d("Exception :- ", "${e.message}")
                }
            }

            "edit_program" -> {
                try {
                    performanceProfileBinding.main.setBackgroundColor(resources.getColor(R.color.grey))
                    val performanceProfile = categoryData[position]
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
                    editText.setText(performanceProfile.name)
                    addButton.text = "Save"
                    cancelButton.text = "Cancel"

                    addButton.setOnClickListener {
                        performanceProfileBinding.main.setBackgroundColor(resources.getColor(R.color.black))
                        if (editText.text.isNullOrEmpty()) {
                            Toast.makeText(this, "Add Template Name First", Toast.LENGTH_SHORT)
                                .show()
                            return@setOnClickListener
                        }

                        performanceProfileBinding.ProgressBar.visibility = View.VISIBLE
                        apiInterface.EditPerformanceCategory(
                            id = type.toInt(),
                            athId = athleteId.id,
                            name = editText.text.toString()
                        )
                            .enqueue(
                                object : Callback<BaseClass> {
                                    override fun onResponse(
                                        call: Call<BaseClass>,
                                        response: Response<BaseClass>
                                    ) {
                                        performanceProfileBinding.ProgressBar.visibility = View.GONE
                                        Log.d("TAG", response.code().toString() + "")
                                        Log.d("TAG", response.message() + "")
                                        val code = response.code()
                                        if (code == 200) {
                                            if (response.isSuccessful) {
                                                Toast.makeText(
                                                    this@PerformanceProfileActivity,
                                                    "" + response.message(),
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                loadPerformance(athleteId.id!!)
                                            }
                                        } else if (code == 403) {
                                            Utils.setUnAuthDialog(this@PerformanceProfileActivity)
                                        } else {
                                            Toast.makeText(
                                                this@PerformanceProfileActivity,
                                                "" + response.message(),
                                                Toast.LENGTH_SHORT
                                            )
                                                .show()
                                            call.cancel()
                                        }
                                    }

                                    override fun onFailure(call: Call<BaseClass>, t: Throwable) {
                                        performanceProfileBinding.ProgressBar.visibility = View.GONE
                                        Toast.makeText(
                                            this@PerformanceProfileActivity,
                                            "" + t.message,
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                        call.cancel()
                                    }

                                })
                        dialog.dismiss()
                    }

                    cancelButton.setOnClickListener {
                        performanceProfileBinding.main.setBackgroundColor(resources.getColor(R.color.black))
                        dialog.dismiss()
                    }
                    dialog.show()
                } catch (e: Exception) {
                    Log.d("Error :-", "${e.message}")
                }
            }
        }
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
                        Utils.setUnAuthDialog(this@PerformanceProfileActivity)
                    } else {
                        Toast.makeText(
                            this@PerformanceProfileActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Toast.makeText(
                        this@PerformanceProfileActivity,
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
        getAthleteData()
        getPerformanceTempData()
        super.onResume()
    }

    override fun onQualityClick(position: Int, qualId: Long, type: String, catId: Long) {
        when (type) {
            "edit_quality" -> {
                Log.d(
                    "Quality Data :-",
                    "quality Id - ${qualId}\n category Id - ${catId}\n athlete id :- ${athleteId.id}"
                )
                try {
                    val performanceQuality = qualityData.find { it.id == qualId.toInt() }
                    var updateQuality = UpdateQuality()
                    performanceProfileBinding.main.setBackgroundColor(resources.getColor(R.color.grey))
                    val dialog = Dialog(this)
                    val view = LayoutInflater.from(this).inflate(R.layout.add_quality_layout, null)
                    dialog.setCancelable(false)
                    dialog.setContentView(view)
                    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                    val qualityText = view.findViewById<EditText>(R.id.edtQualityText)
                    val coachText = view.findViewById<EditText>(R.id.edtCoachText)
                    val athleteText = view.findViewById<EditText>(R.id.edtAthleteText)
                    val addButton = view.findViewById<TextView>(R.id.addButton)
                    val cancelButton = view.findViewById<TextView>(R.id.cancelButton)

                    qualityText.setText(performanceQuality!!.name)
                    coachText.setText(performanceQuality.coach_score)
                    athleteText.setText(performanceQuality.athelet_score)
                    addButton.text = "Save"

                    addButton.setOnClickListener {
                        if (qualityText.text.isNullOrEmpty() || coachText.text.isNullOrEmpty() || athleteText.text.isNullOrEmpty()) {
                            Toast.makeText(this, "Please Fill All Fields", Toast.LENGTH_SHORT)
                                .show()
                            return@setOnClickListener
                        }
                        updateQuality = UpdateQuality(
                            performance_category_id = catId.toInt(),
                            athlete_id = athleteId.id,
                            performance_quality_ids = listOf(qualId.toInt()),
                            qualities = listOf(
                                Quality(
                                    athelet_score = athleteText.text.toString().toInt(),
                                    coach_score = coachText.text.toString().toInt(),
                                    quality_name = qualityText.text.toString()
                                )
                            )
                        )
                        performanceProfileBinding.main.setBackgroundColor(resources.getColor(R.color.black))
                        performanceProfileBinding.ProgressBar.visibility = View.VISIBLE
                        apiInterface.EditPerformanceQuality(
                            updateQuality
                        ).enqueue(
                            object : Callback<PerformanceQualityAdd> {
                                override fun onResponse(
                                    call: Call<PerformanceQualityAdd>,
                                    response: Response<PerformanceQualityAdd>
                                ) {
                                    performanceProfileBinding.ProgressBar.visibility = View.GONE
                                    Log.d("TAG", response.code().toString() + "")
                                    val code = response.code()
                                    if (code == 200) {
                                        if (response.isSuccessful) {
                                            Toast.makeText(
                                                this@PerformanceProfileActivity,
                                                "" + response.message(),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            loadPerformance(athleteId.id!!)
                                        }
                                    } else if (code == 403) {
                                        Utils.setUnAuthDialog(this@PerformanceProfileActivity)
                                    } else {
                                        Toast.makeText(
                                            this@PerformanceProfileActivity,
                                            "" + response.message(),
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                        call.cancel()
                                    }
                                }

                                override fun onFailure(
                                    call: Call<PerformanceQualityAdd>,
                                    t: Throwable
                                ) {
                                    performanceProfileBinding.ProgressBar.visibility = View.GONE
                                    Toast.makeText(
                                        this@PerformanceProfileActivity,
                                        "" + t.message,
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    call.cancel()
                                }

                            })
                        dialog.dismiss()
                    }
                    cancelButton.setOnClickListener {
                        performanceProfileBinding.main.setBackgroundColor(resources.getColor(R.color.black))
                        dialog.dismiss()
                    }

                    dialog.show()
                } catch (e: Exception) {
                    Log.d("Error :-", "${e.message}")
                }
            }

            "delete_quality" -> {
                Log.d("Quality Data :-", "quality Id - ${qualId}\n category Id - ${catId}")
                try {
                    performanceProfileBinding.main.setBackgroundColor(resources.getColor(R.color.grey))
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
                        performanceProfileBinding.main.setBackgroundColor(resources.getColor(R.color.black))
                        performanceProfileBinding.ProgressBar.visibility = View.VISIBLE
                        apiInterface.DeletePerformanceQuality(
                            id = qualId.toInt(),
                            athId = athleteId.id
                        )
                            .enqueue(
                                object : Callback<BaseClass> {
                                    override fun onResponse(
                                        call: Call<BaseClass>,
                                        response: Response<BaseClass>
                                    ) {
                                        performanceProfileBinding.ProgressBar.visibility = View.GONE
                                        Log.d("TAG", response.code().toString() + "")
                                        val code = response.code()
                                        if (code == 200) {
                                            if (response.isSuccessful) {
                                                Toast.makeText(
                                                    this@PerformanceProfileActivity,
                                                    "" + response.message(),
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                loadPerformance(athleteId.id!!)
                                            }
                                        } else if (code == 403) {
                                            Utils.setUnAuthDialog(this@PerformanceProfileActivity)
                                        } else {
                                            Toast.makeText(
                                                this@PerformanceProfileActivity,
                                                "" + response.message(),
                                                Toast.LENGTH_SHORT
                                            )
                                                .show()
                                            call.cancel()
                                        }
                                    }

                                    override fun onFailure(call: Call<BaseClass>, t: Throwable) {
                                        performanceProfileBinding.ProgressBar.visibility = View.GONE

                                        call.cancel()
                                    }

                                })
                        dialog.dismiss()
                    }

                    cancelButton.setOnClickListener {
                        performanceProfileBinding.main.setBackgroundColor(resources.getColor(R.color.black))
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