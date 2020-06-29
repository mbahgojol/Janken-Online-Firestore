package com.blank.firestorefirebase.ui.users

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.blank.chapter9.DataManager
import com.blank.firestorefirebase.R
import com.blank.firestorefirebase.data.model.NotifFriends
import com.blank.firestorefirebase.data.model.Users
import com.blank.firestorefirebase.db
import com.blank.firestorefirebase.mAuth
import com.blank.firestorefirebase.utils.AppConstant
import com.google.firebase.firestore.ktx.toObjects
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_users.*

class UsersActivity : AppCompatActivity() {

    private val TAG = UsersActivity::class.java.simpleName
    private val adapter = UsersAdapter()
    val id = mAuth.currentUser?.uid
    private val disposable = CompositeDisposable()
    private var user: Users? = null

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
                        if (doc.id != id) {
                            data.add(doc)
                        } else {
                            user = doc
                        }
                    }
                    adapter.setData(data)
                } else {
                    Log.d(TAG, "$source data: null")
                }
            }

        adapter.setListener { user ->
            notifAddFriend(user)
        }
    }

    private fun notifAddFriend(users: Users) {
        val modelNotif = NotifFriends(
            to = users.tokenNotif,
            data = NotifFriends.Data(
                idTarget = users.id,
                idPengirim = id,
                statusFriend = false,
                title = "Permintaan mintaan pertemanan",
                message = "${user?.username}  Ingin berteman dengan Anda!",
                type = AppConstant.FRIENDS_NOTIF
            )
        )
        disposable.add(DataManager.pushNotif(modelNotif).subscribe({
            Log.d(TAG, it)

            val idPenerima = users.id

            db.collection("NotifList")
                .document(idPenerima)
                .collection("notif")
                .add(modelNotif)
                .addOnSuccessListener {
                    Log.d(TAG, "Suskes masukin notif")
                }
                .addOnFailureListener {
                    Log.d(TAG, "Gagal masukin notif ke id penerima", it)
                }

        }, {
            Log.d(TAG, it.toString())
        }))
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }
}