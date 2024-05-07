package com.gmlab.team_benew.main

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gmlab.team_benew.R

class HomeFragment: Fragment(), MainView,UserNameCallback {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)


    }
    override fun onResume() {
        super.onResume()
        getUserInfo() // 사용자 정보를 새로고침하는 메서드 호출
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 여기서 사용자 정보를 가져옴
        getUserInfo()

        val buttonNavProfile = view.findViewById<CardView>(R.id.cv_user_info_card)
        val buttonNavProject = view.findViewById<CardView>(R.id.cv_project_info_card)
        val buttonNavMyteamlist = view.findViewById<CardView>(R.id.cv_my_team_list)
        val buttonNavTestIntro = view.findViewById<Button>(R.id.btn_do_test)

        // 모든 버튼에 같은 클릭 리스너 설정
        buttonNavProfile.setOnClickListener { onCardClicked(it) }
        buttonNavProject.setOnClickListener { onCardClicked(it) }
        buttonNavMyteamlist.setOnClickListener { onCardClicked(it) }
        buttonNavTestIntro.setOnClickListener{ onCardClicked(it) }



    }

    private fun onCardClicked(view: View) {
        when (view.id) {
            R.id.cv_user_info_card -> findNavController().navigate(R.id.action_home_to_profileDetail) // 프로필 디테일로 이동
            R.id.cv_project_info_card -> findNavController().navigate(R.id.action_home_to_projectList) // 프로젝트 리스트로
            R.id.cv_my_team_list -> findNavController().navigate(R.id.action_home_to_teamList) // 팀 리스트로
            R.id.btn_do_test -> findNavController().navigate(R.id.action_home_to_intro_testing) // testing 화면으로
        }
    }
    private fun getUserInfo() {
        val cachedUserName = getCachedUserName()
        if (cachedUserName == null) {
            // 서버에서 정보를 가져올 때까지 기본 텍스트를 표시하지 않음
            // 필요한 경우 로딩 인디케이터를 표시할 수 있습니다.
            val token = getTokenFromSharedPreferences()
            val account = getAccountFromSharedPreferences()
            if (token != null && account != null) {
                val homeService = MainAuthService(this)
                homeService.setMainView(this)
                homeService.setUserNameCallback(this)
                homeService.getUserName(token, account)
            }
        }
        else {
            // 캐시된 사용자 이름이 있으면 UI 업데이트
            updateUserNameUI(cachedUserName)
        }
    }
    override fun onUserNameReceived(userName: String) {
        val cachedUserName = getCachedUserName()
        if (cachedUserName != userName) {
            cacheUserName(userName)
        }
        updateUserNameUI(userName)

    }

    private fun updateUserNameUI(userName: String) {
        val tvUserData = view?.findViewById<TextView>(R.id.tv_username_data)
        tvUserData?.text = "${userName}님"
    }

    private fun cacheUserName(userName: String) {
        val sharedPref = activity?.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        sharedPref?.edit()?.putString("cachedUserName", userName)?.apply()
    }

    private fun getCachedUserName(): String? {
        val sharedPref = activity?.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        return sharedPref?.getString("cachedUserName", null)
    }

    private fun getTokenFromSharedPreferences(): String? {
        val sharedPref = activity?.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        return sharedPref?.getString("userToken", null)
    }

    // SharedPreferences에서 account 가져오는 함수
    private fun getAccountFromSharedPreferences(): String? {
        val sharedPref = activity?.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        return sharedPref?.getString("userAccount", null)
    }

    override fun onMainGetSuccess() {
        Log.d("USER/GET/SUCCESS","유저정보 획득성공 콜백성공")
    }

    override fun onMainGetFailure() {
        Log.d("USER/GET/FAILURE","유저정보 획득성공 콜백실패")
    }


}