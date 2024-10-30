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
    import com.example.trainerapp.PreferencesManager
    import com.example.trainerapp.SignInActivity
    import com.example.trainerapp.databinding.FragmentGroupBinding
    import retrofit2.Call
    import retrofit2.Callback
    import retrofit2.Response

    class GroupFragment : Fragment(), OnItemClickListener.OnItemClickCallback {
        //    lateinit var recycler_view: RecyclerView
        lateinit var apiInterface: APIInterface
        lateinit var apiClient: APIClient
        lateinit var preferenceManager: PreferencesManager
        lateinit var groupadapter: GroupAdapter

        //    lateinit var group_progress: ProgressBar
    //    lateinit var create_group: ImageView
        lateinit var groupBinding: FragmentGroupBinding
        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            groupBinding = FragmentGroupBinding.inflate(layoutInflater, container, false)
    //        recycler_view = view.findViewById(R.id.group_rly)
    //        group_progress = view.findViewById(R.id.group_progress)
    //        create_group = view.findViewById(R.id.create_group)
            apiClient = APIClient(requireContext())
            preferenceManager = PreferencesManager(requireContext())
            apiInterface = apiClient.client().create(APIInterface::class.java)
            groupBinding.createGroup.setOnClickListener {
                startActivity(Intent(requireContext(), CreateGropActivity::class.java))
            }
            callGroupApi()
            return groupBinding.root
        }

        private fun callGroupApi() {
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
                            initrecycler(resource.data!!)
                        } else {
                            groupBinding.groupProgress.visibility = View.GONE
                            Toast.makeText(requireContext(), "" + Message, Toast.LENGTH_SHORT).show()
                        }
                    } else if (response.code() == 403) {
                        groupBinding.groupProgress.visibility = View.GONE
                        val message = response.message()
                        Toast.makeText(
                            requireContext(),
                            "" + message,
                            Toast.LENGTH_SHORT
                        )
                            .show()
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
        }

        private fun initrecycler(data: ArrayList<GroupListData.groupData>) {
            groupBinding.groupRly.setLayoutManager(
                LinearLayoutManager(requireActivity())
            )
            groupadapter =
                GroupAdapter(data, requireContext(), this)
            groupBinding.groupRly.adapter = groupadapter
        }

        override fun onItemClicked(view: View, position: Int, type: Long, string: String) {

            startActivity(
                Intent(requireContext(), GroupDetailActivity::class.java).putExtra(
                    "position",
                    position
                )
            )

        }

    }