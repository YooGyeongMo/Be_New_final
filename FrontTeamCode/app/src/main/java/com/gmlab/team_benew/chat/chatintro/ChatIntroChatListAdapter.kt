package com.gmlab.team_benew.chat.chatintro

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gmlab.team_benew.R

class ChatIntroChatListAdapter (private val chatIntroChatList: MutableList<ChatData>) : RecyclerView.Adapter<ChatIntroChatListAdapter.ChatDataViewHolder>(){

    class ChatDataViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val roomName: TextView = view.findViewById(R.id.tv_chat_list_item_user_name)
        val moreOptions: ImageView = view.findViewById(R.id.popup_chat_list_edit_or_disband)
        val addFriend: ImageView = view.findViewById(R.id.btn_add_freinds_this_chat_room)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatDataViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_list_chat_fragment, parent, false)
        return ChatDataViewHolder(view)
    }
    override fun onBindViewHolder(holder: ChatDataViewHolder, position: Int) {
        val chatData = chatIntroChatList[position]
        holder.roomName.text = chatData.roomName
        // 필요한 경우 더 많은 데이터 바인딩
    }

    override fun getItemCount(): Int {
        return chatIntroChatList.size
    }


}

data class ChatData(
    val projectId: Int,
    val roomId: String,
    val roomName: String
)