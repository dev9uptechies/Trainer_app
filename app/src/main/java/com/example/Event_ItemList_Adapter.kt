package com.example

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.model.Date_Event_model
import com.example.trainerapp.ApiClass.ExcerciseData
import com.example.trainerapp.ApiClass.ProgramListData
import com.example.trainerapp.R
import java.text.DateFormat
import java.text.SimpleDateFormat

class Event_ItemList_Adapter(
    private var splist: Date_Event_model.Data,
    var context: Context,
    val listener: CalenderFragment
) : RecyclerView.Adapter<Event_ItemList_Adapter.MyViewHolder>() {
    var time:Int?= null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Event_ItemList_Adapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.event_item_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: Event_ItemList_Adapter.MyViewHolder, position: Int) {
//        val view = splist!!
//        holder.tvFname.text = view!!.name
//        holder.tv_goal.text = view!!.goal!!.name
//        holder.image!!.setImageResource(R.drawable.ic_favorite_select)
//        if(movie.is_favourite!!.toString() == "1"){
//            holder.image!!.setImageResource(R.drawable.ic_favorite_select)
//        }else{
//            holder.image!!.setImageResource(R.drawable.ic_favorite_red)
//        }

    }

    override fun getItemCount(): Int {
        return splist.count!!.size
    }
    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var image: ImageView = view.findViewById<View>(R.id.image) as ImageView
        var tv_goal: TextView = view.findViewById<View>(R.id.tv_goal) as TextView
        var tv_total_time: TextView = view.findViewById<View>(R.id.tv_total_time) as TextView
        var tvFname: TextView = view.findViewById<View>(R.id.tv_program_name) as TextView
        var click: ConstraintLayout = view.findViewById<View>(R.id.click) as ConstraintLayout
    }
}
