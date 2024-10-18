package com.example.trainerapp.ApiClass

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
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


        val date =
            OnDateSetListener { view, year, month, day ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, month)
                myCalendar.set(Calendar.DAY_OF_MONTH, day)
                updateLabel()
            }
        finalRegisterBinding.edtBirthday.setOnClickListener {
            DatePickerDialog(
                this,
                date,
                myCalendar[Calendar.YEAR],
                myCalendar[Calendar.MONTH],
                myCalendar[Calendar.DAY_OF_MONTH]
            ).show()
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
                        Log.d("TAG", response.code().toString() + "")
                        val code = response.code()
                        if (code == 200) {
                            val resource: RegisterData? = response.body()
                            val Success: Boolean = resource?.status!!
                            val Message: String = resource.message!!
                            preferenceManager.setToken(resource.token)
                            preferenceManager.setUserId(response.body()!!.data!!.id.toString())
                            preferenceManager.setUserLogIn(true)
                            if (Success.equals(true)) {
                                progress_bar.visibility = View.GONE
                                startActivity(
                                    Intent(
                                        this@FinalRegisterActivity,
                                        SelectSportActivity::class.java
                                    )
                                )
                                finish()
                            } else {
                                progress_bar.visibility = View.GONE
                                Toast.makeText(
                                    this@FinalRegisterActivity,
                                    "" + Message,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        } else {
                            progress_bar.visibility = View.GONE
                            val message = response.message()
                            Toast.makeText(
                                this@FinalRegisterActivity,
                                "" + message,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            call.cancel()
                            startActivity(
                                Intent(
                                    this@FinalRegisterActivity,
                                    SignInActivity::class.java
                                )
                            )
                            finish()
                        }

                    }

                    override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                        progress_bar.visibility = View.GONE
                        Toast.makeText(
                            this@FinalRegisterActivity,
                            "" + t.message,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                })

            }
        }

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