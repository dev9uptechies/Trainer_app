package com.example.Adapter.personal_diary

import android.content.Context
import android.content.Intent
import android.media.Image
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.OnItemClickListener
import com.example.model.AthleteDataPackage.AthltepersonaldiaryModel
import com.example.model.AthleteDataPackage.Datas
import com.example.model.notification.NotificationData
import com.example.model.notification.NotificationModel
import com.example.model.personal_diary.GetPersonalDiary
import com.example.trainerapp.R
import com.example.trainerapp.personal_diary.EditPersonalDiaryDataActivity
class ReadNotificatioAdapter(
    private val notificationList: MutableList<NotificationModel.Data>,
    private val context: Context,
    private val listener: OnItemClickListener.OnItemClickCallback
) : RecyclerView.Adapter<ReadNotificatioAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int = notificationList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val notificationItem = notificationList[position]

        holder.name.text = notificationItem.title
        holder.message.text = notificationItem.message
        holder.status.text = notificationItem.status.toString()

        Log.d("notificationItem", "onBindViewHolder: $notificationItem")
        Log.d("notificationItem", "onBindViewHolder: ${notificationList.size}")

        holder.img_delete.setOnClickListener {
            Log.d("AKAKAKAKAKKA", "onBindViewHolder: OKOKOKO")
            listener.onItemClicked(it, position, notificationItem.id.toLong(), "Delete")
        }
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.training_name)
        val message: TextView = view.findViewById(R.id.start_date)
        val status: TextView = view.findViewById(R.id.end_date)
        val img_delete: ImageView = view.findViewById(R.id.img_copy)
    }
}
