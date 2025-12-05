package org.example.src.dto

import jakarta.validation.constraints.NotBlank

data class LoginRequest(
    @field:NotBlank val correo: String,
    @field:NotBlank val password: String
)