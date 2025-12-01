package org.example.src.utils

object ValidationUtils {

    // Email más robusto
    fun isValidEmail(email: String): Boolean {
        return Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\$").matches(email)
    }

    // Password más seguro
    fun isStrongPassword(password: String): Boolean {
        if (password.length < 8) return false
        if (!password.any { it.isDigit() }) return false
        if (!password.any { it.isLetter() }) return false
        if (!password.any { !it.isLetterOrDigit() }) return false
        return true
    }

    // Validaciones adicionales útiles
    fun isValidUsername(username: String): Boolean {
        return username.length in 3..50 &&
                Regex("^[a-zA-Z0-9_.-]+\$").matches(username)
    }

    fun isValidPhoneNumber(phone: String): Boolean {
        return Regex("^[+]?[0-9]{8,15}\$").matches(phone)
    }
}