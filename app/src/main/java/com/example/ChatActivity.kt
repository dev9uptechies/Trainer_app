package com.example

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.Adapter.ChatAdapter
import com.example.model.ChatMessage
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.databinding.ActivityChatBinding
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatActivity : AppCompatActivity() {
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    lateinit var chatBinding: ActivityChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chatBinding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(chatBinding.root)

        initView()
        buttonClick()

        chatBinding.btnSend.setOnClickListener(View.OnClickListener {

        })
    }

    private fun initView() {
        val groupid = intent.getStringExtra("GroupId")
        chatBinding.userName.text = intent.getStringExtra("UserName")
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)

        if (groupid != null) {
            calapi(groupid)
        } else {
            Toast.makeText(this, "Group ID is null", Toast.LENGTH_SHORT).show()
        }
    }

    private fun buttonClick() {
        chatBinding.back.setOnClickListener { finish() }
    }

    private fun calapi(groupid: String) {
        val data: MutableMap<String, String> = HashMap()
        data["group_id"] = groupid

        apiInterface.GetChate(data)?.enqueue(object : Callback<ChateData?> {
            override fun onResponse(call: Call<ChateData?>, response: Response<ChateData?>) {
                if (response.isSuccessful && response.body() != null) {
                    val chatData = response.body()
                    Log.e("CHATDATAAAAAAAA", "onResponse: "+response.body() )
                    Log.d("CHATDATAAAAAAAA", "Extracted mesocycles: ${Gson().toJson(chatData)}")
                    val chatMessages = chatData?.data?.mapNotNull {

                        Log.d("CHATDATAAAAAAAAA", "Extracted mesocycles: ${Gson().toJson(chatData.data)}")
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

                    chatBinding.chatRly.layoutManager = LinearLayoutManager(this@ChatActivity)
                    chatBinding.chatRly.adapter = ChatAdapter(this@ChatActivity, chatMessages)
                } else {
                    Toast.makeText(this@ChatActivity, "Failed to load messages", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ChateData?>, t: Throwable) {
                Toast.makeText(this@ChatActivity, t.message ?: "Unknown error", Toast.LENGTH_SHORT).show()
                call.cancel()
            }
        })
    }
}
