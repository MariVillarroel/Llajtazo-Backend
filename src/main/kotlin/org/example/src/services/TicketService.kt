package org.example.src.services

import org.example.src.dto.TicketResponseDto
import org.example.src.repositories.TicketRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TicketService(
    private val ticketRepository: TicketRepository
) {

    @Transactional(readOnly = true)
    fun listarPorZona(zonaId: Int): List<TicketResponseDto> =
        ticketRepository.findByZona_IdZona(zonaId).map {
            TicketResponseDto(
                idTicket = requireNotNull(it.idTicket),
                zonaId = requireNotNull(it.zona.idZona),
                estado = it.estado.name
            )
        }
}
