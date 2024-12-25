package com.example.trainerapp

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.databinding.ActivitySignInBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInActivity : AppCompatActivity() {
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    lateinit var preferenceManager: PreferencesManager
    private var isOldChecked: Boolean = false
    lateinit var email_error: TextView
    lateinit var password_error: TextView
    lateinit var progress_bar: ProgressBar
    lateinit var signInBinding: ActivitySignInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signInBinding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(signInBinding.root)


        initViews()
//        signInBinding.edtEmail.setText("gaurav.bhattacharya7@gmail.com")
//        signInBinding.edtPassword.setText("Gaurav@7613")
        signInBinding.edtEmail.setText("testusernew@gmail.com")
        signInBinding.edtPassword.setText("Testing@112")
//        signInBinding.edtEmail.setText("4trainersapp@gmail.com")
//        signInBinding.edtPassword.setText("4Trainersapp!")
        checkFieldValue()
        checkButtonClick()
        checkChangeListner()

    }

    private fun checkChangeListner() {
        signInBinding.edtEmail.addTextChangedListener(object : TextWatcher {

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
                isemailValidate
                if (signInBinding.edtPassword.text.toString()
                        .equals("") || signInBinding.edtEmail.text.toString()
                        .equals("")
                ) {
                    signInBinding.btnSignIn.setCardBackgroundColor(resources.getColor(R.color.grey))
                } else {
                    signInBinding.btnSignIn.setCardBackgroundColor(resources.getColor(R.color.splash_text_color))
                }
            }
        })

        signInBinding.edtPassword.addTextChangedListener(object : TextWatcher {

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
                ispasswordValidate
                if (signInBinding.edtPassword.text.toString()
                        .equals("") || signInBinding.edtEmail.text.toString()
                        .equals("")
                ) {
                    signInBinding.btnSignIn.setCardBackgroundColor(resources.getColor(R.color.grey))
                } else {
                    signInBinding.btnSignIn.setCardBackgroundColor(resources.getColor(R.color.splash_text_color))
                }
            }
        })
    }

    private fun checkFieldValue() {
        if (signInBinding.edtEmail.text.toString()
                .equals("") && signInBinding.edtPassword.text.toString().equals("")
        ) {
            signInBinding.btnSignIn.setCardBackgroundColor(resources.getColor(R.color.grey))
        } else {
            signInBinding.btnSignIn.setCardBackgroundColor(resources.getColor(R.color.splash_text_color))
        }
    }

    private fun checkButtonClick() {
        signInBinding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        signInBinding.tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgetPasswordActivity::class.java))
        }

        signInBinding.back.setOnClickListener {
            finish()
        }

        signInBinding.icHidePassword.setOnClickListener {
            hideOrShowOldPassword()
        }

        signInBinding.btnSignIn.setOnClickListener {

            if (isValidate) {
                progress_bar.visibility = View.VISIBLE
                email_error.visibility = View.GONE
                password_error.visibility = View.GONE
                apiInterface.Logoin(
                    signInBinding.edtEmail.text.toString(),
                    signInBinding.edtPassword.text.toString(),
                    preferenceManager.GetFlage()
                )?.enqueue(object : Callback<RegisterData?> {
                    override fun onResponse(
                        call: Call<RegisterData?>,
                        response: Response<RegisterData?>
                    ) {
//                        Log.d("GHGHGHGHGHGH", response.code().toString() + "")

                        val code = response.code()
                        if (code == 200) {
                            val resource: RegisterData? = response.body()
                            val Success: Boolean = resource?.status!!
                            Log.e("QWQWQWQWWQW", "" + resource.status)
                            if (resource.status == true) {
                                val Message: String = resource.message!!
                                preferenceManager.setToken(resource.token)
                                preferenceManager.setUserId(response.body()!!.data!!.id.toString())
                                preferenceManager.setUserLogIn(true)
                                if (Success.equals(true)) {
                                    progress_bar.visibility = View.GONE
                                    if (resource.data!!.userSports!!.size == 0) {

                                        startActivity(
                                            Intent(
                                                this@SignInActivity,
                                                SelectSportActivity::class.java
                                            )
                                        )


                                        finish()
                                    } else {
//                                val intent =
//                                    Intent(this@SignInActivity, MainHomeActivity::class.java)
//                                startActivity(intent)
//                                finish()
                                        startActivity(
                                            Intent(
                                                this@SignInActivity,
                                                HomeActivity::class.java
                                            )
                                        )
                                        finish()
                                    }
                                } else {
                                    progress_bar.visibility = View.GONE
                                    Toast.makeText(
                                        this@SignInActivity,
                                        "" + Message,
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                            } else {
                                progress_bar.visibility = View.GONE
                                Utils.setUnAuthDialog1(this@SignInActivity)

                            }


                        } else if (response.code() == 403) {
                            Utils.setUnAuthDialog(this@SignInActivity)
//                            val message = response.message()
//                            Toast.makeText(
//                                this@SignInActivity,
//                                "" + message,
//                                Toast.LENGTH_SHORT
//                            )
//                                .show()
//                            call.cancel()
//                            preferenceManager.setUserLogIn(false)
//                            startActivity(
//                                Intent(
//                                    this@SignInActivity,
//                                    SignInActivity::class.java
//                                )
//                            )
//                            finish()
                        } else {
                            progress_bar.visibility = View.GONE
                            val message = response.message()
                            Toast.makeText(this@SignInActivity, "" + message, Toast.LENGTH_SHORT)
                                .show()
                            call.cancel()
                        }
                    }

                    override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                        progress_bar.visibility = View.GONE
                        Toast.makeText(this@SignInActivity, "" + t.message, Toast.LENGTH_SHORT)
                            .show()
                        call.cancel()
                    }
                })

            } else {
                progress_bar.visibility = View.GONE
            }
        }
    }

    private fun initViews() {
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        preferenceManager = PreferencesManager(this)
        progress_bar = findViewById(R.id.progress_bar)
        email_error = findViewById(R.id.email_error)
        password_error = findViewById(R.id.password_error)
    }

    private fun hideOrShowOldPassword() {
        if (signInBinding.edtPassword.getText().toString().trim({ it <= ' ' }).length > 0) {
            if (!isOldChecked) {
                signInBinding.edtPassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                signInBinding.edtPassword.setSelection(
                    signInBinding.edtPassword.getText().toString().length
                )
                signInBinding.icHidePassword.setImageDrawable(resources.getDrawable(R.drawable.ic_show_password))
                isOldChecked = true
            } else {
                // hide password
                signInBinding.edtPassword.transformationMethod =
                    PasswordTransformationMethod.getInstance()
                signInBinding.edtPassword.setSelection(
                    signInBinding.edtPassword.getText().toString().length
                )
                signInBinding.icHidePassword.setImageDrawable(resources.getDrawable(R.drawable.hide_password))
                isOldChecked = false

            }
        }
    }

    private val isValidate: Boolean
        get() {
            var email = signInBinding.edtEmail.text.toString()
            var password = signInBinding.edtPassword.text.toString()
            if (email == "") {
                email_error.visibility = View.VISIBLE
                email_error.text = "Please Enter Email"
                return false
            } else {
                email_error.visibility = View.GONE
            }

            if (password == "") {
                password_error.visibility = View.VISIBLE
                password_error.text = "Please Enter Password"
                return false
            } else {
                password_error.visibility = View.GONE
            }
            return true
        }


    private val isemailValidate: Boolean
        get() {
            password_error.visibility = View.GONE
            var email = signInBinding.edtEmail.text.toString()
            if (email == "") {
                email_error.visibility = View.VISIBLE
                email_error.text = "Please Enter Email"
                return false
            } else {
                email_error.visibility = View.GONE
            }

            if (!checkEmailValidation(email)) {
                email_error.visibility = View.VISIBLE
                email_error.text = "Please Enter Valid Email"
                return false
            } else {
                email_error.visibility = View.GONE
            }
            return true
        }


    private val ispasswordValidate: Boolean
        get() {
            password_error.visibility = View.GONE
            var password = signInBinding.edtPassword.text.toString()
            if (password == "") {
                password_error.visibility = View.VISIBLE
                password_error.text = "Please Enter Password"
                return false
            } else {
                password_error.visibility = View.GONE
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