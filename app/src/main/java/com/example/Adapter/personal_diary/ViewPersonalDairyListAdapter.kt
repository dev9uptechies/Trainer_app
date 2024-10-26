package com.example.Adapter.personal_diary

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.OnItemClickListener
import com.example.model.personal_diary.GetPersonalDiary
import com.example.trainerapp.R
import com.example.trainerapp.personal_diary.EditPersonalDiaryDataActivity

class ViewPersonalDairyListAdapter(
    private val diaryList: MutableList<GetPersonalDiary.Data>,
    private val context: Context,
    private val listener: OnItemClickListener.OnItemClickCallback
) : RecyclerView.Adapter<ViewPersonalDairyListAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.viewpersonaldiarylist, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int = diaryList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val diaryItem = diaryList[position]
        holder.date.text = diaryItem.date
        holder.sleepHours.text = "SleepHours - ${diaryItem.sleepHours ?: "00:00:00"}"

        Log.d("Date","${diaryItem.date}")
        holder.card.setOnClickListener {
            val intent = Intent(context, EditPersonalDiaryDataActivity::class.java).apply {
                putExtra("Date", diaryItem.date)
            }
            context.startActivity(intent)
        }
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val date: TextView = view.findViewById(R.id.training_name_one)
        val sleepHours: TextView = view.findViewById(R.id.start_date_one)
        val card: CardView = view.findViewById(R.id.card_one)
    }
}
