package com.example.trainerappAthlete.model

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.OnItemClickListener
import com.example.trainerapp.R
import com.example.trainerapp.training_plan.ViewTrainingPlanActivity
import com.google.gson.Gson

class PlanningAdapterAthleteHome(
    data: ArrayList<GroupListAthlete.GroupPlanning>?,
    private var context: Context,
    val listener: OnItemClickListener.OnItemClickCallback,
    var type: String
) : RecyclerView.Adapter<PlanningAdapterAthleteHome.MyViewHolder>() {

    private var filteredData: ArrayList<GroupListAthlete.GroupPlanning> = ArrayList()

    init {
        data?.let { list ->
            filteredData =
                list.filter { isValidItem(it) } as ArrayList<GroupListAthlete.GroupPlanning>
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.planning_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val movie = filteredData[position]
        val planning = movie.planning ?: return

        holder.LinerMeso.visibility = View.VISIBLE
        Log.d("SMSMSMSM", "onBindViewHolder: $type")

        if (type == "ToDayPreSeason") {
            holder.group_name.text = planning.pre_season?.name ?: ""
            holder.tv_start_date.text = planning.pre_season?.end_date ?: ""
            holder.tv_edt_time.text = planning.pre_season?.start_date ?: ""
            holder.tv_meso.text = planning.pre_season?.mesocycle ?: ""
        }
        if (type == "ToDayPreCompetitive") {
            holder.group_name.text = planning.pre_competitive?.name ?: ""
            holder.tv_start_date.text = planning.pre_competitive?.end_date ?: ""
            holder.tv_edt_time.text = planning.pre_competitive?.start_date ?: ""
            holder.tv_meso.text = planning.pre_competitive?.mesocycle ?: ""
        }
        if (type == "ToDayCompetitive") {
            holder.group_name.text = planning.competitive?.name ?: ""
            holder.tv_start_date.text = planning.competitive?.end_date ?: ""
            holder.tv_edt_time.text = planning.competitive?.start_date ?: ""
            holder.tv_meso.text = planning.competitive?.mesocycle ?: ""
        }
    }

    override fun getItemCount(): Int = filteredData.size

    private fun isValidItem(item: GroupListAthlete.GroupPlanning): Boolean {
        return item.planning?.let {
            !it.name.isNullOrEmpty() &&
                    !it.start_date.isNullOrEmpty() &&
                    !it.competition_date.isNullOrEmpty()
        } ?: false
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var group_name: TextView = view.findViewById(R.id.tv_program_name)
        var tv_edt_time: TextView = view.findViewById(R.id.tv_edt_time)
        var tv_start_date: TextView = view.findViewById(R.id.tv_start_date)
        var tv_athelet_name: TextView = view.findViewById(R.id.tv_athelet_name)
        var LinerMeso: LinearLayout = view.findViewById(R.id.liner_meso)
        var tv_meso: TextView = view.findViewById(R.id.tv_mesocycle)
    }
}
