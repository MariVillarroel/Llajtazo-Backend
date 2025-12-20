package org.example.src.services

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

    @Transactional(readOnly = true)
    fun login(correo: String, password: String): UserResponse? {
        val asistente = asistenteRepository.findByCorreo(correo) ?: return null
        return if (PasswordUtils.verifyPassword(password, asistente.password)) {
            UserResponse.fromEntity(asistente)
        } else null
    }

    fun crearAsistente(request: UserRequest): UserResponse {
        if (!ValidationUtils.isValidEmail(request.correo)) {
            throw IllegalArgumentException("Formato de correo electrónico inválido")
        }
        if (!ValidationUtils.isStrongPassword(request.password)) {
            throw IllegalArgumentException("La contraseña debe tener al menos 6 caracteres")
        }
        if (!ValidationUtils.isValidUsername(request.username)) {
            throw IllegalArgumentException("Username inválido")
        }

        if (asistenteRepository.findByCorreo(request.correo) != null) {
            throw IllegalArgumentException("El correo ya está registrado")
        }
        if (asistenteRepository.existsByUsername(request.username)) {
            throw IllegalArgumentException("El username ya está en uso")
        }

        val hashedPassword = PasswordUtils.hashPassword(request.password)

        val asistente = Asistente(
            username = request.username,
            nombreCompleto = request.nombreCompleto, // <-- agrega esto
            correo = request.correo,
            password = hashedPassword,
            profile_pic = request.profilePic
        )

        // inicializar intereses si vienen en el request
        request.categoriasIds?.forEach { categoriaId ->
            val categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow { IllegalArgumentException("Categoría con id $categoriaId no encontrada") }
            if (!asistente.tags.contains(categoria)) {
                asistente.tags.add(categoria)
            }
        }

        val saved = asistenteRepository.save(asistente)
        return UserResponse.fromEntity(saved)
    }

    @Transactional(readOnly = true)
    fun listarAsistentes(): List<UserResponse> =
        asistenteRepository.findAll().map { UserResponse.fromEntity(it) }

    @Transactional(readOnly = true)
    fun obtenerAsistentePorId(id: Int): UserResponse? {
        val asistente = asistenteRepository.findById(id).orElse(null) ?: return null
        return UserResponse.fromEntity(asistente)
    }

    fun actualizarAsistente(id: Int, request: UpdateAsistenteRequest): UserResponse {
        val asistente = asistenteRepository.findById(id)
            .orElseThrow { NoSuchElementException("Asistente con id $id no encontrado") }

        request.correo?.let {
            if (!ValidationUtils.isValidEmail(it)) throw IllegalArgumentException("Correo inválido")
            if (it != asistente.correo && asistenteRepository.findByCorreo(it) != null) {
                throw IllegalArgumentException("El correo $it ya está registrado")
            }
            asistente.correo = it
        }

        request.username?.let {
            if (!ValidationUtils.isValidUsername(it)) throw IllegalArgumentException("Username inválido")
            if (it != asistente.username && asistenteRepository.existsByUsername(it)) {
                throw IllegalArgumentException("El username $it ya está en uso")
            }
            asistente.username = it
        }

        request.password?.let {
            if (!ValidationUtils.isStrongPassword(it)) throw IllegalArgumentException("Contraseña inválida")
            asistente.password = PasswordUtils.hashPassword(it)
        }

        request.profilePic?.let { asistente.profile_pic = it }

        request.categoriasIds?.let { nuevosIds ->
            val nuevasCategorias = categoriaRepository.findAllById(nuevosIds)
            asistente.tags.clear()
            nuevasCategorias.forEach { cat ->
                if (!asistente.tags.contains(cat)) asistente.tags.add(cat)
            }
        }

        val asistenteActualizado = asistenteRepository.save(asistente)
        return UserResponse.fromEntity(asistenteActualizado)
    }

    fun eliminarAsistente(id: Int): Boolean {
        return if (asistenteRepository.existsById(id)) {
            asistenteRepository.deleteById(id)
            true
        } else false
    }

    @Transactional(readOnly = true)
    fun buscarPorCorreo(correo: String): UserResponse? {
        val asistente = asistenteRepository.findByCorreo(correo) ?: return null
        return UserResponse.fromEntity(asistente)
    }

    @Transactional(readOnly = true)
    fun existeAsistente(id: Int): Boolean = asistenteRepository.existsById(id)

    @Transactional(readOnly = true)
    fun verificarCredenciales(correo: String, password: String): Boolean {
        val asistente = asistenteRepository.findByCorreo(correo) ?: return false
        return PasswordUtils.verifyPassword(password, asistente.password)
    }

    fun agregarInteres(asistenteId: Int, categoriaId: Int): UserResponse {
        val asistente = asistenteRepository.findById(asistenteId)
            .orElseThrow { NoSuchElementException("Asistente no encontrado") }

        val categoria = categoriaRepository.findById(categoriaId)
            .orElseThrow { IllegalArgumentException("Categoría no encontrada") }

        if (!asistente.tags.contains(categoria)) {
            asistente.tags.add(categoria)
        }

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
        val asistentes = asistenteRepository.findByTagsId(categoriaId) // repo debe usar JOIN a.tags
        return asistentes.map { UserResponse.fromEntity(it) }
    }

    @Transactional(readOnly = true)
    fun obtenerAsistentePorUsername(username: String): UserResponse? {
        val asistente = asistenteRepository.findByUsername(username) ?: return null
        return UserResponse.fromEntity(asistente)
    }
}