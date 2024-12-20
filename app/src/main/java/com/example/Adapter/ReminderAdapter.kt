package com.example.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.trainerapp.R

class ReminderAdapter(context: Context, private val reminderList: List<String>) :
    ArrayAdapter<String>(context, R.layout.viewpersonaldiarylist, reminderList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = LayoutInflater.from(context).inflate(R.layout.viewpersonaldiarylist, parent, false)

        // Get the views for the name and the date
        val nameTextView = view.findViewById<TextView>(R.id.training_name_one)
        val dateTextView = view.findViewById<TextView>(R.id.start_date_one)

        // Split the combined reminder string into name and date (assuming "name - date" format)
        val reminder = reminderList[position].split(" - ")
        if (reminder.size == 2) {
            nameTextView.text = reminder[0] // The name part
            dateTextView.text = reminder[1] // The date part
        }

        return view
    }
}
