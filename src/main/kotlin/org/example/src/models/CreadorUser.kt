package org.example.src.models

class CreadorUser {
    fun crearUser(
        tipo: String,
        username: String,
        correo: String,
        password: String,
        profile_pic: String = "",
        // Parámetros opcionales para tipos específicos
        nombre_org: String? = null,
        numero: String? = null,
        nombreCompleto: String? = null
    ): User {
        return when (tipo.lowercase()) {
            "organizador" -> Organizador(
                username = username,
                correo = correo,
                password = password,
                profile_pic = profile_pic,
                nombre_org = nombre_org ?: "",  // Usar si viene, sino vacío
                numero = numero ?: ""
            )
            "asistente" -> Asistente(
                username = username,
                correo = correo,
                password = password,
                profile_pic = profile_pic
            )
            else -> throw IllegalArgumentException("Tipo de usuario no soportado: $tipo")
        }
    }
}