package com.example.trainerapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.trainerapp.databinding.ActivityPerformanceProfileBinding
import com.example.trainerapp.databinding.ActivitySettingBinding

class SettingActivity : AppCompatActivity() {

    private lateinit var  binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.back.setOnClickListener { finish() }
    }
}