package com.example.Adapter.lesson

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trainerapp.R
import com.example.trainerapp.TestListData

class SectionLessonAdapter(
    private var splist: MutableList<TestListData.testData>,
    var context: Context,
    private val itemClickListener: OnItemClickListener,
    private var selectId: Int? = null
) :
    RecyclerView.Adapter<SectionLessonAdapter.MyViewHolder>() {
    interface OnItemClickListener {
        fun onItemClick(id: Int, name: String, position: Int)
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.name)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SectionLessonAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_select_section_layout, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SectionLessonAdapter.MyViewHolder, position: Int) {
        val movie = splist[position]

        if (movie.id == selectId) {
            holder.name.setBackgroundResource(R.drawable.card_select_1)  // Set red color
        } else {
            holder.name.setBackgroundResource(R.drawable.card_unselect_1)
        }

        holder.name.text = movie.name!!
        holder.name.setOnClickListener {
            selectId = movie.id
            notifyDataSetChanged()
            itemClickListener.onItemClick(movie.id!!, movie.name!!, position)
        }
    }

    override fun getItemCount(): Int {
        return splist.size
    }

    fun updateSelectedId(newSelectId: Int) {
        this.selectId = newSelectId
        notifyDataSetChanged()
    }
}