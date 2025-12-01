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

    // 🆕 ENDPOINT DE LOGIN
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<Any> {
        return try {
            val organizador = organizadorService.login(request.correo, request.password)
            if (organizador != null) {
                ResponseEntity.ok(organizador)
            } else {
                ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(mapOf("error" to "Credenciales inválidas"))
            }
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    // ✅ CREAR ORGANIZADOR (con validaciones mejoradas)
    @PostMapping
    fun crearOrganizador(@RequestBody request: OrganizadorRequest): ResponseEntity<Any> {
        return try {
            val response = organizadorService.crearOrganizador(request)
            ResponseEntity.status(HttpStatus.CREATED).body(response)
        } catch (e: IllegalArgumentException) {
            // Captura errores de validación
            ResponseEntity.badRequest().body(mapOf(
                "error" to e.message,
                "type" to "VALIDATION_ERROR"
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Error interno del servidor"))
        }
    }

    // 📋 LISTAR ORGANIZADORES (sin cambios)
    @GetMapping
    fun listarOrganizadores(): ResponseEntity<List<OrganizadorResponse>> {
        val lista = organizadorService.listarOrganizadores()
        return ResponseEntity.ok(lista)
    }

    // 👤 OBTENER POR ID (mejor manejo de errores)
    @GetMapping("/{id}")
    fun obtenerOrganizadorPorId(@PathVariable id: Int): ResponseEntity<Any> {
        return try {
            val organizador = organizadorService.obtenerOrganizadorPorId(id)
            if (organizador != null) {
                ResponseEntity.ok(organizador)
            } else {
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(mapOf("error" to "Organizador no encontrado"))
            }
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Error al obtener organizador"))
        }
    }

    // ✏️ ACTUALIZAR ORGANIZADOR (con validaciones)
    @PutMapping("/{id}")
    fun actualizarOrganizador(
        @PathVariable id: Int,
        @RequestBody request: UpdateOrganizadorRequest
    ): ResponseEntity<Any> {
        return try {
            val response = organizadorService.actualizarOrganizador(id, request)
            ResponseEntity.ok(response)
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("error" to "Organizador no encontrado"))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf(
                "error" to e.message,
                "type" to "VALIDATION_ERROR"
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Error interno del servidor"))
        }
    }

    // 🗑️ ELIMINAR ORGANIZADOR (sin cambios)
    @DeleteMapping("/{id}")
    fun eliminarOrganizador(@PathVariable id: Int): ResponseEntity<Any> {
        return if (organizadorService.eliminarOrganizador(id)) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("error" to "Organizador no encontrado"))
        }
    }

    // 🔍 BUSCAR POR CORREO (nuevo endpoint)
    @GetMapping("/buscar")
    fun buscarPorCorreo(@RequestParam correo: String): ResponseEntity<Any> {
        return try {
            val organizador = organizadorService.buscarPorCorreo(correo)
            if (organizador != null) {
                ResponseEntity.ok(organizador)
            } else {
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(mapOf("error" to "Organizador no encontrado"))
            }
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Error en la búsqueda"))
        }
    }

    // ✅ VERIFICAR CREDENCIALES (endpoint utilitario)
    @PostMapping("/verificar")
    fun verificarCredenciales(@RequestBody request: LoginRequest): ResponseEntity<Any> {
        return try {
            val esValido = organizadorService.verificarCredenciales(
                request.correo,
                request.password
            )
            ResponseEntity.ok(mapOf("valido" to esValido))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }
}