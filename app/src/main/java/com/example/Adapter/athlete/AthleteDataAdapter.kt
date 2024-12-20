package com.example

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.model.AthleteDataPackage.AthleteDatas
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class AthleteDataAdapter(
    private var splist: List<AthleteDatas.AthleteList>?,
    var context: Context,
    val listener: OnItemClickListener.OnItemClickCallback
) :
    RecyclerView.Adapter<AthleteDataAdapter.MyViewHolder>() {
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AthleteDataAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_athlete_state, parent, false)
        return MyViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: AthleteDataAdapter.MyViewHolder, position: Int) {
        val movie = splist!![position]

        holder.date.text = movie.date
        holder.weight.text =  movie.weight
        holder.fat.text =  movie.fatData
        holder.baseline.text = movie.baseline

    }

    fun convertDate(inputDate: String): String {
        // Define the input format
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")

        // Parse the input date
        val date: Date? = inputFormat.parse(inputDate)

        // Define the output format
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // Format the parsed date into the desired output format
        return date?.let { outputFormat.format(it) } ?: "Invalid date"
    }


    override fun getItemCount(): Int {
        return splist!!.size
    }


    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var date: TextView = view.findViewById<View>(R.id.tv_program_name) as TextView
        var weight: TextView = view.findViewById<View>(R.id.tv_edt_goal) as TextView
        var fat: TextView = view.findViewById<View>(R.id.tv_edt_fat) as TextView
        var baseline: TextView = view.findViewById<View>(R.id.tv_edit_total_time) as TextView



    }
}
