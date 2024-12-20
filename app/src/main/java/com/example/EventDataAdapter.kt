package com.example

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trainerapp.ApiClass.EventListData
import com.example.trainerapp.R
import com.zerobranch.layout.SwipeLayout
import java.text.DateFormat
import java.text.SimpleDateFormat

class EventDataAdapter(
    private var splist: ArrayList<EventListData.testData>?,
    var context: Context,
    val listener: OnItemClickListener.OnItemClickCallback
) :
    RecyclerView.Adapter<EventDataAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EventDataAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.facebookdata_format, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: EventDataAdapter.MyViewHolder, position: Int) {
        val movie = splist!![position]
        holder.tvgoal.visibility = View.GONE
        holder.tv_unit.visibility = View.GONE
        holder.tv_time1.visibility = View.GONE
        holder.tv_event.visibility = View.VISIBLE
        holder.tvFname.text = movie.title
        holder.tv_event.text = "Event Type : " + movie.type

        val athletesNames = StringBuilder()

        for (eventAthlete in movie.event_athletes!!) {
            val athleteName = eventAthlete.athlete?.name
            if (athleteName != null) {
                if (athletesNames.isNotEmpty()) {
                    athletesNames.append(", ") // Add a comma to separate names
                }
                athletesNames.append(athleteName)
            }
        }

        holder.tv_athlet.text = "Interested Athletes: $athletesNames"
        val formatter: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        holder.tv_time.text = formatter.format(formatter.parse(movie.date!!)!!)
//        holder.tvgoal.text = "Goal: " + movie.event_athletes
//        holder.tv_unit.text = "Unit: " + movie.event_athletes
        if (movie.is_favourite!! == "1") {
            holder.image.setImageResource(R.drawable.ic_favorite_select)
        } else {
            holder.image.setImageResource(R.drawable.ic_favorite_red)
        }
//        holder.img_edit.setOnClickListener(
//            OnItemClickListener(
//                position,
//                listener,
//                movie.id!!.toLong(),
//                "Edit"
//            )
//        )

        holder.img_edit.setOnClickListener {
            holder.swipe.close(false)
            listener.onItemClicked(it, position, movie.id!!.toLong(), "Edit")
        }
        holder.img_delete.setOnClickListener {
            holder.swipe.close(false)
            listener.onItemClicked(it, position, movie.id!!.toLong(), "Delete")
        }
//            OnItemClickListener(
//                position,
//                listener,
//                movie.id!!.toLong(),
//                "Delete"
//            )

        holder.image.setOnClickListener(
            if (movie.is_favourite!!.equals(1)) {
                OnItemClickListener(position, listener, movie.id!!.toLong(), "unfav")
            } else {
                OnItemClickListener(position, listener, movie.id!!.toLong(), "fav")
            }
        )

    }

    override fun getItemCount(): Int {
        return splist!!.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvFname: TextView = view.findViewById<View>(R.id.tvFname) as TextView
        var tv_athlet: TextView = view.findViewById<View>(R.id.tv_athlet) as TextView
        var tvgoal: TextView = view.findViewById<View>(R.id.tvgoal) as TextView
        var tv_unit: TextView = view.findViewById<View>(R.id.tv_unit) as TextView
        var tv_time: TextView = view.findViewById<View>(R.id.tv_time) as TextView
        var tv_time1: TextView = view.findViewById<View>(R.id.tv_time1) as TextView
        var tv_event: TextView = view.findViewById<View>(R.id.tv_event) as TextView
        var img_delete: ImageView = view.findViewById<View>(R.id.img_delete) as ImageView
        var img_edit: ImageView = view.findViewById<View>(R.id.img_edit) as ImageView
        var image: ImageView = view.findViewById<View>(R.id.image) as ImageView
        var swipe: SwipeLayout = view.findViewById(R.id.swipe_layout)
    }
}
