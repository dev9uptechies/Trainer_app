package com.example.Adapter.training_plan.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.OnItemClickListener
import com.example.model.training_plan.MicroCycle.GetMicrocycle
import com.example.trainerapp.R
import com.rtugeek.android.colorseekbar.ColorSeekBar
import com.rtugeek.android.colorseekbar.thumb.DefaultThumbDrawer
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ViewMicrocycleListAdapter(
    private var splist: MutableList<GetMicrocycle.Data>?,
    var context: Context,
    val listener: OnItemClickListener.OnItemClickCallback,
    val type: String? = null,
    val mainId: Int? = null
) : RecyclerView.Adapter<ViewMicrocycleListAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_microcycle_list, parent, false)
        return MyViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = splist?.get(position) ?: return
        holder.progress.isEnabled = false
        holder.name.text = item.name
        holder.start_date.text = formatDate(item.startDate)
        holder.end_date.text = formatDate(item.endDate)
        holder.progress.progress = item.workload ?: 0


        holder.colorSeekBar.maxProgress = 1000
        holder.colorSeekBar.progress = 50
        holder.colorSeekBar.borderColor = Color.BLACK
        holder.colorSeekBar.borderRadius = 10
        holder.colorSeekBar.borderSize = 10
        holder. colorSeekBar.thumbDrawer = DefaultThumbDrawer(25,Color.WHITE,Color.BLUE)
        holder. colorSeekBar.isVertical = false
        holder. colorSeekBar.barHeight = 10
        holder. colorSeekBar.color = Color.BLACK
        holder. colorSeekBar.isEnabled =false


        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            intArrayOf(
                Color.parseColor("#FFFFFF"), // White
                Color.parseColor("#00FF00"), // Green
                Color.parseColor("#FFFF00"), // Yellow
                Color.parseColor("#FFA500"), // Orange
                Color.parseColor("#FF4500"), // Red-Orange
                Color.parseColor("#FF0000")  // Red
            )
        )
        gradientDrawable.cornerRadius = 8f
        holder.progress.progressDrawable = gradientDrawable

        gradientDrawable.gradientType = GradientDrawable.LINEAR_GRADIENT
        gradientDrawable.cornerRadius = 8f

        holder.progress.progressDrawable = gradientDrawable
        holder.progress.isEnabled = false


    }

    override fun getItemCount(): Int {
        return splist?.size ?: 0
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView = view.findViewById(R.id.training_name_one)
        var start_date: TextView = view.findViewById(R.id.start_date_one)
        var end_date: TextView = view.findViewById(R.id.end_date_one)
        var progress: SeekBar = view.findViewById(R.id.seekbar_workload)
        var colorSeekBar: ColorSeekBar = view.findViewById(R.id.colorSeekBar)
        var card: CardView = view.findViewById(R.id.card_one)

    }

    @SuppressLint("NewApi")
    private fun formatDate(dateString: String?): String {
        return if (!dateString.isNullOrEmpty()) {
            val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val outputFormatter = DateTimeFormatter.ofPattern("dd MMM, yyyy")
            val date = LocalDate.parse(dateString, inputFormatter)
            date.format(outputFormatter)
        } else {
            "N/A"
        }
    }

}