package com.blank.firestorefirebase.ui.users

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blank.firestorefirebase.R
import com.blank.firestorefirebase.data.model.Users
import kotlinx.android.synthetic.main.item_users.view.*

class UsersAdapter(
    private val data: MutableList<Users> = mutableListOf(),
    private var listener: (Users) -> Unit = fun(_) {}
) :
    RecyclerView.Adapter<UsersAdapter.UsersViewHolder>() {

    fun setListener(listener: (Users) -> Unit) {
        this.listener = listener
    }

    fun setData(data: MutableList<Users>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder =
        UsersViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_users, parent, false
            )
        )

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        holder.bind(data[position])
    }

    inner class UsersViewHolder(private val v: View) : RecyclerView.ViewHolder(v) {
        fun bind(users: Users) {
            v.tvNama.text = users.nama
            v.btnAddFriends.setOnClickListener {
                listener.invoke(users)
            }
        }
    }
}