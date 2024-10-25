package com.example.trainerapp.personal_diary

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.trainerapp.R
import com.example.trainerapp.databinding.ActivityPerformanceProfileBinding
import com.example.trainerapp.databinding.ActivityViewPersonalDiaryBinding
import com.example.trainerapp.performance_profile.view_average_graph.ViewPerformanesProfileAverageActivity

class ViewPersonalDiaryActivity : AppCompatActivity() {

    lateinit var viewPersonalDiaryActivityBinding: ActivityViewPersonalDiaryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewPersonalDiaryActivityBinding= ActivityViewPersonalDiaryBinding.inflate(layoutInflater)
        setContentView(viewPersonalDiaryActivityBinding.root)
        initViews()
        ButtonClick()
    }

    private fun ButtonClick() {
        viewPersonalDiaryActivityBinding.back.setOnClickListener { finish() }

        viewPersonalDiaryActivityBinding.add.setOnClickListener { startActivity(Intent(this,AddPersonalDIaryActivity::class.java)) }
    }

    private fun initViews() {

    }
}