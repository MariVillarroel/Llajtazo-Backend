package org.example.src.dto

data class UserRegisterRequest(
    val username: String,
    val correo: String,
    val password: String,
    val profilePic: String = ""
)