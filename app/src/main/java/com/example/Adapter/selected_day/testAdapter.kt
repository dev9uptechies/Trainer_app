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

class testAdapter(
    private var user: List<SelectedDaysModel.Test>,
    var context: Context,
    val listener: OnItemClickListener.OnItemClickCallback
) : RecyclerView.Adapter<testAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.favorite_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = user?.get(position)

        holder.total_time.text = "Interested Athletes:"
        holder.date.visibility = View.VISIBLE
        holder.unit.visibility = View.VISIBLE

        holder.tvFname.text = data!!.title
            holder.tvgoal.text = data!!.goal
        holder.date.text = data.date.take(10)
        holder.unit.text = "Unit: "+data.unit
            holder.tvAthlete.text = data!!.test_athletes[0].athlete.name

        if (data.is_favourite!! == 1) {
            holder.image.setImageResource(R.drawable.ic_favorite_select)
        } else {
            holder.image.setImageResource(R.drawable.ic_favorite_red)
        }

        holder.image.setOnClickListener(
            if (data.is_favourite == 1) {
                OnItemClickListener(position, listener, data.id.toLong(), "unfavtest")
            } else {
                OnItemClickListener(position, listener, data.id.toLong(), "favtest")
            }
        )
    }

    override fun getItemCount(): Int {
        return user.size ?: 0
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvFname: TextView = view.findViewById(R.id.tvFname)
        var image: ImageView = view.findViewById(R.id.image)
        var tvgoal: TextView = view.findViewById(R.id.tvgoal)
        var total_time: TextView = view.findViewById(R.id.total_time)
        var tvAthlete: TextView = view.findViewById(R.id.tv_athlet)
        var date: TextView = view.findViewById(R.id.date)
        var unit: TextView = view.findViewById(R.id.unit)
    }
}
