package org.example.src.controllers

import org.example.src.dto.CrearZonaRequestDto
import org.example.src.dto.ZonaResponseDto
import org.example.src.services.ZonaService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/zonas")
class ZonaController(
    private val zonaService: ZonaService
) {

    @PostMapping
    fun crear(@RequestBody dto: CrearZonaRequestDto): ZonaResponseDto =
        zonaService.crearZona(dto)

    @GetMapping("/evento/{eventoId}")
    fun listarPorEvento(@PathVariable eventoId: Int): List<ZonaResponseDto> =
        zonaService.listarZonasPorEvento(eventoId)

}