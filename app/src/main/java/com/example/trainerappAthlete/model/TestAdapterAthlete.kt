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


class TestAdapterAthlete(
    private var data: ArrayList<GroupListAthlete.GroupTest>?,
    private val context: Context,
    private val listener: OnItemClickListener.OnItemClickCallback
) : RecyclerView.Adapter<TestAdapterAthlete.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.lession_list_item, parent, false)
        return MyViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val movie = data?.get(position) ?: return // Safely handle null data
        val movie2 = data?.get(position)!!.test ?: return // Safely handle null data


        holder.unit.visibility = View.VISIBLE
        holder.unitedt.visibility = View.VISIBLE

        try {
            holder.name.text = movie.test!!.title ?: "-"
            holder.goal.text = movie.test?.goal.toString() ?: ""
            holder.date.text = movie.test?.date?.take(10) ?: ""
            holder.unitedt.text = movie.test!!.unit ?: ""

        }catch (e:Exception){
            Log.e("Error","ERror:-  ${e.message.toString()}")
        }

        holder.itemView.setOnClickListener(OnItemClickListener(position, listener, movie.id?.toLong() ?: 0L, "test"))


        if (movie2.is_favourite!! == 1) {
            holder.image.setImageResource(R.drawable.ic_favorite_select)
        } else {
            holder.image.setImageResource(R.drawable.ic_favorite_red)
        }

        holder.image.setOnClickListener(
            if (movie2.is_favourite == 1) {
                OnItemClickListener(position, listener, movie2.id!!.toLong(), "unfavtest")
            } else {
                OnItemClickListener(position, listener, movie2.id!!.toLong(), "favtest")
            }
        )
    }

    override fun getItemCount(): Int = data!!.size

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_program_name)
        val goal: TextView = view.findViewById(R.id.tv_edt_goal)
        val totaltime: TextView = view.findViewById(R.id.tv_edit_total_time)
        val date: TextView = view.findViewById(R.id.edt_date)
        val unit: TextView = view.findViewById(R.id.Unit)
        val unitedt: TextView = view.findViewById(R.id.tv_edit_Unit)
        val image: ImageView = view.findViewById(R.id.image)
    }
}
