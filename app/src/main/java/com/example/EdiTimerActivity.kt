package com.example

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.model.newClass.audio.Audio
import com.example.model.newClass.cycle.AddCycle
import com.example.model.newClass.delete.DeleteBase
import com.example.model.newClass.timer.Timer
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.Data
import com.example.trainerapp.ApiClass.EditTimer
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.PreferencesManager
import com.example.trainerapp.R
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.ActivityCreateNewTimerBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EdiTimerActivity : AppCompatActivity(), OnItemClickListener.OnItemClickCallback {
    lateinit var editTimerBinding: ActivityCreateNewTimerBinding
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    lateinit var Progress: ProgressBar

    lateinit var tv_reps: TextView
    lateinit var edt_name: TextView
    lateinit var tv_pause_time: TextView
    lateinit var tv_weight: TextView
    lateinit var tv_pause_cycle: TextView
    lateinit var tv_distance: TextView
    lateinit var tv_time: TextView
    lateinit var rootlayout: LinearLayout

    lateinit var musicData: MutableList<Audio.AudioData>
    lateinit var adapter: Library_AudioAdapter
    lateinit var cycleAdapter: CycleItemAdapter
    lateinit var list: MutableList<AddCycle>
    lateinit var newList: MutableList<Data>
    lateinit var timeData: Timer.TimerData

    var timerId: String? = null
    var audio_id = 0
    var pause_audio_id = 0
    var pause_between_id = 0
    var size = 0
    var userId: Int? = null
    lateinit var preferenceManager: PreferencesManager
    private lateinit var id: MutableList<AddCycle>

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
                        Utils.setUnAuthDialog(this@EdiTimerActivity)
                    } else {
                        Toast.makeText(
                            this@EdiTimerActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Toast.makeText(
                        this@EdiTimerActivity,
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

    override fun onResume() {
        checkUser()
        super.onResume()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        editTimerBinding = ActivityCreateNewTimerBinding.inflate(layoutInflater)
        setContentView(editTimerBinding.root)
        initViews()
        getTimerData()
        getSongData()
        checkAudioClick()

        editTimerBinding.back.setOnClickListener {
            finish()
        }

        editTimerBinding.createCycle.setOnClickListener {
            editTimerBinding.main.setBackgroundColor(resources.getColor(R.color.grey))
            goalDialog()
        }

        editTimerBinding.addCycle.setOnClickListener {
            editTimerBinding.main.setBackgroundColor(resources.getColor(R.color.grey))
            goalDialog()
        }
        editTimerBinding.saveCycle.setOnClickListener {
            newList = mutableListOf()
            newList.clear()
            if (id.size == 1 && (editTimerBinding.edtTimerName.text.toString()
                    .isEmpty() || editTimerBinding.edtAudio.text.toString()
                    .isEmpty() || editTimerBinding.edtPauseAudio.toString()
                    .isEmpty() || editTimerBinding.edtPauseBetween.toString().isEmpty())
            ) {
                Toast.makeText(this, "Please Fill All Data", Toast.LENGTH_SHORT).show()
            } else if (id.size == 1 && (id[0].time == "Time" || id[0].set == "Enter Set/Round No." || id[0].reps == "Reps" || id[0].pause == "Pause Time" || id[0].pause_timer == "Pause Between Cycles")) {
                Toast.makeText(this, "Please Enter Cycle Data", Toast.LENGTH_SHORT).show()
            } else {
                val name = editTimerBinding.edtTimerName.text.toString().trim()
                newList = id.map {
                    Data(
                        id = it.id,
                        set = it.set,
                        time = it.time,
                        reps = it.reps,
                        pause = it.pause,
                        weight = it.weight ?: "",
                        distance = it.distance ?: "",
                        pause_timer = it.pause_timer
                    )
                }.toMutableList()
                try {
                    apiInterface.UpdateTimer(
                        editTimer = EditTimer(
                            id = timerId!!.toInt(),
                            name = name,
                            audio_id = audio_id,
                            pause_time_audio_id = pause_audio_id,
                            pause_between_time_audio_id = pause_between_id,
                            data = newList
                        )
                    ).enqueue(object : Callback<Timer> {
                        override fun onResponse(call: Call<Timer>, response: Response<Timer>) {
                            editTimerBinding.Progress.visibility = View.GONE
                            val code = response.code()
                            if (code == 200) {
                                Log.d("TAG", "onResponse: ${response.message()}")
                                Log.d("TAG", "onResponse: ${response.body()}")
                                Log.d("TAG", "onResponse: ${response.code()}")
                                Log.d("TAG", "onResponse: ${response.errorBody().toString()}")
                                Log.d("TAG", "onResponse: ${response.body()!!.status}")
                                if (response.isSuccessful) {
                                    val message = response.body()!!.message
                                    Toast.makeText(
                                        this@EdiTimerActivity,
                                        message + "",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    finish()
                                } else {
                                    finish()
                                    Toast.makeText(
                                        this@EdiTimerActivity,
                                        "Something Went Wrong",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else if (code == 403) {
                                Utils.setUnAuthDialog(this@EdiTimerActivity)
                            } else {
                                Toast.makeText(
                                    this@EdiTimerActivity,
                                    "" + response.message(),
                                    Toast.LENGTH_SHORT
                                ).show()
                                call.cancel()
                            }
                        }

                        override fun onFailure(call: Call<Timer>, t: Throwable) {
                            editTimerBinding.Progress.visibility = View.GONE
                            Log.d("TAG", "onResponse failure: ${t}")
                        }

                    })
                } catch (e: Exception) {
                    Log.d("TAG", "onCreate: $e")
                }
            }
        }
    }

    private fun checkAudioClick() {
        editTimerBinding.edtAudio.setOnClickListener {
            openAudioDialog(editTimerBinding.edtAudio, "audio_id")
        }
        editTimerBinding.edtPauseAudio.setOnClickListener {
            openAudioDialog(editTimerBinding.edtPauseAudio, "pause_audio_id")
        }
        editTimerBinding.edtPauseBetween.setOnClickListener {
            openAudioDialog(editTimerBinding.edtPauseBetween, "pause_between_id")
        }
    }

    private fun openAudioDialog(editText: EditText, idRef: String) {
        val dialog = Dialog(this, R.style.CustomDialogTheme)
        dialog.setContentView(R.layout.dialog_audio_picker)
        val recycler = dialog.findViewById<RecyclerView>(R.id.ry_audio)
        recycler.layoutManager = LinearLayoutManager(this)
        adapter = Library_AudioAdapter(musicData, this)
        recycler.adapter = adapter
        val cancel = dialog.findViewById<View>(R.id.btnCancel)
        val apply = dialog.findViewById<View>(R.id.btnApply)
        apply.setOnClickListener {
            if (adapter.selected != 0) {
                when (idRef) {
                    "audio_id" -> audio_id = adapter.selected
                    "pause_audio_id" -> pause_audio_id = adapter.selected
                    "pause_between_id" -> pause_between_id = adapter.selected
                }
                editText.setText(musicData.find { it.id == adapter.selected }!!.name)
            }
            dialog.dismiss()

        }
        cancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setCancelable(true)
        dialog.show()
    }

    private fun goalDialog() {
        val dialog = Dialog(this, R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
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
            //rootlayout.setBackgroundColor(resources.getColor(R.color.grey))
            SetDialog("set")
        }

        ly_time.setOnClickListener {
            //rootlayout.setBackgroundColor(resources.getColor(R.color.grey))
            setTimerDialog("time")
        }

        ly_reps.setOnClickListener {
//            rootlayout.setBackgroundColor(resources.getColor(R.color.grey))
            SetDialog("reps")
        }

        ly_pause_time.setOnClickListener {
//            rootlayout.setBackgroundColor(resources.getColor(R.color.grey))
            setShortTimerDialog("pause_time")
        }

        ly_weight.setOnClickListener {
//            rootlayout.setBackgroundColor(resources.getColor(R.color.grey))
            SetDialog("weight")
        }

        ly_distance.setOnClickListener {
//            rootlayout.setBackgroundColor(resources.getColor(R.color.grey))
            SetDialog("distance")
        }

        ly_pause_cycle.setOnClickListener {
//            rootlayout.setBackgroundColor(resources.getColor(R.color.grey))
            setShortTimerDialog("pause_cycle")
        }

        cancel.setOnClickListener {
            dialog.dismiss()
            editTimerBinding.main.setBackgroundColor(resources.getColor(R.color.black))
        }

        add.setOnClickListener {
            val weight = if (tv_weight.text.toString()
                    .isEmpty() || tv_weight.text.toString() == "Weight"
            ) null else tv_weight.text.toString()
            val distance = if (tv_distance.text.toString()
                    .isEmpty() || tv_distance.text.toString() == "Distance"
            ) null else tv_distance.text.toString()
            Log.d(
                "TAG",
                "goalDialog: ${size} \n ${edt_name.text} \n ${tv_time.text} \n ${tv_reps.text} \n ${tv_pause_time} n ${tv_weight} \n ${tv_distance} \n ${tv_pause_cycle} \n w$weight \n $distance"
            )
            if (id.size == 1) {
                if (id[0].time.equals("Time")) {
                    id.clear()
                    id.add(
                        AddCycle(
                            size,
                            edt_name.text.toString(),
                            tv_time.text.toString(),
                            tv_reps.text.toString(),
                            tv_pause_time.text.toString(),
                            weight,
                            distance,
                            tv_pause_cycle.text.toString()
                        )
                    )
                } else {
                    id.add(
                        AddCycle(
                            size,
                            edt_name.text.toString(),
                            tv_time.text.toString(),
                            tv_reps.text.toString(),
                            tv_pause_time.text.toString(),
                            weight,
                            distance,
                            tv_pause_cycle.text.toString()
                        )
                    )
                }

            } else {
                id.add(
                    AddCycle(
                        size,
                        edt_name.text.toString(),
                        tv_time.text.toString(),
                        tv_reps.text.toString(),
                        tv_pause_time.text.toString(),
                        weight,
                        distance,
                        tv_pause_cycle.text.toString()
                    )
                )
            }
            size += 1
            setDefaultRecycler()
            dialog.dismiss()
            editTimerBinding.main.setBackgroundColor(resources.getColor(R.color.black))
        }

    }

    private fun setShortTimerDialog(s: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_set_short_time_picker)
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = (displayMetrics.widthPixels * 0.9f).toInt()
        val height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.setLayout(width, height)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val mint = dialog.findViewById<NumberPicker>(R.id.mint_num)
        val second = dialog.findViewById<NumberPicker>(R.id.second_num)
        val btnApply = dialog.findViewById<Button>(R.id.btnApply)
        val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)

        var mintNumber = 0
        var secondNumber = 0

        mint.minValue = 0
        mint.maxValue = 60
        mint.wrapSelectorWheel = true

        second.minValue = 0
        second.maxValue = 60
        second.wrapSelectorWheel = true

        mint.setOnValueChangedListener { picker, oldVal, newVal ->
            mintNumber = newVal
        }

        second.setOnValueChangedListener { picker, oldVal, newVal ->
            secondNumber = newVal
        }

        btnCancel.setOnClickListener { v: View? ->
//            rootlayout.setBackgroundResource(R.drawable.bg_black_round_corner_10)
            dialog.dismiss()
        }
        btnApply.setOnClickListener { view: View? ->
//            rootlayout.setBackgroundResource(R.drawable.bg_black_round_corner_10)
            if (s == "pause_time") {
                val formattedTime =
                    String.format("%02d min, %02d sec", mintNumber, secondNumber)
                tv_pause_time.text = formattedTime
            } else if (s == "pause_cycle") {
                val formattedTime =
                    String.format("%02d min, %02d sec", mintNumber, secondNumber)
                tv_pause_cycle.text = formattedTime
            }

            dialog.dismiss()
        }
        dialog.show()
    }

    private fun setTimerDialog(s: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_set_full_time_picker)
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = (displayMetrics.widthPixels * 0.9f).toInt()
        val height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.setLayout(width, height)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        var title = dialog.findViewById<AppCompatTextView>(R.id.tvTitle)
        val hour = dialog.findViewById<NumberPicker>(R.id.hour_num)
        val mint = dialog.findViewById<NumberPicker>(R.id.mint_num)
        val second = dialog.findViewById<NumberPicker>(R.id.second_num)
        val btnApply = dialog.findViewById<Button>(R.id.btnApply)
        val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)

        title.text = "Time Picker"

        var hourNumber = 0
        var mintNumber = 0
        var secondNumber = 0

        hour.minValue = 0
        hour.maxValue = 12
        hour.wrapSelectorWheel = true

        mint.minValue = 0
        mint.maxValue = 60
        mint.wrapSelectorWheel = true

        second.minValue = 0
        second.maxValue = 60
        second.wrapSelectorWheel = true

        hour.setOnValueChangedListener { picker, oldVal, newVal ->
            hourNumber = newVal
        }

        mint.setOnValueChangedListener { picker, oldVal, newVal ->
            mintNumber = newVal
        }

        second.setOnValueChangedListener { picker, oldVal, newVal ->
            secondNumber = newVal
        }

        btnCancel.setOnClickListener { v: View? ->
//            rootlayout.setBackgroundResource(R.drawable.bg_black_round_corner_10)
            dialog.dismiss()
        }
        btnApply.setOnClickListener { view: View? ->
//            rootlayout.setBackgroundResource(R.drawable.bg_black_round_corner_10)
            if (s == "time") {
                val formattedTime =
                    String.format("%02d:%02d:%02d", hourNumber, mintNumber, secondNumber)
                tv_time.text = formattedTime
            }

            dialog.dismiss()
        }
        dialog.show()
    }

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

        var selectedNumber = 1
        numberPicker.minValue = 1
        numberPicker.maxValue = 100
        numberPicker.wrapSelectorWheel = true
        numberPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            selectedNumber = newVal
        }

        btnCancel.setOnClickListener { v: View? ->
//            rootlayout.setBackgroundResource(R.drawable.bg_black_round_corner_10)
            dialog.dismiss()
        }
        btnApply.setOnClickListener { view: View? ->
//            rootlayout.setBackgroundResource(R.drawable.bg_black_round_corner_10)
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

    private fun getSongData() {
        musicData = mutableListOf()
        musicData.clear()
        try {
            Progress.visibility = View.VISIBLE
            apiInterface.GetAudioData().enqueue(object : Callback<Audio> {
                override fun onResponse(call: Call<Audio>, response: Response<Audio>) {
                    val code = response.code()
                    if (code == 200) {
                        if (response.isSuccessful) {
                            Progress.visibility = View.GONE
                            val data = response.body()!!.data!!.toMutableList()
                            if (data != null) {
                                musicData.addAll(data)
                            }
                        }
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@EdiTimerActivity)
                    } else {
                        Toast.makeText(
                            this@EdiTimerActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        ).show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<Audio>, t: Throwable) {
                    Progress.visibility = View.GONE
                    call.cancel()
                    Log.d("TAG Category", t.message.toString() + "")
                }
            })
        } catch (e: Exception) {
            Log.d("Audio :-", e.message.toString())
        }
    }

    private fun getTimerData() {
        Progress.visibility = View.VISIBLE
        apiInterface.GetTimerData().enqueue(object : Callback<Timer> {
            override fun onResponse(call: Call<Timer>, response: Response<Timer>) {
                val code = response.code()
                if (code == 200) {
                    if (response.isSuccessful) {
                        Progress.visibility = View.GONE
                        val data = response.body()!!.data!!.filter {
                            Log.d("88451", "onResponse: $timerId  -- ${it.id}")

                            it.id.toString() == timerId
                        }

                        Log.d("DEUnunfjf", "onResponse: $data")
                        if (data != null) {
                            timeData = data[0]
                            setTimerData(data)
                        }
                    }
                } else if (code == 403) {
                    Utils.setUnAuthDialog(this@EdiTimerActivity)
                } else {
                    Toast.makeText(
                        this@EdiTimerActivity,
                        "" + response.message(),
                        Toast.LENGTH_SHORT
                    ).show()
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<Timer>, t: Throwable) {
                Log.d("Response :-", t.message.toString())
            }
        })
    }

    private fun setTimerData(data: List<Timer.TimerData>) {
        if (data.isEmpty()) {
            Log.e("ViewTimerDetails", "Timer data is empty")
            return
        }

        val timerItem = data[0]

//        audio_id = timerItem.audio?.id ?: run {
//            Log.e("ViewTimerDetails", "Audio is null")
//            return
//        }
//
//        pause_audio_id = timerItem.pause_time_audio?.id ?: run {
//            Log.e("ViewTimerDetails", "Pause time audio is null")
//            return
//        }
//
//        pause_between_id = timerItem.pause_between_time_audio?.id ?: run {
//            Log.e("ViewTimerDetails", "Pause between audio is null")
//            return
//        }

        editTimerBinding.edtTimerName.setText(timerItem.name ?: "")
        editTimerBinding.edtAudio.setText(timerItem.audio?.name ?: "")
        editTimerBinding.edtPauseAudio.setText(timerItem.pause_time_audio?.name ?: "")
        editTimerBinding.edtPauseBetween.setText(timerItem.pause_between_time_audio?.name ?: "")

        setCycleData(timerItem.timer ?: emptyList())
    }

    private fun setCycleData(timer: List<Timer.TimerX>?) {
        if (timer != null) {
            size = timer.size
            for (i in timer) {
                id.add(
                    AddCycle(
                        i.id,
                        i.set,
                        i.time,
                        i.reps,
                        i.pause,
                        i.weight,
                        i.distance,
                        i.pause_timer
                    )
                )
            }
            setDefaultRecycler()
        }
    }

    private fun setDefaultRecycler() {
        editTimerBinding.recyclerView.layoutManager = LinearLayoutManager(this)
        cycleAdapter =
            CycleItemAdapter(id, this, this)
        editTimerBinding.recyclerView.adapter = cycleAdapter
    }

    private fun initViews() {
        apiClient = APIClient(this)
        Progress = editTimerBinding.Progress
        apiInterface = apiClient.client().create(APIInterface::class.java)
        preferenceManager = PreferencesManager(this)
        timerId = intent.getStringExtra("timerId").toString()
        Log.d("Timer Id :-", timerId.toString())
        userId = preferenceManager.getUserId()!!.toInt()
        Log.d("User Id :-", userId.toString())
        id = mutableListOf()

        Log.d("587478418", "initViews: $timerId")
    }

    fun setDefaultData() {
        id.add(
            AddCycle(
                size,
                "Enter Set/Round No.",
                "Time",
                "Reps",
                "Pause Time",
                "Weight",
                "Distance",
                "Pause Between Cycles"
            )
        )
    }

    override fun onItemClicked(view: View, position: Int, type: Long, string: String) {
        when (string) {
            "delete" -> {

                try {
                    Log.d("Delete Data :-", "${id.size} \t ${id[position].id}")

                    if (id.isNotEmpty()) {
                        val delId = id[position].id!!.toInt()
                        Log.d("Delete Id :-", delId.toString())
                        Progress.visibility = View.VISIBLE
                        apiInterface.DeleteTimerCycle(id = delId).enqueue(
                            object : Callback<DeleteBase> {
                                override fun onResponse(
                                    call: Call<DeleteBase>,
                                    response: Response<DeleteBase>
                                ) {
                                    Progress.visibility = View.GONE
                                    val code = response.code()
                                    if (code == 200) {
                                        if (response.isSuccessful) {
                                            Log.d("Delete Success", response.code().toString())
                                            id.removeAt(position)
                                            cycleAdapter.notifyItemRemoved(position)
                                            cycleAdapter.notifyItemRangeChanged(position, id.size)
                                            size -= 1
                                            if (id.isEmpty()) {
                                                setDefaultData()
                                            }
                                        } else {
                                            Log.d("Delete Failed", "Error Code: ${response.code()}")
                                            Toast.makeText(
                                                this@EdiTimerActivity,
                                                "Delete failed. Try again.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } else if (code == 403) {
                                        Utils.setUnAuthDialog(this@EdiTimerActivity)
                                    } else {
                                        Log.d("Delete Failed", "Error Code: ${response.code()}")
                                        Toast.makeText(
                                            this@EdiTimerActivity,
                                            "Delete failed. Try again.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        call.cancel()
                                    }
                                }

                                override fun onFailure(call: Call<DeleteBase>, t: Throwable) {
                                    Progress.visibility = View.GONE
                                    Toast.makeText(
                                        this@EdiTimerActivity,
                                        t.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    Log.d("Delete Failed", "Error: ${t.message}")
                                    call.cancel()
                                }
                            }
                        )
                    }
                } catch (e: Exception) {
                    Log.d("exception :- ", e.message.toString())
                }

            }

            "edit" -> {
                try {
                    val splist = cycleAdapter.getSpList()  // Access the list
                    val selectedCycle = splist?.get(position)  // Get the selected cycle data
                    editTimerBinding.main.setBackgroundColor(resources.getColor(R.color.grey))
                    showEditDialog(selectedCycle, position)
                } catch (e: Exception) {
                    Log.d("exception :- ", e.message.toString())
                }
            }
        }
    }

    private fun showEditDialog(selectedCycle: AddCycle?, position: Int) {
        val dialog = Dialog(this, R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
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

        edt_name.text = selectedCycle?.set ?: "Enter Set/Round No."
        tv_time.text = selectedCycle?.time ?: "Time"
        tv_reps.text = selectedCycle?.reps ?: "Reps"
        tv_pause_time.text = selectedCycle?.pause ?: "Pause Time"
        tv_weight.text = selectedCycle?.weight ?: "Weight"
        tv_distance.text = selectedCycle?.distance ?: "Distance"
        tv_pause_cycle.text = selectedCycle?.pause_timer ?: "Pause Between Cycle"


        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = (displayMetrics.widthPixels * 0.9f).toInt()
        val height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.setLayout(width, height)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        ly_set.setOnClickListener {
//            rootlayout.setBackgroundColor(resources.getColor(R.color.grey))
            SetDialog("set")
        }

        ly_time.setOnClickListener {
//            rootlayout.setBackgroundColor(resources.getColor(R.color.grey))
            setTimerDialog("time")
        }

        ly_reps.setOnClickListener {
//            rootlayout.setBackgroundColor(resources.getColor(R.color.grey))
            SetDialog("reps")
        }

        ly_pause_time.setOnClickListener {
//            rootlayout.setBackgroundColor(resources.getColor(R.color.grey))
            setShortTimerDialog("pause_time")
        }

        ly_weight.setOnClickListener {
//            rootlayout.setBackgroundColor(resources.getColor(R.color.grey))
            SetDialog("weight")
        }

        ly_distance.setOnClickListener {
//            rootlayout.setBackgroundColor(resources.getColor(R.color.grey))
            SetDialog("distance")
        }

        ly_pause_cycle.setOnClickListener {
//            rootlayout.setBackgroundColor(resources.getColor(R.color.grey))
            setShortTimerDialog("pause_cycle")
        }

        cancel.setOnClickListener {
            dialog.dismiss()
            editTimerBinding.main.setBackgroundColor(resources.getColor(R.color.black))
        }

        add.setOnClickListener {

            val weight = if (tv_weight.text.toString()
                    .isEmpty() || tv_weight.text.toString() == "Weight"
            ) null else tv_weight.text.toString()
            val distance = if (tv_distance.text.toString()
                    .isEmpty() || tv_distance.text.toString() == "Distance"
            ) null else tv_distance.text.toString()

            val updatedCycle = AddCycle(
                selectedCycle!!.id,
                edt_name.text.toString(),
                tv_time.text.toString(),
                tv_reps.text.toString(),
                tv_pause_time.text.toString(),
                weight,
                distance,
                tv_pause_cycle.text.toString()
            )

            id[position] = updatedCycle
            setDefaultRecycler()
            editTimerBinding.main.setBackgroundColor(resources.getColor(R.color.black))
            dialog.dismiss()
        }
    }

}