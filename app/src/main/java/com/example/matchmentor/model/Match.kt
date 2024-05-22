package com.example.matchmentor.model

data class Match(
    val id: Int,
    val nome: String,
    val sobrenome: String,
    val profissao: String?,
    val foto: String?,
    val match_date: String?
)
