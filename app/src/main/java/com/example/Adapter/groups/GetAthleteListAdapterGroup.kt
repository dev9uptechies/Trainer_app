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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.Adapter.training_plan.VewTrainingPlanAdapter
import com.example.OnItemClickListener
import com.example.model.newClass.athlete.AthleteData
import com.example.model.training_plan.TrainingPlanData
import com.example.trainerapp.ApiClass.EventListData
import com.example.trainerapp.R
import com.example.trainerapp.training_plan.ViewTrainingPlanActivity
import com.zerobranch.layout.SwipeLayout
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class GetAthleteListAdapterGroup(
    private var splist: MutableList<AthleteData.Athlete>?,
    var context: Context,
    val listener: OnItemClickListener.OnItemClickCallback,
    val mainId: Int? = null,
    private var selectedAthletes: MutableSet<Int> = mutableSetOf()
) : RecyclerView.Adapter<GetAthleteListAdapterGroup.MyViewHolder>() {

    private val selectedItems: MutableSet<Int> = mutableSetOf()
    private var filterList: ArrayList<AthleteData.Athlete> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.athlete_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    init {
        // If you have the list of selected athlete IDs (from the lesson attendance response)
        // you can initialize the selectedItems set with those IDs
        selectedItems.addAll(selectedAthletes)
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvFname: TextView = view.findViewById<View>(R.id.tv_program_name) as TextView
        var checkBox: CheckBox = view.findViewById(R.id.myCheckBox) }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val athlete = splist?.get(position) ?: return

        holder.tvFname.text = athlete.name
        holder.checkBox.visibility = View.VISIBLE

        Log.d("DKDKDKDKDKk", "onBindViewHolder: $selectedAthletes")

        // Set the checkbox based on whether the athlete's ID is in selectedItems
        holder.checkBox.isChecked = selectedItems.contains(athlete.id)

        holder.checkBox.isClickable = false  // Disabling checkbox click since you aren't handling it here

        holder.itemView.setOnClickListener {
            // Safely handle athlete.id (nullable) and add/remove it from selectedItems
            athlete.id?.let {
                if (selectedItems.contains(it)) {
                    selectedItems.remove(it)  // Remove if it exists
                } else {
                    selectedItems.add(it)  // Add if it doesn't exist
                }
            }
            // Also update selectedAthletes to keep the set in sync
            selectedAthletes.clear()
            selectedAthletes.addAll(selectedItems)
            notifyItemChanged(position)
        }
    }

    fun getSelectedAthleteData(): List<Int> {
        // Ensure selectedItems has the correct athlete IDs
        return selectedItems.toList()  // Convert the selectedItems set to a list and return
    }



    override fun getItemCount(): Int {
        return splist?.size ?: 0
    }
}