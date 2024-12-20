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
import com.example.OnItemClickListener
import com.example.trainerapp.R
import com.example.trainerapp.Sport_list
import com.example.trainerapp.Work_Out


class WorkOutAdapter(
    private var splist: ArrayList<Work_Out>,
    private val context: Context,
    private val listener: OnItemClickListener.OnItemClickCallback
) : RecyclerView.Adapter<WorkOutAdapter.MyViewHolder>() {

    private var selectedPosition: Int = 2

    init {
        // Reverse the list
        splist.reverse()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.workout_recycler_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val movie = splist[holder.adapterPosition]
        holder.tv_instruction.text = movie.Sport_title

        if (holder.adapterPosition == selectedPosition) {
            holder.card.setCardBackgroundColor(Color.parseColor("#FF0000")) // Highlight color
        } else {
            holder.card.setCardBackgroundColor(Color.parseColor("#1AFFFFFF")) // Default color
        }

        holder.card.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = holder.adapterPosition

            if (previousPosition != RecyclerView.NO_POSITION) {
                notifyItemChanged(previousPosition)
            }
            notifyItemChanged(selectedPosition)

            listener.onItemClicked(it, position, position.toLong(), "click")
        }
    }

    override fun getItemCount(): Int {
        return splist.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tv_instruction: TextView = view.findViewById(R.id.tv_instruction)
        val card: CardView = view.findViewById(R.id.card)
    }
}
