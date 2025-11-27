package org.example.utils

import java.security.MessageDigest

object PasswordUtils {
    fun hashPassword(plain: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest(plain.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}