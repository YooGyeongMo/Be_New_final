package com.gmlab.team_benew.matching

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.gmlab.team_benew.R
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.CardStackView
import com.yuyakaido.android.cardstackview.Direction
import kotlinx.coroutines.*

class MatchingFragment: Fragment(), MatchingPostView {
    private lateinit var matchingService: MatchingService
    lateinit var cardStackAdapter: CardStackAdapter
    lateinit var manager : CardStackLayoutManager
    private val matchingProfiles = mutableListOf<Profile>() // 프로필 정보를 담을 리스트
    private lateinit var cardStackView: CardStackView // 카드 스택 뷰
//    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private var userCount = 0;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_matching, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // MatchingService 싱글톤 인스턴스 초기화
        matchingService = MatchingService.getInstance(requireContext())
        matchingService.setMatchingPostView(this)
        fetchMatchingProfiles()

        cardStackView = view.findViewById<CardStackView>(R.id.cardstackView)
        manager = CardStackLayoutManager(requireContext(), object : CardStackListener{
            override fun onCardDragging(direction: Direction?, ratio: Float) {

            }

            override fun onCardSwiped(direction: Direction?) {
                if(direction == Direction.Right){
                    Toast.makeText(requireContext(),"right",Toast.LENGTH_LONG).show()
                }

                if(direction == Direction.Left){
                    Toast.makeText(requireContext(),"left",Toast.LENGTH_LONG).show()
                }

                userCount += 1

                if(userCount == matchingProfiles.count()){
                    // 매칭 프로필을 서버로부터 불러오는 함수를 호출
                    fetchMatchingProfiles()
                    userCount = 0
                }
            }

            override fun onCardRewound() {

            }

            override fun onCardCanceled() {
                Log.d("CARDCANCELED","매칭/거절")
            }

            override fun onCardAppeared(view: View?, position: Int) {

            }

            override fun onCardDisappeared(view: View?, position: Int) {

            }

        })
//        // MatchingService 인스턴스 생성
//        val matchingService = MatchingService(requireContext())
//
//        // 현재 Fragment를 MatchingPostView로 설정
//        matchingService.setMatchingPostView(this)
//
//        // 매칭 프로필을 서버로부터 불러오는 함수를 호출
//        fetchMatchingProfiles(matchingService)

    }

    // MatchingFragment 내부
    private fun fetchMatchingProfiles() {
        val userId = getIdFromSharedPreferences(requireContext()) ?: return // ID가 없으면 함수 종료
        matchingProfiles.clear()
        for (i in 1..3) {
            val matchRequestDto = MatchRequestDto(uid1 = userId)
            matchingService.getUserData(matchRequestDto, onResponse = { matchingResponse ->
                val profile = matchingResponse.profile
                matchingProfiles.add(profile)
                if (matchingProfiles.size == 3) {
                    updateUI(matchingProfiles)
                    Log.d("MATCHING/POST/CARD/DATA","SUCCESS")
                }
            })
        }
    }

    // 받아온 프로필 정보로 UI를 업데이트하는 함수
    private fun updateUI(profiles: List<Profile>) {
        if (!::cardStackAdapter.isInitialized) {
            // 어댑터가 초기화되지 않았다면 새로 생성
            cardStackAdapter = CardStackAdapter(requireContext(), profiles)
            cardStackView.layoutManager = manager
            cardStackView.adapter = cardStackAdapter
        } else {
            // 이미 생성된 어댑터에 새 데이터 설정
            cardStackAdapter.items = profiles
            cardStackAdapter.notifyDataSetChanged()
        }
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