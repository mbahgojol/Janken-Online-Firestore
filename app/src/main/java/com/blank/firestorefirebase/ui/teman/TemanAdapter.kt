package com.blank.firestorefirebase.ui.teman

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blank.firestorefirebase.R
import com.blank.firestorefirebase.data.model.Users
import kotlinx.android.synthetic.main.item_users.view.*

class TemanAdapter(
    private val data: MutableList<Users> = mutableListOf(),
    private var listener: (Users) -> Unit = fun(_) {}
) : RecyclerView.Adapter<TemanAdapter.TemanViewHolder>() {

    fun setData(data: MutableList<Users>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    fun clear() {
        this.data.clear()
        notifyDataSetChanged()
    }

    fun setListener(listener: (Users) -> Unit) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TemanViewHolder =
        TemanViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_users, parent, false)
        )

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: TemanViewHolder, position: Int) {
        holder.bind(data[position])
    }

    inner class TemanViewHolder(private val v: View) : RecyclerView.ViewHolder(v) {
        fun bind(users: Users) {
            v.tvNama.text = users.nama
            v.btnAddFriends.text = "Battle"
            v.btnAddFriends.setOnClickListener {
                listener.invoke(users)
            }
        }
    }
}