package org.example.models

import org.example.src.models.Categoria
import org.example.src.models.UserRole

data class Organizador(
    override val id: Int,
    override val username: String,
    override val correo: String,
    override val passwordHash: String,
    override var profilePic: String? = null,
    override val tags: List<Categoria> = emptyList(),
    var nombreOrg: String,
    var numero: String
) : User {

    override fun getRole(): UserRole = UserRole.ORGANIZADOR
}
