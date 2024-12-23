package com.example.trainerappAthlete

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.Adapter.groups.GetTestListAdapterGroup
import com.example.Adapter.groups.SetTestInGroup
import com.example.OnItemClickListener
import com.example.TestDataAdapter
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.ApiClass.User_Sportd_Data
import com.example.trainerapp.PreferencesManager
import com.example.trainerapp.R
import com.example.trainerapp.ShowSportAdaper
import com.example.trainerapp.Sport_list
import com.example.trainerapp.TestListData
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.FragmentHomeBinding
import com.example.trainerapp.databinding.FragmentProfileBinding
import com.makeramen.roundedimageview.RoundedTransformationBuilder
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ProfileFragment : Fragment(),OnItemClickListener.OnItemClickCallback {

    lateinit var binding: FragmentProfileBinding

    lateinit var preferenceManager: PreferencesManager
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    private lateinit var Container: LinearLayout
    private lateinit var Sportlist: java.util.ArrayList<Sport_list>
    private var selectedImageUri: Uri? = null
    lateinit var adapterTest: SetTestInGroup



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        initView()
        GetProfile()
        checkBoxManage()
        buttonClick()
        GetTestList()

        return binding.root
    }

    private fun buttonClick() {
        binding.cardEdtSave.setOnClickListener { UpdateProfile() }

        binding.roundImage.setOnClickListener {
            getContent.launch("image/*")

        }
    }

    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedImageUri = it
                binding.roundImage.setImageURI(uri)
            }
        }

    private fun checkBoxManage() {
        val checkBoxBasicPlan = binding.checkBoxBasicPlan
        val checkBoxTrainerProPlan = binding.checkBoxTrainerProPlan
        val checkBoxTopTrainerPan = binding.checkBoxTopTrainerPan

        val checkboxes = listOf(checkBoxBasicPlan, checkBoxTrainerProPlan, checkBoxTopTrainerPan)

        checkboxes.forEach { checkBox ->
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    checkboxes.forEach { otherCheckBox ->
                        if (otherCheckBox != checkBox) {
                            otherCheckBox.isChecked = false
                        }
                    }
                }
            }
        }
    }

    private fun GetProfile() {
        binding.progressBar.visibility = View.VISIBLE

        apiInterface.ProfileData()?.enqueue(object : Callback<RegisterData?> {
            override fun onResponse(call: Call<RegisterData?>, response: Response<RegisterData?>) {
                binding.progressBar.visibility = View.GONE

                if (response.isSuccessful) {
                    val resource = response.body()

                    val data = resource?.data
                    if (data != null) {
                        Sportlist.clear()

                        setData(data)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "No profile data available",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Log.e("TAG", "Error: ${response.code()}")
                    Toast.makeText(requireContext(), "Failed to load profile", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Log.d("FERROR", "onFailure: ${t.message}")
                Toast.makeText(requireContext(), "Failed to load profile", Toast.LENGTH_SHORT)
                    .show()
                call.cancel()
            }
        })
    }


    private fun setData(data: RegisterData.Data?) {
        binding.groupName.setText(data?.name ?: "")
        binding.edtEmail.setText(data?.email ?: "")
        binding.edtBirthday.setText(data?.birthdate ?: "")
        binding.edtBirthdayPlace.setText(data?.address ?: "")
        binding.edtZipCode.setText(data?.zipcode ?: "")

        binding.belowEdt.setText(data?.below ?: "Enter below")
        binding.athletesEdt.setText(data?.athletes ?: "Enter Athletes")
        binding.baselineEdt.setText(data?.baseline ?: "Enter Baseline")
        binding.fatMassEdt.setText(data?.fatMass ?: "Enter Fat mass")

        val transformation: Transformation = RoundedTransformationBuilder()
            .borderColor(Color.BLACK)
            .borderWidthDp(1f)
            .cornerRadiusDp(10f)
            .oval(false)
            .build()

        Picasso.get()
            .load("https://trainers.codefriend.in" + data?.image)
            .fit()
            .transform(transformation)
            .into(binding.roundImage)


        val image = data!!.image
        selectedImageUri = image?.let { Uri.parse(it) }


        val userSports = data?.userSports
        if (userSports != null && userSports.isNotEmpty()) {
            binding.noSi.visibility = View.GONE
            Container.visibility = View.VISIBLE
            Container.removeAllViews()

            userSports.forEach { userSport ->
                val sportTitle = userSport.sport?.title ?: "No Title"

                val abilityLayout = LayoutInflater.from(requireContext())
                    .inflate(R.layout.ability_item, Container, false)


                val textView: TextView = abilityLayout.findViewById(R.id.ability_txt)
                textView.text = sportTitle

                Container.addView(abilityLayout)
                Log.d("MNMNMN", "setData: $sportTitle added to Container")
            }

            Log.d("ContainerChildCount", "Total children in Container: ${Container.childCount}")
        } else {
            Log.d("TAGs", "userSports is null or empty for Athlete")
            binding.noSi.visibility = View.VISIBLE
            Container.visibility = View.GONE
        }
    }


    private fun initView() {
        apiClient = APIClient(requireContext())
        apiInterface =
            apiClient.client().create(APIInterface::class.java)
        preferenceManager = PreferencesManager(requireContext())
        Log.d("Login Token", preferenceManager.getToken()!!)
        Container = binding.chvvp

        Sportlist = ArrayList()

    }

    private fun getFileFromUri(uri: Uri): File? {
        val filePath = getRealPathFromURI(uri)
        return if (filePath != null) File(filePath) else null
    }

    private fun getRealPathFromURI(uri: Uri): String? {
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        return if (cursor != null && cursor.moveToFirst()) {
            val index = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
            if (index != -1) {
                val path = cursor.getString(index)
                cursor.close()
                path
            } else {
                cursor.close()
                null
            }
        } else {
            null
        }
    }

    private fun UpdateProfile() {
        binding.progressBar.visibility = View.VISIBLE

        val imageFile = selectedImageUri?.let { getFileFromUri(it) }
        Log.d("VBBVBV", "UpdateProfile: $selectedImageUri")
        Log.d("VBBVBV", "UpdateProfile: $imageFile")

        if (imageFile == null || !imageFile.exists()) {
            Log.e("ERROR", "Invalid file URI or file does not exist.")
            binding.progressBar.visibility = View.GONE
            return
        }

        val groupName = binding.groupName.text.toString()
        val email = binding.edtEmail.text.toString()

        val nameRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), groupName)
        val emailRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), email)
        val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), imageFile)
        val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)

        apiInterface.EditProfileAthlete(nameRequestBody, emailRequestBody, imagePart)
            ?.enqueue(object : Callback<RegisterData?> {
                override fun onResponse(
                    call: Call<RegisterData?>,
                    response: Response<RegisterData?>
                ) {
                    binding.progressBar.visibility = View.GONE

                    if (response.isSuccessful) {
                        val resource = response.body()
                        val success = resource?.status ?: false
                        val message = resource?.message ?: "No message"

                        if (success) {
                            Log.d("UpdateProfile", "Profile updated successfully.")
                            Toast.makeText(
                                requireContext(),
                                "Profile updated successfully.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Log.d("UpdateProfile", "Error: $message")
                            Toast.makeText(
                                requireContext(),
                                "Failed to update profile: $message",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Log.e("UpdateProfile", "Error: ${response.code()}")
                        Toast.makeText(
                            requireContext(),
                            "Failed to update profile: ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    binding.progressBar.visibility = View.GONE
                    Log.e("UpdateProfile", "Error: ${t.message}")
                    Toast.makeText(
                        requireContext(),
                        "Failed to update profile: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun GetTestList() {
        try {

            binding.progressBar.visibility = View.VISIBLE
            apiInterface.GetTest()?.enqueue(object : Callback<TestListData?> {
                override fun onResponse(
                    call: Call<TestListData?>,
                    response: Response<TestListData?>
                ) {
                    Log.d("TAG", response.code().toString() + "")
                    val code = response.code()
                    if (code == 200) {
                        val resource: TestListData? = response.body()
                        val Success: Boolean = resource?.status!!
                        val Message: String = resource.message!!
                        if (Success == true) {
                            try {
                                if (resource.data!! != null) {
                                    initrecyclerTest(resource.data)
                                } else {
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else {
                            binding.progressBar.visibility = View.GONE
                        }
                    } else if (response.code() == 403) {
                        Utils.setUnAuthDialog(requireContext())

                    } else {
                        val message = response.message()
                        Log.d("ERRROR", "onResponse: $message")
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<TestListData?>, t: Throwable) {
                    binding.progressBar.visibility = View.GONE
                    Log.d("ERRROR", "onFailure: ${t.message}")
                    call.cancel()
                }
            })

        } catch (e: Exception) {
            Log.d("ERROR", "GetTestList Catch: ${e.message.toString()}")
        }

    }

    private fun initrecyclerTest(testdatalist: ArrayList<TestListData.testData>?) {
        binding.progressBar.visibility = View.GONE
        binding.rcvtest.layoutManager = LinearLayoutManager(requireContext())
        adapterTest = SetTestInGroup(testdatalist, requireContext(), this)
        binding.rcvtest.adapter = adapterTest
    }

    override fun onItemClicked(view: View, position: Int, type: Long, string: String) {
        TODO("Not yet implemented")
    }

}
