package com.gmlab.team_benew.matching

import com.gmlab.team_benew.auth.Role

data class MatchingResponse(
    val isUid2Team: Boolean,
    val matchId: Long,
    val matchSuccess: Boolean,
    val matchingDate: String,
    val matchingRequest: String,
    val profile: Profile,
    val projectId: Int,
    val uid1: Int
)

data class Profile(
    val matchId: Long, // 서버 응답에 따라 추가
    val id: Int,
    val instruction: String,
    val member: Member,
    val nickname: String,
    val peer: Int,
    val personalLink: String,
    val photo: String,
    val projectExperience: Boolean,
    val role: String
)

data class Member(
    val account: String,
    val birthday: String,
    val email: String,
    val gender: String,
    val id: Int,
    val major: String,
    val name: String,
    val password: String,
    val phoneNumber: String,
    val roles: List<Role>
)

data class Role(
    val name: String
)

