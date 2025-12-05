package org.example.src.models

enum class EstadoPago(
    val descripcion: String,
    val esFinal: Boolean,
    val permiteReembolso: Boolean
) {
    PENDIENTE(
        descripcion = "Pago creado, esperando procesamiento",
        esFinal = false,
        permiteReembolso = false
    ),
    PROCESANDO(
        descripcion = "Pago en proceso de verificación",
        esFinal = false,
        permiteReembolso = false
    ),
    COMPLETADO(
        descripcion = "Pago exitosamente procesado",
        esFinal = true,
        permiteReembolso = true
    ),
    RECHAZADO(
        descripcion = "Pago rechazado por el proveedor",
        esFinal = true,
        permiteReembolso = false
    ),
    REEMBOLSADO(
        descripcion = "Pago reembolsado al cliente",
        esFinal = true,
        permiteReembolso = false
    ),
    CANCELADO(
        descripcion = "Pago cancelado por el usuario",
        esFinal = true,
        permiteReembolso = false
    ),
    FALLIDO(
        descripcion = "Pago falló por error técnico",
        esFinal = true,
        permiteReembolso = false
    );

    companion object {
        fun estadosExitosos(): List<EstadoPago> {
            return listOf(COMPLETADO)
        }

        fun estadosEnProceso(): List<EstadoPago> {
            return listOf(PENDIENTE, PROCESANDO)
        }

        fun estadosFinales(): List<EstadoPago> {
            return entries.filter { it.esFinal }
        }

        fun puedeCambiarA(estadoActual: EstadoPago, nuevoEstado: EstadoPago): Boolean {
            if (estadoActual.esFinal) return false

            val transicionesPermitidas = when (estadoActual) {
                PENDIENTE -> listOf(PROCESANDO, COMPLETADO, RECHAZADO, CANCELADO, FALLIDO)
                PROCESANDO -> listOf(COMPLETADO, RECHAZADO, FALLIDO)
                else -> emptyList()
            }

            return nuevoEstado in transicionesPermitidas
        }
    }
}