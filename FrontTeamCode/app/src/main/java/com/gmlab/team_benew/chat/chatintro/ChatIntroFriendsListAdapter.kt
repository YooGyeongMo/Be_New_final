package com.gmlab.team_benew.chat.chatintro

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gmlab.team_benew.R

class ChatIntroFriendsListAdapter(private val friendsList: MutableList<Friend>) : RecyclerView.Adapter<ChatIntroFriendsListAdapter.FriendViewHolder>() {

    class FriendViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val profileImage: ImageView = view.findViewById(R.id.iv_profile_friends_pic)
        val userName: TextView = view.findViewById(R.id.tv_friends_list_item_user_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_freinds_list_chat_fragment, parent, false)
        return FriendViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val friend = friendsList[position]
        holder.profileImage.setImageResource(friend.profileImageResId)
        holder.userName.text = friend.name
    }

    override fun getItemCount() = friendsList.size

    fun addFriend(friend: Friend) {
        friendsList.add(friend)
        notifyItemInserted(friendsList.size - 1)
    }

    fun removeFriend(position: Int) {
        if (position >= 0 && position < friendsList.size) {
            friendsList.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}

data class Friend(
    val name: String,
    val profileImageResId: Int
)