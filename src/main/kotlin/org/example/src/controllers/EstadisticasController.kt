package org.example.src.controllers

import org.example.src.dto.EstadisticasRequest
import org.example.src.dto.EstadisticasResponse
import org.example.src.dto.EstadisticasUpdateRequest
import org.example.src.services.EstadisticasService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/estadisticas")
class EstadisticasController(
    private val estadisticasService: EstadisticasService
) {

    @PostMapping
    fun registrarEstadistica(@RequestBody request: EstadisticasRequest): ResponseEntity<EstadisticasResponse> {
        val response = estadisticasService.registrarEstadistica(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/{eventoId}/{dia}")
    fun obtenerEstadistica(
        @PathVariable eventoId: Int,
        @PathVariable dia: LocalDate
    ): ResponseEntity<EstadisticasResponse> {
        val estadistica = estadisticasService.obtenerEstadistica(eventoId, dia)
        return if (estadistica != null) {
            ResponseEntity.ok(estadistica)
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    @GetMapping("/evento/{eventoId}")
    fun listarPorEvento(@PathVariable eventoId: Int): ResponseEntity<List<EstadisticasResponse>> {
        val estadisticas = estadisticasService.listarPorEvento(eventoId)
        return ResponseEntity.ok(estadisticas)
    }

    @PatchMapping
    fun actualizarEstadistica(@RequestBody request: EstadisticasUpdateRequest): ResponseEntity<EstadisticasResponse> {
        val response = estadisticasService.actualizarEstadistica(request)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{eventoId}/{dia}")
    fun eliminarEstadistica(
        @PathVariable eventoId: Int,
        @PathVariable dia: LocalDate
    ): ResponseEntity<Void> {
        return if (estadisticasService.eliminarEstadistica(eventoId, dia)) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }
}