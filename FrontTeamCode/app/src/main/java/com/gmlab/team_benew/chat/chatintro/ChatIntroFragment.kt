package com.gmlab.team_benew.chat.chatintro

import ChatData
import ChatIntroChatListAdapter
import ChatIntroFriendsListAdapter
import Friend
import FriendResponse
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gmlab.team_benew.R
import com.gmlab.team_benew.chat.socket.ChatFragment

class ChatIntroFragment : Fragment(), ChatIntroView {

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

        chatAdapter = ChatIntroChatListAdapter(requireContext(), mutableListOf(), object : ChatIntroChatListAdapter.OnItemClickListener {

            override fun onChatRoomClick(chatData: ChatData) {
                val bundle = Bundle().apply {
                    putString("roomId", chatData.roomId)
                    putString("roomName", chatData.roomName)
                }

                findNavController().navigate(R.id.action_chatListFragment_to_chatRoomFragment, bundle)
            }

            override fun onAddFriendClick(chatData: ChatData) {
                showAddChatModal()
            }
        })

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = friendsAdapter

        viewModel.setView(this)

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

        ivAddFriendOrChat.setOnClickListener {
            if (recyclerView.adapter == friendsAdapter) {
                showAddFriendModal()
            } else if (recyclerView.adapter == chatAdapter) {

            }
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
        recyclerView.adapter = friendsAdapter
    }

    private fun setChatList() {
        tv_menu_bar_1.setTextColor(ContextCompat.getColor(requireContext(), R.color.mainNewBg))
        tv_menu_bar_2.setTextColor(ContextCompat.getColor(requireContext(), R.color.mainBlue2))
        tv_type_welcome.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        ivAddFriendOrChat.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.chat_add))
        tv_type_welcome.text = "채팅 목록"
        recyclerView.adapter = chatAdapter
    }

    override fun onGetChatRoomsSuccess(chatRooms: List<ChatData>) {
        chatAdapter.updateChatList(if (chatRooms.isEmpty()) {
            mutableListOf(ChatData(0, "", ""))
        } else {
            chatRooms.toMutableList()
        })
        recyclerView.adapter = chatAdapter
        progressBar.visibility = View.GONE
    }

    override fun onGetChatRoomsFailure(message: String) {
        showFailureDialog(message)
        progressBar.visibility = View.GONE
    }

    override fun onGetFriendsListSuccess(friends: List<FriendResponse>) {
        val myUserId = getUserIdFromSharedPreferences().toLong()
        val nonNullFriends = friends.map {

            if(it.profile.id == myUserId){
                Friend(
                    name = it.friendProfile.member.name,
                    profileImageUrl = it.friendProfile.photo // URL을 사용하여 이미지를 로드
                )
            }
            else {
                Friend(
                    name = it.profile.member.name,
                    profileImageUrl = it.profile.photo
                )
            }

        }
        friendsAdapter.updateFriendsList(nonNullFriends)
        recyclerView.adapter = friendsAdapter
        progressBar.visibility = View.GONE
    }

    override fun onGetFriendsListFailure(message: String) {
        showFailureDialog(message)
        progressBar.visibility = View.GONE
    }

    override fun onUnauthorized() {
        showFailureDialog("Unauthorized: Please login again.")
        progressBar.visibility = View.GONE
    }

    override fun onForbidden() {
        showFailureDialog("Forbidden: You do not have permission to access this resource.")
        progressBar.visibility = View.GONE
    }

    override fun onNotFound() {
        showFailureDialog("Not Found: The requested resource could not be found.")
        progressBar.visibility = View.GONE
    }

    private fun showAddFriendModal() {
        val builder = AlertDialog.Builder(requireContext())
        val view = LayoutInflater.from(context).inflate(R.layout.modal_add_friends_chatting_room, null)
        builder.setView(view)

        val dialog = builder.create()

        val btnAddFriends = view.findViewById<Button>(R.id.btn_add_friends)
        val btnCancel = view.findViewById<Button>(R.id.btn_cancel_add_friends)

        btnAddFriends.setOnClickListener {
            // 친구 추가 로직 구현
            dialog.dismiss()
            showCompletionDialog("친구 추가 완료!")
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showCompletionDialog(message: String) {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("친구 추가")
            setMessage(message)
            setPositiveButton("확인") { dialog, _ -> dialog.dismiss() }
            create()
            show()
        }
    }

    private fun showAddChatModal() {
        val builder = AlertDialog.Builder(requireContext())
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_add_frined_and_searching, null)
        builder.setView(view)

        val dialog = builder.create()

        val recyclerViewChatInviteList = view.findViewById<RecyclerView>(R.id.rc_chat_friend_list)
        recyclerViewChatInviteList.layoutManager = LinearLayoutManager(context)
        recyclerViewChatInviteList.adapter = friendsAdapter


        val btnInviteFriend = view.findViewById<Button>(R.id.btn_add_friends_in_chat_list)
        val btnCancel = view.findViewById<Button>(R.id.btn_cancel_add_friends_in_chat_list)

        btnInviteFriend.setOnClickListener {
            // 채팅방 친구 초대
            dialog.dismiss()
            showCompletionInviteDialog("친구 초대 완료!")
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
    private fun showCompletionInviteDialog(message: String) {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("친구 초대")
            setMessage(message)
            setPositiveButton("확인") { dialog, _ -> dialog.dismiss() }
            create()
            show()
        }
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
