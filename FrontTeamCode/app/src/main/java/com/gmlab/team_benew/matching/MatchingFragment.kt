package com.gmlab.team_benew.matching

import android.app.ProgressDialog.show
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.gmlab.team_benew.R
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.CardStackView
import com.yuyakaido.android.cardstackview.Direction
import kotlinx.coroutines.*

class MatchingFragment : Fragment(), MatchingPostView, MatchingAlarmsPostView {
    private lateinit var matchingService: MatchingService
    lateinit var cardStackAdapter: CardStackAdapter
    lateinit var manager: CardStackLayoutManager
    private val matchingProfiles = mutableListOf<Profile>() // 프로필 정보를 담을 리스트
    private val matchingId = mutableListOf<Long>()// 매칭Id담을 리스트
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
        matchingService.setMatchingAlarmsPostView(this)
        fetchMatchingProfiles(true) // -> 첫번째 함수일때

        cardStackView = view.findViewById<CardStackView>(R.id.cardstackView)
        manager = CardStackLayoutManager(requireContext(), object : CardStackListener {
            override fun onCardDragging(direction: Direction?, ratio: Float) {

            }

            override fun onCardSwiped(direction: Direction?) {

                val currentMatchId = matchingId[manager.topPosition - 1]
                val currentProfile = matchingProfiles[manager.topPosition - 1] // 현재 스와이프한 프로필


                if (direction == Direction.Right) {

                    val receiverId = currentProfile.id.toLong() // 받는 사람의 ID를 얻어오는 로직
                    val myUserId = getIdFromSharedPreferences(requireContext())?.toLong()
                        ?: return // 현재 사용자의 ID를 얻어오는 로직
                    val myName = getNameFromSharedPreferences(requireContext()) ?: return

                    val message = "${myName}님이 프로젝트 요청을 보냈습니다." // 알림 메시지 설정

                    val matchingAlarmRequest = MatchingAlarmRequest(
                        message = message,
                        receiverId = receiverId,
                        myUserId = myUserId
                    )

                    matchingService.sendMatchingAlrams(matchingAlarmRequest,
                        onResponse = { response ->
                            // 알람 전송 성공시 처리 로직
                            // AlertDialog 생성 및 표시
                            AlertDialog.Builder(requireContext()).apply {
                                setTitle("프로젝트 요청")
                                setMessage("프로젝트 요청을 성공적으로 보냈습니다.")
                                setPositiveButton("확인") { dialog, which ->
                                    // 여기서 아무 것도 하지 않음
                                }
                                create()
                                show()
                            }
                        },
                        onFailure = { throwable ->
                            // AlertDialog 생성 및 표시
                            AlertDialog.Builder(requireContext()).apply {
                                setTitle("프로젝트 요청 실패")
                                setMessage("프로젝트 요청을 보내지 못했습니다.")
                                setPositiveButton("확인") { dialog, which ->
                                    // 여기서 아무 것도 하지 않음
                                }
                                create()
                                show()
                            }
                        }
                    )

                    matchingService.likeMatch(currentMatchId) { response ->
                        Toast.makeText(
                            context,
                            "${currentMatchId}매칭에 '좋아요'를 보냈습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
//                        Toast.makeText(context, "매칭에 '좋아요'를 보냈습니다.", Toast.LENGTH_SHORT).show()

                    }
                }

                if (direction == Direction.Left) {

                    matchingService.disLikeMatch(currentMatchId,
                        onResponse = { response ->
                            // 통신 성공 시 처리
                            Toast.makeText(
                                context,
                                "${currentMatchId}매칭에 '싫어요'를 보냈습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        onFailure = { throwable ->
                            // 통신 실패 시 처리
                            Toast.makeText(context, "매칭에 '싫어요'를 보내지 못했습니다.", Toast.LENGTH_SHORT)
                                .show()
                            Log.e("MatchingFragment", "disLikeMatch failed", throwable)
                        }
                    )
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
                Log.d("CARDCANCELED", "카드 놓침")
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
        if (preload) showProgressBar() // 데이터 로딩중 프로그레스바 표시
        val userId = getIdFromSharedPreferences(requireContext()) ?: return // ID가 없으면 함수 종료

        lifecycleScope.launch {
            val newProfiles = mutableListOf<Pair<Long, Profile>>() //담는용 뮤터블리스트
            for (i in 1..10) {
                val matchRequestDto = MatchRequestDto(uid1 = userId)
                matchingService.getUserData(matchRequestDto, onResponse = { matchingResponse ->
                    val profile = matchingResponse.profile
                    val matchId = matchingResponse.matchId

                    val profilePair = Pair(matchId, profile)
                    newProfiles.add(profilePair)
//                    val profile = matchingResponse.profile
                    if (newProfiles.size == 10) {

                        // 기존 리스트에서 이미 스와이프된 프로필 제거
                        if (userCount > 0 && matchingProfiles.size > userCount) {
                            matchingProfiles.subList(0, userCount).clear()
                            matchingId.subList(0, userCount).clear()
                        }

                        matchingProfiles.addAll(newProfiles.map { it.second }) // Profile만을 matchingProfiles에 추가
                        matchingId.addAll(newProfiles.map { it.first })
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
        Log.e("MATCHING/POST/FAILURE", "유저매칭POST 실패")
    }

    override fun onMatchingLikePatchSuccess() {
        Log.d("MATCHINGLIKE/PATCH/SUCCESS", "유저매칭 좋아요 성공~")
    }

    override fun onMatchingLikePatchFailure() {
        Log.e("MATCHINGLIKE/PATCH/FAILURE", "유저매칭 좋아요 실패 ㅠㅠ")
    }

    override fun onMatchingUnLikePatchSuccess() {
        Log.d("MATCHINGUNLIKE/PATCH/SUCCESS", "유저매칭 싫어요 성공~")
    }

    override fun onMatchingUnLikePatchFailure() {
        Log.e("MATCHINGLIKE/PATCH/FAILURE", "유저매칭 싫어요 실패 ㅠㅠ ")
    }

    override fun onMatchingAlarmsSuccess() {
        Log.d("MATCHINGALARMS/POST/SUCCESS", "유저매칭 알람 성공!")
    }

    override fun onMatchingAlarmsFailure() {
        Log.e("MATCHINGALARMS/POST/FAILURE", "유저매칭 알람 실패!")
    }

    override fun onMatchingRequestFailure(errorMessage: String) {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Error")
            setMessage(errorMessage)
            setPositiveButton("OK") { dialog, which ->
                // 오류 대화상자 확인 버튼 클릭 시 수행할 동작
            }
            create().show()
        }
    }

    private fun getNameFromSharedPreferences(context: Context): String? {
        val shardPref = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        return shardPref.getString("cachedUserName", null)
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