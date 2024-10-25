package com

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.AthletesActivity
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.ApiClass.WorkOutAdapter
import com.example.trainerapp.HomeAdapter
import com.example.trainerapp.LibraryActivity
import com.example.trainerapp.MainActivity
import com.example.trainerapp.PerformanceProfileActivity
import com.example.trainerapp.PreferencesManager
import com.example.trainerapp.R
import com.example.trainerapp.SettingActivity
import com.example.trainerapp.Sport_list
import com.example.trainerapp.Utils
import com.example.trainerapp.Work_Out
import com.example.trainerapp.competition.CompetitionActivity
import com.example.trainerapp.databinding.FragmentHomeBinding
import com.example.trainerapp.personal_diary.ViewPersonalDiaryActivity
import com.example.trainerapp.view_analysis.ViewAnalysisActivity
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var homeFragmentBinding: FragmentHomeBinding
    private lateinit var Sportlist: java.util.ArrayList<Sport_list>
    private lateinit var WorkOutlist: java.util.ArrayList<Work_Out>
    lateinit var preferenceManager: PreferencesManager
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    var actionBarDrawerToggle: ActionBarDrawerToggle? = null
    lateinit var adapter: HomeAdapter
    lateinit var workoutadapter: WorkOutAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeFragmentBinding = FragmentHomeBinding.inflate(inflater, container, false)
        initViews()
        setDrawerToggle()
        setContent()

        return homeFragmentBinding.root
    }

    private fun setContent() {
        val workout = arrayOf("Instruction", "Information", "News")

        for (item in workout.indices) {

            WorkOutlist.add(Work_Out(workout[item]))
        }

        val language = arrayOf("LUN", "USR", "UFN", "LUN", "USR", "UFN")

        for (item in language.indices) {

            Sportlist.add(Sport_list(language[item]))
        }

        initRecyclerView(Sportlist)
        initView(WorkOutlist)
    }

    private fun setDrawerToggle() {
        actionBarDrawerToggle =
            ActionBarDrawerToggle(
                requireActivity(),
                homeFragmentBinding.drawerLayout,
                R.string.nav_open,
                R.string.nav_close
            )
        actionBarDrawerToggle!!.syncState()
        homeFragmentBinding.drawerLayout.addDrawerListener(actionBarDrawerToggle!!)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        homeFragmentBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        homeFragmentBinding.sidemenu.isClickable = true
        homeFragmentBinding.sidemenu.setOnClickListener {
            Log.d("TAG", "onCreateView: Touch Side Menu")
            homeFragmentBinding.drawerLayout.openDrawer(GravityCompat.START)
        }
        homeFragmentBinding.navigationView.setNavigationItemSelectedListener(this)
    }

    private fun initViews() {
        apiClient = APIClient(requireContext())
        preferenceManager = PreferencesManager(requireContext())
        Log.d("Login Token", preferenceManager.getToken()!!)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        Sportlist = ArrayList()
        WorkOutlist = ArrayList()
    }

    private fun initRecyclerView(user: ArrayList<Sport_list>) {
        homeFragmentBinding.viewRecycler.layoutManager = LinearLayoutManager(requireActivity())
        adapter =
            HomeAdapter(user, requireContext())
        homeFragmentBinding.viewRecycler.adapter = adapter
    }


    private fun initView(workOutlist: ArrayList<Work_Out>) {

        homeFragmentBinding.recyclerView.setLayoutManager(
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                true
            )
        )
        workoutadapter =
            WorkOutAdapter(workOutlist, requireContext())
        homeFragmentBinding.recyclerView.adapter = workoutadapter
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.tv_notification) {
            homeFragmentBinding.drawerLayout.closeDrawer(GravityCompat.START)
            Toast.makeText(requireContext(), "Notification", Toast.LENGTH_SHORT).show()
            return true
        } else if (item.itemId == R.id.tv_policy) {
            homeFragmentBinding.drawerLayout.closeDrawer(GravityCompat.START)
            Toast.makeText(requireContext(), "Privacy Policy", Toast.LENGTH_SHORT).show()
            return true
        }
//        else if (item.itemId == R.id.tv_invited_friend) {
//            homeFragmentBinding.drawerLayout.closeDrawer(GravityCompat.START)
//            val intent = Intent(requireContext(), Invite_friendActivity::class.java)
//            startActivity(intent)
//            return true
//        }
        else if (item.itemId == R.id.tv_library) {
            homeFragmentBinding.drawerLayout.closeDrawer(GravityCompat.START)
            val intent = Intent(requireContext(), LibraryActivity::class.java)
            startActivity(intent)
            return true
        } else if (item.itemId == R.id.tv_favorite) {
            homeFragmentBinding.drawerLayout.closeDrawer(GravityCompat.START)
            val intent = Intent(requireContext(), FavoriteActivity::class.java)
            startActivity(intent)
            return true
        } else if (item.itemId == R.id.tv_athletes) {
            homeFragmentBinding.drawerLayout.closeDrawer(GravityCompat.START)
            val intent = Intent(requireContext(), AthletesActivity::class.java)
            startActivity(intent)
            return true
        } else if (item.itemId == R.id.tv_view_analysis) {
            homeFragmentBinding.drawerLayout.closeDrawer(GravityCompat.START)
            val intent = Intent(requireContext(), ViewAnalysisActivity::class.java)
            startActivity(intent)
            return true
        } else if (item.itemId == R.id.tv_profile) {
            val intent = Intent(requireContext(), PerformanceProfileActivity::class.java)
            startActivity(intent)
            homeFragmentBinding.drawerLayout.closeDrawer(GravityCompat.START)
            return true
        } else if (item.itemId == R.id.tv_analysis) {
            homeFragmentBinding.drawerLayout.closeDrawer(GravityCompat.START)
            preferenceManager.setSelectEvent(false)
            val intent = Intent(requireContext(), CompetitionActivity::class.java)
            startActivity(intent)
            //Toast.makeText(requireContext(), "Competition Analysis", Toast.LENGTH_SHORT).show()
            return true
        } else if (item.itemId == R.id.tv_personal_diary) {
            homeFragmentBinding.drawerLayout.closeDrawer(GravityCompat.START)
            val intent = Intent(requireContext(), ViewPersonalDiaryActivity::class.java)
            startActivity(intent)
//            Toast.makeText(requireContext(), "Personal Diary", Toast.LENGTH_SHORT).show()
            return true
        } else if (item.itemId == R.id.tv_setting) {
            homeFragmentBinding.drawerLayout.closeDrawer(GravityCompat.START)
            val intent = Intent(requireContext(), SettingActivity::class.java)
            startActivity(intent)
            return true
        } else if (item.itemId == R.id.tv_remind) {
            homeFragmentBinding.drawerLayout.closeDrawer(GravityCompat.START)
            Toast.makeText(requireContext(), "Remind Me", Toast.LENGTH_SHORT).show()
            return true
        } else if (item.itemId == R.id.logout) {
            homeFragmentBinding.drawerLayout.closeDrawer(GravityCompat.START)

            homeFragmentBinding.homeProgress.visibility = View.VISIBLE
            apiInterface.LogOut()?.enqueue(object : Callback<RegisterData?> {
                override fun onResponse(
                    call: Call<RegisterData?>,
                    response: Response<RegisterData?>
                ) {
                    homeFragmentBinding.homeProgress.visibility = View.GONE
                    Log.d("TAG", response.code().toString() + "")
                    val resource: RegisterData? = response.body()
//                    val Success: Boolean = resource?.status!!

                    if (response.code() == 200) {
                        val Message: String = resource!!.message!!
                        Toast.makeText(requireContext(), "" + Message, Toast.LENGTH_SHORT).show()
                        preferenceManager.setUserLogIn(false)
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    } else if (response.code() == 403) {
                        Utils.setUnAuthDialog(requireContext())
//                        Toast.makeText(
//                            requireContext(),
//                            "" + response.message(),
//                            Toast.LENGTH_SHORT
//                        ).show()
//                        preferenceManager.setUserLogIn(false)
//                        val intent = Intent(requireContext(), MainActivity::class.java)
//                        startActivity(intent)
//                        requireActivity().finish()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        ).show()
                        call.cancel()
//                        preferenceManager.setUserLogIn(false)
//                        val intent = Intent(requireContext(), MainActivity::class.java)
//                        startActivity(intent)
//                        requireActivity().finish()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Toast.makeText(requireContext(), "" + t.message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                    homeFragmentBinding.homeProgress.visibility = View.GONE
                }
            })
            return true
        } else {
            return false
        }
    }
}