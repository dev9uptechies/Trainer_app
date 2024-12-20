package com.example.Adapter.selected_day

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.OnItemClickListener
import com.example.model.SelectedDaysModel
import com.example.trainerapp.R

class eventAdapter(
    private var events: List<SelectedDaysModel.Event>,
    var context: Context,
    val listener: OnItemClickListener.OnItemClickCallback
) : RecyclerView.Adapter<eventAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.favorite_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val event = events[position]

        holder.tvinAthlete.visibility = View.VISIBLE
        holder.date.visibility = View.VISIBLE

        if (event.event_athletes.isNotEmpty()) {
            val athlete = event.event_athletes.firstOrNull()?.athlete

            holder.tvFname.text = event.title
            holder.tvgoal.text = event.type
            holder.date.text = event.date.take(10)
            holder.tvinAthlete.text =  "Interested Athletes:" +  (athlete?.athletes ?: "").toString()
            holder.tvAthlete.text = (athlete?.name ?: "").toString()
        } else {
            holder.tvFname.text = event.title
            holder.tvgoal.text = event.type
            holder.tvAthlete.text = ""
        }



        if (event.is_favourite!! == 1) {
            holder.image.setImageResource(R.drawable.ic_favorite_select)
        } else {
            holder.image.setImageResource(R.drawable.ic_favorite_red)
        }

        holder.image.setOnClickListener(
            if (event.is_favourite == 1) {
                OnItemClickListener(position, listener, event.id.toLong(), "unfavevent")
            } else {
                OnItemClickListener(position, listener, event.id.toLong(), "favevent")
            }
        )
    }

    override fun getItemCount(): Int {
        return events.size
    }

    fun clearData() {
        events = emptyList()
        notifyDataSetChanged()
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvFname: TextView = view.findViewById(R.id.tvFname)
        var image: ImageView = view.findViewById(R.id.image)
        var tvgoal: TextView = view.findViewById(R.id.tvgoal)
        var total_time: TextView = view.findViewById(R.id.total_time)
        var tvAthlete: TextView = view.findViewById(R.id.tv_athlet)
        var tvinAthlete: TextView = view.findViewById(R.id.tvinAt)
        var date: TextView = view.findViewById(R.id.date)
    }
}
