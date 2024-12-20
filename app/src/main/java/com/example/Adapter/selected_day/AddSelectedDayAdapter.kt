package com.example.Adapter.selected_day

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.Create_Event_Activity
import com.example.LessonActivity
import com.example.OnItemClickListener
import com.example.model.newClass.lesson.Lesson
import com.example.trainerapp.R
import com.zerobranch.layout.SwipeLayout

class AddSelectedDayAdapter(
    private var spList: ArrayList<Lesson.LessonDatabase>,
    var context: Context,
    val listener: OnItemClickListener.OnItemClickCallback
) : RecyclerView.Adapter<AddSelectedDayAdapter.MyViewHolder>() {

    private var filterList: ArrayList<Lesson.LessonDatabase> = ArrayList(spList)
    private var selectedPosition = -1
    private var selectedGroupId: String? = null
    private var selectedDate: String? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list_selected_day, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val movie = filterList[position]
        holder.tv_unit.visibility = View.GONE
        holder.tv_athlet.visibility = View.VISIBLE
        holder.checkBox.visibility = View.VISIBLE

        holder.tvFname.text = movie.name
        holder.tvTime1.text = movie.date
        holder.tv_athlet.text = "Time: " + movie.time

        holder.checkBox.isChecked = position == selectedPosition
        holder.checkBox.isClickable = false

        holder.itemView.setOnClickListener {
            val currentPosition = holder.adapterPosition
            if (selectedPosition != currentPosition) {
                val previousSelectedPosition = selectedPosition
                selectedPosition = currentPosition
                selectedGroupId = movie.id.toString()
                selectedDate = movie.date // Store the selected date


                holder.itemView.post {
                    notifyItemChanged(previousSelectedPosition)
                    notifyItemChanged(selectedPosition)
                }
            }
        }

        holder.editImage.setOnClickListener {

            val lessonNames = arrayListOf<String>()
            movie.lesson_goal?.forEach { athlete ->
                lessonNames.add(athlete.goal?.name ?: "Unknown")
            }

            val lessonIdList = arrayListOf<Int>()

            movie.lesson_goal?.forEach { athlete ->
                lessonIdList.add(athlete.goal?.id ?: 0)
            }

            val intent = Intent(context, LessonActivity::class.java).apply {
                putExtra("id", movie.id)
                putExtra("name", movie.name)
                putExtra("goal", lessonNames)
                putExtra("goalid", arrayOf(lessonIdList))
                putExtra("date", movie.date)
                putExtra("AvailableTime", movie.time)
                putExtra("PortialTime", movie.section_time)
                putExtra("fromday", true)
                putExtra("lesson", lessonNames)
                putExtra("lessonId", lessonIdList)
            }

            // Start the activity
            context.startActivity(intent)
        }

    }

    fun getSelectedLessonData(): Pair<String?, String?> {
        return Pair(selectedGroupId, selectedDate)
    }



    override fun getItemCount(): Int {
        return filterList.size
    }

    fun filter(query: String) {
        filterList = if (query.isEmpty()) {
            ArrayList(spList)
        } else {
            spList.filter { it.name!!.contains(query, ignoreCase = true) } as ArrayList<Lesson.LessonDatabase>
        }
        notifyDataSetChanged()
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tv_unit: TextView = view.findViewById(R.id.tv_unit)
        var tvgoal: TextView = view.findViewById(R.id.tvgoal)
        var tv_athlet: TextView = view.findViewById(R.id.tv_athlet)
        var tvFname: TextView = view.findViewById(R.id.tvFname)
        var click: LinearLayout = view.findViewById(R.id.click)
        var tvTime1: TextView = view.findViewById(R.id.tv_date)
        var checkBox: CheckBox = view.findViewById(R.id.myCheckBox)
        var editImage: ImageView = view.findViewById(R.id.img_edit)
        var favimage: ImageView = view.findViewById(R.id.img_edit)
    }
}
