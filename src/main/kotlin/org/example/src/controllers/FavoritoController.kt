package org.example.src.controllers

import org.example.src.models.Favorito
import org.example.src.models.Evento
import org.example.src.services.FavoritoService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/asistentes/{asistenteId}/favoritos")
class FavoritoController(
    private val favoritoService: FavoritoService
) {

    // 1) Endpoint para guardar un evento como favorito (HIGH prioridad)
    // POST /asistentes/{asistenteId}/favoritos/{eventoId}
    @PostMapping("/{eventoId}")
    fun guardarFavorito(
        @PathVariable asistenteId: Int,
        @PathVariable eventoId: Int
    ): ResponseEntity<Favorito> {
        val favorito = favoritoService.guardarFavorito(asistenteId, eventoId)
        return ResponseEntity.ok(favorito)
    }

    // 2) Listar eventos guardados con orden cronológico (MEDIUM)
    // GET /asistentes/{asistenteId}/favoritos
    @GetMapping
    fun listarFavoritos(
        @PathVariable asistenteId: Int
    ): ResponseEntity<List<Favorito>> {
        val favoritos = favoritoService.listarFavoritos(asistenteId)
        return ResponseEntity.ok(favoritos)
    }

    // 3) Integración de los favoritos para el calendario del usuario (LOW)
    // GET /asistentes/{asistenteId}/favoritos/calendario
    @GetMapping("/calendario")
    fun listarEventosFavoritosParaCalendario(
        @PathVariable asistenteId: Int
    ): ResponseEntity<List<Evento>> {
        val eventos = favoritoService.listarEventosFavoritosParaCalendario(asistenteId)
        return ResponseEntity.ok(eventos)
    }
}
