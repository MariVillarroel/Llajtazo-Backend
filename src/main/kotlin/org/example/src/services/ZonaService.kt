package org.example.src.services

import org.example.src.dto.CrearZonaRequestDto
import org.example.src.dto.ZonaResponseDto
import org.example.src.models.Zona
import org.example.src.repositories.EventoRepository
import org.example.src.repositories.ZonaRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ZonaService(
    private val zonaRepository: ZonaRepository,
    private val eventoRepository: EventoRepository
) {

    @Transactional
    fun crearZona(dto: CrearZonaRequestDto): ZonaResponseDto {
        require(dto.nombre.isNotBlank()) { "El nombre es obligatorio" }
        require(dto.price > 0) { "El precio debe ser mayor a 0" }
        require(dto.cantidadTickets >= 0) { "La cantidadTickets no puede ser negativa" }

        val evento = eventoRepository.findById(dto.eventoId)
            .orElseThrow { NoSuchElementException("Evento con id ${dto.eventoId} no encontrado") }

        val zona = Zona(
            evento = evento,
            nombre = dto.nombre.trim(),
            price = dto.price,
            activo = dto.estado,
            cantidadTickets = dto.cantidadTickets,
            currency = dto.currency
        )

        val saved = zonaRepository.save(zona)
        return saved.toDto()
    }

    @Transactional(readOnly = true)
    fun listarZonasPorEvento(eventoId: Int): List<ZonaResponseDto> =
        zonaRepository.findByEvento_Id(eventoId).map { it.toDto() }

    private fun Zona.toDto() = ZonaResponseDto(
        idZona = requireNotNull(this.idZona),
        nombre = this.nombre,
        price = this.price,
        estado = this.activo,
        cantidadTickets = this.cantidadTickets,
        currency = this.currency,
        soldOut = this.isSoldOut()
    )
}
