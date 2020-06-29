package com.blank.firestorefirebase.data.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Vibrator
import android.widget.Toast


class MyBroadcast : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Toast.makeText(context, "Ada Notif Brow", Toast.LENGTH_SHORT).show()

        val vibrator =
            context!!.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(2000)
    }
}