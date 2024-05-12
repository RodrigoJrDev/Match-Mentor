package com.example.matchmentor.repository

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

// Interface Retrofit para os endpoints
interface UserProfileService {

    // Endpoint para criar um perfil de usuário
    @POST("usuarios.php")
    fun createProfile(@Body profile: UserProfile): Call<Void>

    // Endpoint para obter todos os perfis de usuário
    @GET("usuarios.php")
    fun getProfiles(): Call<List<UserProfile>>

    // Endpoint para atualizar um perfil de usuário
    @PUT("usuarios.php")
    fun updateProfile(@Body profile: UserProfile): Call<Void>

    // Endpoint para deletar um perfil de usuário (se necessário)
    @DELETE("usuarios.php")  // Você precisa implementar suporte DELETE no backend
    fun deleteProfile(@Body profile: UserProfile): Call<Void>

    @Multipart
    @POST("upload_image.php")
    fun uploadImage(@Part image: MultipartBody.Part, @Part("email") email: RequestBody): Call<Void>
}
