package com.example.onboarding.messaging

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat

object AppNotificationManager {
    private const val TAG = "AppNotificationManager"
    const val CHANNEL_CONNECTIONS = "channel_connections"
    const val CHANNEL_PROFILE_UPDATES = "channel_profile_updates"
    const val CHANNEL_MESSAGING = "channel_messaging"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val connectionsChannel = NotificationChannel(
                CHANNEL_CONNECTIONS,
                "Connection Requests",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts for new professional connection requests on MusicLinkd"
            }

            val updatesChannel = NotificationChannel(
                CHANNEL_PROFILE_UPDATES,
                "Profile & Network Updates",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for profile updates and portfolio releases"
            }

            val messagingChannel = NotificationChannel(
                CHANNEL_MESSAGING,
                "Direct Messages & Sessions",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts for new chat messages and studio session bookings"
            }

            notificationManager.createNotificationChannel(connectionsChannel)
            notificationManager.createNotificationChannel(updatesChannel)
            notificationManager.createNotificationChannel(messagingChannel)
            Log.d(TAG, "Notification channels initialized.")
        }
    }

    fun showConnectionRequestNotification(
        context: Context,
        senderName: String,
        message: String = "sent you a connection request on MusicLinkd."
    ) {
        val title = "🤝 New Connection Request"
        val body = "$senderName $message"
        dispatchNotification(context, CHANNEL_CONNECTIONS, title, body, notificationId = (System.currentTimeMillis() % 10000).toInt())
    }

    fun showProfileUpdateNotification(
        context: Context,
        artistName: String,
        updateType: String
    ) {
        val title = "🎵 Profile & Portfolio Update"
        val body = "$artistName published a new $updateType update."
        dispatchNotification(context, CHANNEL_PROFILE_UPDATES, title, body, notificationId = (System.currentTimeMillis() % 10000).toInt())
    }

    fun handleIncomingFcmMessage(
        context: Context,
        title: String,
        body: String,
        channelId: String = CHANNEL_CONNECTIONS
    ) {
        dispatchNotification(
            context = context,
            channelId = channelId,
            title = title,
            body = body,
            notificationId = (System.currentTimeMillis() % 10000).toInt()
        )
    }

    private fun dispatchNotification(
        context: Context,
        channelId: String,
        title: String,
        body: String,
        notificationId: Int
    ) {
        try {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            createNotificationChannels(context)

            val builder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)

            notificationManager.notify(notificationId, builder.build())
            Log.d(TAG, "Notification dispatched: [$title] $body")
        } catch (e: Exception) {
            Log.e(TAG, "Error displaying notification: ${e.message}")
        }
    }
}
