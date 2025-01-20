package com.example

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.trainerapp.ApiClass.ProgramListData
import com.example.trainerapp.PreferencesManager
import com.example.trainerapp.R
import com.google.gson.Gson

class ProgramListAdapter(
    private var splist: ArrayList<ProgramListData.testData>?,
    var context: Context,
    val listener: OnItemClickListener.OnItemClickCallback,
    private var selectId: ArrayList<Int>? = null,
    private var Sname: String? = null,
    private var type: String? = null
) : RecyclerView.Adapter<ProgramListAdapter.MyViewHolder>() {


    private var programIid1: ArrayList<ProgramListData.testData>
    var preferenceManager: PreferencesManager = PreferencesManager(context)


    init {
        programIid1 = arrayListOf()
        programIid1.clear()
        if (type == "edit") {
            if (!selectId.isNullOrEmpty()) {
                if (selectId != null && selectId!!.isNotEmpty()) {
                    for (id in selectId!!) {
                        val exc = splist!!.find { it.id == id }
                        if (exc != null) {
                            programIid1.add(exc)
                        }
                        SetIdList1(context, programIid1, "Exercise")
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProgramListAdapter.MyViewHolder {

        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.exercise_data_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProgramListAdapter.MyViewHolder, position: Int) {
        val movie = splist!![position]

        // Check if Sname matches movie.section!!.name
        if (Sname == movie.section!!.name) {
            // Display the item's data
            holder.image.visibility = View.GONE
            holder.tvFname.text = movie.name
            holder.tvgoal.text = "Goal: " + movie.goal!!.name
            holder.tv_athlet.text = "Time: " + movie.time

            Log.d("MATCHING ITEM", "Sname: $Sname matches Section Name: ${movie.section!!.name}")
        } else {
            // Keep the layout unchanged, but do not set data
            holder.image.visibility = View.GONE
            holder.card.visibility = View.GONE
            holder.tvFname.text = ""
            holder.tvgoal.text = ""
            holder.tv_athlet.text = ""

            Log.d("NON-MATCHING ITEM", "Sname: $Sname does not match Section Name: ${movie.section!!.name}")
        }

        // Handle checkbox state
        holder.checkbox.isChecked = selectId?.contains(movie.id) == true

        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                setdata1(position)  // Add to selection
            } else {
                OnItemClickListener(position, listener, movie.id!!.toLong(), "remove")
                removeData(movie.id!!)  // Remove from selection
            }
        }
    }

    override fun getItemCount(): Int {
        return splist!!.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvFname: TextView = view.findViewById<View>(R.id.tvFname) as TextView
        var tvgoal: TextView = view.findViewById<View>(R.id.tvgoal) as TextView
        var tv_athlet: TextView = view.findViewById<View>(R.id.tv_athlet) as TextView
        var image: ImageView = view.findViewById<View>(R.id.image) as ImageView
        var checkbox: CheckBox = view.findViewById<View>(R.id.checkbox) as CheckBox
        var card: CardView = view.findViewById<View>(R.id.card) as CardView

    }

    private fun removeData(id: Int) {
        // Find and remove the item from programIid1 list
        val iterator = programIid1.iterator()
        while (iterator.hasNext()) {
            val item = iterator.next()
            if (item.id == id) {
                iterator.remove()
            }
        }

        if (programIid1.isEmpty()) {
            // If no items are selected, save null to preferences
            SetIdList1(context, null, "Exercise")
        } else {
            // Save the selected items
            SetIdList1(context, programIid1, "Exercise")
        }

        // Update the stored data after removing
//        SetIdList1(context, programIid1, "Exercise")
    }


    private fun setdata1(position: Int) {
        programIid1.add(splist!![position])
        SetIdList1(context, programIid1, "Exercise")
    }


    fun SetIdList1(c: Context, modal: ArrayList<ProgramListData.testData>?, key: String?) {
        val appSharedPrefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(
            c.applicationContext
        )
        val prefsEditor: SharedPreferences.Editor = appSharedPrefs.edit()
        val gson = Gson()

        if (modal == null) {
            // Save null to SharedPreferences
            prefsEditor.putString(key, null)
        } else {
            // Save the list to SharedPreferences
            val jsonObject = gson.toJson(modal)
            prefsEditor.putString(key, jsonObject)
        }

//        val jsonObject = gson.toJson(modal)
//        prefsEditor.putString(key, jsonObject)
        prefsEditor.commit()

    }
}

//    private lateinit var id: ArrayList<Ecercise_data_list>
//    var id1: ArrayList<ProgramListData.testData> = arrayListOf()
//    private lateinit var programIid: ArrayList<ProgramListData.testData>
//    var id_list: ArrayList<Int> = ArrayList()

//init {
//        id1.clear()
//        if (type == "edit") {
//            if (!selectId.isNullOrEmpty()) {
//                if (selectId != null && selectId!!.isNotEmpty()) {
//                    for (id in selectId!!) {
//                        val exc = splist!!.find { it.id == id }
//                        if (exc != null) {
//                            SetIdList1(context, arrayListOf(exc), "Exercise")
//                            if (!id1.isEmpty()) {
//                                preferenceManager.setselectAthelete(true)
//                            }
//                        }
//                    }
//                }
//            }
//        }

//        id_list.clear()
//        if (type == "edit") {
//            if (!selectId.isNullOrEmpty()) {
//                // Load previously selected exercises based on selectId
//                if (selectId != null && selectId!!.isNotEmpty()) {
//                    for (id in selectId!!) {
//                        val exercise = splist!!.find { it.id == id }
//                        if (exercise != null) {
////                        id_list.add(exercise.id!!)
////                        name_list.add(exercise.name!!)
////                            SetIdList(context, exercise.id!!, "Exercise")
//                            setdata(exercise.id!!, "add", exercise.name!!)
//                            if (!id_list.isEmpty()) {
//                                preferenceManager.setselectAthelete(true)
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }

//        id = ArrayList()
//        programIid = ArrayList()

//if (type == "edit") {
////            holder.checkbox.isChecked = id_list.contains(movie.id)
//
//    holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
//        if (isChecked) {
//            setdata1(position)
//            //setdata(movie.id!!, "add", movie.name!!)
//        }
////                else {
////
////                    //setdata(movie.id!!, "remove", movie.name!!)
////                }
//    }
//} else {
//    holder.checkbox.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, b ->
//        if (b.equals(true)) {
//            //setdata(movie.id!!, "add", movie.name!!)
//            setdata1(position)
////                    if (!id_list.isEmpty()) {
////                        preferenceManager.setselectAthelete(true)
////                    }
//        } else {
//            OnItemClickListener(position, listener, movie.id!!.toLong(), "remove")
//        }
////                else {
//////                selectId!!.remove(movie.id!!.toInt())
////                    setdata(movie.id!!, "remove", movie.name!!)
////                }
//    })
//}

//        holder.checkbox.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, b ->
//            Toast.makeText(context, "$position", Toast.LENGTH_SHORT).show()
//            if (b.equals(true)) {
//                setdata(movie.id!!, "add", movie.name!!)
//                if (!id_list.isEmpty()) {
//                    preferenceManager.setselectAthelete(true)
//                }
//            } else {
//                OnItemClickListener(position, listener, movie.id!!.toLong(), "remove")
//            }
//        })

//    private fun setdata(id: Int, status: String, name: String) {
//        if (status == "add") {
//            id_list.add(id)
////            name_list.add(name)
////
//        } else {
//            id_list.remove(id)
////            name_list.remove(name)
//        }
//        SetIdList(context, id_list, "Exercise")
////        SetnameList(context, name_list, "setAthleteName")
////        Log.d("jenis", "" + id_list.size.toString())
//    }

//private fun setdata1(position: Int) {
//        var time: String? = "01:00:00"
//        if (splist!!.size != 0) {
//            time = splist!![position].time
//        }
//        programIid1.add(splist!![position])
//        SetIdList1(context, programIid1, "Exercise")
////        id.add(
////            Ecercise_data_list(
////                splist!![position].id!!.toString(),
////                splist!![position].coach_id.toString(),
////                splist!![position].name.toString(),
////                splist!![position].goal!!.name.toString(),
////                time!!,
////                "",
////            )
////        )
////        SetIdList(context, id, "Exercise")
//    }


//    fun SetIdList(c: Context, modal: ArrayList<Ecercise_data_list>?, key: String?) {
//        val appSharedPrefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(
//            c.applicationContext
//        )
//        val prefsEditor: SharedPreferences.Editor = appSharedPrefs.edit()
//        val gson = Gson()
//        val jsonObject = gson.toJson(modal)
//        prefsEditor.putString(key, jsonObject)
//        prefsEditor.commit()
//
//    }

//    fun SetIdList(c: Context, modal: ArrayList<Int>?, key: String?) {
//        val appSharedPrefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(
//            c.applicationContext
//        )
//        val prefsEditor: SharedPreferences.Editor = appSharedPrefs.edit()
//        val gson = Gson()
//        val jsonObject = gson.toJson(modal)
//        prefsEditor.putString(key, jsonObject)
//        prefsEditor.commit()
//    }