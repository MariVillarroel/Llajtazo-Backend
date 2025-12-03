package org.example.src.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class OrganizadorRequest(
    @field:NotBlank(message = "Username es requerido")
    @field:Size(min = 3, max = 50, message = "Username debe tener entre 3 y 50 caracteres")
    val username: String,

    @field:NotBlank(message = "Correo es requerido")
    @field:Email(message = "Formato de correo inválido")
    val correo: String,

    @field:NotBlank(message = "Password es requerido")
    @field:Size(min = 6, message = "Password debe tener al menos 6 caracteres")
    val password: String,

    val profilePic: String? = "",

    val about: String? = null,

    val suscribed: Boolean = false
)
