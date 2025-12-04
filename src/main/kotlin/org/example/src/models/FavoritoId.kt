package org.example.src.models

import java.io.Serializable

// Clave compuesta: usuario_id + evento_id
data class FavoritoId(
    val asistente: Int = 0,
    val evento: Int = 0
) : Serializable
