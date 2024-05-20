package com.example.matchmentor.model

data class MatchResponse(
    val isMatch: Boolean,
    val mentorId: Int?,
    val usuarioId: Int?
)
