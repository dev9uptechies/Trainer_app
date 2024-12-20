package com.example.Adapter.groups
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
import androidx.recyclerview.widget.RecyclerView
import com.example.GroupListData
import com.example.OnItemClickListener
import com.example.TestActivity
import com.example.trainerapp.R
import com.example.trainerapp.TestListData
import retrofit2.http.GET
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


class SetEditGroupTestAdapter(
    private var splist: ArrayList<GroupListData.GroupTest>?,
    var context: Context,
    val listener: OnItemClickListener.OnItemClickCallback
) : RecyclerView.Adapter<SetEditGroupTestAdapter.MyViewHolder>() {

    private var filterList: ArrayList<GroupListData.GroupTest> = ArrayList()
    private var selectedPosition = -1
    private val selectedItems: MutableSet<Int> = mutableSetOf() // Store selected item positions
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

        // Ensure movie and test are not null
        val test = movie.test
        if (test == null) {
            holder.tvFname.text = "-"
            holder.tv_unit.text = "Unit: N/A"
            holder.tvgoal.text = "Goal: N/A"
            holder.tv_athlet.text = "Interested Athletes: N/A"
            holder.tvDate.text = "Invalid date"
        } else {
            holder.tvFname.text = test.title ?: "-"
            holder.tv_unit.text = "Unit: " + (test.unit ?: "N/A")
            holder.tvgoal.text = "Goal: " + (test.goal ?: "N/A")
            holder.tv_athlet.text = "Interested Athletes: " + (test.test_athletes?.getOrNull(0)?.athlete?.name ?: "N/A")
            holder.tvDate.text = convertDate(test.date ?: "")
        }

        holder.editImage.visibility = View.GONE
        holder.checkBox.visibility = View.GONE

        holder.checkBox.isChecked = selectedItems.contains(position)
        holder.checkBox.isClickable = false

        holder.itemView.setOnClickListener {
            if (selectedItems.contains(position)) {
                selectedItems.remove(position)
            } else {
                selectedItems.add(position)
            }
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int {
        return filterList.size
    }

    fun getSelectedTestData(): List<Int> {
        return selectedItems.map { filterList[it].id ?: 0 }
    }


    fun filter(query: String) {
        filterList = if (query.isEmpty()) {
            ArrayList(splist ?: emptyList())
        } else {
            ArrayList(
                splist?.filter {
                    it.test!!.title!!.contains(query, ignoreCase = true)
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
    }
}
