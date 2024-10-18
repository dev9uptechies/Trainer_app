package com.example.trainerapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HomeAdapter(private var splist: ArrayList<Sport_list>, var context: Context) :
    RecyclerView.Adapter<HomeAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.next_lession_rly_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HomeAdapter.MyViewHolder, position: Int) {
        val movie = splist[position]
        holder.et_name.text = movie.Sport_title
        holder.time.text = "20:30"
        holder.count.text = "2"
    }

    override fun getItemCount(): Int {
        return  splist.size
    }


    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var et_name: TextView = view.findViewById<View>(R.id.name) as TextView
        var time: TextView = view.findViewById<View>(R.id.time) as TextView
        var count: TextView = view.findViewById<View>(R.id.count) as TextView
    }


}
