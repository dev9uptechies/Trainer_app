package com.example.Adapter.training_plan.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.OnItemClickListener
import com.example.model.training_plan.MicroCycle.GetMicrocycle
import com.example.trainerapp.R

class ViewMicrocycleListAdapter(
    private var splist: MutableList<GetMicrocycle.Data>?,
    var context: Context,
    val listener: OnItemClickListener.OnItemClickCallback,
    val type: String? = null,
    val mainId: Int? = null
) : RecyclerView.Adapter<ViewMicrocycleListAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_microcycle_list, parent, false)
        return MyViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = splist?.get(position) ?: return
        holder.progress.isEnabled = false
        holder.name.text = item.name
        holder.start_date.text = item.start_date
        holder.end_date.text = item.end_date
        holder.progress.progress = item.workload ?: 0
        
    }

    override fun getItemCount(): Int {
        return splist?.size ?: 0
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView = view.findViewById(R.id.training_name_one)
        var start_date: TextView = view.findViewById(R.id.start_date_one)
        var end_date: TextView = view.findViewById(R.id.end_date_one)
        var progress: SeekBar = view.findViewById(R.id.seekbar_workload)
        var card: CardView = view.findViewById(R.id.card_one)

    }

}