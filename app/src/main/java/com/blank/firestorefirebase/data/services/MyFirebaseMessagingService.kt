package com.blank.firestorefirebase.data.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.blank.firestorefirebase.R
import com.blank.firestorefirebase.data.model.NotifFriends
import com.blank.firestorefirebase.mAuth
import com.blank.firestorefirebase.ui.MainActivity
import com.blank.firestorefirebase.utils.AppConstant
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import org.json.JSONObject


class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")

        remoteMessage.data.isNotEmpty().let {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
            val jsonData = Gson().toJson(remoteMessage.data)
            Log.d(TAG, "RESPONSE $jsonData")

            sendBroadcast(Intent(this, BroadcastReceiver::class.java))

            if (jsonData != null) {
                val jsonObject = JSONObject(jsonData)
                val type = jsonObject.getString("type")
                when (type) {
                    AppConstant.FRIENDS_NOTIF -> {
                        val model = Gson().fromJson(jsonData, NotifFriends.Data::class.java)
                        if (mAuth.currentUser?.uid == model.idTarget) {
                            sendNotificationRequestFriends(model)
                        } else {
                            sendNotification(
                                model.title.toString(),
                                model.message.toString()
                            )
                        }
                    }

                    AppConstant.BATTLE_NOTIF -> {

                    }
                }
            }

            if (true) {
                scheduleJob()
            } else {
                handleNow()
            }
        }

        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }

    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")

        sendRegistrationToServer(token)
    }

    private fun scheduleJob() {
        val work = OneTimeWorkRequest.Builder(MyWorker::class.java).build()
        WorkManager.getInstance().beginWith(work).enqueue()
    }


    private fun handleNow() {
        Log.d(TAG, "Short lived task is done.")
    }

    private fun sendRegistrationToServer(token: String?) {
        // TODO: Implement this method to send token to your app server.
        Log.d(TAG, "sendRegistrationTokenToServer($token)")
    }

    private fun sendNotification(vararg msg: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(msg[0])
            .setContentText(msg[1])
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }

    private fun sendNotificationRequestFriends(msg: NotifFriends.Data) {
        val intent = Intent(this, MainActivity::class.java)
            .apply {
                putExtra("fromNotification", "book_ride")
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }

        val intentConfirm = Intent(this, NotificationActionReceiver::class.java)
            .apply {
                action = "TERIMA"
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra("notifData", msg)
            }

        val intentCancel = Intent(this, NotificationActionReceiver::class.java)
            .apply {
                action = "TOLAK"
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra("notifData", msg)
            }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        // requestCode 0 stayNotif, 1 close notif
        val pendingIntentConfirm =
            PendingIntent.getBroadcast(this, 1, intentConfirm, PendingIntent.FLAG_CANCEL_CURRENT)

        val pendingIntentCancel =
            PendingIntent.getBroadcast(this, 1, intentCancel, PendingIntent.FLAG_CANCEL_CURRENT)

        val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val channelId = getString(R.string.default_notification_channel_id)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(msg.title)
            .setContentText(msg.message)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        notificationBuilder.addAction(
            R.drawable.ic_launcher_foreground,
            "TERIMA",
            pendingIntentConfirm
        )

        notificationBuilder.addAction(
            R.drawable.ic_launcher_foreground,
            "TOLAK",
            pendingIntentCancel
        )

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(11111 /* ID of notification */, notificationBuilder.build())
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}