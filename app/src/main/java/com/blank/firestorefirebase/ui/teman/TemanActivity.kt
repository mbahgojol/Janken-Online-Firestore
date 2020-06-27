package com.blank.firestorefirebase.ui.teman

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.blank.firestorefirebase.R
import com.blank.firestorefirebase.data.model.Users
import com.blank.firestorefirebase.db
import com.blank.firestorefirebase.mAuth
import com.blank.firestorefirebase.ui.battle.BattleActivity
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.android.synthetic.main.activity_teman.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

class TemanActivity : AppCompatActivity() {

    private val TAG = TemanActivity::class.simpleName
    private val temanAdapter = TemanAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teman)

        rvFriends.apply {
            layoutManager = LinearLayoutManager(this@TemanActivity)
            setHasFixedSize(true)
            adapter = temanAdapter
        }

        val id = mAuth.currentUser?.uid
        db.collection("users")
            .whereEqualTo("id", id)
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
                    Log.d(TAG, snapshot.size().toString())
                    val model = snapshot.toObjects<Users>().toMutableList()
                    val friends = mutableListOf<Users>()

                    GlobalScope.launch(Dispatchers.IO) {
                        model[0].friends.forEach {
                            val frend = getUserById(it)
                            frend?.let { it1 -> friends.add(it1) }
                        }
                        launch(Dispatchers.Main) {
                            temanAdapter.setData(friends)
                        }
                    }
                } else {
                    Log.d(TAG, "$source data: null")
                    temanAdapter.clear()
                }
            }

        temanAdapter.setListener {
            val id_room = UUID.randomUUID().toString()

            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Chose Enemy or Challenger")
            dialog.setPositiveButton("Enemy") { dialog, which ->
                Intent(this, BattleActivity::class.java).apply {
                    putExtra("enemy", true)
                    putExtra("room", id_room)
                    putExtra("id", it.id)
                    startActivity(this)
                }
            }
            dialog.setNegativeButton("Challenger") { dialog, which ->
                Intent(this, BattleActivity::class.java).apply {
                    putExtra("enemy", false)
                    putExtra("room", id_room)
                    putExtra("id", it.id)
                    startActivity(this)
                }
            }
            val alertDialog = dialog.create()
            alertDialog.show()
        }
    }

    private suspend fun getUserById(id: String): Users? {
        var users: Users? = null
        db.collection("users")
            .document(id)
            .get()
            .addOnSuccessListener {
                Log.d(TAG, "Success get User ID : $id")
                val model = it.toObject<Users>()
                if (model != null) {
                    users = model
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "Error get User by ID : $id")
            }
            .await()
        return users
    }
}