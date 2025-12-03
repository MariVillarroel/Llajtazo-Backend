package org.example.src.models

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "organizadores")
class Organizador(
    override var username: String,
    override var correo: String,
    override var password: String,
    override var profile_pic: String? = null,

    @Column(name = "about", columnDefinition = "TEXT")
    var about: String? = null,

    @Column(name = "suscribed")
    var suscribed: Boolean = false,

    @Column(name = "created_at", updatable = false)
    override var fechaCreacion: LocalDateTime = LocalDateTime.now()



) : User() {

    // Rol
    override fun get_Role(): UserRole = UserRole.ORGANIZADOR

    // Relación con eventos - LAZY para evitar problemas de carga
    @OneToMany(mappedBy = "organizador", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var eventosCreados: MutableList<Evento> = mutableListOf()

    // Seguidores - relación ManyToMany con ASISTENTES
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "seguidores",
        joinColumns = [JoinColumn(name = "organizador_id")],
        inverseJoinColumns = [JoinColumn(name = "usuario_id")]
    )
    var followers: MutableList<Asistente> = mutableListOf()


    fun crearEvento(evento: Evento) {
        eventosCreados.add(evento)
    }

    fun deleteEvento(evento: Evento) {
        eventosCreados.remove(evento)
    }

    fun listarEventos(): List<Evento> = eventosCreados.toList()

    fun actualizarPerfil(
        nuevoUsername: String? = null,
        nuevoCorreo: String? = null,
        nuevoPassword: String? = null,
        nuevaProfilePic: String? = null,
        nuevoAbout: String? = null,
    ) {
        nuevoUsername?.let { this.username = it }
        nuevoCorreo?.let { this.correo = it }
        nuevoPassword?.let { this.password = it }
        nuevaProfilePic?.let { this.profile_pic = it }
        nuevoAbout?.let { this.about = it }
    }

    fun agregarSeguidor(asistente: Asistente) {
        if (!followers.contains(asistente)) {
            followers.add(asistente)
        }
    }

    fun removerSeguidor(asistente: Asistente) {
        followers.remove(asistente)
    }

    fun totalSeguidores(): Int = followers.size

    fun totalEventos(): Int = eventosCreados.size

    fun esSeguidor(asistente: Asistente): Boolean {
        return followers.contains(asistente)
    }

    fun obtenerIdsSeguidores(): List<Int> {
        return followers.map { asistente -> asistente.id }
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
        return "Organizador(id=$id, username='$username', correo='$correo', seguidores=${followers.size})"
    }
}