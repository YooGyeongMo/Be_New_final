    package com.gmlab.team_benew.main

    import android.content.ContentValues.TAG
    import android.os.Bundle
    import android.util.Log
    import android.view.Menu
    import android.view.MenuItem
    import androidx.appcompat.app.AppCompatActivity
    import androidx.navigation.findNavController
    import androidx.navigation.fragment.findNavController
    import androidx.navigation.ui.setupWithNavController
    import com.gmlab.team_benew.R
    import com.google.android.material.bottomnavigation.BottomNavigationView


    class MainActivity : AppCompatActivity() { //compat 호환성을 해준다는 이야기

        //lifecycle 콜백함수
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(com.gmlab.team_benew.R.layout.activity_main) //사용자에게 보여줄 레이아웃 선정 파일 ID인수로
            setSupportActionBar(findViewById(com.gmlab.team_benew.R.id.toolbar_app_default))
            // 툴바 제목 설정 제거
            supportActionBar?.title = ""


            Log.d(TAG, "onCreate")

            val bottomNavigationView = findViewById<BottomNavigationView>(com.gmlab.team_benew.R.id.bottom_navigation_main)
            bottomNavigationView.itemIconTintList = null

            val navController = supportFragmentManager.findFragmentById(com.gmlab.team_benew.R.id.Fragment_container)
                ?.findNavController() // 참조를 반환, find or get 존재하지않을수 있으니 safe call 컨트롤러
            navController?.let {
                bottomNavigationView.setupWithNavController(it) //navHostFragment에서 관리하는 controller
            }
        }

        override fun onCreateOptionsMenu(menu: Menu): Boolean {
            menuInflater.inflate(com.gmlab.team_benew.R.menu.menu_toolbar, menu)
            return true
        }


        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            if (item.itemId == com.gmlab.team_benew.R.id.item1) {
                // notification 프래그먼트로 이동
                val navController = findNavController(com.gmlab.team_benew.R.id.Fragment_container)
                navController.navigate(com.gmlab.team_benew.R.id.navigation_notification)
                return true
            }
            else if(item.itemId==R.id.item2){
                val navController=findNavController(R.id.Fragment_container)
                navController.navigate(R.id.navigation_chatList)
                return true
            }
            return super.onOptionsItemSelected(item)
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