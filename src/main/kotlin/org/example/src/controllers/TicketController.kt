package org.example.src.controllers
import org.example.src.dto.TicketResponseDto
import org.example.src.services.TicketService

import org.springframework.web.bind.annotation.*
@RestController
@RequestMapping("/tickets")
class TicketController(private val ticketService: TicketService) {

    @GetMapping("/zona/{zonaId}")
    fun listar(@PathVariable zonaId: Int): List<TicketResponseDto> =
        ticketService.listarPorZona(zonaId)

}