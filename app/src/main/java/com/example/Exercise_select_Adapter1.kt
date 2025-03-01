package com.example

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trainerapp.ApiClass.ExcerciseData
import com.example.trainerapp.R
import com.makeramen.roundedimageview.RoundedImageView
import com.makeramen.roundedimageview.RoundedTransformationBuilder
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation

class Exercise_select_Adapter1(
//    private var splist: ArrayList<Ecercise_data_list>?,
    private var splist: MutableList<ExcerciseData.Exercise>,
    var context: Context
) :
    RecyclerView.Adapter<Exercise_select_Adapter1.MyViewHolder>() {

    var time = ArrayList<String>()
    var reps = ArrayList<String>()
    var weight = ArrayList<String>()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Exercise_select_Adapter1.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.exercise_list_image_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: Exercise_select_Adapter1.MyViewHolder, position: Int) {
        val movie = splist[position]
        holder.tvFname.text = movie.name

        for (i in movie.cycles!!) {
            if (i.time == null || i.time.toString() == "null") {
                time.add("00:00:00")
            } else {
                time.add(i.time!!)
            }
            if (i.reps == null || i.reps.toString() == "null") {
                reps.add("0")
            } else {
                reps.add(i.reps!!)
            }
//            time.add(i.time!!)
//            reps.add(i.reps!!)
            if (i.weight == null || i.weight.toString() == "null") {
                weight.add("0")
            } else {
                weight.add(i.weight!!)
            }
//            weight.add(i.weight!!)
        }

        for (i in weight) {
            if (i == null || i == "" || i == "null") {
                holder.tv_athlet.text = "Weight"
            } else {
                holder.tv_athlet.text = "$i"
            }
        }

        for (i in time) {
            if (i == null || i == "" || i == "null") {
                for (j in reps) {
                    if (j == null || j == "" || j == "null") {
                        holder.tvgoal.text = "00:00:00/00"
                    } else {
                        holder.tvgoal.text = "00:00:00/$reps"
                    }
                }
            } else {
                for (j in reps) {
                    if (j == null || j == "" || j == "null") {
                        holder.tvgoal.text = "$i/00"
                    } else {
                        holder.tvgoal.text = "$i/$j"
                    }
                }
                holder.tvgoal.text = "$i/00"
            }
        }
//        val time = movie.cycles!![0].time
//        val reps = movie.cycles!![0].reps
//        val weight = movie.cycles!![0].weight
//        if (weight == null || weight == "" || weight == "null") {
//            holder.tv_athlet.text = "Weight"
//        } else {
//            holder.tv_athlet.text = "$weight"
//        }
//
//        if (time == null || time == "" || time == "null") {
//            if (reps == null || reps == "" || reps == "null") {
//                holder.tvgoal.text = "00:00:00/00"
//            } else {
//                holder.tvgoal.text = "00:00:00/$reps"
//            }
//        } else {
//            if (reps == null || reps == "" || reps == "null") {
//                holder.tvgoal.text = "$time/00"
//            } else {
//                holder.tvgoal.text = "$time/$reps"
//            }
//        }
//        holder.tvgoal.text = "${movie.time}/${movie.reps}"
//        holder.tv_athlet.text = movie.weight
//        holder.tvgoal.text = movie.goal_id
//        holder.tv_athlet.text = movie.time
        val transformation: Transformation = RoundedTransformationBuilder()
            .borderColor(Color.BLACK)
            .borderWidthDp(1f)
            .cornerRadiusDp(10f)
            .oval(false)
            .build()

        Picasso.get()
            .load("https://4trainersapp.com" + movie.image)
            .fit()
            .transform(transformation)
            .placeholder(R.drawable.ic_youtube)
            .error(R.drawable.ic_youtube)
            .into(holder.rounded_image)
    }

    override fun getItemCount(): Int {
        return splist.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvFname: TextView = view.findViewById<View>(R.id.tvFname) as TextView
        var tvgoal: TextView = view.findViewById<View>(R.id.tvgoal) as TextView
        var tv_athlet: TextView = view.findViewById<View>(R.id.tv_athlet) as TextView
        var rounded_image: RoundedImageView =
            view.findViewById<View>(R.id.rounded_image) as RoundedImageView
    }

    fun filterData(filterList: MutableList<ExcerciseData.Exercise>) {
        splist.clear()
        splist.addAll(filterList)
        notifyDataSetChanged()
    }
}
