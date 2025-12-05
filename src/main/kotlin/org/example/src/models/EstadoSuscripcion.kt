package org.example.src.models

enum class EstadoSuscripcion(
    val descripcion: String,
    val permitePagos: Boolean,
    val esActivo: Boolean
) {
    ACTIVA(
        descripcion = "Suscripción activa y funcionando",
        permitePagos = true,
        esActivo = true
    ),

    INACTIVA(
        descripcion =  "Suscripcion inactiva",
        permitePagos = true,
        esActivo = false
    ),

    PENDIENTE_PAGO(
        descripcion = "Esperando pago para activar",
        permitePagos = true,
        esActivo = false
    ),
    SUSPENDIDA(
        descripcion = "Suscripción temporalmente suspendida",
        permitePagos = false,
        esActivo = false
    ),
    CANCELADA(
    descripcion = "Suscripción cancelada",
    permitePagos = false,
    esActivo = true
    );

    companion object {
        fun estadosActivos(): List<EstadoSuscripcion> {
            return entries.filter { it.esActivo }
        }

        fun estadosQuePermitenPagos(): List<EstadoSuscripcion> {
            return entries.filter { it.permitePagos }
        }
    }
}