package com.example.model

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.trainerapp.MainActivity
import com.example.trainerapp.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessageReceiver : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FirebaseMsgReceiver"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New Firebase Token: $token")
    }

    private fun sendChatUpdateBroadcast() {
        val intent = Intent("com.example.trainerapp.REFRESH_CHAT")
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "Received Notification: $remoteMessage")

        if (remoteMessage.notification != null) {
            val title = remoteMessage.notification!!.title ?: "No Title"
            val body = remoteMessage.notification!!.body ?: "No Body"

            Log.d(TAG, "Notification Title: $title")
            Log.d(TAG, "Notification Body: $body")
            showNotification(title, body)
            sendChatUpdateBroadcast()

        } else {
            Log.d(TAG, "Message does not contain a notification")
        }
    }

    @SuppressLint("RemoteViewLayout")
    private fun getCustomDesign(title: String, message: String): RemoteViews {
        val remoteViews = RemoteViews(packageName, R.layout.notification)
        remoteViews.setTextViewText(R.id.title, title)
        remoteViews.setTextViewText(R.id.message, message)
        remoteViews.setImageViewResource(R.id.icon, R.drawable.app_icon)
        return remoteViews
    }

    private fun showNotification(title: String, message: String) {
        Log.d(TAG, "Showing Notification: $title - $message")

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "trainer_notification_channel"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId, "Trainer Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.app_icon)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(500, 500, 500, 500))
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.setCustomContentView(getCustomDesign(title, message))
        } else {
            builder.setStyle(NotificationCompat.BigTextStyle().bigText(message))
        }

        notificationManager.notify(0, builder.build())
        Log.d(TAG, "Notification Displayed Successfully")
    }
}
