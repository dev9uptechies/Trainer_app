package com.example

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trainerapp.R


class Library_LessonAdapter(
    private var splist: ArrayList<LessonData.lessionData>?,
    var context: Context,
    val listener: OnItemClickListener.OnItemClickCallback
) :
    RecyclerView.Adapter<Library_LessonAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Library_LessonAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.library_lessonlist, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return splist!!.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var img_edit: ImageView = view.findViewById<View>(R.id.img_edit) as ImageView
        var img_delete: ImageView = view.findViewById<View>(R.id.img_delete) as ImageView
        var image: ImageView = view.findViewById<View>(R.id.image) as ImageView
        var tvgoal: TextView = view.findViewById<View>(R.id.tv_goal) as TextView
        var tvFname: TextView = view.findViewById<View>(R.id.tv_lesson_name) as TextView
        var time: TextView = view.findViewById<View>(R.id.tv_total_time) as TextView
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val movie = splist!![position]
        holder.tvFname.text = movie.name
        holder.tvgoal.text = movie.goal!!.name
        holder.time.text = movie.time
        if (movie.is_favourite!! == "1") {
            holder.image.setImageResource(R.drawable.ic_favorite_select)

        } else {
            holder.image.setImageResource(R.drawable.ic_favorite_red)
        }
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
                movie.id!!.toLong(),
                "Delete"
            )
        )


    }

}
