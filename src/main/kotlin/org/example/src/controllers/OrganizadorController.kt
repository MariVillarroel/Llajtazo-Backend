package org.example.src.controllers

import org.example.src.dto.*
import org.example.src.services.OrganizadorService
import org.example.src.services.SuscripcionService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/organizadores")
class OrganizadorController(
    private val organizadorService: OrganizadorService,
    private val suscripcionService: SuscripcionService
) {

    // 🆕 ENDPOINT DE LOGIN
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<Any> {
        return try {
            val organizador = organizadorService.login(request.correo, request.password)
            if (organizador != null) {
                // ✅ Incluir información de suscripción en login
                val suscripcion = suscripcionService.obtenerPorOrganizadorId(organizador.id)
                val response = mapOf(
                    "organizador" to organizador,
                    "suscripcion" to suscripcion?.let { SuscripcionResponseDTO.fromSuscripcion(it) },
                    "estaSuscrito" to organizador.estaSuscrito,  // Método actualizado
                    "tienePremium" to (suscripcion?.tienePremiumActivo ?: false)  // Propiedad calculada
                )
                ResponseEntity.ok(response)
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
            ResponseEntity.badRequest().body(mapOf(
                "error" to e.message,
                "type" to "VALIDATION_ERROR"
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Error interno del servidor"))
        }
    }

    // 📋 LISTAR ORGANIZADORES (✅ NUEVO: Filtrar por suscripción)
    @GetMapping
    fun listarOrganizadores(
        @RequestParam(required = false) suscritos: Boolean? = null
    ): ResponseEntity<Any> {
        return try {
            val lista = if (suscritos == true) {
                // ✅ NUEVO: Solo organizadores suscritos
                organizadorService.listarOrganizadoresSuscritos()
            } else if (suscritos == false) {
                // ✅ NUEVO: Solo organizadores NO suscritos
                organizadorService.listarOrganizadores().filter { !it.estaSuscrito }
            } else {
                // Todos los organizadores
                organizadorService.listarOrganizadores()
            }
            ResponseEntity.ok(lista)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Error al listar organizadores"))
        }
    }

    // 👤 OBTENER POR ID (✅ MEJORADO: Incluye info de suscripción completa)
    @GetMapping("/{id}")
    fun obtenerOrganizadorPorId(@PathVariable id: Int): ResponseEntity<Any> {
        return try {
            val organizador = organizadorService.obtenerOrganizadorPorId(id)
            if (organizador != null) {
                // ✅ NUEVO: Obtener información completa de suscripción
                val suscripcion = suscripcionService.obtenerPorOrganizadorId(id)
                val infoCompleta = mapOf(
                    "organizador" to organizador,
                    "suscripcion" to suscripcion?.let { SuscripcionResponseDTO.fromSuscripcion(it) },
                    "estadoSuscripcion" to mapOf(
                        "estaSuscrito" to organizador.estaSuscrito,
                        "tieneSuscripcionActiva" to suscripcionService.tieneSuscripcionActiva(id),
                        "puedeCrearEventos" to suscripcionService.puedeCrearEventos(id),
                        "diasRestantes" to suscripcionService.obtenerDiasRestantes(id),
                        "planActual" to suscripcionService.obtenerPlanActual(id)
                    )
                )
                ResponseEntity.ok(infoCompleta)
            } else {
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(mapOf("error" to "Organizador no encontrado"))
            }
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Error al obtener organizador"))
        }
    }

    // ✅ **NUEVO ENDPOINT: Obtener información COMPLETA de organizador con suscripción**
    @GetMapping("/{id}/info-completa")
    fun obtenerOrganizadorCompleto(@PathVariable id: Int): ResponseEntity<Any> {
        return try {
            val organizadorCompleto = organizadorService.obtenerOrganizadorCompleto(id)
            if (organizadorCompleto != null) {
                ResponseEntity.ok(organizadorCompleto)
            } else {
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(mapOf("error" to "Organizador no encontrado"))
            }
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Error al obtener información completa"))
        }
    }

    // ✅ **NUEVO ENDPOINT: Verificar tipo de suscripción (PREMIUM/GRATUITO)**
    @GetMapping("/{id}/tipo-suscripcion")
    fun obtenerTipoSuscripcion(@PathVariable id: Int): ResponseEntity<Any> {
        return try {
            val tipo = suscripcionService.obtenerPlanActual(id)
            ResponseEntity.ok(mapOf(
                "organizadorId" to id,
                "tipoSuscripcion" to tipo,
                "esPremium" to (tipo == "PREMIUM"),
                "esGratuito" to (tipo == "GRATUITO")
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Error al obtener tipo de suscripción"))
        }
    }

    // ✅ **NUEVO ENDPOINT: Verificar si puede crear eventos**
    @GetMapping("/{id}/puede-crear-eventos")
    fun puedeCrearEventos(@PathVariable id: Int): ResponseEntity<Any> {
        return try {
            val puede = suscripcionService.puedeCrearEventos(id)
            ResponseEntity.ok(mapOf(
                "organizadorId" to id,
                "puedeCrearEventos" to puede,
                "mensaje" to if (puede) "Puede crear eventos" else "No puede crear eventos"
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Error al verificar permisos"))
        }
    }

    // ✅ **NUEVO ENDPOINT: Obtener eventos disponibles**
    @GetMapping("/{id}/eventos-disponibles")
    fun obtenerEventosDisponibles(@PathVariable id: Int): ResponseEntity<Any> {
        return try {
            val disponibles = organizadorService.eventosDisponibles(id)
            ResponseEntity.ok(mapOf(
                "organizadorId" to id,
                "eventosDisponibles" to disponibles,
                "tieneIlimitados" to (disponibles == Int.MAX_VALUE),
                "limite" to if (disponibles == Int.MAX_VALUE) "ILIMITADO" else "3 (gratis)",
                "mensaje" to when {
                    disponibles == Int.MAX_VALUE -> "Tienes eventos ilimitados (suscripción PREMIUM)"
                    disponibles > 0 -> "Te quedan $disponibles eventos gratis"
                    else -> "No tienes eventos disponibles"
                }
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Error al obtener eventos disponibles"))
        }
    }

    // ✏️ ACTUALIZAR ORGANIZADOR (con validaciones) - SIN CAMBIOS
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

    // ✅ **NUEVO ENDPOINT: Suscribir organizador**
    @PostMapping("/{id}/suscribir")
    fun suscribirOrganizador(@PathVariable id: Int): ResponseEntity<Any> {
        return try {
            val response = organizadorService.suscribirOrganizador(id)
            ResponseEntity.ok(mapOf(
                "mensaje" to "Organizador suscrito exitosamente",
                "organizador" to response,
                "estaSuscrito" to true
            ))
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("error" to "Organizador no encontrado"))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Error al suscribir organizador"))
        }
    }

    // ✅ **NUEVO ENDPOINT: Cancelar suscripción de organizador**
    @PostMapping("/{id}/cancelar-suscripcion")
    fun cancelarSuscripcionOrganizador(@PathVariable id: Int): ResponseEntity<Any> {
        return try {
            val response = organizadorService.cancelarSuscripcion(id)
            ResponseEntity.ok(mapOf(
                "mensaje" to "Suscripción cancelada exitosamente",
                "organizador" to response,
                "estaSuscrito" to false
            ))
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("error" to "Organizador no encontrado"))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Error al cancelar suscripción"))
        }
    }
}