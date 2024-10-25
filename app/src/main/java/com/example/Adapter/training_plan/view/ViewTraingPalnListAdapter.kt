package com.example.Adapter.training_plan.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.OnItemClickListener
import com.example.model.training_plan.cycles.GetMessocyclePreSession
import com.example.trainerapp.R
import com.example.trainerapp.training_plan.micro.ViewMicroCycleActivity
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ViewTraingPalnListAdapter(
    private var splist: MutableList<GetMessocyclePreSession.Data>?,
    var context: Context,
    val listener: OnItemClickListener.OnItemClickCallback,
    val type: String? = null,
    val mainId: Int? = null,
    private var startDate: String? = null,
    private var endDate: String? = null

) : RecyclerView.Adapter<ViewTraingPalnListAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.viewtrainingplanlist, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = splist?.get(position) ?: return

        holder.name.text = "Mesocycle " + (position + 1)

        Log.e("errorrd", "onBindViewHolder: "+item.start_date  +"   " + item.end_date )
        holder.start_date.text = "Start Date: " + formatDate(item.start_date)
        holder.end_date.text = "End Date: " + formatDate(item.end_date)
        val periods = item.periods ?: 0
        holder.mesocycle.text = "Micro No: " + periods

        holder.card.setOnClickListener {
            listener.onItemClicked(it, position, position.toLong(), "pre_session")
            val intent = Intent(context, ViewMicroCycleActivity::class.java)
            intent.putExtra("mainId", mainId)
            intent.putExtra("seasonId", item.id)
            intent.putExtra("CardType", type)
            intent.putExtra("startDate", startDate)
            intent.putExtra("endDate", endDate)
            context.startActivity(intent)
        }

        holder.card.setOnClickListener {
            listener.onItemClicked(it, position, position.toLong(), "pre_competitive")
            val intent = Intent(context, ViewMicroCycleActivity::class.java)
            intent.putExtra("mainId", mainId)
            intent.putExtra("seasonId", item.id)
            intent.putExtra("CardType", type)
            intent.putExtra("startDate", startDate)
            intent.putExtra("endDate", endDate)
            context.startActivity(intent)
        }


        holder.card.setOnClickListener {
            listener.onItemClicked(it, position, position.toLong(), "competitive")
            val intent = Intent(context, ViewMicroCycleActivity::class.java)
            intent.putExtra("mainId", mainId)
            intent.putExtra("seasonId", item.id)
            intent.putExtra("CardType", type)
            intent.putExtra("startDate", startDate)
            intent.putExtra("endDate", endDate)
            context.startActivity(intent)
        }


        holder.card.setOnClickListener {
            listener.onItemClicked(it, position, position.toLong(), "transition")
            val intent = Intent(context, ViewMicroCycleActivity::class.java)
            intent.putExtra("mainId", mainId)
            intent.putExtra("seasonId", item.id)
            intent.putExtra("CardType", type)
            intent.putExtra("startDate", startDate)
            intent.putExtra("endDate", endDate)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return splist?.size ?: 0
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView = view.findViewById(R.id.training_name_one)
        var start_date: TextView = view.findViewById(R.id.start_date_one)
        var end_date: TextView = view.findViewById(R.id.end_date_one)
        var mesocycle: TextView = view.findViewById(R.id.mesocycle_one)
        var card: CardView = view.findViewById(R.id.card_one)

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
