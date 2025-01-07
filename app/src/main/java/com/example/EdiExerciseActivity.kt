package com.example

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
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
import androidx.core.content.res.ResourcesCompat
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
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.regex.Pattern

class EdiExerciseActivity : AppCompatActivity(), PickiTCallbacks,
    OnItemClickListener.OnItemClickCallback {
    lateinit var editExerciseBinding: ActivityCreateExerciseBinding
    lateinit var apiInterface: APIInterface
    var position: Int? = null
    var exercise_id: String? = null
    var age = ArrayList<String>()
    var youtube = false
    lateinit var mediaControls: MediaController
    var video: MultipartBody.Part? = null
    lateinit var adapter: EquipmentAdapter
    private var gif_file: File? = null
    private var image_file: File? = null
    private var fileNameDl = ""
    var pickiT: PickiT? = null
    lateinit var apiClient: APIClient
    private var file: File? = null
    private lateinit var id: ArrayList<Int>
    lateinit var Progress_bar: ProgressBar

    var section = ArrayList<String>()
    var Goal = ArrayList<String>()
    var category = ArrayList<String>()
    var type = ArrayList<String>()
    var timer = ArrayList<String>()

    var requestCode = 0
    var requestFile: RequestBody? = null

    lateinit var generallist: MutableList<Exercise.ExerciseData>
    lateinit var specificlist: MutableList<Exercise.ExerciseData>
    lateinit var exerciselist: MutableList<Exercise.ExerciseData>
    lateinit var Data: ArrayList<CategoriesData.Categoty>

    //ids
    var goalId = SelectedValue(null)
    var sectionId = SelectedValue(null)
    var categoryId = SelectedValue(null)
    var timerId = SelectedValue(null)

    //data's
    lateinit var goalData: MutableList<TestListData.testData>
    lateinit var categoryData: MutableList<TestListData.testData>
    lateinit var sectionData: MutableList<TestListData.testData>
    lateinit var timerData: MutableList<Timer.TimerData>
    private var videolink: String? = ""
    var videoLinkPart: MultipartBody.Part? = null
    var image: MultipartBody.Part? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        editExerciseBinding = ActivityCreateExerciseBinding.inflate(layoutInflater)
        setContentView(editExerciseBinding.root)
        initViews()
        GetEquipment()
        getExercise()
        getGoalData()
        getTimerData()
        getCategoryData()
        getTypeData()
        getSection()

        operations()
    }

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
                        Utils.setUnAuthDialog(this@EdiExerciseActivity)
                    } else {
                        Toast.makeText(
                            this@EdiExerciseActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Toast.makeText(
                        this@EdiExerciseActivity,
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

    private fun getExercise() {
        Progress_bar.visibility = View.VISIBLE
        try {
            apiInterface.GetExerciseData().enqueue(
                object : Callback<Exercise> {
                    override fun onResponse(call: Call<Exercise>, response: Response<Exercise>) {
                        Progress_bar.visibility = View.GONE

                        Log.d("Response Body", response.body().toString())
                        Log.d("Response Code", response.code().toString())

                        val code = response.code()
                        if (code == 200 && response.isSuccessful) {
                            val data = response.body()?.data?.filter { it.id == exercise_id?.toInt() }

                            if (!data.isNullOrEmpty()) {
                                val exercise = data[0]

                                // Populate Exercise Data
                                editExerciseBinding.edtName.setText(exercise.name ?: "")
                                editExerciseBinding.edtType.setText(exercise.type ?: "")
                                editExerciseBinding.edtNotes.setText(exercise.notes ?: "")

                                // Goal
                                if (exercise.goal_id != null && exercise.goal != null) {
                                    editExerciseBinding.edtGoal.setText(exercise.goal.goal_name ?: "")
                                    goalId = SelectedValue(exercise.goal_id.toInt())
                                } else {
                                    editExerciseBinding.edtGoal.text.clear()
                                    goalId = SelectedValue(0)
                                }

                                // Section
                                if (exercise.section_id != null && exercise.section != null) {
                                    editExerciseBinding.edtSection.setText(exercise.section.section_name ?: "")
                                    sectionId = SelectedValue(exercise.section_id.toInt())
                                } else {
                                    editExerciseBinding.edtSection.text.clear()
                                    sectionId = SelectedValue(0)
                                }

                                // Category
                                if (exercise.category_id != null && exercise.category != null) {
                                    editExerciseBinding.edtCategory.setText(exercise.category.category_name ?: "")
                                    categoryId = SelectedValue(exercise.category_id.toInt())
                                } else {
                                    editExerciseBinding.edtCategory.text.clear()
                                    categoryId = SelectedValue(0)
                                }

                                // Timer
                                if (exercise.timer_id != null && exercise.timer_name != null) {
                                    editExerciseBinding.edtTimer.setText(exercise.timer_name.timer_name_name ?: "")
                                    timerId = SelectedValue(exercise.timer_id.toInt())
                                } else {
                                    editExerciseBinding.edtTimer.text.clear()
                                    timerId = SelectedValue(0)
                                }

                                checkPredefineValue(exercise.exercise_equipments)

                                when {
                                    !exercise.video.isNullOrEmpty() -> {
                                        playNormalVideo("https://trainers.codefriend.in${exercise.video}")
                                    }
                                    !exercise.video_link.isNullOrEmpty() -> {
                                        if (isYouTubeUrl(exercise.video_link)) {
                                            playYouTubeVideo(exercise.video_link)
                                        } else {
                                            playNormalVideo(exercise.video_link)
                                        }
                                    }
                                    else -> {
                                        editExerciseBinding.videoUpload.visibility = View.GONE
                                        editExerciseBinding.selectUploadLy.visibility = View.VISIBLE
                                    }
                                }
                            } else {
                                Log.d("Response", "No matching data found.")
                                Toast.makeText(
                                    this@EdiExerciseActivity,
                                    "No exercise data found.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else if (code == 403) {
                            Utils.setUnAuthDialog(this@EdiExerciseActivity)
                        } else {
                            Toast.makeText(
                                this@EdiExerciseActivity,
                                "Error: ${response.message()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<Exercise>, t: Throwable) {
                        Progress_bar.visibility = View.GONE
                        Log.d("Response Error", t.message ?: "Unknown error")
                        Toast.makeText(
                            this@EdiExerciseActivity,
                            "Failed to fetch exercise data: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    private fun checkPredefineValue(exerciseEquipments: List<Exercise.ExerciseEquipment>?) {
                        if (!exerciseEquipments.isNullOrEmpty()) {
                            exerciseEquipments.forEach {
                                id.add(it.exercise_equipment_id?.toInt() ?: 0)
                            }
                            Log.d("Array List", id.toString())
                        }

                        editExerciseBinding.equipmentRly.layoutManager = GridLayoutManager(this@EdiExerciseActivity, 2)
                        adapter = EquipmentAdapter(
                            Data,
                            this@EdiExerciseActivity,
                            this@EdiExerciseActivity,
                            id
                        )
                        editExerciseBinding.equipmentRly.adapter = adapter
                    }
                }
            )
        } catch (e: Exception) {
            Progress_bar.visibility = View.GONE
            Log.d("Exception", e.message ?: "Unknown exception")
            Toast.makeText(
                this@EdiExerciseActivity,
                "An error occurred: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
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
            "https://www.youtube.com/embed/$videoId"
        } else {
            null
        }
    }

    private fun previewVideo(videoString: String) {
        if (isYouTubeUrl(videoString)) {
            youtube = true
            videolink = convertToYouTubeEmbedUrl(videoString)
            Log.d("Video Link", videolink.toString())
            playYouTubeNewVideo(videoString)
        } else {
            youtube = false
            videolink = videoString
            Log.d("Video Link", videolink.toString())
            playNormalNewVideo(videoString)
        }
    }

    private fun playNormalNewVideo(videoString: String) {
        editExerciseBinding.selectUploadLy.visibility = View.GONE
        editExerciseBinding.imageUpload.visibility = View.GONE
        editExerciseBinding.imageUpload.setImageURI(null)
        editExerciseBinding.videoUpload.stopPlayback() // Stop any previous playback
        editExerciseBinding.videoUpload.setVideoURI(null) // Clear the previous video URI
        editExerciseBinding.videoUpload.setVideoURI(Uri.parse(videoString)) // Set new video URI

        // Request layout update and redraw
        editExerciseBinding.videoUpload.requestLayout()
        editExerciseBinding.videoUpload.invalidate()

        mediaControls = MediaController(this)
        mediaControls.setAnchorView(editExerciseBinding.videoUpload)
        editExerciseBinding.videoUpload.setMediaController(mediaControls)
        editExerciseBinding.videoUpload.requestFocus()

        editExerciseBinding.videoUpload.setOnPreparedListener {
            it.isLooping = false
            if (videoString.contains("https") || videoString.contains("http") || videoString.contains(
                    ".com"
                )
            ) {
                Log.d("internet Video:-", "")
                loadThumbnailFromUrl(this, videoString, editExerciseBinding.imageUpload)
            } else {
                val thumbnail = getThumbnailFromContentUri(this, Uri.parse(videoString))
                Log.d("Local Video:-", "$thumbnail")
                if (thumbnail.toString().contains("null")) {
                    //image_file = File()
                    val predefinedDrawable = ContextCompat.getDrawable(this, R.drawable.ic_youtube)
                    val bitmap = (predefinedDrawable as BitmapDrawable).bitmap
                    image_file = bitmapToFile(bitmap, this)
                    editExerciseBinding.imageUpload.setImageBitmap(bitmap)
                } else {
                    image_file = bitmapToFile(thumbnail!!, this)
                    editExerciseBinding.imageUpload.setImageBitmap(thumbnail)
                }

            }
            editExerciseBinding.imageUpload.visibility = View.VISIBLE

            it.setOnInfoListener { _, what, _ ->
                if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                    editExerciseBinding.imageUpload.visibility = View.GONE
                    true
                } else {
                    false
                }
            }

        }
        editExerciseBinding.videoUpload.visibility = View.VISIBLE
    }

    private fun playYouTubeNewVideo(videoString: String) {
        editExerciseBinding.selectUploadLy.visibility = View.GONE
        editExerciseBinding.imageUpload.visibility = View.GONE
        editExerciseBinding.imageUpload.setImageURI(null)
        editExerciseBinding.videoUpload.visibility = View.GONE
        editExerciseBinding.webView.visibility = View.VISIBLE
        val unencodedHtml = """
            <html>
            <body>
            <iframe width="100%" height="100%" src="${convertToYouTubeEmbedUrl(videoString)}" frameborder="0"></iframe>
            </body>
            </html>
        """.trimIndent()
        editExerciseBinding.webView.settings.javaScriptEnabled = true
        editExerciseBinding.webView.settings.loadWithOverviewMode = true
        val predefinedDrawable = ContextCompat.getDrawable(this, R.drawable.ic_youtube)
        val bitmap = (predefinedDrawable as BitmapDrawable).bitmap
        image_file = bitmapToFile(bitmap, this)
        editExerciseBinding.imageUpload.setImageBitmap(bitmap)
//        videoString.let {
//            loadYouTubeThumbnail(
//                it,
//                createExerciseBinding.imageUpload
//            )
//        }
//        createExerciseBinding.webView.settings.useWideViewPort = true
        editExerciseBinding.webView.loadData(unencodedHtml, "text/html", "utf-8")
    }

    private fun playYouTubeVideo(videoString: String) {
        editExerciseBinding.selectUploadLy.visibility = View.GONE
        editExerciseBinding.imageUpload.visibility = View.GONE
        editExerciseBinding.videoUpload.visibility = View.GONE
        editExerciseBinding.webView.visibility = View.VISIBLE
        val unencodedHtml = """
            <html>
            <body>
            <iframe width="100%" height="100%" src="$videoString" frameborder="0"></iframe>
            </body>
            </html>
        """.trimIndent()
        editExerciseBinding.webView.settings.javaScriptEnabled = true
        editExerciseBinding.webView.settings.loadWithOverviewMode = true
//        val predefinedDrawable = ContextCompat.getDrawable(this, R.drawable.ic_youtube)
//        val bitmap = (predefinedDrawable as BitmapDrawable).bitmap
//        image_file = bitmapToFile(bitmap, this)
//        editExerciseBinding.imageUpload.setImageBitmap(bitmap)
        editExerciseBinding.webView.loadData(unencodedHtml, "text/html", "utf-8")
    }

    private fun playNormalVideo(videoString: String) {
        editExerciseBinding.selectUploadLy.visibility = View.GONE
        editExerciseBinding.imageUpload.visibility = View.GONE
        editExerciseBinding.videoUpload.stopPlayback() // Stop any previous playback
        editExerciseBinding.videoUpload.setVideoURI(null) // Clear the previous video URI
        editExerciseBinding.videoUpload.setVideoURI(Uri.parse(videoString)) // Set new video URI

        // Request layout update and redraw
        editExerciseBinding.videoUpload.requestLayout()
        editExerciseBinding.videoUpload.invalidate()

        mediaControls = MediaController(this)
        mediaControls.setAnchorView(editExerciseBinding.videoUpload)
        editExerciseBinding.videoUpload.setMediaController(mediaControls)
        editExerciseBinding.videoUpload.requestFocus()

//        editExerciseBinding.videoUpload.setOnPreparedListener {
//            it.isLooping = false
//            if (videoString.contains("https") || videoString.contains("http") || videoString.contains(
//                    ".com"
//                )
//            ) {
//                loadThumbnailFromUrl(this, videoString, editExerciseBinding.imageUpload)
//            } else {
//                val thumbnail = getThumbnailFromContentUri(this, Uri.parse(videoString))
//                Log.d("Local Video:-", "$thumbnail")
//
//                if (thumbnail.toString().contains("null")) {
//                    //image_file = File()
//                    val predefinedDrawable = ContextCompat.getDrawable(this, R.drawable.ic_youtube)
//                    val bitmap = (predefinedDrawable as BitmapDrawable).bitmap
//                    image_file = bitmapToFile(bitmap, this)
//                    editExerciseBinding.imageUpload.setImageBitmap(bitmap)
//                } else {
//                    image_file = bitmapToFile(thumbnail!!, this)
//                    editExerciseBinding.imageUpload.setImageBitmap(thumbnail)
//                }
//
////                image_file = bitmapToFile(thumbnail!!, this)
////                editExerciseBinding.imageUpload.setImageBitmap(thumbnail)
//            }
////            val thumbnail = getThumbnailFromFilePath(videoString)
////            createExerciseBinding.imageUpload.setImageBitmap(thumbnail)
//            editExerciseBinding.imageUpload.visibility = View.VISIBLE
////            val thumb = ThumbnailUtils.createVideoThumbnail(
////                fileAbsPath,
////                MediaStore.Video.Thumbnails.MINI_KIND
////            )
////            createExerciseBinding.imageUpload.setImageBitmap(thumb)
////            createExerciseBinding.imageUpload.visibility = View.VISIBLE
//
//            it.setOnInfoListener { _, what, _ ->
//                if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
//                    editExerciseBinding.imageUpload.visibility = View.GONE
//                    true
//                } else {
//                    false
//                }
//            }
//
//        }
        editExerciseBinding.videoUpload.visibility = View.VISIBLE
    }

    fun bitmapToFile(bitmap: Bitmap, context: Context): File {
        val file = File(context.cacheDir, "thumbnail.jpg")
        val fos = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        fos.flush()
        fos.close()
        return file
    }

    fun getThumbnailFromContentUri(context: Context, uri: Uri): Bitmap? {
        val filePath = getFilePathFromUri(context, uri)
        return filePath?.let {
            ThumbnailUtils.createVideoThumbnail(it, MediaStore.Video.Thumbnails.MINI_KIND)
        }
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

    fun loadThumbnailFromUrl(context: Context, url: String, imageView: ImageView) {
        Glide.with(context)
            .asBitmap()
            .load(url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    imageView.setImageBitmap(resource)
                    image_file = bitmapToFile(resource, this@EdiExerciseActivity)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // Handle case where image load is cleared
                }
            })
    }

    private fun isYouTubeUrl(url: String): Boolean {
        return url.contains("youtube.com") || url.contains("youtu.be")
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
        val weightInPixels = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, // The unit type (dp)
            330f, // The value in dp
            resources.displayMetrics // The display metrics
        ).toInt()

        val popupWindow = PopupWindow(
            popupView,
//            ViewGroup.LayoutParams.WRAP_CONTENT,
            weightInPixels,
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
                    val typeface = ResourcesCompat.getFont(this@EdiExerciseActivity, R.font.poppins_medium)
                    view.typeface = typeface
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
        val weightInPixels = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, // The unit type (dp)
            330f, // The value in dp
            resources.displayMetrics // The display metrics
        ).toInt()

        val popupWindow = PopupWindow(
            popupView,
//            ViewGroup.LayoutParams.WRAP_CONTENT,
            weightInPixels,
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
            object : ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, type) {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val view = super.getView(position, convertView, parent) as TextView
                    val typeface = ResourcesCompat.getFont(this@EdiExerciseActivity, R.font.poppins_medium)
                    view.typeface = typeface
                    view.setTextColor(Color.WHITE) // Set text color to white
                    return view
                }
            }
        listView.adapter = adapter
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = type[position]
            editExerciseBinding.edtType.setText(selectedItem)
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

    private fun showTimerPopup(
        anchorView: View?,
        data: MutableList<Timer.TimerData>,
        editText: EditText,
        list: ArrayList<String>,
        selectedValue: SelectedValue
    ) {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.popup_list, null)
        val weightInPixels = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, // The unit type (dp)
            330f, // The value in dp
            resources.displayMetrics // The display metrics
        ).toInt()

        val popupWindow = PopupWindow(
            popupView,
//            ViewGroup.LayoutParams.WRAP_CONTENT,
            weightInPixels,
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
                    val typeface = ResourcesCompat.getFont(this@EdiExerciseActivity, R.font.poppins_medium)
                    view.typeface = typeface
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

    private fun areAllFieldsFilled(): Boolean {
        return !(editExerciseBinding.edtName.text.isNullOrEmpty() || editExerciseBinding.edtNotes.text.isNullOrEmpty() ||
                editExerciseBinding.edtSection.text.toString() == "Select Section" ||
                editExerciseBinding.edtGoal.text.toString() == "Select Goal" ||
                editExerciseBinding.edtType.text.toString() == "Select Type" ||
                editExerciseBinding.edtCategory.text.toString() == "Select Category" ||
                editExerciseBinding.edtTimer.text.toString() == "Select Timer")
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

    private fun operations() {

        updateUI(editExerciseBinding.nextCard)

        editExerciseBinding.edtSection.setOnClickListener {
            showPopup(it, sectionData, editExerciseBinding.edtSection, section, sectionId)
        }

        editExerciseBinding.edtGoal.setOnClickListener {
            showPopup(it, goalData, editExerciseBinding.edtGoal, Goal, goalId)
        }

        editExerciseBinding.edtCategory.setOnClickListener {
            showPopup(it, categoryData, editExerciseBinding.edtCategory, category, categoryId)
        }

        editExerciseBinding.edtType.setOnClickListener {
            showTypePopup(it)
        }

        editExerciseBinding.edtTimer.setOnClickListener {
            showTimerPopup(it, timerData, editExerciseBinding.edtTimer, timer, timerId)
        }

        editExerciseBinding.selectImage.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(this)
            bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog)
            val videoLink = bottomSheetDialog.findViewById<LinearLayout>(R.id.gallery) // video link
            val video = bottomSheetDialog.findViewById<LinearLayout>(R.id.video) // video
            bottomSheetDialog.show()

            videoLink!!.setOnClickListener {
                bottomSheetDialog.dismiss()
                requestCode = 2
                videoDialog()
//                val intent = Intent()
//                intent.type = "image/gif"
//                intent.action = Intent.ACTION_GET_CONTENT
//                startActivityForResult(
//                    Intent.createChooser(intent, "Select Picture"),
//                    2
//                )
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

        editExerciseBinding.back.setOnClickListener {
            finish()
        }

        pickiT = PickiT(this, this, this)
        editExerciseBinding.nextCard.setOnClickListener {
            if (areAllFieldsFilled()) {
                if (isValidate) {

                    Progress_bar.visibility = View.VISIBLE
                    val str = arrayOfNulls<Int>(id.size)
                    val array = JsonArray()

                    for (i in 0 until id.size) {
                        str[i] = id.get(i)
                        array.add(id.get(i))
                    }

                    val nameString = editExerciseBinding.edtName.text.toString().trim()
                    val sectionString = sectionId.id.toString()
                    val goalString = goalId.id.toString()
                    val typeString = editExerciseBinding.edtType.text.toString().trim()
                    val categoryString = categoryId.id.toString()
                    val timerString = timerId.id.toString()
                    val noteString = editExerciseBinding.edtNotes.text.toString().trim()
                    val equipmentIds: MultipartBody.Part =
                        MultipartBody.Part.createFormData("equipment_ids", array.toString())
                    Log.d(
                        "Response :-",
                        "${
                            "id" + exercise_id + "\n" + "name -" + nameString + "\n" + "section -" + sectionString + "\n" + "goal -" + goalString + "\n"
                                    + "equipment -" + equipmentIds + "\n" + "type -" + typeString + "\n" + "category -" + categoryString + "\n" + "timer -" + timerString +
                                    "\n" + "note -" + noteString + "\n" + "file -" + file + "\n" + "video link -" + videolink + "\n" + "image file -" + image_file + "\n Request Code" + requestCode
                        }"
                    )
                    if (requestCode == 0) {
                        updateExercise(
                            exerciseId = exercise_id,
                            nameString = nameString,
                            sectionString = sectionString,
                            goalString = goalString,
                            typeString = typeString,
                            categoryString = categoryString,
                            timerString = timerString,
                            noteString = noteString,
                            equipmentIds = equipmentIds
                        )
                    } else if (requestCode == 1) {
                        requestFile = file!!.asRequestBody("video/*".toMediaTypeOrNull())
                        video = MultipartBody.Part.createFormData("video", "video", requestFile!!)
                        val imageFile = image_file!!.asRequestBody("image/*".toMediaTypeOrNull())
                        image = MultipartBody.Part.createFormData("image", "image", imageFile)
                        videoLinkPart = MultipartBody.Part.createFormData("video_link", "")

                        updateExercise(
                            exerciseId = exercise_id,
                            nameString = nameString,
                            sectionString = sectionString,
                            goalString = goalString,
                            typeString = typeString,
                            categoryString = categoryString,
                            timerString = timerString,
                            noteString = noteString,
                            equipmentIds = equipmentIds,
                            video = video,
                            videoLinkPart = videoLinkPart,
                            image = image
                        )
                    } else if (requestCode == 2) {
                        videoLinkPart = MultipartBody.Part.createFormData("video_link", videolink!!)
                        requestFile = image_file!!.asRequestBody("image/*".toMediaTypeOrNull())
                        image = MultipartBody.Part.createFormData("image", "image", requestFile!!)
                        val video: MultipartBody.Part? = null
                        updateExercise(
                            exerciseId = exercise_id,
                            nameString = nameString,
                            sectionString = sectionString,
                            goalString = goalString,
                            typeString = typeString,
                            categoryString = categoryString,
                            timerString = timerString,
                            noteString = noteString,
                            equipmentIds = equipmentIds,
                            video = video,
                            videoLinkPart = videoLinkPart,
                            image = image
                        )
                    }
//                if (requestCode == 1) {
//                    //video
//                    requestFile = file!!.asRequestBody("video/*".toMediaTypeOrNull())
//                    video = MultipartBody.Part.createFormData("video", "video", requestFile!!)
//                } else {
//                    if (videolink == null || videolink.toString().isEmpty()) {
//                        if (oldVideoLink!!.isNotEmpty()) {
//                            videoLinkPart =
//                                MultipartBody.Part.createFormData("video_link", oldVideoLink!!)
//                        } else {
//                            videoLinkPart = null
//                        }
//                    } else {
//                        videoLinkPart = MultipartBody.Part.createFormData("video_link", videolink!!)
//                    }
//                    if (image_file.toString().isNotEmpty()) {
//                        requestFile = image_file!!.asRequestBody("image/*".toMediaTypeOrNull())
//                        image = MultipartBody.Part.createFormData("image", "image", requestFile!!)
//                    }
//                }


//                try {
//                    apiInterface.EditExercise1(
//                        method = "PUT",
//                        id = exercise_id,
//                        name = nameString,
//                        sectionId = sectionString,
//                        timerId = timerString,
//                        goalId = goalString,
//                        type = typeString,
//                        categoryId = categoryString,
//                        notes = noteString,
//                        equipment_ids = equipmentIds,
//                        video = video,
//                        videoLink = videoLinkPart,
//                        thumbImage = image
//                    ).enqueue(object : Callback<Exercise> {
//                        override fun onResponse(
//                            call: Call<Exercise>,
//                            response: Response<Exercise>
//                        ) {
//                            Log.d(
//                                "Response :- ",
//                                "${response.code()} \n ${response.message()} \n ${response.isSuccessful}"
//                            )
//                        }
//
//                        override fun onFailure(call: Call<Exercise>, t: Throwable) {
//                            Log.d(
//                                "Response :- ",
//                                "${t.message} \n $t"
//                            )
//                        }
//
//                    })
//                } catch (e: Exception) {
//                    Progress_bar.visibility = View.GONE
//                    Toast.makeText(this, "" + e.message, Toast.LENGTH_SHORT).show()
//                }

//                Progress_bar.visibility = View.VISIBLE
//                val str = arrayOfNulls<Int>(id.size)
//                val array = JsonArray()
//
//                for (i in 0 until id.size) {
//                    str[i] = id.get(i)
//                    array.add(id.get(i))
//                }
//                val requestFile: RequestBody =
//                    image_file!!.asRequestBody("image/*".toMediaTypeOrNull())
//                val image =
//                    MultipartBody.Part.createFormData("image", "image", requestFile)
//
//                if (!file!!.equals(null)) {
//                    val requestFile1: RequestBody =
//                        file!!.asRequestBody("video/*".toMediaTypeOrNull())
//                    video =
//                        MultipartBody.Part.createFormData("video", "video", requestFile1)
//                }
//
//                val name: MultipartBody.Part =
//                    MultipartBody.Part.createFormData(
//                        "name",
//                        editExerciseBinding.edtName.text.toString()
//                    )
//                val method: MultipartBody.Part =
//                    MultipartBody.Part.createFormData("_method", "PUT")
//                val section_id: MultipartBody.Part =
//                    MultipartBody.Part.createFormData("section_id", "1")
//                val id: MultipartBody.Part =
//                    MultipartBody.Part.createFormData("id", exercise_id!!)
//                val goal_id: MultipartBody.Part = MultipartBody.Part.createFormData("goal_id", "1")
//                val type: MultipartBody.Part = MultipartBody.Part.createFormData("type", "General")
//                val category_id: MultipartBody.Part =
//                    MultipartBody.Part.createFormData("category_id", "2")
//                val equipment_ids: MultipartBody.Part =
//                    MultipartBody.Part.createFormData("equipment_ids", array.toString())
//                val notes: MultipartBody.Part =
//                    MultipartBody.Part.createFormData(
//                        "notes",
//                        editExerciseBinding.edtNotes.text.toString()
//                    )

//                apiInterface.EditExercise(
//                    method,
//                    id,
//                    image,
//                    video!!,
//                    name,
//                    section_id,
//                    goal_id,
//                    type,
//                    category_id,
//                    equipment_ids,
//                    notes
//                )?.enqueue(object : Callback<CycleData?> {
//                    override fun onResponse(
//                        call: Call<CycleData?>,
//                        response: Response<CycleData?>
//                    ) {
//                        Log.d("TAG", response.code().toString() + "")
//                        val resource: CycleData? = response.body()
//                        val Success: Boolean = resource?.status!!
//                        val Message: String = resource.message!!
//                        val Data = resource.data
//                        if (Success) {
//                            Progress_bar.visibility = View.GONE
//                            Toast.makeText(
//                                this@EdiExerciseActivity,
//                                "" + Message.toString(),
//                                Toast.LENGTH_SHORT
//                            ).show()
//                            startActivity(
//                                Intent(
//                                    this@EdiExerciseActivity,
//                                    EditCycleActivity::class.java
//                                ).putExtra("position", position)
//                                    .putExtra("exercise_id", Data!![0].id)
//                            )
//                            finish()
//                        } else {
//                            Progress_bar.visibility = View.GONE
//                            Toast.makeText(
//                                this@EdiExerciseActivity,
//                                "" + Message.toString(),
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//
//                    }
//
//                    override fun onFailure(call: Call<CycleData?>, t: Throwable) {
//                        Progress_bar.visibility = View.GONE
//                        Toast.makeText(
//                            this@EdiExerciseActivity,
//                            "" + t.message,
//                            Toast.LENGTH_SHORT
//                        )
//                            .show()
//                        call.cancel()
//                    }
//                })
                }
            }
        }

        editExerciseBinding.edtName.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    updateUI(editExerciseBinding.nextCard)
                }

                override fun afterTextChanged(s: Editable?) {}
            }
        )

        editExerciseBinding.edtNotes.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    updateUI(editExerciseBinding.nextCard)
                }

                override fun afterTextChanged(s: Editable?) {}
            }
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

    private fun updateExercise(
        exerciseId: String?,
        nameString: String,
        sectionString: String,
        goalString: String,
        typeString: String,
        categoryString: String,
        timerString: String,
        noteString: String,
        equipmentIds: MultipartBody.Part,
        video: MultipartBody.Part? = null,
        videoLinkPart: MultipartBody.Part? = null,
        image: MultipartBody.Part? = null
    ) {
        Log.d(
            "Response :-",
            "${
                "id" + exercise_id + "\n" + "name -" + nameString + "\n" + "section -" + sectionString + "\n" + "goal -" + goalString + "\n"
                        + "equipment -" + equipmentIds + "\n" + "type -" + typeString + "\n" + "category -" + categoryString + "\n" + "timer -" + timerString +
                        "\n" + "note -" + noteString + "\n" + "file -" + file + "\n" + "video link -" + videolink + "\n" + "image file -" + image_file + "\n Request Code" + requestCode
            }"
        )
        try {
            apiInterface.EditExercise1(
                method = "PUT",
                id = exerciseId,
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
                    call: Call<Exercise>,
                    response: Response<Exercise>
                ) {
                    Progress_bar.visibility = View.GONE
                    val code = response.code()
                    if (code == 200) {
                        Toast.makeText(
                            this@EdiExerciseActivity,
                            response.body()!!.message ?: "",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d(
                            "Response :- ",
                            "${response.code()} \n ${response.message()} \n ${response.isSuccessful}"
                        )
                        finish()
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@EdiExerciseActivity)
                    } else {
                        Toast.makeText(
                            this@EdiExerciseActivity,
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        ).show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<Exercise>, t: Throwable) {
                    Progress_bar.visibility = View.GONE
                    Toast.makeText(this@EdiExerciseActivity, t.message ?: "", Toast.LENGTH_SHORT)
                        .show()
                    Log.d(
                        "Response :- ",
                        "${t.message} \n $t"
                    )
                }

            })
        } catch (e: Exception) {
            Progress_bar.visibility = View.GONE
            Toast.makeText(this, "" + e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun initViews() {
        Progress_bar = findViewById(R.id.Progress_bar)
        id = ArrayList()
        type = ArrayList()
        editExerciseBinding.nextButtonText.text = "Edit"
        editExerciseBinding.titleName.text = "Edit Exercise"
        exerciselist = mutableListOf()
        generallist = mutableListOf()
        specificlist = mutableListOf()
        position = intent.getIntExtra("position", 0)
        exercise_id = intent.getIntExtra("exercise", 0).toString()

        Log.d("Position & id :-", "${position} \t ${exercise_id}")
        apiClient = APIClient(this)
        apiInterface = apiClient.client().create(APIInterface::class.java)

        editExerciseBinding.selectUploadLy.visibility = View.VISIBLE
        editExerciseBinding.imageUpload.visibility = View.GONE
    }

    private fun getTypeData() {
        type.add("General")
        type.add("Specific")
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
                        if (Data != null) {
                            sectionData.addAll(Data)
                            for (i in 0 until Data.size) {
                                section.add(Data[i].name!!)
                            }
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
                    Utils.setUnAuthDialog(this@EdiExerciseActivity)
                } else {
                    Progress_bar.visibility = View.GONE
                    Toast.makeText(
                        this@EdiExerciseActivity,
                        "" + response.message(),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<TestListData?>, t: Throwable) {
                Progress_bar.visibility = View.GONE
                Toast.makeText(this@EdiExerciseActivity, "" + t.message, Toast.LENGTH_SHORT)
                    .show()
                call.cancel()
            }
        })
    }

    private fun getTimerData() {
        timerData = mutableListOf()
        apiInterface.GetTimerData().enqueue(
            object : Callback<Timer> {
                override fun onResponse(call: Call<Timer>, response: Response<Timer>) {
                    val code = response.code()
                    if (code == 200) {
                        if (response.isSuccessful) {
                            val data = response.body()!!.data
                            if (data != null) {
                                timerData.addAll(data.toMutableList())
                                for (i in timerData) {
                                    timer.add(i.name!!)
                                }
                            }
                        }
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(this@EdiExerciseActivity)
                    } else {
                        Toast.makeText(
                            this@EdiExerciseActivity,
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
    }

    private fun getCategoryData() {
        categoryData = mutableListOf()
        apiInterface.GetCategoriesData()?.enqueue(object : Callback<TestListData> {
            override fun onResponse(call: Call<TestListData>, response: Response<TestListData>) {
                val code = response.code()
                if (code == 200) {
                    if (response.isSuccessful) {
                        val data = response.body()!!.data
                        if (data != null) {
                            categoryData.addAll(data.toMutableList())
                            for (i in categoryData) {
                                category.add(i.name!!)
                            }
                        }
                    }
                } else if (code == 403) {
                    Utils.setUnAuthDialog(this@EdiExerciseActivity)
                } else {
                    Toast.makeText(
                        this@EdiExerciseActivity,
                        "" + response.message(), Toast.LENGTH_SHORT
                    ).show()
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<TestListData>, t: Throwable) {
                Log.d("TAG Category", t.message.toString() + "")
            }

        })
    }

    private fun getGoalData() {
        goalData = mutableListOf()
        apiInterface.GetGoal()?.enqueue(object : Callback<TestListData> {
            override fun onResponse(call: Call<TestListData>, response: Response<TestListData>) {
                val code = response.code()
                if (code == 200) {
                    if (response.isSuccessful) {
                        val data = response.body()!!.data
                        if (data != null) {
                            goalData.addAll(data.toMutableList())
                            for (i in goalData) {
                                Goal.add(i.name!!)
                            }
                        }
                    }
                } else if (code == 403) {
                    Utils.setUnAuthDialog(this@EdiExerciseActivity)
                } else {
                    Toast.makeText(
                        this@EdiExerciseActivity,
                        "" + response.message(), Toast.LENGTH_SHORT
                    ).show()
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<TestListData>, t: Throwable) {
                Log.d("TAG Goal", t.message.toString() + "")
            }

        })
    }

    private fun GetEquipment() {
        Data = ArrayList()
        Data.clear()
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
                    if (resource.data != null) {
                        Data = resource.data!!
                    }
                } else if (code == 403) {
                    Utils.setUnAuthDialog(this@EdiExerciseActivity)
                } else {
                    Toast.makeText(
                        this@EdiExerciseActivity,
                        "" + response.message(), Toast.LENGTH_SHORT
                    ).show()
                    call.cancel()
                }
//                initrecycler(Data)

            }

            override fun onFailure(call: Call<CategoriesData?>, t: Throwable) {
                Toast.makeText(this@EdiExerciseActivity, "" + t.message, Toast.LENGTH_SHORT)
                    .show()
                call.cancel()
            }
        })
    }

    private fun initrecycler(data: ArrayList<CategoriesData.Categoty>) {
        editExerciseBinding.equipmentRly.layoutManager = GridLayoutManager(this, 2)
        adapter =
            EquipmentAdapter(data, this, this)
        editExerciseBinding.equipmentRly.adapter = adapter

    }

    private fun GetSection() {
        apiInterface.GetSection()?.enqueue(object : Callback<CategoriesData?> {
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
                    if (Success == true) {
                        if (Data != null) {
                            for (i in 0 until Data.size) {
                                age.add(Data[i].name!!)
                            }
                            Utils.setSpinnerAdapter(
                                applicationContext,
                                age,
                                editExerciseBinding.spSection
                            )
                        }
                    }
                } else if (code == 403) {
                    Utils.setUnAuthDialog(this@EdiExerciseActivity)
                } else {
                    Toast.makeText(
                        this@EdiExerciseActivity,
                        "" + response.message(), Toast.LENGTH_SHORT
                    ).show()
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<CategoriesData?>, t: Throwable) {
                Toast.makeText(this@EdiExerciseActivity, "" + t.message, Toast.LENGTH_SHORT)
                    .show()
                call.cancel()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                editExerciseBinding.selectUploadLy.visibility = View.GONE
                editExerciseBinding.imageUpload.visibility = View.VISIBLE
                val fileNameDl = data!!.data!!
                val fileAbsPath = FileUtils.getRealPath(this, fileNameDl)
                file = File(fileAbsPath!!)
                try {
                    playNormalVideo(fileNameDl.toString())
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
//                    editExerciseBinding.imageUpload.setImageBitmap(thumb)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                editExerciseBinding.selectUploadLy.visibility = View.GONE
                editExerciseBinding.imageUpload.visibility = View.VISIBLE
                val fileNameDl = data!!.data!!
                val fileAbsPath = FileUtils.getRealPath(this, fileNameDl)
                gif_file = File(fileAbsPath!!)
                try {
                    val thumb = ThumbnailUtils.createVideoThumbnail(
                        fileAbsPath,
                        MediaStore.Video.Thumbnails.MICRO_KIND
                    )
                    val bytes = ByteArrayOutputStream()
                    thumb!!.compress(Bitmap.CompressFormat.JPEG, 50, bytes)
                    val path: String = MediaStore.Images.Media.insertImage(
                        this.contentResolver,
                        thumb,
                        "Title",
                        null
                    )
                    pickiT!!.getPath(Uri.parse(path), Build.VERSION.SDK_INT)
                    editExerciseBinding.imageUpload.setImageBitmap(thumb)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

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

//        if (id.size != 0) {
//            if (contains(id, type.toInt())) {
//            } else {
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
            var name = editExerciseBinding.edtName.text.toString()

            var notes = editExerciseBinding.edtNotes.text.toString()

            if (fileNameDl == "") {
                editExerciseBinding.imageError.visibility = View.GONE
                editExerciseBinding.imageError.text = "Please Select Image"
            } else {
                editExerciseBinding.imageError.visibility = View.GONE
            }

            if (name == "") {
                editExerciseBinding.nameError.visibility = View.VISIBLE
                editExerciseBinding.nameError.text = "Please Name"
                return false
            } else {
                editExerciseBinding.nameError.visibility = View.GONE
            }

            if (editExerciseBinding.spGoal.selectedItem == "Enter Goal") {
                editExerciseBinding.goalError.visibility = View.VISIBLE
                editExerciseBinding.goalError.text = "Please Select Goal"
                return false
            } else {
                editExerciseBinding.goalError.visibility = View.GONE
            }

            if (notes == "") {
                editExerciseBinding.notesError.visibility = View.VISIBLE
                editExerciseBinding.notesError.text = "Please Enter Notes"
                return false
            } else {
                editExerciseBinding.notesError.visibility = View.GONE
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
}

//    private fun GetExercise() {
//        Log.d("Response :- ", "GetExercise: ")
//
//        Progress_bar.visibility = View.VISIBLE
//        try {
//            apiInterface.GetExerciseData()?.enqueue(object : Callback<Exercise> {
//                override fun onResponse(call: Call<Exercise>, response: Response<Exercise>) {
//                    Log.d(
//                        "Response :- ",
//                        "${response.body()!!.message} \n${response.message()}\n${response.body()!!.data!!.size}"
//                    )
//                    if (response.isSuccessful) {
////                        val data = response.body()!!.data
////                        exerciselist.addAll(data!!)
////                        for (i in 0 until exerciselist.size) {
////                            if (exerciselist[i].type == "General") {
////                                generallist.add(exerciselist[i])
////                            } else {
////                                specificlist.add(exerciselist[i])
////                            }
////                        }
////                        Progress_bar.visibility = View.GONE
////
////                        editExerciseBinding.edtName.setText(generallist[position!!].name)
////                        exercise_id = generallist[position!!].id!!.toString()
////                        editExerciseBinding.edtNotes.setText(generallist[position!!].notes)
//                    } else {
//                        Toast.makeText(
//                            this@EdiExerciseActivity,
//                            "" + response.message(),
//                            Toast.LENGTH_SHORT
//                        )
//                            .show()
//                        Progress_bar.visibility = View.GONE
//                    }
//                }
//
//                override fun onFailure(call: Call<Exercise>, t: Throwable) {
//                    Toast.makeText(this@EdiExerciseActivity, "" + t.message, Toast.LENGTH_SHORT)
//                        .show()
//                    call.cancel()
//                }
//
//            })
//        } catch (e: Exception) {
//            Log.d(
//                "Response :- ",
//                "${e.message.toString()}"
//            )
//        }
////        apiInterface.GetExercise()?.enqueue(object : Callback<ExcerciseData?> {
////            override fun onResponse(
////                call: Call<ExcerciseData?>,
////                response: Response<ExcerciseData?>
////            ) {
////                Log.d("TAG", response.code().toString() + "")
////                val resource: ExcerciseData? = response.body()
////                val Success: Boolean = resource?.status!!
////                val Message: String = resource.message!!
////                if (Success == true) {
////                    exerciselist = resource.data!!
////                    for (i in 0 until exerciselist.size) {
////                        if (exerciselist[i].type == "General") {
////                            generallist.add(exerciselist[i])
////                        } else {
////                            specificlist.add(exerciselist[i])
////                        }
////                    }
////                    Progress_bar.visibility = View.GONE
////
////                    editExerciseBinding.edtName.setText(generallist[position!!].name)
////                    exercise_id = generallist[position!!].id!!.toString()
////                    editExerciseBinding.edtNotes.setText(generallist[position!!].notes)
////                }
////
////            }
////
////            override fun onFailure(call: Call<ExcerciseData?>, t: Throwable) {
////                Toast.makeText(this@EdiExerciseActivity, "" + t.message, Toast.LENGTH_SHORT)
////                    .show()
////                call.cancel()
////            }
////        })
//
//    }
