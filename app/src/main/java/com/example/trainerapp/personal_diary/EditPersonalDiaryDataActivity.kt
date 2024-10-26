package com.example.trainerapp.personal_diary

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.model.personal_diary.GetDiaryDataForEdit
import com.example.model.personal_diary.GetPersonalDiary
import com.example.model.personal_diary.GetPersonalDiaryData
import com.example.model.personal_diary.TrainingAssessment
import com.example.model.personal_diary.TrainingSession
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.PreferencesManager
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.ActivityEditPersonalDiaryDataBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditPersonalDiaryDataActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditPersonalDiaryDataBinding
    private lateinit var apiInterface: APIInterface
    private lateinit var preferenceManager: PreferencesManager
    private lateinit var apiClient: APIClient
    private lateinit var diaryDataa: MutableList<GetDiaryDataForEdit.Data>
    private var date: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPersonalDiaryDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        date = intent.getStringExtra("Date")
        Log.d("DEBUG", "onCreate: Date received from intent: $date")

        initViews()
        loadData()
        setupButtonClickListeners()

    }

    private fun setupButtonClickListeners() {
        binding.back.setOnClickListener { finish() }

        binding.cardSave.setOnClickListener{ saveDiaryData() }
    }

    private fun loadData() {
        getPersonalDiaryData(date.toString())
    }

    private fun initViews() {
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        preferenceManager = PreferencesManager(this)
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
                            // Check if the response is successful and the body is not null
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
                                    Toast.makeText(
                                        this@EditPersonalDiaryDataActivity,
                                        "No diary data available",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } ?: run {
                                Log.e("ERROR", "Response body is null")
                                Toast.makeText(
                                    this@EditPersonalDiaryDataActivity,
                                    "Failed to retrieve data",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        403 -> {
                            Utils.setUnAuthDialog(this@EditPersonalDiaryDataActivity)
                        }
                        else -> {
                            Log.e("ERROR", "Error: ${response.message()}")
                            Toast.makeText(
                                this@EditPersonalDiaryDataActivity,
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
                        this@EditPersonalDiaryDataActivity,
                        "Network error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } catch (e: Exception) {
            Log.e("error", "Exception: ${e.message}")
            Toast.makeText(
                this@EditPersonalDiaryDataActivity,
                "Unexpected error: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun saveDiaryData() {
        // Create an instance of the data class to hold the updated diary data
        val updatedDiaryData = TrainingSession(
            date = binding.dateTextView.text.toString(),
            sleep_hours = binding.sleepHoursTextView.text.toString(),
            nutrition_and_hydration = binding.NutritionAndHydration.text.toString(),
            notes = binding.Notes.text.toString(),
            share = 0,
            data = createPersonalDiaryDetails()
        )

        // Call the API to save the data
        apiInterface.AddPersonalDIaryData(updatedDiaryData)?.enqueue(object : Callback<GetPersonalDiaryData> {
            override fun onResponse(call: Call<GetPersonalDiaryData>, response: Response<GetPersonalDiaryData>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@EditPersonalDiaryDataActivity, "Data saved successfully", Toast.LENGTH_SHORT).show()
                    finish() // Close the activity if save is successful
                } else {
                    Toast.makeText(this@EditPersonalDiaryDataActivity, "Failed to save data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<GetPersonalDiaryData>, t: Throwable) {
                Toast.makeText(this@EditPersonalDiaryDataActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun createPersonalDiaryDetails(): List<TrainingAssessment> {
        return listOf(
            TrainingAssessment("Energy", binding.EnergyBT.text.toString(), binding.EnergyDT.text.toString(), binding.EnergyAT.text.toString()),
            TrainingAssessment("Satisfaction", binding.SatisfationBT.text.toString(), binding.SatisfationDT.text.toString(), binding.SatisfationAT.text.toString()),
            TrainingAssessment("Happiness", binding.HapinessBT.text.toString(), binding.HapinessDT.text.toString(), binding.HapinessAT.text.toString()),
            TrainingAssessment("Irritability", binding.IrritabilityBT.text.toString(), binding.IrritabilityDT.text.toString(), binding.IrritabilityAT.text.toString()),
            TrainingAssessment("Determination", binding.DeterminationBT.text.toString(), binding.DeterminationDT.text.toString(), binding.DeterminationAT.text.toString()),
            TrainingAssessment("Anxiety", binding.AnxietyBT.text.toString(), binding.AnxietyDT.text.toString(), binding.AnxietyAT.text.toString()),
            TrainingAssessment("Tiredness", binding.TirednessBT.text.toString(), binding.TirednessDT.text.toString(), binding.TirednessAT.text.toString())
        )
    }


    private fun SetData(data: GetDiaryDataForEdit.Data) {
        // Set basic details
        binding.dateTextView.text = data.date ?: ""
        binding.sleepHoursTextView.text = data.sleepHours ?: ""
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



}
