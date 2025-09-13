package com.example.Adapter.selected_day

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.OnItemClickListener
import com.example.TestActivity
import com.example.model.newClass.lesson.Lesson
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.R
import com.example.trainerapp.TestListData
import com.zerobranch.layout.SwipeLayout
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


class AddSelectedDayTestAdapter(
    private var splist: ArrayList<TestListData.testData>?,
    var context: Context,
    val listener: OnItemClickListener.OnItemClickCallback
) : RecyclerView.Adapter<AddSelectedDayTestAdapter.MyViewHolder>() {

    private var filterList: ArrayList<TestListData.testData> = ArrayList()
    private var selectedPosition = -1
    private var selectedGroupId: String? = null
    private var selectedDate: String? = null  // Store the selected date

    init {
        filterList = ArrayList(splist ?: emptyList())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list_selected_day, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val movie = filterList[position]

        holder.tvFname.text = movie.title
        holder.tv_unit.text = "Unit: " +  movie.unit
        holder.tvgoal.text = "Goal: " + (movie.goal ?: "N/A")
        holder.tv_athlet.text = "Interested Athletes: " + (movie.data?.getOrNull(0)?.athlete?.name ?: "N/A")
        holder.tvDate.text = convertDate(movie.date ?: "")

        holder.checkBox.isChecked = position == selectedPosition
        holder.checkBox.isClickable = false

        holder.card.setOnClickListener {
            val currentPosition = holder.adapterPosition
            if (selectedPosition != currentPosition) {
                val previousSelectedPosition = selectedPosition
                selectedPosition = currentPosition
                selectedGroupId = movie.id?.toString()
                selectedDate = movie.date // Store the selected date

                holder.itemView.post {
                    notifyItemChanged(previousSelectedPosition)
                    notifyItemChanged(selectedPosition)
                }
            }
        }

        holder.editImage.setOnClickListener {
            val testId = movie.data?.getOrNull(0)?.test_id ?: ""
            val athleteIds = movie.data?.map { it.athlete!!.id } ?: emptyList()
            val athleteNames = movie.data?.map { it.athlete!!.name } ?: emptyList()

                Log.d("SDSDSDSDDS", "onBindViewHolder: "+movie.data!!.get(0).athlete!!.id)

                val intent = Intent(context, TestActivity::class.java)
                intent.putExtra("id", testId)
                intent.putExtra("fromday", true)
                intent.putExtra("name",movie.title)
                intent.putExtra("date",movie.date)
                intent.putExtra("goal",movie.goal)
                intent.putExtra("unit",movie.unit)
                intent.putExtra("athletename1",movie.data!!.get(0).athlete!!.name)
                intent.putIntegerArrayListExtra("athleteid", ArrayList(athleteIds))
                intent.putStringArrayListExtra("athletename", ArrayList(athleteNames))
                context.startActivity(intent)
        }


    }

    override fun getItemCount(): Int {
        return filterList.size
    }

    fun getSelectedTestData(): Pair<String?, String?> {
        return Pair(selectedGroupId, selectedDate)
    }

    fun filter(query: String) {
        filterList = if (query.isEmpty()) {
            ArrayList(splist ?: emptyList())
        } else {
            ArrayList(
                splist?.filter {
                    it.title!!.contains(query, ignoreCase = true)
                } ?: emptyList()
            )
        }
        notifyDataSetChanged()
    }

    fun convertDate(inputDate: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date: Date? = inputFormat.parse(inputDate)
            val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            date?.let { outputFormat.format(it) } ?: "Invalid date"
        } catch (e: Exception) {
            "Invalid date"
        }
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tv_unit: TextView = view.findViewById(R.id.tv_unit)
        var tvgoal: TextView = view.findViewById(R.id.tvgoal)
        var tv_athlet: TextView = view.findViewById(R.id.tv_athlet)
        var tvFname: TextView = view.findViewById(R.id.tvFname)
        var click: LinearLayout = view.findViewById(R.id.click)
        var tvDate: TextView = view.findViewById(R.id.tv_date)
        var checkBox: CheckBox = view.findViewById(R.id.myCheckBox)
        var editImage: ImageView = view.findViewById(R.id.img_edit)
        var swipe: SwipeLayout = view.findViewById(R.id.swipe_layout)
        var card: CardView = view.findViewById(R.id.rela_dragged)

    }
}
