package com.example

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.PreferencesManager
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.ActivityCrateBinding
import com.example.trainerapp.program_section.New_Program_Activity
import com.example.trainerapp.timer_section.CreateTimerActivity
import com.example.trainerapp.training_plan.TrainingPlanActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CrateActivity : AppCompatActivity() {
    lateinit var preferenceManager: PreferencesManager
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    lateinit var crateBinding: ActivityCrateBinding

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
                        Utils.setUnAuthDialog(this@CrateActivity)
                    } else {
                        Toast.makeText(
                            this@CrateActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Toast.makeText(
                        this@CrateActivity,
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
        crateBinding = ActivityCrateBinding.inflate(layoutInflater)
        setContentView(crateBinding.root)
        preferenceManager = PreferencesManager(this)
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)

        crateBinding.back.setOnClickListener {
            finish()
        }

        crateBinding.cardProgram.setOnClickListener {
            preferenceManager.setexercisedata(false)
            startActivity(Intent(this, New_Program_Activity::class.java))

        }

        crateBinding.cardTest.setOnClickListener {
            preferenceManager.setselectAthelete(false)
            startActivity(Intent(this, TestActivity::class.java))
        }

        crateBinding.cardEvent.setOnClickListener {
            preferenceManager.setselectAthelete(false)
            startActivity(Intent(this, Create_Event_Activity::class.java))
        }

        crateBinding.cardLesson.setOnClickListener {
            preferenceManager.setexercisedata(false)
            startActivity(Intent(this, LessonActivity::class.java))
        }

        crateBinding.cardExercise.setOnClickListener {
            startActivity(Intent(this, ExerciseActivity::class.java))
        }

        crateBinding.cardTrainingPlan.setOnClickListener {
            startActivity(Intent(this, TrainingPlanActivity::class.java))
        }
        crateBinding.cardTimer.setOnClickListener {
            startActivity(Intent(this, CreateTimerActivity::class.java))
        }

    }
}