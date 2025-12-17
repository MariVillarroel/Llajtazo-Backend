package org.example.src.dto

import java.time.LocalDate
import org.example.src.models.EstadisticasEventoDiario

data class EstadisticasResponse(
    val eventoId: Int,
    val dia: LocalDate,
    val visitas: Int,
    val ticketsVendidos: Int
) {
    companion object {
        fun fromEntity(entity: EstadisticasEventoDiario): EstadisticasResponse =
            EstadisticasResponse(
                eventoId = entity.id.eventoId,
                dia = entity.id.dia,
                visitas = entity.visitas,
                ticketsVendidos = entity.ticketsVendidos
            )
    }
}