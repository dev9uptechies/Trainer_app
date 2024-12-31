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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewRemindMeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = ReminderAdapter(this, reminderList)
        binding.reminderListView.adapter = adapter

        ButtonClick()
        getSavedReminders()
    }

    private fun ButtonClick() {
        binding.back.setOnClickListener { finish() }
        binding.add.setOnClickListener {
            AddRemindMe()
        }
    }

    private fun AddRemindMe() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_add_remind_me)

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = (displayMetrics.widthPixels * 0.9f).toInt()
        val height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.setLayout(width, height)
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

                with(sharedPreferences.edit()) {
                    putStringSet("RemindMeDataList", reminderList.toSet()) // Store as Set
                    apply()
                }

                getSavedReminders()

                Toast.makeText(this, "Reminder saved!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Please enter valid reminders", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun getSavedReminders() {
        val savedSet = sharedPreferences.getStringSet("RemindMeDataList", null)

        if (!savedSet.isNullOrEmpty()) {
            reminderList.clear()
            reminderList.addAll(savedSet)

            (binding.reminderListView.adapter as ReminderAdapter).notifyDataSetChanged()

            binding.reminderListView.visibility = View.VISIBLE
            binding.tvNodata.visibility = View.GONE
        } else {
            // Show "No Data" message if there are no reminders saved
            binding.reminderListView.visibility = View.GONE
            binding.tvNodata.visibility = View.VISIBLE
        }
    }
}
