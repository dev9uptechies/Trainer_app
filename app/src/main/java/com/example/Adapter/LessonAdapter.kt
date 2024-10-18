package com.example.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.model.LessonModel
import com.example.trainerapp.R
import com.example.trainerapp.databinding.CartitemLayoutBinding
import com.example.trainerapp.databinding.LessonItemlistBinding
import java.lang.Exception

class LessonAdapter(val context: Context, val list: ArrayList<LessonModel>) : RecyclerView.Adapter<LessonAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: LessonItemlistBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            LessonItemlistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder,  position: Int) {
        holder.binding.tvProgramName.text = list[position].name.toString()
        holder.binding.tvGoal.text = list[position].goalId.toString()+"Program"
        holder.binding.tvTotalTime.text = list[position].time.toString()
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

