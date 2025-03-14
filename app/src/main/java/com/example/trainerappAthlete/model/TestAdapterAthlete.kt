package com.example.trainerappAthlete.model

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.GroupListData
import com.example.OnItemClickListener
import com.example.trainerapp.R
import com.example.trainerapp.viewTestActivity


class TestAdapterAthlete(
    private var data: ArrayList<GroupListAthlete.GroupTest>?,
    private val context: Context,
    private val listener: OnItemClickListener.OnItemClickCallback,
    val screen:String

) : RecyclerView.Adapter<TestAdapterAthlete.MyViewHolder>() {

    val athleteNames = ArrayList<String>()
    val athleteResults = ArrayList<String>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.lession_list_item, parent, false)
        return MyViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val movie = data?.get(position) ?: return // Safely handle null data
        val movie2 = data?.get(position)!!.test ?: return // Safely handle null data

        holder.unit.visibility = View.VISIBLE
        holder.unitedt.visibility = View.VISIBLE

        Log.d("KKSKSKSKSK", "onBindViewHolder: ${movie.test?.goal}")
        Log.d("KKSKSKSKSK", "onBindViewHolder: ${movie.test?.test_athletes?.get(0)?.athlete?.id}")
        Log.d("KKSKSKSKSK", "onBindViewHolder: ${movie.test?.test_athletes?.get(0)?.athlete?.name}")

        try {
            holder.name.text = movie.test!!.title ?: "-"
            holder.goal.text = movie.test?.goal.toString() ?: ""
            holder.date.text = movie.test?.date?.take(10) ?: ""
            holder.unitedt.text = movie.test!!.unit ?: ""

        }catch (e:Exception){
            Log.e("Error","ERror:-  ${e.message.toString()}")
        }


        holder.itemView.setOnClickListener(OnItemClickListener(position, listener, movie.id?.toLong() ?: 0L, "test"))




        movie.test?.test_athletes?.forEach { athleteData ->
            athleteData.athlete?.name?.let { athleteNames.add(it) }
            athleteResults.add(athleteData.result ?: "")
        }

        if(screen == "GroupDetailsScreen") {
            holder.itemView.setOnClickListener {
                val intent = Intent(context, viewTestActivity::class.java)
                intent.putExtra("AthleteTestName", movie.test?.title)
                intent.putExtra("AthleteTestGoal", movie.test?.goal)
                intent.putExtra("AthleteTestUnit", movie.test?.unit)
                intent.putStringArrayListExtra("AthleteTestAthleteNames", athleteNames)
                intent.putStringArrayListExtra("AthleteTestAthleteResults", athleteResults)
                context.startActivity(intent)
            }
        }

        if (movie2.is_favourite!! == 1) {
            holder.image.setImageResource(R.drawable.ic_favorite_select)
        } else {
            holder.image.setImageResource(R.drawable.ic_favorite_red)
        }

        holder.image.setOnClickListener(
            if (movie2.is_favourite == 1) {
                OnItemClickListener(position, listener, movie2.id!!.toLong(), "unfavtest")
            } else {
                OnItemClickListener(position, listener, movie2.id!!.toLong(), "favtest")
            }
        )
    }

    override fun getItemCount(): Int = data!!.size

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_program_name)
        val goal: TextView = view.findViewById(R.id.tv_edt_goal)
        val totaltime: TextView = view.findViewById(R.id.tv_edit_total_time)
        val date: TextView = view.findViewById(R.id.edt_date)
        val unit: TextView = view.findViewById(R.id.Unit)
        val unitedt: TextView = view.findViewById(R.id.tv_edit_Unit)
        val image: ImageView = view.findViewById(R.id.image)
    }
}
