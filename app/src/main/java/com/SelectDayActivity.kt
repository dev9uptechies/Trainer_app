package com

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.Adapter.selected_day.LessonAdapter
import com.example.Adapter.selected_day.eventAdapter
import com.example.Adapter.selected_day.testAdapter
import com.example.AddSelectedActivity
import com.example.OnItemClickListener
import com.example.model.SelectedDaysModel
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.ActivitySelectDayBinding
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SelectDayActivity : AppCompatActivity(), OnItemClickListener.OnItemClickCallback {

    lateinit var binding: ActivitySelectDayBinding
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient

    lateinit var lessonadapter: LessonAdapter
    lateinit var eventadapter: eventAdapter
    lateinit var testadapter: testAdapter

    var groupId: Int = 0
    var date: String? = null

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
                        fetchDayData()
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@SelectDayActivity)
                    } else {
                        Toast.makeText(
                            this@SelectDayActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Toast.makeText(
                        this@SelectDayActivity,
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
        binding = ActivitySelectDayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)

        groupId = intent.getIntExtra("id", 0)
        date = intent.getStringExtra("date")
        Log.d("idok", "IDDD:-    $groupId")
        Log.d("idok", "DATE:-    $date")

        binding.tvCreditit.text = date

        ButtonCLick()
        loadData()

    }

    private fun loadData() {
        fetchDayData()
    }

    private fun ButtonCLick() {

        binding.back.setOnClickListener { finish() }

        binding.add.setOnClickListener {
            val intent = Intent(this, AddSelectedActivity::class.java)
            intent.putExtra("Add", "lesson")
            intent.putExtra("Date", date)
            intent.putExtra("GroupId", groupId)
            startActivity(intent)
        }

        binding.eventAdd.setOnClickListener {
            val intent = Intent(this, AddSelectedActivity::class.java)
            intent.putExtra("Add", "event")
            intent.putExtra("Date", date)
            intent.putExtra("GroupId", groupId)
            startActivity(intent)
        }

        binding.addTest.setOnClickListener {
            val intent = Intent(this, AddSelectedActivity::class.java)
            intent.putExtra("Add", "test")
            intent.putExtra("Date", date)
            intent.putExtra("GroupId", groupId)
            startActivity(intent)
        }
    }

    private fun fetchDayData() {
        try {
            binding.progresBar.visibility = View.VISIBLE
            apiInterface.GetSelectedDays(date, groupId.toString())!!
                .enqueue(object : Callback<SelectedDaysModel> {
                    override fun onResponse(
                        call: Call<SelectedDaysModel>,
                        response: Response<SelectedDaysModel>
                    ) {
                        if (response.isSuccessful && response.body() != null) {

                            binding.progresBar.visibility = View.GONE

                            val selectedDaysModel = response.body()

                            Log.d("API Response", "Response: $selectedDaysModel")

                            val data = selectedDaysModel?.data
                            if (data != null) {
                                if (data.tests.isNotEmpty()) {
                                    Log.d(
                                        "First Test",
                                        "Test ID: ${data.tests[0].id}, Title: ${data.tests[0].title}"
                                    )
                                } else {
                                    Log.d("First Test", "No tests available.")
                                }

                                initTestRecyclerView(data.tests)
                                initLessonRecyclerView(data.lessons)

                                Log.d("Event Data", "Events: ${data.events}")

                                initEventRecyclerView(data.events)
                            } else {
                                Log.e("API Response", "Data is null.")
                                Toast.makeText(
                                    this@SelectDayActivity,
                                    "Data is null",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Log.e("API Response", "Failed to fetch data: ${response.message()}")
                            Toast.makeText(
                                this@SelectDayActivity,
                                "Failed to fetch data",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<SelectedDaysModel>, t: Throwable) {
                        binding.progresBar.visibility = View.GONE
                        Log.e("API Response", "Error: ${t.message}")
                        Toast.makeText(
                            this@SelectDayActivity,
                            "Error: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        } catch (e: Exception) {
            binding.progresBar.visibility = View.GONE
            Log.e("Catch", "CatchError :- ${e.message}")
        }
    }

    private fun initLessonRecyclerView(programs: List<SelectedDaysModel.Lesson>) {
        binding.favLessonRly.layoutManager = LinearLayoutManager(this)
        lessonadapter = LessonAdapter(programs, this, this)
        binding.favLessonRly.adapter = lessonadapter
    }

    private fun initTestRecyclerView(tests: List<SelectedDaysModel.Test>) {
        binding.favTestRly.layoutManager = LinearLayoutManager(this)
        testadapter = testAdapter(tests, this, this)
        binding.favTestRly.adapter = testadapter
    }

    private fun initEventRecyclerView(events: List<SelectedDaysModel.Event>) {
        if (events.isNotEmpty()) {
            Log.d("Event RecyclerView", "Setting up RecyclerView with events.")
            binding.favEventRly.layoutManager = LinearLayoutManager(this)
            eventadapter = eventAdapter(events, this, this)
            binding.favEventRly.adapter = eventadapter
        } else {
            Log.d("Event RecyclerView", "No events available.")
        }
    }

    override fun onItemClicked(view: View, position: Int, type: Long, string: String) {
        if (string == "fav") {
            binding.progresBar.visibility = View.VISIBLE
            val id: MultipartBody.Part =
                MultipartBody.Part.createFormData("id", type.toInt().toString())
            apiInterface.Favourite_lession(id)?.enqueue(object : Callback<RegisterData?> {
                override fun onResponse(
                    call: Call<RegisterData?>,
                    response: Response<RegisterData?>
                ) {
                    binding.progresBar.visibility = View.GONE
                    Log.d("TAG", response.code().toString() + "")
                    val code = response.code()
                    if (code == 200) {
                        val resource: RegisterData? = response.body()
                        val Success: Boolean = resource?.status!!
                        val Message: String = resource.message!!
                        if (Success) {
                            binding.progresBar.visibility = View.GONE
                            loadData()
                        } else {
                            binding.progresBar.visibility = View.GONE
                            Toast.makeText(
                                this@SelectDayActivity,
                                "" + Message,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@SelectDayActivity)
                    } else {
                        binding.progresBar.visibility = View.GONE
                        Toast.makeText(
                            this@SelectDayActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    binding.progresBar.visibility = View.GONE
                    Toast.makeText(this@SelectDayActivity, "" + t.message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }
            })
        } else if (string == "unfav") {
            binding.progresBar.visibility = View.VISIBLE
            val id: MultipartBody.Part =
                MultipartBody.Part.createFormData("id", type.toInt().toString())
            apiInterface.DeleteFavourite_lession(type.toInt())
                ?.enqueue(object : Callback<RegisterData?> {
                    override fun onResponse(
                        call: Call<RegisterData?>,
                        response: Response<RegisterData?>
                    ) {
                        Log.d("TAG", response.code().toString() + "")
                        binding.progresBar.visibility = View.GONE
                        val code = response.code()
                        if (code == 200) {
                            val resource: RegisterData? = response.body()
                            val Success: Boolean = resource?.status!!
                            val Message: String = resource.message!!
                            if (Success) {
                                binding.progresBar.visibility = View.GONE
                                loadData()
                            } else {
                                binding.progresBar.visibility = View.GONE
                                Toast.makeText(
                                    this@SelectDayActivity,
                                    "" + Message,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        } else if (code == 403) {
                            Utils.setUnAuthDialog(this@SelectDayActivity)
                        } else {
                            Toast.makeText(
                                this@SelectDayActivity,
                                "" + response.message(),
                                Toast.LENGTH_SHORT
                            ).show()
                            call.cancel()
                        }
                    }

                    override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                        binding.progresBar.visibility = View.GONE
                        Toast.makeText(
                            this@SelectDayActivity,
                            "" + t.message,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                })
        } else if (string == "favtest"){
            binding.progresBar.visibility = View.VISIBLE
            val id: MultipartBody.Part =
                MultipartBody.Part.createFormData("id", type.toInt().toString())
            apiInterface.Favourite_Test(id)?.enqueue(object : Callback<RegisterData?> {
                override fun onResponse(
                    call: Call<RegisterData?>,
                    response: Response<RegisterData?>
                ) {
                    binding.progresBar.visibility = View.GONE
                    Log.d("TAG", response.code().toString() + "")
                    val code = response.code()
                    if (code == 200) {
                        val resource: RegisterData? = response.body()
                        val Success: Boolean = resource?.status!!
                        val Message: String = resource.message!!
                        if (Success) {
                            binding.progresBar.visibility = View.GONE
                            loadData()
                        } else {
                            binding.progresBar.visibility = View.GONE
                            Toast.makeText(
                                this@SelectDayActivity,
                                "" + Message,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@SelectDayActivity)
                    } else {
                        binding.progresBar.visibility = View.GONE
                        Toast.makeText(
                            this@SelectDayActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    binding.progresBar.visibility = View.GONE
                    Toast.makeText(this@SelectDayActivity, "" + t.message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }
            })
        } else if (string == "unfavtest") {
            binding.progresBar.visibility = View.VISIBLE
            var id: MultipartBody.Part =
                MultipartBody.Part.createFormData("id", type.toInt().toString())
            apiInterface.DeleteFavourite_Test(type.toInt())
                ?.enqueue(object : Callback<RegisterData?> {
                    override fun onResponse(
                        call: Call<RegisterData?>,
                        response: Response<RegisterData?>
                    ) {
                        Log.d("TAG", response.code().toString() + "")
                        binding.progresBar.visibility = View.GONE
                        val code = response.code()
                        if (code == 200) {
                            val resource: RegisterData? = response.body()
                            val Success: Boolean = resource?.status!!
                            val Message: String = resource.message!!
                            if (Success) {
                                binding.progresBar.visibility = View.GONE
                                loadData()
                            } else {
                                binding.progresBar.visibility = View.GONE
                                Toast.makeText(
                                    this@SelectDayActivity,
                                    "" + Message,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        } else if (code == 403) {
                            Utils.setUnAuthDialog(this@SelectDayActivity)
                        } else {
                            Toast.makeText(
                                this@SelectDayActivity,
                                "" + response.message(),
                                Toast.LENGTH_SHORT
                            ).show()
                            call.cancel()
                        }
                    }

                    override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                        binding.progresBar.visibility = View.GONE
                        Toast.makeText(
                            this@SelectDayActivity,
                            "" + t.message,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                })
        } else if (string == "favevent"){
            binding.progresBar.visibility = View.VISIBLE
            val id: MultipartBody.Part =
                MultipartBody.Part.createFormData("id", type.toInt().toString())
            apiInterface.Favourite_Event(id)?.enqueue(object : Callback<RegisterData?> {
                override fun onResponse(
                    call: Call<RegisterData?>,
                    response: Response<RegisterData?>
                ) {
                    binding.progresBar.visibility = View.GONE
                    Log.d("TAG", response.code().toString() + "")
                    val code = response.code()
                    if (code == 200) {
                        val resource: RegisterData? = response.body()
                        val Success: Boolean = resource?.status!!
                        val Message: String = resource.message!!
                        if (Success) {
                            binding.progresBar.visibility = View.GONE
                            loadData()
                        } else {
                            binding.progresBar.visibility = View.GONE
                            Toast.makeText(
                                this@SelectDayActivity,
                                "" + Message,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@SelectDayActivity)
                    } else {
                        binding.progresBar.visibility = View.GONE
                        Toast.makeText(
                            this@SelectDayActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    binding.progresBar.visibility = View.GONE
                    Toast.makeText(this@SelectDayActivity, "" + t.message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }
            })
        } else if (string == "unfavevent") {
            binding.progresBar.visibility = View.VISIBLE
            val id: MultipartBody.Part =
                MultipartBody.Part.createFormData("id", type.toInt().toString())
            apiInterface.DeleteFavourite_Event(type.toInt())
                ?.enqueue(object : Callback<RegisterData?> {
                    override fun onResponse(
                        call: Call<RegisterData?>,
                        response: Response<RegisterData?>
                    ) {
                        Log.d("TAG", response.code().toString() + "")
                        binding.progresBar.visibility = View.GONE
                        val code = response.code()
                        if (code == 200) {
                            val resource: RegisterData? = response.body()
                            val Success: Boolean = resource?.status!!
                            val Message: String = resource.message!!
                            if (Success) {
                                binding.progresBar.visibility = View.GONE
                                loadData()
                            } else {
                                binding.progresBar.visibility = View.GONE
                                Toast.makeText(
                                    this@SelectDayActivity,
                                    "" + Message,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        } else if (code == 403) {
                            Utils.setUnAuthDialog(this@SelectDayActivity)
                        } else {
                            Toast.makeText(
                                this@SelectDayActivity,
                                "" + response.message(),
                                Toast.LENGTH_SHORT
                            ).show()
                            call.cancel()
                        }
                    }

                    override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                        binding.progresBar.visibility = View.GONE
                        Toast.makeText(
                            this@SelectDayActivity,
                            "" + t.message,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                })
        }
    }
}