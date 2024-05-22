package com.gmlab.team_benew.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {
    private val _profileData = MutableLiveData<getProfilePreiviewData>()
    val profileData: LiveData<getProfilePreiviewData> get() = _profileData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun setProfileData(data: getProfilePreiviewData) {
        _profileData.value = data
        _isLoading.value = false
    }

    fun setLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }

}