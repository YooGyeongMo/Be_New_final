package com.gmlab.team_benew.chat.chatintro

import android.content.Context
import android.util.Log
import com.gmlab.team_benew.auth.getRetrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatIntroService(private val context: Context) {

    interface ServiceCallback<T> {
        fun onSuccess(data: T)
        fun onFailure(message: String)
    }

    fun getChatRooms(userId: Int, callback: ServiceCallback<List<ChatData>>) {
        val token = getTokenFromSharedPreferences()
        if (token != null) {
            val bearerToken = "Bearer $token"
            val service = getRetrofit().create(ChatIntroRetrofitInterface::class.java)
            service.getChatRooms(bearerToken, userId).enqueue(object : Callback<List<ChatData>> {
                override fun onResponse(call: Call<List<ChatData>>, response: Response<List<ChatData>>) {
                    when (response.code()) {
                        200 -> callback.onSuccess(response.body() ?: listOf())
                        401, 403, 404 -> callback.onFailure("Error: ${response.code()}")
                        else -> callback.onFailure("Unknown error: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<List<ChatData>>, t: Throwable) {
                    callback.onFailure(t.message ?: "Network error")
                }
            })
        } else {
            callback.onFailure("Token not found")
        }
    }

    fun getFriendsList(memberId: Long, callback: ServiceCallback<List<FriendResponse>>) {
        val token = getTokenFromSharedPreferences()
        if (token != null) {
            val bearerToken = "Bearer $token"
            val service = getRetrofit().create(ChatIntroRetrofitInterface::class.java)
            service.getFriendsList(bearerToken, memberId).enqueue(object : Callback<List<FriendResponse>> {
                override fun onResponse(call: Call<List<FriendResponse>>, response: Response<List<FriendResponse>>) {
                    when (response.code()) {
                        200 -> callback.onSuccess(response.body() ?: listOf())
                        401, 403, 404 -> callback.onFailure("Error: ${response.code()}")
                        else -> callback.onFailure("Unknown error: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<List<FriendResponse>>, t: Throwable) {
                    callback.onFailure(t.message ?: "Network error")
                }
            })
        } else {
            callback.onFailure("Token not found")
        }
    }

    private fun getTokenFromSharedPreferences(): String? {
        val sharedPref = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        return sharedPref.getString("userToken", null)
    }
}
