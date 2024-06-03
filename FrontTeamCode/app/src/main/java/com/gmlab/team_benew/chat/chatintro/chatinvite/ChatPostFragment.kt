package com.gmlab.team_benew.chat.chatintro.chatinvite

import ChatIntroFriendsListAdapter
import Friend
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gmlab.team_benew.R
import com.gmlab.team_benew.chat.chatintro.ChatIntroViewModel


class ChatPostFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var friendsAdapter: ChatIntroFriendsListAdapter

    private val viewModel: ChatIntroViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.rc_chat_post_friend_list)
        friendsAdapter = ChatIntroFriendsListAdapter(mutableListOf())

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = friendsAdapter

        // ViewModel에서 친구 목록을 가져오는 로직
        val userId = getUserIdFromSharedPreferences()
        viewModel.getFriendsList(userId.toLong())

        // ViewModel의 데이터를 관찰하여 업데이트
        viewModel.friendsList.observe(viewLifecycleOwner, Observer { friends ->
            val nonNullFriends = friends.map {
                Friend(
                    name = it.profile.member.name,
                    profileImageUrl = it.profile.photo
                )
            }
            friendsAdapter.updateFriendsList(nonNullFriends)
        })

        viewModel.loading.observe(viewLifecycleOwner, Observer { isLoading ->
            // 로딩 상태를 처리합니다.
        })

        viewModel.error.observe(viewLifecycleOwner, Observer { error ->
            // 에러 상태를 처리합니다.
        })
    }

    private fun getUserIdFromSharedPreferences(): Int {
        val sharedPref = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        return sharedPref.getInt("loginId", -1).takeIf { it != -1 } ?: 0
    }
}
