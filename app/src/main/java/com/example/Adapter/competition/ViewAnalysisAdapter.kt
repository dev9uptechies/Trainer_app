package com.example.Adapter.competition

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.model.newClass.competition.Competition
import com.example.trainerapp.R
import com.example.trainerapp.competition.ViewAnalysisDataActivity

class ViewAnalysisAdapter(
    private var splist: MutableList<Competition.CompetitionData>,
    var context: Context,
) :
    RecyclerView.Adapter<ViewAnalysisAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewAnalysisAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_anlysis_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewAnalysisAdapter.MyViewHolder, position: Int) {
        Log.d("Adapter", "onBindViewHolder invoked for position: $position")

        val movie = splist[position]
        val athlete = movie.athlete

        Log.d("DRDRDRRR", "onBindViewHolder: $movie")

        val competition = movie.competition_analysis_area
        if (athlete.toString() != null || athlete.toString() != "") {
            holder.title_name.text = athlete!!.name
        }

        holder.sub_title_1.text = movie.category
        holder.date_name.text = movie.date
        if (competition.toString() != null || competition.toString() != "") {
            holder.sub_title_2.text = competition!!.title
        }
        holder.card_view.setOnClickListener {
            Log.d("Event Id :-", "${splist[position].id} \t ${splist[position].category}")
            val intent = Intent(context, ViewAnalysisDataActivity::class.java).apply {
                putExtra("title", splist[position].category)
                putExtra("eventId", splist[position].id)
                putExtra("Date", splist[position].date)
                putExtra("areaId", splist[position].competition_analysis_area_id)
            }
            context.startActivity(intent)
        }
    }


    override fun getItemCount(): Int {
        return splist.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var title_name: TextView = view.findViewById(R.id.tv_title_name)
        var sub_title_1: TextView = view.findViewById(R.id.tv_sub_title_name)
        var sub_title_2: TextView = view.findViewById(R.id.tv_sub_title_name_2)
        var date_name: TextView = view.findViewById(R.id.tv_date_name)
        var card_view: ConstraintLayout = view.findViewById(R.id.click)
    }
}
