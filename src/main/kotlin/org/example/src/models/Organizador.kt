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

    // Relación con Suscripción
    @OneToOne(mappedBy = "organizador", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var suscripcion: Suscripcion? = null,

    @Column(name = "created_at", updatable = false)
    override var fechaCreacion: LocalDateTime = LocalDateTime.now()

) : User() {

    // Rol
    override fun get_Role(): UserRole = UserRole.ORGANIZADOR

    // Relación con eventos
    @OneToMany(mappedBy = "organizador", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var eventosCreados: MutableList<EventoEntity> = mutableListOf()

    // Seguidores
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "seguidores",
        joinColumns = [JoinColumn(name = "organizador_id")],
        inverseJoinColumns = [JoinColumn(name = "usuario_id")]
    )
    var followers: MutableList<Asistente> = mutableListOf()

    // Método para verificar si está suscrito (para lógica de negocio)
    fun estaSuscrito(): Boolean {
        return this.suscripcion?.estaActiva ?: false
    }


    // Método para verificar si puede crear eventos
    fun puedeCrearEventos(): Boolean {
        return when {
            // Usa propiedades calculadas de Suscripcion
            estaSuscrito() && suscripcion?.tienePremiumActivo == true -> true
            !estaSuscrito() && eventosCreados.size < 3 -> true  // 3 eventos gratis
            else -> false
        }
    }

    // Método para obtener eventos disponibles
    fun eventosDisponibles(): Int {
        val limite = if (estaSuscrito() && suscripcion?.tienePremiumActivo == true) {
            Int.MAX_VALUE
        } else if (!estaSuscrito()) {
            3  // 3 eventos gratis
        } else {
            0
        }

        return if (limite == Int.MAX_VALUE) {
            Int.MAX_VALUE
        } else {
            maxOf(0, limite - eventosCreados.size)
        }
    }

    fun totalSeguidores(): Int = followers.size

    fun totalEventos(): Int = eventosCreados.size

    fun obtenerIdsSeguidores(): List<Int> {
        return followers.map { asistente -> asistente.id }
    }

    // Métodos adicionales útiles
    fun obtenerPlanActual(): String {
        return suscripcion?.tipo?.name ?: "GRATUITO"
    }


    // equals y hashCode
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Organizador
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String {
        return "Organizador(id=$id, username='$username', " +
                "suscribed=$suscribed, eventos=${eventosCreados.size}, " +
                "plan=${obtenerPlanActual()})"
    }
}