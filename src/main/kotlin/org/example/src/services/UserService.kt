package org.example.src.services

import org.example.src.dto.LoginRequest
import org.example.src.dto.UserRequest
import org.example.src.dto.UserResponse
import org.example.src.dto.UpdateAsistenteRequest
import org.example.src.models.Asistente
import org.example.src.repositories.AsistenteRepository
import org.example.src.repositories.CategoriaRepository
import org.example.src.utils.PasswordUtils
import org.example.src.utils.ValidationUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.NoSuchElementException

@Service
@Transactional
class AsistenteService(
    private val asistenteRepository: AsistenteRepository,
    private val categoriaRepository: CategoriaRepository
) {

    // 🔐 MÉTODO DE LOGIN PARA ASISTENTE
    @Transactional(readOnly = true)
    fun login(correo: String, password: String): UserResponse? {
        val asistente = asistenteRepository.findByCorreo(correo) ?: return null

        return if (PasswordUtils.verifyPassword(password, asistente.password)) {
            UserResponse.fromEntity(asistente)
        } else {
            null
        }
    }

    // 🆕 CREAR ASISTENTE CON VALIDACIONES Y HASH
    fun crearAsistente(request: UserRequest): UserResponse {
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

        // ✅ VALIDAR UNICIDAD
        if (asistenteRepository.findByCorreo(request.correo) != null) {
            throw IllegalArgumentException("El correo ya está registrado")
        }
        if (asistenteRepository.existsByUsername(request.username)) {
            throw IllegalArgumentException("El username ya está en uso")
        }

        // 🔐 HASHEAR CONTRASEÑA
        val hashedPassword = PasswordUtils.hashPassword(request.password)

        val asistente = Asistente(
            username = request.username,
            correo = request.correo,
            password = hashedPassword, // ✅ Password hasheado
            profile_pic = request.profilePic
        )

        // Agregar categorías/intereses si vienen en el request
        request.categoriasIds?.forEach { categoriaId ->
            val categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow { IllegalArgumentException("Categoría con id $categoriaId no encontrada") }
            asistente.agregarInteres(categoria)
        }

        val saved = asistenteRepository.save(asistente)
        return UserResponse.fromEntity(saved)
    }

    // 📋 LISTAR ASISTENTES (sin cambios)
    @Transactional(readOnly = true)
    fun listarAsistentes(): List<UserResponse> =
        asistenteRepository.findAll().map { UserResponse.fromEntity(it) }

    // 👤 OBTENER POR ID (sin cambios)
    @Transactional(readOnly = true)
    fun obtenerAsistentePorId(id: Int): UserResponse? {
        val asistente = asistenteRepository.findById(id).orElse(null) ?: return null
        return UserResponse.fromEntity(asistente)
    }

    // ✏️ ACTUALIZAR ASISTENTE MEJORADO
    fun actualizarAsistente(id: Int, request: UpdateAsistenteRequest): UserResponse {
        val asistente = asistenteRepository.findById(id)
            .orElseThrow { NoSuchElementException("Asistente con id $id no encontrado") }

        // ✅ VALIDACIONES EN UPDATE
        request.correo?.let { nuevoCorreo ->
            if (!ValidationUtils.isValidEmail(nuevoCorreo)) {
                throw IllegalArgumentException("Formato de correo electrónico inválido")
            }
            if (nuevoCorreo != asistente.correo) {
                val asistenteExistente = asistenteRepository.findByCorreo(nuevoCorreo)
                if (asistenteExistente != null) {
                    throw IllegalArgumentException("El correo $nuevoCorreo ya está registrado")
                }
            }
        }

        request.username?.let { nuevoUsername ->
            if (nuevoUsername != asistente.username) {
                if (!ValidationUtils.isValidUsername(nuevoUsername)) {
                    throw IllegalArgumentException("Username inválido")
                }
                if (asistenteRepository.existsByUsername(nuevoUsername)) {
                    throw IllegalArgumentException("El username $nuevoUsername ya está en uso")
                }
            }
        }

        request.password?.let { nuevoPassword ->
            if (!ValidationUtils.isStrongPassword(nuevoPassword)) {
                throw IllegalArgumentException("La nueva contraseña no cumple con los requisitos mínimos")
            }
        }

        // 🔐 HASHEAR NUEVA CONTRASEÑA SI SE PROVEE
        val passwordToUpdate = request.password?.let { PasswordUtils.hashPassword(it) }

        // ✅ ACTUALIZAR CAMPOS CON PASSWORD HASHEADO
        request.username?.let { asistente.username = it }
        request.correo?.let { asistente.correo = it }
        passwordToUpdate?.let { asistente.password = it } // ✅ Usar hash si se proporcionó
        request.profilePic?.let { asistente.profile_pic = it }

        // Actualizar categorías/intereses si se proporcionan
        request.categoriasIds?.let { nuevosIds ->
            val nuevasCategorias = categoriaRepository.findAllById(nuevosIds)
            asistente.tags.clear()
            asistente.tags.addAll(nuevasCategorias)
        }

        val asistenteActualizado = asistenteRepository.save(asistente)
        return UserResponse.fromEntity(asistenteActualizado)
    }

    // 🗑️ ELIMINAR ASISTENTE (sin cambios)
    fun eliminarAsistente(id: Int): Boolean {
        return if (asistenteRepository.existsById(id)) {
            asistenteRepository.deleteById(id)
            true
        } else {
            false
        }
    }

    // 🔍 BUSCAR POR CORREO (sin cambios)
    @Transactional(readOnly = true)
    fun buscarPorCorreo(correo: String): UserResponse? {
        val asistente = asistenteRepository.findByCorreo(correo) ?: return null
        return UserResponse.fromEntity(asistente)
    }

    // ✅ VERIFICAR EXISTENCIA (sin cambios)
    @Transactional(readOnly = true)
    fun existeAsistente(id: Int): Boolean {
        return asistenteRepository.existsById(id)
    }

    // 🔐 VERIFICAR CREDENCIALES (método utilitario)
    @Transactional(readOnly = true)
    fun verificarCredenciales(correo: String, password: String): Boolean {
        val asistente = asistenteRepository.findByCorreo(correo) ?: return false
        return PasswordUtils.verifyPassword(password, asistente.password)
    }

    // 🏷️ MÉTODOS ESPECÍFICOS PARA ASISTENTE (sin cambios)

    fun agregarInteres(asistenteId: Int, categoriaId: Int): UserResponse {
        val asistente = asistenteRepository.findById(asistenteId)
            .orElseThrow { NoSuchElementException("Asistente no encontrado") }

        val categoria = categoriaRepository.findById(categoriaId)
            .orElseThrow { IllegalArgumentException("Categoría no encontrada") }

        asistente.agregarInteres(categoria)

        val actualizado = asistenteRepository.save(asistente)
        return UserResponse.fromEntity(actualizado)
    }

    fun eliminarInteres(asistenteId: Int, categoriaId: Int): UserResponse {
        val asistente = asistenteRepository.findById(asistenteId)
            .orElseThrow { NoSuchElementException("Asistente no encontrado") }

        val categoria = categoriaRepository.findById(categoriaId)
            .orElseThrow { IllegalArgumentException("Categoría no encontrada") }

        asistente.tags.remove(categoria)

        val actualizado = asistenteRepository.save(asistente)
        return UserResponse.fromEntity(actualizado)
    }

    @Transactional(readOnly = true)
    fun buscarPorInteres(categoriaId: Int): List<UserResponse> {
        val asistentes = asistenteRepository.findByCategoriaId(categoriaId)
        return asistentes.map { UserResponse.fromEntity(it) }
    }

    // 🆕 MÉTODO PARA OBTENER ASISTENTE POR USERNAME
    @Transactional(readOnly = true)
    fun obtenerAsistentePorUsername(username: String): UserResponse? {
        val asistente = asistenteRepository.findByUsername(username) ?: return null
        return UserResponse.fromEntity(asistente)
    }
}