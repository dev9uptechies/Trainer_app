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
import com.example.trainerapp.ApiClass.FinalRegisterActivity
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.databinding.ActivityRegisterBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    lateinit var registerBinding: ActivityRegisterBinding
    lateinit var apiInterface: APIInterface
    lateinit var progress_bar: ProgressBar
    lateinit var apiClient: APIClient
    lateinit var email_error: TextView
    lateinit var password_error: TextView
    var Role: String? = null
    lateinit var preferenceManager: PreferencesManager
    private var isOldChecked: Boolean = false
    private var isNewChecked: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(registerBinding.root)
        initViews()
        checkButtonClick()
        checkChangeListener()
    }

    private fun checkChangeListener() {
        registerBinding.edtEmail.addTextChangedListener(object : TextWatcher {

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
                isEmailValidate
                if (registerBinding.edtUserName.text.toString()
                        .equals("") || registerBinding.edtPassword.text.toString()
                        .equals("") || registerBinding.edtEmail.text.toString().equals("")
                    || registerBinding.edtCmPassword.text.toString().equals("")
                ) {
                    registerBinding.btnSignIn.setCardBackgroundColor(resources.getColor(R.color.grey))
                    registerBinding.btnNextRegister.setCardBackgroundColor(resources.getColor(R.color.grey))
                } else {
                    registerBinding.btnSignIn.setCardBackgroundColor(resources.getColor(R.color.splash_text_color))
                    registerBinding.btnNextRegister.setCardBackgroundColor(resources.getColor(R.color.splash_text_color))
                }
            }
        })


        registerBinding.edtPassword.addTextChangedListener(object : TextWatcher {

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
                isPasswordValidate
                if (registerBinding.edtUserName.text.toString()
                        .equals("") || registerBinding.edtPassword.text.toString()
                        .equals("") || registerBinding.edtEmail.text.toString().equals("")
                    || registerBinding.edtCmPassword.text.toString().equals("")
                ) {
                    registerBinding.btnSignIn.setCardBackgroundColor(resources.getColor(R.color.grey))
                    registerBinding.btnNextRegister.setCardBackgroundColor(resources.getColor(R.color.grey))
                } else {
                    registerBinding.btnSignIn.setCardBackgroundColor(resources.getColor(R.color.splash_text_color))
                    registerBinding.btnNextRegister.setCardBackgroundColor(resources.getColor(R.color.splash_text_color))
                }
            }
        })

        registerBinding.edtCmPassword.addTextChangedListener(object : TextWatcher {

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
                isComPasswordValidate
                if (registerBinding.edtUserName.text.toString()
                        .equals("") || registerBinding.edtPassword.text.toString()
                        .equals("") || registerBinding.edtEmail.text.toString().equals("")
                    || registerBinding.edtCmPassword.text.toString().equals("")
                ) {
                    registerBinding.btnSignIn.setCardBackgroundColor(resources.getColor(R.color.grey))
                    registerBinding.btnNextRegister.setCardBackgroundColor(resources.getColor(R.color.grey))
                } else {
                    registerBinding.btnSignIn.setCardBackgroundColor(resources.getColor(R.color.splash_text_color))
                    registerBinding.btnNextRegister.setCardBackgroundColor(resources.getColor(R.color.splash_text_color))
                }
            }
        })
        registerBinding.edtUserName.addTextChangedListener(object : TextWatcher {

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
                isUsernameValidate
                if (registerBinding.edtUserName.text.toString()
                        .equals("") || registerBinding.edtPassword.text.toString()
                        .equals("") || registerBinding.edtEmail.text.toString().equals("")
                    || registerBinding.edtCmPassword.text.toString().equals("")
                ) {
                    registerBinding.btnSignIn.setCardBackgroundColor(resources.getColor(R.color.grey))
                    registerBinding.btnNextRegister.setCardBackgroundColor(resources.getColor(R.color.grey))
                } else {
                    registerBinding.btnSignIn.setCardBackgroundColor(resources.getColor(R.color.splash_text_color))
                    registerBinding.btnNextRegister.setCardBackgroundColor(resources.getColor(R.color.splash_text_color))
                }
            }
        })
    }

    private fun checkButtonClick() {
        registerBinding.tvSignIn.setOnClickListener {
            finish()
        }

        registerBinding.back.setOnClickListener {
            finish()
        }

        registerBinding.icHidePassword.setOnClickListener {
            hideOrShowOldPassword()
        }
        registerBinding.icHideCmPassword.setOnClickListener {
            hideOrShowconfirmPassword()
        }
        registerBinding.btnSign.setOnClickListener {
            finish()
        }

        registerBinding.btnNextRegister.setOnClickListener {
            if (isValidate) {
                preferenceManager.setusername(registerBinding.edtUserName.text.toString())
                preferenceManager.setemail(registerBinding.edtEmail.text.toString())
                preferenceManager.setpassword(registerBinding.edtPassword.text.toString())
                preferenceManager.setreffrel(registerBinding.edtRefferal.text.toString())
                startActivity(Intent(this, FinalRegisterActivity::class.java))

            }
        }

        registerBinding.btnSignIn.setOnClickListener {
            if (isValidate) {
                progress_bar.visibility = View.VISIBLE
                apiInterface.register(
                    registerBinding.edtUserName.text.toString(),
                    registerBinding.edtEmail.text.toString(),
                    registerBinding.edtPassword.text.toString(),
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
                            preferenceManager.setUserLogIn(true)
                            preferenceManager.setToken(resource.token)
                            preferenceManager.setUserId(response.body()!!.data!!.id.toString())
                            if (Success.equals(true)) {

                                progress_bar.visibility = View.GONE
                                startActivity(
                                    Intent(
                                        this@RegisterActivity,
                                        SelectSportActivity::class.java
                                    )
                                )
                                finish()
                            } else {
                                progress_bar.visibility = View.GONE
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "" + Message,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        } else {
                            progress_bar.visibility = View.GONE
                            val message = response.message()
                            Toast.makeText(this@RegisterActivity, "" + message, Toast.LENGTH_SHORT)
                                .show()
                            call.cancel()

                            startActivity(
                                Intent(
                                    this@RegisterActivity,
                                    SignInActivity::class.java
                                )
                            )
                            finish()

                        }

                    }

                    override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                        progress_bar.visibility = View.GONE
                        Toast.makeText(this@RegisterActivity, "" + t.message, Toast.LENGTH_SHORT)
                            .show()
                        call.cancel()
                    }
                })

            }
        }
    }

    private fun initViews() {
        progress_bar = findViewById(R.id.progress_bar)
        email_error = findViewById(R.id.email_error)
        password_error = findViewById(R.id.password_error)
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        preferenceManager = PreferencesManager(this)
        Role = preferenceManager.GetFlage()

        if (Role == "Athlete") {
            registerBinding.lyReffrel.visibility = View.VISIBLE
            registerBinding.btnSignIn.visibility = View.GONE
            registerBinding.btnNextRegister.visibility = View.VISIBLE
            registerBinding.tvSignIn.visibility = View.GONE
            registerBinding.btnSign.visibility = View.VISIBLE
        } else {
            registerBinding.lyReffrel.visibility = View.GONE
            registerBinding.btnSignIn.visibility = View.VISIBLE
            registerBinding.btnNextRegister.visibility = View.GONE
            registerBinding.tvSignIn.visibility = View.VISIBLE
            registerBinding.btnSign.visibility = View.GONE
        }
    }

    private fun hideOrShowOldPassword() {
        if (registerBinding.edtPassword.getText().toString().trim({ it <= ' ' }).length > 0) {
            if (!isOldChecked) {
                registerBinding.edtPassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                registerBinding.edtPassword.setSelection(
                    registerBinding.edtPassword.getText().toString().length
                )
                registerBinding.icHidePassword.setImageDrawable(resources.getDrawable(R.drawable.ic_show_password))
                isOldChecked = true
            } else {
                // hide password
                registerBinding.edtPassword.transformationMethod =
                    PasswordTransformationMethod.getInstance()
                registerBinding.edtPassword.setSelection(
                    registerBinding.edtPassword.getText().toString().length
                )
                registerBinding.icHidePassword.setImageDrawable(resources.getDrawable(R.drawable.hide_password))
                isOldChecked = false

            }
        }
    }

    private fun hideOrShowconfirmPassword() {
        if (registerBinding.edtCmPassword.getText().toString().trim({ it <= ' ' }).length > 0) {
            if (!isNewChecked) {
                registerBinding.edtCmPassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                registerBinding.edtCmPassword.setSelection(
                    registerBinding.edtCmPassword.getText().toString().length
                )
                registerBinding.icHideCmPassword.setImageDrawable(resources.getDrawable(R.drawable.ic_show_password))
                isNewChecked = true
            } else {
                // hide password
                registerBinding.edtCmPassword.transformationMethod =
                    PasswordTransformationMethod.getInstance()
                registerBinding.edtCmPassword.setSelection(
                    registerBinding.edtCmPassword.getText().toString().length
                )
                registerBinding.icHideCmPassword.setImageDrawable(resources.getDrawable(R.drawable.hide_password))
                isNewChecked = false

            }
        }
    }

    private val isValidate: Boolean
        get() {

            val email = registerBinding.edtEmail.text.toString()
            val password = registerBinding.edtPassword.text.toString()
            val username = registerBinding.edtUserName.text.toString()
            val cm_password = registerBinding.edtCmPassword.text.toString()
            if (username == "") {
                registerBinding.userNameError.visibility = View.VISIBLE
                registerBinding.userNameError.text = "Please Enter User Name"
                return false
            } else {
                registerBinding.userNameError.visibility = View.GONE
            }
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
            if (cm_password == "") {
                registerBinding.cmPasswordError.visibility = View.VISIBLE
                registerBinding.cmPasswordError.text = "Please Enter Confirm Password"
                return false
            } else {
                registerBinding.cmPasswordError.visibility = View.GONE
            }

            if (password != cm_password) {
                registerBinding.cmPasswordError.visibility = View.VISIBLE
                registerBinding.cmPasswordError.text = "Please Enter Valid Password"
                return false
            } else {
                registerBinding.cmPasswordError.visibility = View.GONE
            }

            return true
        }


    private val isEmailValidate: Boolean
        get() {
            email_error.visibility = View.GONE
            password_error.visibility = View.GONE
            val email = registerBinding.edtEmail.text.toString()
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


    private val isUsernameValidate: Boolean
        get() {
            registerBinding.userNameError.visibility = View.GONE
            val username = registerBinding.edtUserName.text.toString()
            if (username == "") {
                registerBinding.userNameError.visibility = View.VISIBLE
                registerBinding.userNameError.text = "Please Enter User Name"
                return false
            } else {
                registerBinding.userNameError.visibility = View.GONE
            }
            return true
        }

    private val isPasswordValidate: Boolean
        get() {
            password_error.visibility = View.GONE
            val password = registerBinding.edtPassword.text.toString()
            val cm_password = registerBinding.edtCmPassword.text.toString()
            if (password == "") {
                password_error.visibility = View.VISIBLE
                password_error.text = "Please Enter Password"
                return false
            } else {
                password_error.visibility = View.GONE
            }

            if (cm_password != password) {
                password_error.visibility = View.VISIBLE
                password_error.text = "Please Enter Valid Password"
                return false
            } else {
                password_error.visibility = View.GONE
                registerBinding.cmPasswordError.visibility = View.GONE
            }
            return true
        }


    private val isComPasswordValidate: Boolean
        get() {
            password_error.visibility = View.GONE
            val cm_password = registerBinding.edtCmPassword.text.toString()
            val password = registerBinding.edtPassword.text.toString()
            if (cm_password == "") {
                registerBinding.cmPasswordError.visibility = View.VISIBLE
                registerBinding.cmPasswordError.text = "Please Enter Confirm Password"
                return false
            } else {
                registerBinding.cmPasswordError.visibility = View.GONE
            }

            if (cm_password != password) {
                registerBinding.cmPasswordError.visibility = View.VISIBLE
                registerBinding.cmPasswordError.text = "Please Enter Valid Confirm Password"
                return false
            } else {
                registerBinding.cmPasswordError.visibility = View.GONE
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