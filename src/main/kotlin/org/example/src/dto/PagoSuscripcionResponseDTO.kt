package org.example.src.dto

import org.example.src.models.PagoSuscripcion
import java.time.LocalDateTime

data class PagoSuscripcionResponseDTO(
    val id: Int,
    val referencia: String,
    val monto: Double,
    val precioMensual: Double,
    val periodoMeses: Int,
    val metodo: String,
    val estado: String,
    val tipoOperacion: String,
    val fechaPago: LocalDateTime,
    val validoHasta: LocalDateTime,
    val organizadorId: Int,  // ✅ Este viene como parámetro
    val organizadorNombre: String?,

    // Información del recibo
    val recibo: Map<String, Any>,

    // Enlaces para acciones adicionales
    val enlaces: Map<String, String> = mapOf(
        "detalle" to "/api/pagos/{id}",
        "descargarRecibo" to "/api/pagos/{id}/recibo/pdf"
    )
) {
    companion object {
        fun fromPagoSuscripcion(
            pago: PagoSuscripcion,
            organizadorId: Int,
            organizadorNombre: String? = null
        ): PagoSuscripcionResponseDTO {
            return PagoSuscripcionResponseDTO(
                id = pago.id,
                referencia = pago.referencia,
                monto = pago.monto,
                precioMensual = 80.0,
                periodoMeses = pago.periodoMeses,
                metodo = pago.metodo.name,
                estado = pago.estado.name,
                tipoOperacion = pago.tipoOperacion.name,
                fechaPago = pago.fecha,
                validoHasta = pago.calcularValidoHasta(),
                organizadorId = organizadorId,  // ✅ Usar el parámetro
                organizadorNombre = organizadorNombre,
                recibo = pago.generarRecibo(),
                enlaces = mapOf(
                    "detalle" to "/api/pagos/${pago.id}",
                    "descargarRecibo" to "/api/pagos/${pago.id}/recibo/pdf"
                )
            )
        }
    }
}