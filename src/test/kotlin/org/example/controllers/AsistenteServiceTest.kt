package org.example.services

import org.example.src.dto.UpdateAsistenteRequest
import org.example.src.dto.UserRequest
import org.example.src.dto.UserResponse
import org.example.src.models.Asistente
import org.example.src.models.Categoria
import org.example.src.models.UserRole
import org.example.src.repositories.AsistenteRepository
import org.example.src.repositories.CategoriaRepository
import org.example.src.services.AsistenteService
import org.example.src.utils.PasswordUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*

import java.time.LocalDateTime
import java.util.*

class AsistenteServiceTest {

    private val asistenteRepository: AsistenteRepository = mock()
    private val categoriaRepository: CategoriaRepository = mock()
    private val service = AsistenteService(asistenteRepository, categoriaRepository)

    @Test
    fun `crear asistente con correo duplicado lanza IllegalArgumentException`() {
        val request = UserRequest(
            username = "user1",
            correo = "duplicado@example.com",
            password = "Passw0rd!",
            profilePic = "",
            categoriasIds = emptyList()
        )

        // Simulamos que ya existe un asistente con ese correo
        whenever(asistenteRepository.findByCorreo("duplicado@example.com"))
            .thenReturn(fakeAsistente(1, "duplicado@example.com", "user1", "Passw0rd!"))

        val ex = assertThrows<IllegalArgumentException> {
            service.crearAsistente(request)
        }
        assertEquals("El correo ya está registrado", ex.message)
    }

    @Test
    fun `login exitoso devuelve UserResponse`() {
        val correo = "user1@example.com"
        val rawPassword = "Passw0rd!"
        val hashedPassword = PasswordUtils.hashPassword(rawPassword)

        val asistente = fakeAsistente(1, correo, "user1", hashedPassword)
        whenever(asistenteRepository.findByCorreo(correo)).thenReturn(asistente)

        val result = service.login(correo, rawPassword)

        assertNotNull(result)
        assertEquals("user1", result!!.username)
        assertEquals(UserRole.ASISTENTE, result.role)
    }

    @Test
    fun `login fallido devuelve null`() {
        val correo = "user1@example.com"
        val hashedPassword = PasswordUtils.hashPassword("Passw0rd!")

        val asistente = fakeAsistente(1, correo, "user1", hashedPassword)
        whenever(asistenteRepository.findByCorreo(correo)).thenReturn(asistente)

        val result = service.login(correo, "wrongpass")
        assertNull(result)
    }

    @Test
    fun `actualizar asistente cambia username y correo`() {
        val asistente = fakeAsistente(1, "old@example.com", "oldUser", PasswordUtils.hashPassword("Passw0rd!"))
        whenever(asistenteRepository.findById(1)).thenReturn(Optional.of(asistente))
        whenever(asistenteRepository.save(any())).thenAnswer { it.arguments[0] as Asistente }
        whenever(asistenteRepository.findByCorreo("new@example.com")).thenReturn(null)
        whenever(asistenteRepository.existsByUsername("newUser")).thenReturn(false)
        whenever(categoriaRepository.findAllById(emptyList())).thenReturn(emptyList())

        val request = UpdateAsistenteRequest(
            correo = "new@example.com",
            username = "newUser",
            password = null,
            profilePic = "",
            categoriasIds = emptyList()
        )

        val result = service.actualizarAsistente(1, request)

        assertEquals("newUser", result.username)
        assertEquals("new@example.com", result.correo)
    }

    @Test
    fun `eliminar asistente devuelve true si existe`() {
        whenever(asistenteRepository.existsById(1)).thenReturn(true)
        val result = service.eliminarAsistente(1)
        assertTrue(result)
    }

    @Test
    fun `eliminar asistente devuelve false si no existe`() {
        whenever(asistenteRepository.existsById(99)).thenReturn(false)
        val result = service.eliminarAsistente(99)
        assertFalse(result)
    }

    @Test
    fun `agregar interes devuelve UserResponse actualizado`() {
        val categoria = Categoria(id = 10, nombre = "Tech")
        val asistente = fakeAsistente(1, "user@example.com", "user1", PasswordUtils.hashPassword("Passw0rd!"))

        whenever(asistenteRepository.findById(1)).thenReturn(Optional.of(asistente))
        whenever(categoriaRepository.findById(10)).thenReturn(Optional.of(categoria))
        whenever(asistenteRepository.save(any())).thenAnswer { it.arguments[0] as Asistente }

        val result = service.agregarInteres(1, 10)

        assertEquals(1, result.id)
        assertEquals("user1", result.username)
    }

    @Test
    fun `eliminar interes devuelve UserResponse actualizado`() {
        val categoria = Categoria(id = 10, nombre = "Tech")
        val asistente = fakeAsistente(1, "user@example.com", "user1", PasswordUtils.hashPassword("Passw0rd!"))
        asistente.tags.add(categoria)

        whenever(asistenteRepository.findById(1)).thenReturn(Optional.of(asistente))
        whenever(categoriaRepository.findById(10)).thenReturn(Optional.of(categoria))
        whenever(asistenteRepository.save(any())).thenAnswer { it.arguments[0] as Asistente }

        val result = service.eliminarInteres(1, 10)

        assertEquals(1, result.id)
        assertEquals("user1", result.username)
    }

    private fun fakeAsistente(id: Int, correo: String, username: String, password: String): Asistente {
        return Asistente(
            id = id,
            username = username,
            correo = correo,
            password = password,
            profile_pic = null,
            fechaCreacion = LocalDateTime.now()
        )
    }
}