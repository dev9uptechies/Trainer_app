package com.example.trainerappAthlete.model

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.GroupListData
import com.example.OnItemClickListener
import com.example.trainerapp.R

class EventAdapterAthlete(
    private var data: ArrayList<GroupListAthlete.GroupEvents>?,
    var context: Context,
    val listener: OnItemClickListener.OnItemClickCallback
) : RecyclerView.Adapter<EventAdapterAthlete.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.lession_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val event = data?.get(position) ?: return
        val event2 = data?.get(position)!!.event ?: return
        holder.goal.setText("Event Type: ")

        try {
            holder.name.text = event!!.event!!.title ?: ""
            holder.goaledi.text = event!!.event!!.type ?: ""
            holder.intrestedA.text = event.event!!.type ?: ""
            holder.date.text = event.event?.date?.take(10) ?: ""
        }catch (e:Exception){
            Log.e("Error", "onBindViewHolder: Error:-  ${e.message.toString()} ")
        }

        holder.itemView.setOnClickListener {
            OnItemClickListener(position, listener, it.id.toLong() ?: 0L, "event")
        }


        if (event2.is_favourite!! == 1) {
            holder.image.setImageResource(R.drawable.ic_favorite_select)
        } else {
            holder.image.setImageResource(R.drawable.ic_favorite_red)
        }

        holder.image.setOnClickListener(
            if (event2.is_favourite == 1) {
                OnItemClickListener(position, listener, event2.id!!.toLong(), "unfavevent")
            } else {
                OnItemClickListener(position, listener, event2.id!!.toLong(), "favevent")
            }
        )

    }
    override fun getItemCount(): Int = data!!.size

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView = view.findViewById(R.id.tv_program_name)
        var goaledi: TextView = view.findViewById(R.id.tv_edt_goal)
        var goal: TextView = view.findViewById(R.id.goal)
        var intrestedA: TextView = view.findViewById(R.id.tv_edit_total_time)
        var image: ImageView = view.findViewById(R.id.image)
        var date: TextView = view.findViewById(R.id.edt_date)
        var goaltv: TextView = view.findViewById(R.id.tv_edt_goal)
    }
}

