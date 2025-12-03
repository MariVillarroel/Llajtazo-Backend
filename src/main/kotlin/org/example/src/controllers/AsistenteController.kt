package org.example.src.controllers

import org.example.src.dto.*
import org.example.src.services.AsistenteService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.NoSuchElementException

@RestController
@RequestMapping("/asistentes")
class AsistenteController(
    private val asistenteService: AsistenteService
) {

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<Any> {
        return try {
            val asistente = asistenteService.login(request.correo, request.password)
            if (asistente != null) {
                ResponseEntity.ok(asistente)
            } else {
                ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(mapOf("error" to "Credenciales inválidas"))
            }
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    @PostMapping
    fun crearAsistente(@RequestBody request: UserRequest): ResponseEntity<Any> {
        return try {
            val response = asistenteService.crearAsistente(request)
            ResponseEntity.status(HttpStatus.CREATED).body(response)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(
                mapOf(
                    "error" to e.message,
                    "type" to "VALIDATION_ERROR"
                )
            )
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Error interno del servidor"))
        }
    }

    @GetMapping
    fun listarAsistentes(): ResponseEntity<List<UserResponse>> {
        val lista = asistenteService.listarAsistentes()
        return ResponseEntity.ok(lista)
    }

    @GetMapping("/{id}")
    fun obtenerAsistente(@PathVariable id: Int): ResponseEntity<Any> {
        return try {
            val asistente = asistenteService.obtenerAsistentePorId(id)
            if (asistente != null) {
                ResponseEntity.ok(asistente)
            } else {
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(mapOf("error" to "Asistente no encontrado"))
            }
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Error al obtener asistente"))
        }
    }

    @PatchMapping("/{id}")
    fun actualizarAsistente(
        @PathVariable id: Int,
        @RequestBody request: UpdateAsistenteRequest
    ): ResponseEntity<UserResponse> {
        val actualizado = asistenteService.actualizarAsistente(id, request)
        return ResponseEntity.ok(actualizado) // ✅ ya es UserResponse
    }

    @DeleteMapping("/{id}")
    fun eliminarAsistente(@PathVariable id: Int): ResponseEntity<Any> {
        return if (asistenteService.eliminarAsistente(id)) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("error" to "Asistente no encontrado"))
        }
    }

    @GetMapping("/buscar")
    fun buscarPorCorreo(@RequestParam correo: String): ResponseEntity<Any> {
        return try {
            val asistente = asistenteService.buscarPorCorreo(correo)
            if (asistente != null) {
                ResponseEntity.ok(asistente)
            } else {
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(mapOf("error" to "Asistente no encontrado"))
            }
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Error en la búsqueda"))
        }
    }

    @GetMapping("/username/{username}")
    fun buscarPorUsername(@PathVariable username: String): ResponseEntity<Any> {
        return try {
            val asistente = asistenteService.obtenerAsistentePorUsername(username)
            if (asistente != null) {
                ResponseEntity.ok(asistente)
            } else {
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(mapOf("error" to "Asistente no encontrado"))
            }
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Error en la búsqueda"))
        }
    }

    @PostMapping("/verificar")
    fun verificarCredenciales(@RequestBody request: LoginRequest): ResponseEntity<Any> {
        return try {
            val esValido = asistenteService.verificarCredenciales(
                request.correo,
                request.password
            )
            ResponseEntity.ok(mapOf("valido" to esValido))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    // 🏷️ ENDPOINTS ESPECÍFICOS DE ASISTENTE
    @PostMapping("/{id}/intereses/{categoriaId}")
    fun agregarInteres(
        @PathVariable id: Int,
        @PathVariable categoriaId: Int
    ): ResponseEntity<Any> {
        return try {
            val response = asistenteService.agregarInteres(id, categoriaId)
            ResponseEntity.ok(response)
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("error" to "Recurso no encontrado"))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    @DeleteMapping("/{id}/intereses/{categoriaId}")
    fun eliminarInteres(
        @PathVariable id: Int,
        @PathVariable categoriaId: Int
    ): ResponseEntity<Any> {
        return try {
            val response = asistenteService.eliminarInteres(id, categoriaId)
            ResponseEntity.ok(response)
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("error" to "Recurso no encontrado"))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    @GetMapping("/intereses/{categoriaId}")
    fun buscarPorInteres(@PathVariable categoriaId: Int): ResponseEntity<Any> {
        return try {
            val asistentes = asistenteService.buscarPorInteres(categoriaId)
            ResponseEntity.ok(asistentes)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Error en la búsqueda"))
        }
    }
}