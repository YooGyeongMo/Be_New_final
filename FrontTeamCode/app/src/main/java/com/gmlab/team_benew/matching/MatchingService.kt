package com.gmlab.team_benew.matching


import android.content.Context
import android.hardware.camera2.CaptureFailure
import android.os.Handler
import android.os.Looper
import android.service.autofill.FieldClassification.Match
import android.util.Log
import com.gmlab.team_benew.auth.getRetrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MatchingService private constructor(private val context: Context) {
    private lateinit var matchingPostView: MatchingPostView
    private lateinit var matchingAlarmsPostView: MatchingAlarmsPostView

    companion object {
        @Volatile private var instance: MatchingService? = null

        fun getInstance(context: Context): MatchingService {
            return instance ?: synchronized(this) {
                instance ?: MatchingService(context).also { instance = it }
            }
        }
    }

    fun setMatchingPostView(matchingPostView: MatchingPostView){
        this.matchingPostView = matchingPostView
    }

    fun setMatchingAlarmsPostView(matchingAlarmsPostView: MatchingAlarmsPostView){
        this.matchingAlarmsPostView = matchingAlarmsPostView
    }



    // 1개씩 Post 매칭 만들고 유저 데이터 받아오는 함수
    fun getUserData(matchRequestDto: MatchRequestDto, onResponse: (MatchingResponse) -> Unit){
        val token = getTokenFromSharedPreferences(context) ?: return
        val userid = getIdFromSharedPreferences(context) ?: return //문자열을 정수형으로 반환
        val bearerToken = "Bearer $token"
        val matchingPostService = getRetrofit().create(MatchingRetrofitInterface::class.java)

        // 데이터 불변성으로 유지하기위해 사용
        // MatchRequestDto에 사용자 ID 설정
        val updatedMatchRequestDto = matchRequestDto.copy(uid1 = userid)

        matchingPostService.postCreateMatch(bearerToken, updatedMatchRequestDto).enqueue(object: Callback<MatchingResponse> {
            override fun onResponse(call: Call<MatchingResponse>, response: Response<MatchingResponse>)
            {
                Log.d("NETWORK_MATCHING_USER_SUCCESS","USER_MATCHING_DATA_POST")
               when(response.code()) {
                   201 -> {
                       val matchingResponse = response.body()
                       matchingResponse?.let {
                           onResponse(it)
                           matchingPostView.onMatchingPostSuccess()
                       } ?: run {
                           Log.e("MatchingService", "Response body is null")
                       }
                   }
                   204 -> {
                       Log.e("MATHCING/POST/NOCONTENT","204,서버에서 보낼 유저가 없음")
                   }

                   401-> {
                       matchingPostView.onMatchingPostFailure()
                   }
                   else -> {
                       Log.e("MatchingService", "Error with response code: ${response.code()}")
                   }
               }
            }

            override fun onFailure(call: Call<MatchingResponse>, t: Throwable) {
                Log.e("NETWORK_MATCHING_USER_FAILURE","USER_MATCHING_DATA_FAILURE")
                // 실패시 처리 로직 호출
                matchingPostView.onMatchingRequestFailure()
            }
        })
    }

    fun likeMatch(matchId: Long, onResponse: (MatchingResponse) -> Unit) {
        val token = getTokenFromSharedPreferences(context) ?: return
        val bearerToken = "Bearer $token"

        val matchingLikePatchService = getRetrofit().create(MatchingRetrofitInterface::class.java)

        matchingLikePatchService.patchLikeMatch(bearerToken, matchId).enqueue(object : Callback<MatchingResponse>
        {
            override fun onResponse(call: Call<MatchingResponse>, response: Response<MatchingResponse>)
            {
                Log.d("NETWORK_MATCHING_PATCH_SUCCESS","USER_MATCHING_LIKE")

                when(response.code()){
                    200 -> {
                        val patchResponse = response.body()
                        patchResponse?.let{
                            onResponse(it)
                            matchingPostView.onMatchingLikePatchSuccess()
                        } ?: run {
                            Log.e("PATCHING/SERVICE", "Response body is null")
                        }
                    }
                    401 -> {
                        matchingPostView.onMatchingLikePatchFailure()
                    }

                    else -> {
                        Log.e("PatchService", "Error with response code: ${response.code()}")

                    }
                }
            }
            override fun onFailure(call: Call<MatchingResponse>, t: Throwable) {
                Log.d("NETWORK_MATCHING_PATCH_Failure","USER_MATCHING_LIKE")
            }
        })
    }

    fun disLikeMatch(matchId: Long, onResponse: (MatchingResponse) -> Unit){
        val token = getTokenFromSharedPreferences(context) ?: return
        val bearerToken = "Bearer $token"

        val matchingLikePatchService = getRetrofit().create(MatchingRetrofitInterface::class.java)

        matchingLikePatchService.patchDisLikeMatch(bearerToken, matchId).enqueue(object : Callback<MatchingResponse>
        {
            override fun onResponse(call: Call<MatchingResponse>, response: Response<MatchingResponse>)
            {
                Log.d("NETWORK_MATCHING_PATCH_SUCCESS","USER_MATCHING_DISLIKE")

                when(response.code()){
                    200 -> {
                        val patchResponse = response.body()
                        patchResponse?.let{
                            onResponse(it)
                            matchingPostView.onMatchingUnLikePatchSuccess()
                        } ?: run {
                            Log.e("PATCHING/SERVICE", "Response body is null")
                        }
                    }
                    401 -> {
                        matchingPostView.onMatchingUnLikePatchFailure()
                    }

                    else -> {
                        Log.e("PatchService", "Error with response code: ${response.code()}")
                    }
                }
            }

            override fun onFailure(call: Call<MatchingResponse>, t: Throwable) {
                Log.d("NETWORK_MATCHING_PATCH_Failure","USER_MATCHING_DISLIKE")
            }
        })

    }

    fun sendMatchingAlrams(matchingAlarmsRequest: MatchingAlarmRequest, onResponse: (MatchingAlarmResponse) -> Unit, onFailure: (Throwable)-> Unit){

        val getToken = getTokenFromSharedPreferences(context)
        val matchingAlramsSendService = getRetrofit().create(MatchingAlarmsRetrofitInterface:: class.java)
        val bearerToken = "Bearer ${getToken}"

        matchingAlramsSendService.matchingAlarmsSend(bearerToken, matchingAlarmsRequest).enqueue(object: Callback<MatchingAlarmResponse>{
            override fun onResponse(
                call: Call<MatchingAlarmResponse>,
                response: Response<MatchingAlarmResponse>
            ) {
                Log.d("NETWORK/SUCCESS/MATCHINGALRAMS","네트워크 매칭 알람 통신 성공")
                when(response.code()){
                    201 -> {
                        response.body()?.let{
                            onResponse(it)
                            matchingPostView.onMatchingPostSuccess()
                        }
                    }
                    401 ->{
                        matchingPostView.onMatchingPostFailure()
                    }
                    else -> {
                        Log.e("POSTSERVICE/POST/FAILURE", "Error with response code: ${response.code()}")
                    }

                }
            }

            override fun onFailure(call: Call<MatchingAlarmResponse>, t: Throwable) {
                Log.e("NETWORK/FAILURE/MATCHINGALRAMS","네트워크 매칭 알람 통신 실패")
            }


        })
    }


    private fun getTokenFromSharedPreferences(context: Context): String? {
        val sharedPref = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        return sharedPref.getString("userToken", null)
    }

    private fun getIdFromSharedPreferences(context: Context): Int? {
        val sharedPref = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        return sharedPref.getInt("loginId", -1).takeIf { it != -1 }
    }

}