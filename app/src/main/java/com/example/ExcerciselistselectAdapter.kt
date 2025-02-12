package com.example

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.trainerapp.ApiClass.ExcerciseData
import com.example.trainerapp.R
import com.google.gson.Gson
import com.makeramen.roundedimageview.RoundedImageView
import com.makeramen.roundedimageview.RoundedTransformationBuilder
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import java.text.DateFormat
import java.text.SimpleDateFormat

class ExcerciselistselectAdapter(
    private var splist: ArrayList<ExcerciseData.Exercise>?,
    var context: Context,
    val listener: OnItemClickListener.OnItemClickCallback,
    private var selectId: ArrayList<Int>? = null,
    private var type: String? = null
) :
    RecyclerView.Adapter<ExcerciselistselectAdapter.MyViewHolder>() {
    var time: Int? = null
    private var newdata: ArrayList<ExcerciseData.Exercise> = ArrayList()

    init {
        newdata.clear()
        if (type == "edit") {
            // Load previously selected exercises based on selectId
            if (selectId != null && selectId!!.isNotEmpty()) {
                for (id in selectId!!) {
                    val exercise = splist!!.find { it.id == id }
                    if (exercise != null && !newdata.contains(exercise)) {
                        newdata.add(exercise)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ExcerciselistselectAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.exercise_data_item, parent, false)
        return MyViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: ExcerciselistselectAdapter.MyViewHolder, position: Int) {
        Log.d("Old Database :- ", "${selectId}")
        val movie = splist!![position]
        holder.tvFname.text = movie.name
        val cycle = movie.cycles
        val formatter: DateFormat = SimpleDateFormat("hh:mm:ss")
        for (i in 0 until cycle!!.size) {
            val secont = cycle[i].time
            holder.tvgoal.text = "Time or Reps: " + secont
            val date1 = formatter.parse(secont)
            time = ((date1.time / 1000).toInt())
            if (cycle[i].weight == null || cycle[i].weight.equals("") || cycle[i].weight.equals("null")) {
                holder.tvAthlet.text = "Weight: Weight"
            } else {
                holder.tvAthlet.text = "Weight: " + cycle[i].weight.toString()
            }
        }
//        try {
//            if (selectId!!.size != 0) {
//                holder.checkbox.isChecked = selectId!!.contains(movie.id!!)
//                //setdata(position)
//            }
//        } catch (e: Exception) {
//            Log.d("Exception :-", "$e \t ${e.message}")
//        }

        //holder.checkbox.isChecked = selectId!!.size != 0 && selectId != null

        val transformation: Transformation = RoundedTransformationBuilder()
            .borderColor(Color.BLACK)
            .borderWidthDp(1f)
            .cornerRadiusDp(10f)
            .oval(false)
            .build()

        Picasso.get()
            .load("https://4trainersapp.com" + movie.image)
            .fit()
            .transform(transformation)
            .placeholder(R.drawable.ic_youtube)
            .error(R.drawable.ic_youtube)
            .into(holder.rounded_image)

//        try {
//            holder.checkbox.isChecked = selectId!!.contains(movie.id!!.toInt())
//        } catch (e: Exception) {
//            Log.d("Exception :-", "$e \t ${e.message}")
//        }

        // Handle checkbox selection
//        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
//            if (isChecked) {
//                // Add to selected IDs list if it's not already there
//                if (!selectId!!.contains(movie.id!!.toInt())) {
//                    selectId!!.add(movie.id!!.toInt())
//                    setdata(position)
//                }
//            } else {
//                OnItemClickListener(position, listener, movie.id!!.toLong(), "remove")
//                // Remove from selected IDs list
//                selectId!!.remove(movie.id!!.toInt())
//            }
//        }

//        holder.checkbox.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, b ->
//            if (b.equals(true)) {
////                if (!selectId!!.contains(movie.id!!.toInt())) {
////                    selectId!!.add(movie.id!!.toInt())
////                    setdata(position)
////                }
//                setdata(position)
//            } else {
////                selectId!!.remove(movie.id!!.toInt())
//                OnItemClickListener(position, listener, movie.id!!.toLong(), "remove")
//            }
//        })

        Log.d("LDDMKMMDK", "onBindViewHolder: $type")
        if (type == "edit") {
            try {
                holder.checkbox.setOnCheckedChangeListener(null) // Disable listener to avoid unwanted triggers
                holder.checkbox.isChecked = selectId!!.contains(movie.id)
                holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        // Add to newdata if it's not already present
                        if (!newdata.contains(movie)) {
                            newdata.add(movie)
                        }
                    } else {
                        // Remove from newdata when unchecked
                        newdata.remove(movie)
                    }


                    if (newdata.isEmpty()) {
                        Toast.makeText(context, "Please select at least one exercise.", Toast.LENGTH_SHORT).show()
                        return@setOnCheckedChangeListener

                    }
                    setExerciseData(context, newdata, "Exercise_list")
                }
            } catch (e: Exception) {
                Log.d("Exception", e.message ?: "Unknown error")
            }
        } else if (type == "create") {
            holder.checkbox.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, b ->
                if (b.equals(true)) {
                    setdata(position)
                } else {
//                selectId!!.remove(movie.id!!.toInt())
                    OnItemClickListener(position, listener, movie.id!!.toLong(), "remove")
                }
            })

        }
    }


    // Save the selected data in SharedPreferences
//    private fun setExerciseData(
//        c: Context,
//        model: ArrayList<ExcerciseData.Exercise>?,
//        key: String
//    ) {
//        val appSharedPrefs: SharedPreferences =
//            PreferenceManager.getDefaultSharedPreferences(c.applicationContext)
//        val prefsEditor: SharedPreferences.Editor = appSharedPrefs.edit()
//        val gson = Gson()
//        val jsonObject = gson.toJson(model)
//        prefsEditor.putString(key, jsonObject)
//        prefsEditor.commit()
//    }

    fun getSelectedExercises(): List<ExcerciseData.Exercise> {
        return newdata
    }

    fun resetExerciseList() {
        newdata.clear()
        notifyDataSetChanged() // Update UI
    }

    override fun getItemCount(): Int {
        return splist!!.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvFname: TextView = view.findViewById<View>(R.id.tvFname) as TextView
        var tvgoal: TextView = view.findViewById<View>(R.id.tvgoal) as TextView
        var tvAthlet: TextView = view.findViewById<View>(R.id.tv_athlet) as TextView
        var checkbox: CheckBox = view.findViewById<View>(R.id.checkbox) as CheckBox
        var rounded_image: RoundedImageView =
            view.findViewById<View>(R.id.image) as RoundedImageView
    }

    private fun setdata(position: Int) {

        // Check if the item is already in newdata to avoid duplicates
        val selectedExercise = splist!![position]
        if (!newdata.contains(selectedExercise)) {
            newdata.add(selectedExercise)
        }

        Log.d("New Database :-", "${newdata}")
        for (i in newdata) {
            Log.d("New Database id:-", "${i.id} \t ${i.name}")
        }

        // Save the selected exercises to SharedPreferences
        setExerciseData(context, newdata, "Exercise_list")


//        newdata.add(splist!![position])
//        Log.d("New Database :-", "${newdata}")
//        for (i in newdata) {
//            Log.d("New Database id:-", "${i.id} \t ${i.name}")
//        }
//        setExerciseData(context, newdata, "Exercise_list")

//        var time: String? = "01:00:00"
//        if (splist!![position].cycles!!.size != 0) {
//            time = splist!![position].cycles!![0].time
//        }
//        id.add(
//            Ecercise_data_list(
//                splist!![position].id!!.toString(),
//                splist!![position].coach_id.toString(),
//                splist!![position].name.toString(),
//                splist!![position].goal_id.toString(),
//                time!!,
//                splist!![position].image.toString(),
//            )
//        )
//        SetIdList(context, id, "Exercise")


//        var time1 = "00:00:00"
//        var reps = "0"
//        var weight = "0"
//        if (splist!![position].cycles!!.size != 0) {
//            time1 = splist!![position].cycles!![0].time.toString()
//            reps = splist!![position].cycles!![0].reps.toString()
//            weight = splist!![position].cycles!![0].weight.toString()
//        }
//        data.add(
//            Ecercise_list_data(
//                splist!![position].id!!.toString(),
//                splist!![position].coach_id.toString(),
//                splist!![position].name.toString(),
//                splist!![position].goal_id.toString(),
//                time1,
//                splist!![position].image.toString(),
//                weight,
//                reps
//            )
//        )
//        setDataList(context, data, "Exercise_list")

    }

//    fun SetIdList(c: Context, modal: ArrayList<Ecercise_data_list>?, key: String?) {
//        val appSharedPrefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(
//            c.applicationContext
//        )
//        val prefsEditor: SharedPreferences.Editor = appSharedPrefs.edit()
//        val gson = Gson()
//        val jsonObject = gson.toJson(modal)
//        prefsEditor.putString(key, jsonObject)
//        prefsEditor.commit()
//    }
//
//    fun setDataList(c: Context, modal: ArrayList<Ecercise_list_data>?, key: String?) {
//        val appSharedPrefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(
//            c.applicationContext
//        )
//        val prefsEditor: SharedPreferences.Editor = appSharedPrefs.edit()
//        val gson = Gson()
//        val jsonObject = gson.toJson(modal)
//        prefsEditor.putString(key, jsonObject)
//        prefsEditor.commit()
//    }

    fun setExerciseData(c: Context, model: ArrayList<ExcerciseData.Exercise>?, key: String) {
        val appSharedPrefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(
            c.applicationContext
        )
        val prefsEditor: SharedPreferences.Editor = appSharedPrefs.edit()
        val gson = Gson()
        val jsonObject = gson.toJson(model)
        prefsEditor.putString(key, jsonObject)
        prefsEditor.commit()
    }
}
