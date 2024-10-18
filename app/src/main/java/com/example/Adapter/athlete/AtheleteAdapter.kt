package com.example.Adapter.athlete

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.model.newClass.athlete.AthleteData
import com.example.trainerapp.PreferencesManager
import com.example.trainerapp.R
import com.google.gson.Gson

class AtheleteAdapter(
    private var splist: ArrayList<AthleteData.Athlete>?,
    var context: Context,
    private var selectId: ArrayList<Int>? = null,
    private var type: String? = null
) :
    RecyclerView.Adapter<AtheleteAdapter.MyViewHolder>() {
    var preferenceManager: PreferencesManager = PreferencesManager(context)
    var nameData: ArrayList<AthleteData.Athlete> = ArrayList()
    var id_list: ArrayList<Int> = ArrayList()
    var name_list: ArrayList<String> = ArrayList()
    var isSelectAll = false

    init {
        nameData.clear()
        name_list.clear()
        id_list.clear()
        if (type == "edit") {
            if (!selectId.isNullOrEmpty()) {
                // Load previously selected exercises based on selectId
                if (selectId != null && selectId!!.isNotEmpty()) {
                    for (id in selectId!!) {
                        val exercise = splist!!.find { it.id == id }
                        if (exercise != null && !nameData.contains(exercise)) {
//                        id_list.add(exercise.id!!)
//                        name_list.add(exercise.name!!)
                            setdata(exercise.id!!, "add", exercise.name!!)
                            if (!id_list.isEmpty()) {
                                preferenceManager.setselectAthelete(true)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AtheleteAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list_athletes, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AtheleteAdapter.MyViewHolder, position: Int) {
        val movie = splist!![position]
        holder.cbAthleteName.setOnCheckedChangeListener(null)
        holder.tvAthleteName.text = movie.name

        if (type == "edit") {
            holder.cbAthleteName.isChecked = id_list.contains(movie.id)

            holder.cbAthleteName.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    setdata(movie.id!!, "add", movie.name!!)
                } else {
                    setdata(movie.id!!, "remove", movie.name!!)
                }
            }
        } else {
            holder.cbAthleteName.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, b ->
                if (b.equals(true)) {
                    setdata(movie.id!!, "add", movie.name!!)
                    if (!id_list.isEmpty()) {
                        preferenceManager.setselectAthelete(true)
                    }
                } else {
//                selectId!!.remove(movie.id!!.toInt())
                    setdata(movie.id!!, "remove", movie.name!!)
                }
            })
        }
//        holder.cbAthleteName.isChecked = id_list.contains(movie.id)
//        holder.cbAthleteName.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, b ->
//            if (holder.cbAthleteName.isChecked) {
//                setdata(movie.id!!, "add", movie.name!!)
//                if (!id_list.isEmpty()) {
//                    preferenceManager.setselectAthelete(true)
//                }
//            } else {
//                setdata(movie.id!!, "remove", movie.name!!)
//            }
//
//        })


//        if (type == "edit") {
//            try { // Disable listener to avoid unwanted triggers
//                holder.cbAthleteName.isChecked = selectId!!.contains(movie.id)
//                holder.cbAthleteName.setOnCheckedChangeListener { _, isChecked ->
//                    if (isChecked) {
//                        if (nameData.isNotEmpty()) {
//                            // Add to newdata if it's not already present
//                            if (!nameData.contains(movie)) {
////                            name_list.add(movie.name!!)
////                            id_list.add(movie.id!!)
//                                nameData.add(movie)
//                                setdata(movie.id!!, "add", movie.name!!)
//                                if (!id_list.isEmpty()) {
//                                    preferenceManager.setselectAthelete(true)
//                                }
//                            } else {
//                                // Remove from newdata when unchecked
////                        name_list.remove(movie.name!!)
////                        id_list.remove(movie.id!!)
//                                nameData.remove(movie)
//                                setdata(movie.id!!, "remove", movie.name!!)
//                            }
//                        } else {
//                            if (isChecked) {
//                                setdata(movie.id!!, "add", movie.name!!)
//                                if (!id_list.isEmpty()) {
//                                    preferenceManager.setselectAthelete(true)
//                                }
//                            } else {
//                                setdata(movie.id!!, "remove", movie.name!!)
//                            }
//                        }
//                    } else {
//                        //nameData.remove(movie)
//                        setdata(movie.id!!, "remove", movie.name!!)
//                    }
////                    setdata()
////
////                    // Save the updated newdata list to SharedPreferences
////                    setExerciseData(context, nameData, "Exercise_list")
//                }
//            } catch (e: Exception) {
//                Log.d("Exception", e.message ?: "Unknown error")
//            }
//        } else if (type == "create") {
//            holder.cbAthleteName.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, b ->
//                if (b.equals(true)) {
//                    setdata(movie.id!!, "add", movie.name!!)
//                    if (!id_list.isEmpty()) {
//                        preferenceManager.setselectAthelete(true)
//                    }
//                } else {
////                selectId!!.remove(movie.id!!.toInt())
//                    setdata(movie.id!!, "remove", movie.name!!)
//                }
//            })
//        }
    }

    private fun setdata(id: Int, status: String, name: String) {
        if (status == "add") {
            id_list.add(id)
            name_list.add(name)

        } else {
            id_list.remove(id)
            name_list.remove(name)
        }
        SetIdList(context, id_list, "setAthlete")
        SetnameList(context, name_list, "setAthleteName")
        Log.d("jenis", "" + id_list.size.toString())
    }

    override fun getItemCount(): Int {
        return splist!!.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvAthleteName: TextView = view.findViewById<View>(R.id.tvAthleteName) as TextView
        var cbAthleteName: CheckBox = view.findViewById<View>(R.id.cbAthleteName) as CheckBox
        var clAthlete: ConstraintLayout =
            view.findViewById<View>(R.id.clAthlete) as ConstraintLayout
    }

    fun SetIdList(c: Context, modal: ArrayList<Int>?, key: String?) {
        val appSharedPrefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(
            c.applicationContext
        )
        val prefsEditor: SharedPreferences.Editor = appSharedPrefs.edit()
        val gson = Gson()
        val jsonObject = gson.toJson(modal)
        prefsEditor.putString(key, jsonObject)
        prefsEditor.commit()
    }


    fun SetnameList(c: Context, modal: ArrayList<String>?, key: String?) {
        val appSharedPrefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(
            c.applicationContext
        )
        val prefsEditor: SharedPreferences.Editor = appSharedPrefs.edit()
        val gson = Gson()
        val jsonObject = gson.toJson(modal)
        prefsEditor.putString(key, jsonObject)
        prefsEditor.commit()
    }

    fun selectAll(b: Boolean) {
        this.isSelectAll = b
        id_list.clear()
        name_list.clear()


//        if (isSelectAll) {
//            for (i in 0 until splist!!.size) {
//                id_list.add(splist!![i].id!!)
//                name_list.add(splist!![i].name!!)
//            }
//            SetIdList(context, id_list, "setAthlete")
//            SetnameList(context, name_list, "setAthleteName")
//        } else {
//            for (i in 0 until splist!!.size) {
//                id_list.remove(splist!![i].id)
//
//                name_list.remove(splist!![i].name)
//            }
//            SetIdList(context, id_list, "setAthlete")
//            SetnameList(context, name_list, "setAthleteName")
//        }
//
//        notifyDataSetChanged()

//        this.isSelectAll = b
//        id_list.clear()
//        name_list.clear()
//
        if (isSelectAll) {
            // Select all items
            splist?.forEach { athlete ->
                athlete.id?.let {
                    if (!id_list.contains(it)) { // Prevent duplicates
                        id_list.add(it)
                    }
                }
                athlete.name?.let {
                    if (!name_list.contains(it)) { // Prevent duplicates
                        name_list.add(it)
                    }
                }
            }
        }

        // Save to SharedPreferences regardless of selection
        SetIdList(context, id_list, "setAthlete")
        SetnameList(context, name_list, "setAthleteName")

        // Refresh the RecyclerView to reflect the changes
        notifyDataSetChanged()
    }

}
