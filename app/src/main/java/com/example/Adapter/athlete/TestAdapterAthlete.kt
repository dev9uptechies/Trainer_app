package com.example

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.model.AthleteDataPackage.AthleteDetails
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.R
import com.example.trainerapp.viewTestActivity

class TestAdapterAthlete(
    private val athleteList: List<AthleteDetails.Athlete.TestAthlete>,
    var context: Context,
    val listener: OnItemClickListener.OnItemClickCallback
) :
    RecyclerView.Adapter<TestAdapterAthlete.MyViewHolder>() {
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TestAdapterAthlete.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.test_layout, parent, false)
        return MyViewHolder(itemView)
    }

    val athleteNames = ArrayList<String>()
    val athleteResults = ArrayList<String>()


    override fun onBindViewHolder(holder: TestAdapterAthlete.MyViewHolder, position: Int) {
        val athlete = athleteList[position]

        if (athlete.test?.title.isNullOrEmpty()){
            holder.tvcard.visibility = View.VISIBLE
        } else {
            holder.tvcard.visibility = View.VISIBLE
        }

        holder.tvdate.visibility = View.VISIBLE
        holder.tvunit.visibility = View.VISIBLE
        holder.tvimage.visibility = View.GONE


        holder.tvFname.text = "Test Name: "+ (athlete.test?.title ?: "")
        holder.tvgoal.text = athlete.test?.goal ?: ""
        holder.tvtime.text = athlete.athlete?.name ?: ""
        holder.tvdate.text = athlete.test?.date?.take(10) ?: ""
        holder.tvunit.text = "Unit: " + (athlete.test?.unit ?: "")


        athlete.test?.test_athletes?.forEach { athleteData ->
            athleteData.athlete?.name?.let { athleteNames.add(it) }
            athleteResults.add(athleteData.result ?: "")
        }


        holder.itemView.setOnClickListener {
            val intent = Intent(context, viewTestActivity::class.java)
            intent.putExtra("AthleteTestName", athlete.test?.title)
            intent.putExtra("AthleteTestGoal", athlete.test?.goal)
            intent.putExtra("From", "AthleteSection")
            intent.putExtra("AthleteTestUnit", athlete.test?.unit)
            intent.putStringArrayListExtra("AthleteTestAthleteNames", athleteNames)
            intent.putStringArrayListExtra("AthleteTestAthleteResults", athleteResults)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return athleteList.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var tvFname: TextView = view.findViewById<View>(R.id.tv_event_name) as TextView
        var tvgoal: TextView = view.findViewById<View>(R.id.tv_event_type) as TextView
        var tvtime: TextView = view.findViewById<View>(R.id.tv_interested_atheletes) as TextView
        var tvdate: TextView = view.findViewById<View>(R.id.tv_event_date) as TextView
        var tvunit: TextView = view.findViewById<View>(R.id.tv_event_unit) as TextView
        var tvimage: ImageView = view.findViewById<View>(R.id.image) as ImageView
        var tvcard: CardView = view.findViewById<View>(R.id.card) as CardView

    }
}
