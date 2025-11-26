package org.example.src.controllers

import org.example.src.dto.OrganizadorRequest
import org.example.src.dto.OrganizadorResponse
import org.example.src.services.OrganizadorService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/organizadores")
class OrganizadorController(private val service: OrganizadorService) {

    @PostMapping
    fun crearOrganizador(@RequestBody request: OrganizadorRequest): ResponseEntity<OrganizadorResponse> {
        val response = service.crearOrganizador(request)
        return ResponseEntity.ok(response)
    }

    @GetMapping
    fun listarOrganizadores(): ResponseEntity<List<OrganizadorResponse>> {
        val lista = service.listarOrganizadores()
        return ResponseEntity.ok(lista)
    }

    @GetMapping("/{id}")
    fun obtenerOrganizadorPorId(@PathVariable id: Int): ResponseEntity<OrganizadorResponse> {
        val organizador = service.obtenerOrganizadorPorId(id)
        return if (organizador != null) ResponseEntity.ok(organizador)
        else ResponseEntity.notFound().build()
    }

    @DeleteMapping("/{id}")
    fun eliminarOrganizador(@PathVariable id: Int): ResponseEntity<Void> {
        return if (service.eliminarOrganizador(id)) ResponseEntity.noContent().build()
        else ResponseEntity.notFound().build()
    }
}