package com.example.trainerapp.competition

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.example.model.SelectedValue
import com.example.model.competition.CompetitionData
import com.example.model.newClass.athlete.AthleteData
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.CategoriesData
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.PreferencesManager
import com.example.trainerapp.R
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.ActivityCompetitionBinding
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.reflect.TypeToken
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CompetitionActivity : AppCompatActivity() {
    lateinit var competitionBinding: ActivityCompetitionBinding

    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    lateinit var preferenceManager: PreferencesManager

    lateinit var athleteData: ArrayList<AthleteData.Athlete>
    lateinit var categoryData: ArrayList<CategoriesData.Categoty>
    lateinit var areaData: ArrayList<CompetitionData.CompetitionAreaData>

    var athleteId = SelectedValue(null)
    var categoryId = SelectedValue(null)
    var areaId = SelectedValue(null)
    var eventId = SelectedValue(null)

    private lateinit var id: ArrayList<Int>
    private lateinit var name: ArrayList<String>

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
                        Utils.setUnAuthDialog(this@CompetitionActivity)
                    } else {
                        Toast.makeText(
                            this@CompetitionActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Toast.makeText(
                        this@CompetitionActivity,
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        competitionBinding = ActivityCompetitionBinding.inflate(layoutInflater)
        setContentView(competitionBinding.root)

        initViews()
        loadData()
        if (preferenceManager.getSelectEvent()) {
            name = getObject(this, "setEventName") as ArrayList<String>
            id = getObjectJson(this, "setEventId") as ArrayList<Int>

            Log.d("Event Id :-", "${id.size} \n ${id[0]}")
        }
        checkButtonClick()
        checkTextWatcher()
    }


    private fun loadData() {
        try {
            reset()
            getAthletes()
            getCategory()
            getCompetitionArea()

        } catch (e: Exception) {
            Log.d("Error :-", "${e.message}")
        }
    }

    private fun getCompetitionArea() {
        areaData.clear()
        competitionBinding.ProgressBar.visibility = View.VISIBLE
        apiInterface.GetCompetitionArea().enqueue(object : Callback<CompetitionData> {
            override fun onResponse(
                call: Call<CompetitionData>,
                response: Response<CompetitionData>
            ) {
                competitionBinding.ProgressBar.visibility = View.GONE
                val code = response.code()
                if (code == 200) {
                    Log.d("TAG", response.code().toString() + "")
                    Log.d("TAG", response.body()!!.message.toString())
                    Log.d("TAG", response.body()!!.status.toString())
                    val Data = response.body()!!.data!!
                    if (Data != null) {
                        for (i in Data) {
                            Log.d("Area $i", "${i.title} \t ${i.id}")
                        }
                        if (response.isSuccessful) {
                            areaData.addAll(Data)
                        }
                    }
                } else if (code == 403) {
                    Utils.setUnAuthDialog(this@CompetitionActivity)
                } else {
                    Toast.makeText(
                        this@CompetitionActivity,
                        "" + response.message(),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<CompetitionData>, t: Throwable) {
                competitionBinding.ProgressBar.visibility = View.GONE
                Toast.makeText(this@CompetitionActivity, "" + t.message, Toast.LENGTH_SHORT)
                    .show()
                call.cancel()
            }

        })
    }

    private fun getCategory() {
        categoryData.clear()
        competitionBinding.ProgressBar.visibility = View.VISIBLE
        apiInterface.GetCategories()?.enqueue(object : Callback<CategoriesData?> {
            override fun onResponse(
                call: Call<CategoriesData?>,
                response: Response<CategoriesData?>
            ) {
                competitionBinding.ProgressBar.visibility = View.GONE
                val code = response.code()
                if (code == 200) {
                    Log.d("TAG", response.code().toString() + "")
                    Log.d("TAG", response.body()!!.message.toString())
                    Log.d("TAG", response.body()!!.status.toString())
                    val Data = response.body()!!.data!!
                    if (Data != null) {
                        for (i in Data) {
                            Log.d("Category $i", "${i.name} \t ${i.id}")
                        }
                        if (response.isSuccessful) {
                            categoryData.addAll(Data)
                        }
                    }
                } else if (code == 403) {
                    Utils.setUnAuthDialog(this@CompetitionActivity)
                } else {
                    Toast.makeText(
                        this@CompetitionActivity,
                        "" + response.message(),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<CategoriesData?>, t: Throwable) {
                competitionBinding.ProgressBar.visibility = View.GONE
                Toast.makeText(this@CompetitionActivity, "" + t.message, Toast.LENGTH_SHORT)
                    .show()
                call.cancel()
            }
        })
    }

    private fun getAthletes() {
        athleteData.clear()
        competitionBinding.ProgressBar.visibility = View.VISIBLE
        apiInterface.GetAthleteList()!!.enqueue(
            object : Callback<AthleteData> {
                override fun onResponse(
                    call: Call<AthleteData>,
                    response: Response<AthleteData>
                ) {
                    competitionBinding.ProgressBar.visibility = View.GONE
                    val code = response.code()
                    if (code == 200) {
                        Log.d("Athlete :- Tag ", response.code().toString() + "")
                        val data = response.body()!!
                        Log.d("Athlete :- Data ", "${data}")
                        if (data.data!! != null) {
                            for (i in data.data) {
                                Log.d("Athlete $i", "${i.name} \t ${i.id} \t ${i.athletes}")
                            }
                            val success: Boolean = data.status!!
                            if (success) {
                                athleteData.addAll(data.data)
                            }
                        }
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@CompetitionActivity)
                    } else {
                        Toast.makeText(
                            this@CompetitionActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        ).show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<AthleteData>, t: Throwable) {
                    competitionBinding.ProgressBar.visibility = View.GONE
                    Toast.makeText(this@CompetitionActivity, "" + t.message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }

            }
        )
    }

    private fun reset() {
        competitionBinding.edtAthletes.text.clear()
        competitionBinding.edtCategory.text.clear()
        competitionBinding.edtCompetition.text.clear()
        competitionBinding.edtDate.text!!.clear()
        competitionBinding.edtArea.text.clear()
        competitionBinding.cardNext.isEnabled = false
//        competitionBinding.cardNext.setBackgroundResource(R.drawable.card_unselect_1)
    }

    private fun checkButtonClick() {
        competitionBinding.back.setOnClickListener {
            finish()
        }

        competitionBinding.edtDate.setOnClickListener {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                Utils.selectDate(this, competitionBinding.edtDate)
//            }

            showDateRangePickerDialog(
                competitionBinding.edtDate.context,
            ) { start ->
                val formattedDate = formatDate2(start)

                competitionBinding.edtDate.setText(formattedDate)
            }
        }

        competitionBinding.imgDate.setOnClickListener {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                Utils.selectDate(this, competitionBinding.edtDate)
//            }

            showDateRangePickerDialog(
                competitionBinding.edtDate.context,
            ) { start ->
                val formattedDate = formatDate2(start)

                competitionBinding.edtDate.setText(formattedDate)
            }
        }

        competitionBinding.edtCategory.setOnClickListener {
            val list = categoryData.map { it.name }
            val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val popupView = inflater.inflate(R.layout.popup_list, null)
            val popupWindow = PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
            )
            popupWindow.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    this@CompetitionActivity,
                    R.drawable.popup_background
                )
            )
            popupWindow.elevation = 10f
            val listView = popupView.findViewById<ListView>(R.id.listView)

            val adapter =
                object : ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list) {
                    override fun getView(
                        position: Int,
                        convertView: View?,
                        parent: ViewGroup
                    ): View {
                        val view = super.getView(position, convertView, parent) as TextView
                        view.setTextColor(Color.WHITE) // Set text color to white
                        return view
                    }
                }
            listView.adapter = adapter
            listView.setOnItemClickListener { _, _, position, _ ->
                val selectedItem = list[position]
                competitionBinding.edtCategory.setText(selectedItem)
                categoryId.id = categoryData.filter { it.name == selectedItem }.first().id!!
                println("Selected item: $selectedItem")
                popupWindow.dismiss()
            }
            popupWindow.showAsDropDown(it)
            popupWindow.setBackgroundDrawable(
                AppCompatResources.getDrawable(
                    this,
                    android.R.color.white
                )
            )
        }

        competitionBinding.edtArea.setOnClickListener {
            val list = areaData.map { it.title }
            val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val popupView = inflater.inflate(R.layout.popup_list, null)
            val popupWindow = PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true // Focusable to allow outside clicks to dismiss
            )
            popupWindow.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    this@CompetitionActivity,
                    R.drawable.popup_background
                )
            )
            popupWindow.elevation = 10f
            val listView = popupView.findViewById<ListView>(R.id.listView)

            val adapter =
                object : ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list) {
                    override fun getView(
                        position: Int,
                        convertView: View?,
                        parent: ViewGroup
                    ): View {
                        val view = super.getView(position, convertView, parent) as TextView
                        view.setTextColor(Color.WHITE) // Set text color to white
                        return view
                    }
                }
            listView.adapter = adapter
            listView.setOnItemClickListener { _, _, position, _ ->
                val selectedItem = list[position]
                competitionBinding.edtArea.setText(selectedItem)
                areaId.id = areaData.filter { it.title == selectedItem }.first().id!!
                println("Selected item: $selectedItem")
                popupWindow.dismiss()
            }
            popupWindow.showAsDropDown(it)
            popupWindow.setBackgroundDrawable(
                AppCompatResources.getDrawable(
                    this,
                    android.R.color.white
                )
            )
        }

        competitionBinding.edtAthletes.setOnClickListener {
            val list = athleteData.map { it.name }
            val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val popupView = inflater.inflate(R.layout.popup_list, null)
            val popupWindow = PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true // Focusable to allow outside clicks to dismiss
            )
            popupWindow.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    this@CompetitionActivity,
                    R.drawable.popup_background
                )
            )
            popupWindow.elevation = 10f
            val listView = popupView.findViewById<ListView>(R.id.listView)

            val adapter =
                object : ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list) {
                    override fun getView(
                        position: Int,
                        convertView: View?,
                        parent: ViewGroup
                    ): View {
                        val view = super.getView(position, convertView, parent) as TextView
                        view.setTextColor(Color.WHITE) // Set text color to white
                        return view
                    }
                }
            listView.adapter = adapter
            listView.setOnItemClickListener { _, _, position, _ ->
                val selectedItem = list[position]
                competitionBinding.edtAthletes.setText(selectedItem)
                athleteId.id = athleteData.filter { it.name == selectedItem }.first().id!!
                println("Selected item: $selectedItem")
                popupWindow.dismiss()
            }
            popupWindow.showAsDropDown(it)
            popupWindow.setBackgroundDrawable(
                AppCompatResources.getDrawable(
                    this,
                    android.R.color.white
                )
            )
        }

        competitionBinding.edtCompetition.setOnClickListener {
            val i = Intent(this, SelectEventActivity::class.java)
            startActivity(i)
        }

        competitionBinding.cardNext.setOnClickListener {
            if (isValidate) {
                Intent(this, ViewCompetitionAnalysisActivity::class.java).apply {
                    putExtra("athleteId", athleteId.id)
                    putExtra("catId", categoryId.id)
                    putExtra("catName", competitionBinding.edtCategory.text.toString())
                    putExtra("areaName", competitionBinding.edtArea.text.toString())
                    putExtra("areaId", areaId.id)
                    putExtra("compName", competitionBinding.edtCompetition.text.toString())
                    putExtra("compId", eventId.id)
                    putExtra("compDate", competitionBinding.edtDate.text.toString())
                    startActivity(this)
                }
                resetData()
            }
        }
    }

    private fun resetData() {
        competitionBinding.edtAthletes.text.clear()
        competitionBinding.edtCategory.text.clear()
        competitionBinding.edtCompetition.text.clear()
        competitionBinding.edtDate.text!!.clear()
        competitionBinding.edtArea.text.clear()
    }

    fun showDateRangePickerDialog(
        context: Context,
        callback: (start: Long) -> Unit
    ) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.date_range_picker_dialog)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#80000000")))
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setGravity(Gravity.CENTER)

        val calendarView = dialog.findViewById<MaterialCalendarView>(R.id.calendarView)
        val textView = dialog.findViewById<TextView>(R.id.textView)
        val confirmButton = dialog.findViewById<Button>(R.id.confirmButton)
        val cancelButton = dialog.findViewById<Button>(R.id.cancelButton)

        calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE)

        cancelButton.setOnClickListener { dialog.dismiss() }

        calendarView.state().edit()
            .setCalendarDisplayMode(CalendarMode.MONTHS)
            .commit()

        calendarView.addDecorator(object : DayViewDecorator {
            val today = CalendarDay.today()
            override fun shouldDecorate(day: CalendarDay?): Boolean {
                return day == today
            }

            override fun decorate(view: DayViewFacade?) {
                view?.addSpan(ForegroundColorSpan(Color.WHITE)) // Text color for today
                ContextCompat.getDrawable(context, R.drawable.todays_date_selecte)?.let {
                    view?.setBackgroundDrawable(it)
                }
            }
        })


        confirmButton.setOnClickListener {
            val selectedDates = calendarView.selectedDates

            if (selectedDates.isNotEmpty()) {
                val selectedDate = selectedDates.first().calendar

                selectedDate.set(Calendar.HOUR_OF_DAY, 0)
                selectedDate.set(Calendar.MINUTE, 0)
                selectedDate.set(Calendar.SECOND, 0)
                selectedDate.set(Calendar.MILLISECOND, 0)

                callback(selectedDate.timeInMillis)

                dialog.dismiss()
            } else {
                textView.text = "Please select a date"
                textView.setTextColor(Color.RED)
            }
        }

        dialog.show()
    }

    private fun formatDate2(dateMillis: Long): String {
        val format = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
        return format.format(Date(dateMillis))
    }


    private fun checkTextWatcher() {
        competitionBinding.edtAthletes.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateUI(competitionBinding.cardNext)
            }

            override fun afterTextChanged(s: Editable?) {
                updateUI(competitionBinding.cardNext)
            }
        })

        competitionBinding.edtCategory.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateUI(competitionBinding.cardNext)
            }

            override fun afterTextChanged(s: Editable?) {
                updateUI(competitionBinding.cardNext)
            }
        })
        competitionBinding.edtCompetition.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateUI(competitionBinding.cardNext)
            }

            override fun afterTextChanged(s: Editable?) {
                updateUI(competitionBinding.cardNext)
            }
        })
        competitionBinding.edtDate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateUI(competitionBinding.cardNext)
            }

            override fun afterTextChanged(s: Editable?) {
                updateUI(competitionBinding.cardNext)
            }
        })
        competitionBinding.edtArea.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateUI(competitionBinding.cardNext)
            }

            override fun afterTextChanged(s: Editable?) {
                updateUI(competitionBinding.cardNext)
            }
        })
    }

    private fun updateUI(cardNext: CardView) {
        cardNext.isEnabled = true
        cardNext.setBackgroundResource(R.drawable.card_select_1)

    }

    private fun initViews() {
        preferenceManager = PreferencesManager(this)
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        id = ArrayList<Int>()
        name = ArrayList<String>()

        athleteData = arrayListOf()
        categoryData = arrayListOf()
        areaData = arrayListOf()
    }

    private val isValidate: Boolean
        get() {
            val athleteName = competitionBinding.edtAthletes.text.toString()
            val categoryName = competitionBinding.edtCategory.text.toString()
            val competitionName = competitionBinding.edtCompetition.text.toString()
            val dateName = competitionBinding.edtDate.text.toString()
            val areaName = competitionBinding.edtArea.text.toString()

            if (athleteName == "") {
                competitionBinding.errorSelectAthlete.visibility = View.VISIBLE
                return false
            } else {
                competitionBinding.errorSelectAthlete.visibility = View.GONE
            }

            if (categoryName == "") {
                competitionBinding.errorSelectCategory.visibility = View.VISIBLE
                return false
            } else {
                competitionBinding.errorSelectCategory.visibility = View.GONE
            }

            if (competitionName == "") {
                competitionBinding.errorSelectCompetition.visibility = View.VISIBLE
                return false
            } else {
                competitionBinding.errorSelectCompetition.visibility = View.GONE
            }

            if (dateName == "") {
                competitionBinding.errorSelectDate.visibility = View.VISIBLE
                return false
            } else {
                competitionBinding.errorSelectDate.visibility = View.GONE
            }

            if (areaName == "") {
                competitionBinding.errorSelectArea.visibility = View.VISIBLE
                return false
            } else {
                competitionBinding.errorSelectArea.visibility = View.GONE
            }
            return true
        }

    fun getObjectJson(c: Context, key: String): List<Int> {
        val appSharedPrefs: SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(c.applicationContext)
        val json = appSharedPrefs.getString(key, "")
        val gson = Gson()
        val type = object : TypeToken<List<Int>>() {}.type
        val arrayList: List<Int> = gson.fromJson<List<Int>>(json, type)
        return arrayList
    }

    fun getObject(c: Context, key: String): List<String> {
        val appSharedPrefs: SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(c.applicationContext)
        val json = appSharedPrefs.getString(key, "")
        val gson = Gson()
        val type = object : TypeToken<List<String>>() {}.type
        val arrayList: List<String> = gson.fromJson<List<String>>(json, type)
        return arrayList
    }

    override fun onResume() {
        super.onResume()
        checkUser()
        if (preferenceManager.getSelectEvent()) {
            name = getObject(this, "setEventName") as ArrayList<String>
            id = getObjectJson(this, "setEventId") as ArrayList<Int>
            if (name.isNotEmpty()) {

                Log.d("Resume Data :-", "${name} \n ${id}")

                val str = arrayOfNulls<String>(name.size)
                val array = JsonArray()

                for (i in 0 until name.size) {
                    str[i] = name[i]
                    array.add(name[i])
                }

                val str1 = convertStringArrayToString(str, ",")
                competitionBinding.edtCompetition.setText(str1)

                if (id.isNotEmpty()) {
                    eventId.id = id[0]
                } else {
                    Log.d("Event Id :-", "Null Event Id")
                }
            } else {

                Log.d("name", "Name list is empty or null")
                competitionBinding.edtCompetition.text.clear()
            }
        }
    }

    private fun convertStringArrayToString(strArr: Array<String?>, deLimiter: String): String? {
        val sb = StringBuilder()
        for (str in strArr) sb.append(str).append(deLimiter)
        return sb.substring(0, sb.length - 1)
    }
}