package org.example.src.services

import org.example.src.dto.OrganizadorRequest
import org.example.src.dto.OrganizadorResponse
import org.example.src.models.Organizador
import org.example.src.repositories.OrganizadorRepository
import org.springframework.stereotype.Service

@Service
class OrganizadorService(private val repository: OrganizadorRepository) {

    fun crearOrganizador(request: OrganizadorRequest): OrganizadorResponse {
        val organizador = Organizador(
            username = request.username,
            correo = request.correo,
            password = request.password,
            profile_pic = request.profilePic,
            nombre_org = request.nombreOrg,
            numero = request.numero
        )
        val saved = repository.save(organizador)
        return OrganizadorResponse(
            id = saved.id,
            username = saved.username,
            correo = saved.correo,
            profilePic = saved.profile_pic,
            nombreOrg = saved.nombre_org,
            numero = saved.numero,
            //followers = saved.followers,
            role = saved.get_Role()
        )
    }

    fun listarOrganizadores(): List<OrganizadorResponse> =
        repository.findAll().map {
            OrganizadorResponse(
                id = it.id,
                username = it.username,
                correo = it.correo,
                profilePic = it.profile_pic,
                nombreOrg = it.nombre_org,
                numero = it.numero,
                //followers = it.followers,
                role = it.get_Role()
            )
        }

    fun obtenerOrganizadorPorId(id: Int): OrganizadorResponse? {
        val organizador = repository.findById(id).orElse(null) ?: return null
        return OrganizadorResponse(
            id = organizador.id,
            username = organizador.username,
            correo = organizador.correo,
            profilePic = organizador.profile_pic,
            nombreOrg = organizador.nombre_org,
            numero = organizador.numero,
            //followers = organizador.followers,
            role = organizador.get_Role()
        )

    }

    fun eliminarOrganizador(id: Int): Boolean {
        return if (repository.existsById(id)) {
            repository.deleteById(id)
            true
        } else {
            false
        }
    }
}