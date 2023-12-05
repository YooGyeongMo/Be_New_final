package com.gmlab.team_benew.matching

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gmlab.team_benew.R
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.CardStackView
import com.yuyakaido.android.cardstackview.Direction
import kotlinx.coroutines.*

class MatchingFragment: Fragment(), MatchingPostView {

    lateinit var cardStackAdapter: CardStackAdapter
    lateinit var manager : CardStackLayoutManager
    private val matchingProfiles = mutableListOf<Profile>() // 프로필 정보를 담을 리스트
    private lateinit var cardStackView: CardStackView // 카드 스택 뷰
//    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_matching, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cardStackView = view.findViewById<CardStackView>(R.id.cardstackView)

        manager = CardStackLayoutManager(requireContext(), object : CardStackListener{
            override fun onCardDragging(direction: Direction?, ratio: Float) {

            }

            override fun onCardSwiped(direction: Direction?) {

            }

            override fun onCardRewound() {

            }

            override fun onCardCanceled() {

            }

            override fun onCardAppeared(view: View?, position: Int) {

            }

            override fun onCardDisappeared(view: View?, position: Int) {

            }

        })
        // MatchingService 인스턴스 생성
        val matchingService = MatchingService(requireContext())

        // 현재 Fragment를 MatchingPostView로 설정
        matchingService.setMatchingPostView(this)

        // 매칭 프로필을 서버로부터 불러오는 함수를 호출
        fetchMatchingProfiles(matchingService)

    }

    // MatchingFragment 내부
    private fun fetchMatchingProfiles(matchingService: MatchingService) {
        val userId = getIdFromSharedPreferences(requireContext()) ?: return // ID가 없으면 함수 종료
        for (i in 1..10) {
            val matchRequestDto = MatchRequestDto(uid1 = userId)
            matchingService.getUserData(matchRequestDto, onResponse = { matchingResponse ->
                val profile = matchingResponse.profile
                matchingProfiles.add(profile)
                if (matchingProfiles.size == 10) {
                    updateUI(matchingProfiles)
                    Log.d("MATCHING/POST/CARD/DATA","SUCCESS")
                }
            })
        }
    }

    // 받아온 프로필 정보로 UI를 업데이트하는 함수
    private fun updateUI(profiles: List<Profile>) {
        cardStackAdapter = CardStackAdapter(requireContext(), profiles)
        cardStackView.layoutManager = manager
        cardStackView.adapter = cardStackAdapter
    }


    override fun onMatchingPostSuccess() {
        Log.d("MATCHING/POST/SUCCESS", "유저매칭POST 성공")
    }

    override fun onMatchingPostFailure() {
        Log.d("MATCHING/POST/FAILURE", "유저매칭POST 실패")
    }

    private fun getIdFromSharedPreferences(context: Context): Int? {
        val sharedPref = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        return sharedPref.getInt("loginId", -1).takeIf { it != -1 }
    }

}