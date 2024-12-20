package com.example.trainerapp

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.trainerapp.databinding.ActivityMainBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging


class MainActivity : AppCompatActivity() {
    lateinit var preferencesManager: PreferencesManager
    lateinit var mainBinding: ActivityMainBinding

    @SuppressLint("StringFormatInvalid")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        initViews()
        gettoken()
        checkButtonClick()
    }

    private fun checkButtonClick() {
        mainBinding.btnSignIn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            preferencesManager.SetFlage("Coach")

        }

        mainBinding.btnSignUp.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            preferencesManager.SetFlage("Athlete")
        }

    }

    private fun initViews() {
        preferencesManager = PreferencesManager(this)
    }

    private fun gettoken() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }
                val token = task.result
                Log.d(TAG, "Refreshed token: $token")
            })
    }
}