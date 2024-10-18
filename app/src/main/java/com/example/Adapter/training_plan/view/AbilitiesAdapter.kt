package com.example.Adapter.training_plan.view

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.model.training_plan.MicroCycle.AbilityData
import com.example.trainerapp.R

class AbilitiesAdapter(
    private var splist: MutableList<AbilityData>?,
    private val context: Context,
) : RecyclerView.Adapter<AbilitiesAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView = view.findViewById(R.id.tvAbilityName)
        var liner: LinearLayout = view.findViewById(R.id.liner_ability)
        var checkBox: CheckBox = view.findViewById(R.id.cbSelectAbility)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ability, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = splist?.get(position) ?: return

        holder.name.text = item.name
        holder.checkBox.isChecked = item.isSelected


        holder.liner.setOnClickListener {
            item.isSelected = !item.isSelected
            holder.checkBox.isChecked = item.isSelected
            holder.liner.isSelected = item.isSelected
            Log.d("Ability Toggled", "Id: ${item.id}, New isSelected: ${item.isSelected}")
            notifyItemChanged(position)
        }


        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            item.isSelected = isChecked
            holder.liner.isSelected = isChecked
            Log.d("Ability Toggled", "Id: ${item.id}, New isSelected: ${item.isSelected}")

        }
    }

    override fun getItemCount(): Int {
        return splist?.size ?: 0
    }
}
