package org.example.models

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