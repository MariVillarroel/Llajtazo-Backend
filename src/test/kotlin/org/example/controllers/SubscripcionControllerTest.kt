package org.example.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import org.example.src.controllers.SuscripcionController
import org.example.src.dto.*
import org.example.src.models.*
import org.example.src.services.SuscripcionService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDateTime

@WebMvcTest(controllers = [SuscripcionController::class])
class SuscripcionControllerTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper
) {

    @MockBean
    private lateinit var suscripcionService: SuscripcionService

    @Test
    fun `procesar pago suscripcion exitoso devuelve 201`() {
        val request = CrearPagoSuscripcionDTO(
            organizadorId = 1,
            metodo = MetodoPago.TARJETA,
            periodoMeses = 12,
            tipoOperacion = TipoOperacionSuscripcion.NUEVA
        )

        val fechaInicio = LocalDateTime.now()
        val fechaFin = fechaInicio.plusMonths(12)

        val response = SuscripcionResponseDTO(
            id = 1,
            tipo = "PREMIUM",
            estado = "ACTIVA",
            precioMensual = 80.0,
            periodoMeses = 12,
            fechaInicio = fechaInicio,
            fechaFin = fechaFin,
            diasRestantes = 365,
            estaActiva = true,
            tienePremium = true,
            organizadorId = 1,
            organizadorUsername = "org1",
            organizadorEmail = "org1@example.com",
            ultimoPago = UltimoPagoDTO(
                monto = 960.0, // 80.0 * 12
                metodo = "TARJETA_CREDITO", // Asegúrate que coincida con el enum
                referencia = "SUB-123456789",
                estado = "COMPLETADO",
                fecha = fechaInicio,
                periodoMeses = 12
            ),
            puedeCrearEventos = true,
            eventosCreados = 0,
            eventosDisponibles = Int.MAX_VALUE,
            creadoEn = fechaInicio
        )

        whenever(suscripcionService.procesarPago(any())).thenReturn(response)

        // Asegúrate de usar la ruta correcta con /api/suscripciones
        mockMvc.perform(
            post("/api/suscripciones/procesar-pago") // Ruta corregida
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated) // Cambiado de isOk a isCreated (201)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.tipo").value("PREMIUM"))
            .andExpect(jsonPath("$.precioMensual").value(80.0))
            .andExpect(jsonPath("$.tienePremium").value(true))
            .andExpect(jsonPath("$.ultimoPago.monto").value(960.0))
    }

    @Test
    fun `obtener suscripcion por organizador id devuelve 200 si existe`() {
        val fechaInicio = LocalDateTime.now().minusMonths(1)
        val fechaFin = fechaInicio.plusMonths(11)

        // Mock de Organizador
        val organizadorMock = mock(Organizador::class.java)
        `when`(organizadorMock.id).thenReturn(1)
        `when`(organizadorMock.username).thenReturn("org1")
        `when`(organizadorMock.correo).thenReturn("org1@example.com")
        `when`(organizadorMock.puedeCrearEventos()).thenReturn(true)
        `when`(organizadorMock.eventosCreados).thenReturn(mutableListOf<EventoEntity>())
        `when`(organizadorMock.eventosDisponibles()).thenReturn(Int.MAX_VALUE)

        // Crear suscripción con id (necesario para el DTO)
        val suscripcion = Suscripcion(
            id = 1, // IMPORTANTE: agregar el id aquí
            tipo = TipoSuscripcion.PREMIUM,
            estado = EstadoSuscripcion.ACTIVA,
            fechaInicio = fechaInicio,
            fechaFin = fechaFin,
            precioMensual = 80.0,
            organizador = organizadorMock,
            ultimoMontoPago = 80.0,
            ultimoMetodoPago = MetodoPago.TARJETA,
            ultimaReferenciaPago = "SUB-12345",
            estadoUltimoPago = EstadoPago.COMPLETADO,
            periodoMesesUltimoPago = 1
        )

        whenever(suscripcionService.obtenerPorOrganizadorId(1)).thenReturn(suscripcion)

        mockMvc.perform(get("/api/suscripciones/organizador/1")) // Ruta corregida
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1)) // Ahora sí debería funcionar
            .andExpect(jsonPath("$.tipo").value("PREMIUM"))
            .andExpect(jsonPath("$.precioMensual").value(80.0))
            .andExpect(jsonPath("$.organizadorId").value(1))
            .andExpect(jsonPath("$.organizadorUsername").value("org1"))
    }

    @Test
    fun `obtener suscripcion por organizador id devuelve 404 si no existe`() {
        whenever(suscripcionService.obtenerPorOrganizadorId(99)).thenReturn(null)

        mockMvc.perform(get("/suscripciones/organizador/99"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `cancelar suscripcion exitoso devuelve 200`() {
        val fechaInicio = LocalDateTime.now().minusMonths(1)
        val fechaFin = fechaInicio.plusMonths(11)

        val response = SuscripcionResponseDTO(
            id = 1,
            tipo = "PREMIUM",
            estado = "CANCELADA",
            precioMensual = 80.0,
            periodoMeses = 12,
            fechaInicio = fechaInicio,
            fechaFin = fechaFin,
            diasRestantes = 0,
            estaActiva = false,
            tienePremium = false,
            organizadorId = 1,
            organizadorUsername = "org1",
            organizadorEmail = "org1@example.com",
            ultimoPago = UltimoPagoDTO(
                monto = 960.0,
                metodo = "TARJETA_CREDITO",
                referencia = "SUB-12345",
                estado = "COMPLETADO",
                fecha = fechaInicio,
                periodoMeses = 12
            ),
            puedeCrearEventos = false,
            eventosCreados = 5,
            eventosDisponibles = 0,
            creadoEn = fechaInicio
        )

        whenever(suscripcionService.cancelarSuscripcion(1)).thenReturn(response)

        // Usa la ruta correcta: /organizador/{organizadorId}/cancelar
        mockMvc.perform(post("/api/suscripciones/organizador/1/cancelar"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.estado").value("CANCELADA"))
            .andExpect(jsonPath("$.estaActiva").value(false))
            .andExpect(jsonPath("$.tienePremium").value(false))
            .andExpect(jsonPath("$.organizadorId").value(1))
    }

    @Test
    fun `verificar si organizador puede crear eventos devuelve 200`() {
        whenever(suscripcionService.puedeCrearEventos(1)).thenReturn(true)

        mockMvc.perform(get("/api/suscripciones/organizador/1/puede-crear-eventos"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.puedeCrearEventos").value(true)) // Corrección aquí
    }

    @Test
    fun `obtener dias restantes suscripcion devuelve 200`() {
        whenever(suscripcionService.obtenerDiasRestantes(1)).thenReturn(30)

        mockMvc.perform(get("/api/suscripciones/organizador/1/dias-restantes"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.diasRestantes").value(30)) // Corrección aquí
            .andExpect(jsonPath("$.organizadorId").value(1))
    }

    @Test
    fun `obtener tipo suscripcion devuelve 200`() {
        whenever(suscripcionService.obtenerPlanActual(1)).thenReturn("PREMIUM")

        // La ruta correcta es /tipo, no /plan-actual
        mockMvc.perform(get("/api/suscripciones/organizador/1/tipo"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.tipoSuscripcion").value("PREMIUM")) // Corrección aquí
            .andExpect(jsonPath("$.esPremium").value(true))
            .andExpect(jsonPath("$.organizadorId").value(1))
    }

    @Test
    fun `listar suscripciones activas devuelve 200`() {
        val fechaInicio = LocalDateTime.now().minusMonths(1)
        val fechaFin = fechaInicio.plusMonths(11)

        val suscripcion1 = SuscripcionResponseDTO(
            id = 1,
            tipo = "PREMIUM",
            estado = "ACTIVA",
            precioMensual = 80.0,
            periodoMeses = 12,
            fechaInicio = fechaInicio,
            fechaFin = fechaFin,
            diasRestantes = 335,
            estaActiva = true,
            tienePremium = true,
            organizadorId = 1,
            organizadorUsername = "org1",
            organizadorEmail = "org1@example.com",
            ultimoPago = UltimoPagoDTO(
                monto = 960.0,
                metodo = "TARJETA_CREDITO",
                referencia = "SUB-12345",
                estado = "COMPLETADO",
                fecha = fechaInicio,
                periodoMeses = 12
            ),
            puedeCrearEventos = true,
            eventosCreados = 5,
            eventosDisponibles = Int.MAX_VALUE,
            creadoEn = fechaInicio
        )

        whenever(suscripcionService.listarActivas()).thenReturn(listOf(suscripcion1))

        mockMvc.perform(get("/api/suscripciones/activas"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].tipo").value("PREMIUM"))
            .andExpect(jsonPath("$[0].organizadorUsername").value("org1"))
    }

    @Test
    fun `obtener suscripciones por vencer devuelve 200`() {
        val fechaInicio = LocalDateTime.now().minusMonths(12)
        val fechaFin = fechaInicio.plusDays(5) // Expira en 5 días

        val response = SuscripcionResponseDTO(
            id = 1,
            tipo = "PREMIUM",
            estado = "ACTIVA",
            precioMensual = 80.0,
            periodoMeses = 12,
            fechaInicio = fechaInicio,
            fechaFin = fechaFin,
            diasRestantes = 5,
            estaActiva = true,
            tienePremium = true,
            organizadorId = 1,
            organizadorUsername = "org1",
            organizadorEmail = "org1@example.com",
            ultimoPago = UltimoPagoDTO(
                monto = 960.0,
                metodo = "TARJETA_CREDITO",
                referencia = "SUB-12345",
                estado = "COMPLETADO",
                fecha = fechaInicio,
                periodoMeses = 12
            ),
            puedeCrearEventos = true,
            eventosCreados = 10,
            eventosDisponibles = Int.MAX_VALUE,
            creadoEn = fechaInicio
        )

        whenever(suscripcionService.obtenerSuscripcionesPorVencer(7)).thenReturn(listOf(response))

        mockMvc.perform(get("/api/suscripciones/por-vencer").param("dias", "7"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].diasRestantes").value(5))
            .andExpect(jsonPath("$[0].organizadorEmail").value("org1@example.com"))
    }

    @Test
    fun `procesar pago con periodo invalido devuelve 400`() {
        val request = CrearPagoSuscripcionDTO(
            organizadorId = 1,
            metodo = MetodoPago.TARJETA,
            periodoMeses = 50, // Más del máximo (36)
            tipoOperacion = TipoOperacionSuscripcion.NUEVA
        )

        whenever(suscripcionService.procesarPago(any()))
            .thenThrow(IllegalArgumentException("El período debe estar entre 1 y 36 meses"))

        mockMvc.perform(
            post("/api/suscripciones/procesar-pago")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `procesar pago para organizador inexistente devuelve 404`() {
        val request = CrearPagoSuscripcionDTO(
            organizadorId = 99,
            metodo = MetodoPago.TARJETA,
            periodoMeses = 12,
            tipoOperacion = TipoOperacionSuscripcion.NUEVA
        )

        whenever(suscripcionService.procesarPago(any()))
            .thenThrow(jakarta.persistence.EntityNotFoundException("Organizador 99 no encontrado"))

        mockMvc.perform(
            post("/suscripciones/procesar-pago")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isNotFound)
    }
}