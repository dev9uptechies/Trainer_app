package com.example.trainerapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.databinding.ActivitySplashBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashActivity : AppCompatActivity() {
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    lateinit var preferenceManager: PreferencesManager
    lateinit var splashBinding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashBinding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(splashBinding.root)
        initViews()
        checkLogin()
    }

    private fun checkLogin() {
        if (preferenceManager.UserLogIn()) {
            splashBinding.btnGetStart.visibility = View.GONE
            apiCall()
        } else {
            splashBinding.btnGetStart.visibility = View.VISIBLE
        }

        splashBinding.btnGetStart.setOnClickListener {
            Log.d("TAG", "onCreate: Btn Start Click")
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun initViews() {
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        preferenceManager = PreferencesManager(this)
    }

    private fun apiCall() {
        splashBinding.progress.visibility = View.VISIBLE
        apiInterface.ProfileData()?.enqueue(object : Callback<RegisterData?> {
            override fun onResponse(call: Call<RegisterData?>, response: Response<RegisterData?>) {
                Log.d("TAG", response.code().toString() + "")

                val code = response.code()
                if (code == 200) {
                    val resource: RegisterData? = response.body()
                    if (resource!!.data!!.user_sports!!.size == 0) {
                        val intent =
                            Intent(this@SplashActivity, SelectSportActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        val intent = Intent(this@SplashActivity, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    Log.d("Get Profile Data ", "${response.body()}")
                } else if (code == 403) {
                    Utils.setUnAuthDialog(this@SplashActivity)
                } else {
                    Toast.makeText(
                        this@SplashActivity,
                        "" + response.message(),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    val intent = Intent(this@SplashActivity, MainActivity::class.java)
                    startActivity(intent)
                    call.cancel()
                }


//                if (!response.code().equals(403)) {
//                    val resource: RegisterData? = response.body()
//                    val Success: Boolean = resource?.status!!
//                    val Message: String = resource.message!!
//                    splashBinding.progress.visibility = View.GONE
//                    if (Message != "Unauthorized.") {
//                        if (!Success) {
//                            val intent = Intent(this@SplashActivity, MainActivity::class.java)
//                            startActivity(intent)
//                        } else {
//                            if (resource.data!!.user_sports!!.size == 0) {
//                                val intent =
//                                    Intent(this@SplashActivity, SelectSportActivity::class.java)
//                                startActivity(intent)
//                                finish()
//                            } else {
//                                val intent = Intent(this@SplashActivity, HomeActivity::class.java)
//                                startActivity(intent)
//                                finish()
//                            }
//                        }
//                    }
//                } else {
//                    Utils.setUnAuthDialog(this@SplashActivity)
////                    preferenceManager.setUserLogIn(false)
////                    val intent = Intent(this@SplashActivity, MainActivity::class.java)
////                    startActivity(intent)
//                }

            }

            override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(intent)
                Toast.makeText(this@SplashActivity, "" + t.message, Toast.LENGTH_SHORT)
                    .show()
                call.cancel()
                splashBinding.progress.visibility = View.GONE
            }
        })
    }
}