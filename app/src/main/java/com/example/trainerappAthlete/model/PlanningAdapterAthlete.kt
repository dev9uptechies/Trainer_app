package com.example.trainerappAthlete.model

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.GroupListData
import com.example.OnItemClickListener
import com.example.trainerapp.R
import com.example.trainerapp.training_plan.ViewTrainingPlanActivity

class PlanningAdapterAthlete(
    data: ArrayList<GroupListAthlete.GroupPlanning>?,
    private var context: Context,
    val listener: OnItemClickListener.OnItemClickCallback
) : RecyclerView.Adapter<PlanningAdapterAthlete.MyViewHolder>() {

    private var filteredData: ArrayList<GroupListAthlete.GroupPlanning> = ArrayList()

    init {
        // ✅ Filter out items with missing fields
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

        holder.group_name.text = planning.name
        holder.tv_start_date.text = planning.start_date
        holder.tv_edt_time.text = planning.competition_date

        holder.itemView.setOnClickListener {
            val planningId = planning.id?.toInt() ?: 0
            val intent = Intent(context, ViewTrainingPlanActivity::class.java).apply {
                putExtra("Id", planningId)
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
    }
}
