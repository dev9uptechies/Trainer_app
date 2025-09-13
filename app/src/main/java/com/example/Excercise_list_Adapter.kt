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
import com.example.trainerapp.ApiClass.ProgramListData
import com.example.trainerapp.R

class Excercise_list_Adapter(
    var splist: ArrayList<ProgramListData.Program>? = null,
    var context: Context,
    private val itemClickListener: OnItemClickListener,
    val listener: com.example.OnItemClickListener.OnItemClickCallback,
    private var selectId: Int? = null
) : RecyclerView.Adapter<Excercise_list_Adapter.MyViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(id: Int, name: String, position: Int)
    }

//    init {
//        // If selectId is not set, assign the first item's exercise id as default selected
//        if (selectId == null && splist?.isNotEmpty() == true) {
//            selectId = splist!![0].exercise?.id
//        }
//    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.exercise_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val exercise = splist?.get(position)?.exercise
        if (exercise == null) return

        holder.tvFname.text = exercise.name
        holder.tv_goal.text = exercise.goal?.name ?: ""

        Log.d("ExcerciseAdapter", "onBindViewHolder: pos=$position, id=${exercise.id}, selectId=$selectId")

        // Change background based on selection
        val isSelected = if (selectId != null) {
            exercise.id == selectId
        } else {
            position == 0 // If no selectId, select the first item by default
        }

        // Set background accordingly
        if (isSelected) {
            holder.click.setBackgroundResource(R.drawable.card_select_1)
            // Update selectId so that next binds know what's selected
            if (selectId == null) {
                selectId = exercise.id
            }
        } else {
            holder.click.setBackgroundResource(R.drawable.card_unselect_1)
        }

            // Handle favorite icon
        if (exercise.is_favourite == 1) {
            holder.image.setImageResource(R.drawable.ic_favorite_select)
        } else {
            holder.image.setImageResource(R.drawable.ic_favorite_red)
        }

        // Favorite icon click listener
        holder.image.setOnClickListener {
            if (exercise.is_favourite == 1) {
                listener.onItemClicked(it, position, exercise.id!!.toLong(), "unfavEX")
            } else {
                listener.onItemClicked(it, position, exercise.id!!.toLong(), "favEX")
            }
        }

        holder.itemView.setOnClickListener {
            if (selectId != exercise.id) {
                selectId = exercise.id
                notifyDataSetChanged()
                itemClickListener.onItemClick(exercise.id!!, exercise.name!!, position)
            }
        }
    }

    override fun getItemCount(): Int = splist?.size ?: 0

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.image)
        val tv_goal: TextView = view.findViewById(R.id.tv_goal)
        val tv_total_time: TextView = view.findViewById(R.id.tv_total_time)
        val tvFname: TextView = view.findViewById(R.id.tv_program_name)
        val click: ConstraintLayout = view.findViewById(R.id.click)
    }

    fun updateSelectedId(newSelectId: Int) {
        if (selectId != newSelectId) {
            selectId = newSelectId
            notifyDataSetChanged()
        }
    }
}
