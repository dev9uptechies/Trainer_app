package com.example.Adapter.training_plan.view

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.OnItemClickListener
import com.example.model.training_plan.TrainingPlanData
import com.example.trainerapp.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ViewTrainingAdapter(
    private var splist: MutableList<TrainingPlanData.TrainingPlan>?,
    var context: Context,
    val listener: OnItemClickListener.OnItemClickCallback
) : RecyclerView.Adapter<ViewTrainingAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var name: TextView = view.findViewById(R.id.training_name_one)
        var start_date: TextView = view.findViewById(R.id.start_date_one)
        var end_date: TextView = view.findViewById(R.id.end_date_one)
        var mesocycles: TextView = view.findViewById(R.id.mesocycle_one)

        var name2: TextView = view.findViewById(R.id.training_name_two)
        var start_date2: TextView = view.findViewById(R.id.start_date_two)
        var end_date2: TextView = view.findViewById(R.id.end_date_two)
        var mesocycles2: TextView = view.findViewById(R.id.mesocycle_two)

        var name3: TextView = view.findViewById(R.id.training_name_three)
        var start_date3: TextView = view.findViewById(R.id.start_date_three)
        var end_date3: TextView = view.findViewById(R.id.end_date_three)
        var mesocycles3: TextView = view.findViewById(R.id.mesocycle_three)

        var name4: TextView = view.findViewById(R.id.training_name_four)
        var start_date4: TextView = view.findViewById(R.id.start_date_four)
        var end_date4: TextView = view.findViewById(R.id.end_date_four)
        var mesocycles4: TextView = view.findViewById(R.id.mesocycle_four)

        var card: CardView = view.findViewById(R.id.card_one)
        var card2: CardView = view.findViewById(R.id.card_two)
        var card3: CardView = view.findViewById(R.id.card_three)
        var card4: CardView = view.findViewById(R.id.card_four)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.viewtrainingplanlist, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val trainingData = splist?.get(position) ?: return


        val outputFormatter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DateTimeFormatter.ofPattern("dd MMM, yyyy")
        } else {
            TODO("VERSION.SDK_INT < O")
        }

        trainingData.pre_season?.let { preSeason ->
            holder.card.visibility = View.VISIBLE

            if (preSeason.name != null) {
                holder.name.text = preSeason.name
            } else {
                holder.card.visibility = View.GONE
            }
            if (preSeason.start_date != null) {
                holder.start_date.text =
                    "Start: " + formatDate(preSeason.start_date, outputFormatter)
            } else {
                holder.card.visibility = View.GONE
            }
            if (preSeason.end_date != null) {
                holder.end_date.text = "End: " + formatDate(preSeason.end_date, outputFormatter)
            } else {
                holder.card.visibility = View.GONE
            }
            if (preSeason.mesocycle != null) {
                holder.mesocycles.text = "Mesocycle: " + preSeason.mesocycle +" Cycle"
            } else {
                holder.mesocycles.text = "Mesocycle: " + "0 Cycle"
            }
        } ?: run {
            holder.card.visibility = View.GONE
        }

        trainingData.pre_competitive?.let { preCompetitive ->
            var hasValidData = false
            holder.card2.visibility = View.VISIBLE

            if (preCompetitive.name != null) {
                holder.name2.text = preCompetitive.name
                hasValidData = true
            }

            if (preCompetitive.start_date != null) {
                holder.start_date2.text =
                    "Start: " + formatDate(preCompetitive.start_date, outputFormatter)
                hasValidData = true
            }

            if (preCompetitive.end_date != null) {
                holder.end_date2.text =
                    "End: " + formatDate(preCompetitive.end_date, outputFormatter)
                hasValidData = true
            }

            if (preCompetitive.mesocycle != null) {
                holder.mesocycles2.text = "Mesocycle: " + preCompetitive.mesocycle+" Cycle"
                hasValidData = true
            } else {
                holder.mesocycles2.text = "Mesocycle: 0 Cycle"
            }

            if (!hasValidData) {
                holder.card2.visibility = View.GONE
            }

        } ?: run {
            holder.card2.visibility = View.GONE
        }

        trainingData.competitive?.let { competitive ->

            holder.card3.visibility = View.VISIBLE

            if (competitive.name != null) {
                holder.name3.text = competitive.name
            } else {
                holder.card3.visibility = View.GONE
            }
            if (competitive.start_date != null) {
                holder.start_date3.text =
                    "Start: " + formatDate(competitive.start_date, outputFormatter)
            } else {
                holder.card3.visibility = View.GONE
            }
            if (competitive.end_date != null) {
                holder.end_date3.text = "End: " + formatDate(competitive.end_date, outputFormatter)
            } else {
                holder.card3.visibility = View.GONE
            }
            if (competitive.mesocycle != null) {
                holder.mesocycles3.text = "Mesocycle: " + competitive.mesocycle+" Cycle"
            } else {
                holder.mesocycles3.text = "Mesocycle: " + "0 Cycle"
            }
        } ?: run {
            holder.card3.visibility = View.GONE
        }

        trainingData.transition?.let { transition ->

            holder.card4.visibility = View.VISIBLE

            if (transition.name != null) {
                holder.name4.text = transition.name
            } else {
                holder.card4.visibility = View.GONE
            }
            if (transition.start_date != null) {
                holder.start_date4.text =
                    "Start: " + formatDate(transition.start_date, outputFormatter)
            } else {
                holder.card4.visibility = View.GONE
            }
            if (transition.end_date != null) {
                holder.end_date4.text = "End: " + transition.end_date
            } else {
                holder.card4.visibility = View.GONE
            }
            if (transition.mesocycle != null) {
                holder.mesocycles4.text = "Mesocycle: " + transition.mesocycle+" Cycle"
            } else {
                holder.mesocycles4.text = "Mesocycle: " + "0 Cycle"
            }
        } ?: run {
            holder.card4.visibility = View.GONE
        }

        // Set click listeners for cards
        holder.card.setOnClickListener {
            listener.onItemClicked(it, position, position.toLong(), "pre_session")
            //navigateToViewTrainingPlanList(position, "pre_session")
        }
        holder.card2.setOnClickListener {
            listener.onItemClicked(it, position, position.toLong(), "pre_competitive")
//            navigateToViewTrainingPlanList(
//                position,
//                "pre_competitive"
//            )
        }
        holder.card3.setOnClickListener {
            listener.onItemClicked(it, position, position.toLong(), "competitive")
//            navigateToViewTrainingPlanList(
//                position,
//                "competitive"
//
//            )
        }
        holder.card4.setOnClickListener {
            listener.onItemClicked(it, position, position.toLong(), "transition")
//            navigateToViewTrainingPlanList(
//                position,
//                "transition"
//            )
        }
    }

    private fun navigateToViewTrainingPlanList(position: Int, cardType: String) {
//        val intent = Intent(context, ViewTraingPlanList::class.java)
//
//        val trainingData = splist?.get(position)
//
//        when (cardType) {
//            "pre_session" -> {
//                intent.putExtra("mainId", trainingData?.pre_season?.id)
//                intent.putExtra("Id", trainingData?.id)
//                intent.putExtra("startDate", trainingData?.start_date)
//                intent.putExtra("endDate", trainingData?.competition_date)
//            }
//
//            "pre_competitive" -> {
//                intent.putExtra("mainId", trainingData?.pre_competitive?.id)
//                intent.putExtra("startDate", trainingData?.start_date)
//                intent.putExtra("endDate", trainingData?.competition_date)
//            }
//
//            "competitive" -> {
//                intent.putExtra("mainId", trainingData?.competitive?.id)
//                intent.putExtra("startDate", trainingData?.start_date)
//                intent.putExtra("endDate", trainingData?.competition_date)
//            }
//
//            "transition" -> {
//                intent.putExtra("mainId", trainingData?.transition?.id)
//                intent.putExtra("startDate", trainingData?.start_date)
//                intent.putExtra("endDate", trainingData?.competition_date)
//            }
//        }
//        intent.putExtra("CardType", cardType)
//        context.startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatDate(dateString: String?, outputFormatter: DateTimeFormatter): String {
        if (dateString.isNullOrEmpty()) {
            return "N/A"
        }
        return try {
            // Assuming the input date is in "yyyy-MM-dd" format
            val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val date = LocalDate.parse(dateString, inputFormatter)
            date.format(outputFormatter)
        } catch (e: Exception) {
            "N/A" // Handle parsing errors gracefully
        }
    }

    override fun getItemCount(): Int {
        return splist?.size ?: 0
    }
}