package com.example.matchmentor.repository

import com.example.matchmentor.model.CheckSessionRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import com.example.matchmentor.model.UserLogin
import com.example.matchmentor.model.LoginResponse
import com.example.matchmentor.model.SessionResponse
import retrofit2.http.GET

interface AuthService {
    @POST("login.php")
    fun loginUser(@Body user: UserLogin): Call<LoginResponse>

    @POST("checkSession.php")
    fun checkSession(@Body request: CheckSessionRequest): Call<SessionResponse>
}
