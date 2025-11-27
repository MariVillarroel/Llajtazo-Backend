package org.example.src.services

import org.example.src.dto.OrganizadorRequest
import org.example.src.dto.OrganizadorResponse
import org.example.src.dto.UpdateOrganizadorRequest
import org.example.src.models.Organizador
import org.example.src.repositories.OrganizadorRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.NoSuchElementException

@Service
@Transactional
class OrganizadorService(private val repository: OrganizadorRepository) {

    fun crearOrganizador(request: OrganizadorRequest): OrganizadorResponse {
        // Validar que no existe el correo o username
        if (repository.findByCorreo(request.correo) != null) {
            throw IllegalArgumentException("El correo ya está registrado")
        }
        if (repository.existsByUsername(request.username)) {
            throw IllegalArgumentException("El username ya está en uso")
        }

        val organizador = Organizador(
            username = request.username,
            correo = request.correo,
            password = request.password, // En producción, encriptar esto
            profile_pic = request.profilePic,
            nombre_org = request.nombreOrg,
            numero = request.numero
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

    // ✅ CORRECCIÓN: Método de actualización que funciona con JPA
    fun actualizarOrganizador(id: Int, request: UpdateOrganizadorRequest): OrganizadorResponse {
        val organizador = repository.findById(id)
            .orElseThrow { NoSuchElementException("Organizador con id $id no encontrado") }

        // Validar unicidad si se cambia correo o username
        request.correo?.let { nuevoCorreo ->
            if (nuevoCorreo != organizador.correo) {
                val organizadorExistente = repository.findByCorreo(nuevoCorreo)
                if (organizadorExistente != null) {
                    throw IllegalArgumentException("El correo $nuevoCorreo ya está registrado")
                }
            }
        }

        request.username?.let { nuevoUsername ->
            if (nuevoUsername != organizador.username) {
                if (repository.existsByUsername(nuevoUsername)) {
                    throw IllegalArgumentException("El username $nuevoUsername ya está en uso")
                }
            }
        }

        // Actualizar campos usando el método de la entidad
        organizador.actualizarPerfil(
            nuevoUsername = request.username,
            nuevoCorreo = request.correo,
            nuevoPassword = request.password,
            nuevaProfilePic = request.profilePic,
            nuevoNombreOrg = request.nombreOrg,
            nuevoNumero = request.numero
        )

        // JPA automáticamente detecta los cambios y los persiste por el @Transactional
        val organizadorActualizado = repository.save(organizador)
        return OrganizadorResponse.fromEntity(organizadorActualizado)
    }

    fun eliminarOrganizador(id: Int): Boolean {
        return if (repository.existsById(id)) {
            repository.deleteById(id)
            true
        } else {
            false
        }
    }

    @Transactional(readOnly = true)
    fun buscarPorCorreo(correo: String): OrganizadorResponse? {
        val organizador = repository.findByCorreo(correo) ?: return null
        return OrganizadorResponse.fromEntity(organizador)
    }

    @Transactional(readOnly = true)
    fun existeOrganizador(id: Int): Boolean {
        return repository.existsById(id)
    }
}