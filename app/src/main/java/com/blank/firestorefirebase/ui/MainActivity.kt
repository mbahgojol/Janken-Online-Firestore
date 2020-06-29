package com.blank.firestorefirebase.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.blank.chapter9.DataManager
import com.blank.firestorefirebase.R
import com.blank.firestorefirebase.data.model.PayloadNotif
import com.blank.firestorefirebase.data.model.Users
import com.blank.firestorefirebase.db
import com.blank.firestorefirebase.mAuth
import com.blank.firestorefirebase.ui.teman.TemanActivity
import com.blank.firestorefirebase.ui.users.UsersActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream


class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.java.simpleName
    var token = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnUpload.setOnClickListener {
            /*var mStorageRef = FirebaseStorage.getInstance().reference
            val file: Uri = Uri.fromFile(File("path/to/images/rivers.jpg"))
            val riversRef: StorageReference = mStorageRef.child("images/rivers.jpg")

            riversRef.putFile(file)
                .addOnSuccessListener { taskSnapshot ->
                    Log.d(TAG, "Suskes Save Image")
                    // Get a URL to the uploaded content

                }
                .addOnFailureListener {
                    Log.d(TAG, "Error Save Image", it)
                    // Handle unsuccessful uploads
                    // ...
                }*/

            val storageRef = FirebaseStorage.getInstance().reference
            val mountainsRef = storageRef.child("test.jpg")
            val mountainImagesRef = storageRef.child("images/test.jpg")
            mountainsRef.name == mountainImagesRef.name
            mountainsRef.path == mountainImagesRef.path

            imageView.isDrawingCacheEnabled = true
            imageView.buildDrawingCache()
            val bitmap = (imageView.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            // cara uploadnya
            mountainsRef.putBytes(data)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        Log.d(TAG, "DownloadURI : $downloadUri")
                    }
                }
                .addOnSuccessListener { taskSnapshot ->
                    Log.d(TAG, "Path : ${taskSnapshot.metadata?.path}")
                    Log.d(TAG, "URL : ${taskSnapshot.storage.downloadUrl}")
                    Log.d(TAG, "URL : ${taskSnapshot.storage.bucket}")
                    Log.d(TAG, "PATH-Storage : ${taskSnapshot.storage.path}")
                    Log.d(TAG, "Suskes Save Image")

                    // masukin data profile fotonya ke users db sesuai dengan id masing masing
                    val id = mAuth.currentUser?.uid
                    db.collection("users")
                        .document(id.toString())
                        .update("pic", taskSnapshot.storage.path)
                        .addOnFailureListener {
                            Log.d(TAG, "Gagal update pic to usersnya", it)
                        }
                        .addOnSuccessListener {
                            Log.d(TAG, "Suskes update pic")

                            // ini proses ngambil path dari users
                            db.collection("users")
                                .document(id.toString())
                                .get()
                                .addOnSuccessListener { doc ->
                                    val model = doc.toObject<Users>()
                                    // ini proses ngambil gambar dari storage by path yg sudah didapat dari atas
                                    storageRef.child(model?.pic!!).downloadUrl.addOnSuccessListener { uri ->
                                        Glide.with(this@MainActivity).load(uri).into(getImage)
                                    }.addOnFailureListener {
                                        Log.d(TAG, "Gagal ambil fotonya", it)
                                    }
                                }
                                .addOnFailureListener {
                                    Log.d(TAG, "Gagal get pic by users idnya", it)
                                }
                        }

                }
                .addOnFailureListener {
                    Log.d(TAG, "Error Save Image", it)
                }
        }

        btnFriends.setOnClickListener {
            startActivity(Intent(this, TemanActivity::class.java))
        }

        btnListUser.setOnClickListener {
            startActivity(Intent(this, UsersActivity::class.java))
        }

        btnLogToken.setOnClickListener {
            FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w(TAG, "getInstanceId failed", task.exception)
                        return@addOnCompleteListener
                    }

                    // Get new Instance ID token
                    val token = task.result?.token
                    this.token = token!!

                    val clipboard: ClipboardManager =
                        getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("token", token)
                    clipboard.setPrimaryClip(clip)

                    // Log and toast
                    val msg = getString(R.string.msg_token_fmt, token)
                    Log.d(TAG, msg)
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                }

        }

        sendNotif.setOnClickListener {
            val modelNotif = PayloadNotif(
                to = "ep7HG7GRSLS7nrOyU-ocSm:APA91bHOGLGNJ3Z3HRyHWIxmWUqyy-L5mHVe6uMj12_jZhAsOm4wh_0KY5tR7Qq6GrFSPF27nKSOQW6ikQpZVfg3WgCNjIWEE5cRlY7VYitUOVzAuZq0_9sUFa7qs7d1HpcNcPSSjoX2",
                data = PayloadNotif.Data(
                    idTarget = "punya id target",
                    idPengirim = "ini idnya pengirim",
                    title = "Lagi ngpush notif dari client",
                    message = "Test Ajah"
                )
            )
            DataManager.pushNotif(modelNotif).subscribe({
                Log.d(TAG, it)

                val idPenerima = "123penerima"

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
            })
        }
    }
}