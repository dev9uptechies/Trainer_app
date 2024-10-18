package com.example.trainerapp

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.SportlistData
import com.example.trainerapp.databinding.ActivitySelectSportListBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SelectSportListActivity : AppCompatActivity() {
    lateinit var selectSportListBinding: ActivitySelectSportListBinding
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    private lateinit var id_list: java.util.ArrayList<Id_list>
    private lateinit var sp_list: java.util.ArrayList<Sport_list>
    lateinit var preferenceManager: PreferencesManager
    lateinit var progress_bar: ProgressBar
    lateinit var EditText: EditText
    lateinit var dialog: AlertDialog
    lateinit var adapter: SportListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectSportListBinding = ActivitySelectSportListBinding.inflate(layoutInflater)
        setContentView(selectSportListBinding.root)
        initViews()
        getSportData()
        checkButtonClick()


    }

    private fun checkButtonClick() {
        selectSportListBinding.close.setOnClickListener {
            finish()
        }

        selectSportListBinding.btnAddSport.setOnClickListener {
            EditText = EditText(this)
            dialog = AlertDialog.Builder(this)
                .setTitle("Title")
                .setMessage("Message")
                .setView(EditText)
                .setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->
                    progress_bar.visibility = View.VISIBLE
                    val editTextInput: String = EditText.getText().toString()

                    apiInterface.AddSport(editTextInput)
                        ?.enqueue(object : Callback<SportlistData?> {
                            override fun onResponse(
                                call: Call<SportlistData?>,
                                response: Response<SportlistData?>
                            ) {
                                Log.d("TAG", response.code().toString() + "")
                                val code = response.code()
                                if (code == 200) {
                                    val resource: SportlistData? = response.body()
                                    val Success: Boolean = resource?.status!!
                                    val Message: String = resource.message!!
                                    progress_bar.visibility = View.GONE
                                    Toast.makeText(
                                        this@SelectSportListActivity,
                                        "" + Message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    if (resource.status!!.equals(true)) {
                                        finish()
                                        startActivity(intent)
                                    }
                                } else if (response.code() == 403) {
                                    Utils.setUnAuthDialog(this@SelectSportListActivity)
//                        val message = response.message()
//                        Toast.makeText(
//                            this@SelectSportListActivity,
//                            "" + message,
//                            Toast.LENGTH_SHORT
//                        )
//                            .show()
//                        call.cancel()
//                        startActivity(
//                            Intent(
//                                this@SelectSportListActivity,
//                                SignInActivity::class.java
//                            )
//                        )
//                        finish()
                                } else {
                                    progress_bar.visibility = View.GONE
                                    val message = response.message()
                                    Toast.makeText(
                                        this@SelectSportListActivity,
                                        "" + message,
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    call.cancel()
                                }
                            }


                            override fun onFailure(call: Call<SportlistData?>, t: Throwable) {
                                Toast.makeText(
                                    this@SelectSportListActivity,
                                    "" + t.message,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                call.cancel()
                                progress_bar.visibility = View.GONE
                            }
                        })

                })
                .setNegativeButton("Cancel", null)
                .create()
            dialog.show()
        }
        selectSportListBinding.btnDone.setOnClickListener {
            startActivity(Intent(this, SelectSportActivity::class.java))
            finish()
        }
    }

    private fun getSportData() {
        try {
            apiInterface.sportlist()?.enqueue(object : Callback<SportlistData?> {
                override fun onResponse(
                    call: Call<SportlistData?>,
                    response: Response<SportlistData?>
                ) {
                    Log.d("TAG", response.code().toString() + "")
                    val code = response.code()
                    if (code == 200) {
                        val resource: SportlistData? = response.body()
                        val Success: Boolean = resource?.status!!
                        val Message: String = resource.message!!
                        if (resource.data != null) {
                            selectSportListBinding.notdata.visibility = View.GONE
                            val data = resource.data
                            if (data!!.isNotEmpty()) {
                                initRecycler(resource.data, id_list, sp_list)
                            } else {
                                Toast.makeText(
                                    this@SelectSportListActivity,
                                    "No Data Found",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            selectSportListBinding.notdata.visibility = View.VISIBLE
                            progress_bar.visibility = View.GONE
                        }
                    } else if (response.code() == 403) {
                        Utils.setUnAuthDialog(this@SelectSportListActivity)
//                        val message = response.message()
//                        Toast.makeText(
//                            this@SelectSportListActivity,
//                            "" + message,
//                            Toast.LENGTH_SHORT
//                        )
//                            .show()
//                        call.cancel()
//                        startActivity(
//                            Intent(
//                                this@SelectSportListActivity,
//                                SignInActivity::class.java
//                            )
//                        )
//                        finish()
                    } else {
                        progress_bar.visibility = View.GONE
                        val message = response.message()
                        Toast.makeText(
                            this@SelectSportListActivity,
                            "" + message,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }


                override fun onFailure(call: Call<SportlistData?>, t: Throwable) {
                    Toast.makeText(this@SelectSportListActivity, "" + t.message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                    progress_bar.visibility = View.GONE
                }
            })
        } catch (e: Exception) {
            Log.d("Error Exception", "${e.message}")
        }
    }

    private fun initViews() {
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        progress_bar = findViewById(R.id.progress_bar)
        preferenceManager = PreferencesManager(this)
        progress_bar.visibility = View.VISIBLE
        id_list = ArrayList()
        sp_list = ArrayList()
        if (preferenceManager.getdata()) {
            id_list = intent.getParcelableArrayListExtra<Id_list>("list")!!
            sp_list = intent.getParcelableArrayListExtra<Sport_list>("slist")!!
        }
    }

    private fun initRecycler(
        data: ArrayList<SportlistData.sportlist>?,
        idList: ArrayList<Id_list>,
        spList: ArrayList<Sport_list>
    ) {

        selectSportListBinding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter =
            SportListAdapter(data, idList, spList, this)
        selectSportListBinding.recyclerView.adapter = adapter
        progress_bar.visibility = View.GONE
    }

    fun getPreferenceObjectJson(c: Context, key: String?): List<String> {
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
}