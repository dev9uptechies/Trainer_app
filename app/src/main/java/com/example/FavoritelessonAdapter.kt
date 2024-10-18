package com.example

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trainerapp.R

class FavoritelessonAdapter(private var user: ArrayList<LessonData.lessionData>?, var context: Context, val listener: OnItemClickListener.OnItemClickCallback)
    : RecyclerView.Adapter<FavoritelessonAdapter.MyViewHolder>()  {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavoritelessonAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.favorite_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FavoritelessonAdapter.MyViewHolder, position: Int) {
        val movie = user!![position].lesson
        holder.tvFname.text = movie!!.name
        holder.image.setImageResource(R.drawable.ic_favorite_select)
        holder.image.setOnClickListener(OnItemClickListener(position, listener, movie.id!!.toLong() , "lesson"))
    }

    override fun getItemCount(): Int {
        return user!!.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvFname: TextView = view.findViewById<View>(R.id.tvFname) as TextView
        var image: ImageView = view.findViewById<View>(R.id.image) as ImageView
    }
}
