package com.example.trainerapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.trainerapp.databinding.ActivityInAppPurchaseBinding

class InAppPurchaseActivity : AppCompatActivity() {
    lateinit var inAppPurchaseBinding: ActivityInAppPurchaseBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inAppPurchaseBinding = ActivityInAppPurchaseBinding.inflate(layoutInflater)
        setContentView(inAppPurchaseBinding.root)

        inAppPurchaseBinding.back.setOnClickListener {
            finish()
        }

        inAppPurchaseBinding.btnPay.setOnClickListener {
//            val intent = Intent(this, MainHomeActivity::class.java)
//            startActivity(intent)
//            finish()
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}