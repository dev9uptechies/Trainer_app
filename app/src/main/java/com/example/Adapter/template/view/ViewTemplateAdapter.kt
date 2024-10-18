package com.example.Adapter.template.view

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.model.performance_profile.PerformanceProfileData
import com.example.trainerapp.R

class ViewTemplateAdapter(
    private var splist: MutableList<PerformanceProfileData.PerformanceProfile>?,
    var context: Context,
//    val listener: OnItemClickListener.OnItemClickCallback
) :
    RecyclerView.Adapter<ViewTemplateAdapter.MyViewHolder>() {
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
//        holder.chart_performance.visibility = View.GONE
//        holder.add_performance.visibility = View.GONE
//        holder.del_performance.visibility = View.GONE
//        holder.edit_performance.visibility = View.GONE
//        holder.performance_name.visibility = View.GONE

        holder.mainContainer.removeAllViews()

        val categoryList = movie.performanceTemplateCategory ?: mutableListOf()
        Log.d("Category Size", "${categoryList.size}")

        if (categoryList.isNotEmpty()) {
            for (category in categoryList) {
                val categoryView =
                    LayoutInflater.from(context).inflate(R.layout.performance_item, null)
                val categoryNameTextView =
                    categoryView.findViewById<TextView>(R.id.performance_name)


                val main_layout: ConstraintLayout = categoryView.findViewById(R.id.mainLayout)
                val add_performance: ImageView = categoryView.findViewById(R.id.addPerformance)
                val del_performance: ImageView = categoryView.findViewById(R.id.deletePerformance)
                val edit_performance: ImageView = categoryView.findViewById(R.id.editPerformance)
                val chart_performance: ImageView = categoryView.findViewById(R.id.chartPerformance)

                add_performance.visibility = View.GONE
                del_performance.visibility = View.GONE
                edit_performance.visibility = View.GONE
                chart_performance.visibility = View.GONE

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

                        val margin = context.resources.getDimensionPixelSize(R.dimen._35sdp)
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
                            performance.coachScore?.toString() ?: ""  // Handle null values
                        athleteStar.text =
                            performance.atheletScore?.toString() ?: ""  // Handle null values
                        performanceLinearLayout.addView(performanceView)
                    }
                }

                // Add the whole category view to the main performanceDetailsLinear layout
                holder.mainContainer.addView(categoryView)
            }
        }


//        holder.performanceDetailsLinear.removeAllViews()
//        val list = movie.performanceTemplateCategory ?: mutableListOf()
//        Log.d("Postiton Database :-", "${list.size}")
//        if (list.size != 0) {
//            for (i in list) {
//                Log.d("Postiton Database :-", "${i.name}")
//                holder.performance_name.text = i.name ?: ""
//                val data = i.performanceTemplateQuality ?: mutableListOf()
//                if (data.isNotEmpty()) {
//                    holder.performanceDetailsLinear.visibility = View.VISIBLE
//                    for (j in data) {
//                        Log.d(
//                            "Postion Database :- ",
//                            "${j.id} 	 ${j.name} "
//                        )
//                        val addView =
//                            LayoutInflater.from(context).inflate(R.layout.performance_data, null)
//                        val title = addView.findViewById<TextView>(R.id.performance_data_name)
//                        val coach_star = addView.findViewById<TextView>(R.id.performance_coach_star)
//                        val athlete_star =
//                            addView.findViewById<TextView>(R.id.performance_athelete_star)
//
//                        val margin = context.resources.getDimensionPixelSize(R.dimen._25sdp)
//                        val margin2 = context.resources.getDimensionPixelSize(R.dimen._3sdp)
//                        val layoutParams = LinearLayout.LayoutParams(
//                            LinearLayout.LayoutParams.MATCH_PARENT, // width
//                            margin  // height (you can specify a pixel value too)
//                        )
//
//                        // Set margins (in pixels or you can convert dp to pixels)
//                        layoutParams.setMargins(
//                            0,
//                            0,
//                            0,
//                            margin2
//                        ) // left, top, right, bottom margins
//
//                        // Apply layout parameters to the view
//                        addView.layoutParams = layoutParams
//
//                        title.text = j.name
//                        coach_star.text = ""
//                        athlete_star.text = ""
//                        holder.performanceDetailsLinear.addView(addView)
//                    }
//                }
//            }
//        }

//        holder.performance_name.text = movie.name
//        holder.performanceDetailsLinear.removeAllViews()
//        val data = movie.performanceTemplateCategory ?: mutableListOf()
//        if (data.isNotEmpty()) {
//            holder.performanceDetailsLinear.visibility = View.VISIBLE
//            for (i in data) {
//                Log.d(
//                    "Postion Database :- ",
//                    "${i.id} 	 ${i.name} "
//                )
//                val addView = LayoutInflater.from(context).inflate(R.layout.performance_data, null)
//                val title = addView.findViewById<TextView>(R.id.performance_data_name)
//                val coach_star = addView.findViewById<TextView>(R.id.performance_coach_star)
//                val athlete_star = addView.findViewById<TextView>(R.id.performance_athelete_star)
//
//                val margin = context.resources.getDimensionPixelSize(R.dimen._25sdp)
//                val margin2 = context.resources.getDimensionPixelSize(R.dimen._3sdp)
//                val layoutParams = LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.MATCH_PARENT, // width
//                    margin  // height (you can specify a pixel value too)
//                )
//
//                // Set margins (in pixels or you can convert dp to pixels)
//                layoutParams.setMargins(0, 0, 0, margin2) // left, top, right, bottom margins
//
//                // Apply layout parameters to the view
//                addView.layoutParams = layoutParams
//
//                title.text = i.name
//                coach_star.text = ""
//                athlete_star.text = ""
//                holder.performanceDetailsLinear.addView(addView)
//            }
//        }
//        holder.add_performance.setOnClickListener(
//            OnItemClickListener(
//                position,
//                listener,
//                holder.bindingAdapterPosition.toLong(),
//                "add_program"
//            )
//        )
//        holder.del_performance.setOnClickListener(
//            OnItemClickListener(
//                position,
//                listener,
//                holder.bindingAdapterPosition.toLong(),
//                "del_program"
//            )
//        )
//        holder.edit_performance.setOnClickListener(
//            OnItemClickListener(
//                position,
//                listener,
//                holder.bindingAdapterPosition.toLong(),
//                "edit_program"
//            )
//        )
    }

    override fun getItemCount(): Int {
        return splist!!.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mainContainer: LinearLayout = view.findViewById(R.id.mainContainer)
    }
}
