package org.example.src.models

import jakarta.persistence.*

@Entity
@Table(name = "organizadores")
data class Organizador (
    override val username: String,
    override val correo: String,
    override val password: String,
    override val profile_pic: String,
    val eventosCreados: MutableList<Evento> = mutableListOf(),
    //val suscripcion: Suscripcion? = null
    val nombre_org: String,
    val numero: String,
    //val followers: MutableList<Asistente>

) : User() {
    override fun get_Role(): String = "organizador"

    fun crearEvento(evento: Evento) {
        eventosCreados.add(evento)
    }

    fun deleteEvento(evento: Evento) {
        eventosCreados.remove(evento)
    }

    fun listarEventos(): List<Evento> = eventosCreados

    fun actualizar_perfil(
        nuevoUsername: String? = null,
        nuevoCorreo: String? = null,
        nuevoPassword: String? = null,
        nuevaProfilePic: String? = null,
        nuevoNombreOrg: String? = null,
        nuevoNumero: String? = null,
        //nuevosFollowers: List<Asistente>? = null
    ): Organizador {
        return this.copy(
            username = nuevoUsername ?: this.username,
            correo = nuevoCorreo ?: this.correo,
            password = nuevoPassword ?: this.password,
            profile_pic = nuevaProfilePic ?: this.profile_pic,
            nombre_org = nuevoNombreOrg ?: this.nombre_org,
            numero = nuevoNumero ?: this.numero
        )
    }
}