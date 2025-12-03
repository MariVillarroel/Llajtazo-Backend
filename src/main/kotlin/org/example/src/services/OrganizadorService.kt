package org.example.src.services

import org.example.src.dto.OrganizadorRequest
import org.example.src.dto.OrganizadorResponse
import org.example.src.dto.UpdateOrganizadorRequest
import org.example.src.models.Organizador
import org.example.src.repositories.OrganizadorRepository
import org.example.src.utils.PasswordUtils
import org.example.src.utils.ValidationUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.NoSuchElementException

@Service
@Transactional
class OrganizadorService(private val repository: OrganizadorRepository) {

    @Transactional(readOnly = true)
    fun login(correo: String, password: String): OrganizadorResponse? {
        val organizador = repository.findByCorreo(correo) ?: return null
        return if (PasswordUtils.verifyPassword(password, organizador.password)) {
            OrganizadorResponse.fromEntity(organizador)
        } else null
    }

    fun crearOrganizador(request: OrganizadorRequest): OrganizadorResponse {
        if (!ValidationUtils.isValidEmail(request.correo)) {
            throw IllegalArgumentException("Formato de correo electrónico inválido")
        }
        if (!ValidationUtils.isStrongPassword(request.password)) {
            throw IllegalArgumentException("La contraseña debe tener al menos 6 caracteres")
        }
        if (!ValidationUtils.isValidUsername(request.username)) {
            throw IllegalArgumentException("Username inválido")
        }

        if (repository.findByCorreo(request.correo) != null) {
            throw IllegalArgumentException("El correo ya está registrado")
        }
        if (repository.existsByUsername(request.username)) {
            throw IllegalArgumentException("El username ya está en uso")
        }

        val hashedPassword = PasswordUtils.hashPassword(request.password)

        val organizador = Organizador(
            username = request.username,
            correo = request.correo,
            password = hashedPassword,
            profile_pic = request.profilePic
        )

        val saved = repository.save(organizador)
        return OrganizadorResponse.fromEntity(saved)
    }

    @Transactional(readOnly = true)
    fun listarOrganizadores(): List<OrganizadorResponse> =
        repository.findAll().map { OrganizadorResponse.fromEntity(it) }

    @Transactional(readOnly = true)
    fun obtenerOrganizadorPorId(id: Int): OrganizadorResponse? {
        val organizador = repository.findById(id).orElse(null) ?: return null
        return OrganizadorResponse.fromEntity(organizador)
    }

    fun actualizarOrganizador(id: Int, request: UpdateOrganizadorRequest): OrganizadorResponse {
        val organizador = repository.findById(id)
            .orElseThrow { NoSuchElementException("Organizador con id $id no encontrado") }

        request.correo?.let {
            if (!ValidationUtils.isValidEmail(it)) throw IllegalArgumentException("Correo inválido")
            if (it != organizador.correo && repository.findByCorreo(it) != null) {
                throw IllegalArgumentException("El correo $it ya está registrado")
            }
            organizador.correo = it
        }

        request.username?.let {
            if (!ValidationUtils.isValidUsername(it)) throw IllegalArgumentException("Username inválido")
            if (it != organizador.username && repository.existsByUsername(it)) {
                throw IllegalArgumentException("El username $it ya está en uso")
            }
            organizador.username = it
        }

        request.password?.let {
            if (!ValidationUtils.isStrongPassword(it)) throw IllegalArgumentException("Contraseña inválida")
            organizador.password = PasswordUtils.hashPassword(it)
        }

        request.profilePic?.let { organizador.profile_pic = it }


        val saved = repository.save(organizador)
        return OrganizadorResponse.fromEntity(saved)
    }

    fun eliminarOrganizador(id: Int): Boolean {
        return if (repository.existsById(id)) {
            repository.deleteById(id)
            true
        } else false
    }

    @Transactional(readOnly = true)
    fun buscarPorCorreo(correo: String): OrganizadorResponse? {
        val organizador = repository.findByCorreo(correo) ?: return null
        return OrganizadorResponse.fromEntity(organizador)
    }

    @Transactional(readOnly = true)
    fun existeOrganizador(id: Int): Boolean = repository.existsById(id)

    @Transactional(readOnly = true)
    fun verificarCredenciales(correo: String, password: String): Boolean {
        val organizador = repository.findByCorreo(correo) ?: return false
        return PasswordUtils.verifyPassword(password, organizador.password)
    }
}