package com.example

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.model.Cycle
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.CycleData
import com.example.trainerapp.R
import com.example.trainerapp.SignInActivity
import com.example.trainerapp.databinding.ActivityCreateCycleBinding
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CreateCycleActivity : AppCompatActivity(), OnItemClickListener.OnItemClickCallback {
    lateinit var createCycleBinding: ActivityCreateCycleBinding
    lateinit var adapter: CycleItemAdapter
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    lateinit var tv_reps: TextView
    lateinit var edt_name: TextView
    lateinit var tv_pause_time: TextView
    lateinit var tv_weight: TextView
    lateinit var tv_pause_cycle: TextView
    lateinit var tv_distance: TextView
    lateinit var tv_time: TextView
    lateinit var rootlayout: LinearLayout
    private lateinit var id: ArrayList<Cycle>
    private lateinit var final: ArrayList<String>
    var size: Int? = 0
    var exercise_id: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createCycleBinding = ActivityCreateCycleBinding.inflate(layoutInflater)
        setContentView(createCycleBinding.root)
        id = ArrayList()
        final = ArrayList()
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        exercise_id = intent.getIntExtra("exercise_id", 0)
        createCycleBinding.cardAddCycle.setOnClickListener {
            createCycleBinding.rootLayout.setBackgroundColor(resources.getColor(R.color.grey))
            goalDialog()
        }
        id.add(
            Cycle(
                "0",
                exercise_id.toString(),
                "Enter Set/Round No.",
                "Time",
                "Reps",
                "Pause Time",
                "Weight",
                "Distance",
                "Pause Between Cycles"
            )
        )

        initrecycler()

        createCycleBinding.saveCycle.setOnClickListener {
            if (id.size != 0) {
                createCycleBinding.Progress.visibility = View.VISIBLE
                val array = id
                val gson = GsonBuilder().create()
                val myCustomArray: JsonArray = gson.toJsonTree(array).getAsJsonArray()
                apiInterface.CreateCycle(myCustomArray)?.enqueue(object : Callback<CycleData?> {
                    override fun onResponse(
                        call: Call<CycleData?>,
                        response: Response<CycleData?>
                    ) {
                        Log.d("TAG", response.code().toString() + "")
                        val code = response.code()
                        if (code == 200) {
                            val resource: CycleData? = response.body()
                            createCycleBinding.Progress.visibility = View.GONE
                            val Success: Boolean = resource?.status!!
                            val Message: String = resource.message!!
                            if (Success) {
                                Toast.makeText(
                                    this@CreateCycleActivity,
                                    "" + Message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                                startActivity(
                                    Intent(
                                        this@CreateCycleActivity,
                                        ExerciseActivity::class.java
                                    )
                                )
                                finish()
                            } else {
                                Toast.makeText(
                                    this@CreateCycleActivity,
                                    "" + Message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            createCycleBinding.Progress.visibility = View.GONE
                            val message = response.message()
                            Toast.makeText(
                                this@CreateCycleActivity,
                                "" + message,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            call.cancel()
                            startActivity(
                                Intent(
                                    this@CreateCycleActivity,
                                    SignInActivity::class.java
                                )
                            )
                            finish()
                        }

                    }

                    override fun onFailure(call: Call<CycleData?>, t: Throwable) {
                        Toast.makeText(
                            this@CreateCycleActivity,
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

    private fun goalDialog() {
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
        rootlayout = dialog.findViewById<LinearLayout>(R.id.rootlayout)

        val ly_set = dialog.findViewById<CardView>(R.id.ly_name)
        val ly_time = dialog.findViewById<CardView>(R.id.ly_time)
        val ly_reps = dialog.findViewById<CardView>(R.id.ly_reps)
        val ly_pause_time = dialog.findViewById<CardView>(R.id.ly_pause_time)
        val ly_weight = dialog.findViewById<CardView>(R.id.ly_weight)
        val ly_distance = dialog.findViewById<CardView>(R.id.ly_distance)
        val ly_pause_cycle = dialog.findViewById<CardView>(R.id.ly_pause_cycle)

        val add = dialog.findViewById<CardView>(R.id.card_add)

        updateAddButtonState(add)

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
            updateAddButtonState(add)
        }

        ly_time.setOnClickListener {
            rootlayout.setBackgroundColor(resources.getColor(R.color.grey))
            SetDialog("time")
            updateAddButtonState(add)
        }

        ly_reps.setOnClickListener {
            rootlayout.setBackgroundColor(resources.getColor(R.color.grey))
            SetDialog("reps")
            updateAddButtonState(add)
        }

        ly_pause_time.setOnClickListener {
            rootlayout.setBackgroundColor(resources.getColor(R.color.grey))
            SetDialog("pause_time")
            updateAddButtonState(add)
        }

        ly_weight.setOnClickListener {
            rootlayout.setBackgroundColor(resources.getColor(R.color.grey))
            SetDialog("weight")
            updateAddButtonState(add)
        }

        ly_distance.setOnClickListener {
            rootlayout.setBackgroundColor(resources.getColor(R.color.grey))
            SetDialog("distance")
            updateAddButtonState(add)
        }

        ly_pause_cycle.setOnClickListener {
            rootlayout.setBackgroundColor(resources.getColor(R.color.grey))
            SetDialog("pause_cycle")
            updateAddButtonState(add)
        }

        cancel.setOnClickListener {
            dialog.dismiss()
            createCycleBinding.rootLayout.setBackgroundColor(resources.getColor(R.color.black))
        }

        add.setOnClickListener {
            if (areAllFieldsFilled()) {
                if (id.size == 1) {
                    if (id[0].time.equals("Time")) {
                        id.clear()
                        id.add(
                            Cycle(
                                "0",
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
                    } else {
                        id.add(
                            Cycle(
                                "0",
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
                    }

                } else {
                    id.add(
                        Cycle(
                            "0",
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
                }
                initrecycler()
                dialog.dismiss()
                createCycleBinding.rootLayout.setBackgroundColor(resources.getColor(R.color.black))
            }
        }

        edt_name.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    updateAddButtonState(add)
                }

                override fun afterTextChanged(s: Editable?) {}
            }
        )
        tv_reps.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    updateAddButtonState(add)
                }

                override fun afterTextChanged(s: Editable?) {}
            }
        )
        tv_pause_cycle.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    updateAddButtonState(add)
                }

                override fun afterTextChanged(s: Editable?) {}
            }
        )
        tv_time.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    updateAddButtonState(add)
                }

                override fun afterTextChanged(s: Editable?) {}
            }
        )
        tv_pause_time.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    updateAddButtonState(add)
                }

                override fun afterTextChanged(s: Editable?) {}
            }
        )

    }

    private fun areAllFieldsFilled(): Boolean {
        return !(edt_name.text.isNullOrEmpty() ||
                tv_time.text.toString() == "Time" ||
                tv_reps.text.toString() == "Reps" ||
                tv_pause_time.text.toString() == "Pause Time" ||
                tv_pause_cycle.text.toString() == "Pause Cycle")
    }

    private fun updateAddButtonState(addButton: CardView) {
        if (areAllFieldsFilled()) {
            addButton.isEnabled = true
            addButton.setCardBackgroundColor(resources.getColor(R.color.splash_text_color)) // Change to your desired color
        } else {
            addButton.isEnabled = false
            addButton.setCardBackgroundColor(resources.getColor(R.color.grey)) // Disabled color
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

    private fun initrecycler() {
//        createCycleBinding.recyclerView.layoutManager = LinearLayoutManager(this)
//        adapter =
//            CycleItemAdapter(id.toMutableList(), this, this)
//        createCycleBinding.recyclerView.adapter = adapter
    }

    override fun onItemClicked(view: View, position: Int, type: Long, string: String) {

    }
}