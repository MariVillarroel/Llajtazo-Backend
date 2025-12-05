package org.example.src.dto

import org.example.src.models.Organizador

data class OrganizadorResponse(
    val id: Int,
    val username: String,
    val correo: String,
    val profilePic: String?,
    val role: String,
    val totalSeguidores: Int,
    val totalEventos: Int,
    val seguidoresIds: List<Int>,
    val estaSuscrito: Boolean
) {
    companion object {
        fun fromEntity(organizador: Organizador): OrganizadorResponse {
            return OrganizadorResponse(
                id = organizador.id,
                username = organizador.username,
                correo = organizador.correo,
                profilePic = organizador.profile_pic,
                role = organizador.get_Role().name,
                totalSeguidores = organizador.totalSeguidores(),
                totalEventos = organizador.totalEventos(),
                seguidoresIds = organizador.obtenerIdsSeguidores(),
                estaSuscrito = organizador.suscribed
            )
        }
    }
}