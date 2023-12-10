package com.gmlab.team_benew.chat.socket

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gmlab.team_benew.R

class ChatAdapter(private val messages: List<ChatMessage>) :
    RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageText: TextView = itemView.findViewById(R.id.tv_chat_message)
        val senderText: TextView = itemView.findViewById(R.id.tv_chat_sender)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_item_chat, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messages[position]
        holder.messageText.text = message.message

        if (message.userId == message.senderId) {
            holder.senderText.gravity = Gravity.END
            holder.messageText.gravity = Gravity.END
        } else {
            holder.senderText.gravity = Gravity.START
            holder.messageText.gravity = Gravity.START
        }

        holder.senderText.text = message.sender
    }

    override fun getItemCount(): Int {
        return messages.size
    }
}