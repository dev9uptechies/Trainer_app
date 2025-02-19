package com.example

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trainerapp.R
import com.example.trainerapp.viewTestActivity

class FavoritetestAdapter(private var user: ArrayList<LessonData.lessionData>?, var context: Context, val listener: OnItemClickListener.OnItemClickCallback)
    : RecyclerView.Adapter<FavoritetestAdapter.MyViewHolder>()  {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavoritetestAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.favorite_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    val athleteNames = ArrayList<String>()
    val athleteResults = ArrayList<String>()

    override fun onBindViewHolder(holder: FavoritetestAdapter.MyViewHolder, position: Int) {
        val movie = user!![position].test

        holder.date.visibility = View.VISIBLE
        holder.unit.visibility = View.VISIBLE

        holder.total_time.text = "Instrested Athlete:"

        holder.date.text = movie!!.date?.take(10)
        holder.unit.text = "unit: "+movie.unit
        holder.tvFname.text = movie!!.title
        holder.tvgoal.text = movie!!.goal
        holder.tv_athlet.text = movie!!.testAthletes?.getOrNull(0)?.athlete?.name ?: ""

        holder.itemView.setOnClickListener {

            athleteNames.clear()
            athleteResults.clear()

            movie?.testAthletes?.forEach { athleteData ->
                athleteData.athlete?.name?.let { athleteNames.add(it) }
                athleteResults.add((athleteData.result ?: "").toString())
            }

            val intent = Intent(context, viewTestActivity::class.java)
            intent.putExtra("AthleteTestName", movie?.title)
            intent.putExtra("AthleteTestGoal", movie?.goal)
            intent.putExtra("AthleteTestUnit", movie?.unit)
            intent.putStringArrayListExtra("AthleteTestAthleteNames", athleteNames)
            intent.putStringArrayListExtra("AthleteTestAthleteResults", athleteResults)
            context.startActivity(intent)
        }

        holder.image.setImageResource(R.drawable.ic_favorite_select)
        holder.image.setOnClickListener(OnItemClickListener(position, listener, movie.id!!.toLong() , "test"))
    }

    override fun getItemCount(): Int {
        return user!!.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvFname: TextView = view.findViewById<View>(R.id.tvFname) as TextView
        var image: ImageView = view.findViewById<View>(R.id.image) as ImageView
        var date: TextView = view.findViewById<View>(R.id.date) as TextView
        var unit: TextView = view.findViewById<View>(R.id.unit) as TextView
        var tvgoal: TextView = view.findViewById<View>(R.id.tvgoal) as TextView
        var goal: TextView = view.findViewById<View>(R.id.goal) as TextView
        var total_time: TextView = view.findViewById<View>(R.id.total_time) as TextView
        var tv_athlet: TextView = view.findViewById<View>(R.id.tv_athlet) as TextView

    }
}
