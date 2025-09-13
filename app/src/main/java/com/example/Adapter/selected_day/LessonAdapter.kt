package com.example.Adapter.selected_day

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.OnItemClickListener
import com.example.ViewLessonActivity
import com.example.model.SelectedDaysModel
import com.example.trainerapp.R

class LessonAdapter(
    private var lessons: List<SelectedDaysModel.Lesson>,
    private var context: Context,
    private val listener: OnItemClickListener.OnItemClickCallback
) : RecyclerView.Adapter<LessonAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.favorite_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val lesson = lessons[position]

        holder.tvDate.visibility = View.VISIBLE

        val program = lesson.lesson_programs.firstOrNull()?.program
        if (program != null) {
            holder.tvFname.text = lesson.name
            holder.tvgoal.text = program.goal.name
            holder.tvDate.text = lesson.date.take(10)
            holder.tvAthlete.text = lesson.time.toString()
        } else {
            holder.tvFname.text = ""
            holder.tvgoal.text = ""
            holder.tvAthlete.text = ""
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ViewLessonActivity::class.java)
            intent.putExtra("LessonLibraryId", lesson.id.toInt())
            intent.putExtra("GroupAttends", "GroupAttends")
            context.startActivity(intent)
        }


        if (lesson != null) {
            if (lesson.is_favourite!! == 1) {
                holder.image.setImageResource(R.drawable.ic_favorite_select)
            } else {
                holder.image.setImageResource(R.drawable.ic_favorite_red)
            }
        }

        if (lesson != null) {
            holder.image.setOnClickListener(
                if (lesson.is_favourite == 1) {
                    OnItemClickListener(position, listener, lesson.id.toLong(), "unfav")
                } else {
                    OnItemClickListener(position, listener, lesson.id.toLong(), "fav")
                }
            )
        }

    }

    override fun getItemCount(): Int {
        return lessons.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvFname: TextView = view.findViewById(R.id.tvFname)
        var image: ImageView = view.findViewById(R.id.image)
        var tvgoal: TextView = view.findViewById(R.id.tvgoal)
        var total_time: TextView = view.findViewById(R.id.total_time)
        var tvAthlete: TextView = view.findViewById(R.id.tv_athlet)
        var tvDate: TextView = view.findViewById(R.id.date)
    }
}
