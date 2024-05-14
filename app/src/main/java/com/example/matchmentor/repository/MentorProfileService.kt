package com.example.matchmentor.repository

import com.example.matchmentor.model.MentorProfile
import retrofit2.Call
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Multipart
import retrofit2.http.DELETE
import retrofit2.http.Part
import com.example.matchmentor.model.UserProfile
import okhttp3.ResponseBody

interface MentorProfileService {

    @POST("mentores.php")
    fun createMentor(@Body mentorProfile: MentorProfile): Call<ResponseBody>

    @GET("mentores.php")
    fun getMentores(): Call<List<MentorProfile>>

    @PUT("mentores.php")
    fun updateMentor(@Body profile: MentorProfile): Call<Void>

    @DELETE("mentores.php")
    fun deleteMentor(@Body profile: MentorProfile): Call<Void>

    @Multipart
    @POST("upload_image.php")
    fun uploadImage(@Part image: MultipartBody.Part, @Part("email") email: RequestBody): Call<Void>
}
