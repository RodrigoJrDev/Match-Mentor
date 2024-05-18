// Model para a resposta de login
package com.example.matchmentor.model

data class LoginResponse(
    val message: String,
    val user_id: Int?,
    val email_user: String,
    val type_user: String,
)
