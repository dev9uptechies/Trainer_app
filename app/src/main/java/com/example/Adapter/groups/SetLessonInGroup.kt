package com.example.Adapter.groups

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.LessonActivity
import com.example.model.newClass.lesson.Lesson
import com.example.trainerapp.R

class SetLessonInGroup(
    private var spList: List<Lesson.LessonDatabase>,
    var context: Context,
    val listener: Any
) : RecyclerView.Adapter<SetLessonInGroup.MyViewHolder>() {

    private var filterList: ArrayList<Lesson.LessonDatabase> = ArrayList(spList)
    private var selectedPosition = -1
    private val selectedItems: MutableSet<Int> = mutableSetOf()
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
        holder.editImage.visibility = View.GONE
        holder.checkBox.visibility = View.GONE
        holder.right_view.visibility = View.GONE


        val params = holder.card.layoutParams as ViewGroup.MarginLayoutParams
        params.marginStart = 10
        params.marginEnd = 10
        holder.card.layoutParams = params


        holder.tvFname.text = movie.name
        holder.tvTime1.text = movie.date
        holder.tv_athlet.text = "Time: " + movie.time

        holder.checkBox.isChecked = selectedItems.contains(position)
        holder.checkBox.isClickable = false

        holder.itemView.setOnClickListener {
            if (selectedItems.contains(position)) {
                selectedItems.remove(position)
            } else {
                selectedItems.add(position)
            }
            notifyItemChanged(position)
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

            context.startActivity(intent)
        }

    }


    override fun getItemCount(): Int {
        return filterList.size
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
        var card: CardView = view.findViewById(R.id.card)
        var right_view: LinearLayout = view.findViewById(R.id.right_view)

    }
}
