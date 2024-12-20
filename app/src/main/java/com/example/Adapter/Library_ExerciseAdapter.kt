package com.example

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.trainerapp.ApiClass.ExcerciseData
import com.example.trainerapp.ApiClass.ProgramListData
import com.example.trainerapp.R
import com.example.trainerapp.databinding.ExerciseDataItemBinding


class Library_ExerciseAdapter(private var splist: ArrayList<ExcerciseData.Exercise>?, var context: Context, val listener: OnItemClickListener.OnItemClickCallback) :
    RecyclerView.Adapter<Library_ExerciseAdapter.MyViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Library_ExerciseAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.exercise_librarylist, parent, false)
        return MyViewHolder(itemView)
    }


    override fun getItemCount(): Int {
        return splist!!.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var image: ImageView = view.findViewById<View>(R.id.image) as ImageView
        var tvFname: TextView = view.findViewById<View>(R.id.tv_exercise_name) as TextView
        var cotegories: TextView = view.findViewById<View>(R.id.categories) as TextView
        var exercise_type:TextView = view.findViewById<View>(R.id.exercise_type) as TextView
        var img_edit: ImageView = view.findViewById<View>(R.id.img_edit) as ImageView
        var img_delete: ImageView = view.findViewById<View>(R.id.img_delete) as ImageView
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int)  {
        val movie = splist!![position]
        holder.tvFname.text = movie.name
        holder.exercise_type.text = movie.type
        if(movie.is_favourite!! == "1"){
            holder.image.setImageResource(R.drawable.ic_favorite_select)

        }else{
            holder.image.setImageResource(R.drawable.ic_favorite_red)
        }

        holder.img_edit.setOnClickListener {
            Log.d("TAG", "onBindViewHolder: " + movie.id)
            holder.img_edit.setOnClickListener(
                OnItemClickListener(
                    position,
                    listener,
                    movie.id!!.toLong(),
                    "EditExercise",
                )
            )
        }

//        holder.img_edit.setOnClickListener(OnItemClickListener(position, listener, movie.id!!.toLong() , "EditExercise"))
        holder.img_delete.setOnClickListener(OnItemClickListener(position, listener, movie.id!!.toLong() , "DeleteExercise"))



    }

}
