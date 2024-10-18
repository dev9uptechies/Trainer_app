package com.example.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.model.*
import com.example.trainerapp.R
import com.example.trainerapp.databinding.TestLayoutBinding

class TestListAdapter(val context: Context, val list: ArrayList<TestListModel>) : RecyclerView.Adapter<TestListAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: TestLayoutBinding):
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            TestLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder,  position: Int) {
        holder.binding.tvEventName.text = list[position].title
        holder.binding.tvEventType.text = list[position].goal.toString()
        holder.binding.tvInterestedAtheletes.text = list[position].testAthletes?.get(position)?.athlete?.name
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

