package com.gmlab.team_benew.chat.chatintro

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class ChatIntroViewModel(application: Application) : AndroidViewModel(application) {

    private val chatIntroService: ChatIntroService = ChatIntroService(application)

    private val _friendsList = MutableLiveData<List<FriendResponse>>()
    val friendsList: LiveData<List<FriendResponse>> get() = _friendsList

    private val _chatRooms = MutableLiveData<List<ChatData>>()
    val chatRooms: LiveData<List<ChatData>> get() = _chatRooms

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    fun getFriendsList(memberId: Long) {
        _loading.value = true
        chatIntroService.getFriendsList(memberId, object : ChatIntroService.ServiceCallback<List<FriendResponse>> {
            override fun onSuccess(data: List<FriendResponse>) {
                _loading.value = false
                _friendsList.value = data
                _error.value = null
            }

            override fun onFailure(message: String) {
                _loading.value = false
                _error.value = message
            }
        })
    }

    fun getChatRooms(userId: Int) {
        _loading.value = true
        chatIntroService.getChatRooms(userId, object : ChatIntroService.ServiceCallback<List<ChatData>> {
            override fun onSuccess(data: List<ChatData>) {
                _loading.value = false
                _chatRooms.value = data
                _error.value = null
            }

            override fun onFailure(message: String) {
                _loading.value = false
                _error.value = message
            }
        })
    }
}
