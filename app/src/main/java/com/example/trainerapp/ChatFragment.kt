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
    lateinit var chatBinding: FragmentChatBinding

    private val chatUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context:     Context?, intent: Intent?) {
            Log.d("CHATFRAGMENTREFRESH", "Notification received - Refreshing chat")
            val userType = preferenceManager.GetFlage()

            if (userType == "Athlete") {
                callGroupChatApiAthlete()
            } else {
                callGroupChatApi()
            }
        }
    }

    //lateinit var recycler_view: RecyclerView
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    lateinit var preferenceManager: PreferencesManager

    lateinit var groupadapter: GroupChatAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        chatBinding = FragmentChatBinding.inflate(inflater, container, false)
//        val view: View = inflater.inflate(R.layout.fragment_chat, container, false)
//        recycler_view = view.findViewById(R.id.group_chat_rly)
//        group_progress = view.findViewById(R.id.group_chat_progress)

        apiClient = APIClient(requireContext())
        preferenceManager = PreferencesManager(requireContext())
        apiInterface = apiClient.client().create(APIInterface::class.java)

        val userType = preferenceManager.GetFlage()

        if (userType == "Athlete") {
            callGroupChatApiAthlete()
        }else{
            callGroupChatApi()
        }

        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            chatUpdateReceiver,
            IntentFilter("com.example.trainerapp.REFRESH_CHAT")
        )

        return chatBinding.root
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
                Log.d("TAG", response.code().toString() + "")
                val code = response.code()
                if (code == 200) {
                    val resource: GroupChateListData? = response.body()
                    val Success: Boolean = resource?.status!!
                    val Message: String = resource.message!!
                    if (Success) {
                        chatBinding.groupChatProgress.visibility = View.GONE
                        initRecycler(resource.data!!)
                    } else {
                        chatBinding.groupChatProgress.visibility = View.GONE
                        Toast.makeText(requireContext(), "" + Message, Toast.LENGTH_SHORT).show()
                    }
                } else if (response.code() == 403) {
                    chatBinding.groupChatProgress.visibility = View.GONE
                    val message = response.message()
                    Toast.makeText(requireContext(), "Too Many Request", Toast.LENGTH_SHORT).show()
                    call.cancel()
                    preferenceManager.setUserLogIn(false)
                    startActivity(
                        Intent(
                            requireContext(),
                            SignInActivity::class.java
                        )
                    )
                    requireActivity().finish()
                } else {
                    chatBinding.groupChatProgress.visibility = View.GONE
                    val message = response.message()
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<GroupChateListData?>, t: Throwable) {
                Toast.makeText(requireContext(),t.message, Toast.LENGTH_SHORT).show()
                call.cancel()
                chatBinding.groupChatProgress.visibility = View.GONE
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
                Log.d("TAG", response.code().toString() + "")
                val code = response.code()
                if (code == 200) {
                    val resource: GroupChateListData? = response.body()
                    val Success: Boolean = resource?.status!!
                    val Message: String = resource.message!!
                    if (Success) {
                        Log.d("SPSPSPPSPS", "onResponse: Refresh")
                        chatBinding.groupChatProgress.visibility = View.GONE
                        initRecycler(resource.data!!)
                    } else {
                        chatBinding.groupChatProgress.visibility = View.GONE
                        Toast.makeText(requireContext(), "" + Message, Toast.LENGTH_SHORT).show()
                    }
                } else if (response.code() == 403) {
                    chatBinding.groupChatProgress.visibility = View.GONE
                    val message = response.message()
                    Toast.makeText(
                        requireContext(),
                        "Too Many Request",
                        Toast.LENGTH_SHORT
                    ).show()
                    call.cancel()
                    preferenceManager.setUserLogIn(false)
                    startActivity(
                        Intent(
                            requireContext(),
                            SignInActivity::class.java
                        )
                    )
                    requireActivity().finish()
                } else {
                    chatBinding.groupChatProgress.visibility = View.GONE
                    val message = response.message()
                    Toast.makeText(requireContext(), "" + message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<GroupChateListData?>, t: Throwable) {
                Toast.makeText(requireContext(), "" + t.message, Toast.LENGTH_SHORT).show()
                call.cancel()
                chatBinding.groupChatProgress.visibility = View.GONE
            }
        })
    }

    private fun initRecycler(data: ArrayList<GroupChateListData.groupData>) {
        chatBinding.groupChatRly.setLayoutManager(LinearLayoutManager(requireContext()))
        groupadapter = GroupChatAdapter(data, requireContext(), this)
        chatBinding.groupChatRly.adapter = groupadapter
    }

    override fun onItemClicked(view: View, position: Int, type: Long, string: String) {
        startActivity(Intent(requireContext(), ChatActivity::class.java)
            .putExtra("GroupId", type.toString())
            .putExtra("UserName", string)
        )
    }

}