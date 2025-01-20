package com.example

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trainerapp.ApiClass.ProgramListData
import com.example.trainerapp.R
import com.makeramen.roundedimageview.RoundedImageView
import com.makeramen.roundedimageview.RoundedTransformationBuilder
import com.squareup.picasso.Transformation

class Exercise_select_Adapter(
//    private var splist: ArrayList<Ecercise_data_list>?,
    private var splist: ArrayList<ProgramListData.testData>?,
    private var sectionName: String? = null,
    var context: Context
) :
    RecyclerView.Adapter<Exercise_select_Adapter.MyViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Exercise_select_Adapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.exercise_list_image_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: Exercise_select_Adapter.MyViewHolder, position: Int) {
        val movie = splist!![position]

        if (sectionName == movie.section!!.name) {
            holder.tvFname.text = movie.name
            holder.titleGoal.text = "Goal : "
            holder.tvgoal.text = "${movie.goal!!.name}"
            holder.tvTime.text = "Time :"
            holder.tv_athlet.text = "${movie.time}"
            holder.rounded_image.visibility = View.GONE

            Log.d("SECTION NAME:- ", "onBindViewHolder: $sectionName")
            Log.d("SECTION NAME:- ", "onBindViewHolder: ${movie.section!!.name}")
//        holder.tv_athlet.text = movie.weight
//        holder.tvgoal.text = movie.goal_id
//        holder.tv_athlet.text = movie.time
            val transformation: Transformation = RoundedTransformationBuilder()
                .borderColor(Color.BLACK)
                .borderWidthDp(1f)
                .cornerRadiusDp(10f)
                .oval(false)
                .build()

//        Picasso.get()
//            .load("https://trainers.codefriend.in" + movie.image)
//            .fit()
//            .transform(transformation)
//            .placeholder(R.drawable.ic_youtube)
//            .error(R.drawable.ic_youtube)
//            .into(holder.rounded_image)

        } else {
            // If names don't match, you can hide the item or clear the fields
            Log.d(
                "SECTION NAME:- ",
                "onBindViewHolder: $sectionName does not match ${movie.section!!.name}"
            )

            // Option 1: Make the item invisible
            holder.itemView.visibility = View.GONE
            holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)

            // Option 2: Clear the fields if you don't want to hide the item
            // holder.tvFname.text = ""
            // holder.titleGoal.text = ""
            // holder.tvgoal.text = ""
            // holder.tvTime.text = ""
            // holder.tv_athlet.text = ""
            // holder.rounded_image.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return splist!!.size
    }


    fun updateData(newExercises: List<ProgramListData.testData>) {
        splist = ArrayList(newExercises) // Convert to ArrayList if needed
        notifyDataSetChanged()
    }




    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvFname: TextView = view.findViewById<View>(R.id.tvFname) as TextView
        var titleGoal: TextView = view.findViewById<View>(R.id.goal) as TextView
        var tvgoal: TextView = view.findViewById<View>(R.id.tvgoal) as TextView

        var tvTime: TextView = view.findViewById<View>(R.id.total_time) as TextView
        var tv_athlet: TextView = view.findViewById<View>(R.id.tv_athlet) as TextView
        var rounded_image: RoundedImageView =
            view.findViewById<View>(R.id.rounded_image) as RoundedImageView
    }
}
