package com.gmlab.team_benew.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.gmlab.team_benew.R

class ChatFragment:Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_chatting, container, false)
    }

    //override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //super.onViewCreated(view, savedInstanceState)

        //val room=arguments?.getString("roomId") 이거는 chatFragment 에서 putstring한 roomId가져오는거

       // val roomIdTextView=view.findViewById<TextView>(R.id.hello) 이거랑 밑에 줄은 가져온 roomId를 fragment_chat에 띄워보는 거
    //roomIdTextView.text=room
   // }

}