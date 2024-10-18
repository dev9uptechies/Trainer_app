package com.example

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.model.newClass.timer.Timer
import com.example.trainerapp.R


class Library_TimerAdapter(
    var splist: MutableList<Timer.TimerData>,
    var context: Context,
    val listener: OnItemClickListener.OnItemClickCallback
) :
    RecyclerView.Adapter<Library_TimerAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Library_TimerAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.timer_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return splist.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var timeCard: ConstraintLayout = view.findViewById<View>(R.id.time_card) as ConstraintLayout
        var tvFname: TextView = view.findViewById<View>(R.id.tv_test_name) as TextView
        var img_edit: ImageView = view.findViewById<View>(R.id.img_edit) as ImageView
        var img_delete: ImageView = view.findViewById<View>(R.id.img_delete) as ImageView
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val movie = splist[position]
        holder.tvFname.text = movie.name
        holder.img_edit.setOnClickListener(
            OnItemClickListener(
                position,
                listener,
                movie.id!!.toLong(),
                "Edit"
            )
        )
        holder.img_delete.setOnClickListener(
            OnItemClickListener(
                position,
                listener,
                movie.id.toLong(),
                "Delete"
            )
        )
        holder.timeCard.setOnClickListener(
            OnItemClickListener(
                position,
                listener,
                movie.id.toLong(),
                "View"
            )
        )
    }

}
