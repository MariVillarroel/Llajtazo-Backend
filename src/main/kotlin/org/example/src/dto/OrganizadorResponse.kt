package org.example.src.dto

data class OrganizadorResponse(
    val id: Int,
    val username: String,
    val correo: String,
    val profilePic: String,
    val nombreOrg: String,
    val numero: String,
    val role: String
    //val followers: Int,
    //val eventosCreados: List<EventoResponse> // otro DTO para eventos
)