package org.example.src.controllers

import org.example.src.dto.CrearPagoSuscripcionDTO
import org.example.src.dto.SuscripcionResponseDTO
import org.example.src.models.TipoOperacionSuscripcion
import org.example.src.services.SuscripcionService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/suscripciones")
class SuscripcionController(
    private val suscripcionService: SuscripcionService
) {
    @PostMapping("/procesar-pago")
    fun procesarPagoYSuscripcion(
        @Valid @RequestBody dto: CrearPagoSuscripcionDTO
    ): ResponseEntity<SuscripcionResponseDTO> {
        val suscripcion = suscripcionService.procesarPago(dto)
        return ResponseEntity.status(HttpStatus.CREATED).body(suscripcion)
    }

    // ✅ Obtener suscripción por ID
    @GetMapping("/{id}")
    fun obtenerSuscripcion(@PathVariable id: Int): ResponseEntity<SuscripcionResponseDTO> {
        val suscripcion = suscripcionService.obtenerPorId(id)
        return ResponseEntity.ok(SuscripcionResponseDTO.fromSuscripcion(suscripcion))
    }

    // ✅ Obtener suscripción por organizador (para frontend)
    @GetMapping("/organizador/{organizadorId}")
    fun obtenerSuscripcionPorOrganizador(
        @PathVariable organizadorId: Int
    ): ResponseEntity<SuscripcionResponseDTO?> {
        val suscripcion = suscripcionService.obtenerPorOrganizadorId(organizadorId)
        return ResponseEntity.ok(suscripcion?.let { SuscripcionResponseDTO.fromSuscripcion(it) })
    }

    // ✅ Verificar si organizador tiene suscripción activa
    @GetMapping("/organizador/{organizadorId}/activa")
    fun verificarSuscripcionActiva(
        @PathVariable organizadorId: Int
    ): ResponseEntity<Map<String, Boolean>> {
        val tieneActiva = suscripcionService.tieneSuscripcionActiva(organizadorId)
        return ResponseEntity.ok(mapOf("tieneSuscripcionActiva" to tieneActiva))
    }

    // ✅ **VERIFICAR SI ES PREMIUM O GRATUITO** (¡Lo que pediste!)
    @GetMapping("/organizador/{organizadorId}/tipo")
    fun obtenerTipoSuscripcion(
        @PathVariable organizadorId: Int
    ): ResponseEntity<Map<String, Any>> {
        val plan = suscripcionService.obtenerPlanActual(organizadorId)

        return ResponseEntity.ok(mapOf(
            "organizadorId" to organizadorId,
            "tipoSuscripcion" to (plan ?: "GRATUITO"),
            "esPremium" to (plan == "PREMIUM"),
            "esGratuito" to (plan != "PREMIUM")
        ))
    }

    // ✅ Cancelar suscripción
    @PostMapping("/organizador/{organizadorId}/cancelar")
    fun cancelarSuscripcion(
        @PathVariable organizadorId: Int
    ): ResponseEntity<SuscripcionResponseDTO> {
        val suscripcionCancelada = suscripcionService.cancelarSuscripcion(organizadorId)
        return ResponseEntity.ok(suscripcionCancelada)
    }

    // ✅ Renovar suscripción (endpoint específico para renovaciones)
    @PostMapping("/organizador/{organizadorId}/renovar")
    fun renovarSuscripcion(
        @PathVariable organizadorId: Int,
        @Valid @RequestBody dto: CrearPagoSuscripcionDTO
    ): ResponseEntity<SuscripcionResponseDTO> {
        // Forzar tipo de operación como RENOVACION
        val dtoRenovacion = dto.copy(tipoOperacion = TipoOperacionSuscripcion.RENOVACION)
        val suscripcion = suscripcionService.procesarPago(dtoRenovacion)
        return ResponseEntity.ok(suscripcion)
    }

    // ✅ Actualizar suscripción (upgrade de GRATUITO a PREMIUM)
    @PostMapping("/organizador/{organizadorId}/upgrade")
    fun actualizarSuscripcion(
        @PathVariable organizadorId: Int,
        @Valid @RequestBody dto: CrearPagoSuscripcionDTO
    ): ResponseEntity<SuscripcionResponseDTO> {
        // Forzar tipo de operación como UPGRADE
        val dtoUpgrade = dto.copy(tipoOperacion = TipoOperacionSuscripcion.UPGRADE)
        val suscripcion = suscripcionService.procesarPago(dtoUpgrade)
        return ResponseEntity.ok(suscripcion)
    }

    // ✅ Obtener días restantes de suscripción
    @GetMapping("/organizador/{organizadorId}/dias-restantes")
    fun obtenerDiasRestantes(
        @PathVariable organizadorId: Int
    ): ResponseEntity<Map<String, Any>> {
        val dias = suscripcionService.obtenerDiasRestantes(organizadorId)

        return ResponseEntity.ok(mapOf<String, Any>(
            "organizadorId" to organizadorId,
            "diasRestantes" to (dias ?: 0),
            "estado" to when {
                dias == null -> "SIN_SUSCRIPCION"
                dias > 30 -> "MAS_DE_UN_MES"
                dias > 7 -> "MENOS_DE_UN_MES"
                dias > 0 -> "POR_VENCER"
                else -> "VENCIDO"
            },
            "tieneSuscripcion" to (dias != null && dias > 0),
            "mensaje" to when {
                dias == null -> "No tiene suscripción activa"
                dias > 0 -> "La suscripción vence en $dias días"
                else -> "La suscripción ha vencido"
            }
        ))
    }

    // ✅ Listar suscripciones activas (para admin)
    @GetMapping("/activas")
    fun listarSuscripcionesActivas(): ResponseEntity<List<SuscripcionResponseDTO>> {
        val suscripciones = suscripcionService.listarActivas()
        return ResponseEntity.ok(suscripciones)
    }

    // ✅ Obtener suscripciones que expiran pronto (para notificaciones)
    @GetMapping("/por-vencer")
    fun obtenerSuscripcionesPorVencer(
        @RequestParam(defaultValue = "7") dias: Int
    ): ResponseEntity<List<SuscripcionResponseDTO>> {
        val suscripciones = suscripcionService.obtenerSuscripcionesPorVencer(dias)
        return ResponseEntity.ok(suscripciones)
    }

    // ✅ Verificar si organizador puede crear eventos
    @GetMapping("/organizador/{organizadorId}/puede-crear-eventos")
    fun puedeCrearEventos(
        @PathVariable organizadorId: Int
    ): ResponseEntity<Map<String, Boolean>> {
        val puede = suscripcionService.puedeCrearEventos(organizadorId)
        return ResponseEntity.ok(mapOf("puedeCrearEventos" to puede))
    }

    // ✅ Manejo de errores
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequest(ex: IllegalArgumentException): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(mapOf(
                "error" to "Solicitud inválida",
                "mensaje" to (ex.message ?: "").toString()
            ))
    }

    @ExceptionHandler(jakarta.persistence.EntityNotFoundException::class)
    fun handleNotFound(ex: jakarta.persistence.EntityNotFoundException): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(mapOf(
                "error" to "Recurso no encontrado",
                "mensaje" to (ex.message ?: "").toString()  // Cambio aquí
            ))
    }

    @ExceptionHandler(IllegalStateException::class)
    fun handleConflict(ex: IllegalStateException): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(mapOf(
                "error" to "Conflicto",
                "mensaje" to (ex.message ?: "").toString()  // Cambio aquí
            ))
    }
}