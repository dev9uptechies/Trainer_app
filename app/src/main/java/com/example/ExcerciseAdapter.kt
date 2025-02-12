package com.example

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.model.newClass.excercise.Exercise
import com.example.trainerapp.R
import com.makeramen.roundedimageview.RoundedImageView
import com.makeramen.roundedimageview.RoundedTransformationBuilder
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import java.text.DateFormat
import java.text.SimpleDateFormat


class ExcerciseAdapter(
    private var splist: MutableList<Exercise.ExerciseData>,
    var context: Context,

    val listener: OnItemClickListener.OnItemClickCallback
) :
    RecyclerView.Adapter<ExcerciseAdapter.MyViewHolder>() {
    var time: Int? = null
    var searchItems: List<Exercise.ExerciseData> = splist
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ExcerciseAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.excercisedata_format, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ExcerciseAdapter.MyViewHolder, position: Int) {
        val movie = searchItems[position]
        holder.tvFname.text = movie.name
        val cycle = movie.cycles
        val formatter: DateFormat = SimpleDateFormat("hh:mm:ss")

        for (i in 0 until cycle!!.size) {
            val secont = cycle[i].cycle_time
            val date1 = formatter.parse(secont)
            time = ((date1.time / 1000).toInt())

        }
        if (time == null) {
            holder.tvgoal.text = "Time or Reps: 0 Hour"
        } else {
            val data = time!! / 60
            holder.tvgoal.text = "Time or Reps: " + data / 60 + "Hour"
        }

        val transformation: Transformation = RoundedTransformationBuilder()
            .borderColor(Color.BLACK)
            .borderWidthDp(1f)
            .cornerRadiusDp(10f)
            .oval(false)
            .build()

        Picasso.get()
            .load("https://4trainersapp.com" + movie.image)
            .fit()
            .placeholder(R.drawable.ic_youtube)
            .transform(transformation)
            .into(holder.rounded_image)

        holder.tv_view_exercise.setOnClickListener(
            OnItemClickListener(position, listener, movie.id!!.toLong(), "View")
        )
        holder.tv_edit_exercise.setOnClickListener {
            Log.d("TAG", "onBindViewHolder: " + movie.id)
            holder.tv_edit_exercise.setOnClickListener(
                OnItemClickListener(
                    position,
                    listener,
                    movie.id.toLong(),
                    "Edit",
                )
            )
        }
        holder.img_delete.setOnClickListener(
            OnItemClickListener(
                position,
                listener,
                movie.id.toLong(),
                "delete"
            )
        )
    }

    override fun getItemCount(): Int {
        return searchItems.size
    }

    fun updateList(newList: List<Exercise.ExerciseData>, id: Int) {
        splist.clear()
        splist.addAll(newList)
        notifyDataSetChanged()
    }


    fun searchFilter(name: String): List<Exercise.ExerciseData> {
        searchItems = if (name.isEmpty()) {
            splist
        } else {
            splist.filter {
                it.name!!.contains(name, true)
            }
        }
        notifyDataSetChanged()
        return searchItems
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvFname: TextView = view.findViewById<View>(R.id.tvFname) as TextView
        var tv_view_exercise: TextView = view.findViewById<View>(R.id.tv_view_exercise) as TextView
        var tv_edit_exercise: TextView = view.findViewById<View>(R.id.tv_edit_exercise) as TextView
        var tvgoal: TextView = view.findViewById<View>(R.id.tvgoal) as TextView
        var img_delete: ImageView = view.findViewById<View>(R.id.img_delete) as ImageView
        var rounded_image: RoundedImageView =
            view.findViewById<View>(R.id.rounded_image) as RoundedImageView
    }
}
