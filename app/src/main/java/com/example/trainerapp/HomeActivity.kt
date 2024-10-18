package com.example.trainerapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.HomeFragment
import com.example.CalenderFragment
import com.example.CrateActivity
import com.example.GroupFragment
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.databinding.ActivityHomeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(homeBinding.root)
        setFragments()
        initViews()
    }

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
                        Log.d("Get Profile Data ", "${response.body()}")
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@HomeActivity)
                    } else {
                        Toast.makeText(
                            this@HomeActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
//                    if (!response.code().equals(403)) {
//                        val resource: RegisterData? = response.body()
//                        val Success: Boolean = resource?.status!!
//                        val Message: String = resource.message!!
//                        if (Message != "Unauthorized.") {
//                            if (!Success) {
//                                val intent = Intent(this@HomeActivity, MainActivity::class.java)
//                                startActivity(intent)
//                            } else {
//                                if (resource.data!!.user_sports!!.size == 0) {
//                                    val intent =
//                                        Intent(this@HomeActivity, SelectSportActivity::class.java)
//                                    startActivity(intent)
//                                    finish()
//                                } else {
//                                    val intent = Intent(this@HomeActivity, HomeActivity::class.java)
//                                    startActivity(intent)
//                                    finish()
//                                }
//                            }
//                        }
//                    } else {
//                        Utils.setUnAuthDialog(this@HomeActivity)
////                    preferenceManager.setUserLogIn(false)
////                    val intent = Intent(this@SplashActivity, MainActivity::class.java)
////                    startActivity(intent)
//                    }

                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Toast.makeText(
                        this@HomeActivity,
                        "" + t.message,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    call.cancel()
//                    val intent = Intent(this@HomeActivity, MainActivity::class.java)
//                    startActivity(intent)
//                    Toast.makeText(this@HomeActivity, "" + t.message, Toast.LENGTH_SHORT)
//                        .show()
//                    call.cancel()
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

        active = HomeFragment()
        ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.main_container, active)
        ft.commit()
    }

    private fun initViews() {

        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        preferenceManager = PreferencesManager(this)

        val imgHome = homeBinding.navView.findViewById<ImageView>(R.id.img_home)
        val imgCalendar = homeBinding.navView.findViewById<ImageView>(R.id.img_calander)
        val imgGroup = homeBinding.navView.findViewById<ImageView>(R.id.img_group)
        val imgChat = homeBinding.navView.findViewById<ImageView>(R.id.img_chat)

        val homeImage = homeBinding.navView.findViewById<LinearLayout>(R.id.home_image)
        val calendarImage = homeBinding.navView.findViewById<LinearLayout>(R.id.calendar_image)
        val createImage = homeBinding.navView.findViewById<LinearLayout>(R.id.create_image)
        val groupImage = homeBinding.navView.findViewById<LinearLayout>(R.id.group_image)
        val chatImage = homeBinding.navView.findViewById<LinearLayout>(R.id.chat_image)

        imgHome.setColorFilter(
            resources.getColor(R.color.navigation),
            android.graphics.PorterDuff.Mode.MULTIPLY
        )

        createImage.setOnClickListener {
            startActivity(Intent(this, CrateActivity::class.java))
        }

        homeImage.setOnClickListener {
            imgHome.setColorFilter(
                resources.getColor(R.color.navigation),
                android.graphics.PorterDuff.Mode.MULTIPLY
            )
            imgCalendar.setColorFilter(
                resources.getColor(R.color.navigation_unselect),
                android.graphics.PorterDuff.Mode.MULTIPLY
            )
            imgGroup.setColorFilter(
                resources.getColor(R.color.navigation_unselect),
                android.graphics.PorterDuff.Mode.MULTIPLY
            )
            imgChat.setColorFilter(
                resources.getColor(R.color.navigation_unselect),
                android.graphics.PorterDuff.Mode.MULTIPLY
            )

            active = HomeFragment()
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.main_container, active)
            ft.commit()
        }

        calendarImage.setOnClickListener {
            imgHome.setColorFilter(
                resources.getColor(R.color.navigation_unselect),
                android.graphics.PorterDuff.Mode.MULTIPLY
            )
            imgCalendar.setColorFilter(
                resources.getColor(R.color.navigation),
                android.graphics.PorterDuff.Mode.MULTIPLY
            )
            imgGroup.setColorFilter(
                resources.getColor(R.color.navigation_unselect),
                android.graphics.PorterDuff.Mode.MULTIPLY
            )
            imgChat.setColorFilter(
                resources.getColor(R.color.navigation_unselect),
                android.graphics.PorterDuff.Mode.MULTIPLY
            )
            active = CalenderFragment()
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.main_container, active)
            ft.commit()
        }

        groupImage.setOnClickListener {
            imgHome.setColorFilter(
                resources.getColor(R.color.navigation_unselect),
                android.graphics.PorterDuff.Mode.MULTIPLY
            )
            imgCalendar.setColorFilter(
                resources.getColor(R.color.navigation_unselect),
                android.graphics.PorterDuff.Mode.MULTIPLY
            )
            imgGroup.setColorFilter(
                resources.getColor(R.color.navigation),
                android.graphics.PorterDuff.Mode.MULTIPLY
            )
            imgChat.setColorFilter(
                resources.getColor(R.color.navigation_unselect),
                android.graphics.PorterDuff.Mode.MULTIPLY
            )
            active = GroupFragment()
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.main_container, active)
            ft.commit()
        }

        chatImage.setOnClickListener {
            imgHome.setColorFilter(
                resources.getColor(R.color.navigation_unselect),
                android.graphics.PorterDuff.Mode.MULTIPLY
            )
            imgCalendar.setColorFilter(
                resources.getColor(R.color.navigation_unselect),
                android.graphics.PorterDuff.Mode.MULTIPLY
            )
            imgGroup.setColorFilter(
                resources.getColor(R.color.navigation_unselect),
                android.graphics.PorterDuff.Mode.MULTIPLY
            )
            imgChat.setColorFilter(
                resources.getColor(R.color.navigation),
                android.graphics.PorterDuff.Mode.MULTIPLY
            )
            active = ChatFragment()
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.main_container, active)
            ft.commit()
        }

    }


    override fun onBackPressed() {
        super.onBackPressed()
        val a = Intent(Intent.ACTION_MAIN)
        a.addCategory(Intent.CATEGORY_HOME)
        a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(a)
    }
}