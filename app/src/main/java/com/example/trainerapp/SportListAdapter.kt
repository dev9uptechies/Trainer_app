package com.example.trainerapp

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.trainerapp.ApiClass.SportlistData
import com.google.gson.Gson

class SportListAdapter(
    private var data: ArrayList<SportlistData.sportlist>?, private var idList: ArrayList<Id_list>,private var splist: ArrayList<Sport_list>,
    var context: Context


) : RecyclerView.Adapter<SportListAdapter.MyViewHolder>() {
    private lateinit var list: ArrayList<String>
    private lateinit var id_list: ArrayList<Int>
    lateinit var Sportlist: ArrayList<Sport_list>
    lateinit var preferenceManager: PreferencesManager
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SportListAdapter.MyViewHolder {
        list = ArrayList()
        id_list = ArrayList()
        Sportlist = ArrayList()

        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sport_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SportListAdapter.MyViewHolder, position: Int) {
        val movie = data!![position]

        preferenceManager = PreferencesManager(context)
        holder.et_name.text = movie.title

        for (i in 0 until idList.size) {
            if (movie.id == idList[i].Sport_id) {
                holder.checkbox.isChecked = true
            }
            setdata(splist[i].Sport_title,idList[i].Sport_id,"add")

        }

        holder.checkbox.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, b ->
            if (b.equals(true)) {
                setdata(movie.title!!, movie.id!!, "add")
                if (!list.isEmpty()) {
                    preferenceManager.setselectsport(true)
                }
            } else {
                setdata(movie.title!!, movie.id!!, "remove")
            }


        })


    }

    private fun setdata(title: String, id: Int, status: String) {
        if (status == "add") {
            list.add(title)
            id_list.add(id)
        } else {
            id_list.remove(id)
            list.remove(title)
        }

        setPreferenceObject(context, list, "Set")
        SetIdList(context, id_list, "setid")
    }

    override fun getItemCount(): Int {
        return data!!.size
    }


    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var et_name: TextView = view.findViewById<View>(R.id.text_sport) as TextView
        var checkbox: CheckBox = view.findViewById<View>(R.id.checkbox) as CheckBox
    }


    fun setPreferenceObject(c: Context, modal: ArrayList<String>?, key: String?) {
        /*** storing object in preferences   */
        val appSharedPrefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(
            c.applicationContext
        )
        val prefsEditor: SharedPreferences.Editor = appSharedPrefs.edit()
        val gson = Gson()
        val jsonObject = gson.toJson(modal)
        prefsEditor.putString(key, jsonObject)
        prefsEditor.commit()
    }


    fun SetIdList(c: Context, modal: ArrayList<Int>?, key: String?) {
        /*** storing object in preferences   */
        val appSharedPrefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(
            c.applicationContext
        )
        val prefsEditor: SharedPreferences.Editor = appSharedPrefs.edit()
        val gson = Gson()
        val jsonObject = gson.toJson(modal)
        prefsEditor.putString(key, jsonObject)
        prefsEditor.commit()
    }
}
