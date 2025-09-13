package com

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
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.Adapter.selected_day.LessonAdapter
import com.example.Adapter.selected_day.eventAdapter
import com.example.Adapter.selected_day.testAdapter
import com.example.AddSelectedActivity
import com.example.OnItemClickListener
import com.example.model.SelectedDaysModel
import com.example.model.personal_diary.GetDiaryDataForEdit
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.PreferencesManager
import com.example.trainerapp.R
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.ActivitySelectDayBinding
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

class SelectDayActivity : AppCompatActivity(), OnItemClickListener.OnItemClickCallback {

    lateinit var binding: ActivitySelectDayBinding
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient

    lateinit var lessonadapter: LessonAdapter
    lateinit var eventadapter: eventAdapter
    lateinit var testadapter: testAdapter

    var groupId: Int = 0
    var date: String? = null
    lateinit var preferenceManager: PreferencesManager

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
//                        fetchDayData(date.toString())
                        loadData()
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@SelectDayActivity)
                    } else {
                        Toast.makeText(
                            this@SelectDayActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Toast.makeText(
                        this@SelectDayActivity,
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

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private var currentDate: LocalDate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectDayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        preferenceManager = PreferencesManager(this)

        groupId = intent.getIntExtra("id", 0)
        date = intent.getStringExtra("date")
        Log.d("idok", "IDDD:-    $groupId")
        Log.d("idok", "DATE:-    $date")

        binding.tvCreditit.text = date

        ButtonCLick()
        loadData()

        date?.let {
            currentDate = LocalDate.parse(it, dateFormatter)
            binding.tvCreditit.text = date
        }

        binding.previousDate.setOnClickListener {
            currentDate = currentDate?.minusDays(1)   // ⬅️ Go back one day
            updateDate()
        }

        binding.nextDate.setOnClickListener {
            currentDate = currentDate?.plusDays(1)   // ➡️ Go forward one day
            updateDate()
        }
        binding.tvCreditit.setOnClickListener {
            showDatePickerDialog { year, month, dayOfMonth ->
                val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                currentDate = selectedDate
                binding.tvCreditit.text = selectedDate.format(dateFormatter)
                updateDate()
            }
        }

        getPersonalDiaryData(date.toString())

    }

    private fun loadData() {
        val userType = preferenceManager.GetFlage()
        Log.d("DJUEHFN", "updateDate: $userType")
        if (userType == "Athlete"){
            fetchDayDataAthlete(date.toString())
        }else{
            fetchDayData(date.toString())
        }
    }

    private fun updateDate() {
        currentDate?.let {
            val formatted = it.format(dateFormatter)
            binding.tvCreditit.text = formatted

            val userType = preferenceManager.GetFlage()

            Log.d("TAG", "updateDate: $userType")
            if (userType == "Athlete"){
                fetchDayDataAthlete(formatted)

            }else{
                fetchDayData(formatted)

            }
            getPersonalDiaryData(formatted)
        }
    }

    private fun ButtonCLick() {

        binding.back.setOnClickListener { finish() }

        binding.add.setOnClickListener {
            val intent = Intent(this, AddSelectedActivity::class.java)
            intent.putExtra("Add", "lesson")
            intent.putExtra("Date", date)
            intent.putExtra("GroupId", groupId)
            startActivity(intent)
        }

        binding.eventAdd.setOnClickListener {
            val intent = Intent(this, AddSelectedActivity::class.java)
            intent.putExtra("Add", "event")
            intent.putExtra("Date", date)
            intent.putExtra("GroupId", groupId)
            startActivity(intent)
        }

        binding.addTest.setOnClickListener {
            val intent = Intent(this, AddSelectedActivity::class.java)
            intent.putExtra("Add", "test")
            intent.putExtra("Date", date)
            intent.putExtra("GroupId", groupId)
            startActivity(intent)
        }
    }

    private fun fetchDayData(date:String) {
        try {
            Log.d("DJDHE", "fetchDayData: $date")
            binding.progresBar.visibility = View.VISIBLE
            apiInterface.GetSelectedDays(date, groupId.toString())!!
                .enqueue(object : Callback<SelectedDaysModel> {
                    override fun onResponse(
                        call: Call<SelectedDaysModel>,
                        response: Response<SelectedDaysModel>
                    ) {
                        if (response.isSuccessful && response.body() != null) {

                            binding.progresBar.visibility = View.GONE

                            val selectedDaysModel = response.body()

                            Log.d("API Response", "Response: $selectedDaysModel")

                            val data = selectedDaysModel?.data
                            if (data != null) {
                                if (data.tests.isNotEmpty()) {
                                    Log.d(
                                        "First Test",
                                        "Test ID: ${data.tests[0].id}, Title: ${data.tests[0].title}"
                                    )
                                } else {
                                    Log.d("First Test", "No tests available.")
                                }

                                initTestRecyclerView(data.tests)
                                initLessonRecyclerView(data.lessons)
                                initEventRecyclerView(data.events)

                                Log.d("Event Data", "Events: ${data.events}")

                            } else {
                                Log.e("API Response", "Data is null.")
                                Toast.makeText(
                                    this@SelectDayActivity,
                                    "Data is null",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Log.e("API Response", "Failed to fetch data: ${response.message()}")
                            Toast.makeText(
                                this@SelectDayActivity,
                                "Failed to fetch data",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<SelectedDaysModel>, t: Throwable) {
                        binding.progresBar.visibility = View.GONE
                        Log.e("API Response", "Error: ${t.message}")
                        Toast.makeText(
                            this@SelectDayActivity,
                            "Error: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        } catch (e: Exception) {
            binding.progresBar.visibility = View.GONE
            Log.e("Catch", "CatchError :- ${e.message}")
        }
    }

    private fun fetchDayDataAthlete(date:String) {
        try {
            Log.d("DJDHEsss", "fetchDayData: $date - $groupId")
            binding.progresBar.visibility = View.VISIBLE
            apiInterface.GetSelectedDaysAthlete(date, groupId.toString())!!
                .enqueue(object : Callback<SelectedDaysModel> {
                    override fun onResponse(
                        call: Call<SelectedDaysModel>,
                        response: Response<SelectedDaysModel>
                    ) {
                        if (response.isSuccessful && response.body() != null) {

                            binding.progresBar.visibility = View.GONE

                            val selectedDaysModel = response.body()

                            Log.d("API Response", "Response: $selectedDaysModel")

                            val data = selectedDaysModel?.data
                            if (data != null) {
                                if (data.tests.isNotEmpty()) {
                                    Log.d(
                                        "First Test",
                                        "Test ID: ${data.tests[0].id}, Title: ${data.tests[0].title}"
                                    )
                                } else {
                                    Log.d("First Test", "No tests available.")
                                }

                                initTestRecyclerView(data.tests)
                                initLessonRecyclerView(data.lessons)
                                initEventRecyclerView(data.events)

                                Log.d("Event Data", "Events: ${data.events}")

                            } else {
                                Log.e("API Response", "Data is null.")
                                Toast.makeText(
                                    this@SelectDayActivity,
                                    "Data is null",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            binding.progresBar.visibility = View.GONE

                            Log.e("Pldnc", "Failed to fetch data: ${response.message()}")
                            Toast.makeText(
                                this@SelectDayActivity,
                                "Failed to fetch data",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<SelectedDaysModel>, t: Throwable) {
                        binding.progresBar.visibility = View.GONE
                        Log.e("API Response", "Error: ${t.message}")
                        Toast.makeText(
                            this@SelectDayActivity,
                            "Error: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        } catch (e: Exception) {
            binding.progresBar.visibility = View.GONE
            Log.e("Catch", "CatchError :- ${e.message}")
        }
    }

    private fun getPersonalDiaryData(date: String) {
        try {
            apiInterface.GetPersonalDiaryData(date)?.enqueue(object : Callback<GetDiaryDataForEdit> {
                override fun onResponse(
                    call: Call<GetDiaryDataForEdit>,
                    response: Response<GetDiaryDataForEdit>
                ) {
                    Log.d("TAG", "Response code: ${response.code()}")

                    when (response.code()) {
                        200 -> {

                            response.body()?.let { responseBody ->
                                val diaryData = responseBody.data


                                if (diaryData != null) {
                                    Log.d("DATA", "Date: ${diaryData.date}")

                                    // Access the personal_dairie_detaile list
                                    val personalDiaryDetails = diaryData.personalDiaryDetails
                                    personalDiaryDetails?.forEach { detail ->
                                        Log.d(
                                            "DETAIL",
                                            "Assess Level: ${detail.assessYourLevelOf}, " +
                                                    "Before: ${detail.beforeTraining}, " +
                                                    "During: ${detail.duringTraining}, " +
                                                    "After: ${detail.afterTraining}"
                                        )
                                    }

                                    // Pass the data to a method to update the UI
                                    SetData(diaryData)
                                } else {
                                    Log.e("ERROR", "Data is null")

                                }
                            } ?: run {
                                Log.e("ERROR", "Response body is null")
                                Toast.makeText(
                                    this@SelectDayActivity,
                                    "Failed to retrieve data",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        403 -> {
                            Utils.setUnAuthDialog(this@SelectDayActivity)
                        }
                        else -> {
                            Log.e("ERROR", "Error: ${response.message()}")
                            Toast.makeText(
                                this@SelectDayActivity,
                                "Error: ${response.message()}",
                                Toast.LENGTH_SHORT
                            ).show()
                            call.cancel()
                        }
                    }
                }

                override fun onFailure(call: Call<GetDiaryDataForEdit>, t: Throwable) {
                    Log.e("Error", "Network Error: ${t.message}")
                    Toast.makeText(
                        this@SelectDayActivity,
                        "Network error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } catch (e: Exception) {
            Log.e("error", "Exception: ${e.message}")
            Toast.makeText(
                this@SelectDayActivity,
                "Unexpected error: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun SetData(data: GetDiaryDataForEdit.Data) {
        // Set basic details
//        binding.dateTextView.text = data.date ?: ""
//        binding.sleepHoursTextView.text = data.sleepHours ?: ""
        binding.NutritionAndHydration.setText(data.nutritionAndHydration ?: "")
        binding.Notes.setText(data.notes ?: "")

        // Check if personalDiaryDetails is empty or null
        if (data.personalDiaryDetails.isNullOrEmpty() || data.personalDiaryDetails.equals("0")) {
            // Set all values to empty string if no details available
            binding.EnergyBT.setText("")
            binding.EnergyDT.setText("")
            binding.EnergyAT.setText("")

            binding.SatisfationBT.setText("")
            binding.SatisfationDT.setText("")
            binding.SatisfationAT.setText("")

            binding.HapinessBT.setText("")
            binding.HapinessDT.setText("")
            binding.HapinessAT.setText("")

            binding.IrritabilityBT.setText("")
            binding.IrritabilityDT.setText("")
            binding.IrritabilityAT.setText("")

            binding.DeterminationBT.setText("")
            binding.DeterminationDT.setText("")
            binding.DeterminationAT.setText("")

            binding.AnxietyBT.setText("")
            binding.AnxietyDT.setText("")
            binding.AnxietyAT.setText("")

            binding.TirednessBT.setText("")
            binding.TirednessDT.setText("")
            binding.TirednessAT.setText("")
        } else {
            // Iterate through personal diary details list and set each value as needed
            data.personalDiaryDetails.forEach { detail ->
                when (detail.assessYourLevelOf) {
                    "Energy" -> {
                        binding.EnergyBT.setText(detail.beforeTraining ?: "") // Use setText for EditText
                        binding.EnergyDT.setText(detail.duringTraining ?: "") // Use setText for EditText
                        binding.EnergyAT.setText(detail.afterTraining ?: "") // Use setText for EditText
                    }
                    "Satisfaction" -> {
                        binding.SatisfationBT.setText(detail.beforeTraining ?: "") // Use setText for EditText
                        binding.SatisfationDT.setText(detail.duringTraining ?: "") // Use setText for EditText
                        binding.SatisfationAT.setText(detail.afterTraining ?: "") // Use setText for EditText
                    }
                    "Happiness" -> {
                        binding.HapinessBT.setText(detail.beforeTraining ?: "") // Use setText for EditText
                        binding.HapinessDT.setText(detail.duringTraining ?: "") // Use setText for EditText
                        binding.HapinessAT.setText(detail.afterTraining ?: "") // Use setText for EditText
                    }
                    "Irritability" -> {
                        binding.IrritabilityBT.setText(detail.beforeTraining ?: "") // Use setText for EditText
                        binding.IrritabilityDT.setText(detail.duringTraining ?: "") // Use setText for EditText
                        binding.IrritabilityAT.setText(detail.afterTraining ?: "") // Use setText for EditText
                    }
                    "Determination" -> {
                        binding.DeterminationBT.setText(detail.beforeTraining ?: "") // Use setText for EditText
                        binding.DeterminationDT.setText(detail.duringTraining ?: "") // Use setText for EditText
                        binding.DeterminationAT.setText(detail.afterTraining ?: "") // Use setText for EditText
                    }
                    "Anxiety" -> {
                        binding.AnxietyBT.setText(detail.beforeTraining ?: "") // Use setText for EditText
                        binding.AnxietyDT.setText(detail.duringTraining ?: "") // Use setText for EditText
                        binding.AnxietyAT.setText(detail.afterTraining ?: "") // Use setText for EditText
                    }
                    "Tiredness" -> {
                        binding.TirednessBT.setText(detail.beforeTraining ?: "") // Use setText for EditText
                        binding.TirednessDT.setText(detail.duringTraining ?: "") // Use setText for EditText
                        binding.TirednessAT.setText(detail.afterTraining ?: "") // Use setText for EditText
                    }
                }
            }
        }
    }


    private fun initLessonRecyclerView(programs: List<SelectedDaysModel.Lesson>) {
        binding.favLessonRly.layoutManager = LinearLayoutManager(this)
        lessonadapter = LessonAdapter(programs, this, this)
        binding.favLessonRly.adapter = lessonadapter
    }

    private fun initTestRecyclerView(tests: List<SelectedDaysModel.Test>) {
        binding.favTestRly.layoutManager = LinearLayoutManager(this)
        testadapter = testAdapter(tests, this, this)
        binding.favTestRly.adapter = testadapter
    }

    private fun initEventRecyclerView(events: List<SelectedDaysModel.Event>) {
        if (events.isNotEmpty()) {
            binding.favEventRly.layoutManager = LinearLayoutManager(this)
            eventadapter = eventAdapter(events, this, this)
            binding.favEventRly.adapter = eventadapter
        } else {
            // Clear old data
            binding.favEventRly.adapter = null
            Log.d("Event RecyclerView", "No events available.")
        }
    }


    private fun showDatePickerDialog(onDateSelected: (year: Int, month: Int, dayOfMonth: Int) -> Unit) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_date_picker)

        // Set dialog width to 90% of screen width
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = (displayMetrics.widthPixels * 0.9f).toInt()
        dialog.window?.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val datePicker = dialog.findViewById<DatePicker>(R.id.datePicker)
        val cancelButton = dialog.findViewById<Button>(R.id.btnCancel)
        val applyButton = dialog.findViewById<Button>(R.id.btnApply)


        // Default date (today)
        val calendar = Calendar.getInstance()
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(
            Calendar.DAY_OF_MONTH), null)

        cancelButton.setOnClickListener { dialog.dismiss() }

        applyButton.setOnClickListener {
            val selectedYear = datePicker.year
            val selectedMonth = datePicker.month
            val selectedDay = datePicker.dayOfMonth
            onDateSelected(selectedYear, selectedMonth, selectedDay)
            dialog.dismiss()
        }

        dialog.show()
    }



    override fun onItemClicked(view: View, position: Int, type: Long, string: String) {
        if (string == "fav") {
            binding.progresBar.visibility = View.VISIBLE
            val id: MultipartBody.Part =
                MultipartBody.Part.createFormData("id", type.toInt().toString())
            apiInterface.Favourite_lession(id)?.enqueue(object : Callback<RegisterData?> {
                override fun onResponse(
                    call: Call<RegisterData?>,
                    response: Response<RegisterData?>
                ) {
                    binding.progresBar.visibility = View.GONE
                    Log.d("TAG", response.code().toString() + "")
                    val code = response.code()
                    if (code == 200) {
                        val resource: RegisterData? = response.body()
                        val Success: Boolean = resource?.status!!
                        val Message: String = resource.message!!
                        if (Success) {
                            binding.progresBar.visibility = View.GONE
                            loadData()
                        } else {
                            binding.progresBar.visibility = View.GONE
                            Toast.makeText(
                                this@SelectDayActivity,
                                "" + Message,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@SelectDayActivity)
                    } else {
                        binding.progresBar.visibility = View.GONE
                        Toast.makeText(
                            this@SelectDayActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    binding.progresBar.visibility = View.GONE
                    Toast.makeText(this@SelectDayActivity, "" + t.message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }
            })
        } else if (string == "unfav") {
            binding.progresBar.visibility = View.VISIBLE
            val id: MultipartBody.Part =
                MultipartBody.Part.createFormData("id", type.toInt().toString())
            apiInterface.DeleteFavourite_lession(type.toInt())
                ?.enqueue(object : Callback<RegisterData?> {
                    override fun onResponse(
                        call: Call<RegisterData?>,
                        response: Response<RegisterData?>
                    ) {
                        Log.d("TAG", response.code().toString() + "")
                        binding.progresBar.visibility = View.GONE
                        val code = response.code()
                        if (code == 200) {
                            val resource: RegisterData? = response.body()
                            val Success: Boolean = resource?.status!!
                            val Message: String = resource.message!!
                            if (Success) {
                                binding.progresBar.visibility = View.GONE
                                loadData()
                            } else {
                                binding.progresBar.visibility = View.GONE
                                Toast.makeText(
                                    this@SelectDayActivity,
                                    "" + Message,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        } else if (code == 403) {
                            Utils.setUnAuthDialog(this@SelectDayActivity)
                        } else {
                            Toast.makeText(
                                this@SelectDayActivity,
                                "" + response.message(),
                                Toast.LENGTH_SHORT
                            ).show()
                            call.cancel()
                        }
                    }

                    override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                        binding.progresBar.visibility = View.GONE
                        Toast.makeText(
                            this@SelectDayActivity,
                            "" + t.message,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                })
        } else if (string == "favtest"){
            binding.progresBar.visibility = View.VISIBLE
            val id: MultipartBody.Part =
                MultipartBody.Part.createFormData("id", type.toInt().toString())
            apiInterface.Favourite_Test(id)?.enqueue(object : Callback<RegisterData?> {
                override fun onResponse(
                    call: Call<RegisterData?>,
                    response: Response<RegisterData?>
                ) {
                    binding.progresBar.visibility = View.GONE
                    Log.d("TAG", response.code().toString() + "")
                    val code = response.code()
                    if (code == 200) {
                        val resource: RegisterData? = response.body()
                        val Success: Boolean = resource?.status!!
                        val Message: String = resource.message!!
                        if (Success) {
                            binding.progresBar.visibility = View.GONE
                            loadData()
                        } else {
                            binding.progresBar.visibility = View.GONE
                            Toast.makeText(
                                this@SelectDayActivity,
                                "" + Message,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@SelectDayActivity)
                    } else {
                        binding.progresBar.visibility = View.GONE
                        Toast.makeText(
                            this@SelectDayActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    binding.progresBar.visibility = View.GONE
                    Toast.makeText(this@SelectDayActivity, "" + t.message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }
            })
        } else if (string == "unfavtest") {
            binding.progresBar.visibility = View.VISIBLE
            var id: MultipartBody.Part =
                MultipartBody.Part.createFormData("id", type.toInt().toString())
            apiInterface.DeleteFavourite_Test(type.toInt())
                ?.enqueue(object : Callback<RegisterData?> {
                    override fun onResponse(
                        call: Call<RegisterData?>,
                        response: Response<RegisterData?>
                    ) {
                        Log.d("TAG", response.code().toString() + "")
                        binding.progresBar.visibility = View.GONE
                        val code = response.code()
                        if (code == 200) {
                            val resource: RegisterData? = response.body()
                            val Success: Boolean = resource?.status!!
                            val Message: String = resource.message!!
                            if (Success) {
                                binding.progresBar.visibility = View.GONE
                                loadData()
                            } else {
                                binding.progresBar.visibility = View.GONE
                                Toast.makeText(
                                    this@SelectDayActivity,
                                    "" + Message,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        } else if (code == 403) {
                            Utils.setUnAuthDialog(this@SelectDayActivity)
                        } else {
                            Toast.makeText(
                                this@SelectDayActivity,
                                "" + response.message(),
                                Toast.LENGTH_SHORT
                            ).show()
                            call.cancel()
                        }
                    }

                    override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                        binding.progresBar.visibility = View.GONE
                        Toast.makeText(
                            this@SelectDayActivity,
                            "" + t.message,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                })
        } else if (string == "favevent"){
            binding.progresBar.visibility = View.VISIBLE
            val id: MultipartBody.Part =
                MultipartBody.Part.createFormData("id", type.toInt().toString())
            apiInterface.Favourite_Event(id)?.enqueue(object : Callback<RegisterData?> {
                override fun onResponse(
                    call: Call<RegisterData?>,
                    response: Response<RegisterData?>
                ) {
                    binding.progresBar.visibility = View.GONE
                    Log.d("TAG", response.code().toString() + "")
                    val code = response.code()
                    if (code == 200) {
                        val resource: RegisterData? = response.body()
                        val Success: Boolean = resource?.status!!
                        val Message: String = resource.message!!
                        if (Success) {
                            binding.progresBar.visibility = View.GONE
                            loadData()
                        } else {
                            binding.progresBar.visibility = View.GONE
                            Toast.makeText(
                                this@SelectDayActivity,
                                "" + Message,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@SelectDayActivity)
                    } else {
                        binding.progresBar.visibility = View.GONE
                        Toast.makeText(
                            this@SelectDayActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    binding.progresBar.visibility = View.GONE
                    Toast.makeText(this@SelectDayActivity, "" + t.message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }
            })
        } else if (string == "unfavevent") {
            binding.progresBar.visibility = View.VISIBLE
            val id: MultipartBody.Part =
                MultipartBody.Part.createFormData("id", type.toInt().toString())
            apiInterface.DeleteFavourite_Event(type.toInt())
                ?.enqueue(object : Callback<RegisterData?> {
                    override fun onResponse(
                        call: Call<RegisterData?>,
                        response: Response<RegisterData?>
                    ) {
                        Log.d("TAG", response.code().toString() + "")
                        binding.progresBar.visibility = View.GONE
                        val code = response.code()
                        if (code == 200) {
                            val resource: RegisterData? = response.body()
                            val Success: Boolean = resource?.status!!
                            val Message: String = resource.message!!
                            if (Success) {
                                binding.progresBar.visibility = View.GONE
                                loadData()
                            } else {
                                binding.progresBar.visibility = View.GONE
                                Toast.makeText(
                                    this@SelectDayActivity,
                                    "" + Message,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        } else if (code == 403) {
                            Utils.setUnAuthDialog(this@SelectDayActivity)
                        } else {
                            Toast.makeText(
                                this@SelectDayActivity,
                                "" + response.message(),
                                Toast.LENGTH_SHORT
                            ).show()
                            call.cancel()
                        }
                    }

                    override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                        binding.progresBar.visibility = View.GONE
                        Toast.makeText(
                            this@SelectDayActivity,
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