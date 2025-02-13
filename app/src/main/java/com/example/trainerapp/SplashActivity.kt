package com.example.trainerapp

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.databinding.ActivitySplashBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.Manifest


class SplashActivity : AppCompatActivity() {
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    lateinit var preferenceManager: PreferencesManager
    lateinit var splashBinding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize preferences
        preferenceManager = PreferencesManager(this)

        // Check login status
        if (preferenceManager.UserLogIn()) {
            // User is logged in, load MainActivity directly
            navigateToMainActivity()
        } else {
            // User is not logged in, continue with splash screen
            splashBinding = ActivitySplashBinding.inflate(layoutInflater)
            setContentView(splashBinding.root)
            initViews()
            checkLogin()
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startActivity(Intent(this, MainActivity::class.java))

            Log.d("FCM", "Notification permission granted")
        } else {
            Log.d("FCM", "Notification permission denied")
        }
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
            checkNotificationPermission() // Check permission when button is clicked
        }
    }

    private fun checkNotificationPermission() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            requestNotificationPermission() // If not granted, request permission
        }
    }


    private fun initViews() {
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
    }

    private fun apiCall() {
        splashBinding.progress.visibility = View.VISIBLE
        apiInterface.ProfileData()?.enqueue(object : Callback<RegisterData?> {
            override fun onResponse(call: Call<RegisterData?>, response: Response<RegisterData?>) {
                splashBinding.progress.visibility = View.GONE
                Log.d("TAG", response.code().toString() + "")

                val code = response.code()
                if (code == 200) {
                    val resource: RegisterData? = response.body()
                    if (resource?.data?.userSports.isNullOrEmpty()) {
                        val intent = Intent(this@SplashActivity, SelectSportActivity::class.java)
                        startActivity(intent)
                    } else {
                        navigateToMainActivity()
                    }
                    finish()
                } else if (code == 403) {
                    Utils.setUnAuthDialog(this@SplashActivity)
                } else {
                    Toast.makeText(
                        this@SplashActivity,
                        "" + response.message(),
                        Toast.LENGTH_SHORT
                    ).show()
                    navigateToMainActivity()
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                splashBinding.progress.visibility = View.GONE
                Toast.makeText(this@SplashActivity, "" + t.message, Toast.LENGTH_SHORT).show()
                navigateToMainActivity()
                call.cancel()
            }
        })
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this@SplashActivity, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}
