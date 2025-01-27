package com.example

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
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
        var img_copy: ImageView = view.findViewById<View>(R.id.img_copy) as ImageView
        var image: ImageView = view.findViewById<View>(R.id.image) as ImageView
        var tvgoal: TextView = view.findViewById<View>(R.id.tv_goal) as TextView
        var tvFname: TextView = view.findViewById<View>(R.id.tv_lesson_name) as TextView
        var time: TextView = view.findViewById<View>(R.id.tv_total_time) as TextView
        var tvDate: TextView = view.findViewById<View>(R.id.tv_date) as TextView
        var card: CardView = view.findViewById<View>(R.id.card) as CardView

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val movie = splist!![position]
        holder.tvFname.text = movie.name

        if (!movie.goal.isNullOrEmpty() && movie.goal!![0].goal != null) {
            holder.tvgoal.text = movie.goal!![0].goal!!.name
        } else {
            holder.tvgoal.text = "No goal available"
        }

        holder.time.text = movie.time
        holder.tvDate.text = movie.date ?: "Invalid Date"
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
                "EditLession"
            )
        )
        holder.img_delete.setOnClickListener(
            OnItemClickListener(
                position,
                listener,
                movie.id!!.toLong(),
                "DeleteLession"
            )
        )
        holder.img_copy.setOnClickListener(
            OnItemClickListener(
                position,
                listener,
                movie.id!!.toLong(),
                "DuplicateLesson"
            )
        )

        holder.card.setOnClickListener(
            OnItemClickListener(
                position,
                listener,
                movie.id!!.toLong(),
                "ViewLesson"
            )
        )

        holder.img_edit.setOnClickListener(
            OnItemClickListener(
                position,
                listener,
                movie.id!!.toLong(),
                "EditLesson"
            )
        )

        if (movie.is_favourite.toString() == "1") {
            holder.image.setImageResource(R.drawable.ic_favorite_select)

        } else {
            holder.image.setImageResource(R.drawable.ic_favorite_red)
        }


        holder.image.setOnClickListener(
            if (movie.is_favourite.toString() == "1") {
                OnItemClickListener(position, listener, movie.id!!.toLong(), "UnFavLesson")
            } else {
                OnItemClickListener(position, listener, movie.id!!.toLong(), "FavLesson")
            }
        )



    }

}
