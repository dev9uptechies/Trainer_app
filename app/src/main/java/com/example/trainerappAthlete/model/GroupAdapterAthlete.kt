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
    private var splist: List<GroupListData.groupData>?,
    private val context: Context,
    private val listener: OnItemClickListener.OnItemClickCallback
) : RecyclerView.Adapter<GroupAdapterAthlete.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.group_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val movie = splist?.get(position)
        movie?.let {
            holder.group_name.text = it.name ?: "Unknown Group"
            holder.tv_sport.text = it.sport?.title ?: "Unknown Sport"
            holder.days_txt.text = it.schedule?.joinToString(", ") { schedule -> schedule.day ?: "" }

            Log.d("CCRCRCRCRC", "onBindViewHolder: ${it.schedule?.get(0)?.day}")
            Log.d("CCRCRCRCRC", "onBindViewHolder: ${it.days}")


            val transformation: Transformation = RoundedTransformationBuilder()
                .borderColor(Color.BLACK)
                .borderWidthDp(1f)
                .cornerRadiusDp(10f)
                .oval(false)
                .build()

            // Set a progress bar while loading the image (optional)
            holder.progressBar.visibility = View.VISIBLE

            Picasso.get()
                .load("https://uat.4trainersapp.com${it.image}")
                .fit()
                .transform(transformation)
                .error(R.drawable.group_chate_boarder)
                .into(holder.rounded_image, object : com.squareup.picasso.Callback {
                    override fun onSuccess() {
                        holder.progressBar.visibility = View.GONE
                        Log.d("Picasso", "Image loaded successfully.")
                    }

                    override fun onError(e: Exception?) {
                        holder.progressBar.visibility = View.GONE
                        Log.e("PicassoError", "Image load failed: ${e?.message}")
                    }
                })

            holder.card.setOnClickListener {
                Log.d("S::S::S:S", "onBindViewHolder: ${movie.id}")
                val intent = Intent(context, GroupDetailActivity::class.java).apply {
                    putExtra("id", movie.id)
                    putExtra("group_id", movie.id?.toInt() ?: -1)
                    putExtra("position", position)
                }
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return splist?.size ?: 0
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var group_name: TextView = view.findViewById(R.id.group_name)
        var tv_sport: TextView = view.findViewById(R.id.tv_sport)
        var card: CardView = view.findViewById(R.id.card)
        var rounded_image: RoundedImageView = view.findViewById(R.id.round_image)
        var days_txt: TextView = view.findViewById<View>(R.id.days_txt) as TextView
        var progressBar: ProgressBar = view.findViewById(R.id.progressBar) // Add this to your XML
    }
}
