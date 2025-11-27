package org.example.src.models

class CreadorUser {

    fun crearUser(
        tipo: String,
        username: String,
        correo: String,
        password: String,
        profile_pic: String = "",
        nombre_org: String? = null,
        numero: String? = null
    ): User {
        return when (tipo.lowercase()) {
            "organizador" -> Organizador(
                username = username,
                correo = correo,
                password = password,
                profile_pic = profile_pic,
                nombre_org = nombre_org ?: "",
                numero = numero ?:"",
                //followers = followers
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