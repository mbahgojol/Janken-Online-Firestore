package com.blank.firestorefirebase.ui.login

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.blank.firestorefirebase.R
import com.blank.firestorefirebase.db
import com.blank.firestorefirebase.ui.MainActivity
import com.blank.firestorefirebase.ui.register.RegisterActivity
import com.blank.firestorefirebase.utils.FirebaseUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private val mAuth = FirebaseAuth.getInstance()
    private val TAG = LoginActivity::class.simpleName

    private val dialog: AlertDialog by lazy {
        SpotsDialog.Builder().setContext(this).build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnLogin.setOnClickListener {
            if (!dialog.isShowing) dialog.show()

            val email = etEmail.text.toString()
            val pwd = etPwd.text.toString()

            mAuth.signInWithEmailAndPassword(email, pwd)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "signInWithEmail:success")

                        val id = mAuth.currentUser?.uid

                        GlobalScope.launch(Dispatchers.IO) {
                            db.collection("users")
                                .document(id!!)
                                .update("tokenNotif", FirebaseUtils.getTokenNotification())
                                .addOnSuccessListener {
                                    if (dialog.isShowing) dialog.hide()
                                    startActivity(
                                        Intent(
                                            this@LoginActivity,
                                            MainActivity::class.java
                                        )
                                    )
                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "Authentication failed",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                    } else {
                        Log.d(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            this@LoginActivity,
                            "Authentication failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}