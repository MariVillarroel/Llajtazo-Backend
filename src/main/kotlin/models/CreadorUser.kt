package org.example.models

object CreadorUser {

    fun crearUser(
        id: Int,
        tipo: String,
        username: String,
        correo: String,
        passwordHash: String
    ): User {
        return when (tipo.lowercase()) {
            "asistente" -> Asistente(
                id = id,
                username = username,
                correo = correo,
                passwordHash = passwordHash
            )
            "organizador" -> Organizador(
                id = id,
                username = username,
                correo = correo,
                passwordHash = passwordHash,
                nombreOrg = username,
                numero = ""
            )
            else -> throw IllegalArgumentException("Tipo de usuario no soportado: $tipo")
        }
    }
}