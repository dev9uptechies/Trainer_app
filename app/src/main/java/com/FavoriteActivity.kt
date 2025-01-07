package com

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.PopupMenu
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.Adapter.template.profile.PerformanceProfileAdapter
import com.example.FavoriteAdapter
import com.example.FavoriteeventAdapter
import com.example.FavoriteexerciseAdapter
import com.example.FavoritelessonAdapter
import com.example.FavoritetestAdapter
import com.example.LessonData
import com.example.OnItemClickListener
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.R
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.ActivityFavoriteBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoriteActivity : AppCompatActivity(), OnItemClickListener.OnItemClickCallback {
    lateinit var favoriteBinding: ActivityFavoriteBinding
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    lateinit var exerciseadapter: FavoriteexerciseAdapter
    lateinit var programadapter: FavoriteAdapter
    lateinit var lessonadapter: FavoritelessonAdapter
    lateinit var eventadapter: FavoriteeventAdapter
    lateinit var testadapter: FavoritetestAdapter
    var type = arrayOf("All Favorite Items", "Program", "Lesson", "Test", "Event", "Exercise")

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
                        Utils.setUnAuthDialog(this@FavoriteActivity)
                    } else {
                        Toast.makeText(
                            this@FavoriteActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Toast.makeText(
                        this@FavoriteActivity,
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
        favoriteBinding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(favoriteBinding.root)
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        favoriteBinding.lyExercise.visibility = View.VISIBLE
        favoriteBinding.lyProgram.visibility = View.VISIBLE
        favoriteBinding.lyLesson.visibility = View.VISIBLE
        favoriteBinding.lyEvent.visibility = View.VISIBLE
        favoriteBinding.lyTest.visibility = View.VISIBLE

        callApi()
        showAll()

        favoriteBinding.selectFavorite.setOnClickListener {
//            showPopupMenu(favoriteBinding.textSpinner)
            val list = type

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
                    this@FavoriteActivity,
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
                    val typeface = ResourcesCompat.getFont(this@FavoriteActivity, R.font.poppins_medium)
                    view.typeface = typeface
                    view.setTextColor(Color.WHITE) // Set text color to white
                    return view
                }
            }
            listView.adapter = adapter

            listView.setOnItemClickListener { _, _, position, _ ->
                val selectedItem = list[position]
                favoriteBinding.textSpinner.setText(selectedItem)
                handleTypeSelection(selectedItem)
                popupWindow.dismiss()
            }



            popupWindow.showAsDropDown(it)
            popupWindow.setBackgroundDrawable(
                AppCompatResources.getDrawable(
                    this,
                    android.R.color.white
                )
            )

        }




        favoriteBinding.back.setOnClickListener {
            finish()
        }

    }

    private fun handleTypeSelection(selectedType: String) {
        when (selectedType) {
            "All Favorite Items" -> {
                showAll()
            }
            "Program" -> {
               showProgram()
            }
            "Lesson" -> {
               showLesson()
            }
            "Test" -> {
                showTest()
            }
            "Event" -> {
                showEvent()
            }
            "Exercise" -> {
                showExercise()
            }
        }
    }

    private fun callApi() {
        favoriteBinding.favoriteProgress.visibility = View.VISIBLE
        val data: MutableMap<String, String> = HashMap()
        data["id"] = "1"
        apiInterface.get_fav_program(data)
            ?.enqueue(object : Callback<LessonData?> {
                override fun onResponse(call: Call<LessonData?>, response: Response<LessonData?>) {
                    val code = response.code()
                    if (code == 200) {
                        val resource: LessonData? = response.body()
                        val Success: Boolean = resource?.status!!
                        val Message: String = resource.message!!
                        if (Success) {
//                            favoriteBinding.lyProgram.visibility = View.VISIBLE

                            for (data in resource.data!!){
                                Log.d("DATA_PROGRAM","program :- ${data.program!!.name}")
                            }
                            if (resource.data!!.isNotEmpty()) {
                                initProgramRecyclerView(resource.data)
                            } else {
                                    // Show RecyclerView and hide "No Data Found" message
                                NoData()
                                Toast.makeText(
                                    this@FavoriteActivity,
                                    "No data found",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
//                            favoriteBinding.lyProgram.visibility = View.GONE
                        }

                        getFavLesson()
                    } else if (response.code() == 403) {
                        Utils.setUnAuthDialog(this@FavoriteActivity)
                    } else {
                        val message = response.message()
                        Toast.makeText(this@FavoriteActivity, "" + message, Toast.LENGTH_SHORT)
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<LessonData?>, t: Throwable) {
                    Toast.makeText(
                        this@FavoriteActivity,
                        "" + t.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    call.cancel()
                }
            })

    }

    private fun getFavLesson() {
        apiInterface.get_fav_lesson()
            ?.enqueue(object : Callback<LessonData?> {
                override fun onResponse(call: Call<LessonData?>, response: Response<LessonData?>) {
                    val code = response.code()
                    if (code == 200) {
                        val resource: LessonData? = response.body()
                        val Success: Boolean = resource?.status!!
                        val Message: String = resource.message!!
                        if (Success) {
//                            favoriteBinding.lyLesson.visibility = View.VISIBLE
                            val data = resource.data

                            if (data != null) {
                                for (datas in data){
                                    Log.d("DATA_PROGRAM","program :- ${datas.lesson!!.name}")
                                }
                            }
                            if (data!!.isNotEmpty()) {
                                initLessonRecyclerView(resource.data)
                            } else {
                                Toast.makeText(
                                    this@FavoriteActivity,
                                    "No Data Found",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                NoData()

                            }
                        } else {
//                            favoriteBinding.lyLesson.visibility = View.GONE
                        }
                        getFavEvent()
                    } else if (response.code() == 403) {
                        Utils.setUnAuthDialog(this@FavoriteActivity)
                    } else {
                        val message = response.message()
                        Toast.makeText(this@FavoriteActivity, "" + message, Toast.LENGTH_SHORT)
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<LessonData?>, t: Throwable) {
                    Toast.makeText(
                        this@FavoriteActivity,
                        "" + t.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    call.cancel()
                }
            })
    }

    private fun getFavEvent() {
        apiInterface.get_fav_event()
            ?.enqueue(object : Callback<LessonData?> {
                override fun onResponse(call: Call<LessonData?>, response: Response<LessonData?>) {
                    val code = response.code()
                    if (code == 200) {
                        val resource: LessonData? = response.body()
                        val Success: Boolean = resource?.status!!
                        val Message: String = resource.message!!
                        if (Success) {
//                            favoriteBinding.lyEvent.visibility = View.VISIBLE
                            if (resource.data!!.isNotEmpty()) {
                                initEventRecyclerView(resource.data)
                            } else {
                                NoData()
                                Toast.makeText(
                                    this@FavoriteActivity,
                                    "No data found",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
//                            favoriteBinding.lyEvent.visibility = View.GONE
                        }

                        getFavTest()
                    } else if (response.code() == 403) {
                        Utils.setUnAuthDialog(this@FavoriteActivity)
                    } else {
                        val message = response.message()
                        Toast.makeText(this@FavoriteActivity, "" + message, Toast.LENGTH_SHORT)
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<LessonData?>, t: Throwable) {
                    Toast.makeText(
                        this@FavoriteActivity,
                        "" + t.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    call.cancel()
                }
            })
    }

    private fun getFavTest() {
        apiInterface.get_fav_test()
            ?.enqueue(object : Callback<LessonData?> {
                override fun onResponse(call: Call<LessonData?>, response: Response<LessonData?>) {
                    val code = response.code()
                    if (code == 200) {
                        val resource: LessonData? = response.body()
                        val Success: Boolean = resource?.status!!
                        val Message: String = resource.message!!
                        if (Success) {
//                            favoriteBinding.lyTest.visibility = View.VISIBLE
                            if (resource.data!!.isNotEmpty()) {
                                initTestRecyclerView(resource.data)
                            } else {
                                NoData()
                                Toast.makeText(
                                    this@FavoriteActivity,
                                    "No data found",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
//                            favoriteBinding.lyTest.visibility = View.GONE
                        }

                        getFavExercise()
                    } else if (response.code() == 403) {
                        Utils.setUnAuthDialog(this@FavoriteActivity)
                    } else {
                        val message = response.message()
                        Toast.makeText(this@FavoriteActivity, "" + message, Toast.LENGTH_SHORT)
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<LessonData?>, t: Throwable) {
                    Toast.makeText(
                        this@FavoriteActivity,
                        "" + t.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    call.cancel()
                }
            })
    }

    private fun getFavExercise() {
        apiInterface.get_fav_exercise()
            ?.enqueue(object : Callback<LessonData?> {
                override fun onResponse(call: Call<LessonData?>, response: Response<LessonData?>) {
                    val code = response.code()
                    if (code == 200) {
                        val resource: LessonData? = response.body()
                        val Success: Boolean = resource?.status!!
                        val Message: String = resource.message!!
                        favoriteBinding.favoriteProgress.visibility = View.GONE
                        if (Success) {
//                            favoriteBinding.lyExercise.visibility = View.VISIBLE
                            if (resource.data!!.isNotEmpty()) {
                                initExerciseRecyclerView(resource.data)
                            } else {
                                NoData()
                                Toast.makeText(
                                    this@FavoriteActivity,
                                    "No data found",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
//                            favoriteBinding.lyExercise.visibility = View.GONE
                        }
                    } else if (response.code() == 403) {
                        Utils.setUnAuthDialog(this@FavoriteActivity)
                    } else {
                        val message = response.message()
                        Toast.makeText(this@FavoriteActivity, "" + message, Toast.LENGTH_SHORT)
                            .show()
                        call.cancel()
                    }

                }

                override fun onFailure(call: Call<LessonData?>, t: Throwable) {
                    Toast.makeText(
                        this@FavoriteActivity,
                        "" + t.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    call.cancel()
                }
            })
    }

    private fun initProgramRecyclerView(data: ArrayList<LessonData.lessionData>?) {
        favoriteBinding.favProgramRly.layoutManager = LinearLayoutManager(this)
        programadapter =
            FavoriteAdapter(data, this, this)
        favoriteBinding.favProgramRly.adapter = programadapter
    }

    private fun initLessonRecyclerView(data: ArrayList<LessonData.lessionData>?) {
        favoriteBinding.favLessonRly.layoutManager = LinearLayoutManager(this)
        lessonadapter =
            FavoritelessonAdapter(data, this, this)
        favoriteBinding.favLessonRly.adapter = lessonadapter
    }

    private fun initEventRecyclerView(data: ArrayList<LessonData.lessionData>?) {
        favoriteBinding.favEventRly.layoutManager = LinearLayoutManager(this)
        eventadapter =
            FavoriteeventAdapter(data, this, this)
        favoriteBinding.favEventRly.adapter = eventadapter
    }

    private fun initTestRecyclerView(data: ArrayList<LessonData.lessionData>?) {
        favoriteBinding.favTestRly.layoutManager = LinearLayoutManager(this)
        testadapter =
            FavoritetestAdapter(data, this, this)
        favoriteBinding.favTestRly.adapter = testadapter
    }

    private fun initExerciseRecyclerView(data: ArrayList<LessonData.lessionData>?) {
        favoriteBinding.favExerciseRly.layoutManager = LinearLayoutManager(this)
        exerciseadapter =
            FavoriteexerciseAdapter(data, this, this)
        favoriteBinding.favExerciseRly.adapter = exerciseadapter
    }

    override fun onItemClicked(view: View, position: Int, type: Long, string: String) {
        if (string == "program") {
            favoriteBinding.favoriteProgress.visibility = View.VISIBLE
            apiInterface.DeleteFavourite_Program(type.toInt())?.enqueue(object :
                Callback<RegisterData?> {
                override fun onResponse(
                    call: Call<RegisterData?>,
                    response: Response<RegisterData?>
                ) {
                    val code = response.code()
                    if (code == 200) {
                        Log.d("TAG", response.code().toString() + "")
                        val resource: RegisterData? = response.body()
                        val Success: Boolean = resource?.status!!
                        val Message: String = resource.message!!
                        if (Success) {
                            favoriteBinding.favoriteProgress.visibility = View.GONE
                            Toast.makeText(this@FavoriteActivity, "" + Message, Toast.LENGTH_SHORT)
                                .show()
                            finish()
                            startActivity(intent)
                        } else {
                            favoriteBinding.favoriteProgress.visibility = View.GONE
                            Toast.makeText(this@FavoriteActivity, "" + Message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else if (response.code() == 403) {
                        Utils.setUnAuthDialog(this@FavoriteActivity)
                    } else {
                        val message = response.message()
                        Toast.makeText(this@FavoriteActivity, "" + message, Toast.LENGTH_SHORT)
                            .show()
                        call.cancel()
                    }

                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    favoriteBinding.favoriteProgress.visibility = View.GONE
                    Toast.makeText(this@FavoriteActivity, "" + t.message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }
            })

        } else if (string == "lesson") {
            favoriteBinding.favoriteProgress.visibility = View.VISIBLE
            apiInterface.DeleteFavourite_lession(type.toInt())?.enqueue(object :
                Callback<RegisterData?> {
                override fun onResponse(
                    call: Call<RegisterData?>,
                    response: Response<RegisterData?>
                ) {
                    Log.d("TAG", response.code().toString() + "")
                    val code = response.code()
                    if (code == 200) {
                        val resource: RegisterData? = response.body()
                        val Success: Boolean = resource?.status!!
                        val Message: String = resource.message!!
                        if (Success) {
                            favoriteBinding.favoriteProgress.visibility = View.GONE
                            Toast.makeText(this@FavoriteActivity, "" + Message, Toast.LENGTH_SHORT)
                                .show()
                            finish()
                            startActivity(intent)
                        } else {
                            favoriteBinding.favoriteProgress.visibility = View.GONE
                            Toast.makeText(this@FavoriteActivity, "" + Message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else if (response.code() == 403) {
                        Utils.setUnAuthDialog(this@FavoriteActivity)
                    } else {
                        val message = response.message()
                        Toast.makeText(this@FavoriteActivity, "" + message, Toast.LENGTH_SHORT)
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    favoriteBinding.favoriteProgress.visibility = View.GONE
                    Toast.makeText(this@FavoriteActivity, "" + t.message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }
            })
        } else if (string == "event") {
            favoriteBinding.favoriteProgress.visibility = View.VISIBLE
            apiInterface.DeleteFavourite_Event(type.toInt())?.enqueue(object :
                Callback<RegisterData?> {
                override fun onResponse(
                    call: Call<RegisterData?>,
                    response: Response<RegisterData?>
                ) {
                    Log.d("TAG", response.code().toString() + "")
                    val code = response.code()
                    if (code == 200) {
                        val resource: RegisterData? = response.body()
                        val Success: Boolean = resource?.status!!
                        val Message: String = resource.message!!
                        if (Success) {
                            favoriteBinding.favoriteProgress.visibility = View.GONE
                            Toast.makeText(this@FavoriteActivity, "" + Message, Toast.LENGTH_SHORT)
                                .show()
                            finish()
                            startActivity(intent)
                        } else {
                            favoriteBinding.favoriteProgress.visibility = View.GONE
                            Toast.makeText(this@FavoriteActivity, "" + Message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else if (response.code() == 403) {
                        Utils.setUnAuthDialog(this@FavoriteActivity)
                    } else {
                        val message = response.message()
                        Toast.makeText(this@FavoriteActivity, "" + message, Toast.LENGTH_SHORT)
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    favoriteBinding.favoriteProgress.visibility = View.GONE
                    Toast.makeText(this@FavoriteActivity, "" + t.message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }
            })

        } else if (string == "test") {
            favoriteBinding.favoriteProgress.visibility = View.VISIBLE
            apiInterface.DeleteFavourite_Test(type.toInt())?.enqueue(object :
                Callback<RegisterData?> {
                override fun onResponse(
                    call: Call<RegisterData?>,
                    response: Response<RegisterData?>
                ) {
                    Log.d("TAG", response.code().toString() + "")
                    val code = response.code()
                    if (code == 200) {
                        val resource: RegisterData? = response.body()
                        val Success: Boolean = resource?.status!!
                        val Message: String = resource.message!!
                        if (Success) {
                            favoriteBinding.favoriteProgress.visibility = View.GONE
                            Toast.makeText(this@FavoriteActivity, "" + Message, Toast.LENGTH_SHORT)
                                .show()
                            finish()
                            startActivity(intent)
                        } else {
                            favoriteBinding.favoriteProgress.visibility = View.GONE
                            Toast.makeText(this@FavoriteActivity, "" + Message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else if (response.code() == 403) {
                        Utils.setUnAuthDialog(this@FavoriteActivity)
                    } else {
                        val message = response.message()
                        Toast.makeText(this@FavoriteActivity, "" + message, Toast.LENGTH_SHORT)
                            .show()
                        call.cancel()
                    }

                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    favoriteBinding.favoriteProgress.visibility = View.GONE
                    Toast.makeText(this@FavoriteActivity, "" + t.message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }
            })

        } else if (string == "exercise") {
            favoriteBinding.favoriteProgress.visibility = View.VISIBLE
            apiInterface.DeleteFavourite_Exercise(type.toInt())?.enqueue(object :
                Callback<RegisterData?> {
                override fun onResponse(
                    call: Call<RegisterData?>,
                    response: Response<RegisterData?>
                ) {
                    Log.d("TAG", response.code().toString() + "")
                    val code = response.code()
                    if (code == 200) {
                        val resource: RegisterData? = response.body()
                        val Success: Boolean = resource?.status!!
                        val Message: String = resource.message!!
                        if (Success) {
                            favoriteBinding.favoriteProgress.visibility = View.GONE
                            Toast.makeText(this@FavoriteActivity, "" + Message, Toast.LENGTH_SHORT)
                                .show()
                            finish()
                            startActivity(intent)
                        } else {
                            favoriteBinding.favoriteProgress.visibility = View.GONE
                            Toast.makeText(this@FavoriteActivity, "" + Message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else if (response.code() == 403) {
                        Utils.setUnAuthDialog(this@FavoriteActivity)
                    } else {
                        val message = response.message()
                        Toast.makeText(this@FavoriteActivity, "" + message, Toast.LENGTH_SHORT)
                            .show()
                        call.cancel()
                    }

                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    favoriteBinding.favoriteProgress.visibility = View.GONE
                    Toast.makeText(this@FavoriteActivity, "" + t.message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }
            })

        }
    }

    private fun showProgram() {
        favoriteBinding.lyExercise.visibility = View.GONE
        favoriteBinding.lyProgram.visibility = View.VISIBLE
        favoriteBinding.lyLesson.visibility = View.GONE
        favoriteBinding.lyEvent.visibility = View.GONE
        favoriteBinding.lyTest.visibility = View.GONE
    }

    private fun showLesson() {
        favoriteBinding.lyExercise.visibility = View.GONE
        favoriteBinding.lyProgram.visibility = View.GONE
        favoriteBinding.lyLesson.visibility = View.VISIBLE
        favoriteBinding.lyEvent.visibility = View.GONE
        favoriteBinding.lyTest.visibility = View.GONE
    }

    private fun showEvent() {
        favoriteBinding.lyExercise.visibility = View.GONE
        favoriteBinding.lyProgram.visibility = View.GONE
        favoriteBinding.lyLesson.visibility = View.GONE
        favoriteBinding.lyEvent.visibility = View.VISIBLE
        favoriteBinding.lyTest.visibility = View.GONE
    }

    private fun showTest() {
        favoriteBinding.lyExercise.visibility = View.GONE
        favoriteBinding.lyProgram.visibility = View.GONE
        favoriteBinding.lyLesson.visibility = View.GONE
        favoriteBinding.lyEvent.visibility = View.GONE
        favoriteBinding.lyTest.visibility = View.VISIBLE
    }

    private fun showExercise() {
        favoriteBinding.lyExercise.visibility = View.VISIBLE
        favoriteBinding.lyProgram.visibility = View.GONE
        favoriteBinding.lyLesson.visibility = View.GONE
        favoriteBinding.lyEvent.visibility = View.GONE
        favoriteBinding.lyTest.visibility = View.GONE
    }

    private fun showAll() {
        favoriteBinding.lyExercise.visibility = View.VISIBLE
        favoriteBinding.lyProgram.visibility = View.VISIBLE
        favoriteBinding.lyLesson.visibility = View.VISIBLE
        favoriteBinding.lyEvent.visibility = View.VISIBLE
        favoriteBinding.lyTest.visibility = View.VISIBLE
        favoriteBinding.tvNodata.visibility = View.GONE

    }

    private fun NoData() {
        favoriteBinding.lyExercise.visibility = View.GONE
        favoriteBinding.lyProgram.visibility = View.GONE
        favoriteBinding.lyLesson.visibility = View.GONE
        favoriteBinding.lyEvent.visibility = View.GONE
        favoriteBinding.lyTest.visibility = View.GONE
        favoriteBinding.tvNodata.visibility = View.VISIBLE

    }


}