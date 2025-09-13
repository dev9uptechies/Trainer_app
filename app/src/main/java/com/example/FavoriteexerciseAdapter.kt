package com.example

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trainerapp.R

class FavoriteexerciseAdapter(private var user: ArrayList<LessonData.lessionData>?, var context: Context, val listener: OnItemClickListener.OnItemClickCallback)
    : RecyclerView.Adapter<FavoriteexerciseAdapter.MyViewHolder>()  {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavoriteexerciseAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.favorite_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FavoriteexerciseAdapter.MyViewHolder, position: Int) {
        val movie = user!![position].exercise

        holder.image.setImageResource(R.drawable.ic_favorite_select)
        holder.image.setOnClickListener(OnItemClickListener(position, listener, movie?.id!!.toLong() , "exercise"))

        holder.goal.setText("Categories: ")
        holder.total_time.setText("Exercise Type: ")

        holder.date.text = movie!!.date?.take(10)
        holder.tvFname.text = movie!!.name
        holder.tvgoal.text = movie.lessonPrograms?.joinToString(", ") { it.program?.goal?.name.toString() }
        holder.tv_athlet.text = movie.lessonPrograms?.getOrNull(0)?.program?.programExercises?.getOrNull(0)?.exercise?.goal?.name

    }

    override fun getItemCount(): Int {
        return user!!.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var tvFname: TextView = view.findViewById<View>(R.id.tvFname) as TextView
        var image: ImageView = view.findViewById<View>(R.id.image) as ImageView
        var date: TextView = view.findViewById<View>(R.id.date) as TextView
        var unit: TextView = view.findViewById<View>(R.id.unit) as TextView
        var tvgoal: TextView = view.findViewById<View>(R.id.tvgoal) as TextView
        var goal: TextView = view.findViewById<View>(R.id.goal) as TextView
        var total_time: TextView = view.findViewById<View>(R.id.total_time) as TextView
        var tv_athlet: TextView = view.findViewById<View>(R.id.tv_athlet) as TextView
        var tvinAthlete: TextView = view.findViewById(R.id.tvinAt)
    }
}
