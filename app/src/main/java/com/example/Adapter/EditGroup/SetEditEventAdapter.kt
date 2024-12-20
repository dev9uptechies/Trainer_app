package com.example.Adapter.EditGroup
import android.content.Context
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
import com.example.trainerapp.R
import java.text.DateFormat
import java.text.SimpleDateFormat


class SetEditEventAdapter(
    private var splist: ArrayList<GroupListData. GroupEvents>?,
    var context: Context,
    val listener: OnItemClickListener.OnItemClickCallback
) : RecyclerView.Adapter<SetEditEventAdapter.MyViewHolder>() {

    private var selectedPosition = -1
    private var selectedGroupId: String? = null
    private var filterList: ArrayList<GroupListData. GroupEvents> = ArrayList()
    private var selectedDate: String? = null
    private val selectedItems: MutableSet<Int> = mutableSetOf() // Store selected item positions


    init {
        filterList = ArrayList(splist ?: emptyList())
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list_selected_day, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val movie = filterList[position] // Use filterList instead of splist

        holder.editImage.visibility = View.GONE
        holder.checkBox.visibility = View.GONE

        // Safely check and build athlete names
        val athletesNames = StringBuilder()
        val event = movie.event
        if (event?.event_athletes != null) {
            for (eventAthlete in event!!.event_athletes!!) {
                val athleteName = eventAthlete.athlete?.name
                if (!athleteName.isNullOrEmpty()) {
                    if (athletesNames.isNotEmpty()) {
                        athletesNames.append(", ")
                    }
                    athletesNames.append(athleteName)
                }
            }
        }

        holder.tvgoal.text = "Event Type: ${event?.type ?: "N/A"}"
        holder.tvFname.text = event?.title ?: "N/A"
        holder.tv_athlet.text = "Interested Athletes: $athletesNames"

        // Safely parse and format date
        val date = event?.date
        if (!date.isNullOrEmpty()) {
            try {
                val formatter: DateFormat = SimpleDateFormat("yyyy-MM-dd")
                holder.tvDate.text = formatter.format(formatter.parse(date)!!)
            } catch (e: Exception) {
                holder.tvDate.text = "Invalid date"
            }
        } else {
            holder.tvDate.text = "N/A"
        }

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

        holder.editImage.setOnClickListener {
            val athleteNames = event?.event_athletes?.mapNotNull { it.athlete?.name } ?: listOf("Unknown")
            val athleteIdList = event?.event_athletes?.mapNotNull { it.athlete?.id } ?: listOf(0)

            // Perform actions with athleteNames and athleteIdList
        }
    }

    fun getSelectedEventData(): List<Int> {
        return selectedItems.map { filterList[it].id ?: 0 }
    }

    override fun getItemCount(): Int {
        return filterList.size
    }


    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tv_unit: TextView = view.findViewById<View>(R.id.tv_unit) as TextView
        var tvgoal: TextView = view.findViewById<View>(R.id.tvgoal) as TextView
        var tv_athlet: TextView = view.findViewById<View>(R.id.tv_athlet) as TextView
        var tvFname: TextView = view.findViewById<View>(R.id.tvFname) as TextView
        var click: LinearLayout = view.findViewById<View>(R.id.click) as LinearLayout
        var tvDate: TextView = view.findViewById(R.id.tv_date)
        var checkBox: CheckBox = view.findViewById(R.id.myCheckBox)
        var editImage: ImageView = view.findViewById(R.id.img_edit)
    }
}
