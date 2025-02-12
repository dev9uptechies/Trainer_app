package com.example

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.NewsActivity
import com.bumptech.glide.Glide
import com.example.model.AthleteDataPackage.AthleteDetails
import com.example.model.HomeFragment.NewsModel
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.R
import com.squareup.picasso.Picasso

class NewsAdapter(
    private val newsList: MutableList<NewsModel.Data>,
    var context: Context,
    val listener: OnItemClickListener.OnItemClickCallback
) :
    RecyclerView.Adapter<NewsAdapter.MyViewHolder>() {
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NewsAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list_news, parent, false)
        return MyViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: NewsAdapter.MyViewHolder, position: Int) {
        val news = newsList[position]

        holder.title.text = news.title ?: ""
        holder.description.text = news.description  ?: ""
        holder.tvimage

        Glide.with(holder.itemView.context)
            .load("https://4trainersapp.com" + news.image)
            .error(R.drawable.app_icon)
            .into(holder.tvimage)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, NewsActivity::class.java)
            intent.putExtra("title", news.title)
            intent.putExtra("dec", news.description)
            intent.putExtra("image", news.image)
            intent.putExtra("date", news.created_at)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return newsList.size
    }


    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var title: TextView = view.findViewById<View>(R.id.title) as TextView
        var description: TextView = view.findViewById<View>(R.id.news_descripiton) as TextView
        var tvimage: ImageView = view.findViewById<View>(R.id.news_image) as ImageView

    }
}
