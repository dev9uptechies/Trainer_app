package com.example

import android.content.Context
import android.graphics.Color
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
        holder.tvFname.text = movie.name
        holder.titleGoal.text = "Goal : "
        holder.tvgoal.text = "${movie.goal!!.name}"
        holder.tvTime.text = "Time :"
        holder.tv_athlet.text = "${movie.time}"
        holder.rounded_image.visibility = View.GONE
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
    }

    override fun getItemCount(): Int {
        return splist!!.size
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
