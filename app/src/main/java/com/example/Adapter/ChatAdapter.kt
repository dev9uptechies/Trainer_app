package com.example.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.model.ChatMessage
import com.example.trainerapp.PreferencesManager
import com.example.trainerapp.R
import com.google.android.material.imageview.ShapeableImageView
import de.hdodenhof.circleimageview.CircleImageView

class ChatAdapter(private val context: Context, private val chatList: List<ChatMessage>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val preferenceManager: PreferencesManager = PreferencesManager(context)
    private val userId: String? = preferenceManager.getUserId()

    companion object {
        private const val VIEW_TYPE_SENDER = 1
        private const val VIEW_TYPE_RECEIVER = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (chatList[position].senderId == userId) VIEW_TYPE_SENDER else VIEW_TYPE_RECEIVER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENDER) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_sender, parent, false)
            SenderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_receiver, parent, false)
            ReceiverViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chatMessage = chatList[position]
        if (holder is SenderViewHolder) {
            holder.messageTextView.text = chatMessage.message
        } else if (holder is ReceiverViewHolder) {
            holder.messageTextView.text = chatMessage.message
            holder.userNameTextView.text = chatMessage.senderName
            Glide.with(context).load("https://4trainersapp.com"+chatMessage.senderImage).placeholder(R.drawable.app_icon)
                .into(holder.userImageView)
        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    class SenderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageTextView: TextView = itemView.findViewById(R.id.tvSenderMessage)
    }

    class ReceiverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageTextView: TextView = itemView.findViewById(R.id.tvReceiverMessage)
        val userImageView: CircleImageView = itemView.findViewById(R.id.ivReceiverImage)
        val userNameTextView: TextView = itemView.findViewById(R.id.tvReceiverName)
    }
}
