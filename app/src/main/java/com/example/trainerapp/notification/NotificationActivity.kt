package com.example.trainerapp.notification

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.Adapter.personal_diary.NotificatioAdapter
import com.example.Adapter.personal_diary.ReadNotificatioAdapter
import com.example.OnItemClickListener
import com.example.model.notification.NotificationData
import com.example.model.notification.NotificationModel
import com.example.model.notification.ReadNotificationModel
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.ActivityNotification2Binding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationActivity : AppCompatActivity(), OnItemClickListener.OnItemClickCallback {
    private lateinit var binding: ActivityNotification2Binding
    private lateinit var apiInterface: APIInterface
    private var NewNotification: MutableList<NotificationModel.Data> = mutableListOf() // Use MutableList here
    private var ReadNotification: MutableList<NotificationData> = mutableListOf() // Use MutableList here
    private var ReadNotifications: MutableList<NotificationModel.Data> = mutableListOf() // Use MutableList here
    private lateinit var adapter: NotificatioAdapter
    private lateinit var readadapter :ReadNotificatioAdapter

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

        readadapter = ReadNotificatioAdapter(ReadNotifications, this, this)
        binding.recyclerIew.layoutManager = LinearLayoutManager(this)
        binding.recyclerIew.adapter = readadapter
    }

    private fun GetNewNotification() {
        try {
            apiInterface.GetNewNotification()?.enqueue(object : Callback<NotificationModel> {
                override fun onResponse(call: Call<NotificationModel>, response: Response<NotificationModel>) {
                    Log.d("APIResponse", "Response Code: ${response.code()}")

                    when (response.code()) {
                        200 -> {
                            val notifications = response.body()?.data ?: emptyList()
                            NewNotification.clear()
                            ReadNotifications.clear()
//                            NewNotification.addAll(notifications)
                            Log.d("APIResponse", "Notifications fetched: $notifications")

                            NewNotification = notifications.filter { it.status == 0 }.toMutableList() ?: mutableListOf()
                            ReadNotifications = notifications.filter { it.status == 1 }.toMutableList() ?: mutableListOf()

                            if (NewNotification.isNotEmpty()) {
                                processUnreadNotifications(NewNotification, 0)
                                setAdapter()
                            } else if (ReadNotifications.isNotEmpty()) {
                                    setReadAdapter()
                                Log.d("APIResponse", "No unread notifications")
                            }else{
                                binding.tvNodata.visibility = View.VISIBLE
                                binding.dotp.visibility = View.GONE
                                binding.recyclerIew.visibility = View.GONE
                                binding.recyclerView.visibility = View.GONE
                                binding.readTxt.visibility = View.GONE
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

    // Process unread notifications one by one
    private fun processUnreadNotifications(unreadNotifications: List<NotificationModel. Data>, index: Int) {
        if (index >= unreadNotifications.size) {
            Log.d("Process", "All unread notifications marked as read")
            return
        }

        val id = unreadNotifications[index].id
        Log.d("APIResponse", "Marking as read: $id")

        ReadNotification(id) {
            processUnreadNotifications(unreadNotifications, index + 1)
        }
    }

    private fun ReadNotification(id: Int, onComplete: () -> Unit) {
        try {
            apiInterface.ReadNotification(id)?.enqueue(object : Callback<ReadNotificationModel> {
                override fun onResponse(call: Call<ReadNotificationModel>, response: Response<ReadNotificationModel>) {
                    Log.d("APIResponse", "Read Notification Response Code: ${response.code()}")

                    if (response.isSuccessful) {
                        response.body()?.data?.let { notification ->
                            setReadAdapter()
                            Log.d("APIResponsesssss", "Notification marked as read: $notification")
                        } ?: run {
                            Log.d("APIResponse", "Response data is null")
                        }
                    } else {
                        Log.e("APIError", "Failed to mark as read: ${response.errorBody()?.string()}")
                    }
                    onComplete()
                }

                override fun onFailure(call: Call<ReadNotificationModel>, t: Throwable) {
                    Log.e("APIError", "Network error: ${t.message}")
                    onComplete()
                }
            })
        } catch (e: Exception) {
            Log.e("APIException", "Exception: ${e.message}")
            onComplete()
        }
    }

    private fun DeleteNotification(id:Int) {
        try {
            apiInterface.DeleteNotification(id)?.enqueue(object : Callback<NotificationModel> {
                override fun onResponse(call: Call<NotificationModel>, response: Response<NotificationModel>) {
                    Log.d("APIResponse", "Response Code: ${response.code()}")

                    if (response.isSuccessful) {
                        GetNewNotification()
                        Log.d("DELETE", "Item deleted successfully")
                    } else {
                        Log.e("DELETE", "Failed to delete item: ${response.errorBody()?.string()}")
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

    private fun setReadAdapter() {
        readadapter = ReadNotificatioAdapter(ReadNotifications, this, this)
        binding.recyclerIew.adapter = readadapter
    }

    override fun onItemClicked(view: View, position: Int, type: Long, string: String) {

        Log.d("AKAKAKAKAKKA", "onItemClicked: $string")
        if (string == "Delete"){
            DeleteNotification(type.toInt())
        }

    }
}
