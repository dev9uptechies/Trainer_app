package com.example.trainerapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
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
import com.example.trainerapp.databinding.ActivitySelectSportBinding
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SelectSportActivity : AppCompatActivity() {
    lateinit var selectSportBinding: ActivitySelectSportBinding
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    lateinit var preferenceManager: PreferencesManager
    lateinit var progress_bar: ProgressBar
    private lateinit var user: ArrayList<String>
    private lateinit var id: ArrayList<Int>
    lateinit var back: ImageView
    private lateinit var dsf: Array<Int?>
    private lateinit var Sportlist: java.util.ArrayList<Sport_list>
    private lateinit var id_list: java.util.ArrayList<Id_list>
    lateinit var adapter: ShowSportAdaper
    lateinit var list1: ArrayList<Int>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectSportBinding = ActivitySelectSportBinding.inflate(layoutInflater)
        setContentView(selectSportBinding.root)
        initViews()
        checkButtonClick()
    }

    private fun checkButtonClick() {
        back.setOnClickListener {
            finish()
        }

        selectSportBinding.lySelect.setOnClickListener {
            preferenceManager.setdata(true)
            startActivity(
                Intent(this, SelectSportListActivity::class.java).putExtra(
                    "list",
                    id_list
                ).putExtra("slist", Sportlist)
            )

        }

        selectSportBinding.lySelectSport.setOnClickListener {
            preferenceManager.setdata(false)
            startActivity(
                Intent(this, SelectSportListActivity::class.java).putExtra(
                    "list",
                    id_list
                ).putExtra("slist", Sportlist)
            )
        }
    }

    private fun initViews() {
        back = findViewById(R.id.back)
        apiClient = APIClient(this)
        progress_bar = findViewById(R.id.progress_bar)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        preferenceManager = PreferencesManager(this)
        Sportlist = ArrayList()
        id_list = ArrayList()
        list1 = ArrayList()

        if (preferenceManager.getselectsport().equals(false)) {
            selectSportBinding.lySelectSport.visibility = View.VISIBLE
            selectSportBinding.lyRecyclerView.visibility = View.GONE
            selectSportBinding.igPlus.visibility = View.GONE
            selectSportBinding.cardNext.setCardBackgroundColor(resources.getColor(R.color.grey))
        } else {
            selectSportBinding.cardNext.setCardBackgroundColor(resources.getColor(R.color.splash_text_color))
            selectSportBinding.lySelectSport.visibility = View.GONE
            selectSportBinding.lyRecyclerView.visibility = View.VISIBLE
            selectSportBinding.igPlus.visibility = View.VISIBLE
            user = getPreferenceObjectJson(this, "Set") as ArrayList<String>
            id = getObjectJson(this, "setid") as ArrayList<Int>

            selectSportBinding.cardNext.setOnClickListener {
                progress_bar.visibility = View.VISIBLE
                val str = arrayOfNulls<Int>(id.size)
                val array = JsonArray()

                for (i in 0 until id.size) {
                    str[i] = id.get(i)
                    array.add(id.get(i))
                }
                val `object` = JsonObject()
                `object`.add("sport_ids", array)
                apiInterface.SelectSport(`object`)?.enqueue(object : Callback<RegisterData?> {
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
                            if (Success.equals(true)) {
                                progress_bar.visibility = View.GONE
                                val intent =
                                    Intent(this@SelectSportActivity, ProfileActivity::class.java)
                                intent.putExtra("list", user)
                                startActivity(intent)
                                finish()
                            } else {
                                progress_bar.visibility = View.GONE
                                Toast.makeText(
                                    this@SelectSportActivity,
                                    "" + Message,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        } else if (response.code() == 403) {
                            Utils.setUnAuthDialog(this@SelectSportActivity)
//                            val message = response.message()
//                            Toast.makeText(
//                                this@SelectSportActivity,
//                                "" + message,
//                                Toast.LENGTH_SHORT
//                            )
//                                .show()
//                            call.cancel()
//                            startActivity(
//                                Intent(
//                                    this@SelectSportActivity,
//                                    SignInActivity::class.java
//                                )
//                            )
//                            finish()
                        } else {
                            progress_bar.visibility = View.GONE
                            val message = response.message()
                            Toast.makeText(
                                this@SelectSportActivity,
                                "" + message,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            call.cancel()
                        }
                    }

                    override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                        progress_bar.visibility = View.GONE
                        Toast.makeText(
                            this@SelectSportActivity,
                            "" + t.message,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                })

            }

            if (!user.equals(null)) {
                for (i in 0 until user.size) {
                    Sportlist.add(Sport_list(user[i]))
                }
                initrecyclerview(Sportlist)
            }

            if (!id.equals(null)) {
                for (i in 0 until id.size) {
                    id_list.add(Id_list(id[i]))
                }
            }
        }
    }

    private fun initrecyclerview(user: ArrayList<Sport_list>) {

        selectSportBinding.sportRv.layoutManager = GridLayoutManager(this, 2)
        adapter =
            ShowSportAdaper(user, this)
        selectSportBinding.sportRv.adapter = adapter
    }

    fun getPreferenceObjectJson(c: Context, key: String): List<String> {
        val appSharedPrefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(
            c.applicationContext
        )

        /*** get user data     */
        val json = appSharedPrefs.getString(key, "")
        val gson = Gson()
        val type = object : TypeToken<List<String>>() {}.type
        val arrayList: List<String> = gson.fromJson<List<String>>(json, type)
        return arrayList
//        return gson.fromJson(
//            json,
//            ArrayList<User_Data>()
//        )
    }


    fun getObjectJson(c: Context, key: String): List<Int> {
        val appSharedPrefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(
            c.applicationContext
        )

        /*** get user data     */
        val json = appSharedPrefs.getString(key, "")
        val gson = Gson()
        val type = object : TypeToken<List<Int>>() {}.type
        val arrayList: List<Int> = gson.fromJson<List<Int>>(json, type)
        return arrayList
//        return gson.fromJson(
//            json,
//            ArrayList<User_Data>()
//        )
    }
}