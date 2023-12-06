package com.gmlab.team_benew.matching

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.gmlab.team_benew.R
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.CardStackView
import com.yuyakaido.android.cardstackview.Direction
import kotlinx.coroutines.*

class MatchingFragment : Fragment(), MatchingPostView {
    private lateinit var matchingService: MatchingService
    lateinit var cardStackAdapter: CardStackAdapter
    lateinit var manager: CardStackLayoutManager
    private val matchingProfiles = mutableListOf<Profile>() // 프로필 정보를 담을 리스트
    private lateinit var cardStackView: CardStackView // 카드 스택 뷰
    private lateinit var progressBar: ProgressBar // 프로그레스바 초기화
    //    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private var userCount = 0;
    private var isFetching = false // 데이터 요청 중인지 표시하는 플래그

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_matching, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 프로그레스바 초기화
        progressBar = view.findViewById(R.id.pg_matchingCheck_progressBar)
        // MatchingService 싱글톤 인스턴스 초기화
        matchingService = MatchingService.getInstance(requireContext())
        matchingService.setMatchingPostView(this)
        fetchMatchingProfiles(true) // -> 첫번째 함수일때

        cardStackView = view.findViewById<CardStackView>(R.id.cardstackView)
        manager = CardStackLayoutManager(requireContext(), object : CardStackListener {
            override fun onCardDragging(direction: Direction?, ratio: Float) {

            }

            override fun onCardSwiped(direction: Direction?) {
                if (direction == Direction.Right) {
                    Toast.makeText(requireContext(), "right", Toast.LENGTH_LONG).show()
                }

                if (direction == Direction.Left) {
                    Toast.makeText(requireContext(), "left", Toast.LENGTH_LONG).show()
                }

                userCount += 1
                if (matchingProfiles.size - userCount <= 5 && !isFetching) {
                    fetchMatchingProfiles() // 추가 데이터 로드
                }

//                userCount += 1
//
//                if (userCount == matchingProfiles.count()) {
//                    // 매칭 프로필을 서버로부터 불러오는 함수를 호출
//                    fetchMatchingProfiles()
//                    userCount = 0
//                }
            }

            override fun onCardRewound() {

            }

            override fun onCardCanceled() {
                Log.d("CARDCANCELED", "매칭/거절")
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
    private fun fetchMatchingProfiles(preload: Boolean = false) {
        if (isFetching) return
        isFetching = true // 데이터 요청시작
        if(preload) showProgressBar() // 데이터 로딩중 프로그레스바 표시
        val userId = getIdFromSharedPreferences(requireContext()) ?: return // ID가 없으면 함수 종료

        lifecycleScope.launch {
            val newProfiles = mutableListOf<Profile>() //담는용 뮤터블리스트
            for (i in 1..10) {
                val matchRequestDto = MatchRequestDto(uid1 = userId)
                matchingService.getUserData(matchRequestDto, onResponse = { matchingResponse ->
                    newProfiles.add(matchingResponse.profile)
//                    val profile = matchingResponse.profile
                    if (newProfiles.size == 10) {

                        // 기존 리스트에서 이미 스와이프된 프로필 제거
                        if (userCount > 0 && matchingProfiles.size > userCount) {
                            matchingProfiles.subList(0, userCount).clear()
                        }

                        matchingProfiles.addAll(newProfiles)
                        updateUI(matchingProfiles)
                        userCount = 0 // 새 데이터 로드 후 userCount 초기화
                        isFetching = false // 데이터 요청완료
                        if (preload) hideProgressBar() // 데이터 로딩 완료 시 프로그레스바 숨김
                        Log.d("MATCHING/POST/CARD/DATA", "SUCCESS")
                    }
                })
                delay(200)
            }
//            // 최소 1초 동안 프로그레스바 표시 보장
//            delay(5000)

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

    override fun onMatchingLikePatchSuccess() {
        Log.d("MATCHINGLIKE/PATCH/SUCCESS", "유저매칭 좋아요 성공~")
    }

    override fun onMatchingLikePatchFailure() {
        Log.d("MATCHINGLIKE/PATCH/FAILURE", "유저매칭 좋아요 실패 ㅠㅠ")
    }

    override fun onMatchingUnLikePatchSuccess() {
        Log.d("MATCHINGUNLIKE/PATCH/SUCCESS", "유저매칭 싫어요 성공~")
    }

    override fun onMatchingUnLikePatchFailure() {
        Log.d("MATCHINGLIKE/PATCH/FAILURE", "유저매칭 싫어요 실패 ㅠㅠ ")
    }

    private fun getIdFromSharedPreferences(context: Context): Int? {
        val sharedPref = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        return sharedPref.getInt("loginId", -1).takeIf { it != -1 }
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.GONE
    }
}