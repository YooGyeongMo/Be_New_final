package com.gmlab.team_benew.main.notification

import com.gmlab.team_benew.matching.MatchingAlarmResponse
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.Path

interface MatchingAlarmsReadRetrofitInterface {

    //알람매칭수락
    @PATCH("/api/patch/match/success/{sender}/{receiver}")
    fun matchingAlarmsAccess(@Header("Authorization") bearerToken: String, @Path("sender") sender: Long,@Path("receiver") receiver: Long): Call<MatchingAlarmResponse>

    //알람매칭거절
    @PATCH("/api/patch/match/false/{sender}/{receiver}")
    fun matchingAlarmsReject(@Header("Authorization") bearerToken: String, @Path("sender") sender: Long,@Path("receiver") receiver: Long): Call<MatchingAlarmResponse>
}