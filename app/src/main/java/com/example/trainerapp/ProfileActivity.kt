package com.example.trainerapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.databinding.ActivityProfileBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {
    lateinit var profileBinding: ActivityProfileBinding
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    lateinit var progress_bar: ProgressBar
    lateinit var back: ImageView
    lateinit var preferenceManager: PreferencesManager
    private lateinit var user: ArrayList<String>
    lateinit var adapter: ShowSportAdaper

    private lateinit var Sportlist: java.util.ArrayList<Sport_list>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileBinding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(profileBinding.root)
        progress_bar = findViewById(R.id.progress_bar)
        back = findViewById(R.id.back)
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        preferenceManager = PreferencesManager(this)
        Sportlist = ArrayList()
        apiCall()
        profileBinding.editProfile.visibility = View.GONE
        user = intent.getStringArrayListExtra("list")!!
        profileBinding.cardPurchase.setOnClickListener {
            val intent = Intent(this, InAppPurchaseActivity::class.java)
            startActivity(intent)
        }

        profileBinding.imgEditProfile.setOnClickListener {
            profileBinding.editProfile.visibility = View.VISIBLE
        }

        back.setOnClickListener {
            finish()
        }
        profileBinding.cardFree.setOnClickListener {
//            val intent = Intent(this, MainHomeActivity::class.java)
//            startActivity(intent)
//            finish()
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }


        profileBinding.editProfile.setOnClickListener {
            profileBinding.editProfile.visibility = View.VISIBLE
            apiInterface.EditProfile(
                profileBinding.edtUserName.text.toString(),
                profileBinding.edtEmail.text.toString()
            )
                ?.enqueue(object : Callback<RegisterData?> {
                    override fun onResponse(
                        call: Call<RegisterData?>,
                        response: Response<RegisterData?>
                    ) {
                        Log.d("TAG", response.code().toString() + "")
                        val resource: RegisterData? = response.body()
                        val Success: Boolean = resource?.status!!
                        val Message: String = resource.message!!

                        finish()
                        startActivity(intent)
                    }

                    override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                        Toast.makeText(this@ProfileActivity, "" + t.message, Toast.LENGTH_SHORT)
                            .show()
                        call.cancel()
                        progress_bar.visibility = View.GONE
                    }
                })
        }

    }

    private fun apiCall() {
        progress_bar.visibility = View.VISIBLE
        apiInterface.ProfileData()?.enqueue(object : Callback<RegisterData?> {
            override fun onResponse(call: Call<RegisterData?>, response: Response<RegisterData?>) {
                Log.d("TAG", response.code().toString() + "")
                val resource: RegisterData? = response.body()
                val Success: Boolean = resource?.status!!
                val Message: String = resource.message!!
                setData(resource.data)
                if (!user.equals(null)) {
                    for (i in 0 until user.size) {
                        Sportlist.add(Sport_list(user[i]))
                    }
                    initRecyclerView(Sportlist)
                }
            }

            override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                Toast.makeText(this@ProfileActivity, "" + t.message, Toast.LENGTH_SHORT)
                    .show()
                call.cancel()
                progress_bar.visibility = View.GONE
            }
        })
    }

    private fun setData(data: RegisterData.Data?) {
        profileBinding.edtUserName.setText(data!!.name)
        profileBinding.edtEmail.setText(data.email)
    }

    private fun initRecyclerView(user: ArrayList<Sport_list>) {

        profileBinding.sportRv.layoutManager = GridLayoutManager(this, 2)
        adapter =
            ShowSportAdaper(user, this)
        profileBinding.sportRv.adapter = adapter
        progress_bar.visibility = View.GONE
    }
}
