package com.example

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.trainerapp.ApiClass.EventListData
import com.example.trainerapp.ApiClass.ProgramListData
import com.example.trainerapp.R
import com.example.trainerapp.program_section.ViewProgramActivity
import com.zerobranch.layout.SwipeLayout

class GetProgramListAdapterGroup(
    private var splist: ArrayList<ProgramListData.testData>?,
    var context: Context,
    val listener: OnItemClickListener.OnItemClickCallback

) :
    RecyclerView.Adapter<GetProgramListAdapterGroup.MyViewHolder>() {
    private val selectedItems: MutableSet<Int> = mutableSetOf() // Store selected item positions
    private var filterList: ArrayList<EventListData.testData> = ArrayList()


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GetProgramListAdapterGroup.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list_selected_day, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GetProgramListAdapterGroup.MyViewHolder, position: Int) {
        holder.tv_athlet.visibility = View.GONE
        val movie = splist!![position]
        holder.tv_unit.visibility = View.GONE
        holder.tvFname.text = movie.name
        holder.tvgoal.text = "Goal: " + movie.goal!!.name

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

    override fun getItemCount(): Int {
        return splist!!.size
    }

    fun getSelectedProgramData(): List<Int> {
        return selectedItems.map { filterList[it].id ?: 0 }
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tv_unit: TextView = view.findViewById<View>(R.id.tv_unit) as TextView
        var tvgoal: TextView = view.findViewById<View>(R.id.tvgoal) as TextView
        var tv_athlet: TextView = view.findViewById<View>(R.id.tv_athlet) as TextView
        var tvFname: TextView = view.findViewById<View>(R.id.tvFname) as TextView
        var click: LinearLayout = view.findViewById<View>(R.id.click) as LinearLayout
        var tvDate: TextView = view.findViewById(R.id.tv_date)
        var checkBox: CheckBox = view.findViewById(R.id.myCheckBox)
        var editImage: ImageView = view.findViewById(R.id.img_edit)
    }
}
