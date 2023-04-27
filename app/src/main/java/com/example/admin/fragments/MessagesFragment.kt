package com.example.admin.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.adapter.MessageAdapter
import com.example.admin.adapter.UserAdapter
import com.example.admin.databinding.FragmentMessagesBinding
import com.example.admin.model.Message
import com.example.admin.model.User
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@Suppress("DEPRECATION")
class MessagesFragment : Fragment(), UserAdapter.ItemClickListener {

    private lateinit var binding: FragmentMessagesBinding
    private lateinit var recyclerViewUsers: RecyclerView
    private lateinit var recyclerViewChats: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var databaseUser: DatabaseReference

    private val listUser = ArrayList<User>()
    private var isChooseUser: Boolean = false
    private var adapter: MessageAdapter? = null
    private var messages: ArrayList<Message>? = null
    private var senderRoom: String? = null
    private var receiveRoom: String? = null
    private var databaseMessage: FirebaseDatabase? = null
    private var storage: FirebaseStorage? = null
    private var senderUid: String = "admin"
    private var receiveUid: String? = null

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 25) {
            if (data != null) {
                if (data.data != null) {
                    val selectedImage = data.data
                    val calendar = Calendar.getInstance()
                    val reference = storage!!.reference.child("chats")
                        .child(calendar.timeInMillis.toString())

                    reference.putFile(selectedImage!!)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                reference.downloadUrl.addOnSuccessListener { uri ->
                                    val filePath = uri.toString()
                                    val messageText = binding.editText.text.toString()
                                    val date = Date()
                                    val message = Message(messageText, senderUid, date.time)
                                    message.message = "photo"
                                    message.imageUrl = filePath
                                    binding.editText.text = null
                                    val key = databaseMessage!!.reference.push().key
                                    val lastMsgObj = HashMap<String, Any>()
                                    lastMsgObj["lastMsg"] = message.message!!
                                    lastMsgObj["lastMsgTime"] = date.time

                                    databaseMessage!!.reference.child("chats").child(senderRoom!!)
                                        .updateChildren(lastMsgObj)
                                    databaseMessage!!.reference.child("chats").child(receiveRoom!!)
                                        .updateChildren(lastMsgObj)
                                    databaseMessage!!.reference.child("chats").child(senderRoom!!)
                                        .child("messages")
                                        .child(key!!)
                                        .setValue(message).addOnCompleteListener {
                                            databaseMessage!!.reference.child("chats")
                                                .child(receiveRoom!!)
                                                .child("messages")
                                                .child(key)
                                                .setValue(message).addOnSuccessListener { }
                                        }
                                }
                            }
                        }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMessagesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // initialize variable database to connect to Database
        databaseUser = FirebaseDatabase.getInstance().getReference("User")

        recyclerViewUsers = binding.listUsers
        recyclerViewChats = binding.recycleChats

        userAdapter = UserAdapter(listUser, requireContext(), this)
        recyclerViewUsers.adapter = userAdapter

        databaseMessage = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        messages = ArrayList()

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
        if (!isChooseUser) {
            isChooseUser = true
            binding.chats.visibility = View.VISIBLE
            binding.notChooseUser.visibility = View.GONE
        }
        binding.userEmailChats.text = user.email
        binding.userNameChats.text = user.userName

        receiveUid = user.uid
        senderRoom = senderUid + receiveUid
        receiveRoom = receiveUid + senderUid

        adapter = MessageAdapter(requireContext(), messages, senderRoom!!, receiveRoom!!)
        recyclerViewChats.adapter = adapter

        databaseMessage!!.reference
            .child("chats")
            .child(senderRoom!!)
            .child("messages")
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    messages!!.clear()
                    for (s in snapshot.children) {
                        val message = s.getValue(Message::class.java)
                        message!!.messageId = s.key
                        messages!!.add(message)
                    }
                    adapter!!.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {}

            })


        binding.buttonSend.setOnClickListener {
            val messageText = binding.editText.text.toString()
            val date = Date()
            val message = Message(messageText, senderUid, date.time)

            binding.editText.text = null
            val key = databaseMessage!!.reference.push().key
            val lastMsgObj = HashMap<String, Any>()
            lastMsgObj["lastMsg"] = message.message!!
            lastMsgObj["lastMsgTime"] = date.time

            databaseMessage!!.reference.child("chats").child(senderRoom!!)
                .updateChildren(lastMsgObj)
            databaseMessage!!.reference.child("chats").child(receiveRoom!!)
                .updateChildren(lastMsgObj)
            databaseMessage!!.reference.child("chats").child(senderRoom!!)
                .child("messages")
                .child(key!!)
                .setValue(message).addOnCompleteListener {
                    databaseMessage!!.reference.child("chats")
                        .child(receiveRoom!!)
                        .child("messages")
                        .child(key)
                        .setValue(message).addOnSuccessListener {
                        }
                }
        }

        binding.attach.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 25)
        }

        val handler = Handler()
        binding.editText.addTextChangedListener {
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    databaseMessage!!.reference.child("Presence")
                        .child(senderUid)
                        .setValue("typing")
                    handler.removeCallbacksAndMessages(null)
                    handler.postDelayed(userStoppedTyping, 1000)
                }

                var userStoppedTyping = Runnable {
                    databaseMessage!!.reference.child("Presence")
                        .child(senderUid)
                        .setValue("online")
                }

            }

        }
    }
}