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
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
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
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.concurrent.TimeUnit


class ChatFragment:Fragment(),ChatLogView {
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = context?.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val memberId = sharedPref?.getInt("loginId", 0) ?: 0  // 현재 사용자의 ID

        // Adapter 초기화
        adapter = ChatAdapter(chatList, memberId)

        // RecyclerView 설정
        chatView.layoutManager = LinearLayoutManager(requireContext())
        chatView.adapter = adapter

        // 현재 날짜 및 시간을 LocalDateTime 객체로 가져옴
        val currentDateTime = LocalDateTime.now()

// 날짜 및 시간을 "2023-12-11T06:33:41.423397" 형식으로 포맷팅
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
        val currentDate = currentDateTime.format(formatter)

// formattedDateTime에는 "2023-12-11T06:33:41.423397" 형식의 날짜 및 시간 정보가 포함됨

//        // SimpleDateFormat을 사용하여 현재 날짜를 "년-월-일" 형식으로 변환
//        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//        val currentDate = dateFormat.format(Date())

        // roomId는 번들에서 받아온 값을 사용
        val roomId = arguments?.getString("roomId") ?: ""

        // 채팅 로그 서비스 초기화 및 로그 가져오기 요청
        val chatLogService = ChatLogService()
        chatLogService.setChatLogView(this)
        chatLogService.getChatLog(requireContext(), currentDate, roomId)

    }

    private val webSocketListener = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
            // 연결이 열렸을 때의 동작
            Log.d("CHAT", "소켓 연결")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            activity?.runOnUiThread {
                Log.d("CHAT", "Received message: $text")

                val sharedPref = context?.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                val memberId = sharedPref?.getInt("loginId", 0)?: 0

                val gson = Gson()
                val jsonMessage = gson.fromJson(text, onMessageData::class.java)

//                val chatMessage = ChatMessage(
//                    jsonMessage.senderName,
//                    jsonMessage.message,
//                    jsonMessage.sender.toInt(),
//                    memberId
//                )
                val chatMessage = ChatMessage(
                    sender = jsonMessage.senderName,
                    message = jsonMessage.message,
                    userId = memberId,  // 현재 사용자의 ID
                    senderId = jsonMessage.sender,
                    senddate = jsonMessage.senddate
                )
                chatList.add(chatMessage)

                //소켓이
                adapter.notifyItemInserted(chatList.size - 1)
                chatView.scrollToPosition(chatList.size - 1)
            }
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            // 연결이 닫혔을 때의 동작
            // 예: "연결이 닫혔습니다. 코드: $code, 이유: $reason"
            Log.e("CHAT", "연결 닫힘, 코드 : $code, 이유 : $reason")
            findNavController().navigateUp()
            // AlertDialog 생성 및 표시
            AlertDialog.Builder(requireContext()).apply {
                setTitle("연결 닫힘")
                setMessage("웹소켓 연결이 닫혔습니다.")
                setPositiveButton("확인") { dialog, which ->
                    // 여기서 아무 것도 하지 않음
                }
                create()
                show()
            }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
            // 연결이 실패했을 때의 동작
            // 예: "연결 실패. 이유: ${t.message}"
            Log.e("CHAT", "실패 사유 : ${t.message}")
            findNavController().navigateUp()
            // AlertDialog 생성 및 표시
            AlertDialog.Builder(requireContext()).apply {
                setTitle("네트워크 오류")
                setMessage("네트워크 요청이 실패했습니다.")
                setPositiveButton("확인") { dialog, which ->
                    // 여기서 아무 것도 하지 않음
                }
                create()
                show()
            }

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
        val memberId = sharedPref?.getInt("loginId", 0) ?: 0

        val getRoomId = (arguments?.getString("roomId")).toString()

        roomId = getRoomId

        btn_send = view.findViewById(R.id.btn_chatting_send)
        et_sendMsg = view.findViewById(R.id.et_chatting_sendMsg)
        chatView = view.findViewById(R.id.Recycler_chatting_chat)

        adapter = ChatAdapter(chatList, memberId)

        chatView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)

        chatView.adapter = adapter

        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        chatView.viewTreeObserver.addOnGlobalLayoutListener {
            //chatView.scrollToPosition(adapter.itemCount - 1 )
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

    override fun onGetChattingLogSuccess(chatLogs: MutableList<ChatMessage>) {
        chatList.clear()

        val sortedChatLogs = chatLogs.sortedByDescending { it.senddate }

        chatList.addAll(sortedChatLogs.reversed())
        adapter.notifyDataSetChanged()
        chatView.scrollToPosition(chatList.size - 1) // 최신 메시지로 스크롤
    }

    override fun onGetChattingLogFailure() {

        findNavController().navigateUp()
        // AlertDialog 생성 및 표시
        AlertDialog.Builder(requireContext()).apply {
            setTitle("사용자 채팅 로그 불러오기 네트워크 오류")
            setMessage("네트워크 요청이 실패했습니다.")
            setPositiveButton("확인") { dialog, which ->
                // 여기서 아무 것도 하지 않음
            }
            create()
            show()
        }
        Log.d("CHATTING/POST/실패", "유저에 대한 채팅방 만들기 실패")
    }
}