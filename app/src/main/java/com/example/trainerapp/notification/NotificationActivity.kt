package com.example.trainerapp.notification

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.Adapter.personal_diary.NotificatioAdapter
import com.example.Adapter.training_plan.view.ViewMicrocycleListAdapter
import com.example.OnItemClickListener
import com.example.model.notification.NotificationModel
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.R
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.ActivityNotification2Binding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationActivity : AppCompatActivity(), OnItemClickListener.OnItemClickCallback {
    private lateinit var binding: ActivityNotification2Binding
    private lateinit var apiInterface: APIInterface
    private var NewNotification: MutableList<NotificationModel.Data> = mutableListOf() // Use MutableList here
    private lateinit var adapter: NotificatioAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotification2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        initview()
        loadData()
        ButtonClick()
    }

    private fun ButtonClick() {
        binding.back.setOnClickListener { finish() }
    }

    private fun loadData() {
        GetNewNotification()
    }

    private fun initview() {
        apiInterface = APIClient(this).client().create(APIInterface::class.java)
        adapter = NotificatioAdapter(NewNotification, this, this)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun GetNewNotification() {
        try {
            apiInterface.GetNewNotification()?.enqueue(object : Callback<NotificationModel> {
                override fun onResponse(call: Call<NotificationModel>, response: Response<NotificationModel>) {
                    Log.d("APIResponse", "Response Code: ${response.code()}")

                    when (response.code()) {
                        200 -> {
                            NewNotification.clear()  // Clear old data before adding new data
                            response.body()?.data?.let {
                                NewNotification.addAll(it)
                                Log.d("APIResponse", "Data added: $it")
                            } ?: run {
                                Log.d("APIResponse", "Response data is null")
                                Toast.makeText(this@NotificationActivity, "No data found", Toast.LENGTH_SHORT).show()
                            }


                            if (NewNotification.isNotEmpty()) {
                                setAdapter()
                            } else {
                                Log.d("APIResponse", "Response data is empty")
                                Toast.makeText(this@NotificationActivity, "No data found", Toast.LENGTH_SHORT).show()
                            }
                        }

                        403 -> {
                            Utils.setUnAuthDialog(this@NotificationActivity)
                        }

                        else -> {
                            val errorMessage = response.message() ?: "Unknown error occurred"
                            Log.d("APIResponse", "Error: $errorMessage")
                            Toast.makeText(this@NotificationActivity, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<NotificationModel>, t: Throwable) {
                    Log.e("APIError", "Network error: ${t.message}")
                    Toast.makeText(this@NotificationActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } catch (e: Exception) {
            Log.e("APIException", "Exception: ${e.message}")
            Toast.makeText(this@NotificationActivity, "An unexpected error occurred: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setAdapter() {
        adapter = NotificatioAdapter(NewNotification, this, this)
        binding.recyclerView.adapter = adapter
    }

    override fun onItemClicked(view: View, position: Int, type: Long, string: String) {
        // Handle item click here if necessary
    }
}
