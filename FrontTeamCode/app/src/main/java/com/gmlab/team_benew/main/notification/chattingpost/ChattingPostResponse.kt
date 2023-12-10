package com.gmlab.team_benew.main.notification.chattingpost

import com.google.gson.annotations.SerializedName

data class ChattingPostResponse(
    @SerializedName("name")
    val name: String,
    @SerializedName("roomId")
    val roomId: String
)