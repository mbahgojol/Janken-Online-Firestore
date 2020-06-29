package com.blank.firestorefirebase.utils

import android.util.Log
import com.blank.firestorefirebase.data.model.Users
import com.blank.firestorefirebase.db
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.toObject
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

    suspend fun getTokenNotification(id: String): String {
        var token = ""
        db.collection("users")
            .document(id)
            .get()
            .addOnSuccessListener {
                Log.d("GetTokenNotif", "Berhasil Ambil Token Notif")
                val model = it.toObject<Users>()
                token = model?.tokenNotif!!
            }
            .addOnFailureListener {
                Log.d("GetTokenNotif", "Gagal ambil Token Notif", it)
            }.await()
        return token
    }

    fun addFriendToTarget(
        targetId: String,
        id: String,
        listener: () -> Unit = fun() {}
    ) {
        db.collection("users")
            .document(targetId)
            .update(
                "friends", FieldValue.arrayUnion(id)
            ).addOnSuccessListener {
                Log.d("Friend", "Success Add Friend for $targetId")
                listener.invoke()
            }
            .addOnFailureListener { e ->
                Log.w("Friend", "Error updating document", e)
            }
    }

    suspend fun getUserName(id: String): String {
        var name = ""
        db.collection("users")
            .document(id)
            .get()
            .addOnSuccessListener {
                Log.d("GetUserName", "Berhasil Ambil UserName")
                val model = it.toObject<Users>()
                name = model?.username!!
            }
            .addOnFailureListener {
                Log.d("GetUserName", "Gagal Ambil UserName", it)
            }
            .await()
        return name
    }
}