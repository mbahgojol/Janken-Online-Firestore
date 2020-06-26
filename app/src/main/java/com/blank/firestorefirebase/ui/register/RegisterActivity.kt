package com.blank.firestorefirebase.ui.register

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.blank.firestorefirebase.R
import com.blank.firestorefirebase.db
import com.blank.firestorefirebase.utils.FirebaseUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private val mAuth = FirebaseAuth.getInstance()
    private val TAG = RegisterActivity::class.java.simpleName

    private val dialog: AlertDialog by lazy {
        SpotsDialog.Builder().setContext(this).build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btnLogin.setOnClickListener {
            finish()
        }

        btnRegister.setOnClickListener {
            if (!dialog.isShowing) dialog.show()

            val username = etUsername.text.toString()
            val umur = etUmur.text.toString()
            val email = etEmail.text.toString()
            val pwd = etPwd.text.toString()

            mAuth.createUserWithEmailAndPassword(email, pwd)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "createUserWithEmail:success")

                        val user = mAuth.currentUser
                        val id = user?.uid.toString()

                        GlobalScope.launch(Dispatchers.IO) {

                            val userData = hashMapOf<String, Any>()
                                .apply {
                                    put("id", id)
                                    put("username", username)
                                    put("email", user?.email!!)
                                    put("umur", umur.toInt())
                                    put("timestamp", FieldValue.serverTimestamp())
                                    put("tokenNotif", FirebaseUtils.getTokenNotification())
                                    put("gender", "L")
                                }

                            db.collection("users")
                                .document(id)
                                .set(userData)
                                .addOnSuccessListener {
                                    Log.d(TAG, "DocumentSnapshot added with ID: $id")


                                    if (dialog.isShowing) dialog.hide()
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        "Register sukses",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                .addOnFailureListener {
                                    Log.d(TAG, "DocumentSnapshot error with ID:", it)
                                }
                        }

                    } else {
                        if (dialog.isShowing) dialog.hide()
                        Log.d(TAG, "createdUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            this@RegisterActivity,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }
}