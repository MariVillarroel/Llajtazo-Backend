package org.example.src.dto

import org.example.src.models.Organizador

data class OrganizadorResponse(
    val id: Int,
    val username: String,
    val correo: String,
    val profilePic: String,
    val nombreOrg: String,
    val numero: String,
    val role: String,
    val totalSeguidores: Int,
    val totalEventos: Int,
    val fechaCreacion: String,
    val fechaActualizacion: String
) {
    companion object {
        fun fromEntity(organizador: Organizador): OrganizadorResponse {
            return OrganizadorResponse(
                id = organizador.id,
                username = organizador.username,
                correo = organizador.correo,
                profilePic = organizador.profile_pic,
                nombreOrg = organizador.nombre_org,
                numero = organizador.numero,
                role = organizador.get_Role(),
                totalSeguidores = organizador.totalSeguidores(),
                totalEventos = organizador.totalEventos(),
                fechaCreacion = organizador.fechaCreacion.toString(),
                fechaActualizacion = organizador.fechaActualizacion.toString()
            )
        }
    }
}