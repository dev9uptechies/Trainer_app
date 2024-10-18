package com.example

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.trainerapp.ApiClass.SportlistData
import com.example.trainerapp.R
import com.google.gson.Gson

class LessonAdapter(
    private var data: ArrayList<GroupListData.GroupLesson>?,var context: Context,    val listener: OnItemClickListener.OnItemClickCallback) : RecyclerView.Adapter<LessonAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LessonAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.planning_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: LessonAdapter.MyViewHolder, position: Int) {
        val movie = data!![position]
        holder.group_name.text = movie.lession!!.name
        holder.tv_start_date.text = movie.lession!!.time
        holder.itemView.setOnClickListener(OnItemClickListener(position,listener,movie.id!!.toLong(),"lesson"))
    }

    override fun getItemCount(): Int {
        return data!!.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var group_name: TextView = view.findViewById<View>(R.id.tv_program_name) as TextView
        var tv_edt_time: TextView = view.findViewById<View>(R.id.tv_edt_time) as TextView
        var tv_start_date: TextView = view.findViewById<View>(R.id.tv_start_date) as TextView
        var tv_athelet_name: TextView = view.findViewById<View>(R.id.tv_athelet_name) as TextView
    }
}
