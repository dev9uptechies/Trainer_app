package com.example.trainerapp

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.SportlistData
import com.example.trainerapp.databinding.ActivityForgetPasswordBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgetPasswordActivity : AppCompatActivity() {
    lateinit var forgetPasswordBinding: ActivityForgetPasswordBinding
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    lateinit var progress_bar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        forgetPasswordBinding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        setContentView(forgetPasswordBinding.root)
        initViews()
        checkChangeListener()
        checkButtonClick()


    }

    private fun checkButtonClick() {
//        if (isEmailValidate) {
//            forgetPasswordBinding.cardContinue.setCardBackgroundColor(
//                resources.getColor(
//                    R.color.splash_text_color
//                )
//            )
//        } else {
//            forgetPasswordBinding.cardContinue.setCardBackgroundColor(
//                resources.getColor(
//                    R.color.grey
//                )
//            )
//        }
        forgetPasswordBinding.back.setOnClickListener {
            finish()
        }
        forgetPasswordBinding.cardContinue.setOnClickListener {
            if (isEmailValidate) {
                progress_bar.visibility = View.VISIBLE
                apiInterface.ForgetPassword(forgetPasswordBinding.edtEmail.text.toString())
                    ?.enqueue(object :
                        Callback<SportlistData?> {
                        override fun onResponse(
                            call: Call<SportlistData?>,
                            response: Response<SportlistData?>
                        ) {
                            Log.d("TAG", response.code().toString() + "")
                            if (response.code() == 200) {
                                val resource: SportlistData? = response.body()
                                val Success: Boolean = resource?.status!!
                                val Message: String = resource.message!!
                                if (Success) {
                                    progress_bar.visibility = View.GONE
                                    forgetPasswordBinding.forget.setBackgroundColor(
                                        resources.getColor(
                                            R.color.grey
                                        )
                                    )
                                    val dialogBuilder =
                                        AlertDialog.Builder(this@ForgetPasswordActivity)
                                    val layoutView =
                                        layoutInflater.inflate(
                                            R.layout.layout_dialog_forget,
                                            null
                                        )
                                    dialogBuilder.setView(layoutView)
                                    val card_close =
                                        layoutView.findViewById<CardView>(R.id.card_close)
                                    val tv_email =
                                        layoutView.findViewById<TextView>(R.id.tv_email)
                                    val alertDialog: AlertDialog
                                    alertDialog = dialogBuilder.create()
                                    alertDialog.window!!.setBackgroundDrawable(
                                        ColorDrawable(
                                            Color.TRANSPARENT
                                        )
                                    )
                                    alertDialog.setCanceledOnTouchOutside(true)
                                    alertDialog.setCancelable(true)
                                    alertDialog.show()

                                    tv_email.text =
                                        forgetPasswordBinding.edtEmail.text.toString()
                                    card_close.setOnClickListener {
                                        forgetPasswordBinding.forget.setBackgroundColor(
                                            resources.getColor(R.color.black)
                                        )
                                        alertDialog.hide()
                                    }

                                } else {
                                    progress_bar.visibility = View.GONE
                                    forgetPasswordBinding.forget.setBackgroundColor(
                                        resources.getColor(
                                            R.color.grey
                                        )
                                    )
                                    val dialogBuilder =
                                        AlertDialog.Builder(this@ForgetPasswordActivity)
                                    val layoutView =
                                        layoutInflater.inflate(
                                            R.layout.layout_dialog_cancel_forget,
                                            null
                                        )
                                    dialogBuilder.setView(layoutView)
                                    val alertDialog: AlertDialog
                                    alertDialog = dialogBuilder.create()
                                    alertDialog.window!!.setBackgroundDrawable(
                                        ColorDrawable(
                                            Color.TRANSPARENT
                                        )
                                    )
                                    alertDialog.setCanceledOnTouchOutside(true)
                                    alertDialog.setCancelable(true)
                                    alertDialog.show()
                                    val card_close =
                                        layoutView.findViewById<CardView>(R.id.card_close)
                                    card_close.setOnClickListener {
                                        forgetPasswordBinding.forget.setBackgroundColor(
                                            resources.getColor(R.color.black)
                                        )
                                        alertDialog.hide()
                                    }
                                }
                            } else if (response.code() == 403) {
                                val message = response.message()
                                Toast.makeText(
                                    this@ForgetPasswordActivity,
                                    "" + message,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                call.cancel()
                                startActivity(
                                    Intent(
                                        this@ForgetPasswordActivity,
                                        SignInActivity::class.java
                                    )
                                )
                                finish()
                            } else {
                                val message = response.message()
                                Toast.makeText(
                                    this@ForgetPasswordActivity,
                                    "" + message,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                call.cancel()
                            }
                        }

                        override fun onFailure(call: Call<SportlistData?>, t: Throwable) {
                            Toast.makeText(
                                this@ForgetPasswordActivity,
                                "" + t.message,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            call.cancel()
                        }
                    })
            } else {
                forgetPasswordBinding.cardContinue.setCardBackgroundColor(resources.getColor(R.color.grey))
            }
        }
    }

    private fun checkChangeListener() {
        forgetPasswordBinding.edtEmail.addTextChangedListener(object : TextWatcher {

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
                if (forgetPasswordBinding.edtEmail.text.toString().equals("")) {
                    forgetPasswordBinding.cardContinue.setCardBackgroundColor(resources.getColor(R.color.grey))
                } else {
                    forgetPasswordBinding.cardContinue.setCardBackgroundColor(
                        resources.getColor(
                            R.color.splash_text_color
                        )
                    )
                }
            }
        })
    }

    private fun initViews() {
        progress_bar = findViewById(R.id.progress_bar)
        apiClient = APIClient(this)

        apiInterface = apiClient.client().create(APIInterface::class.java)
    }


    private val isEmailValidate: Boolean
        get() {
            val email = forgetPasswordBinding.edtEmail.text.toString()
            if (!checkEmailValidation(email)) {
                forgetPasswordBinding.emailLyError.visibility = View.VISIBLE
                forgetPasswordBinding.emailLyError.text = "Please Enter Valid Email"
                return false
            } else {
                forgetPasswordBinding.emailLyError.visibility = View.GONE
            }
            return true
        }

    private fun checkEmailValidation(
        email: String
    ): Boolean {
        val separated = email.split(" ".toRegex()).toTypedArray()
        return Patterns.EMAIL_ADDRESS.matcher(separated[0]).matches()
    }
}