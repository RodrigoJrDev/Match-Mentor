package com.example.matchmentor.repository

import com.example.matchmentor.model.Item
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ProfileService {
    @GET("profiles.php")
    fun getProfilesForMentor(@Query("userId") userId: Int, @Query("userType") userType: String): Call<List<Item>>

    @GET("profiles.php")
    fun getProfilesForUser(@Query("userId") userId: Int,  @Query("userType") userType: String): Call<List<Item>>
}
