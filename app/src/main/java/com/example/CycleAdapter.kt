package com.example

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.model.newClass.excercise.Exercise
import com.example.trainerapp.R
import com.example.trainerapp.View_Exercise_Activity

class CycleAdapter(
    private var splist: MutableList<Exercise.Cycle>?,
    var context: Context,
    val listener: OnItemClickListener.OnItemClickCallback
) :
    RecyclerView.Adapter<CycleAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CycleAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.cycle_item_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CycleAdapter.MyViewHolder, position: Int) {

        val movie = splist!![position]
        holder.tv_cycle_count.text = "Cycle " + (position + 1)
        holder.tv_round.text = movie.cycle_set
        holder.tv_time.text = movie.cycle_time
        holder.tv_pause_cycle.text = movie.cycle_pause_cycle

        if (movie.cycle_weight == "null" || movie.cycle_weight == "" || movie.cycle_weight == null) {
            holder.tv_weight.text = "Weight"
        } else {
            holder.tv_weight.text = movie.cycle_weight
        }

        if (movie.cycle_distance == "null" || movie.cycle_distance == "" || movie.cycle_distance == null) {
            holder.tv_distance.text = "Distance"
        } else {
            holder.tv_distance.text = movie.cycle_distance
        }

        holder.tv_distance.text = movie.cycle_distance
        holder.tv_weight.text = movie.cycle_weight
        holder.tv_pause_time.text = movie.cycle_pause
        holder.tv_reps.text = movie.cycle_reps



    }

    override fun getItemCount(): Int {
        return splist!!.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tv_cycle_count: TextView = view.findViewById<View>(R.id.tv_cycle_count) as TextView
        var tv_round: TextView = view.findViewById<View>(R.id.tv_round) as TextView
        var tv_time: TextView = view.findViewById<View>(R.id.tv_time) as TextView
        var tv_pause_cycle: TextView = view.findViewById<View>(R.id.tv_pause_cycle) as TextView
        var tv_distance: TextView = view.findViewById<View>(R.id.tv_distance) as TextView
        var tv_weight: TextView = view.findViewById<View>(R.id.tv_weight) as TextView
        var tv_pause_time: TextView = view.findViewById<View>(R.id.tv_pause_time) as TextView
        var tv_reps: TextView = view.findViewById<View>(R.id.tv_reps) as TextView
    }
}
