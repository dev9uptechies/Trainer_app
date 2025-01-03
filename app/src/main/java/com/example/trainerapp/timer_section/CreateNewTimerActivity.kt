package com.example.trainerapp.timer_section

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
import com.example.CycleItemAdapter
import com.example.Library_AudioAdapter
import com.example.OnItemClickListener
import com.example.model.newClass.audio.Audio
import com.example.model.newClass.cycle.AddCycle
import com.example.model.newClass.cycle.AddTimerBody
import com.example.model.newClass.timer.Timer
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.PreferencesManager
import com.example.trainerapp.R
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.ActivityCreateNewTimerBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateNewTimerActivity : AppCompatActivity(), OnItemClickListener.OnItemClickCallback {

    lateinit var createNewTimerBinding: ActivityCreateNewTimerBinding
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    lateinit var Progress: ProgressBar
    lateinit var musicData: MutableList<Audio.AudioData>
    lateinit var adapter: Library_AudioAdapter
    lateinit var cycleAdapter: CycleItemAdapter
    lateinit var tv_reps: TextView
    lateinit var edt_name: TextView
    lateinit var tv_pause_time: TextView
    lateinit var tv_weight: TextView
    lateinit var tv_pause_cycle: TextView
    lateinit var tv_distance: TextView
    lateinit var tv_time: TextView
    lateinit var rootlayout: LinearLayout
    private lateinit var id: MutableList<AddCycle>
    private lateinit var final: ArrayList<String>
    lateinit var list: MutableList<AddCycle>
    lateinit var preferenceManager: PreferencesManager

    //music ids
    var audio_id = 0
    var pause_audio_id = 0
    var pause_between_id = 0
    var size = 0
    var user_id: Int? = null

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
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@CreateNewTimerActivity)
                    } else {
                        Toast.makeText(
                            this@CreateNewTimerActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Toast.makeText(
                        this@CreateNewTimerActivity,
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
        createNewTimerBinding = ActivityCreateNewTimerBinding.inflate(layoutInflater)
        setContentView(createNewTimerBinding.root)
        initViews()
        getSongData()
        checkAudioClick()
        setDefaultRecycler()
        createNewTimerBinding.back.setOnClickListener {
            finish()
        }

        createNewTimerBinding.createCycle.setOnClickListener {
            createNewTimerBinding.main.setBackgroundColor(resources.getColor(R.color.grey))
            goalDialog()
        }

        createNewTimerBinding.addCycle.setOnClickListener {
            createNewTimerBinding.main.setBackgroundColor(resources.getColor(R.color.grey))
            goalDialog()
        }

        createNewTimerBinding.saveCycle.setOnClickListener {
            if (createNewTimerBinding.edtTimerName.text.isNullOrEmpty() || createNewTimerBinding.edtAudio.text.toString()
                    .isNullOrEmpty() || createNewTimerBinding.edtPauseAudio.text.toString()
                    .isNullOrEmpty() || createNewTimerBinding.edtPauseBetween.text.toString().isNullOrEmpty()
            ) {
                Toast.makeText(this, "Please Fill All Data", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            list = mutableListOf()
            list.clear()
            if (id.size == 1 && (createNewTimerBinding.edtTimerName.text.toString()
                    .isNullOrEmpty() ||
                        createNewTimerBinding.edtTimerName.text.toString() == ""
                        || createNewTimerBinding.edtTimerName.text.toString() == "Enter Timer Name"
                        || createNewTimerBinding.edtAudio.text.toString()
                    .isEmpty() || createNewTimerBinding.edtPauseAudio.toString()
                    .isEmpty() || createNewTimerBinding.edtPauseBetween.toString().isEmpty())
            ) {
                Toast.makeText(this, "Please Fill All Data", Toast.LENGTH_SHORT).show()
            } else if (id.size == 1 && (id[0].time == "Time" || id[0].set == "Enter Set/Round No." || id[0].reps == "Reps" || id[0].pause == "Pause Time" || id[0].pause_timer == "Pause Between Cycles")) {
                Toast.makeText(this, "Please Enter Cycle Data", Toast.LENGTH_SHORT).show()
            } else {
                val name = createNewTimerBinding.edtTimerName.text.toString().trim()
                list = id.map {
                    AddCycle(
                        it.id,
                        it.set,
                        it.time,
                        it.reps,
                        it.pause,
                        it.weight ?: "null",
                        it.distance ?: "null",
                        it.pause_timer
                    )
                }.toMutableList()

                for (i in list) {
                    Log.d(
                        "data $i :",
                        "${i.id} \n ${i.set} \n ${i.time} \n ${i.reps}\n ${i.pause} \n ${i.weight}\n ${i.distance} \n${i.pause}"
                    )
                }

                val timerRequest = AddTimerBody(
                    id = user_id.toString(),
                    name = name,
                    audioId = audio_id.toInt(),
                    pauseTimeAudioId = pause_audio_id.toInt(),
                    pauseBetweenTimeAudioId = pause_between_id.toInt(),
                    data = list
                )
                try {
                    createNewTimerBinding.Progress.visibility = View.VISIBLE
                    apiInterface.CreateTimerData(
                        addTimerBody = timerRequest
                    )!!.enqueue(object : Callback<Timer> {
                        override fun onResponse(call: Call<Timer>, response: Response<Timer>) {
                            createNewTimerBinding.Progress.visibility = View.GONE
                            val code = response.code()
                            if (code == 200) {
                                Log.d("TAG", "onResponse: ${response.message()}")
                                Log.d("TAG", "onResponse: ${response.body()!!.message}")
                                Log.d("TAG", "onResponse: ${response.code()}")
                                if (response.isSuccessful) {
                                    val message = response.body()!!.message
                                    Toast.makeText(
                                        this@CreateNewTimerActivity,
                                        message + "",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    finish()
                                }
                            } else if (code == 403) {
                                Utils.setUnAuthDialog(this@CreateNewTimerActivity)
                            } else {
                                Toast.makeText(
                                    this@CreateNewTimerActivity,
                                    "Something Went Wrong",
                                    Toast.LENGTH_SHORT
                                ).show()
                                call.cancel()
                            }
                        }

                        override fun onFailure(call: Call<Timer>, t: Throwable) {
                            createNewTimerBinding.Progress.visibility = View.GONE
                            Log.d("TAG", "onResponse failure: ${t}")
                        }
                    })
                } catch (e: Exception) {
                    Log.d("TAG", "onCreate: $e")
                }
            }
        }
    }

    private fun setDefaultRecycler() {
        createNewTimerBinding.recyclerView.layoutManager = LinearLayoutManager(this)
        cycleAdapter =
            CycleItemAdapter(id, this, this)
        createNewTimerBinding.recyclerView.adapter = cycleAdapter
    }

    private fun checkAudioClick() {
        createNewTimerBinding.edtAudio.setOnClickListener {
            openAudioDialog(createNewTimerBinding.edtAudio, "audio_id")
        }
        createNewTimerBinding.edtPauseAudio.setOnClickListener {
            openAudioDialog(createNewTimerBinding.edtPauseAudio, "pause_audio_id")
        }
        createNewTimerBinding.edtPauseBetween.setOnClickListener {
            openAudioDialog(createNewTimerBinding.edtPauseBetween, "pause_between_id")
        }
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
//            rootlayout.setBackgroundColor(resources.getColor(R.color.grey))
            SetDialog("set")
            updateAddButtonState(add)
        }

        ly_time.setOnClickListener {
//            rootlayout.setBackgroundColor(resources.getColor(R.color.grey))
            setTimerDialog("time")
            updateAddButtonState(add)
        }

        ly_reps.setOnClickListener {
//            rootlayout.setBackgroundColor(resources.getColor(R.color.grey))
            SetDialog("reps")
            updateAddButtonState(add)
        }

        ly_pause_time.setOnClickListener {
//            rootlayout.setBackgroundColor(resources.getColor(R.color.grey))
            setShortTimerDialog("pause_time")
            updateAddButtonState(add)
        }

        ly_weight.setOnClickListener {
//            rootlayout.setBackgroundColor(resources.getColor(R.color.grey))
            SetDialog("weight")
            updateAddButtonState(add)
        }

        ly_distance.setOnClickListener {
//            rootlayout.setBackgroundColor(resources.getColor(R.color.grey))
            SetDialog("distance")
            updateAddButtonState(add)
        }

        ly_pause_cycle.setOnClickListener {
//            rootlayout.setBackgroundColor(resources.getColor(R.color.grey))
            setShortTimerDialog("pause_cycle")
            updateAddButtonState(add)
        }

        cancel.setOnClickListener {
            dialog.dismiss()
            createNewTimerBinding.main.setBackgroundColor(resources.getColor(R.color.black))
        }

        add.setOnClickListener {
            if (areAllFieldsFilled()) {
                createNewTimerBinding.main.setBackgroundColor(resources.getColor(R.color.black))
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
            }
        }

    }

    private fun areAllFieldsFilled(): Boolean {
        Log.d("Validation", "Name: ${edt_name.text.isNullOrEmpty()}")
        Log.d("Validation", "Time: ${tv_time.text.toString().isEmpty()}")
        Log.d("Validation", "Reps: ${tv_reps.text.toString().isEmpty()}")
        Log.d("Validation", "Pause Time: ${tv_pause_time.text.toString().isEmpty()}")

        return !(  tv_pause_cycle.text.toString() == "Pause Cycle" ||
                edt_name.text.isNullOrEmpty() ||
                tv_time.text.toString() == "Time" ||
                tv_reps.text.toString() == "Reps" ||
                tv_pause_time.text.toString() == "Pause Time")
    }

    private fun updateAddButtonState(addButton: CardView) {
        if (areAllFieldsFilled()) {
            addButton.isEnabled = true
            addButton.setCardBackgroundColor(resources.getColor(R.color.splash_text_color))
        } else {
            addButton.isEnabled = false
            addButton.setCardBackgroundColor(resources.getColor(R.color.grey))
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
                            if (data.isNotEmpty()) {
                                musicData.addAll(data)
                            }
                        }
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@CreateNewTimerActivity)
                    } else {
                        Toast.makeText(
                            this@CreateNewTimerActivity,
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

    private fun initViews() {
        apiClient = APIClient(this)
        Progress = createNewTimerBinding.Progress
        apiInterface = apiClient.client().create(APIInterface::class.java)
        preferenceManager = PreferencesManager(this)
        id = mutableListOf()
        final = ArrayList()
        size = 1
        user_id = preferenceManager.getUserId()!!.toInt()
        Log.d("User Id :-", user_id.toString())
        setDefaultData()
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
                    id.removeAt(position)
                    cycleAdapter.notifyItemRemoved(position)
                    cycleAdapter.notifyItemRangeChanged(position, id.size)
                    size = size - 1
                    if (id.size == 0) {
                        setDefaultData()
                    }
                } catch (e: Exception) {
                    Log.d("exception :- ", e.message.toString())
                }
            }

            "edit" -> {
                try {
                    val splist = cycleAdapter.getSpList()  // Access the list
                    val selectedCycle = splist?.get(position)  // Get the selected cycle data
                    createNewTimerBinding.main.setBackgroundColor(resources.getColor(R.color.grey))
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

        edt_name = dialog.findViewById<TextView>(R.id.edt_name)
        tv_reps = dialog.findViewById<TextView>(R.id.tv_reps)
        tv_pause_time = dialog.findViewById<TextView>(R.id.tv_pause_time)
        tv_weight = dialog.findViewById<TextView>(R.id.tv_weight)
        tv_pause_cycle = dialog.findViewById<TextView>(R.id.tv_pause_cycle)
        tv_distance = dialog.findViewById<TextView>(R.id.tv_distance)
        tv_time = dialog.findViewById<TextView>(R.id.tv_time)
        rootlayout = dialog.findViewById<LinearLayout>(R.id.rootlayout)

        val cancel = dialog.findViewById<CardView>(R.id.crad_cancel)
        val ly_set = dialog.findViewById<CardView>(R.id.ly_name)
        val ly_time = dialog.findViewById<CardView>(R.id.ly_time)
        val ly_reps = dialog.findViewById<CardView>(R.id.ly_reps)
        val ly_pause_time = dialog.findViewById<CardView>(R.id.ly_pause_time)
        val ly_weight = dialog.findViewById<CardView>(R.id.ly_weight)
        val ly_distance = dialog.findViewById<CardView>(R.id.ly_distance)
        val ly_pause_cycle = dialog.findViewById<CardView>(R.id.ly_pause_cycle)
        val add = dialog.findViewById<CardView>(R.id.card_add)


        edt_name.text = selectedCycle?.set ?: "Enter Set/Round No."
        tv_time.text = selectedCycle?.time ?: "Time"
        tv_reps.text = selectedCycle?.reps ?: "Reps"
        tv_pause_time.text = selectedCycle?.pause ?: "Pause Time"
        tv_weight.text = selectedCycle?.weight ?: "Weight"
        tv_distance.text = selectedCycle?.distance ?: "Distance"
        tv_pause_cycle.text = selectedCycle?.pause_timer ?: "Pause Between Cycle"


        updateAddButtonState(add)

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = (displayMetrics.widthPixels * 0.9f).toInt()
        val height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.setLayout(width, height)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        ly_set.setOnClickListener {
//            rootlayout.setBackgroundColor(resources.getColor(R.color.grey))
            SetDialog("set")
            updateAddButtonState(add)
        }

        ly_time.setOnClickListener {
//            rootlayout.setBackgroundColor(resources.getColor(R.color.grey))
            setTimerDialog("time")
            updateAddButtonState(add)
        }

        ly_reps.setOnClickListener {
//            rootlayout.setBackgroundColor(resources.getColor(R.color.grey))
            SetDialog("reps")
            updateAddButtonState(add)
        }

        ly_pause_time.setOnClickListener {
//            rootlayout.setBackgroundColor(resources.getColor(R.color.grey))
            setShortTimerDialog("pause_time")
            updateAddButtonState(add)
        }

        ly_weight.setOnClickListener {
//            rootlayout.setBackgroundColor(resources.getColor(R.color.grey))
            SetDialog("weight")
            updateAddButtonState(add)
        }

        ly_distance.setOnClickListener {
//            rootlayout.setBackgroundColor(resources.getColor(R.color.grey))
            SetDialog("distance")
            updateAddButtonState(add)
        }

        ly_pause_cycle.setOnClickListener {
//            rootlayout.setBackgroundColor(resources.getColor(R.color.grey))
            setShortTimerDialog("pause_cycle")
            updateAddButtonState(add)
        }

        cancel.setOnClickListener {
            dialog.dismiss()
            createNewTimerBinding.main.setBackgroundColor(resources.getColor(R.color.black))
        }

        add.setOnClickListener {
            if (areAllFieldsFilled()) {
                createNewTimerBinding.main.setBackgroundColor(resources.getColor(R.color.black))
                dialog.dismiss()

                val weight = if (tv_weight.text.toString()
                        .isEmpty() || tv_weight.text.toString() == "Weight"
                ) null else tv_weight.text.toString()
                val distance = if (tv_distance.text.toString()
                        .isEmpty() || tv_distance.text.toString() == "Distance"
                ) null else tv_distance.text.toString()

                val updatedCycle = AddCycle(
                    size,
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
            }
        }
    }

}