package com.gmlab.team_benew.chat

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.gmlab.team_benew.R
import com.gmlab.team_benew.auth.getRetrofit
import com.gmlab.team_benew.chat.retrofit.ChatListRoomGet_Interface
import com.gmlab.team_benew.chat.retrofit.ChatRoomListModelItem
import com.gmlab.team_benew.chat.retrofit.chatdata
import com.gmlab.team_benew.databinding.FragmentChatlistBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatListFragment: Fragment() {

    private var listSize:Int=0
    //private lateint var recyclerView: RecyclerView

    //private var getMymodel?
    //val binding by lazy{ FragmentChatlistBinding.inflate(layoutInflater)}


    private var binding:FragmentChatlistBinding?=null

    override fun onCreateView(//뷰가 처음 시작될 때
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState:Bundle?
    ): View?{
       //binding = FragmentChatlistBinding.inflate(inflater,container,false)
        //return binding?.root
        binding = FragmentChatlistBinding.inflate(inflater, container, false)
        return binding?.root
        //return inflater.inflate(R.layout.fragment_chatlist, container, false)
        //val binding=FragmentChatlistBinding.inflate(inflater,container,false)
        //return binding.root
        //val mymodellist=  //여기에 레트로핏 통신한 걸 리턴해야함
        //val adapter1 = ChatlistAdapter()//어댑터 적용
        //adapter1.modelList=mymodellist
        //binding.myRecyclerViewChat.adapter=adapter1
        //binding.myRecyclerViewChat.layoutManager= LinearLayoutManager(context)
        //return binding.root
       // return inflater.inflate(R.layout.fragment_chatlist, container, false)
        //loadData()

        //return binding.root
    }


    //override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    // super.onViewCreated(view, savedInstanceState)

    // setAdapter()


    // }

    private fun setAdapter(chatList:List<chatdata>){
        val chatAdapter=ChatlistAdapter(chatList,requireContext())//여기서 데이터를 바인딩 함
        binding?.myRecyclerViewChat?.adapter=chatAdapter
        binding?.myRecyclerViewChat?.layoutManager=LinearLayoutManager(requireContext())
    }
    //카드뷰 동적으로 생성하기
    //여기서 레트로핏 통신으로 get 해서 데이터 불러오기



    //chatroomlist가져오는 함수
    private fun getChatRoomList(userId: Int) {
        val token = getTokenFromSharedPreferences()
        val service = getRetrofit().create(ChatListRoomGet_Interface::class.java)
        if (userId != null && token != null) {
            val call: Call<List<ChatRoomListModelItem>> = service.getChatRoomList("Bearer $token", userId)
            call.enqueue(object : Callback<List<ChatRoomListModelItem>> {
                override fun onResponse(
                    call: Call<List<ChatRoomListModelItem>>,
                    response: Response<List<ChatRoomListModelItem>>
                ) {
                    when (response.code()) {
                        200 -> {
                            val jsonStringList:List<ChatRoomListModelItem>?=response.body()

                            jsonStringList?.let{
                                val chatDataList= mutableListOf<chatdata>()

                                for(chatRoomItem in it){

                                    val chatItem=chatdata(roomId=chatRoomItem.roomId, name=chatRoomItem.name)
                                    chatDataList.add(chatItem)
                                    // 변환된 데이터 리스트를 어댑터에 전달
                                    setAdapter(chatDataList)
                                }
                            }



                        }
                        else -> {
                            // 다른 HTTP 응답 코드에 대한 처리
                            Log.e("코드가 200이 아님 ", "Response Code: ${response.code()}")
                        }
                    }
                }

                override fun onFailure(call: Call<List<ChatRoomListModelItem>>, t: Throwable) {
                    Log.e("NETWORK/ERROR/FAILURE", "CHATLIST/GET/실패")
                }
            })
        }
    }


//여기까지가 레트로핏 통신 함수임
    private fun getLoginIdFromSharedPreferences():Int?{
        val sharedPref=activity?.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        return sharedPref?.getInt("loginId",-1)

    }
    private fun getTokenFromSharedPreferences(): String? {
        val sharedPref = activity?.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        return sharedPref?.getString("userToken", null)
    }

    // SharedPreferences에서 account 가져오는 함수
    private fun getAccountFromSharedPreferences(): String? {
        val sharedPref = activity?.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        return sharedPref?.getString("userAccount", null)
    }

}