package org.example.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import org.example.src.controllers.OrganizadorController
import org.example.src.dto.LoginRequest
import org.example.src.dto.OrganizadorRequest
import org.example.src.dto.OrganizadorResponse
import org.example.src.services.OrganizadorService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(controllers = [OrganizadorController::class])
class OrganizadorControllerExtraTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper
) {

    @MockBean
    private lateinit var organizadorService: OrganizadorService

    @Test
    fun `login exitoso devuelve 200`() {
        val request = LoginRequest(correo = "org1@example.com", password = "Passw0rd!")
        val response = OrganizadorResponse(
            id = 1,
            username = "org1",
            correo = "org1@example.com",
            profilePic = null,
            role = "ORGANIZER",
            totalSeguidores = 0,
            totalEventos = 0,
            seguidoresIds = emptyList()
        )

        whenever(organizadorService.login(any(), any())).thenReturn(response)

        mockMvc.perform(
            post("/organizadores/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.username").value("org1"))
    }

    @Test
    fun `login fallido devuelve 401`() {
        val request = LoginRequest(correo = "org1@example.com", password = "wrongpass")

        whenever(organizadorService.login(any(), any())).thenReturn(null)

        mockMvc.perform(
            post("/organizadores/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `buscar por correo devuelve 200 si existe`() {
        val response = OrganizadorResponse(
            id = 2,
            username = "org2",
            correo = "org2@example.com",
            profilePic = null,
            role = "ORGANIZER",
            totalSeguidores = 0,
            totalEventos = 0,
            seguidoresIds = emptyList()
        )

        whenever(organizadorService.buscarPorCorreo("org2@example.com")).thenReturn(response)

        mockMvc.perform(get("/organizadores/buscar").param("correo", "org2@example.com"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(2))
            .andExpect(jsonPath("$.correo").value("org2@example.com"))
    }

    @Test
    fun `buscar por correo devuelve 404 si no existe`() {
        whenever(organizadorService.buscarPorCorreo("noexist@example.com")).thenReturn(null)

        mockMvc.perform(get("/organizadores/buscar").param("correo", "noexist@example.com"))
            .andExpect(status().isNotFound)
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
    }
}