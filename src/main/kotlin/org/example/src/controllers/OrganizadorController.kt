package org.example.src.controllers

import org.example.src.dto.OrganizadorRequest
import org.example.src.dto.OrganizadorResponse
import org.example.src.dto.UpdateOrganizadorRequest
import org.example.src.services.OrganizadorService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/organizadores")
class OrganizadorController(private val service: OrganizadorService) {

    @PostMapping
    fun crearOrganizador(@RequestBody request: OrganizadorRequest): ResponseEntity<Any> {
        return try {
            val response = service.crearOrganizador(request)
            ResponseEntity.status(HttpStatus.CREATED).body(response)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    @GetMapping
    fun listarOrganizadores(): ResponseEntity<List<OrganizadorResponse>> {
        val lista = service.listarOrganizadores()
        return ResponseEntity.ok(lista)
    }

    @GetMapping("/{id}")
    fun obtenerOrganizadorPorId(@PathVariable id: Int): ResponseEntity<Any> {
        return try {
            val organizador = service.obtenerOrganizadorPorId(id)
            if (organizador != null) ResponseEntity.ok(organizador)
            else ResponseEntity.notFound().build()
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Error al obtener organizador"))
        }
    }

    @PutMapping("/{id}")
    fun actualizarOrganizador(
        @PathVariable id: Int,
        @RequestBody request: UpdateOrganizadorRequest  // ← Cambiado aquí
    ): ResponseEntity<Any> {
        return try {
            val response = service.actualizarOrganizador(id, request)
            ResponseEntity.ok(response)
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    @DeleteMapping("/{id}")
    fun eliminarOrganizador(@PathVariable id: Int): ResponseEntity<Void> {
        return if (service.eliminarOrganizador(id)) ResponseEntity.noContent().build()
        else ResponseEntity.notFound().build()
    }
}