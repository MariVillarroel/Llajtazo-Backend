package org.example.services

import org.example.src.dto.UserRequest
import org.example.src.models.Asistente
import org.example.models.User
import org.example.src.models.UserRole
import org.example.src.repositories.UserRepository
import org.example.src.services.UserService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class FakeUserRepository : UserRepository() {

    private val usuarios = mutableListOf<Asistente>()
    private var currentId = 1

    override fun findByEmail(email: String): User? {
        return usuarios.find { it.correo == email }
    }

    override fun create(user: Asistente): Int {
        val newUser = user.copy(id = currentId++)
        usuarios.add(newUser)
        return newUser.id
    }
}

class UserAsistenteTest {

    private val fakeRepo = FakeUserRepository()
    private val userService = UserService(fakeRepo)

    // TEST 1: Registro exitoso
    @Test
    fun `registro exitoso devuelve UserResponse correcto`() {
        val req = UserRequest(
            id = 0,
            tipo = "asistente",
            username = "daniela",
            correo = "daniela@example.com",
            password = "PasswordSegura123"
        )

        val resp = userService.registerUser(req)

        assertEquals("daniela", resp.username)
        assertEquals("daniela@example.com", resp.correo)
        assertEquals(UserRole.ASISTENTE, resp.role)
        assertTrue(resp.id > 0)
    }

    // TEST 2: No permite correos duplicados
    @Test
    fun `no permite registrar dos usuarios con el mismo correo`() {
        val req1 = UserRequest(
            id = 0,
            tipo = "asistente",
            username = "user1",
            correo = "duplicado@example.com",
            password = "PasswordSegura123"
        )

        val req2 = UserRequest(
            id = 0,
            tipo = "asistente",
            username = "user2",
            correo = "duplicado@example.com", // MISMO CORREO
            password = "PasswordSegura123"
        )

        // Primer registro ok
        userService.registerUser(req1)

        // Segundo registro debe lanzar excepción
        val ex = assertThrows(IllegalStateException::class.java) {
            userService.registerUser(req2)
        }

        assertEquals("El correo ya está registrado", ex.message)
    }
}
