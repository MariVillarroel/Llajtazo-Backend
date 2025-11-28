package org.example.src.services

import org.example.src.dto.DailyEventStatsResponse
import org.example.src.repositories.EventStatsRepository

class EventStatsService(
    private val statsRepository: EventStatsRepository
) {

    fun getDailyStats(eventoId: Int, organizerId: Int): DailyEventStatsResponse {

        // 1) Validar acceso premium
        //val isPremium = statsRepository.isOrganizerPremium(organizerId)
        //if (!isPremium) {
         //   throw IllegalAccessException("Acceso restringido: solo organizadores premium pueden ver estadísticas.")
        //}

        // 2) Obtener datos desde la BD
        val stats = statsRepository.getDailyStatsForEvent(eventoId)

        // 3) Validar existencia de registros
        if (stats.isEmpty()) {
            throw NoSuchElementException("No existen estadísticas registradas para el evento con ID $eventoId.")
        }

        // 4) Transformar datos a DTO para dashboards
        val estadisticas = stats.map { stat ->
            DailyEventStatsResponse.EstadisticaDiaria(
                fecha = stat.id.dia.toString(),       // usamos el campo de la PK compuesta
                visitas = stat.visitas,
                ticketsVendidos = stat.ticketsVendidos
            )
        }

        // 5) Retornar respuesta final
        return DailyEventStatsResponse(
            eventoId = eventoId,
            estadisticas = estadisticas
        )
    }
}
