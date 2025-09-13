package com.example.trainerapp

import android.app.usage.UsageEvents
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.GroupChateListData
import com.example.GroupListData
import com.example.OnItemClickListener
import com.example.trainerapp.ApiClass.*
import com.example.trainerapp.R
import com.example.trainerapp.TestListData
import com.makeramen.roundedimageview.RoundedImageView
import com.makeramen.roundedimageview.RoundedTransformationBuilder
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import com.zerobranch.layout.SwipeLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GroupChatAdapter (private var splist: ArrayList<GroupChateListData.groupData>?, var context: Context,val listener: OnItemClickListener.OnItemClickCallback) :
    RecyclerView.Adapter<GroupChatAdapter.MyViewHolder>(){

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GroupChatAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.group_chat_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GroupChatAdapter.MyViewHolder, position: Int) {
        val movie = splist!![position]
        holder.group_name.text = movie.name
        if (movie.last_message != null) {
            holder.chat_description.text =
                movie.last_message?.sender?.name + ": ${movie.last_message?.message}"
        }else{
            holder.chat_description.text = ""
        }

        Log.d("56555656", "onBindViewHolder: ${movie.cycles}")

        if(movie.cycles.equals(null)){
            holder.tv_cycle.text ="0 Cycle"
        }else{
            holder.tv_cycle.text = movie.cycles+" Cycle"
        }
        holder.itemView.setOnClickListener(OnItemClickListener(position,listener,movie.id!!.toLong(), movie.name!!))

        val transformation: Transformation = RoundedTransformationBuilder()
            .borderColor(Color.WHITE)
            .borderWidthDp(1f)
            .cornerRadiusDp(10f)
            .oval(false)
            .build()

        Picasso.get()
            .load("https://uat.4trainersapp.com"+movie.image)
            .fit()
            .transform(transformation)
            .into(holder.rounded_image)
    }

    override fun getItemCount(): Int {
        return splist!!.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var group_name: TextView = view.findViewById<View>(R.id.group_name) as TextView
        var chat_description: TextView = view.findViewById<View>(R.id.chat_description) as TextView
        var tv_cycle: TextView = view.findViewById<View>(R.id.tv_cycle) as TextView
        var rounded_image: RoundedImageView = view.findViewById<View>(R.id.round_image) as RoundedImageView
    }
}
