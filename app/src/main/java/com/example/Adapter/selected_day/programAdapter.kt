package com.example.Adapter.selected_day

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.SelectDayActivity
import com.example.OnItemClickListener
import com.example.model.SelectedDaysModel
import com.example.trainerapp.R

class programAdapter(private var user: ArrayList<SelectedDaysModel.Data>?, var context: Context, val listener: OnItemClickListener.OnItemClickCallback)
    : RecyclerView.Adapter<programAdapter.MyViewHolder>()  {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.favorite_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = user?.get(position)
    }

    override fun getItemCount(): Int {
        return user!!.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvFname: TextView = view.findViewById(R.id.tvFname)
        var image: ImageView = view.findViewById(R.id.image)
        var tvgoal: TextView = view.findViewById(R.id.tvgoal)
        var total_time: TextView = view.findViewById(R.id.total_time)
        var tvAthlete: TextView = view.findViewById(R.id.tv_athlet)
    }
}
