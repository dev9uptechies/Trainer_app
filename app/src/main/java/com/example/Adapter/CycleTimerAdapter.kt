package com.example.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.OnItemClickListener
import com.example.model.newClass.timer.Timer
import com.example.trainerapp.R

class CycleTimerAdapter(
    private var splist: MutableList<Timer.TimerX>?,
    var context: Context,
    val listener: OnItemClickListener.OnItemClickCallback
) :
    RecyclerView.Adapter<CycleTimerAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CycleTimerAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.cycle_item_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CycleTimerAdapter.MyViewHolder, position: Int) {

        val movie = splist!![position]
        holder.tv_cycle_count.text = "Cycle " + (position + 1)
        holder.tv_round.text = movie.set
        holder.tv_time.text = movie.time
        holder.tv_pause_cycle.text = movie.pause_timer
        holder.tv_distance.text = movie.distance ?: ""
        holder.tv_weight.text = movie.weight ?: ""
        holder.tv_pause_time.text = movie.pause
        holder.tv_reps.text = movie.reps
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
