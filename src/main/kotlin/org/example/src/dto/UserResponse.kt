package org.example.src.dto

import org.example.src.models.UserRole

@Serializable
data class UserResponse(
    val id: Int,
    val username: String,
    val correo: String,
    val role: UserRole
)