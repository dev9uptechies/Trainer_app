package com.example

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.trainerapp.ApiClass.ProgramListData
import com.example.trainerapp.R


class Library_programAdapter(private var splist: ArrayList<ProgramListData.testData>?, var context: Context, val listener: OnItemClickListener.OnItemClickCallback) :
    RecyclerView.Adapter<Library_programAdapter.MyViewHolder>(){
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
        var tvgoal: TextView = view.findViewById<View>(R.id.tv_goal) as TextView
        var time:TextView = view.findViewById<View>(R.id.tv_total_time) as TextView
        var img_edit: ImageView = view.findViewById<View>(R.id.img_edit) as ImageView
        var img_copy: ImageView = view.findViewById<View>(R.id.img_copy) as ImageView
        var img_delete: ImageView = view.findViewById<View>(R.id.img_delete) as ImageView
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int)  {
        val movie = splist!![position]
        holder.tvFname.text = movie.name
        holder.tvgoal.text =movie.goal!!.name
        holder.time.text = movie.time
        if(movie.is_favourite!! == "1"){
            holder.image.setImageResource(R.drawable.ic_favorite_select)

        }else{
            holder.image.setImageResource(R.drawable.ic_favorite_red)
        }
        holder.img_edit.setOnClickListener(OnItemClickListener(position, listener, movie.id!!.toLong() , "Edit"))
        holder.img_delete.setOnClickListener(OnItemClickListener(position, listener, movie.id!!.toLong() , "Delete"))
        holder.img_copy.setOnClickListener(OnItemClickListener(position, listener, movie.id!!.toLong() , "Copy"))



    }

}
