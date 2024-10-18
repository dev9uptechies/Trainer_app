package com.example.Adapter.template

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.OnItemClickListener
import com.example.model.performance_profile.PerformanceProfileData
import com.example.trainerapp.PreferencesManager
import com.example.trainerapp.R
import com.google.gson.Gson

class ProfileTemplateAdapter(
    private var splist: MutableList<PerformanceProfileData.PerformanceProfile>,
    var context: Context,
    val listener: OnItemClickListener.OnItemClickCallback,
    val checkListener: OnCheckboxCheckedListener
) :
    RecyclerView.Adapter<ProfileTemplateAdapter.MyViewHolder>() {

    private var selectedPosition = -1
    var preferenceManager: PreferencesManager = PreferencesManager(context)
    var id_list: ArrayList<Int> = ArrayList()
    //var checkboxCheckedListener: OnCheckboxCheckedListener? = null

    interface OnCheckboxCheckedListener {
        fun onItemChecked(id: Long, name: String, position: Int)
    }


    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleName: TextView = view.findViewById(R.id.titleText)
        val viewImage: ImageView = view.findViewById(R.id.imgView)
        val editImage: ImageView = view.findViewById(R.id.imgEdit)
        val deleteImage: ImageView = view.findViewById(R.id.imgDelete)
        val checkBox: CheckBox = view.findViewById(R.id.checkBox)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProfileTemplateAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_template_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProfileTemplateAdapter.MyViewHolder, position: Int) {
        val movie = splist[position]
        holder.titleName.text = movie.name ?: ""
        holder.checkBox.isChecked = selectedPosition == position
        holder.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                val previousPosition = selectedPosition
                selectedPosition = holder.adapterPosition

                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)

                checkListener.onItemChecked(
                    movie.id!!.toLong(),
                    movie.name ?: "",
                    selectedPosition
                )
            } else if (selectedPosition == holder.bindingAdapterPosition) {
                selectedPosition = -1
            }
        }
        holder.viewImage.setOnClickListener(
            OnItemClickListener(
                position,
                listener,
                movie.id!!.toLong(),
                "View"
            )
        )

        holder.deleteImage.setOnClickListener(
            OnItemClickListener(
                position,
                listener,
                movie.id!!.toLong(),
                "Delete"
            )
        )

        holder.editImage.setOnClickListener(
            OnItemClickListener(
                position,
                listener,
                movie.id!!.toLong(),
                "Edit"
            )
        )
    }

    override fun getItemCount(): Int {
        return splist.size
    }

    private fun setData(id: Int, status: String) {
        if (status == "add") {
            id_list.add(id)
        } else {
            id_list.remove(id)
        }
        SetIdList(context, id_list, "setTempIds")
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
}