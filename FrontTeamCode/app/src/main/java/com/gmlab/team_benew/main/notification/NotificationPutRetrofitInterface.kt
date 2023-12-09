package com.gmlab.team_benew.main.notification

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path

interface NotificationPutRetrofitInterface {

    @PUT("/alarms/{userId}/{alarmId}/read")
    fun alarmsPut(@Header("Authorization") bearerToken: String,
                  @Path("userId") userId: Long,
                  @Path("alarmId") alarmId: Long): Call<ResponseBody>

}