package com.example.Adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.GroupListData
import com.example.OnItemClickListener
import com.example.trainerapp.R
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_select_grope, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val group = splist!![position]
        holder.group_name.text = group.name

        val transformation: Transformation = RoundedTransformationBuilder()
            .borderColor(Color.BLACK)
            .borderWidthDp(1f)
            .cornerRadiusDp(10f)
            .oval(false)
            .build()

        Picasso.get()
            .load("https://4trainersapp.com" + group.image)
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

    override fun getItemCount(): Int {
        return splist!!.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var group_name: TextView = view.findViewById(R.id.start_date_one)
        var checkBox: CheckBox = view.findViewById(R.id.myCheckBox)
        var rounded_image: RoundedImageView = view.findViewById(R.id.roundedImageView)
    }
}
