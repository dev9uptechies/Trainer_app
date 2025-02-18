package com.example

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.databinding.ActivityChatBinding
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

    }

    private fun initView() {
        val groupid = intent.getStringExtra("GroupId")
        chatBinding.userName.text = intent.getStringExtra("UserName")
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        calapi(groupid)
    }

    private fun buttonClick() {
        chatBinding.back.setOnClickListener { finish() }
    }

    private fun calapi(groupid: String?) {
        val data: MutableMap<String, String> = HashMap()
        data["group_id"] = groupid!!
        apiInterface.GetChate(data)?.enqueue(object : Callback<ChateData?> {
            override fun onResponse(
                call: Call<ChateData?>?,
                response: Response<ChateData?>
            ) {
                Log.d("TAG", response.code().toString() + "")
                val resource: ChateData? = response.body()
                val Success: Boolean = resource?.status!!
                val Message: String = resource.message!!
                if (Success) {

                }
            }

            override fun onFailure(call: Call<ChateData?>, t: Throwable?) {
                Toast.makeText(this@ChatActivity, "" + t!!.message, Toast.LENGTH_SHORT)
                    .show()
                call.cancel()
            }
        })

    }
}