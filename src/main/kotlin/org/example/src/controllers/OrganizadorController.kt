package org.example.src.controllers

import org.example.src.dto.*
import org.example.src.services.OrganizadorService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/organizadores")
class OrganizadorController(
    private val organizadorService: OrganizadorService
) {

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<OrganizadorResponse> {
        val organizador = organizadorService.login(request.correo, request.password)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        return ResponseEntity.ok(organizador) // ✅ ya es OrganizadorResponse
    }

    @PostMapping
    fun crearOrganizador(@RequestBody request: OrganizadorRequest): ResponseEntity<OrganizadorResponse> {
        val response = organizadorService.crearOrganizador(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping
    fun listarOrganizadores(): ResponseEntity<List<OrganizadorResponse>> {
        val lista = organizadorService.listarOrganizadores()
        return ResponseEntity.ok(lista)
    }

    @GetMapping("/{id}")
    fun obtenerOrganizadorPorId(@PathVariable id: Int): ResponseEntity<OrganizadorResponse> {
        val organizador = organizadorService.obtenerOrganizadorPorId(id)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        return ResponseEntity.ok(organizador) // ✅ ya es OrganizadorResponse
    }

    @PatchMapping("/{id}")
    fun actualizarOrganizador(
        @PathVariable id: Int,
        @RequestBody request: UpdateOrganizadorRequest
    ): ResponseEntity<OrganizadorResponse> {
        val response = organizadorService.actualizarOrganizador(id, request)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    fun eliminarOrganizador(@PathVariable id: Int): ResponseEntity<Void> {
        return if (organizadorService.eliminarOrganizador(id)) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    @GetMapping("/buscar")
    fun buscarPorCorreo(@RequestParam correo: String): ResponseEntity<OrganizadorResponse> {
        val organizador = organizadorService.buscarPorCorreo(correo)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        return ResponseEntity.ok(organizador)
    }

    @PostMapping("/verificar")
    fun verificarCredenciales(@RequestBody request: LoginRequest): ResponseEntity<Map<String, Boolean>> {
        val esValido = organizadorService.verificarCredenciales(request.correo, request.password)
        return ResponseEntity.ok(mapOf("valido" to esValido))
    }
}

