package com.example.Adapter.EditGroup
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
import com.example.GroupListData
import com.example.LessonActivity
import com.example.model.newClass.lesson.Lesson
import com.example.trainerapp.R

class SetEditGroupLessonAdapter(
    private var spList: List<GroupListData.GroupLesson>,
    var context: Context,
    val listener: Any
) : RecyclerView.Adapter<SetEditGroupLessonAdapter.MyViewHolder>() {

    private var filterList: ArrayList<GroupListData.GroupLesson> = ArrayList(spList)
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
        val lesson = movie.lession

        holder.tv_unit.visibility = View.GONE
        holder.tv_athlet.visibility = View.VISIBLE
        holder.editImage.visibility = View.GONE
        holder.checkBox.visibility = View.GONE

        val params = holder.card.layoutParams as ViewGroup.MarginLayoutParams
        params.marginStart = 10
        params.marginEnd = 10
        holder.card.layoutParams = params

        // Null-safe access for lesson properties
        holder.tvFname.text = lesson?.name ?: ""
        holder.tvTime1.text = lesson?.date ?: ""
        holder.tv_athlet.text = "Time: ${lesson?.time ?: ""}"

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
            val lessonNames = lesson?.lesson_goal?.map { it.goal?.name ?: "Unknown" } ?: emptyList()
            val lessonIdList = lesson?.lesson_goal?.map { it.goal?.id ?: 0 } ?: emptyList()

            // You can now use lessonNames and lessonIdList safely
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
    }
}
