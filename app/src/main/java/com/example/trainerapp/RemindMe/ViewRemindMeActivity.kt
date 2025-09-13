package com.example.trainerapp.RemindMe

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.Adapter.ReminderAdapter
import com.example.trainerapp.R
import com.example.trainerapp.databinding.ActivityViewRemindMeBinding

class ViewRemindMeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewRemindMeBinding
    private val sharedPreferences by lazy {
        getSharedPreferences("RemindMePrefs", MODE_PRIVATE)
    }
    private val reminderList = mutableListOf<String>()
    private lateinit var adapter: ReminderAdapter // Define adapter globally

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewRemindMeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize adapter with delete functionality
        adapter = ReminderAdapter(this, reminderList,
            { position -> deleteReminder(position) },  // ✅ Delete Callback
            { position -> editReminder(position) }  // ✅ Edit Callback
        )
        binding.reminderListView.adapter = adapter

        binding.reminderListView.adapter = adapter

        ButtonClick()
        getSavedReminders()
    }

    private fun ButtonClick() {
        binding.back.setOnClickListener { finish() }
        binding.add.setOnClickListener { AddRemindMe() }
    }

    private fun AddRemindMe() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_add_remind_me)

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = (displayMetrics.widthPixels * 0.9f).toInt()
        dialog.window!!.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val editText = dialog.findViewById<EditText>(R.id.edt_name)
        val editText2 = dialog.findViewById<EditText>(R.id.tv_pause_cycle)
        val saveButton = dialog.findViewById<CardView>(R.id.card_add)
        val cancelButton = dialog.findViewById<CardView>(R.id.crad_cancel)

        cancelButton.setOnClickListener { dialog.cancel() }
        saveButton.setOnClickListener {
            val inputText = editText.text.toString().trim()
            val inputText2 = editText2.text.toString().trim()

            if (inputText.isNotEmpty() && inputText2.isNotEmpty()) {
                val combinedReminder = "$inputText - $inputText2"
                reminderList.add(combinedReminder)
                saveReminders()
                getSavedReminders()

                Toast.makeText(this, "Reminder saved!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Please enter valid reminders", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun editReminder(position: Int) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_add_remind_me)

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = (displayMetrics.widthPixels * 0.9f).toInt()
        dialog.window!!.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val editText = dialog.findViewById<EditText>(R.id.edt_name)
        val editText2 = dialog.findViewById<EditText>(R.id.tv_pause_cycle)
        val saveButton = dialog.findViewById<CardView>(R.id.card_add)
        val cancelButton = dialog.findViewById<CardView>(R.id.crad_cancel)

        // ✅ Pre-fill the edit texts with the selected reminder details
        val selectedReminder = reminderList[position].split(" - ")
        if (selectedReminder.size == 2) {
            editText.setText(selectedReminder[0])  // Set Name
            editText2.setText(selectedReminder[1])  // Set Date
        }

        cancelButton.setOnClickListener { dialog.dismiss() }

        saveButton.setOnClickListener {
            val newName = editText.text.toString().trim()
            val newDate = editText2.text.toString().trim()

            if (newName.isNotEmpty() && newDate.isNotEmpty()) {
                // ✅ Update the existing reminder
                reminderList[position] = "$newName - $newDate"
                saveReminders()
                getSavedReminders()

                Toast.makeText(this, "Reminder updated!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Please enter valid data", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun getSavedReminders() {
        val savedSet = sharedPreferences.getStringSet("RemindMeDataList", null)

        if (!savedSet.isNullOrEmpty()) {
            reminderList.clear()
            reminderList.addAll(savedSet)
            adapter.notifyDataSetChanged()

            binding.reminderListView.visibility = View.VISIBLE
            binding.tvNodata.visibility = View.GONE
        } else {
            binding.reminderListView.visibility = View.GONE
            binding.tvNodata.visibility = View.VISIBLE
        }
    }

    private fun deleteReminder(position: Int) {
        if (position < reminderList.size) {
            reminderList.removeAt(position)
            saveReminders()
            getSavedReminders()
        }
    }

    private fun saveReminders() {
        with(sharedPreferences.edit()) {
            putStringSet("RemindMeDataList", reminderList.toSet())
            apply()
        }
    }
}
