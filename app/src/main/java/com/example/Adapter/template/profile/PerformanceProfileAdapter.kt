package com.example.Adapter.template.profile

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
import com.example.model.performance_profile.performance.category.PerformanceCategoryData
import com.example.model.performance_profile.performance.quality.PerformanceQualityData
import com.example.trainerapp.R
import com.zerobranch.layout.SwipeLayout

class PerformanceProfileAdapter(
    private var catList: MutableList<PerformanceCategoryData>? = null,
    private var qualityList: MutableList<PerformanceQualityData>? = null,
    var context: Context,
    val listener: OnItemClickListener.OnItemClickCallback,
    val qualityListener: OnQualityClick
) :
    RecyclerView.Adapter<PerformanceProfileAdapter.MyViewHolder>() {

    interface OnQualityClick {
        fun onQualityClick(position: Int, qualId: Long, type: String, catId: Long)
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
        val movie = catList!![position]
        holder.chart_performance.visibility = View.VISIBLE
        holder.performance_name.text = movie.name
        holder.performanceDetailsLinear.removeAllViews()

        val filteredQualities =
            qualityList!!.filter { it.performance_category_id == movie.id.toString() }

        if (filteredQualities.isNotEmpty()) {
            holder.performanceDetailsLinear.visibility = View.VISIBLE
            for (i in filteredQualities) {
                Log.d(
                    "Postion Database :- ",
                    "${i.id} 	 ${i.name} 	 ${i.athelet_score} 	 ${i.coach_score}"
                )
                val addView = LayoutInflater.from(context).inflate(R.layout.performance_data, null)
                val swipe = addView.findViewById<SwipeLayout>(R.id.swipe_layout)
                val title = addView.findViewById<TextView>(R.id.performance_data_name)
                val coach_star = addView.findViewById<TextView>(R.id.performance_coach_star)
                val athlete_star = addView.findViewById<TextView>(R.id.performance_athelete_star)
                val edit = addView.findViewById<ImageView>(R.id.img_edit)
                val delete = addView.findViewById<ImageView>(R.id.img_delete)

                val margin = context.resources.getDimensionPixelSize(R.dimen._35sdp)
                val margin2 = context.resources.getDimensionPixelSize(R.dimen._3sdp)
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, // width
                    margin  // height (you can specify a pixel value too)
                )

                layoutParams.setMargins(0, 0, 0, margin2) // left, top, right, bottom margins

                addView.layoutParams = layoutParams

                title.text = i.name
                coach_star.text = i.coach_score ?: "0"
                athlete_star.text = i.athelet_score ?: "0"

                edit.setOnClickListener {
                    swipe.close()
                    qualityListener.onQualityClick(
                        position,
                        i.id!!.toLong(),
                        "edit_quality",
                        movie.id!!.toLong()
                    )
                }

                delete.setOnClickListener {
                    swipe.close()
                    qualityListener.onQualityClick(
                        position,
                        i.id!!.toLong(),
                        "delete_quality",
                        movie.id!!.toLong()
                    )
                }

                holder.performanceDetailsLinear.addView(addView)
            }
        }
        holder.add_performance.setOnClickListener(
            OnItemClickListener(
                position,
                listener,
                movie.id!!.toLong(),
                "add_program"
            )
        )

        holder.chart_performance.setOnClickListener(
            OnItemClickListener(
                position,
                listener,
                movie.id.toLong(),
                "view_chart"
            )
        )
        holder.del_performance.setOnClickListener(
            OnItemClickListener(
                position,
                listener,
                movie.id.toLong(),
                "del_program"
            )
        )
        holder.edit_performance.setOnClickListener(
            OnItemClickListener(
                position,
                listener,
                movie.id.toLong(),
                "edit_program"
            )
        )
    }

    override fun getItemCount(): Int {
        return catList!!.size
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
