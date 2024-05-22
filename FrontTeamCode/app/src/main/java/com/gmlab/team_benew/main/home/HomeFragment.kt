package com.gmlab.team_benew.main.home

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.gmlab.team_benew.R
import com.gmlab.team_benew.main.MainAuthService
import com.gmlab.team_benew.main.MainView
import com.gmlab.team_benew.main.UserNameCallback
import java.io.ByteArrayOutputStream


class HomeFragment: Fragment(), MainView, UserNameCallback, HomeView {

    private lateinit var viewPager: ViewPager2
    private lateinit var textIndicator: TextView
    private lateinit var textIndicatorData: TextView
    private lateinit var  homeService: HomeService
    private lateinit var profileImageView: ImageView
    private lateinit var peerImageView: ImageView
    private lateinit var loadingIndicator: ProgressBar

    private val homeViewModel: HomeViewModel by viewModels()

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
        getUserProfileInfo()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewPager = view.findViewById(R.id.vp_home_banner)
        textIndicator = view.findViewById(R.id.tv_home_banner_text_indicator)
        textIndicatorData = view.findViewById(R.id.tv_home_banner_text_indicator_data)
        profileImageView = view.findViewById(R.id.iv_profile_info_pic)
        peerImageView = view.findViewById(R.id.home_profile_preview_peer_review_weather)
        loadingIndicator = view.findViewById(R.id.loading_indicator)

        // ViewModel의 프로필 데이터를 관찰합니다.
        homeViewModel.profileData.observe(viewLifecycleOwner, Observer { profileData ->
            profileData?.let { updateProfileUI(it) }
        })

        // ViewModel의 로딩 상태를 관찰합니다.
        homeViewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            loadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        })


        // 여기서 사용자 정보를 가져옴
        getUserInfo()
        //여기서 사용자 프로젝트 프로필 미리보기 데이터 가져오기
        getUserProfileInfo()


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

    private fun getUserProfileInfo(){
        homeViewModel.setLoading(true) // 로딩 시작
        homeService = HomeService(requireContext())
        homeService.setHomeView(this)
        homeService.getUserProfilePreviewData()
    }

    private fun updateProfileUI(profileData: getProfilePreiviewData) {

        val photoUrl = profileData.photo

        if (photoUrl.isNullOrEmpty()) {
            // photo가 null이거나 빈 문자열인 경우
            Glide.with(this)
                .load(R.drawable.male_avatar)
                .placeholder(R.drawable.male_avatar) // 로딩 중일 때 표시할 기본 이미지
                .error(R.drawable.male_avatar) // 로드 실패 시 표시할 기본 이미지
                .into(profileImageView)
        } else {
            // photo가 유효한 URL인 경우
            Glide.with(this)
                .asBitmap()
                .load(photoUrl)
                .placeholder(R.drawable.male_avatar) // 로딩 중일 때 표시할 기본 이미지
                .error(R.drawable.male_avatar) // 로드 실패 시 표시할 기본 이미지
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        handleBitmap(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        // Handle if needed
                    }
                })
        }

        val drawableResource = when (profileData.peer) {
            in 0..19 -> R.drawable.profilecard_detail_peer0_19
            in 20..39 -> R.drawable.profilecard_detail_peer20_39
            in 40..59 -> R.drawable.profilecard_detail_peer40_59
            in 60..79 -> R.drawable.profilecard_detail_peer60_79
            in 80..100 -> R.drawable.profilecard_detail_peer80_100
            else -> R.drawable.profilecard_detail_peer40_59
        }
        peerImageView.setImageResource(drawableResource)
    }

    private fun handleBitmap(bitmap: Bitmap) {
        // 이미지 크기 조절
        val resizedBitmap = resizeBitmap(bitmap, 100, 100)
        // Bitmap을 Base64로 인코딩
        val base64Image = compressAndEncodeBitmap(resizedBitmap)
        // 인코딩된 이미지를 다시 이미지 버튼에 설정
        setBitmapFromBase64(base64Image, profileImageView)
    }

    fun compressAndEncodeBitmap(bitmap: Bitmap, quality: Int = 100): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        // 이미지 크기를 줄이고 JPEG 형식으로 압축
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun resizeBitmap(bitmap: Bitmap, targetWidth: Int, targetHeight: Int): Bitmap {
        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
    }

    private fun setBitmapFromBase64(base64String: String, imageView: ImageView) {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        // 이미지 버튼에 디코딩된 이미지 설정
        imageView.setImageBitmap(bitmap)
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

    private fun getIdFromSharedPreferences(context: Context): Int? {
        val sharedPref = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        return sharedPref.getInt("loginId", -1).takeIf { it != -1 }
    }

    private fun cacheProfileData(profileData: getProfilePreiviewData) {
        val sharedPref = activity?.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        sharedPref?.edit()?.apply {
            putString("profilePhoto", profileData.photo)
            putInt("profilePeer", profileData.peer)
            apply()
        }
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

    override fun onHomeGetSuccess(profileData: getProfilePreiviewData) {
        Log.d("HomeFragment","유저프로필정보 획득성공 콜백성공")
        homeViewModel.setProfileData(profileData)
    }

    override fun onHomeGetFailure() {
        Log.e("HomeFragment", "Failed to get profile data")
    }


}