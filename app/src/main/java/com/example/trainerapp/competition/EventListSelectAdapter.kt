package com.example.trainerapp.competition

import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.OnItemClickListener
import com.example.trainerapp.ApiClass.EventListData
import com.example.trainerapp.PreferencesManager
import com.example.trainerapp.R
import com.google.gson.Gson
import java.text.DateFormat
import java.text.SimpleDateFormat

class EventListSelectAdapter(
    private var splist: ArrayList<EventListData.testData>?,
    var context: Context,
    val listener: OnItemClickListener.OnItemClickCallback,
) :
    RecyclerView.Adapter<EventListSelectAdapter.MyViewHolder>() {
    private var selectedPosition = -1
    var time: Int? = null
    var preferenceManager: PreferencesManager = PreferencesManager(context)
    var id_list: ArrayList<Int> = ArrayList()
    var name_list: ArrayList<String> = ArrayList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EventListSelectAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.event_select_list, parent, false)
        return MyViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: EventListSelectAdapter.MyViewHolder, position: Int) {

        val formatter: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        val movie = splist!![position]
        holder.tvEventName.text = movie.title
        holder.tvEventType.text = movie.type
        holder.tvEventDate.text = formatter.format(formatter.parse(movie.date!!)!!)

        val athletesNames = StringBuilder()

        for (eventAthlete in movie.event_athletes!!) {
            val athleteName = eventAthlete.athlete?.name
            if (athleteName != null) {
                if (athletesNames.isNotEmpty()) {
                    athletesNames.append(", ") // Add a comma to separate names
                }
                athletesNames.append(athleteName)
            }
        }
        holder.tvIntAthlete.text = athletesNames.toString()

        holder.checkbox.isChecked = selectedPosition == position

        val colorStateList = if (position == selectedPosition) {
            ColorStateList.valueOf(Color.RED)
        } else {
            ColorStateList.valueOf(Color.WHITE)
        }
        holder.checkbox.buttonTintList = colorStateList

        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Update selected position and notify changes
                id_list.clear()
                name_list.clear()
                val previousPosition = selectedPosition
                selectedPosition = holder.adapterPosition
                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)
                setData(movie.id!!, "add", movie.title!!)
                if (!id_list.isEmpty()) {
                    preferenceManager.setSelectEvent(true)
                }
            } else {
                OnItemClickListener(position, listener, movie.id!!.toLong(), "remove")
            }
        }

    }

    private fun setData(id: Int, status: String, name: String) {
        if (status == "add") {
            id_list.add(id)
            name_list.add(name)

        } else {
            id_list.remove(id)
            name_list.remove(name)
        }
        SetIdList(context, id_list, "setEventId")
        SetnameList(context, name_list, "setEventName")
        Log.d("jenis", "" + id_list.size.toString())
    }

    fun SetIdList(c: Context, modal: ArrayList<Int>?, key: String?) {
        val appSharedPrefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(
            c.applicationContext
        )
        val prefsEditor: SharedPreferences.Editor = appSharedPrefs.edit()
        val gson = Gson()
        val jsonObject = gson.toJson(modal)
        Log.d("jenis Id :-", jsonObject)
        prefsEditor.putString(key, jsonObject)
        prefsEditor.apply()
    }


    fun SetnameList(c: Context, modal: ArrayList<String>?, key: String?) {
        val appSharedPrefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(
            c.applicationContext
        )
        val prefsEditor: SharedPreferences.Editor = appSharedPrefs.edit()
        val gson = Gson()
        val jsonObject = gson.toJson(modal)
        prefsEditor.putString(key, jsonObject)
        prefsEditor.apply()
    }


    override fun getItemCount(): Int {
        return splist!!.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvEventName: TextView = view.findViewById<View>(R.id.tv_event_name) as TextView
        var tvEventDate: TextView = view.findViewById<View>(R.id.tv_event_date) as TextView
        var tvEventType: TextView = view.findViewById<View>(R.id.event_type) as TextView
        var tvIntAthlete: TextView = view.findViewById<View>(R.id.int_athlete) as TextView
        var checkbox: CheckBox = view.findViewById<View>(R.id.checkbox) as CheckBox
    }

}
