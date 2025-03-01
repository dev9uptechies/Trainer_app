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
        var tvDate: TextView = view.findViewById<View>(R.id.tv_date) as TextView

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val movie = splist!![position]
        holder.tveventname.text = movie.title
        holder.tvtype.text = movie.type
        holder.tvDate.text = movie.date?.take(10) ?: "Invalid Date"

        if (!movie.event_athletes.isNullOrEmpty()) {
            val athleteNames = StringBuilder()
            for (eventAthlete in movie.event_athletes!!) {
                eventAthlete.athlete?.name?.let {
                    athleteNames.append(it).append(", ")
                }
            }

            if (athleteNames.isNotEmpty()) {
                athleteNames.setLength(athleteNames.length - 2)
            }

            Log.d("AthleteNames", "onBindViewHolder: $athleteNames")

            holder.tvintrestedathelets.text = "Intrested Athelets:" + athleteNames.toString()
        } else {
            holder.tvintrestedathelets.text = "No Athlete Data" // Default text if no athletes
        }

        if (movie.is_favourite.toString() == "1") {
            holder.image.setImageResource(R.drawable.ic_favorite_select)

        } else {
            holder.image.setImageResource(R.drawable.ic_favorite_red)
        }


        holder.image.setOnClickListener(
            if (movie.is_favourite.toString() == "1") {
                OnItemClickListener(position, listener, movie.id!!.toLong(), "UnFavEvent")
            } else {
                OnItemClickListener(position, listener, movie.id!!.toLong(), "FavEvent")
            }
        )

        holder.img_edit.setOnClickListener {

            Log.e("lolllolol", "onBindViewHolder: "+movie.id )
            listener.onItemClicked(it, position, movie.id!!.toLong(), "EditEvent")
        }

//        holder.img_edit.setOnClickListener(OnItemClickListener(position, listener, movie.id!!.toLong(), "EditEvent"))
        holder.img_delete.setOnClickListener(OnItemClickListener(position, listener, movie.id!!.toLong(), "DeleteEvent"))
    }

}
