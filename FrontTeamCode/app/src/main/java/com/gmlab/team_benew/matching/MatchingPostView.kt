package com.gmlab.team_benew.matching

interface MatchingPostView {

    fun onMatchingPostSuccess()

    fun onMatchingPostFailure()

    fun onMatchingLikePatchSuccess()

    fun onMatchingLikePatchFailure()

    fun onMatchingUnLikePatchSuccess()

    fun onMatchingUnLikePatchFailure()

    // 네트워크 요청시 뒤 프레그먼트로 가게할예정
    fun onMatchingRequestFailure()

}