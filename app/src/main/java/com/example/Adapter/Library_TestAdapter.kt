package com.example

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.model.TestListAthlete
import com.example.model.TestListModel
import com.example.trainerapp.R
import com.example.trainerapp.TestListData


class Library_TestAdapter(private var splist: ArrayList<TestListData.testData>, var context: Context, val listener: OnItemClickListener.OnItemClickCallback) :
    RecyclerView.Adapter<Library_TestAdapter.MyViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Library_TestAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.library_testlist, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return splist.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var img_edit: ImageView = view.findViewById<View>(R.id.img_edit) as ImageView
        var img_delete: ImageView = view.findViewById<View>(R.id.img_delete) as ImageView
        var image: ImageView = view.findViewById<View>(R.id.image) as ImageView
        var tvgoal: TextView = view.findViewById<View>(R.id.tv_goal) as TextView
        var tvFname: TextView = view.findViewById<View>(R.id.tv_test_name) as TextView
        var intrested_athletes:TextView = view.findViewById<View>(R.id.tv_intrest_ath) as TextView
        var tv_unit_id:TextView = view.findViewById<View>(R.id.tv_unit_id) as TextView
        var tvDate: TextView = view.findViewById<View>(R.id.tv_date) as TextView

    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int)  {
        val movie = splist[position]
//        Log.e("TTTTTT", "onBindViewHolder: "+splist[position].data?.get(position)?.athlete )
        Log.e("TTTTTT", "onBindViewHolder: "+movie.data?.get(0)!!.athlete!!.name )
        holder.tvFname.text = movie.title
        holder.tvgoal.text =movie.goal
        holder.tv_unit_id.text =movie.unit
        holder.tvDate.text = movie.date?.take(10) ?:"Invalid Date"
//        holder.intrested_athletes.text = splist[position].data?.get(0)?.athlete?.name



        // Create a string to hold all athlete names
        val athleteNames = movie.data?.joinToString(separator = ", ") { it.athlete?.name ?: "" }

        // Set the concatenated names to the TextView
        holder.intrested_athletes.text = "Intrested Athletes: "+ athleteNames

        if (movie.is_favourite!!.equals(1)) {
            holder.image.setImageResource(R.drawable.ic_favorite_select)

        } else {
            holder.image.setImageResource(R.drawable.ic_favorite_red)
        }
        holder.image.setOnClickListener(
            if (movie.is_favourite!!.equals(1)) {
                OnItemClickListener(position, listener, movie.id!!.toLong(), "UnFavTest")
            } else {
                OnItemClickListener(position, listener, movie.id!!.toLong(), "FavTest")
            }
        )


        holder.img_edit.setOnClickListener(OnItemClickListener(position, listener, movie.id!!.toLong() , "EditTest"))
        holder.img_delete.setOnClickListener(OnItemClickListener(position, listener, movie.id!!.toLong() , "DeleteTest"))
    }

}
