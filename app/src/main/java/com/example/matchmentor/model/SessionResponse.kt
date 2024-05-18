package com.example.matchmentor.model

data class SessionResponse(
    val logged_in: Boolean,
    val user_id: Int?,
    val type: String?
)