package com.example

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.model.newClass.cycle.AddCycle
import com.example.trainerapp.R

class CycleItemAdapter(
    private var splist: MutableList<AddCycle>?,
    var context: Context,
    val listener: OnItemClickListener.OnItemClickCallback
) :
    RecyclerView.Adapter<CycleItemAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CycleItemAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.cycle_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CycleItemAdapter.MyViewHolder, position: Int) {
        val movie = splist!![position]
        holder.cycle_count.text = "Cycle " + (position + 1)
        if (movie.set.isNullOrEmpty()) {
            holder.edt_name.setHint(R.string.settings)
        } else {
            holder.edt_name.text = movie.set
        }

        if (movie.time.isNullOrEmpty()) {
            holder.tv_time.setHint(R.string.time)
        } else {
            holder.tv_time.text = movie.time
        }

        if (movie.reps.isNullOrEmpty()) {
            holder.tv_reps.setHint(R.string.reps)
        } else {
            holder.tv_reps.text = movie.reps
        }

        if (movie.pause.isNullOrEmpty()) {
            holder.tv_pause_time.setHint(R.string.pauseTime)
        } else {
            holder.tv_pause_time.text = movie.pause
        }

        if (movie.weight.isNullOrEmpty()) {
            holder.tv_weight.hint = "Weight"
        } else {
            holder.tv_weight.text = movie.weight
        }

        if (movie.distance.isNullOrEmpty()) {
            holder.tv_distance.hint = "Distance"
        } else {
            holder.tv_distance.text = movie.distance
        }

        if (movie.pause_timer.isNullOrEmpty()) {
            holder.tv_pause_cycle.setHint(R.string.pauseBetweenCycles)
        } else {
            holder.tv_pause_cycle.text = movie.pause_timer
        }
        holder.delete.setOnClickListener(
            OnItemClickListener(
                position,
                listener,
                movie.id!!.toLong(),
                "delete"
            )
        )
        holder.edit.setOnClickListener(
            OnItemClickListener(
                position,
                listener,
                movie.id!!.toLong(),
                "edit"
            )
        )

    }

    override fun getItemCount(): Int {
        return splist!!.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var cycle_count: TextView = view.findViewById<View>(R.id.cycle_count) as TextView
        var edt_name: TextView = view.findViewById<View>(R.id.edt_name) as TextView
        var tv_time: TextView = view.findViewById<View>(R.id.tv_time) as TextView
        var tv_reps: TextView = view.findViewById<View>(R.id.tv_reps) as TextView
        var tv_pause_time: TextView = view.findViewById<View>(R.id.tv_pause_time) as TextView
        var tv_weight: TextView = view.findViewById<View>(R.id.tv_weight) as TextView
        var tv_distance: TextView = view.findViewById<View>(R.id.tv_distance) as TextView
        var tv_pause_cycle: TextView = view.findViewById<View>(R.id.tv_pause_cycle) as TextView
        var delete: ImageView = view.findViewById<View>(R.id.delete) as ImageView
        var edit: ImageView = view.findViewById<View>(R.id.edit) as ImageView
    }

    fun getSpList(): MutableList<AddCycle>? {
        return splist
    }
}
