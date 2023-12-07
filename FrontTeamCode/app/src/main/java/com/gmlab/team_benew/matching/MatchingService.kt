package com.gmlab.team_benew.matching


import android.content.Context
import android.util.Log
import com.gmlab.team_benew.auth.getRetrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MatchingService private constructor(private val context: Context) {
    private lateinit var matchingPostView: MatchingPostView

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
                Log.d("NETWORK_MATCHING_USER_FAILURE","USER_MATCHING_DATA_FAILURE")
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