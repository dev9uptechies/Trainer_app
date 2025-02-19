package com.example

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trainerapp.R

class FavoriteeventAdapter(private var user: ArrayList<LessonData.lessionData>?, var context: Context, val listener: OnItemClickListener.OnItemClickCallback)
    : RecyclerView.Adapter<FavoriteeventAdapter.MyViewHolder>()  {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavoriteeventAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.favorite_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FavoriteeventAdapter.MyViewHolder, position: Int) {
        val movie = user!![position].event

        holder.date.visibility = View.VISIBLE

        holder.total_time.text = "Instrested Athlete:"
        holder.goal.text = "Event type:"

        holder.date.text = movie!!.date?.take(10) ?: ""
        holder.tvgoal.text = movie.type ?: ""
        holder.tv_athlet.text = movie.eventAthletes?.getOrNull(0)?.athlete?.name ?: ""

        holder.tvFname.text = movie!!.title ?:""
        holder.image.setImageResource(R.drawable.ic_favorite_select)
        holder.image.setOnClickListener(OnItemClickListener(position, listener, movie.id!!.toLong() , "event"))
    }

    override fun getItemCount(): Int {
        return user!!.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvFname: TextView = view.findViewById<View>(R.id.tvFname) as TextView
        var image: ImageView = view.findViewById<View>(R.id.image) as ImageView
        var date: TextView = view.findViewById<View>(R.id.date) as TextView
        var goal: TextView = view.findViewById<View>(R.id.goal) as TextView

        var unit: TextView = view.findViewById<View>(R.id.unit) as TextView
        var tvgoal: TextView = view.findViewById<View>(R.id.tvgoal) as TextView
        var total_time: TextView = view.findViewById<View>(R.id.total_time) as TextView
        var tv_athlet: TextView = view.findViewById<View>(R.id.tv_athlet) as TextView

    }
}
