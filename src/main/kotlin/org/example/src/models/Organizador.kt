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
    var eventosCreados: MutableList<EventoBasico> = mutableListOf()

    // Seguidores
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "seguidores",
        joinColumns = [JoinColumn(name = "organizador_id")],
        inverseJoinColumns = [JoinColumn(name = "usuario_id")]
    )
    var followers: MutableList<Asistente> = mutableListOf()

    // Método para obtener si está suscrito (actualiza automáticamente el campo suscribed)
    @PostLoad
    @PostPersist
    @PostUpdate
    private fun actualizarEstadoSuscripcion() {
        // Calcula si tiene suscripción activa usando la propiedad calculada
        this.suscribed = this.suscripcion?.estaActiva ?: false
    }

    // Método para verificar si está suscrito (para lógica de negocio)
    fun estaSuscrito(): Boolean {
        return this.suscripcion?.estaActiva ?: false
    }

    // Método para asignar suscripción
    fun asignarSuscripcion(nuevaSuscripcion: Suscripcion) {
        this.suscripcion = nuevaSuscripcion
        nuevaSuscripcion.organizador = this
        actualizarEstadoSuscripcion()  // Actualiza el campo suscribed
    }

    // Método para renovar suscripción
    fun renovarSuscripcion(pago: PagoSuscripcion): Boolean {
        return if (suscripcion != null) {
            // Usa el método de Suscripcion que ahora está disponible
            val exito = suscripcion!!.procesarPagoYActualizar(pago)
            actualizarEstadoSuscripcion()
            exito
        } else {
            // Crear nueva suscripción si no existe
            val nuevaSuscripcion = Suscripcion.crearDesdePago(this, pago)
            asignarSuscripcion(nuevaSuscripcion)
            true
        }
    }

    // Método para cancelar suscripción
    fun cancelarSuscripcion() {
        suscripcion?.cancelar()  // Usa el nuevo método fluido
        actualizarEstadoSuscripcion()
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

    fun tienePremium(): Boolean {
        return this.suscripcion?.tienePremiumActivo ?: false
    }

    // Método para obtener información de suscripción
    fun informacionSuscripcion(): Map<String, Any?> {
        return if (suscripcion != null) {
            mapOf(
                "suscribed" to suscribed,
                "estaSuscrito" to estaSuscrito(),
                "suscripcionId" to suscripcion!!.id,
                "tipoSuscripcion" to suscripcion!!.tipo.name,  // Propiedad directa
                "estadoSuscripcion" to suscripcion!!.estado.name,  // Propiedad directa
                "fechaFin" to suscripcion!!.fechaFin,  // Propiedad directa
                "diasRestantes" to suscripcion!!.diasRestantes,  // Propiedad calculada
                "puedeCrearEventos" to puedeCrearEventos(),
                "eventosDisponibles" to eventosDisponibles()
            )
        } else {
            mapOf(
                "suscribed" to false,
                "estaSuscrito" to false,
                "puedeCrearEventos" to puedeCrearEventos(),
                "eventosDisponibles" to eventosDisponibles()
            )
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

    // Métodos existentes (con ajustes para la nueva lógica)
    fun crearEvento(eventoBasico: EventoBasico) {
        if (!puedeCrearEventos()) {
            throw IllegalStateException("No puede crear más eventos. Suscríbete para crear más.")
        }
        eventosCreados.add(eventoBasico)
        eventoBasico.organizador = this
    }

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

    // Métodos adicionales útiles
    fun obtenerPlanActual(): String {
        return suscripcion?.tipo?.name ?: "GRATUITO"
    }

    fun obtenerDiasRestantesSuscripcion(): Long? {
        return suscripcion?.diasRestantes
    }

    fun obtenerFechaVencimientoSuscripcion(): LocalDateTime? {
        return suscripcion?.fechaFin
    }

    fun tieneSuscripcionActiva(): Boolean {
        return suscripcion?.estaActiva ?: false
    }

    fun obtenerUltimoPagoInfo(): Map<String, Any?>? {
        return if (suscripcion != null && suscripcion!!.ultimaReferenciaPago.isNotEmpty()) {
            mapOf(
                "monto" to suscripcion!!.ultimoMontoPago,
                "metodo" to suscripcion!!.ultimoMetodoPago?.name,
                "fecha" to suscripcion!!.fechaInicio,
                "estado" to suscripcion!!.estadoUltimoPago.name,
                "referencia" to suscripcion!!.ultimaReferenciaPago
            )
        } else {
            null
        }
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