package com.example.trainerapp.privacy_policy

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.R
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.ActivityPrivacyPolicyBinding
import com.example.model.PrivacyPolicy.privacypolicy
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PrivacyPolicyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPrivacyPolicyBinding
    private lateinit var apiInterface: APIInterface
    private var privacyPolicyData: privacypolicy.Data? = null // Variable to store fetched data

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivacyPolicyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initview()
        loadData()
        ButtonClick()
    }

    private fun ButtonClick() {
        binding.back.setOnClickListener { finish() }
    }

    private fun initview() {
        apiInterface = APIClient(this).client().create(APIInterface::class.java)
    }

    private fun loadData() {
        getPrivacyPolicy()
    }

    private fun getPrivacyPolicy() {
        try {
            apiInterface.GetPrivacyPolicy()?.enqueue(object : Callback<privacypolicy> {
                override fun onResponse(call: Call<privacypolicy>, response: Response<privacypolicy>) {
                    Log.d("APIResponse", "Response Code: ${response.code()}")

                    when (response.code()) {
                        200 -> {
                            privacyPolicyData = response.body()?.data
                            if (privacyPolicyData != null) {
                                // Bind the fetched data to the UI
                                bindDataToUI(privacyPolicyData!!)
                            } else {
                                Log.d("APIResponse", "Response data is null")
                                Toast.makeText(
                                    this@PrivacyPolicyActivity,
                                    "No data found",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        403 -> {
                            Utils.setUnAuthDialog(this@PrivacyPolicyActivity)
                        }

                        else -> {
                            val errorMessage = response.message() ?: "Unknown error occurred"
                            Log.d("APIResponse", "Error: $errorMessage")
                            Toast.makeText(
                                this@PrivacyPolicyActivity,
                                errorMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                override fun onFailure(call: Call<privacypolicy>, t: Throwable) {
                    Log.e("APIError", "Network error: ${t.message}")
                    Toast.makeText(
                        this@PrivacyPolicyActivity,
                        "Network error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } catch (e: Exception) {
            Log.e("APIException", "Exception: ${e.message}")
            Toast.makeText(
                this@PrivacyPolicyActivity,
                "An unexpected error occurred: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }



    private fun bindDataToUI(data: privacypolicy.Data) {
        val title = data.title ?: "No Title Available"
        val descriptionHtml = data.description ?: "No Description Available"

        val descriptionPlainText = HtmlCompat.fromHtml(descriptionHtml, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()

        binding.textView3.text = title
        binding.descriptionPp.text = descriptionPlainText
    }
}
