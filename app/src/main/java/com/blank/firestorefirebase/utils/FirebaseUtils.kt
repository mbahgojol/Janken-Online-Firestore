package com.blank.firestorefirebase.utils

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.coroutines.tasks.await

object FirebaseUtils {
    suspend fun getTokenNotification(): String {
        var token = ""
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("Get Token Firebase", "getInstanceId failed", task.exception)
                    return@addOnCompleteListener
                }

                // Get new Instance ID token
                token = task.result?.token!!
            }
            .await()
        return token
    }
}