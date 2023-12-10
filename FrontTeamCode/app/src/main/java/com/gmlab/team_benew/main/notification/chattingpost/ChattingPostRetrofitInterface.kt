package com.gmlab.team_benew.main.notification.chattingpost

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ChattingPostRetrofitInterface {

    @POST("chat/new/{user1}/{user2}")
    fun chatPost(@Header("Authorization") beaererToken: String,
                 @Path("user1") user1: Long,
                 @Path("user2") user2: Long
    ): Call<ChattingPostResponse>
}