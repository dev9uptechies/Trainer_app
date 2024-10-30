package com.example

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.model.Cycle
import com.example.trainerapp.R

class PerformanceProfileAdapter(
    private var splist: ArrayList<String>?,
    var context: Context,
    val listener: OnItemClickListener.OnItemClickCallback
) :
    RecyclerView.Adapter<PerformanceProfileAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PerformanceProfileAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.performance_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PerformanceProfileAdapter.MyViewHolder, position: Int) {
        val movie = splist!![position]
        holder.performance_name.text = movie
//        holder.view_graph.setOnClickListener(OnItemClickListener(position, listener, 1L , "program"))
    }

    override fun getItemCount(): Int {
       return splist!!.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var performance_name: TextView = view.findViewById<View>(R.id.performance_name) as TextView
//        var view_graph: TextView = view.findViewById<View>(R.id.view_graph) as TextView
    }
}
