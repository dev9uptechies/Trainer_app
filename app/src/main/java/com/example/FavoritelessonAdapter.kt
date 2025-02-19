package com.example

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trainerapp.R

class FavoritelessonAdapter(private var user: ArrayList<LessonData.lessionData>?, var context: Context, val listener: OnItemClickListener.OnItemClickCallback)
    : RecyclerView.Adapter<FavoritelessonAdapter.MyViewHolder>()  {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavoritelessonAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.favorite_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FavoritelessonAdapter.MyViewHolder, position: Int) {
        val movie = user!![position].lesson

        holder.date.visibility = View.VISIBLE
        holder.tvFname.text = movie!!.name
        holder.date.text = movie!!.date
        holder.tvgoal.text = movie!!.goal?.name
        holder.tv_athlet.text = movie!!.time
        holder.image.setImageResource(R.drawable.ic_favorite_select)
        holder.image.setOnClickListener(OnItemClickListener(position, listener, movie.id!!.toLong() , "lesson"))

        holder.itemView.setOnClickListener {
            val lessonId = movie?.id?.toInt() ?: 0


            Log.d("AdapterId", "onBindViewHolder: " + lessonId)
            val intent = Intent(context, ViewLessonActivity::class.java).apply {
                putExtra("lessonId", lessonId)
                putExtra("position", position)
            }
            context.startActivity(intent)
        }
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
        var total_time: TextView = view.findViewById<View>(R.id.total_time) as TextView
        var tv_athlet: TextView = view.findViewById<View>(R.id.tv_athlet) as TextView
    }
}
