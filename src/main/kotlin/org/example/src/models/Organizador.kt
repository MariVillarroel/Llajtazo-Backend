package org.example.src.models

import jakarta.persistence.*
import kotlin.collections.map
import kotlin.collections.toList

@Entity
@Table(name = "organizadores")
class Organizador(
    final override var id: Int = 0,

    @Column(nullable = false, unique = true)
    final override var username: String,

    @Column(nullable = false, unique = true)
    final override var correo: String,

    @Column(nullable = false)
    final override var password: String,

    @Column(name = "profile_pic")
    final override var profile_pic: String = "",

    @Column(name = "nombre_org", nullable = false)
    var nombre_org: String,

    @Column(nullable = false)
    var numero: String

) : User() {

    // Relación con eventos - LAZY para evitar problemas de carga
    @OneToMany(mappedBy = "organizador", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var eventosCreados: MutableList<Evento> = mutableListOf()

    // Seguidores - relación ManyToMany con ASISTENTES
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "organizador_seguidores",
        joinColumns = [JoinColumn(name = "organizador_id")],
        inverseJoinColumns = [JoinColumn(name = "asistente_id")]  // Cambiado a asistente_id
    )
    var followers: MutableList<Asistente> = mutableListOf()

    @Column(name = "fecha_actualizacion")
    var fechaActualizacion: java.time.LocalDateTime = java.time.LocalDateTime.now()

    override fun get_Role(): UserRole = UserRole.ORGANIZADOR

    fun crearEvento(evento: Evento) {
        eventosCreados.add(evento)
        actualizarFecha()
    }

    fun deleteEvento(evento: Evento) {
        eventosCreados.remove(evento)
        actualizarFecha()
    }

    fun listarEventos(): List<Evento> = eventosCreados.toList()

    fun actualizarPerfil(
        nuevoUsername: String? = null,
        nuevoCorreo: String? = null,
        nuevoPassword: String? = null,
        nuevaProfilePic: String? = null,
        nuevoNombreOrg: String? = null,
        nuevoNumero: String? = null
    ) {
        nuevoUsername?.let { this.username = it }
        nuevoCorreo?.let { this.correo = it }
        nuevoPassword?.let { this.password = it }
        nuevaProfilePic?.let { this.profile_pic = it }
        nuevoNombreOrg?.let { this.nombre_org = it }
        nuevoNumero?.let { this.numero = it }

        actualizarFecha()
    }

    private fun actualizarFecha() {
        this.fechaActualizacion = java.time.LocalDateTime.now()
    }

    fun agregarSeguidor(asistente: Asistente) {
        if (!followers.contains(asistente)) {
            followers.add(asistente)
            actualizarFecha()
        }
    }

    fun removerSeguidor(asistente: Asistente) {
        followers.remove(asistente)
        actualizarFecha()
    }

    fun totalSeguidores(): Int = followers.size

    fun totalEventos(): Int = eventosCreados.size

    // Método para verificar si un asistente específico sigue a este organizador
    fun esSeguidor(asistente: Asistente): Boolean {
        return followers.contains(asistente)
    }

    // Método para obtener lista de seguidores (solo IDs)
    fun obtenerIdsSeguidores(): List<Int> {
        return followers.map { asistente: Asistente -> asistente.id }
    }

    // equals y hashCode para JPA
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Organizador
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String {
        return "Organizador(id=$id, username='$username', correo='$correo', nombre_org='$nombre_org', seguidores=${followers.size})"
    }
}