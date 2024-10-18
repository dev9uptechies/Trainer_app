package com.example

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.trainerapp.ApiClass.ProgramListData
import com.example.trainerapp.R
import com.example.trainerapp.program_section.ViewProgramActivity
import com.zerobranch.layout.SwipeLayout

class ProgramAdapter(
    private var splist: ArrayList<ProgramListData.testData>?,
    var context: Context,
    val listener: OnItemClickListener.OnItemClickCallback
) :
    RecyclerView.Adapter<ProgramAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProgramAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.facebookdata_format, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProgramAdapter.MyViewHolder, position: Int) {
        holder.tvTime1.visibility = View.VISIBLE
        holder.tv_athlet.visibility = View.GONE
        holder.tvTime.visibility = View.GONE
        val movie = splist!![position]
        holder.tv_unit.visibility = View.GONE
        holder.tvFname.text = movie.name
        holder.tvgoal.text = "Goal: " + movie.goal!!.name
//        holder.tv_athlet.text = "Interested Athletes: " + movie.section!!.name
        holder.tvTime1.text = "Time :" + movie.time
        if (movie.is_favourite.toString() == "1") {
            holder.image.setImageResource(R.drawable.ic_favorite_select)

        } else {
            holder.image.setImageResource(R.drawable.ic_favorite_red)
        }

        holder.img_edit.setOnClickListener {
            holder.swipe.close(true)
            listener.onItemClicked(it, position, movie.id!!.toLong(), "Edit")
//            OnItemClickListener(position, listener, movie.id!!.toLong(), "Edit")
        }
        holder.img_delete.setOnClickListener {
            holder.swipe.close(true)
            listener.onItemClicked(it, position, movie.id!!.toLong(), "Delete")
        }

        holder.image.setOnClickListener(
            if (movie.is_favourite.toString() == "1") {
                OnItemClickListener(position, listener, movie.id!!.toLong(), "unfav")
            } else {
                OnItemClickListener(position, listener, movie.id!!.toLong(), "fav")
            }
        )

        holder.cardView.setOnClickListener {
            //Toast.makeText(context, "click ${movie.id}", Toast.LENGTH_SHORT).show()
            context.startActivity(Intent(context, ViewProgramActivity::class.java).apply {
                putExtra("position", position)
                putExtra("id", movie.id!!)
            })
//            listener.onItemClicked(it, position, movie.id!!.toLong(), "View")
        }

    }

    override fun getItemCount(): Int {
        return splist!!.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var cardView: CardView = view.findViewById<View>(R.id.rela_dragged) as CardView
        var img_edit: ImageView = view.findViewById<View>(R.id.img_edit) as ImageView
        var img_delete: ImageView = view.findViewById<View>(R.id.img_delete) as ImageView
        var image: ImageView = view.findViewById<View>(R.id.image) as ImageView
        var tv_unit: TextView = view.findViewById<View>(R.id.tv_unit) as TextView
        var tvgoal: TextView = view.findViewById<View>(R.id.tvgoal) as TextView
        var tv_athlet: TextView = view.findViewById<View>(R.id.tv_athlet) as TextView
        var tvFname: TextView = view.findViewById<View>(R.id.tvFname) as TextView
        var tvTime: TextView = view.findViewById(R.id.tv_time)
        val swipe = view.findViewById<SwipeLayout>(R.id.swipe_layout)
        var tvTime1 = view.findViewById<TextView>(R.id.tv_time1)
    }
}
