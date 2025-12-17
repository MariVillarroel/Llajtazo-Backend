package org.example.src.services

import org.example.src.dto.EstadisticasRequest
import org.example.src.dto.EstadisticasResponse
import org.example.src.dto.EstadisticasUpdateRequest
import org.example.src.models.EstadisticaId
import org.example.src.models.EstadisticasEventoDiario
import org.example.src.repositories.EstadisticasRepository
import org.example.src.repositories.EventoRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.NoSuchElementException

@Service
@Transactional
class EstadisticasService(
    private val estadisticasRepository: EstadisticasRepository,
    private val eventoRepository: EventoRepository
) {

    fun registrarEstadistica(request: EstadisticasRequest): EstadisticasResponse {
        val evento = eventoRepository.findById(request.eventoId)
            .orElseThrow { IllegalArgumentException("Evento con id ${request.eventoId} no existe") }

        val entity = EstadisticasEventoDiario(
            id = EstadisticaId(request.eventoId, request.dia),
            evento = evento,
            visitas = request.visitas,
            ticketsVendidos = request.ticketsVendidos
        )

        val saved = estadisticasRepository.save(entity)
        return EstadisticasResponse.fromEntity(saved)
    }

    @Transactional(readOnly = true)
    fun obtenerEstadistica(eventoId: Int, dia: LocalDate): EstadisticasResponse? {
        val id = EstadisticaId(eventoId, dia)
        val entity = estadisticasRepository.findById(id).orElse(null) ?: return null
        return EstadisticasResponse.fromEntity(entity)
    }

    @Transactional(readOnly = true)
    fun listarPorEvento(eventoId: Int): List<EstadisticasResponse> {
        return estadisticasRepository.findByEventoId(eventoId)
            .map { EstadisticasResponse.fromEntity(it) }
    }

    fun actualizarEstadistica(request: EstadisticasUpdateRequest): EstadisticasResponse {
        val id = EstadisticaId(request.eventoId, request.dia)
        val entity = estadisticasRepository.findById(id)
            .orElseThrow { NoSuchElementException("Estadística para evento ${request.eventoId} en día ${request.dia} no encontrada") }

        request.visitas?.let { entity.visitas = it }
        request.ticketsVendidos?.let { entity.ticketsVendidos = it }

        val updated = estadisticasRepository.save(entity)
        return EstadisticasResponse.fromEntity(updated)
    }

    fun eliminarEstadistica(eventoId: Int, dia: LocalDate): Boolean {
        val id = EstadisticaId(eventoId, dia)
        return estadisticasRepository.findById(id).map {
            estadisticasRepository.delete(it)
            true
        }.orElse(false)
    }
}