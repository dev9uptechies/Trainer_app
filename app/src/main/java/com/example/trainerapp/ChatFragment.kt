package com.example.trainerapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ChatActivity
import com.example.GroupChateListData
import com.example.OnItemClickListener
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.databinding.FragmentChatBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatFragment : Fragment(), OnItemClickListener.OnItemClickCallback {
    private lateinit var chatBinding: FragmentChatBinding

    private val chatUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("CHATFRAGMENTREFRESH", "Notification received - Refreshing chat")
            val userType = preferenceManager.GetFlage()

            if (userType == "Athlete") {
                callGroupChatApiAthlete()
            } else {
                callGroupChatApi()
            }
        }
    }

    private lateinit var apiInterface: APIInterface
    private lateinit var apiClient: APIClient
    private lateinit var preferenceManager: PreferencesManager
    private lateinit var groupAdapter: GroupChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        chatBinding = FragmentChatBinding.inflate(inflater, container, false)
        return chatBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!isAdded) return

        preferenceManager = PreferencesManager(requireContext())
        apiClient = APIClient(requireContext())
        apiInterface = apiClient.client().create(APIInterface::class.java)

        val userType = preferenceManager.GetFlage()

        if (userType == "Athlete") {
            callGroupChatApiAthlete()
        } else {
            callGroupChatApi()
        }

        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            chatUpdateReceiver,
            IntentFilter("com.example.trainerapp.REFRESH_CHAT")
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(chatUpdateReceiver)
    }

    private fun callGroupChatApi() {
        chatBinding.groupChatProgress.visibility = View.VISIBLE
        apiInterface.GropChateList()?.enqueue(object : Callback<GroupChateListData?> {
            override fun onResponse(
                call: Call<GroupChateListData?>,
                response: Response<GroupChateListData?>
            ) {
                if (!isAdded) return
                Log.d("TAG", response.code().toString())
                val code = response.code()
                chatBinding.groupChatProgress.visibility = View.GONE

                if (code == 200) {
                    val resource = response.body()
                    val success = resource?.status ?: false
                    val message = resource?.message ?: ""

                    if (success) {
                        initRecycler(resource!!.data!!)
                    } else {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                } else if (code == 403) {
                    handleSessionExpired()
                } else {
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<GroupChateListData?>, t: Throwable) {
                if (!isAdded) return
                chatBinding.groupChatProgress.visibility = View.GONE
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
                call.cancel()
            }
        })
    }

    private fun callGroupChatApiAthlete() {
        chatBinding.groupChatProgress.visibility = View.VISIBLE
        apiInterface.GropChateListAthlete()?.enqueue(object : Callback<GroupChateListData?> {
            override fun onResponse(
                call: Call<GroupChateListData?>,
                response: Response<GroupChateListData?>
            ) {
                if (!isAdded) return
                Log.d("TAG", response.code().toString())
                val code = response.code()
                chatBinding.groupChatProgress.visibility = View.GONE

                if (code == 200) {
                    val resource = response.body()
                    val success = resource?.status ?: false
                    val message = resource?.message ?: ""

                    if (success) {
                        initRecycler(resource!!.data!!)
                    } else {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                } else if (code == 403) {
                    handleSessionExpired()
                } else {
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<GroupChateListData?>, t: Throwable) {
                if (!isAdded) return
                chatBinding.groupChatProgress.visibility = View.GONE
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
                call.cancel()
            }
        })
    }

    private fun handleSessionExpired() {
        if (!isAdded) return
        preferenceManager.setUserLogIn(false)
        startActivity(Intent(requireContext(), SignInActivity::class.java))
        requireActivity().finish()
    }

    private fun initRecycler(data: ArrayList<GroupChateListData.groupData>) {
        context?.let { safeContext ->
            chatBinding.groupChatRly.layoutManager = LinearLayoutManager(safeContext)
            groupAdapter = GroupChatAdapter(data, safeContext, this)
            chatBinding.groupChatRly.adapter = groupAdapter
        }
    }

    override fun onItemClicked(view: View, position: Int, type: Long, string: String) {
        if (!isAdded) return
        startActivity(
            Intent(requireContext(), ChatActivity::class.java)
                .putExtra("GroupId", type.toString())
                .putExtra("UserName", string)
        )
    }
}
