package org.example.src.dto

data class UpdateOrganizadorRequest(
    val username: String? = null,
    val correo: String? = null,
    val password: String? = null,
    val profilePic: String? = null,
    val nombreOrg: String? = null,
    val numero: String? = null
)