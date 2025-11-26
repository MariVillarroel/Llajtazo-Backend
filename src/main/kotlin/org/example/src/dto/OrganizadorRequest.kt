package org.example.src.dto

data class OrganizadorRequest(
    val username: String,
    val correo: String,
    val password: String,
    val profilePic: String,
    val nombreOrg: String,
    val numero: String
    // followers normalmente no lo envía el cliente, se calcula en backend
)