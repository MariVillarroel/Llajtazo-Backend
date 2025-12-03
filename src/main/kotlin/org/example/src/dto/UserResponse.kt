package org.example.src.dto

import org.example.src.models.Asistente
import org.example.src.models.UserRole

data class UserResponse(
    val id: Int,
    val username: String,
    val correo: String,
    val profilePic:String?,
    val role: UserRole,
    val totalOrganizadoresSeguidos: Int,
    val fechaCreacion: String
) {
    companion object {
        fun fromEntity(asistente: Asistente): UserResponse {
            return UserResponse(
                id = asistente.id,
                username = asistente.username,
                correo = asistente.correo,
                profilePic = asistente.profile_pic,
                role = asistente.get_Role(),
                totalOrganizadoresSeguidos = asistente.totalOrganizadoresSeguidos(),
                fechaCreacion = asistente.fechaCreacion.toString()
            )
        }
    }
}