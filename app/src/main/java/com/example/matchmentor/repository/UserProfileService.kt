package com.example.matchmentor.repository
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// Classe de dados representando um perfil de usuário
data class UserProfile(val name: String, val experience: String)

// Interface Retrofit para os endpoints
interface UserProfileService {
    @GET("profiles.php")
    fun getProfiles(): Call<List<UserProfile>>

    @POST("profile.php") // Correção aqui
    fun createProfile(@Body profile: UserProfile): Call<Void>
}
