package org.example.src.dto

import kotlinx.serialization.Serializable

@Serializable
data class DailyEventStatsResponse(
    val eventoId: Int,
    val estadisticas: List<EstadisticaDiaria>
) {
    @Serializable
    data class EstadisticaDiaria(
        val fecha: String,
        val visitas: Int,
        val ticketsVendidos: Int
    )
}