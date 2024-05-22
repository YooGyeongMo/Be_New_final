package com.gmlab.team_benew.main.home

import android.content.Context
import com.gmlab.team_benew.auth.AuthRetrofitInterface
import com.gmlab.team_benew.auth.getRetrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeService(private val context: Context) {
    private lateinit var homeView: HomeView

    fun setHomeView(homeView: HomeView){
        this.homeView = homeView
    }

    fun getUserProfilePreviewData() {
        val token = getTokenFromSharedPreferences()
        val memberId = getIdFromSharedPreferences(context)

        if (token != null && memberId != null) {
            val homeService = getRetrofit().create(HomeRetrofitInterface::class.java)
            val bearerToken = "Bearer $token"
            homeService.getProfile(bearerToken, memberId).enqueue(object :
                Callback<getProfilePreiviewData> {
                override fun onResponse(call: Call<getProfilePreiviewData>, response: Response<getProfilePreiviewData>) {
                    when (response.code()) {
                        200 -> {
                            response.body()?.let {
                                homeView.onHomeGetSuccess(it)
                            }
                        }
                        401 -> {
                            homeView.onHomeGetFailure()
                        }
                        else -> {
                            homeView.onHomeGetFailure()
                        }
                    }
                }

                override fun onFailure(call: Call<getProfilePreiviewData>, t: Throwable) {
                    homeView.onHomeGetFailure()
                }
            })
        } else {
            homeView.onHomeGetFailure()
        }
    }


    private fun getTokenFromSharedPreferences(): String? {
        val sharedPref = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        return sharedPref?.getString("userToken", null)
    }

    private fun getIdFromSharedPreferences(context: Context): Int? {
        val sharedPref = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        return sharedPref.getInt("loginId", -1).takeIf { it != -1 }
    }
}