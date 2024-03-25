package com.gmlab.team_benew.start

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.gmlab.team_benew.R
import com.gmlab.team_benew.auth.LoginActivity
import com.gmlab.team_benew.auth.SignUpActivity
import com.gmlab.team_benew.auth.register.RegisterActivity
import com.gmlab.team_benew.databinding.ActivityIntroBinding

class IntroActivity:AppCompatActivity() {
    private val handler = Handler()
    private lateinit var binding : ActivityIntroBinding
    private lateinit var fadeInAnim : Animation
    private lateinit var moveToCenter : Animation
    private lateinit var fadeInAnimSub : Animation
    private lateinit var fadeInAnimBtn : Animation
    private lateinit var moveToCenter2 : Animation


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnIntroLogin.visibility = View.GONE
        binding.tvIntro.visibility = View.GONE
        binding.ivIntroPic.visibility = View.GONE
        binding.tvIntroSub.visibility = View.GONE
        binding.btnIntroRegister.visibility = View.GONE

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
            binding.ivIntroPic.visibility = View.VISIBLE

            binding.tvIntro.startAnimation(fadeInAnim)
            binding.ivIntroPic.startAnimation(fadeInAnim)
        },1000)

        handler.postDelayed({
            binding.tvIntro.startAnimation(moveToCenter)
        }, 3500) // 2초뒤

        handler.postDelayed({
            binding.tvIntroSub.visibility = View.VISIBLE
            binding.btnIntroLogin.visibility = View.VISIBLE
            binding.btnIntroRegister.visibility = View.VISIBLE

            binding.btnIntroLogin.startAnimation(animationSet2)
            binding.btnIntroRegister.startAnimation(animationSet2)
            binding.tvIntroSub.startAnimation(fadeInAnimSub)
        }, 6500) // 5.5초뒤







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