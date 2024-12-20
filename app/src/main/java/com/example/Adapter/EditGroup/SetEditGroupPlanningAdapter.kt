package com.example.Adapter.EditGroup
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.GroupListData
import com.example.OnItemClickListener
import com.example.model.training_plan.TrainingPlanData
import com.example.trainerapp.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SetEditGroupPlanningAdapter(
    private var splist: MutableList<GroupListData.GroupPlanning>?,
    var context: Context,
    val listener: OnItemClickListener.OnItemClickCallback,
    val mainId: Int? = null
) : RecyclerView.Adapter<SetEditGroupPlanningAdapter.MyViewHolder>() {

    private val selectedItems: MutableSet<Int> = mutableSetOf()
    private var filterList: ArrayList<TrainingPlanData.TrainingPlan> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list_selected_day, parent, false)
        return MyViewHolder(itemView)
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tv_unit: TextView = view.findViewById<View>(R.id.tv_unit) as TextView
        var tvgoal: TextView = view.findViewById<View>(R.id.tvgoal) as TextView
        var tv_athlet: TextView = view.findViewById<View>(R.id.tv_athlet) as TextView
        var tvFname: TextView = view.findViewById<View>(R.id.tvFname) as TextView
        var click: LinearLayout = view.findViewById<View>(R.id.click) as LinearLayout
        var tvDate: TextView = view.findViewById(R.id.tv_date)
        var checkBox: CheckBox = view.findViewById(R.id.myCheckBox)
        var editImage: ImageView = view.findViewById(R.id.img_edit)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val movie = splist?.get(position) ?: return

        holder.editImage.visibility = View.GONE
        holder.checkBox.visibility = View.GONE

        Log.d("SetEditGroupPlanningAdapter", "onBindViewHolder: Binding data for position $position")

        // Safely access and set the text fields
        holder.tvFname.text = movie.planning?.name ?: ""

        val startDate = movie.planning?.start_date
        holder.tvgoal.text = if (startDate != null) {
            "Start: ${formatDate(startDate)}"
        } else {
            "Start: N/A"
        }

        val endDate = movie.planning?.competition_date
        holder.tv_athlet.text = if (endDate != null) {
            "End: ${formatDate(endDate)}"
        } else {
            "End: N/A"
        }

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
    }

    override fun getItemCount(): Int {
        return splist?.size ?: 0
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