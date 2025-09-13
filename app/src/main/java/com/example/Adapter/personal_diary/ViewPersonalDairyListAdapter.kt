package com.example.Adapter.personal_diary

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.OnItemClickListener
import com.example.model.personal_diary.GetPersonalDiary
import com.example.trainerapp.R
import com.example.trainerapp.personal_diary.EditPersonalDiaryDataActivity

class ViewPersonalDairyListAdapter(
    private val diaryList: MutableList<GetPersonalDiary.Data>,
    private val context: Context,
    var FromAthlete:Boolean,
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
                putExtra("DiaryId", diaryItem.id)
                putExtra("Date", diaryItem.date)
                putExtra("AthleteId", diaryItem.userId)
                putExtra("FromAthlete", FromAthlete)
            }
            context.startActivity(intent)
        }
    }

    fun updateData(newList: MutableList<GetPersonalDiary.Data>) {
        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize() = diaryList.size
            override fun getNewListSize() = newList.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                diaryList[oldItemPosition].id == newList[newItemPosition].id

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                diaryList[oldItemPosition] == newList[newItemPosition]
        }

        val diffResult = DiffUtil.calculateDiff(diffCallback)

        diaryList.clear()
        diaryList.addAll(newList)

        diffResult.dispatchUpdatesTo(this) // Move this after modifying diaryList
    }


    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val date: TextView = view.findViewById(R.id.training_name_one)
        val sleepHours: TextView = view.findViewById(R.id.start_date_one)
        val card: CardView = view.findViewById(R.id.card_one)
    }
}
