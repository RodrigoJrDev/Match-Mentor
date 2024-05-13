package com.example.matchmentor.repository

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import com.example.matchmentor.model.UserLogin
import com.example.matchmentor.model.LoginResponse

interface AuthService {
    @POST("login.php")
    fun loginUser(@Body user: UserLogin): Call<LoginResponse>
}
