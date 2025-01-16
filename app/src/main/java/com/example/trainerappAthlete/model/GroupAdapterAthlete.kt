package com.example.trainerappAthlete.model

import com.example.GroupListData
import com.example.OnItemClickListener

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
import com.example.GroupDetailActivity
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

class GroupAdapterAthlete(
    private var splist: List<GroupListAthlete.Data>?,
    var context: Context,
    val listener: OnItemClickListener.OnItemClickCallback
) :
    RecyclerView.Adapter<GroupAdapterAthlete.MyViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GroupAdapterAthlete.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.group_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GroupAdapterAthlete.MyViewHolder, position: Int) {
        val movie = splist!![position]
        holder.group_name.text = movie.group!!.name
        holder.tv_sport.text =
            (movie.group?.sport?.title ?: "").toString() // Default to "Unknown" if sport or title is null

        val transformation: Transformation = RoundedTransformationBuilder()
            .borderColor(Color.BLACK)
            .borderWidthDp(1f)
            .cornerRadiusDp(10f)
            .oval(false)
            .build()

        Picasso.get()
            .load("https://trainers.codefriend.in" + movie.group!!.image)
            .fit()
            .transform(transformation)
            .into(holder.rounded_image, object : com.squareup.picasso.Callback {
                override fun onSuccess() {
                    Log.d("Picasso", "Image loaded successfully.")
                }

                override fun onError(e: Exception?) {
                    Log.e("PicassoError", "Image load failed: ${e?.message}")
                }
            })

//        holder.itemView.setOnClickListener(
//            OnItemClickListener(
//                position,
//                listener,
//                movie.id!!.toLong(),
//                "Athlete"
//            )
//        )

        holder.card.setOnClickListener {
            val intent = Intent(context,GroupDetailActivity::class.java)
            intent.putExtra("id", movie.id)
            intent.putExtra("group_id", movie.group_id!!.toInt())
            intent.putExtra("position", position)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return splist!!.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var group_name: TextView = view.findViewById<View>(R.id.group_name) as TextView
        var tv_sport: TextView = view.findViewById<View>(R.id.tv_sport) as TextView
        var card: CardView = view.findViewById<View>(R.id.card) as CardView
        var rounded_image: RoundedImageView = view.findViewById<View>(R.id.round_image) as RoundedImageView
    }
}