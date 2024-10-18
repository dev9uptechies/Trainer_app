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
import com.example.model.performance_profile.PerformanceProfileData
import com.example.trainerapp.R

class EditTemplateAdapter(
    private var splist: MutableList<PerformanceProfileData.PerformanceProfile>?,
    var context: Context,
//    val listener: OnItemClickListener.OnItemClickCallback
) :
    RecyclerView.Adapter<EditTemplateAdapter.MyViewHolder>() {
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

        holder.mainContainer.removeAllViews()

        val categoryList = movie.performanceTemplateCategory ?: mutableListOf()
        Log.d("Category Size", "${categoryList.size}")

        if (categoryList.isNotEmpty()) {
            for (category in categoryList) {
                val categoryView =
                    LayoutInflater.from(context).inflate(R.layout.performance_item, null)
                val categoryNameTextView =
                    categoryView.findViewById<TextView>(R.id.performance_name)

                val add_performance: ImageView = categoryView.findViewById(R.id.addPerformance)
                val del_performance: ImageView = categoryView.findViewById(R.id.deletePerformance)
                val edit_performance: ImageView = categoryView.findViewById(R.id.editPerformance)
                val chart_performance: ImageView = categoryView.findViewById(R.id.chartPerformance)

                add_performance.visibility = View.VISIBLE
                del_performance.visibility = View.VISIBLE
                edit_performance.visibility = View.VISIBLE
                chart_performance.visibility = View.VISIBLE

                val topMargin = context.resources.getDimensionPixelSize(R.dimen._10sdp)
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, topMargin, 0, 0) // Set top margin
                }
                categoryView.layoutParams = layoutParams

                categoryNameTextView.text = category.name ?: ""
                val performanceList = category.performanceTemplateQuality ?: mutableListOf()
                val performanceLinearLayout =
                    categoryView.findViewById<LinearLayout>(R.id.performanceDetailsLinear)
                if (performanceList.isNotEmpty()) {
                    for (performance in performanceList) {
                        val performanceView =
                            LayoutInflater.from(context).inflate(R.layout.performance_data, null)
                        val title =
                            performanceView.findViewById<TextView>(R.id.performance_data_name)
                        val coachStar =
                            performanceView.findViewById<TextView>(R.id.performance_coach_star)
                        val athleteStar =
                            performanceView.findViewById<TextView>(R.id.performance_athelete_star)

                        val margin = context.resources.getDimensionPixelSize(R.dimen._25sdp)
                        val margin2 = context.resources.getDimensionPixelSize(R.dimen._3sdp)

                        val layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, // width
                            margin  // height (you can specify a pixel value too)
                        )

                        layoutParams.setMargins(
                            0,
                            0,
                            0,
                            margin2
                        )

                        performanceView.layoutParams = layoutParams
                        title.text = performance.name
                        coachStar.text =
                            performance.coachScore?.toString() ?: ""
                        athleteStar.text =
                            performance.atheletScore?.toString() ?: ""
                        performanceLinearLayout.addView(performanceView)
                    }
                }
                holder.mainContainer.addView(categoryView)
            }
        }
    }

    override fun getItemCount(): Int {
        return splist!!.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mainContainer: LinearLayout = view.findViewById(R.id.mainContainer)
    }
}
