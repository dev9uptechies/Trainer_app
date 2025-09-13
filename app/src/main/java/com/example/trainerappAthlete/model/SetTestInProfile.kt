package com.example.trainerappAthlete.model

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.OnItemClickListener
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.R


class SetTestInProfile(
    private var splist: ArrayList<RegisterData.TestAthletes>?,
    var context: Context,
    val listener: OnItemClickListener.OnItemClickCallback
) : RecyclerView.Adapter<SetTestInProfile.MyViewHolder>() {

    private var filterList: ArrayList<RegisterData.TestAthletes> = ArrayList()

    init {
        filterList = ArrayList(splist ?: emptyList())
    }
    init {
        filterList = ArrayList(splist?.filter { it.test != null } ?: emptyList())
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.favorite_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val movie = filterList[position]
        val test = movie.test
        holder.unit.visibility = View.VISIBLE
        holder.date.visibility = View.VISIBLE

        holder.total_time.text = "Intrested Athlete:"
        holder.tvFname.text = test?.title ?: ""
        holder.tvgoal.text = test?.goal ?: ""
        holder.date.text = test?.date?.take(10) ?: ""
        holder.unit.text = "Unit: "+test?.unit ?: ""
        holder.tvAthlete.text = test?.testAthletes?.getOrNull(0)?.athlete?.name ?: ""

        if (test?.isFavourite == 1) {
            holder.image.setImageResource(R.drawable.ic_favorite_select)
        } else {
            holder.image.setImageResource(R.drawable.ic_favorite_red)
        }

        holder.image.setOnClickListener {
            Log.d("HSHSHSHS", "onBindViewHolder: ${test?.isFavourite}")
            if (test?.isFavourite == 1) {
                listener.onItemClicked(it, position, test.id!!.toLong(), "unfavtest")
//                OnItemClickListener(position, listener, test.id!!.toLong(), "unfavtest")
            } else {
                listener.onItemClicked(it, position, test?.id!!.toLong(), "favtest")

//                OnItemClickListener(position, listener, test?.id!!.toLong(), "favtest")
            }
        }
    }

    override fun getItemCount(): Int {
        return filterList.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvFname: TextView = view.findViewById(R.id.tvFname)
        var image: ImageView = view.findViewById(R.id.image)
        var tvgoal: TextView = view.findViewById(R.id.tvgoal)
        var total_time: TextView = view.findViewById(R.id.total_time)
        var tvAthlete: TextView = view.findViewById(R.id.tv_athlet)
        var date: TextView = view.findViewById(R.id.date)
        var unit: TextView = view.findViewById(R.id.unit)
    }
}