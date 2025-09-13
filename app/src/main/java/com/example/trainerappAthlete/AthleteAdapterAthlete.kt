package com.example.trainerappAthlete

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.GroupListData
import com.example.OnItemClickListener
import com.example.trainerapp.R
import com.example.trainerappAthlete.model.GroupListAthlete


class AthleteAdapterAthlete(
    private var data: ArrayList<GroupListData.Athlete>?,
    private val context: Context,
    private val listener: OnItemClickListener.OnItemClickCallback
) : RecyclerView.Adapter<AthleteAdapterAthlete.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.athlete_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val movie = data?.get(position) ?: return // Safely handle null data

        try {
            Log.d("name", "onBindViewHolder: " + movie.name.toString())
        }catch (e:Exception){
            Log.e("Error","ERror:-  ${e.message.toString()}")
        }

        holder.name.text = movie.name ?: ""

        holder.itemView.setOnClickListener(OnItemClickListener(position, listener, movie.id?.toLong() ?: 0L, "athlete"))
    }

    override fun getItemCount(): Int = data!!.size

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_program_name)

    }
}
