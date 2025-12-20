package org.example.src.models

class CreadorUser {
    fun crearUser(
        tipo: String,
        username: String,
        correo: String,
        password: String,
        profile_pic: String = "",
        about: String? = null,
        //followers: Int = 0,
        suscribed: Boolean = false
    ): User {
        return when (tipo.lowercase()) {
            "organizador" -> Organizador(
                username = username,
                correo = correo,
                password = password,
                profile_pic = profile_pic,
                about = about,
                //followers = followers,
                suscribed = suscribed
            )
            "asistente" -> Asistente(
                username = username,
                nombreCompleto = username, // o recibirlo como parámetro
                correo = correo,
                password = password,
                profile_pic = profile_pic
            )

            else -> throw IllegalArgumentException("Tipo de usuario no soportado: $tipo")
        }
    }
}