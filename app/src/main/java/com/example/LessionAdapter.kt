package com.example

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.model.newClass.lesson.Lesson
import com.example.trainerapp.R
import com.zerobranch.layout.SwipeLayout

class LessionAdapter(
    private var spList: ArrayList<Lesson.LessonDatabase>,
//    private var splist: ArrayList<LessonData.lessionData>?,
    var context: Context,
    val listener: OnItemClickListener.OnItemClickCallback
) :
    RecyclerView.Adapter<LessionAdapter.MyViewHolder>() {
    val goal: ArrayList<String> = arrayListOf()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LessionAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.facebookdata_format, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: LessionAdapter.MyViewHolder, position: Int) {
        goal.clear()
        val movie = spList[position]
        holder.tv_unit.visibility = View.GONE
        holder.tv_athlet.visibility = View.GONE

        holder.tvFname.text = movie.name
        holder.tvTime.text = movie.date
        holder.tvTime1.text = "Time: " + movie.time
        for (i in movie.lesson_goal!!) {
            goal.add(i.goal!!.name!!)
        }

        holder.tvgoal.text = "Goal: " + goal.joinToString(", ")

        if (movie.is_favourite!! == 1) {
            holder.image.setImageResource(R.drawable.ic_favorite_select)
        } else {
            holder.image.setImageResource(R.drawable.ic_favorite_red)
        }

        holder.img_edit.setOnClickListener {
            holder.swipe.close()
            listener.onItemClicked(
                it, position, movie.id!!.toLong(),
                "Edit"
            )
        }
        holder.img_delete.setOnClickListener {
            holder.swipe.close()
            listener.onItemClicked(
                it, position, movie.id!!.toLong(),
                "Delete"
            )
        }

        holder.click.setOnClickListener(
            OnItemClickListener(
                position,
                listener,
                movie.id!!.toLong(),
                "View"
            )

        )

        holder.image.setOnClickListener(
            if (movie.is_favourite == 1) {
                OnItemClickListener(position, listener, movie.id.toLong(), "unfav")
            } else {
                OnItemClickListener(position, listener, movie.id.toLong(), "fav")
            }
        )




//        val movie = splist!![position]
//        holder.tv_unit.visibility = View.GONE
//        holder.tv_athlet.visibility = View.GONE
//        holder.tvFname.text = movie.name
//        holder.tvTime.text = movie.date
//        holder.tvTime1.text = "Time: " + movie.time!!
////        for (i in movie.lesson_goal!!) {
////            goal.add(i.goal!!.name!!)
////        }
////        holder.tvgoal.text = "Goal: " + goal.joinToString(", ")
////        holder.tvgoal.text = "Goal: " + movie.lesson_goal
////        holder.tv_athlet.text =  "Interested Athletes: "+movie.section!!.name
//        if (movie.is_favourite!! == "1") {
//            holder.image.setImageResource(R.drawable.ic_favorite_select)
//
//        } else {
//            holder.image.setImageResource(R.drawable.ic_favorite_red)
//        }
//        holder.img_edit.setOnClickListener(
//            OnItemClickListener(
//                position,
//                listener,
//                movie.id!!.toLong(),
//                "Edit"
//            )
//        )
//        holder.img_delete.setOnClickListener(
//            OnItemClickListener(
//                position,
//                listener,
//                movie.id!!.toLong(),
//                "Delete"
//            )
//        )
//
//        holder.click.setOnClickListener(
//            OnItemClickListener(
//                position,
//                listener,
//                movie.id!!.toLong(),
//                "View"
//            )
//        )
//
//        holder.image.setOnClickListener(
//            if (movie.is_favourite!! == "1") {
//                OnItemClickListener(position, listener, movie.id!!.toLong(), "unfav")
//            } else {
//                OnItemClickListener(position, listener, movie.id!!.toLong(), "fav")
//            }
//        )

    }

    override fun getItemCount(): Int {
//        return splist!!.size
        return spList.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var img_edit: ImageView = view.findViewById<View>(R.id.img_edit) as ImageView
        var img_delete: ImageView = view.findViewById<View>(R.id.img_delete) as ImageView
        var image: ImageView = view.findViewById<View>(R.id.image) as ImageView
        var tv_unit: TextView = view.findViewById<View>(R.id.tv_unit) as TextView
        var tvgoal: TextView = view.findViewById<View>(R.id.tvgoal) as TextView
        var tv_athlet: TextView = view.findViewById<View>(R.id.tv_athlet) as TextView
        var tvFname: TextView = view.findViewById<View>(R.id.tvFname) as TextView
        var click: LinearLayout = view.findViewById<View>(R.id.click) as LinearLayout
        var tvTime: TextView = view.findViewById(R.id.tv_time)
        var tvTime1: TextView = view.findViewById(R.id.tv_time1)
        var swipe: SwipeLayout = view.findViewById(R.id.swipe_layout)
    }
}
