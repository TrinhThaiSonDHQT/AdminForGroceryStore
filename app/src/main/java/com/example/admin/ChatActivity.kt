package com.example.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.admin.adapter.MessageAdapter
import com.example.admin.databinding.ActivityChatBinding
import com.example.admin.model.Message
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    val adapter: MessageAdapter? = null
    var message: ArrayList<Message>? = null
    var senderRoom: String? = null
    var database: FirebaseDatabase? = null
    var storage: FirebaseFirestore? = null
    var senderUid: String? = null
    var receiveUid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        setSupportActionBar(binding.toolbar)
        database = FirebaseDatabase.getInstance()
        storage = FirebaseFirestore.getInstance()
        message = ArrayList()
        val name = intent.getStringExtra("")
    }
}