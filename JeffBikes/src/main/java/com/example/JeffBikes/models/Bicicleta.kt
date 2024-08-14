package com.example.JeffBikes.models

data class Bicicleta(
    val codigo: String = "",
    val modelo: String = "",
    val materialDoChassi: String = "",
    val aro: Int = 0,
    val preco: Double = 0.0,
    val quantidadeDeMarchas: Int = 0,
    val proprietario: String = ""
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "codigo" to codigo,
            "modelo" to modelo,
            "materialDoChassi" to materialDoChassi,
            "aro" to aro,
            "preco" to preco,
            "quantidadeDeMarchas" to quantidadeDeMarchas,
            "proprietario" to proprietario
        )
    }
}