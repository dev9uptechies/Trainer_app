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
import com.example.model.AthleteDataPackage.AthltepersonaldiaryModel
import com.example.model.AthleteDataPackage.Datas
import com.example.model.notification.NotificationModel
import com.example.model.personal_diary.GetPersonalDiary
import com.example.trainerapp.R
import com.example.trainerapp.personal_diary.EditPersonalDiaryDataActivity
class NotificatioAdapter(
    private val notificationList: MutableList<NotificationModel.Data>,
    private val context: Context,
    private val listener: OnItemClickListener.OnItemClickCallback
) : RecyclerView.Adapter<NotificatioAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int = notificationList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val diaryItem = notificationList[position]

        holder.name.text = diaryItem.title
        holder.message.text = diaryItem.message
        holder.status.text = diaryItem.status.toString()
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.training_name)
        val message: TextView = view.findViewById(R.id.start_date)
        val status: TextView = view.findViewById(R.id.end_date)
    }
}
