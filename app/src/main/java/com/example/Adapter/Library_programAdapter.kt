package com.example

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.trainerapp.ApiClass.ProgramListData
import com.example.trainerapp.R
import com.example.trainerapp.program_section.New_Program_Activity
import com.example.trainerapp.program_section.ViewProgramActivity


class Library_programAdapter(
    private var splist: ArrayList<ProgramListData.testData>?,
    var context: Context,
    val listener: OnItemClickListener.OnItemClickCallback
) :
    RecyclerView.Adapter<Library_programAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Library_programAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.program_librarylist, parent, false)
        return MyViewHolder(itemView)
    }


    override fun getItemCount(): Int {
        return splist!!.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var image: ImageView = view.findViewById<View>(R.id.image) as ImageView
        var tvFname: TextView = view.findViewById<View>(R.id.tv_program_name) as TextView
        var tvDate: TextView = view.findViewById<View>(R.id.tv_date) as TextView
        var tvgoal: TextView = view.findViewById<View>(R.id.tv_goal) as TextView
        var time: TextView = view.findViewById<View>(R.id.tv_total_time) as TextView
        var img_edit: ImageView = view.findViewById<View>(R.id.img_edit) as ImageView
        var img_copy: ImageView = view.findViewById<View>(R.id.img_copy) as ImageView
        var img_delete: ImageView = view.findViewById<View>(R.id.img_delete) as ImageView
        var card: CardView = view.findViewById<View>(R.id.card) as CardView
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val movie = splist!![position]
        holder.tvFname.text = movie.name
        holder.tvgoal.text = movie.goal!!.name
        holder.time.text = movie.time
        holder.tvDate.text = movie.date ?: "Invalid Date"

        holder.img_edit.setOnClickListener {
            listener.onItemClicked(it, position, movie.id!!.toLong(), "EditProgram")
//            OnItemClickListener(position, listener, movie.id!!.toLong(), "Edit")

            Log.d("ADAPTERCLICK", "onBindViewHolder: $position     ${movie.id}")

            context.startActivity(Intent(context, New_Program_Activity::class.java).apply {
                putExtra("position", position)
                putExtra("PID", movie.id!!)
            })
        }

//        holder.image.setOnClickListener {
//            if (movie.is_favourite.toString() == "1") {
//                Log.e("FAVOURITE", "onBindViewHolder: " + "11111")
//                holder.image.setImageResource(R.drawable.ic_favorite_select)
//                OnItemClickListener(position, listener, movie.id!!.toLong(), "UnFavProgram")
//            } else {
//                Log.e("FAVOURITEEE", "onBindViewHolder: " + "2222")
//                OnItemClickListener(position, listener, movie.id!!.toLong(), "FavProgram")
//                holder.image.setImageResource(R.drawable.ic_favorite_red)
//            }
//
//        }

        if (movie.is_favourite.toString() == "1") {
            holder.image.setImageResource(R.drawable.ic_favorite_select)

        } else {
            holder.image.setImageResource(R.drawable.ic_favorite_red)
        }


        holder.image.setOnClickListener(
            if (movie.is_favourite.toString() == "1") {
                OnItemClickListener(position, listener, movie.id!!.toLong(), "UnFavProgram")
            } else {
                OnItemClickListener(position, listener, movie.id!!.toLong(), "FavProgram")
            }
        )


//        holder.img_edit.setOnClickListener(OnItemClickListener(position, listener, movie.id!!.toLong() , "EditProgram"))
        holder.img_delete.setOnClickListener(
            OnItemClickListener(
                position,
                listener,
                movie.id!!.toLong(),
                "DeleteProgram"
            )
        )
        holder.img_copy.setOnClickListener(
            OnItemClickListener(
                position,
                listener,
                movie.id!!.toLong(),
                "CopyProgram"
            )
        )
        holder.card.setOnClickListener(
            OnItemClickListener(
                position,
                listener,
                movie.id!!.toLong(),
                "ViewProgram"
            )
        )
//        holder.image.setOnClickListener(OnItemClickListener(position, listener, movie.is_favourite!!.toLong() , "isFavProgram"))

    }

}
