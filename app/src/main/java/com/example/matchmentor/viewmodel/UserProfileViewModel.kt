package com.example.matchmentor.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.matchmentor.model.UserProfile
import com.example.matchmentor.repository.UserProfileService
import com.example.matchmentor.repository.RetrofitClient


class UserProfileViewModel : ViewModel() {

    private val _profiles = MutableLiveData<List<UserProfile>>()
    val profiles: LiveData<List<UserProfile>> get() = _profiles

    private val service: UserProfileService = RetrofitClient.instance.create(UserProfileService::class.java)

}