package org.example.src.utils

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import java.security.SecureRandom
import java.security.MessageDigest

object PasswordUtils {

    // Usar BCrypt (recomendado para Spring)
    private val encoder: PasswordEncoder = BCryptPasswordEncoder()

    fun hashPassword(plainPassword: String): String {
        return encoder.encode(plainPassword)
    }

    fun verifyPassword(plainPassword: String, hashedPassword: String): Boolean {
        return encoder.matches(plainPassword, hashedPassword)
    }

    // Alternativa si no quieres Spring Security
    fun hashPasswordManual(plainPassword: String): String {
        val salt = generateSalt()
        val hash = hashWithSalt(plainPassword, salt)
        return "$salt:$hash"  // Guardar salt y hash juntos
    }

    private fun generateSalt(): String {
        val random = SecureRandom()
        val salt = ByteArray(16)
        random.nextBytes(salt)
        return salt.joinToString("") { "%02x".format(it) }
    }

    private fun hashWithSalt(password: String, salt: String): String {
        val algorithm = "SHA-256"
        val digest = MessageDigest.getInstance(algorithm)
        val saltedPassword = "$salt$password"
        val hash = digest.digest(saltedPassword.toByteArray())
        return hash.joinToString("") { "%02x".format(it) }
    }
}