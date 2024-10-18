package com.example.trainerapp.ApiClass

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.trainerapp.R
import com.example.trainerapp.Sport_list
import com.example.trainerapp.Work_Out

class WorkOutAdapter(private var splist: ArrayList<Work_Out>, var context: Context) :
    RecyclerView.Adapter<WorkOutAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.workout_recycler_item, parent, false)
        return MyViewHolder(itemView)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val movie = splist[position]
        holder.tv_instruction.text = movie.Sport_title
        holder.card.setOnClickListener {
            holder.card.setCardBackgroundColor(Color.parseColor("#FF0000"))
        }
    }

    override fun getItemCount(): Int {
        return splist.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tv_instruction: TextView = view.findViewById<View>(R.id.tv_instruction) as TextView
        var card: CardView = view.findViewById<View>(R.id.card) as CardView
    }

}
