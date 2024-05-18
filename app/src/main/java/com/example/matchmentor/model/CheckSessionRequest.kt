package com.example.matchmentor.model

data class CheckSessionRequest(
    val user_id: Int,
    val email: String,
    val type: String
)
