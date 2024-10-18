package com.example.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.model.EventAthlete
import com.example.model.EventsModel
import com.example.model.LessonModel
import com.example.model.ProgramsModel
import com.example.trainerapp.R
import com.example.trainerapp.databinding.CartitemLayoutBinding

class EventAdapter(val context: Context, val list: ArrayList<EventsModel>) : RecyclerView.Adapter<EventAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: CartitemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            CartitemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder,  position: Int) {
        holder.binding.tvEventName.text = list[position].title
        holder.binding.tvEventType.text = list[position].type.toString()
        holder.binding.tvInterestedAtheletes.text = list[position].eventAthletes?.get(position)?.athlete?.name
        if (list[position].isFavourite?.equals(0) == true) {
            holder.binding.image.setImageResource(R.drawable.ic_favorite_select)
        } else {

            holder.binding.image.setImageResource(R.drawable.ic_favorite_red)
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
}

