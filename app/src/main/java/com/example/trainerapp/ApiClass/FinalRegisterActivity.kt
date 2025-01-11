package com.example.trainerapp.ApiClass

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.trainerapp.PreferencesManager
import com.example.trainerapp.R
import com.example.trainerapp.SelectSportActivity
import com.example.trainerapp.SignInActivity
import com.example.trainerapp.databinding.ActivityFinalRegisterBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class FinalRegisterActivity : AppCompatActivity() {
    lateinit var finalRegisterBinding: ActivityFinalRegisterBinding
    lateinit var apiInterface: APIInterface
    lateinit var progress_bar: ProgressBar
    lateinit var apiClient: APIClient
    val myCalendar = Calendar.getInstance()
    lateinit var preferenceManager: PreferencesManager
    lateinit var back: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        finalRegisterBinding = ActivityFinalRegisterBinding.inflate(layoutInflater)
        setContentView(finalRegisterBinding.root)
        back = findViewById(R.id.back)
        preferenceManager = PreferencesManager(this)
        progress_bar = findViewById(R.id.progress_bar)
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)

        finalRegisterBinding.edtBirthday.setOnClickListener {
            showDatePickerDialog {dayOfMonth, month, year ->
                val selectedDate = "$dayOfMonth-${month + 1}-$year"
                finalRegisterBinding.edtBirthday.setText(selectedDate)
                updateLabel()
            }
        }

        back.setOnClickListener {
            finish()
        }

        finalRegisterBinding.edtAddress.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {

            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                isaddressValidate
                if (finalRegisterBinding.edtBirthday.text.toString()
                        .equals("") || finalRegisterBinding.edtAddress.text.toString()
                        .equals("") || finalRegisterBinding.edtCode.text.toString().equals("")
                ) {
                    finalRegisterBinding.cardSignup.setCardBackgroundColor(resources.getColor(R.color.grey))
                } else {
                    finalRegisterBinding.cardSignup.setCardBackgroundColor(resources.getColor(R.color.splash_text_color))
                }
            }
        })

        finalRegisterBinding.edtCode.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {

            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                iscodeValidate
                if (finalRegisterBinding.edtBirthday.text.toString()
                        .equals("") || finalRegisterBinding.edtAddress.text.toString()
                        .equals("") || finalRegisterBinding.edtCode.text.toString().equals("")
                ) {
                    finalRegisterBinding.cardSignup.setCardBackgroundColor(resources.getColor(R.color.grey))
                } else {
                    finalRegisterBinding.cardSignup.setCardBackgroundColor(resources.getColor(R.color.splash_text_color))
                }
            }
        })

        finalRegisterBinding.cardSignup.setOnClickListener {
            if (isValidate) {
                Log.d("FHFHFFGGFG", "onCreate: ${preferenceManager.GetPassword()}")
                progress_bar.visibility = View.VISIBLE
                apiInterface.registerathlete(
                    preferenceManager.Getusername(),
                    preferenceManager.Getemail(),
                    preferenceManager.GetPassword(),
                    preferenceManager.GetReffral(),
                    finalRegisterBinding.edtBirthday.text.toString(),
                    finalRegisterBinding.edtAddress.text.toString(),
                    finalRegisterBinding.edtCode.text.toString(),
                    preferenceManager.GetFlage()
                )?.enqueue(object : Callback<RegisterData?> {
                    override fun onResponse(
                        call: Call<RegisterData?>,
                        response: Response<RegisterData?>
                    ) {
                        Log.d("TAG", response.code().toString())
                        if (response.isSuccessful) {
                            val resource = response.body()

                            if (resource != null) {
                                val success = resource.status ?: false
                                val message = resource.message ?: "Unknown error"
                                val token = resource.token
                                val userId = resource.data?.id?.toString() ?: ""

                                // Handle success
                                if (success) {
                                    preferenceManager.setToken(token)
                                    preferenceManager.setUserId(userId)
                                    preferenceManager.setUserLogIn(true)

                                    progress_bar.visibility = View.GONE
                                    startActivity(Intent(this@FinalRegisterActivity, SelectSportActivity::class.java))
                                    finish()
                                } else {
                                    progress_bar.visibility = View.GONE
                                    Toast.makeText(this@FinalRegisterActivity, message, Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                progress_bar.visibility = View.GONE
                                Toast.makeText(this@FinalRegisterActivity, "Response body is null", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            progress_bar.visibility = View.GONE
                            val message = response.message()
                            Toast.makeText(this@FinalRegisterActivity, message, Toast.LENGTH_SHORT).show()
                            call.cancel()
                            startActivity(Intent(this@FinalRegisterActivity, SignInActivity::class.java))
                            finish()
                        }
                    }

                    override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                        progress_bar.visibility = View.GONE
                        Toast.makeText(this@FinalRegisterActivity, t.message ?: "An error occurred", Toast.LENGTH_SHORT).show()
                        call.cancel()
                    }
                })
            }
        }

    }

    private fun showDatePickerDialog(onDateSelected: (year: Int, month: Int, dayOfMonth: Int) -> Unit) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_date_picker)

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = (displayMetrics.widthPixels * 0.9f).toInt()
        dialog.window?.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val datePicker = dialog.findViewById<DatePicker>(R.id.datePicker)
        val cancelButton = dialog.findViewById<Button>(R.id.btnCancel)
        val applyButton = dialog.findViewById<Button>(R.id.btnApply)


        // Default date (today)
        val calendar = Calendar.getInstance()
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(
            Calendar.DAY_OF_MONTH), null)

        cancelButton.setOnClickListener { dialog.dismiss() }

        applyButton.setOnClickListener {
            val selectedYear = datePicker.year
            val selectedMonth = datePicker.month
            val selectedDay = datePicker.dayOfMonth
            onDateSelected(selectedYear, selectedMonth, selectedDay)
            dialog.dismiss()
        }

        dialog.show()
    }


    private val isValidate: Boolean
        get() {
            if (finalRegisterBinding.edtBirthday.text.toString().isEmpty()) {
                finalRegisterBinding.birthdayError.visibility = View.VISIBLE
                finalRegisterBinding.birthdayError.text = "Please Enter Birthday"

                return false
            } else {
                finalRegisterBinding.birthdayError.visibility = View.GONE
            }

            if (finalRegisterBinding.edtAddress.text.toString().isEmpty()) {
                finalRegisterBinding.addressError.visibility = View.VISIBLE
                finalRegisterBinding.addressError.text = "Please Enter Address"

                return false
            } else {
                finalRegisterBinding.addressError.visibility = View.GONE
            }

            if (finalRegisterBinding.edtCode.text.toString().isEmpty()) {
                finalRegisterBinding.codeError.visibility = View.VISIBLE
                finalRegisterBinding.codeError.text = "Please Enter Fiscal Code"

                return false
            } else {
                finalRegisterBinding.codeError.visibility = View.GONE
            }
            return true
        }


    private val isaddressValidate: Boolean
        get() {
            finalRegisterBinding.addressError.visibility = View.GONE
            if (finalRegisterBinding.edtAddress.text.toString().isEmpty()) {
                finalRegisterBinding.addressError.visibility = View.VISIBLE
                finalRegisterBinding.addressError.text = "Please Enter Address"

                return false
            } else {
                finalRegisterBinding.addressError.visibility = View.GONE
            }

            return true
        }

    private val iscodeValidate: Boolean
        get() {
            finalRegisterBinding.codeError.visibility = View.GONE
            if (finalRegisterBinding.edtCode.text.toString().isEmpty()) {
                finalRegisterBinding.codeError.visibility = View.VISIBLE
                finalRegisterBinding.codeError.text = "Please Enter Fiscal Code"

                return false
            } else {
                finalRegisterBinding.codeError.visibility = View.GONE
            }
            return true
        }

    private fun updateLabel() {
        val myFormat = "yyyy-MM-dd"
        val dateFormat = SimpleDateFormat(myFormat, Locale.US)
        finalRegisterBinding.edtBirthday.setText(dateFormat.format(myCalendar.time))
        finalRegisterBinding.birthdayError.visibility = View.GONE
    }
}