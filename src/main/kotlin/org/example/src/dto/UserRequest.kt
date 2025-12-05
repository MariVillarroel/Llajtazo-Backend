package org.example.src.dto

data class UserRequest(
    val username: String,
    val correo: String,
    val password: String,
    val profilePic: String = "",
    val categoriasIds: List<Int>? = null
)