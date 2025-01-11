package com.example.trainerappAthlete

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class ProfileFragment : Fragment(), OnItemClickListener.OnItemClickCallback {

    lateinit var binding: FragmentProfileBinding

    lateinit var preferenceManager: PreferencesManager
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    private lateinit var Container: LinearLayout
    private lateinit var Sportlist: java.util.ArrayList<Sport_list>
    private var selectedImageUri: Uri? = null
    lateinit var adapterTest: SetTestInGroup
    val sportsIds = mutableListOf<Int>()



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

        val pass = preferenceManager.GetPassword()
        binding.edtPassword.setText(pass)
        Log.d("FHFHFFGGFG", "onCreate: ${preferenceManager.GetPassword()}")

        return binding.root
    }

    private fun buttonClick() {
        binding.cardEdtSave.setOnClickListener {
            Log.d("IOIOIOIO", "buttonClick: $selectedImageUri")

            UpdateProfile(requireContext(), selectedImageUri)
        }

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

        apiInterface.ProfileDataAthlete()?.enqueue(object : Callback<RegisterData?> {
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

        binding.belowEdt.setText(/*"Below - " +*/ data?.below ?: "Enter below")
        binding.athletesEdt.setText(/*"Athlete - "+*/ data?.athletes/* + " (Fat)" */
            ?: "Enter Athletes"
        )
        binding.baselineEdt.setText(/*"Baseline - "+*/data?.baseline ?: "Enter Baseline")
        binding.fatMassEdt.setText(/*"Fat mass - "+*/data?.fatMass /*+ " (KG)"*/
            ?: "Enter Fat mass"
        )

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

        val imageUrl =
            "https://trainers.codefriend.in" + (data?.image ?: "")
        Log.d("ImageURL", "URL: $imageUrl")

        val sharedPreferences = context?.getSharedPreferences("appPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        editor?.putString("imageUrll", imageUrl)
        editor?.apply()


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


                for (userSport in userSports) { // Assuming userSportsList contains your data
                    val sportId = userSport.sport?.id
                    sportsIds.add(sportId!!)
                    Log.d("OOOOOOOOO", "Processing ID: $sportId")
                }

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

    private fun UpdateProfile(context: Context, imageUri: Uri?) {
        binding.progressBar.visibility = View.VISIBLE
        if (!validations()) {
            binding.progressBar.visibility = View.GONE
            return // Stop execution if validation fails
        }


        val imageFile = selectedImageUri?.let { getFileFromUri(it) }
        Log.d("VBBVBV", "UpdateProfile: $selectedImageUri")
        Log.d("VBBVBV", "UpdateProfile: $imageFile")
        //        Log.d("QPQPPQPQPQP", "setData: $sportsIds")


        val imageParttest = processImage(context, imageUri)

        Log.d("IMAGEPARTFORTEST", "UpdateProfile: $imageParttest")

        if (imageParttest == null) {
            Log.e("ERRORNUll", "Invalid file URI or file does not exist.")
            binding.progressBar.visibility = View.GONE
            return
        }


        val groupName = binding.groupName.text.toString()
        val email = binding.edtEmail.text.toString()
        val birthdate = binding.edtBirthday.text.toString()
        val address = binding.edtBirthdayPlace.text.toString()
        val zipcode = binding.edtZipCode.text.toString()
        val below = binding.belowEdt.text.toString()
        val athletes = binding.athletesEdt.text.toString()
        val baseline = binding.baselineEdt.text.toString()
        val fat_mass = binding.fatMassEdt.text.toString()


        val Sportsids = sportsIds.joinToString(",", prefix = "[", postfix = "]")
        Log.d("QPQPPQPQPQP", "setData: $Sportsids")


        val nameRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), groupName)
        val emailRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), email)
        val birthdateRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), birthdate)
        val addressRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), address)
        val zipcodeRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), zipcode)
        val belowRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), below)
        val athletesRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), athletes)
        val baselineRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), baseline)
        val fat_massRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), fat_mass)
        val sportidReuestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), Sportsids)
        //        val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), imageFile)
        //        val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)

        apiInterface.EditProfileAthlete(
            nameRequestBody,
            emailRequestBody,
            birthdateRequestBody,
            addressRequestBody,
            zipcodeRequestBody,
            imageParttest,
            sportidReuestBody,
            belowRequestBody,
            athletesRequestBody,
            baselineRequestBody,
            fat_massRequestBody
        )
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

    fun processImage(context: Context, imageUri: Uri?): MultipartBody.Part? {
        Log.d("ProcessImage", "processImage: URI -> $imageUri")

        // If a new image URI is selected, process it
        val imageFile = imageUri?.let { createFileFromContentUri(context, it) }
        if (imageFile != null && imageFile.exists()) {
            Log.d("ProcessImage", "Image file found: ${imageFile.absolutePath}")
            val imageRequestBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            return MultipartBody.Part.createFormData("image", imageFile.name, imageRequestBody)
        } else {
            Log.e("ProcessImage", "Image file not found or inaccessible.")
        }

        // Fallback to the image stored in SharedPreferences if no new image is selected
        val imageUrl = getImageUriFromPreferences(context)?.toString()
        Log.d("ProcessImage", "Fallback: Image URL from SharedPreferences -> $imageUrl")

        if (!imageUrl.isNullOrEmpty()) {
            // If image URL starts with "http" or "https", treat it as remote and download
            if (imageUrl.startsWith("http") || imageUrl.startsWith("https")) {
                val imageFileFromUrl = runBlocking { convertUrlToFile(context, imageUrl) }
                if (imageFileFromUrl != null && imageFileFromUrl.exists()) {
                    val imageRequestBody =
                        imageFileFromUrl.asRequestBody("image/*".toMediaTypeOrNull())
                    return MultipartBody.Part.createFormData(
                        "image",
                        imageFileFromUrl.name,
                        imageRequestBody
                    )
                } else {
                    Log.e("ProcessImage", "Error creating image file from URL.")
                }
            } else {
                // Treat it as a local file path
                val fileFromUri = createFileFromContentUri(context, Uri.parse(imageUrl))
                if (fileFromUri != null && fileFromUri.exists()) {
                    Log.d("ProcessImage", "Image file found from URL: ${fileFromUri.absolutePath}")
                    val imageRequestBody = fileFromUri.asRequestBody("image/*".toMediaTypeOrNull())
                    return MultipartBody.Part.createFormData(
                        "image",
                        fileFromUri.name,
                        imageRequestBody
                    )
                } else {
                    Log.e("ProcessImage", "File from URL path does not exist, creating new file.")
                    // In case the file does not exist, try to create it again
                    val fileFromUri = createFileFromContentUri(
                        context,
                        Uri.parse(imageUrl)
                    ) // Retry creating the file
                    if (fileFromUri != null && fileFromUri.exists()) {
                        val imageRequestBody =
                            fileFromUri.asRequestBody("image/*".toMediaTypeOrNull())
                        return MultipartBody.Part.createFormData(
                            "image",
                            fileFromUri.name,
                            imageRequestBody
                        )
                    } else {
                        Log.e("ProcessImage", "Failed to create the image file from fallback URL.")
                    }
                }
            }
        }

        Log.e("ProcessImage", "No valid image found. Returning null.")
        return null
    }

    fun createFileFromContentUri(context: Context, contentUri: Uri): File? {
        Log.d("createFileFromContentUri", "Input URI: $contentUri")
        return try {
            when (contentUri.scheme) {
                "content" -> {
                    // Handle content URIs (e.g., from content provider)
                    val fileName = getFileNameFromUri(context, contentUri)
                        ?: "temp_image_${System.currentTimeMillis()}"
                    val tempFile = File(context.cacheDir, fileName)

                    // Try opening input stream and copying to a file
                    context.contentResolver.openInputStream(contentUri)?.use { inputStream ->
                        FileOutputStream(tempFile).use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
                    Log.d("createFileFromContentUri", "File created at: ${tempFile.absolutePath}")
                    tempFile
                }

                "file" -> {
                    // Handle file URIs (local file paths)
                    val file = File(contentUri.path ?: return null)
                    if (file.exists()) {
                        Log.d("createFileFromContentUri", "File exists at: ${file.absolutePath}")
                        file
                    } else {
                        Log.e(
                            "createFileFromContentUri",
                            "File does not exist at: ${file.absolutePath}"
                        )
                        null
                    }
                }

                else -> {
                    // Handle plain file paths (no scheme)
                    val file = File(contentUri.toString())
                    if (file.exists()) {
                        Log.d("createFileFromContentUri", "File exists at: ${file.absolutePath}")
                        file
                    } else {
                        Log.e(
                            "createFileFromContentUri",
                            "File does not exist at: ${file.absolutePath}"
                        )
                        null
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("createFileFromContentUri", "Error processing URI: ${e.message}", e)
            null
        }
    }

    fun getFileNameFromUri(context: Context, uri: Uri): String? {
        return if (uri.scheme == "content") {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (cursor.moveToFirst() && nameIndex >= 0) cursor.getString(nameIndex) else null
            }
        } else {
            uri.path?.let { File(it).name }
        }
    }

    fun getImageUriFromPreferences(context: Context): Uri? {
        val sharedPreferences = context.getSharedPreferences("appPrefs", Context.MODE_PRIVATE)
        val imageUrl = sharedPreferences.getString("imageUrll", null)
        Log.d("Preferences", "Retrieved image URL from SharedPreferences: $imageUrl")
        return imageUrl?.let { Uri.parse(it) }
    }

    suspend fun convertUrlToFile(context: Context, imageUrl: String): File? {
        return withContext(Dispatchers.IO) {
            try {
                val fileName = imageUrl.substringAfterLast("/")
                val file = File(context.cacheDir, fileName)

                Log.d("convertUrlToFile", "Attempting to download: $imageUrl")

                if (file.exists()) {
                    Log.d("convertUrlToFile", "File already exists: ${file.absolutePath}")
                    return@withContext file
                }

                val url = URL(imageUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.connect()

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    connection.inputStream.use { input ->
                        FileOutputStream(file).use { output ->
                            input.copyTo(output)
                        }
                    }
                    Log.d("convertUrlToFile", "File downloaded and saved: ${file.absolutePath}")
                    return@withContext file
                } else {
                    Log.e(
                        "convertUrlToFile",
                        "Failed to download file. Response code: ${connection.responseCode}"
                    )
                }
            } catch (e: Exception) {
                Log.e("convertUrlToFile", "Error downloading file: ${e.message}", e)
            }
            return@withContext null
        }
    }

    private fun validations(): Boolean {
        val email = binding.edtEmail.text
        val password = binding.edtPassword.text
        val birthday = binding.edtBirthday.text
        val birthplace = binding.edtBirthdayPlace.text
        val zipcode = binding.edtZipCode.text
        val athlete = binding.athletesEdt.text
        val below = binding.belowEdt.text
        val baseline = binding.baselineEdt.text
        val fatMass = binding.fatMassEdt.text

        when {
            email.isNullOrEmpty() -> {
                Toast.makeText(requireContext(), "Please Enter Email", Toast.LENGTH_SHORT).show()
                return false
            }

            password.isNullOrEmpty() -> {
                Toast.makeText(requireContext(), "Please Enter Password", Toast.LENGTH_SHORT).show()
                return false
            }

            birthday.isNullOrEmpty() -> {
                Toast.makeText(requireContext(), "Please Enter Birthday", Toast.LENGTH_SHORT).show()
                return false
            }

            birthplace.isNullOrEmpty() -> {
                Toast.makeText(requireContext(), "Please Enter Birthplace", Toast.LENGTH_SHORT)
                    .show()
                return false
            }

            zipcode.isNullOrEmpty() -> {
                Toast.makeText(requireContext(), "Please Enter Zipcode", Toast.LENGTH_SHORT).show()
                return false
            }

            athlete.isNullOrEmpty() -> {
                Toast.makeText(requireContext(), "Please Enter Athlete", Toast.LENGTH_SHORT).show()
                return false
            }

            below.isNullOrEmpty() -> {
                Toast.makeText(requireContext(), "Please Enter Below", Toast.LENGTH_SHORT).show()
                return false
            }

            baseline.isNullOrEmpty() -> {
                Toast.makeText(requireContext(), "Please Enter Baseline", Toast.LENGTH_SHORT).show()
                return false
            }

            fatMass.isNullOrEmpty() -> {
                Toast.makeText(requireContext(), "Please Enter Fatmass", Toast.LENGTH_SHORT).show()
                return false
            }

            else -> {
                return true
            }
        }
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
