package com.example.Adapter.template.edit_template

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.OnItemClickListener
import com.example.model.base_class.PerformanceProfileBase
import com.example.model.performance_profile.PerformanceProfileData
import com.example.trainerapp.R
import com.zerobranch.layout.SwipeLayout

class EditTemplateAdapter(
    private var splist: MutableList<PerformanceProfileBase>?,
//    private var catList: MutableList<PerformanceCategoryData>? = null,
//    private var qualityList: MutableList<PerformanceQualityData>? = null,
    var context: Context,
    var qulityIds: List<Int>,
    val listener: OnItemClickListener.OnItemClickCallback,
    val qualityListener: OnQualityClick
) :
    RecyclerView.Adapter<EditTemplateAdapter.MyViewHolder>() {

    interface OnQualityClick {
        fun onQualityClick(
            position: Int,
            catPosition: Int? = null,
            qualId: Long,
            type: String,
            catId: Long
        )
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.performance_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val movie = splist!![position]
        holder.chart_performance.visibility = View.GONE
        holder.performance_name.text = movie.titleName
        holder.performanceDetailsLinear.removeAllViews()
        val data = movie.subTitleName ?: mutableListOf()
        if (data.isNotEmpty()) {
            holder.performanceDetailsLinear.visibility = View.VISIBLE

            data.forEachIndexed { qualityPosition, quality ->
                Log.d("Quality Data :-", "${quality.id} ${quality.title} ${quality.athleteStar} ${quality.coachStar}")

                val addView = LayoutInflater.from(context).inflate(R.layout.performance_data, null)
                val swipe = addView.findViewById<SwipeLayout>(R.id.swipe_layout)
                val title = addView.findViewById<TextView>(R.id.performance_data_name)
                val coach_star = addView.findViewById<TextView>(R.id.performance_coach_star)
                val athlete_star = addView.findViewById<TextView>(R.id.performance_athelete_star)
                val edit = addView.findViewById<ImageView>(R.id.img_edit)
                val delete = addView.findViewById<ImageView>(R.id.img_delete)

                title.text = quality.title
                coach_star.text = quality.coachStar ?: ""
                athlete_star.text = quality.athleteStar ?: ""

                Log.d("SSLSLS", "onBindViewHolder: $quality")

                edit.setOnClickListener {
                    swipe.close()
                    qualityListener.onQualityClick(
                        position = qualityPosition,
                        catPosition = position,
                        qualId = if (qulityIds.contains(quality.id)) quality.id?.toLong() ?: 0 else 0,
                        type = "edit_quality",
                        catId = movie.id!!.toLong()
                    )
                }

                delete.setOnClickListener {
                    swipe.close()
                    Log.d("XRXRRRX", "onBindViewHolder: ${quality.id}----  ${quality.title}")
                    qualityListener.onQualityClick(
                        position = qualityPosition,
                        catPosition = position,
                        qualId = quality.id?.toLong() ?: 0,
                        type = "delete_quality",
                        catId = movie.id!!.toLong()
                    )
                }

                holder.performanceDetailsLinear.addView(addView)
            }

        }
        holder.add_performance.setOnClickListener(
            OnItemClickListener(
                position,
                listener,
                holder.bindingAdapterPosition.toLong(),
                "add_program"
            )
        )
        holder.del_performance.setOnClickListener(
            OnItemClickListener(
                position,
                listener,
                holder.bindingAdapterPosition.toLong(),
                "del_program"
            )
        )
        holder.edit_performance.setOnClickListener(
            OnItemClickListener(
                position,
                listener,
                holder.bindingAdapterPosition.toLong(),
                "edit_program"
            )
        )
    }

    override fun getItemCount(): Int {
        return splist!!.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var performance_name: TextView = view.findViewById<View>(R.id.performance_name) as TextView
        var add_performance: ImageView = view.findViewById(R.id.addPerformance)
        var del_performance: ImageView = view.findViewById(R.id.deletePerformance)
        var edit_performance: ImageView = view.findViewById(R.id.editPerformance)
        var chart_performance: ImageView = view.findViewById(R.id.chartPerformance)
        var performanceDetailsLinear: LinearLayout =
            view.findViewById(R.id.performanceDetailsLinear)
    }
}
