package com.example.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.trainerapp.R

class ReminderAdapter(
    context: Context,
    private val reminderList: MutableList<String>,
    private val onDeleteClick: (Int) -> Unit,
    private val onEditClick: (Int) -> Unit  // ✅ New Edit Click Callback
) : ArrayAdapter<String>(context, R.layout.item_remind_me_list, reminderList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_remind_me_list, parent, false)

        val nameTextView = view.findViewById<TextView>(R.id.tvFname)
        val dateTextView = view.findViewById<TextView>(R.id.tvgoal)
        val imgDelete = view.findViewById<ImageView>(R.id.img_delete)
        val imgEdit = view.findViewById<ImageView>(R.id.img_edit2) // ✅ Ensure this ID exists in XML

        val reminder = reminderList[position].split(" - ")
        if (reminder.size == 2) {
            nameTextView.text = reminder[0]
            dateTextView.text = reminder[1]
        }

        imgDelete.setOnClickListener { onDeleteClick(position) }  // ✅ Delete Action
        imgEdit.setOnClickListener { onEditClick(position) }  // ✅ Edit Action

        return view
    }
}
