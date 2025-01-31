package com.example.Adapter.groups

import android.annotation.SuppressLint
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
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.Adapter.training_plan.VewTrainingPlanAdapter
import com.example.LessonActivity
import com.example.OnItemClickListener
import com.example.model.training_plan.TrainingPlanData
import com.example.trainerapp.ApiClass.EventListData
import com.example.trainerapp.R
import com.example.trainerapp.training_plan.EditTrainingPlanActivity
import com.example.trainerapp.training_plan.ViewTrainingPlanActivity
import com.zerobranch.layout.SwipeLayout
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class GetPlanningListAdapterGroup(
    private var splist: MutableList<TrainingPlanData.TrainingPlan>?,
    var context: Context,
    val listener: OnItemClickListener.OnItemClickCallback,
    val mainId: Int? = null
) : RecyclerView.Adapter<GetPlanningListAdapterGroup.MyViewHolder>() {

    private val selectedItems: MutableSet<Int> = mutableSetOf() // Store selected item positions
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
        var editImage2: ImageView = view.findViewById(R.id.img_edit2)
        val swipe = view.findViewById<SwipeLayout>(R.id.swipe_layout)
        var cardView: CardView = view.findViewById<View>(R.id.rela_dragged) as CardView
        var img_delete: ImageView = view.findViewById(R.id.img_delete)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val movie = splist?.get(position) ?: return

        holder.editImage.visibility = View.GONE

        holder.tvFname.text = movie.name
        holder.tvgoal.text = "Start: ${formatDate(movie.start_date)}"
        holder.tv_athlet.text = "End: ${formatDate(movie.competition_date)}"

        holder.checkBox.isChecked = selectedItems.contains(position)
        holder.checkBox.isClickable = false

        holder.cardView.setOnClickListener {
            if (selectedItems.contains(position)) {
                selectedItems.remove(position)
            } else {
                selectedItems.add(position)
            }
            notifyItemChanged(position)

        }

        holder.editImage2.setOnClickListener {
            Log.d("LLDLDLDLD", "onBindViewHolder: OK")

            holder.swipe.close()


            val intent = Intent(context, EditTrainingPlanActivity::class.java).apply {
                putExtra("PlanningIdGroup", movie.id)
            }

            context.startActivity(intent)
        }

        holder.img_delete.setOnClickListener {
            holder.swipe.close(true)
            listener.onItemClicked(it, position, movie.id!!.toLong(), "DeletePlanning")
        }

    }


    fun getSelectedPlanningData(): List<Int> {
        // Make sure splist is not null before accessing
        return if (!splist.isNullOrEmpty()) {
            selectedItems.map { splist!![it].id ?: 0 }
        } else {
            emptyList() // Return empty list if splist is null or empty
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