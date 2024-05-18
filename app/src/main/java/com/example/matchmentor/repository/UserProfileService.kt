package com.example.matchmentor.repository

import retrofit2.Call
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Multipart
import retrofit2.http.DELETE
import retrofit2.http.Part
import com.example.matchmentor.model.UserProfile
import okhttp3.ResponseBody

interface UserProfileService {

    @POST("usuarios.php")
    fun createProfile(@Body userProfile: UserProfile): Call<ResponseBody>

    @GET("usuarios.php")
    fun getProfiles(): Call<List<UserProfile>>

    @PUT("usuarios.php")
    fun updateProfile(@Body profile: UserProfile): Call<Void>

    @DELETE("usuarios.php")
    fun deleteProfile(@Body profile: UserProfile): Call<Void>

    @Multipart
    @POST("upload_image.php")
    fun uploadImage(@Part file: MultipartBody.Part): Call<ResponseBody>
}
