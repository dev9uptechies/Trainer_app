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
    data: ArrayList<GroupListAthlete.GroupLesson>?,
    private val context: Context,
    private val listener: OnItemClickListener.OnItemClickCallback
) : RecyclerView.Adapter<LessonAdapterAthlete.MyViewHolder>() {

    private var filteredData: ArrayList<GroupListAthlete.GroupLesson> = ArrayList()

    init {
        // ✅ Filter out items with missing fields
        data?.let { list ->
            filteredData = list.filter { isValidItem(it) } as ArrayList<GroupListAthlete.GroupLesson>
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.lession_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val lesson = filteredData[position]
        val lessonData = lesson.lession ?: return

        holder.name.text = lessonData.name
        holder.totaltime.text = lessonData.time
        holder.date.text = lessonData.date
        holder.goal.text = lessonData.lesson_programs?.getOrNull(0)?.program?.goal?.name ?: ""

        holder.itemView.setOnClickListener {
            val lessonId = lessonData.id?.toInt() ?: 0
            val intent = Intent(context, ViewLessonActivity::class.java).apply {
                putExtra("lessonId", lessonId)
                putExtra("position", position)
            }
            context.startActivity(intent)
        }

        if (lessonData.is_favourite == 1) {
            holder.image.setImageResource(R.drawable.ic_favorite_select)
        } else {
            holder.image.setImageResource(R.drawable.ic_favorite_red)
        }

        holder.image.setOnClickListener(
            if (lessonData.is_favourite == 1) {
                OnItemClickListener(position, listener, lessonData.id!!.toLong(), "unfav")
            } else {
                OnItemClickListener(position, listener, lessonData.id!!.toLong(), "fav")
            }
        )
    }

    override fun getItemCount(): Int = filteredData.size

    private fun isValidItem(item: GroupListAthlete.GroupLesson): Boolean {
        return item.lession?.let {
            !it.name.isNullOrEmpty() &&
                    !it.time.isNullOrEmpty() &&
                    !it.date.isNullOrEmpty()
        } ?: false
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_program_name)
        val totaltime: TextView = view.findViewById(R.id.tv_edit_total_time)
        var image: ImageView = view.findViewById(R.id.image)
        var date: TextView = view.findViewById(R.id.edt_date)
        var goal: TextView = view.findViewById(R.id.tv_edt_goal)
    }
}
