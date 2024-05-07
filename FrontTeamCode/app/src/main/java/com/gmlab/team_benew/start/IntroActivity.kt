package com.gmlab.team_benew.start

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.viewpager2.widget.ViewPager2
import com.gmlab.team_benew.R
import com.gmlab.team_benew.auth.LoginActivity
import com.gmlab.team_benew.auth.register.RegisterActivity
import com.gmlab.team_benew.databinding.ActivityIntroBinding
import com.gmlab.team_benew.util.getStatusBarHeight
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class IntroActivity:AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: HomeBannerAdapter
    private val handler = Handler()
    private lateinit var binding : ActivityIntroBinding
    private lateinit var fadeInAnim : Animation
    private lateinit var moveToCenter : Animation
    private lateinit var fadeInAnimSub : Animation
    private lateinit var fadeInAnimBtn : Animation
    private lateinit var moveToCenter2 : Animation
    private lateinit var tabLayout: TabLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.introRootLayout.setPadding(0,getStatusBarHeight(this), 0, 0)

        //status bar와 navigation bar 모두 투명하게 만드는 코드
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        viewPager = findViewById(R.id.viewpager_home_banner)
        adapter = HomeBannerAdapter()
        viewPager.adapter = adapter

        // 이미지 리소스 배열
        val imageResIds = listOf(R.drawable.intro_bg, R.drawable.my_project_detail, R.drawable.splash_bg)
        adapter.submitList(imageResIds)

        //화면표시 인디케이터

        binding.dotsIndicator.visibility = View.GONE
        binding.viewpagerHomeBanner.visibility = View.GONE
        binding.btnIntroLogin.visibility = View.GONE
        binding.tvIntro.visibility = View.GONE
        binding.tvIntroSub.visibility = View.GONE
        binding.btnIntroRegister.visibility = View.GONE

        binding.dotsIndicator.setViewPager2(binding.viewpagerHomeBanner)


        val animationSet = AnimationSet(true)
        val animationSet2 = AnimationSet(true)
        fadeInAnim = AnimationUtils.loadAnimation(this, R.anim.fadee_in)
        moveToCenter = AnimationUtils.loadAnimation(this, R.anim.intro_move)
        fadeInAnimSub = AnimationUtils.loadAnimation(this, R.anim.fadee_in)
        moveToCenter2 = AnimationUtils.loadAnimation(this, R.anim.intro_move)
        fadeInAnimBtn = AnimationUtils.loadAnimation(this, R.anim.fadee_in)

        animationSet.addAnimation(fadeInAnim)
        animationSet2.addAnimation(fadeInAnimBtn)


        handler.postDelayed({
            binding.tvIntro.visibility = View.VISIBLE

            binding.tvIntro.startAnimation(animationSet)

        },1000)

        handler.postDelayed({
            binding.viewpagerHomeBanner.visibility = View.VISIBLE
            binding.dotsIndicator.visibility = View.VISIBLE
            binding.tvIntroSub.visibility = View.VISIBLE
            binding.btnIntroLogin.visibility = View.VISIBLE
            binding.btnIntroRegister.visibility = View.VISIBLE

            binding.btnIntroLogin.startAnimation(animationSet2)
            binding.btnIntroRegister.startAnimation(animationSet2)
            binding.tvIntroSub.startAnimation(fadeInAnimSub)
            binding.viewpagerHomeBanner.startAnimation(fadeInAnim)
            binding.dotsIndicator.startAnimation(fadeInAnim)
        }, 3500)



        binding.btnIntroLogin.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.btnIntroRegister.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }


    }
}