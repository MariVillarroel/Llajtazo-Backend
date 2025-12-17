package org.example.src.controllers

import org.example.src.dto.EventoRequest
import org.example.src.dto.EventoResponse
import org.example.src.dto.UpdateEventoRequest
import org.example.src.services.EventoService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/eventos")
class EventoController(
    private val eventoService: EventoService
) {

    @PostMapping
    fun crearEvento(@RequestBody request: EventoRequest): ResponseEntity<EventoResponse> {
        val response = eventoService.crearEvento(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/{id}")
    fun obtenerEvento(@PathVariable id: Int): ResponseEntity<EventoResponse> {
        val evento = eventoService.obtenerEvento(id)
        return if (evento != null) {
            ResponseEntity.ok(evento)
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    @GetMapping("/futuros")
    fun listarEventosFuturos(): ResponseEntity<List<EventoResponse>> =
        ResponseEntity.ok(eventoService.listarEventosFuturos())

    @GetMapping("/organizador/{organizadorId}")
    fun listarPorOrganizador(@PathVariable organizadorId: Int): ResponseEntity<List<EventoResponse>> =
        ResponseEntity.ok(eventoService.listarEventosPorOrganizador(organizadorId))

    @PatchMapping
    fun actualizarEvento(@RequestBody request: UpdateEventoRequest): ResponseEntity<EventoResponse> {
        val response = eventoService.actualizarEvento(request)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    fun eliminarEvento(@PathVariable id: Int): ResponseEntity<Void> {
        return if (eventoService.eliminarEvento(id)) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }
}