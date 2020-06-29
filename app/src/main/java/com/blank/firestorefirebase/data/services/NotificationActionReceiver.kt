package com.blank.firestorefirebase.data.services

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.blank.chapter9.DataManager
import com.blank.firestorefirebase.data.model.NotifFriends
import com.blank.firestorefirebase.db
import com.blank.firestorefirebase.utils.AppConstant
import com.blank.firestorefirebase.utils.FirebaseUtils
import com.blank.firestorefirebase.utils.FirebaseUtils.addFriendToTarget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class NotificationActionReceiver : BroadcastReceiver() {
    private val TAG = NotificationActionReceiver::class.java.simpleName

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "TERIMA") {
            Toast.makeText(context, "Selamat Anda sudah berteman dengan", Toast.LENGTH_SHORT).show()

            val notifData = intent.extras?.getParcelable<NotifFriends.Data>("notifData")
            GlobalScope.launch(Dispatchers.IO) {
                val notifToken = FirebaseUtils.getTokenNotification(notifData?.idPengirim!!)
                val name = FirebaseUtils.getUserName(notifData.idTarget!!)

                val modelNotif = NotifFriends(
                    to = notifToken,
                    data = NotifFriends.Data(
                        idTarget = notifData?.idTarget,
                        idPengirim = notifData?.idPengirim,
                        title = "Permintaan pertemanan",
                        message = "Permintaan pertemanan anda sudah diterima oleh $name",
                        type = AppConstant.FRIENDS_NOTIF
                    )
                )
                DataManager.pushNotif(modelNotif).subscribe({
                    Log.d(TAG, it)

                    val idPenerima = notifData.idPengirim

                    db.collection("NotifList")
                        .document(idPenerima!!)
                        .collection("notif")
                        .add(modelNotif)
                        .addOnSuccessListener {
                            Log.d(TAG, "Suskes masukin notif")
                        }
                        .addOnFailureListener {
                            Log.d(TAG, "Gagal masukin notif ke id penerima", it)
                        }

                    addFriendToTarget(notifData.idPengirim, notifData.idTarget) {
                        Toast.makeText(context, "Teman berhasil ditambahkan", Toast.LENGTH_LONG)
                            .show()
                        addFriendToTarget(notifData.idTarget, notifData.idPengirim)
                    }

                }, {
                    Log.d(TAG, it.toString())
                })
            }

        } else if (intent?.action == "TOLAK") {
            var notificationManager =
                context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(AppConstant.NOTIF_CANCEL)

        }
    }
}