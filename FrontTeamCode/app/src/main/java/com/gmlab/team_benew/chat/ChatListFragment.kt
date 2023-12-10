package com.gmlab.team_benew.chat

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gmlab.team_benew.R
import com.gmlab.team_benew.auth.getRetrofit
import com.gmlab.team_benew.chat.retrofit.ChatListRoomGet_Interface
import com.gmlab.team_benew.chat.retrofit.ChatRoomListModelItem
import com.gmlab.team_benew.chat.retrofit.chatdata
import com.gmlab.team_benew.databinding.FragmentChatlistBinding
import com.gmlab.team_benew.main.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatListFragment: Fragment() {
    private lateinit var chatAdapter:ChatlistAdapter

    private var listSize:Int=0

    //val binding by lazy{ FragmentChatlistBinding.inflate(layoutInflater)}

    private var binding:FragmentChatlistBinding?=null

    private var userId: Int? = null
    private var token : String? = null
    override fun onCreateView(//뷰가 처음 시작될 때, 일단 얘는 앱에서 실행됨
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState:Bundle?
    ): View?{
        Log.d("oncreateview","oncreateview 시작")

        //binding = FragmentChatlistBinding.inflate(inflater,container,false)
        //return binding?.root
        binding = FragmentChatlistBinding.inflate(inflater, container, false)
        return binding?.root

    }


    //onViewCreated는 onCreateView에 의해 완전히 생성된 뷰가 준비된 후 호출되므로, 이 메서드에서 뷰와 관련된 초기화 및 설정 작업을 수행ㅐㅜㅍ
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ChatlistAdapter 생성시 NavController에 전달
        chatAdapter = ChatlistAdapter(findNavController())

        //RecyclerView 설정
        binding?.myRecyclerViewChat?.apply {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

       //Log.d("onViewCreated","onViewCreated")

        //Log.d("이제 람다함수 실행됨","이제 람다함수 실행됨")

        /*chatAdapter= ChatlistAdapter {
            chatItem->
            val bundle=Bundle().apply{
                putString("roomId",chatItem.roomId)
            }
            val chatFragment=ChatFragment().apply {
                arguments=bundle
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.Fragment_container,chatFragment)
                .addToBackStack(null)
                .commit()
        }
        Log.d("전환됨","전환됨")*/

       /*if(isAdded){
            chatAdapter=ChatlistAdapter()
            binding?.myRecyclerViewChat?.apply{
                adapter = chatAdapter
                //binding?.myRecyclerViewChat?.layoutManager=LinearLayoutManager(requireContext())
                layoutManager=LinearLayoutManager(requireContext())
            }
        }*/
        /*binding?.myRecyclerViewChat?.apply {
            //adapter=chatAdapter
            adapter=this@ChatListFragment.chatAdapter
            layoutManager=LinearLayoutManager(requireContext())
        }*/
        val sharedPref = context?.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)

        token = sharedPref?.getString("userToken", "")
        userId = sharedPref?.getInt("loginId", 0)

     userId?.let{

         getChatRoomList(it)
     }
    }

    //chatroomlist가져오는 함수
    private fun getChatRoomList(userId: Int) {

        val service = getRetrofit().create(ChatListRoomGet_Interface::class.java)
        if (userId != null && token != null) {
            val call: Call<List<ChatRoomListModelItem>> = service.getChatRoomList("Bearer $token", userId)
            call.enqueue(object : Callback<List<ChatRoomListModelItem>> {
                override fun onResponse(
                    call: Call<List<ChatRoomListModelItem>>,
                    response: Response<List<ChatRoomListModelItem>>
                ) {
                    Log.d("network success","통신 성공")
                    when (response.code()) {
                        200 -> {

                            val jsonStringList:List<ChatRoomListModelItem>?=response.body()

                            jsonStringList?.let{
                                val chatDataList= mutableListOf<chatdata>()

                                for(chatRoomItem in it){

                                    val chatItem=chatdata(roomId=chatRoomItem.roomId, name=chatRoomItem.name)
                                    chatDataList.add(chatItem)

                                    updateUIChatlst(chatDataList)
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

    fun updateUIChatlst(chatList: MutableList<chatdata>)
    {
        chatAdapter.modelList = chatList
        chatAdapter.notifyDataSetChanged()
    }


//여기까지가 레트로핏 통신 함수임
    private fun getLoginIdFromSharedPreferences():Int?{
        val sharedPref=context?.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        return sharedPref?.getInt("loginId",-1)
    }
    private fun getTokenFromSharedPreferences(): String? {
        val sharedPref = context?.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        return sharedPref?.getString("userToken", null)
    }

    // SharedPreferences에서 account 가져오는 함수
    private fun getAccountFromSharedPreferences(): String? {

        val sharedPref = context?.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        return sharedPref?.getString("userAccount", null)
    }

}