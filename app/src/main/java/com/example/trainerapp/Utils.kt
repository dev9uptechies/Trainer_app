package com.example.trainerapp

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Spinner
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Utils {
    companion object {
        fun setSpinnerAdapter(
            context: Context?,
            spinnerArray: ArrayList<String>,
            spinner: Spinner,
            message: String? = "Select"
        ) {
            val spinnerArrayAdapter: ArrayAdapter<String> = object : ArrayAdapter<String>(
                context!!, android.R.layout.simple_spinner_item,
                spinnerArray
            ) {
                override fun getDropDownView(
                    position: Int, convertView: View?,
                    parent: ViewGroup
                ): View {
                    val view = super.getDropDownView(position, convertView, parent)
                    val tv = view as TextView
                    tv.setBackgroundColor(Color.BLACK)
                    return view
                }
            }
            spinnerArrayAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
            )
            spinner.adapter = spinnerArrayAdapter
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    Log.d("TAG", "onItemSelected: " + parent.getItemAtPosition(position))
                    (view as TextView).setTextColor(Color.WHITE)
                    //spinnerArrayAdapter.notifyDataSetChanged()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

        fun selectDate2(
            context: Context,
            etDate: TextView,
            minDate: Long,
            maxDate: Long,
            callback: (Long) -> Unit
        ) {
            val myCalendar = Calendar.getInstance()
            val myFormat = "yyyy-MM-dd" // Corrected format to lower case 'yyyy'
            val dateFormat = SimpleDateFormat(myFormat, Locale.US)

            val date = OnDateSetListener { _, year, month, day ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, month)
                myCalendar.set(Calendar.DAY_OF_MONTH, day)
                val selectedDateMillis = myCalendar.timeInMillis
                etDate.text = dateFormat.format(myCalendar.time)
                callback(selectedDateMillis) // Call the callback with selected date
            }

            // Create the DatePickerDialog
            val datePickerDialog = DatePickerDialog(
                context,
                date,
                myCalendar[Calendar.YEAR],
                myCalendar[Calendar.MONTH],
                myCalendar[Calendar.DAY_OF_MONTH]
            )

            // Set the minimum and maximum date for the DatePicker
            datePickerDialog.datePicker.minDate = minDate
            datePickerDialog.datePicker.maxDate = maxDate

            datePickerDialog.show()
        }

        fun selectDate3(
            context: Context,
            etDate: AppCompatEditText,
            minDate: Long,
            maxDate: Long,
            callback: (Long) -> Unit
        ) {
            val myCalendar = Calendar.getInstance()
            val myFormat = "yyyy-MM-dd" // Corrected format to lower case 'yyyy'
            val dateFormat = SimpleDateFormat(myFormat, Locale.US)

            val date = OnDateSetListener { _, year, month, day ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, month)
                myCalendar.set(Calendar.DAY_OF_MONTH, day)
                val selectedDateMillis = myCalendar.timeInMillis
                etDate.setText(dateFormat.format(myCalendar.time))
                callback(selectedDateMillis) // Call the callback with selected date
            }

            // Create the DatePickerDialog
            val datePickerDialog = DatePickerDialog(
                context,
                date,
                myCalendar[Calendar.YEAR],
                myCalendar[Calendar.MONTH],
                myCalendar[Calendar.DAY_OF_MONTH]
            )

            // Set the minimum and maximum date for the DatePicker
            datePickerDialog.datePicker.minDate = minDate
            datePickerDialog.datePicker.maxDate = maxDate

            datePickerDialog.show()
        }

        @SuppressLint("NewApi")
        fun selectDate(context: Context?, etDate: AppCompatEditText) {
            val myCalendar = Calendar.getInstance()
            val myFormat = "YYYY-MM-dd"
            val dateFormat = SimpleDateFormat(myFormat, Locale.US)
            val date = OnDateSetListener { view1: DatePicker?, year: Int, month: Int, day: Int ->
                myCalendar[Calendar.YEAR] = year
                myCalendar[Calendar.MONTH] = month
                myCalendar[Calendar.DAY_OF_MONTH] = day
                etDate.setText(dateFormat.format(myCalendar.time))
            }
            DatePickerDialog(
                context!!,
                date,
                myCalendar[Calendar.YEAR],
                myCalendar[Calendar.MONTH],
                myCalendar[Calendar.DAY_OF_MONTH]
            ).show()
        }


        @RequiresApi(Build.VERSION_CODES.N)
        fun select(context: Context?, etDate: AppCompatEditText) {
            val myCalendar = Calendar.getInstance()
            val myFormat = "dd MMM, yyyy"
            val dateFormat = SimpleDateFormat(myFormat, Locale.US)
            val date = OnDateSetListener { view1: DatePicker?, year: Int, month: Int, day: Int ->
                myCalendar[Calendar.YEAR] = year
                myCalendar[Calendar.MONTH] = month
                myCalendar[Calendar.DAY_OF_MONTH] = day
                etDate.setText(dateFormat.format(myCalendar.time))
            }
            DatePickerDialog(
                context!!,
                date,
                myCalendar[Calendar.YEAR],
                myCalendar[Calendar.MONTH],
                myCalendar[Calendar.DAY_OF_MONTH]
            ).show()
        }

        fun setUnAuthDialog(context: Context?) {
            val preferenceManager: PreferencesManager = PreferencesManager(context!!)
            val dialogLayout = Dialog(context)
            dialogLayout.setContentView(R.layout.unauth_dialog)
            dialogLayout.window?.setBackgroundDrawableResource(android.R.color.transparent)
//            dialogLayout.window?.setLayout(
//                WindowManager.LayoutParams.MATCH_PARENT,  // Set width to match parent
//                WindowManager.LayoutParams.WRAP_CONTENT   // Set height to wrap content
//            )
            val btnOk = dialogLayout.findViewById<TextView>(R.id.btnOk)
            btnOk.setOnClickListener {
                preferenceManager.setUserLogIn(false)
                val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)
                dialogLayout.dismiss()
            }
            dialogLayout.show()
        }
    }

}