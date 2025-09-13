package com.example

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.trainerapp.R

class ViewLessionAdapter(
    private var splist: List<LessonData.Lesson_Programs>,
    var context: Context,
    val listener: OnItemClickListener.OnItemClickCallback,
    private var selectId: Int? = null
) :
    RecyclerView.Adapter<ViewLessionAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewLessionAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.exercise_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val movie = splist[position].program

        // Null-safe check for movie object and its properties

        Log.d("DDVDVDVDV", "onBindViewHolder: EDDDD")
        movie?.let {
            holder.tvFname.text = it.name ?: "No Name"  // Use default text if name is null
            holder.tv_goal.text = it.goal?.name ?: "No Goal"  // Use default text if goal is null
            holder.tv_total_time.text = it.time ?: "No Time"  // Use default text if time is null

            // Set background for selected item
            if (it.id == selectId) {
                holder.click.setBackgroundResource(R.drawable.card_select_1)
            } else {
                holder.click.setBackgroundResource(R.drawable.card_unselect_1)
            }

            holder.image.setOnClickListener{
                if (movie.is_favourite.toString() == "1") {
                    Log.d("DJJDJDJD", "onBindViewHolder: ${movie.id}")
                    listener.onItemClicked(it, position, movie.id!!.toLong(), "unfav")
                } else {
                    Log.d("DJJDJDJD", "onBindViewHolder: ${movie.id}")

                    listener.onItemClicked(it, position, movie.id!!.toLong(), "fav")

                }
            }
            if (it.is_favourite?.toString() == "1") {
                Log.d(":AAOAOOAO", "onBindViewHolder: koo")
                holder.image.setImageResource(R.drawable.ic_favorite_select)
            } else {
                holder.image.setImageResource(R.drawable.ic_favorite_red)
            }

            // Handle click event
//            holder.itemView.setOnClickListener {
//                selectId = it.id
//                notifyDataSetChanged()
//                listener.onItemClicked(it, position, it.id!!.toLong(), "Click")
//            }
        } ?: run {
            holder.tvFname.text = "No Program"
            holder.tv_goal.text = "No Goal"
            holder.tv_total_time.text = "No Time"
        }
    }

    override fun getItemCount(): Int {
        return splist.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var image: ImageView = view.findViewById<View>(R.id.image) as ImageView
        var tv_goal: TextView = view.findViewById<View>(R.id.tv_goal) as TextView
        var tv_total_time: TextView = view.findViewById<View>(R.id.tv_total_time) as TextView
        var tvFname: TextView = view.findViewById<View>(R.id.tv_program_name) as TextView
        var click: ConstraintLayout = view.findViewById<View>(R.id.click) as ConstraintLayout
    }

    fun updateSelectedId(newSelectId: Int) {
        this.selectId = newSelectId
        notifyDataSetChanged()
    }
}
