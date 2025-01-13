package com.example.Adapter.competition

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.trainerapp.ApiClass.RatingItem
import com.example.trainerapp.R

class ViewCompetitionAdapter(
    private var splist: MutableList<RatingItem>,
    var context: Context,
    var isCoach: Boolean,
    var isAthlete: Boolean,
    var isSetData: Boolean? = false
) :
    RecyclerView.Adapter<ViewCompetitionAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewCompetitionAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.analysis_star_item, parent, false)
        return MyViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewCompetitionAdapter.MyViewHolder, position: Int) {
        val movie = splist[position]
        holder.noOfItem.text = (position + 1).toString()
        holder.titleItem.text = movie.name
        holder.coachRating.rating = movie.coachRating!!.toFloat()
        holder.athleteRating.rating = movie.athleteRating!!.toFloat()
        if (isSetData!!) {
            holder.athleteRating.isEnabled = false
            holder.coachRating.isEnabled = false

            holder.athleteRating.progressTintList =
                context.resources.getColorStateList(R.color.yellow, null)
            holder.coachRating.progressTintList =
                context.resources.getColorStateList(R.color.red, null)
        } else {
            if (isCoach == true) {
                Log.d("tetttssttsstst", "onBindViewHolder: Coachhhhhhhhh")

                holder.coachRating.isEnabled = true
                holder.athleteRating.isEnabled = false
                holder.athleteRating.isClickable = false
                holder.athleteRating.isFocusable = false
                holder.coachRating.progressTintList =
                    context.resources.getColorStateList(R.color.red, null)
                holder.coachRating.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
                    if (fromUser) {
                        Log.d("Rating", "$rating")
                        movie.coachRating = rating.toInt()
                    }
                }
            } else if (isAthlete == true){
                Log.d("tetttssttsstst", "onBindViewHolder: Athleteeeeeeeeee")
                holder.athleteRating.isEnabled = true
                holder.coachRating.isEnabled = false
                holder.coachRating.isClickable = false
                holder.coachRating.isFocusable = false
                holder.athleteRating.progressTintList = context.resources.getColorStateList(R.color.yellow, null)
                holder.athleteRating.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
                    if (fromUser) {
                        Log.d("Rating", "$rating")
                        Toast.makeText(context, "$rating", Toast.LENGTH_SHORT).show()
                        movie.athleteRating = rating.toInt()
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return splist.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var noOfItem: TextView = view.findViewById(R.id.noOfItem)
        var titleItem: TextView = view.findViewById(R.id.titleItem)
        var coachRating: RatingBar = view.findViewById(R.id.coachRating)
        var athleteRating: RatingBar = view.findViewById(R.id.athleteRating)
    }
}
