package org.example.models

interface User {
    val id: Int
    val username: String
    val correo: String
    val passwordHash: String
    var profilePic: String?
    val tags: List<Categoria>

    fun getRole(): UserRole
}