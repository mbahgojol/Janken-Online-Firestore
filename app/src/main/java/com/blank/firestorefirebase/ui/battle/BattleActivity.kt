package com.blank.firestorefirebase.ui.battle

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.blank.firestorefirebase.R
import com.blank.firestorefirebase.data.model.ChoseBattle
import com.blank.firestorefirebase.db
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.android.synthetic.main.activity_battle.*

class BattleActivity : AppCompatActivity(), NavigatorBattle {

    private val TAG = BattleActivity::class.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_battle)

        val id = intent.extras?.getString("id")!!
        val enemy = intent.extras?.getBoolean("enemy")!!

        val btn2 = listOf<Button>(batu2, gunting2, kertas2)
        val btn1 = listOf<Button>(batu1, gunting1, kertas1)

        val controller = Controller(this)

        if (enemy) { // enemy
            tvStatus2.text = "Is you"
            var enemyChose = ""
            var challengerChose = ""

            btn2.forEach { btn ->
                btn.setOnClickListener {
                    db.collection("battle")
                        .document("room_a")
                        .collection("challenger")
                        .add(ChoseBattle(btn.contentDescription.toString(), id))
                        .addOnSuccessListener {
                            enemyChose = btn.contentDescription.toString()
                            if (challengerChose != "")
                                controller.checkPemenang(enemyChose, challengerChose)
                        }.addOnFailureListener {
                            Log.d(TAG, "Tambah battle gagal : ${it.message}")
                        }
                }
            }

            db.collection("battle")
                .document("room_a")
                .collection("enemy")
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
                        val model = snapshot.toObjects<ChoseBattle>().toMutableList()

                        btn1.forEach {
                            if (it.contentDescription.toString() == model[0].chose) {
                                it.isEnabled = false
                                challengerChose = model[0].chose
                                if (enemyChose != "")
                                    controller.checkPemenang(enemyChose, challengerChose)
                            } else {
                                it.isEnabled = true
                            }
                        }
                    } else {
                        Log.d(TAG, "$source data: null")
                    }
                }
        } else { // challanger
            tvStatus1.text = "Is you"
            var enemyChose = ""
            var challengerChose = ""

            btn1.forEach { btn ->
                btn.setOnClickListener {
                    db.collection("battle")
                        .document("room_a")
                        .collection("enemy")
                        .add(ChoseBattle(btn.contentDescription.toString(), id))
                        .addOnSuccessListener {
                            challengerChose = btn.contentDescription.toString()
                            if (enemyChose != "")
                                controller.checkPemenang(challengerChose, enemyChose)
                        }.addOnFailureListener {
                            Log.d(TAG, "Tambah battle gagal : ${it.message}")
                        }
                }
            }

            db.collection("battle")
                .document("room_a")
                .collection("challenger")
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
                        val model = snapshot.toObjects<ChoseBattle>().toMutableList()

                        btn2.forEach {
                            if (it.contentDescription.toString() == model[0].chose) {
                                it.isEnabled = false
                                enemyChose = model[0].chose
                                if (challengerChose != "")
                                    controller.checkPemenang(challengerChose, enemyChose)
                            } else {
                                it.isEnabled = true
                            }
                        }
                    } else {
                        Log.d(TAG, "$source data: null")
                    }
                }
        }
    }

    override fun onResult(msg: String) {
        tvVs.text = msg
    }
}