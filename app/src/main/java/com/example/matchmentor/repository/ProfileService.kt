package com.example.matchmentor.repository

import com.example.matchmentor.model.Item
import com.example.matchmentor.model.Profile
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ProfileService {
    @GET("profiles.php")
    fun getProfilesForMentor(@Query("userId") userId: Int, @Query("userType") userType: String): Call<List<Item>>

    @GET("profiles.php")
    fun getProfilesForUser(@Query("userId") userId: Int,  @Query("userType") userType: String): Call<List<Item>>

    @GET("search.php")
    fun getProfiles(
        @Query("userId") userId: Int,
        @Query("userType") userType: String,
        @Query("query") query: String
    ): Call<List<Profile>>

    @GET("get_suggestions.php")
    fun getSuggestions(
        @Query("userType") userType: String
    ): Call<List<String>>

}
