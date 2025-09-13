package com.example.trainerapp

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.app.Activity

import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
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

            datePickerDialog.datePicker.minDate = minDate
            datePickerDialog.datePicker.maxDate = maxDate

            datePickerDialog.show()
        }

        @SuppressLint("NewApi")
        fun selectDate4(
            context: Context,
            etDate: AppCompatEditText,
            minDate: Long,
            maxDate: Long,
            disabledRanges: List<Pair<Long, Long>>, // List of date ranges to disable
            callback: (Long) -> Unit
        ) {
            val myCalendar = Calendar.getInstance()
            val myFormat = "yyyy-MM-dd"
            val dateFormat = SimpleDateFormat(myFormat, Locale.US)

            val date = DatePickerDialog.OnDateSetListener { _, year, month, day ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, month)
                myCalendar.set(Calendar.DAY_OF_MONTH, day)
                val selectedDateMillis = myCalendar.timeInMillis

                // Check if the selected date is within a disabled range
                val isDisabled = disabledRanges.any { (start, end) ->
                    selectedDateMillis in start..end // Include the end date in the range
                }

                if (!isDisabled && selectedDateMillis in minDate..maxDate) {
                    etDate.setText(dateFormat.format(myCalendar.time))
                    callback(selectedDateMillis) // Return the valid selected date
                }
            }

            val datePickerDialog = DatePickerDialog(
                context,
                date,
                myCalendar[Calendar.YEAR],
                myCalendar[Calendar.MONTH],
                myCalendar[Calendar.DAY_OF_MONTH]
            )

            datePickerDialog.datePicker.minDate = minDate
            datePickerDialog.datePicker.maxDate = maxDate

            datePickerDialog.setOnShowListener {
                val datePicker = datePickerDialog.datePicker

                // Disable dates and change their color immediately when the dialog is opened
                try {
                    val dpv = datePicker.javaClass.getDeclaredField("mDayPickerView")
                    dpv.isAccessible = true
                    val dayPickerView = dpv.get(datePicker)

                    // Access the mDayOfWeekViews properly.
                    val mDayOfWeekViewsField = dayPickerView.javaClass.getDeclaredField("mDayOfWeekViews")
                    mDayOfWeekViewsField.isAccessible = true

                    // Here, the `mDayOfWeekViews` should be an array of day views (could be View[] or something else)
                    val dayOfWeekViews = mDayOfWeekViewsField.get(dayPickerView) as Array<*>

                    // Iterate through all days and disable those in the disabledRanges
                    for (dayView in dayOfWeekViews) {
                        val dayViewInstance = dayView as View
                        val dayMillis = dayViewInstance.tag as Long // Assuming the tag stores the date in milliseconds

                        // Check if the current day is within any disabled range
                        val isDisabled = disabledRanges.any { (start, end) ->
                            dayMillis in start..end // Ensure the end date is included
                        }

                        if (isDisabled) {
                            dayViewInstance.setBackgroundColor(Color.GRAY)
                            dayViewInstance.isEnabled = false // Disable the day
                        } else {
                            dayViewInstance.setBackgroundColor(Color.WHITE)
                            dayViewInstance.isEnabled = true
                        }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            datePickerDialog.show()
        }




        @SuppressLint("NewApi")
        fun selectDate(context: Context, dateedt: EditText) {
            val calendar = Calendar.getInstance()

            val datePickerDialog = DatePickerDialog(
                context,
                { _, year, monthOfYear, dayOfMonth ->
                    calendar.set(year, monthOfYear, dayOfMonth)
                    val dateFormat = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
                    val formattedDate = dateFormat.format(calendar.time)
                    dateedt.setText(formattedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            datePickerDialog.show()
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

        private fun getActivityFromContext(context: Context?): Activity? {
            var ctx = context
            while (ctx is ContextWrapper) {
                if (ctx is Activity) {
                    return ctx
                }
                ctx = ctx.baseContext
            }
            return null
        }
        private val overlayMap = mutableMapOf<Activity, FrameLayout>()

        fun showLoading(activity: Activity) {
            Log.d("Utils", "showLoading called for ${activity.localClassName}")

            val overlay = overlayMap.getOrPut(activity) {
                Log.d("Utils", "Creating new loading overlay for ${activity.localClassName}")
                createOverlay(activity).also {
                    (activity.window.decorView as ViewGroup).addView(
                        it,
                        ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    )
                }
            }

            overlay.visibility = View.VISIBLE
            Log.d("Utils", "Loading overlay set to VISIBLE")
        }

        fun hideLoading(activity: Activity) {
            Log.d("Utils", "hideLoading called for ${activity.localClassName}")
            overlayMap[activity]?.visibility = View.GONE
            Log.d("Utils", "Loading overlay set to GONE")
        }

        private fun createOverlay(activity: Activity): FrameLayout {
            Log.d("Utils", "createOverlay called for ${activity.localClassName}")
            val overlay = FrameLayout(activity).apply {
                setBackgroundColor(Color.parseColor("#80000000")) // 50% black
                visibility = View.GONE
                isClickable = true
                isFocusable = true
            }

            val progressBar = ProgressBar(activity).apply {
                val size = (50 * activity.resources.displayMetrics.density).toInt()
                layoutParams = FrameLayout.LayoutParams(size, size).apply {
                    gravity = Gravity.CENTER
                }
            }

            overlay.addView(progressBar)
            return overlay
        }


        fun setUnAuthDialog(context: Context?) {
            val preferenceManager = PreferencesManager(context!!)
            val dialogLayout = Dialog(context)
            dialogLayout.setContentView(R.layout.unauth_dialog)
            dialogLayout.window?.setBackgroundDrawableResource(android.R.color.transparent)

            val btnOk = dialogLayout.findViewById<TextView>(R.id.btnOk)
            btnOk.setOnClickListener {
                preferenceManager.setUserLogIn(false)

                val activity = getActivityFromContext(context)
                if (activity != null && !activity.isFinishing) {
                    val intent = Intent(activity, MainActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    }
                    activity.startActivity(intent)
                    activity.finishAffinity()
                } else {
                    // Fallback if no valid Activity found
                    val appContext = context.applicationContext
                    val intent = Intent(appContext, MainActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    }
                    appContext.startActivity(intent)
                }

                dialogLayout.dismiss()
            }


            dialogLayout.show()
        }


        fun setUnAuthDialog1(context: Context?) {
            val preferenceManager: PreferencesManager = PreferencesManager(context!!)
            val dialogLayout = Dialog(context)
            dialogLayout.setContentView(R.layout.invalid_auth)
            dialogLayout.window?.setBackgroundDrawableResource(android.R.color.transparent)
//            dialogLayout.window?.setLayout(
//                WindowManager.LayoutParams.MATCH_PARENT,  // Set width to match parent
//                WindowManager.LayoutParams.WRAP_CONTENT   // Set height to wrap content
//            )
            val btnOk = dialogLayout.findViewById<TextView>(R.id.btnOk)
            btnOk.setOnClickListener {
                preferenceManager.setUserLogIn(false)
                dialogLayout.dismiss()
            }
            dialogLayout.show()
        }
    }

}