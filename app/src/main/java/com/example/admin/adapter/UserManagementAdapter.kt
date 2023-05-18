package com.example.admin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.admin.R
import com.example.admin.fragments.UsersFragment
import com.example.admin.model.UserManagement

class UserManagementAdapter(
    private val list: ArrayList<UserManagement>,
    private val context: Context,
    usersFragment: UsersFragment,
) : RecyclerView.Adapter<UserManagementAdapter.ViewHolder>() {

    interface OnDeleteClickListener {
        fun onDeleteClick(user: UserManagement)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imageUser: ImageView
        val userName: TextView
        val emailUser:TextView
        init {
            imageUser = view.findViewById(R.id.user_avatar)
            userName = view.findViewById(R.id.user_name)
            emailUser = view.findViewById(R.id.user_email)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_usermanagement, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: UserManagementAdapter.ViewHolder, position: Int) {
        val usermanagement = list[position]
        Glide.with(context).load(usermanagement.user_avatar).into(holder.imageUser)
        holder.emailUser.text = usermanagement.email.toString()
        holder.userName.text = usermanagement.name.toString()


    }
}