package com.example.Adapter.training_plan

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.OnItemClickListener
import com.example.model.training_plan.TrainingPlanData
import com.example.trainerapp.R
import com.example.trainerapp.training_plan.ViewTrainingPlanActivity
import com.zerobranch.layout.SwipeLayout
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class VewTrainingPlanAdapter(
    private var splist: MutableList<TrainingPlanData.TrainingPlan>?,
    var context: Context,
    val listener: OnItemClickListener.OnItemClickCallback,
    val mainId: Int? = null
) : RecyclerView.Adapter<VewTrainingPlanAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.training_plan_list, parent, false)
        return MyViewHolder(itemView)
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var img_edit: ImageView = view.findViewById(R.id.img_edit)
        var img_delete: ImageView = view.findViewById(R.id.img_delete)
        var image_cpy: ImageView = view.findViewById(R.id.img_copy)
        var name: TextView = view.findViewById(R.id.training_name)
        var start_date: TextView = view.findViewById(R.id.start_date)
        var end_date: TextView = view.findViewById(R.id.end_date)
        val swipe = view.findViewById<SwipeLayout>(R.id.swipe_layout)
        val card = view.findViewById<ConstraintLayout>(R.id.time_card)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val movie = splist?.get(position) ?: return

        holder.name.text = movie.name
        holder.start_date.text = "Start: ${formatDate(movie.start_date)}"
        holder.end_date.text = "End: ${formatDate(movie.competition_date)}"


        holder.img_edit.setOnClickListener {
            holder.swipe.close(true)
            listener.onItemClicked(
                it,
                position, movie.id!!.toLong(), "Edit"
            )
        }


        holder.img_delete.setOnClickListener {
            holder.swipe.close(true)
            listener.onItemClicked(it, position, movie.id!!.toLong(), "Delete")
        }

        holder.image_cpy.setOnClickListener {
            holder.swipe.close(true)
            listener.onItemClicked(it, position, movie.id!!.toLong(), "Duplicate")
        }


        holder.card.setOnClickListener {
            val intent = Intent(context, ViewTrainingPlanActivity::class.java)
            intent.putExtra("Id", splist!![position].id)
            context.startActivity(intent)
        }
//
//        holder.img_edit.setOnClickListener {
//            holder.swipe.close(true)
//            val intent = Intent(context, EditeTrainingPlanActivity::class.java)
//            intent.putExtra("MainId", movie.id)
//            intent.putExtra("Id", movie.pre_season!!.id)
//            context.startActivity(intent)
//        }
    }

    override fun getItemCount(): Int {
        return splist?.size ?: 0
    }

    @SuppressLint("NewApi")
    private fun formatDate(dateString: String?): String {
        return if (!dateString.isNullOrEmpty()) {
            val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val outputFormatter = DateTimeFormatter.ofPattern("dd MMM, yyyy")
            val date = LocalDate.parse(dateString, inputFormatter)
            date.format(outputFormatter)
        } else {
            "N/A"
        }
    }
}