package com.gmlab.team_benew.chat.chatintro

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gmlab.team_benew.R

class ChatIntroFriendsListAdapter(private val friendsList: MutableList<Friend>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_FRIEND = 1
    private val VIEW_TYPE_NO_FRIEND = 0

    override fun getItemViewType(position: Int): Int {
        return if (friendsList[position].id == 0) VIEW_TYPE_NO_FRIEND else VIEW_TYPE_FRIEND
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_FRIEND) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_freinds_list_chat_fragment, parent, false)
            FriendViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_no_friends_list_chat_fragment, parent, false)
            NoFriendViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is FriendViewHolder) {
            val friend = friendsList[position]
            holder.profileImage.setImageResource(friend.profileImageResId)
            holder.userName.text = friend.name
        } else if (holder is NoFriendViewHolder) {
            holder.message.text = friendsList[position].name
        }
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

    class FriendViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val profileImage: ImageView = view.findViewById(R.id.iv_profile_friends_pic)
        val userName: TextView = view.findViewById(R.id.tv_friends_list_item_user_name)
    }

    class NoFriendViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val message: TextView = view.findViewById(R.id.tv_no_friends_list_item_user_name)
    }
}

data class Friend(
    val name: String,
    val profileImageResId: Int,
    val id: Int,
    val account: String,
    val email: String,
    val gender: String,
    val major: String,
    val password: String,
    val phoneNumber: String
)

data class FriendResponse(
    val friend: Friend
)
