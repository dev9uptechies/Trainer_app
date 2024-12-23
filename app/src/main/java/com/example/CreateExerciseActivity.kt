package com.example

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.MediaController
import android.widget.PopupWindow
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.model.SelectedValue
import com.example.model.newClass.excercise.Exercise
import com.example.model.newClass.timer.Timer
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.CategoriesData
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.R
import com.example.trainerapp.TestListData
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.ActivityCreateExerciseBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.JsonArray
import com.hbisoft.pickit.PickiT
import com.hbisoft.pickit.PickiTCallbacks
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.util.regex.Pattern


class CreateExerciseActivity : AppCompatActivity(), OnItemClickListener.OnItemClickCallback,
    PickiTCallbacks {
    lateinit var createExerciseBinding: ActivityCreateExerciseBinding
    lateinit var apiInterface: APIInterface
    var section = ArrayList<String>()
    var Goal = ArrayList<String>()
    var category = ArrayList<String>()
    var type = ArrayList<String>()
    var timer = ArrayList<String>()
    lateinit var goalData: MutableList<TestListData.testData>
    lateinit var categoryData: MutableList<TestListData.testData>
    lateinit var sectionData: MutableList<TestListData.testData>
    lateinit var timerData: MutableList<Timer.TimerData>
    lateinit var adapter: EquipmentAdapter
    private var fileNameDl = ""
    var pickiT: PickiT? = null
    lateinit var apiClient: APIClient
    private var file: File? = null
    private var videolink: String? = ""
    private var image_file: File? = null
    private lateinit var id: ArrayList<Int>
    lateinit var Progress_bar: ProgressBar
    lateinit var mediaControls: MediaController
    var goalId = SelectedValue(null)
    var sectionId = SelectedValue(null)
    var categoryId = SelectedValue(null)
    var timerId = SelectedValue(null)
    var requestCode = 0
    var requestFile: RequestBody? = null
    var video: MultipartBody.Part? = null
    var image: MultipartBody.Part? = null
    var videoLinkPart: MultipartBody.Part? = null
    var youtube = false
    private fun getTypeData() {
        type.add("General")
        type.add("Specific")
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
                        loadData()
                        Log.d("Get Profile Data ", "${response.body()}")
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@CreateExerciseActivity)
                    } else {
                        Toast.makeText(
                            this@CreateExerciseActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Toast.makeText(
                        this@CreateExerciseActivity,
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

    private fun getTimerData() {
        try {
        timerData = mutableListOf()
        apiInterface.GetTimerData().enqueue(
            object : Callback<Timer> {
                override fun onResponse(call: Call<Timer>, response: Response<Timer>) {
                    val code = response.code()
                    if (code == 200) {
                        if (response.isSuccessful) {
                            if (response.body() != null) {
                                val data = response.body()!!.data
                                if (!data.isNullOrEmpty()) { // Safe check for null and empty
                                    timerData.addAll(data.toMutableList())
                                    for (i in timerData) {
                                        timer.add(i.name ?: "") // Handle potential nulls in `name`
                                    }
                                }
                            } else {
                                Toast.makeText(
                                    this@CreateExerciseActivity,
                                    "Response body is null",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@CreateExerciseActivity)
                    } else {
                        Toast.makeText(
                            this@CreateExerciseActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        ).show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<Timer>, t: Throwable) {
                    Log.d("TAG Category", t.message.toString() + "")
                }
            }
        )
        }catch (e:Exception){
            Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
        }

    }

    private fun getCategoryData() {
        try {

        categoryData = mutableListOf()
        apiInterface.GetCategoriesData()?.enqueue(object : Callback<TestListData> {
            override fun onResponse(call: Call<TestListData>, response: Response<TestListData>) {
                val code = response.code()
                if (code == 200) {
                    if (response.isSuccessful) {
                        val data = response.body()!!.data
                        if (data!!.isNotEmpty()) {
                            categoryData.addAll(data.toMutableList())
                            for (i in categoryData) {
                                category.add(i.name!!)
                            }
                        }
                    }
                } else if (code == 403) {
                    Utils.setUnAuthDialog(this@CreateExerciseActivity)
                } else {
                    Toast.makeText(
                        this@CreateExerciseActivity,
                        "" + response.message(),
                        Toast.LENGTH_SHORT
                    ).show()
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<TestListData>, t: Throwable) {
                Log.d("TAG Category", t.message.toString() + "")
            }

        })
        }catch (e:Exception){
            Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
        }

    }

    private fun getGoalData() {
        try {
        goalData = mutableListOf()
        apiInterface.GetGoal()?.enqueue(object : Callback<TestListData> {
            override fun onResponse(call: Call<TestListData>, response: Response<TestListData>) {
                val code = response.code()
                if (code == 200) {
                    if (response.isSuccessful) {
                        val data = response.body()!!.data
                        if (data!!.isNotEmpty()) {
                            goalData.addAll(data.toMutableList())
                            for (i in goalData) {
                                Goal.add(i.name!!)
                            }
                        }
                    }
                } else if (code == 403) {
                    Utils.setUnAuthDialog(this@CreateExerciseActivity)
                } else {
                    Toast.makeText(
                        this@CreateExerciseActivity,
                        "" + response.message(), Toast.LENGTH_SHORT
                    ).show()
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<TestListData>, t: Throwable) {
                Log.d("TAG Goal", t.message.toString() + "")
            }

        })
        }catch (e:Exception){
            Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
        }

    }

    fun bitmapToFile(bitmap: Bitmap, context: Context): File {
        val file = File(context.cacheDir, "thumbnail.jpg")
        val fos = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        fos.flush()
        fos.close()
        return file
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createExerciseBinding = ActivityCreateExerciseBinding.inflate(layoutInflater)
        setContentView(createExerciseBinding.root)
        id = ArrayList()
        Progress_bar = findViewById(R.id.Progress_bar)
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)
        createExerciseBinding.selectUploadLy.visibility = View.VISIBLE
        createExerciseBinding.imageUpload.visibility = View.GONE
//        lifecycle.addObserver(createExerciseBinding.youtubePlayerView)

        //createExerciseBinding.youtubePlayerView.initialize()

        loadData()

//        createExerciseBinding.videoUpload.setOnClickListener {
//            createExerciseBinding.videoUpload.start()
//        }

        createExerciseBinding.edtSection.setOnClickListener {
            showPopup(it, sectionData, createExerciseBinding.edtSection, section, sectionId)
            updateUI(createExerciseBinding.nextCard)
        }

        createExerciseBinding.edtGoal.setOnClickListener {
            showPopup(it, goalData, createExerciseBinding.edtGoal, Goal, goalId)
            updateUI(createExerciseBinding.nextCard)
        }

        createExerciseBinding.edtCategory.setOnClickListener {
            showPopup(it, categoryData, createExerciseBinding.edtCategory, category, categoryId)
            updateUI(createExerciseBinding.nextCard)
        }

        createExerciseBinding.edtType.setOnClickListener {
            showTypePopup(it)
            updateUI(createExerciseBinding.nextCard)
        }

        createExerciseBinding.edtTimer.setOnClickListener {
            showTimerPopup(it, timerData, createExerciseBinding.edtTimer, timer, timerId)
            updateUI(createExerciseBinding.nextCard)
        }

        createExerciseBinding.selectUploadLy.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(this)
            bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog)
            val videoLink = bottomSheetDialog.findViewById<LinearLayout>(R.id.gallery)
            val video = bottomSheetDialog.findViewById<LinearLayout>(R.id.video)
            bottomSheetDialog.show()

            videoLink!!.setOnClickListener {
                bottomSheetDialog.dismiss()
                videoDialog()
            }
            video!!.setOnClickListener {
                bottomSheetDialog.dismiss()
                if (videolink!!.isNotEmpty()) {
                    videolink = ""
                }
                requestCode = 1
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(
                    Intent.createChooser(
                        intent,
                        "Choose Video"
                    ), 1
                )
            }

        }

        createExerciseBinding.back.setOnClickListener {
            finish()
        }

        pickiT = PickiT(this, this, this)
        createExerciseBinding.nextCard.setOnClickListener {
            if (areAllFieldsFilled()) {
                Log.d(
                    "TAG",
                    "onCreate: " + "equipments :- ${id} \n Goal :- $goalId \n category :- $categoryId \n timer :- $timerId \n section :- $sectionId \n name :- ${createExerciseBinding.edtName} \n notes :- ${createExerciseBinding.edtNotes} \n video :- ${videolink} \n imageFile :- $image_file\n"
                )
                if (isValidate) {
                    Progress_bar.visibility = View.VISIBLE
                    val str = arrayOfNulls<Int>(id.size)
                    val array = JsonArray()

                    for (i in 0 until id.size) {
                        str[i] = id.get(i)
                        array.add(id.get(i))
                    }
                    val nameString = createExerciseBinding.edtName.text.toString().trim()
                    val sectionString = sectionId.id.toString()
                    val goalString = goalId.id.toString()
                    val typeString = createExerciseBinding.edtType.text.toString().trim()
                    val categoryString = categoryId.id.toString()
                    val timerString = timerId.id.toString()
                    val noteString = createExerciseBinding.edtNotes.text.toString().trim()
                    // val videoString = videolink!!.trim()
                    val equipmentIds: MultipartBody.Part =
                        MultipartBody.Part.createFormData("equipment_ids", array.toString())
                    if (requestCode != 0) {
                        createExerciseBinding.imageError.visibility = View.GONE
                        if (requestCode == 1) {
                            //video
                            requestFile = file!!.asRequestBody("video/*".toMediaTypeOrNull())
                            video =
                                MultipartBody.Part.createFormData("video", "video", requestFile!!)
                            image =
                                MultipartBody.Part.createFormData("image", "image", requestFile!!)
                        } else {
                            videoLinkPart =
                                MultipartBody.Part.createFormData("video_link", videolink!!)
                            requestFile = image_file!!.asRequestBody("image/*".toMediaTypeOrNull())
                            image =
                                MultipartBody.Part.createFormData("image", "image", requestFile!!)
                        }
                    }

                    apiInterface.CreateExercise1(
                        name = nameString,
                        sectionId = sectionString,
                        timerId = timerString,
                        goalId = goalString,
                        type = typeString,
                        categoryId = categoryString,
                        notes = noteString,
                        equipment_ids = equipmentIds,
                        video = video,
                        videoLink = videoLinkPart,
                        thumbImage = image
                    ).enqueue(object : Callback<Exercise> {
                        override fun onResponse(
                            call: Call<Exercise?>,
                            response: Response<Exercise?>
                        ) {
                            val code = response.code()
                            if (code == 200) {
                                Log.d("TAG Response", response.message() + "")
                                Log.d("TAG Response", response.body()!!.message + "")
                                if (response.isSuccessful) {
                                    Progress_bar.visibility = View.GONE
                                    Toast.makeText(
                                        this@CreateExerciseActivity,
                                        "" + response.body()!!.message.toString(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    finish()
                                } else {
                                    val message = response.errorBody()?.string()
                                    if (message != null) {
                                        Toast.makeText(
                                            this@CreateExerciseActivity,
                                            "" + message,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            this@CreateExerciseActivity,
                                            "" + response.message(),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    //finish()
                                }
                            } else if (code == 403) {
                                Utils.setUnAuthDialog(this@CreateExerciseActivity)
                            } else {
                                Toast.makeText(
                                    this@CreateExerciseActivity,
                                    "" + response.message(),
                                    Toast.LENGTH_SHORT
                                ).show()
                                call.cancel()
                            }
                        }

                        override fun onFailure(call: Call<Exercise?>, t: Throwable) {
                            Progress_bar.visibility = View.GONE
                            Toast.makeText(
                                this@CreateExerciseActivity,
                                "" + t.message,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            call.cancel()
                        }
                    })
                }else{
                    createExerciseBinding.ProgressBar.visibility = View.GONE
                    Toast.makeText(this, "fill all fild", Toast.LENGTH_SHORT).show()
                }
            }
        }

        createExerciseBinding.edtName.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    updateUI(createExerciseBinding.nextCard)
                }

                override fun afterTextChanged(s: Editable?) {}
            }
        )

        createExerciseBinding.edtNotes.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    updateUI(createExerciseBinding.nextCard)
                }

                override fun afterTextChanged(s: Editable?) {}
            }
        )
    }

    private fun loadData(){
        getGoalData()
        getTimerData()
        getCategoryData()
        getTypeData()
        getSection()
        GetEquipment()
    }

    private fun areAllFieldsFilled(): Boolean {
        return !(createExerciseBinding.edtName.text.isNullOrEmpty() || createExerciseBinding.edtNotes.text.isNullOrEmpty() ||
                createExerciseBinding.edtSection.text.toString() == "Select Section" ||
                createExerciseBinding.edtGoal.text.toString() == "Select Goal" ||
                createExerciseBinding.edtType.text.toString() == "Select Type" ||
                createExerciseBinding.edtCategory.text.toString() == "Select Category" ||
                createExerciseBinding.edtTimer.text.toString() == "Select Timer")
    }

    private fun updateUI(addButton: CardView) {
        if (areAllFieldsFilled()) {
            addButton.isEnabled = true
            addButton.setCardBackgroundColor(resources.getColor(R.color.splash_text_color)) // Change to your desired color
        } else {
            addButton.isEnabled = false
            addButton.setCardBackgroundColor(resources.getColor(R.color.grey)) // Disabled color
        }
    }

    private fun showTimerPopup(
        anchorView: View?,
        data: MutableList<Timer.TimerData>,
        editText: EditText,
        list: ArrayList<String>,
        selectedValue: SelectedValue
    ) {
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
                this,
                R.drawable.popup_background
            )
        )
        popupWindow.elevation = 10f
        val listView = popupView.findViewById<ListView>(R.id.listView)

        val adapter =
            object : ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list) {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val view = super.getView(position, convertView, parent) as TextView
                    view.setTextColor(Color.WHITE) // Set text color to white
                    return view
                }
            }
        listView.adapter = adapter
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = list[position]
            editText.setText(selectedItem)
            selectedValue.id = data.filter { it.name == selectedItem }.first().id!!
            println("Selected item: $selectedItem")
            popupWindow.dismiss()
        }
        popupWindow.showAsDropDown(anchorView)
        popupWindow.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                this,
                android.R.color.white
            )
        )
    }

    private fun showPopup(
        anchorView: View?,
        data: MutableList<TestListData.testData>,
        editText: EditText,
        list: ArrayList<String>,
        selectedValue: SelectedValue
    ) {

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
                this,
                R.drawable.popup_background
            )
        )
        popupWindow.elevation = 10f
        val listView = popupView.findViewById<ListView>(R.id.listView)

        val adapter =
            object : ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list) {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val view = super.getView(position, convertView, parent) as TextView
                    view.setTextColor(Color.WHITE) // Set text color to white
                    return view
                }
            }
        listView.adapter = adapter
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = list[position]
            editText.setText(selectedItem)
            selectedValue.id = data.filter { it.name == selectedItem }.first().id!!
            println("Selected item: $selectedItem")
            popupWindow.dismiss()
        }
        popupWindow.showAsDropDown(anchorView)
        popupWindow.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                this,
                android.R.color.white
            )
        )

    }

    private fun showTypePopup(anchorView: View?) {
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
                this,
                R.drawable.popup_background
            )
        )
        popupWindow.elevation = 10f
        val listView = popupView.findViewById<ListView>(R.id.listView)
        val adapter =
            object : ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, type) {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val view = super.getView(position, convertView, parent) as TextView
                    view.setTextColor(Color.WHITE) // Set text color to white
                    return view
                }
            }
        listView.adapter = adapter
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = type[position]
            createExerciseBinding.edtType.setText(selectedItem)
            println("Selected item: $selectedItem")
            popupWindow.dismiss()
        }
        popupWindow.showAsDropDown(anchorView)
        popupWindow.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                this,
                android.R.color.white
            )
        )
    }

    private fun videoDialog() {
        val dialog = Dialog(this, R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(R.layout.video_link_dialog)
        val close = dialog.findViewById<ImageView>(R.id.close)
        val done_card = dialog.findViewById<CardView>(R.id.done_card)
        val edt_link = dialog.findViewById<EditText>(R.id.edt_link)
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = (displayMetrics.widthPixels * 0.9f).toInt()
        val height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.setLayout(width, height)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        done_card.setOnClickListener {
            dialog.dismiss()
            previewVideo(edt_link.text.toString().trim())
        }
        close.setOnClickListener {
            dialog.dismiss()
        }
    }

    //https://www.youtube.com/1yszd636ckI?si=dweQF-GsxU_LKEJu
    private fun previewVideo(videoString: String) {
        if (isYouTubeUrl(videoString)) {
            youtube = true
            videolink = convertToYouTubeEmbedUrl(videoString)
            Log.d("Video Link", videolink.toString())
//            Log.d("Invalid Video Id", "Video Link:- $videolink")
//            loadYouTubeThumbnail(videolink!!, createExerciseBinding.imageUpload)
            playYouTubeVideo(videoString)
        } else {
            youtube = false
            videolink = videoString
            Log.d("Video Link", videolink.toString())
            playNormalVideo(videoString)
        }
    }

    private fun playYouTubeVideo(videoString: String) {
        createExerciseBinding.selectUploadLy.visibility = View.GONE
        createExerciseBinding.imageUpload.visibility = View.GONE
        createExerciseBinding.videoUpload.visibility = View.GONE
        createExerciseBinding.webView.visibility = View.VISIBLE
        val unencodedHtml = """
            <html>
            <body>
            <iframe width="100%" height="100%" src="${convertToYouTubeEmbedUrl(videoString)}" frameborder="0"></iframe>
            </body>
            </html>
        """.trimIndent()
        createExerciseBinding.webView.settings.javaScriptEnabled = true
        createExerciseBinding.webView.settings.loadWithOverviewMode = true
        val predefinedDrawable = ContextCompat.getDrawable(this, R.drawable.ic_youtube)
        val bitmap = (predefinedDrawable as BitmapDrawable).bitmap
        image_file = bitmapToFile(bitmap, this)
        createExerciseBinding.imageUpload.setImageBitmap(bitmap)
//        videoString.let {
//            loadYouTubeThumbnail(
//                it,
//                createExerciseBinding.imageUpload
//            )
//        }
//        createExerciseBinding.webView.settings.useWideViewPort = true
        createExerciseBinding.webView.loadData(unencodedHtml, "text/html", "utf-8")
    }

    fun downloadThumbnailAsBitmap(thumbnailUrl: String): Bitmap? {
//        return try {
//            val url = URL(thumbnailUrl)
//            val connection = url.openConnection() as HttpURLConnection
//            connection.doInput = true
//            connection.connect()
//            val input = connection.inputStream
//            BitmapFactory.decodeStream(input)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            null
//        }

        try {
            val mediaMetadataRetriever = MediaMetadataRetriever()
            mediaMetadataRetriever.setDataSource(this, Uri.parse(thumbnailUrl))
            return mediaMetadataRetriever.frameAtTime
        } catch (ex: Exception) {
            Log.d("Invalid Video :-", ex.toString())
            Toast
                .makeText(this, "Error retrieving bitmap", Toast.LENGTH_SHORT)
                .show()
        }
        return null

//        Log.d("Invalid Video :-", thumbnailUrl)
//        var bitmap: Bitmap? = null
//        try {
//            val url = URL(thumbnailUrl)
//            val connection = url.openConnection() as HttpURLConnection
//            connection.doInput = true
//            connection.connect()
//
//            // Check for HTTP response code
//            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
//                val input = connection.inputStream
//                bitmap = BitmapFactory.decodeStream(input)
//                input.close()
//            } else {
//                Log.e(
//                    "DownloadThumbnail",
//                    "Failed to fetch image, HTTP response code: ${connection.responseCode}"
//                )
//            }
//        } catch (e: Exception) {
//            Log.e("DownloadThumbnail", "Exception while downloading thumbnail: $e")
//            e.printStackTrace()
//        }
//        return bitmap
    }

    fun extractYouTubeVideoId(url: String): String? {
        val regex =
            "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|shorts\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%2F|youtu.be%2F|\\/v%2F)[^#\\&\\?\\n]*"
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(url)
        return if (matcher.find()) matcher.group() else null
    }

    fun convertToYouTubeEmbedUrl(originalUrl: String): String? {
        val videoId = extractYouTubeVideoId(originalUrl)
        return if (videoId != null) {
//            val thumbnailUrl = "https://img.youtube.com/vi/$videoId/hqdefault.jpg"
//            try {
//                image_file = File(URI.create(thumbnailUrl))
//                Log.d("Invalid Video :-", "$image_file")
//            } catch (e: Exception) {
//                Log.d("Invalid Video :-", "$e \t ${e.message}")
//            }
            //val bitmap = downloadThumbnailAsBitmap(thumbnailUrl)
            "https://www.youtube.com/embed/$videoId"
        } else {
            null
        }
    }

    private fun playNormalVideo(videoString: String) {

        createExerciseBinding.selectUploadLy.visibility = View.GONE
        createExerciseBinding.imageUpload.visibility = View.GONE
        createExerciseBinding.videoUpload.stopPlayback() // Stop any previous playback
        createExerciseBinding.videoUpload.setVideoURI(null) // Clear the previous video URI
        createExerciseBinding.videoUpload.setVideoURI(Uri.parse(videoString)) // Set new video URI

        // Request layout update and redraw
        createExerciseBinding.videoUpload.requestLayout()
        createExerciseBinding.videoUpload.invalidate()

        mediaControls = MediaController(this)
        mediaControls.setAnchorView(createExerciseBinding.videoUpload)
        createExerciseBinding.videoUpload.setMediaController(mediaControls)
        createExerciseBinding.videoUpload.requestFocus()

        createExerciseBinding.videoUpload.setOnPreparedListener {
            it.isLooping = false
            if (videoString.contains("https") || videoString.contains("http") || videoString.contains(
                    ".com"
                )
            ) {
                Log.d("internet Video:-", "")
                loadThumbnailFromUrl(this, videoString, createExerciseBinding.imageUpload)
            } else {
                val thumbnail = getThumbnailFromContentUri(this, Uri.parse(videoString))
                Log.d("Local Video:-", "$thumbnail")
                if (thumbnail.toString().contains("null")) {
                    //image_file = File()
                    val predefinedDrawable = ContextCompat.getDrawable(this, R.drawable.ic_youtube)
                    val bitmap = (predefinedDrawable as BitmapDrawable).bitmap
                    image_file = bitmapToFile(bitmap, this)
                    createExerciseBinding.imageUpload.setImageBitmap(bitmap)
                } else {
                    image_file = bitmapToFile(thumbnail!!, this)
                    createExerciseBinding.imageUpload.setImageBitmap(thumbnail)
                }

            }
            createExerciseBinding.imageUpload.visibility = View.VISIBLE

            it.setOnInfoListener { _, what, _ ->
                if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                    createExerciseBinding.imageUpload.visibility = View.GONE
                    true
                } else {
                    false
                }
            }

        }
        createExerciseBinding.videoUpload.visibility = View.VISIBLE
    }

    private fun isYouTubeUrl(url: String): Boolean {
        return url.contains("youtube.com") || url.contains("youtu.be")
    }

    fun getFilePathFromUri(context: Context, uri: Uri): String? {
        val projection = arrayOf(MediaStore.Video.Media.DATA)
        val cursor = context.contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
                return it.getString(columnIndex)
            }
        }
        return null
    }

    fun getThumbnailFromContentUri(context: Context, uri: Uri): Bitmap? {
        val filePath = getFilePathFromUri(context, uri)
        return filePath?.let {
            ThumbnailUtils.createVideoThumbnail(it, MediaStore.Video.Thumbnails.MINI_KIND)
        }
    }

    fun loadThumbnailFromUrl(context: Context, url: String, imageView: ImageView) {
        Glide.with(context)
            .asBitmap()
            .load(url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    image_file = bitmapToFile(resource, this@CreateExerciseActivity)
                    imageView.setImageBitmap(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // Handle case where image load is cleared
                }
            })
    }

    private fun GetEquipment() {
        Progress_bar.visibility = View.VISIBLE
        apiInterface.GetEquipment()?.enqueue(object : Callback<CategoriesData?> {
            override fun onResponse(
                call: Call<CategoriesData?>,
                response: Response<CategoriesData?>
            ) {
                Log.d("TAG", response.code().toString() + "")
                val code = response.code()
                if (code == 200) {
                    val resource: CategoriesData? = response.body()
                    val Success: Boolean = resource?.status!!
                    val Message: String = resource.message!!
                    val Data = resource.data!!
                    if (Data.isNotEmpty()) {
                        initRecycler(Data)
                    }
                } else if (code == 403) {
                    Utils.setUnAuthDialog(this@CreateExerciseActivity)
                } else {
                    Toast.makeText(
                        this@CreateExerciseActivity,
                        "" + response.message(),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<CategoriesData?>, t: Throwable) {
                Progress_bar.visibility = View.GONE
                Toast.makeText(this@CreateExerciseActivity, "" + t.message, Toast.LENGTH_SHORT)
                    .show()
                call.cancel()
            }
        })
    }

    private fun initRecycler(data: ArrayList<CategoriesData.Categoty>) {
        createExerciseBinding.equipmentRly.layoutManager = GridLayoutManager(this, 2)
        adapter =
            EquipmentAdapter(data, this, this)
        createExerciseBinding.equipmentRly.adapter = adapter

    }

    private fun getSection() {
        Progress_bar.visibility = View.VISIBLE
        sectionData = mutableListOf()
        apiInterface.GetSection1()?.enqueue(object : Callback<TestListData?> {
            override fun onResponse(
                call: Call<TestListData?>,
                response: Response<TestListData?>
            ) {
                Log.d("TAG", response.code().toString() + "")
                val code = response.code()
                if (code == 200) {
                    val resource = response.body()
                    val Success: Boolean = resource?.status!!
                    val Message: String = resource.message!!
                    val Data = resource.data!!
                    if (Success == true) {
                        //section.add("Select Section")
                        sectionData.addAll(Data)
                        for (i in 0 until Data.size) {
                            section.add(Data[i].name!!)
                        }
//                    setSpinner(age)
//                    Utils.setSpinnerAdapter(
//                        applicationContext,
//                        section,
//                        createExerciseBinding.spSection,
//                        "Select Section"
//                    )
                        Progress_bar.visibility = View.GONE
                    } else {
                        Progress_bar.visibility = View.GONE
                    }
                } else if (code == 403) {
                    Utils.setUnAuthDialog(this@CreateExerciseActivity)
                } else {
                    Toast.makeText(
                        this@CreateExerciseActivity,
                        "" + response.message(),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<TestListData?>, t: Throwable) {
                Progress_bar.visibility = View.GONE
                Toast.makeText(this@CreateExerciseActivity, "" + t.message, Toast.LENGTH_SHORT)
                    .show()
                call.cancel()
            }
        })
    }

    private fun setSpinner(age: ArrayList<String>) {
        val adapter1 = object : ArrayAdapter<String>(
            this, android.R.layout.simple_spinner_item, age
        ) {
            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view = super.getDropDownView(position, convertView, parent) as TextView
                view.setBackgroundColor(Color.BLACK)
                return view
            }
        }

        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        createExerciseBinding.spSection.adapter = adapter1

        createExerciseBinding.spSection.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem = parent.getItemAtPosition(position) as String
                    Log.d("Spinner1", "Selected: $selectedItem")
                    (view as? TextView)?.setTextColor(Color.WHITE)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                createExerciseBinding.selectUploadLy.visibility = View.GONE
                createExerciseBinding.imageUpload.visibility = View.GONE
                val fileNameDl = data!!.data!!
                val fileAbsPath = FileUtils.getRealPath(this, fileNameDl)
                file = File(fileAbsPath!!)
                try {
                    playNormalVideo(fileNameDl.toString())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
//                try {
//                    val thumb = ThumbnailUtils.createVideoThumbnail(
//                        fileAbsPath,
//                        MediaStore.Video.Thumbnails.MICRO_KIND
//                    )
//                    val bytes = ByteArrayOutputStream()
//                    thumb!!.compress(Bitmap.CompressFormat.JPEG, 50, bytes)
//                    val path: String = MediaStore.Images.Media.insertImage(
//                        this.contentResolver,
//                        thumb,
//                        "Title",
//                        null
//                    )
//                    pickiT!!.getPath(Uri.parse(path), Build.VERSION.SDK_INT)
//                    createExerciseBinding.imageUpload.setImageBitmap(thumb)
//                    //createExerciseBinding.imageUpload.visibility = View.GONE
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
            }
        }
//        else if (requestCode == 2) {
//            if (resultCode == RESULT_OK) {
//                createExerciseBinding.selectUploadLy.visibility = View.GONE
//                createExerciseBinding.imageUpload.visibility = View.VISIBLE
//                val fileNameDl = data!!.data!!
//                Log.d("File Name ", "onActivityResult: $fileNameDl")
//                try {
//                    createExerciseBinding.imageUpload.setImageURI(fileNameDl)
//                    image_file = FileUtils.getRealPath(this, fileNameDl)?.let { File(it) }
//                    this.fileNameDl = fileNameDl.toString()
//                    //createExerciseBinding.imageUpload.visibility = View.GONE
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//                val fileAbsPath = FileUtils.getRealPath(this, fileNameDl)
//                file = File(fileAbsPath!!)
//                try {
//                    val thumb = ThumbnailUtils.createVideoThumbnail(
//                        fileAbsPath,
//                        MediaStore.Images.Thumbnails.MICRO_KIND
//                    )
//                    val bytes = ByteArrayOutputStream()
//                    thumb!!.compress(Bitmap.CompressFormat.JPEG, 50, bytes)
//                    val path: String = MediaStore.Images.Media.insertImage(
//                        this.contentResolver,
//                        thumb,
//                        "Title",
//                        null
//                    )
//                    pickiT!!.getPath(Uri.parse(path), Build.VERSION.SDK_INT)
//                    createExerciseBinding.imageUpload.setImageBitmap(thumb)
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }
//        }
    }

    override fun onItemClicked(view: View, position: Int, type: Long, string: String) {

        val image = view.findViewById<ImageView>(R.id.equipment_image)
        val text = view.findViewById<TextView>(R.id.name_equipment)
        image.setColorFilter(getResources().getColor(R.color.white))
        text.setTextColor(getResources().getColor(R.color.white))

        if (id.contains(type.toInt())) {
            // ID is already in the list, so remove it and reset the view
            id.remove(type.toInt())
            image.clearColorFilter()
            text.setTextColor(getResources().getColor(R.color.black)) // Replace with your default color
        } else {
            // ID is not in the list, so add it and change the view's appearance
            id.add(type.toInt())
            image.setColorFilter(getResources().getColor(R.color.white))
            text.setTextColor(getResources().getColor(R.color.white))
        }
//
//        if (id.size != 0) {
//            if (!contains(id, type.toInt())) {
//                id.add(type.toInt())
//            }
//        } else {
//            id.add(type.toInt())
//        }

    }

    fun contains(list: ArrayList<Int>, name: Int): Boolean {
        for (item in list) {
            if (item.equals(name)) {
                return true
            }
        }
        return false
    }

    private val isValidate: Boolean
        get() {
            val name = createExerciseBinding.edtName.text.toString()
            val notes = createExerciseBinding.edtNotes.text.toString()

            if (fileNameDl == "") {
                createExerciseBinding.imageError.visibility = View.GONE
                createExerciseBinding.imageError.text = "Please Select Image"
            } else {
                createExerciseBinding.imageError.visibility = View.GONE
            }

            if (file == null) {
                createExerciseBinding.imageError.visibility = View.GONE
                createExerciseBinding.imageError.text = "Please Upload Video"
            } else {
                createExerciseBinding.imageError.visibility = View.GONE
            }

            if (videolink.isNullOrEmpty()) {
                createExerciseBinding.imageError.visibility = View.GONE
                createExerciseBinding.imageError.text = "Please Upload Video"
            } else {
                createExerciseBinding.imageError.visibility = View.GONE

            }

            if (name == "") {
                createExerciseBinding.nameError.visibility = View.VISIBLE
                createExerciseBinding.nameError.text = "Please Name"
                return false
            } else {
                createExerciseBinding.nameError.visibility = View.GONE
            }

            if (createExerciseBinding.spGoal.selectedItem == "Enter Goal") {
                createExerciseBinding.goalError.visibility = View.VISIBLE
                createExerciseBinding.goalError.text = "Please Select Goal"

            } else {
                createExerciseBinding.goalError.visibility = View.GONE
            }

            if (notes == "") {
                createExerciseBinding.notesError.visibility = View.VISIBLE
                createExerciseBinding.notesError.text = "Please Enter Notes"
                return false
            } else {
                createExerciseBinding.notesError.visibility = View.GONE
            }

            return true
        }

    override fun PickiTonUriReturned() {

    }

    override fun PickiTonStartListener() {

    }

    override fun PickiTonProgressUpdate(progress: Int) {

    }

    override fun PickiTonCompleteListener(
        path: String?,
        wasDriveFile: Boolean,
        wasUnknownProvider: Boolean,
        wasSuccessful: Boolean,
        Reason: String?
    ) {
        image_file = File(path!!)
    }

//    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//        if (parent != null) {
//            Log.d(
//                "TAG",
//                "onItemSelected: " + parent.getItemAtPosition(position)
//            )
//            (parent.getChildAt(position) as TextView).setTextColor(Color.WHITE)
//        }
//    }
//
//    override fun onNothingSelected(parent: AdapterView<*>?) {
//        Log.d("TAG", "onNothingSelected: ")
//    }
}