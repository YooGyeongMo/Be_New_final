package com.gmlab.team_benew.main.mypage

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path

interface putMypagePhoneNumberRequest {
    @PUT("/user/update/{id}")
    fun putMypagePhoneNumber(
        @Header("Authorization") token: String,
        @Path("id") memberId: Int,
        @Body request: putMypagePhoneNumberData
    ): Call<String>
}