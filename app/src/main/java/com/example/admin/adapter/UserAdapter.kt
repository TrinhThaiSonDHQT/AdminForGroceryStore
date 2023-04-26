package com.example.admin.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.SurfaceControl.Transaction
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.admin.R
import com.example.admin.fragments.MessagesFragment
import com.example.admin.model.User


class UserAdapter(
    private val list: ArrayList<User>,
    private val context: Context,
    private val clickListener: ItemClickListener
) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userName: TextView
        val userEmail: TextView
        val avatar: ImageView
        val relativeLayout: RelativeLayout

        init {
            userName = view.findViewById(R.id.userNameChats)
            userEmail = view.findViewById(R.id.userEmailChats)
            avatar = view.findViewById(R.id.avatarChats)
            relativeLayout = view.findViewById(R.id.item_users)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_users, viewGroup, false)
        return ViewHolder(view)
    }

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.userEmail.text = list[position].email
        viewHolder.userName.text = list[position].userName

        Glide.with(context).load(R.drawable.default_avatar_profile).into(viewHolder.avatar)
        viewHolder.relativeLayout.setOnClickListener {
            clickListener.onItemClick(User(list[position].uid, list[position].email, list[position].userName))
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = list.size

    interface ItemClickListener {
        fun onItemClick(user: User)
    }
}