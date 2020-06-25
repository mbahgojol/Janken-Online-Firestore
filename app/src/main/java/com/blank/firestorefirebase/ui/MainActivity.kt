package com.blank.firestorefirebase.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.blank.chapter9.DataManager
import com.blank.firestorefirebase.R
import com.blank.firestorefirebase.data.model.Data
import com.blank.firestorefirebase.data.model.PayloadNotif
import com.blank.firestorefirebase.ui.teman.TemanActivity
import com.blank.firestorefirebase.ui.users.UsersActivity
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.java.simpleName
    var token = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnBattle.setOnClickListener {

        }

        btnFriends.setOnClickListener {
            startActivity(Intent(this, TemanActivity::class.java))
        }

        btnListUser.setOnClickListener {
            startActivity(Intent(this, UsersActivity::class.java))
        }

        btnLogToken.setOnClickListener {
            FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w(TAG, "getInstanceId failed", task.exception)
                        return@addOnCompleteListener
                    }

                    // Get new Instance ID token
                    val token = task.result?.token
                    this.token = token!!

                    val clipboard: ClipboardManager =
                        getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("token", token)
                    clipboard.setPrimaryClip(clip)

                    // Log and toast
                    val msg = getString(R.string.msg_token_fmt, token)
                    Log.d(TAG, msg)
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                }

        }

        sendNotif.setOnClickListener {
            DataManager.pushNotif(
                PayloadNotif(
                    to = "ep7HG7GRSLS7nrOyU-ocSm:APA91bHOGLGNJ3Z3HRyHWIxmWUqyy-L5mHVe6uMj12_jZhAsOm4wh_0KY5tR7Qq6GrFSPF27nKSOQW6ikQpZVfg3WgCNjIWEE5cRlY7VYitUOVzAuZq0_9sUFa7qs7d1HpcNcPSSjoX2",
                    data = Data(
                        idTarget = "punya id target",
                        idPengirim = "ini idnya pengirim",
                        title = "Lagi ngpush notif dari client",
                        message = "Test Ajah"
                    )
                )
            ).subscribe({
                Log.d(TAG, it)
            }, {
                Log.d(TAG, it.toString())
            })
        }
    }
}