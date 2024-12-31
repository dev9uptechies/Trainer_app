    package com.example

    import android.content.Intent
    import android.os.Bundle
    import android.util.Log
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.Toast
    import androidx.fragment.app.Fragment
    import androidx.recyclerview.widget.LinearLayoutManager
    import com.example.trainerapp.ApiClass.APIClient
    import com.example.trainerapp.ApiClass.APIInterface
    import com.example.trainerapp.ApiClass.RegisterData
    import com.example.trainerapp.PreferencesManager
    import com.example.trainerapp.SignInActivity
    import com.example.trainerapp.Utils
    import com.example.trainerapp.databinding.FragmentGroupBinding
    import retrofit2.Call
    import retrofit2.Callback
    import retrofit2.Response

    class GroupFragment : Fragment(), OnItemClickListener.OnItemClickCallback {
        lateinit var apiInterface: APIInterface
        lateinit var apiClient: APIClient
        lateinit var preferenceManager: PreferencesManager
        lateinit var groupadapter: GroupAdapter
        private var receivedId: String = ""
        private var groupList: ArrayList<GroupListData.groupData> = ArrayList()


        override fun onResume() {
            checkUser()
            super.onResume()
        }

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
                            callGroupApi()
                            Log.d("Get Profile Data ", "${response.body()}")
                        } else if (code == 403) {
                            Utils.setUnAuthDialog(requireContext())
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "" + response.message(),
                                Toast.LENGTH_SHORT
                            ).show()
                            call.cancel()
                        }
                    }

                    override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                        Toast.makeText(
                            requireContext(),
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


    //    lateinit var group_progress: ProgressBar
    //    lateinit var create_group: ImageView
        lateinit var groupBinding: FragmentGroupBinding
        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            groupBinding = FragmentGroupBinding.inflate(layoutInflater, container, false)

            receivedId = arguments?.getString("isd").toString()
            Log.d("GroupFragment", "Received ID: $receivedId")

    //        recycler_view = view.findViewById(R.id.group_rly)
    //        group_progress = view.findViewById(R.id.group_progress)
    //        create_group = view.findViewById(R.id.create_group)
            apiClient = APIClient(requireContext())
            preferenceManager = PreferencesManager(requireContext())
            apiInterface = apiClient.client().create(APIInterface::class.java)
            groupBinding.createGroup.setOnClickListener {
                startActivity(Intent(requireContext(), CreateGropActivity::class.java))
            }

            val userType = preferenceManager.GetFlage()

            if (userType == "Athlete") {
                groupBinding.createGroup.visibility = View.GONE
                callGroupApiAthlete()
            }else{
                groupBinding.createGroup.visibility = View.VISIBLE
                callGroupApi()
            }
            return groupBinding.root
        }

        private fun callGroupApiAthlete() {
            try {
            groupBinding.groupProgress.visibility = View.VISIBLE
            apiInterface.GropListAthlete().enqueue(object : Callback<GroupListData?> {
                override fun onResponse(
                    call: Call<GroupListData?>,
                    response: Response<GroupListData?>
                ) {

                    val code = response.code()
                    if (code == 200) {
                        val resource: GroupListData? = response.body()
                        val Success: Boolean = resource?.status!!
                        val Message: String = resource.message!!

                        if (Success == true) {
                            groupBinding.groupProgress.visibility = View.GONE
                            groupList = resource.data!! // Populate the groupList here
                            initrecycler(resource.data!!)

                            for (data in resource.data!!){
                                Log.d("GHHGHGHGHGH", "onResponse: Name:- ${data.name }")
                            }

                        } else {
                            groupBinding.groupProgress.visibility = View.GONE
                        }
                    } else if (response.code() == 403) {
                        groupBinding.groupProgress.visibility = View.GONE
                        val message = response.message()
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
                        groupBinding.groupProgress.visibility = View.GONE
                        val message = response.message()
                        //                    Toast.makeText(requireContext(), "" + message, Toast.LENGTH_SHORT)
                        //                        .show()

                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<GroupListData?>, t: Throwable) {
                    Toast.makeText(requireContext(), "" + t.message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                    Log.d("GROOOOOOOP", t.message.toString() + "")
                    groupBinding.groupProgress.visibility = View.GONE
                }
            })
            }catch (e:Exception){
                Log.d("catch", "callGroupApiAthlete: ${e.message.toString()}")
            }
        }

        private fun callGroupApi() {
            try {

            groupBinding.groupProgress.visibility = View.VISIBLE
            apiInterface.GropList()?.enqueue(object : Callback<GroupListData?> {
                override fun onResponse(
                    call: Call<GroupListData?>,
                    response: Response<GroupListData?>
                ) {

                    val code = response.code()
                    if (code == 200) {
                        val resource: GroupListData? = response.body()
                        val Success: Boolean = resource?.status!!
                        val Message: String = resource.message!!

                        if (Success) {
                            groupBinding.groupProgress.visibility = View.GONE
                            groupList = resource.data!! // Populate the groupList here
                            initrecycler(resource.data!!)


                        } else {
                            groupBinding.groupProgress.visibility = View.GONE
                        }
                    } else if (response.code() == 403) {
                        groupBinding.groupProgress.visibility = View.GONE
                        val message = response.message()
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
                        groupBinding.groupProgress.visibility = View.GONE
                        val message = response.message()
    //                    Toast.makeText(requireContext(), "" + message, Toast.LENGTH_SHORT)
    //                        .show()

                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<GroupListData?>, t: Throwable) {
                    Toast.makeText(requireContext(), "" + t.message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                    Log.d("GROOOOOOOP", t.message.toString() + "")
                    groupBinding.groupProgress.visibility = View.GONE
                }
            })
            }catch (e:Exception){
                Log.d("catch", "callGroupApiAthlete: ${e.message.toString()}")
            }
        }

        private fun initrecycler(data: ArrayList<GroupListData.groupData>) {
            try {
            if (!isAdded) {
                Log.e("GroupFragment", "Fragment is not attached to an activity.")
                return
            }

            groupBinding.groupRly.layoutManager = LinearLayoutManager(requireActivity())
            groupadapter = GroupAdapter(data, requireContext(), this)
            groupBinding.groupRly.adapter = groupadapter
            }catch (e:Exception){
                Log.d("catch", "callGroupApiAthlete: ${e.message.toString()}")
            }
        }

        override fun onItemClicked(view: View, position: Int, type: Long, string: String) {
            val groupId = groupList[position].id

            startActivity(
                Intent(requireContext(), GroupDetailActivity::class.java).apply {
                    putExtra("id", groupList[position].id)
                    putExtra("group_id", groupId)
                    putExtra("position", position)
                }

            )
        }


    }