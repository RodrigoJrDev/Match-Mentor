package com.example.matchmentor.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.matchmentor.model.MentorProfile
import com.example.matchmentor.repository.MentorProfileService
import com.example.matchmentor.repository.RetrofitClient


class UserMentorViewModel : ViewModel() {

    private val _profiles = MutableLiveData<List<MentorProfile>>()
    val profiles: LiveData<List<MentorProfile>> get() = _profiles

    private val service: MentorProfileService = RetrofitClient.instance.create(MentorProfileService::class.java)

}