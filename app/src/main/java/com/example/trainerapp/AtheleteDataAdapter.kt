package com.example.trainerapp

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
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
import com.example.trainerapp.ApiClass.AthleteDatalist
import com.google.gson.Gson

class AtheleteDataAdapter(
    private var splist: ArrayList<AthleteDatalist.AtheleteList>?,
    var context: Context
) :
    RecyclerView.Adapter<AtheleteDataAdapter.MyViewHolder>() {
    lateinit var preferenceManager: PreferencesManager
    private lateinit var id_list: ArrayList<Int>
    private lateinit var name_list: ArrayList<String>
    var isSelectAll = false

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AtheleteDataAdapter.MyViewHolder {
        id_list = ArrayList()
        name_list = ArrayList()
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list_athletes, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AtheleteDataAdapter.MyViewHolder, position: Int) {
        val movie = splist!![position]
        preferenceManager = PreferencesManager(context)
        holder.tvAthleteName.text = movie.name
        holder.cbAthleteName.isChecked = isSelectAll
        holder.cbAthleteName.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, b ->
            if (holder.cbAthleteName.isChecked) {
                setdata(movie.id!!, "add", movie.name!!)
                if (!id_list.isEmpty()) {
                    preferenceManager.setselectAthelete(true)
                }
            } else {
                setdata(movie.id!!, "remove", movie.name!!)
            }

        })
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

        if (isSelectAll) {
            for (i in 0 until splist!!.size) {
                id_list.add(splist!![i].id!!)
                name_list.add(splist!![i].name!!)
            }
            SetIdList(context, id_list, "setAthlete")
            SetnameList(context, name_list, "setAthleteName")
        } else {
            for (i in 0 until splist!!.size) {
                id_list.remove(splist!![i].id!!)

                name_list.remove(splist!![i].name!!)
            }
            SetIdList(context, id_list, "setAthlete")
            SetnameList(context, name_list, "setAthleteName")
        }

        notifyDataSetChanged()
    }

}
