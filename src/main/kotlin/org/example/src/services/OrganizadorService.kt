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

    // 🔐 MÉTODO DE LOGIN
    @Transactional(readOnly = true)
    fun login(correo: String, password: String): OrganizadorResponse? {
        val organizador = repository.findByCorreo(correo) ?: return null

        return if (PasswordUtils.verifyPassword(password, organizador.password)) {
            OrganizadorResponse.fromEntity(organizador)
        } else {
            null
        }
    }

    // 🆕 CREAR ORGANIZADOR CON VALIDACIONES Y HASH
    fun crearOrganizador(request: OrganizadorRequest): OrganizadorResponse {
        // ✅ VALIDACIONES BÁSICAS
        if (!ValidationUtils.isValidEmail(request.correo)) {
            throw IllegalArgumentException("Formato de correo electrónico inválido")
        }

        if (!ValidationUtils.isStrongPassword(request.password)) {
            throw IllegalArgumentException("La contraseña debe tener al menos 6 caracteres")
        }

        if (!ValidationUtils.isValidUsername(request.username)) {
            throw IllegalArgumentException("Username debe tener entre 3 y 50 caracteres y solo letras, números, _ o .")
        }

        if (!ValidationUtils.isValidPhoneNumber(request.numero)) {
            throw IllegalArgumentException("Número de teléfono inválido. Use solo números, máximo 15 dígitos")
        }

        // ✅ VALIDAR UNICIDAD
        if (repository.findByCorreo(request.correo) != null) {
            throw IllegalArgumentException("El correo ya está registrado")
        }
        if (repository.existsByUsername(request.username)) {
            throw IllegalArgumentException("El username ya está en uso")
        }

        // 🔐 HASHEAR CONTRASEÑA
        val hashedPassword = PasswordUtils.hashPassword(request.password)

        val organizador = Organizador(
            username = request.username,
            correo = request.correo,
            password = hashedPassword, // ✅ Password hasheado
            profile_pic = request.profilePic,
            nombre_org = request.nombreOrg,
            numero = request.numero
        )

        val saved = repository.save(organizador)
        return OrganizadorResponse.fromEntity(saved)
    }

    // 📋 LISTAR ORGANIZADORES (sin cambios)
    @Transactional(readOnly = true)
    fun listarOrganizadores(): List<OrganizadorResponse> =
        repository.findAll().map { OrganizadorResponse.fromEntity(it) }

    // 👤 OBTENER POR ID (sin cambios)
    @Transactional(readOnly = true)
    fun obtenerOrganizadorPorId(id: Int): OrganizadorResponse? {
        val organizador = repository.findById(id).orElse(null) ?: return null
        return OrganizadorResponse.fromEntity(organizador)
    }

    // ✏️ ACTUALIZAR ORGANIZADOR MEJORADO
    fun actualizarOrganizador(id: Int, request: UpdateOrganizadorRequest): OrganizadorResponse {
        val organizador = repository.findById(id)
            .orElseThrow { NoSuchElementException("Organizador con id $id no encontrado") }

        // ✅ VALIDACIONES EN UPDATE
        request.correo?.let { nuevoCorreo ->
            if (!ValidationUtils.isValidEmail(nuevoCorreo)) {
                throw IllegalArgumentException("Formato de correo electrónico inválido")
            }
            if (nuevoCorreo != organizador.correo) {
                val organizadorExistente = repository.findByCorreo(nuevoCorreo)
                if (organizadorExistente != null) {
                    throw IllegalArgumentException("El correo $nuevoCorreo ya está registrado")
                }
            }
        }

        request.username?.let { nuevoUsername ->
            if (nuevoUsername != organizador.username) {
                if (!ValidationUtils.isValidUsername(nuevoUsername)) {
                    throw IllegalArgumentException("Username inválido")
                }
                if (repository.existsByUsername(nuevoUsername)) {
                    throw IllegalArgumentException("El username $nuevoUsername ya está en uso")
                }
            }
        }

        request.password?.let { nuevoPassword ->
            if (!ValidationUtils.isStrongPassword(nuevoPassword)) {
                throw IllegalArgumentException("La nueva contraseña no cumple con los requisitos mínimos")
            }
        }

        request.numero?.let { nuevoNumero ->
            if (!ValidationUtils.isValidPhoneNumber(nuevoNumero)) {
                throw IllegalArgumentException("Número de teléfono inválido")
            }
        }

        // 🔐 HASHEAR NUEVA CONTRASEÑA SI SE PROVEE
        val passwordToUpdate = request.password?.let { PasswordUtils.hashPassword(it) }

        // ✅ ACTUALIZAR PERFIL CON PASSWORD HASHEADO
        organizador.actualizarPerfil(
            nuevoUsername = request.username,
            nuevoCorreo = request.correo,
            nuevoPassword = passwordToUpdate, // ✅ Usar hash si se proporcionó nueva contraseña
            nuevaProfilePic = request.profilePic,
            nuevoNombreOrg = request.nombreOrg,
            nuevoNumero = request.numero
        )

        val organizadorActualizado = repository.save(organizador)
        return OrganizadorResponse.fromEntity(organizadorActualizado)
    }

    // 🗑️ ELIMINAR ORGANIZADOR (sin cambios)
    fun eliminarOrganizador(id: Int): Boolean {
        return if (repository.existsById(id)) {
            repository.deleteById(id)
            true
        } else {
            false
        }
    }

    // 🔍 BUSCAR POR CORREO (sin cambios)
    @Transactional(readOnly = true)
    fun buscarPorCorreo(correo: String): OrganizadorResponse? {
        val organizador = repository.findByCorreo(correo) ?: return null
        return OrganizadorResponse.fromEntity(organizador)
    }

    // ✅ VERIFICAR EXISTENCIA (sin cambios)
    @Transactional(readOnly = true)
    fun existeOrganizador(id: Int): Boolean {
        return repository.existsById(id)
    }

    // 🔐 VERIFICAR CREDENCIALES (método utilitario)
    @Transactional(readOnly = true)
    fun verificarCredenciales(correo: String, password: String): Boolean {
        val organizador = repository.findByCorreo(correo) ?: return false
        return PasswordUtils.verifyPassword(password, organizador.password)
    }
}