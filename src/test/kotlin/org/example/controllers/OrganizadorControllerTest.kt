package org.example.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import org.example.src.controllers.OrganizadorController
import org.example.src.dto.LoginRequest
import org.example.src.dto.OrganizadorResponse
import org.example.src.dto.SuscripcionResponseDTO
import org.example.src.dto.UltimoPagoDTO
import org.example.src.models.*
import org.example.src.services.OrganizadorService
import org.example.src.services.SuscripcionService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDateTime

@WebMvcTest(controllers = [OrganizadorController::class])
class OrganizadorControllerExtraTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper
) {

    @MockBean
    private lateinit var organizadorService: OrganizadorService

    @MockBean
    private lateinit var suscripcionService: SuscripcionService

    @Test
    fun `login exitoso devuelve 200`() {
        val request = LoginRequest(correo = "org1@example.com", password = "Passw0rd!")

        // 1. Mock del organizadorService.login()
        val organizadorResponse = OrganizadorResponse(
            id = 1,
            username = "org1",
            correo = "org1@example.com",
            profilePic = null,
            role = "ORGANIZADOR",
            totalSeguidores = 0,
            totalEventos = 0,
            seguidoresIds = emptyList<Int>(),
            estaSuscrito = true
        )

        `when`(organizadorService.login(any(), any())).thenReturn(organizadorResponse)

        // 2. Crear un mock COMPLETO de Organizador
        val organizadorMock = mock(Organizador::class.java)
        `when`(organizadorMock.id).thenReturn(1)
        `when`(organizadorMock.username).thenReturn("org1")
        `when`(organizadorMock.correo).thenReturn("org1@example.com")
        `when`(organizadorMock.puedeCrearEventos()).thenReturn(true)
        `when`(organizadorMock.eventosCreados).thenReturn(mutableListOf<EventoBasico>())
        `when`(organizadorMock.eventosDisponibles()).thenReturn(Int.MAX_VALUE)

        // 3. Crear un mock COMPLETO de Suscripcion usando las propiedades nuevas
        val suscripcionMock = mock(Suscripcion::class.java)

        // Métodos usando propiedades directas (nueva API)
        `when`(suscripcionMock.id).thenReturn(1)
        `when`(suscripcionMock.tipo).thenReturn(TipoSuscripcion.PREMIUM)  // Propiedad directa
        `when`(suscripcionMock.estado).thenReturn(EstadoSuscripcion.ACTIVA)  // Propiedad directa
        `when`(suscripcionMock.precioMensual).thenReturn(80.0)  // Propiedad directa
        `when`(suscripcionMock.periodoMesesUltimoPago).thenReturn(1)

        val fechaInicio = LocalDateTime.now().minusMonths(1)
        val fechaFin = LocalDateTime.now().plusMonths(1)
        `when`(suscripcionMock.fechaInicio).thenReturn(fechaInicio)  // Propiedad directa
        `when`(suscripcionMock.fechaFin).thenReturn(fechaFin)  // Propiedad directa

        // Propiedades calculadas
        `when`(suscripcionMock.estaActiva).thenReturn(true)  // Propiedad calculada
        `when`(suscripcionMock.tienePremiumActivo).thenReturn(true)  // Propiedad calculada

        `when`(suscripcionMock.organizador).thenReturn(organizadorMock)
        `when`(suscripcionMock.ultimaReferenciaPago).thenReturn("REF-12345")  // Propiedad directa
        `when`(suscripcionMock.estadoUltimoPago).thenReturn(EstadoPago.COMPLETADO)  // Propiedad directa

        // Para compatibilidad con métodos existentes en otros lugares
        `when`(suscripcionMock.obtenerEstadoUltimoPago()).thenReturn(EstadoPago.COMPLETADO)
        `when`(suscripcionMock.obtenerUltimaReferenciaPago()).thenReturn("REF-12345")

        // 4. Mock del suscripcionService
        `when`(suscripcionService.obtenerPorOrganizadorId(1)).thenReturn(suscripcionMock)

        // 5. Ejecutar el test
        mockMvc.perform(
            MockMvcRequestBuilders.post("/organizadores/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.estaSuscrito").value(true))
            .andExpect(jsonPath("$.tienePremium").value(true))
            .andExpect(jsonPath("$.organizador.id").value(1))
            .andExpect(jsonPath("$.organizador.username").value("org1"))
    }

    @Test
    fun `login exitoso sin suscripcion devuelve 200`() {
        val request = LoginRequest(correo = "org1@example.com", password = "Passw0rd!")

        // 1. Mock del organizadorService.login()
        val organizadorResponse = OrganizadorResponse(
            id = 1,
            username = "org1",
            correo = "org1@example.com",
            profilePic = null,
            role = "ORGANIZADOR",
            totalSeguidores = 0,
            totalEventos = 0,
            seguidoresIds = emptyList<Int>(),
            estaSuscrito = false  // Sin suscripción
        )

        `when`(organizadorService.login(any(), any())).thenReturn(organizadorResponse)

        // 2. Mock del suscripcionService - sin suscripción
        `when`(suscripcionService.obtenerPorOrganizadorId(1)).thenReturn(null)

        // 3. Ejecutar el test
        mockMvc.perform(
            MockMvcRequestBuilders.post("/organizadores/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.estaSuscrito").value(false))
            .andExpect(jsonPath("$.tienePremium").value(false))
            .andExpect(jsonPath("$.suscripcion").doesNotExist())
    }

    @Test
    fun `login exitoso con suscripcion gratuita devuelve 200`() {
        val request = LoginRequest(correo = "org1@example.com", password = "Passw0rd!")

        // 1. Mock del organizadorService.login()
        val organizadorResponse = OrganizadorResponse(
            id = 1,
            username = "org1",
            correo = "org1@example.com",
            profilePic = null,
            role = "ORGANIZADOR",
            totalSeguidores = 0,
            totalEventos = 0,
            seguidoresIds = emptyList<Int>(),
            estaSuscrito = true
        )

        `when`(organizadorService.login(any(), any())).thenReturn(organizadorResponse)

        // 2. Crear un mock de Suscripcion GRATUITA
        val organizadorMock = mock(Organizador::class.java)
        `when`(organizadorMock.id).thenReturn(1)
        `when`(organizadorMock.username).thenReturn("org1")
        `when`(organizadorMock.correo).thenReturn("org1@example.com")
        `when`(organizadorMock.puedeCrearEventos()).thenReturn(true)
        `when`(organizadorMock.eventosCreados).thenReturn(mutableListOf<EventoBasico>())

        val suscripcionMock = mock(Suscripcion::class.java)
        `when`(suscripcionMock.id).thenReturn(1)
        `when`(suscripcionMock.tipo).thenReturn(TipoSuscripcion.GRATUITO)  // Tipo GRATUITO
        `when`(suscripcionMock.estado).thenReturn(EstadoSuscripcion.ACTIVA)
        `when`(suscripcionMock.precioMensual).thenReturn(0.0)
        `when`(suscripcionMock.fechaInicio).thenReturn(LocalDateTime.now().minusMonths(1))
        `when`(suscripcionMock.fechaFin).thenReturn(LocalDateTime.now().plusMonths(1))
        `when`(suscripcionMock.estaActiva).thenReturn(true)
        `when`(suscripcionMock.tienePremiumActivo).thenReturn(false)  // No tiene premium
        `when`(suscripcionMock.organizador).thenReturn(organizadorMock)
        `when`(suscripcionMock.ultimaReferenciaPago).thenReturn("")
        `when`(suscripcionMock.estadoUltimoPago).thenReturn(EstadoPago.PENDIENTE)

        `when`(suscripcionService.obtenerPorOrganizadorId(1)).thenReturn(suscripcionMock)

        // 3. Ejecutar el test
        mockMvc.perform(
            MockMvcRequestBuilders.post("/organizadores/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.estaSuscrito").value(true))
            .andExpect(jsonPath("$.tienePremium").value(false))  // Debe ser false
            .andExpect(jsonPath("$.suscripcion").exists())
    }

    @Test
    fun `login exitoso con suscripcion expirada devuelve 200`() {
        val request = LoginRequest(correo = "org1@example.com", password = "Passw0rd!")

        // 1. Mock del organizadorService.login()
        val organizadorResponse = OrganizadorResponse(
            id = 1,
            username = "org1",
            correo = "org1@example.com",
            profilePic = null,
            role = "ORGANIZADOR",
            totalSeguidores = 0,
            totalEventos = 0,
            seguidoresIds = emptyList<Int>(),
            estaSuscrito = true
        )

        `when`(organizadorService.login(any(), any())).thenReturn(organizadorResponse)

        // 2. Crear un mock COMPLETO de Suscripcion con TODAS las propiedades necesarias
        val fechaInicio = LocalDateTime.now().minusMonths(2)
        val fechaFin = LocalDateTime.now().minusDays(1)

        val suscripcionMock = mock(Suscripcion::class.java)

        // Propiedades básicas (MOCKEA TODAS)
        `when`(suscripcionMock.id).thenReturn(1)
        `when`(suscripcionMock.tipo).thenReturn(TipoSuscripcion.PREMIUM)
        `when`(suscripcionMock.estado).thenReturn(EstadoSuscripcion.INACTIVA)
        `when`(suscripcionMock.precioMensual).thenReturn(80.0)
        `when`(suscripcionMock.periodoMesesUltimoPago).thenReturn(12)
        `when`(suscripcionMock.fechaInicio).thenReturn(fechaInicio)
        `when`(suscripcionMock.fechaFin).thenReturn(fechaFin)

        // Propiedades calculadas
        `when`(suscripcionMock.estaActiva).thenReturn(false)
        `when`(suscripcionMock.tienePremiumActivo).thenReturn(false)
        `when`(suscripcionMock.diasRestantes).thenReturn(0L)

        // ¡PROPIEDADES CRÍTICAS QUE ESTABAN FALTANDO!
        `when`(suscripcionMock.ultimaReferenciaPago).thenReturn("REF-EXPIRADA-123")  // NO NULL
        `when`(suscripcionMock.ultimoMontoPago).thenReturn(960.0)  // 80 * 12
        `when`(suscripcionMock.ultimoMetodoPago).thenReturn(MetodoPago.TARJETA)
        `when`(suscripcionMock.estadoUltimoPago).thenReturn(EstadoPago.COMPLETADO)

        // Mock del organizador dentro de la suscripción
        val organizadorEnSuscripcionMock = mock(Organizador::class.java)
        `when`(organizadorEnSuscripcionMock.id).thenReturn(1)
        `when`(organizadorEnSuscripcionMock.username).thenReturn("org1")
        `when`(organizadorEnSuscripcionMock.correo).thenReturn("org1@example.com")
        `when`(organizadorEnSuscripcionMock.puedeCrearEventos()).thenReturn(false)
        `when`(organizadorEnSuscripcionMock.eventosCreados).thenReturn(mutableListOf<EventoBasico>())

        `when`(suscripcionMock.organizador).thenReturn(organizadorEnSuscripcionMock)

        `when`(suscripcionService.obtenerPorOrganizadorId(1)).thenReturn(suscripcionMock)

        // 3. Ejecutar el test
        mockMvc.perform(
            MockMvcRequestBuilders.post("/organizadores/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.estaSuscrito").value(true))
            .andExpect(jsonPath("$.tienePremium").value(false))
            .andExpect(jsonPath("$.suscripcion").exists())
    }

    @Test
    fun `login fallido devuelve 401`() {
        val request = LoginRequest(correo = "org1@example.com", password = "wrongpass")

        whenever(organizadorService.login(any(), any())).thenReturn(null)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/organizadores/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isUnauthorized)
            .andExpect(jsonPath("$.error").value("Credenciales inválidas"))
    }

    @Test
    fun `buscar por correo devuelve 200 si existe`() {
        val response = OrganizadorResponse(
            id = 2,
            username = "org2",
            correo = "org2@example.com",
            profilePic = null,
            role = "ORGANIZADOR",  // Corregido de "ORGANIZER"
            totalSeguidores = 0,
            totalEventos = 0,
            seguidoresIds = emptyList(),
            estaSuscrito = true
        )

        whenever(organizadorService.buscarPorCorreo("org2@example.com")).thenReturn(response)

        mockMvc.perform(get("/organizadores/buscar").param("correo", "org2@example.com"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(2))
            .andExpect(jsonPath("$.correo").value("org2@example.com"))
            .andExpect(jsonPath("$.estaSuscrito").value(true))
    }

    @Test
    fun `buscar por correo devuelve 404 si no existe`() {
        whenever(organizadorService.buscarPorCorreo("noexist@example.com")).thenReturn(null)

        mockMvc.perform(get("/organizadores/buscar").param("correo", "noexist@example.com"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.error").value("Organizador no encontrado"))
    }

    @Test
    fun `eliminar organizador devuelve 204 si existe`() {
        whenever(organizadorService.eliminarOrganizador(1)).thenReturn(true)

        mockMvc.perform(delete("/organizadores/1"))
            .andExpect(status().isNoContent)
    }

    @Test
    fun `eliminar organizador devuelve 404 si no existe`() {
        whenever(organizadorService.eliminarOrganizador(99)).thenReturn(false)

        mockMvc.perform(delete("/organizadores/99"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.error").value("Organizador no encontrado"))
    }

    @Test
    fun `obtener organizador por id devuelve 200 si existe`() {
        val response = OrganizadorResponse(
            id = 1,
            username = "org1",
            correo = "org1@example.com",
            profilePic = null,
            role = "ORGANIZADOR",
            totalSeguidores = 10,
            totalEventos = 5,
            seguidoresIds = listOf(2, 3, 4),
            estaSuscrito = true
        )

        whenever(organizadorService.obtenerOrganizadorPorId(1)).thenReturn(response)

        mockMvc.perform(get("/organizadores/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.organizador.id").value(1))  // Cambiado: $.organizador.id
            .andExpect(jsonPath("$.organizador.username").value("org1"))
            .andExpect(jsonPath("$.organizador.totalSeguidores").value(10))
            .andExpect(jsonPath("$.organizador.totalEventos").value(5))
            .andExpect(jsonPath("$.organizador.estaSuscrito").value(true))
            .andExpect(jsonPath("$.suscripcion").doesNotExist())
            .andExpect(jsonPath("$.estadoSuscripcion.estaSuscrito").value(true))
            .andExpect(jsonPath("$.estadoSuscripcion.tieneSuscripcionActiva").value(false))
    }

    @Test
    fun `obtener organizador por id devuelve 404 si no existe`() {
        whenever(organizadorService.obtenerOrganizadorPorId(99)).thenReturn(null)

        mockMvc.perform(get("/organizadores/99"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.error").value("Organizador no encontrado"))
    }
}