package com.example.trainerapp.timer_section

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.EdiTimerActivity
import com.example.Library_TimerAdapter
import com.example.OnItemClickListener
import com.example.model.newClass.delete.DeleteBase
import com.example.model.newClass.timer.Timer
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.R
import com.example.trainerapp.Utils
import com.example.trainerapp.ViewTimerDetailsActivity
import com.example.trainerapp.databinding.ActivityCreateTimerBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateTimerActivity : AppCompatActivity(), OnItemClickListener.OnItemClickCallback {
    lateinit var createTimerBinding: ActivityCreateTimerBinding
    lateinit var adapter: Library_TimerAdapter
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    lateinit var Progress: ProgressBar
    lateinit var timerData: MutableList<Timer.TimerData>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createTimerBinding = ActivityCreateTimerBinding.inflate(layoutInflater)
        setContentView(createTimerBinding.root)
        initViews()
        loadData()
        checkRefresh()
        createNewTimer()
        initRecycler(timerData)
    }

    override fun onResume() {
        checkUser()
        super.onResume()
    }

    private fun checkUser() {
        try {
            apiInterface.ProfileData()?.enqueue(object : Callback<RegisterData?> {
                override fun onResponse(
                    call: Call<RegisterData?>,
                    response: Response<RegisterData?>
                ) {
                    Log.d("TAG", response.code().toString() + "")
                    val code = response.code()
                    if (code == 200) {
                        Log.d("Get Profile Data ", "${response.body()}")
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@CreateTimerActivity)
                    } else {
                        Toast.makeText(
                            this@CreateTimerActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Toast.makeText(
                        this@CreateTimerActivity,
                        "" + t.message,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    call.cancel()
                }
            })
        } catch (e: Exception) {
            Log.d("Exception", "${e.message}")
        }
    }

    private fun createNewTimer() {
        createTimerBinding.createTimer.setOnClickListener {
            startActivity(Intent(this, CreateNewTimerActivity::class.java))
        }

        createTimerBinding.back.setOnClickListener {
            finish()
        }
    }

    private fun checkRefresh() {
        createTimerBinding.scrollView.setOnRefreshListener {
            loadData()
        }
    }

    override fun onRestart() {
        loadData()
        super.onRestart()
    }

    private fun loadData() {
        getTimerData()
        createTimerBinding.scrollView.isRefreshing = false
    }

    fun initRecycler(timerData: MutableList<Timer.TimerData>) {
        adapter = Library_TimerAdapter(timerData, this, this)
        createTimerBinding.rlyExercise.layoutManager = LinearLayoutManager(this)
        createTimerBinding.rlyExercise.adapter = adapter
    }

    private fun getTimerData() {
        Progress.visibility = View.VISIBLE
        timerData.clear()
        try {
            apiInterface.GetTimerData().enqueue(
                object : Callback<Timer> {
                    override fun onResponse(call: Call<Timer>, response: Response<Timer>) {
                        val code = response.code()
                        if (code == 200) {
                            if (response.isSuccessful) {
                                Progress.visibility = View.GONE
                                val body = response.body()
                                if (body!!.data != null) {
                                    val data = body.data!!
                                    Log.d("Timer Data :-", data.size.toString())
                                    for (i in data) {
                                        timerData.add(i)
                                        Log.d("Timer Data :-", i.name.toString())
                                    }
                                    initRecycler(timerData)
                                } else {
                                    initRecycler(mutableListOf())
                                }
                            }
                        } else if (code == 403) {
                            Utils.setUnAuthDialog(this@CreateTimerActivity)
                        } else {
                            Toast.makeText(
                                this@CreateTimerActivity,
                                "" + response.message(), Toast.LENGTH_SHORT
                            ).show()
                            call.cancel()
                        }
                    }

                    override fun onFailure(call: Call<Timer>, t: Throwable) {
                        Progress.visibility = View.GONE
                        call.cancel()
                        Log.d("TAG Category", t.message.toString() + "")
                    }
                }
            )
        } catch (e: Exception) {
            Log.d("Error :-", "${e.message}")
        }
    }

    private fun initViews() {
        apiClient = APIClient(this)
        Progress = createTimerBinding.Progress
        apiInterface = apiClient.client().create(APIInterface::class.java)
        timerData = mutableListOf()
    }

    override fun onItemClicked(view: View, position: Int, type: Long, string: String) {
        when (string) {
            "Delete" -> {
                val builder: AlertDialog.Builder
                builder = AlertDialog.Builder(this)
                builder.setMessage("Are you sure you want to delete Timer?").setTitle("Delete")
                builder.setMessage("Are you sure you want to delete Timer?")
                    .setCancelable(false)
                    .setPositiveButton("Yes") { dialog, id ->
                        try {
                            deleteTimer(type, dialog)
                        } catch (e: Exception) {
                            Log.d("Error :-", "${e.message}")
                        }
                    }
                    .setNegativeButton(
                        "No"
                    ) { dialog, id ->
                        dialog.dismiss()
                    }
                val alert = builder.create()
                val titleTextView = TextView(this).apply {
                    text = "Delete"
                    typeface =
                        ResourcesCompat.getFont(this@CreateTimerActivity, R.font.poppins_medium) // Set the font
                    textSize = 20f
                    setPadding(50, 50, 50, 5) // Optional: add padding
                    setTextColor(Color.BLACK) // Set text color to black
                }



                alert.setCustomTitle(titleTextView)


                val typeface = ResourcesCompat.getFont(this, R.font.poppins_medium)


                alert.setOnShowListener {
                    val titleTextView = alert.findViewById<TextView>(android.R.id.title)
                    titleTextView?.typeface = typeface


                    val messageTextView = alert.findViewById<TextView>(android.R.id.message)
                    messageTextView?.typeface = typeface

                    // Set the font for the buttons
                    val positiveButton = alert.getButton(AlertDialog.BUTTON_POSITIVE)
                    positiveButton?.typeface = typeface

                    val negativeButton = alert.getButton(AlertDialog.BUTTON_NEGATIVE)
                    negativeButton?.typeface = typeface
                }
                alert.show()
            }

            "Edit" -> {
                val intent = Intent(this, EdiTimerActivity::class.java)
                intent.putExtra("timerId", type.toInt())
                startActivity(intent)
//                Toast.makeText(this@CreateTimerActivity, "edit $type click", Toast.LENGTH_SHORT)
//                    .show()
            }

            "View" -> {
                val data = timerData.find { it.id == type.toInt() }
                Log.d(
                    "view data :-", data.toString()
                )

                if (data != null) {
                    Log.d("Passing Data", "${data.name} \t ${data.id}")
                    val intent = Intent(this, ViewTimerDetailsActivity::class.java)
                    intent.putExtra("timerId", type.toInt())
                    intent.putExtra("timerData", data)
                    startActivity(intent)
                } else {
                    Log.d("Error", "Selected timerData is null")
                }

//                val intent = Intent(this, ViewTimerDetailsActivity::class.java)
//                intent.putExtra("timerData", data)
//                intent.putExtra("timerId", type.toInt())
//                startActivity(intent)
//                try {
//
//                } catch (e: Exception) {
//                    Log.d("exception :- ", e.message.toString())
//                }
            }
        }
    }

    private fun deleteTimer(type: Long, dialog: DialogInterface) {
        Progress.visibility = View.VISIBLE
        val id = type.toInt()
        apiInterface.DeleteTimer(id = id).enqueue(object : Callback<DeleteBase> {
            override fun onResponse(call: Call<DeleteBase>, response: Response<DeleteBase>) {
                Log.d("TAG", response.code().toString() + "")
                Progress.visibility = View.GONE
                val code = response.code()
                if (code == 200) {
                    val resource = response.body()
                    val Message: String = resource!!.message!!
                    Progress.visibility = View.GONE
                    dialog.dismiss()
                    Toast.makeText(
                        this@CreateTimerActivity,
                        "" + Message,
                        Toast.LENGTH_SHORT
                    ).show()
                    loadData()
                } else if (code == 403) {
                    Utils.setUnAuthDialog(this@CreateTimerActivity)
                } else {
                    Toast.makeText(
                        this@CreateTimerActivity,
                        "" + response.message(), Toast.LENGTH_SHORT
                    ).show()
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<DeleteBase>, t: Throwable) {
                Progress.visibility = View.GONE
                dialog.dismiss()
                Toast.makeText(
                    this@CreateTimerActivity,
                    "" + t.message,
                    Toast.LENGTH_SHORT
                ).show()
                call.cancel()
            }

        })
    }
}