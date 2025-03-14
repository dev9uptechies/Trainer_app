package com

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.trainerapp.R
import com.example.trainerapp.databinding.ActivityCreateEventBinding
import com.example.trainerapp.databinding.ActivityNewsBinding
import com.example.trainerapp.databinding.FragmentHomeBinding

class NewsActivity : AppCompatActivity() {

    lateinit var binding: ActivityNewsBinding
    var title:String?= null
    var dec:String?= null
    var image:String?= null
    var date:String?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        ButtonClick()
    }

    private fun ButtonClick() {
        binding.back.setOnClickListener { finish() }
    }

    private fun initView() {
        title =  intent.getStringExtra("title")
        dec =  intent.getStringExtra("dec")
        image =  intent.getStringExtra("image")
        date =  intent.getStringExtra("date")


        binding.tvCredit.text = title
        binding.title.text = date?.take(10)
        binding.newsDescripiton.text = "Description: "+dec

        Glide.with(binding.newsImage.context)
            .load("https://4trainersapp.com" + image)
            .into(binding.newsImage)

    }
}