package org.example.src.models

import jakarta.persistence.*
import kotlin.collections.toList

@Entity
@Table(name = "organizadores")
class Organizador(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    // Seguidores - relación ManyToMany con usuarios
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "organizador_seguidores",
        joinColumns = [JoinColumn(name = "organizador_id")],
        inverseJoinColumns = [JoinColumn(name = "usuario_id")]
    )
    var followers: MutableList<Asistente> = mutableListOf()

    @Column(name = "fecha_actualizacion")
    var fechaActualizacion: java.time.LocalDateTime = java.time.LocalDateTime.now()

    override fun get_Role(): String = "organizador"

    fun crearEvento(evento: Evento) {
        eventosCreados.add(evento)
        actualizarFecha()
    }

    fun deleteEvento(evento: Evento) {
        eventosCreados.remove(evento)
        actualizarFecha()
    }

    fun listarEventos(): List<Evento> = eventosCreados.toList()

    // ✅ CORRECCIÓN: Método que actualiza la instancia actual
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

    fun agregarSeguidor(usuario: Asistente) {
        if (!followers.contains(usuario)) {
            followers.add(usuario)
            actualizarFecha()
        }
    }

    fun removerSeguidor(usuario: Asistente) {
        followers.remove(usuario)
        actualizarFecha()
    }

    fun totalSeguidores(): Int = followers.size

    fun totalEventos(): Int = eventosCreados.size

    // equals y hashCode para JPA
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Organizador
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String {
        return "Organizador(id=$id, username='$username', correo='$correo', nombre_org='$nombre_org')"
    }
}