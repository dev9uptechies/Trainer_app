package com.example.trainerapp.program_section

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.Adapter.program.View_Program_Adapter
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.CycleData
import com.example.trainerapp.ApiClass.ProgramListData
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.PreferencesManager
import com.example.trainerapp.R
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.ActivityViewProgramBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewProgramActivity : AppCompatActivity() {
    lateinit var viewProgramBinding: ActivityViewProgramBinding
    lateinit var apiInterface: APIInterface
    lateinit var programData: MutableList<ProgramListData.testData>
    lateinit var apiClient: APIClient
    lateinit var preferenceManager: PreferencesManager
    lateinit var adapter: View_Program_Adapter
    var excList: ArrayList<ProgramListData.Program> = arrayListOf()
    var id: Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewProgramBinding = ActivityViewProgramBinding.inflate(layoutInflater)
        setContentView(viewProgramBinding.root)
        initViews()
        initRecyclerView(excList)
        loadData()
        checkButtonClick()
    }

    private fun checkButtonClick() {
        viewProgramBinding.cardDuplicate.setOnClickListener {
            showDialog()
        }
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
                        Utils.setUnAuthDialog(this@ViewProgramActivity)
                    } else {
                        Toast.makeText(
                            this@ViewProgramActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Toast.makeText(
                        this@ViewProgramActivity,
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

    private fun showDialog() {

        val dialog = Dialog(this)
        dialog.setContentView(R.layout.alert_dialog_view)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val title = dialog.findViewById<AppCompatTextView>(R.id.tvTitle)
        val subtitle = dialog.findViewById<AppCompatTextView>(R.id.tvSubtitle)
        val cancel = dialog.findViewById<AppCompatButton>(R.id.btnCancel)
        val apply = dialog.findViewById<AppCompatButton>(R.id.btnApply)

        title.visibility = View.GONE
        subtitle.text = "Want You Duplicate the Program? \n\n" +
                "Are You Sure to duplicate the program?"
        cancel.setOnClickListener {
            dialog.cancel()
        }
        apply.setOnClickListener {
            dialog.dismiss()
            viewProgramBinding.progresBar.visibility = View.VISIBLE
            try {
                apiInterface.DuplicateProgram(id = id).enqueue(object : Callback<CycleData> {
                    override fun onResponse(
                        call: Call<CycleData>,
                        response: Response<CycleData>
                    ) {
                        Log.d("TAG", response.code().toString() + "")
                        val code = response.code()
                        if (code == 200) {
                            val resource = response.body()
                            val Success = resource?.status
                            val Message = resource?.message
                            viewProgramBinding.progresBar.visibility = View.GONE
                            Toast.makeText(
                                this@ViewProgramActivity,
                                "" + Message,
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        } else if (code == 403) {
                            Utils.setUnAuthDialog(this@ViewProgramActivity)
                        } else {
                            Toast.makeText(
                                this@ViewProgramActivity,
                                "" + response.message(),
                                Toast.LENGTH_SHORT
                            ).show()
                            call.cancel()
                        }
                    }

                    override fun onFailure(call: Call<CycleData>, t: Throwable) {
                        viewProgramBinding.progresBar.visibility = View.GONE
                        Toast.makeText(
                            this@ViewProgramActivity,
                            "" + t.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        dialog.show()

//        val builder: AlertDialog.Builder
//        builder = AlertDialog.Builder(this)
//        //builder.setMessage("Are you sure you want to delete Program?").setTitle("Success")
//
//        builder.setMessage("Want You Duplicate the Program? \n Are You Sure to duplicate the program?")
//            .setCancelable(false)
//            .setPositiveButton("Apply") { dialog, id ->
//                dialog.dismiss()
//                viewProgramBinding.progresBar.visibility = View.VISIBLE
//                try {
//                    apiInterface.DuplicateProgram(id = id).enqueue(object : Callback<CycleData> {
//                        override fun onResponse(
//                            call: Call<CycleData>,
//                            response: Response<CycleData>
//                        ) {
//                            Log.d("TAG", response.code().toString() + "")
//                            val resource = response.body()
//                            val Success = resource?.status
//                            val Message = resource?.message
//                            viewProgramBinding.progresBar.visibility = View.GONE
//                            Toast.makeText(
//                                this@ViewProgramActivity,
//                                "" + Message,
//                                Toast.LENGTH_SHORT
//                            ).show()
//                            finish()
//                        }
//
//                        override fun onFailure(call: Call<CycleData>, t: Throwable) {
//                            viewProgramBinding.progresBar.visibility = View.GONE
//                            Toast.makeText(
//                                this@ViewProgramActivity,
//                                "" + t.message,
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//
//                    })
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
////                apiInterface.DeleteProgram(type.toInt())
////                    ?.enqueue(object : Callback<RegisterData?> {
////                        override fun onResponse(
////                            call: Call<RegisterData?>,
////                            response: Response<RegisterData?>
////                        ) {
////                            Log.d("TAG", response.code().toString() + "")
////                            val resource: RegisterData? = response.body()
////                            val Success: Boolean = resource?.status!!
////                            val Message: String = resource.message!!
////                            newProgramBinding.progresBar.visibility = View.GONE
////                            Toast.makeText(
////                                this@New_Program_Activity,
////                                "" + Message,
////                                Toast.LENGTH_SHORT
////                            )
////                                .show()
////                            setRefresh()
////                            refreshData()
////                        }
////
////                        override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
////                            newProgramBinding.progresBar.visibility = View.GONE
////                            Toast.makeText(
////                                this@New_Program_Activity,
////                                "" + t.message,
////                                Toast.LENGTH_SHORT
////                            )
////                                .show()
////                            call.cancel()
////                        }
////                    })
//            }
//            .setNegativeButton(
//                "cancel"
//            ) { dialog, id -> //  Action for 'NO' Button
//                dialog.cancel()
//            }
//
//        val alert = builder.create()
//        alert.show()
    }

    private fun loadData() {
        try {
            GetProgramData()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initViews() {
        preferenceManager = PreferencesManager(this)
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        programData = mutableListOf()
        id = intent.getIntExtra("id", 0)
    }

    private fun GetProgramData() {
        viewProgramBinding.progresBar.visibility = View.VISIBLE
        programData.clear()
        apiInterface.GetProgam()?.enqueue(object : Callback<ProgramListData?> {
            override fun onResponse(
                call: Call<ProgramListData?>,
                response: Response<ProgramListData?>
            ) {
                val code = response.code()
                if (code == 200) {
                    Log.d(
                        "TAG",
                        response.code()
                            .toString() + " " + response.body()!!.message!! + " " + response.body()!!.status
                    )
                    val resource: ProgramListData? = response.body()
                    val Success: Boolean = resource?.status!!
                    val Message: String = resource.message!!
                    viewProgramBinding.progresBar.visibility = View.GONE
                    Log.d("TAG", resource.data.toString())
                    if (Success) {
                        try {
                            val data = response.body()!!.data!!.filter {
                                it.id == id
                            }
                            if (data != null) {
                                programData.addAll(data)
                                setProgramData()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        Toast.makeText(this@ViewProgramActivity, "" + Message, Toast.LENGTH_SHORT)
                            .show()
                    }
                } else if (code == 403) {
                    Utils.setUnAuthDialog(this@ViewProgramActivity)
                } else {
                    Toast.makeText(
                        this@ViewProgramActivity,
                        "" + response.message(),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    call.cancel()
                }

            }

            override fun onFailure(call: Call<ProgramListData?>, t: Throwable) {
                viewProgramBinding.progresBar.visibility = View.GONE
                Toast.makeText(this@ViewProgramActivity, "" + t.message, Toast.LENGTH_SHORT)
                    .show()
                call.cancel()
            }
        })
    }

    private fun setProgramData() {
        viewProgramBinding.edtProgramName.setText(programData[0].name)
        viewProgramBinding.edtGoal.setText(programData[0].goal!!.name)
        viewProgramBinding.edtSection.setText(programData[0].section!!.name)
        viewProgramBinding.edtTime.setText(programData[0].time)

        initRecyclerView(programData[0].program_exercises!!)
//        for (i in programData) {
//            Log.d("Program Data :-", "${i.name}")
//            for (j in i.program_exercises!!) {
//                Log.d("Program Exe Data :-", "${j.exercise!!.name}")
//            }
//        }
    }

    private fun initRecyclerView(programExercises: ArrayList<ProgramListData.Program>) {
        viewProgramBinding.exerciseRecycler.layoutManager = LinearLayoutManager(this)
        adapter = View_Program_Adapter(programExercises, this)
        viewProgramBinding.exerciseRecycler.adapter = adapter
    }
}