package com.example

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.trainerapp.ApiClass.SportlistData
import com.example.trainerapp.R
import com.example.trainerapp.training_plan.ViewTrainingPlanActivity
import com.google.gson.Gson

class PlanningAdapter(
    private var data: ArrayList<GroupListData.GroupPlanning>?,var context: Context,
    val listener: OnItemClickListener.OnItemClickCallback)
    : RecyclerView.Adapter<PlanningAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlanningAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.planning_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PlanningAdapter.MyViewHolder, position: Int) {
        val movie = data!![position]

        // Check if movie.planning is null before accessing its properties
        val planning = movie.planning
        if (planning != null) {
            holder.group_name.text = planning.name ?: "No name"
            holder.tv_start_date.text = planning.start_date ?: "No start date"
            holder.tv_edt_time.text = planning.competition_date ?: "No competition date"
        } else {
            holder.group_name.text = "No planning data"
            holder.tv_start_date.text = "No start date"
            holder.tv_edt_time.text = "No competition date"
        }

        holder.itemView.setOnClickListener {
            val planningId = planning?.id?.toInt() ?: 0 // Safely handle the ID
            val intent = Intent(context, ViewTrainingPlanActivity::class.java).apply {
                putExtra("Id", planningId) // Pass the planning ID to the next activity
            }
            context.startActivity(intent) // Start the activity
        }
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
