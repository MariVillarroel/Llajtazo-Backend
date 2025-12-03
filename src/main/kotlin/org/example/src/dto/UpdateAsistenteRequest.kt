package org.example.src.dto

data class UpdateAsistenteRequest(
    val username: String? = null,
    val correo: String? = null,
    val password: String? = null,
    val profilePic: String? = null,
    val categoriasIds: List<Int>? = null
)
