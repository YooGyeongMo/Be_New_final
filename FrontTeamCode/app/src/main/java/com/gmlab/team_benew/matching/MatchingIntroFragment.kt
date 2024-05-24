package com.gmlab.team_benew.matching

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.gmlab.team_benew.R

class MatchingIntroFragment:Fragment() {

    private val handler = Handler()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_intro_matching, container, false)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val buttonTeamSearchingIntro = view.findViewById<Button>(R.id.btn_team_searching_intro)
        val buttonNavMatchingDetail = view.findViewById<Button>(R.id.btn_coworker_searching_intro)
        val imageView = view.findViewById<ImageView>(R.id.iv_matching_intro_gif)
        val progressBar = view.findViewById<ProgressBar>(R.id.intro_matching_indicator)


        // 모든 버튼에 같은 클릭 리스너 설정
        buttonNavMatchingDetail.setOnClickListener { onCardClicked(it) }

        loadGifWithGlide(imageView, progressBar)
    }

    private fun onCardClicked(view: View) {
        when (view.id) {
            R.id.btn_coworker_searching_intro -> findNavController().navigate(R.id.action_intro_matching_to_navigation_matching) // 매칭 디테일로 이동
        }
    }

    private fun loadGifWithGlide(imageView: ImageView, progressBar: ProgressBar) {
        Glide.with(this)
            .asGif()
            .load("file:///android_asset/matching_intro_new_2.gif")
            .diskCacheStrategy(DiskCacheStrategy.ALL) // 디스크 캐시를 최적화
            .into(object : CustomTarget<GifDrawable>() {
                override fun onResourceReady(resource: GifDrawable, transition: Transition<in GifDrawable>?) {
                    imageView.setImageDrawable(resource)
                    resource.start()
                    progressBar.visibility = View.GONE // GIF 로드가 완료되면 프로그레스바 숨기기

                    // 6초 후에 GIF를 멈춤
                    handler.postDelayed({
                        resource.stop()
                    }, 6000)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // 필요 시 처리
                }
            })
    }
}