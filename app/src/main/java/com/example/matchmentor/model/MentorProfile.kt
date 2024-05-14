package com.example.matchmentor.model

import androidx.room.Entity
import com.google.gson.annotations.SerializedName

@Entity(tableName = "mentor")
data class MentorProfile(
    @SerializedName("nome") val name: String,
    @SerializedName("sobrenome") val lastName: String,
    @SerializedName("idade") val age: Int?,
    @SerializedName("cidade") val city: String,
    @SerializedName("descricao_pessoal") val personalDescription: String,
    @SerializedName("profissao") val profession: String,
    @SerializedName("area_de_atuacao") val areaOfExpertise: String,
    @SerializedName("area_de_atuacao_descricao") val areaOfExpertiseDescription: String,
    @SerializedName("foto") val photo: String?,
    @SerializedName("email") val email: String,
    @SerializedName("senha") val password: String
)
