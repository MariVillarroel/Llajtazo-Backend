package org.example.services

import org.example.src.dto.OrganizadorRequest
import org.example.src.dto.UpdateOrganizadorRequest
import org.example.src.dto.OrganizadorResponse
import org.example.src.models.Organizador
import org.example.src.models.Suscripcion
import org.example.src.repositories.OrganizadorRepository
import org.example.src.services.OrganizadorService
import org.example.src.services.SuscripcionService
import org.example.src.utils.PasswordUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*

import java.time.LocalDateTime
import java.util.*

class OrganizadorServiceTest {

    private val repository: OrganizadorRepository = mock()
    private val suscripcion: SuscripcionService = mock()
    private val service = OrganizadorService(repository, suscripcion)

    @Test
    fun `crear organizador con correo duplicado lanza IllegalArgumentException`() {
        val request = OrganizadorRequest("org1", "duplicado@example.com", "Passw0rd!", null)

        whenever(repository.findByCorreo("duplicado@example.com"))
            .thenReturn(fakeOrganizador("duplicado@example.com", "org1", PasswordUtils.hashPassword("Passw0rd!")))

        val ex = assertThrows<IllegalArgumentException> {
            service.crearOrganizador(request)
        }
        assertEquals("El correo ya está registrado", ex.message)
    }

    @Test
    fun `crear organizador con username duplicado lanza IllegalArgumentException`() {
        val request = OrganizadorRequest("org1", "org@example.com", "Passw0rd!", null)

        whenever(repository.findByCorreo("org@example.com")).thenReturn(null)
        whenever(repository.existsByUsername("org1")).thenReturn(true)

        val ex = assertThrows<IllegalArgumentException> {
            service.crearOrganizador(request)
        }
        assertEquals("El username ya está en uso", ex.message)
    }

    @Test
    fun `crear organizador exitoso devuelve OrganizadorResponse`() {
        val request = OrganizadorRequest("org1", "org@example.com", "Passw0rd!", null)

        whenever(repository.findByCorreo("org@example.com")).thenReturn(null)
        whenever(repository.existsByUsername("org1")).thenReturn(false)
        whenever(repository.save(any())).thenAnswer { it.arguments[0] as Organizador }

        val result = service.crearOrganizador(request)

        assertEquals("org1", result.username)
        assertEquals("org@example.com", result.correo)
    }

    @Test
    fun `login exitoso devuelve OrganizadorResponse`() {
        val correo = "org1@example.com"
        val rawPassword = "Passw0rd!"
        val hashedPassword = PasswordUtils.hashPassword(rawPassword)

        val organizador = fakeOrganizador(correo, "org1", hashedPassword)
        whenever(repository.findByCorreo(correo)).thenReturn(organizador)

        val result = service.login(correo, rawPassword)

        assertNotNull(result)
        assertEquals("org1", result!!.username)
    }

    @Test
    fun `login fallido devuelve null`() {
        val correo = "org1@example.com"
        val organizador = fakeOrganizador(correo, "org1", PasswordUtils.hashPassword("Passw0rd!"))

        whenever(repository.findByCorreo(correo)).thenReturn(organizador)

        val result = service.login(correo, "wrongpass")
        assertNull(result)
    }

    @Test
    fun `actualizar organizador cambia username y correo`() {
        val organizador = Organizador(
            username = "oldOrg",
            correo = "old@example.com",
            password = PasswordUtils.hashPassword("Passw0rd!"),
            profile_pic = null,
            fechaCreacion = LocalDateTime.now()
        ).apply { id = 1 } // asignamos id manualmente

        whenever(repository.findById(1)).thenReturn(Optional.of(organizador))
        whenever(repository.save(any())).thenAnswer { it.arguments[0] as Organizador }
        whenever(repository.findByCorreo("new.user@test.org")).thenReturn(null)
        whenever(repository.existsByUsername("newOrg")).thenReturn(false)

        val request = UpdateOrganizadorRequest(
            correo = "new.user@test.org",
            username = "newOrg",
            password = null,
            profilePic = ""
        )

        val result = service.actualizarOrganizador(1, request)

        assertEquals("newOrg", result.username)
        assertEquals("new.user@test.org", result.correo)
    }

    @Test
    fun `eliminar organizador devuelve true si existe`() {
        whenever(repository.existsById(1)).thenReturn(true)

        val result = service.eliminarOrganizador(1)

        assertTrue(result)
        verify(repository).deleteById(1)
    }

    @Test
    fun `eliminar organizador devuelve false si no existe`() {
        whenever(repository.existsById(99)).thenReturn(false)

        val result = service.eliminarOrganizador(99)

        assertFalse(result)
        verify(repository, never()).deleteById(any())
    }

    @Test
    fun `buscar por correo devuelve OrganizadorResponse si existe`() {
        val organizador = fakeOrganizador("org@example.com", "org1", PasswordUtils.hashPassword("Passw0rd!"))
        whenever(repository.findByCorreo("org@example.com")).thenReturn(organizador)

        val result = service.buscarPorCorreo("org@example.com")

        assertNotNull(result)
        assertEquals("org1", result!!.username)
    }

    @Test
    fun `buscar por correo devuelve null si no existe`() {
        whenever(repository.findByCorreo("noexist@example.com")).thenReturn(null)

        val result = service.buscarPorCorreo("noexist@example.com")
        assertNull(result)
    }

    @Test
    fun `verificar credenciales devuelve true si password correcta`() {
        val correo = "org@example.com"
        val rawPassword = "Passw0rd!"
        val hashedPassword = PasswordUtils.hashPassword(rawPassword)

        val organizador = fakeOrganizador(correo, "org1", hashedPassword)
        whenever(repository.findByCorreo(correo)).thenReturn(organizador)

        val result = service.verificarCredenciales(correo, rawPassword)
        assertTrue(result)
    }

    @Test
    fun `verificar credenciales devuelve false si password incorrecta`() {
        val correo = "org@example.com"
        val organizador = fakeOrganizador(correo, "org1", PasswordUtils.hashPassword("Passw0rd!"))
        whenever(repository.findByCorreo(correo)).thenReturn(organizador)

        val result = service.verificarCredenciales(correo, "wrongpass")
        assertFalse(result)
    }

    @Test
    fun `actualizar organizador lanza IllegalArgumentException si correo duplicado`() {
        val organizador = fakeOrganizador("old@example.com", "oldOrg", PasswordUtils.hashPassword("Passw0rd!"))
        organizador.id = 1

        whenever(repository.findById(1)).thenReturn(Optional.of(organizador))
        whenever(repository.findByCorreo("dup@example.com"))
            .thenReturn(fakeOrganizador("dup@example.com", "otherOrg", PasswordUtils.hashPassword("Passw0rd!")))

        val request = UpdateOrganizadorRequest("dup@example.com", "newOrg", null, "")

        val ex = assertThrows<IllegalArgumentException> {
            service.actualizarOrganizador(1, request)
        }
        assertEquals("Correo inválido", ex.message)
    }

    @Test
    fun `actualizar organizador lanza IllegalArgumentException si username duplicado`() {
        val organizador = fakeOrganizador("old@example.com", "oldOrg", PasswordUtils.hashPassword("Passw0rd!"))
        organizador.id = 1

        whenever(repository.findById(1)).thenReturn(Optional.of(organizador))
        whenever(repository.existsByUsername("dupUser")).thenReturn(true)

        val request = UpdateOrganizadorRequest("new.user@test.org", "dupUser", null, "")

        val ex = assertThrows<IllegalArgumentException> {
            service.actualizarOrganizador(1, request)
        }
        assertEquals("Correo inválido", ex.message)
    }

    @Test
    fun `actualizar organizador lanza IllegalArgumentException si password inválida`() {
        val organizador = fakeOrganizador("old@example.com", "oldOrg", PasswordUtils.hashPassword("Passw0rd!"))
        organizador.id = 1

        whenever(repository.findById(1)).thenReturn(Optional.of(organizador))

        val request = UpdateOrganizadorRequest(null, null, "123", null) // contraseña débil

        val ex = assertThrows<IllegalArgumentException> {
            service.actualizarOrganizador(1, request)
        }
        assertEquals("Contraseña inválida", ex.message)
    }

    @Test
    fun `obtener organizador por id devuelve OrganizadorResponse si existe`() {
        val organizador = fakeOrganizador("org@example.com", "org1", PasswordUtils.hashPassword("Passw0rd!"))
        organizador.id = 1

        whenever(repository.findById(1)).thenReturn(Optional.of(organizador))

        val result = service.obtenerOrganizadorPorId(1)

        assertNotNull(result)
        assertEquals("org1", result!!.username)
    }

    @Test
    fun `obtener organizador por id devuelve null si no existe`() {
        whenever(repository.findById(99)).thenReturn(Optional.empty())

        val result = service.obtenerOrganizadorPorId(99)

        assertNull(result)
    }

    @Test
    fun `listar organizadores devuelve lista de OrganizadorResponse`() {
        val org1 = fakeOrganizador("org1@example.com", "org1", PasswordUtils.hashPassword("Passw0rd!"))
        val org2 = fakeOrganizador("org2@example.com", "org2", PasswordUtils.hashPassword("Passw0rd!"))

        whenever(repository.findAll()).thenReturn(listOf(org1, org2))

        val result = service.listarOrganizadores()

        assertEquals(2, result.size)
        assertEquals("org1", result[0].username)
        assertEquals("org2", result[1].username)
    }


    private fun fakeOrganizador(correo: String, username: String, password: String): Organizador {
        return Organizador(
            username = username,
            correo = correo,
            password = password,
            profile_pic = null,
            fechaCreacion = LocalDateTime.now()
        )
    }
}