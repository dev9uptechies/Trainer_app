package com.example

import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.ApiClass.RegisterData
import com.example.trainerapp.PreferencesManager
import com.example.trainerapp.R
import com.example.trainerapp.SignInActivity
import com.example.trainerapp.Utils
import com.example.trainerapp.databinding.FragmentGroupBinding
import com.example.trainerappAthlete.model.GroupAdapterAthlete
import com.example.trainerappAthlete.model.GroupListAthlete
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GroupFragment : Fragment(), OnItemClickListener.OnItemClickCallback {
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    lateinit var preferenceManager: PreferencesManager
    var userType: String? = null
    lateinit var groupadapter: GroupAdapter
    lateinit var groupadapterAthlete: GroupAdapterAthlete
    private var receivedId: String = ""
    private var groupList: ArrayList<GroupListData.groupData> = ArrayList()
    private var groupListAthlete: List<GroupListAthlete.Data>? = null



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
                        callGroupApi()
                        Log.d("Get Profile Data ", "${response.body()}")
                    } else if (code == 403) {
                        Utils.setUnAuthDialog(requireContext())
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "" + response.message(),
                            Toast.LENGTH_SHORT
                        ).show()
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<RegisterData?>, t: Throwable) {
                    Toast.makeText(
                        requireContext(),
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

    //    lateinit var group_progress: ProgressBar
    //    lateinit var create_group: ImageView

    lateinit var groupBinding: FragmentGroupBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        groupBinding = FragmentGroupBinding.inflate(layoutInflater, container, false)

        receivedId = arguments?.getString("isd").toString()
        Log.d("GroupFragment", "Received ID: $receivedId")

        //        recycler_view = view.findViewById(R.id.group_rly)
        //        group_progress = view.findViewById(R.id.group_progress)
        //        create_group = view.findViewById(R.id.create_group)

        apiClient = APIClient(requireContext())
        preferenceManager = PreferencesManager(requireContext())
        apiInterface = apiClient.client().create(APIInterface::class.java)
        userType = preferenceManager.GetFlage()


        ButtonClick()
        loadData()


        return groupBinding.root
    }

    private fun loadData() {
        if (userType == "Athlete") {
            groupBinding.createGroup.setImageResource(R.drawable.qr_code_image)
            callGroupApiAthlete()
        } else {
            groupBinding.createGroup.visibility = View.VISIBLE
            callGroupApi()
        }
    }

    private fun ButtonClick() {

        if (userType == "Athlete") {

            groupBinding.createGroup.setOnClickListener {
                if (checkAndRequestPermissions()) {
                    groupBinding.main.setBackgroundColor(resources.getColor(R.color.grey))
                    showQRScannerDialog()
                }
            }


        } else {
            groupBinding.createGroup.setOnClickListener {
                startActivity(Intent(requireContext(), CreateGropActivity::class.java))
            }
        }

    }

    private fun showQRScannerDialog() {
        val dialog = Dialog(requireContext(), R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.scan_qr_for_join_group)

        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = (displayMetrics.widthPixels * 0.8f).toInt()
        val height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.setLayout(width, height)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val scannerView = dialog.findViewById<CodeScannerView>(R.id.scanner_view)
        val codeScanner = CodeScanner(requireContext(), scannerView)
        var isScannerRunning = true // Scanner running state

        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = CodeScanner.ALL_FORMATS
        codeScanner.autoFocusMode = AutoFocusMode.SAFE
        codeScanner.scanMode = ScanMode.SINGLE
        codeScanner.isAutoFocusEnabled = true
        codeScanner.isFlashEnabled = false

        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
           if (it.text != null){
               Log.d("IT.TEXT", "showQRScannerDialog: ${it.text}")
               JoinGroup(it.text)
               groupBinding.main.setBackgroundColor(resources.getColor(R.color.black))
               dialog.dismiss()
           }
        }
        codeScanner.errorCallback = ErrorCallback {
            requireActivity().runOnUiThread {
                Toast.makeText(requireContext(), "Camera error: ${it.message}", Toast.LENGTH_LONG)
                    .show()
            }
        }

        dialog.setOnShowListener {
            codeScanner.startPreview()
        }

        dialog.setOnDismissListener {
            codeScanner.releaseResources()
        }

        // Close button action
        val closeButton = dialog.findViewById<AppCompatButton>(R.id.btnCancel)
        closeButton.setOnClickListener {
            groupBinding.main.setBackgroundColor(resources.getColor(R.color.black))
            dialog.dismiss()
        }

        val stopButton = dialog.findViewById<AppCompatButton>(R.id.btnApply)
        stopButton.text = "Stop" // Initial text
        stopButton.setOnClickListener {
            if (isScannerRunning) {
                // Stop the scanner
                codeScanner.stopPreview()
                stopButton.text = "Scan" // Update button text
            } else {
                // Resume the scanner
                codeScanner.startPreview()
                stopButton.text = "Stop" // Update button text
            }
            isScannerRunning = !isScannerRunning // Toggle the state
        }

        // Show dialog
        dialog.show()
    }


    private fun JoinGroup(groupId: String) {
        try {

            // Call the API
            apiInterface.JoinGroup(groupId).enqueue(object : Callback<Any> {
                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    if (response.isSuccessful) {
                        val groupResponse = response.body()
                        if (groupResponse != null) {

                            if (response.code() == 200) {
                                Log.d("ADDDDDDDDDDDDD", "onResponse: (Group Added)")
                                loadData()

                            }else if (response.code() == 429) {
                                Toast.makeText(requireContext(), "Too Many Request", Toast.LENGTH_SHORT).show()
                            }

                        }  else {
                            Toast.makeText(requireContext(), "Invalid Group", Toast.LENGTH_SHORT).show()
                        }

                    } else {
                        Toast.makeText(requireContext(), "Error:- ${response.errorBody()}", Toast.LENGTH_SHORT).show()
                        Log.d("DHDHDHHDHDH", "onResponse: ${response.errorBody()}")
                    }
                }

                override fun onFailure(call: Call<Any>, t: Throwable) {
                    Log.d("Failure", "Failed: ${t.message ?: "Unknown failure"}")
                }
            })

        } catch (e: Exception) {
            Log.d("CATCHHHH", "fetchGroupDetails: ${e.message.toString()}")
        }
    }


    private fun checkAndRequestPermissions(): Boolean {
        val permission = android.Manifest.permission.CAMERA
        val granted = ContextCompat.checkSelfPermission(
            requireContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED

        if (!granted) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(permission),
                101
            )
        }
        return granted
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            groupBinding.main.setBackgroundColor(resources.getColor(R.color.grey))
            showQRScannerDialog()
        } else {
            Toast.makeText(
                requireContext(),
                "Camera permission is required to scan QR codes",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    private fun callGroupApiAthlete() {
        try {
            groupBinding.groupProgress.visibility = View.VISIBLE
            apiInterface.GropListAthlete().enqueue(object : Callback<GroupListAthlete?> {
                override fun onResponse(
                    call: Call<GroupListAthlete?>,
                    response: Response<GroupListAthlete?>
                ) {
                    val code = response.code()
                    if (code == 200) {
                        val resource: GroupListAthlete? = response.body()
                        val success: Boolean = resource?.status ?: false
                        val message: String = resource?.message ?: "Unknown"

                        if (success) {
                            groupBinding.groupProgress.visibility = View.GONE
                            // Store the list of GroupListAthlete.Data items
                            groupListAthlete =
                                resource?.data // No need for toList() since it's already a list
                            initrecyclerAthlete(groupListAthlete!!) // Pass the list to the RecyclerView

                        } else if (response.code() == 429) {
                            Toast.makeText(requireContext(), "Too Many Request", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            groupBinding.groupProgress.visibility = View.GONE
                            // Handle failure case
                        }
                    } else if (response.code() == 403) {
                        groupBinding.groupProgress.visibility = View.GONE
                        val message = response.message()
                        call.cancel()
                        preferenceManager.setUserLogIn(false)
                        startActivity(Intent(requireContext(), SignInActivity::class.java))
                        requireActivity().finish()
                    } else {
                        groupBinding.groupProgress.visibility = View.GONE
                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<GroupListAthlete?>, t: Throwable) {
                    Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                    call.cancel()
                    Log.d("GROOOOOOOP", t.message.toString())
                    groupBinding.groupProgress.visibility = View.GONE
                    groupBinding.groupProgress.visibility
                }
            })
        } catch (e: Exception) {
            Log.d("catch", "callGroupApiAthlete: ${e.message}")
        }
    }


    private fun callGroupApi() {
        try {
            groupBinding.groupProgress.visibility = View.VISIBLE
            apiInterface.GropList()?.enqueue(object : Callback<GroupListData?> {
                override fun onResponse(
                    call: Call<GroupListData?>,
                    response: Response<GroupListData?>
                ) {

                    val code = response.code()
                    if (code == 200) {
                        val resource: GroupListData? = response.body()
                        val Success: Boolean = resource?.status!!
                        val Message: String = resource.message!!

                        if (Success) {
                            groupBinding.groupProgress.visibility = View.GONE
                            groupList = resource.data!!
                            initrecycler(resource.data!!)


                        } else {
                            groupBinding.groupProgress.visibility = View.GONE
                        }
                    } else if (response.code() == 403) {
                        groupBinding.groupProgress.visibility = View.GONE
                        val message = response.message()
                        call.cancel()
                        preferenceManager.setUserLogIn(false)
                        startActivity(
                            Intent(
                                requireContext(),
                                SignInActivity::class.java
                            )
                        )
                        requireActivity().finish()
                    } else {
                        groupBinding.groupProgress.visibility = View.GONE
                        val message = response.message()
                        //                    Toast.makeText(requireContext(), "" + message, Toast.LENGTH_SHORT)
                        //                        .show()

                        call.cancel()
                    }
                }

                override fun onFailure(call: Call<GroupListData?>, t: Throwable) {
                    Toast.makeText(requireContext(), "" + t.message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                    Log.d("GROOOOOOOP", t.message.toString() + "")
                    groupBinding.groupProgress.visibility = View.GONE
                }
            })
        } catch (e: Exception) {
            Log.d("catch", "callGroupApiAthlete: ${e.message.toString()}")
        }
    }

    private fun initrecycler(data: ArrayList<GroupListData.groupData>) {
        try {
            if (!isAdded) {
                Log.e("GroupFragment", "Fragment is not attached to an activity.")
                return
            }

            groupBinding.groupRly.layoutManager = LinearLayoutManager(requireActivity())
            groupadapter = GroupAdapter(data, requireContext(), this)
            groupBinding.groupRly.adapter = groupadapter
        } catch (e: Exception) {
            Log.d("catch", "callGroupApiAthlete: ${e.message.toString()}")
        }
    }

    private fun initrecyclerAthlete(data: List<GroupListAthlete.Data>) {
        try {
            if (!isAdded) {
                Log.e("GroupFragment", "Fragment is not attached to an activity.")
                return
            }

            groupBinding.groupRly.layoutManager = LinearLayoutManager(requireActivity())
            groupadapterAthlete = GroupAdapterAthlete(data, requireContext(), this)
            groupBinding.groupRly.adapter = groupadapterAthlete



        } catch (e: Exception) {
            Log.d("catch", "initrecyclerAthlete: ${e.message}")
        }
    }

    override fun onItemClicked(view: View, position: Int, type: Long, string: String) {
        if (groupList != null) {

            val groupId = groupList[position].id

            startActivity(
                Intent(requireContext(), GroupDetailActivity::class.java).apply {
                    putExtra("id", groupList[position].id)
                    putExtra("group_id", groupId)
                    putExtra("position", position)
                }

            )
        }

    }


}