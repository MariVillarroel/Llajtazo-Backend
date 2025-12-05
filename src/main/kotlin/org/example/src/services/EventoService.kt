package org.example.src.services

import org.example.src.dto.CreateEventoRequest
import org.example.src.dto.EventoResponse
import org.example.src.dto.UpdateEventoRequest
import org.example.src.models.Categoria
import org.example.src.models.EventoBasico
import org.example.src.models.Location
import org.example.src.models.Organizador
import org.example.src.repositories.CategoriaRepository
import org.example.src.repositories.EventoRepository
import org.example.src.repositories.LocationRepository
import org.example.src.repositories.OrganizadorRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.NoSuchElementException

@Service
@Transactional
class EventoService(
    private val eventoRepository: EventoRepository,
    private val organizadorRepository: OrganizadorRepository,
    private val locationRepository: LocationRepository,
    private val categoriaRepository: CategoriaRepository
) {

    fun crearEvento(request: CreateEventoRequest): EventoResponse {
        val organizador: Organizador? = request.organizadorId?.let {
            organizadorRepository.findById(it)
                .orElseThrow { IllegalArgumentException("Organizador con id $it no existe") }
        }

        val lugar: Location? = request.lugarId?.let {
            locationRepository.findById(it)
                .orElseThrow { IllegalArgumentException("Lugar con id $it no existe") }
        }

        val categoria: Categoria? = request.categoriaId?.let {
            categoriaRepository.findById(it)
                .orElseThrow { IllegalArgumentException("Categoría con id $it no existe") }
        }

        if (request.endTime.isBefore(request.startTime)) {
            throw IllegalArgumentException("endTime no puede ser antes de startTime")
        }

        val eventoBasico = EventoBasico(
            organizador = organizador,
            lugar = lugar,
            categoria = categoria,
            titulo = request.titulo,
            descripcion = request.descripcion,
            startTime = request.startTime,
            endTime = request.endTime,
            coverUrl = request.coverUrl,
            estado = request.estado ?: "PUBLISHED"
        )

        val saved = eventoRepository.save(eventoBasico)
        return EventoResponse.fromEntity(saved)
    }

    @Transactional(readOnly = true)
    fun obtenerEvento(id: Int): EventoResponse? {
        val evento = eventoRepository.findById(id).orElse(null) ?: return null
        return EventoResponse.fromEntity(evento)
    }

    @Transactional(readOnly = true)
    fun listarEventosFuturos(): List<EventoResponse> {
        val eventos = eventoRepository
            .findByEndTimeGreaterThanEqualAndEstadoOrderByStartTimeAsc(
                LocalDateTime.now(), "PUBLISHED"
            )
        return eventos.map { EventoResponse.fromEntity(it) }
    }

    @Transactional(readOnly = true)
    fun listarEventosPorOrganizador(organizadorId: Int): List<EventoResponse> {
        val eventos = eventoRepository.findByOrganizadorIdOrderByStartTimeDesc(organizadorId)
        return eventos.map { EventoResponse.fromEntity(it) }
    }

    fun actualizarEvento(id: Int, request: UpdateEventoRequest): EventoResponse {
        val evento = eventoRepository.findById(id)
            .orElseThrow { NoSuchElementException("Evento con id $id no encontrado") }

        request.lugarId?.let {
            evento.lugar = locationRepository.findById(it)
                .orElseThrow { IllegalArgumentException("Lugar con id $it no existe") }
        }

        request.categoriaId?.let {
            evento.categoria = categoriaRepository.findById(it)
                .orElseThrow { IllegalArgumentException("Categoría con id $it no existe") }
        }

        val nuevoStart = request.startTime ?: evento.startTime
        val nuevoEnd = request.endTime ?: evento.endTime
        if (nuevoEnd.isBefore(nuevoStart)) {
            throw IllegalArgumentException("endTime no puede ser antes de startTime")
        }

        evento.titulo = request.titulo ?: evento.titulo
        evento.descripcion = request.descripcion ?: evento.descripcion
        evento.startTime = nuevoStart
        evento.endTime = nuevoEnd
        evento.coverUrl = request.coverUrl ?: evento.coverUrl
        evento.estado = request.estado ?: evento.estado

        val saved = eventoRepository.save(evento)
        return EventoResponse.fromEntity(saved)
    }



    fun eliminarEvento(id: Int): Boolean {
        return if (eventoRepository.existsById(id)) {
            eventoRepository.deleteById(id)
            true
        } else {
            false
        }
    }
}
