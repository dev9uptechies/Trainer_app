package com.example.Adapter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.GroupListData
import com.example.OnItemClickListener
import com.example.trainerapp.R
import com.example.trainerappAthlete.model.GroupListAthlete
import com.makeramen.roundedimageview.RoundedImageView
import com.makeramen.roundedimageview.RoundedTransformationBuilder
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation

class selectGroupAdapter(
    private var splist: ArrayList<GroupListData.groupData>?,
    var context: Context,
    val listener: OnItemClickListener.OnItemClickCallback
) : RecyclerView.Adapter<selectGroupAdapter.MyViewHolder>() {

    private var selectedPosition = -1
    private var selectedGroupId: String? = null
    private lateinit var PreSeason: GroupListData.groupData


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_select_grope, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val group = splist!![position]
        holder.group_name.text = group.name
        PreSeason = group

        val transformation: Transformation = RoundedTransformationBuilder()
            .borderColor(Color.BLACK)
            .borderWidthDp(1f)
            .cornerRadiusDp(10f)
            .oval(false)
            .build()

        Picasso.get()
            .load("https://uat.4trainersapp.com" + group.image)
            .fit()
            .transform(transformation)
            .into(holder.rounded_image)

        holder.checkBox.isChecked = position == selectedPosition
        holder.checkBox.isClickable = false

        holder.itemView.setOnClickListener {
            val currentPosition = holder.adapterPosition
            if (selectedPosition != currentPosition) {
                val previousSelectedPosition = selectedPosition
                selectedPosition = currentPosition
                selectedGroupId = group.id.toString()

                holder.itemView.post {
                    notifyItemChanged(previousSelectedPosition)
                    notifyItemChanged(selectedPosition)
                }
            }
        }

    }

    fun getSelectedGroupId(): String? {
        return selectedGroupId
    }


    fun getSelectedGroupData(): Map<String, String?> {
        val groupPlanning = PreSeason.group_plannings?.getOrNull(0)?.planning?.pre_season
        val mesocycles = groupPlanning?.mesocycles
        val microcycles = mesocycles?.getOrNull(0)?.microcycles

        return mapOf(
            "name" to groupPlanning?.name,
            "start_date" to groupPlanning?.start_date,
            "end_date" to groupPlanning?.end_date,
            "mesocycle" to groupPlanning?.mesocycle,
            "workload_color" to microcycles?.getOrNull(0)?.workloadColor
        )
    }

    fun getSelectedPreCompetitiveGroupData(): Map<String, String?> {
        val groupPlanning = PreSeason.group_plannings?.getOrNull(0)?.planning?.pre_competitive
        val mesocycles = groupPlanning?.mesocycles
        val microcycles = mesocycles?.getOrNull(0)?.microcycles

        return mapOf(
            "PreCompetitivename" to groupPlanning?.name,
            "PreCompetitivestart_date" to groupPlanning?.start_date,
            "PreCompetitiveend_date" to groupPlanning?.end_date,
            "PreCompetitivemesocycle" to groupPlanning?.mesocycle,
            "PreCompetitiveworkload_color" to microcycles?.getOrNull(0)?.workloadColor
        )
    }

    fun getSelectedCompetitiveGroupData(): Map<String, String?> {
        val groupPlanning = PreSeason.group_plannings?.getOrNull(0)?.planning?.competitive
        val mesocycles = groupPlanning?.mesocycles
        val microcycles = mesocycles?.getOrNull(0)?.microcycles

        return mapOf(
            "Competitivename" to groupPlanning?.name,
            "Competitivestart_date" to groupPlanning?.start_date,
            "Competitiveend_date" to groupPlanning?.end_date,
            "Competitivemesocycle" to groupPlanning?.mesocycle,
            "Competitiveworkload_color" to microcycles?.getOrNull(0)?.workloadColor
        )
    }

//    fun getSelectedTransitionGroupData(): Map<String, String?> {
//        val groupPlanning = PreSeason.group?.group_plannings?.getOrNull(0)?.planning?.transition
//        val mesocycles = groupPlanning?.mesocycles
//        val microcycles = mesocycles?.getOrNull(0)?.microcycles
//
//        Log.d("AXAXAXAXAX", "getSelectedTransitionGroupData: ${groupPlanning}")
//
//        return mapOf(
//            "Transitionname" to groupPlanning?.name,
//            "Transitionstart_date" to groupPlanning?.start_date,
//            "Transitionend_date" to groupPlanning?.end_date,
//            "Transitionmesocycle" to groupPlanning?.mesocycle,
//            "Transitionworkload_color" to microcycles?.getOrNull(0)?.workloadColor
//        )
//    }

    override fun getItemCount(): Int {
        return splist!!.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var group_name: TextView = view.findViewById(R.id.start_date_one)
        var checkBox: CheckBox = view.findViewById(R.id.myCheckBox)
        var rounded_image: RoundedImageView = view.findViewById(R.id.roundedImageView)
    }
}
