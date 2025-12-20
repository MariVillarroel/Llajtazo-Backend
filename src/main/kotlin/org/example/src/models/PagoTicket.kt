package org.example.src.pagos

import org.example.src.models.EstadoPago
import org.example.src.models.MetodoPago
import org.example.src.models.PagoBase
import java.time.LocalDateTime

/**
 * Pago mock para compra de tickets.
 * NO es entidad JPA: solo sirve para validar/procesar dentro del CheckoutService.
 */
class PagoTickets(
    override val metodo: MetodoPago,
    override val estado: EstadoPago = EstadoPago.PENDIENTE,
    override val referencia: String,
    override val fecha: LocalDateTime = LocalDateTime.now(),
    private val monto: Double,
    private val moneda: String // "BOB" o "USD" (si ya tienes enum Currency, úsalo aquí)
) : PagoBase() {

    override fun esMontoValido(): Boolean {
        // Mock: monto debe ser > 0 y razonable
        return monto > 0.0 && monto.isFinite()
    }

    override fun validar(): Boolean {
        if (!esMontoValido()) return false

        // Mock simple: referencia no vacía
        if (referencia.isBlank()) return false

        if (metodo != MetodoPago.TRANSFERENCIA_BANCARIA && referencia.trim().length < 4) return false

        return true
    }

    override fun procesar(): Boolean {
        // Mock: si valida, "procesa"
        return validar()
    }

    override fun generarRecibo(): Map<String, Any> {
        return mapOf(
            "tipo" to "PAGO_TICKETS_MOCK",
            "metodo" to metodo.name,
            "estado" to estado.name,
            "referencia" to referencia,
            "monto" to monto,
            "moneda" to moneda,
            "fecha" to fecha.toString()
        )
    }

    override fun estaCompletado(): Boolean {
        TODO("Not yet implemented")
    }

    override fun estaPendiente(): Boolean {
        TODO("Not yet implemented")
    }

    override fun estaFallido(): Boolean {
        TODO("Not yet implemented")
    }

    override fun resumen(): String {
        TODO("Not yet implemented")
    }
}
