package org.example.dto

import kotlinx.serialization.Serializable
import org.example.models.UserRole

@Serializable
data class UserResponse(
    val id: Int,
    val username: String,
    val correo: String,
    val role: UserRole
)