package com.example.trainerappAthlete.model
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.model.newClass.competition.Competition
import com.example.trainerapp.R
import com.example.trainerapp.competition.ViewAnalysisDataActivity

class ViewAnalysisAthleteAdapter(
    private var splist: MutableList<Competition.CompetitionData>,
    var context: Context,
) :
    RecyclerView.Adapter<ViewAnalysisAthleteAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_anlysis_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = splist[position]
        Log.d("Adapter", "Binding item at position $position: $item")
        holder.sub_title_2.visibility = View.GONE


        holder.title_name.text = item.category ?: ""
        holder.sub_title_1.text = item.competition_analysis_area?.title  ?: ""
        holder.date_name.text = item.date ?: ""

        holder.card_view.setOnClickListener {
            Log.d("Event Id :-", "${splist[position].id} \t ${splist[position].category}")
            val intent = Intent(context, ViewAnalysisDataActivity::class.java).apply {
                putExtra("title", splist[position].category)
                putExtra("eventId", splist[position].id)
                putExtra("eventIdss", splist[position].event_id)
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
