package com.example.matchmentor.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "usuarios")
data class UserProfile(
    @SerializedName("nome") val name: String,
    @SerializedName("sobrenome") val lastName: String,
    @SerializedName("idade") val age: Int?,
    @SerializedName("cidade") val city: String,
    @SerializedName("area_de_interesse") val interestArea: String,
    @SerializedName("profissao") val profession: String,
    @SerializedName("descricao") val description: String,
    @SerializedName("foto") val photo: String?,
    @SerializedName("email") val email: String,
    @SerializedName("senha") val password: String
)
