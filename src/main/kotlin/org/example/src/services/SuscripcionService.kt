package org.example.src.services

import org.example.src.dto.*
import org.example.src.models.*
import org.example.src.repositories.SuscripcionRepository
import org.example.src.repositories.OrganizadorRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import jakarta.persistence.EntityNotFoundException
import java.time.LocalDateTime

@Service
@Transactional
class SuscripcionService(
    private val suscripcionRepository: SuscripcionRepository,
    private val organizadorRepository: OrganizadorRepository
) {

    companion object {
        const val PRECIO_MENSUAL = 80.0
        const val PERIODO_MINIMO = 1
        const val PERIODO_MAXIMO = 36
    }

    // 🔴 MÉTODO PRINCIPAL: Procesar pago y manejar suscripción
    fun procesarPago(dto: CrearPagoSuscripcionDTO): SuscripcionResponseDTO {
        // 1. Validaciones básicas
        validarDatosPago(dto)

        // 2. Obtener organizador
        val organizador = obtenerOrganizador(dto.organizadorId)

        // 3. Crear pago temporal (siempre exitoso en MVP)
        val pago = crearPagoDesdeDTO(dto)

        // 4. Determinar tipo de operación
        val operacion = determinarTipoOperacion(organizador.suscripcion, dto.tipoOperacion)
        pago.tipoOperacion = operacion

        // 5. Procesar pago (siempre exitoso en MVP)
        val exitoPago = procesarPagoInterno(pago)

        if (!exitoPago) {
            throw IllegalStateException("Error procesando el pago")
        }

        // 6. Manejar suscripción según operación
        val suscripcion = when (operacion) {
            TipoOperacionSuscripcion.NUEVA -> crearNuevaSuscripcion(organizador, pago)
            TipoOperacionSuscripcion.RENOVACION -> renovarSuscripcion(organizador, pago)
            TipoOperacionSuscripcion.UPGRADE -> actualizarSuscripcion(organizador, pago)
        }

        // 7. Actualizar estado del organizador
        actualizarEstadoOrganizador(organizador, suscripcion)

        // 8. Guardar y retornar
        val savedSuscripcion = suscripcionRepository.save(suscripcion)
        organizadorRepository.save(organizador)

        return SuscripcionResponseDTO.fromSuscripcion(savedSuscripcion)
    }

    // ✅ Obtener suscripción por ID
    @Transactional(readOnly = true)
    fun obtenerPorId(id: Int): Suscripcion {
        return suscripcionRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Suscripción $id no encontrada") }
    }

    // ✅ Obtener suscripción por organizador
    @Transactional(readOnly = true)
    fun obtenerPorOrganizadorId(organizadorId: Int): Suscripcion? {
        return suscripcionRepository.findByOrganizadorId(organizadorId)
    }

    // ✅ Verificar si organizador tiene suscripción activa
    @Transactional(readOnly = true)
    fun tieneSuscripcionActiva(organizadorId: Int): Boolean {
        val suscripcion = obtenerPorOrganizadorId(organizadorId)
        return suscripcion?.estaActiva ?: false  // Usa la propiedad calculada
    }

    // ✅ Cancelar suscripción (solo cambia estado, no elimina)
    fun cancelarSuscripcion(organizadorId: Int): SuscripcionResponseDTO {
        val suscripcion = obtenerPorOrganizadorId(organizadorId)
            ?: throw EntityNotFoundException("No existe suscripción para el organizador $organizadorId")

        suscripcion.cancelar()  // Usa el nuevo método fluido

        // Actualizar organizador
        val organizador = suscripcion.organizador
        organizador?.suscribed = false
        organizador?.let { organizadorRepository.save(it) }

        val saved = suscripcionRepository.save(suscripcion)
        return SuscripcionResponseDTO.fromSuscripcion(saved)
    }

    // ✅ Suspender suscripción
    fun suspenderSuscripcion(organizadorId: Int): SuscripcionResponseDTO {
        val suscripcion = obtenerPorOrganizadorId(organizadorId)
            ?: throw EntityNotFoundException("No existe suscripción para el organizador $organizadorId")

        suscripcion.suspender()

        val saved = suscripcionRepository.save(suscripcion)
        return SuscripcionResponseDTO.fromSuscripcion(saved)
    }

    // ✅ Reactivar suscripción
    fun reactivarSuscripcion(organizadorId: Int): SuscripcionResponseDTO {
        val suscripcion = obtenerPorOrganizadorId(organizadorId)
            ?: throw EntityNotFoundException("No existe suscripción para el organizador $organizadorId")

        suscripcion.reactivar()

        val saved = suscripcionRepository.save(suscripcion)
        return SuscripcionResponseDTO.fromSuscripcion(saved)
    }

    // ✅ Renovar suscripción directamente
    fun renovarSuscripcion(organizadorId: Int, periodoMeses: Int, metodoPago: MetodoPago): SuscripcionResponseDTO {
        val suscripcion = obtenerPorOrganizadorId(organizadorId)
            ?: throw EntityNotFoundException("No existe suscripción para el organizador $organizadorId")

        suscripcion.renovar(periodoMeses, metodoPago)

        // Actualizar organizador
        val organizador = suscripcion.organizador
        organizador?.suscribed = suscripcion.tienePremiumActivo
        organizador?.let { organizadorRepository.save(it) }

        val saved = suscripcionRepository.save(suscripcion)
        return SuscripcionResponseDTO.fromSuscripcion(saved)
    }

    // ✅ Obtener suscripciones que expiran pronto (para notificaciones)
    @Transactional(readOnly = true)
    fun obtenerSuscripcionesPorVencer(dias: Int = 7): List<SuscripcionResponseDTO> {
        val hoy = LocalDateTime.now()
        val limite = hoy.plusDays(dias.toLong())

        return suscripcionRepository.findAllPorVencer(hoy, limite)
            .map { SuscripcionResponseDTO.fromSuscripcion(it) }
    }

    // ✅ Tarea programada: Actualizar estado de suscripciones expiradas
    @Transactional
    fun actualizarEstadosExpirados() {
        val ahora = LocalDateTime.now()

        // Buscar TODAS las suscripciones y filtrar las ACTIVAS
        val todasSuscripciones = suscripcionRepository.findAll()
        val suscripcionesActivas = todasSuscripciones.filter {
            it.estado == EstadoSuscripcion.ACTIVA  // Acceso directo a la propiedad
        }

        suscripcionesActivas.forEach { suscripcion ->
            if (suscripcion.fechaFin.isBefore(ahora)) {  // Acceso directo a la propiedad
                // Pasa a INACTIVA usando el método fluido
                suscripcion.estado = EstadoSuscripcion.INACTIVA
                suscripcionRepository.save(suscripcion)
            }
        }
    }

    // ========== MÉTODOS PRIVADOS DE APOYO ==========

    private fun validarDatosPago(dto: CrearPagoSuscripcionDTO) {
        if (dto.periodoMeses !in PERIODO_MINIMO..PERIODO_MAXIMO) {
            throw IllegalArgumentException("El período debe estar entre $PERIODO_MINIMO y $PERIODO_MAXIMO meses")
        }

        if (dto.metodo == null) {
            throw IllegalArgumentException("El método de pago es requerido")
        }
    }

    private fun obtenerOrganizador(organizadorId: Int): Organizador {
        return organizadorRepository.findById(organizadorId)
            .orElseThrow { EntityNotFoundException("Organizador $organizadorId no encontrado") }
    }

    private fun crearPagoDesdeDTO(dto: CrearPagoSuscripcionDTO): PagoSuscripcion {
        return PagoSuscripcion(
            metodo = dto.metodo,
            periodoMeses = dto.periodoMeses,
            tipoOperacion = dto.tipoOperacion  // Ya es del tipo correcto, no necesita conversión
        )
    }

    private fun determinarTipoOperacion(
        suscripcionExistente: Suscripcion?,
        tipoOperacion: TipoOperacionSuscripcion  // Cambiado de DTO a modelo
    ): TipoOperacionSuscripcion {
        // Si ya viene especificado desde el frontend, úsalo directamente
        return when (tipoOperacion) {
            TipoOperacionSuscripcion.RENOVACION -> TipoOperacionSuscripcion.RENOVACION
            TipoOperacionSuscripcion.UPGRADE -> TipoOperacionSuscripcion.UPGRADE
            TipoOperacionSuscripcion.NUEVA -> {
                // Determinar automáticamente solo si es NUEVA
                when {
                    suscripcionExistente == null -> TipoOperacionSuscripcion.NUEVA
                    suscripcionExistente.tipo == TipoSuscripcion.GRATUITO ->
                        TipoOperacionSuscripcion.UPGRADE
                    else -> TipoOperacionSuscripcion.RENOVACION
                }
            }
        }
    }

    private fun procesarPagoInterno(pago: PagoSuscripcion): Boolean {
        // En MVP, siempre es exitoso
        println("💳 Procesando pago ${pago.referencia} por $${pago.monto}")
        pago.estado = EstadoPago.COMPLETADO
        return true
    }

    private fun crearNuevaSuscripcion(organizador: Organizador, pago: PagoSuscripcion): Suscripcion {
        println("🆕 Creando NUEVA suscripción para ${organizador.username}")
        return Suscripcion.crearDesdePago(organizador, pago)
    }

    private fun renovarSuscripcion(organizador: Organizador, pago: PagoSuscripcion): Suscripcion {
        val suscripcion = organizador.suscripcion
            ?: throw IllegalStateException("No existe suscripción para renovar")

        println("🔄 RENOVANDO suscripción de ${organizador.username}")

        // Usar el método renovar de Suscripcion
        suscripcion.renovar(pago.periodoMeses, pago.metodo)
        suscripcion.actualizarDesdePago(pago)

        return suscripcion
    }

    private fun actualizarSuscripcion(organizador: Organizador, pago: PagoSuscripcion): Suscripcion {
        val suscripcion = organizador.suscripcion
            ?: throw IllegalStateException("No existe suscripción para actualizar")

        println("⚡ UPGRADE de suscripción de ${organizador.username}")

        // Usar upgrade en lugar de actualizar manualmente
        suscripcion.tipo = TipoSuscripcion.PREMIUM  // Acceso directo
        suscripcion.fechaInicio = pago.fecha  // Acceso directo
        suscripcion.fechaFin = pago.fecha.plusMonths(pago.periodoMeses.toLong())  // Acceso directo
        suscripcion.actualizarDesdePago(pago)

        // Cambiar a ACTIVA si no lo estaba
        if (suscripcion.estado != EstadoSuscripcion.ACTIVA) {  // Acceso directo
            suscripcion.estado = EstadoSuscripcion.ACTIVA  // Acceso directo
        }

        return suscripcion
    }

    private fun actualizarEstadoOrganizador(organizador: Organizador, suscripcion: Suscripcion) {
        // Solo actualizar suscribed si la suscripción es PREMIUM y ACTIVA
        organizador.suscribed = (suscripcion.tipo == TipoSuscripcion.PREMIUM &&  // Acceso directo
                suscripcion.estado == EstadoSuscripcion.ACTIVA)  // Acceso directo

        println("👤 Organizador ${organizador.username}: suscribed = ${organizador.suscribed}")
    }

    // ✅ Método para verificar si puede crear eventos (siempre true según tus reglas)
    @Transactional(readOnly = true)
    fun puedeCrearEventos(organizadorId: Int): Boolean {
        val organizador = organizadorRepository.findById(organizadorId).orElse(null)
        return organizador != null  // Cualquier organizador registrado puede crear eventos
    }

    // ✅ Método para obtener días restantes de suscripción
    @Transactional(readOnly = true)
    fun obtenerDiasRestantes(organizadorId: Int): Long? {
        val suscripcion = obtenerPorOrganizadorId(organizadorId)
        return suscripcion?.diasRestantes  // Usa la propiedad calculada
    }

    // ✅ Método para obtener plan actual
    @Transactional(readOnly = true)
    fun obtenerPlanActual(organizadorId: Int): String? {
        val suscripcion = obtenerPorOrganizadorId(organizadorId)
        return suscripcion?.tipo?.name ?: "GRATUITO"  // Acceso directo
    }

    @Transactional(readOnly = true)
    fun listarActivas(): List<SuscripcionResponseDTO> {
        val suscripciones = suscripcionRepository.findAllActivas()
        return suscripciones.map { SuscripcionResponseDTO.fromSuscripcion(it) }
    }

    // ✅ Método para obtener suscripción como DTO
    @Transactional(readOnly = true)
    fun obtenerSuscripcionDTO(organizadorId: Int): SuscripcionResponseDTO? {
        val suscripcion = obtenerPorOrganizadorId(organizadorId)
        return suscripcion?.let { SuscripcionResponseDTO.fromSuscripcion(it) }
    }
}