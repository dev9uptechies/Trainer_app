package com.example

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.trainerapp.ApiClass.EventListData
import com.example.trainerapp.ApiClass.ProgramListData
import com.example.trainerapp.R


class Library_EventAdapter(private var splist: ArrayList<EventListData.testData>?, var context: Context, val listener: OnItemClickListener.OnItemClickCallback) :
    RecyclerView.Adapter<Library_EventAdapter.MyViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Library_EventAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.event_librarylist, parent, false)
        return MyViewHolder(itemView)
    }


    override fun getItemCount(): Int {
        return splist!!.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var image: ImageView = view.findViewById<View>(R.id.image) as ImageView
        var tveventname: TextView = view.findViewById<View>(R.id.tv_event_name) as TextView
        var tvtype: TextView = view.findViewById<View>(R.id.event_type) as TextView
        var tvintrestedathelets:TextView = view.findViewById<View>(R.id.intrested_athelets) as TextView
        var img_edit: ImageView = view.findViewById<View>(R.id.img_edit) as ImageView
        var img_delete: ImageView = view.findViewById<View>(R.id.img_delete) as ImageView
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val movie = splist!![position]
        holder.tveventname.text = movie.title
        holder.tvtype.text = movie.type

        // Check if event_athletes has data and display all athlete names
        if (!movie.event_athletes.isNullOrEmpty()) {
            val athleteNames = StringBuilder()
            for (eventAthlete in movie.event_athletes!!) {
                eventAthlete.athlete?.name?.let {
                    athleteNames.append(it).append(", ")
                }
            }

            // Remove the trailing comma and space if names were added
            if (athleteNames.isNotEmpty()) {
                athleteNames.setLength(athleteNames.length - 2)
            }

            holder.tvintrestedathelets.text = athleteNames.toString()
        } else {
            holder.tvintrestedathelets.text = "No Athlete Data" // Default text if no athletes
        }

        // Set the favorite icon based on the is_favourite value
        if (movie.is_favourite == "1") {
            holder.image.setImageResource(R.drawable.ic_favorite_select)
        } else {
            holder.image.setImageResource(R.drawable.ic_favorite_red)
        }

        holder.img_edit.setOnClickListener(OnItemClickListener(position, listener, movie.id!!.toLong(), "Edit"))
        holder.img_delete.setOnClickListener(OnItemClickListener(position, listener, movie.id!!.toLong(), "Delete"))
    }

}
