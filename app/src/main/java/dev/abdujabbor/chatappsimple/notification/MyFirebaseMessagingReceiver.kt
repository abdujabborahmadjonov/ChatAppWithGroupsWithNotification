package dev.abdujabbor.chatappsimple.notification

import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dev.abdujabbor.chatappsimple.MainActivity
import dev.abdujabbor.chatappsimple.R
import java.util.*

class MyFirebaseMessagingReceiver : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Check if message contains a data payload.
        remoteMessage.data.isNotEmpty().let {
            // Get notification data
            val title = remoteMessage.data["title"]
            val message = remoteMessage.data["message"]

            // Check if app is in foreground
            if (isAppInForeground(this)) {
                // App is in foreground, show a Toast or handle the message as appropriate
                // ...
            } else {
                // App is in background or not running, show a notification
                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val notificationId = Random().nextInt(100000)

                // Create a notification channel for Android O and higher
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = NotificationChannel("my_channel_id", "My Channel", NotificationManager.IMPORTANCE_HIGH).apply {
                        enableLights(true)
                        lightColor = Color.RED
                        enableVibration(true)
                        description = "My Notification Channel"
                    }
                    notificationManager.createNotificationChannel(channel)
                }

                // Create an intent to show the notification
                val showNotificationIntent = Intent(this, MainActivity::class.java)
                showNotificationIntent.putExtra("title", title)
                showNotificationIntent.putExtra("message", message)
                showNotificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                val pendingIntent = PendingIntent.getActivity(
                    this,
                    0,
                    showNotificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )

                // Build the notification
                val notification = NotificationCompat.Builder(this, "my_channel_id")
                    .setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.drawable.ic_baseline_error_24)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build()

                // Show the notification
                notificationManager.notify(notificationId, notification)
            }
        }
    }

    private fun isAppInForeground(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses ?: return false
        for (appProcess in appProcesses) {
            if (appProcess.processName == context.packageName && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true
            }
        }
        return false
    }
}
