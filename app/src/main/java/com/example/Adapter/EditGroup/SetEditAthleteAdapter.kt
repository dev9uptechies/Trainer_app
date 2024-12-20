package com.example.Adapter.EditGroup

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.GroupListData
import com.example.OnItemClickListener
import com.example.model.newClass.athlete.AthleteData
import com.example.trainerapp.R

class SetEditAthleteAdapter(
    private var splist: MutableList<GroupListData. GroupMembar>?,
    var context: Context,
    val listener: OnItemClickListener.OnItemClickCallback,
    val mainId: Int? = null
) : RecyclerView.Adapter<SetEditAthleteAdapter.MyViewHolder>() {

    private val selectedItems: MutableSet<Int> = mutableSetOf() // Store selected item positions

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.athlete_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvFname: TextView = view.findViewById<View>(R.id.tv_program_name) as TextView
        var checkBox: CheckBox = view.findViewById(R.id.myCheckBox) }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val movie = splist?.get(position) ?: return

        holder.checkBox.visibility = View.GONE

        holder.tvFname.text = (movie.athlete!!.name ?: "-")

        holder.checkBox.isChecked = selectedItems.contains(position)
        holder.checkBox.isClickable = false

        holder.itemView.setOnClickListener {
            if (selectedItems.contains(position)) {
                selectedItems.remove(position)
            } else {
                selectedItems.add(position)
            }
            notifyItemChanged(position)
        }

    }


    fun getSelectedAthleteData(): List<Int> {
        // Make sure splist is not null before accessing
        return if (!splist.isNullOrEmpty()) {
            selectedItems.map { splist!![it].id ?: 0 }
        } else {
            emptyList() // Return empty list if splist is null or empty
        }
    }


    override fun getItemCount(): Int {
        return splist?.size ?: 0
    }
}