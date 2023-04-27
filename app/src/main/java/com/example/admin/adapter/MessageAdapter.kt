package com.example.admin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.admin.R
import com.example.admin.databinding.ReceiverMsgBinding
import com.example.admin.databinding.SenderMsgBinding
import com.example.admin.model.Message
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(
    private val context: Context, messages: ArrayList<Message>?,
    senderRoom: String, receiverRoom: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {

    lateinit var messages: ArrayList<Message>
    val ITEM_SENT = 1
    val ITEM_RECEIVE = 2
    private val senderRoom: String
    private val receiverRoom: String

    inner class SentMsgHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: SenderMsgBinding = SenderMsgBinding.bind(itemView)
    }

    inner class ReceiverMsgHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: ReceiverMsgBinding = ReceiverMsgBinding.bind(itemView)
    }

    init {
        if (messages != null) {
            this.messages = messages
        }
        this.senderRoom = senderRoom
        this.receiverRoom = receiverRoom
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_SENT) {
            val view = LayoutInflater.from(context).inflate(R.layout.sender_msg, parent, false)
            SentMsgHolder(view)
        } else {
            val view = LayoutInflater.from(context).inflate(R.layout.receiver_msg, parent, false)
            ReceiverMsgHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val messages = messages[position]
        return if (FirebaseAuth.getInstance().uid == messages.senderId) {
            ITEM_SENT
        } else {
            ITEM_RECEIVE
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder.javaClass == SentMsgHolder::class.java) {
            val viewHolder = holder as SentMsgHolder
            if (message.message.equals("photo")) {
                viewHolder.binding.imageChats.visibility = View.VISIBLE
                viewHolder.binding.linearLayoutChats.visibility = View.GONE
                viewHolder.binding.message.visibility = View.GONE
                Glide.with(context).load(message.imageUrl).placeholder(R.drawable.placeholder)
                    .into(viewHolder.binding.imageChats)
            }
            viewHolder.binding.message.text = message.message
        } else {
            val viewHolder = holder as ReceiverMsgHolder
            if (message.message.equals("photo")) {
                viewHolder.binding.imageChats.visibility = View.VISIBLE
                viewHolder.binding.linearLayoutChats.visibility = View.GONE
                viewHolder.binding.message.visibility = View.GONE
                Glide.with(context).load(message.imageUrl).placeholder(R.drawable.placeholder)
                    .into(viewHolder.binding.imageChats)
            }
            viewHolder.binding.message.text = message.message
        }
    }

    override fun getItemCount(): Int = messages.size
}