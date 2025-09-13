package com.example.trainerappAthlete.model

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.GroupListData
import com.example.OnItemClickListener
import com.example.ViewLessonActivity
import com.example.trainerapp.R

class LessonAdapterAthlete(
    private var data: ArrayList<GroupListData.GroupLesson>?,
    private val context: Context,
    private val listener: OnItemClickListener.OnItemClickCallback,
    val screen:String
) : RecyclerView.Adapter<LessonAdapterAthlete.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.lession_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val lesson = data?.get(position) ?: return // Safely handle null data
        val lesson2 = data!![position].lession


        holder.name.text = lesson.lession?.name ?: ""
        holder.totaltime.text = lesson.lession?.time ?: ""
        holder.date.text = lesson.lession?.date ?: ""
        holder.goal.text = lesson.lession?.lesson_programs
            ?.mapNotNull { it.program?.goal?.name }
            ?.joinToString(", ") ?: ""

        if(screen == "GroupDetailsScreen") {
            holder.itemView.setOnClickListener {
                val lessonId = lesson.lession?.id?.toInt() ?: 0
                val lessonName = lesson.lession?.name ?: ""
                val totalTime = lesson.lession?.time ?: ""
                val sectionTime = lesson.lession?.section_time ?: ""

                Log.d("AdapterId", "onBindViewHolder: " + lessonId)
                val intent = Intent(context, ViewLessonActivity::class.java).apply {
                    putExtra("lessonId", lessonId)
                    putExtra("position", position)
                    putExtra("GroupAttends", "GroupAttends")
                }
                context.startActivity(intent)
            }
        }

        if (lesson2 != null) {
            if (lesson2.is_favourite!! == 1) {
                holder.image.setImageResource(R.drawable.ic_favorite_select)
            } else {
                holder.image.setImageResource(R.drawable.ic_favorite_red)
            }
        }

        if (lesson2 != null) {
            holder.image.setOnClickListener(
                if (lesson2.is_favourite == 1) {
                    OnItemClickListener(position, listener, lesson2.id!!.toLong(), "unfav")
                } else {
                    OnItemClickListener(position, listener, lesson2.id!!.toLong(), "fav")
                }
            )
        }

    }

    override fun getItemCount(): Int = data?.size ?: 0

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_program_name)
        val totaltime: TextView = view.findViewById(R.id.tv_edit_total_time)
        var image: ImageView = view.findViewById(R.id.image)
        var date: TextView = view.findViewById(R.id.edt_date)
        var goal: TextView = view.findViewById(R.id.tv_edt_goal)

    }
}
