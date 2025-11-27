package org.example.src.models

import org.example.src.models.Categoria
import org.example.models.User
import org.example.src.models.UserRole

data class Asistente(
    override val id: Int,
    override val username: String,
    override val correo: String,
    override val passwordHash: String,
    override var profilePic: String? = null,
    override val tags: List<Categoria> = emptyList()
) : User {

    override fun getRole(): UserRole = UserRole.ASISTENTE
}