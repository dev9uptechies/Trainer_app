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
import com.example.GroupListData
import com.example.OnItemClickListener
import com.example.trainerapp.R
import com.example.trainerapp.training_plan.ViewTrainingPlanActivity
import com.google.gson.Gson

class PlanningAdapterAthlete(
    data: ArrayList<GroupListAthlete.GroupPlanning>?,
    private var context: Context,
    val listener: OnItemClickListener.OnItemClickCallback
) : RecyclerView.Adapter<PlanningAdapterAthlete.MyViewHolder>() {

    private var filteredData: ArrayList<GroupListAthlete.GroupPlanning> = ArrayList()

    init {
        data?.let { list ->
            filteredData = list.filter { isValidItem(it) } as ArrayList<GroupListAthlete.GroupPlanning>
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

        Log.d("SLSLSLLSLSLS", "onBindViewHolder: ${planning.start_date}")
        Log.d("SLSLSLLSLSLS", "onBindViewHolder: ${planning.competition_date}")

        holder.group_name.text = planning.name
        holder.tv_start_date.text = planning.competition_date
        holder.tv_edt_time.text = planning.start_date

        val mesocycles = movie.planning?.pre_competitive?.mesocycles
        val firstMesocycle = mesocycles?.getOrNull(0)

        if (firstMesocycle?.microcycles.isNullOrEmpty()) {
            Log.d("DEBUGDDDDDD", "Microcycles list is empty")
        } else {
            Log.d("DEBUGDDDDPPPPDD", "Microcycle Name: ${firstMesocycle?.microcycles?.getOrNull(0)?.startDate}")
        }

        val gson = Gson()
        val preSeasonJson = gson.toJson(movie.planning?.pre_season)
        val preCompetitiveJson = gson.toJson(movie.planning?.pre_competitive)
        val CompetitiveJson = gson.toJson(movie.planning?.competitive)
        val TransitionJson = gson.toJson(movie.planning?.transition)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ViewTrainingPlanActivity::class.java)
            intent.putExtra("Id", planning?.id)
            if (movie.planning?.pre_season != null) {
                intent.putExtra("AthleteGroupPreSeason", "Pre Season")
                intent.putExtra("AthleteGroupName", movie.planning?.pre_season?.name)
                intent.putExtra("AthleteGroupStartDate", movie.planning?.pre_season?.start_date)
                intent.putExtra("AthleteGroupEndDate", movie.planning?.pre_season?.end_date)
                intent.putExtra("AthleteGroupMesocycle", movie.planning?.pre_season?.mesocycle)
                intent.putExtra("AthleteGroupMesocycles", preSeasonJson)
//                intent.putExtra("AthleteGroupMesocycles", movie.planning?.pre_season)
            }

            if (movie.planning?.pre_competitive != null) {
                //pre competitive
                intent.putExtra("AthleteGroupPreCompetitive", "Pre Competitive")
                intent.putExtra("AthletePreComGroupName", movie.planning?.pre_competitive?.name)
                intent.putExtra("AthletePreComGroupStartDate", movie.planning?.pre_competitive?.start_date)
                intent.putExtra("AthletePreComGroupEndDate", movie.planning?.pre_competitive?.end_date)
                intent.putExtra("AthletePreComGroupMesocycle", movie.planning?.pre_competitive?.mesocycle)
                intent.putExtra("AthleteGroupPreCompetitiveMesocycles", preCompetitiveJson)

            }

            if (movie.planning?.competitive != null) {
                //competitive
                intent.putExtra("AthleteGroupCompetitive", "Competitive")
                intent.putExtra("AthleteComGroupName", movie.planning?.competitive?.name)
                intent.putExtra("AthleteComGroupStartDate", movie.planning?.competitive?.start_date)
                intent.putExtra("AthleteComGroupEndDate", movie.planning?.competitive?.end_date)
                intent.putExtra("AthleteComGroupMesocycle", movie.planning?.competitive?.mesocycle)
                intent.putExtra("AthleteGroupCompetitiveMesocycles", CompetitiveJson)

            }

            Log.d("ODODODOODO", "onBindViewHolder: ${movie.planning?.transition}")

            if (movie.planning?.transition != null) {
                //transition
                intent.putExtra("AthleteGroupTransition", "Transition")
                intent.putExtra("AthleteTranGroupName", movie.planning?.transition?.name)
                intent.putExtra("AthleteTranGroupStartDate", movie.planning?.transition?.start_date)
                intent.putExtra("AthleteTranGroupEndDate", movie.planning?.transition?.end_date)
                intent.putExtra("AthleteTranGroupMesocycle", movie.planning?.transition?.mesocycle)
                intent.putExtra("AthleteGroupTransitionMesocycles", TransitionJson)

            }
            context.startActivity(intent)

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
    }
}
