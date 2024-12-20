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
import com.example.Create_Event_Activity
import com.example.OnItemClickListener
import com.example.TestActivity
import com.example.model.SelectedDaysModel
import com.example.trainerapp.ApiClass.EventListData
import com.example.trainerapp.R
import com.example.trainerapp.TestListData
import retrofit2.http.GET
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


class GetEventListAdapterGroup(
    private var splist: ArrayList<EventListData.testData>?,
    var context: Context,
    val listener: OnItemClickListener.OnItemClickCallback
) : RecyclerView.Adapter<GetEventListAdapterGroup.MyViewHolder>() {

    private var selectedPosition = -1
    private var selectedGroupId: String? = null
    private var filterList: ArrayList<EventListData.testData> = ArrayList()
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

        val athletesNames = StringBuilder()

        for (eventAthlete in movie.event_athletes!!) {
            val athleteName = eventAthlete.athlete?.name
            if (athleteName != null) {
                if (athletesNames.isNotEmpty()) {
                    athletesNames.append(", ")
                }
                athletesNames.append(athleteName)
            }
        }

        holder.tvgoal.text = "Event Type: " + movie.type
        holder.tvFname.text = movie.title ?: ""
        holder.tv_athlet.text = "Interested Athletes: $athletesNames"

        val formatter: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        holder.tvDate.text = formatter.format(formatter.parse(movie.date!!)!!)


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


        Log.d("DHDHDHDH", "onBindViewHolder: $movie.event_athletes!!.get(0).athlete!!.name")

        holder.editImage.setOnClickListener {

            val athleteNames = arrayListOf<String>()
            movie.event_athletes?.forEach { athlete ->
                athleteNames.add(athlete.athlete?.name ?: "Unknown")
            }


            val athleteIdList = arrayListOf<Int>()

            movie.event_athletes?.forEach { athlete ->
                athleteIdList.add(athlete.athlete?.id ?: 0)
            }

            val intent = Intent(context, Create_Event_Activity::class.java)
            intent.putExtra("id", movie.id)
            intent.putExtra("name", movie.title)
            intent.putExtra("typed", movie.type)
            intent.putExtra("date", movie.date)
            intent.putExtra("fromday", true)
            intent.putExtra("athlete", athleteNames)
            intent.putExtra("athleteId", athleteIdList)
            context.startActivity(intent)
        }


    }

    fun getSelectedEventData(): List<Int> {
        return selectedItems.map { filterList[it].id ?: 0 }
    }

    override fun getItemCount(): Int {
        return filterList.size
    }

    fun filter(query: String) {
        filterList = if (query.isEmpty()) {
            ArrayList(splist ?: emptyList())
        } else {
            ArrayList(
                splist?.filter {
                    it.title?.contains(query, ignoreCase = true) == true ||
                            it.type?.contains(query, ignoreCase = true) == true ||
                            it.event_athletes?.any { athlete ->
                                athlete.athlete?.name?.contains(query, ignoreCase = true) == true
                            } == true
                } ?: emptyList()
            )
        }
        notifyDataSetChanged()
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
