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
import androidx.viewpager2.widget.ViewPager2
import com.gmlab.team_benew.R


class HomeFragment: Fragment(), MainView,UserNameCallback {

    private lateinit var viewPager: ViewPager2
    private lateinit var textIndicator: TextView
    private lateinit var textIndicatorData: TextView

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

        // ViewPager2와 텍스트 인디케이터 초기화
        viewPager = view.findViewById(R.id.vp_home_banner)
        textIndicator = view.findViewById(R.id.tv_home_banner_text_indicator)
        textIndicatorData = view.findViewById(R.id.tv_home_banner_text_indicator_data)

        // 이미지 리스트
        val imageList = listOf(
            R.drawable.home_banner_1,
            R.drawable.home_banner_2,
            R.drawable.home_banner_3,
            R.drawable.home_banner_4
        )

        // 어댑터 설정
        val adapter = HomeBannerImageAdapter(imageList)
        viewPager.adapter = adapter

        // 초기 페이지 설정
        viewPager.setCurrentItem(0, false)

        // 텍스트 인디케이터 초기화
        updateTextIndicator(0, imageList.size)

        // 페이지 변경 리스너 설정
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateTextIndicator(position, imageList.size)
            }
        })

        // PageTransformer 설정
        val pageWidth = resources.getDimension(R.dimen.viewpager_item_width)
        val pageMargin = resources.getDimension(R.dimen.viewpager_item_margin)
        val screenWidth = resources.displayMetrics.widthPixels
        val offsetPx = screenWidth - pageWidth - pageMargin

        viewPager.offscreenPageLimit = 3

        viewPager.setPageTransformer { page, position ->
            page.translationX = position * -offsetPx
        }

//        val pageMarginPx = resources.getDimensionPixelOffset(R.dimen.pageMargin)
//        val offsetPx = resources.getDimensionPixelOffset(R.dimen.offset)
//        viewPager.setPageTransformer { page, position ->
//            val myOffset = position * -(2 * offsetPx + pageMarginPx)
//            if (viewPager.orientation == ViewPager2.ORIENTATION_HORIZONTAL) {
//                if (ViewCompat.getLayoutDirection(viewPager) == ViewCompat.LAYOUT_DIRECTION_RTL) {
//                    page.translationX = -myOffset
//                } else {
//                    page.translationX = myOffset
//                }
//            } else {
//                page.translationY = myOffset
//            }
//        }


     val buttonNavProfile = view.findViewById<CardView>(R.id.cv_user_info_card)
//        val buttonNavProject = view.findViewById<CardView>(R.id.cv_project_info_card)
//        val buttonNavMyteamlist = view.findViewById<CardView>(R.id.cv_my_team_list)
//        val buttonNavTestIntro = view.findViewById<Button>(R.id.btn_do_test)

        // 모든 버튼에 같은 클릭 리스너 설정
        buttonNavProfile.setOnClickListener { onCardClicked(it) }
//        buttonNavProject.setOnClickListener { onCardClicked(it) }
//        buttonNavMyteamlist.setOnClickListener { onCardClicked(it) }
//        buttonNavTestIntro.setOnClickListener{ onCardClicked(it) }


    }

    private fun updateTextIndicator(position: Int, total: Int) {
        val currentPosition = (position % total) + 1
        textIndicatorData.text = currentPosition.toString()
        textIndicator.text = "/$total"

        if(currentPosition == 4) {
            textIndicatorData.setTextColor(resources.getColor(R.color.black, null))
            textIndicator.setTextColor(resources.getColor(R.color.black, null))
        }
        else{
            textIndicatorData.setTextColor(resources.getColor(R.color.white, null))
            textIndicator.setTextColor(resources.getColor(R.color.white, null))
        }
    }

    private fun onCardClicked(view: View) {
        when (view.id) {
            R.id.cv_user_info_card -> findNavController().navigate(R.id.action_home_to_profileDetail) // 프로필 디테일로 이동
//            R.id.cv_project_info_card -> findNavController().navigate(R.id.action_home_to_projectList) // 프로젝트 리스트로
//            R.id.cv_my_team_list -> findNavController().navigate(R.id.action_home_to_teamList) // 팀 리스트로
//            R.id.btn_do_test -> findNavController().navigate(R.id.action_home_to_intro_testing) // testing 화면으로
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
        val tvUserData_2 = view?.findViewById<TextView>(R.id.tv_username_data_2)
        tvUserData?.text = "${userName}님,"
        tvUserData_2?.text ="${userName}님의 프로필"
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