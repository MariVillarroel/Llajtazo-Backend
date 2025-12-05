package org.example.src.dto

import org.example.src.models.Suscripcion
import java.time.LocalDateTime

data class UltimoPagoDTO(
    val monto: Double,
    val metodo: String,
    val referencia: String,
    val estado: String,
    val fecha: LocalDateTime,
    val periodoMeses: Int,
    val ultimos4Digitos: String? = null,
    val tipoTarjeta: String? = null,
    val procesadoPor: String? = null
) {
    companion object {
        fun fromSuscripcion(suscripcion: Suscripcion): UltimoPagoDTO {
            return UltimoPagoDTO(
                monto = suscripcion.ultimoMontoPago,
                metodo = suscripcion.ultimoMetodoPago?.name ?: "DESCONOCIDO",
                referencia = suscripcion.ultimaReferenciaPago,
                estado = suscripcion.estadoUltimoPago.name,
                fecha = suscripcion.fechaInicio,
                periodoMeses = suscripcion.periodoMesesUltimoPago
            )
        }
    }
}