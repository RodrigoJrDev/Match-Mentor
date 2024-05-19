package com.example.matchmentor.model

data class MatchRequest(
    val userId: Int,
    val profileId: Int,
    val userType: String,
    val match: Boolean
)
