package org.example.src.dto

import org.example.src.models.Organizador
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

data class OrganizadorConSuscripcionDTO(
    // Información básica del organizador
    val id: Int,
    val username: String,
    val correo: String,
    val profilePic: String?,
    val about: String?,
    val fechaCreacion: LocalDateTime,

    // Estadísticas
    val totalEventos: Int,
    val totalSeguidores: Int,

    // Estado de suscripción (lo más importante)
    val estaSuscrito: Boolean,
    val suscripcionActiva: Boolean,
    val tienePremium: Boolean,
    val puedeCrearEventos: Boolean,

    // Detalles de la suscripción (si existe)
    val suscripcion: SuscripcionResumenDTO?,

    // Plan y capacidades actuales
    val planActual: String?,               // "GRATUITO", "PREMIUM"
    val eventosDisponibles: Int,           // -1 = ilimitado, 0 = ninguno, 3 = gratis
    val eventosCreadosEsteMes: Int = 0,

    // Fechas importantes (si está suscrito)
    val diasRestantesSuscripcion: Long?,
    val fechaVencimientoSuscripcion: LocalDateTime?,

    // Información del último pago (si aplica)
    val ultimoPago: PagoResumenDTO?,

    // Metadatos
    val rol: String = "ORGANIZADOR",
    val cuentaVerificada: Boolean = false,

    // Enlaces para acciones
    val enlaces: Map<String, String> = mapOf(
        "perfil" to "/api/organizadores/{id}",
        "eventos" to "/api/organizadores/{id}/eventos",
        "seguidores" to "/api/organizadores/{id}/seguidores",
        "suscripcion" to "/api/organizadores/{id}/suscripcion",
        "actualizarPerfil" to "/api/organizadores/{id}/actualizar",
        "suscribirse" to "/api/suscripciones/procesar-pago"
    )
) {
    companion object {
        fun fromOrganizador(organizador: Organizador): OrganizadorConSuscripcionDTO {
            val suscripcion = organizador.suscripcion

            return OrganizadorConSuscripcionDTO(
                id = organizador.id,
                username = organizador.username,
                correo = organizador.correo,
                profilePic = organizador.profile_pic,
                about = organizador.about,
                fechaCreacion = organizador.fechaCreacion,

                totalEventos = organizador.totalEventos(),
                totalSeguidores = organizador.totalSeguidores(),

                estaSuscrito = organizador.estaSuscrito(),
                suscripcionActiva = suscripcion?.estaActiva ?: false,
                tienePremium = suscripcion?.tienePremiumActivo ?: false,
                puedeCrearEventos = organizador.puedeCrearEventos(),

                suscripcion = suscripcion?.let { SuscripcionResumenDTO.fromSuscripcion(it) },

                planActual = suscripcion?.tipo?.name ?: "GRATUITO",  // Propiedad directa
                eventosDisponibles = organizador.eventosDisponibles(),
                eventosCreadosEsteMes = calcularEventosEsteMes(organizador),

                diasRestantesSuscripcion = suscripcion?.diasRestantes,  // Propiedad calculada
                fechaVencimientoSuscripcion = suscripcion?.fechaFin,  // Propiedad directa

                ultimoPago = if (suscripcion != null && suscripcion.ultimaReferenciaPago.isNotEmpty()) {
                    PagoResumenDTO(
                        monto = suscripcion.ultimoMontoPago,
                        metodo = suscripcion.ultimoMetodoPago?.name,
                        fecha = suscripcion.fechaInicio,  // Propiedad directa
                        estado = suscripcion.estadoUltimoPago.name  // Propiedad directa
                    )
                } else {
                    null
                },

                enlaces = mapOf(
                    "perfil" to "/api/organizadores/${organizador.id}",
                    "eventos" to "/api/organizadores/${organizador.id}/eventos",
                    "seguidores" to "/api/organizadores/${organizador.id}/seguidores",
                    "suscripcion" to "/api/organizadores/${organizador.id}/suscripcion",
                    "actualizarPerfil" to "/api/organizadores/${organizador.id}/actualizar",
                    "suscribirse" to "/api/suscripciones/procesar-pago?organizadorId=${organizador.id}"
                )
            )
        }

        private fun calcularEventosEsteMes(organizador: Organizador): Int {
            val inicioMes = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0)
            return organizador.eventosCreados
                .count { it.fechaCreacion.isAfter(inicioMes) }
        }
    }
}

// DTO auxiliar para resumen de suscripción - Versión Refactorizada
data class SuscripcionResumenDTO(
    val id: Int,
    val tipo: String,
    val estado: String,
    val fechaInicio: LocalDateTime,
    val fechaFin: LocalDateTime,
    val diasRestantes: Long,
    val precioMensual: Double
) {
    companion object {
        fun fromSuscripcion(suscripcion: org.example.src.models.Suscripcion): SuscripcionResumenDTO {
            return SuscripcionResumenDTO(
                id = suscripcion.id,
                tipo = suscripcion.tipo.name,  // Propiedad directa
                estado = suscripcion.estado.name,  // Propiedad directa
                fechaInicio = suscripcion.fechaInicio,  // Propiedad directa
                fechaFin = suscripcion.fechaFin,  // Propiedad directa
                diasRestantes = suscripcion.diasRestantes,  // Propiedad calculada
                precioMensual = suscripcion.precioMensual  // Propiedad directa
            )
        }
    }
}

// DTO auxiliar para resumen de pago
data class PagoResumenDTO(
    val monto: Double,
    val metodo: String?,
    val fecha: LocalDateTime,
    val estado: String
)