package com.blank.firestorefirebase.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.blank.firestorefirebase.R
import com.blank.firestorefirebase.ui.teman.TemanActivity
import com.blank.firestorefirebase.ui.users.UsersActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.java.simpleName

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
    }
}