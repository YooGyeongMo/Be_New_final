package com.gmlab.team_benew.chat.chatintro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gmlab.team_benew.R

class ChatIntroFragment : Fragment() {

    private lateinit var tv_menu_bar_1: TextView
    private lateinit var tv_menu_bar_2: TextView
    private lateinit var tv_type_welcome: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var friendsAdapter: ChatIntroFriendsListAdapter
    private lateinit var chatAdapter: ChatIntroChatListAdapter
    private lateinit var ivAddFriendOrChat: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat_intro, container, false)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_menu_bar_1 = view.findViewById(R.id.menu_bar_friend_list)
        tv_menu_bar_2 = view.findViewById(R.id.menu_bar_chatting_list)
        tv_type_welcome = view.findViewById(R.id.tv_chat_intro_type_check)
        recyclerView = view.findViewById(R.id.rc_freinds_list_or_chat_list)
        ivAddFriendOrChat = view.findViewById(R.id.btn_add_friends_or_add_chat)

        val friendsList = generateDummyFriendsList().toMutableList()
        friendsAdapter = ChatIntroFriendsListAdapter(friendsList)

        val chatList = generateDummyChatList().toMutableList()
        chatAdapter = ChatIntroChatListAdapter(chatList)

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = friendsAdapter

        tv_menu_bar_1.setOnClickListener {
            tv_menu_bar_1.setTextColor(ContextCompat.getColor(requireContext(), R.color.mainBlue2))
            tv_menu_bar_2.setTextColor(ContextCompat.getColor(requireContext(), R.color.mainNewBg))
            tv_type_welcome.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            // ImageView에 drawable 설정
            ivAddFriendOrChat.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.person_add))
            tv_type_welcome.text = "친구 목록"
            recyclerView.adapter = friendsAdapter
        }

        tv_menu_bar_2.setOnClickListener {
            tv_menu_bar_1.setTextColor(ContextCompat.getColor(requireContext(), R.color.mainNewBg))
            tv_menu_bar_2.setTextColor(ContextCompat.getColor(requireContext(), R.color.mainBlue2))
            tv_type_welcome.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            // ImageView에 drawable 설정
            ivAddFriendOrChat.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.chat_add))
            tv_type_welcome.text = "채팅 목록"
            recyclerView.adapter = chatAdapter
        }
    }

    private fun generateDummyChatList(): List<ChatData> {
        return List(10) { index -> ChatData(index, "roomId_$index", "채팅방 $index") }
    }

    private fun generateDummyFriendsList(): List<Friend> {
        return List(10) { index -> Friend("친구 $index", R.drawable.male_avatar) }
    }
}