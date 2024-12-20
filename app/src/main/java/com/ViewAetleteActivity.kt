package com

import android.app.DatePickerDialog
import com.example.model.AthleteDataPackage.AthleteDatas
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.EventAdapterAthlete
import com.example.OnItemClickListener
import com.example.TestAdapterAthlete
import com.example.model.AthleteDataPackage.AthleteData
import com.example.model.AthleteDataPackage.AthleteDatass
import com.example.model.AthleteDataPackage.AthleteDetails
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.PerformanceProfileActivity
import com.example.trainerapp.PreferencesManager
import com.example.trainerapp.R
import com.example.trainerapp.TestListData
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.ActivityViewAetleteBinding
import com.example.trainerapp.performance_profile.ViewTemplateActivity
import com.example.trainerapp.performance_profile.view_all_graph.ViewAllPerformanceProfileActivity
import com.example.trainerapp.personal_diary.ViewPersonalDiaryActivity
import com.example.trainerapp.view_analysis.ViewAnalysisActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ViewAetleteActivity : AppCompatActivity(),  OnItemClickListener.OnItemClickCallback {
    private lateinit var viewAetleteBinding: ActivityViewAetleteBinding

    lateinit var TestList: ArrayList<TestListData.testData>
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    lateinit var preferenceManager: PreferencesManager
    lateinit var adapter: TestAdapterAthlete
    lateinit var adapter2: EventAdapterAthlete
    private lateinit var Container: LinearLayout
    var sportsid:String?= null
    var dta:String? = null
    var Name:String? = null

    private var athleteData: MutableList<AthleteDatas.AthleteList> = mutableListOf()
    private var athleteDataDialog: MutableList<AthleteDatas.AthleteList> = mutableListOf()
    private var athleteDetails: MutableList<AthleteDetails.Athlete> = mutableListOf()

    private var mainId: Int = 0


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
                        Utils.setUnAuthDialog(this@ViewAetleteActivity)
                    } else {
                        Toast.makeText(
                            this@ViewAetleteActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Toast.makeText(
                        this@ViewAetleteActivity,
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
        viewAetleteBinding = ActivityViewAetleteBinding.inflate(layoutInflater)
        setContentView(viewAetleteBinding.root)
        initview()
        lordData()
        ButtonClick()

    }

    private fun lordData() {
        //GetTestList(mainId)
        fetchAthleteData(mainId)
        GetAthleteDetails(mainId)
    }

    private fun ButtonClick() {
        viewAetleteBinding.EditData.setOnClickListener { showDialog() }
        viewAetleteBinding.back.setOnClickListener { finish() }

        viewAetleteBinding.viewAthleteStatus.setOnClickListener {
            val intent = Intent(this, ViewAthleteStatusActivity::class.java)
            intent.putExtra("MainId", mainId)
            startActivity(intent)
        }

        viewAetleteBinding.cardPerformances.setOnClickListener {
//            val intent = Intent(this, PerformanceProfileActivity::class.java)
//            intent.putExtra("MainId", mainId)
//            startActivity(intent)

            startActivity(Intent(this, PerformanceProfileActivity::class.java).apply {
                putExtra("athleteId", mainId)
            })
        }
        viewAetleteBinding.cards.setOnClickListener {
            val intent = Intent(this, ViewAnalysisActivity::class.java)
            intent.putExtra("athleteId", mainId)
            intent.putExtra("athleteName", Name)
            startActivity(intent)
        }

        viewAetleteBinding.cardDiary.setOnClickListener {
            val intent = Intent(this, ViewPersonalDiaryActivity::class.java)
            intent.putExtra("athleteId", mainId)
            startActivity(intent)
        }
    }

    private fun initview() {
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        preferenceManager = PreferencesManager(this)
        TestList = ArrayList()
        athleteData = arrayListOf()
        Container = viewAetleteBinding.chvvp

        mainId = intent.getIntExtra("MainId", 0)
        Name = intent.getStringExtra("Name")

        Log.d("MainId","MainID:- "+ mainId)
        Log.d("MainId","MainID:- "+ Name)

    }
    private fun GetAthleteDetails(id: Int) {
        try {
            viewAetleteBinding.progressbar.visibility = View.VISIBLE

            athleteDetails.clear()

            apiInterface.GetAthleteDetails(id)
                .enqueue(object : Callback<AthleteDetails> {
                    override fun onResponse(
                        call: Call<AthleteDetails>,
                        response: Response<AthleteDetails>
                    ) {
                        viewAetleteBinding.progressbar.visibility = View.GONE

                        Log.d("TAGs", response.code().toString())
                        val code = response.code()
                        if (code == 200) {
                            if (response.isSuccessful && response.body() != null) {
                                val data = response.body()?.data

                                if (data != null) {
                                    Log.d("DATA_ITEM", "Id: ${data.id}")
                                    Log.d("DATA_ITEM", "Name: ${data.name}")
                                    if (data.userSports != null && data.userSports.isNotEmpty()) {
                                        data.userSports.forEach { userSport ->
                                            userSport.sport?.title?.let { sportTitle ->
                                                Log.d("DATA_ITEM", "Sports: $sportTitle")
                                            }
                                        }
                                    } else {
                                        Log.e("DATA_TAG", "No Sports Available")
                                    }

                                    athleteDetails.add(data)

                                    SetathleteDetails(athleteDetails)
                                } else {
                                    Log.e("DATA_TAG", "No Data Available")
                                }
                            } else {
                                Log.d("DATA_TAG", "No Data Available")
                                Toast.makeText(
                                    this@ViewAetleteActivity,
                                    "Error: ${response.code()} - ${response.message()}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else if (code == 403) {
                            Utils.setUnAuthDialog(this@ViewAetleteActivity)
                        } else {
                            Toast.makeText(
                                this@ViewAetleteActivity,
                                response.message(),
                                Toast.LENGTH_SHORT
                            ).show()
                            call.cancel()
                        }
                    }

                    override fun onFailure(call: Call<AthleteDetails>, t: Throwable) {
                        Log.e("API_CALL_FAILURE", "Error: ${t.localizedMessage}")
                        Toast.makeText(
                            this@ViewAetleteActivity,
                            "Api Call Failed: ${t.localizedMessage}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        } catch (e: Exception) {
            Log.d("errors", e.message.toString())
        }
    }

    private fun SetathleteDetails(data: MutableList<AthleteDetails.Athlete>) {
        try {
            if (data.isNotEmpty()) {
                viewAetleteBinding.tvCredit.text = data[0].name
                viewAetleteBinding.tvProgramName.text = data[0].name

                data[0].userSports?.let { sports ->

                    Container.removeAllViews()

                    sports.forEach { userSport ->
                        val sportTitle = userSport.sport?.title

                        val abilityLayout = LayoutInflater.from(this)
                            .inflate(R.layout.ability_item, Container, false)

                        val textView: TextView = abilityLayout.findViewById(R.id.ability_txt)

                        textView.text = sportTitle ?: "No Title"

                        Container.addView(abilityLayout)

                        Log.d("TAG", "Added TextView for Sport Title: $sportTitle")
                    }

                } ?: run {
                    Log.d("TAGs", "userSports is null for Athlete")
                }


                data[0].testAthletes?.let { testAthletes ->
                    adapter = TestAdapterAthlete(testAthletes,this,this)
                    viewAetleteBinding.recyclerTestList.adapter = adapter
                    viewAetleteBinding.recyclerTestList.layoutManager = LinearLayoutManager(this)
                } ?: run {
                    Log.d("TAG", "testAthletes is null for Athlete")
                }
                data[0].eventAthletes?.let { eventAthletes ->
                    adapter2 = EventAdapterAthlete(eventAthletes,this,this)
                    viewAetleteBinding.recyclerEventList.adapter = adapter2
                    viewAetleteBinding.recyclerEventList.layoutManager = LinearLayoutManager(this)
                } ?: run {
                    Log.d("TAG", "eventAthletes is null for Athlete")
                }

            } else {
                Toast.makeText(this, "No data available for the selected athlete.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.d("Exss", e.message.toString())
        }
    }

    private fun fetchAthleteData(id: Int) {
        try {

            viewAetleteBinding.progressbar.visibility = View.VISIBLE

            athleteData.clear()

            apiInterface.GetAthleteData(id)
                .enqueue(object : Callback<AthleteDatas> {
                    override fun onResponse(
                        call: Call<AthleteDatas>,
                        response: Response<AthleteDatas>
                    ) {
                        viewAetleteBinding.progressbar.visibility = View.GONE


                        Log.d("TAG", response.code().toString() + "")
                        val code = response.code()
                        if (code == 200) {
                            if (response.isSuccessful && response.body() != null) {
                                val data = response.body()?.athleteData

                                if (data != null) {
//                                    for (item in data) {
//                                        Log.d("DATA_ITEM", "Id: ${item.id}")
//                                        Log.d("DATA_ITEM", "Name: ${item.athleteId}")
//                                        Log.d("DATA_ITEM", "Start Date: ${item.fatMass}")
//                                        Log.d("DATA_ITEM", "End Date: ${item.athleteId}")
//                                        Log.d("DATA_ITEM", "Mesocycle: ${item.baseline}")
//                                    }
                                }

                                if (data!!.isNotEmpty()) {
                                    if (data != null) {
                                        athleteData.addAll(data)
                                    }
                                    //seasonId = data[0].id!!
                                    setAdapter(data.toMutableList())

                                } else {
                                    Log.e("DATA_TAG", "No Data Available")
                                    viewAetleteBinding.noSi.visibility = View.VISIBLE
                                    viewAetleteBinding.chvvp.visibility = View.GONE
//                                viewTrainingPlanBinding.add.setOnClickListener {
//                                    showToast("Please Add Mesocycle")
//                                }
                                }
                            } else {
                                Log.d("DATA_TAG", "No Data Available")
                                Toast.makeText(this@ViewAetleteActivity, "Error: ${response.code()} - ${response.message()}", Toast.LENGTH_SHORT).show()
                            }
                        } else if (code == 403) {
                            Utils.setUnAuthDialog(this@ViewAetleteActivity)
                        } else {
                            Toast.makeText(
                                this@ViewAetleteActivity,
                                "" + response.message(),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            call.cancel()
                        }
                    }

                    override fun onFailure(call: Call<AthleteDatas>, t: Throwable) {
                        Log.d("TAG Category", t.message.toString())
                        Toast.makeText(this@ViewAetleteActivity, "Api Call Failed: ${t.message.toString()}", Toast.LENGTH_SHORT).show()
                        call.cancel()
                    }
                })
        }catch (e:Exception){
            Log.d("error",e.message.toString())
        }
    }

    private fun setAdapter(data: MutableList<AthleteDatas.AthleteList> = mutableListOf()) {
        try {
            if (data.isNotEmpty()) {
                val lastItem = data.last()

                viewAetleteBinding.HeightEdt.text = "Height - ${lastItem.baseline} cm"
                viewAetleteBinding.WeightEdt.text = "Weight - ${lastItem.weight} kg"
                viewAetleteBinding.FatEdt.text = "Fat - ${lastItem.fatData}%"
            } else {
                Log.d("TAG","No data available for the selected training plan.")
            }
        } catch (e: Exception) {
            Log.d("Exss", e.message.toString())
        }
    }


    private fun showDialog(data : MutableList<AthleteDatas.AthleteList> = mutableListOf()) {
        try {
            Log.d("Dialog", "Preparing to show dialog")
            val dialog = Dialog(this)
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val width = (displayMetrics.widthPixels * 0.9f).toInt()
            val height = WindowManager.LayoutParams.WRAP_CONTENT
            dialog.window!!.setLayout(width, height)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.add_athelete_status)
            dialog.show()


            Log.d("ok","ok")
            Log.d("Log","Data" + athleteData.get(0).weight)

            val Apply = dialog.findViewById<CardView>(R.id.card_add)

            val cancal = dialog.findViewById<CardView>(R.id.crad_cancel)

            val dateedt = dialog.findViewById<androidx.appcompat.widget.AppCompatEditText>(R.id.edt_name)
            dateedt.setText(athleteData.get(0).date)
            dateedt.setOnClickListener {
                val calendar = Calendar.getInstance()

                // Create a DatePickerDialog to select a date
                val datePickerDialog = DatePickerDialog(
                    this,
                    { _, year, monthOfYear, dayOfMonth ->
                        // Create a calendar instance and set the selected date
                        calendar.set(year, monthOfYear, dayOfMonth)

                        // Format the date as "dd MMM, yyyy"
                        val dateFormat = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
                        val formattedDate = dateFormat.format(calendar.time)

                        // Set the formatted date in the EditText
                        dateedt.setText(formattedDate)
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )

                // Show the DatePickerDialog
                datePickerDialog.show()
            }


            val heightedt = dialog.findViewById<EditText>(R.id.Height_edt)
            heightedt.setText(athleteData.get(0).baseline)

            val weightedt = dialog.findViewById<EditText>(R.id.Weight_edt)
            weightedt.setText(athleteData.get(0).fatData)

            val fatedt = dialog.findViewById<EditText>(R.id.Fat_edt)
            fatedt.setText(athleteData.get(0).weight)

            cancal.setOnClickListener{dialog.cancel()}
            Apply.setOnClickListener {
                try {
                    viewAetleteBinding.progressbar.visibility = View.VISIBLE

                    val addAthleteDataModel = AthleteData(
                        id = mainId,
                        athlete_id = mainId.toString(),
                        base_line = heightedt.text.toString(),
                        date = dateedt.text.toString(),
                        fat_data = fatedt.text.toString(),
                        fat_mass = "",
                        weight = weightedt.text.toString(),
                        created_at = "",
                        updated_at = ""
                    )

                    apiInterface.addAthleteData(addAthleteDataModel)!!.enqueue(object : Callback<AthleteDatass> {
                        override fun onResponse(call: Call<AthleteDatass>, response: Response<AthleteDatass>) {
                            viewAetleteBinding.progressbar.visibility = View.GONE

                            if (response.isSuccessful) {
                                val athleteDatas = response.body()
                                if (athleteDatas != null) {
                                    val rawData = athleteDatas.data

                                    val athleteList: List<AthleteData> = when (rawData) {
                                        is List<*> -> {
                                            // Data is already a list of AthleteData
                                            rawData.filterIsInstance<AthleteData>()
                                        }
                                        is AthleteData -> {
                                            // Wrap single object into a list
                                            listOf(rawData)
                                        }
                                        else -> {
                                            emptyList()
                                        }
                                    }
                                     dialog.cancel()
                                        lordData()

                                        for (athlete in athleteList) {
                                            Log.d("Athlete", "Athlete ID: ${athlete.athlete_id}")
                                        }

                                } else {
                                    Toast.makeText(this@ViewAetleteActivity, "Response body is null", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                                Log.e("API Error", "Response: ${response.code()} - $errorBody")
                                Toast.makeText(
                                    this@ViewAetleteActivity,
                                    "Error: $errorBody",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }



                        override fun onFailure(call: Call<AthleteDatass>, t: Throwable) {
                            viewAetleteBinding.progressbar.visibility = View.GONE
                            Log.e("API Error", "Error: ${t.message}")
                            Toast.makeText(
                                this@ViewAetleteActivity,
                                "Network error: ${t.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
                } catch (e: Exception) {
                    viewAetleteBinding.progressbar.visibility = View.GONE
                    Log.e("Catch", "Exception: ${e.message}")
                }
            }

            Log.d("Dialog", "Dialog displayed successfully")
        } catch (e: Exception) {
            Log.e("Exception", "Error showing dialog: ${e.message}")
        }
    }

    private fun formatDate2(dateMillis: Long): String {
        val format = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
        return format.format(Date(dateMillis))
    }

//    private fun addAthleteDataToApi(addAthleteDataModel: AddAthleteDataModel, dialog: Dialog) {
//        try {
//            viewAetleteBinding.progressbar.visibility = View.VISIBLE
//
//            val addAthleteDataModel = AddAthleteDataModel(
//                athlete_id = mainId,
//                base_line = "",
//                date = "",
//                fat_data = "",
//                fat_mass = "",
//                weight = "",
//            )
//
//            apiInterface.addAthleteData(addAthleteDataModel)
//                ?.enqueue(object : Callback<com.example.model.AthleteDataPackage.com.example.model.AthleteDataPackage.AthleteDatas> {
//                    override fun onResponse(
//                        call: Call<com.example.model.AthleteDataPackage.com.example.model.AthleteDataPackage.AthleteDatas>,
//                        response: Response<com.example.model.AthleteDataPackage.com.example.model.AthleteDataPackage.AthleteDatas>
//                    ) {
//                        Log.d("APIResponse", "Response: ${response.code()}")
//
//                        if (response.isSuccessful) {
//                            Toast.makeText(this@ViewAetleteActivity, "Ability Added", Toast.LENGTH_SHORT).show()                                } else {
//                            val errorBody =
//                                response.errorBody()?.string() ?: "Unknown error"
//
//                            Log.e("API Error", "Response: ${response.code()} - $errorBody")
//                            Toast.makeText(
//                                this@ViewAetleteActivity,
//                                "Error: $errorBody",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                    }
//
//                    override fun onFailure(call: Call<com.example.model.AthleteDataPackage.com.example.model.AthleteDataPackage.AthleteDatas>, t: Throwable) {
//                        Log.e("API Error", "Error: ${t.message}")
//                        Toast.makeText(
//                            this@ViewAetleteActivity,
//                            "Network error: ${t.message}",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                })
//        } catch (e: Exception) {
//            Log.e("Catch", "Exception: ${e.message}")
//        }
//    }

    override fun onItemClicked(view: View, position: Int, type: Long, string: String) {

    }

}