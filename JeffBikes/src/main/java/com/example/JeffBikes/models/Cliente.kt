package com.example.JeffBikes.models

data class Cliente(
    val id: String? = null,
    val cpf: String = "",
    val nome: String = "",
    val email: String = "",
    val instagram: String = ""
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "cpf" to cpf,
            "nome" to nome,
            "email" to email,
            "instagram" to instagram
        )
    }
}