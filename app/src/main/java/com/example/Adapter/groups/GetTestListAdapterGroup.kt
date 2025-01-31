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
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.OnItemClickListener
import com.example.TestActivity
import com.example.trainerapp.R
import com.example.trainerapp.TestListData
import com.zerobranch.layout.SwipeLayout
import retrofit2.http.GET
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


class GetTestListAdapterGroup(
    private var splist: ArrayList<TestListData.testData>?,
    var context: Context,
    val listener: OnItemClickListener.OnItemClickCallback
) : RecyclerView.Adapter<GetTestListAdapterGroup.MyViewHolder>() {

    private var filterList: ArrayList<TestListData.testData> = ArrayList()
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
        holder.editImage.visibility = View.GONE

        holder.tvFname.text = movie.title
        holder.tv_unit.text = "Unit: " +  movie.unit
        holder.tvgoal.text = "Goal: " + (movie.goal ?: "N/A")
        holder.tv_athlet.text = "Interested Athletes: " + (movie.data?.getOrNull(0)?.athlete?.name ?: "N/A")
        holder.tvDate.text = convertDate(movie.date ?: "")

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


        holder.cardView.setOnClickListener {
            if (selectedItems.contains(position)) {
                selectedItems.remove(position)
            } else {
                selectedItems.add(position)
            }
            notifyItemChanged(position)

        }
        holder.editImage2.setOnClickListener {
            val testId = movie.data?.getOrNull(0)?.test_id ?: ""
            val athleteIds = movie.data?.map { it.athlete!!.id } ?: emptyList()
            val athleteNames = movie.data?.map { it.athlete!!.name } ?: emptyList()

            Log.d("SDSDSDSDDS", "onBindViewHolder: "+movie.data!!.get(0).athlete!!.id)

            val intent = Intent(context, TestActivity::class.java)
            intent.putExtra("TestIdGroup", testId.toInt())
            intent.putExtra("TestPositionGroup", position)
            context.startActivity(intent)
        }


        holder.img_delete.setOnClickListener {
            holder.swipe.close(true)
            listener.onItemClicked(it, position, movie.id!!.toLong(), "DeleteTest")
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
        var editImage2: ImageView = view.findViewById(R.id.img_edit2)
        val swipe = view.findViewById<SwipeLayout>(R.id.swipe_layout)
        var img_delete: ImageView = view.findViewById(R.id.img_delete)
        var cardView: CardView = view.findViewById<View>(R.id.rela_dragged) as CardView

    }
}
