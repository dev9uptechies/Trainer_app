package com.example

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.model.Cycle
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.CycleData
import com.example.trainerapp.ApiClass.ExcerciseData
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.R
import com.example.trainerapp.databinding.ActivityEditCycleBinding
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditCycleActivity : AppCompatActivity(), OnItemClickListener.OnItemClickCallback {
    lateinit var editCycleBinding: ActivityEditCycleBinding
    lateinit var apiInterface: APIInterface
    lateinit var adapter: CycleItemAdapter
    lateinit var apiClient: APIClient
    lateinit var rootlayout: LinearLayout
    lateinit var tv_reps: TextView
    lateinit var edt_name: TextView
    lateinit var tv_pause_time: TextView
    lateinit var tv_weight: TextView
    lateinit var tv_pause_cycle: TextView
    lateinit var tv_distance: TextView
    lateinit var tv_time: TextView
    lateinit var exerciselist: ArrayList<ExcerciseData.Exercise>
    lateinit var generallist: ArrayList<ExcerciseData.Exercise>
    lateinit var specificlist: ArrayList<ExcerciseData.Exercise>
    var position: Int? = null
    var exercise_id: Int? = 0
    private lateinit var id: ArrayList<Cycle>
    lateinit var Progress_bar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        editCycleBinding = ActivityEditCycleBinding.inflate(layoutInflater)
        setContentView(editCycleBinding.root)
        Progress_bar = findViewById(R.id.Progress)
        generallist = ArrayList()
        specificlist = ArrayList()
        position = intent.getIntExtra("position", 0)
        exercise_id = intent.getIntExtra("exercise_id", 0)
        id = ArrayList()
        id.clear()
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        GetExercise()

        editCycleBinding.addCycle.setOnClickListener {
            val dialog = Dialog(this, R.style.Theme_Dialog)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setCanceledOnTouchOutside(false)
            dialog.setContentView(R.layout.add_cycle_dialog)
            val cancel = dialog.findViewById<CardView>(R.id.crad_cancel)
            edt_name = dialog.findViewById<TextView>(R.id.edt_name)
            tv_reps = dialog.findViewById<TextView>(R.id.tv_reps)
            tv_pause_time = dialog.findViewById<TextView>(R.id.tv_pause_time)
            tv_weight = dialog.findViewById<TextView>(R.id.tv_weight)
            tv_pause_cycle = dialog.findViewById<TextView>(R.id.tv_pause_cycle)
            tv_distance = dialog.findViewById<TextView>(R.id.tv_distance)
            tv_time = dialog.findViewById<TextView>(R.id.tv_time)
            val ly_set = dialog.findViewById<CardView>(R.id.ly_name)
            rootlayout = dialog.findViewById<LinearLayout>(R.id.rootlayout)
            val ly_time = dialog.findViewById<CardView>(R.id.ly_time)
            val ly_reps = dialog.findViewById<CardView>(R.id.ly_reps)
            val ly_pause_time = dialog.findViewById<CardView>(R.id.ly_pause_time)
            val ly_weight = dialog.findViewById<CardView>(R.id.ly_weight)
            val ly_distance = dialog.findViewById<CardView>(R.id.ly_distance)
            val ly_pause_cycle = dialog.findViewById<CardView>(R.id.ly_pause_cycle)
            val add = dialog.findViewById<CardView>(R.id.card_add)
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            val width = (displayMetrics.widthPixels * 0.9f).toInt()
            val height = WindowManager.LayoutParams.WRAP_CONTENT
            dialog.window!!.setLayout(width, height)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()

            ly_set.setOnClickListener {
                rootlayout.setBackgroundColor(resources.getColor(R.color.grey))
                SetDialog("set")

            }

            ly_time.setOnClickListener {
                rootlayout.setBackgroundColor(resources.getColor(R.color.grey))
                SetDialog("time")

            }

            ly_reps.setOnClickListener {
                rootlayout.setBackgroundColor(resources.getColor(R.color.grey))
                SetDialog("reps")

            }

            ly_pause_time.setOnClickListener {
                rootlayout.setBackgroundColor(resources.getColor(R.color.grey))
                SetDialog("pause_time")

            }

            ly_weight.setOnClickListener {
                rootlayout.setBackgroundColor(resources.getColor(R.color.grey))
                SetDialog("weight")

            }

            ly_distance.setOnClickListener {
                rootlayout.setBackgroundColor(resources.getColor(R.color.grey))
                SetDialog("distance")

            }

            ly_pause_cycle.setOnClickListener {
                rootlayout.setBackgroundColor(resources.getColor(R.color.grey))
                SetDialog("pause_cycle")

            }

            cancel.setOnClickListener {
                dialog.hide()
            }

            add.setOnClickListener {

                id.add(
                    Cycle(
                        "",
                        exercise_id.toString(),
                        edt_name.text.toString(),
                        tv_time.text.toString(),
                        tv_reps.text.toString(),
                        tv_pause_time.text.toString(),
                        tv_weight.text.toString(),
                        tv_distance.text.toString(),
                        tv_pause_cycle.text.toString()
                    )
                )
                callAPI("add")
                dialog.hide()
            }
        }

        editCycleBinding.saveCycle.setOnClickListener {
            if (id.size != 0) {
                Progress_bar.visibility = View.VISIBLE
                val array = id
                val gson = GsonBuilder().create()
                val myCustomArray: JsonArray = gson.toJsonTree(array).getAsJsonArray()
                apiInterface.EditCycle(myCustomArray)?.enqueue(object : Callback<CycleData?> {
                    override fun onResponse(
                        call: Call<CycleData?>,
                        response: Response<CycleData?>
                    ) {
                        Log.d("TAG", response.code().toString() + "")
                        val code = response.code()
                        if (code == 200) {
                            val resource: CycleData? = response.body()
                            Progress_bar.visibility = View.VISIBLE
                            val Success: Boolean = resource?.status!!
                            val Message: String = resource.message!!
                            if (Success) {
                                Toast.makeText(
                                    this@EditCycleActivity,
                                    "" + Message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                                startActivity(
                                    Intent(
                                        this@EditCycleActivity,
                                        ExerciseActivity::class.java
                                    )
                                )
                                finish()
                            } else {
                                Toast.makeText(
                                    this@EditCycleActivity,
                                    "" + Message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }

                    override fun onFailure(call: Call<CycleData?>, t: Throwable) {
                        Toast.makeText(
                            this@EditCycleActivity,
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

    private fun GetExercise() {
        Progress_bar.visibility = View.VISIBLE
        apiInterface.GetExercise()?.enqueue(object : Callback<ExcerciseData?> {
            override fun onResponse(
                call: Call<ExcerciseData?>,
                response: Response<ExcerciseData?>
            ) {
                Log.d("TAG", response.code().toString() + "")
                val resource: ExcerciseData? = response.body()
                val Success: Boolean = resource?.status!!
                val Message: String = resource.message!!
                if (Success == true) {
                    exerciselist = resource.data!!
                    for (i in 0 until exerciselist.size) {
                        if (exerciselist[i].type == "General") {
                            generallist.add(exerciselist[i])
                        } else {
                            specificlist.add(exerciselist[i])
                        }
                    }
                    Progress_bar.visibility = View.GONE
                    val cycle = generallist[position!!].cycles
                    if (cycle!!.size != 0) {
                        for (i in 0 until cycle.size) {
                            id.add(
                                Cycle(
                                    cycle[i].id!!.toString(),
                                    cycle[i].exercise_id!!, cycle[i].set!!,
                                    cycle[i].time!!, cycle[i].reps!!, cycle[i].pause!!,
                                    cycle[i].weight!!, cycle[i].distance!!,
                                    cycle[i].pause_cycle!!
                                )
                            )
                        }
                        initrecycler()
                    }
                }
            }

            override fun onFailure(call: Call<ExcerciseData?>, t: Throwable) {
                Toast.makeText(this@EditCycleActivity, "" + t.message, Toast.LENGTH_SHORT)
                    .show()
                call.cancel()
            }
        })
    }

    private fun initrecycler() {
        editCycleBinding.recyclerView.layoutManager = LinearLayoutManager(this)
//        adapter =
//            CycleItemAdapter(id.toMutableList(), this, this)
//        editCycleBinding.recyclerView.adapter = adapter
    }

    override fun onItemClicked(view: View, position: Int, type: Long, string: String) {
        if (string == "delete") {
            val builder: AlertDialog.Builder
            builder = AlertDialog.Builder(this)
            builder.setMessage("Are you sure you want to delete Cycle?").setTitle("Success")

            builder.setMessage("Are you sure you want to delete Cycle?")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialog, test ->
                    Progress_bar.visibility = View.VISIBLE
                    apiInterface.DeleteCycle(type.toInt(), id[position].exercise_id.toInt())
                        ?.enqueue(object : Callback<RegisterData?> {
                            override fun onResponse(
                                call: Call<RegisterData?>,
                                response: Response<RegisterData?>
                            ) {
                                Log.d("TAG", response.code().toString() + "")
                                val resource: RegisterData? = response.body()
                                val Success: Boolean = resource?.status!!
                                val Message: String = resource.message!!
                                Progress_bar.visibility = View.GONE
                                finish()
                                startActivity(
                                    Intent(
                                        this@EditCycleActivity,
                                        EditCycleActivity::class.java
                                    )
                                )

                            }

                            override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                                Toast.makeText(
                                    this@EditCycleActivity,
                                    "" + t.message,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                call.cancel()
                            }
                        })
                }
                .setNegativeButton(
                    "No"
                ) { dialog, id -> //  Action for 'NO' Button
                    dialog.cancel()
                }

            val alert = builder.create()
            alert.setTitle("Success")
            alert.show()

        } else {
            goalDialog(position)
        }
    }

    private fun goalDialog(position: Int) {
        val dialog = Dialog(this, R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(R.layout.add_cycle_dialog)
        val cancel = dialog.findViewById<CardView>(R.id.crad_cancel)
        edt_name = dialog.findViewById<TextView>(R.id.edt_name)
        tv_reps = dialog.findViewById<TextView>(R.id.tv_reps)
        tv_pause_time = dialog.findViewById<TextView>(R.id.tv_pause_time)
        tv_weight = dialog.findViewById<TextView>(R.id.tv_weight)
        tv_pause_cycle = dialog.findViewById<TextView>(R.id.tv_pause_cycle)
        tv_distance = dialog.findViewById<TextView>(R.id.tv_distance)
        tv_time = dialog.findViewById<TextView>(R.id.tv_time)
        val ly_set = dialog.findViewById<CardView>(R.id.ly_name)
        rootlayout = dialog.findViewById<LinearLayout>(R.id.rootlayout)
        val ly_time = dialog.findViewById<CardView>(R.id.ly_time)
        val ly_reps = dialog.findViewById<CardView>(R.id.ly_reps)
        val ly_pause_time = dialog.findViewById<CardView>(R.id.ly_pause_time)
        val ly_weight = dialog.findViewById<CardView>(R.id.ly_weight)
        val ly_distance = dialog.findViewById<CardView>(R.id.ly_distance)
        val ly_pause_cycle = dialog.findViewById<CardView>(R.id.ly_pause_cycle)
        val add = dialog.findViewById<CardView>(R.id.card_add)
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = (displayMetrics.widthPixels * 0.9f).toInt()
        val height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.setLayout(width, height)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        edt_name.text = id[position].set
        tv_reps.text = id[position].reps
        tv_pause_time.text = id[position].pause
        tv_weight.text = id[position].weight
        tv_pause_cycle.text = id[position].pause_cycle
        tv_distance.text = id[position].distance
        tv_time.text = id[position].time

        ly_set.setOnClickListener {
            rootlayout.setBackgroundColor(resources.getColor(R.color.grey))
            SetDialog("set")

        }

        ly_time.setOnClickListener {
            rootlayout.setBackgroundColor(resources.getColor(R.color.grey))
            SetDialog("time")

        }

        ly_reps.setOnClickListener {
            rootlayout.setBackgroundColor(resources.getColor(R.color.grey))
            SetDialog("reps")

        }

        ly_pause_time.setOnClickListener {
            rootlayout.setBackgroundColor(resources.getColor(R.color.grey))
            SetDialog("pause_time")

        }

        ly_weight.setOnClickListener {
            rootlayout.setBackgroundColor(resources.getColor(R.color.grey))
            SetDialog("weight")

        }

        ly_distance.setOnClickListener {
            rootlayout.setBackgroundColor(resources.getColor(R.color.grey))
            SetDialog("distance")

        }

        ly_pause_cycle.setOnClickListener {
            rootlayout.setBackgroundColor(resources.getColor(R.color.grey))
            SetDialog("pause_cycle")

        }

        cancel.setOnClickListener {
            dialog.hide()
        }

        add.setOnClickListener {

            id.set(
                position,
                Cycle(
                    id[position].id,
                    id[position].exercise_id,
                    edt_name.text.toString(),
                    tv_time.text.toString(),
                    tv_reps.text.toString(),
                    tv_pause_time.text.toString(),
                    tv_weight.text.toString(),
                    tv_distance.text.toString(),
                    tv_pause_cycle.text.toString()
                )
            )
            initrecycler()
            dialog.hide()
        }

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun SetDialog(set: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_set_number_picker)
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = (displayMetrics.widthPixels * 0.9f).toInt()
        val height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.setLayout(width, height)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val numberPicker = dialog.findViewById<NumberPicker>(R.id.npGoal)
        val btnApply = dialog.findViewById<Button>(R.id.btnApply)
        val tvReps = dialog.findViewById<TextView>(R.id.tvReps)
        val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)
        if (set == "set") {
            tvReps.text = "Round On"
        } else if (set == "time") {
            tvReps.text = "Time Picker"
        } else if (set == "reps") {
            tvReps.text = "Reps"
        } else if (set == "pause_time") {
            tvReps.text = "Time Picker"
        } else if (set == "weight") {
            tvReps.text = "Weight"
        } else if (set == "distance") {
            tvReps.text = "Distance"
        } else if (set == "pause_cycle") {
            tvReps.text = "Time Picker"
        }

        var selectedNumber = 0
        numberPicker.minValue = 1
        numberPicker.maxValue = 100
        numberPicker.wrapSelectorWheel = true
        numberPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            selectedNumber = newVal
        }

        btnCancel.setOnClickListener { v: View? ->
            rootlayout.setBackgroundResource(R.drawable.bg_black_round_corner_10)
            dialog.dismiss()
        }
        btnApply.setOnClickListener { view: View? ->
            rootlayout.setBackgroundResource(R.drawable.bg_black_round_corner_10)
            if (set == "set") {
                edt_name.text = selectedNumber.toString() + " Round"
            } else if (set == "time") {
                tv_time.text = selectedNumber.toString() + ":00:00"
            } else if (set == "reps") {
                tv_reps.text = selectedNumber.toString() + " Reps"
            } else if (set == "pause_time") {
                tv_pause_time.text = selectedNumber.toString() + " min"
            } else if (set == "weight") {
                tv_weight.text = selectedNumber.toString() + " KG"
            } else if (set == "distance") {
                tv_distance.text = selectedNumber.toString() + " KM"
            } else if (set == "pause_cycle") {
                tv_pause_cycle.text = selectedNumber.toString() + " min"
            }

            dialog.dismiss()
        }
        dialog.show()
    }


    private fun callAPI(type: String) {
        Progress_bar.visibility = View.VISIBLE
        val array = id
        val gson = GsonBuilder().create()
        val myCustomArray: JsonArray = gson.toJsonTree(array).getAsJsonArray()
        apiInterface.CreateCycle(myCustomArray)?.enqueue(object : Callback<CycleData?> {
            override fun onResponse(call: Call<CycleData?>, response: Response<CycleData?>) {
                Log.d("TAG", response.code().toString() + "")
                val code = response.code()
                if (code == 200) {
                    val resource: CycleData? = response.body()
                    Progress_bar.visibility = View.VISIBLE
                    val Success: Boolean = resource?.status!!
                    val Message: String = resource.message!!
                    if (Success) {
                        startActivity(Intent(this@EditCycleActivity, ExerciseActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(
                            this@EditCycleActivity,
                            "" + Message.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<CycleData?>, t: Throwable) {
                Toast.makeText(
                    this@EditCycleActivity,
                    "" + t.message,
                    Toast.LENGTH_SHORT
                )
                    .show()
                call.cancel()
            }
        })

    }

}