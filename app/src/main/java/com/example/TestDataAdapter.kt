package com.example

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.R
import com.example.trainerapp.TestListData
import com.zerobranch.layout.SwipeLayout
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class TestDataAdapter(
    private var splist: ArrayList<TestListData.testData>?,
    var context: Context,
    val listener: OnItemClickListener.OnItemClickCallback
) :
    RecyclerView.Adapter<TestDataAdapter.MyViewHolder>() {
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TestDataAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.facebookdata_format, parent, false)
        return MyViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: TestDataAdapter.MyViewHolder, position: Int) {
        holder.time1.visibility = View.GONE
        val movie = splist!![position]
        apiClient = APIClient(context)
        apiInterface = apiClient.client().create(APIInterface::class.java)

        holder.tvFname.text = movie.title
        holder.tv_unit.text = "Unit: " + movie.unit
        holder.tvgoal.text = "Goal: " + movie.goal
        holder.tv_athlet.text =
            "Interested Athletes: " + movie.data!![0].athlete!!.name
        holder.tvtime.text = convertDate(movie.date!!)
        holder.img_edit.setOnClickListener {
            holder.swipe.close(true)
            listener.onItemClicked(it, position, movie.id!!.toLong(), "Edit")
        }

        holder.img_delete.setOnClickListener {
            holder.swipe.close(true)
            listener.onItemClicked(it, position, movie.id!!.toLong(), "Delete")
        }
//        holder.img_edit.setOnClickListener(
//            OnItemClickListener(
//                position,
//                listener,
//                movie.id!!.toLong(),
//                "Edit"
//            )
//        )
//        holder.img_delete.setOnClickListener(
//            OnItemClickListener(
//                position,
//                listener,
//                movie.id!!.toLong(),
//                "Delete"
//            )
//        )
        if (movie.is_favourite!!.equals(1)) {
            holder.image.setImageResource(R.drawable.ic_favorite_select)

        } else {
            holder.image.setImageResource(R.drawable.ic_favorite_red)
        }
        holder.image.setOnClickListener(
            if (movie.is_favourite!!.equals(1)) {
                OnItemClickListener(position, listener, movie.id!!.toLong(), "unfav")
            } else {
                OnItemClickListener(position, listener, movie.id!!.toLong(), "fav")
            }
        )
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

        var tvFname: TextView = view.findViewById<View>(R.id.tvFname) as TextView
        var tvgoal: TextView = view.findViewById<View>(R.id.tvgoal) as TextView
        var tvtime: TextView = view.findViewById<View>(R.id.tv_time) as TextView
        var tv_unit: TextView = view.findViewById<View>(R.id.tv_unit) as TextView
        var tv_athlet: TextView = view.findViewById<View>(R.id.tv_athlet) as TextView
        var img_delete: ImageView = view.findViewById<View>(R.id.img_delete) as ImageView
        var img_edit: ImageView = view.findViewById<View>(R.id.img_edit) as ImageView
        var image: ImageView = view.findViewById<View>(R.id.image) as ImageView
        var swipe: SwipeLayout = view.findViewById(R.id.swipe_layout)
        var time1: TextView = view.findViewById(R.id.tv_time1)
    }
}
