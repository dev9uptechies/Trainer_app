package com.example

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.model.AthleteDataPackage.AthleteDetails
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.R

class EventAdapterAthlete(
    private val athleteList: List<AthleteDetails.Athlete.EventAthlete>,
    var context: Context,
    val listener: OnItemClickListener.OnItemClickCallback
) :
    RecyclerView.Adapter<EventAdapterAthlete.MyViewHolder>() {
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EventAdapterAthlete.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.test_layout, parent, false)
        return MyViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: EventAdapterAthlete.MyViewHolder, position: Int) {
        val athlete = athleteList[position]

        holder.tvdate.visibility = View.VISIBLE
        holder.tvunit.visibility = View.VISIBLE
        holder.tvimage.visibility = View.GONE
        holder.tvunit.visibility = View.GONE

            holder.tvFname.text = "Event Name: "+ (athlete.event?.title ?: "")
            holder.tvgoal.text = athlete.event?.type ?: ""
            holder.tvtime.text = athlete.athlete?.name ?: ""
            holder.tvdate.text = athlete.event?.date?.take(10) ?: ""

    }

    override fun getItemCount(): Int {
        return athleteList.size
    }


    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var tvFname: TextView = view.findViewById<View>(R.id.tv_event_name) as TextView
        var tvgoal: TextView = view.findViewById<View>(R.id.tv_event_type) as TextView
        var tvtime: TextView = view.findViewById<View>(R.id.tv_interested_atheletes) as TextView
        var tvdate: TextView = view.findViewById<View>(R.id.tv_event_date) as TextView
        var tvunit: TextView = view.findViewById<View>(R.id.tv_event_unit) as TextView
        var tvimage: ImageView = view.findViewById<View>(R.id.image) as ImageView
        var tvcard: CardView = view.findViewById<View>(R.id.card) as CardView

    }
}
