package org.example.src.dto

import org.example.src.models.Suscripcion
import java.time.LocalDateTime

data class SuscripcionResponseDTO(
    val id: Int,
    val tipo: String,
    val estado: String,
    val precioMensual: Double,
    val periodoMeses: Int,
    val fechaInicio: LocalDateTime,
    val fechaFin: LocalDateTime,
    val diasRestantes: Long,
    val estaActiva: Boolean,
    val tienePremium: Boolean,
    val organizadorId: Int,
    val organizadorUsername: String,
    val organizadorEmail: String?,
    val ultimoPago: UltimoPagoDTO?,
    val puedeCrearEventos: Boolean,
    val eventosCreados: Int,
    val eventosDisponibles: Int,
    val creadoEn: LocalDateTime,
    val actualizadoEn: LocalDateTime? = null,
    val enlaces: Map<String, String> = mapOf(
        "detalle" to "/api/suscripciones/$id",
        "renovar" to "/api/suscripciones/$id/renovar",
        "cancelar" to "/api/suscripciones/$id/cancelar"
    )
) {
    companion object {
        fun fromSuscripcion(suscripcion: Suscripcion): SuscripcionResponseDTO {
            val organizador = suscripcion.organizador

            return SuscripcionResponseDTO(
                id = suscripcion.id,
                tipo = suscripcion.tipo.name,
                estado = suscripcion.estado.name,
                precioMensual = suscripcion.precioMensual,
                periodoMeses = suscripcion.periodoMesesUltimoPago,
                fechaInicio = suscripcion.fechaInicio,
                fechaFin = suscripcion.fechaFin,
                diasRestantes = suscripcion.diasRestantes,
                estaActiva = suscripcion.estaActiva,
                tienePremium = suscripcion.tienePremiumActivo,
                organizadorId = organizador?.id ?: 0,
                organizadorUsername = organizador?.username ?: "",
                organizadorEmail = organizador?.correo,
                ultimoPago = if (suscripcion.ultimaReferenciaPago.isNotEmpty()) {
                    UltimoPagoDTO.fromSuscripcion(suscripcion)
                } else {
                    null
                },
                puedeCrearEventos = organizador?.puedeCrearEventos() ?: false,
                eventosCreados = organizador?.eventosCreados?.size ?: 0,
                eventosDisponibles = organizador?.eventosDisponibles() ?: 0,
                creadoEn = suscripcion.fechaInicio
            )
        }
    }
}