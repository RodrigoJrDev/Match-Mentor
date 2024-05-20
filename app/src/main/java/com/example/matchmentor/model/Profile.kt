package com.example.matchmentor.model

data class Profile(
    val id: Int,
    val nome: String,
    val sobrenome: String,
    val idade: Int?,
    val cidade: String?,
    val area_de_interesse: String?, // Para usuário
    val profissao: String?, // Para mentor
    val area_de_atuacao: String?, // Para mentor
    val descricao_pessoal: String?, // Para mentor
    val foto: String?
)
