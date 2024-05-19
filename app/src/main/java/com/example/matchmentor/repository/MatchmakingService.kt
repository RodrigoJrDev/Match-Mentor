package com.example.matchmentor.repository

import com.example.matchmentor.model.MatchRequest
import com.example.matchmentor.model.MatchResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface MatchmakingService {
    @POST("matchmaking.php")
    fun updateMatch(@Body request: MatchRequest): Call<MatchResponse>
}
