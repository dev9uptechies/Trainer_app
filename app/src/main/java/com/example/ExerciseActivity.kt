package com.example

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.model.SelectedValue
import com.example.model.newClass.excercise.Exercise
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.ExcerciseData
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.R
import com.example.trainerapp.TestListData
import com.example.trainerapp.Utils
import com.example.trainerapp.View_Exercise_Activity
import com.example.trainerapp.databinding.ActivityExerciseBinding
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ExerciseActivity : AppCompatActivity(), OnItemClickListener.OnItemClickCallback {
    lateinit var exerciseBinding: ActivityExerciseBinding
    var age = ArrayList<String>()
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    lateinit var generallist1: ArrayList<ExcerciseData.Exercise>
    lateinit var exerciselist1: ArrayList<ExcerciseData.Exercise>
    lateinit var specificlist1: ArrayList<ExcerciseData.Exercise>
    val testdatalist: MutableList<Exercise.ExerciseData> =
        mutableListOf()  // This should be defined somewhere in your activity
    lateinit var adapter: ExcerciseAdapter
    private var checktype: Boolean? = false
    private var isDataLoaded = false


    var selecteValue = SelectedValue(null)

    //new

    var category = ArrayList<String>()
    var categoryId = SelectedValue(null)

    lateinit var categoryData: MutableList<TestListData.testData>
    lateinit var generallist: MutableList<Exercise.ExerciseData>
    lateinit var specificlist: MutableList<Exercise.ExerciseData>
    lateinit var exerciselist: MutableList<Exercise.ExerciseData>

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
                        exerciseBinding.generalCard.setCardBackgroundColor(resources.getColor(R.color.splash_text_color))
                        exerciseBinding.specificCard.setCardBackgroundColor(resources.getColor(R.color.grey))
                        Log.d("Get Profile Data ", "${response.body()}")
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@ExerciseActivity)
                    } else {
                        Toast.makeText(
                            this@ExerciseActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Toast.makeText(
                        this@ExerciseActivity,
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
        exerciseBinding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(exerciseBinding.root)
        initViews()
        initrecycler(generallist)
        checkSearch()
//        loadData()

        exerciseBinding.edtSection.setOnClickListener {
            showPopup(it, categoryData, exerciseBinding.edtSection, category, categoryId)
        }

        exerciseBinding.scrollView.setOnRefreshListener {
            loadData()
            exerciseBinding.generalCard.setCardBackgroundColor(resources.getColor(R.color.splash_text_color))
            exerciseBinding.specificCard.setCardBackgroundColor(resources.getColor(R.color.grey))
            exerciseBinding.scrollView.isRefreshing = false
        }

        exerciseBinding.back.setOnClickListener {
            finish()
        }

        exerciseBinding.generalCard.setOnClickListener {
            checktype = false
            if (selecteValue.id == null) {
                initrecycler(generallist)
            } else {
                effectRecycler(selecteValue)
            }
            exerciseBinding.generalCard.setCardBackgroundColor(resources.getColor(R.color.splash_text_color))
            exerciseBinding.specificCard.setCardBackgroundColor(resources.getColor(R.color.grey))
        }

        exerciseBinding.specificCard.setOnClickListener {
            checktype = true
            if (selecteValue.id == null) {
                initrecycler(specificlist)
            } else {
                effectRecycler(selecteValue)
            }
            exerciseBinding.generalCard.setCardBackgroundColor(resources.getColor(R.color.grey))
            exerciseBinding.specificCard.setCardBackgroundColor(resources.getColor(R.color.splash_text_color))

        }

        exerciseBinding.cardCreateExercise.setOnClickListener {
            startActivity(Intent(this, CreateExerciseActivity::class.java))
        }
    }

    private fun showPopup(
        anchorView: View?,
        data: MutableList<TestListData.testData>,
        editText: EditText,
        list: ArrayList<String>,
        selectedValue: SelectedValue
    ) {

        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.popup_list, null)

        val weightInPixels = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, // The unit type (dp)
            330f, // The value in dp
            resources.displayMetrics // The display metrics
        ).toInt()

        val popupWindow = PopupWindow(
            popupView,
//            ViewGroup.LayoutParams.WRAP_CONTENT,
            weightInPixels,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true // Focusable to allow outside clicks to dismiss
        )

        popupWindow.setBackgroundDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.popup_background
            )
        )
        popupWindow.elevation = 10f
        val listView = popupView.findViewById<ListView>(R.id.listView)

        val adapter =
            object : ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list) {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val view = super.getView(position, convertView, parent) as TextView

                    val typeface = ResourcesCompat.getFont(this@ExerciseActivity, R.font.poppins_medium)
                    view.typeface = typeface
                    view.setTextColor(Color.WHITE) // Set text color to white
                    return view
                }
            }
        listView.adapter = adapter
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = list[position]
            editText.setText(selectedItem)
            selectedValue.id = data.filter { it.name == selectedItem }.first().id!!
            selecteValue = selectedValue
            println("Selected item: $selectedItem")
            popupWindow.dismiss()
            effectRecycler(selectedValue)
        }
        popupWindow.showAsDropDown(anchorView)
        popupWindow.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                this,
                android.R.color.white
            )
        )

    }

    private fun effectRecycler(selectedValue: SelectedValue) {
        if (checktype == true) {
            val list = specificlist
            val newList = list.filter { it.category_id == selectedValue.id.toString() }
            Log.d("New List", "${newList.size} ${list.size} ${specificlist.size}")
            initrecycler(newList.toMutableList())
        } else {
            val list = generallist
            val newList = list.filter { it.category_id == selectedValue.id.toString() }
            Log.d("New List", "${newList.size} ${list.size} ${generallist.size}")
            initrecycler(newList.toMutableList())
        }
    }

    private fun checkSearch() {
        exerciseBinding.search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // No action needed here
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val data = adapter.searchFilter(s.toString())
                if (data.isEmpty()) {
                    exerciseBinding.noData.visibility = View.VISIBLE
                } else {
                    exerciseBinding.noData.visibility = View.GONE
                }

            }
        })
    }

    private fun initViews() {
        apiClient = APIClient(this)
        generallist1 = ArrayList()
        specificlist1 = ArrayList()
        apiInterface = apiClient.client().create(APIInterface::class.java)

        generallist1 = ArrayList()
        exerciselist1 = ArrayList()
        specificlist1 = ArrayList()


        generallist = mutableListOf()
        specificlist = mutableListOf()
        exerciselist = mutableListOf()

    }

    private fun loadData() {
        if (!isDataLoaded) {
            isDataLoaded = true
            getCategory()
            getExerciseData()
        } else {
            refreshData()
        }
    }

    private fun refreshData() {
        getCategory()
        getExerciseData()
    }

    private fun getCategory() {
        exerciseBinding.ProgressBar.visibility = View.VISIBLE
        categoryData = mutableListOf()
        categoryData.clear()
        category.clear()
        apiInterface.GetCategoriesData()?.enqueue(object : Callback<TestListData> {
            override fun onResponse(call: Call<TestListData>, response: Response<TestListData>) {
                val code = response.code()
                if (code == 200) {
                    if (response.isSuccessful) {
                        val data = response.body()!!.data
                        if (data!! != null) {
                            categoryData.addAll(data.toMutableList())
                            for (i in categoryData) {
                                category.add(i.name!!)
                            }
//                    Utils.setSpinnerAdapter(
//                        applicationContext,
//                        category,
//                        exerciseBinding.spCategories
//                    )
                        }
                    }
                } else if (code == 403) {
                    Utils.setUnAuthDialog(this@ExerciseActivity)
                } else {
                    Toast.makeText(
                        this@ExerciseActivity,
                        "" + response.message(),
                        Toast.LENGTH_SHORT
                    ).show()
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<TestListData>, t: Throwable) {
                Log.d("TAG Category", t.message.toString() + "")
            }

        })
    }

//    private fun getExerciseData() {
//        exerciselist.clear()
//        generallist.clear()
//        specificlist.clear()
//        apiInterface.GetExerciseData().enqueue(
//            object : Callback<Exercise> {
//                override fun onResponse(call: Call<Exercise>, response: Response<Exercise>) {
//                    exerciseBinding.ProgressBar.visibility = View.GONE
//                    val code = response.code()
//                    if (code == 200) {
//                        Log.d("Response :- ", "${response.body()}")
//                        Log.d("Response :- ", "${response.code()}")
//
//                        if (response.isSuccessful) {
//                            if (response.body()!!.data!! != null) {
//                                exerciselist.addAll(response.body()!!.data!!.toMutableList())
//                                for (i in 0 until exerciselist.size) {
//                                    if (exerciselist[i].type == "General") {
//                                        generallist.add(exerciselist[i])
//                                    } else {
//                                        specificlist.add(exerciselist[i])
//                                    }
//                                }
//                                if (selecteValue.id == null) {
//                                    if (generallist != null) {
//                                        initrecycler(generallist)
//                                    }
//                                } else {
//                                    effectRecycler(selecteValue)
//                                }
//                                Log.d(
//                                    "Values :-",
//                                    "$exerciselist $generallist $specificlist \n ${exerciselist.size} ${generallist.size} ${specificlist.size}"
//                                )
//                            }
//                        } else {
//                            Toast.makeText(
//                                this@ExerciseActivity,
//                                "" + response.message(),
//                                Toast.LENGTH_SHORT
//                            )
//                                .show()
//                        }
//                    } else if (code == 403) {
//                        Utils.setUnAuthDialog(this@ExerciseActivity)
//                    } else {
//                        Toast.makeText(
//                            this@ExerciseActivity,
//                            "" + response.message(),
//                            Toast.LENGTH_SHORT
//                        ).show()
//                        call.cancel()
//                    }
//                }
//
//                override fun onFailure(call: Call<Exercise>, t: Throwable) {
//                    exerciseBinding.ProgressBar.visibility = View.GONE
//                    Toast.makeText(this@ExerciseActivity, "" + t.message, Toast.LENGTH_SHORT)
//                        .show()
//                    call.cancel()
//                }
//
//            }
//        )
//    }

    private fun getExerciseData() {
        exerciselist.clear()
        generallist.clear()
        specificlist.clear()

        exerciseBinding.ProgressBar.visibility = View.VISIBLE

        apiInterface.GetExerciseData().enqueue(object : Callback<Exercise> {
            override fun onResponse(call: Call<Exercise>, response: Response<Exercise>) {
                exerciseBinding.ProgressBar.visibility = View.GONE
                val code = response.code()

                if (code == 200) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val data = responseBody.data
                        if (!data.isNullOrEmpty()) {
                            exerciselist.addAll(data.toMutableList())
                            for (exercise in exerciselist) {
                                if (exercise.type == "General") {
                                    generallist.add(exercise)
                                } else {
                                    specificlist.add(exercise)
                                }
                            }

                            if (selecteValue.id == null) {
                                if (generallist.isNotEmpty()) {
                                    initrecycler(generallist)
                                }
                            } else {
                                effectRecycler(selecteValue)
                            }

                            Log.d(
                                "Values :-",
                                "$exerciselist $generallist $specificlist \n ${exerciselist.size} ${generallist.size} ${specificlist.size}"
                            )
                        } else {
                            // Handle empty data
                            Toast.makeText(
                                this@ExerciseActivity,
                                "No exercise data available",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        // Handle null response body
                        Toast.makeText(
                            this@ExerciseActivity,
                            "Empty response from server",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else if (code == 403) {
                    Utils.setUnAuthDialog(this@ExerciseActivity)
                } else {
                    Toast.makeText(this@ExerciseActivity, response.message(), Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<Exercise>, t: Throwable) {
                exerciseBinding.ProgressBar.visibility = View.GONE
                Toast.makeText(this@ExerciseActivity, t.message ?: "Error", Toast.LENGTH_SHORT)
                    .show()
                call.cancel()
            }
        })
    }


//    private fun GetCategories() {
//        exerciseBinding.ProgressBar.visibility = View.VISIBLE
//        apiInterface.GetCategories()?.enqueue(object : Callback<CategoriesData?> {
//            override fun onResponse(
//                call: Call<CategoriesData?>,
//                response: Response<CategoriesData?>
//            ) {
//                Log.d("TAG", response.code().toString() + "")
//                Log.d("TAG", response.body()!!.message.toString())
//                Log.d("TAG", response.body()!!.status.toString())
//                val Data = response.body()!!.data!!
//                if (response.isSuccessful) {
//                    for (i in 0 until Data.size) {
//                        age.add(Data[i].name!!)
//                    }
//                    Utils.setSpinnerAdapter(applicationContext, age, exerciseBinding.spCategories)
//                    GetExercise()
//                }
////                val resource: CategoriesData? = response.body()
////                val Success: Boolean = resource?.status!!
////                val Data = resource.data!!
////                if (Success == true) {
////                    for (i in 0 until Data.size) {
////                        age.add(Data[i].name!!)
////                    }
////                    Utils.setSpinnerAdapter(applicationContext, age, exerciseBinding.spCategories)
////                    GetExercise()
//                else {
//                    exerciseBinding.ProgressBar.visibility = View.GONE
//                }
//            }
//
//            override fun onFailure(call: Call<CategoriesData?>, t: Throwable) {
//                exerciseBinding.ProgressBar.visibility = View.GONE
//                Toast.makeText(this@ExerciseActivity, "" + t.message, Toast.LENGTH_SHORT)
//                    .show()
//                call.cancel()
//            }
//        })
//    }

//    private fun GetExercise() {
//        exerciselist1.clear()
//        generallist1.clear()
//        specificlist1.clear()
//        exerciseBinding.ProgressBar.visibility = View.VISIBLE
//        apiInterface.GetExercise()?.enqueue(object : Callback<ExcerciseData?> {
//            override fun onResponse(
//                call: Call<ExcerciseData?>,
//                response: Response<ExcerciseData?>
//            ) {
//                Log.d("TAG", response.code().toString() + "")
//                val resource: ExcerciseData? = response.body()
//                val Success: Boolean = resource?.status!!
//                if (Success == true) {
//                    exerciselist1 = resource.data!!
//
//                    for (i in 0 until exerciselist1.size) {
//                        if (exerciselist1[i].type == "General") {
//                            generallist1.add(exerciselist1[i])
//                        } else {
//                            specificlist1.add(exerciselist1[i])
//                        }
//                    }
//                    //initrecycler(generallist1)
//
//                } else {
//                    exerciseBinding.ProgressBar.visibility = View.GONE
//                }
//
//            }
//
//            override fun onFailure(call: Call<ExcerciseData?>, t: Throwable) {
//                exerciseBinding.ProgressBar.visibility = View.GONE
//                Toast.makeText(this@ExerciseActivity, "" + t.message, Toast.LENGTH_SHORT)
//                    .show()
//                call.cancel()
//            }
//        })
//
//    }

    private fun initrecycler(testdatalist: MutableList<Exercise.ExerciseData>) {
        exerciseBinding.ProgressBar.visibility = View.GONE
        exerciseBinding.rlyExercise.layoutManager = LinearLayoutManager(this)
        adapter = ExcerciseAdapter(testdatalist, this, this)
        exerciseBinding.rlyExercise.adapter = adapter
        adapter.notifyDataSetChanged()  // Notify the adapter about changes
    }

    override fun onItemClicked(
        view: View,
        position: Int,
        type: Long,
        string: String,
    ) {
        if (string == "View") {
            val intent = Intent(this, View_Exercise_Activity::class.java)
            intent.putExtra("position", position)
            intent.putExtra("id", type.toInt())
            startActivity(intent)
//            val list = generallist[position].cycles
//            startActivity(
//                Intent(this, View_Exercise_Activity::class.java).putExtra(
//                    "position",
//                    position
//                )
//            )
        } else if (string == "delete") {
            val builder: AlertDialog.Builder
            builder = AlertDialog.Builder(this)
            builder.setMessage("Are you sure you want to delete Exercise?").setTitle("Delete")
            builder.setMessage("Are you sure you want to delete Exercise?")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialog, id ->
                    exerciseBinding.ProgressBar.visibility = View.VISIBLE
                    apiInterface.Deleteexercise(type.toInt())
                        ?.enqueue(object : Callback<RegisterData?> {
                            override fun onResponse(
                                call: Call<RegisterData?>,
                                response: Response<RegisterData?>
                            ) {
                                exerciseBinding.ProgressBar.visibility = View.GONE
                                Log.d("TAG", response.code().toString() + "")
                                val code = response.code()
                                if (code == 200) {
                                    val resource: RegisterData? = response.body()
                                    val Message: String = resource!!.message!!
                                    exerciseBinding.ProgressBar.visibility = View.GONE
                                    Toast.makeText(
                                        this@ExerciseActivity,
                                        "" + Message,
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    testdatalist.removeAll { it.id == type.toInt() }
                                    adapter.notifyDataSetChanged()

                                    loadData()
                                    exerciseBinding.generalCard.setCardBackgroundColor(
                                        resources.getColor(
                                            R.color.splash_text_color
                                        )
                                    )
                                    exerciseBinding.specificCard.setCardBackgroundColor(
                                        resources.getColor(
                                            R.color.grey
                                        )
                                    )

//                    finish()
//                    startActivity(
//                        Intent(
//                            this@ExerciseActivity,
//                            ExerciseActivity::class.java
//                        )
//                    )
                                } else if (code == 403) {
                                    Utils.setUnAuthDialog(this@ExerciseActivity)
                                } else {
                                    Toast.makeText(
                                        this@ExerciseActivity,
                                        "" + response.message(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    call.cancel()
                                }
                            }

                            override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                                exerciseBinding.ProgressBar.visibility = View.GONE
                                Toast.makeText(
                                    this@ExerciseActivity,
                                    "" + t.message,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                call.cancel()
                            }
                        })
                }
                .setNegativeButton(
                    "No"
                ) { dialog, id ->
                    dialog.cancel()
                }
            val alert = builder.create()

            val titleTextView = TextView(this).apply {
                text = "Delete"
                typeface = ResourcesCompat.getFont(this@ExerciseActivity, R.font.poppins_medium) // Set the font
                textSize = 20f
                setPadding(50, 50, 50, 5) // Optional: add padding
                setTextColor(Color.BLACK) // Set text color to black
            }



            alert.setCustomTitle(titleTextView)
//            alert.setTitle("Delete")


            val typeface = ResourcesCompat.getFont(this, R.font.poppins_medium)


            alert.setOnShowListener {
                val titleTextView = alert.findViewById<TextView>(android.R.id.title)
                titleTextView?.typeface = typeface


                val messageTextView = alert.findViewById<TextView>(android.R.id.message)
                messageTextView?.typeface = typeface

                // Set the font for the buttons
                val positiveButton = alert.getButton(AlertDialog.BUTTON_POSITIVE)
                positiveButton?.typeface = typeface

                val negativeButton = alert.getButton(AlertDialog.BUTTON_NEGATIVE)
                negativeButton?.typeface = typeface
            }

            alert.show()

        } else if (string == "fav") {
            exerciseBinding.ProgressBar.visibility = View.VISIBLE
            val id: MultipartBody.Part =
                MultipartBody.Part.createFormData("id", type.toInt().toString())
            apiInterface.Favourite_Exercise(id)?.enqueue(object : Callback<RegisterData?> {
                override fun onResponse(
                    call: Call<RegisterData?>,
                    response: Response<RegisterData?>
                ) {
                    Log.d("TAG", response.code().toString() + "")
                    exerciseBinding.ProgressBar.visibility = View.GONE
                    val code = response.code()
                    if (code == 200) {
                        val resource: RegisterData? = response.body()
                        val Success: Boolean = resource?.status!!
                        val Message: String = resource.message!!
                        if (Success) {
                            exerciseBinding.ProgressBar.visibility = View.GONE
                            Toast.makeText(this@ExerciseActivity, "" + Message, Toast.LENGTH_SHORT)
                                .show()
                            initViews()
                        } else {
                            exerciseBinding.ProgressBar.visibility = View.GONE
                            Toast.makeText(this@ExerciseActivity, "" + Message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@ExerciseActivity)
                    } else {
                        Toast.makeText(
                            this@ExerciseActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    exerciseBinding.ProgressBar.visibility = View.GONE
                    Toast.makeText(this@ExerciseActivity, "" + t.message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }
            })
        } else if (string == "unfav") {
            exerciseBinding.ProgressBar.visibility = View.VISIBLE
            apiInterface.DeleteFavourite_Exercise(type.toInt())
                ?.enqueue(object : Callback<RegisterData?> {
                    override fun onResponse(
                        call: Call<RegisterData?>,
                        response: Response<RegisterData?>
                    ) {
                        Log.d("TAG", response.code().toString() + "")
                        exerciseBinding.ProgressBar.visibility = View.GONE
                        val code = response.code()
                        if (code == 200) {
                            val resource: RegisterData? = response.body()
                            val Success: Boolean = resource?.status!!
                            val Message: String = resource.message!!
                            if (Success) {
                                exerciseBinding.ProgressBar.visibility = View.GONE
                                Toast.makeText(
                                    this@ExerciseActivity,
                                    "" + Message,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                initViews()
                            } else {
                                exerciseBinding.ProgressBar.visibility = View.GONE
                                Toast.makeText(
                                    this@ExerciseActivity,
                                    "" + Message,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        } else if (code == 403) {
                            Utils.setUnAuthDialog(this@ExerciseActivity)
                        } else {
                            Toast.makeText(
                                this@ExerciseActivity,
                                "" + response.message(),
                                Toast.LENGTH_SHORT
                            ).show()
                            call.cancel()
                        }
                    }

                    override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                        exerciseBinding.ProgressBar.visibility = View.GONE
                        Toast.makeText(this@ExerciseActivity, "" + t.message, Toast.LENGTH_SHORT)
                            .show()
                        call.cancel()
                    }
                })
        } else {
            val intent = Intent(this, EdiExerciseActivity::class.java)
            intent.putExtra("position", position)
            intent.putExtra("exercise", type.toInt())
            startActivity(intent)
//            startActivity(Intent(this, EdiExerciseActivity::class.java).putExtra("position",position))
//            startActivity(
//                Intent(this, EdiExerciseActivity::class.java).putExtra(
//                    "position",
//                    position,
//                    "exercise", type
//                )
//            )
        }

    }

}