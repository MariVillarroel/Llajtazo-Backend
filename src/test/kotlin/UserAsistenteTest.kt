package org.example.services

import org.example.dto.UserRegisterRequest
import org.example.models.Asistente
import org.example.models.User
import org.example.models.UserRole
import org.example.repositories.UserRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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
        val req = UserRegisterRequest(
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
        val req1 = UserRegisterRequest(
            id = 0,
            tipo = "asistente",
            username = "user1",
            correo = "duplicado@example.com",
            password = "PasswordSegura123"
        )

        val req2 = UserRegisterRequest(
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
