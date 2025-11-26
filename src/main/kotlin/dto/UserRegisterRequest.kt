package org.example.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserRegisterRequest(
    val id: Int,
    val tipo: String,
    val username: String,
    val correo: String,
    val password: String
)