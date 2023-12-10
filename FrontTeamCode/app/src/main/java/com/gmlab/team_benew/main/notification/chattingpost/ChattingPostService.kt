package com.gmlab.team_benew.main.notification.chattingpost

import android.content.Context
import com.gmlab.team_benew.R
import com.gmlab.team_benew.auth.getRetrofit
import retrofit2.Response


class ChattingPostService {

    private lateinit var chattingPostView: ChattingPostView


    fun setChattingPostView(chattingPostView: ChattingPostView) {
        this.chattingPostView = chattingPostView
    }

    suspend fun chattingPost(context: Context,user1Id:Long, user2Id: Long): Response<ChattingPostResponse> {
        val token = getTokenFromSharedPreferences(context)
        val bearerToken = "Bearer $token"
        val service = getRetrofit().create(ChattingPostRetrofitInterface::class.java)
//동기로직
        return service.chatPost(bearerToken, user1Id, user2Id).execute()
    }

    private fun getTokenFromSharedPreferences(context: Context): String? {
        val sharedPref = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        return sharedPref.getString("userToken", null)
    }
}