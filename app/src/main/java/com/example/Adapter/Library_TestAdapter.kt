package com.example

import android.content.Context
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
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int)  {
        val movie = splist[position]
        holder.tvFname.text = movie.title
        holder.tvgoal.text =movie.goal
        holder.intrested_athletes.text = splist[position].data?.get(position)?.athlete?.name
        if(splist[position].is_favourite?.equals(0) == true){
            holder.image.setImageResource(R.drawable.ic_favorite_select)
        }else{
            holder.image.setImageResource(R.drawable.ic_favorite_red)
        }
        holder.img_edit.setOnClickListener(OnItemClickListener(position, listener, movie.id!!.toLong() , "Edit"))
        holder.img_delete.setOnClickListener(OnItemClickListener(position, listener, movie.id!!.toLong() , "Delete"))
    }

}
