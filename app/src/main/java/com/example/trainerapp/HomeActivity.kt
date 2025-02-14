package com.example.trainerapp

import android.content.Intent
import android.graphics.PorterDuff
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.FavoriteActivity
import com.HomeFragment
import com.example.AthletesActivity
import com.example.CalenderFragment
import com.example.CrateActivity
import com.example.GroupFragment
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.RemindMe.ViewRemindMeActivity
import com.example.trainerapp.competition.CompetitionActivity
import com.example.trainerapp.databinding.ActivityHomeBinding
import com.example.trainerapp.notification.NotificationActivity
import com.example.trainerapp.personal_diary.ViewPersonalDiaryActivity
import com.example.trainerapp.privacy_policy.PrivacyPolicyActivity
import com.example.trainerapp.view_analysis.ViewAnalysisActivity
import com.example.trainerappAthlete.ProfileFragment
import com.example.trainerappAthlete.model.GroupListAthlete
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.log

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var homeBinding: ActivityHomeBinding
    lateinit var fm: FragmentManager
    lateinit var active: Fragment
    lateinit var fragment1: Fragment
    lateinit var fragment2: Fragment
    lateinit var fragment3: Fragment
    lateinit var fragment4: Fragment
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    lateinit var ft: FragmentTransaction
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    lateinit var preferenceManager: PreferencesManager
    var id: String = ""
    var group_id: String = ""

    private var name: String? = null
    private var startDate: String? = null
    private var endDate: String? = null
    private var mesocycle: String? = null
    private var workloadColor: String? = null


        private var preCompetitiveName: String? = null
        private var preCompetitiveStartDate: String? = null
        private var preCompetitiveEndDate: String? = null
        private var preCompetitiveMesocycle: String? = null
        private var preCompetitiveWorkloadColor: String? = null

        // competitive
        private var CompetitiveName: String? = null
        private var CompetitiveStartDate: String? = null
        private var CompetitiveEndDate: String? = null
        private var CompetitiveMesocycle: String? = null
        private var CompetitiveWorkloadColor: String? = null

        // Transition
        private var TransitionName: String? = null
        private var TransitionStartDate: String? = null
        private var TransitionEndDate: String? = null
        private var TransitionMesocycle: String? = null
        private var TransitionWorkloadColor: String? = null


    private val sharedPreferences by lazy {
        this.getSharedPreferences("RemindMePrefs", MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(homeBinding.root)
        initViews()
        setFragments()
        setDrawerToggle()

        val sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("id", id)
        editor.putString("group_id", group_id)
        editor.putString("name", name)
        editor.putString("start_date", startDate)
        editor.putString("end_date", endDate)
        editor.putString("mesocycle", mesocycle)
        editor.putString("workload_color", workloadColor)

        //pre competitive
        editor.putString("preCompetitiveName", preCompetitiveName)
        editor.putString("preCompetitiveStartDate", preCompetitiveStartDate)
        editor.putString("preCompetitiveEndDate", preCompetitiveEndDate)
        editor.putString("preCompetitiveMesocycle", preCompetitiveMesocycle)
        editor.putString("preCompetitiveWorkloadColor", preCompetitiveWorkloadColor)

        //competitive
        editor.putString("CompetitiveName", CompetitiveName)
        editor.putString("CompetitiveStartDate", CompetitiveStartDate)
        editor.putString("CompetitiveEndDate", CompetitiveEndDate)
        editor.putString("CompetitiveMesocycle", CompetitiveMesocycle)
        editor.putString("CompetitiveWorkloadColor", CompetitiveWorkloadColor)
        editor.apply()

        //Transition
        editor.putString("TransitionName", TransitionName)
        editor.putString("TransitionStartDate", TransitionStartDate)
        editor.putString("TransitionEndDate", TransitionEndDate)
        editor.putString("TransitionMesocycle", TransitionMesocycle)
        editor.putString("TransitionWorkloadColor", TransitionWorkloadColor)
        editor.apply()


    }

    override fun onDestroy() {
        super.onDestroy()

        val sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("id", id)
        editor.putString("group_id", group_id)
        editor.putString("name", name)
        editor.putString("start_date", startDate)
        editor.putString("end_date", endDate)
        editor.putString("mesocycle", mesocycle)
        editor.putString("workload_color", workloadColor)

        //pre competitive
        editor.putString("preCompetitiveName", preCompetitiveName)
        editor.putString("preCompetitiveStartDate", preCompetitiveStartDate)
        editor.putString("preCompetitiveEndDate", preCompetitiveEndDate)
        editor.putString("preCompetitiveMesocycle", preCompetitiveMesocycle)
        editor.putString("preCompetitiveWorkloadColor", preCompetitiveWorkloadColor)

        //competitive
        editor.putString("CompetitiveName", CompetitiveName)
        editor.putString("CompetitiveStartDate", CompetitiveStartDate)
        editor.putString("CompetitiveEndDate", CompetitiveEndDate)
        editor.putString("CompetitiveMesocycle", CompetitiveMesocycle)
        editor.putString("CompetitiveWorkloadColor", CompetitiveWorkloadColor)

        //Transition
        editor.putString("TransitionName", TransitionName)
        editor.putString("TransitionStartDate", TransitionStartDate)
        editor.putString("TransitionEndDate", TransitionEndDate)
        editor.putString("TransitionMesocycle", TransitionMesocycle)
        editor.putString("TransitionWorkloadColor", TransitionWorkloadColor)

        editor.apply()


        Log.d("desss", "onDestroy: App destroyed, ID removed")
    }

    override fun onStop() {
        super.onStop()
        Log.d("desss", "onStop: App in background")
    }

    override fun onResume() {
        super.onResume()

        checkUser()
        val sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("id", id)
        editor.putString("group_id", group_id)
        editor.putString("name", name)
        editor.putString("start_date", startDate)
        editor.putString("end_date", endDate)
        editor.putString("mesocycle", mesocycle)
        editor.putString("workload_color", workloadColor)

        //pre competitive
        editor.putString("preCompetitiveName", preCompetitiveName)
        editor.putString("preCompetitiveStartDate", preCompetitiveStartDate)
        editor.putString("preCompetitiveEndDate", preCompetitiveEndDate)
        editor.putString("preCompetitiveMesocycle", preCompetitiveMesocycle)
        editor.putString("preCompetitiveWorkloadColor", preCompetitiveWorkloadColor)

        //competitive
        editor.putString("CompetitiveName", CompetitiveName)
        editor.putString("CompetitiveStartDate", CompetitiveStartDate)
        editor.putString("CompetitiveEndDate", CompetitiveEndDate)
        editor.putString("CompetitiveMesocycle", CompetitiveMesocycle)
        editor.putString("CompetitiveWorkloadColor", CompetitiveWorkloadColor)

        //Transition
        editor.putString("TransitionName", TransitionName)
        editor.putString("TransitionStartDate", TransitionStartDate)
        editor.putString("TransitionEndDate", TransitionEndDate)
        editor.putString("TransitionMesocycle", TransitionMesocycle)
        editor.putString("TransitionWorkloadColor", TransitionWorkloadColor)
        editor.apply()

        Log.d("desss", "onResume: App resumed, ID saved")
    }


    private fun checkUser() {
        try {
            apiInterface.ProfileData()?.enqueue(object : Callback<RegisterData?> {
                override fun onResponse(
                    call: Call<RegisterData?>,
                    response: Response<RegisterData?>
                ) {
                    Log.d("TAG", response.code().toString())
                    val code = response.code()
                    if (code == 200) {
                        Log.d("Get Profile Data", "${response.body()}")
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@HomeActivity)
                    } else {
                        Toast.makeText(
                            this@HomeActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        ).show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Toast.makeText(
                        this@HomeActivity,
                        "" + t.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    call.cancel()
                }
            })
        } catch (e: Exception) {
            Log.d("Exception", "${e.message}")
        }
    }

    private fun setFragments() {
        fragment1 = HomeFragment()
        fragment2 = CalenderFragment()
        fragment3 = GroupFragment()
        fragment4 = ChatFragment()
        fm = supportFragmentManager

        fragment1.arguments
        fragment2.arguments
        fragment3.arguments
        fragment4.arguments

        val addGroupBack = intent.getStringExtra("group") ?: ""
        Log.d("addGroupBack", "Value received: $addGroupBack")
        if (addGroupBack == "addGroup") {
            active = fragment3

        } else {
            active = fragment1
            Log.d("addGroupBack", "Condition not met. Received value: $addGroupBack")
        }

        ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.main_container, active)
        ft.commit()
    }

    private fun initViews() {
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        preferenceManager = PreferencesManager(this)

        id = intent.getStringExtra("idddd") ?: ""
        group_id = intent.getStringExtra("group_idddd") ?: ""
        Log.d("ID", "Received ID: $id")
        Log.d("IDDDDDDDDDD", "Received ID: $group_id")


        val bundle = intent.extras
        name = bundle?.getString("name")
        startDate = bundle?.getString("start_date")
        endDate = bundle?.getString("end_date")
        mesocycle = bundle?.getString("mesocycle")
        workloadColor = bundle?.getString("workload_color")

        val bundlePreCompetitive= intent.extras
        preCompetitiveName = bundlePreCompetitive?.getString("PreCompetitivename")
        preCompetitiveStartDate = bundlePreCompetitive?.getString("PreCompetitivestart_date")
        preCompetitiveEndDate = bundlePreCompetitive?.getString("PreCompetitiveend_date")
        preCompetitiveMesocycle = bundlePreCompetitive?.getString("PreCompetitivemesocycle")
        preCompetitiveWorkloadColor = bundlePreCompetitive?.getString("PreCompetitiveworkload_color")

        val bundleCompetitive= intent.extras
        CompetitiveName = bundleCompetitive?.getString("Competitivename")
        CompetitiveStartDate = bundleCompetitive?.getString("Competitivestart_date")
        CompetitiveEndDate = bundleCompetitive?.getString("Competitiveend_date")
        CompetitiveMesocycle = bundleCompetitive?.getString("Competitivemesocycle")
        CompetitiveWorkloadColor = bundleCompetitive?.getString("Competitiveworkload_color")

        val bundleTransition= intent.extras
        TransitionName = bundleTransition?.getString("Transitionname")
        TransitionStartDate = bundleTransition?.getString("Transitionstart_date")
        TransitionEndDate = bundleTransition?.getString("Transitionend_date")
        TransitionMesocycle = bundleTransition?.getString("Transitionmesocycle")
        TransitionWorkloadColor = bundleTransition?.getString("Transitionworkload_color")


        Log.d("SLSLSLSLLSLSL", "initViews: $name   $TransitionStartDate")


        if (preferenceManager.GetFlage() == "Athlete") {
            homeBinding.LinerAthlete.visibility = View.VISIBLE
            homeBinding.LinerCoach.visibility = View.GONE
            initAthleteViews()
        } else {
            homeBinding.LinerAthlete.visibility = View.GONE
            homeBinding.LinerCoach.visibility = View.VISIBLE
            initCoachViews()
        }

        homeBinding.sidemenu.setOnClickListener {
            homeBinding.drawerLayout.openDrawer(GravityCompat.START)
        }

        val userType = preferenceManager.GetFlage()

        if (userType == "Athlete") {
            homeBinding.navigationView.menu.findItem(R.id.tv_library).isVisible = false
            homeBinding.navigationView.menu.findItem(R.id.tv_athletes).isVisible = false
        } else {
            homeBinding.navigationView.menu.findItem(R.id.tv_library).isVisible = true
            homeBinding.navigationView.menu.findItem(R.id.tv_athletes).isVisible = true
        }

    }

    private fun setDrawerToggle() {
        actionBarDrawerToggle = ActionBarDrawerToggle(
            this@HomeActivity,
            homeBinding.drawerLayout,
            R.string.nav_open,
            R.string.nav_close
        )

        homeBinding.drawerLayout.addDrawerListener(actionBarDrawerToggle!!)
        actionBarDrawerToggle?.syncState()
        (this as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        homeBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        homeBinding.navigationView.setNavigationItemSelectedListener(this)
    }

    private fun initCoachViews() {
        val imgHome = homeBinding.navView.findViewById<ImageView>(R.id.img_home)
        val imgCalendar = homeBinding.navView.findViewById<ImageView>(R.id.img_calander)
        val imgGroup = homeBinding.navView.findViewById<ImageView>(R.id.img_group)
        val imgChat = homeBinding.navView.findViewById<ImageView>(R.id.img_chat)
        val createImage = homeBinding.navView.findViewById<LinearLayout>(R.id.create_image)
        val homeImage = homeBinding.navView.findViewById<LinearLayout>(R.id.home_image)
        val calendarImage = homeBinding.navView.findViewById<LinearLayout>(R.id.calendar_image)
        val groupImage = homeBinding.navView.findViewById<LinearLayout>(R.id.group_image)
        val chatImage = homeBinding.navView.findViewById<LinearLayout>(R.id.chat_image)


        val addGroupBack = intent.getStringExtra("group") ?: ""
        Log.d("addGroupBack", "Value received: $addGroupBack")
        if (addGroupBack == "addGroup") {

            imgGroup.setColorFilter(
                resources.getColor(R.color.navigation),
                PorterDuff.Mode.MULTIPLY
            )
        } else {
            imgHome.setColorFilter(
                resources.getColor(R.color.navigation),
                PorterDuff.Mode.MULTIPLY
            )
            Log.d("addGroupBack", "Condition not met. Received value: $addGroupBack")

            Log.d("addGroupBack","Condition not met. Received value")
        }


        createImage.setOnClickListener {
            startActivity(Intent(this, CrateActivity::class.java))
        }

        homeImage.setOnClickListener {
            setNavigationSelection(imgHome, imgCalendar, imgGroup, imgChat)
            replaceFragment(HomeFragment())
        }

        calendarImage.setOnClickListener {
            if (id.isNullOrEmpty() || id == "0") {
                Toast.makeText(this, "Select Group First", Toast.LENGTH_SHORT).show()
                replaceFragment(HomeFragment())
                return@setOnClickListener
            }
            setNavigationSelection(imgCalendar, imgHome, imgGroup, imgChat)
            replaceFragment(CalenderFragment())
        }

        groupImage.setOnClickListener {
            setNavigationSelection(imgGroup, imgHome, imgCalendar, imgChat)
            replaceFragment(GroupFragment())
        }

        chatImage.setOnClickListener {
            setNavigationSelection(imgChat, imgHome, imgCalendar, imgGroup)
            replaceFragment(ChatFragment())
        }
    }

    private fun initAthleteViews() {
        val imgHomeAthlete = homeBinding.includedAthlete.imgHome
        val imgCalendarAthlete = homeBinding.includedAthlete.imgCalander
        val imgGroupAthlete = homeBinding.includedAthlete.imgGroup
        val imgChatAthlete = homeBinding.includedAthlete.create
        val imgProfileAthlete = homeBinding.includedAthlete.imgProfile
        val createImageAthlete = homeBinding.includedAthlete.createImage
        val homeImageAthlete = homeBinding.includedAthlete.homeImage
        val calendarImageAthlete = homeBinding.includedAthlete.calendarImage
        val groupImageAthlete = homeBinding.includedAthlete.groupImage
        val chatImageAthlete = homeBinding.includedAthlete.createImage
        val ProfileImageAthlete = homeBinding.includedAthlete.profileImage

        imgHomeAthlete.setColorFilter(
            resources.getColor(R.color.navigation),
            PorterDuff.Mode.MULTIPLY
        )

        homeImageAthlete.setOnClickListener {
            setNavigationSelection(imgHomeAthlete, imgCalendarAthlete, imgGroupAthlete, imgChatAthlete, imgProfileAthlete)
            replaceFragment(HomeFragment())
        }

        calendarImageAthlete.setOnClickListener {
            setNavigationSelection(imgCalendarAthlete, imgHomeAthlete, imgGroupAthlete, imgChatAthlete, imgProfileAthlete)
            replaceFragment(GroupFragment())
        }

        groupImageAthlete.setOnClickListener {
            if (id.isNullOrEmpty() || id == "0") {
                Toast.makeText(this, "Select Group First", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            setNavigationSelection(imgGroupAthlete, imgHomeAthlete, imgCalendarAthlete, imgChatAthlete, imgProfileAthlete)
            replaceFragment(CalenderFragment())
        }

        chatImageAthlete.setOnClickListener {
            setNavigationSelection(imgChatAthlete, imgHomeAthlete, imgCalendarAthlete, imgGroupAthlete, imgProfileAthlete)
            replaceFragment(ChatFragment())
        }
        ProfileImageAthlete.setOnClickListener {
            setNavigationSelection(imgProfileAthlete, imgChatAthlete, imgHomeAthlete, imgCalendarAthlete, imgGroupAthlete)
            replaceFragment(ProfileFragment())
        }
    }

    private fun setNavigationSelection(
        selectedImage: ImageView,
        vararg unselectedImages: ImageView
    ) {
        selectedImage.setColorFilter(
            resources.getColor(R.color.navigation),
            PorterDuff.Mode.MULTIPLY
        )
        for (image in unselectedImages) {
            image.setColorFilter(
                resources.getColor(R.color.navigation_unselect),
                PorterDuff.Mode.MULTIPLY
            )
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        active = fragment
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.main_container, active)
        ft.commit()
    }



    override fun onBackPressed() {
        super.onBackPressed()
        val a = Intent(Intent.ACTION_MAIN)
        a.addCategory(Intent.CATEGORY_HOME)
        a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(a)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        val menu = homeBinding.navigationView.menu

        if (item.itemId == R.id.tv_personal_diary) {
            val menuItem = menu.findItem(R.id.tv_personal_diary)
            menuItem.icon = ContextCompat.getDrawable(this, R.drawable.ic_diaryy)
        }

        item.itemId == R.id.tv_notification
        if (item.itemId == R.id.tv_notification) {
            homeBinding.drawerLayout.closeDrawer(GravityCompat.START)
            val intent = Intent(this, NotificationActivity::class.java)
            startActivity(intent)
            return true
        } else if (item.itemId == R.id.tv_policy) {
            homeBinding.drawerLayout.closeDrawer(GravityCompat.START)
            val intent = Intent(this, PrivacyPolicyActivity::class.java)
            startActivity(intent)
            return true
        }
        //        else if (item.itemId == R.id.tv_invited_friend) {
        //            homeBinding.drawerLayout.closeDrawer(GravityCompat.START)
        //            val intent = Intent(this, Invite_friendActivity::class.java)
        //            startActivity(intent)
        //            return true
        //        }
        else if (item.itemId == R.id.tv_library) {
            homeBinding.drawerLayout.closeDrawer(GravityCompat.START)
            val intent = Intent(this, LibraryActivity::class.java)
            startActivity(intent)
            return true
        } else if (item.itemId == R.id.tv_favorite) {
            homeBinding.drawerLayout.closeDrawer(GravityCompat.START)
            val intent = Intent(this, FavoriteActivity::class.java)
            startActivity(intent)
            return true
        } else if (item.itemId == R.id.tv_athletes) {
            homeBinding.drawerLayout.closeDrawer(GravityCompat.START)
            val intent = Intent(this, AthletesActivity::class.java)
            startActivity(intent)
            return true
        } else if (item.itemId == R.id.tv_view_analysis) {
            homeBinding.drawerLayout.closeDrawer(GravityCompat.START)
            val intent = Intent(this, ViewAnalysisActivity::class.java)
            startActivity(intent)
            return true
        } else if (item.itemId == R.id.tv_profile) {
            val intent = Intent(this, PerformanceProfileActivity::class.java)
            startActivity(intent)
            homeBinding.drawerLayout.closeDrawer(GravityCompat.START)
            return true
        } else if (item.itemId == R.id.tv_analysis) {
            homeBinding.drawerLayout.closeDrawer(GravityCompat.START)
            preferenceManager.setSelectEvent(false)
            val intent = Intent(this, CompetitionActivity::class.java)
            startActivity(intent)
            //Toast.makeText(this, "Competition Analysis", Toast.LENGTH_SHORT).show()
            return true
        } else if (item.itemId == R.id.tv_personal_diary) {
            homeBinding.drawerLayout.closeDrawer(GravityCompat.START)
            val intent = Intent(this, ViewPersonalDiaryActivity::class.java)
            startActivity(intent)
            //          Toast.makeText(this, "Personal Diary", Toast.LENGTH_SHORT).show()
            return true
        } else if (item.itemId == R.id.tv_setting) {
            homeBinding.drawerLayout.closeDrawer(GravityCompat.START)
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
            return true
        } else if (item.itemId == R.id.tv_remind) {
            homeBinding.drawerLayout.closeDrawer(GravityCompat.START)
            val intent = Intent(this, ViewRemindMeActivity::class.java)
            startActivity(intent)
            return true
        } else if (item.itemId == R.id.logout) {
            homeBinding.drawerLayout.closeDrawer(GravityCompat.START)

            homeBinding.homeProgress.visibility = View.VISIBLE
            apiInterface.LogOut()?.enqueue(object : Callback<RegisterData?> {
                override fun onResponse(
                    call: Call<RegisterData?>,
                    response: Response<RegisterData?>
                ) {
                    homeBinding.homeProgress.visibility = View.GONE
                    Log.d("TAG", response.code().toString() + "")
                    val resource: RegisterData? = response.body()
                    //                    val Success: Boolean = resource?.status!!

                    if (response.code() == 200) {
                        val Message: String = resource!!.message!!
                        Toast.makeText(this@HomeActivity, "" + Message, Toast.LENGTH_SHORT).show()
                        preferenceManager.setUserLogIn(false)
                        with(sharedPreferences.edit()) {
                            clear()
                            apply()
                            Log.d("FVFVVF", "onResponse: clear in activity")
                        }

                        val intent = Intent(this@HomeActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else if (response.code() == 403) {
                        Utils.setUnAuthDialog(this@HomeActivity)
                        //                        Toast.makeText(
                        //                            this,
                        //                            "" + response.message(),
                        //                            Toast.LENGTH_SHORT
                        //                        ).show()
                        //                        preferenceManager.setUserLogIn(false)
                        //                        val intent = Intent(this, MainActivity::class.java)
                        //                        startActivity(intent)
                        //                        requireActivity().finish()
                    } else {
                        Toast.makeText(
                            this@HomeActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        ).show()
                        call.cancel()
                        //                        preferenceManager.setUserLogIn(false)
                        //                        val intent = Intent(this, MainActivity::class.java)
                        //                        startActivity(intent)
                        //                        requireActivity().finish()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Toast.makeText(this@HomeActivity, "" + t.message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                    homeBinding.homeProgress.visibility = View.GONE
                }
            })


            return true
        } else {
            return false
        }
    }

}
