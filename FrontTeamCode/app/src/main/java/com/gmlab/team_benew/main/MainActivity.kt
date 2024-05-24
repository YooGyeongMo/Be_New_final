    package com.gmlab.team_benew.main

    import android.app.Activity
    import android.content.ContentValues.TAG
    import android.os.Bundle
    import android.util.Log
    import android.view.Menu
    import android.view.MenuItem
    import android.view.View
    import android.view.WindowManager
    import android.widget.ImageView
    import androidx.appcompat.app.AppCompatActivity
    import androidx.fragment.app.Fragment
    import androidx.lifecycle.lifecycleScope
    import androidx.navigation.findNavController
    import androidx.navigation.fragment.NavHostFragment
    import androidx.navigation.fragment.findNavController
    import androidx.navigation.ui.NavigationUI
    import androidx.navigation.ui.setupWithNavController
    import com.gmlab.team_benew.R
    import com.gmlab.team_benew.databinding.ActivityMainBinding
    import com.gmlab.team_benew.util.getStatusBarHeight
    import com.google.android.material.bottomnavigation.BottomNavigationView
    import kotlinx.coroutines.*
    import okhttp3.ResponseBody


    class MainActivity : AppCompatActivity(),MainLiveAlarmsView { //compat 호환성을 해준다는 이야기

        private lateinit var binding: ActivityMainBinding
        private var redDot: View? = null

        private lateinit var mainAlarmsGetService: MainAlarmsGetService
        private lateinit var toolbar: androidx.appcompat.widget.Toolbar
        private lateinit var logoImageView: ImageView

        //lifecycle 콜백함수
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root) //사용자에게 보여줄 레이아웃 선정 파일 ID인수로

            toolbar = findViewById(R.id.toolbar_app_default)
            logoImageView = toolbar.findViewById(R.id.iv_logo_image_view_home)

            setSupportActionBar(toolbar)
            // 툴바 제목 설정 제거
            supportActionBar?.title = ""

//            binding.mainRootLayout.setPadding(0, getStatusBarHeight(this), 0, 0)
//
//            //status bar와 navigation bar 모두 투명하게 만드는 코드
//            window.setFlags(
//                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

            Log.d(TAG, "onCreate")

            val bottomNavigationView = findViewById<BottomNavigationView>(com.gmlab.team_benew.R.id.bottom_navigation_main)
            bottomNavigationView.itemIconTintList = null

            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.Fragment_container) as NavHostFragment
            val navController = navHostFragment.navController

//            bottomNavigationView.setupWithNavController(navController)

            bottomNavigationView.setOnItemSelectedListener { item ->
                NavigationUI.onNavDestinationSelected(item, navController)
                return@setOnItemSelectedListener true
            }

            navController.addOnDestinationChangedListener{_, destination, _ ->
                if(destination.id == R.id.navigation_profile_deatil ||
                    destination.id == R.id.navigation_notification ||
                    destination.id == R.id.navigation_chatListFragment ||
                    destination.id == R.id.navigation_chat ||
                    destination.id == R.id.navigation_matching ||
                    destination.id == R.id.navigation_testing ||
                    destination.id == R.id.navigation_project_list ||
                    destination.id == R.id.navigation_project_deatil ||
                    destination.id == R.id.navigation_project_post_deatil
                    ) {
                    //해당 프레그먼트로 이동 시에 툴바의 로고 버튼을 뒤로가기 버튼으로 대체
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                    supportActionBar?.setDisplayShowHomeEnabled(true)
                    toolbar.setNavigationIcon(R.drawable.backtab) //뒤로가기 버튼
                    toolbar.setNavigationOnClickListener{
                        navController.navigateUp() //뒤로가기 기능
                    }
                    logoImageView.visibility = View.GONE //로고숨기기
                } else {
                    //기본 상태로 변경
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    supportActionBar?.setDisplayShowHomeEnabled(false)
                    logoImageView.visibility = View.VISIBLE //
                }

            }

//            val navController = supportFragmentManager.findFragmentById(com.gmlab.team_benew.R.id.Fragment_container)
//                ?.findNavController() // 참조를 반환, find or get 존재하지않을수 있으니 safe call 컨트롤러
//            navController?.let {
//                bottomNavigationView.setupWithNavController(it) //navHostFragment에서 관리하는 controller
//
//            }


            mainAlarmsGetService = MainAlarmsGetService(this)
            mainAlarmsGetService.setMainLiveAlarmsView(this)

            // 5초 후에 폴링 시작
            startPolling()

        }
        private fun checkAlarmsOnce() {
            mainAlarmsGetService.getUserAlarms(this)
        }

        private fun startPolling(){
            lifecycleScope.launch(Dispatchers.IO) {
                while (isActive) {
                    delay(5000) // 5초마다 반복
                    mainAlarmsGetService.getUserAlarms(this@MainActivity)
                }
            }
        }

        override fun onCreateOptionsMenu(menu: Menu): Boolean {
            // 액티비티 시작 시 처음으로 알람 확인
            checkAlarmsOnce()
            menuInflater.inflate(R.menu.menu_toolbar, menu)

            val notificationItem = menu.findItem(R.id.item1)
            val chatItem = menu.findItem(R.id.item2)

            val notificationActionView = notificationItem.actionView
            redDot = notificationActionView?.findViewById(R.id.red_dot_notification)
            val chatActionView = chatItem.actionView

            notificationActionView?.setOnClickListener { navigateToFragment(R.id.navigation_notification) }
            chatActionView?.setOnClickListener { navigateToFragment(R.id.navigation_chatListFragment) }

            return true
        }

        private fun navigateToFragment(fragmentId: Int) {
            findNavController(R.id.Fragment_container).navigate(fragmentId)
        }

        private fun updateRedDot(body: ResponseBody) {
            val count = parseResponse(body)
            lifecycleScope.launch(Dispatchers.Main) {
                if (count > 0) {
                    redDot?.visibility = View.VISIBLE
                    Log.d("MAIN/LIVE/ALARMS/SUCCESS","알람 갯수 데이터 1개이상 정상성공")
                } else {
                    redDot?.visibility = View.GONE
                    Log.d("MAIN/LIVE/ALARMS/SUCCESS","알람 갯수 데이터 0개 정상성공")
                }
            }
        }


        private fun parseResponse(body: ResponseBody): Int {
            // 서버 응답에서 알람 개수를 파싱하는 로직 구현
            return body.string().toIntOrNull() ?: 0
        }

        // MainAlarmsGetService에서 알람 확인이 성공적일 때 호출되는 함수
        override fun onMainLiveAlarmsGetSuccess(responseBody: ResponseBody) {
            updateRedDot(responseBody)
        }


        override fun onMainLiveAlarmsGetFailure() {
            Log.e("MAIN/LIVE/ALARMS/FAILURE","알람 갯수 데이터 실패")
        }





//        private fun showAlertDialog()
//        {
//            AlertDialog.Builder(this)
//                .setTitle("알림")
//                .setMessage("유경모님으로부터 프로젝트 요청이 왔습니다.함께 하시겠습니까?")
//                .setNegativeButton("거절") { dialog, which ->
//                    // "취소" 버튼 클릭 시 수행할 동작
//                }
//                .setPositiveButton("수락") { dialog, which ->
//                    // "확인" 버튼 클릭 시 수행할 동작
//                }
//                .show()
//        }


    }