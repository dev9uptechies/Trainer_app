package com.example

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.Adapter.ChatAdapter
import com.example.model.ChatMessage
import com.example.model.MessageRequest
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.PreferencesManager
import com.example.trainerapp.databinding.ActivityChatBinding
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ChatActivity : AppCompatActivity() {
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    lateinit var chatBinding: ActivityChatBinding
    private lateinit var chatAdapter: ChatAdapter
    private val chatMessages = mutableListOf<ChatMessage>()


    lateinit var preferenceManager: PreferencesManager

    var groupid: String ?= null

    private val chatUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("CHATFRAGMENTREFRESH", "Notification received - Refreshing chat")

            if (!isFinishing && !isDestroyed) {
                groupid?.let {
                    calapi(it)
                } ?: run {
                    Toast.makeText(this@ChatActivity, "Group ID is null", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }


    override fun onResume() {
        super.onResume()
        try {
        if (groupid != null) {
            calapi(groupid!!)
        } else {
            Toast.makeText(this, "Group ID is null", Toast.LENGTH_SHORT).show()
        }
        }catch (e:Exception){

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chatBinding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(chatBinding.root)

        initView()
        buttonClick()

        preferenceManager = PreferencesManager(this)

        LocalBroadcastManager.getInstance(this).registerReceiver(
            chatUpdateReceiver,
            IntentFilter("com.example.trainerapp.REFRESH_CHAT")
        )
    }

    private fun initView() {
        groupid = intent.getStringExtra("GroupId")
        Log.d("XOOXOXO", "initView: $groupid")
        chatBinding.userName.text = intent.getStringExtra("UserName")
        preferenceManager = PreferencesManager(this)
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        chatAdapter = ChatAdapter(this, chatMessages)
        chatBinding.chatRly.layoutManager = LinearLayoutManager(this)
        chatBinding.chatRly.adapter = chatAdapter

        if (groupid != null) {
            calapi(groupid!!)
        } else {
            Toast.makeText(this, "Group ID is null", Toast.LENGTH_SHORT).show()
        }

    }

    private fun buttonClick() {
        chatBinding.back.setOnClickListener { finish() }

        chatBinding.btnSend.setOnClickListener {
            sendMessageToApi(groupid.toString(),chatBinding.etMessage.text.toString())
        }

        chatBinding.icEmoji.setOnClickListener {
            chatBinding.etMessage.requestFocus()

            Log.d("SKSKSKSK", "buttonClick: okok")

            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(chatBinding.etMessage, InputMethodManager.SHOW_FORCED)

            val event = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SEARCH)
            chatBinding.etMessage.dispatchKeyEvent(event)
        }



    }

    private fun calapi(groupid: String) {
        chatBinding.groupChatProgress.visibility = View.VISIBLE
        Log.d("SSLLSLSLSL", "calapi: kokokk")

        val data: MutableMap<String, String> = HashMap()
        data["group_id"] = groupid

        apiInterface.GetChate(data)?.enqueue(object : Callback<ChateData?> {
            override fun onResponse(call: Call<ChateData?>, response: Response<ChateData?>) {
                chatBinding.groupChatProgress.visibility = View.GONE

                if (response.isSuccessful && response.body() != null) {
                    val chatData = response.body()
                    Log.e("CHATDATA", "onResponse: ${Gson().toJson(chatData)}")

                    val newMessages = chatData?.data?.mapNotNull { it ->
                        if (it.sender != null) {
                            ChatMessage(
                                senderId = it.sender_id?.toString() ?: "",
                                senderName = it.sender?.name ?: "Unknown",
                                senderImage = it.sender?.image ?: "",
                                message = it.message ?: ""
                            )
                        } else {
                            null
                        }
                    } ?: emptyList()

                    // Update messages list
                    chatMessages.clear()
                    chatMessages.addAll(newMessages)

                    // Notify adapter of data change
                    chatAdapter.notifyDataSetChanged()

                    // Scroll to the latest message
                    scrollToLatestMessage()
                } else {
                    Toast.makeText(this@ChatActivity, "Failed to load messages", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ChateData?>, t: Throwable) {
                chatBinding.groupChatProgress.visibility = View.GONE
                Toast.makeText(this@ChatActivity, t.message ?: "Unknown error", Toast.LENGTH_SHORT).show()
                call.cancel()
            }
        })
    }

    private fun sendMessageToApi(groupid: String, message: String) {
        chatBinding.groupChatProgress.visibility = View.VISIBLE

        Log.d("APICHTA", "Preparing request with group_id: $groupid and message: $message")

        val request = MessageRequest(group_id = groupid, message = message)
        Log.d("APICHTA", "Request Object Created: $request")

        apiInterface.sendMessage(request).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Log.d("APICHTA", "API Call Successful - Response Received")
                chatBinding.groupChatProgress.visibility = View.GONE
                hideKeyboard()  // Hide keyboard after API response

                if (response.isSuccessful) {
                    chatBinding.etMessage.setText("")
                    onResume()
                    Log.d("APICHTA", "Message sent successfully! Response Code: ${response.code()}")
                } else {
                    Log.e("APICHTA", "API Call Failed - Response Code: ${response.code()}, Message: ${response.message()}")
                    try {
                        val errorBody = response.errorBody()?.string()
                        Log.e("APICHTA", "Error Body: $errorBody")
                    } catch (e: Exception) {
                        Log.e("APICHTA", "Error reading errorBody: ${e.message}")
                    }
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                chatBinding.groupChatProgress.visibility = View.GONE
                hideKeyboard()  // Hide keyboard even on failure
                Log.e("APICHTA", "API Call Failed - Network Error: ${t.message}")
                Log.e("APICHTA", "Request Failed: ${call.request().url}")
            }
        })

        Log.d("APICHTA", "API Request Sent - Awaiting Response...")
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(chatBinding.etMessage.windowToken, 0)
    }

    private fun scrollToLatestMessage() {
        chatBinding.chatRly.post {
            if (chatAdapter.itemCount > 0) {
                chatBinding.chatRly.smoothScrollToPosition(chatAdapter.itemCount - 1)
            }
        }
    }


}
