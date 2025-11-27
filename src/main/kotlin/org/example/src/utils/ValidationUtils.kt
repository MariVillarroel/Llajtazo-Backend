package org.example.src.utils

object ValidationUtils {
    fun isValidEmail(email: String): Boolean =
        Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$").matches(email)

    fun isStrongPassword(password: String): Boolean =
        password.length >= 8
}