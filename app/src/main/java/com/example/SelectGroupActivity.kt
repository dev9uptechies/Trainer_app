package com.example

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.HomeFragment
import com.example.Adapter.selectGroupAdapter
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.HomeActivity
import com.example.trainerapp.PreferencesManager
import com.example.trainerapp.R
import com.example.trainerapp.SignInActivity
import com.example.trainerapp.databinding.ActivitySelectGroupBinding
import com.example.trainerapp.databinding.ActivityViewAetleteBinding
import com.example.trainerappAthlete.model.GroupListAthlete
import com.example.trainerappAthlete.model.selectGroupAdapterAthlete
import org.checkerframework.checker.units.qual.A
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SelectGroupActivity : AppCompatActivity(), OnItemClickListener.OnItemClickCallback {

    private lateinit var binding: ActivitySelectGroupBinding

    lateinit var apiInterface: APIInterface
    lateinit var preferenceManager: PreferencesManager
    lateinit var apiClient: APIClient
    lateinit var groupadapter: selectGroupAdapter
    lateinit var groupadapterAthlete: selectGroupAdapterAthlete
    var selectedGroupId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        BUttonClick()

        val userType = preferenceManager.GetFlage()


        if (userType == "Athlete"){
            callGroupApiAthlete()
        }else{
            callGroupApi()
        }
    }

    private fun initView() {
        apiClient = APIClient(this)
        preferenceManager = PreferencesManager(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
    }

    private fun BUttonClick() {
        binding.back.setOnClickListener { finish() }

        val userType = preferenceManager.GetFlage()

        if (userType == "Athlete"){
            binding.cardSave.setOnClickListener {
                try {
                    val selectedGroupIds = groupadapterAthlete.getSelectedGroupId()

                    if (selectedGroupIds == null || selectedGroupIds.first == null || selectedGroupIds.second == null) {
                        Toast.makeText(this, "Please select a valid group", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    val selectedGroupId = selectedGroupIds.first
                    val selectedGroup_Id = selectedGroupIds.second
                    val selectedGroupData = groupadapterAthlete.getSelectedGroupData()
                    val selectedPreCompetitiveGroupData = groupadapterAthlete.getSelectedPreCompetitiveGroupData()
                    val selectedCompetitiveGroupData = groupadapterAthlete.getSelectedCompetitiveGroupData()
                    val selectedTransitionGroupData = groupadapterAthlete.getSelectedTransitionGroupData()

                    val bundle = Bundle().apply {
                        selectedGroupData.forEach { (key, value) ->
                            putString(key, value)
                        }
                    }

                    val bundlePreCompetitive = Bundle().apply {
                        selectedPreCompetitiveGroupData.forEach { (key, value) ->
                            putString(key, value)
                        }
                    }

                    val bundleCompetitive = Bundle().apply {
                        selectedCompetitiveGroupData.forEach { (key, value) ->
                            putString(key, value)
                        }
                    }

                    val bundleTransition = Bundle().apply {
                        selectedTransitionGroupData.forEach { (key, value) ->
                            putString(key, value)
                        }
                    }

                    Log.d("SelectedGroupAthlete", "Selected Group ID: $selectedGroupId")
                    Log.d("SelectedGroupAthlete", "Selected Group ID: $selectedGroup_Id")

                    val intent = Intent(this, HomeActivity::class.java).apply {
                        putExtra("idddd", selectedGroupId.toString())
                        putExtra("group_idddd", selectedGroup_Id.toString())
                        putExtras(bundle)
                        putExtras(bundlePreCompetitive)
                        putExtras(bundleCompetitive)
                        putExtras(bundleTransition)
                    }

                    startActivity(intent)
                    finish()
                } catch (e: Exception) {
                    Log.e("GHHGHGH", "ButtonClick Error: ${e.message}", e)
                }
            }

        }else{
            binding.cardSave.setOnClickListener {
                try {
                    val selectedGroupId = groupadapter.getSelectedGroupId()

                    if (selectedGroupId == null) {
                        Toast.makeText(this, "Please select a group", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    Log.d("SelectedGroup", "Selected Group ID: $selectedGroupId")
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.putExtra("idddd", selectedGroupId.toString())
                    startActivity(intent)
                    finish()
                } catch (e: Exception) {
                    Log.d("GHHGHGH", "BUttonClick: ${e.message.toString()}")
                }
            }

        }



    }

    private fun callGroupApi() {
        binding.groupProgress.visibility = View.VISIBLE
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
                        binding.groupProgress.visibility = View.GONE
                        initrecycler(resource.data!!)
                    } else {
                        binding.groupProgress.visibility = View.GONE
                        Toast.makeText(this@SelectGroupActivity, "" + Message, Toast.LENGTH_SHORT)
                            .show()
                    }
                } else if (response.code() == 403) {
                    binding.groupProgress.visibility = View.GONE
                    val message = response.message()
                    Toast.makeText(
                        this@SelectGroupActivity,
                        "" + message,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    call.cancel()
                    preferenceManager.setUserLogIn(false)
                    startActivity(
                        Intent(
                            this@SelectGroupActivity,
                            SignInActivity::class.java
                        )
                    )
                    finish()
                } else {
                    binding.groupProgress.visibility = View.GONE
                    val message = response.message()
                    //                    Toast.makeText(requireContext(), "" + message, Toast.LENGTH_SHORT)
                    //                        .show()

                    call.cancel()
                }
            }

            override fun onFailure(call: Call<GroupListData?>, t: Throwable) {
                Toast.makeText(this@SelectGroupActivity, "" + t.message, Toast.LENGTH_SHORT)
                    .show()
                call.cancel()
                Log.d("GROOOOOOOP", t.message.toString() + "")
                binding.groupProgress.visibility = View.GONE
            }
        })
    }

    private fun callGroupApiAthlete() {
        binding.groupProgress.visibility = View.VISIBLE
        apiInterface.GropListAthlete()?.enqueue(object : Callback<GroupListAthlete?> {
            override fun onResponse(
                call: Call<GroupListAthlete?>,
                response: Response<GroupListAthlete?>
            ) {

                val code = response.code()
                if (code == 200) {
                    val resource: GroupListAthlete? = response.body()
                    val Success: Boolean = resource?.status!!
                    val Message: String = resource.message!!

                    if (Success) {
                        binding.groupProgress.visibility = View.GONE
                        initrecyclerAthlee(resource.data!!)

                        Log.d("DMKMMMDMKDKD", "onResponse: ${resource.data!!.get(0).group_id}")
                    } else {
                        binding.groupProgress.visibility = View.GONE
                        Toast.makeText(this@SelectGroupActivity, "" + Message, Toast.LENGTH_SHORT)
                            .show()
                    }
                } else if (response.code() == 403) {
                    binding.groupProgress.visibility = View.GONE
                    val message = response.message()
                    Toast.makeText(
                        this@SelectGroupActivity,
                        "" + message,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    call.cancel()
                    preferenceManager.setUserLogIn(false)
                    startActivity(
                        Intent(
                            this@SelectGroupActivity,
                            SignInActivity::class.java
                        )
                    )
                    finish()
                } else {
                    binding.groupProgress.visibility = View.GONE
                    val message = response.message()
                    //                    Toast.makeText(requireContext(), "" + message, Toast.LENGTH_SHORT)
                    //                        .show()

                    call.cancel()
                }
            }

            override fun onFailure(call: Call<GroupListAthlete?>, t: Throwable) {
                Toast.makeText(this@SelectGroupActivity, "" + t.message, Toast.LENGTH_SHORT)
                    .show()
                call.cancel()
                Log.d("GROOOOOOOP", t.message.toString() + "")
                binding.groupProgress.visibility = View.GONE
            }
        })
    }

    private fun initrecycler(data: ArrayList<GroupListData.groupData>) {
        binding.recylerSelectGroup.setLayoutManager(LinearLayoutManager(this))
        groupadapter = selectGroupAdapter(data, this, this)
        binding.recylerSelectGroup.adapter = groupadapter
    }

    private fun initrecyclerAthlee(data: List<GroupListAthlete.Data>) {
        binding.recylerSelectGroup.setLayoutManager(LinearLayoutManager(this))
        groupadapterAthlete = selectGroupAdapterAthlete(data, this, this)
        binding.recylerSelectGroup.adapter = groupadapterAthlete
    }

    override fun onItemClicked(view: View, position: Int, type: Long, string: String) {

        if (string == "Data"){
            Log.d("SLSSLSLLSLS", "onItemClicked: $type")
        }
    }

}