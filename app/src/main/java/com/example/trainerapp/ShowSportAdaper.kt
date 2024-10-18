package com.example.trainerapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ShowSportAdaper(private var user: ArrayList<Sport_list>, var context: Context)
    : RecyclerView.Adapter<ShowSportAdaper.MyViewHolder>()  {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ShowSportAdaper.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sport, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ShowSportAdaper.MyViewHolder, position: Int) {
        val movie = user[position]
        holder.et_name.text = movie.Sport_title
    }

    override fun getItemCount(): Int {
        return user.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var et_name: TextView = view.findViewById<View>(R.id.sport_text) as TextView
    }
}
