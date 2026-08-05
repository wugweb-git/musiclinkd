package com.example.onboarding.messaging

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.onboarding.database.FirebaseManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class AppFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM Registration Token generated: $token")
        FirebaseManager.updateFcmToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "From FCM: ${remoteMessage.from}")

        // Check if message contains a notification payload
        val title = remoteMessage.notification?.title ?: remoteMessage.data["title"] ?: "New MusicLinkd Alert"
        val body = remoteMessage.notification?.body ?: remoteMessage.data["body"] ?: "You have a new update in MusicLinkd."
        val channelId = remoteMessage.data["channel"] ?: AppNotificationManager.CHANNEL_CONNECTIONS

        AppNotificationManager.handleIncomingFcmMessage(this, title, body, channelId)
    }

    private fun showSystemNotification(title: String, messageText: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "musiclinkd_fcm_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Push Notifications & Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channels for FCM push alerts, studio bookings, and chat messages"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(messageText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val notificationId = (System.currentTimeMillis() % 10000).toInt()
        notificationManager.notify(notificationId, builder.build())
    }

    companion object {
        private const val TAG = "AppFirebaseMessaging"
    }
}
