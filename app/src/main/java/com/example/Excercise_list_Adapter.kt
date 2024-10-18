package com.example

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.trainerapp.ApiClass.ProgramListData
import com.example.trainerapp.R

class Excercise_list_Adapter(
    private var splist: ArrayList<ProgramListData.Program>?,
    var context: Context,
    private val itemClickListener: OnItemClickListener,
    private var selectId: Int? = null
) : RecyclerView.Adapter<Excercise_list_Adapter.MyViewHolder>() {
    var time: Int? = null

    interface OnItemClickListener {
        fun onItemClick(id: Int, name: String, position: Int)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Excercise_list_Adapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.exercise_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: Excercise_list_Adapter.MyViewHolder, position: Int) {
        val movie = splist!![position].exercise
        holder.tvFname.text = movie!!.name
        holder.tv_goal.text = movie.goal!!.name

        if (movie.id == selectId) {
            holder.click.setBackgroundResource(R.drawable.card_select_1)
        } else {
            holder.click.setBackgroundResource(R.drawable.card_unselect_1)
        }
        if (movie.is_favourite!!.toString() == "1") {
            holder.image.setImageResource(R.drawable.ic_favorite_select)
        } else {
            holder.image.setImageResource(R.drawable.ic_favorite_red)
        }
        holder.click.setOnClickListener {
            selectId = movie.id
            notifyDataSetChanged()
            itemClickListener.onItemClick(movie.id!!, movie.name!!, position)
        }

    }

    override fun getItemCount(): Int {
        return splist!!.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var image: ImageView = view.findViewById<View>(R.id.image) as ImageView
        var tv_goal: TextView = view.findViewById<View>(R.id.tv_goal) as TextView
        var tv_total_time: TextView = view.findViewById<View>(R.id.tv_total_time) as TextView
        var tvFname: TextView = view.findViewById<View>(R.id.tv_program_name) as TextView
        var click: ConstraintLayout = view.findViewById<View>(R.id.click) as ConstraintLayout
    }

    fun updateSelectedId(newSelectId: Int) {
        this.selectId = newSelectId
        notifyDataSetChanged()
    }
}
