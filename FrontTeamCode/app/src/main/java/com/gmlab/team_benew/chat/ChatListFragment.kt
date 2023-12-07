package com.gmlab.team_benew.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.gmlab.team_benew.R
import com.gmlab.team_benew.databinding.FragmentChatlistBinding

class ChatListFragment: Fragment() {
    val binding by lazy{ FragmentChatlistBinding.inflate(layoutInflater)}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val mymodellist=getMymodel()
        val adapter1= ChatlistAdapter()
        adapter1.modelList=mymodellist
        binding.myRecyclerViewChat.adapter=adapter1
        binding.myRecyclerViewChat.layoutManager= LinearLayoutManager(context)

        return binding.root

    }

    fun getMymodel():MutableList<ChatData_recycler>{
        var mymodellist= mutableListOf<ChatData_recycler>()
        for (i in 1..4) {
            val chatRoomName= "유경모님의 채팅방_$i"
            val mymodeldata= ChatData_recycler(chatRoomName)
            mymodellist.add(mymodeldata)
        }
        return mymodellist
    }
}