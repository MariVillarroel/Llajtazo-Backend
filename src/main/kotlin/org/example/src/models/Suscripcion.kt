package org.example.src.models

import jakarta.persistence.*
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Entity
@Table(name = "suscripcion")
class Suscripcion(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idsuscripcion")
    val id: Int = 0,

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_suscripcion")
    var tipo: TipoSuscripcion = TipoSuscripcion.GRATUITO,

    @Column(name = "pagado_en")
    var fechaInicio: LocalDateTime = LocalDateTime.now(),

    @Column(name = "valido_hasta")
    var fechaFin: LocalDateTime = LocalDateTime.now().plusMonths(1),

    @Enumerated(EnumType.STRING)
    var estado: EstadoSuscripcion = EstadoSuscripcion.INACTIVA,

    var precioMensual: Double = 80.0,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizadores_id")
    var organizador: Organizador? = null,

    var ultimoMontoPago: Double = 0.0,

    @Enumerated(EnumType.STRING)
    var ultimoMetodoPago: MetodoPago? = null,

    var ultimaReferenciaPago: String = "",

    @Enumerated(EnumType.STRING)
    var ultimaOperacionPago: TipoOperacionSuscripcion = TipoOperacionSuscripcion.NUEVA,

    var periodoMesesUltimoPago: Int = 1,

    @Enumerated(EnumType.STRING)
    var estadoUltimoPago: EstadoPago = EstadoPago.PENDIENTE

) {
    // Propiedades calculadas (no se persisten en la BD)
    val diasRestantes: Long
        get() = ChronoUnit.DAYS.between(LocalDateTime.now(), fechaFin)
            .let { if (it < 0) 0 else it }

    val estaActiva: Boolean
        get() = LocalDateTime.now().isBefore(fechaFin) && estado == EstadoSuscripcion.ACTIVA

    val tienePremiumActivo: Boolean
        get() = tipo == TipoSuscripcion.PREMIUM && estaActiva

    // Constructor para crear desde PagoSuscripcion
    constructor(organizador: Organizador, pago: PagoSuscripcion) : this() {
        this.organizador = organizador
        this.actualizarDesdePago(pago)
    }

    fun actualizarDesdePago(pago: PagoSuscripcion) {
        // Guardar información del pago
        ultimoMontoPago = pago.monto
        ultimoMetodoPago = pago.metodo
        ultimaReferenciaPago = pago.referencia
        ultimaOperacionPago = pago.tipoOperacion
        periodoMesesUltimoPago = pago.periodoMeses
        estadoUltimoPago = pago.estado

        // Establecer fechas
        fechaInicio = pago.fecha
        fechaFin = pago.calcularValidoHasta()

        // Tipo según estado del pago
        tipo = determinarTipoSuscripcion(pago)

        // Estado de suscripción según pago
        estado = determinarEstadoSuscripcion(pago)
    }

    private fun determinarTipoSuscripcion(pago: PagoSuscripcion): TipoSuscripcion {
        return when {
            pago.estaCompletado() -> TipoSuscripcion.PREMIUM
            pago.estaFallido() -> TipoSuscripcion.GRATUITO
            else -> TipoSuscripcion.GRATUITO
        }
    }

    private fun determinarEstadoSuscripcion(pago: PagoSuscripcion): EstadoSuscripcion {
        return when {
            pago.estaCompletado() -> EstadoSuscripcion.ACTIVA
            pago.estaFallido() -> EstadoSuscripcion.SUSPENDIDA
            pago.estaPendiente() -> EstadoSuscripcion.PENDIENTE_PAGO
            else -> EstadoSuscripcion.INACTIVA
        }
    }

    fun procesarPagoYActualizar(pago: PagoSuscripcion): Boolean {
        val exito = pago.procesar()
        actualizarDesdePago(pago)
        return exito
    }

    fun renovar(periodoMeses: Int, metodoPago: MetodoPago): Suscripcion {
        val pago = PagoSuscripcion(
            metodo = metodoPago,
            periodoMeses = periodoMeses,
            tipoOperacion = TipoOperacionSuscripcion.RENOVACION
        )

        actualizarDesdePago(pago)
        return this
    }

    fun cancelar(): Suscripcion {
        estado = EstadoSuscripcion.CANCELADA
        return this
    }

    fun suspender(): Suscripcion {
        estado = EstadoSuscripcion.SUSPENDIDA
        return this
    }

    fun reactivar(): Suscripcion {
        if (LocalDateTime.now().isBefore(fechaFin)) {
            estado = EstadoSuscripcion.ACTIVA
        }
        return this
    }

    // Métodos de conveniencia para compatibilidad
    fun obtenerEstadoUltimoPago(): EstadoPago = estadoUltimoPago
    fun obtenerUltimaReferenciaPago(): String = ultimaReferenciaPago

    companion object {
        fun crearDesdePago(organizador: Organizador, pago: PagoSuscripcion): Suscripcion {
            return Suscripcion(organizador, pago)
        }
    }
}