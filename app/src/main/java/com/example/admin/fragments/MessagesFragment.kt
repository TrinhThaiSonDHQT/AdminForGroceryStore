package com.example.admin.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.adapter.UserAdapter
import com.example.admin.databinding.FragmentMessagesBinding
import com.example.admin.model.User
import com.google.firebase.database.*

class MessagesFragment : Fragment(), UserAdapter.ItemClickListener {

    private lateinit var databaseUser: DatabaseReference
    private lateinit var binding: FragmentMessagesBinding
    private lateinit var recyclerViewUsers: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private lateinit var progressBar: ProgressBar
    private val listUser = ArrayList<User>()
    private var isChooseUser: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMessagesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerViewUsers = binding.listUsers

        // initialize variable database to connect to Database
        databaseUser = FirebaseDatabase.getInstance().getReference("User")

        userAdapter = UserAdapter(listUser, requireContext(), this)
        recyclerViewUsers.adapter = userAdapter

        progressBar = binding.progressBarChats

        getUsers()
    }

    private fun getUsers() {
        val childEventListener = object : ChildEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val uid = dataSnapshot.key
                val email = dataSnapshot.child("email").value.toString()
                val name = dataSnapshot.child("name").value.toString()
                val user = User(uid, email, name)
                listUser.add(user)
                userAdapter.notifyDataSetChanged()

                progressBar.visibility = View.GONE
                binding.notChooseUser.visibility = View.VISIBLE
                binding.lefSideChats.visibility = View.VISIBLE
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {

            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        databaseUser.addChildEventListener(childEventListener)
    }

    override fun onItemClick(user: User) {
        if(!isChooseUser) {
            isChooseUser = true
            binding.chats.visibility = View.VISIBLE
            binding.notChooseUser.visibility = View.GONE
        }
        binding.userEmailChats.text = user.email
        binding.userNameChats.text = user.userName
    }
}