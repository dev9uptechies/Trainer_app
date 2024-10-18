package com.example.trainerapp.ApiClass.Training_Plan.create_training

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.OnItemClickListener
import com.example.model.training_plan.TrainingPlanData
import com.example.trainerapp.R

class EditeTrainingPlanAdapter(

    private var splist: MutableList<TrainingPlanData.TrainingPlan>?,
    var context: Context,
    val listener: OnItemClickListener.OnItemClickCallback


) : RecyclerView.Adapter<EditeTrainingPlanAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EditeTrainingPlanAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.editetriningplanactivityadapter, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(
        holder: EditeTrainingPlanAdapter.MyViewHolder,
        position: Int
    ) {

        val movie = splist?.get(position) ?: return

        holder.img_delete.setOnClickListener {
            listener.onItemClicked(it, position, movie.id!!.toLong(), "Delete")
        }


        if (movie.pre_season != null) {

            if (movie.pre_season.name != null) {
                holder.card.visibility = View.VISIBLE
                holder.name.text = movie.pre_season.name
            } else {
                holder.card.visibility = View.GONE
            }

            if (movie.pre_season.start_date != null) {
                holder.card.visibility = View.VISIBLE
                holder.start_date.text =
                    "Start: - ${movie.pre_season.start_date}" // Use Elvis operator for fallback
            } else {
                holder.card.visibility = View.GONE
            }

            if (movie.pre_season.end_date != null) {
                holder.card.visibility = View.VISIBLE
                holder.end_date.text = "End:- ${movie.pre_season.end_date}"
            } else {
                holder.card.visibility = View.GONE
            }

            if (movie.pre_season.mesocycle != null) {
                holder.mesocycles.text = "Mesocycle:- ${movie.pre_season.mesocycle}"
            } else {
//                holder.card.visibility = View.GONE
                holder.mesocycles.text = "0" + " Days"
            }

        } else {
            holder.card.visibility = View.GONE
        }

        if (movie.pre_competitive != null) {

            if (movie.pre_competitive.name != null) {
                holder.card2.visibility = View.VISIBLE
                holder.name2.text = movie.pre_competitive.name
            } else {
                holder.card.visibility = View.GONE
            }

            if (movie.pre_competitive.start_date != null) {
                holder.card2.visibility = View.VISIBLE
                holder.start_date2.text = movie.pre_competitive.start_date
            } else {
                holder.card.visibility = View.GONE
            }

            if (movie.pre_competitive.end_date != null) {
                holder.card2.visibility = View.VISIBLE
                holder.end_date2.text = movie.pre_competitive.end_date
            } else {
                holder.card2.visibility = View.GONE
            }

            if (movie.pre_competitive.mesocycle != null) {
                holder.mesocycles2.text = movie.pre_competitive.mesocycle + " Days"
            } else {
//                holder.card2.visibility = View.GONE
                holder.mesocycles2.text = "0" + " Days"
            }

        } else {
            holder.card4.visibility = View.GONE

        }

        if (movie.competitive != null) {

            if (movie.competitive.name != null) {
                holder.card3.visibility = View.VISIBLE
                holder.name3.text = movie.competitive.name
            } else {
                holder.card.visibility = View.GONE
            }

            if (movie.competitive.start_date != null) {
                holder.card3.visibility = View.VISIBLE
                holder.start_date3.text = movie.competitive.start_date
            } else {
                holder.card.visibility = View.GONE
            }

            if (movie.competitive.end_date != null) {
                holder.card3.visibility = View.VISIBLE
                holder.end_date3.text = movie.competitive.end_date
            } else {
                holder.card3.visibility = View.GONE
            }

            if (movie.competitive.mesocycle != null) {
                holder.mesocycles3.text = movie.competitive.mesocycle + " Days"
            } else {
//                holder.card3.visibility = View.GONE
                holder.mesocycles3.text = "0" + " Days"
            }

        } else {
            holder.card3.visibility = View.GONE
        }
        if (movie.transition != null) {

            if (movie.transition.name != null) {
                holder.card4.visibility = View.VISIBLE
                holder.name4.text = movie.transition.name
            } else {
                holder.card4.visibility = View.GONE
            }

            if (movie.transition.start_date != null) {
                holder.card4.visibility = View.VISIBLE
                holder.start_date4.text = movie.transition.start_date
            } else {
                holder.card.visibility = View.GONE
            }

            if (movie.transition.end_date != null) {
                holder.card4.visibility = View.VISIBLE
                holder.end_date4.text = movie.transition.end_date
            } else {
                holder.card4.visibility = View.GONE
            }

            if (movie.transition.mesocycle != null) {
                holder.mesocycles4.text = movie.transition.mesocycle + " Days"
            } else {
//                holder.card4.visibility = View.GONE
                holder.mesocycles4.text = "0" + " Days"
            }

        } else {
            holder.card4.visibility = View.GONE
        }


//
//        holder.card.setOnClickListener { navigateToViewTrainingPlanList(position, "pre_season") }
//        holder.card2.setOnClickListener {
//            navigateToViewTrainingPlanList(
//                position,
//                "pre_competitive"
//            )
//        }
//        holder.card3.setOnClickListener { navigateToViewTrainingPlanList(position, "competitive") }
//        holder.card4.setOnClickListener { navigateToViewTrainingPlanList(position, "transition") }

    }


    override fun getItemCount(): Int {
        return splist?.size ?: 0
    }


    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var name: TextView = view.findViewById(R.id.ent_pre_sea_name)
        var start_date: TextView = view.findViewById(R.id.ent_start_date_liner)
        var end_date: TextView = view.findViewById(R.id.ent_ent_date_liner)
        var mesocycles: TextView = view.findViewById(R.id.linear_days_list)

        var name2: TextView = view.findViewById(R.id.ent_pre_sea_name2)
        var start_date2: TextView = view.findViewById(R.id.ent_start_date_liner2)
        var end_date2: TextView = view.findViewById(R.id.ent_ent_date_liner2)
        var mesocycles2: TextView = view.findViewById(R.id.linear_days_list2)

        var name3: TextView = view.findViewById(R.id.ent_pre_sea_name3)
        var start_date3: TextView = view.findViewById(R.id.ent_start_date_liner3)
        var end_date3: TextView = view.findViewById(R.id.ent_ent_date_liner3)
        var mesocycles3: TextView = view.findViewById(R.id.linear_days_list3)

        var name4: TextView = view.findViewById(R.id.ent_pre_sea_name4)
        var start_date4: TextView = view.findViewById(R.id.ent_start_date_liner4)
        var end_date4: TextView = view.findViewById(R.id.ent_ent_date_liner4)
        var mesocycles4: TextView = view.findViewById(R.id.linear_days_list4)

        var card: LinearLayout = view.findViewById(R.id.layout1)
        var card2: LinearLayout = view.findViewById(R.id.layout2)
        var card3: LinearLayout = view.findViewById(R.id.layout3)
        var card4: LinearLayout = view.findViewById(R.id.layout4)

        var img_delete: ImageView = view.findViewById(R.id.img_delete)


    }

}