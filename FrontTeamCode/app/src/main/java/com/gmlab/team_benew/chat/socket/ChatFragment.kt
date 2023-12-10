package com.gmlab.team_benew.chat.socket

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gmlab.team_benew.R
import com.gmlab.team_benew.databinding.FragmentChatlistBinding
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okhttp3.logging.HttpLoggingInterceptor
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

class ChatFragment:Fragment() {
    private lateinit var btn_send : Button
    private lateinit var et_sendMsg : EditText
    private lateinit var  chatView : RecyclerView

    private lateinit var webSocket: WebSocket
    private lateinit var adapter: ChatAdapter
    private lateinit var roomId : String
    private lateinit var request: Request
    private val client: OkHttpClient
    private val chatList = ArrayList<ChatMessage>()

    val binding by lazy{ FragmentChatlistBinding.inflate(layoutInflater)}

    private val webSocketListener = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
            // 연결이 열렸을 때의 동작
            Log.d("CHAT", "소켓 연결")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            activity?.runOnUiThread {
                Log.d("CHAT", "Received message: $text")

                val sharedPref = context?.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                val memberId = sharedPref?.getInt("loginId", 0)

                val gson = Gson()
                val jsonMessage = gson.fromJson(text, onMessageData::class.java)

                val chatMessage = ChatMessage(
                    jsonMessage.senderName,
                    jsonMessage.message,
                    jsonMessage.sender.toInt(),
                    memberId
                )
                chatList.add(chatMessage)
                adapter.notifyItemInserted(chatList.size - 1)
                chatView.scrollToPosition(adapter.itemCount - 1)
            }
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            // 연결이 닫혔을 때의 동작
            // 예: "연결이 닫혔습니다. 코드: $code, 이유: $reason"
            Log.e("CHAT", "연결 닫힘, 코드 : $code, 이유 : $reason")
            requireActivity().supportFragmentManager.popBackStack()
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
            // 연결이 실패했을 때의 동작
            // 예: "연결 실패. 이유: ${t.message}"
            Log.e("CHAT", "실패 사유 : ${t.message}")
            requireActivity().supportFragmentManager.popBackStack()

        }
    }
    init {
        // OkHttpClient 설정
        val builder = OkHttpClient.Builder()
        builder.connectTimeout(10, TimeUnit.SECONDS) // 연결 시간 초과 설정
        builder.readTimeout(10, TimeUnit.SECONDS)    // 읽기 시간 초과 설정
        builder.writeTimeout(10, TimeUnit.SECONDS)   // 쓰기 시간 초과 설정

        // 로깅 인터셉터 추가 (필요에 따라)
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor(loggingInterceptor)

        client = builder.build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chatting, container, false)

        val sharedPref = context?.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)

        val token = sharedPref?.getString("userToken", "")
        val name = sharedPref?.getString("cachedUserName", "")
        val memberId = sharedPref?.getInt("loginId", 0)

        val getRoomId = (arguments?.getString("roomId")).toString()

        roomId = getRoomId

        btn_send = view.findViewById(R.id.btn_chatting_send)
        et_sendMsg = view.findViewById(R.id.et_chatting_sendMsg)
        chatView = view.findViewById(R.id.Recycler_chatting_chat)

        adapter = ChatAdapter(chatList)

        chatView.layoutManager = LinearLayoutManager(requireContext())
        chatView.adapter = adapter

        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        chatView.viewTreeObserver.addOnGlobalLayoutListener {
            chatView.scrollToPosition(adapter.itemCount - 1 )
        }

        request = Request.Builder()
            .url("ws://15.164.217.105:32000/ws/chat/$roomId")
            .build()

        webSocket = client.newWebSocket(request, webSocketListener)

        btn_send.setOnClickListener {
            if (token != null) {
                if (memberId != null) {
                    if (name != null) {
                        sendToJson(webSocket, token, memberId, name)
                        et_sendMsg.text.clear()
                    }
                }
            }
        }

        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendToJson(webSocket: WebSocket, token : String, sender : Int, senderName : String){
        var datetime = LocalDateTime.now().toString()

        val jsonMessage : String = """
           {
            "type" : "TALK",
            "roomId" : "$roomId",
            "sender" : "$sender",
            "senderName" : "$senderName",
            "message" : "${et_sendMsg.text}",
            "sendDate" : "$datetime"
            }
        """.trimIndent()

        webSocket.send(jsonMessage)
    }
}