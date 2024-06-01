package com.gmlab.team_benew.chat.chatintro

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
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
    private lateinit var progressBar: ProgressBar

    private val viewModel: ChatIntroViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat_intro, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_menu_bar_1 = view.findViewById(R.id.menu_bar_friend_list)
        tv_menu_bar_2 = view.findViewById(R.id.menu_bar_chatting_list)
        tv_type_welcome = view.findViewById(R.id.tv_chat_intro_type_check)
        recyclerView = view.findViewById(R.id.rc_freinds_list_or_chat_list)
        ivAddFriendOrChat = view.findViewById(R.id.btn_add_friends_or_add_chat)
        progressBar = view.findViewById(R.id.chat_intro_fragment_loading_indicator)

        friendsAdapter = ChatIntroFriendsListAdapter(mutableListOf())
        chatAdapter = ChatIntroChatListAdapter(mutableListOf())

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = friendsAdapter

        // ViewModel의 LiveData 관찰
        viewModel.loading.observe(viewLifecycleOwner, Observer { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        viewModel.friendsList.observe(viewLifecycleOwner, Observer { friends ->
            friendsAdapter = ChatIntroFriendsListAdapter(friends.map { it.friend }.toMutableList())
            recyclerView.adapter = friendsAdapter
            progressBar.visibility = View.GONE
        })

        viewModel.chatRooms.observe(viewLifecycleOwner, Observer { chatRooms ->
            chatAdapter = if (chatRooms.isEmpty()) {
                ChatIntroChatListAdapter(mutableListOf(ChatData(0, "", "채팅방이 없습니다. 새로운 채팅방을 만드세요.")))
            } else {
                ChatIntroChatListAdapter(chatRooms.toMutableList())
            }
            recyclerView.adapter = chatAdapter
            progressBar.visibility = View.GONE
        })

        viewModel.error.observe(viewLifecycleOwner, Observer { message ->
            message?.let { showFailureDialog(it) }
            progressBar.visibility = View.GONE
        })

        // 최초 로드 시 친구 목록을 가져옵니다.
        val userId = getUserIdFromSharedPreferences()
        viewModel.getFriendsList(userId.toLong())
        setFriendList()

        tv_menu_bar_1.setOnClickListener {
            setFriendList()
            progressBar.visibility = View.VISIBLE
            viewModel.getFriendsList(userId.toLong())
        }

        tv_menu_bar_2.setOnClickListener {
            setChatList()
            progressBar.visibility = View.VISIBLE
            viewModel.getChatRooms(userId)
        }
    }

    override fun onResume() {
        super.onResume()
        val userId = getUserIdFromSharedPreferences()
        viewModel.getFriendsList(userId.toLong())
        setFriendList()
    }

    private fun setFriendList() {
        tv_menu_bar_1.setTextColor(ContextCompat.getColor(requireContext(), R.color.mainBlue2))
        tv_menu_bar_2.setTextColor(ContextCompat.getColor(requireContext(), R.color.mainNewBg))
        tv_type_welcome.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        ivAddFriendOrChat.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.person_add))
        tv_type_welcome.text = "친구 목록"
    }

    private fun setChatList() {
        tv_menu_bar_1.setTextColor(ContextCompat.getColor(requireContext(), R.color.mainNewBg))
        tv_menu_bar_2.setTextColor(ContextCompat.getColor(requireContext(), R.color.mainBlue2))
        tv_type_welcome.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        ivAddFriendOrChat.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.chat_add))
        tv_type_welcome.text = "채팅 목록"
    }

    private fun showFailureDialog(message: String) {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Error")
            setMessage(message)
            setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            create()
            show()
        }
    }

    private fun getUserIdFromSharedPreferences(): Int {
        val sharedPref = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        return sharedPref.getInt("loginId", -1).takeIf { it != -1 } ?: 0
    }
}
