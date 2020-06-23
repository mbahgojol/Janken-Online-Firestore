package com.blank.firestorefirebase.ui.users

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.blank.firestorefirebase.R
import com.blank.firestorefirebase.data.model.Users
import com.blank.firestorefirebase.db
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.android.synthetic.main.activity_users.*

class UsersActivity : AppCompatActivity() {

    private val TAG = UsersActivity::class.java.simpleName
    private val adapter = UsersAdapter()
    private var myId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users)

        rvList.layoutManager = LinearLayoutManager(this)
        rvList.setHasFixedSize(true)
        rvList.adapter = adapter

        db.collection("users")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                val source = if (snapshot != null && snapshot.metadata.hasPendingWrites())
                    "Local"
                else
                    "Server"

                if (snapshot != null && !snapshot.isEmpty) {
                    Log.d(TAG, "$source data: ${snapshot.metadata}")
                    Log.d("DB_SIZE", snapshot.size().toString())
                    val model = snapshot.toObjects<Users>().toMutableList()
                    val data = mutableListOf<Users>()
                    model.forEachIndexed { index, doc ->
                        if (doc.nama == "Ghozi Mahdi") {
                            myId = doc.id
                        } else {
                            doc.id = doc.id
                            data.add(doc)
                        }
                    }
                    adapter.setData(data)
                } else {
                    Log.d(TAG, "$source data: null")
                }
            }

        adapter.setListener { user ->
            addFriendToTarget(myId, user.id) {
                Toast.makeText(this, "Teman berhasil ditambahkan", Toast.LENGTH_LONG).show()
                addFriendToTarget(user.id, myId)
            }
        }
    }

    private fun addFriendToTarget(
        targetId: String,
        id: String,
        listener: () -> Unit = fun() {}
    ) {
        db.collection("users")
            .document(targetId)
            .update(
                "friends", FieldValue.arrayUnion(id)
            ).addOnSuccessListener {
                Log.d(TAG, "Success Add Friend for $targetId")
                listener.invoke()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error updating document", e)
            }
    }
}