package com.gmlab.team_benew.chat.chatintro

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gmlab.team_benew.R

class ChatIntroChatListAdapter(private val chatIntroChatList: MutableList<ChatData>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_CHAT_ROOM = 1
    private val VIEW_TYPE_NO_CHAT_ROOM = 0

    override fun getItemViewType(position: Int): Int {
        return if (chatIntroChatList.isEmpty()) VIEW_TYPE_NO_CHAT_ROOM else VIEW_TYPE_CHAT_ROOM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_CHAT_ROOM) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_list_chat_fragment, parent, false)
            ChatDataViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_no_chat_list_chat_fragment, parent, false)
            NoChatRoomViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ChatDataViewHolder) {
            val chatData = chatIntroChatList[position]
            holder.roomName.text = chatData.roomName
        }
    }

    override fun getItemCount(): Int {
        return if (chatIntroChatList.isEmpty()) 1 else chatIntroChatList.size
    }

    class ChatDataViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val roomName: TextView = view.findViewById(R.id.tv_chat_list_item_user_name)
        val moreOptions: ImageView = view.findViewById(R.id.popup_chat_list_edit_or_disband)
        val addFriend: ImageView = view.findViewById(R.id.btn_add_freinds_this_chat_room)
    }

    class NoChatRoomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val message: TextView = view.findViewById(R.id.tv_no_chat_list_item_notice)
    }
}

data class ChatData(
    val projectId: Int,
    val roomId: String,
    val roomName: String
)
