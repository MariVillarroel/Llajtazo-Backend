package org.example.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import org.example.src.controllers.AsistenteController
import org.example.src.dto.LoginRequest
import org.example.src.dto.UpdateAsistenteRequest
import org.example.src.dto.UserRequest
import org.example.src.dto.UserResponse
import org.example.src.models.UserRole
import org.example.src.services.AsistenteService
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

@WebMvcTest(controllers = [AsistenteController::class])
class AsistenteControllerTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper
) {

    @MockBean
    private lateinit var asistenteService: AsistenteService

    @Test
    fun `crear asistente OK`() {
        val request = UserRequest(
            username = "user1",
            correo = "user1@example.com",
            password = "Passw0rd!",
            profilePic = "https://example.com/avatar.png",
            categoriasIds = emptyList()
        )

        val response = UserResponse(
            id = 1,
            username = "user1",
            correo = "user1@example.com",
            profilePic = "https://example.com/avatar.png",
            role = UserRole.ASISTENTE,
            totalOrganizadoresSeguidos = 0,
            fechaCreacion = "2025-12-02T21:55:00"
        )

        whenever(asistenteService.crearAsistente(any())).thenReturn(response)

        mockMvc.perform(
            post("/asistentes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.username").value("user1"))
            .andExpect(jsonPath("$.correo").value("user1@example.com"))
            .andExpect(jsonPath("$.role").value("ASISTENTE"))
            .andExpect(jsonPath("$.totalOrganizadoresSeguidos").value(0))
            .andExpect(jsonPath("$.fechaCreacion").value("2025-12-02T21:55:00"))
    }

    @Test
    fun `login exitoso devuelve 200`() {
        val request = LoginRequest(correo = "user1@example.com", password = "Passw0rd!")
        val response = UserResponse(
            id = 1,
            username = "user1",
            correo = "user1@example.com",
            profilePic = null,
            role = UserRole.ASISTENTE,
            totalOrganizadoresSeguidos = 0,
            fechaCreacion = "2025-12-02T21:55:00"
        )

        whenever(asistenteService.login(any(), any())).thenReturn(response)

        mockMvc.perform(
            post("/asistentes/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.username").value("user1"))
            .andExpect(jsonPath("$.role").value("ASISTENTE"))
    }

    @Test
    fun `login fallido devuelve 401`() {
        val request = LoginRequest(correo = "user1@example.com", password = "wrongpass")

        whenever(asistenteService.login(any(), any())).thenReturn(null)

        mockMvc.perform(
            post("/asistentes/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isUnauthorized)
            .andExpect(jsonPath("$.error").value("Credenciales inválidas"))
    }

    @Test
    fun `obtener asistente por id devuelve 200 si existe`() {
        val response = UserResponse(
            id = 2,
            username = "user2",
            correo = "user2@example.com",
            profilePic = null,
            role = UserRole.ASISTENTE,
            totalOrganizadoresSeguidos = 1,
            fechaCreacion = "2025-12-02T21:55:00"
        )

        whenever(asistenteService.obtenerAsistentePorId(2)).thenReturn(response)

        mockMvc.perform(get("/asistentes/2"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(2))
            .andExpect(jsonPath("$.correo").value("user2@example.com"))
            .andExpect(jsonPath("$.totalOrganizadoresSeguidos").value(1))
    }

    @Test
    fun `obtener asistente por id devuelve 404 si no existe`() {
        whenever(asistenteService.obtenerAsistentePorId(99)).thenReturn(null)

        mockMvc.perform(get("/asistentes/99"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.error").value("Asistente no encontrado"))
    }

    @Test
    fun `actualizar asistente OK`() {
        val request = UpdateAsistenteRequest(
            correo = "new@example.com",
            username = "newUser",
            password = null,
            profilePic = null,
            categoriasIds = emptyList()
        )

        val response = UserResponse(
            id = 1,
            username = "newUser",
            correo = "new@example.com",
            profilePic = null,
            role = UserRole.ASISTENTE,
            totalOrganizadoresSeguidos = 0,
            fechaCreacion = "2025-12-02T21:55:00"
        )

        whenever(asistenteService.actualizarAsistente(any(), any())).thenReturn(response)

        mockMvc.perform(
            patch("/asistentes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.username").value("newUser"))
            .andExpect(jsonPath("$.correo").value("new@example.com"))
    }

    @Test
    fun `eliminar asistente devuelve 204 si existe`() {
        whenever(asistenteService.eliminarAsistente(1)).thenReturn(true)

        mockMvc.perform(delete("/asistentes/1"))
            .andExpect(status().isNoContent)
    }

    @Test
    fun `eliminar asistente devuelve 404 si no existe`() {
        whenever(asistenteService.eliminarAsistente(99)).thenReturn(false)

        mockMvc.perform(delete("/asistentes/99"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.error").value("Asistente no encontrado"))
    }

    @Test
    fun `agregar interes devuelve 200`() {
        val response = UserResponse(
            id = 1,
            username = "user1",
            correo = "user1@example.com",
            profilePic = null,
            role = UserRole.ASISTENTE,
            totalOrganizadoresSeguidos = 2,
            fechaCreacion = "2025-12-02T21:55:00"
        )

        whenever(asistenteService.agregarInteres(1, 10)).thenReturn(response)

        mockMvc.perform(post("/asistentes/1/intereses/10"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.totalOrganizadoresSeguidos").value(2))
    }

    @Test
    fun `eliminar interes devuelve 200`() {
        val response = UserResponse(
            id = 1,
            username = "user1",
            correo = "user1@example.com",
            profilePic = null,
            role = UserRole.ASISTENTE,
            totalOrganizadoresSeguidos = 0,
            fechaCreacion = "2025-12-02T21:55:00"
        )

        whenever(asistenteService.eliminarInteres(1, 10)).thenReturn(response)

        mockMvc.perform(delete("/asistentes/1/intereses/10"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.totalOrganizadoresSeguidos").value(0))
    }


}