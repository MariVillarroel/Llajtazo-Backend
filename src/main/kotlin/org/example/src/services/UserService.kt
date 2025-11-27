package org.example.src.services

import org.example.src.dto.UserRegisterRequest
import org.example.src.dto.UserResponse
import org.example.src.models.Asistente
import org.example.models.CreadorUser
import org.example.src.repositories.UserRepository
import org.example.src.utils.PasswordUtils
import org.example.src.utils.ValidationUtils

class UserService(
    private val userRepository: UserRepository
) {
    fun registerUser(request: UserRegisterRequest): UserResponse {
        // 1) Validaciones
        require(ValidationUtils.isValidEmail(request.correo)) { "Correo inválido" }
        require(ValidationUtils.isStrongPassword(request.password)) { "Contraseña muy débil" }

        if (userRepository.findByEmail(request.correo) != null) {
            throw IllegalStateException("El correo ya está registrado")
        }

        // 2) Encriptar contraseña
        val hashed = PasswordUtils.hashPassword(request.password)

        // 3) Convertir a modelo de dominio
        val domainUser = CreadorUser.crearUser(
            id = 0,
            tipo = request.tipo,
            username = request.username,
            correo = request.correo,
            passwordHash = hashed
        )

        // 4) Guardar en BD
        val id = userRepository.create(domainUser as Asistente)

        // 5) Devolver DTO de respuesta
        return UserResponse(
            id = id,
            username = domainUser.username,
            correo = domainUser.correo,
            role = domainUser.getRole()
        )
    }
}