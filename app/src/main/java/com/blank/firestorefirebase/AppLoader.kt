package com.blank.firestorefirebase

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AppLoader : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}

val db = Firebase.firestore
val mAuth = FirebaseAuth.getInstance()